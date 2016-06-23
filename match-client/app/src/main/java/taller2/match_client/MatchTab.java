package taller2.match_client;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import taller2.match_client.Helpers.Base64Converter;
import taller2.match_client.Match_Manage.MatchManagerProxy;

/* Match Tab is a Fragment that has match information. On create get match alias, profile photo
 * and age and show them in activity. */
public class MatchTab extends Fragment {
    /* Attributes */
    private MatchManagerProxy matchManager;
    private String matchEmail;
    private static final String TAG = "MatchTab";

    public MatchTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        matchEmail = getArguments().getString(getResources().getString(taller2.match_client.R.string.email));
        return inflater.inflate(taller2.match_client.R.layout.activity_match_info, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        Log.i(TAG, "Create MatchTab");
        super.onActivityCreated(savedInstanceState);

        /*** Match Manager ***/
        matchManager = MatchManagerProxy.getInstance();

        Base64Converter b64conv = new Base64Converter();
        JSONObject matchData = matchManager.getMatch(matchEmail);
        String alias = "";
        int age = 0;
        Bitmap matchPhoto = null;
        try {
            alias = matchData.getString(getResources().getString(taller2.match_client.R.string.alias));
            age = matchData.getInt(getResources().getString(taller2.match_client.R.string.age));
            matchPhoto = b64conv.Base64ToBitmap(matchData.getString(getResources().getString(taller2.match_client.R.string.photoProfile)));
        } catch (JSONException e) {
            Log.w(TAG, "Can't get match information from Json Match Profile");
        }
        ImageView photo = (ImageView) getView().findViewById(taller2.match_client.R.id.matchProfilePhoto);
        TextView aliasView = (TextView) getView().findViewById(taller2.match_client.R.id.matchAlias);
        TextView ageView = (TextView) getView().findViewById(taller2.match_client.R.id.matchAge);
        photo.setImageBitmap(matchPhoto);
        aliasView.setText(alias);
        ageView.setText(getResources().getString(taller2.match_client.R.string.age_en) + ": " + String.valueOf(age));
        Log.d(TAG, "Match Tab is created");
    }
}