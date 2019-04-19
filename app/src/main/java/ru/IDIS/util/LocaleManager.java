package ru.IDIS.util;

import android.util.Log;

import com.idis.android.redx.CharacterSet;

// This class manages CharacterSet of Android OS and DVR character data.
public class LocaleManager 
{
    private static final String TAG = LocaleManager.class.getSimpleName();

    // Debug Log    
    private static final boolean DEBUG_LOG = false;
    
    // returns languageCode of Locale
    public static String getSystemLanguage()
    {
        java.util.Locale loc = java.util.Locale.getDefault();
        if (DEBUG_LOG) {
            Log.v(TAG, "system language = " + loc.getLanguage());
        }
        
        return loc.getLanguage();
    }

    // returns countryCode of Lacale
    public static String getSystemCountry()
    {
        java.util.Locale loc = java.util.Locale.getDefault();
        if (DEBUG_LOG) {
            Log.v(TAG, "system country = " + loc.getCountry());
        }
        
        return loc.getCountry();
    }

    public static CharacterSet getCurrentCharacterSet()
    {
        return findCharacterSetByRegionStrings(getSystemLanguage(), getSystemCountry());
    }
    
    // This mothod is used only in 2 cases.
    //
    // Case 1.
    // Application is launched for the first time, 
    // and codepage is not set yet.
    //
    // Case 2.
    // Application is updated, and old settings are converted to newer version.
    // Older CountryCode and LanguageCode are translated to Codepage.
    public static CharacterSet findCharacterSetByRegionStrings(String language, String country)
    {
        CharacterSet characterSet = CharacterSet.Western;
    
        if (language.equalsIgnoreCase("en") ||
            language.equalsIgnoreCase("fr") ||
            language.equalsIgnoreCase("de") ||
            language.equalsIgnoreCase("it")) {
            characterSet = CharacterSet.Western;
        }
        else if (language.equalsIgnoreCase("zh")) {
            if (country.equalsIgnoreCase("CN")) {   // People's Republic of China (PRC)
                characterSet = CharacterSet.ChineseSimplified;
            }
            else {  // Republic of China (ROC)
                characterSet = CharacterSet.ChineseTraditional;
            }
        }
        else if (language.equalsIgnoreCase("ja")) {
            characterSet = CharacterSet.Japanese;
        }
        else if (language.equalsIgnoreCase("ko")) {
            characterSet = CharacterSet.Korean;
        }
        // below cases are for other languages which are not supported by android.
        else if (language.equalsIgnoreCase("cs") || // czech
                  language.equalsIgnoreCase("hu") || // hungarian    
                  language.equalsIgnoreCase("pl") || // polish
                  language.equalsIgnoreCase("ro") || // romanian
                  language.equalsIgnoreCase("hr") || // croatian
                  language.equalsIgnoreCase("sk") || // slovak
                  language.equalsIgnoreCase("sq") || // albanian
                  language.equalsIgnoreCase("sl") || // slovenian
                  language.equalsIgnoreCase("az") || // azeri
                  language.equalsIgnoreCase("uz") || // uzbek
                  language.equalsIgnoreCase("sr")) { // serbian 
            characterSet = CharacterSet.CentralEuropean;
        }
        else if (language.equalsIgnoreCase("et") || // estonian
                  language.equalsIgnoreCase("lv") || // latvian
                  language.equalsIgnoreCase("lt")) { // lithuanian
            characterSet = CharacterSet.Baltic;
        }
        else if (language.equalsIgnoreCase("bg") || // bulgarian
                  language.equalsIgnoreCase("ru") || // russian
                  language.equalsIgnoreCase("uk") || // ukrainian
                  language.equalsIgnoreCase("be") || // belarusian
                  language.equalsIgnoreCase("mk") || // macedonian
                  language.equalsIgnoreCase("tt")) { // tatar
            characterSet = CharacterSet.Cyrillic;
        }
        else if (language.equalsIgnoreCase("ar")) { // support for arabic
            characterSet = CharacterSet.Arabic;
        }
        else if (language.equalsIgnoreCase("vi")) { // support for vietnamese
            characterSet = CharacterSet.Vietnamese;
        }
        else if (language.equalsIgnoreCase("tr")) { // support for turkish
            characterSet = CharacterSet.Turkish; 
        }
        else if (language.equalsIgnoreCase("th")) { // support for thai
            characterSet = CharacterSet.Thai;
        }

        return characterSet;
    }

    public static CharacterSet findCharacterSetByCharacterSetIndex(int charsetIndex)
    {
        return CharacterSet.fromIndex(charsetIndex);
    }

    public static CharacterSet findCharacterSetByCodepage(int codePage)
    {
        return CharacterSet.fromCodePage(codePage);
    }
}

