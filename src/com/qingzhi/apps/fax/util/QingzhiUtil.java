package com.qingzhi.apps.fax.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class QingzhiUtil {

    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getMd5(String s) {
        char hexChar[] = {'0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        //md5加密算法的加密对象为字符数组，这里是为了得到加密的对象
        byte[] b = s.getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(b);
            byte[] b2 = md.digest();// 进行加密并返回字符数组
            char str[] = new char[b2.length << 1];
            int len = 0;
            //将字符数组转换成十六进制串，形成最终的密文
            for (int i = 0; i < b2.length; i++) {
                byte val = b2[i];
                str[len++] = hexChar[(val >>> 4) & 0xf];
                str[len++] = hexChar[val & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
