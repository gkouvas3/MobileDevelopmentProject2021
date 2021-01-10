package gr.uom.socialmedianetworkaggregator;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


public class hashtagsListFragment extends Fragment {

    public static final String TAG="Thanos";

    private SearchView hashtagsSearchView;
    private ListView hashtagsListView;

    private List<String> trendingHashtags;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        this.view = inflater.inflate(R.layout.fragment_hashtags_list, container, false);

        hashtagsListView =  this.view.findViewById(R.id.hashtagsListViewFragment);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        hashtagsSearchView=view.findViewById(R.id.hashtagsSearchView);
        hashtagsListView=view.findViewById(R.id.hashtagsListViewFragment);


        try {
            trendingHashtags = AppUser.getTwitterUser().getTrendingHashtags();
            Log.d(TAG, "Trending hashtags: " + trendingHashtags.toString());


        } catch (IOException | JSONException | GeneralSecurityException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this.view.getContext(), R.layout.hashtags_list_item,trendingHashtags);
        hashtagsListView.setAdapter(adapter);


        hashtagsListView.setClickable(true);
        hashtagsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG,"onItemClick");

                ((SearchPostsActivity)getActivity()).replaceFragments(ViewPostsFragment.class, hashtagsListView.getItemAtPosition(position).toString());

            }
        });


            return this.view;
        }

    public void loadFragment() {
        Fragment fragment=this;
// create a FragmentManager
        FragmentManager fm = getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.searchPostsFrameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
    }
}
