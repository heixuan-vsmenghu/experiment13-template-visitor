package com.lzl.experiment13.visitor;

public class FullTimeEmployee implements Employee {
    public static final double STANDARD_WORK_HOURS = 40.0;

    private final String name;
    private final String department;
    private final String level;
    private final double weeklyBaseSalary;
    private final double workHours;

    public FullTimeEmployee(String name, String department, String level, double weeklyBaseSalary, double workHours) {
        this.name = name;
        this.department = department;
        this.level = level;
        this.weeklyBaseSalary = weeklyBaseSalary;
        this.workHours = workHours;
    }

    @Override
    public void accept(DepartmentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getLevel() {
        return level;
    }

    public double getWeeklyBaseSalary() {
        return weeklyBaseSalary;
    }

    @Override
    public double getWorkHours() {
        return workHours;
    }

    public double getOvertimeHours() {
        return Math.max(workHours - STANDARD_WORK_HOURS, 0);
    }

    public double getLeaveHours() {
        return Math.max(STANDARD_WORK_HOURS - workHours, 0);
    }

    @Override
    public String toString() {
        return "正式员工 " + name
                + "，部门：" + department
                + "，级别：" + level
                + "，周基本工资：" + String.format("%.2f", weeklyBaseSalary)
                + "，本周工时：" + String.format("%.2f", workHours);
    }
}
