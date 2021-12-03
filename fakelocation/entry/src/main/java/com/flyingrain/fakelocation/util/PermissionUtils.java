package com.flyingrain.fakelocation.util;

import ohos.app.Context;
import ohos.bundle.IBundleManager;

public class PermissionUtils {

    public static boolean hasPermission(String permissionCode, Context context) {
        return context.verifySelfPermission(permissionCode) == IBundleManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(String permission, Context context) {
        if (context.verifySelfPermission(permission) != IBundleManager.PERMISSION_GRANTED) {
            context.requestPermissionsFromUser(new String[]{permission}, 0);
        }
    }
}
