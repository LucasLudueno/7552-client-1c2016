package taller2.match_client;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/*  */
public class ProfileManager {
    /* Atributes */
    Context context;

    ProfileManager(Context contextActivity) {
        context = contextActivity;
    }

    /*  */
    public JSONObject getProfile() {
        String profileString = "";
        try {
            String fileName = context.getResources().getString(R.string.profile_filename);
            FileInputStream profileData = context.openFileInput(fileName);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(profileData));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = bufferReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            profileString = stringBuilder.toString();
            profileData.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject profile = null;
        try {
            profile = new JSONObject(profileString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return profile;
    }

    /* */
    public boolean updateProfile(JSONObject profile) {
        try {
            String profileString = String.valueOf(profile);

            FileOutputStream outputStream = context.openFileOutput(context.getResources().getString(R.string.profile_filename), Context.MODE_PRIVATE);
            outputStream.write(profileString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            return false;
            //LOG - ERROR
        }
        return true;
    }
}
