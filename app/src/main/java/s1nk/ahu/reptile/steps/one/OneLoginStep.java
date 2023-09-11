package s1nk.ahu.reptile.steps.one;


import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import s1nk.ahu.reptile.common.utils.TriDES;
import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.common.exception.LoginException;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.IStepChain;
import s1nk.ahu.reptile.steps.general.BasicStepChain;

public class OneLoginStep<T> implements IStep<T> {
    @Override
    public Ret<T> handle(IStepChain<T> chain) {
        // 判断是否已有 cookies
        String cookies = chain.getSimpleData("cookies", String.class);
        if (cookies != null) { // 已有 cookies 跳过登录
            Log.e("reptile", "已有 Cookie 跳过登录");
            return chain.proceed();
        }
        // 登录
        String studentID = chain.getSimpleData("studentID", String.class);
        String password = chain.getSimpleData("password", String.class);
        final OkHttpClient client = BasicStepChain.httpClient;
        // 访问主页获取参数
        Request request = new Request.Builder()
                .url("https://one.ahu.edu.cn")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            cookies = String.join(";", response.headers("Set-Cookie"));
            // baseUri 用来解析相对链接，不需要，也不能获取 loginURL
            Document document = Jsoup.parse(Objects.requireNonNull(response.body()).byteStream(),
                    StandardCharsets.UTF_8.name(), "");
            String loginURL = document.select("#loginForm").attr("action");
            String lt = document.select("#lt").attr("value");
            String execution = document.select("input[name='execution']").attr("value");
            String payload = TriDES.encryptAHUPayload(studentID + password + lt);
            FormBody loginFormBody = new FormBody.Builder()
                    .add("rsa", payload)
                    .add("ul", String.valueOf(studentID.length()))
                    .add("pl", String.valueOf(password.length()))
                    .add("lt", lt)
                    .add("execution", execution)
                    .add("_eventId", "submit")
                    .build();
            request = new Request.Builder()
                    .url("https://one.ahu.edu.cn" + loginURL)
                    .addHeader("Cookie", cookies)
                    .post(loginFormBody)
                    .build();
            Response rsp = client.newCall(request).execute();
            // 判断是否经过重定向，以此断定是否登录成功
            if (rsp.priorResponse() == null) {
                // 没有重定向，登录失败！
                document = Jsoup.parse(Objects.requireNonNull(rsp.body()).byteStream(),
                        StandardCharsets.UTF_8.name(), "");
                String errorMessage = document.select("#errormsghide").text();
                rsp.close();
                return Ret.newFailedInstance(new LoginException(errorMessage));
            }
            rsp.close();
            // 登录成功
            chain.putSimpleData("cookies", cookies);
            Log.e("reptile", "登录成功");
        } catch (IOException e) {
            return Ret.newFailedInstance(e);
        }
        return chain.proceed();
    }
}
