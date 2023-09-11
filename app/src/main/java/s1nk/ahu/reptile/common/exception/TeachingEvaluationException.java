package s1nk.ahu.reptile.common.exception;

public final class TeachingEvaluationException extends ReptileException {
    public TeachingEvaluationException() {
        super("您尚未完成教学评价，请先登录教务系统完成教学评价！");
    }
}
