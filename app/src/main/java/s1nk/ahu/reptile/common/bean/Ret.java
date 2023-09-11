package s1nk.ahu.reptile.common.bean;

public class Ret<T> {
    private final T data;
    private final Throwable error;

    public Ret(T data, Throwable error) {
        this.data = data;
        this.error = error;
    }

    public boolean isSuccessful() {
        return error == null;
    }

    public T getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }

    public static <T> Ret<T> newSuccessfulInstance(T data) {
        return new Ret<>(data, null);
    }

    public static <T> Ret<T> newFailedInstance(Throwable error) {
        return new Ret<>(null, error);
    }

}
