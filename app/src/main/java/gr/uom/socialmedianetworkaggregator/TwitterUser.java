package gr.uom.socialmedianetworkaggregator;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class TwitterUser {
    private static final String TAG = "Thanos" ;
    private String bearerToken;
    private String tokenSecret;

    public TwitterUser(String bearerToken, String tokenSecret) {
        this.bearerToken = bearerToken;
        this.tokenSecret = tokenSecret;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public  List<String> getTrendingHashtags() throws IOException, JSONException {
        List<String> trendingHashtags = new ArrayList<>();
        String url = "https://api.twitter.com/1.1/trends/place.json?id=963291";

        ANRequest request =AndroidNetworking.get(url)
                .addHeaders("Authorization", "OAuth oauth_consumer_key=\"UA0RjRV585BOUQHnem9BZru2s\",oauth_token=\"1523029542-gHLu1gUrIt1DG3IYgSDj8Y9eeQk6Jc7fxCEJ84R\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"1609667120\",oauth_nonce=\"NRC3KyEtM56\",oauth_version=\"1.0\",oauth_signature=\"NwoAYi4EiAo4XxVrHBYnoSaU87A%3D\"")
                .build();

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
}
