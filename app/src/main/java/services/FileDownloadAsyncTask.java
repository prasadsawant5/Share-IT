package services;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import constants.ApplicationConstants;
import constants.ServerConstants;

/**
 * Created by prasadsawant on 4/21/16.
 */
public class FileDownloadAsyncTask extends AsyncTask<String, Integer, Integer> {

    private Context context;
    private ImageButton ibDownload;

    private static final String TAG = FileDownloadAsyncTask.class.getName();

    public FileDownloadAsyncTask(Context context, ImageButton ibDownload) {

        this.context = context;
        this.ibDownload = ibDownload;

    }

    @Override
    protected Integer doInBackground(String... params) {

        String fileName = params[0];
        int responseCode = 0;

        Log.i(TAG, fileName);

        HttpURLConnection httpURLConnection = null;
        URL url;
        FileOutputStream fileOutputStream;
        File file;

        try {

            url = new URL(ServerConstants.SERVER_URL + ServerConstants.DOWNLOAD_PATH);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);

            httpURLConnection.setRequestMethod(ServerConstants.REQUEST_METHOD_GET);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_FILE_NAME, fileName);


            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    ApplicationConstants.ALBUM);

            if (!file.exists()) {
                file.mkdirs();
                Log.e(TAG, "Directory created");
            }



            Log.i(TAG, file.getPath() + "/" + fileName);

            fileOutputStream = new FileOutputStream(file.getPath() + "/" + fileName);

            int bytesRead;
            byte[] buffer = new byte[ApplicationConstants.BUFFER_SIZE];
            while ((bytesRead = httpURLConnection.getInputStream().read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File(file.getPath() + "/" + fileName)));
            context.sendBroadcast(intent);


            responseCode = httpURLConnection.getResponseCode();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
        }

        return responseCode;
    }

    @Override
    protected void onPostExecute(Integer integer) {

        Log.i(TAG, String.valueOf(integer));

        ibDownload.setEnabled(true);

    }
}
