package s1nk.ahu.reptile.common.exception;

abstract class ReptileException extends Exception {
    public ReptileException() {
        super();
    }

    public ReptileException(String message) {
        super(message);
    }
}
