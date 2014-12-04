package ichat4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.util.Map;

/**
 * Created by luob on 2014/12/3 0003.
 */
public class ChatApi {
    private static String login = "http://192.168.0.207/chat/login.php";
    private static String sendMsg = "http://192.168.0.207/chat/api/send.php";

    private static Map<String, String> accessCookie;

    static {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(ClassLoader.getSystemResource("cookie.tmp").getFile());
            ois = new ObjectInputStream(fis);
            accessCookie = (Map<String, String>) ois.readObject();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Map<String, String> getAccessCookie() {
        if (accessCookie != null)
            return accessCookie;

        refreshAccesCookie();
        return accessCookie;
    }

    public static void refreshAccesCookie() {
        accessCookie = requestAccesCookie();
    }

    private static synchronized Map<String, String> requestAccesCookie() {
        Map<String, String> result = null;
        for (int i = 0; i < 3; i++) {
            String uname = ApiConfig.getAppId();
            try {
                Connection.Response conn = Jsoup.connect(login).data("uname", uname).timeout(3000).method(Connection.Method.POST).execute();
                if (conn.statusCode() == 200) {
                    result = conn.cookies();
                    FileInputStream fis = null;
                    ObjectInputStream ois = null;
                    try {
                        FileOutputStream fos = new FileOutputStream(ClassLoader.getSystemResource("cookie.tmp").getFile());
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(result);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (ois != null) {
                            try {
                                ois.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        }
        return result;
    }

    /**
     * 发送群消息
     */
    public ApiResult sendBroadcast(String msg) {
        ApiResult result = doSendMsg(ApiConfig.getBroadcastId(), msg);
        if (result.isAccessTokenInvalid()) {
            ChatApi.refreshAccesCookie();        // 刷新 access_cookie
            result = doSendMsg(ApiConfig.getBroadcastId(), msg);
        }
        return result;
    }

    private ApiResult doSendMsg(String uid2, String msg) {
        String jsonResult = null;
        try {
            Connection.Response conn = Jsoup.connect(sendMsg)
                    .cookies(getAccessCookie())
                    .data("uid2", uid2)
                    .data("text", msg)
                    .timeout(30000)
                    .method(Connection.Method.POST).execute();
            if (conn.statusCode() == 200)
                jsonResult = conn.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResult(jsonResult);
    }

    public static void main(String[] args) {
        ApiConfig.setAppId("dev");
        ApiConfig.setBroadcastId("luob");

        accessCookie = getAccessCookie();
        System.out.println(accessCookie);
        ApiResult result = new ChatApi().sendBroadcast("from jsoup");
        System.out.println(result.getJson());
    }

}
