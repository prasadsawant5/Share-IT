package irrationalstudio.com.shareit;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constants.ApplicationConstants;
import services.FileSendAsyncTask;
import storage.MySQLiteHelper;
import util.UtilClass;

/**
 * Created by prasadsawant on 4/14/16.
 */
public class FileShareActivity extends AppCompatActivity {

    private String name, contactNumber;
    private static final String TAG = FileShareActivity.class.getName();
    private AutoCompleteTextView actvTo;
    private MySQLiteHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;
    private String[] projection = { MySQLiteHelper.COLUMN_FIRST_NAME, MySQLiteHelper.COLUMN_LAST_NAME,
            MySQLiteHelper.COLUMN_CONTACT_NUMBER, MySQLiteHelper.COLUMN_EMAIL, MySQLiteHelper.COLUMN_PLAYER_ID };
    private String[] names;
    private List<Map<String, String>> listOfMaps = new ArrayList<Map<String, String>>();
    private ArrayAdapter arrayAdapter;
    private FloatingActionButton fabAttachment;
    private Uri uri;
    private String receiver, fileManagerString, filePath;
    private ProgressBar pbProgress;

    private static final int SELECT_FILE = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_share);

        actvTo = (AutoCompleteTextView) findViewById(R.id.actv_to);
        fabAttachment = (FloatingActionButton) findViewById(R.id.fab_attachment);

        pbProgress = (ProgressBar) findViewById(R.id.pb_progress);

        dbHelper = new MySQLiteHelper(getApplicationContext());
        database = dbHelper.getReadableDatabase();

        cursor = database.query(true, MySQLiteHelper.TABLE_CONTACTS, projection, null, null, null, null, null, null);

        Intent intent = this.getIntent();
        name = intent.getStringExtra(ApplicationConstants.INTENT_EXTRA_NAME);
        contactNumber = intent.getStringExtra(ApplicationConstants.INTENT_EXTRA_CONTACT_NUMBER);

        if (name != null && contactNumber != null) {
            actvTo.setText(name);
        }

        names = new String[cursor.getCount()];

        int i = 0;
        while (cursor.moveToNext()) {

            Map<String, String> hashMaps = new HashMap<>();
            hashMaps.put(ApplicationConstants.HASH_ARRAY_FULLNAME, cursor.getString(0) + " " + cursor.getString(1));
            hashMaps.put(ApplicationConstants.HASH_ARRAY_CONTACT, cursor.getString(2));
            hashMaps.put(ApplicationConstants.HASH_ARRAY_EMAIL, cursor.getString(3));
            hashMaps.put(ApplicationConstants.HASH_ARRAY_PLAYER_ID, cursor.getString(4));

            listOfMaps.add(i, hashMaps);

            names[i] = cursor.getString(0) + " " + cursor.getString(1);
            i++;

        }

        if (!cursor.isClosed()) {
            cursor.close();
            database.close();
        }

        if (names.length > 0) {
            arrayAdapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, names);
            actvTo.setThreshold(2);
            actvTo.setAdapter(arrayAdapter);
        }

        fabAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("*/*");
                startActivityForResult(intent, SELECT_FILE);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            default:

                pbProgress.setVisibility(View.VISIBLE);

                receiver = actvTo.getText().toString();

                if (UtilClass.isConnected(getApplicationContext())) {
                    if (receiver == null || receiver.equals("") || filePath == null) {
                        UtilClass.showToast(getApplicationContext(), getResources().getString(R.string.no_receiver));
                    } else {
                        FileSendAsyncTask fileSendAsyncTask = new FileSendAsyncTask(getApplicationContext(), pbProgress);

                        if (contactNumber == null)
                            fileSendAsyncTask.execute(receiver, filePath);
                        else
                            fileSendAsyncTask.execute(receiver, filePath, contactNumber);
                    }
                } else {
                    pbProgress.setVisibility(View.VISIBLE);
                    UtilClass.showToast(getApplicationContext(), getString(R.string.no_internet));
                }
        }


        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK && requestCode == SELECT_FILE && data != null) {

            uri = data.getData();

            fileManagerString = uri.getPath();

            filePath = getFilePath(uri);

        }
    }

    private String getFilePath(Uri uri) {

        String[] projection = { MediaStore.Files.FileColumns.DATA };
        String path = null;
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            path = cursor.getString(columnIndex);
            Log.i(TAG, path);
        }

        return path;

    }


}
