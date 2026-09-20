package com.mahashri.mahashrimart.service.chat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * ChatProvider implementation that calls the Google Gemini API (generateContent endpoint).
 * The API key is read from the environment variable GEMINI_API_KEY and never appears
 * in client-side code. On any failure (timeout, network error, bad response), this
 * provider throws so that ChatService can fall back to the static FALLBACK_REPLY.
 */
public class GeminiChatProvider implements ChatProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiChatProvider.class);

    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String MODEL = System.getenv().getOrDefault("GEMINI_MODEL", "gemini-flash-latest");
    private static final String ENDPOINT_TEMPLATE =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    private static final Duration TIMEOUT = Duration.ofSeconds(8);

    private static final String SYSTEM_PROMPT =
            "You are the MahashriMart shopping assistant. Answer ONLY questions about " +
            "MahashriMart products, categories, stock, pricing, checkout, orders and delivery. " +
            "If asked anything outside this domain, politely say you can only help with " +
            "MahashriMart shopping questions. Keep replies short (2-4 sentences), plain text, " +
            "no markdown. Use the CATALOG CONTEXT below as your source of truth for products " +
            "and prices; do not invent products or prices not present in it.";

    private final HttpClient httpClient;

    public GeminiChatProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
    }

    @Override
    public String getReply(String userMessage, String context) {
        if (API_KEY == null || API_KEY.isBlank()) {
            log.error("GEMINI_API_KEY is not set; cannot call Gemini API");
            throw new IllegalStateException("Gemini API key missing");
        }

        try {
            String requestBody = buildRequestBody(userMessage, context);
            String url = String.format(ENDPOINT_TEMPLATE, MODEL);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIMEOUT)
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Gemini API returned status {}: {}", response.statusCode(), response.body());
                throw new IOException("Gemini API error status " + response.statusCode());
            }

            return extractReply(response.body());

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.error("Gemini API call failed", e);
            throw new RuntimeException("Gemini API call failed", e);
        }
    }

    private String buildRequestBody(String userMessage, String context) {
        JsonObject root = new JsonObject();

        JsonObject systemInstruction = new JsonObject();
        JsonArray systemParts = new JsonArray();
        JsonObject systemPart = new JsonObject();
        String fullSystemText = SYSTEM_PROMPT + "\n\nCATALOG CONTEXT:\n" + safe(context);
        systemPart.addProperty("text", fullSystemText);
        systemParts.add(systemPart);
        systemInstruction.add("parts", systemParts);
        root.add("system_instruction", systemInstruction);

        JsonArray contents = new JsonArray();
        JsonObject userContent = new JsonObject();
        userContent.addProperty("role", "user");
        JsonArray userParts = new JsonArray();
        JsonObject userPart = new JsonObject();
        userPart.addProperty("text", safe(userMessage));
        userParts.add(userPart);
        userContent.add("parts", userParts);
        contents.add(userContent);
        root.add("contents", contents);

        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.3);
        generationConfig.addProperty("maxOutputTokens", 500);

        JsonObject thinkingConfig = new JsonObject();
        thinkingConfig.addProperty("thinkingBudget", 0);
        generationConfig.add("thinkingConfig", thinkingConfig);

        root.add("generationConfig", generationConfig);

        return root.toString();
    }

    private String extractReply(String responseBody) {
        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonArray candidates = json.getAsJsonArray("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("Gemini API returned no candidates");
        }
        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
        JsonObject content = firstCandidate.getAsJsonObject("content");
        JsonArray parts = content.getAsJsonArray("parts");
        if (parts == null || parts.isEmpty()) {
            throw new RuntimeException("Gemini API returned no parts");
        }
        String text = parts.get(0).getAsJsonObject().get("text").getAsString();
        return text == null ? "" : text.trim();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}