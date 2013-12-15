package com.qingzhi.apps.fax.util;

import android.text.TextUtils;

import java.util.*;

public class ApiSignatureTool {

    private static ApiSignatureTool defaultTool = new ApiSignatureTool();

    public class KeyValue {

        private String key = null;
        private String feature = null;

        public KeyValue(String key, String object) {
            this.key = key;
            this.feature = object;
        }

    }

    @SuppressWarnings("unchecked")
    /**
     * 签名。methodName也要参与运算，放在第一个。secret 放在最后一个。
     * NOTE: 若参数值为空或者null，则不参与运算。
     */
    public static String signWithMD5(String methodName, Map<String, String> map, String secret) {

        if (TextUtils.isEmpty(methodName)
                || TextUtils.isEmpty(secret)
                || map == null) {
            throw new Error("Unexcepted input param.");
        }

        /**
         * 生成签名的方式：
         * methodName:Map的值逐个按照key的英文正序排列 : ap_serct
         */

        /**
         * 先将 map 排序
         */
        List<KeyValue> list = new ArrayList<KeyValue>();

        for (String key : map.keySet()) {

            if (map.get(key) != null) {
                String value = map.get(key).toString();
                KeyValue kv = defaultTool.new KeyValue(key, value);
                list.add(kv);
            }

        }

        /**
         * 按字母序将 key 重新排列
         */
        Collections.sort(list, new Comparator() {

            public int compare(Object objLeft, Object objRight) {

                KeyValue kv01 = (KeyValue) objLeft;
                KeyValue kv02 = (KeyValue) objRight;

                return kv01.key.compareTo(kv02.key);

            }

        });

        StringBuffer sContent = new StringBuffer();

        /**
         * 按照  <methodName>:<value>:<value>:....<value>:<secret> 的格式拼装字串，
         * 然后进行 MD5 签名。
         */
        sContent.append(methodName);

        for (KeyValue kv : list) {

            if (!TextUtils.isEmpty(kv.feature)) {
                sContent.append(":").append(kv.feature);
            }

        }

        sContent.append(":").append(secret);
        return QingzhiUtil.getMd5(sContent.toString());
    }
}
