package org.algo.translator.algo.poster.enums;

/**
 * @author Yormamatov Davronbek
 * @since 20.02.2022
 */


public enum ButtonData {
    SWITCH_NOTIFY("posting-notify", "Send with notification? "),
    SWITCH_DESC_SENDING("posting_desc", "Send in reverse order? "),
    HOW_MANY_SEND("posting-count", "How many subscribers to send? "),
    SET_CHANNEL_USERNAME("posting-channel-username", "Username: "),
    SET_POST_ID("posting-post-id", "Post ID: "),
    SET_HTML_TEXT("posting-html-text", "Has html text? "),
    SET_CUSTOM_BOARD("posting-board", "Board? "),
    TEST_POST("posting-test", "Test post to admin "),
    KILL_PROCESS("posting-kill-process", "\uD83D\uDCA3 Kill process \uD83D\uDCA3"),
    RESUME_EDIT("posting-resume-edit", "\uD83D\uDEE0 Resume Edit \uD83D\uDEE0"),
    START_POSTING("posting-start", "✈️Start Sending ✈️");
    private final String data;
    private final String text;

    ButtonData(String data, String text) {
        this.data = data;
        this.text = text;
    }

    public String getData() {
        return data;
    }

    public String getText() {
        return text;
    }
}
