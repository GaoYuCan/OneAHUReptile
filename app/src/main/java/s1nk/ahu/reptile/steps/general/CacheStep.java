package s1nk.ahu.reptile.steps.general;

import android.util.Log;

import java.util.HashMap;

import s1nk.ahu.reptile.common.bean.CachedCookie;
import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.common.exception.LoginExpiredException;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.IStepChain;

public class CacheStep<T> implements IStep<T> {
    private static final HashMap<String, CachedCookie> cachedCookies = new HashMap<>();

    @Override
    public Ret<T> handle(IStepChain<T> chain) {
        String studentID = chain.getSimpleData("studentID", String.class);
        // 不在对 studentID 进行判空，这里非用户控制
        // 判断是否存在缓存
        boolean flag = false;
        if (cachedCookies.containsKey(studentID)) {
            CachedCookie cookie = cachedCookies.get(studentID);
            if (cookie == null || cookie.isExpired()) {
                cachedCookies.remove(studentID);
            } else {
                // 从缓存中读取数据
                chain.putSimpleData("cookies", cookie.getCookie());
                flag = true;
            }
        }
        Ret<T> ret = chain.proceed();
        if (flag && !ret.isSuccessful() && (ret.getError() instanceof LoginExpiredException)) {
            // 删除缓存后重试
            chain.removeSimpleData("cookies");
            ret = chain.proceed();
        }
        // 添加缓存
        if (!flag && ret.isSuccessful()) {
            String cookies = chain.getSimpleData("cookies", String.class);
            // 有效时间 20 min
            cachedCookies.put(studentID, new CachedCookie(cookies,
                    System.currentTimeMillis() + 20 * 60 * 1000));
        }
        return ret;
    }
}
