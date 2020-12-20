package gr.uom.socialmedianetworkaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.DrawableContainer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class FacebookLoginActivity extends AppCompatActivity {

    private TextView loginTextView;
    private ImageView profileImageView;
    private LoginButton loginButton;

    private AccessToken accessToken;



    private CallbackManager callbackManager;

    private static String TAG="Thanos";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media_login);

        loginTextView = findViewById(R.id.loginTextView);
        profileImageView = findViewById(R.id.profileImageViewe);
        loginButton = findViewById(R.id.login_button);

        
        Log.d(TAG, "Making callback manager");

        callbackManager = CallbackManager.Factory.create();

        Log.d(TAG, "register callback");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d(TAG, "successfull login");

                accessToken=loginResult.getAccessToken();

                Log.d(TAG,"Permissions: "+AccessToken.getCurrentAccessToken().getPermissions().toString());


                new GraphRequest(
                        loginResult.getAccessToken(),
                        "/"+loginResult.getAccessToken().getUserId()+"/",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                try {
                                    //name = response.getJSONObject().getString("name");
                                    loginTextView.setText("Hello "+response.getJSONObject().getString("name"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();



                Bundle params = new Bundle();
                params.putBoolean("redirect", false);
                params.putInt("width",500);
                params.putInt("height", 500);




                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "me/picture",
                        params,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    String picUrlString = (String) response.getJSONObject().getJSONObject("data").get("url");
                                    Picasso.get().load(picUrlString).into(profileImageView);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();

                FbInstaUSer user = new FbInstaUSer(AccessToken.getCurrentAccessToken());
                Log.d(TAG, "Fb id : "+user.getFacebookId());



            }

            @Override
            public void onCancel() {
                Log.d(TAG, "canceled login");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "error on login");

            }
        });



    }

    public AccessToken getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,data);
    }

}