package irrationalstudio.com.shareit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.onesignal.OneSignal;

import java.util.HashMap;

import constants.ApplicationConstants;
import services.RegisterationAsyncTask;
import util.UtilClass;

/**
 * Created by prasadsawant on 3/27/16.
 */
public class EmailContactActivity extends AppCompatActivity {

    private static final String TAG = EmailContactActivity.class.getName();
    private EditText etEmail, etPhone;
    private ImageView ivAvatar;
    private Button btnRegister;
    private String email, contactNo, firstName, lastName, deviceId, password, avatarPath, playerId;
    private HashMap<String, String> userInfo = new HashMap();

    private static final int SELECT_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_contact);



        etEmail = (EditText) findViewById(R.id.et_email);
        etPhone = (EditText) findViewById(R.id.et_phone);
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);

        btnRegister = (Button) findViewById(R.id.btn_register);

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_IMAGE);
            }
        });

        email = this.getIntent().getStringExtra(ApplicationConstants.EXTRA_EMAIL);
        password = this.getIntent().getStringExtra(ApplicationConstants.EXTRA_PASSWORD);
        firstName = this.getIntent().getStringExtra(ApplicationConstants.EXTRA_FIRST_NAME);
        lastName = this.getIntent().getStringExtra(ApplicationConstants.EXTRA_LAST_NAME);
        contactNo = this.getIntent().getStringExtra(ApplicationConstants.EXTRA_CONTACT_NO);
        deviceId = this.getIntent().getStringExtra(ApplicationConstants.EXTRA_DEVICE_ID);
        avatarPath = ApplicationConstants.AVATAR;

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                playerId = userId;
            }
        });



        if (!contactNo.equals("") || contactNo != null)
            etPhone.setText(contactNo);

        if (!email.equals("") || email != null)
            etEmail.setText(email);
        else
            etEmail.setFocusable(true);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ((etPhone.getText().toString().equals("") || etPhone.getText() == null) &&
                        (etEmail.getText().toString().equals("") || etEmail.getText() == null)) {

                    UtilClass.showToast(getApplicationContext(), getResources().getString(R.string.no_email_contact));


                } else if (etPhone.getText().toString().equals("") || etPhone.getText() == null) {

                    Log.d(TAG, "No contact no!");
                    UtilClass.showToast(getApplicationContext(), getResources().getString(R.string.no_contact));


                } else if (etEmail.getText().toString().equals("") || etEmail.getText() == null) {

                    Log.d(TAG, "No email!");
                    UtilClass.showToast(getApplicationContext(), getResources().getString(R.string.no_email));

                } else {
                    Log.d(TAG, "All OK");

                    email = etEmail.getText().toString();
                    contactNo = etPhone.getText().toString();

                    if ((!email.equals("") || email != null) && (!password.equals("") && password != null) &&
                            (!firstName.equals("") || firstName != null) && (!lastName.equals("") || lastName != null) &&
                            (!contactNo.equals("") || contactNo != null) && (!deviceId.equals("") || deviceId != null) &&
                            (!avatarPath.equals("") || avatarPath != null)) {

                        userInfo.put(ApplicationConstants.HASH_KEY_EMAIL, email);
                        userInfo.put(ApplicationConstants.HASH_KEY_PASSWORD, password);
                        userInfo.put(ApplicationConstants.HASH_KEY_FIRSTNAME, firstName);
                        userInfo.put(ApplicationConstants.HASH_KEY_LASTNAME, lastName);
                        userInfo.put(ApplicationConstants.HASH_KEY_CONTACT, contactNo);
                        userInfo.put(ApplicationConstants.HASH_KEY_DEVICE_ID, deviceId);
                        userInfo.put(ApplicationConstants.HASH_KEY_AVATAR_PATH, avatarPath);
                        userInfo.put(ApplicationConstants.HASH_KEY_PLAYER_ID, playerId);

                        etEmail.setEnabled(false);
                        etPhone.setEnabled(false);
                        ivAvatar.setEnabled(false);
                        btnRegister.setEnabled(false);

                        RegisterationAsyncTask registerationAsyncTask = new RegisterationAsyncTask(getApplicationContext());
                        registerationAsyncTask.execute(userInfo);
                    }
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                Uri selectedImageUri = data.getData();

                avatarPath = getImagePath(selectedImageUri);

                if (!avatarPath.equals("") || avatarPath != null)
                    ivAvatar.setImageURI(selectedImageUri);
            }
        }
    }

    private String getImagePath(Uri uri) {

        String imagePath = null;
        if (uri == null)
            return null;

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            imagePath = cursor.getString(columnIndex);
        }

        cursor.close();

        return imagePath;
    }

    public void enableInput() {

        etEmail.setEnabled(true);
        etPhone.setEnabled(true);
        ivAvatar.setEnabled(true);
        btnRegister.setEnabled(true);

        UtilClass.showToast(getApplicationContext(), getResources().getString(R.string.server_error));

    }
}
