package com.muy.demo.controladores;

import com.muy.demo.servicios.ReportService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAnyRole('PROGRAMMER','ADMIN')")
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reports;

    public ReportController(ReportService reports) {
        this.reports = reports;
    }

    @GetMapping("/advisories/pdf")
    public ResponseEntity<byte[]> advisoryPdf(@RequestParam Long programmerId) {
        byte[] pdf = reports.advisoryPdfForProgrammer(programmerId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=advisories.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/advisories/excel")
    public ResponseEntity<byte[]> advisoryExcel(@RequestParam Long programmerId) {
        byte[] xlsx = reports.advisoryExcelForProgrammer(programmerId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=advisories.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx);
    }
}
