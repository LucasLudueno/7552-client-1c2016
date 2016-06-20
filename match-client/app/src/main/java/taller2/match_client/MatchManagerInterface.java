package taller2.match_client;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/* Interface for MatchManager */
public interface MatchManagerInterface {
    /* Add match */
    public void addMatch(JSONObject matchData) throws JSONException;

    /* Add a conversation with some match */
    public boolean addConversation(JSONObject completeConversation, boolean isNewConversation);

    /* Return the match list. */
    public List<JSONObject> getMatches();

    /* Return match */
    public JSONObject getMatch(String matchEmail);

    /* Return chat conversation of match */
    public ChatConversation getConversation(String matchEmail);

    /* Return Match List */
    public MatchList getMatchList();

    /* Update Conversations */
    public void updateConversationInFile(String fileName);
}
