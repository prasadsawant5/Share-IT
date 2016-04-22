package storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by prasadsawant on 4/3/16.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = MySQLiteHelper.class.getName();

    public static final String TABLE_CONTACTS = "contacts";
    public static final String TABLE_FILES = "files";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_CONTACT_NUMBER = "contact_number";
    public static final String COLUMN_AVATAR = "avatar";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_FILE_NAME = "file_name";


    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_CONTACTS + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_FIRST_NAME + " text, "
            + COLUMN_LAST_NAME + " text, " + COLUMN_EMAIL + " text not null, " + COLUMN_CONTACT_NUMBER + " text not null, "
            + COLUMN_AVATAR + " text, "+ COLUMN_PLAYER_ID + " text not null);";

    private static final String DATABASE_CREATE_FILES = "create table " + TABLE_FILES + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_FILE_NAME + " text not null, " + COLUMN_FIRST_NAME + " text, " + COLUMN_LAST_NAME + " text, "
            + COLUMN_EMAIL + " text not null, " + COLUMN_CONTACT_NUMBER + " text);";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE_FILES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
        onCreate(db);
    }
}
