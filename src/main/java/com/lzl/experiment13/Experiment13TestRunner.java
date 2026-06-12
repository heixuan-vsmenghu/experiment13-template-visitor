package com.lzl.experiment13;

import com.lzl.experiment13.template.AccountRepository;
import com.lzl.experiment13.template.BankAccount;
import com.lzl.experiment13.template.CurrentAccountInterestCalculator;
import com.lzl.experiment13.template.SavingAccountInterestCalculator;
import com.lzl.experiment13.visitor.EmployeeList;
import com.lzl.experiment13.visitor.FinanceDepartmentVisitor;
import com.lzl.experiment13.visitor.FullTimeEmployee;
import com.lzl.experiment13.visitor.HRDepartmentVisitor;
import com.lzl.experiment13.visitor.TemporaryEmployee;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Experiment13TestRunner {
    private static final double DELTA = 0.000001;

    private Experiment13TestRunner() {
    }

    public static void runAllTests() {
        testCurrentAccountInterest();
        testSavingAccountInterest();
        testMissingAccount();
        testTemplateFlowOutput();
        testFullTimeEmployeeHours();
        testTemporaryEmployeeSalary();
        testFinanceTotalSalary();
        testVisitorReportsAreDifferent();
        System.out.println("所有测试通过。");
    }

    private static void testCurrentAccountInterest() {
        BankAccount account = new BankAccount("C001", "张三", 10000, 0.0035, 6, "活期账户");
        CurrentAccountInterestCalculator calculator = new CurrentAccountInterestCalculator(new AccountRepository());
        assertDoubleEquals(17.5, calculator.doCalculateInterest(account), "活期账户利息计算结果不正确");
        System.out.println("[通过] 活期账户利息计算结果符合公式。");
    }

    private static void testSavingAccountInterest() {
        BankAccount account = new BankAccount("S001", "李四", 20000, 0.018, 12, "定期账户");
        SavingAccountInterestCalculator calculator = new SavingAccountInterestCalculator(new AccountRepository());
        assertDoubleEquals(378.0, calculator.doCalculateInterest(account), "定期账户利息计算结果不正确");
        System.out.println("[通过] 定期账户利息计算结果符合公式。");
    }

    private static void testMissingAccount() {
        AccountRepository repository = new AccountRepository();
        CurrentAccountInterestCalculator calculator = new CurrentAccountInterestCalculator(repository);
        try {
            calculator.calculateInterest("NO_ACCOUNT");
            throw new AssertionError("查询不存在账号时没有给出异常提示");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().contains("NO_ACCOUNT"), "不存在账号异常信息不够友好");
        }
        System.out.println("[通过] 查询不存在账号时给出友好提示。");
    }

    private static void testTemplateFlowOutput() {
        AccountRepository repository = new AccountRepository();
        repository.addAccount(new BankAccount("C001", "张三", 10000, 0.0035, 6, "活期账户"));
        CurrentAccountInterestCalculator calculator = new CurrentAccountInterestCalculator(repository);
        String output = captureOutput(() -> calculator.calculateInterest("C001"));
        assertTrue(output.contains("根据账号查询用户信息"), "模板流程缺少查询步骤");
        assertTrue(output.contains("判断账户类型"), "模板流程缺少检查步骤");
        assertTrue(output.contains("采用活期账户利息计算方式"), "模板流程缺少具体计算步骤");
        assertTrue(output.contains("显示利息"), "模板流程缺少显示步骤");
        System.out.println("[通过] calculateInterest() 能完整执行查询、检查、计算、显示流程。");
    }

    private static void testFullTimeEmployeeHours() {
        FullTimeEmployee overtimeEmployee = new FullTimeEmployee("张三", "技术部", "高级", 3000, 45);
        FullTimeEmployee leaveEmployee = new FullTimeEmployee("李四", "财务部", "中级", 2600, 36);
        FullTimeEmployee normalEmployee = new FullTimeEmployee("赵六", "人力资源部", "初级", 2200, 40);
        assertDoubleEquals(5.0, overtimeEmployee.getOvertimeHours(), "45 小时工作应记录 5 小时加班");
        assertDoubleEquals(4.0, leaveEmployee.getLeaveHours(), "36 小时工作应记录 4 小时请假");
        assertDoubleEquals(0.0, normalEmployee.getOvertimeHours(), "40 小时不应记录加班");
        assertDoubleEquals(0.0, normalEmployee.getLeaveHours(), "40 小时不应记录请假");
        System.out.println("[通过] 正式员工加班和请假时长计算正确。");
    }

    private static void testTemporaryEmployeeSalary() {
        TemporaryEmployee employee = new TemporaryEmployee("王五", "仓库搬运", 60, 30);
        FinanceDepartmentVisitor visitor = new FinanceDepartmentVisitor();
        employee.accept(visitor);
        assertDoubleEquals(1800.0, visitor.getTotalSalary(), "临时工工资计算不正确");
        System.out.println("[通过] 临时工工资等于工作小时乘以小时工资。");
    }

    private static void testFinanceTotalSalary() {
        EmployeeList employeeList = createDemoEmployeeList();
        FinanceDepartmentVisitor visitor = new FinanceDepartmentVisitor();
        employeeList.accept(visitor);
        assertDoubleEquals(11030.0, visitor.getTotalSalary(), "财务部总工资计算不正确");
        System.out.println("[通过] 财务部总工资计算正确。");
    }

    private static void testVisitorReportsAreDifferent() {
        EmployeeList employeeList = createDemoEmployeeList();
        HRDepartmentVisitor hrVisitor = new HRDepartmentVisitor();
        FinanceDepartmentVisitor financeVisitor = new FinanceDepartmentVisitor();
        employeeList.accept(hrVisitor);
        employeeList.accept(financeVisitor);
        String hrReport = hrVisitor.getReport();
        String financeReport = financeVisitor.getReport();
        assertTrue(hrReport.contains("工时汇总") && hrReport.contains("总工时"), "HR 访问者报告内容不正确");
        assertTrue(financeReport.contains("工资汇总") && financeReport.contains("总工资"), "Finance 访问者报告内容不正确");
        assertTrue(!hrReport.equals(financeReport), "两个访问者输出不应相同");
        System.out.println("[通过] HR 访问者和 Finance 访问者访问同一 EmployeeList 时输出不同报告。");
    }

    private static EmployeeList createDemoEmployeeList() {
        EmployeeList employeeList = new EmployeeList();
        employeeList.addEmployee(new FullTimeEmployee("张三", "技术部", "高级", 3000, 45));
        employeeList.addEmployee(new FullTimeEmployee("李四", "财务部", "中级", 2600, 36));
        employeeList.addEmployee(new FullTimeEmployee("赵六", "人力资源部", "初级", 2200, 40));
        employeeList.addEmployee(new TemporaryEmployee("王五", "仓库搬运", 60, 30));
        employeeList.addEmployee(new TemporaryEmployee("钱七", "客服支持", 50, 25));
        return employeeList;
    }

    private static String captureOutput(Runnable runnable) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream printStream = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(printStream);
            runnable.run();
        } finally {
            System.setOut(originalOut);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    private static void assertDoubleEquals(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > DELTA) {
            throw new AssertionError(message + "，期望值：" + expected + "，实际值：" + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
