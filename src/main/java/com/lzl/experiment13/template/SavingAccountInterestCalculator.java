package com.lzl.experiment13.template;

public class SavingAccountInterestCalculator extends InterestCalculatorTemplate {
    public SavingAccountInterestCalculator(AccountRepository repository) {
        super(repository);
    }

    @Override
    public double doCalculateInterest(BankAccount account) {
        System.out.println("3. 采用定期账户利息计算方式：余额 × 年利率 × 存期月数 / 12。");
        double interest = account.getBalance() * account.getAnnualRate() * account.getMonths() / 12;
        if (account.getMonths() >= 12) {
            System.out.println("   定期账户满 12 个月，按规则追加 5% 奖励利息。");
            interest *= 1.05;
        }
        return interest;
    }
}
