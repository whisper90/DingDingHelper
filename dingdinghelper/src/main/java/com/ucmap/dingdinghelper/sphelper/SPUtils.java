package com.ucmap.dingdinghelper.sphelper;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.CONTENT_URI;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.CURSOR_COLUMN_NAME;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.CURSOR_COLUMN_TYPE;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.CURSOR_COLUMN_VALUE;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.NULL_STRING;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.SEPARATOR;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_BOOLEAN;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_CLEAN;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_CONTAIN;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_FLOAT;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_GET_ALL;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_INT;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_LONG;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_STRING;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_STRING_SET;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.VALUE;


public class SPUtils {
    public static final String COMMA_REPLACEMENT = "__COMMA__";


    private static ReentrantReadWriteLock mReentrantReadWriteLock = new ReentrantReadWriteLock();
    public static Context context;

    public static void checkContext() {
        if (context == null) {
            throw new IllegalStateException("context has not been initialed");
        }
    }

    public static void init(Application application) {
        context = application.getApplicationContext();
    }

    public static void save(String name, Boolean t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(ConstantUtil.CONTENT_URI + SEPARATOR + ConstantUtil.TYPE_BOOLEAN + SEPARATOR + name);
        ContentValues cv = new ContentValues();
        cv.put(ConstantUtil.VALUE, t);
        ReentrantReadWriteLock.WriteLock mWriteLock = mReentrantReadWriteLock.writeLock();
        mWriteLock.lock();
        try {
            cr.update(uri, cv, null, null);
        } finally {
            mWriteLock.unlock();
        }
    }

    public static void save(String name, String t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(ConstantUtil.CONTENT_URI + SEPARATOR + ConstantUtil.TYPE_STRING + SEPARATOR + name);
        ContentValues cv = new ContentValues();
        cv.put(ConstantUtil.VALUE, t);
        ReentrantReadWriteLock.WriteLock mWriteLock = mReentrantReadWriteLock.writeLock();
        mWriteLock.lock();
        try {
            cr.update(uri, cv, null, null);
        } finally {
            mWriteLock.unlock();
        }
    }

    public  static void save(String name, Integer t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(ConstantUtil.CONTENT_URI + SEPARATOR + TYPE_INT + SEPARATOR + name);
        ContentValues cv = new ContentValues();
        cv.put(ConstantUtil.VALUE, t);
        ReentrantReadWriteLock.WriteLock mWriteLock = mReentrantReadWriteLock.writeLock();
        mWriteLock.lock();
        try {
            cr.update(uri, cv, null, null);
        } finally {
            mWriteLock.unlock();
        }
    }

    public  static void save(String name, Long t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(ConstantUtil.CONTENT_URI + SEPARATOR + TYPE_LONG + SEPARATOR + name);
        ContentValues cv = new ContentValues();
        cv.put(ConstantUtil.VALUE, t);
        ReentrantReadWriteLock.WriteLock mWriteLock = mReentrantReadWriteLock.writeLock();
        mWriteLock.lock();
        try {
            cr.update(uri, cv, null, null);
        } finally {
            mWriteLock.unlock();
        }
    }

    public  static void save(String name, Float t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(ConstantUtil.CONTENT_URI + SEPARATOR + ConstantUtil.TYPE_BOOLEAN + SEPARATOR + name);
        ContentValues cv = new ContentValues();
        cv.put(ConstantUtil.VALUE, t);
        ReentrantReadWriteLock.WriteLock mWriteLock = mReentrantReadWriteLock.writeLock();
        mWriteLock.lock();
        try {
            cr.update(uri, cv, null, null);
        } finally {
            mWriteLock.unlock();
        }
    }


    public  static void save(String name, Set<String> t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(CONTENT_URI + SEPARATOR + TYPE_STRING_SET + SEPARATOR + name);
        ContentValues cv = new ContentValues();
        Set<String> convert = new HashSet<>();
        for (String string : t) {
            convert.add(string.replace(",", COMMA_REPLACEMENT));
        }
        cv.put(VALUE, convert.toString());
        ReentrantReadWriteLock.WriteLock mWriteLock = mReentrantReadWriteLock.writeLock();
        mWriteLock.lock();
        try {
            cr.update(uri, cv, null, null);
        } finally {
            mWriteLock.unlock();
        }
    }

    public static String getString(String name, String defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(CONTENT_URI + SEPARATOR + TYPE_STRING + SEPARATOR + name);
        ReentrantReadWriteLock.ReadLock mReadLock = mReentrantReadWriteLock.readLock();
        mReadLock.lock();
        try {
            String rtn = cr.getType(uri);
            if (rtn == null || rtn.equals(NULL_STRING)) {
                return defaultValue;
            }
            return rtn;
        } finally {
            mReadLock.unlock();
        }

    }

    public static int getInt(String name, int defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(CONTENT_URI + SEPARATOR + TYPE_INT + SEPARATOR + name);
        ReentrantReadWriteLock.ReadLock mReadLock = mReentrantReadWriteLock.readLock();
        mReadLock.lock();
        try {
            String rtn = cr.getType(uri);
            if (rtn == null || rtn.equals(NULL_STRING)) {
                return defaultValue;
            }
            return Integer.parseInt(rtn);
        } finally {
            mReadLock.unlock();
        }

    }

    public static float getFloat(String name, float defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(CONTENT_URI + SEPARATOR + TYPE_FLOAT + SEPARATOR + name);
        ReentrantReadWriteLock.ReadLock mReadLock = mReentrantReadWriteLock.readLock();
        mReadLock.lock();
        try {

            String rtn = cr.getType(uri);
            if (rtn == null || rtn.equals(NULL_STRING)) {
                return defaultValue;
            }
            return Float.parseFloat(rtn);
        } finally {
            mReadLock.unlock();
        }

    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(CONTENT_URI + SEPARATOR + TYPE_BOOLEAN + SEPARATOR + name);
        ReentrantReadWriteLock.ReadLock mReadLock = mReentrantReadWriteLock.readLock();
        mReadLock.lock();
        try {
            String rtn = cr.getType(uri);
            if (rtn == null || rtn.equals(NULL_STRING)) {
                return defaultValue;
            }
            return Boolean.parseBoolean(rtn);
        } finally {
            mReadLock.unlock();
        }

    }

    public static long getLong(String name, long defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(CONTENT_URI + SEPARATOR + TYPE_LONG + SEPARATOR + name);
        ReentrantReadWriteLock.ReadLock mReadLock = mReentrantReadWriteLock.readLock();
        mReadLock.lock();
        try {
            String rtn = cr.getType(uri);
            if (rtn == null || rtn.equals(NULL_STRING)) {
                return defaultValue;
            }
            return Long.parseLong(rtn);
        } finally {
            mReadLock.unlock();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getStringSet(String name, Set<String> defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(CONTENT_URI + SEPARATOR + TYPE_STRING_SET + SEPARATOR + name);
        ReentrantReadWriteLock.ReadLock mReadLock = mReentrantReadWriteLock.readLock();
        mReadLock.lock();
        String rtn = null;
        try {
            rtn = cr.getType(uri);
        } finally {
            mReadLock.unlock();
        }

        if (rtn == null || rtn.equals(NULL_STRING)) {
            return defaultValue;
        }
        if (!rtn.matches("\\[.*\\]")) {
            return defaultValue;
        }
        String sub = rtn.substring(1, rtn.length() - 1);
        String[] spl = sub.split(", ");
        Set<String> returns = new HashSet<>();
        for (String t : spl) {
            returns.add(t.replace(COMMA_REPLACEMENT, ", "));
        }
        return returns;
    }

    public static boolean contains(String name) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(CONTENT_URI + SEPARATOR + TYPE_CONTAIN + SEPARATOR + name);
        String rtn = cr.getType(uri);
        if (rtn == null || rtn.equals(NULL_STRING)) {
            return false;
        } else {
            return Boolean.parseBoolean(rtn);
        }
    }

    public static void remove(String name) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(CONTENT_URI + SEPARATOR + TYPE_LONG + SEPARATOR + name);
        cr.delete(uri, null, null);
    }

    public static void clear() {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(CONTENT_URI + SEPARATOR + TYPE_CLEAN);
        ReentrantReadWriteLock.WriteLock mWriteLock = mReentrantReadWriteLock.writeLock();
        mWriteLock.lock();
        try {
            cr.delete(uri, null, null);
        } finally {
            mWriteLock.unlock();
        }
    }

    public static Map<String, ?> getAll() {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(CONTENT_URI + SEPARATOR + TYPE_GET_ALL);
        ReentrantReadWriteLock.ReadLock mReadLock = mReentrantReadWriteLock.readLock();
        mReadLock.lock();
        Cursor cursor = null;
        try {

            cursor = cr.query(uri, null, null, null, null);
        } finally {
            mReadLock.unlock();
        }
        HashMap resultMap = new HashMap();
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(CURSOR_COLUMN_NAME);
            int typeIndex = cursor.getColumnIndex(CURSOR_COLUMN_TYPE);
            int valueIndex = cursor.getColumnIndex(CURSOR_COLUMN_VALUE);
            do {
                String key = cursor.getString(nameIndex);
                String type = cursor.getString(typeIndex);
                Object value = null;
                if (type.equalsIgnoreCase(TYPE_STRING)) {
                    value = cursor.getString(valueIndex);
                    if (((String) value).contains(COMMA_REPLACEMENT)) {
                        String str = (String) value;
                        if (str.matches("\\[.*\\]")) {
                            String sub = str.substring(1, str.length() - 1);
                            String[] spl = sub.split(", ");
                            Set<String> returns = new HashSet<>();
                            for (String t : spl) {
                                returns.add(t.replace(COMMA_REPLACEMENT, ", "));
                            }
                            value = returns;
                        }
                    }
                } else if (type.equalsIgnoreCase(TYPE_BOOLEAN)) {
                    value = cursor.getString(valueIndex);
                } else if (type.equalsIgnoreCase(TYPE_INT)) {
                    value = cursor.getInt(valueIndex);
                } else if (type.equalsIgnoreCase(TYPE_LONG)) {
                    value = cursor.getLong(valueIndex);
                } else if (type.equalsIgnoreCase(TYPE_FLOAT)) {
                    value = cursor.getFloat(valueIndex);
                } else if (type.equalsIgnoreCase(TYPE_STRING_SET)) {
                    value = cursor.getString(valueIndex);
                }
                resultMap.put(key, value);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return resultMap;
    }

}