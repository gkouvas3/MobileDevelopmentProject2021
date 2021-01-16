package gr.uom.socialmedianetworkaggregator.SearchPostsActivity;

import android.util.Log;

public class PostsListEntry {

    private String username;
    private String description;
    private String url;
    private String instaMediaUrl;
    private int socialMediaIcon;

    public PostsListEntry(String username, String description, String url) {
        this.username = username;
        this.description = description;
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSocialMediaIcon() {
        return socialMediaIcon;
    }

    public void setSocialMediaIcon(int socialMediaIcon) {
        this.socialMediaIcon = socialMediaIcon;
    }

    public String getInstaMediaUrl() {
        if(isInstaPost())
            return instaMediaUrl;
        else return "not an instagram post";
    }

    public void setInstaMediaUrl(String instaMediaUrl) {
        if(isInstaPost())
            this.instaMediaUrl = instaMediaUrl;
    }

    public boolean isInstaPost(){
        return false;
    }

}
