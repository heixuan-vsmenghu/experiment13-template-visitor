package com.lzl.experiment13.template;

public class BankInterestDemo {
    private BankInterestDemo() {
    }

    public static void run() {
        System.out.println("创建账户数据。");
        AccountRepository repository = new AccountRepository();
        repository.addAccount(new BankAccount("C001", "张三", 10000, 0.0035, 6, "活期账户"));
        repository.addAccount(new BankAccount("S001", "李四", 20000, 0.018, 12, "定期账户"));

        System.out.println();
        System.out.println("对活期账户 C001 计算利息：");
        InterestCalculatorTemplate currentCalculator = new CurrentAccountInterestCalculator(repository);
        currentCalculator.calculateInterest("C001");

        System.out.println();
        System.out.println("对定期账户 S001 计算利息：");
        InterestCalculatorTemplate savingCalculator = new SavingAccountInterestCalculator(repository);
        savingCalculator.calculateInterest("S001");

        System.out.println();
        System.out.println("说明：客户端只调用 calculateInterest()，父类固定查询、检查、计算、显示流程，子类只改变具体利息计算步骤。");
    }
}
