/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package ichat4j;

public class ApiConfig {

    private static String broadcastId = null;
    private static String appId = null;

    // 开发模式将输出消息交互 xml 到控制台
    private static boolean devMode = false;

    public static void setDevMode(boolean devMode) {
        ApiConfig.devMode = devMode;
    }

    public static boolean isDevMode() {
        return devMode;
    }

    public static String getBroadcastId() {
        if (broadcastId == null)
            throw new RuntimeException("init ApiConfig.setUrl(...) first");
        return broadcastId;
    }

    public static void setBroadcastId(String broadcastId) {
        if (broadcastId == null)
            throw new IllegalArgumentException("broadcastId can not be null");
        ApiConfig.broadcastId = broadcastId;
    }


    public static String getAppId() {
        if (appId == null)
            throw new RuntimeException("init ApiConfig.setAppId(...) first");
        return appId;
    }

    public static void setAppId(String appId) {
        if (appId == null)
            throw new IllegalArgumentException("appId can not be null");
        ApiConfig.appId = appId;
    }

}




