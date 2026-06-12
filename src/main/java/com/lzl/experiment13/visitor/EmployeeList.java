package com.lzl.experiment13.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeeList {
    private final List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("员工对象不能为空");
        }
        employees.add(employee);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
    }

    public void accept(DepartmentVisitor visitor) {
        for (Employee employee : employees) {
            employee.accept(visitor);
        }
    }

    public List<Employee> getEmployees() {
        return Collections.unmodifiableList(employees);
    }
}
