package gr.uom.socialmedianetworkaggregator;

public class TwitterUser {
    private String bearerToken;

    public TwitterUser(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public String getBearerToken() {
        return bearerToken;
    }
}
