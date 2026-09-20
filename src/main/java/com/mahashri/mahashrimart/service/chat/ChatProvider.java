package com.mahashri.mahashrimart.service.chat;

/**
 * Strategy for producing a chatbot reply. The rest of the application depends on this
 * interface only, so a rule based provider or a real AI provider can be swapped by configuration.
 */
public interface ChatProvider {

    /**
     * Creates a reply for one user question.
     *
     * @param userMessage the validated question typed by the user
     * @param context     product catalogue text prepared by the chat service
     * @return the reply text to show in the chat widget
     */
    String getReply(String userMessage, String context);
}