package gr.uom.socialmedianetworkaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;

public class TwitterLoginActivity extends AppCompatActivity {

    private static final String TAG ="Thanos" ;
    FirebaseAuth firebaseAuth;
    Button signOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_login);
        Log.d(TAG,"Starting TwitterLoginActivity");


        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        Log.d(TAG,"Provider builded" + provider.toString());
        firebaseAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "Got instance "+ firebaseAuth.toString());


            firebaseAuth
                    .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
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
                                    Log.d(TAG, "onSuccess Bearer "+oAuthCredential.getAccessToken());



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



            signOut = findViewById(R.id.signOutBtn);
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseAuth.signOut();
                    Log.d(TAG,"Signed out" + FirebaseAuth.getInstance().toString());

                    Intent intent = new Intent(TwitterLoginActivity.this, SocialMediaLoginActivity.class);
                    startActivity(intent);
                }
            });
    }


}