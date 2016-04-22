package services;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.HashMap;

import constants.ApplicationConstants;
import constants.ServerConstants;
import irrationalstudio.com.shareit.EmailContactActivity;
import irrationalstudio.com.shareit.R;
import irrationalstudio.com.shareit.TabbedActivity;
import util.HttpManager;
import util.UtilClass;

/**
 * Created by prasadsawant on 3/28/16.
 */
public class RegisterationAsyncTask extends AsyncTask<HashMap<String, String>, String, Integer> {

    private static final String TAG = RegisterationAsyncTask.class.getName();
    private int responseCode = 0;

    private Context context;
    private HashMap<String, String> userInfo;

    public RegisterationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(HashMap<String, String>... params) {


        if (UtilClass.isConnected(context)) {
            HttpManager httpManager = new HttpManager();

            userInfo = params[0];

            if (!userInfo.get(ApplicationConstants.HASH_KEY_AVATAR_PATH).equals(ApplicationConstants.AVATAR)) {

                if (httpManager.uploadAvatar(userInfo.get(ApplicationConstants.HASH_KEY_AVATAR_PATH),
                        userInfo.get(ApplicationConstants.HASH_KEY_EMAIL)) == ServerConstants.HTTP_CREATED) {

                    responseCode = httpManager.registerUser(userInfo, context);
                    httpManager.createFriendsList(context);
                } else {

                    responseCode = httpManager.registerUser(userInfo, context);
                    httpManager.createFriendsList(context);
                }

            } else {


                responseCode = httpManager.registerUser(userInfo, context);
                httpManager.createFriendsList(context);
            }
        } else {

            UtilClass.showToast(context, context.getString(R.string.no_internet));

        }

        return responseCode;


    }

    @Override
    protected void onPostExecute(Integer integer) {

        if (integer == ServerConstants.HTTP_CREATED) {

            Intent intent = new Intent(context, TabbedActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } else {

            new EmailContactActivity().enableInput();

        }


    }
}
