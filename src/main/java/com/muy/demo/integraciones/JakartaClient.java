package com.muy.demo.integraciones;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class JakartaClient {

    private final RestTemplate rest = new RestTemplate();
    private final String baseUrl;

    public JakartaClient(@Value("${app.jakarta.baseUrl}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void notifyEmail(String to, String subject, String body) {
        String url = baseUrl + "/notifications/email";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = Map.of(
                "to", to,
                "subject", subject,
                "body", body
        );

        rest.exchange(url, HttpMethod.POST, new HttpEntity<>(payload, headers), Void.class);
    }

    // ✅ GET (como lo tienes)
    public byte[] getReportPdf(Long programmerId) {
        String url = baseUrl + "/reports/advisories/pdf?programmerId=" + programmerId;
        return rest.getForObject(url, byte[].class);
    }

    // ✅ POST PDF (como te piden)
    public byte[] generateAdvisoryPdf(Object payload) {
        String url = baseUrl + "/reports/advisories/pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<byte[]> response = rest.exchange(
                url,
                HttpMethod.POST,
                entity,
                byte[].class
        );

        return response.getBody();
    }

    // ✅ POST EXCEL (nuevo, además del PDF)
    public byte[] generateAdvisoryExcel(Object payload) {
        String url = baseUrl + "/reports/advisories/excel";
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        var entity = new org.springframework.http.HttpEntity<>(payload, headers);

        var response = rest.exchange(
                url,
                org.springframework.http.HttpMethod.POST,
                entity,
                byte[].class
        );

        return response.getBody();
    }

    public void notifyWhatsapp(String phone, String message) {
        String url = baseUrl + "/notifications/whatsapp";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = Map.of(
                "phone", phone,
                "message", message
        );

        rest.exchange(url, HttpMethod.POST, new HttpEntity<>(payload, headers), Void.class);
    }
}
