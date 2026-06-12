package com.lzl.experiment13.template;

public abstract class InterestCalculatorTemplate {
    protected final AccountRepository repository;

    protected InterestCalculatorTemplate(AccountRepository repository) {
        this.repository = repository;
    }

    public final void calculateInterest(String accountNo) {
        BankAccount account = queryAccount(accountNo);
        checkAccount(account, accountNo);
        double interest = doCalculateInterest(account);
        displayInterest(account, interest);
    }

    public BankAccount queryAccount(String accountNo) {
        System.out.println("1. 根据账号查询用户信息：" + accountNo);
        return repository.findByAccountNo(accountNo);
    }

    public void checkAccount(BankAccount account, String accountNo) {
        System.out.println("2. 判断账户类型并检查账户数据。");
        if (account == null) {
            throw new IllegalArgumentException("未找到账号 " + accountNo + " 对应的账户信息，请检查输入账号。");
        }
        System.out.println("   查询结果：" + account);
    }

    public abstract double doCalculateInterest(BankAccount account);

    public void displayInterest(BankAccount account, double interest) {
        System.out.println("4. 显示利息结果。");
        System.out.printf("   %s %s 的本期利息为：%.2f 元%n", account.getAccountType(), account.getUserName(), interest);
    }
}
