package gr.uom.socialmedianetworkaggregator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class SearchPostsActivity extends AppCompatActivity {

    private static final String TAG = "Thanos";


    private ListView hashtagsListView;

    private List<String> trendingHashtags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_posts);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        hashtagsListView=findViewById(R.id.hashatgsListView);


        try {
            trendingHashtags = AppUser.getTwitterUser().getTrendingHashtags();
            Log.d(TAG, "Trending hashtags: " + trendingHashtags.toString());


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.hashtags_list_item,trendingHashtags);
        hashtagsListView.setAdapter(adapter);

    }
}