package gr.uom.socialmedianetworkaggregator;

public class AppUser {
    private static TwitterUser twitterUser;
    private static FbInstaUSer fbInstaUSer;

    public static void setTwitterUser(TwitterUser twitterUser) {
        AppUser.twitterUser = twitterUser;
    }

    public static void setFbInstaUSer(FbInstaUSer fbInstaUSer) {
        AppUser.fbInstaUSer = fbInstaUSer;
    }

    public static TwitterUser getTwitterUser() {
        return twitterUser;
    }

    public static FbInstaUSer getFbInstaUSer() {
        return fbInstaUSer;
    }
}
