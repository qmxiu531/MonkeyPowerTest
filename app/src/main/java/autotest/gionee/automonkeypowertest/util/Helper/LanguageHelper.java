package autotest.gionee.automonkeypowertest.util.Helper;


import java.util.Locale;

import autotest.gionee.automonkeypowertest.util.Preference;

public class LanguageHelper {

    public static Language getCurrentLanguage() {
        return new Language(Locale.getDefault().getLanguage());
    }

    public static void setLastLanguage(Language l) {
        Preference.putString("lastLanguage", l.lan);
    }

    public static Language getLastLanguage() {
        String string = Preference.getString("lastLanguage", "");
        return new Language(string);
    }

    public static class Language {
        public String lan = "zh";

        public Language(String lan) {
            this.lan = lan;
        }

        public boolean isChinese() {
            return lan.equals("zh");
        }

        public boolean isEmpty(){
            return lan.equals("");
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Language && lan.equals(((Language) obj).lan);
        }
    }
}
