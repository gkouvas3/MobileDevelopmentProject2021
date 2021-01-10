package gr.uom.socialmedianetworkaggregator;

import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TwitterUser {
    private static final String TAG = "Thanos" ;
    private String consumerToken;
    private String consumerSecret;
    private String consumerBearer;
    private String accessToken;
    private String tokenSecret;

    public TwitterUser(String accessToken, String tokenSecret) {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                consumerToken = snapshot.child("twitter_consumer_token").getValue().toString();
                consumerSecret = snapshot.child("twitter_consumer_secret").getValue().toString();
                consumerBearer=snapshot.child("twitter_bearer_token").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                consumerToken=null;
                consumerSecret=null;
                consumerBearer=null;
            }
        });

        this.accessToken = accessToken;
        this.tokenSecret = tokenSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public  List<String> getTrendingHashtags() throws IOException, JSONException, GeneralSecurityException {
        List<String> trendingHashtags = new ArrayList<>();
        String url = "https://api.twitter.com/1.1/trends/place.json";
        Map<String, String> params = new HashMap<>();
        params.put("id","23424977");
        String headerString=generateOauthHeaders("GET",url, params);

        Log.d(TAG, "HeaderString: "+headerString);
        ANRequest request =AndroidNetworking.get(url+"?id=23424977")
                .addHeaders("Authorization", headerString)
                .build();

        Log.d(TAG,"Request: "+request.getHeaders().toString());
        ANResponse<JSONArray> response = request.executeForJSONArray();

        if(response.isSuccess()){

            JSONArray result = response.getResult();

            JSONArray trends = result.getJSONObject(0).getJSONArray("trends");

            for(int i=0; i<trends.length();i++){
                trendingHashtags.add(trends.getJSONObject(i).getString("name"));
            }

            Log.d(TAG, "Twitter user onResponse: "+ response.getResult().toString());


        }else Log.d(TAG,"Error: "+response.getError().getErrorDetail() + " "+response.getError().getErrorCode() + " "+ response.getError().getErrorBody() + " "+response.getError().getMessage() );


        /*
        .getAsJSONArray(new JSONArrayRequestListener() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            JSONArray trends = response.getJSONObject(0).getJSONArray("trends");
                            for(int i = 0; i<trends.length(); i++){
                                trendingHashtags.add(trends.getJSONObject(i).getString("name"));
                            }
                            //Log.d(TAG, "List of Hashtags:"+trendingHashtags.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                            Log.d(TAG, "Twitter user onResponse: "+ response.toString());


                    }

                    @Override
                    public void onError(ANError anError){
                        Log.d(TAG, "Twiiter user onError  " + anError.getErrorDetail());
                    }

                });

       */
        return trendingHashtags;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public String generateOauthHeaders(String httpMethod, String url, Map<String, String> params) throws UnsupportedEncodingException, GeneralSecurityException {
        String oauthHeader;
        String oauth_nonce;

        //generate oauth_nonce
        Random random = ThreadLocalRandom.current();
        byte[] r = new byte[32]; //Means 2048 bit
        random.nextBytes(r);
        oauth_nonce= android.util.Base64.encodeToString(r,Base64.DEFAULT).replaceAll("[=/+]","0").substring(0,11);
        Log.d(TAG,"Oauth nonce:" +oauth_nonce);

         Log.d(TAG,"ConsumerSecret:"+this.consumerSecret);
         Log.d(TAG,"consumerToken:"+this.consumerToken);




         oauthHeader=new OAuth1AuthorizationHeaderBuilder()
                 .withMethod(httpMethod)
                 .withURL(url)
                 .withConsumerSecret(this.consumerSecret)
                 .withTokenSecret(this.tokenSecret)
                 .withParameter("oauth_consumer_key",this.consumerToken)
                 .withParameter("oauth_token",this.accessToken)
                 .withParameter("oauth_nonce",oauth_nonce)
                 .withParameter(params)
                 .build();




        Log.d(TAG,"Generate oauth header: "+ oauthHeader);
        return oauthHeader;
    }

    public String generateOauthHeaders(String httpMethod, String url) throws UnsupportedEncodingException, GeneralSecurityException {
        String oauthHeader;
        String oauth_nonce;

        //generate oauth_nonce
        Random random = ThreadLocalRandom.current();
        byte[] r = new byte[32]; //Means 2048 bit
        random.nextBytes(r);
        oauth_nonce= android.util.Base64.encodeToString(r,Base64.DEFAULT).replaceAll("[=/+]","0").substring(0,11);
        Log.d(TAG,"Oauth nonce:" +oauth_nonce);

        Log.d(TAG,"ConsumerSecret:"+this.consumerSecret);
        Log.d(TAG,"consumerToken:"+this.consumerToken);




        oauthHeader=new OAuth1AuthorizationHeaderBuilder()
                .withMethod(httpMethod)
                .withURL(url)
                .withConsumerSecret(this.consumerSecret)
                .withTokenSecret(this.tokenSecret)
                .withParameter("oauth_consumer_key",this.consumerToken)
                .withParameter("oauth_token",this.accessToken)
                .withParameter("oauth_nonce",oauth_nonce)
                .build();




        Log.d(TAG,"Generate oauth header: "+ oauthHeader);
        return oauthHeader;
    }

    public List<String> getTweetsByHashtag(String hashtag) throws UnsupportedEncodingException, GeneralSecurityException, JSONException {
        List<String> tweetsUrls = new ArrayList<>();
        String url = "https://api.twitter.com/1.1/search/tweets.json";
        Map<String, String> params = new HashMap<>();
        params.put("result_type","popular");
        if(hashtag.contains("#")) hashtag=hashtag.substring(1);
        params.put("q",hashtag);
        String headerString=generateOauthHeaders("GET",url, params);

        Log.d(TAG, "HeaderString: "+headerString);
        ANRequest request =AndroidNetworking.get(url+"?result_type=popular&q="+hashtag)
                .addHeaders("Authorization", headerString)
                .build();

        Log.d(TAG,"Request: "+request.getHeaders().toString());
        ANResponse<JSONObject> response = request.executeForJSONObject();

        if(response.isSuccess()){

            Log.d(TAG,"Before getting result as JSONArray: "+response.getResult().toString());
            JSONObject result = response.getResult();
            Log.d(TAG,"getTweetsByHashtag result:"+result.toString());

            JSONArray statuses = result.getJSONArray("statuses");

            Log.d(TAG,"Statuses JSONArray:"+statuses.toString());
            Log.d(TAG,"One of statuses :"+statuses.getJSONObject(0).toString());

            for(int i=0; i<statuses.length();i++) {

                for (int j = 0; j < statuses.getJSONObject(i).getJSONObject("entities").getJSONArray("urls").length(); j++) {
                    tweetsUrls.add(statuses.getJSONObject(i).getJSONObject("entities").getJSONArray("urls").getJSONObject(j).getString("url"));

                }
                //tweetsUrls.add(statuses.getJSONObject(i).getJSONObject("entities").getJSONArray("urls").toString());
            }

            Log.d(TAG, "Tweets by hashtag tweetsUrls: "+tweetsUrls.toString());


        }else Log.d(TAG,"Tweets by hashtag Error: "+response.getError().getErrorDetail() + " "+response.getError().getErrorCode() + " "+ response.getError().getErrorBody() + " "+response.getError().getMessage() );



        return tweetsUrls;
    }
}
