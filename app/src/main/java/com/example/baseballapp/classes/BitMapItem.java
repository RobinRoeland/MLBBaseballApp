package com.example.baseballapp.classes;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(inheritSuperIndices = true)
public class BitMapItem {
    @JsonIgnore
    @Ignore
    public Bitmap m_image = null;
    @JsonIgnore
    public String m_imageName = "";
    @JsonIgnore
    public String m_fullImageURL = "";
    @JsonIgnore
    public String m_localFileSubFolder = "/images";
    @Ignore
    public boolean m_ImageNotLoaded = false;

}
