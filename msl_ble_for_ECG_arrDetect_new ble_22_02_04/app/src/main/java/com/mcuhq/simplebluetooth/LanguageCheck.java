package com.mcuhq.simplebluetooth;

import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;

import java.util.Locale;

public class LanguageCheck {

    public static String checklanguage(Context context){

        Configuration configuration = context.getResources().getConfiguration();
        LocaleList localeList = configuration.getLocales();
        Locale locale;
        if (!localeList.isEmpty()) {
            locale = localeList.get(0); // 리스트의 첫 번째 로케일이 사용자의 현재 로케일입니다.
        } else {
            // 로케일 리스트가 비어 있을 때 대비책을 마련하세요.
            locale = Locale.getDefault(); // 시스템의 현재 로케일을 가져옵니다.
        }

        String language = locale.getLanguage(); // "en", "ko" 등등

        return language;
    }

}
