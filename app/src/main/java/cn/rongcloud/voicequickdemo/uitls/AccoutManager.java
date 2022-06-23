package cn.rongcloud.voicequickdemo.uitls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kit.cache.GsonUtil;
import com.kit.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.authentication.bean.Account;

public class AccoutManager {
    private final static List<Account> accounts = new ArrayList<>(16);

    private static String currentId;//当前账号

    public static List<Account> getAccounts() {
        return accounts;
    }

    public static String getCurrentId() {
        return currentId;
    }

    public static void setAcctount(Account a, boolean mine) {
        Logger.e("AccountManager","a = "+ GsonUtil.obj2Json(a));
        if (null == a) return;
        if (mine) {
            currentId = a.getUserId();
        }
        if (!accounts.contains(a)) {
            accounts.add(a);
        }
    }

    @Nullable
    public static Account getAccount(@Nullable String userId) {
        int size = accounts.size();
        Account result = null;
        for (int i = 0; i < size; i++) {
            Account acc = accounts.get(i);
            if (acc.getUserId().equals(userId)) {
                result = acc;
                break;
            }
        }
        return result;
    }


    @NonNull
    public static String getAccountName(@Nullable String userId) {
        Account accout = getAccount(userId);
        return null == accout ? userId : accout.getUserName();
    }

}
