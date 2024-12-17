package com.app.test.data.local;

import androidx.annotation.Nullable;

import com.app.test.data.entity.UserInfo;

import java.util.Objects;

import pers.sweven.common.utils.cache.CacheManager;

/**
 * <br/>
 * Created by Sweven on 2023/9/22--12:05.
 * Email: sweventears@163.com
 */
public class LocalManager {
    private static LocalManager instance;

    private String token;
    private String loginName;
    private UserInfo userInfo;
    private boolean punchFollowed;

    private LocalManager() {
    }

    public static LocalManager getInstance() {
        if (instance == null) {
            instance = new LocalManager();
        }
        return instance;
    }

    @Nullable
    public String getLoginName() {
        if (loginName == null) {
            loginName = CacheManager.getInStance().getAsString("login_name");
        }
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
        if (loginName == null) {
            CacheManager.getInStance().remove("login_name");
        } else {
            CacheManager.getInStance().put("login_name", loginName);
        }
    }

    public String getToken() {
        if (token == null) {
            token = CacheManager.getInStance().getAsString("token");
        }
        return token;
    }

    public void setToken(String token, long second) {
        this.token = token;
        if (token == null) {
            CacheManager.getInStance().remove("token");
        } else {
            CacheManager.getInStance().put("token", token, (int) second);
        }
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isPunchFollowed() {
        if (!punchFollowed) {
            String s = CacheManager.getInStance().getAsString("punch_followed");
            punchFollowed = Objects.equals(s, "true");
        }
        return punchFollowed;
    }

    public void setPunchFollowed(boolean punchFollowed) {
        this.punchFollowed = punchFollowed;
        CacheManager.getInStance().put("punch_followed", punchFollowed ? "true" : "false");
    }
}
