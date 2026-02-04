package com.muy.demo.controladores;

import com.muy.demo.servicios.ReportService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
}
