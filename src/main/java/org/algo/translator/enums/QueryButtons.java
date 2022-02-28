package org.algo.translator.enums;

public enum QueryButtons {
    ARABIC("Arabic", "ar", "\uD83C\uDDE6\uD83C\uDDEA"),
    ENGLISH("English", "en", "\uD83C\uDDFA\uD83C\uDDF8"),
    CHINESE_SIMPLIFIELD("Chinese SM", "zh-CN", "\uD83C\uDDE8\uD83C\uDDF3"),
    CHINESE_TRADITIONAL("Chinese TR", "zh-TW", "\uD83C\uDDE8\uD83C\uDDF3"),
    KOREAN("Korean", "ko", "\uD83C\uDDF0\uD83C\uDDF7"),
    RUSSIAN("Russian", "ru", "\uD83C\uDDF7\uD83C\uDDFA"),
    TAJIK("Tajik", "tg", "\uD83C\uDDF9\uD83C\uDDEF"),
    TURKISH("Turkish", "tr", "\uD83C\uDDF9\uD83C\uDDF7"),
    UZBEK("Uzbek", "uz", "\uD83C\uDDFA\uD83C\uDDFF"),
    HINDI("Hindi", "hi", "\uD83C\uDDEE\uD83C\uDDF3"),
    MALAYALAM("Malayalam", "ml", "\uD83C\uDDEE\uD83C\uDDF3"),
    URDU("Urdu", "ur", "\uD83C\uDDF5\uD83C\uDDF0"),
    PERSIAN("Persian", "fa", "\uD83C\uDDEE\uD83C\uDDF7"),
    INDONESIAN("Indonesian", "id", "\uD83C\uDDEE\uD83C\uDDE9"),
    GERMAN("German", "de", "\uD83C\uDDE9\uD83C\uDDEA"),
    FRENCH("French", "fr", "\uD83C\uDDEB\uD83C\uDDF7"),
    BACK("Back", "back", null),
    AUTO("Auto", "au", "\uD83D\uDD87");

    private final String text;
    private final String code;
    private final String sticker;


    public String getText() {
        return text;
    }

    public String getCode() {
        return code;
    }

    public String getSticker() {
        return sticker;
    }

    QueryButtons(String text, String code, String sticker) {
        this.text = text;
        this.code = code;
        this.sticker = sticker;
    }

    public static QueryButtons getLanguage(String code) {
        for (QueryButtons value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
