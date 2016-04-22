package constants;

/**
 * Created by prasadsawant on 3/27/16.
 */
public class ServerConstants {

    public static final String PARSE_SERVER = "http://prasad-parse.herokuapp.com/parse/";
    public static final String SERVER_URL = "http://192.168.0.4:3000/";
    public static final String REGISTER_PATH = "api/register/";
    public static final String AVATAR_UPLOAD_PATH = "api/uploads/avatar";
    public static final String CONTACTS_PATH = "api/contacts";
    public static final String AVATAR_PATH = "uploads/avatars/";
    public static final String FILE_PATH = "api/uploads/file";
    public static final String DOWNLOAD_PATH = "api/downloads/";


    public static final String JSON_FIRST_NAME = "firstName";
    public static final String JSON_LAST_NAME = "lastName";
    public static final String JSON_EMAIL = "email";
    public static final String JSON_PASSWORD = "password";
    public static final String JSON_CONTACT_NUMBER = "contactNumber";
    public static final String JSON_DEVICE_ID = "deviceId";
    public static final String JSON_AVATAR = "avatar";
    public static final String JSON_PLAYER_ID = "playerId";
    public static final String JSON_CONTACTS = "contacts";
    public static final String JSON_CONTACT = "contact";
    public static final String JSON_FILE_NAME = "fileName";
    public static final String JSON_SENDER = "sender";
    public static final String JSON_DATA = "data";
    public static final String JSON_CUSTOM = "custom";
    public static final String JSON_A = "a";

    public static final String REQUEST_METHOD_POST = "POST";
    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_CONTENT_TYPE = "Content-Type";
    public static final String REQUEST_CONNECTION = "Connection";
    public static final String REQUEST_KEEP_ALIVE = "Keep-Alive";
    public static final String REQUEST_ENC_TYPE = "ENCTYPE";
    public static final String REQUEST_USER_PHOTO = "userPhoto";
    public static final String REQUEST_USER_FILE = "userFile";
    public static final String REQUEST_FILE_NAME = "fileName";
    public static final String REQUEST_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String REQUEST_MULTIPLE_FORM_DATA = "multipart/form-data";
    public static final String REQUEST_SOURCE = "source";
    public static final String REQUEST_DESTINATION = "destination";

    public static final String LINE_END = "\r\n";
    public static final String TWO_HYPENS = "--";
    public static final String BOUNDARY = "*****";

    public static final int HTTP_CREATED = 201;
    public static final int HTTP_OK = 200;

    public static final String RESPONSE_JSON_TOKEN = "token";
    public static final String RESPONSE_JSON_EXPIRES_IN = "expiresIn";

    public static final String PARSE_CHANNEL = "com.irrationalstudio.parse.shareit";
}
