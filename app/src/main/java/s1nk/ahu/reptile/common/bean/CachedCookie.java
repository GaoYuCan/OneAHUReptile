package s1nk.ahu.reptile.common.bean;

import androidx.annotation.NonNull;

public class CachedCookie {
    private final String cookie;
    private final long expiredTime;

    public CachedCookie(String cookie, long expiredTime) {
        this.cookie = cookie;
        this.expiredTime = expiredTime;
    }

    public String getCookie() {
        return cookie;
    }

    public boolean isExpired() {
        return expiredTime < System.currentTimeMillis();
    }

    @NonNull
    @Override
    public String toString() {
        return "CachedCookie{" +
                "cookie='" + cookie + '\'' +
                ", expiredTime=" + expiredTime +
                '}';
    }
}
