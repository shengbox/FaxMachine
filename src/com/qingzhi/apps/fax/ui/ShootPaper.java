package com.qingzhi.apps.fax.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.*;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.qingzhi.apps.fax.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ShootPaper extends FragmentActivity implements SurfaceHolder.Callback {
    private static final String TAG = "FAX";
    private boolean isPreview = false;
    private SurfaceView mPreviewSV = null; // 预览SurfaceView
    private DrawImageView mDrawIV = null;
    private SurfaceHolder mySurfaceHolder = null;
    private Button mPhotoImgBtn = null;
    private Camera myCamera = null;
    private Bitmap mBitmap = null;
    private AutoFocusCallback myAutoFocusCallback = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏无标题
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        Window myWindow = this.getWindow();
//        myWindow.setFlags(flag, flag);

        setContentView(R.layout.activity_rect_photo);

        // 初始化SurfaceView
        mPreviewSV = (SurfaceView) findViewById(R.id.previewSV);
        mPreviewSV.setZOrderOnTop(false);
        mySurfaceHolder = mPreviewSV.getHolder();
        mySurfaceHolder.setFormat(PixelFormat.TRANSPARENT);// translucent半透明
        // transparent透明

        mySurfaceHolder.addCallback(this);
        mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // 自动聚焦变量回调
        myAutoFocusCallback = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                if (success)// success表示对焦成功
                {
                    Log.i(TAG, "myAutoFocusCallback: success...");
                    // myCamera.setOneShotPreviewCallback(null);
                } else {
                    // 未对焦成功
                    Log.i(TAG, "myAutoFocusCallback: 失败了...");
                }
            }
        };

        // 绘制矩形的ImageView
        mDrawIV = (DrawImageView) findViewById(R.id.drawIV);
        mDrawIV.setType(1);

        mPhotoImgBtn = (Button) findViewById(R.id.photoImgBtn);
        mPhotoImgBtn.setOnClickListener(new PhotoOnClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add("Set");
        item.setIcon(android.R.drawable.ic_menu_set_as);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("Set".equals(item.getTitle())) {
            int i = mDrawIV.getType();
            i++;
            mDrawIV.setType(i % 3);
            mDrawIV.invalidate();
        }
        return super.onOptionsItemSelected(item);
    }

    /* 下面三个是SurfaceHolder.Callback创建的回调函数 */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height)
    // 当SurfaceView/预览界面的格式和大小发生改变时，该方法被调用
    {
        Log.i(TAG, "SurfaceHolder.Callback:surfaceChanged!");
        initCamera();
    }

    public void surfaceCreated(SurfaceHolder holder)
    // SurfaceView启动时/初次实例化，预览界面被创建时，该方法被调用。
    {
        myCamera = Camera.open();
        try {
            myCamera.setPreviewDisplay(mySurfaceHolder);
            Log.i(TAG, "SurfaceHolder.Callback: surfaceCreated!");
        } catch (IOException e) {
            if (null != myCamera) {
                myCamera.release();
                myCamera = null;
            }
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    // 销毁时被调用
    {
        Log.i(TAG, "SurfaceHolder.Callback：Surface Destroyed");
        if (null != myCamera) {
            myCamera.setPreviewCallback(null);
            /*
                                                 * 在启动PreviewCallback时这个必须在前不然退出出错。
												 * 这里实际上注释掉也没关系
												 */

            myCamera.stopPreview();
            isPreview = false;
            myCamera.release();
            myCamera = null;
        }
    }

    // 初始化相机
    public void initCamera() {
        if (isPreview) {
            myCamera.stopPreview();
        }
        if (null != myCamera) {
            Camera.Parameters myParam = myCamera.getParameters();
            //查询屏幕的宽和高
//            WindowManager wm =
//            (WindowManager)getSystemService(Context.WINDOW_SERVICE);
//            Display display = wm.getDefaultDisplay();
//            Log.i(TAG,
//            "屏幕宽度："+display.getWidth()+" 屏幕高度:"+display.getHeight());

            myParam.setPictureFormat(PixelFormat.JPEG);// 设置拍照后存储的图片格式

            //查询camera支持的picturesize和previewsize
            List<Camera.Size> pictureSizes = myParam.getSupportedPictureSizes();
            List<Camera.Size> previewSizes = myParam.getSupportedPreviewSizes();
            for (int i = 0; i < pictureSizes.size(); i++) {
                Camera.Size size = pictureSizes.get(i);
                Log.i(TAG, "initCamera:摄像头支持的pictureSizes: width = " + size.width + "height = " + size.height);
            }
            for (int i = 0; i < previewSizes.size(); i++) {
                Camera.Size size = previewSizes.get(i);
                Log.i(TAG, "initCamera:摄像头支持的previewSizes: width = " + size.width + "height = " + size.height);
            }

            // 设置大小和方向等参数
//            myParam.setPictureSize(1280, 960);
//			myParam.setPreviewSize(960, 720);
//            myParam.set("rotation", 90);
            myCamera.setDisplayOrientation(90);
            myParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            myCamera.setParameters(myParam);
            myCamera.startPreview();
            myCamera.autoFocus(myAutoFocusCallback);
            isPreview = true;
        }
    }

    /* 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量 */
    ShutterCallback myShutterCallback = new ShutterCallback()
            // 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    {

        public void onShutter() {
            Log.i(TAG, "myShutterCallback:onShutter...");

        }
    };

    /**
     * 对jpeg图像数据的回调
     */
    PictureCallback myJpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            if (null != data) {
                // data是字节数据，将其解析成位图
                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                myCamera.stopPreview();
                isPreview = false;
            }
            // 在三星的山寨机上，myParam.set("rotation",
            // 90)失效。图片竟然不能旋转了，故这里要旋转下
            Matrix matrix = new Matrix();
            matrix.postRotate((float) 90.0);
            int height = mBitmap.getWidth();
            int width = mBitmap.getHeight();

            Bitmap targetBitmap = null;

            if (mDrawIV.getType() > 0) {

                int x = (int) ((float) width * mDrawIV.getRectX() / mDrawIV.getWidth());
                int y = (int) ((float) height * mDrawIV.getRectY() / mDrawIV.getHeight());
                int h = (int) ((float) height * mDrawIV.getRectHeight() / mDrawIV.getHeight());
                int w = (int) ((float) width * mDrawIV.getRectWidth() / mDrawIV.getWidth());

                float scale = 0;
                // 缩放 身份证
                if (mDrawIV.getType() == 1) {
                    scale = (float) 704 / w;
                }
                if (mDrawIV.getType() == 2) {
                    scale = (float) 1728 / w;
                }

                matrix.postScale(scale, scale);
                //先截取再进行旋转和缩放
                Bitmap rectBitmap = Bitmap.createBitmap(mBitmap, y, x, h, w, matrix, true);

                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                    mBitmap = null;
                }

                targetBitmap = Bitmap.createBitmap(1728, 2444, Bitmap.Config.ARGB_8888);
                Canvas cv = new Canvas(targetBitmap);
                cv.drawColor(Color.WHITE);

                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);
                ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(f);

                if (mDrawIV.getType() == 1)
                    cv.drawBitmap(rectBitmap, 100, 100, paint);
                if (mDrawIV.getType() == 2)
                    cv.drawBitmap(rectBitmap, 0, 0, paint);
                cv.save(Canvas.ALL_SAVE_FLAG);
                cv.restore();

                if (rectBitmap != null && !rectBitmap.isRecycled()) {
                    rectBitmap.recycle();
                }
            } else {
                targetBitmap = mBitmap;
            }
            // 保存图片到sdcard
            long dataTake = System.currentTimeMillis();
            if (null != targetBitmap) {
                saveJpeg(targetBitmap, dataTake);
            }

            if (targetBitmap != null && !targetBitmap.isRecycled()) {
                targetBitmap.recycle();

            }

            Intent intent = new Intent(ShootPaper.this, PaperShow.class);
            intent.putExtra("fileName", String.valueOf(dataTake));
            startActivity(intent);
            ShootPaper.this.finish();
            System.gc();
        }
    };

    // 拍照按键的监听
    public class PhotoOnClickListener implements OnClickListener {
        public void onClick(View v) {
            if (isPreview && myCamera != null) {
                myCamera.takePicture(myShutterCallback, null, myJpegCallback);
            }
        }
    }

    /* 给定一个Bitmap，进行保存 */
    public void saveJpeg(Bitmap bm, long dataTake) {
        String savePath = "/mnt/sdcard/rectPhoto/";
        File folder = new File(savePath);
        if (!folder.exists()) // 如果文件夹不存在则创建
        {
            folder.mkdir();
        }

        String jpegName = savePath + dataTake + ".jpg";
        Log.i(TAG, "saveJpeg:jpegName--" + jpegName);
        // File jpegFile = new File(jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);

            // //如果需要改变大小(默认的是宽960×高1280),如改成宽600×高800
            // Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);

            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            Log.i(TAG, "saveJpeg：存储完毕！");
        } catch (IOException e) {
            Log.i(TAG, "saveJpeg:存储失败！");
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed()
    // 无意中按返回键时要释放内存
    {
        super.onBackPressed();
        ShootPaper.this.finish();
    }
}
