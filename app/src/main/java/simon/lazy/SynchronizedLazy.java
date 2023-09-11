package simon.lazy;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 线程安全的懒加载
 *
 * @param <T>
 */
public class SynchronizedLazy<T> implements Lazy<T> {

    private volatile Object value = LazyInternal.UNINITIALIZED_VALUE;
    private final LazyInitializer<T> initializer;
    private final Object lock;

    public SynchronizedLazy(LazyInitializer<T> initializer) {
        this(initializer, null);
    }

    /**
     * 指定线程同步锁
     *
     * @param lock 锁对象
     */
    public SynchronizedLazy(LazyInitializer<T> initializer, @Nullable Object lock) {
        this.initializer = initializer;
        this.lock = lock == null ? this : lock;
    }

    @Override
    public T get() {
        if (value != LazyInternal.UNINITIALIZED_VALUE) {
            //noinspection unchecked
            return (T) value;
        }

        synchronized (lock) {
            if (value != LazyInternal.UNINITIALIZED_VALUE) {
                //noinspection unchecked
                return (T) value;
            }

            value = initializer.invoke();
            //noinspection unchecked
            return (T) value;
        }
    }

    @Override
    public final boolean isInitialized() {
        return value != LazyInternal.UNINITIALIZED_VALUE;
    }

    @NonNull
    @Override
    public String toString() {
        return "SynchronizedLazy{" +
                "value=" + (value == LazyInternal.UNINITIALIZED_VALUE ? "(uninitialized)" : value.toString()) +
                '}';
    }
}
