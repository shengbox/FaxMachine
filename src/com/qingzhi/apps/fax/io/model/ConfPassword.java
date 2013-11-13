package com.qingzhi.apps.fax.io.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 会议密码信息
 * User: zhangsheng
 * Date: 13-10-9
 */
public class ConfPassword implements Parcelable {
    public String holder_pwd;       // 8 位主持密码
    public String talker_pwd;       // 8 位与会密码
    public String listener_pwd;     // 8 位旁听密码

    public ConfPassword() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ConfPassword> CREATOR = new Creator<ConfPassword>() {
        public ConfPassword createFromParcel(Parcel in) {
            return new ConfPassword(in);
        }

        public ConfPassword[] newArray(int size) {
            return new ConfPassword[size];
        }
    };

    private ConfPassword(Parcel in) {
        holder_pwd = in.readString();
        talker_pwd = in.readString();
        listener_pwd = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(holder_pwd);
        dest.writeString(talker_pwd);
        dest.writeString(listener_pwd);
    }
}
