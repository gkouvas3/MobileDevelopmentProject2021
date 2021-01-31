package gr.uom.socialmedianetworkaggregator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import gr.uom.socialmedianetworkaggregator.SearchPostsActivity.SearchPostsActivity;

public class MainMenuActivity extends AppCompatActivity {

    private static final String TAG = "Thanos";
    private String facebookAccessToken;
    private String twitterBearerToken;
    private Button searchPostsButton;
    private Button createPostButton;
    private Button createStoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        Log.d(TAG, "get fb id: "+ AppUser.getFbInstaUSer().getInstagramId());
        Log.d(TAG, "Get twitter bearer: "+AppUser.getTwitterUser().getAccessToken());

        searchPostsButton = findViewById(R.id.searchPostsButton);
        createPostButton = findViewById(R.id.createPostButton);
        createStoryButton = findViewById(R.id.createStoryButton);


        searchPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, SearchPostsActivity.class);
                startActivity(intent);
            }
        });

        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, CreatePostActivity.class);
                startActivity(intent);
            }
        });

        createStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, CreateStoryActivity.class);
                startActivity(intent);
            }
        });





    }
}