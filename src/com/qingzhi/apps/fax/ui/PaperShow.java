package com.qingzhi.apps.fax.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qingzhi.apps.fax.R;

public class PaperShow extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_show);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        String savePath = "/mnt/sdcard/rectPhoto/";
        String name = getIntent().getStringExtra("fileName");
        String url = "file://" + savePath + name + ".jpg";
        ImageLoader.getInstance().displayImage(url, imageView);
    }
}