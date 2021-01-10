package gr.uom.socialmedianetworkaggregator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.List;

public class SearchPostsActivity extends AppCompatActivity {

    private static final String TAG = "Thanos";

    private SearchView hashtagsSearchView;
   /* private ListView hashtagsListView;

    private List<String> trendingHashtags;
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_posts);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        getSupportFragmentManager().beginTransaction().replace(R.id.searchPostsFrameLayout,new hashtagsListFragment()).commit();
        Log.d(TAG,"getSupport... new hashtagsListFragment");


        hashtagsSearchView=findViewById(R.id.hashtagsSearchView);
        //(new hashtagsListFragment()).loadFragment();
//        hashtagsListView=findViewById(R.id.hashatgsListView);

/*
        try {
            trendingHashtags = AppUser.getTwitterUser().getTrendingHashtags();
            Log.d(TAG, "Trending hashtags: " + trendingHashtags.toString());


        } catch (IOException | JSONException | GeneralSecurityException e) {
            e.printStackTrace();
        }
*//*
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.hashtags_list_item,trendingHashtags);
        hashtagsListView.setAdapter(adapter);

        hashtagsListView.setClickable(true);
        hashtagsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG,"OnItemClicked");
                getSupportFragmentManager().beginTransaction().replace(R.id.searchPostsFrameLayout,new ViewPostsFragment()).commit();
            }
        });

*/
        hashtagsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    public void replaceFragments(Class fragmentClass, String selectedHashtag) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();

            Log.d(TAG,"replaceFragments try");
        } catch (Exception e) {
            Log.d(TAG,"replaceFragments exception:"+e.getMessage());
        }
        // Insert the fragment by replacing any existing fragment
        Bundle b = new Bundle();
        b.putString("hashtag",selectedHashtag);
        fragment.setArguments(b);
        Log.d(TAG,"Bundle completed");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Log.d(TAG,"replaceFragments get support...");
        fragmentManager.beginTransaction().replace(R.id.searchPostsFrameLayout, fragment).addToBackStack(null).commit();
    }

    public void replaceFragments(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();

            Log.d(TAG,"replaceFragments try");
        } catch (Exception e) {
            Log.d(TAG,"replaceFragments exception:"+e.getMessage());
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Log.d(TAG,"replaceFragments get support...");
        fragmentManager.beginTransaction().replace(R.id.searchPostsFrameLayout, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG,"onBackPressed");
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}