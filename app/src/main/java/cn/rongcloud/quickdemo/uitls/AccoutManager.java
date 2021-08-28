package cn.rongcloud.quickdemo.uitls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AccoutManager {
    private final static List<Accout> accounts = new ArrayList<>(4);

    static {
        Accout accout = new Accout("92814153-3330-48dd-9627-6eb190c3e101", "测试1");
        accout.token = "r3CkHmcqe/7SOuRelcAA6T0EGJbKyhhJd4lM5R1sjd4dqebDKYJHlNs5peb8z1U4vlcCu5XJ7aNNTV5nptNBkQ==@4d1h.cn.rongnav.com;4d1h.cn.rongcfg.com";
        accounts.add(accout);
        accout = new Accout("675aac38-0fa5-433d-a708-f2e2ba161f3b", "测试2");
        accout.token = "Kv7dLdFVKJdTAdg4BOKgpsPrFVLmLzSKVx3O8NKc5NpDAzQBwNcYDds5peb8z1U4oXQ47RkwlGxgj+9Kp58cvg==@4d1h.cn.rongnav.com;4d1h.cn.rongcfg.com";
        accounts.add(accout);
        accout = new Accout("05568b81-ac7a-4276-b629-6b433c275ea3", "测试");
        accout.token = "NnnW2SbMYsqtiCwO115diWV0kvUlkYnag78TV70FFAFq3AsJsuiCHts5peb8z1U4sgYlNnsio/whmfXNbv6c3w==@4d1h.cn.rongnav.com;4d1h.cn.rongcfg.com";
        accounts.add(accout);
    }

    public static List<Accout> getAccounts() {
        return accounts;
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
        return null == accout ? "" : accout.getName();
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
