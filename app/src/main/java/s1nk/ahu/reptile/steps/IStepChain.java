package s1nk.ahu.reptile.steps;

import s1nk.ahu.reptile.common.bean.Ret;

public interface IStepChain<T> {
    Ret<T> proceed();

    <R> R getSimpleData(String key, Class<R> targetClass);

    void putSimpleData(String key, Object val);

    void removeSimpleData(String key);
}