package simon.lazy;

/**
 * 懒加载
 *
 * @param <T>
 */
public interface Lazy<T> {

    /**
     * 如果值未初始化，则调用 {@link LazyInitializer#invoke()}
     */
    T get();

    boolean isInitialized();
}