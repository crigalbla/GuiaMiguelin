package domain;

public class FollowedsDomain {

    public User[] getFolloweds() {
        return followeds;
    }

    public void setFolloweds(User[] followeds) {
        this.followeds = followeds;
    }

    private User[] followeds;
}
