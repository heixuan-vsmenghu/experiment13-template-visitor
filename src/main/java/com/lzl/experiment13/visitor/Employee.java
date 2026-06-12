package com.lzl.experiment13.visitor;

public interface Employee {
    void accept(DepartmentVisitor visitor);

    String getName();

    double getWorkHours();
}
