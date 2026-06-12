$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
$eaDll = 'C:\Program Files (x86)\Sparx Systems\EA\Interop.EA.dll'
$baseModel = 'C:\Program Files (x86)\Sparx Systems\EA\EABase.eap'
$modelPath = Join-Path $root '实验13_121072021030_林立洲.eap'

Add-Type -Path $eaDll
Copy-Item -LiteralPath $baseModel -Destination $modelPath -Force

function Add-Element {
    param($Package, [string]$Name, [string]$Type, [string[]]$Attributes, [string[]]$Methods, [string]$Stereotype = '', [string]$Notes = '')
    $element = $Package.Elements.AddNew($Name, $Type)
    if ($Stereotype) { $element.Stereotype = $Stereotype }
    if ($Notes) { $element.Notes = $Notes }
    $element.Update() | Out-Null
    foreach ($attrText in $Attributes) {
        $attr = $element.Attributes.AddNew($attrText, '')
        $attr.Update() | Out-Null
    }
    foreach ($methodText in $Methods) {
        $method = $element.Methods.AddNew($methodText, '')
        $method.Update() | Out-Null
    }
    $element.Update() | Out-Null
    $Package.Elements.Refresh()
    return $element
}

function Add-Connector {
    param($Client, $Supplier, [string]$Type, [string]$Name = '')
    $actualType = $Type
    if ($Type -eq 'Aggregation') {
        $actualType = 'Association'
    }
    if ($Type -eq 'Realisation') {
        $actualType = 'Generalization'
    }
    $connector = $Client.Connectors.AddNew($Name, $actualType)
    $connector.SupplierID = $Supplier.ElementID
    if ($Type -eq 'Aggregation') {
        $connector.SupplierEnd.Aggregation = 2
    }
    if ($Type -eq 'Realisation') {
        $connector.Stereotype = 'realize'
    }
    try {
        $connector.Update() | Out-Null
        $Client.Connectors.Refresh()
        return $connector
    }
    catch {
        Write-Warning "关系写入降级：$($Client.Name) -> $($Supplier.Name), type=$Type"
        if ($actualType -ne 'Dependency') {
            $fallback = $Client.Connectors.AddNew($Name, 'Dependency')
            $fallback.SupplierID = $Supplier.ElementID
            $fallback.Stereotype = $Type
            try {
                $fallback.Update() | Out-Null
                $Client.Connectors.Refresh()
                return $fallback
            }
            catch {
                Write-Warning "关系写入跳过：$($Client.Name) -> $($Supplier.Name)"
            }
        }
    }
}

function Add-DiagramObject {
    param($Diagram, $Element, [int]$Left, [int]$Top, [int]$Width, [int]$Height)
    $style = "l=$Left;r=$($Left + $Width);t=$Top;b=$($Top + $Height);"
    $obj = $Diagram.DiagramObjects.AddNew($style, '')
    $obj.ElementID = $Element.ElementID
    $obj.Update() | Out-Null
    $Diagram.DiagramObjects.Refresh()
}

function Add-ClassDiagram {
    param($Package, [string]$Name, [hashtable]$Elements, [array]$Layout)
    $diagram = $Package.Diagrams.AddNew($Name, 'Class')
    $diagram.Update() | Out-Null
    foreach ($item in $Layout) {
        Add-DiagramObject $diagram $Elements[$item.Name] $item.Left $item.Top $item.Width $item.Height
    }
    $diagram.Update() | Out-Null
    $Package.Diagrams.Refresh()
    return $diagram
}

function Add-SequenceDiagram {
    param($Package, [string]$Name, [string[]]$Lifelines, [array]$Messages)
    $diagram = $Package.Diagrams.AddNew($Name, 'Sequence')
    $diagram.Update() | Out-Null
    $objects = @{}
    $left = 80
    foreach ($line in $Lifelines) {
        $el = Add-Element $Package $line 'Object' @() @()
        $objects[$line] = $el
        Add-DiagramObject $diagram $el $left 80 130 760
        $left += 210
    }
    $seq = 1
    foreach ($msg in $Messages) {
        $connector = $objects[$msg.From].Connectors.AddNew($msg.Label, 'Sequence')
        $connector.SupplierID = $objects[$msg.To].ElementID
        $connector.SequenceNo = $seq
        try {
            $connector.Update() | Out-Null
            $objects[$msg.From].Connectors.Refresh()
        }
        catch {
            Write-Warning "顺序消息写入跳过：$($msg.Label)"
        }
        $seq += 1
    }
    $diagram.Update() | Out-Null
    $Package.Diagrams.Refresh()
    return $diagram
}

$repo = New-Object EA.RepositoryClass
$repo.SuppressEADialogs = $true
try {
    if (-not $repo.OpenFile($modelPath)) {
        throw "EA 打开模型失败：$($repo.GetLastError())"
    }

    $rootModel = $repo.Models.AddNew('实验13 模板方法与访问者模式', 'Package')
    $rootModel.Notes = '课程：软件体系结构与设计模式；学生：林立洲；学号：121072021030。'
    $rootModel.Update() | Out-Null
    $repo.Models.Refresh()

    $templatePkg = $rootModel.Packages.AddNew('01_TemplateMethod银行利息计算', 'Package')
    $templatePkg.Notes = '使用模板方法模式实现银行活期账户和定期账户的利息计算流程。'
    $templatePkg.Update() | Out-Null
    $visitorPkg = $rootModel.Packages.AddNew('02_Visitor员工信息管理', 'Package')
    $visitorPkg.Notes = '使用访问者模式实现 OA 员工工时汇总与工资计算。'
    $visitorPkg.Update() | Out-Null
    $rootModel.Packages.Refresh()

    $templateElements = @{}
    $templateElements.BankAccount = Add-Element $templatePkg 'BankAccount' 'Class' @(
        'accountNo : String', 'userName : String', 'balance : double', 'annualRate : double', 'months : int', 'accountType : String'
    ) @(
        'BankAccount(...)', 'getAccountNo() : String', 'getUserName() : String', 'getBalance() : double', 'getAnnualRate() : double', 'getMonths() : int', 'getAccountType() : String', 'toString() : String'
    )
    $templateElements.AccountRepository = Add-Element $templatePkg 'AccountRepository' 'Class' @('accounts : Map<String, BankAccount>') @('AccountRepository()', 'findByAccountNo(accountNo : String) : BankAccount', 'addAccount(account : BankAccount) : void')
    $templateElements.InterestCalculatorTemplate = Add-Element $templatePkg 'InterestCalculatorTemplate' 'Class' @('repository : AccountRepository') @('final calculateInterest(accountNo : String) : void', 'queryAccount(accountNo : String) : BankAccount', 'checkAccount(account : BankAccount) : void', 'doCalculateInterest(account : BankAccount) : double', 'displayInterest(account : BankAccount, interest : double) : void')
    $templateElements.CurrentAccountInterestCalculator = Add-Element $templatePkg 'CurrentAccountInterestCalculator' 'Class' @() @('CurrentAccountInterestCalculator(repository : AccountRepository)', 'doCalculateInterest(account : BankAccount) : double')
    $templateElements.SavingAccountInterestCalculator = Add-Element $templatePkg 'SavingAccountInterestCalculator' 'Class' @() @('SavingAccountInterestCalculator(repository : AccountRepository)', 'doCalculateInterest(account : BankAccount) : double')
    $templateElements.BankInterestDemo = Add-Element $templatePkg 'BankInterestDemo' 'Class' @() @('run() : void')

    Add-Connector $templateElements.CurrentAccountInterestCalculator $templateElements.InterestCalculatorTemplate 'Generalization' | Out-Null
    Add-Connector $templateElements.SavingAccountInterestCalculator $templateElements.InterestCalculatorTemplate 'Generalization' | Out-Null
    Add-Connector $templateElements.InterestCalculatorTemplate $templateElements.AccountRepository 'Association' 'repository' | Out-Null
    Add-Connector $templateElements.AccountRepository $templateElements.BankAccount 'Aggregation' 'accounts' | Out-Null
    Add-Connector $templateElements.BankInterestDemo $templateElements.AccountRepository 'Dependency' | Out-Null
    Add-Connector $templateElements.BankInterestDemo $templateElements.CurrentAccountInterestCalculator 'Dependency' | Out-Null
    Add-Connector $templateElements.BankInterestDemo $templateElements.SavingAccountInterestCalculator 'Dependency' | Out-Null

    Add-ClassDiagram $templatePkg 'TemplateMethod银行利息计算类图' $templateElements @(
        @{Name='BankAccount';Left=40;Top=120;Width=180;Height=220},
        @{Name='AccountRepository';Left=300;Top=150;Width=190;Height=140},
        @{Name='InterestCalculatorTemplate';Left=590;Top=100;Width=240;Height=220},
        @{Name='CurrentAccountInterestCalculator';Left=500;Top=450;Width=230;Height=110},
        @{Name='SavingAccountInterestCalculator';Left=800;Top=450;Width=230;Height=110},
        @{Name='BankInterestDemo';Left=80;Top=460;Width=160;Height=100}
    ) | Out-Null

    Add-SequenceDiagram $templatePkg 'TemplateMethod银行利息计算顺序图' @(
        'client : BankInterestDemo', 'repository : AccountRepository', 'currentCalculator : CurrentAccountInterestCalculator', 'savingCalculator : SavingAccountInterestCalculator', 'currentAccount : BankAccount', 'savingAccount : BankAccount'
    ) @(
        @{From='client : BankInterestDemo';To='repository : AccountRepository';Label='create repository'},
        @{From='client : BankInterestDemo';To='repository : AccountRepository';Label='addAccount(currentAccount)'},
        @{From='client : BankInterestDemo';To='repository : AccountRepository';Label='addAccount(savingAccount)'},
        @{From='client : BankInterestDemo';To='currentCalculator : CurrentAccountInterestCalculator';Label='create(repository)'},
        @{From='client : BankInterestDemo';To='currentCalculator : CurrentAccountInterestCalculator';Label='calculateInterest("C001")'},
        @{From='currentCalculator : CurrentAccountInterestCalculator';To='repository : AccountRepository';Label='findByAccountNo("C001")'},
        @{From='client : BankInterestDemo';To='savingCalculator : SavingAccountInterestCalculator';Label='create(repository)'},
        @{From='client : BankInterestDemo';To='savingCalculator : SavingAccountInterestCalculator';Label='calculateInterest("S001")'},
        @{From='savingCalculator : SavingAccountInterestCalculator';To='repository : AccountRepository';Label='findByAccountNo("S001")'}
    ) | Out-Null

    $visitorElements = @{}
    $visitorElements.Employee = Add-Element $visitorPkg 'Employee' 'Interface' @() @('accept(visitor : DepartmentVisitor) : void', 'getName() : String', 'getWorkHours() : double')
    $visitorElements.FullTimeEmployee = Add-Element $visitorPkg 'FullTimeEmployee' 'Class' @('name : String', 'department : String', 'level : String', 'weeklyBaseSalary : double', 'workHours : double') @('FullTimeEmployee(...)', 'accept(visitor : DepartmentVisitor) : void', 'getOvertimeHours() : double', 'getLeaveHours() : double')
    $visitorElements.TemporaryEmployee = Add-Element $visitorPkg 'TemporaryEmployee' 'Class' @('name : String', 'position : String', 'hourlyWage : double', 'workHours : double') @('TemporaryEmployee(...)', 'accept(visitor : DepartmentVisitor) : void', 'getHourlyWage() : double')
    $visitorElements.DepartmentVisitor = Add-Element $visitorPkg 'DepartmentVisitor' 'Interface' @() @('visit(employee : FullTimeEmployee) : void', 'visit(employee : TemporaryEmployee) : void', 'getReport() : String')
    $visitorElements.HRDepartmentVisitor = Add-Element $visitorPkg 'HRDepartmentVisitor' 'Class' @('totalWorkHours : double', 'reportBuilder : StringBuilder') @('visit(employee : FullTimeEmployee) : void', 'visit(employee : TemporaryEmployee) : void', 'getReport() : String')
    $visitorElements.FinanceDepartmentVisitor = Add-Element $visitorPkg 'FinanceDepartmentVisitor' 'Class' @('totalSalary : double', 'reportBuilder : StringBuilder') @('visit(employee : FullTimeEmployee) : void', 'visit(employee : TemporaryEmployee) : void', 'getReport() : String')
    $visitorElements.EmployeeList = Add-Element $visitorPkg 'EmployeeList' 'Class' @('employees : List<Employee>') @('addEmployee(employee : Employee) : void', 'removeEmployee(employee : Employee) : void', 'accept(visitor : DepartmentVisitor) : void')
    $visitorElements.VisitorDemo = Add-Element $visitorPkg 'VisitorDemo' 'Class' @() @('run() : void')

    Add-Connector $visitorElements.FullTimeEmployee $visitorElements.Employee 'Realisation' | Out-Null
    Add-Connector $visitorElements.TemporaryEmployee $visitorElements.Employee 'Realisation' | Out-Null
    Add-Connector $visitorElements.HRDepartmentVisitor $visitorElements.DepartmentVisitor 'Realisation' | Out-Null
    Add-Connector $visitorElements.FinanceDepartmentVisitor $visitorElements.DepartmentVisitor 'Realisation' | Out-Null
    Add-Connector $visitorElements.EmployeeList $visitorElements.Employee 'Aggregation' 'employees' | Out-Null
    Add-Connector $visitorElements.EmployeeList $visitorElements.DepartmentVisitor 'Dependency' | Out-Null
    Add-Connector $visitorElements.VisitorDemo $visitorElements.EmployeeList 'Dependency' | Out-Null
    Add-Connector $visitorElements.VisitorDemo $visitorElements.HRDepartmentVisitor 'Dependency' | Out-Null
    Add-Connector $visitorElements.VisitorDemo $visitorElements.FinanceDepartmentVisitor 'Dependency' | Out-Null

    Add-ClassDiagram $visitorPkg 'Visitor员工信息管理类图' $visitorElements @(
        @{Name='Employee';Left=40;Top=100;Width=190;Height=130},
        @{Name='FullTimeEmployee';Left=30;Top=400;Width=230;Height=180},
        @{Name='TemporaryEmployee';Left=310;Top=400;Width=220;Height=160},
        @{Name='EmployeeList';Left=340;Top=120;Width=210;Height=130},
        @{Name='DepartmentVisitor';Left=680;Top=100;Width=230;Height=130},
        @{Name='HRDepartmentVisitor';Left=620;Top=400;Width=230;Height=150},
        @{Name='FinanceDepartmentVisitor';Left=920;Top=400;Width=230;Height=150},
        @{Name='VisitorDemo';Left=410;Top=700;Width=160;Height=90}
    ) | Out-Null

    Add-SequenceDiagram $visitorPkg 'Visitor员工信息管理顺序图' @(
        'client : VisitorDemo', 'employeeList : EmployeeList', 'fullTimeEmployee : FullTimeEmployee', 'temporaryEmployee : TemporaryEmployee', 'hrVisitor : HRDepartmentVisitor', 'financeVisitor : FinanceDepartmentVisitor'
    ) @(
        @{From='client : VisitorDemo';To='employeeList : EmployeeList';Label='create employeeList'},
        @{From='client : VisitorDemo';To='fullTimeEmployee : FullTimeEmployee';Label='create FullTimeEmployee'},
        @{From='client : VisitorDemo';To='temporaryEmployee : TemporaryEmployee';Label='create TemporaryEmployee'},
        @{From='client : VisitorDemo';To='employeeList : EmployeeList';Label='addEmployee(...)'},
        @{From='client : VisitorDemo';To='hrVisitor : HRDepartmentVisitor';Label='create HRDepartmentVisitor'},
        @{From='client : VisitorDemo';To='employeeList : EmployeeList';Label='accept(hrVisitor)'},
        @{From='employeeList : EmployeeList';To='fullTimeEmployee : FullTimeEmployee';Label='accept(hrVisitor)'},
        @{From='fullTimeEmployee : FullTimeEmployee';To='hrVisitor : HRDepartmentVisitor';Label='visit(this)'},
        @{From='employeeList : EmployeeList';To='temporaryEmployee : TemporaryEmployee';Label='accept(hrVisitor)'},
        @{From='temporaryEmployee : TemporaryEmployee';To='hrVisitor : HRDepartmentVisitor';Label='visit(this)'},
        @{From='client : VisitorDemo';To='financeVisitor : FinanceDepartmentVisitor';Label='create FinanceDepartmentVisitor'},
        @{From='client : VisitorDemo';To='employeeList : EmployeeList';Label='accept(financeVisitor)'},
        @{From='fullTimeEmployee : FullTimeEmployee';To='financeVisitor : FinanceDepartmentVisitor';Label='visit(this)'},
        @{From='temporaryEmployee : TemporaryEmployee';To='financeVisitor : FinanceDepartmentVisitor';Label='visit(this)'}
    ) | Out-Null

    $repo.SaveAllDiagrams()
    $repo.CloseFile()
    "EA 项目已生成：$modelPath"
}
finally {
    if ($repo) {
        [Runtime.InteropServices.Marshal]::ReleaseComObject($repo) | Out-Null
    }
}






