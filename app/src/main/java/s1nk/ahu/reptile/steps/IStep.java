package s1nk.ahu.reptile.steps;

import s1nk.ahu.reptile.common.bean.Ret;

public interface IStep<T> {
    Ret<T> handle(IStepChain<T> chain);

}
