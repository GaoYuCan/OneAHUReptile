package s1nk.ahu.reptile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.models.CampusNetQuery;
import s1nk.ahu.reptile.models.CardBalance;
import s1nk.ahu.reptile.models.CardTransaction;
import s1nk.ahu.reptile.models.Course;
import s1nk.ahu.reptile.models.Grade;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.general.BasicStepChain;
import s1nk.ahu.reptile.steps.general.CacheStep;
import s1nk.ahu.reptile.steps.general.CampusNetQueryStep;
import s1nk.ahu.reptile.steps.general.CardBalanceStep;
import s1nk.ahu.reptile.steps.general.CardBillStep;
import s1nk.ahu.reptile.steps.general.GradeStep;
import s1nk.ahu.reptile.steps.general.ScheduleStep;
import s1nk.ahu.reptile.steps.wvpn.CardAuthorizationStep;
import s1nk.ahu.reptile.steps.wvpn.JwxtAuthorizationStep;
import s1nk.ahu.reptile.steps.wvpn.LoginStep;
import simon.lazy.Lazy;
import simon.lazy.LazyInitializer;
import simon.lazy.SynchronizedLazy;

public class ReptileClient {
    private static final List<IStep<Grade>> JWXT_GRADE_STEPS = List.of(
            new CacheStep<>(),
            new LoginStep<>(),
            new JwxtAuthorizationStep<>(),
            new GradeStep()
    );
    private static final List<IStep<List<Course>>> JWXT_SCHEDULE_STEPS = List.of(
            new CacheStep<>(),
            new LoginStep<>(),
            new JwxtAuthorizationStep<>(),
            new ScheduleStep()
    );
    private static final List<IStep<CardBalance>> CARD_BALANCE_STEPS = List.of(
            new CacheStep<>(),
            new LoginStep<>(),
            new CardAuthorizationStep<>(),
            new CardBalanceStep()
    );
    private static final List<IStep<List<CardTransaction>>> CARD_BILL_STEPS = List.of(
            new CacheStep<>(),
            new LoginStep<>(),
            new CardAuthorizationStep<>(),
            new CardBillStep()
    );
    private static final List<IStep<CampusNetQuery>> CARD_CAMPUS_NET_QUERY_STEPS = List.of(
            new CacheStep<>(),
            new LoginStep<>(),
            new CardAuthorizationStep<>(),
            new CampusNetQueryStep()
    );

    public static Ret<Grade> getGrade(String studentID, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("studentID", studentID);
        params.put("password", password);
        params.put("isOneAHU", false);
        return new BasicStepChain<>(JWXT_GRADE_STEPS, params).proceed();
    }


    public static Ret<List<Course>> getSchedule(String studentID, String password, String schoolYear, int schoolTerm) {
        Map<String, Object> params = new HashMap<>();
        params.put("studentID", studentID);
        params.put("password", password);
        params.put("schoolYear", schoolYear);
        params.put("schoolTerm", schoolTerm);
        params.put("isOneAHU", false);
        return new BasicStepChain<>(JWXT_SCHEDULE_STEPS, params).proceed();
    }

    public static Ret<CardBalance> getCardBalance(String studentID, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("studentID", studentID);
        params.put("password", password);
        params.put("isOneAHU", false);
        return new BasicStepChain<>(CARD_BALANCE_STEPS, params).proceed();
    }


    public static Ret<List<CardTransaction>> getCardBill(String studentID, String password, String startDate,
                                                         String endDate, String account) {
        Map<String, Object> params = new HashMap<>();
        params.put("studentID", studentID);
        params.put("password", password);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("account", account);
        params.put("isOneAHU", false);
        return new BasicStepChain<>(CARD_BILL_STEPS, params).proceed();
    }



    public static Ret<CampusNetQuery> getCampusNetQuery(String studentID, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("studentID", studentID);
        params.put("password", password);
        params.put("isOneAHU", false);
        return new BasicStepChain<>(CARD_CAMPUS_NET_QUERY_STEPS, params).proceed();
    }
}
