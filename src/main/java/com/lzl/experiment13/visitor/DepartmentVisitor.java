package com.lzl.experiment13.visitor;

public interface DepartmentVisitor {
    void visit(FullTimeEmployee employee);

    void visit(TemporaryEmployee employee);

    String getReport();
}
