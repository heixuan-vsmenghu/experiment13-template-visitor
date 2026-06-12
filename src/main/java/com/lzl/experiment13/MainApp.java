package com.lzl.experiment13;

import com.lzl.experiment13.template.BankInterestDemo;
import com.lzl.experiment13.visitor.VisitorDemo;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("========== 实验13：模板方法模式 银行利息计算 ==========");
        BankInterestDemo.run();

        System.out.println();
        System.out.println("========== 实验13：访问者模式 员工信息管理 ==========");
        VisitorDemo.run();

        System.out.println();
        System.out.println("========== 实验13：测试结果 ==========");
        Experiment13TestRunner.runAllTests();
    }
}
