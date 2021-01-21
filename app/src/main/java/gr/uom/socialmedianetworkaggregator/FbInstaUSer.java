package gr.uom.socialmedianetworkaggregator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.facebook.AccessToken;
import com.facebook.AccessTokenManager;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.uom.socialmedianetworkaggregator.SearchPostsActivity.InstaPostEntry;

public class FbInstaUSer {
    private String TAG="Thanos";

    private AccessToken accessToken;
    private String facebookId;
    private String instagramId;
    private String userName;

    private String facebookPageId;
    private String facebookPageAccessToken;



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
                     facebookPageAccessToken=response.getJSONObject().getJSONArray("data").getJSONObject(0).getString("access_token");

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
        parameters.putString("fields", "id,access_token");
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




    public List<InstaPostEntry> getInstaPostsByHashtag(String hashtag) throws InterruptedException {
        List<InstaPostEntry> posts = new ArrayList<InstaPostEntry>();

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/"+this.getHashtagId(hashtag)+"/top_media",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        Log.d(TAG,"FbInstaUser getInstaPosts complete, response: "+response.toString());
                        // Insert your code here
                        try {
                            JSONArray data = response.getJSONObject().getJSONArray("data");
                            for(int i=0;i<data.length();i++){
                                String caption;
                                String mediaUrl;
                                String url;

                                caption=data.getJSONObject(i).getString("caption");
                                mediaUrl=data.getJSONObject(i).getString("media_url");
                                url=data.getJSONObject(i).getString("permalink");

                                InstaPostEntry instaPostEntry = new InstaPostEntry("InstagramUser",caption,url);
                                instaPostEntry.setInstaMediaUrl(mediaUrl);
                                posts.add(instaPostEntry);
                            }
                        } catch (JSONException e) {
                            Log.d(TAG,"FbInstaUser getInstaPostsByHashtag json error "+e.toString());
                        }

                    }


                });

        Bundle parameters = new Bundle();
        parameters.putString("user_id", this.instagramId);
        parameters.putString("fields", "caption,media_url,permalink");
        request.setParameters(parameters);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                GraphResponse gResponse = request.executeAndWait();
            }
        });

        t.start();
        t.join();

        return posts;
    }

    private String getHashtagId(String hashtag) throws InterruptedException {
        final String[] id = new String[1];

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/ig_hashtag_search",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        Log.d(TAG, "FbInstaUser getHashtagId complete, response: "+response.toString());
                        // Insert your code here
                        try {
                            id[0] =response.getJSONObject().getJSONArray("data").getJSONObject(0).getString("id");
                        } catch (JSONException e) {
                            Log.d(TAG, "Hashtag id json error"+e.toString());
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("user_id", this.instagramId);
        if(hashtag.contains("#")) hashtag=hashtag.substring(1);
        parameters.putString("q", hashtag);
        request.setParameters(parameters);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                GraphResponse gResponse = request.executeAndWait();
            }
        });

        t.start();
        t.join();
        Log.d(TAG,"hashtag id:"+id[0]);
        return id[0];
    }

    public void createFbPost(String description, String imageUrl){

        if(description!=null&&imageUrl==null) {
            Bundle params = new Bundle();
            params.putString("message", description);
            params.putString("access_token", facebookPageAccessToken);
            /* make the API call */
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + facebookPageId + "/feed",
                    params,
                    HttpMethod.POST,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            /* handle the result */
                            Log.d(TAG, "response:" + response.toString());
                        }
                    }
            ).executeAsync();
        }else if(description==null){
            Bundle params = new Bundle();
            params.putString("url", imageUrl);
            params.putString("access_token", facebookPageAccessToken);
            /* make the API call */
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + facebookPageId + "/photos",
                    params,
                    HttpMethod.POST,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            /* handle the result */
                            Log.d(TAG, "response:" + response.toString());
                        }
                    }
            ).executeAsync();
        }else{
            Bundle params = new Bundle();
            params.putString("message",description);
            params.putString("url", imageUrl);
            params.putString("access_token", facebookPageAccessToken);
            /* make the API call */
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + facebookPageId + "/photos",
                    params,
                    HttpMethod.POST,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            /* handle the result */
                            Log.d(TAG, "response:" + response.toString());
                        }
                    }
            ).executeAsync();
        }
    }

    public void createInstaPost(String description, String imageUri){

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

}
