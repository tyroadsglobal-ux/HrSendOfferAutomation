package com.hrprocessautomation.hr_process_automation.service;


import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
public class BrevoEmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final OkHttpClient client = new OkHttpClient();

    public void sendEmailWithAttachment(String toEmail, String toName, 
                                       String subject, String htmlContent, 
                                       byte[] pdfBytes, String pdfFileName) throws Exception {
        
        String url = "https://api.brevo.com/v3/smtp/email";

        // Encode PDF to base64
        String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

        // Build JSON payload
        JSONObject json = new JSONObject();
        
        // Sender
        JSONObject sender = new JSONObject();
        sender.put("email", "tyroads.global@gmail.com");
        sender.put("name", "Tyroads Global HR");
        json.put("sender", sender);

        // Recipient
        JSONArray to = new JSONArray();
        JSONObject recipient = new JSONObject();
        recipient.put("email", toEmail);
        recipient.put("name", toName);
        to.put(recipient);
        json.put("to", to);

        // Subject and content
        json.put("subject", subject);
        json.put("htmlContent", htmlContent);

        // Attachment
        JSONArray attachments = new JSONArray();
        JSONObject attachment = new JSONObject();
        attachment.put("content", base64Pdf);
        attachment.put("name", pdfFileName);
        attachments.put(attachment);
        json.put("attachment", attachments);

        // Build request
        RequestBody body = RequestBody.create(
            json.toString(),
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(url)
            .addHeader("accept", "application/json")
            .addHeader("api-key", brevoApiKey)
            .addHeader("content-type", "application/json")
            .post(body)
            .build();

        // Execute
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Brevo API error: " + response.body().string());
            }
        }
    }
}