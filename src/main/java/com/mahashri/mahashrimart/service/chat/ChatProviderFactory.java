package com.mahashri.mahashrimart.service.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Factory that chooses the chatbot provider from the configuration value. */
public final class ChatProviderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ChatProviderFactory.class);

    private ChatProviderFactory() { }

    /**
     * Creates the provider for a configuration name.
     *
     * @param name provider name, for example "mock" or "gemini"; null or blank means "mock"
     * @return the provider, the mock provider when the name is unknown or gemini is
     *         misconfigured (missing API key)
     */
    public static ChatProvider create(String name) {
        if (name == null || name.isBlank() || "mock".equalsIgnoreCase(name.trim())) {
            return new MockChatProvider();
        }

        if ("gemini".equalsIgnoreCase(name.trim())) {
            String apiKey = System.getenv("GEMINI_API_KEY");
            if (apiKey == null || apiKey.isBlank()) {
                LOG.warn("Chatbot provider 'gemini' requested but GEMINI_API_KEY is not set; "
                        + "using the mock provider instead");
                return new MockChatProvider();
            }
            LOG.info("Using Gemini chatbot provider");
            return new GeminiChatProvider();
        }

        LOG.warn("Unknown chatbot provider '{}', using the mock provider", name);
        return new MockChatProvider();
    }
}