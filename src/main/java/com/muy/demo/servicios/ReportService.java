package com.muy.demo.servicios;

import com.muy.demo.integraciones.JakartaClient;
import com.muy.demo.modelosdto.ReportAdvisoryPdfRequest;
import com.muy.demo.modelosdto.ReportAdvisoryRow;
import com.muy.demo.repositorios.AdvisoryRepository;
import com.muy.demo.repositorios.UserRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    private final AdvisoryRepository advisories;
    private final UserRepository users;
    private final JakartaClient jakarta;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ReportService(AdvisoryRepository advisories, UserRepository users, JakartaClient jakarta) {
        this.advisories = advisories;
        this.users = users;
        this.jakarta = jakarta;
    }

    public byte[] advisoryPdfForProgrammer(Long programmerId) {
        var programmer = users.findById(programmerId)
                .orElseThrow(() -> new IllegalArgumentException("Programmer no existe"));

        var list = advisories.findByProgrammerId(programmerId);

        List<ReportAdvisoryRow> rows = list.stream().map(a -> {
            ReportAdvisoryRow r = new ReportAdvisoryRow();
            r.id = a.getId();
            r.programmerName = a.getProgrammer().getFullName();
            r.externalName = a.getExternalUser().getFullName();
            r.startAt = a.getStartAt().format(fmt);
            r.endAt = a.getEndAt().format(fmt);
            r.modality = a.getModality().name();
            r.status = a.getStatus().name();
            r.topic = a.getTopic();
            return r;
        }).toList();

        ReportAdvisoryPdfRequest payload = new ReportAdvisoryPdfRequest();
        payload.title = "Reporte de asesorías - " + programmer.getFullName();
        payload.rows = rows;

        return jakarta.generateAdvisoryPdf(payload);
    }

    // ✅ NUEVO: Excel
    public byte[] advisoryExcelForProgrammer(Long programmerId) {
        var programmer = users.findById(programmerId)
                .orElseThrow(() -> new IllegalArgumentException("Programmer no existe"));

        var list = advisories.findByProgrammerId(programmerId);

        List<com.muy.demo.modelosdto.ReportAdvisoryRow> rows = list.stream().map(a -> {
            com.muy.demo.modelosdto.ReportAdvisoryRow r = new com.muy.demo.modelosdto.ReportAdvisoryRow();
            r.id = a.getId();
            r.programmerName = a.getProgrammer().getFullName();
            r.externalName = a.getExternalUser().getFullName();
            r.startAt = a.getStartAt().format(fmt);
            r.endAt = a.getEndAt().format(fmt);
            r.modality = a.getModality().name();
            r.status = a.getStatus().name();
            r.topic = a.getTopic();
            return r;
        }).toList();

        com.muy.demo.modelosdto.ReportAdvisoryPdfRequest payload = new com.muy.demo.modelosdto.ReportAdvisoryPdfRequest();
        payload.title = "Reporte de asesorías (Excel) - " + programmer.getFullName();
        payload.rows = rows;

        return jakarta.generateAdvisoryExcel(payload);
    }
}
