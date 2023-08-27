package s1nk.ahu.reptile.steps.general;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.common.exception.QueryCardException;
import s1nk.ahu.reptile.models.CampusNetQuery;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.IStepChain;

public class CampusNetQueryStep implements IStep<CampusNetQuery> {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    public Ret<CampusNetQuery> handle(IStepChain<CampusNetQuery> chain) {
        // 获取 cookie
        String cookies = chain.getSimpleData("cookies", String.class);
        String synjonesAuth = chain.getSimpleData("Synjones-Auth", String.class);
        // 用于判断是 one.ahu 还是 wvpn.ahu 来的
        boolean isOneAHU = chain.getSimpleData("isOneAHU", Boolean.class);
        String domain;
        if (isOneAHU) {
            domain = "https://ycard.ahu.edu.cn";
        } else {
            domain = "https://wvpn.ahu.edu.cn/https/77726476706e69737468656265737421e9f4408e237e69586b468ca88d1b203b";
        }
        OkHttpClient client = BasicStepChain.httpClient;

        // 获取校园网账号信息
        FormBody queryForm = new FormBody.Builder()
                .add("feeitemid", "1")
                .add("type", "IEC")
                .add("level", "0")
                .build();
        Request request = new Request.Builder()
                .url(domain + "/charge/feeitem/getThirdData?vpn-12-o2-ycard.ahu.edu.cn")
                .addHeader("Cookie", cookies)
                .header("Referer", domain + "/Page/Page")
                .header("Synjones-Auth", synjonesAuth)
                .post(queryForm)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String jsonStr = Objects.requireNonNull(response.body()).string();
            JSONObject respJSON = new JSONObject(jsonStr);
            if (respJSON.getInt("code") == 200) {
                JSONObject data = respJSON.getJSONObject("map")
                        .getJSONObject("data");
                CampusNetQuery campusNetQuery = new CampusNetQuery();
                campusNetQuery.balance = Integer.parseInt(data.getString("balance"));
                campusNetQuery.usedAmount = Integer.parseInt(data.getString("use_money"));
                campusNetQuery.usedTime = Integer.parseInt(data.getString("use_time"));
                campusNetQuery.usedFlow = Integer.parseInt(data.getString("use_flow"));
                campusNetQuery.statusStartTime = data.getString("state_time");
                campusNetQuery.userStatus = respJSON.getJSONObject("map")
                        .getJSONObject("showData")
                        .getString("用户状态");
                data.put("myCustomInfo", "undefined：undefined");
                campusNetQuery.rawStr = data.toString(); //
                return Ret.newSuccessfulInstance(campusNetQuery);
            }
            return Ret.newFailedInstance(new QueryCardException(respJSON.getString("msg")));
        } catch (Exception e) {
            return Ret.newFailedInstance(e);
        }
    }
}
