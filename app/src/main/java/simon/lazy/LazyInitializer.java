package simon.lazy;

/**
 * 懒加载初始化器
 *
 * @param <T>
 */
public interface LazyInitializer<T> {

    T invoke();
}
