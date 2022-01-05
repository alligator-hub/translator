package org.algo.translator.enums;

public enum LanguageType {
    ARABIC("Arabic", "ar", "\uD83C\uDDE6\uD83C\uDDEA"),
    CHINESE_SIMPLIFIELD("Chinese", "zh-CN", "\uD83C\uDDE8\uD83C\uDDF3"),
    ENGLISH("English", "en", "\uD83C\uDDFA\uD83C\uDDF8"),
    KOREAN("Korean", "ko", "\uD83C\uDDF0\uD83C\uDDF7"),
    RUSSIAN("Russian", "ru", "\uD83C\uDDF7\uD83C\uDDFA"),
    TAJIK("Tajik", "tg", "\uD83C\uDDF9\uD83C\uDDEF"),
    TURKISH("Turkish", "tr", "\uD83C\uDDF9\uD83C\uDDF7"),
    UZBEK("Uzbek", "uz", "\uD83C\uDDFA\uD83C\uDDFF"),
    HINDI("Hindi", "hi", "\uD83C\uDDEE\uD83C\uDDF3"),
    URDU("Urdu", "ur", "\uD83C\uDDF5\uD83C\uDDF0"),
    AUTO("Auto", "au", "\uD83D\uDD87");

    private final String language;
    private final String code;
    private final String flag;


    public String getLanguage() {
        return language;
    }

    public String getCode() {
        return code;
    }

    public String getFlag() {
        return flag;
    }

    LanguageType(String language, String code, String flag) {
        this.language = language;
        this.code = code;
        this.flag = flag;
    }

    public static LanguageType getLanguage(String code) {
        for (LanguageType value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
