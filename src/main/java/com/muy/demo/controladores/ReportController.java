package com.muy.demo.controladores;

import com.muy.demo.repositorios.UserRepository;
import com.muy.demo.seguridad.AuthUtil;
import com.muy.demo.servicios.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reports;
    private final UserRepository users;

    public ReportController(ReportService reports, UserRepository users) {
        this.reports = reports;
        this.users = users;
    }

    // ✅ Endpoint viejo (con programmerId) - útil para ADMIN si lo necesitas
    @PreAuthorize("hasAnyRole('PROGRAMMER','ADMIN')")
    @GetMapping("/advisories/pdf")
    public ResponseEntity<byte[]> advisoryPdf(@RequestParam Long programmerId) {
        byte[] pdf = reports.advisoryPdfForProgrammer(programmerId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=advisories.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ✅ Endpoint viejo (con programmerId) - útil para ADMIN si lo necesitas
    @PreAuthorize("hasAnyRole('PROGRAMMER','ADMIN')")
    @GetMapping("/advisories/excel")
    public ResponseEntity<byte[]> advisoryExcel(@RequestParam Long programmerId) {
        byte[] xlsx = reports.advisoryExcelForProgrammer(programmerId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=advisories.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx);
    }

    // =========================
    // ✅ NUEVOS ENDPOINTS /me
    // =========================

    @GetMapping("/me/advisories/pdf")
    @PreAuthorize("hasRole('PROGRAMMER')")
    public ResponseEntity<byte[]> myPdf() {
        Long programmerId = users.findIdByEmail(AuthUtil.currentEmail());
        byte[] pdf = reports.advisoryPdfForProgrammer(programmerId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=advisories.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/me/advisories/excel")
    @PreAuthorize("hasRole('PROGRAMMER')")
    public ResponseEntity<byte[]> myExcel() {
        Long programmerId = users.findIdByEmail(AuthUtil.currentEmail());
        byte[] xlsx = reports.advisoryExcelForProgrammer(programmerId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=advisories.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx);
    }
}
