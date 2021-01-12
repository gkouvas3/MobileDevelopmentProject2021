package gr.uom.socialmedianetworkaggregator.SearchPostsActivity;

import gr.uom.socialmedianetworkaggregator.R;

public class InstaPostEntry extends PostsListEntry{


    public InstaPostEntry(String username, String description, String url) {
        super(username, description, url);
        this.setSocialMediaIcon(R.mipmap.instagram_logo);
    }



    @Override
    public boolean isInstaPost() {
        return true;
    }
}
