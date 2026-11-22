package org.example.messmate.notification.infrastructure;

import org.example.messmate.exception.EmailDeliveryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Component
@Profile("stag")
public class ResendEmailProvider implements EmailProvider {

    private final RestClient restClient;
    private final String apiKey;
    private final String fromEmail;

    public ResendEmailProvider(
            @Value("${resend.api-key}") String apiKey,
            @Value("${resend.from-email}") String fromEmail
    ) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .build();
    }

    @Override
    public void send(
            String recipient,
            String subject,
            String message
    ) {

        Map<String, Object> requestBody = Map.of(
                "from", fromEmail,
                "to", new String[]{recipient},
                "subject", subject,
                "text", message
        );

        try {

            String response = restClient.post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            System.out.println("RESEND RESPONSE: " + response);

        } catch (RestClientResponseException e) {

            System.err.println("RESEND STATUS: " + e.getStatusCode());
            System.err.println("RESEND RESPONSE: " + e.getResponseBodyAsString());

            throw new EmailDeliveryException(
                    "Resend failed: " + e.getResponseBodyAsString(),
                    e
            );

        } catch (RestClientException e) {

            System.err.println("RESEND CONNECTION ERROR: " + e.getMessage());

            throw new EmailDeliveryException(
                    "Failed to connect to Resend",
                    e
            );
        }
    }
}