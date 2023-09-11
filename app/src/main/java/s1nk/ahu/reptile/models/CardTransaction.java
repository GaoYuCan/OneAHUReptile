package s1nk.ahu.reptile.models;

import androidx.annotation.NonNull;

import java.util.Date;

public class CardTransaction {
    public Date occurTime;
    public String address;
    public int amount;
    public int posCode;
    public String type;
    public int cardBalance;

    public CardTransaction(Date occurTime, String address, int amount, int posCode, String type, int cardBalance) {
        this.occurTime = occurTime;
        this.address = address;
        this.amount = amount;
        this.posCode = posCode;
        this.type = type;
        this.cardBalance = cardBalance;
    }

    public CardTransaction() {
    }

    @NonNull
    @Override
    public String toString() {
        return "CardTransaction{" +
                "occurTime=" + occurTime +
                ", address='" + address + '\'' +
                ", amount=" + amount +
                ", posCode=" + posCode +
                ", type='" + type + '\'' +
                ", cardBalance=" + cardBalance +
                '}';
    }
}
