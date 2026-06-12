package com.lzl.experiment13.visitor;

public class HRDepartmentVisitor implements DepartmentVisitor {
    private double fullTimeWorkHours;
    private double temporaryWorkHours;
    private double totalOvertimeHours;
    private double totalLeaveHours;
    private final StringBuilder reportBuilder = new StringBuilder();

    public HRDepartmentVisitor() {
        reportBuilder.append("【人力资源部工时汇总报告】").append(System.lineSeparator());
    }

    @Override
    public void visit(FullTimeEmployee employee) {
        fullTimeWorkHours += employee.getWorkHours();
        totalOvertimeHours += employee.getOvertimeHours();
        totalLeaveHours += employee.getLeaveHours();

        reportBuilder.append("正式员工 ")
                .append(employee.getName())
                .append("（")
                .append(employee.getDepartment())
                .append("/")
                .append(employee.getLevel())
                .append("），本周工作 ")
                .append(format(employee.getWorkHours()))
                .append(" 小时");
        if (employee.getOvertimeHours() > 0) {
            reportBuilder.append("，加班 ").append(format(employee.getOvertimeHours())).append(" 小时");
        } else if (employee.getLeaveHours() > 0) {
            reportBuilder.append("，请假 ").append(format(employee.getLeaveHours())).append(" 小时");
        } else {
            reportBuilder.append("，无加班无请假");
        }
        reportBuilder.append("。").append(System.lineSeparator());
    }

    @Override
    public void visit(TemporaryEmployee employee) {
        temporaryWorkHours += employee.getWorkHours();
        reportBuilder.append("临时工 ")
                .append(employee.getName())
                .append("（")
                .append(employee.getPosition())
                .append("），本周工作 ")
                .append(format(employee.getWorkHours()))
                .append(" 小时。")
                .append(System.lineSeparator());
    }

    @Override
    public String getReport() {
        StringBuilder result = new StringBuilder(reportBuilder);
        result.append("正式员工工作小时数：").append(format(fullTimeWorkHours)).append(" 小时").append(System.lineSeparator());
        result.append("临时工实际工作小时数：").append(format(temporaryWorkHours)).append(" 小时").append(System.lineSeparator());
        result.append("加班小时数：").append(format(totalOvertimeHours)).append(" 小时").append(System.lineSeparator());
        result.append("请假小时数：").append(format(totalLeaveHours)).append(" 小时").append(System.lineSeparator());
        result.append("总工时：").append(format(getTotalWorkHours())).append(" 小时");
        return result.toString();
    }

    public double getTotalWorkHours() {
        return fullTimeWorkHours + temporaryWorkHours;
    }

    private String format(double value) {
        return String.format("%.2f", value);
    }
}
