package com.example.canvasapp.data;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Memo implements Serializable {
    String title;
    String content;
//    Bitmap bitmap;

    String bitmap;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }
}
