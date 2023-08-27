package s1nk.ahu.reptile.common.exception;

import android.util.Base64;

public class UnknownException extends ReptileException {
    public UnknownException(String message, byte[] data) {
        super(message);
        // todo(异常上报)
        // byte[] bytes = Base64.encode(data, Base64.DEFAULT);
        // send Email()
    }
}
