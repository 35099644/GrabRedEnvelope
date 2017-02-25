package com.zzy.grabredenvelope;


import de.robv.android.xposed.XSharedPreferences;

public class PreferencesUtils {

    private static XSharedPreferences instance = null;

    private static XSharedPreferences getInstance() {
        if (instance == null) {
            instance = new XSharedPreferences(PreferencesUtils.class.getPackage().getName());
            instance.makeWorldReadable();
        } else {
            instance.reload();
        }
        return instance;
    }

    public static boolean open() {
        return getInstance().getBoolean("open", false);
    }

    public static boolean notSelf() {
        return getInstance().getBoolean("not_self", false);
    }

    public static boolean notWhisper() {
        return getInstance().getBoolean("not_whisper", false);
    }

    public static boolean notMute() {
        return getInstance().getBoolean("not_mute", false);
    }

    public static String maxDelayTime() {
        return getInstance().getString("max_delay_time", "5000");
    }

    public static boolean password() {
        return getInstance().getBoolean("password", false);
    }

    public static String containKeyword() {
        return getInstance().getString("keywords", "");
    }

}


