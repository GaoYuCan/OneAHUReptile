package s1nk.ahu.reptile.steps.general;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.common.exception.TeachingEvaluationException;
import s1nk.ahu.reptile.common.exception.TimeNotAllowException;
import s1nk.ahu.reptile.common.exception.UnknownException;
import s1nk.ahu.reptile.common.utils.JwxtUtils;
import s1nk.ahu.reptile.models.CourseGrade;
import s1nk.ahu.reptile.models.Grade;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.IStepChain;

public class GradeStep implements IStep<Grade> {
    @Override
    public Ret<Grade> handle(IStepChain<Grade> chain) {
        // 获取参数
        String studentID = chain.getSimpleData("studentID", String.class);
        String cookies = chain.getSimpleData("cookies", String.class);
        // 用于判断是 one.ahu 还是 wvpn.ahu 来的
        boolean isOneAHU = chain.getSimpleData("isOneAHU", Boolean.class);
        String domain;
        if (isOneAHU) {
            domain = "https://jwxt8.ahu.edu.cn";
        } else {
            domain = "https://wvpn.ahu.edu.cn/https/77726476706e69737468656265737421fae05988777e69586b468ca88d1b203b";
        }
        OkHttpClient client = BasicStepChain.httpClient;
        // 构造请求
        String getURL = domain + String.format("/xscj_gc2.aspx?xh=%s&gnmkdm=N121605", studentID);
        Request request = new Request.Builder()
                .url(getURL)
                .addHeader("Cookie", cookies)
                .header("Referer", domain)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            String html = Objects.requireNonNull(response.body()).string();
            // 检测教学评价
            if (JwxtUtils.checkTeachingEvaluation(html)) {
                return Ret.newFailedInstance(new TeachingEvaluationException());
            }
            Document document = Jsoup.parse(html, getURL); // 需要处理相对地址，要填上 baseUri
            // 构造请求
            String __VIEWSTATEGENERATOR = document.select("input[name='__VIEWSTATEGENERATOR']").attr("value");
            String __VIEWSTATE = document.select("input[name='__VIEWSTATE']").attr("value");
            FormBody gradeFormBody = new FormBody.Builder()
                    .add("__VIEWSTATEGENERATOR", __VIEWSTATEGENERATOR)
                    .add("__VIEWSTATE", __VIEWSTATE)
                    .add("ddlXN", "")
                    .add("ddlXQ", "")
                    .add("Button2", "在校学习成绩查询")
                    .build();
            // 获取请求地址 [wvpn下绝对地址，one下是相对地址]
            String postURL = document.select("#Form1").attr("action");
            request = new Request.Builder()
                    .url(postURL)
                    .addHeader("Cookie", cookies)
                    .addHeader("Referer", domain)
                    .post(gradeFormBody)
                    .build();
            Response rsp = client.newCall(request).execute();
            document = Jsoup.parse(Objects.requireNonNull(rsp.body()).byteStream(),
                    StandardCharsets.UTF_8.name(), "");
            rsp.close(); // 关闭
            // 解析成绩
            Elements courseGradeNodes = document.select("#Datagrid1 tr:not(:first-child)");
            if (courseGradeNodes.size() == 0) {
                int hours = Calendar.getInstance(Locale.CHINA).get(Calendar.HOUR_OF_DAY);
                if (hours <= 5) {
                    return Ret.newFailedInstance(new TimeNotAllowException("当前时间教务系统无法查询成绩。"));
                } else {
                    return Ret.newFailedInstance(new UnknownException("获取成绩失败,当前系统时间可能存在问题!", new byte[]{}));
                }
            }
            // 获取数字版
            int numberOfStudent = Integer.parseInt(document.select("#zyzrs").text()
                    .replaceAll("[^0-9]", ""));
            double GPA = Double.parseDouble(document.select("#pjxfjd").text()
                    .replaceAll("[^0-9.]", ""));
            double totalGP = Double.parseDouble(document.select("#xfjdzh").text()
                    .replaceAll("[^0-9.]", ""));
            String[] creditTextArray = document.select("#xftj").text().split("；"); // 所选学分127.50；获得学分127.50；重修学分0
            double totalCredit = Double.parseDouble(creditTextArray[0].replaceAll("[^0-9.]", ""));
            double totalCreditEarned = Double.parseDouble(creditTextArray[0].replaceAll("[^0-9.]", ""));
            double totalCreditRelearned = Double.parseDouble(creditTextArray[0].replaceAll("[^0-9.]", ""));
            ArrayList<CourseGrade> courseGrades = new ArrayList<>();
            for (Element courseGradeNode : courseGradeNodes) {
                Elements tds = courseGradeNode.select("td");
                String schoolYear = tds.get(0).text().trim();
                int schoolTerm = Integer.parseInt(tds.get(1).text().trim());
                String courseId = tds.get(2).text().trim();
                String courseName = tds.get(3).text().trim();
                String courseType = tds.get(4).text().trim();
                double credit = Double.parseDouble(tds.get(6).text().trim());
                double gradePoint = Double.parseDouble(tds.get(7).text().trim());
                String score = tds.get(8).text().trim();
                String resitScore = tds.get(10).text().trim();
                String relearnScore = tds.get(11).text().trim();
                courseGrades.add(new CourseGrade(schoolYear, schoolTerm, courseId, courseName, courseType, credit, gradePoint, score, resitScore, relearnScore));
            }
            Grade grade = new Grade(numberOfStudent, GPA, totalGP, totalCredit, totalCreditEarned, totalCreditRelearned, courseGrades);
            return Ret.newSuccessfulInstance(grade);
        } catch (IOException e) {
            return Ret.newFailedInstance(e);
        }
    }
}
