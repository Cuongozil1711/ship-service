package vn.soft.ship_service.utils;

import java.util.Base64;

public class Base64Utils {
    public static boolean isBase64String(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
