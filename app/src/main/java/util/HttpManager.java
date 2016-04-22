package util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import constants.ApplicationConstants;
import constants.ServerConstants;
import storage.MySQLiteHelper;
import storage.MySharedPreferences;

/**
 * Created by prasadsawant on 3/28/16.
 */
public class HttpManager {

    private static final String TAG = HttpManager.class.getName();

    public static HttpManager instanceOf() {
        return new HttpManager();
    }

    public int uploadAvatar(String avatarPath, String fileName) {


        HttpURLConnection httpURLConnection = null;
        URL url;
        DataOutputStream dataOutputStream = null;
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        int responseCode = 0;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File file = new File(avatarPath);
        String extension = file.getName().substring((file.getName().indexOf('.')), file.getName().length());

        try {
            fileInputStream = new FileInputStream(file);

            url = new URL(ServerConstants.SERVER_URL + ServerConstants.AVATAR_UPLOAD_PATH);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);

            httpURLConnection.setRequestMethod(ServerConstants.REQUEST_METHOD_POST);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_CONNECTION, ServerConstants.REQUEST_KEEP_ALIVE);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_ENC_TYPE, ServerConstants.REQUEST_MULTIPLE_FORM_DATA);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_CONTENT_TYPE, ServerConstants.REQUEST_MULTIPLE_FORM_DATA + ";boundary=" + ServerConstants.BOUNDARY);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_USER_PHOTO, fileName);

            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(ServerConstants.TWO_HYPENS + ServerConstants.BOUNDARY + ServerConstants.LINE_END);
            dataOutputStream.writeBytes(ServerConstants.REQUEST_CONTENT_DISPOSITION + ": form-data; name=\"userPhoto\";filename=\"" + fileName + extension + "\"" + ServerConstants.LINE_END);
            dataOutputStream.writeBytes(ServerConstants.LINE_END);

            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(ServerConstants.LINE_END);
            dataOutputStream.writeBytes(ServerConstants.TWO_HYPENS + ServerConstants.BOUNDARY +
                    ServerConstants.TWO_HYPENS + ServerConstants.LINE_END);

            responseCode = httpURLConnection.getResponseCode();



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            httpURLConnection.disconnect();

            try {
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return responseCode;

    }

    public int registerUser(HashMap userInfo, Context context) {


        int responseCode = 0;
        StringBuilder sb = new StringBuilder();
        URL url;
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        OutputStream outputStream = null;
        String accessToken;
        int accessExpiry;
        long accessExpiryMilli, currentMilli;
        String extension, avatar = null;

        if (!userInfo.get(ApplicationConstants.HASH_KEY_AVATAR_PATH).toString().equals(ApplicationConstants.AVATAR)) {
            File file = new File(userInfo.get(ApplicationConstants.HASH_KEY_AVATAR_PATH).toString());
            extension = file.getName().substring((file.getName().indexOf('.')), file.getName().length());
            avatar = userInfo.get(ApplicationConstants.HASH_KEY_EMAIL) + extension;
        }

        try {
            url = new URL(ServerConstants.SERVER_URL + ServerConstants.REGISTER_PATH);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(ServerConstants.REQUEST_METHOD_POST);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_CONTENT_TYPE, "application/json; charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);


            JSONObject object = new JSONObject();
            object.put(ServerConstants.JSON_FIRST_NAME, userInfo.get(ApplicationConstants.HASH_KEY_FIRSTNAME));
            object.put(ServerConstants.JSON_LAST_NAME, userInfo.get(ApplicationConstants.HASH_KEY_LASTNAME));
            object.put(ServerConstants.JSON_EMAIL, userInfo.get(ApplicationConstants.HASH_KEY_EMAIL));
            object.put(ServerConstants.JSON_PASSWORD, userInfo.get(ApplicationConstants.HASH_KEY_PASSWORD));
            object.put(ServerConstants.JSON_CONTACT_NUMBER, userInfo.get(ApplicationConstants.HASH_KEY_CONTACT));
            object.put(ServerConstants.JSON_DEVICE_ID, userInfo.get(ApplicationConstants.HASH_KEY_DEVICE_ID));
            object.put(ServerConstants.JSON_AVATAR, avatar);
            object.put(ServerConstants.JSON_PLAYER_ID, userInfo.get(ApplicationConstants.HASH_KEY_PLAYER_ID));


            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(object.toString().getBytes("UTF-8"));

            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                sb.append(line);

            responseCode = httpURLConnection.getResponseCode();

            if (responseCode == ServerConstants.HTTP_CREATED) {

                JSONObject responseJson = new JSONObject(sb.toString());
                accessToken = responseJson.getString(ServerConstants.RESPONSE_JSON_TOKEN);

                accessExpiry = Integer.parseInt(responseJson.getString(ServerConstants.RESPONSE_JSON_EXPIRES_IN));

                accessExpiryMilli = accessExpiry * 60000;

                currentMilli = System.currentTimeMillis();

                MySharedPreferences.setPreferenceString(context, ApplicationConstants.EMAIL, userInfo.get(ApplicationConstants.HASH_KEY_EMAIL).toString());
                MySharedPreferences.setPreferenceString(context, ApplicationConstants.PASSWORD, userInfo.get(ApplicationConstants.HASH_KEY_PASSWORD).toString());
                MySharedPreferences.setPreferenceString(context, ApplicationConstants.ACCESS_TOKEN, accessToken);
                MySharedPreferences.setPreferenceLong(context, ApplicationConstants.ACCESS_TOKEN_EXPIRY_MILLI, accessExpiryMilli);
                MySharedPreferences.setPreferenceLong(context, ApplicationConstants.CURRENT_MILLI, currentMilli);

                if (userInfo.get(ApplicationConstants.HASH_KEY_PLAYER_ID).toString() != null) {
                    Log.d(TAG, "PLAYER_ID not null");
                    MySharedPreferences.setPreferenceString(context, ApplicationConstants.PLAYER_ID, userInfo.get(ApplicationConstants.HASH_KEY_PLAYER_ID).toString());
                }
                MySharedPreferences.setPreferenceBoolean(context, ApplicationConstants.IS_REGISTERED, true);


            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

            try {

                outputStream.flush();
                outputStream.close();

                bufferedReader.close();

                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return responseCode;
    }

    public void createFriendsList(Context context) {

        HttpURLConnection httpURLConnection = null;
        URL url;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        int responseCode = 0;
        HashMap<String, String> hashMap = new HashMap<>();

        try {
            JSONObject object = new JSONObject();
            object.put(ServerConstants.JSON_EMAIL, MySharedPreferences.getPreferenceString(context, ApplicationConstants.EMAIL));

            JSONArray array = new JSONArray();


            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneNo = phoneNo.replace("(", "");
                            phoneNo = phoneNo.replace(")", "");
                            phoneNo = phoneNo.replace("+", "");
                            phoneNo = phoneNo.replaceAll("-", "");
                            phoneNo = phoneNo.replaceAll(" ", "");

                            if (phoneNo.length() != 10) {
                                if (phoneNo.length() == 11)
                                    phoneNo = phoneNo.substring(1);

                                if (phoneNo.length() == 12)
                                    phoneNo = phoneNo.substring(2);

                                if (phoneNo.length() == 13)
                                    phoneNo = phoneNo.substring(3);

                                if (phoneNo.length() == 14)
                                    phoneNo = phoneNo.substring(4);


                            }

                            if (phoneNo.length() == 10) {
                                hashMap.put(phoneNo, phoneNo);
                            }
                        }
                        pCur.close();
                    }
                }
            }

            if (hashMap.size() > 0) {

                Iterator iterator = hashMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    array.put(pair.getValue());
                }

            }

            object.put(ServerConstants.JSON_CONTACTS, array);


            url = new URL(ServerConstants.SERVER_URL + ServerConstants.CONTACTS_PATH);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(ServerConstants.REQUEST_METHOD_POST);
            httpURLConnection.setRequestProperty(ServerConstants.REQUEST_CONTENT_TYPE, "application/json; charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(object.toString().getBytes("UTF-8"));

            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                sb.append(line);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            httpURLConnection.disconnect();

            try {

                outputStream.flush();
                outputStream.close();

                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public ArrayList getFriendsList(Context context) {

        HttpURLConnection httpURLConnection = null;
        URL url;
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        int responseCode = 0;
        JSONObject jsonObject;
        JSONArray jsonArray;
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String firstName = null, lastName = null, email, contactNumber, avatar = null, playerId;
        Cursor cursor = null;
        String[] mainProjection = { MySQLiteHelper.COLUMN_FIRST_NAME, MySQLiteHelper.COLUMN_LAST_NAME,
                MySQLiteHelper.COLUMN_CONTACT_NUMBER, MySQLiteHelper.COLUMN_AVATAR };
        String sortOrder = MySQLiteHelper.COLUMN_FIRST_NAME + " ASC";
        String[] projection = { MySQLiteHelper.COLUMN_EMAIL, MySQLiteHelper.COLUMN_CONTACT_NUMBER };
        String selection = MySQLiteHelper.COLUMN_EMAIL + " =? AND " + MySQLiteHelper.COLUMN_CONTACT_NUMBER + " =?";
        String[] selectionArgs;
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();


        try {

            url = new URL(ServerConstants.SERVER_URL + ServerConstants.CONTACTS_PATH);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(ServerConstants.REQUEST_METHOD_GET);
            httpURLConnection.addRequestProperty(ServerConstants.JSON_EMAIL, MySharedPreferences.getPreferenceString(context, ApplicationConstants.EMAIL));
            httpURLConnection.setDoInput(true);

            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                sb.append(line);

            responseCode = httpURLConnection.getResponseCode();

            if (responseCode == ServerConstants.HTTP_OK) {


                String json = sb.toString();

                jsonObject = new JSONObject(json);


                jsonArray = jsonObject.getJSONArray(ServerConstants.JSON_CONTACTS);

                if (jsonArray.length() > 0) {
                    JSONObject contacts = jsonArray.getJSONObject(0);
                    JSONArray contact = contacts.getJSONArray(ServerConstants.JSON_CONTACT);

                    for (int i = 0; i < contact.length(); i++) {
                        JSONObject obj = contact.getJSONObject(i);

                        if (obj.has(ServerConstants.JSON_FIRST_NAME))
                            firstName = obj.getString(ServerConstants.JSON_FIRST_NAME);

                        if (obj.has(ServerConstants.JSON_LAST_NAME))
                            lastName = obj.getString(ServerConstants.JSON_LAST_NAME);


                        email = obj.getString(ServerConstants.JSON_EMAIL);
                        contactNumber = obj.getString(ServerConstants.JSON_CONTACT_NUMBER);
                        playerId = obj.getString(ServerConstants.JSON_PLAYER_ID);

                        if (obj.has(ServerConstants.JSON_AVATAR))
                            avatar = obj.getString(ServerConstants.JSON_AVATAR);

                        if ((!email.equals("") || email != null) && (!contactNumber.equals("") || contactNumber != null)) {


                            selectionArgs = new String[]{email, contactNumber};

                            cursor = database.query(MySQLiteHelper.TABLE_CONTACTS, projection, selection, selectionArgs, null, null, null);

                            if (cursor.getCount() != 1) {
                                ContentValues values = new ContentValues();
                                values.put(MySQLiteHelper.COLUMN_FIRST_NAME, firstName);
                                values.put(MySQLiteHelper.COLUMN_LAST_NAME, lastName);
                                values.put(MySQLiteHelper.COLUMN_EMAIL, email);
                                values.put(MySQLiteHelper.COLUMN_CONTACT_NUMBER, contactNumber);
                                values.put(MySQLiteHelper.COLUMN_AVATAR, avatar);
                                values.put(MySQLiteHelper.COLUMN_PLAYER_ID, playerId);

                                database.insert(MySQLiteHelper.TABLE_CONTACTS, null, values);
                            }
                        }
                    }

                    cursor = database.query(true, MySQLiteHelper.TABLE_CONTACTS, mainProjection, null, null, null, null, sortOrder, null);

                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            HashMap<String, String> row = new HashMap<>();

                            row.put(ApplicationConstants.HASH_KEY_FIRSTNAME, cursor.getString(0));
                            row.put(ApplicationConstants.HASH_KEY_LASTNAME, cursor.getString(1));
                            row.put(ApplicationConstants.HASH_KEY_CONTACT, cursor.getString(2));
                            row.put(ApplicationConstants.HASH_KEY_AVATAR_PATH, cursor.getString(3));


                            arrayList.add(row);

                        }

                        cursor.close();
                        dbHelper.close();

                    } else {
                        arrayList = null;
                    }
                } else {
                    arrayList = null;
                }

            }





        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

            httpURLConnection.disconnect();

            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return arrayList;
        }
    }

}
