package gr.uom.socialmedianetworkaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

import java.util.Arrays;


public class SocialMediaLoginActivity extends AppCompatActivity {

    private final static String TAG="Thanos";
    private CallbackManager mCallbackManager;
    private LoginButton loginButton;
    private FirebaseAuth fbAuth;
    private FirebaseAuth twitterAuth;
    private Button twitterBtn;
    private Button goToMenuBtn;

    private FbInstaUSer fbInstaUser=null;
    private TwitterUser twitterUser=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media_login);



        FacebookSdk.sdkInitialize(SocialMediaLoginActivity.this);
        twitterBtn = findViewById(R.id.twitterBtn);
        goToMenuBtn = findViewById(R.id.goToMenuButton);
        goToMenuBtn.setVisibility(View.INVISIBLE);
        if(fbInstaUser!=null && twitterUser!=null){
            AppUser.setTwitterUser(twitterUser);
            AppUser.setFbInstaUSer(fbInstaUser);
            goToMenuBtn.setVisibility(View.VISIBLE);

        }
        goToMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SocialMediaLoginActivity.this,MainMenuActivity.class);
                //intent.putExtra("twitterBearerToken",twitterUser.getBearerToken());
                startActivity(intent);
            }
        });

        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Starting TwitterLoginActivity");


                OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
                Log.d(TAG,"Provider builded" + provider.toString());
                twitterAuth = FirebaseAuth.getInstance();
                Log.d(TAG, "Got instance "+ twitterAuth.toString());


                twitterAuth
                        .startActivityForSignInWithProvider(/* activity= */ SocialMediaLoginActivity.this, provider.build())
                        .addOnSuccessListener(
                                new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        // User is signed in.
                                        // IdP data available in
                                        // authResult.getAdditionalUserInfo().getProfile().
                                        // The OAuth access token can also be retrieved:
                                        // authResult.getCredential().getAccessToken().
                                        // The OAuth secret can be retrieved by calling:
                                        // authResult.getCredential().getSecret().



                                        OAuthCredential oAuthCredential = (OAuthCredential) authResult.getCredential();
                                        Log.d(TAG, "onSuccess Bearer "+  oAuthCredential.getSecret());
                                        twitterUser= new TwitterUser(oAuthCredential.getAccessToken(), oAuthCredential.getSecret());

                                        if(fbInstaUser!=null && twitterUser!=null){
                                            AppUser.setTwitterUser(twitterUser);
                                            AppUser.setFbInstaUSer(fbInstaUser);
                                            goToMenuBtn.setVisibility(View.VISIBLE);
                                        }


                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle failure.
                                        Log.d(TAG, "onFailure result "+ e.toString());

                                    }
                                });

            }
        });


            //Initialise Firebase
            fbAuth = FirebaseAuth.getInstance();


            // Initialize Facebook Login button
            mCallbackManager = CallbackManager.Factory.create();
            loginButton = findViewById(R.id.login_button);
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
            loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook:onSuccess:" + loginResult);
                    try {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:onCancel");
                    // ...
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG, "facebook:onError", error);
                    // ...
                }
            });

        }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fbAuth.getCurrentUser();
        if(currentUser!=null){
            if(fbInstaUser!=null && twitterUser!=null){
                AppUser.setTwitterUser(twitterUser);
                AppUser.setFbInstaUSer(fbInstaUser);
                goToMenuBtn.setVisibility(View.VISIBLE);
            }
            updateUI(currentUser);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) throws InterruptedException {
        Log.d(TAG, "handleFacebookAccessToken:" + token.getToken());
        fbInstaUser = new FbInstaUSer(token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = fbAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SocialMediaLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            //if user is logged in , redirect him to another activity
            Toast.makeText(this, "Successful sign in", Toast.LENGTH_SHORT).show();
            if(fbInstaUser!=null && twitterUser!=null){
                AppUser.setTwitterUser(twitterUser);
                AppUser.setFbInstaUSer(fbInstaUser);
                goToMenuBtn.setVisibility(View.VISIBLE);
            }


        }else{
            Toast.makeText(this,"Please sign in to continue.", Toast.LENGTH_SHORT).show();
        }
    }

}





