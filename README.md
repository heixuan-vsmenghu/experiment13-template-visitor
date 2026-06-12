# 实验13：模板方法模式与访问者模式

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

本实验通过两个具体场景理解行为型设计模式的应用。银行利息计算程序使用模板方法模式，将“查询账户、检查账户、计算利息、显示结果”定义为固定流程，活期账户和定期账户只改变具体利息计算步骤。OA 员工信息管理程序使用访问者模式，将人力资源部工时汇总和财务部工资计算封装为不同访问者，使同一批员工对象能够被不同部门以不同方式处理。

## 项目简介

项目为 Maven Java 控制台程序，主入口为 `com.lzl.experiment13.MainApp`。运行后依次演示模板方法模式银行利息计算、访问者模式员工信息管理，并执行 `Experiment13TestRunner` 中的控制台测试断言。

## GitHub 仓库

公开仓库地址：https://github.com/heixuan-vsmenghu/experiment13-template-visitor

老师可以通过该仓库直接查看源码、README、运行结果、实验报告、EA 工程文件和提交记录。

## 模式说明

模板方法模式部分：

- `BankAccount` 是银行账户实体，保存账号、户名、余额、年利率、存期和账户类型。
- `AccountRepository` 模拟账户仓库，负责根据账号查询账户信息。
- `InterestCalculatorTemplate` 是抽象模板类，`calculateInterest()` 是模板方法，并使用 `final` 固定整体流程。
- `CurrentAccountInterestCalculator` 是活期账户利息计算子类。
- `SavingAccountInterestCalculator` 是定期账户利息计算子类，满 12 个月时追加 5% 奖励利息。
- `BankInterestDemo` 是 Client，负责创建账户数据并调用两个具体计算器。

访问者模式部分：

- `Employee` 是抽象员工元素接口。
- `FullTimeEmployee` 和 `TemporaryEmployee` 是具体员工元素。
- `DepartmentVisitor` 是抽象访问者接口。
- `HRDepartmentVisitor` 是人力资源部访问者，负责统计工时、加班和请假。
- `FinanceDepartmentVisitor` 是财务部访问者，负责计算工资。
- `EmployeeList` 是对象结构，保存员工集合并统一接收访问者。
- `VisitorDemo` 是 Client，演示同一批员工被不同访问者处理。

## 项目结构

```text
experiment13-template-visitor
├── pom.xml
├── README.md
├── src/main/java/com/lzl/experiment13
│   ├── MainApp.java
│   ├── Experiment13TestRunner.java
│   ├── template
│   │   ├── BankAccount.java
│   │   ├── AccountRepository.java
│   │   ├── InterestCalculatorTemplate.java
│   │   ├── CurrentAccountInterestCalculator.java
│   │   ├── SavingAccountInterestCalculator.java
│   │   └── BankInterestDemo.java
│   └── visitor
│       ├── Employee.java
│       ├── FullTimeEmployee.java
│       ├── TemporaryEmployee.java
│       ├── DepartmentVisitor.java
│       ├── HRDepartmentVisitor.java
│       ├── FinanceDepartmentVisitor.java
│       ├── EmployeeList.java
│       └── VisitorDemo.java
├── report-images
├── run-result.txt
├── maven-package-result.txt
└── git-result.txt
```

## UML 图说明

`report-images` 目录中保存四张 UML 图：

- `01_TemplateMethod银行利息计算类图.png`
- `02_TemplateMethod银行利息计算顺序图.png`
- `03_Visitor员工信息管理类图.png`
- `04_Visitor员工信息管理顺序图.png`

EA 项目文件为 `实验13_121072021030_林立洲.eap`。

`report-images` 目录中还保存运行与构建截图：

- `05_项目结构截图.png`
- `06_Maven构建成功截图.png`
- `07_MainApp运行结果截图.png`
- `08_Git提交记录截图.png`
- `09_TemplateMethod运行结果截图.png`
- `10_Visitor运行结果截图.png`

## 运行方式

```bash
mvn clean package
java -cp target/experiment13-template-visitor-1.0.0.jar com.lzl.experiment13.MainApp
```

也可以在 IntelliJ IDEA 中导入 Maven 项目后直接运行 `MainApp`。

## 测试结果说明

`Experiment13TestRunner` 对模板方法模式和访问者模式进行了控制台断言测试。测试内容包括活期账户利息计算、定期账户奖励利息计算、不存在账号提示、模板方法完整流程、正式员工加班与请假时长、临时工工资、财务部总工资以及两个访问者报告差异。运行结果中出现“所有测试通过。”表示测试成功。

## Git 说明

本项目已建立本地 Git 仓库，并推送到 GitHub 公开仓库：

https://github.com/heixuan-vsmenghu/experiment13-template-visitor

仓库中包含源码、EA 工程文件、Word 实验报告、运行结果、Maven 构建结果、项目结构说明和截图文件，便于老师直接查看完整实验材料。
