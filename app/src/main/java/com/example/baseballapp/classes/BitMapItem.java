package com.example.baseballapp.classes;

import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BitMapItem {
    @JsonIgnore
    public Bitmap m_image = null;
    @JsonIgnore
    public String m_imageName = "";
    @JsonIgnore
    public String m_fullImageURL = "";
    @JsonIgnore
    public String m_localFileSubFolder = "/images";

}
