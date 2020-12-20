package gr.uom.socialmedianetworkaggregator;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FbInstaUSer {
    private String TAG="Thanos";

    private AccessToken accessToken;
    private String facebookId;
    private String instagramId;
    private String userName;

    private String facebookPageId;

    public String getFacebookId() {
        return facebookId;
    }

    public String getInstagramId() {
        return instagramId;
    }

    public FbInstaUSer(AccessToken accessToken) {
        this.accessToken = accessToken;
        this.facebookId=accessToken.getUserId();


        /*
        //setUserName
        new GraphRequest(
                this.accessToken,
                "/"+facebookId+"/",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.d(TAG,"Graph Request to getUserName");
                            //name = response.getJSONObject().getString("name");
                           userName =response.getJSONObject().getString("name");
                           Log.d(TAG,"User Name - Constractor FbInstaUser :"+userName);

                        } catch (JSONException e) {
                            Log.d(TAG, "Graph request exception to get user name"+e.toString());
                        }
                    }
                }
        ).executeAndWait();


        //set instagramId
        new GraphRequest(
                accessToken,
                "/" + facebookId + "/accounts",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        try {
                            facebookPageId =response.getJSONObject().getJSONArray("data").getJSONObject(1).getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();


         */
    }

    public String getUserName(){
        return this.userName;
    }

    public String getFacebookPageId() {
        return facebookPageId;
    }
}
