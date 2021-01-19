package com.models;

public class CuisinesData {

    private String imgUrl;
    private String categorieName;
    private long count;
    private  String id;

    public CuisinesData(String imgUrl, String categorieName, int count, String id) {
        this.imgUrl = imgUrl;
        this.categorieName = categorieName;
        this.count = count;
        this.id = id;
    }

    public CuisinesData() {

    }


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategorieName() {
        return categorieName;
    }

    public void setCategorieName(String categorieName) {
        this.categorieName = categorieName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }


}
