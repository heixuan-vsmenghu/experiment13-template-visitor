package com.lzl.experiment13.visitor;

public class VisitorDemo {
    private VisitorDemo() {
    }

    public static void run() {
        EmployeeList employeeList = createDemoEmployeeList();

        System.out.println("员工列表信息：");
        for (Employee employee : employeeList.getEmployees()) {
            System.out.println(" - " + employee);
        }

        HRDepartmentVisitor hrVisitor = new HRDepartmentVisitor();
        employeeList.accept(hrVisitor);
        System.out.println();
        System.out.println(hrVisitor.getReport());

        FinanceDepartmentVisitor financeVisitor = new FinanceDepartmentVisitor();
        employeeList.accept(financeVisitor);
        System.out.println();
        System.out.println(financeVisitor.getReport());

        System.out.println();
        System.out.println("说明：同一批员工对象先后被 HR 和 Finance 两个访问者访问，统计工时和计算工资的逻辑分别封装在访问者类中。");
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
}
