package services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import constants.ApplicationConstants;
import constants.ServerConstants;
import irrationalstudio.com.shareit.R;
import storage.MySQLiteHelper;
import storage.MySharedPreferences;
import util.UtilClass;

/**
 * Created by prasadsawant on 4/18/16.
 */
public class FileSendAsyncTask extends AsyncTask<String, Integer, Integer> {

    private Context context;
    private ProgressBar pbProgress;
    private String receiver, filePath, fileName, contactNumber, email, contact;
    private MySQLiteHelper dbHelper;
    private SQLiteDatabase database;
    private String[] projection = { MySQLiteHelper.COLUMN_CONTACT_NUMBER, MySQLiteHelper.COLUMN_EMAIL };
    private String[] name;
    private static final String TAG = FileSendAsyncTask.class.getName();

    public FileSendAsyncTask(Context context, ProgressBar pbProgress) {
        this.context = context;
        this.pbProgress = pbProgress;
    }

    @Override
    protected Integer doInBackground(String... params) {

        Cursor cursor;
        String[] selectionArgs;
        String selection = MySQLiteHelper.COLUMN_FIRST_NAME + " = ? AND " + MySQLiteHelper.COLUMN_LAST_NAME + " = ?";

        receiver = params[0];
        filePath = params[1];

        name = receiver.split(" ");

        if (params[2] != null) {
            contactNumber = params[2];
            selection += " AND " + MySQLiteHelper.COLUMN_CONTACT_NUMBER + " = ?";
            selectionArgs = new String[]{ name[0], name[1], contactNumber };
        } else {

            selectionArgs = new String[]{ name[0], name[1] };
        }

        HttpURLConnection httpURLConnection = null;
        URL url;
        DataOutputStream dataOutputStream = null;
        FileInputStream fileInputStream = null;
        int responseCode = 0;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File file = new File(filePath);
        fileName = file.getName();
        Log.i(TAG, fileName);


        dbHelper = new MySQLiteHelper(context);
        database = dbHelper.getReadableDatabase();


        cursor = database.query(true, MySQLiteHelper.TABLE_CONTACTS, projection, selection, selectionArgs, null, null, null, null);

        while (cursor.moveToNext()) {
            contact = cursor.getString(0);
            email = cursor.getString(1);
        }


        try {
            fileInputStream = new FileInputStream(file);

            url = new URL(ServerConstants.SERVER_URL + ServerConstants.FILE_PATH);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);

            httpURLConnection.setRequestMethod(ServerConstants.REQUEST_METHOD_POST);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_SOURCE, MySharedPreferences.getPreferenceString(context, ApplicationConstants.EMAIL));
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_DESTINATION, email);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_CONNECTION, ServerConstants.REQUEST_KEEP_ALIVE);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_ENC_TYPE, ServerConstants.REQUEST_MULTIPLE_FORM_DATA);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_CONTENT_TYPE, ServerConstants.REQUEST_MULTIPLE_FORM_DATA + ";boundary=" + ServerConstants.BOUNDARY);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_USER_FILE, url.toString());

            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(ServerConstants.TWO_HYPENS + ServerConstants.BOUNDARY + ServerConstants.LINE_END);
            dataOutputStream.writeBytes(ServerConstants.REQUEST_CONTENT_DISPOSITION + ": form-data; name=\""+ ServerConstants.REQUEST_USER_FILE + "\";filename=\"" + fileName + "\"" + ServerConstants.LINE_END);
            dataOutputStream.writeBytes(ServerConstants.LINE_END);

            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(ServerConstants.LINE_END);
            dataOutputStream.writeBytes(ServerConstants.TWO_HYPENS + ServerConstants.BOUNDARY +
                    ServerConstants.TWO_HYPENS + ServerConstants.LINE_END);

            responseCode = httpURLConnection.getResponseCode();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            httpURLConnection.disconnect();

            if (!cursor.isClosed())
                cursor.close();

            if (database.isOpen())
                database.close();

            dbHelper.close();

            try {
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return responseCode;
    }

    @Override
    protected void onPostExecute(Integer integer) {

        pbProgress.setVisibility(View.GONE);

        if (integer == ServerConstants.HTTP_CREATED)
            UtilClass.showToast(context, context.getResources().getString(R.string.file_share_success));
        else
            UtilClass.showToast(context, context.getResources().getString(R.string.server_error));

    }
}
