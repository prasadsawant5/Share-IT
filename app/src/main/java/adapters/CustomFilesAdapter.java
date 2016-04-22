package adapters;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import constants.ApplicationConstants;
import irrationalstudio.com.shareit.R;
import services.FileDownloadAsyncTask;
import util.UtilClass;

/**
 * Created by prasadsawant on 4/20/16.
 */
public class CustomFilesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> arrayList;
    private static final String TAG = CustomFilesAdapter.class.getName();

    public CustomFilesAdapter(Context context, ArrayList arrayList) {

        this.arrayList = arrayList;
        this.context = context;

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        HashMap<String, String> files;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.layout_row_files, parent, false);

        final TextView tvFileName = (TextView) row.findViewById(R.id.tv_file_name);
        TextView tvName = (TextView) row.findViewById(R.id.tv_sender);
        final ImageButton ibDownload = (ImageButton) row.findViewById(R.id.ib_download);

        files = arrayList.get(position);

        tvFileName.setText(files.get(ApplicationConstants.HASH_ARRAY_FILE_NAME));
        tvName.setText(files.get(ApplicationConstants.HASH_ARRAY_FULLNAME));

        ibDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ibDownload.setEnabled(false);

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" +
                        ApplicationConstants.ALBUM + "/" + tvFileName.getText().toString());
                Log.i(TAG, file.getPath());

                if (!file.exists()) {

                    if (UtilClass.isConnected(context)) {
                        FileDownloadAsyncTask fileDownloadAsyncTask = new FileDownloadAsyncTask(context, ibDownload);
                        fileDownloadAsyncTask.execute(tvFileName.getText().toString());
                    } else {
                        ibDownload.setEnabled(true);
                        UtilClass.showToast(context, context.getString(R.string.no_internet));
                    }

                } else {

                    ibDownload.setEnabled(true);

                    UtilClass.showToast(context, context.getString(R.string.file_exists));

                }

            }
        });

        return row;
    }
}
