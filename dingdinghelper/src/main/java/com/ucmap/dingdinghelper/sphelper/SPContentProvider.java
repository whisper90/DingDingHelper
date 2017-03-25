package com.ucmap.dingdinghelper.sphelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.CURSOR_COLUMN_NAME;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.CURSOR_COLUMN_TYPE;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.CURSOR_COLUMN_VALUE;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.SEPARATOR;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_BOOLEAN;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_CLEAN;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_CONTAIN;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_FLOAT;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_GET_ALL;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_INT;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_LONG;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.TYPE_STRING;
import static com.ucmap.dingdinghelper.sphelper.ConstantUtil.VALUE;


public class SPContentProvider extends ContentProvider {

    @Override
    public boolean onCreate() {

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String[] path = uri.getPath().split(SEPARATOR);
        String type = path[1];
        if (type.equals(TYPE_GET_ALL)) {
            Map<String, ?> all = SPManager.getAll(getContext());
            if (all != null) {
                MatrixCursor cursor = new MatrixCursor(new String[]{CURSOR_COLUMN_NAME, CURSOR_COLUMN_TYPE, CURSOR_COLUMN_VALUE});
                Set<String> keySet = all.keySet();
                for (String key : keySet) {
                    Object[] rows = new Object[3];
                    rows[0] = key;
                    rows[2] = all.get(key);
                    if (rows[2] instanceof Boolean) {
                        rows[1] = TYPE_BOOLEAN;
                    } else if (rows[2] instanceof String) {
                        rows[1] = TYPE_STRING;
                    } else if (rows[2] instanceof Integer) {
                        rows[1] = TYPE_INT;
                    } else if (rows[2] instanceof Long) {
                        rows[1] = TYPE_LONG;
                    } else if (rows[2] instanceof Float) {
                        rows[1] = TYPE_FLOAT;
                    }
                    cursor.addRow(rows);
                }
                return cursor;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        String[] path = uri.getPath().split(SEPARATOR);
        String type = path[1];
        String key = path[2];
        if (type.equals(TYPE_CONTAIN)) {
            return SPManager.contains(getContext(), key) + "";
        }
        return "" + SPManager.get(getContext(), key, type);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String[] path = uri.getPath().split(SEPARATOR);
        String type = path[1];
        String key = path[2];
        Object obj = (Object) values.get(VALUE);
        if (obj != null)
            SPManager.save(getContext(), key, obj);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String[] path = uri.getPath().split(SEPARATOR);
        String type = path[1];
        if (type.equals(TYPE_CLEAN)) {
            SPManager.clear(getContext());
            return 0;
        }
        String key = path[2];
        if (SPManager.contains(getContext(), key)) {
            SPManager.remove(getContext(), key);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        insert(uri, values);
        return 0;
    }
}