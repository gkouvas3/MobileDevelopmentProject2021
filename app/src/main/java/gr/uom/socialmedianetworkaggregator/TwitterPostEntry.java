package gr.uom.socialmedianetworkaggregator;

public class TwitterPostEntry {//extends PostsListEntry{


    private String username;
    private String description;
    private String url;
    private int socialMediaIcon;


    public TwitterPostEntry(String username, String description, String url) {
        this.username = username;
        this.description = description;
        this.url = url;
        this.socialMediaIcon=R.mipmap.twitter_logo;
    }

   // @Override
    public String getUsername() {
        return username;
    }

   // @Override
    public void setUsername(String username) {
        this.username = username;
    }

   // @Override
    public String getDescription() {
        return description;
    }

  //  @Override
    public void setDescription(String description) {
        this.description = description;
    }

  //  @Override
    public String getUrl() {
        return url;
    }

 //   @Override
    public void setUrl(String url) {
        this.url = url;
    }

 //   @Override
    public int getSocialMediaIcon() {
        return socialMediaIcon;
    }

  //  @Override
    public void setSocialMediaIcon(int socialMediaIcon) {
        this.socialMediaIcon = socialMediaIcon;
    }
}
