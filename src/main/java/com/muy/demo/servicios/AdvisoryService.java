package com.muy.demo.servicios;

import com.muy.demo.integraciones.JakartaClient;
import com.muy.demo.modelosdto.CreateAdvisoryRequest;
import com.muy.demo.modelosdto.UpdateAdvisoryStatusRequest;
import com.muy.demo.models.*;
import com.muy.demo.repositorios.AdvisoryRepository;
import com.muy.demo.repositorios.AvailabilityRepository;
import com.muy.demo.repositorios.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
public class AdvisoryService {

    private final AdvisoryRepository advisories;
    private final AvailabilityRepository availabilityRepo;
    private final UserRepository users;
    private final JakartaClient jakarta;

    public AdvisoryService(
            AdvisoryRepository advisories,
            AvailabilityRepository availabilityRepo,
            UserRepository users,
            JakartaClient jakarta
    ) {
        this.advisories = advisories;
        this.availabilityRepo = availabilityRepo;
        this.users = users;
        this.jakarta = jakarta;
    }

    // =========================
    // ✅ MÉTODOS "VIEJOS" (por ID) - los puedes dejar
    // =========================

    @Transactional
    public Advisory create(CreateAdvisoryRequest req) {
        User programmer = users.findById(req.programmerId)
                .orElseThrow(() -> new IllegalArgumentException("Programmer no existe"));
        if (programmer.getRole() != Role.PROGRAMMER) throw new IllegalArgumentException("El usuario no es PROGRAMMER");

        User external = users.findById(req.externalUserId)
                .orElseThrow(() -> new IllegalArgumentException("External user no existe"));
        if (external.getRole() != Role.EXTERNAL) throw new IllegalArgumentException("El usuario no es EXTERNAL");

        if (!req.endAt.isAfter(req.startAt)) {
            throw new IllegalArgumentException("endAt debe ser mayor que startAt");
        }

        DayOfWeek day = req.startAt.getDayOfWeek();
        LocalTime startT = req.startAt.toLocalTime();
        LocalTime endT = req.endAt.toLocalTime();

        var slots = availabilityRepo.findByProgrammerIdAndDayOfWeek(programmer.getId(), day);

        boolean matches = slots.stream().anyMatch(s ->
                s.getModality() == req.modality &&
                        !startT.isBefore(s.getStartTime()) &&
                        !endT.isAfter(s.getEndTime())
        );

        if (!matches) {
            throw new IllegalArgumentException("Horario no disponible para ese programador (día/modality/hora).");
        }

        boolean overlap = advisories.existsOverlap(
                programmer.getId(),
                req.startAt,
                req.endAt,
                AdvisoryStatus.PENDING,
                AdvisoryStatus.CONFIRMED
        );
        if (overlap) throw new IllegalArgumentException("Ya existe una asesoría en ese horario.");

        Advisory a = new Advisory();
        a.setProgrammer(programmer);
        a.setExternalUser(external);
        a.setStartAt(req.startAt);
        a.setEndAt(req.endAt);
        a.setModality(req.modality);
        a.setTopic(req.topic);
        a.setStatus(AdvisoryStatus.PENDING);

        Advisory saved = advisories.save(a);

        jakarta.notifyEmail(programmer.getEmail(),
                "Nueva asesoría solicitada",
                "Tienes una solicitud de asesoría.\nID: " + saved.getId() +
                        "\nFecha/hora: " + saved.getStartAt() +
                        "\nModalidad: " + saved.getModality());

        jakarta.notifyEmail(external.getEmail(),
                "Asesoría solicitada",
                "Tu asesoría fue creada y está PENDIENTE.\nID: " + saved.getId());

        // =========================
        // ✅ WhatsApp (después de emails)
        // =========================
        String wMsgP = "Nueva asesoría solicitada. ID: " + saved.getId() + " Inicio: " + saved.getStartAt();
        String wMsgE = "Tu asesoría fue solicitada. ID: " + saved.getId() + " Estado: PENDING";

        if (programmer.getPhone() != null && !programmer.getPhone().isBlank()) {
            jakarta.notifyWhatsapp(programmer.getPhone(), wMsgP);
        }
        if (external.getPhone() != null && !external.getPhone().isBlank()) {
            jakarta.notifyWhatsapp(external.getPhone(), wMsgE);
        }

        return saved;
    }

    @Transactional
    public Advisory updateStatus(Long id, UpdateAdvisoryStatusRequest req) {
        Advisory a = advisories.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asesoría no existe"));

        if (a.getStatus() == AdvisoryStatus.CANCELLED) {
            throw new IllegalArgumentException("No se puede modificar una asesoría CANCELLED");
        }
        if (a.getStatus() == AdvisoryStatus.REJECTED && req.status != AdvisoryStatus.CANCELLED) {
            throw new IllegalArgumentException("Una asesoría REJECTED solo puede pasar a CANCELLED");
        }

        if (req.status == AdvisoryStatus.REJECTED) {
            if (req.rejectionReason == null || req.rejectionReason.isBlank()) {
                throw new IllegalArgumentException("rejectionReason es obligatorio si REJECTED");
            }
            a.setRejectionReason(req.rejectionReason);
        } else {
            a.setRejectionReason(null);
        }

        a.setStatus(req.status);
        Advisory saved = advisories.save(a);

        String msg = "Asesoría ID " + saved.getId() + " ahora está: " + saved.getStatus();
        jakarta.notifyEmail(saved.getProgrammer().getEmail(), "Actualización de asesoría", msg);
        jakarta.notifyEmail(saved.getExternalUser().getEmail(), "Actualización de asesoría", msg);

        return saved;
    }

    public List<Advisory> listByProgrammer(Long programmerId) {
        return advisories.findByProgrammerId(programmerId);
    }

    public List<Advisory> listByExternal(Long externalUserId) {
        return advisories.findByExternalUserId(externalUserId);
    }

    // =========================
    // ✅ MÉTODOS SEGUROS (por email del token)
    // =========================

    @Transactional
    public Advisory createAsExternal(String externalEmail, CreateAdvisoryRequest req) {
        // External autenticado (sale del token)
        User external = users.findByEmail(externalEmail)
                .orElseThrow(() -> new IllegalArgumentException("External no existe"));
        if (external.getRole() != Role.EXTERNAL) throw new IllegalArgumentException("No eres EXTERNAL");

        // Programmer viene del request (el external elige a quién)
        User programmer = users.findById(req.programmerId)
                .orElseThrow(() -> new IllegalArgumentException("Programmer no existe"));
        if (programmer.getRole() != Role.PROGRAMMER) throw new IllegalArgumentException("El usuario no es PROGRAMMER");

        if (!req.endAt.isAfter(req.startAt)) throw new IllegalArgumentException("endAt debe ser mayor que startAt");

        // Validar disponibilidad
        DayOfWeek day = req.startAt.getDayOfWeek();
        LocalTime startT = req.startAt.toLocalTime();
        LocalTime endT = req.endAt.toLocalTime();

        var slots = availabilityRepo.findByProgrammerIdAndDayOfWeek(programmer.getId(), day);

        boolean matches = slots.stream().anyMatch(s ->
                s.getModality() == req.modality &&
                        !startT.isBefore(s.getStartTime()) &&
                        !endT.isAfter(s.getEndTime())
        );

        if (!matches) throw new IllegalArgumentException("Horario no disponible para ese programador.");

        // Evitar cruce
        boolean overlap = advisories.existsOverlap(
                programmer.getId(), req.startAt, req.endAt,
                AdvisoryStatus.PENDING, AdvisoryStatus.CONFIRMED
        );
        if (overlap) throw new IllegalArgumentException("Ya existe una asesoría en ese horario.");

        Advisory a = new Advisory();
        a.setProgrammer(programmer);
        a.setExternalUser(external);
        a.setStartAt(req.startAt);
        a.setEndAt(req.endAt);
        a.setModality(req.modality);
        a.setTopic(req.topic);
        a.setStatus(AdvisoryStatus.PENDING);

        Advisory saved = advisories.save(a);

        jakarta.notifyEmail(programmer.getEmail(), "Nueva asesoría solicitada",
                "Tienes una solicitud. ID: " + saved.getId() + " - " + saved.getStartAt());

        jakarta.notifyEmail(external.getEmail(), "Asesoría solicitada",
                "Tu asesoría fue creada (PENDIENTE). ID: " + saved.getId());

        return saved;
    }

    @Transactional
    public Advisory updateStatusAsProgrammer(String programmerEmail, Long advisoryId, UpdateAdvisoryStatusRequest req) {
        Advisory a = advisories.findByIdAndProgrammerEmail(advisoryId, programmerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Asesoría no existe o no te pertenece"));

        if (a.getStatus() == AdvisoryStatus.CANCELLED) throw new IllegalArgumentException("No se modifica CANCELLED");

        if (req.status == AdvisoryStatus.REJECTED) {
            if (req.rejectionReason == null || req.rejectionReason.isBlank()) {
                throw new IllegalArgumentException("rejectionReason obligatorio si REJECTED");
            }
            a.setRejectionReason(req.rejectionReason);
        } else {
            a.setRejectionReason(null);
        }

        a.setStatus(req.status);
        Advisory saved = advisories.save(a);

        String msg = "Asesoría ID " + saved.getId() + " ahora está: " + saved.getStatus();
        jakarta.notifyEmail(saved.getProgrammer().getEmail(), "Actualización de asesoría", msg);
        jakarta.notifyEmail(saved.getExternalUser().getEmail(), "Actualización de asesoría", msg);

        return saved;
    }

    public List<Advisory> listByProgrammerEmail(String programmerEmail) {
        return advisories.findByProgrammerEmail(programmerEmail);
    }

    public List<Advisory> listByExternalEmail(String externalEmail) {
        return advisories.findByExternalEmail(externalEmail);
    }
}
