package simon.lazy;


import androidx.annotation.NonNull;

/**
 * 非线程安全懒加载
 *
 * @param <T>
 */
public class UnsafeLazy<T> implements Lazy<T> {

    private volatile Object value = LazyInternal.UNINITIALIZED_VALUE;
    private final LazyInitializer<T> initializer;

    public UnsafeLazy(LazyInitializer<T> initializer) {
        this.initializer = initializer;
    }

    @Override
    public final T get() {
        if (value == LazyInternal.UNINITIALIZED_VALUE) {
            value = initializer.invoke();
        }
        //noinspection unchecked
        return (T) value;
    }

    @Override
    public final boolean isInitialized() {
        return value != LazyInternal.UNINITIALIZED_VALUE;
    }

    @NonNull
    @Override
    public String toString() {
        return "UnsafeLazy{" +
                "value=" + (value == LazyInternal.UNINITIALIZED_VALUE ? "(uninitialized)" : value.toString()) +
                '}';
    }
}
