package gr.uom.socialmedianetworkaggregator.SearchPostsActivity;

import gr.uom.socialmedianetworkaggregator.R;

public class TwitterPostEntry extends PostsListEntry{


    public TwitterPostEntry(String username, String description, String url) {
        super(username, description, url);
        this.setSocialMediaIcon(R.mipmap.twitter_logo);
    }


}
