package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.model.Product;
import com.mahashri.mahashrimart.service.chat.CatalogContext;
import com.mahashri.mahashrimart.service.chat.ChatProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongSupplier;

/**
 * Business rules for the chatbot: input validation, a per-session rate limit, an in-memory
 * cache for repeated questions, and a static reply when the provider fails.
 */
public class ChatService {

    /** Longest message accepted, in characters. */
    public static final int MAX_MESSAGE_LENGTH = 200;
    /** Messages allowed for one session in one minute. */
    public static final int MAX_MESSAGES_PER_MINUTE = 10;
    /** Reply used when the provider or the database fails. */
    public static final String FALLBACK_REPLY =
            "Sorry, the assistant is not available right now. "
            + "Please browse the Marketplace or try again in a minute.";

    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);
    private static final long WINDOW_MILLIS = 60_000L;
    private static final int MAX_CACHE_ENTRIES = 500;
    private static final int PURGE_THRESHOLD = 1000;
    private static final String ANONYMOUS_SESSION = "anonymous";

    /**
     * Result of one chat request.
     *
     * @param status       HTTP status code to return
     * @param reply        reply text, null when the request failed
     * @param errorCode    error code, null when the request succeeded
     * @param errorMessage error message, null when the request succeeded
     */
    public record ChatResult(int status, String reply, String errorCode, String errorMessage) {

        /**
         * Creates a successful result.
         *
         * @param reply reply text
         * @return result with HTTP status 200
         */
        public static ChatResult ok(String reply) {
            return new ChatResult(200, reply, null, null);
        }

        /**
         * Creates a failed result.
         *
         * @param status  HTTP status code
         * @param code    error code
         * @param message error message
         * @return failed result
         */
        public static ChatResult error(int status, String code, String message) {
            return new ChatResult(status, null, code, message);
        }

        /** @return true when the request succeeded */
        public boolean success() {
            return errorCode == null;
        }
    }

    private final ProductService productService;
    private final ChatProvider provider;
    private final LongSupplier clock;
    private final Map<String, Deque<Long>> recentMessages = new HashMap<>();
    private final Map<String, String> cache = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > MAX_CACHE_ENTRIES;
        }
    };

    /**
     * Creates the service with the real clock.
     *
     * @param productService source of the product catalogue
     * @param provider       chatbot provider that creates the replies
     */
    public ChatService(ProductService productService, ChatProvider provider) {
        this(productService, provider, System::currentTimeMillis);
    }

    /**
     * Creates the service with a custom clock, used by tests.
     *
     * @param productService source of the product catalogue
     * @param provider       chatbot provider that creates the replies
     * @param clock          supplier of the current time in milliseconds
     */
    public ChatService(ProductService productService, ChatProvider provider, LongSupplier clock) {
        this.productService = productService;
        this.provider = provider;
        this.clock = clock;
    }

    /**
     * Handles one chat message.
     *
     * @param sessionId  id of the HTTP session, used for the rate limit and the cache
     * @param rawMessage the message typed by the user
     * @return the result with the reply or an error
     */
    public ChatResult handle(String sessionId, String rawMessage) {
        String message = rawMessage == null ? "" : rawMessage.trim();
        if (message.isEmpty()) {
            return ChatResult.error(400, "VALIDATION_ERROR", "Please type your question.");
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            return ChatResult.error(400, "VALIDATION_ERROR",
                    "Your message is too long. Please keep it under " + MAX_MESSAGE_LENGTH + " characters.");
        }
        String session = sessionId == null || sessionId.isBlank() ? ANONYMOUS_SESSION : sessionId;
        if (!allow(session)) {
            return ChatResult.error(429, "RATE_LIMITED",
                    "Too many messages. Please wait a minute and try again.");
        }

        String key = session + "|" + normalize(message);
        String cached = getCached(key);
        if (cached != null) {
            return ChatResult.ok(cached);
        }
        try {
            String reply = provider.getReply(message, buildContext());
            if (reply == null || reply.isBlank()) {
                return ChatResult.ok(FALLBACK_REPLY);
            }
            putCached(key, reply);
            return ChatResult.ok(reply);
        } catch (Exception ex) {
            LOG.error("Chatbot provider failed: {}", ex.getMessage());
            return ChatResult.ok(FALLBACK_REPLY);
        }
    }

    private String buildContext() throws Exception {
        List<String> categories = productService.listCategories();
        Map<Long, Product> unique = new LinkedHashMap<>();
        for (String category : categories) {
            for (Product p : productService.search(null, category)) {
                unique.putIfAbsent(p.getId(), p);
            }
        }
        return CatalogContext.build(categories, List.copyOf(unique.values()));
    }

    private synchronized boolean allow(String session) {
        long now = clock.getAsLong();
        if (recentMessages.size() > PURGE_THRESHOLD) {
            recentMessages.values().removeIf(d -> d.isEmpty() || now - d.peekLast() >= WINDOW_MILLIS);
        }
        Deque<Long> times = recentMessages.computeIfAbsent(session, k -> new ArrayDeque<>());
        while (!times.isEmpty() && now - times.peekFirst() >= WINDOW_MILLIS) {
            times.pollFirst();
        }
        if (times.size() >= MAX_MESSAGES_PER_MINUTE) {
            return false;
        }
        times.addLast(now);
        return true;
    }

    private synchronized String getCached(String key) {
        return cache.get(key);
    }

    private synchronized void putCached(String key, String reply) {
        cache.put(key, reply);
    }

    private static String normalize(String message) {
        return message.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}