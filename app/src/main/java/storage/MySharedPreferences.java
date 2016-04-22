package storage;

import android.content.Context;
import android.content.SharedPreferences;

import constants.ApplicationConstants;

/**
 * Created by prasadsawant on 3/27/16.
 */
public class MySharedPreferences {

    public static String getPreferenceString(Context context, String key) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, null);

        if (value == null || value.equals("")) {
            return null;
        }

        return value;
    }

    public static void setPreferenceString(Context context, String key, String value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public static long getPreferenceLong(Context context, String key) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        long value = sharedPreferences.getLong(key, 0);

        return value;
    }

    public static void setPreferenceLong(Context context, String key, long value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(key, value);
        editor.commit();
    }

    public static boolean getPreferenceBoolean(Context context, String key) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(key, false);

        return value;
    }

    public static void setPreferenceBoolean(Context context, String key, boolean value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }

}
