# Thymeleaf 布局快速参考

## 最小页面模板

```html
<!DOCTYPE html>
<html lang="zh-TW" 
      xmlns:th="http://www.thymeleaf.org" 
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout" 
      layout:decorate="~{fragments/layout}">

<head>
    <title>页面标题</title>
</head>

<body>

<th:block layout:fragment="content">
    <!-- 页面内容在这里 -->
</th:block>

</body>

</html>
```

## 常用 Fragment 语法

### 替换 Fragment
```html
<!-- 直接引用 fragment -->
<th:block th:replace="fragments/header :: header"></th:block>

<!-- 带参数的 fragment -->
<th:block th:replace="fragments/header :: header(headerStyle='1')"></th:block>
```

### 定义自定义 Fragment
```html
<!-- 在模板中定义 -->
<div th:fragment="my-component(title, content)">
    <h3 th:text="${title}">Title</h3>
    <p th:text="${content}">Content</p>
</div>
```

### 使用自定义 Fragment
```html
<th:block th:replace="~{path/to/template :: my-component('标题', '内容')}"></th:block>
```

## Layout Fragment 清单

### 主布局：layout.html
```html
<th:block layout:fragment="content"></th:block>      <!-- 页面主要内容 -->
<th:block layout:fragment="extra-css"></th:block>    <!-- 额外 CSS -->
<th:block layout:fragment="extra-js"></th:block>     <!-- 额外 JS -->
```

### Header Fragment
参数：
- `${headerStyle}` - '1' 或 '2'（默认 '2'）
- `${searchBg}` - 'bg-primary' 或 'bg-secondary'（默认 'bg-secondary'）
- `${togglerBg}` - 按钮背景类

### Footer Fragment
无参数，固定内容。

## Spring Boot 控制器模板

```java
@Controller
@RequestMapping("/pages")
public class PageController {

    @GetMapping("/attorney")
    public String attorney(Model model) {
        model.addAttribute("pageTitle", "律师页面");
        model.addAttribute("headerStyle", "1");
        model.addAttribute("searchBg", "bg-primary");
        return "attorney";
    }
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "首页");
        return "index";  // 使用默认设置
    }
}
```

## 文件路径映射

```
模板文件                           → 访问 URL
templates/index.html             → GET /
templates/attorney.html          → GET /attorney
templates/service.html           → GET /service
templates/case.html              → GET /case
templates/share.html             → GET /share
templates/consultation.html      → GET /consultation
templates/about.html             → GET /about
```

## 常见任务速查

### 修改导航菜单
编辑：`src/main/resources/templates/fragments/header.html`

### 修改页脚信息
编辑：`src/main/resources/templates/fragments/footer.html`

### 添加新的全局样式表
在 `fragments/layout.html` 的 `<head>` 中添加：
```html
<link rel="stylesheet" href="css/new-style.css">
```

### 添加新的全局脚本
在 `fragments/layout.html` 的底部添加：
```html
<script src="js/new-script.js"></script>
```

### 在特定页面添加额外资源
```html
<th:block layout:fragment="extra-css">
    <link rel="stylesheet" href="css/gallery.css">
</th:block>

<th:block layout:fragment="extra-js">
    <script src="js/gallery.js"></script>
</th:block>
```

## 变量/属性传递示例

### 从 Controller 传递
```java
model.addAttribute("lawyers", lawyerList);
model.addAttribute("companyName", "古雅軒法律事務所");
```

### 在模板中使用
```html
<h2 th:text="${companyName}">Company</h2>
<div th:each="lawyer : ${lawyers}">
    <p th:text="${lawyer.name}">Name</p>
</div>
```

## 条件渲染

```html
<!-- if 条件 -->
<div th:if="${user != null}">
    <p>Welcome <span th:text="${user.name}">User</span></p>
</div>

<!-- unless 条件（反向 if） -->
<div th:unless="${user == null}">
    <p>Logged in</p>
</div>

<!-- switch-case -->
<div th:switch="${user.role}">
    <p th:case="'admin'">管理员</p>
    <p th:case="'user'">用户</p>
    <p th:case="*">未知角色</p>
</div>
```

## 迭代/循环

```html
<!-- 遍历列表 -->
<ul>
    <li th:each="item : ${items}" th:text="${item}">Item</li>
</ul>

<!-- 带索引和条件 -->
<div th:each="item,iter : ${items}">
    <span th:if="${iter.odd}">奇数行</span>
    <span th:text="${item}">Item</span>
</div>
```

## URL 生成

```html
<!-- 绝对路径 -->
<a th:href="@{/attorney}">律师</a>

<!-- 带参数 -->
<a th:href="@{/case/{id}(id=${caseId})}">案件详情</a>

<!-- 外部链接 -->
<a th:href="${website}">网站</a>
```

## 属性绑定

```html
<!-- class 绑定 -->
<div th:class="${isActive} ? 'active' : 'inactive'">Content</div>

<!-- style 绑定 -->
<div th:style="${'color: ' + textColor}">Text</div>

<!-- 通用属性 -->
<img th:src="${imagePath}" th:alt="${imageAlt}">
<input th:value="${defaultValue}">
```

## 文本处理

```html
<!-- 原始文本（不转义） -->
<div th:utext="${htmlContent}">HTML Content</div>

<!-- 国际化（i18n） -->
<p th:text="#{message.key}">Default message</p>

<!-- 字符串操作 -->
<p th:text="${#strings.toUpperCase(name)}">NAME</p>
<p th:text="${#strings.startsWith(url, 'https')}">True/False</p>
```

## 常见错误排查

| 错误 | 原因 | 解决方案 |
|------|------|---------|
| 404 Not Found | 模板文件不存在 | 检查文件路径和命名 |
| Expression parsing error | Thymeleaf 表达式错误 | 检查 `${}` 语法 |
| Fragment not found | Fragment 引用错误 | 确保 `th:fragment` 存在且路径正确 |
| 缓存问题 | 模板被缓存 | 设置 `spring.thymeleaf.cache=false` |

## 实用链接

- [Thymeleaf 标准表达式语法](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html#standard-expression-syntax)
- [Thymeleaf 内置对象和方法](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html#expression-utility-objects)
- [Layout Dialect GitHub](https://github.com/ultraq/thymeleaf-layout-dialect)
