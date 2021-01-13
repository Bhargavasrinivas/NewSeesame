package com.models;

public class RateModel {

    private String rateid;
    private String comments;
    private String userId;
    private String commentorId;
    private float rateCount;
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public float getRateCount() {
        return rateCount;
    }

    public void setRateCount(float rateCount) {
        this.rateCount = rateCount;
    }

    public RateModel() {

    }
    private String userName;
    private String profilepic;


    public String getRateid() {
        return rateid;
    }

    public void setRateid(String rateid) {
        this.rateid = rateid;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentorId() {
        return commentorId;
    }

    public void setCommentorId(String commentorId) {
        this.commentorId = commentorId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }
}
