package com.qingzhi.apps.fax.io.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 通用附件接口对象
 */
public class Attach implements Parcelable {
    public String access_code;
    public String creator_id;
    public String creator_name;
    public int enable_thumb;
    public String enterprise_id;
    public String file_name;
    public String height;
    public String id;
    public String length;
    public String md5;
    public String memo;
    public String mime_type;
    public String size;
    public String storage_id;
    public String time;
    public String width;
    public int pages_bundle_id;

//    public Attach(Parcel in) {
//        readFromParcel(in);
//    }

    public void readFromParcel(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Attach> CREATOR = new Creator<Attach>() {
        @Override
        public Attach createFromParcel(Parcel source) {
            Attach attach = new Attach();
            attach.access_code = source.readString();
            attach.creator_id = source.readString();
            attach.creator_name = source.readString();
            attach.enable_thumb = source.readInt();
            attach.enterprise_id = source.readString();
            attach.file_name = source.readString();
            attach.height = source.readString();
            attach.id = source.readString();
            attach.length = source.readString();
            attach.md5 = source.readString();
            attach.memo = source.readString();
            attach.mime_type = source.readString();
            attach.size = source.readString();
            attach.storage_id = source.readString();
            attach.time = source.readString();
            attach.width = source.readString();
            attach.pages_bundle_id = source.readInt();
            return attach;
        }

        @Override
        public Attach[] newArray(int size) {
            return new Attach[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(access_code);
        dest.writeString(creator_id);
        dest.writeString(creator_name);
        dest.writeInt(enable_thumb);
        dest.writeString(enterprise_id);
        dest.writeString(file_name);
        dest.writeString(height);
        dest.writeString(id);
        dest.writeString(length);
        dest.writeString(md5);
        dest.writeString(memo);
        dest.writeString(mime_type);
        dest.writeString(size);
        dest.writeString(storage_id);
        dest.writeString(time);
        dest.writeString(width);
        dest.writeInt(pages_bundle_id);
    }
}
