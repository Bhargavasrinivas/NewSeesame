package com.models;

public class User {

    String id ;
    String imgUrl ;
    String mailId ;

    String password ;

    public User() {

    }


    public User(String id, String imgUrl, String mailId, String password) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.mailId = mailId;
        this.password = password;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
