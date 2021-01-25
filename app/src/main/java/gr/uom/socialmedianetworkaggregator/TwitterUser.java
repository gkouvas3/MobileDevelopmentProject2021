package gr.uom.socialmedianetworkaggregator;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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

import gr.uom.socialmedianetworkaggregator.SearchPostsActivity.TwitterPostEntry;

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

//    public List<String> getTweetsByHashtag(String hashtag) throws UnsupportedEncodingException, GeneralSecurityException, JSONException {
//        List<String> tweetsUrls = new ArrayList<>();
//        String url = "https://api.twitter.com/1.1/search/tweets.json";
//        Map<String, String> params = new HashMap<>();
//        params.put("result_type","popular");
//        if(hashtag.contains("#")) hashtag=hashtag.substring(1);
//        params.put("q",hashtag);
//        String headerString=generateOauthHeaders("GET",url, params);
//
//        Log.d(TAG, "HeaderString: "+headerString);
//        ANRequest request =AndroidNetworking.get(url+"?result_type=popular&q="+hashtag)
//                .addHeaders("Authorization", headerString)
//                .build();
//
//        Log.d(TAG,"Request: "+request.getHeaders().toString());
//        ANResponse<JSONObject> response = request.executeForJSONObject();
//
//        if(response.isSuccess()){
//
//            Log.d(TAG,"Before getting result as JSONArray: "+response.getResult().toString());
//            JSONObject result = response.getResult();
//            Log.d(TAG,"getTweetsByHashtag result:"+result.toString());
//
//            JSONArray statuses = result.getJSONArray("statuses");
//
//            Log.d(TAG,"Statuses JSONArray:"+statuses.toString());
//            Log.d(TAG,"One of statuses :"+statuses.getJSONObject(0).toString());
//
//            for(int i=0; i<statuses.length();i++) {
//
//                for (int j = 0; j < statuses.getJSONObject(i).getJSONObject("entities").getJSONArray("urls").length(); j++) {
//                    tweetsUrls.add(statuses.getJSONObject(i).getJSONObject("entities").getJSONArray("urls").getJSONObject(j).getString("url"));
//
//                }
//                //tweetsUrls.add(statuses.getJSONObject(i).getJSONObject("entities").getJSONArray("urls").toString());
//            }
//
//            Log.d(TAG, "Tweets by hashtag tweetsUrls: "+tweetsUrls.toString());
//
//
//        }else Log.d(TAG,"Tweets by hashtag Error: "+response.getError().getErrorDetail() + " "+response.getError().getErrorCode() + " "+ response.getError().getErrorBody() + " "+response.getError().getMessage() );
//
//
//
//        return tweetsUrls;
//    }


    public List<TwitterPostEntry> getTweetsByHashtag(String hashtag) throws UnsupportedEncodingException, GeneralSecurityException, JSONException {
        List<TwitterPostEntry> tweets = new ArrayList<>();
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

//            for(int i=0; i<statuses.length();i++) {
//
//                for (int j = 0; j < statuses.getJSONObject(i).getJSONObject("entities").getJSONArray("urls").length(); j++) {
//                    tweets.add(statuses.getJSONObject(i).getJSONObject("entities").getJSONArray("urls").getJSONObject(j).getString("url"));
//
//                }
//                //tweetsUrls.add(statuses.getJSONObject(i).getJSONObject("entities").getJSONArray("urls").toString());
//            }

            for(int i=0;i<statuses.length();i++){
                String username;
                String description;
                String tweetUrl=null;

                username="@"+statuses.getJSONObject(i).getJSONObject("user").getString("screen_name");
                description=statuses.getJSONObject(i).getString("text");
                for (int j = 0; j < statuses.getJSONObject(i).getJSONObject("entities").getJSONArray("urls").length(); j++) {
                    tweetUrl = statuses.getJSONObject(i).getJSONObject("entities").getJSONArray("urls").getJSONObject(j).getString("url");
                }
                tweets.add(new TwitterPostEntry(username,description,tweetUrl));


            }




            Log.d(TAG, "Tweets by hashtag : "+tweets.toString());


        }else Log.d(TAG,"Tweets by hashtag Error: "+response.getError().getErrorDetail() + " "+response.getError().getErrorCode() + " "+ response.getError().getErrorBody() + " "+response.getError().getMessage() );



        return tweets;
    }

    public void createPost(String description, String imageUrl) throws UnsupportedEncodingException, GeneralSecurityException {
        Log.d(TAG, "TwitterUser createPost");


            Log.d(TAG,"description is not null: "+description);

            String url = "https://api.twitter.com/1.1/statuses/update.json";
            Map<String, String> params = new HashMap<>();
            params.put("status", description+" "+imageUrl);
            String headerString=generateOauthHeaders("POST",url, params);




            AndroidNetworking.post(url+"?status="+URLEncoder.encode(description+" "+imageUrl,String.valueOf(StandardCharsets.UTF_8)))
                    .addHeaders("Authorization",headerString)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG,"Successful tweet!");
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, "Error while twitter createpost: "+anError.getErrorDetail()+" "+anError.getMessage());
                        }
                    });

//        else{
//            BitmapDrawable bitmapDrawable = (BitmapDrawable)image;
//            Bitmap imageBitmap = bitmapDrawable.getBitmap();
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//            Log.d(TAG,"byte output stream size "+byteArrayOutputStream.size());
//            byte[] byteArr = byteArrayOutputStream.toByteArray();
//
//            Log.d(TAG,"Media byte arr size : "+byteArr.length);
//            Log.d(TAG,"Imagebitmap to string:"+imageBitmap.toString());
//
//
//            String imageEncoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
//
//            String url = "https://upload.twitter.com/1.1/media/upload.json";
//            Map<String, String> params = new HashMap<>();
//           params.put("command", "INIT");
//           params.put("total_bytes",""+byteArr.length);
////            params.put("media_category","tweet_image");
//            String headerString=generateOauthHeaders("POST",url, params);
//
//            Log.d(TAG, "Twitter image binary: "+imageEncoded);
//            AndroidNetworking.post("https://upload.twitter.com/1.1/media/upload.json?command=INIT&total_bytes="+byteArr.length)
//                    .addHeaders("Authorization", headerString)
//                    .build()
//                    .getAsJSONObject(new JSONObjectRequestListener() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                String mediaId= response.getString("media_id_string");
//
//                                Log.d(TAG,"Got media id from twitter: "+mediaId);
//
//                                String url = "https://upload.twitter.com/1.1/media/upload.json";
//                                Map<String, String> params = new HashMap<>();
////                                params.put("command","APPEND");
//////                                if(description==null)
//////                                    params.put("status", "");
//////                                else params.put("status",description);
////                                params.put("media_id",mediaId);
////                                params.put("segment_index","0");
////                                //params.put("media_data",imageEncoded);
//                                String headerString=generateOauthHeaders("POST",url, params);
//
//                                AndroidNetworking.post(url+"?command=APPEND"+"&media_id="+mediaId+"segment_index=0&media_data="+imageEncoded)
//                                        .addHeaders("Authorization",headerString)
//                                        .build()
//                                        .getAsJSONObject(new JSONObjectRequestListener() {
//                                            @Override
//                                            public void onResponse(JSONObject response) {
//                                                Log.d(TAG,"Successful append!");
//
//
//                                                String url = "https://upload.twitter.com/1.1/media/upload.json";
//                                                Map<String, String> params = new HashMap<>();
//                                                params.put("command", "FINALIZE");
//                                                params.put("media_id",mediaId);
//                                                String headerString= null;
//                                                try {
//                                                    headerString = generateOauthHeaders("POST",url, params);
//                                                } catch (UnsupportedEncodingException e) {
//                                                    e.printStackTrace();
//                                                } catch (GeneralSecurityException e) {
//                                                    e.printStackTrace();
//                                                }
//
//                                                AndroidNetworking.post(url+"?command=FINALIZE"+"&media_id="+mediaId)
//                                                        .addHeaders("Authorization",headerString)
//                                                        .build()
//                                                        .getAsJSONObject(new JSONObjectRequestListener() {
//                                                            @Override
//                                                            public void onResponse(JSONObject response) {
//                                                                Log.d(TAG,"Successful finalize!");
//
//                                                                String url = "https://api.twitter.com/1.1/statuses/update.json";
//                                                                Map<String, String> params = new HashMap<>();
//                                                                if(description==null) params.put("status","");
//                                                                else params.put("status", description);
//                                                                params.put("media_ids",mediaId);
//                                                                String headerString= null;
//                                                                try {
//                                                                    headerString = generateOauthHeaders("POST",url, params);
//                                                                } catch (UnsupportedEncodingException e) {
//                                                                    e.printStackTrace();
//                                                                } catch (GeneralSecurityException e) {
//                                                                    e.printStackTrace();
//                                                                }
//
//                                                                try {
//                                                                    AndroidNetworking.post(url+"?status="+URLEncoder.encode(description,String.valueOf(StandardCharsets.UTF_8))+"&media_ids="+mediaId)
//                                                                            .addHeaders("Authorization",headerString)
//                                                                            .build()
//                                                                            .getAsJSONObject(new JSONObjectRequestListener() {
//                                                                                @Override
//                                                                                public void onResponse(JSONObject response) {
//                                                                                    Log.d(TAG,"Successful tweet!");
//                                                                                }
//
//                                                                                @Override
//                                                                                public void onError(ANError anError) {
//                                                                                    Log.d(TAG, "Error while twitter createpost: "+anError.getErrorDetail()+" "+anError.getMessage());
//                                                                                }
//                                                                            });
//                                                                } catch (UnsupportedEncodingException e) {
//                                                                    e.printStackTrace();
//                                                                }
//
//
//                                                            }
//
//                                                            @Override
//                                                            public void onError(ANError anError) {
//                                                                Log.d(TAG, "Error on media finalize: "+anError.getErrorDetail()+" "+anError.getMessage() +anError.getResponse());
//                                                            }
//                                                        });
//
//
//                                            }
//
//                                            @Override
//                                            public void onError(ANError anError) {
//                                                Log.d(TAG, "Error on media append: "+anError.getErrorDetail()+" "+anError.getMessage() +anError.getResponse());
//                                            }
//                                        });
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            } catch (GeneralSecurityException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                        @Override
//                        public void onError(ANError anError) {
//                            Log.d(TAG, "Error getting media id: "+anError.getErrorDetail()+" "+anError.getMessage()+" "+anError.getErrorCode()+" "+anError.getErrorBody());
//
//                        }
//                    });
//        }


    }


}
