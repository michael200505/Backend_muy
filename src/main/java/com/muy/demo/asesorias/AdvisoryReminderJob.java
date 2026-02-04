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

    // cada 1 minuto, envía recordatorio 30 min antes (ventana 29-31 min)
    @Scheduled(initialDelay = 10_000, fixedDelay = 60_000)
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusMinutes(29);
        LocalDateTime to = now.plusMinutes(31);

        advisories.findByStatusAndStartAtBetween(AdvisoryStatus.CONFIRMED, from, to)
                .forEach(a -> {
                    try {
                        String msg = "Recordatorio: asesoría ID " + a.getId() + " inicia a las " + a.getStartAt();
                        jakarta.notifyEmail(a.getProgrammer().getEmail(), "Recordatorio asesoría", msg);
                        jakarta.notifyEmail(a.getExternalUser().getEmail(), "Recordatorio asesoría", msg);
                    } catch (Exception e) {
                        System.err.println("Error enviando recordatorio asesoría " + a.getId() + ": " + e.getMessage());
                    }
                });
    }
}
