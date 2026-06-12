package com.lzl.experiment13.template;

public class CurrentAccountInterestCalculator extends InterestCalculatorTemplate {
    public CurrentAccountInterestCalculator(AccountRepository repository) {
        super(repository);
    }

    @Override
    public double doCalculateInterest(BankAccount account) {
        System.out.println("3. 采用活期账户利息计算方式：余额 × 年利率 × 存期月数 / 12。");
        return account.getBalance() * account.getAnnualRate() * account.getMonths() / 12;
    }
}
