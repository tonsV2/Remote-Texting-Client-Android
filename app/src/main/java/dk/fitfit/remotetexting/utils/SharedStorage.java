package dk.fitfit.remotetexting.utils;

import android.content.Context;
import android.content.SharedPreferences;


// TODO: Perhaps use this instead
// http://stackoverflow.com/a/6393502/672009
public class SharedStorage {
    public static final String STORAGE_KEY_ID_TOKEN = "idToken";
    private static final String PACKAGE = "dk.fitfit.remotetexting";

    public static boolean save(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String load(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }
}
