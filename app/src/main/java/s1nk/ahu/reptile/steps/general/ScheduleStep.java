package s1nk.ahu.reptile.steps.general;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import s1nk.ahu.reptile.common.bean.Ret;
import s1nk.ahu.reptile.common.exception.TeachingEvaluationException;
import s1nk.ahu.reptile.common.exception.UnknownException;
import s1nk.ahu.reptile.common.utils.JwxtUtils;
import s1nk.ahu.reptile.models.Course;
import s1nk.ahu.reptile.steps.IStep;
import s1nk.ahu.reptile.steps.IStepChain;

public class ScheduleStep implements IStep<List<Course>> {
    static final Pattern RE_COURSE_TIME = Pattern.compile( "(.{2})第([0-9,]+)节\\{第([0-9]+)-([0-9]+)周(\\|([单|双])周)?\\}");
    static final Map<String, Integer> MAP_WEEK_2_NUM = new HashMap<>();

    static {
        MAP_WEEK_2_NUM.put("周一", 1);
        MAP_WEEK_2_NUM.put("周二", 2);
        MAP_WEEK_2_NUM.put("周三", 3);
        MAP_WEEK_2_NUM.put("周四", 4);
        MAP_WEEK_2_NUM.put("周五", 5);
        MAP_WEEK_2_NUM.put("周六", 6);
        MAP_WEEK_2_NUM.put("周日", 7);
    }

    @Override
    public Ret<List<Course>> handle(IStepChain<List<Course>> chain) {
        // 获取参数
        String studentID = chain.getSimpleData("studentID", String.class);
        String cookies = chain.getSimpleData("cookies", String.class);
        String schoolYear = chain.getSimpleData("schoolYear", String.class);
        int schoolTerm = chain.getSimpleData("schoolTerm", Integer.class);
        // 用于判断是 one.ahu 还是 wvpn.ahu 来的
        boolean isOneAHU = chain.getSimpleData("isOneAHU", Boolean.class);
        String domain;
        if (isOneAHU) {
            domain = "https://jwxt0.ahu.edu.cn";
        } else {
            domain = "https://wvpn.ahu.edu.cn/https/77726476706e69737468656265737421fae05988777e69586b468ca88d1b203b";
        }
        // 请求
        String getURL = domain + String.format("/xsxkqk.aspx?xh=%s&gnmkdm=N121615", studentID);
        OkHttpClient client = BasicStepChain.httpClient;
        Request request = new Request.Builder()
                .url(getURL)
                .addHeader("Cookie", cookies)
                .addHeader("Referer", domain)
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
            String __VIEWSTATEGENERATOR = document.select("#__VIEWSTATEGENERATOR").attr("value");
            String __VIEWSTATE = document.select("#__VIEWSTATE").attr("value");
            String __EVENTTARGET = document.select("#__EVENTTARGET").attr("value");
            FormBody scheduleFormBody = new FormBody.Builder()
                    .add("__EVENTTARGET", __EVENTTARGET)
                    .add("__EVENTARGUMENT", "")
                    .add("__LASTFOCUS", "")
                    .add("__VIEWSTATE", __VIEWSTATE)
                    .add("__VIEWSTATEGENERATOR", __VIEWSTATEGENERATOR)
                    .add("ddlXN", schoolYear)
                    .add("ddlXQ", String.valueOf(schoolTerm))
                    .build();
            // 获取请求地址 [wvpn下绝对地址，one下是相对地址]
            String postURL = document.select("#Form1").attr("action");
            request = new Request.Builder()
                    .url(postURL)
                    .addHeader("Cookie", cookies)
                    .addHeader("Referer", domain)
                    .post(scheduleFormBody)
                    .build();
            Response rsp = client.newCall(request).execute();
            document = Jsoup.parse(Objects.requireNonNull(rsp.body()).byteStream(),
                    StandardCharsets.UTF_8.name(), "");
            rsp.close(); // 关闭
            Elements courseNodes = document.select("#DBGrid tr:not(:first-child)");
            HashSet<Course> courseSet = new HashSet<>();
            for (Element courseNode : courseNodes) {
                Elements tds = courseNode.select("td");
                String selectionId = tds.get(0).text().trim();
                String courseId = tds.get(1).text().trim();
                String courseName = tds.get(2).text().trim();
                String courseType = tds.get(3).text().trim();
                String teacher = tds.get(5).text().trim();
                String[] courseTimeTextArray = tds.get(8).text().trim().split(";");
                String[] coursePositionArray = tds.get(9).text().trim().split(";");
                // 处理上课时间
                for (int i = 0; i < courseTimeTextArray.length; i++) {
                    String courseTimeText = courseTimeTextArray[i];
                    if (courseTimeText.isEmpty()) {
                        continue; // 这吊课不用上，时间都没有
                    }
                    Matcher matcher = RE_COURSE_TIME.matcher(courseTimeText);
                    if (!matcher.matches()) {
                        return Ret.newFailedInstance(new UnknownException("异常的课程时间格式，请联系作者处理！",
                                courseTimeText.getBytes(StandardCharsets.UTF_8)));
                    }
                    int dayOfWeek = Objects.requireNonNull(MAP_WEEK_2_NUM.get(Objects.requireNonNull(matcher.group(1))));
                    String[] timeList = Objects.requireNonNull(matcher.group(2)).split(",");
                    int startTime = Integer.parseInt(timeList[0]);
                    int length = timeList.length;
                    int startWeek = Integer.parseInt(Objects.requireNonNull(matcher.group(3)));
                    int endWeek = Integer.parseInt(Objects.requireNonNull(matcher.group(4)));
                    // 判断单双周
                    int parity = Course.COURSE_PARITY_WEEK_NORMAL;
                    String parityRaw = matcher.group(5);
                    if (parityRaw != null) {
                        parity = parityRaw.contains("单") ? Course.COURSE_PARITY_WEEK_ODD : Course.COURSE_PARITY_WEEK_EVEN;
                    }
                    String position = coursePositionArray[i];
                    // 添加
                    courseSet.add(new Course(selectionId, courseId, courseName, courseType, teacher,
                            startWeek, endWeek, dayOfWeek, startTime, length, position, parity));
                }
            }
            // 三节课会被分成2次课，合并处理
            List<Course> courseList = new ArrayList<Course>();
            List<Course> mistakeCourseList = new ArrayList<Course>();
            // 1. 把一节的筛选出来
            courseSet.forEach(course -> {
                if (course.length == 1) {
                    mistakeCourseList.add(course);
                } else {
                    courseList.add(course);
                }
            });
            // 2. 判断一节的是不是属于另一个两节的一部分
            for (Course mistakeCourse : mistakeCourseList) {
                Optional<Course> needFixCourse = courseList.stream().filter(c ->
                                // 判断是否 selectionId 一样（是不是同一门课）
                                c.selectionId.equals(mistakeCourse.selectionId)
                                        // 判断是否时间上连续
                                        && c.startTime + c.length == mistakeCourse.startTime)
                        .findFirst();
                if (needFixCourse.isPresent()) {
                    Course course = needFixCourse.get();
                    course.length += 1;
                } else {
                    // 找不到对应的课程，说不定它真一节
                    courseList.add(mistakeCourse);
                }
            }
            return Ret.newSuccessfulInstance(courseList);
        } catch (IOException e) {
            return Ret.newFailedInstance(e);
        }
    }
}
