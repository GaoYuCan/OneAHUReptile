package s1nk.ahu.reptile.steps.one;


import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.common.exception.LoginExpiredException;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.IStepChain;
import s1nk.ahu.reptile.steps.general.BasicStepChain;

public class OneCardAuthorizationStep<T> implements IStep<T> {
    @Override
    public Ret<T> handle(IStepChain<T> chain) {
        // 获取 cookies
        String cookies = chain.getSimpleData("cookies", String.class);
        // cookies 不可能为空
        Request request = new Request.Builder()
                .url("https://ycard.ahu.edu.cn/plat/shouyeUser")
                .addHeader("Cookie", cookies)
                .get()
                .build();
        OkHttpClient client = BasicStepChain.httpClient;
        try (Response response = client.newCall(request).execute()) {
            // priorResponse == null, 在正常情况下是不可能存在的
            HttpUrl from = Objects.requireNonNull(response.priorResponse())
                    .request().url();
            String ticket = from.queryParameter("ticket");
            if (ticket == null) {
                // 过期了
                return Ret.newFailedInstance(new LoginExpiredException("Cookie 过期，校园卡系统认证失败！"));
            }
            FormBody getTokenForm = new FormBody.Builder()
                    .add("username", ticket)
                    .add("password", ticket)
                    .add("grant_type", "password")
                    .add("scope", "all")
                    .add("loginFrom", "h5")
                    .add("logintype", "sso")
                    .add("device_token", "h5")
                    .add("synAccessSource", "h5")
                    .build();
            request = new Request.Builder()
                    .url("https://wvpn.ahu.edu.cn/https/77726476706e69737468656265737421e9f4408e237e69586b468ca88d1b203b/berserker-auth/oauth/token?vpn-12-o2-ycard.ahu.edu.cn")
                    .addHeader("Cookie", cookies)
                    .addHeader("Referer", "https://wvpn.ahu.edu.cn/https/77726476706e69737468656265737421e9f4408e237e69586b468ca88d1b203b")
                    .addHeader("Authorization", "Basic bW9iaWxlX3NlcnZpY2VfcGxhdGZvcm06bW9iaWxlX3NlcnZpY2VfcGxhdGZvcm1fc2VjcmV0")
                    .post(getTokenForm)
                    .build();
            Response rsp = client.newCall(request).execute();
            String jsonStr = Objects.requireNonNull(rsp.body()).string();
            JSONObject json = new JSONObject(jsonStr);
            String synjonesAuth = "bearer ".concat(json.getString("access_token"));
            chain.putSimpleData("Synjones-Auth", synjonesAuth);
        } catch (Exception e) {
            return Ret.newFailedInstance(e);
        }
        return chain.proceed();
    }
}
