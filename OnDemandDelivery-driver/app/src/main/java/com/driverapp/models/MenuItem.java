package com.driverapp.models;

/**
 * Created by bridgelabz on 03/07/18.
 */

public class MenuItem {
    private int resourceId;
    private String title;

    public MenuItem(int resourceId, String title){
        this.resourceId = resourceId;
        this.title = title;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getTitle() {
        return title;
    }




}
