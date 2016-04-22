package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import constants.ApplicationConstants;
import irrationalstudio.com.shareit.FileShareActivity;
import irrationalstudio.com.shareit.R;
import services.ContactsAsyncTask;
import util.UtilClass;

/**
 * Created by prasadsawant on 4/21/16.
 */
public class ContactsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ContactsFragment.class.getName();
    private ListView lvContacts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoContacts;
    private boolean flag = false;

    public ContactsFragment() {

    }

    public static ContactsFragment newInstance() {

        ContactsFragment contactsFragment = new ContactsFragment();
        return contactsFragment;

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        lvContacts = (ListView) rootView.findViewById(R.id.lv_fragment_contacts);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_fragment_contacts);
        tvNoContacts = (TextView) rootView.findViewById(R.id.tv_fragment_no_contacts);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);

                if (UtilClass.isConnected(getActivity().getApplicationContext())) {
                    ContactsAsyncTask contactsAsyncTask = new ContactsAsyncTask(lvContacts, swipeRefreshLayout, tvNoContacts, flag);
                    contactsAsyncTask.execute(getActivity().getApplicationContext());
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    UtilClass.showToast(getActivity().getApplicationContext(), getString(R.string.no_internet));
                }
            }
        });

        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                TextView tvContact = (TextView) view.findViewById(R.id.tv_contact_number);

                String name = tvName.getText().toString(), contactNumber = tvContact.getText().toString();

                Intent intent = new Intent(getActivity(), FileShareActivity.class);
                intent.putExtra(ApplicationConstants.INTENT_EXTRA_NAME, name);
                intent.putExtra(ApplicationConstants.INTENT_EXTRA_CONTACT_NUMBER, contactNumber);
                startActivity(intent);

            }
        });

        return rootView;

    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);

                if (UtilClass.isConnected(getActivity().getApplicationContext())) {
                    ContactsAsyncTask contactsAsyncTask = new ContactsAsyncTask(lvContacts, swipeRefreshLayout, tvNoContacts, flag);
                    contactsAsyncTask.execute(getActivity().getApplicationContext());
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    UtilClass.showToast(getActivity().getApplicationContext(), getString(R.string.no_internet));
                }
            }
        });

    }
}
