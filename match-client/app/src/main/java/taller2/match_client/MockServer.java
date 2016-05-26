package taller2.match_client;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

/* This class represent the Aplication Server. Its a test class  */
public class MockServer {
    /* Attributes */
    private static final MockServer mockServer = new MockServer();
    private Lock mutex;
    private Context context;
    private JSONObject userProfile;
    private ArrayList<JSONObject> matches;
    private ArrayList<JSONObject> possibleMatches;
    private ArrayList<JSONObject> conversations;

    MockServer() {
    }

    /* Initialize profile, matches list, conversations and possible match list to test. */
    public void initialize(Context context) {
        this.context = context;
        createProfile();
        createMatchList();
        createPossibleMatchList();
        createConversations();
    }

    private void createProfile() {
        /** USER PROFILE ***/
        try {
            JSONObject interest1 = new JSONObject("{\"category\": \"music/band\", \"value\": \"Pink Floyd\"}");
            JSONObject interest2 = new JSONObject("{\"category\": \"music/band\", \"value\": \"The Beatles\"}");
            JSONObject interest3 = new JSONObject("{\"category\": \"outdoors\", \"value\": \"running\"}");
            JSONObject interest4 = new JSONObject("{\"category\": \"sex\", \"value\": \"men\"}");
            JSONArray interests = new JSONArray();
            interests.put(interest1);
            interests.put(interest2);
            interests.put(interest3);
            interests.put(interest4);

            userProfile = new JSONObject("{\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\" },\"email\":\"lucas@gmail.com\",\"alias\":\"Milito\",\"name\":\"lucas\",\"password\":\"contraseña\"}");
            Bitmap photodefault = BitmapFactory.decodeResource(context.getResources(), R.drawable.foto_perfil_prueba);
            Base64Converter bs64 = new Base64Converter();
            String base64 = bs64.bitmapToBase64(photodefault);
            userProfile.put(context.getResources().getString(R.string.profilePhoto), base64);
            userProfile.put(context.getResources().getString(R.string.interests), interests);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createMatchList() {
        /*** MATCHES ***/
        matches = new ArrayList<JSONObject>();
        try {
            JSONObject seba = new JSONObject("{\"name\":\"seba\",\"alias\":\"seba\",\"email\":\"seba@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");
            JSONObject fede = new JSONObject("{\"name\":\"fede\",\"alias\":\"fede\",\"email\":\"fede@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");
            JSONObject eze = new JSONObject("{\"name\":\"eze\",\"alias\":\"eze\",\"email\":\"eze@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");

            Bitmap photodefault = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_match);
            Base64Converter bs64 = new Base64Converter();
            String base64 = bs64.bitmapToBase64(photodefault);
            seba.put(context.getResources().getString(R.string.profilePhoto), base64);
            fede.put(context.getResources().getString(R.string.profilePhoto), base64);
            eze.put(context.getResources().getString(R.string.profilePhoto), base64);

            matches.add(seba);
            matches.add(fede);
            matches.add(eze);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createPossibleMatchList() {
        /*** POSSIBLE MATCHES ***/
        possibleMatches = new ArrayList<JSONObject>();
        try {
            JSONObject pmatch1 = new JSONObject("{\"name\":\"wolverine\",\"alias\":\"wolverine\",\"email\":\"wolverine@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");
            JSONObject pmatch2 = new JSONObject("{\"name\":\"yoda\",\"alias\":\"yoda\",\"email\":\"yoda@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");
            JSONObject pmatch3 = new JSONObject("{\"name\":\"terminator\",\"alias\":\"terminator\",\"email\":\"terminator@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");
            JSONObject pmatch4 = new JSONObject("{\"name\":\"anonymous\",\"alias\":\"anonymous\",\"email\":\"anonymous@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");
            JSONObject pmatch5 = new JSONObject("{\"name\":\"mario\",\"alias\":\"mario\",\"email\":\"mario@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");
            JSONObject pmatch6 = new JSONObject("{\"name\":\"soldier\",\"alias\":\"soldier\",\"email\":\"soldier@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");

            Base64Converter bs64 = new Base64Converter();
            Bitmap pmatch1BitmapPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.pmatch1);
            Bitmap pmatch2BitmapPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.pmatch2);
            Bitmap pmatch3BitmapPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.pmatch3);
            Bitmap pmatch4BitmapPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.pmatch4);
            Bitmap pmatch5BitmapPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.pmatch5);
            Bitmap pmatch6BitmapPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.pmatch6);
            String pmatch1Photo = bs64.bitmapToBase64(pmatch1BitmapPhoto);
            String pmatch2Photo = bs64.bitmapToBase64(pmatch2BitmapPhoto);
            String pmatch3Photo = bs64.bitmapToBase64(pmatch3BitmapPhoto);
            String pmatch4Photo = bs64.bitmapToBase64(pmatch4BitmapPhoto);
            String pmatch5Photo = bs64.bitmapToBase64(pmatch5BitmapPhoto);
            String pmatch6Photo = bs64.bitmapToBase64(pmatch6BitmapPhoto);

            pmatch1.put(context.getResources().getString(R.string.profilePhoto), pmatch1Photo);
            pmatch2.put(context.getResources().getString(R.string.profilePhoto), pmatch2Photo);
            pmatch3.put(context.getResources().getString(R.string.profilePhoto), pmatch3Photo);
            pmatch4.put(context.getResources().getString(R.string.profilePhoto), pmatch4Photo);
            pmatch5.put(context.getResources().getString(R.string.profilePhoto), pmatch5Photo);
            pmatch6.put(context.getResources().getString(R.string.profilePhoto), pmatch6Photo);

            possibleMatches.add(pmatch1);
            possibleMatches.add(pmatch2);
            possibleMatches.add(pmatch3);
            possibleMatches.add(pmatch4);
            possibleMatches.add(pmatch5);
            possibleMatches.add(pmatch6);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createConversations() {
        /*** CONVERSATIONS ***/
        try {
            conversations = new ArrayList<JSONObject>();
            JSONObject seba_conv = new JSONObject();
            JSONObject fede_conv = new JSONObject();
            JSONObject eze_conv = new JSONObject();

            JSONObject seba_msg1 = new JSONObject("{\"sendFrom\":\"seba@gmail.com\",\"msg\":\"hola soy seba\"}");
            JSONObject seba_msg2 = new JSONObject("{\"sendFrom\":\"lucas@gmail.com\",\"msg\":\"hola soy lucas\"}");
            JSONObject fede_msg1 = new JSONObject("{\"sendFrom\":\"fede@gmail.com\",\"msg\":\"hola soy fede\"}");
            JSONObject fede_msg2 = new JSONObject("{\"sendFrom\":\"lucas@gmail.com\",\"msg\":\"hola soy lucas\"}");
            JSONObject eze_msg1 = new JSONObject("{\"sendFrom\":\"eze@gmail.com\",\"msg\":\"hola soy eze\"}");
            JSONObject eze_msg2 = new JSONObject("{\"sendFrom\":\"lucas@gmail.com\",\"msg\":\"hola soy lucas\"}");

            JSONArray seba_msg = new JSONArray();
            seba_msg.put(seba_msg1);
            seba_msg.put(seba_msg2);
            JSONArray fede_msg = new JSONArray();
            fede_msg.put(fede_msg1);
            fede_msg.put(fede_msg2);
            JSONArray eze_msg = new JSONArray();
            eze_msg.put(eze_msg1);
            eze_msg.put(eze_msg2);

            seba_conv.put("email","seba@gmail.com");
            seba_conv.put("messages",seba_msg);
            fede_conv.put("email", "fede@gmail.com");
            fede_conv.put("messages", fede_msg);
            eze_conv.put("email", "eze@gmail.com");
            eze_conv.put("messages",eze_msg);

            conversations.add(seba_conv);
            conversations.add(fede_conv);
            conversations.add(eze_conv);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* Return an instance */
    public static MockServer getInstance() {
        return mockServer;
    }

    /* Represent Login Request */
    public String Login(JSONObject data) {
        String userEmail = "";
        try {
            userEmail = data.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String profileString = String.valueOf(userProfile);
        if (userEmail.compareTo("lucas@gmail.com") == 0) {
            return "200:" + profileString;
        }
        return "201:" + profileString;
    }

    /* Represent Get Matches Request */
    public String getMatches(String userMailJson) {
        JSONArray match_array = new JSONArray();
        int count = 1;
        for (int i = 0; i < count; ++i) {
            if (matches.size() != 0) {
                JSONObject match = matches.get(i);
                match_array.put(match);
                matches.remove(i);
            }
        }
        JSONObject matchesToSend = new JSONObject();
        try {
            matchesToSend.put("matches",match_array); // array de matches
        } catch (JSONException e) {
            e.printStackTrace();
            return "201:error";
        }
        return "200:" + matchesToSend.toString();
    }

    /* Represent Get Possible Matches Request */
    public String getPossibleMatches(String pmatchRequest) {
        JSONObject possibleMatchesToSend = new JSONObject();
        JSONArray possibleMatchesArray = new JSONArray();
        int count = 0;
        try {
            JSONObject posMatchRequest = new JSONObject(pmatchRequest);
            String userEmail = posMatchRequest.getString("email");
            count = posMatchRequest.getInt("count");

            for (int i = 0; i < count; ++i) {
                if (possibleMatches.size() > 0) {
                    JSONObject pmatch = possibleMatches.get(0); //Get the first pos match
                    possibleMatchesArray.put(pmatch);
                    possibleMatches.remove(0);
                }
            }
            possibleMatchesToSend.put("possibleMatches", possibleMatchesArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return "201:error";
        }
        return "200:" + possibleMatchesToSend.toString();
    }

    /* Represent Get conversation Request */
    public String getConversation(String conversationRequest) {
        JSONObject conversationToSend = new JSONObject();

        JSONObject conversationJson = null;
        String emailSrc = null;
        try {
            conversationJson = new JSONObject(conversationRequest);
            emailSrc = conversationJson.getString("emailSrc");

            JSONObject conversation = new JSONObject();
            JSONObject msg1 = new JSONObject("{\"sendFrom\":" + "\"" + emailSrc + "\"" + ",\"msg\":\"hola, soy " + emailSrc + "\"" + "}");
            JSONObject msg2 = new JSONObject("{\"sendFrom\":" + "\"" + emailSrc + "\"" + ",\"msg\":\"hola, soy " + emailSrc + "\"" + "}");

            JSONArray messages = new JSONArray();
            messages.put(msg1);
            messages.put(msg2);
            conversation.put("messages", messages);
            conversation.put("email", emailSrc);

            conversationToSend.put("conversation", conversation);
        } catch (JSONException e) {
            e.printStackTrace();
            return "201:error";
        }
        return "200:" + conversationToSend.toString();
    }

    /* Represent Send Pos Match Interest Request */
    public String like_dont(String interest) {
        try {
            JSONObject interestJson = new JSONObject(interest);
            String emailSrc = interestJson.getString("emailSrc");
            String emailDst = interestJson.getString("emailDst");
        } catch (JSONException e) {
            e.printStackTrace();
            return "201:error";
        }
        return "200:ok";
    }
}