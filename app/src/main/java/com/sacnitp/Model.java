package com.sacnitp;

public class Model {
    private String name,desc,imageurl;

    public Model(String username, String desc, String imageurl) {
        this.name = username;
        this.desc = desc;
        this.imageurl = imageurl;
    }

    public Model(String username, String desc) {
        this.name = username;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


    public String getImageurl() {
        return imageurl;
    }



    public String getUsername() {
        return name;
    }
}
