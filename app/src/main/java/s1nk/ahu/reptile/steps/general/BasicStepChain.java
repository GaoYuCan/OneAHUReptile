package s1nk.ahu.reptile.steps.general;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.IStepChain;

public class BasicStepChain<T> implements IStepChain<T> {
    // 用于网络请求
    public static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .followRedirects(true)
            .callTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

    private final List<IStep<T>> steps;
    private final Map<String, Object> simpleDataMap;
    private int index;

    public BasicStepChain(List<IStep<T>> steps, Map<String, Object> simpleDataMap) {
        this.steps = steps;
        this.simpleDataMap = simpleDataMap;
        this.index = 0;
    }


    @Override
    public Ret<T> proceed() {
        if (this.index >= this.steps.size()) {
            throw new IllegalStateException();
        }
        IStep<T> step = this.steps.get(this.index);
        BasicStepChain<T> newOne = new BasicStepChain<>(this.steps, this.simpleDataMap);
        newOne.index = this.index + 1;
        return step.handle(newOne);
    }

    @Override
    public <R> R getSimpleData(@NonNull String key, @NonNull Class<R> targetClass) {
        Object o = simpleDataMap.get(key);
        if (o != null && targetClass.isInstance(o)) {
            return targetClass.cast(o);
        }
        return null;
    }

    @Override
    public void putSimpleData(@NonNull String key, @NonNull Object val) {
        simpleDataMap.put(key, val);
    }

    @Override
    public void removeSimpleData(@NonNull String key) {
        simpleDataMap.remove(key);
    }

}
