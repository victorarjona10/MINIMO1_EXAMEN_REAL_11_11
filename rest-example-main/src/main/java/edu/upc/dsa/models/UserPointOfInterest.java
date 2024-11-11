package edu.upc.dsa.models;

public class UserPointOfInterest {
     String userId;
     PointOfInterest pointOfInterest;

    public UserPointOfInterest(String userId, PointOfInterest pointOfInterest) {
        this.userId = userId;
        this.pointOfInterest = pointOfInterest;
    }

    public UserPointOfInterest() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public PointOfInterest getPointOfInterest() {
        return pointOfInterest;
    }

    public void setPointOfInterest(PointOfInterest pointOfInterest) {
        this.pointOfInterest = pointOfInterest;
    }

    @Override
    public String toString() {
        return "UserPointOfInterest [userId=" + userId + ", pointOfInterest=" + pointOfInterest + "]";
    }
}
