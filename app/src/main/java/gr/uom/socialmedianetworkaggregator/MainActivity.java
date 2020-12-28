package gr.uom.socialmedianetworkaggregator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Thanos";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button socialMediaLoginBtn = findViewById(R.id.socialMediaLoginBtn);

        socialMediaLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SocialMediaLoginActivity.class);

                Log.d(TAG,"Starting Social Media Login  Activity");
                startActivity(intent);
            }
        });


    }
}