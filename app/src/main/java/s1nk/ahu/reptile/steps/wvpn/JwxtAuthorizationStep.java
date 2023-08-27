package s1nk.ahu.reptile.steps.wvpn;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.common.exception.LoginExpiredException;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.IStepChain;
import s1nk.ahu.reptile.steps.general.BasicStepChain;

public class JwxtAuthorizationStep<T> implements IStep<T> {
    @Override
    public Ret<T> handle(IStepChain<T> chain) {
        // 获取 cookies
        String cookies = chain.getSimpleData("cookies", String.class);
        // cookies 不可能为空
        Request request = new Request.Builder()
                .url("https://wvpn.ahu.edu.cn/https/77726476706e69737468656265737421fae05988777e69586b468ca88d1b203b/login_cas.aspx")
                .addHeader("Cookie", cookies)
                .get()
                .build();
        OkHttpClient client = BasicStepChain.httpClient;
        try (Response response = client.newCall(request).execute()) {
            // priorResponse == null, 在正常情况下是不可能存在的
            String from = Objects.requireNonNull(response.priorResponse())
                    .request().url().toString();
            if (!from.contains("ticket=")) {
                // 过期了
                return Ret.newFailedInstance(new LoginExpiredException("Cookie 过期，教务系统认证失败！"));
            }
        } catch (IOException e) {
            return Ret.newFailedInstance(e);
        }
        return chain.proceed();
    }
}
