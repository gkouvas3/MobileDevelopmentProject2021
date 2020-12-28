package gr.uom.socialmedianetworkaggregator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
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



    public FbInstaUSer(AccessToken accessToken) throws InterruptedException {
        this.accessToken = accessToken;
        this.facebookId=accessToken.getUserId();

        setUserName();
        setFacebookPageId();
        setInstagramId();
    }


    private void setInstagramId() throws InterruptedException{
        GraphRequest.Callback callback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                // Insert your code here
                try {
                    instagramId = response.getJSONObject().getJSONObject("instagram_business_account").getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+facebookPageId+"/",
                callback
        );

        Bundle parameters = new Bundle();
        parameters.putString("fields", "instagram_business_account");
        request.setParameters(parameters);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                GraphResponse gResponse = request.executeAndWait();
            }
        });

        t.start();
        t.join();
    }


    private void setUserName() throws InterruptedException{

        GraphRequest.Callback callback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                // Insert your code here
                try {
                    userName = response.getJSONObject().getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+AccessToken.getCurrentAccessToken().getUserId(),
                callback
        );


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                GraphResponse gResponse = request.executeAndWait();
            }
        });

        t.start();
        t.join();

    }

     private void setFacebookPageId() throws InterruptedException {

         GraphRequest.Callback callback = new GraphRequest.Callback() {
             @Override
             public void onCompleted(GraphResponse response) {
                 // Insert your code here


                 try {
                     facebookPageId = response.getJSONObject().getJSONArray("data").getJSONObject(0).getString("id");

                 } catch (JSONException e) {
                     e.printStackTrace();
                 }

             }


         };


        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+AccessToken.getCurrentAccessToken().getUserId()+"/accounts",
                callback
                );

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id");
        request.setParameters(parameters);

         Thread t = new Thread(new Runnable() {
             @Override
             public void run() {
                 GraphResponse gResponse = request.executeAndWait();
             }
         });

         t.start();
         t.join();

    }


    public String getUserName(){
        return this.userName;
    }

    public String getFacebookPageId() {
        return  facebookPageId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getInstagramId() {
        return instagramId;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }
}
