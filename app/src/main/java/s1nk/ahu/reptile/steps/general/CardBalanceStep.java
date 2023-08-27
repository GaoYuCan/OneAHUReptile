package s1nk.ahu.reptile.steps.general;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.common.exception.QueryCardException;
import s1nk.ahu.reptile.models.CardBalance;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.IStepChain;

public class CardBalanceStep implements IStep<CardBalance> {
        @Override
    public Ret<CardBalance> handle(IStepChain<CardBalance> chain) {
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
        //  获取余额
        Request request = new Request.Builder()
                .url(domain + "/berserker-app/ykt/tsm/queryCard?vpn-12-o2-ycard.ahu.edu.cn&synAccessSource=h5")
                .addHeader("Cookie", cookies)
                .addHeader("Referer", domain)
                .addHeader("Synjones-Auth", synjonesAuth)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            // 解析 JSON
            String jsonStr = Objects.requireNonNull(response.body()).string();
            JSONObject respJSON = new JSONObject(jsonStr);
            if (respJSON.getBoolean("success")) {
                JSONObject card = respJSON.getJSONObject("data")
                        .getJSONArray("card")
                        .getJSONObject(0);
                String account = card.getString("account");
                int balance = Integer.parseInt(card.getString("db_balance"));
                int unsettleBalance = Integer.parseInt(card.getString("unsettle_amount"));
                boolean lostFlag = Integer.parseInt(card.getString("lostflag")) != 0;
                boolean freezeFlag = Integer.parseInt(card.getString("freezeflag")) != 0;
                JSONArray accountsJSON = card.getJSONArray("accinfo");
                List<CardBalance.CardAccount> cardAccounts = new ArrayList<>();
                for (int i = 0; i < accountsJSON.length(); i++) {
                    JSONObject accountJSON = accountsJSON.getJSONObject(i);
                    CardBalance.CardAccount cardAccount = new CardBalance.CardAccount();
                    cardAccount.balance = accountJSON.getInt("balance");
                    cardAccount.name = accountJSON.getString("name");
                    cardAccount.type = accountJSON.getString("type");
                    cardAccounts.add(cardAccount);
                }
                return Ret.newSuccessfulInstance(new CardBalance(account, balance,
                        unsettleBalance, lostFlag, freezeFlag, cardAccounts));
            } else {
                return Ret.newFailedInstance(new QueryCardException(respJSON.getString("msg")));
            }
        } catch (Exception e) {
            return Ret.newFailedInstance(e);
        }
    }
}
