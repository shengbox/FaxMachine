package com.qingzhi.apps.fax.io.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 个人基本信息
 */
public class Person implements Parcelable {
    public String n;
    public String imgS;     //头像：30*30
    public String img;      //头像：50*50
    public String imgL;     //头像：154*154
    public String hts;      //头像更新时间戳,long，参考1970-1-1，0表示未设置头像
    public String sex;      //性别， m 表示male, f 表示 female，空字符串表示未知
    public String loc;      //所在地区的描述
    public String desc;     //“我是描述“

    public Person() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    private Person(Parcel in) {
        n = in.readString();
        imgS = in.readString();
        img = in.readString();
        imgL = in.readString();
        hts = in.readString();
        sex = in.readString();
        loc = in.readString();
        desc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(n);
        dest.writeString(imgS);
        dest.writeString(img);
        dest.writeString(imgL);
        dest.writeString(hts);
        dest.writeString(sex);
        dest.writeString(loc);
        dest.writeString(desc);
    }
}
