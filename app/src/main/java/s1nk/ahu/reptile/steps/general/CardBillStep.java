package s1nk.ahu.reptile.steps.general;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.common.exception.QueryCardException;
import s1nk.ahu.reptile.models.CardTransaction;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.IStepChain;

public class CardBillStep implements IStep<List<CardTransaction>> {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    public Ret<List<CardTransaction>> handle(IStepChain<List<CardTransaction>> chain) {
        // 获取参数
        String cookies = chain.getSimpleData("cookies", String.class);
        String synjonesAuth = chain.getSimpleData("Synjones-Auth", String.class);
        String startDate = chain.getSimpleData("startDate", String.class);
        String endDate = chain.getSimpleData("endDate", String.class);
        String account = chain.getSimpleData("account", String.class);
        // 用于判断是 one.ahu 还是 wvpn.ahu 来的
        boolean isOneAHU = chain.getSimpleData("isOneAHU", Boolean.class);
        String domain;
        if (isOneAHU) {
            domain = "https://ycard.ahu.edu.cn";
        } else {
            domain = "https://wvpn.ahu.edu.cn/https/77726476706e69737468656265737421e9f4408e237e69586b468ca88d1b203b";
        }
        OkHttpClient client = BasicStepChain.httpClient;
        // 获取流水
        Request request = new Request.Builder()
                .url(domain + String.format("/berserker-search/search/personal/turnover?vpn-12-o2-ycard.ahu.edu.cn&size=10000&current=1&timeFrom=%s&timeTo=%s&synAccessSource=pc", startDate, endDate))
                .addHeader("Cookie", cookies)
                .header("Referer", domain + "/Page/Page")
                .header("Synjones-Auth", synjonesAuth)
                .build();
        try (Response response = client.newCall(request).execute()) {
            // 解析 JSON
            String jsonStr = Objects.requireNonNull(response.body()).string();
            JSONObject respJSON = new JSONObject(jsonStr);
            if (respJSON.getBoolean("success")) {
                JSONArray rows = respJSON.getJSONObject("data")
                        .getJSONArray("records");
                List<CardTransaction> cardTransactions = new ArrayList<>();
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject row = rows.getJSONObject(i);
                    Date occurTime = SIMPLE_DATE_FORMAT.parse(row.getString("effectdateStr"));
                    String address = row.getString("resume").trim();
                    int amount = row.getInt("tranamt");
                    // 1 代表转入， 其他代表转出
                    // ("1" === e.row.typeFrom ? "+" : "-") + (e.row.tranamt / 100).toFixed(2)
                    if (!"1".equals(row.getString("typeFrom"))) {
                        amount *= -1;
                    }
                    int posCode = row.getInt("posCode");
                    String type = row.getString("payName").trim();
                    int cardBalance = row.getInt("cardBalance");
                    cardTransactions.add(new CardTransaction(occurTime, address, amount, posCode, type, cardBalance));
                }
                return Ret.newSuccessfulInstance(cardTransactions);
            } else {
                return Ret.newFailedInstance(new QueryCardException(respJSON.getString("msg")));
            }

        } catch (Exception e) {
            return Ret.newFailedInstance(e);
        }
    }
}
