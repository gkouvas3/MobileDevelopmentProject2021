package gr.uom.socialmedianetworkaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.DrawableContainer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FacebookLoginActivity extends AppCompatActivity {

    private TextView loginTextView;
    private ImageView profileImageView;
    private ImageView instagramProfileImageView;
    private LoginButton loginButton;

    private AccessToken accessToken;



    private CallbackManager callbackManager;

    private static String TAG="Thanos";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media_login);

        loginTextView = findViewById(R.id.loginTextView);
        profileImageView = findViewById(R.id.profileImageView);
        instagramProfileImageView = findViewById(R.id.profileImageView2);
        loginButton = findViewById(R.id.login_button);

        
        Log.d(TAG, "Making callback manager");

        callbackManager = CallbackManager.Factory.create();

        loginButton.setPermissions(Arrays.asList("user_birthday",
        "user_hometown",
        "user_location",
        "user_likes",
        "user_events",
        "user_photos",
        "user_videos",
        "user_friends",
        "user_status",
        "user_tagged_places",
        "user_posts",
        "user_gender",
        "user_link",
        "user_age_range",
        "email",
        "read_insights",
        "publish_video",
        "catalog_management",
        "create_audience_network_applications",
        "user_managed_groups",
        "groups_show_list",
        "pages_manage_cta",
        "pages_manage_instant_articles",
        "pages_show_list",
        "read_page_mailboxes",
        "pages_messaging",
        "pages_messaging_phone_number",
        "pages_messaging_subscriptions",
        "instagram_basic",
        "instagram_manage_comments",
        "instagram_manage_insights",
        "publish_to_groups",
        "groups_access_member_info",
        "leads_retrieval",
        "pages_read_engagement",
        "pages_manage_metadata",
        "pages_read_user_content",
        "pages_manage_ads",
        "pages_manage_posts",
        "pages_manage_engagement"));

        Log.d(TAG, "register callback");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d(TAG, "successfull login");

                accessToken=loginResult.getAccessToken();


                FbInstaUSer newUser = null;
                try {
                    newUser = new FbInstaUSer(AccessToken.getCurrentAccessToken());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                loginTextView.setText("Hello "+ newUser.getUserName());





                Bundle params = new Bundle();
                params.putBoolean("redirect", false);
                params.putInt("width",500);
                params.putInt("height", 500);




                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        newUser.getFacebookId()+"/picture",
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

                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        newUser.getInstagramId()+"?fields=profile_picture_url",
                        params,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    String picUrlString = response.getJSONObject().getString("profile_picture_url");
                                    Picasso.get().load(picUrlString).into(instagramProfileImageView);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();




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