package s1nk.ahu.reptile.steps.general;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.common.exception.IllegalAmountException;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.IStepChain;

public class CampusNetPayStep implements IStep<Boolean> {
    @Override
    public Ret<Boolean> handle(IStepChain<Boolean> chain) {
        // 获取 cookie
        String cookies = chain.getSimpleData("cookies", String.class);
        String synjonesAuth = chain.getSimpleData("Synjones-Auth", String.class);
        String rawStr = chain.getSimpleData("rawStr", String.class);
        int tranamt = chain.getSimpleData("tranamt", Integer.class);
        String payPassword = chain.getSimpleData("payPassword", String.class);
        // 网费，新平台只能充值这些固定的数字
        if (tranamt != 5 && tranamt != 10 && tranamt != 20 && tranamt != 30 && tranamt != 40
                && tranamt != 50) {
            return Ret.newFailedInstance(new IllegalAmountException("充值数值非法，可能造成财产损失，请调整充值额度后重试！"));
        }
        // 用于判断是 one.ahu 还是 wvpn.ahu 来的
        boolean isOneAHU = chain.getSimpleData("isOneAHU", Boolean.class);
        String domain;
        if (isOneAHU) {
            domain = "https://ycard.ahu.edu.cn";
        } else {
            domain = "https://wvpn.ahu.edu.cn/https/77726476706e69737468656265737421e9f4408e237e69586b468ca88d1b203b";
        }
        OkHttpClient client = BasicStepChain.httpClient;
        // 创建订单
        FormBody createOrderForm = new FormBody.Builder()
                .add("feeitemid", "1")
                .add("tranamt", "1")
                .add("flag", "choose")
                .add("source", "app")
                .add("paystep", "0")
                .add("abstracts", "")
                .add("third_party", rawStr)
                .build();

        return null;
    }
}
