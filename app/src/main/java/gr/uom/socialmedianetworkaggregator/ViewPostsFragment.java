package gr.uom.socialmedianetworkaggregator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


public class ViewPostsFragment extends Fragment {

    public static final String TAG="Thanos";
    private String hashtagText;
    private List<String> tweetsUrls;
    private ListView postsListView;
    private View view;
    //private EditText hashtagEditText;

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_view_posts, container, false);

        Log.d(TAG,"ViewPosts onCreate");
        hashtagText=getArguments().getString("hashtag");

        postsListView=this.view.findViewById(R.id.postsListView);

//        hashtagEditText =this.view.findViewById(R.id.hashtagEditText);
//        hashtagEditText.setVisibility(View.VISIBLE);
//        hashtagEditText.setText(hashtagText);

        try {

            tweetsUrls =AppUser.getTwitterUser().getTweetsByHashtag(hashtagText);
            Log.d(TAG,"ViewPostsFragment getTweets"+ tweetsUrls.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<TwitterPostEntry> postsList = new ArrayList<TwitterPostEntry>();

        for(String url : tweetsUrls){
            Log.d(TAG,"Tweet "+url);
            postsList.add(new TwitterPostEntry(null, null, url));
        }

        ViewPostsListAdapter adapter = new ViewPostsListAdapter(this.view.getContext(), R.layout.posts_list_item, postsList);
        postsListView.setAdapter(adapter);

        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TwitterPostEntry postsListEntry = adapter.getPostsListEntry(position);
                String url = postsListEntry.getUrl();

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });




        return this.view;
    }


//    public void loadFragment() {
//        Fragment fragment=this;
//// create a FragmentManager
//        FragmentManager fm = getFragmentManager();
//// create a FragmentTransaction to begin the transaction and replace the Fragment
//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//// replace the FrameLayout with new Fragment
//        fragmentTransaction.replace(R.id.searchPostsFrameLayout, fragment);
//        fragmentTransaction.commit(); // save the changes
//    }


}
