package broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.json.JSONObject;

import constants.ServerConstants;
import storage.MySQLiteHelper;

/**
 * Created by prasadsawant on 4/22/16.
 */
public class OneSignalBroadcastReceiver extends WakefulBroadcastReceiver {

    private String fileName, sender;
    private MySQLiteHelper dbHelper;
    private SQLiteDatabase database;
    private String[] projections = { MySQLiteHelper.COLUMN_FIRST_NAME, MySQLiteHelper.COLUMN_LAST_NAME, MySQLiteHelper.COLUMN_CONTACT_NUMBER };
    private String selection = MySQLiteHelper.COLUMN_EMAIL + " = ?";
    private String[] selectionArgs;
    private Cursor cursor;

    private static final String TAG = OneSignalBroadcastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getBundleExtra(ServerConstants.JSON_DATA);

        try {

            JSONObject custom = new JSONObject(bundle.getString(ServerConstants.JSON_CUSTOM));

            if (custom.has(ServerConstants.JSON_A)) {

                JSONObject additionalData = custom.getJSONObject(ServerConstants.JSON_A);

                fileName = additionalData.getString(ServerConstants.JSON_FILE_NAME);
                sender = additionalData.getString(ServerConstants.JSON_SENDER);

                Log.i(TAG, fileName);
                Log.i(TAG, sender);

                dbHelper = new MySQLiteHelper(context);
                database = dbHelper.getWritableDatabase();

                selectionArgs = new String[]{sender};
                cursor = database.query(true, MySQLiteHelper.TABLE_CONTACTS, projections, selection, selectionArgs, null, null, null, null);

                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MySQLiteHelper.COLUMN_FILE_NAME, fileName);
                    contentValues.put(MySQLiteHelper.COLUMN_FIRST_NAME, cursor.getString(0));
                    contentValues.put(MySQLiteHelper.COLUMN_LAST_NAME, cursor.getString(1));
                    contentValues.put(MySQLiteHelper.COLUMN_EMAIL, sender);
                    contentValues.put(MySQLiteHelper.COLUMN_CONTACT_NUMBER, cursor.getString(2));

                    long id = database.insert(MySQLiteHelper.TABLE_FILES, null, contentValues);
                    Log.i(TAG, String.valueOf(id));

                    cursor.close();
                }

                if (database.isOpen()) {
                    database.close();
                    dbHelper.close();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
}
