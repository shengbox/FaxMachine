package com.qingzhi.apps.fax.io.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Fax implements Parcelable {
    public static final int FaxSendStatusFail = 0;
    public static final int FaxSendStatusIng = 1;
    public static final int FaxSendStatusSucces = 2;

    public String attach_bundle_access_code;
    public String attach_bundle_id;
    public int category;
    public String create_time_str;
    public String create_user;
    public String duration_seconds;
    public String enterprise_id;
    public int has_commit_to_server;
    public String id;
    public String last_send_time_str;
    public String memo;
    public String receiver_desc;
    public String receiver_phone;
    public int send_error_code;
    public String send_error_message;
    public String sender_phone;
    public String sending_page_count;
    public int has_read;

    public int sendStatus() {
        int status = FaxSendStatusFail;
        switch (has_commit_to_server) {
            case 0:
                status = FaxSendStatusIng;
                break;
            case 1:
                status = FaxSendStatusIng;
                break;
            case 2:
                status = FaxSendStatusFail;
                break;
            case 3:
                if (send_error_code == 200) {
                    status = FaxSendStatusSucces;
                }
                break;
            default:
                break;
        }
        return status;
    }

    public static int sendStatus(int has_commit_to_server, int send_error_code) {
        int status = FaxSendStatusFail;
        switch (has_commit_to_server) {
            case 0:
                status = FaxSendStatusIng;
                break;
            case 1:
                status = FaxSendStatusIng;
                break;
            case 2:
                status = FaxSendStatusFail;
                break;
            case 3:
                if (send_error_code == 200) {
                    status = FaxSendStatusSucces;
                }
                break;
            default:
                break;
        }
        return status;
    }

    public String displayStatus() {
        if (this.sendStatus() == FaxSendStatusSucces) {
            return "成功发送";
        } else if (this.sendStatus() == FaxSendStatusIng) {
            return "发送中";
        }
        //
        String failStr = "发送失败";

        switch (send_error_code) {
            case 40035:
                failStr = "文档格式无法转换为传真页面";
                break;
            case 40050:
                failStr = "被叫号码不存在";
                break;
            case 40051:
                failStr = "被叫号码忙";
                break;
            case 40052:
                failStr = "被叫号码不接听";
                break;
            case 40053:
                failStr = "被叫无传真信号";
                break;
            case 40054:
                failStr = "已呼通，但传真发送未完成";
                break;
            case 40055:
            case 40056:
                failStr = "传真服务故障或停止服务";
                break;
            default:
                break;
        }
        return failStr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Fax> CREATOR = new Creator<Fax>() {
        @Override
        public Fax createFromParcel(Parcel source) {
            Fax fax = new Fax();
            fax.attach_bundle_access_code = source.readString();
            fax.attach_bundle_id = source.readString();
            fax.category = source.readInt();
            fax.create_time_str = source.readString();
            fax.create_user = source.readString();
            fax.duration_seconds = source.readString();
            fax.enterprise_id = source.readString();
            fax.has_commit_to_server = source.readInt();
            fax.id = source.readString();
            fax.last_send_time_str = source.readString();
            fax.memo = source.readString();
            fax.receiver_desc = source.readString();
            fax.receiver_phone = source.readString();
            fax.send_error_code = source.readInt();
            fax.send_error_message = source.readString();
            fax.sender_phone = source.readString();
            fax.sending_page_count = source.readString();
            fax.has_read = source.readInt();
            return fax;
        }

        @Override
        public Fax[] newArray(int size) {
            return new Fax[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(attach_bundle_access_code);
        dest.writeString(attach_bundle_id);
        dest.writeInt(category);
        dest.writeString(create_time_str);
        dest.writeString(create_user);
        dest.writeString(duration_seconds);
        dest.writeString(enterprise_id);
        dest.writeInt(has_commit_to_server);
        dest.writeString(id);
        dest.writeString(last_send_time_str);
        dest.writeString(memo);
        dest.writeString(receiver_desc);
        dest.writeString(receiver_phone);
        dest.writeInt(send_error_code);
        dest.writeString(send_error_message);
        dest.writeString(sender_phone);
        dest.writeString(sending_page_count);
        dest.writeInt(has_read);
    }
}
