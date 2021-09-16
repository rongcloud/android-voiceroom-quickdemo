package cn.rongcloud.quickdemo.uitls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AccoutManager {
    private final static List<Accout> accounts = new ArrayList<>(4);

    static {
        // TODO: 第二步  must add account
        //  Accout accout = new Accout("Your UserId", "Your UserName");
        //  accout.token = "Your Token ";//token 需和userId一一对应
        //  accounts.add(accout);
    }

    private static String currentId;//当前账号

    public static List<Accout> getAccounts() {
        return accounts;
    }

    public static String getCurrentId() {
        return currentId;
    }

    public static void setCurrent(String currentId) {
        AccoutManager.currentId = currentId;
    }

    @Nullable
    public static Accout getAccout(@Nullable String userId) {
        int size = accounts.size();
        Accout result = null;
        for (int i = 0; i < size; i++) {
            Accout acc = accounts.get(i);
            if (acc.userId.equals(userId)) {
                result = acc;
                break;
            }
        }
        return result;
    }


    @NonNull
    public static String getAccoutName(@Nullable String userId) {
        Accout accout = getAccout(userId);
        return null == accout ? "离线" : accout.getName();
    }

    public static class Accout {
        private String name;
        private String userId;
        private String token;

        Accout(String userId, String name) {
            this.name = name;
            this.userId = userId;
        }

        public String getName() {
            return name;
        }


        public String getUserId() {
            return userId;
        }

        public String getToken() {
            return token;
        }


    }
}
