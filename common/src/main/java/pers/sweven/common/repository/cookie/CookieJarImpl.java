package pers.sweven.common.repository.cookie;


import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import pers.sweven.common.repository.cookie.store.CookieStore;

public class CookieJarImpl implements CookieJar {

    private final CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null!");
        }
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.saveCookie(url, cookies);
//        CookiesManager.getInstance().cookieMap.put(url,cookies);

    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        return cookieStore.loadCookie(url);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }
}