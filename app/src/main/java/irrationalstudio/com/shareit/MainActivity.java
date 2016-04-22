package irrationalstudio.com.shareit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import constants.ApplicationConstants;
import constants.ServerConstants;
import services.MainService;
import util.UtilClass;

public class MainActivity extends Activity {

    private ProgressBar progressBar;

    private static final String TAG = MainActivity.class.getName();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String state = intent.getStringExtra(ApplicationConstants.EXTRA_STATE_REGISTER);

            switch (state) {

                case ApplicationConstants.STATE_REGISTER:
                    Intent emailContactIntent = new Intent(MainActivity.this, EmailContactActivity.class);
                    emailContactIntent.putExtras(intent.getExtras());
                    startActivity(emailContactIntent);
                    finish();
                    break;

                case ApplicationConstants.STATE_NAME:
                    Intent nameActivityIntent = new Intent(MainActivity.this, NameActivity.class);
                    nameActivityIntent.putExtras(intent.getExtras());
                    startActivity(nameActivityIntent);
                    finish();
                    break;

                case ApplicationConstants.STATE_REGISTERED:
                    Intent tabbedIntent = new Intent(MainActivity.this, TabbedActivity.class);
                    startActivity(tabbedIntent);
                    finish();
                    break;


            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.pb_spinner);

        if (UtilClass.isConnected(getApplicationContext()))
            MainService.startInitialization(getApplicationContext());
        else {
            UtilClass.showToast(getApplicationContext(), getString(R.string.no_internet));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ApplicationConstants.INITIALIZATION_RESPONSE_EVENT));
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
