package com.lzl.experiment13.template;

public class BankAccount {
    private final String accountNo;
    private final String userName;
    private final double balance;
    private final double annualRate;
    private final int months;
    private final String accountType;

    public BankAccount(String accountNo, String userName, double balance, double annualRate, int months, String accountType) {
        this.accountNo = accountNo;
        this.userName = userName;
        this.balance = balance;
        this.annualRate = annualRate;
        this.months = months;
        this.accountType = accountType;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getUserName() {
        return userName;
    }

    public double getBalance() {
        return balance;
    }

    public double getAnnualRate() {
        return annualRate;
    }

    public int getMonths() {
        return months;
    }

    public String getAccountType() {
        return accountType;
    }

    @Override
    public String toString() {
        return "账号：" + accountNo
                + "，户名：" + userName
                + "，账户类型：" + accountType
                + "，余额：" + String.format("%.2f", balance)
                + "，年利率：" + String.format("%.4f", annualRate)
                + "，存期：" + months + " 个月";
    }
}
