package taller2.match_client;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* This class represent an UserProfile. It can be PossibleMatch, Match or the Principal UserProfile*/
public class UserProfile {
    /* Attributes */
    private Context cntx;
    private String userName;
    private String alias;
    private String email;
    private String birthday;
    private String sex;
    private String password;
    private JSONObject location;
    private JSONArray interests;

    private static final String TAG = "UserProfile";

    UserProfile(Context cntx, String profile) {
        this.cntx = cntx;
        JSONObject profileJson = null;
        try {
            profileJson = new JSONObject(profile);
            userName = profileJson.getString(cntx.getResources().getString(R.string.userName));
            alias = profileJson.getString(cntx.getResources().getString(R.string.alias));
            email = profileJson.getString(cntx.getResources().getString(R.string.email));
            birthday = profileJson.getString(cntx.getResources().getString(R.string.birthday));
            sex = profileJson.getString(cntx.getResources().getString(R.string.sex));
            password = profileJson.getString(cntx.getResources().getString(R.string.password));
            location = profileJson.getJSONObject(cntx.getResources().getString(R.string.location));
            interests = profileJson.getJSONArray(cntx.getResources().getString(R.string.interests));
        } catch (JSONException e) {
            Log.e(TAG, "Can't construct profile");
        }
    }

    /* Return profile as String */
    public String getProfile() {
        JSONObject profileJson = null;
        try {
            profileJson = new JSONObject();
            profileJson.put(cntx.getResources().getString(R.string.userName), userName);
            profileJson.put(cntx.getResources().getString(R.string.alias), alias);
            profileJson.put(cntx.getResources().getString(R.string.email), email);
            profileJson.put(cntx.getResources().getString(R.string.birthday), birthday);
            profileJson.put(cntx.getResources().getString(R.string.sex), sex);
            profileJson.put(cntx.getResources().getString(R.string.password), password);
            profileJson.put(cntx.getResources().getString(R.string.location), location);
            profileJson.put(cntx.getResources().getString(R.string.interests), interests);
        } catch (JSONException e) {
            Log.e(TAG, "Can't construct UserProfile");
        }
        return profileJson.toString();
    }

    /* Getters */
    public String getUserName() {
        return userName;
    }

    public String getAlias() {
        return alias;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getSex() {
        return sex;
    }

    public JSONObject getLocation() {
        return location;
    }

    public JSONArray getInterests() {
        return interests;
    }

    /* Setters */

}
