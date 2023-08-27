package s1nk.ahu.reptile.models;

import java.util.List;

public class Grade {
    public int numberOfStudent;
    public Double GPA;
    public Double totalGP;
    public Double totalCredit;
    public Double totalCreditEarned;
    public Double totalCreditRelearned;
    public List<CourseGrade> courseGrades;

    public Grade(int numberOfStudent, Double GPA, Double totalGP, Double totalCredit, Double totalCreditEarned, Double totalCreditRelearned, List<CourseGrade> courseGrades) {
        this.numberOfStudent = numberOfStudent;
        this.GPA = GPA;
        this.totalGP = totalGP;
        this.totalCredit = totalCredit;
        this.totalCreditEarned = totalCreditEarned;
        this.totalCreditRelearned = totalCreditRelearned;
        this.courseGrades = courseGrades;
    }

    public Grade() {
    }

    @Override
    public String toString() {
        return "Grade{" +
                "numberOfStudent=" + numberOfStudent +
                ", GPA=" + GPA +
                ", totalGP=" + totalGP +
                ", totalCredit=" + totalCredit +
                ", totalCreditEarned=" + totalCreditEarned +
                ", totalCreditRelearned=" + totalCreditRelearned +
                ", courseGrades=" + courseGrades +
                '}';
    }
}
