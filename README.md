# 实验13 模板方法与访问者模式

## 个人信息

- 年级专业：2023级软件工程
- 班级：软工2班
- 学号：121072021030
- 姓名：林立洲
- 课程：软件体系结构与设计模式
- 建模工具：Enterprise Architect 12
- 开发工具：IntelliJ IDEA
- 开发语言：Java
- 项目管理：Maven

## 实验目的

本实验通过两个业务场景练习模板方法模式和访问者模式。模板方法模式用于银行利息计算流程，父类固定查询、检查、计算、显示的算法骨架，子类只负责不同账户类型的利息计算。访问者模式用于 OA 员工信息管理，HR 和 Finance 作为不同访问者访问同一批员工对象，分别完成工时汇总和工资计算。

## 项目简介

项目为 Maven Java 控制台程序，主入口为 `com.lzl.experiment13.MainApp`。运行后依次执行：

1. 模板方法模式银行利息计算演示；
2. 访问者模式员工信息管理演示；
3. `Experiment13TestRunner` 控制台测试。

## 项目结构

```text
experiment13-template-visitor
├─ pom.xml
├─ README.md
├─ src/main/java/com/lzl/experiment13
│  ├─ MainApp.java
│  ├─ Experiment13TestRunner.java
│  ├─ template
│  │  ├─ BankAccount.java
│  │  ├─ AccountRepository.java
│  │  ├─ InterestCalculatorTemplate.java
│  │  ├─ CurrentAccountInterestCalculator.java
│  │  ├─ SavingAccountInterestCalculator.java
│  │  └─ BankInterestDemo.java
│  └─ visitor
│     ├─ Employee.java
│     ├─ FullTimeEmployee.java
│     ├─ TemporaryEmployee.java
│     ├─ DepartmentVisitor.java
│     ├─ HRDepartmentVisitor.java
│     ├─ FinanceDepartmentVisitor.java
│     ├─ EmployeeList.java
│     └─ VisitorDemo.java
├─ uml
├─ report-images
├─ run-result.txt
├─ maven-package-result.txt
└─ git-result.txt
```

## 模式说明

### 模板方法模式

`InterestCalculatorTemplate` 是抽象模板类，`calculateInterest(accountNo)` 是模板方法，并使用 `final` 修饰。该方法固定执行 `queryAccount -> checkAccount -> doCalculateInterest -> displayInterest`。其中 `doCalculateInterest()` 是可变步骤，由 `CurrentAccountInterestCalculator` 和 `SavingAccountInterestCalculator` 分别实现。

### 访问者模式

`Employee` 是抽象元素，`FullTimeEmployee` 和 `TemporaryEmployee` 是具体元素。`DepartmentVisitor` 是抽象访问者，`HRDepartmentVisitor` 负责工时汇总，`FinanceDepartmentVisitor` 负责工资计算。`EmployeeList` 保存员工对象，并通过 `accept(visitor)` 让同一批员工被不同部门访问者处理。

## UML 图说明

EA 项目文件：`实验13_121072021030_林立洲.eap`

UML 图源位于 `uml/`：

- `template-method-class-diagram.mmd`
- `template-method-sequence-diagram.mmd`
- `visitor-class-diagram.mmd`
- `visitor-sequence-diagram.mmd`

UML 导出图片位于 `report-images/`：

- `TemplateMethod银行利息计算类图.png`
- `TemplateMethod银行利息计算顺序图.png`
- `Visitor员工信息管理类图.png`
- `Visitor员工信息管理顺序图.png`

## 运行方式

```bash
mvn clean package
java -cp target/experiment13-template-visitor-1.0.0.jar com.lzl.experiment13.MainApp
```

也可以在 IntelliJ IDEA 中直接运行 `MainApp.main()`。

## 测试结果

`Experiment13TestRunner.runAllTests()` 覆盖了活期账户利息、定期账户利息、不存在账号提示、模板方法流程输出、正式员工加班/请假时长、临时工工资、财务部总工资、两个访问者报告差异等内容。当前运行结果显示所有测试通过，完整输出见 `run-result.txt`。

## Git 说明

本实验文档未明确要求 GitHub 或 Gitee 远程仓库，因此只创建本地 Git 仓库并完成本地提交，不填写远程仓库地址。
