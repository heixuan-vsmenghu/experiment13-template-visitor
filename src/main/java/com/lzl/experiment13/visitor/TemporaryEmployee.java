package com.lzl.experiment13.visitor;

public class TemporaryEmployee implements Employee {
    private final String name;
    private final String position;
    private final double hourlyWage;
    private final double workHours;

    public TemporaryEmployee(String name, String position, double hourlyWage, double workHours) {
        this.name = name;
        this.position = position;
        this.hourlyWage = hourlyWage;
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

    public String getPosition() {
        return position;
    }

    public double getHourlyWage() {
        return hourlyWage;
    }

    @Override
    public double getWorkHours() {
        return workHours;
    }

    @Override
    public String toString() {
        return "临时工 " + name
                + "，岗位：" + position
                + "，小时工资：" + String.format("%.2f", hourlyWage)
                + "，本周工时：" + String.format("%.2f", workHours);
    }
}
