package com.bidblast.api.requests.auctions;

public class BlockedProfileBody {
    private int idProfile;

    public BlockedProfileBody() {
    }

    public BlockedProfileBody(int idProfile) {
        this.idProfile = idProfile;
    }

    public int getIdProfile() {
        return idProfile;
    }

    public void setIdProfile(int idProfile) {
        this.idProfile = idProfile;
    }
}