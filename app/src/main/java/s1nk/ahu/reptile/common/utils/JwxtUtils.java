package s1nk.ahu.reptile.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JwxtUtils {
    private static final Pattern RE_TEACHING_EVALUATION = Pattern.compile("alert\\((.+?)\\)");
    public static boolean checkTeachingEvaluation(String html) {
        Matcher matcher = RE_TEACHING_EVALUATION.matcher(html);
        while (matcher.find()) {
            String alertMessage = matcher.group(1);
            if (alertMessage != null && alertMessage.contains("教学评价")) {
                return true;
            }
        }
        return false;
    }
}
