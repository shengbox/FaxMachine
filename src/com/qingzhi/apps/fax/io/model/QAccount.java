package com.qingzhi.apps.fax.io.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 三宝的青芝账号
 */
public class QAccount implements Parcelable {
    public String q;          // 青芝ID，long 型
    public String c;        // 青芝Token。
    public String ln;       // line number
    public String lp;       // line number password
    public int nq;          //新青芝账号标记
    public int pc_push;     //pc在线时，是否推送ios消息
    public Person p;      //详见《11.1.1 person_info(个人基本信息)》字段定义
    public ConfPassword conf_pwd; // 个人的会议密码信息。此字段仅自身可见。定义详见 conf_password对象定义。
    public String bpc;      // bound phone country 绑定的手机的国家码，不可空
    public String bpn;      // bound phone number 绑定的手机号，不可空
    public String pass_md5;

    public QAccount() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QAccount> CREATOR = new Creator<QAccount>() {
        public QAccount createFromParcel(Parcel in) {
            return new QAccount(in);
        }

        public QAccount[] newArray(int size) {
            return new QAccount[size];
        }
    };

    private QAccount(Parcel in) {
        q = in.readString();
        c = in.readString();
        ln = in.readString();
        lp = in.readString();
        nq = in.readInt();
        pc_push = in.readInt();
        bpc = in.readString();
        bpn = in.readString();
        p = in.readParcelable(Person.class.getClassLoader());
        conf_pwd = in.readParcelable(ConfPassword.class.getClassLoader());
        pass_md5 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(q);
        dest.writeString(c);
        dest.writeString(ln);
        dest.writeString(lp);
        dest.writeInt(nq);
        dest.writeInt(pc_push);
        dest.writeString(bpc);
        dest.writeString(bpn);
        dest.writeParcelable(p, flags);
        dest.writeParcelable(conf_pwd, flags);
        dest.writeString(pass_md5);
    }
}
