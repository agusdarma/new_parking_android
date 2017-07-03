package com.project.parking.adapter;

import android.graphics.drawable.Drawable;

/**
 * Created by Yohanes on 01/07/2017.
 */

public class MallItem {
    private Drawable icon;

    private String name;

    private String information;

    private String slotAvailable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getSlotAvailable() {
        return slotAvailable;
    }

    public void setSlotAvailable(String slotAvailable) {
        this.slotAvailable = slotAvailable;
    }
}
