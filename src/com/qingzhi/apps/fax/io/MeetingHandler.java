/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qingzhi.apps.fax.io;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.qingzhi.apps.fax.io.model.Meeting;
import com.qingzhi.apps.fax.provider.FaxContract;
import com.qingzhi.apps.fax.util.Lists;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.qingzhi.apps.fax.util.LogUtils.makeLogTag;

/**
 * Handler that parses group JSON data into a list of content provider operations.
 */
public class MeetingHandler extends JSONHandler {

    private static final String TAG = makeLogTag(MeetingHandler.class);

    public MeetingHandler(Context context) {
        super(context);
    }

    public ArrayList<ContentProviderOperation> parse(String json) throws IOException {
        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
        JsonObject object = new JsonParser().parse(json).getAsJsonObject();
        List<Meeting> list = new Gson().fromJson(object.get("list"), new TypeToken<List<Meeting>>() {
        }.getType());
        for (int i = 0; i < list.size(); i++) {
            parseMeeting(list.get(i), batch);
        }
        return batch;
    }

    private static void parseMeeting(Meeting meeting, ArrayList<ContentProviderOperation> batch) {
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newInsert(FaxContract.addCallerIsSyncAdapterParameter(
                        FaxContract.Meeting.CONTENT_URI));
        parser(builder, meeting);
        batch.add(builder.build());
    }

    public static Builder parser(Builder builder, Meeting meeting) {
        Class userCla = (Class) meeting.getClass();
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true);
            Object val = null;
            try {
                val = f.get(meeting);
                builder.withValue(f.getName(), val);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return builder;
    }
}
