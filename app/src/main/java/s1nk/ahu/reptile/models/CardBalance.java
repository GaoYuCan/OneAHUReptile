package s1nk.ahu.reptile.models;

import android.accounts.Account;

import androidx.annotation.NonNull;

import java.util.List;

public class CardBalance {
    public String account;
    public int balance;
    public int unsettleBalance;
    public boolean freezeFlag;
    public boolean lostFlag;
    public List<CardAccount> accounts;

    public CardBalance(String account, int balance, int unsettleBalance, boolean freezeFlag, boolean lostFlag, List<CardAccount> accounts) {
        this.account = account;
        this.balance = balance;
        this.unsettleBalance = unsettleBalance;
        this.freezeFlag = freezeFlag;
        this.lostFlag = lostFlag;
        this.accounts = accounts;
    }

    public CardBalance() {
    }


    @NonNull
    @Override
    public String toString() {
        return "CardBalance{" +
                "account='" + account + '\'' +
                ", balance=" + balance +
                ", unsettleBalance=" + unsettleBalance +
                ", freezeFlag=" + freezeFlag +
                ", lostFlag=" + lostFlag +
                ", accounts=" + accounts +
                '}';
    }

    public static class CardAccount {
        public String name;
        public String type;
        public int balance;

        public CardAccount(String name, String type, int balance) {
            this.name = name;
            this.type = type;
            this.balance = balance;
        }

        public CardAccount() {
        }

        @NonNull
        @Override
        public String toString() {
            return "CardAccount{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", balance=" + balance +
                    '}';
        }
    }
}
