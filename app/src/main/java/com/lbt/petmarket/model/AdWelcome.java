package com.lbt.petmarket.model;

/**
 * Created by Administrator on 2015/10/21 0021.
 */
public class AdWelcome {

    String id;
    int jump_type;
    String picture;
    String url;
    String duration; //秒
    int is_activate;
    int is_delete;
    String create_time;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getJump_type() {
        return jump_type;
    }

    public void setJump_type(int jump_type) {
        this.jump_type = jump_type;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIs_activate() {
        return is_activate;
    }

    public void setIs_activate(int is_activate) {
        this.is_activate = is_activate;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(int is_delete) {
        this.is_delete = is_delete;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
