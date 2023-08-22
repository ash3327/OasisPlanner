package com.aurora.oasisplanner.data.structure;

import android.net.Uri;
import android.widget.ImageView;

import com.aurora.oasisplanner.R;

import java.time.Month;

public class Image {
    boolean useURI;
    int resId;
    Uri uri;

    private static int[] monthBanners = new int[]{
            R.drawable.winter_v2,   //January
            R.drawable.winter_v3,   //February
            R.drawable.spring_v1,   //March
            R.drawable.spring_v2,   //April
            R.drawable.spring_v3_1, //May
            R.drawable.summer_beach_v2,     //June
            R.drawable.summer_beach2_v2,    //July
            R.drawable.summer_beach_v3,     //August
            R.drawable.autumn_v1,   //September
            R.drawable.autumn_v2,   //October
            R.drawable.autumn_v3,   //November
            R.drawable.winter_1_v1  //December
    };

    public Image() {
        useURI = false;
        this.resId = R.drawable.no_image_placeholder_dark;
    }
    public static Image emptyIcon() {
        Image img = new Image();
        img.resId = R.drawable.ic_symb_question_mark;
        return img;
    }

    public Image(int resId) {
        useURI = false;
        this.resId = resId;
    }

    public Image(Uri uri) {
        useURI = true;
        this.uri = uri;
    }

    public static Image monthBannerOf(Month month) {
        if (month == null)
            return new Image();
        return new Image(monthBanners[month.getValue() - 1]);
    }

    public void showImageOn(ImageView imageView) {
        if (useURI) imageView.setImageURI(uri);
        else imageView.setImageResource(resId);
    }
}
