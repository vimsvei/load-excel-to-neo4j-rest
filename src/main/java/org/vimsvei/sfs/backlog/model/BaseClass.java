package org.vimsvei.sfs.backlog.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

public class BaseClass {

    public String toJSON() {
        return new Gson().toJson(this);
    }

    public String toFullJSON() {
        return new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC).create().toJson(this);
    }
}
