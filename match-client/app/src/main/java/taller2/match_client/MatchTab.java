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

/* Match Tab is a Fragment that has match information. On create get match alias, profile photo
 * and age and show them in activity. */
public class MatchTab extends Fragment {
    /* Attributes */
    private MatchManager matchManager;
    private String matchEmail;
    private static final String TAG = "MatchTab";

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
        Log.i(TAG, "Create MatchTab");
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
            Log.w(TAG, "Can't get match information from Json Match Profile");
        }
        ImageView photo = (ImageView) getView().findViewById(R.id.matchProfilePhoto);
        TextView aliasView = (TextView) getView().findViewById(R.id.matchAlias);
        TextView ageView = (TextView) getView().findViewById(R.id.matchAge);
        photo.setImageBitmap(matchPhoto);
        int age = ActivityHelper.calculateAge(birthday);
        aliasView.setText(alias);
        ageView.setText(getResources().getString(R.string.age_en) + ": " + String.valueOf(age));
        Log.d(TAG, "Match Tab is created");
    }
}