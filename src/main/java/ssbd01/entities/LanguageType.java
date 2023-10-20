package ssbd01.entities;

public enum LanguageType {
    pl("pl"),
    en("en"),
    cs("cs");

    String language;

    LanguageType(String language) {
        this.language = language;
    }

    public String toString() {
        return language;
    }
}
