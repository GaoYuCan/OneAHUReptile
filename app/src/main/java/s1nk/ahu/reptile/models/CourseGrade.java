package s1nk.ahu.reptile.models;

public class CourseGrade {
    public String schoolYear;
    public int schoolTerm;
    public String courseId;
    public String courseName;
    public String courseType;
    public Double credit;
    public Double gradePoint;
    public String score; // 可能为 良好\优秀等
    public String resitScore; // 补考分数
    public String relearnScore; // 重修分数

    public CourseGrade(String schoolYear, int schoolTerm, String courseId, String courseName, String courseType, Double credit, Double gradePoint, String score, String resitScore, String relearnScore) {
        this.schoolYear = schoolYear;
        this.schoolTerm = schoolTerm;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseType = courseType;
        this.credit = credit;
        this.gradePoint = gradePoint;
        this.score = score;
        this.resitScore = resitScore;
        this.relearnScore = relearnScore;
    }

    public CourseGrade() {
    }

    @Override
    public String toString() {
        return "CourseGrade{" +
                "schoolYear='" + schoolYear + '\'' +
                ", schoolTerm=" + schoolTerm +
                ", courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseType='" + courseType + '\'' +
                ", credit=" + credit +
                ", gradePoint=" + gradePoint +
                ", score='" + score + '\'' +
                ", resitScore='" + resitScore + '\'' +
                ", relearnScore='" + relearnScore + '\'' +
                '}';
    }
}
