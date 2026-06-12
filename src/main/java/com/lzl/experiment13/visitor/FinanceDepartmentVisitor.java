package com.lzl.experiment13.visitor;

public class FinanceDepartmentVisitor implements DepartmentVisitor {
    private double totalSalary;
    private final StringBuilder reportBuilder = new StringBuilder();

    public FinanceDepartmentVisitor() {
        reportBuilder.append("【财务部工资汇总报告】").append(System.lineSeparator());
    }

    @Override
    public void visit(FullTimeEmployee employee) {
        double salary = calculateFullTimeSalary(employee);
        totalSalary += salary;

        reportBuilder.append("正式员工 ")
                .append(employee.getName())
                .append("：基本工资 ")
                .append(format(employee.getWeeklyBaseSalary()))
                .append(" 元，实际工作 ")
                .append(format(employee.getWorkHours()))
                .append(" 小时");
        if (employee.getOvertimeHours() > 0) {
            reportBuilder.append("，加班工资 ")
                    .append(format(employee.getOvertimeHours()))
                    .append(" × 100 = ")
                    .append(format(employee.getOvertimeHours() * 100))
                    .append(" 元");
        } else if (employee.getLeaveHours() > 0) {
            reportBuilder.append("，请假扣款 ")
                    .append(format(employee.getLeaveHours()))
                    .append(" × 80 = ")
                    .append(format(employee.getLeaveHours() * 80))
                    .append(" 元");
        } else {
            reportBuilder.append("，无加班工资和请假扣款");
        }
        reportBuilder.append("，本周工资 ")
                .append(format(salary))
                .append(" 元。")
                .append(System.lineSeparator());
    }

    @Override
    public void visit(TemporaryEmployee employee) {
        double salary = calculateTemporarySalary(employee);
        totalSalary += salary;

        reportBuilder.append("临时工 ")
                .append(employee.getName())
                .append("：")
                .append(format(employee.getWorkHours()))
                .append(" 小时 × ")
                .append(format(employee.getHourlyWage()))
                .append(" 元/小时 = ")
                .append(format(salary))
                .append(" 元。")
                .append(System.lineSeparator());
    }

    @Override
    public String getReport() {
        return reportBuilder + "总工资：" + format(totalSalary) + " 元";
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public double calculateFullTimeSalary(FullTimeEmployee employee) {
        if (employee.getWorkHours() > FullTimeEmployee.STANDARD_WORK_HOURS) {
            return employee.getWeeklyBaseSalary() + employee.getOvertimeHours() * 100;
        }
        if (employee.getWorkHours() < FullTimeEmployee.STANDARD_WORK_HOURS) {
            return Math.max(employee.getWeeklyBaseSalary() - employee.getLeaveHours() * 80, 0);
        }
        return employee.getWeeklyBaseSalary();
    }

    public double calculateTemporarySalary(TemporaryEmployee employee) {
        return employee.getWorkHours() * employee.getHourlyWage();
    }

    private String format(double value) {
        return String.format("%.2f", value);
    }
}
