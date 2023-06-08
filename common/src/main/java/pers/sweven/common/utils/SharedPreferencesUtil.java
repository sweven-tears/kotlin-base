package pers.sweven.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;
import java.util.Set;

public class SharedPreferencesUtil {

    private static final String TAG = SharedPreferencesUtil.class.getName();
    private static String FILE_NAME;
    private final SharedPreferences preferences;
    private final String fileName;

    public static void initDefault(String fileName){
        FILE_NAME = fileName;
    }

    private SharedPreferencesUtil(Context context, String name) {
        this.fileName = name;
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil with(Context context) {
        if (FILE_NAME == null || FILE_NAME.length() == 0) {
            throw new RuntimeException("You haven't configured SharedPreferencesUtil.initDefault() yet.");
        }
        return new SharedPreferencesUtil(context, FILE_NAME);
    }

    public static SharedPreferencesUtil with(Context context, String name) {
        return new SharedPreferencesUtil(context, name);
    }


    public SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

    /**
     * save preference
     *
     * @param key key
     * @param o   value
     */
    public void save(String key, Object o) {
        SharedPreferences.Editor editor = getEditor();
        if (o instanceof String) {
            editor.putString(key, (String) o);
        } else if (o instanceof Integer) {
            editor.putInt(key, (int) o);
        } else if (o instanceof Float) {
            editor.putFloat(key, (float) o);
        } else if (o instanceof Boolean) {
            editor.putBoolean(key, (boolean) o);
        } else if (o instanceof Long) {
            editor.putLong(key, (long) o);
        }
        editor.apply();
    }

    /**
     * save preference
     *
     * @param key
     * @param stringSet
     */
    public void save(String key, Set<String> stringSet) {
        SharedPreferences.Editor editor = getEditor();
        editor.putStringSet(key, stringSet).apply();
    }

    /**
     * @param key
     * @return default null
     */
    public String getString(String key) {
        Log.i(TAG, fileName + "[" + key + "]");
        Map<String, ?> map = preferences.getAll();
        return (String) map.get(key);
    }

    /**
     * @param key key
     * @return default 0
     */
    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    /**
     * @param key key
     * @return default 0
     */
    public float getFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    /**
     * @param key key
     * @return default false
     */
    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    /**
     * @param key key
     * @return default 0
     */
    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }

    /**
     * @param key key
     * @return default null
     */
    public Set<String> getStringSet(String key) {
        return preferences.getStringSet(key, null);
    }

    public String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return preferences.getFloat(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return preferences.getLong(key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> defValue) {
        return preferences.getStringSet(key, defValue);
    }

    public boolean contains(String key) {
        return preferences.contains(key);
    }

    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    public void remove(String... name) {
        SharedPreferences.Editor editor = preferences.edit();
        for (String aName : name) {
            editor.remove(aName);
        }
        editor.apply();
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}