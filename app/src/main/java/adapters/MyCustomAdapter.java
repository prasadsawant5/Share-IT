package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import constants.ApplicationConstants;
import constants.ServerConstants;
import irrationalstudio.com.shareit.R;

/**
 * Created by prasadsawant on 4/7/16.
 */
public class MyCustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> arrayList;
    private static final String TAG = MyCustomAdapter.class.getName();

    public MyCustomAdapter(Context context, ArrayList arrayList) {

        this.arrayList = arrayList;
        this.context = context;

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) { return arrayList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        HashMap<String, String> contact;
        contact = arrayList.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.layout_row, parent, false);

        TextView tvName = (TextView) row.findViewById(R.id.tv_name);
        TextView tvContact = (TextView) row.findViewById(R.id.tv_contact_number);

        String name = contact.get(ApplicationConstants.HASH_KEY_FIRSTNAME) + " " + contact.get(ApplicationConstants.HASH_KEY_LASTNAME);

        tvName.setText(name);
        tvContact.setText(contact.get(ApplicationConstants.HASH_KEY_CONTACT));

        ImageView ivAvatar = (ImageView) row.findViewById(R.id.iv_avatar);

        if (contact.get(ApplicationConstants.HASH_KEY_AVATAR_PATH) == null) {
            ivAvatar.setBackgroundResource(R.drawable.avatar);
        } else {
            Log.d(TAG, ServerConstants.SERVER_URL + ServerConstants.AVATAR_PATH + contact.get(ApplicationConstants.HASH_KEY_AVATAR_PATH));
            Picasso.with(context).load(ServerConstants.SERVER_URL +
                    ServerConstants.AVATAR_PATH +
                    contact.get(ApplicationConstants.HASH_KEY_AVATAR_PATH))
                    .error(R.drawable.avatar).into(ivAvatar);
        }

        return row;
    }
}
