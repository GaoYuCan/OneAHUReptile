package s1nk.ahu.reptile.models;

public class Course {
    public String selectionId;
    public String courseId;
    public String courseName;
    public String courseType;
    public String teacher;
    public int startWeek;
    public int endWeek;
    public int dayOfWeek;
    public int startTime;
    public int length;
    public String position;
    public int parity; // 单双周

    public Course(String selectionId, String courseId, String courseName, String courseType, String teacher, int startWeek, int endWeek, int dayOfWeek, int startTime, int length, String position, int parity) {
        this.selectionId = selectionId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseType = courseType;
        this.teacher = teacher;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.length = length;
        this.position = position;
        this.parity = parity;
    }


    public static final int COURSE_PARITY_WEEK_ODD = 1;
    public static final int COURSE_PARITY_WEEK_EVEN = 2;
    public static final int COURSE_PARITY_WEEK_NORMAL = 3;

    @Override
    public String toString() {
        return "Course{" +
                "selectionId='" + selectionId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseType='" + courseType + '\'' +
                ", teacher='" + teacher + '\'' +
                ", startWeek=" + startWeek +
                ", endWeek=" + endWeek +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", length=" + length +
                ", position='" + position + '\'' +
                ", parity=" + parity +
                '}';
    }
}
