package gr.uom.socialmedianetworkaggregator;

public class PostsListEntry {

    private String username;
    private String description;
    private String url;
    private int socialMediaIcon;

    public PostsListEntry() {
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public int getSocialMediaIcon() {
        return socialMediaIcon;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSocialMediaIcon(int socialMediaIcon) {
        this.socialMediaIcon = socialMediaIcon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
