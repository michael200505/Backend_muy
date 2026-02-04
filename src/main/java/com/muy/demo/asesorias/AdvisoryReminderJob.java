package com.muy.demo.asesorias;

import com.muy.demo.integraciones.JakartaClient;
import com.muy.demo.models.AdvisoryStatus;
import com.muy.demo.repositorios.AdvisoryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdvisoryReminderJob {

    private final AdvisoryRepository advisories;
    private final JakartaClient jakarta;

    public AdvisoryReminderJob(AdvisoryRepository advisories, JakartaClient jakarta) {
        this.advisories = advisories;
        this.jakarta = jakarta;
    }

    // cada minuto revisa asesorías confirmadas próximas (ej: 30 min antes)
    @Scheduled(fixedRate = 60_000)
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusMinutes(29);
        LocalDateTime to = now.plusMinutes(31);

        // solución rápida sin query nueva: traemos y filtramos (si tienes pocos)
        // para producción, mejor query JPA específica
        advisories.findAll().stream()
                .filter(a -> a.getStatus() == AdvisoryStatus.CONFIRMED)
                .filter(a -> !a.getStartAt().isBefore(from) && !a.getStartAt().isAfter(to))
                .forEach(a -> {
                    String msg = "Recordatorio: asesoría ID " + a.getId()
                            + " inicia a las " + a.getStartAt();
                    jakarta.notifyEmail(a.getProgrammer().getEmail(), "Recordatorio asesoría", msg);
                    jakarta.notifyEmail(a.getExternalUser().getEmail(), "Recordatorio asesoría", msg);
                });
    }
}
