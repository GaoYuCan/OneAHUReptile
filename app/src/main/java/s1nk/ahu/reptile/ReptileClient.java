package s1nk.ahu.reptile;

import java.util.ArrayList;
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

public class ReptileClient {
    private static final List<IStep<Grade>> JWXT_GRADE_STEPS = new ArrayList<>();
    private static final List<IStep<List<Course>>> JWXT_SCHEDULE_STEPS = new ArrayList<>();
    private static final List<IStep<CardBalance>> CARD_BALANCE_STEPS = new ArrayList<>();
    private static final List<IStep<List<CardTransaction>>> CARD_BILL_STEPS = new ArrayList<>();
    private static final List<IStep<CampusNetQuery>> CARD_CAMPUS_NET_QUERY_STEPS = new ArrayList<>();

    static {
        // JWXT_GRADE_STEPS
        JWXT_GRADE_STEPS.add(new CacheStep<>());
        JWXT_GRADE_STEPS.add(new LoginStep<>());
        JWXT_GRADE_STEPS.add(new JwxtAuthorizationStep<>());
        JWXT_GRADE_STEPS.add(new GradeStep());
        // JWXT_SCHEDULE_STEPS
        JWXT_SCHEDULE_STEPS.add(new CacheStep<>());
        JWXT_SCHEDULE_STEPS.add(new LoginStep<>());
        JWXT_SCHEDULE_STEPS.add(new JwxtAuthorizationStep<>());
        JWXT_SCHEDULE_STEPS.add(new ScheduleStep());

        // CARD_BALANCE_STEPS
        CARD_BALANCE_STEPS.add(new CacheStep<>());
        CARD_BALANCE_STEPS.add(new LoginStep<>());
        CARD_BALANCE_STEPS.add(new CardAuthorizationStep<>());
        CARD_BALANCE_STEPS.add(new CardBalanceStep());
        // CARD_BILL_STEPS
        CARD_BILL_STEPS.add(new CacheStep<>());
        CARD_BILL_STEPS.add(new LoginStep<>());
        CARD_BILL_STEPS.add(new CardAuthorizationStep<>());
        CARD_BILL_STEPS.add(new CardBillStep());
        // CARD_CAMPUS_NET_QUERY_STEPS
        CARD_CAMPUS_NET_QUERY_STEPS.add(new CacheStep<>());
        CARD_CAMPUS_NET_QUERY_STEPS.add(new LoginStep<>());
        CARD_CAMPUS_NET_QUERY_STEPS.add(new CardAuthorizationStep<>());
        CARD_CAMPUS_NET_QUERY_STEPS.add(new CampusNetQueryStep());
    }

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
