from __future__ import annotations

import os
import textwrap
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[1]
IMAGES = ROOT / "report-images"


def font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont:
    candidates = [
        Path(r"C:\Windows\Fonts\msyhbd.ttc" if bold else r"C:\Windows\Fonts\msyh.ttc"),
        Path(r"C:\Windows\Fonts\simhei.ttf"),
        Path(r"C:\Windows\Fonts\simsun.ttc"),
        Path(r"C:\Windows\Fonts\arial.ttf"),
    ]
    for path in candidates:
        if path.exists():
            return ImageFont.truetype(str(path), size)
    return ImageFont.load_default()


TITLE_FONT = font(34, True)
CLASS_FONT = font(22, True)
TEXT_FONT = font(18)
SMALL_FONT = font(16)
CONSOLE_FONT = font(18)


def ensure_dirs() -> None:
    IMAGES.mkdir(exist_ok=True)


def text_size(draw: ImageDraw.ImageDraw, text: str, used_font: ImageFont.ImageFont = TEXT_FONT) -> tuple[int, int]:
    box = draw.textbbox((0, 0), text, font=used_font)
    return box[2] - box[0], box[3] - box[1]


def arrow(draw: ImageDraw.ImageDraw, start: tuple[int, int], end: tuple[int, int], fill=(60, 80, 110), width=3) -> None:
    draw.line([start, end], fill=fill, width=width)
    x1, y1 = start
    x2, y2 = end
    dx, dy = x2 - x1, y2 - y1
    length = max((dx * dx + dy * dy) ** 0.5, 1)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    size = 12
    p1 = (x2 - ux * size + px * size * 0.55, y2 - uy * size + py * size * 0.55)
    p2 = (x2 - ux * size - px * size * 0.55, y2 - uy * size - py * size * 0.55)
    draw.polygon([end, p1, p2], fill=fill)


def hollow_triangle(draw: ImageDraw.ImageDraw, start: tuple[int, int], end: tuple[int, int], fill=(60, 80, 110), width=3) -> None:
    draw.line([start, end], fill=fill, width=width)
    x1, y1 = start
    x2, y2 = end
    dx, dy = x2 - x1, y2 - y1
    length = max((dx * dx + dy * dy) ** 0.5, 1)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    size = 18
    p1 = (x2 - ux * size + px * size * 0.7, y2 - uy * size + py * size * 0.7)
    p2 = (x2 - ux * size - px * size * 0.7, y2 - uy * size - py * size * 0.7)
    draw.polygon([end, p1, p2], outline=fill, fill=(255, 255, 255))


def diamond_line(draw: ImageDraw.ImageDraw, start: tuple[int, int], end: tuple[int, int], fill=(60, 80, 110), width=3) -> None:
    draw.line([start, end], fill=fill, width=width)
    x1, y1 = start
    x2, y2 = end
    dx, dy = x2 - x1, y2 - y1
    length = max((dx * dx + dy * dy) ** 0.5, 1)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    size = 14
    diamond = [
        start,
        (x1 + ux * size + px * size * 0.6, y1 + uy * size + py * size * 0.6),
        (x1 + ux * size * 2, y1 + uy * size * 2),
        (x1 + ux * size - px * size * 0.6, y1 + uy * size - py * size * 0.6),
    ]
    draw.polygon(diamond, outline=fill, fill=(255, 255, 255))


def class_box(draw: ImageDraw.ImageDraw, x: int, y: int, w: int, title: str, attributes: list[str], methods: list[str], stereotype: str | None = None) -> tuple[int, int, int, int]:
    line_h = 24
    header_h = 44 + (24 if stereotype else 0)
    h = header_h + max(1, len(attributes)) * line_h + max(1, len(methods)) * line_h + 22
    draw.rounded_rectangle([x, y, x + w, y + h], radius=8, fill=(255, 255, 255), outline=(45, 72, 120), width=2)
    draw.rectangle([x, y, x + w, y + header_h], fill=(226, 236, 250), outline=(45, 72, 120), width=2)
    yy = y + 8
    if stereotype:
        sw, _ = text_size(draw, stereotype, SMALL_FONT)
        draw.text((x + (w - sw) / 2, yy), stereotype, fill=(70, 70, 70), font=SMALL_FONT)
        yy += 23
    tw, _ = text_size(draw, title, CLASS_FONT)
    draw.text((x + (w - tw) / 2, yy), title, fill=(25, 45, 80), font=CLASS_FONT)
    yy = y + header_h
    draw.line([(x, yy), (x + w, yy)], fill=(45, 72, 120), width=2)
    yy += 8
    for item in attributes or [" "]:
        draw.text((x + 12, yy), item, fill=(35, 35, 35), font=SMALL_FONT)
        yy += line_h
    draw.line([(x, yy), (x + w, yy)], fill=(45, 72, 120), width=2)
    yy += 8
    for item in methods or [" "]:
        draw.text((x + 12, yy), item, fill=(35, 35, 35), font=SMALL_FONT)
        yy += line_h
    return x, y, x + w, y + h


def draw_title(draw: ImageDraw.ImageDraw, title: str, width: int) -> None:
    tw, th = text_size(draw, title, TITLE_FONT)
    draw.text(((width - tw) / 2, 26), title, fill=(20, 40, 80), font=TITLE_FONT)


def template_class_diagram() -> None:
    img = Image.new("RGB", (1900, 1180), (247, 250, 255))
    draw = ImageDraw.Draw(img)
    draw_title(draw, "TemplateMethod 银行利息计算类图", img.width)

    bank = class_box(draw, 80, 155, 395, "BankAccount",
                     ["- accountNo : String", "- userName : String", "- balance : double", "- annualRate : double", "- months : int", "- accountType : String"],
                     ["+ BankAccount(...)", "+ getAccountNo() : String", "+ getUserName() : String", "+ getBalance() : double", "+ getAnnualRate() : double", "+ getMonths() : int", "+ getAccountType() : String", "+ toString() : String"])
    repo = class_box(draw, 610, 220, 430, "AccountRepository",
                     ["- accounts : Map<String, BankAccount>"],
                     ["+ AccountRepository()", "+ findByAccountNo(accountNo) : BankAccount", "+ addAccount(account) : void"])
    template = class_box(draw, 1210, 150, 560, "InterestCalculatorTemplate",
                         ["# repository : AccountRepository"],
                         ["+ final calculateInterest(accountNo) : void", "+ queryAccount(accountNo) : BankAccount", "+ checkAccount(account) : void", "+ doCalculateInterest(account) : double", "+ displayInterest(account, interest) : void"])
    current = class_box(draw, 930, 760, 430, "CurrentAccountInterestCalculator",
                        [], ["+ CurrentAccountInterestCalculator(repository)", "+ doCalculateInterest(account) : double"])
    saving = class_box(draw, 1430, 760, 410, "SavingAccountInterestCalculator",
                       [], ["+ SavingAccountInterestCalculator(repository)", "+ doCalculateInterest(account) : double"])
    demo = class_box(draw, 130, 760, 330, "BankInterestDemo", [], ["+ run() : void"])

    diamond_line(draw, (repo[0], (repo[1] + repo[3]) // 2), (bank[2], (bank[1] + bank[3]) // 2))
    draw.text((500, 285), "聚合 accounts", fill=(55, 70, 95), font=SMALL_FONT)
    arrow(draw, (template[0], 300), (repo[2], 300))
    draw.text((1065, 272), "关联 repository", fill=(55, 70, 95), font=SMALL_FONT)
    hollow_triangle(draw, (1145, current[1]), (1410, template[3]))
    hollow_triangle(draw, (1625, saving[1]), (1520, template[3]))
    arrow(draw, (demo[2], 820), (repo[0], 360))
    arrow(draw, (demo[2], 870), (current[0], 850))
    arrow(draw, (demo[2], 920), (saving[0], 850))

    note = "calculateInterest() 定义固定流程：queryAccount → checkAccount → doCalculateInterest → displayInterest；子类只实现具体利息计算。"
    draw.rounded_rectangle([80, 1040, 1820, 1110], radius=8, fill=(255, 250, 229), outline=(190, 145, 55), width=2)
    draw.text((110, 1060), note, fill=(85, 65, 20), font=TEXT_FONT)
    img.save(IMAGES / "TemplateMethod银行利息计算类图.png")


def visitor_class_diagram() -> None:
    img = Image.new("RGB", (1950, 1250), (247, 250, 255))
    draw = ImageDraw.Draw(img)
    draw_title(draw, "Visitor 员工信息管理类图", img.width)

    employee = class_box(draw, 80, 140, 380, "Employee", [], ["+ accept(visitor) : void", "+ getName() : String", "+ getWorkHours() : double"], "<<interface>>")
    full = class_box(draw, 70, 560, 420, "FullTimeEmployee",
                     ["- name : String", "- department : String", "- level : String", "- weeklyBaseSalary : double", "- workHours : double"],
                     ["+ accept(visitor) : void", "+ getOvertimeHours() : double", "+ getLeaveHours() : double"])
    temp = class_box(draw, 540, 560, 390, "TemporaryEmployee",
                     ["- name : String", "- position : String", "- hourlyWage : double", "- workHours : double"],
                     ["+ accept(visitor) : void", "+ getHourlyWage() : double"])
    visitor = class_box(draw, 1120, 140, 420, "DepartmentVisitor", [], ["+ visit(FullTimeEmployee) : void", "+ visit(TemporaryEmployee) : void", "+ getReport() : String"], "<<interface>>")
    hr = class_box(draw, 1010, 560, 410, "HRDepartmentVisitor",
                   ["- totalWorkHours : double", "- reportBuilder : StringBuilder"],
                   ["+ visit(FullTimeEmployee) : void", "+ visit(TemporaryEmployee) : void", "+ getReport() : String"])
    finance = class_box(draw, 1480, 560, 410, "FinanceDepartmentVisitor",
                        ["- totalSalary : double", "- reportBuilder : StringBuilder"],
                        ["+ visit(FullTimeEmployee) : void", "+ visit(TemporaryEmployee) : void", "+ getReport() : String"])
    emp_list = class_box(draw, 620, 160, 380, "EmployeeList",
                         ["- employees : List<Employee>"],
                         ["+ addEmployee(employee) : void", "+ removeEmployee(employee) : void", "+ accept(visitor) : void"])
    demo = class_box(draw, 760, 1000, 300, "VisitorDemo", [], ["+ run() : void"])

    hollow_triangle(draw, (260, full[1]), (260, employee[3]))
    hollow_triangle(draw, (730, temp[1]), (360, employee[3]))
    hollow_triangle(draw, (1215, hr[1]), (1245, visitor[3]))
    hollow_triangle(draw, (1700, finance[1]), (1420, visitor[3]))
    diamond_line(draw, (emp_list[0], 260), (employee[2], 260))
    draw.text((500, 230), "聚合 0..*", fill=(55, 70, 95), font=SMALL_FONT)
    arrow(draw, (full[2], 680), (visitor[0], 285))
    arrow(draw, (temp[2], 720), (visitor[0], 330))
    arrow(draw, (emp_list[2], 360), (visitor[0], 360))
    arrow(draw, (demo[0] + 40, demo[1]), (760, emp_list[3]))
    arrow(draw, (demo[2] - 40, demo[1]), (1210, hr[3]))
    arrow(draw, (demo[2], demo[1] + 20), (1590, finance[3]))

    note = "员工对象结构相对稳定；HR 和 Finance 的统计计算封装为访问者，新增部门统计需求时可新增访问者类。"
    draw.rounded_rectangle([80, 1160, 1870, 1225], radius=8, fill=(255, 250, 229), outline=(190, 145, 55), width=2)
    draw.text((110, 1178), note, fill=(85, 65, 20), font=TEXT_FONT)
    img.save(IMAGES / "Visitor员工信息管理类图.png")


def sequence_box(draw: ImageDraw.ImageDraw, x: int, y: int, w: int, label: str) -> tuple[int, int, int, int]:
    draw.rounded_rectangle([x, y, x + w, y + 55], radius=8, fill=(226, 236, 250), outline=(45, 72, 120), width=2)
    tw, _ = text_size(draw, label, SMALL_FONT)
    draw.text((x + (w - tw) / 2, y + 17), label, fill=(25, 45, 80), font=SMALL_FONT)
    return x, y, x + w, y + 55


def lifeline(draw: ImageDraw.ImageDraw, x: int, top: int, bottom: int) -> None:
    y = top
    while y < bottom:
        draw.line([(x, y), (x, min(y + 18, bottom))], fill=(105, 120, 145), width=2)
        y += 30


def message(draw: ImageDraw.ImageDraw, x1: int, x2: int, y: int, label: str, dashed: bool = False) -> None:
    if dashed:
        step = 16
        current = min(x1, x2)
        while current < max(x1, x2):
            draw.line([(current, y), (min(current + 10, max(x1, x2)), y)], fill=(60, 80, 110), width=2)
            current += step
    else:
        draw.line([(x1, y), (x2, y)], fill=(60, 80, 110), width=2)
    arrow(draw, (x1, y), (x2, y), width=2)
    tx = min(x1, x2) + 8
    draw.text((tx, y - 24), label, fill=(35, 45, 70), font=SMALL_FONT)


def template_sequence_diagram() -> None:
    img = Image.new("RGB", (2100, 1320), (247, 250, 255))
    draw = ImageDraw.Draw(img)
    draw_title(draw, "TemplateMethod 银行利息计算顺序图", img.width)
    labels = [
        "client:BankInterestDemo", "repository:AccountRepository", "currentCalculator", "savingCalculator", "currentAccount", "savingAccount"
    ]
    xs = [170, 500, 850, 1200, 1550, 1880]
    for x, label in zip(xs, labels):
        sequence_box(draw, x - 130, 120, 260, label)
        lifeline(draw, x, 175, 1240)

    rows = [
        (0, 1, "create repository"),
        (0, 1, "addAccount(currentAccount)"),
        (0, 1, "addAccount(savingAccount)"),
        (0, 2, "create(repository)"),
        (0, 2, "calculateInterest(\"C001\")"),
        (2, 2, "queryAccount(\"C001\")"),
        (2, 1, "findByAccountNo(\"C001\")"),
        (1, 2, "return currentAccount"),
        (2, 2, "checkAccount(currentAccount)"),
        (2, 2, "doCalculateInterest(currentAccount)"),
        (2, 2, "displayInterest(currentAccount, interest)"),
        (0, 3, "create(repository)"),
        (0, 3, "calculateInterest(\"S001\")"),
        (3, 3, "queryAccount(\"S001\")"),
        (3, 1, "findByAccountNo(\"S001\")"),
        (1, 3, "return savingAccount"),
        (3, 3, "checkAccount(savingAccount)"),
        (3, 3, "doCalculateInterest(savingAccount)"),
        (3, 3, "displayInterest(savingAccount, interest)"),
    ]
    y = 230
    for idx, (a, b, label) in enumerate(rows, start=1):
        if a == b:
            x = xs[a]
            draw.arc([x, y - 12, x + 90, y + 36], start=270, end=90, fill=(60, 80, 110), width=2)
            arrow(draw, (x + 90, y + 12), (x + 25, y + 12), width=2)
            draw.text((x + 36, y - 24), f"{idx}. {label}", fill=(35, 45, 70), font=SMALL_FONT)
        else:
            message(draw, xs[a], xs[b], y, f"{idx}. {label}", dashed=("return" in label))
        y += 52
    img.save(IMAGES / "TemplateMethod银行利息计算顺序图.png")


def visitor_sequence_diagram() -> None:
    img = Image.new("RGB", (2050, 1320), (247, 250, 255))
    draw = ImageDraw.Draw(img)
    draw_title(draw, "Visitor 员工信息管理顺序图", img.width)
    labels = [
        "client:VisitorDemo", "employeeList", "fullTimeEmployee", "temporaryEmployee", "hrVisitor", "financeVisitor"
    ]
    xs = [170, 500, 820, 1160, 1500, 1840]
    for x, label in zip(xs, labels):
        sequence_box(draw, x - 130, 120, 260, label)
        lifeline(draw, x, 175, 1240)

    rows = [
        (0, 1, "create EmployeeList"),
        (0, 2, "create FullTimeEmployee"),
        (0, 3, "create TemporaryEmployee"),
        (0, 1, "addEmployee(...)"),
        (0, 4, "create HRDepartmentVisitor"),
        (0, 1, "accept(hrVisitor)"),
        (1, 2, "accept(hrVisitor)"),
        (2, 4, "visit(this)"),
        (1, 3, "accept(hrVisitor)"),
        (3, 4, "visit(this)"),
        (0, 4, "getReport() 工时汇总"),
        (0, 5, "create FinanceDepartmentVisitor"),
        (0, 1, "accept(financeVisitor)"),
        (1, 2, "accept(financeVisitor)"),
        (2, 5, "visit(this)"),
        (1, 3, "accept(financeVisitor)"),
        (3, 5, "visit(this)"),
        (0, 5, "getReport() 工资汇总"),
    ]
    y = 230
    for idx, (a, b, label) in enumerate(rows, start=1):
        message(draw, xs[a], xs[b], y, f"{idx}. {label}")
        y += 54
    img.save(IMAGES / "Visitor员工信息管理顺序图.png")


def wrap_pixels(draw: ImageDraw.ImageDraw, text: str, max_width: int, used_font: ImageFont.ImageFont) -> list[str]:
    if not text:
        return [""]
    result: list[str] = []
    current = ""
    for ch in text:
        if ch == "\t":
            ch = "    "
        candidate = current + ch
        if text_size(draw, candidate, used_font)[0] <= max_width:
            current = candidate
        else:
            if current:
                result.append(current)
            current = ch
    if current:
        result.append(current)
    return result


def text_file_to_image(input_file: Path, output_file: Path, title: str, max_lines: int = 120) -> None:
    if not input_file.exists():
        return
    raw_lines = input_file.read_text(encoding="utf-8", errors="ignore").splitlines()
    canvas_width = 1500
    tmp = Image.new("RGB", (canvas_width, 500), "white")
    draw = ImageDraw.Draw(tmp)
    lines: list[str] = []
    for line in raw_lines[:max_lines]:
        lines.extend(wrap_pixels(draw, line, canvas_width - 90, CONSOLE_FONT))
    line_height = 26
    height = 90 + max(1, len(lines)) * line_height + 40
    img = Image.new("RGB", (canvas_width, height), (250, 252, 255))
    draw = ImageDraw.Draw(img)
    draw.rectangle([0, 0, canvas_width, 70], fill=(35, 55, 90))
    draw.text((32, 18), title, fill=(255, 255, 255), font=font(24, True))
    y = 92
    for line in lines:
        draw.text((42, y), line, fill=(25, 30, 40), font=CONSOLE_FONT)
        y += line_height
    img.save(output_file)


def create_project_tree_file() -> None:
    include = [
        "pom.xml", "README.md", "src/main/java/com/lzl/experiment13/MainApp.java",
        "src/main/java/com/lzl/experiment13/Experiment13TestRunner.java",
        "src/main/java/com/lzl/experiment13/template", "src/main/java/com/lzl/experiment13/visitor",
        "uml", "report-images", "run-result.txt", "maven-package-result.txt", "git-result.txt",
    ]
    tree = ["experiment13-template-visitor/"]
    for item in include:
        tree.append("  " + item.replace("/", os.sep))
    (ROOT / "project-structure.txt").write_text("\n".join(tree) + "\n", encoding="utf-8")


def main() -> None:
    ensure_dirs()
    template_class_diagram()
    template_sequence_diagram()
    visitor_class_diagram()
    visitor_sequence_diagram()
    create_project_tree_file()
    text_file_to_image(ROOT / "project-structure.txt", IMAGES / "IntelliJ项目结构目录图.png", "项目结构目录")
    text_file_to_image(ROOT / "run-result.txt", IMAGES / "MainApp运行结果.png", "MainApp 完整运行结果")
    text_file_to_image(ROOT / "run-result.txt", IMAGES / "模板方法模式运行结果.png", "模板方法模式运行结果", max_lines=32)
    text_file_to_image(ROOT / "run-result.txt", IMAGES / "访问者模式运行结果.png", "访问者模式运行结果", max_lines=88)
    text_file_to_image(ROOT / "maven-package-result.txt", IMAGES / "Maven构建成功结果.png", "Maven 构建结果")
    text_file_to_image(ROOT / "git-result.txt", IMAGES / "Git提交成功结果.png", "Git 操作结果")


if __name__ == "__main__":
    main()
