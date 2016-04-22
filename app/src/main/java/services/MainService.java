package services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

import com.onesignal.OneSignal;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

import constants.ApplicationConstants;
import storage.MySharedPreferences;
import util.HttpManager;

/**
 * Created by prasadsawant on 3/21/16.
 */
public class MainService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private String email, password, firstName, lastName, contactNumber, deviceId, playerId;
    private SecureRandom random = new SecureRandom();
    private static final String TAG = MainService.class.getName();


    public MainService() {
        super("MainService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {

            final String action = intent.getAction();

            switch (action) {

                case ApplicationConstants.ACTION_INITIALIZE:
                    handleInitialization(getApplicationContext());
                    break;
            }
        }

    }


    public static void startInitialization(Context context) {

        Intent intent = new Intent(context, MainService.class);
        intent.setAction(ApplicationConstants.ACTION_INITIALIZE);
        context.startService(intent);

    }

    private void handleInitialization(Context context) {

        boolean isRegistered = MySharedPreferences.getPreferenceBoolean(context, ApplicationConstants.IS_REGISTERED);

        if (isRegistered) {


            HttpManager.instanceOf().createFriendsList(getApplicationContext());

            if (MySharedPreferences.getPreferenceString(getApplicationContext(), ApplicationConstants.PLAYER_ID) == null) {

                OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, String registrationId) {
                        MySharedPreferences.setPreferenceString(getApplicationContext(), ApplicationConstants.PLAYER_ID, userId);
                    }
                });

            }


            Intent intent = new Intent(ApplicationConstants.INITIALIZATION_RESPONSE_EVENT);
            intent.putExtra(ApplicationConstants.EXTRA_STATE_REGISTER, ApplicationConstants.STATE_REGISTERED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


        } else {

            getUserInformation();

        }



    }


    private void getUserInformation() {

        final String nameColumn = "display_name_alt";

        email = MySharedPreferences.getPreferenceString(getApplicationContext(), ApplicationConstants.EMAIL);

        if (email == null)
            email = getEmailAddress();

        Cursor cursor = getApplicationContext().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);

        int count = cursor.getCount();
        cursor.moveToFirst();
        int position = cursor.getPosition();

        if (count == 1 && position == 0) {
            String fullName = cursor.getString(cursor.getColumnIndex(nameColumn));

            String[] fullNameArray;
            fullNameArray = fullName.split(", ");
            firstName = fullNameArray[1];
            lastName = fullNameArray[0];

        }

        password = MySharedPreferences.getPreferenceString(getApplicationContext(), ApplicationConstants.PASSWORD);

        if (password == null)
            password = new BigInteger(130, random).toString(32);

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        contactNumber = telephonyManager.getLine1Number();

        deviceId = getDeviceId();

        if (firstName != null || !firstName.equals("") ||
                lastName != null || !lastName.equals("")) {

            Intent intent = new Intent(ApplicationConstants.INITIALIZATION_RESPONSE_EVENT);
            intent.putExtra(ApplicationConstants.EXTRA_STATE_REGISTER, ApplicationConstants.STATE_REGISTER);
            intent.putExtra(ApplicationConstants.EXTRA_EMAIL, email);
            intent.putExtra(ApplicationConstants.EXTRA_PASSWORD, password);
            intent.putExtra(ApplicationConstants.EXTRA_FIRST_NAME, firstName);
            intent.putExtra(ApplicationConstants.EXTRA_LAST_NAME, lastName);
            intent.putExtra(ApplicationConstants.EXTRA_CONTACT_NO, contactNumber);
            intent.putExtra(ApplicationConstants.EXTRA_DEVICE_ID, deviceId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        } else {

            Intent intent = new Intent(ApplicationConstants.INITIALIZATION_RESPONSE_EVENT);
            intent.putExtra(ApplicationConstants.EXTRA_STATE_REGISTER, ApplicationConstants.STATE_NAME);
            intent.putExtra(ApplicationConstants.EXTRA_EMAIL, email);
            intent.putExtra(ApplicationConstants.EXTRA_PASSWORD, password);
            intent.putExtra(ApplicationConstants.EXTRA_CONTACT_NO, contactNumber);
            intent.putExtra(ApplicationConstants.EXTRA_DEVICE_ID, deviceId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        }


    }

    private String getEmailAddress() {

        AccountManager accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account emailAddress = accounts[0];

        return emailAddress.name;
    }

    private String getDeviceId() {

        String tmDevice, tmSerial, androidId;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        tmDevice = "" + telephonyManager.getDeviceId();
        tmSerial = "" + telephonyManager.getSimSerialNumber();
        androidId = "" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

        return deviceUuid.toString();

    }
}
