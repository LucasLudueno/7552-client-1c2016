package taller2.match_client;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


/* Match Tab is a Fragment that has match information. On create get match alias, profile photo
 * and age and show them in activity. */
public class MatchTab extends Fragment {
    /* Attributes */
    private MatchManager matchManager;
    private String matchEmail;

    public MatchTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        matchEmail = getArguments().getString(getResources().getString(R.string.email));
        return inflater.inflate(R.layout.activity_match_info, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*** Match Manager ***/
        matchManager = MatchManager.getInstance();

        Base64Converter b64conv = new Base64Converter();
        JSONObject matchData = matchManager.getMatch(matchEmail);
        String alias = "";
        String birthday = "";
        Bitmap matchPhoto = null;
        try {
            alias = matchData.getString(getResources().getString(R.string.alias));
            birthday = matchData.getString(getResources().getString(R.string.birthday));
            matchPhoto = b64conv.Base64ToBitmap(matchData.getString(getResources().getString(R.string.photoProfile)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImageView photo = (ImageView)getView().findViewById(R.id.matchProfilePhoto);
        TextView aliasAndAge = (TextView)getView().findViewById(R.id.matchAliasAndAge);
        photo.setImageBitmap(matchPhoto);
        int age = ActivityHelper.getAge(birthday);
        aliasAndAge.setText(alias + ", " + String.valueOf(age));
    }
}