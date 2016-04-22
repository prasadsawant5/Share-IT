package services;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import adapters.MyCustomAdapter;
import util.HttpManager;

/**
 * Created by prasadsawant on 4/5/16.
 */
public class ContactsAsyncTask extends AsyncTask<Context, String, ArrayList<HashMap<String, String>>> {

    private Context context;
    private ArrayList<HashMap<String, String>> arrayList;
    private ListView listView;
    private boolean flag;
    private TextView tvNoContacts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = ContactsAsyncTask.class.getName();

    public ContactsAsyncTask(ListView listView, SwipeRefreshLayout swipeRefreshLayout, TextView tvNoContacts, boolean flag) {

        this.listView = listView;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.tvNoContacts = tvNoContacts;
        this.flag = flag;

    }


    @Override
    protected ArrayList doInBackground(Context... params) {

        this.context = params[0];

        HttpManager httpManager = HttpManager.instanceOf();

        if (flag)
            httpManager.createFriendsList(context);

        arrayList = httpManager.getFriendsList(context);

        return arrayList;
    }

    @Override
    protected void onPostExecute(ArrayList arrayList) {

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (arrayList != null) {
            if (arrayList.size() > 0) {
                tvNoContacts.setVisibility(View.GONE);
                listView.setAdapter(new MyCustomAdapter(context, arrayList));
            } else {
                tvNoContacts.setVisibility(View.VISIBLE);
            }
        }



    }
}
