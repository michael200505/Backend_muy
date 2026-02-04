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
