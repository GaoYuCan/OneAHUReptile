package s1nk.ahu.reptile.models;

public class CampusNetQuery {
    public String userStatus;
    public int balance;
    public int usedAmount;
    public int usedTime;
    public int usedFlow;
    public String statusStartTime;
    public String rawStr; // 用于充值创建订单

    public CampusNetQuery(String userStatus, int balance, int usedAmount, int usedTime, int usedFlow, String statusStartTime) {
        this.userStatus = userStatus;
        this.balance = balance;
        this.usedAmount = usedAmount;
        this.usedTime = usedTime;
        this.usedFlow = usedFlow;
        this.statusStartTime = statusStartTime;
    }

    public CampusNetQuery() {
    }

    @Override
    public String toString() {
        return "CampusNetQuery{" +
                "userStatus='" + userStatus + '\'' +
                ", balance=" + balance +
                ", usedAmount=" + usedAmount +
                ", usedTime=" + usedTime +
                ", usedFlow=" + usedFlow +
                ", statusStartTime=" + statusStartTime +
                ", rawStr='" + rawStr + '\'' +
                '}';
    }
}
