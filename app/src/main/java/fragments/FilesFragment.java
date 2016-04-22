package fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import adapters.CustomFilesAdapter;
import constants.ApplicationConstants;
import irrationalstudio.com.shareit.R;
import storage.MySQLiteHelper;

/**
 * Created by prasadsawant on 4/21/16.
 */
public class FilesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FilesFragment.class.getName();

    private String fileName, sender;
    private MySQLiteHelper dbHelper;
    private SQLiteDatabase database;
    private String[] projections = { MySQLiteHelper.COLUMN_FIRST_NAME, MySQLiteHelper.COLUMN_LAST_NAME, MySQLiteHelper.COLUMN_CONTACT_NUMBER };
    private String[] mainProjections = { MySQLiteHelper.COLUMN_FILE_NAME, MySQLiteHelper.COLUMN_FIRST_NAME, MySQLiteHelper.COLUMN_LAST_NAME };
    private Cursor cursor;
    private ListView lvFiles;
    private SwipeRefreshLayout srlFiles;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();


    public FilesFragment() {

    }



    public static FilesFragment newInstance() {

        FilesFragment filesFragment = new FilesFragment();
        return filesFragment;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_files, container, false);

        lvFiles = (ListView) rootView.findViewById(R.id.lv_fragment_files);
        srlFiles = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_fragment_files);

        Intent intent = getActivity().getIntent();
        fileName = intent.getStringExtra(ApplicationConstants.INTENT_EXTRA_FILE_NAME);
        sender = intent.getStringExtra(ApplicationConstants.INTENT_EXTRA_SENDER);

        srlFiles.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                R.color.colorPrimaryDark);
        srlFiles.setOnRefreshListener(this);


        srlFiles.post(new Runnable() {
            @Override
            public void run() {

                createListView();

            }


        });


        return rootView;

    }


    private void createListView() {

        srlFiles.setRefreshing(true);

        dbHelper = new MySQLiteHelper(getActivity().getApplicationContext());
        database = dbHelper.getReadableDatabase();

        cursor = database.query(true, MySQLiteHelper.TABLE_FILES, mainProjections, null, null, null, null, null, null);

        if (cursor.getCount() > 0) {

            if (arrayList.size() > 0)
                arrayList.clear();

            int i = 0;
            while (cursor.moveToNext()) {

                HashMap<String, String> row = new HashMap();
                String fullName = cursor.getString(1) + " " + cursor.getString(2);

                row.put(ApplicationConstants.HASH_ARRAY_FILE_NAME, cursor.getString(0));
                row.put(ApplicationConstants.HASH_ARRAY_FULLNAME, fullName);

                arrayList.add(i, row);
                i++;

            }

            cursor.close();

            if (database.isOpen()) {
                database.close();
                dbHelper.close();
            }

            Log.i(TAG, "File found, setting adapter!");

            lvFiles.setAdapter(new CustomFilesAdapter(getActivity().getApplicationContext(), arrayList));



        }

        srlFiles.setRefreshing(false);

    }

    @Override
    public void onRefresh() {
        createListView();
    }


}
