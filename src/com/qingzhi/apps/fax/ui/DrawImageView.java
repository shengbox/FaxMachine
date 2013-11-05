package com.qingzhi.apps.fax.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

public class DrawImageView extends ImageView {
    private int type = 0;
    private int rectWidth;
    private int rectHeight;

    private int rectX;
    private int rectY;

    public DrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    Paint paint = new Paint();

    {
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(7.5f);// 设置线宽
        paint.setAlpha(100);
    }

    Paint p = new Paint();

    {
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStrokeWidth(2.5f);
        p.setTextSize(38);
        p.setAlpha(100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width * height > 0) {
            if (type == 1) {
                // 身份证
                rectX = 0;
                rectWidth = width;
                rectHeight = (int) ((float) width * 54 / 85.6 * 1.1);
                rectY = (height - rectHeight) / 2;
            } else if (type == 2) {
                // A4纸
                rectWidth = width;
                rectHeight = (int) ((float) width * 297 / 210 * 1.1);
                if (rectHeight > height) {
                    rectWidth = (int) ((float) rectHeight * 210 / 297 * ((float) height / rectHeight) * 0.9);
                    rectHeight = height;
                    rectX = (width - rectWidth) / 2;
                    rectY = 0;
                } else {
                    rectX = 0;
                    rectY = (height - rectHeight) / 2;
                }
            }
        }

        if (type > 0) {
            canvas.drawRect(new Rect(rectX, rectY, rectX + rectWidth, rectY + rectHeight), paint);//绘制矩形
            if (type == 1)
                canvas.drawText("请将身份证置于框内", rectX + 50, (rectY + rectHeight) / 2, p);
            if (type == 2)
                canvas.drawText("请A4纸宽和边框对齐", rectX + 50, (rectY + rectHeight) / 2, p);
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRectWidth() {
        return rectWidth;
    }

    public int getRectHeight() {
        return rectHeight;
    }

    public int getRectX() {
        return rectX;
    }

    public int getRectY() {
        return rectY;
    }
}
