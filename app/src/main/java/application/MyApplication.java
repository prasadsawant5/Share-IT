package application;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import com.onesignal.OneSignal;
import org.json.JSONException;
import org.json.JSONObject;
import constants.ServerConstants;
import irrationalstudio.com.shareit.MainActivity;

/**
 * Created by prasadsawant on 3/27/16.
 */
public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getName();
    private String fileName, sender;

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.startInit(this).setNotificationOpenedHandler(new NotificationOpenHelper()).init();
        OneSignal.enableNotificationsWhenActive(true);


    }


    private class NotificationOpenHelper implements OneSignal.NotificationOpenedHandler {

        @Override
        public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {

            Log.i(TAG, message);
            Log.i(TAG, additionalData.toString());

            try {
                fileName = additionalData.getString(ServerConstants.JSON_FILE_NAME);
                sender = additionalData.getString(ServerConstants.JSON_SENDER);


                Intent intent = new Intent(MyApplication.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



}
