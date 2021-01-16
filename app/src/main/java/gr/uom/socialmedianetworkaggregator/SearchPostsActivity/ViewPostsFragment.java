package gr.uom.socialmedianetworkaggregator.SearchPostsActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import gr.uom.socialmedianetworkaggregator.AppUser;
import gr.uom.socialmedianetworkaggregator.R;


public class ViewPostsFragment extends Fragment {

    public static final String TAG="Thanos";
    private String hashtagText;
    private List<TwitterPostEntry> tweets;
    private List<InstaPostEntry> instaPosts;
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

            tweets = AppUser.getTwitterUser().getTweetsByHashtag(hashtagText);
            Log.d(TAG,"ViewPostsFragment getTweets"+ tweets.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            instaPosts = AppUser.getFbInstaUSer().getInstaPostsByHashtag(hashtagText);
            Log.d(TAG,"ViewPostsFragment getInstaPosts "+instaPosts.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        List<PostsListEntry> postsList = new ArrayList<PostsListEntry>();

        for(TwitterPostEntry tweet : tweets){
            //Log.d(TAG,"Tweet "+url);
            postsList.add(new TwitterPostEntry(tweet.getUsername(), tweet.getDescription(), tweet.getUrl()));
        }

        for(InstaPostEntry post : instaPosts){
            postsList.add(new InstaPostEntry(post.getUsername(),post.getDescription(),post.getUrl()));
        }

        ViewPostsListAdapter adapter = new ViewPostsListAdapter(this.view.getContext(), R.layout.posts_list_item, postsList);
        postsListView.setAdapter(adapter);

        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostsListEntry postsListEntry = adapter.getPostsListEntry(position);
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
