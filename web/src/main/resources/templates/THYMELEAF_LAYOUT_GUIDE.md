# Thymeleaf 布局整合说明

## 概述
本项目已使用 Spring Boot Thymeleaf Layout Dialect 将共用部分提取到 `fragments/layout.html`，便于管理和维护。

## 文件结构

```
templates/
├── fragments/
│   ├── layout.html       # 主布局模板（包含 meta、header、footer、JS）
│   ├── header.html       # 导航菜单 fragment
│   ├── footer.html       # 页脚 fragment
│   └── page-title.html   # 页面标题 fragment（可选）
├── attorney-new.html     # 示例：律师页面（使用新布局）
├── index-new.html        # 示例：首页（使用新布局）
└── ... （其他页面）
```

## 使用方法

### 1. 需要的依赖（pom.xml）

```xml
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
    <version>3.2.1</version>
</dependency>
```

### 2. 创建新页面（以 attorney.html 为例）

```html
<!DOCTYPE html>
<html lang="zh-TW" 
      xmlns:th="http://www.thymeleaf.org" 
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout" 
      layout:decorate="~{fragments/layout}">

<head>
    <title>页面标题</title>
    <!-- 如需要额外的 CSS，可在此添加 -->
    <th:block layout:fragment="extra-css">
        <link rel="stylesheet" href="custom.css">
    </th:block>
</head>

<body>

<th:block layout:fragment="content">
    <!-- 这里放入页面内容，会替换 layout.html 中的 content 区域 -->
    <section>
        <!-- 页面特定内容 -->
    </section>
</th:block>

<!-- 如需要额外的 JS，可在此添加 -->
<th:block layout:fragment="extra-js">
    <script src="custom.js"></script>
</th:block>

</body>

</html>
```

## Fragment 说明

### fragments/layout.html
主布局模板，包含：
- HTML 文档声明和基本结构
- `<head>` 标签中的所有 meta、title、CSS 链接
- `<body>` 中的 header、footer、scroll-to-top 和所有通用 JS
- 三个可自定义的部分：
  - `content` - 页面主要内容（必填）
  - `extra-css` - 额外的 CSS 资源（可选）
  - `extra-js` - 额外的 JS 资源（可选）

### fragments/header.html
导航菜单 fragment，支持动态参数：
- `${headerStyle}` - header 样式（'1' 或 '2'，默认 '2'）
- `${headerMenuClass}` - 菜单区域类（默认 'menu_area-light'）
- `${searchBg}` - 搜索框背景类（默认 'bg-secondary'）
- `${togglerBg}` - 移动菜单按钮背景类（默认为空）

**示例用法：**
```html
<!-- 在 Controller 中设置模型属性 -->
model.addAttribute("headerStyle", "1");
model.addAttribute("searchBg", "bg-primary");
```

### fragments/footer.html
页脚信息，包含公司信息、联系方式和导航链接。

### fragments/page-title.html
页面标题和面包屑导航 fragment（可选）。

## 迁移现有页面

1. 将原 HTML 文件作为备份保存
2. 使用 `layout:decorate="~{fragments/layout}"` 声明使用布局
3. 将页面内容包装在 `<th:block layout:fragment="content">` 内
4. 删除重复的 header、footer 和通用 JS 代码

## 常见使用案例

### 案例 1：简单页面（带标题和面包屑）
```html
<th:block layout:fragment="content">
    <!-- 页面标题部分 -->
    <section class="top-position1 pt-0" style="background: #e3e4e5;padding-bottom: 70px;">
        <!-- 标题内容 -->
    </section>
    
    <!-- 页面主要内容 -->
    <section>
        <!-- 内容 -->
    </section>
</th:block>
```

### 案例 2：需要额外样式和脚本的页面
```html
<th:block layout:fragment="extra-css">
    <link rel="stylesheet" href="css/gallery.css">
</th:block>

<th:block layout:fragment="content">
    <!-- 页面内容 -->
</th:block>

<th:block layout:fragment="extra-js">
    <script src="js/gallery.js"></script>
</th:block>
```

## 技术细节

- **Thymeleaf 版本**：3.1+
- **Layout Dialect 版本**：3.2+
- **Spring Boot**：2.6+
- **命名空间**：`xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"`

## 参考资源

- [Thymeleaf Layout Dialect](https://github.com/ultraq/thymeleaf-layout-dialect)
- [Thymeleaf 官方文档](https://www.thymeleaf.org/documentation.html)
- [Spring Boot Thymeleaf 集成](https://spring.io/guides/gs/serving-web-content/)
