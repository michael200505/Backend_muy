package com.muy.demo.servicios;

import com.muy.demo.modelosdto.CreateAvailabilityRequest;
import com.muy.demo.models.Availability;
import com.muy.demo.models.Role;
import com.muy.demo.repositorios.AvailabilityRepository;
import com.muy.demo.repositorios.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvailabilityService {

    private final AvailabilityRepository repo;
    private final UserRepository users;

    public AvailabilityService(AvailabilityRepository repo, UserRepository users) {
        this.repo = repo;
        this.users = users;
    }

    @Transactional
    public Availability create(CreateAvailabilityRequest req) {
        var programmer = users.findById(req.programmerId)
                .orElseThrow(() -> new IllegalArgumentException("Programmer no existe"));
        if (programmer.getRole() != Role.PROGRAMMER) {
            throw new IllegalArgumentException("El usuario no es PROGRAMMER");
        }
        if (!req.endTime.isAfter(req.startTime)) {
            throw new IllegalArgumentException("endTime debe ser mayor que startTime");
        }

        Availability a = new Availability();
        a.setProgrammer(programmer);
        a.setDayOfWeek(req.dayOfWeek);
        a.setStartTime(req.startTime);
        a.setEndTime(req.endTime);
        a.setModality(req.modality);
        return repo.save(a);
    }

    public List<Availability> listByProgrammer(Long programmerId) {
        return repo.findByProgrammerId(programmerId);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
