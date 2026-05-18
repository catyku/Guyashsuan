# 迁移现有页面步骤指南

## 概述
本指南说明如何将现有的静态 HTML 页面转换为使用新的 Thymeleaf 布局系统。

## 准备工作

### 1. 检查依赖
确保 `pom.xml` 中已添加 `thymeleaf-layout-dialect` 依赖。

```bash
# 更新依赖
mvn clean install
```

### 2. 配置应用程序
在 `application.properties` 中添加（若未添加）：

```properties
# Thymeleaf 配置
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.servlet.content-type=text/html
spring.thymeleaf.cache=false
```

## 迁移步骤

### 第 1 步：备份原文件
```bash
# 为原文件创建备份
cp src/main/resources/templates/attorney.html src/main/resources/templates/attorney.html.bak
```

### 第 2 步：创建新页面文件

参考 `attorney-new.html` 的结构，创建新文件。基本模板：

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

### 第 3 步：提取页面内容

从原文件中：
1. 复制 `<title>` 标签内容到新文件的 `<title>` 中
2. 删除所有 `<meta>`, `<link>`, `<script>` 标签（已在 layout 中）
3. 删除 `<header>` 部分（由 layout 提供）
4. 删除 `<footer>` 部分（由 layout 提供）
5. 保留 `<main-wrapper>` 内部的所有内容

### 第 4 步：特殊处理

#### 情况 1：需要不同的 Header 样式
```html
<th:block layout:fragment="content">
    <!-- 页面内容 -->
</th:block>
```

在对应的 Controller 中设置：
```java
model.addAttribute("headerStyle", "1");
model.addAttribute("searchBg", "bg-primary");
```

#### 情况 2：需要自定义 CSS
```html
<th:block layout:fragment="extra-css">
    <link rel="stylesheet" href="css/custom.css">
</th:block>

<th:block layout:fragment="content">
    <!-- 页面内容 -->
</th:block>
```

#### 情况 3：需要自定义 JS
```html
<th:block layout:fragment="content">
    <!-- 页面内容 -->
</th:block>

<th:block layout:fragment="extra-js">
    <script src="js/custom.js"></script>
</th:block>
```

### 第 5 步：更新链接

如果页面中有相对路径链接，确保指向正确的页面：

```html
<!-- 错误：使用 .html 后缀 -->
<a href="attorney.html">律师</a>

<!-- 正确：使用 Thymeleaf 路由 -->
<a th:href="@{/attorney}">律师</a>
```

### 第 6 步：创建 Controller

为每个页面创建对应的 Controller：

```java
@Controller
public class PageController {

    @GetMapping("/attorney")
    public String attorney(Model model) {
        model.addAttribute("pageTitle", "GUYAHSUAN Law Office | 古雅軒法律事務所 | Attorneys, etc. 律師等");
        model.addAttribute("headerStyle", "1");
        model.addAttribute("searchBg", "bg-primary");
        return "attorney";
    }

    @GetMapping("/service")
    public String service(Model model) {
        model.addAttribute("pageTitle", "GUYAHSUAN Law Office | 古雅軒法律事務所 | Business Areas 業務領域");
        return "service";
    }

    @GetMapping("/case")
    public String caseList(Model model) {
        model.addAttribute("pageTitle", "GUYAHSUAN Law Office | 古雅軒法律事務所 | Sucessful Case 案件實績");
        return "case";
    }

    @GetMapping("/share")
    public String share(Model model) {
        model.addAttribute("pageTitle", "GUYAHSUAN Law Office | 古雅軒法律事務所 | Intelligence Sharing 情報分享");
        return "share";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "GUYAHSUAN Law Office | 古雅軒法律事務所 | Office Introduction 事務所概要");
        return "about";
    }

    @GetMapping("/consultation")
    public String consultation(Model model) {
        model.addAttribute("pageTitle", "GUYAHSUAN Law Office | 古雅軒法律事務所 | Free Legal Counsel 免費法律諮詢");
        return "consultation";
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "GUYAHSUAN Law Office | 古雅軒法律事務所");
        model.addAttribute("headerStyle", "2");
        return "index";
    }

    @GetMapping("/404")
    public String notFound() {
        return "404-page";
    }
}
```

## 迁移检查清单

### 代码检查
- [ ] 移除了所有 `<meta>` 标签（除了 title）
- [ ] 移除了所有 `<link>` CSS 标签
- [ ] 移除了所有 `<script>` JS 标签
- [ ] 移除了 `<header>` 部分
- [ ] 移除了 `<footer>` 部分
- [ ] 添加了 `layout:decorate="~{fragments/layout}"`
- [ ] 页面内容包装在 `<th:block layout:fragment="content">` 中
- [ ] 添加了必要的 Thymeleaf 命名空间

### 功能检查
- [ ] 页面显示正确
- [ ] 导航菜单工作正常
- [ ] 页脚显示正确
- [ ] 所有链接都能正常跳转
- [ ] 样式表正确加载
- [ ] JavaScript 脚本正确执行
- [ ] 搜索功能工作正常
- [ ] 响应式设计正常

### 浏览器兼容性
- [ ] Chrome / Edge
- [ ] Firefox
- [ ] Safari
- [ ] 移动设备

## 常见问题排查

### 问题 1：页面显示没有样式
**原因**：CSS 路径错误或缓存问题

**解决方案**：
```bash
# 清除 Spring Boot 缓存
# 删除 target 目录
mvn clean

# 检查 CSS 路径
<link href="css/styles.css" rel="stylesheet">
```

### 问题 2：Fragment not found 错误
**原因**：Fragment 引用路径错误

**解决方案**：
```html
<!-- 确保路径正确 -->
<th:block th:replace="fragments/header :: header"></th:block>

<!-- 检查文件确实存在 -->
<!-- src/main/resources/templates/fragments/header.html -->
```

### 问题 3：模板缓存导致更改未显示
**原因**：Thymeleaf 缓存启用

**解决方案**：
```properties
# application.properties
spring.thymeleaf.cache=false
```

### 问题 4：动态参数不生效
**原因**：Controller 中未设置模型属性

**解决方案**：
```java
// 确保在 Controller 中添加了属性
model.addAttribute("headerStyle", "1");
model.addAttribute("searchBg", "bg-primary");
```

## 迁移优先级

建议按以下优先级迁移页面：

1. **第一批**（基础结构相同）
   - `attorney.html` ✓
   - `service.html`
   - `case.html`

2. **第二批**（带特殊内容）
   - `about.html`
   - `consultation.html`
   - `share.html`

3. **第三批**（特殊页面）
   - `index.html` ✓
   - `404-page.html`
   - `case-detailed.html`
   - `share-detailed.html`

## 完整迁移后

1. 删除所有 `.bak` 备份文件
2. 删除示例文件（`attorney-new.html`, `index-new.html`）
3. 在 `pom.xml` 中调整 Thymeleaf 缓存：
   ```properties
   spring.thymeleaf.cache=true
   ```
4. 运行全面测试

## 快速迁移脚本（可选）

创建一个 `migrate.sh` 脚本自动化迁移：

```bash
#!/bin/bash

# 迁移页面列表
pages=("attorney" "service" "case" "about" "consultation" "share")

for page in "${pages[@]}"; do
    # 备份原文件
    cp "src/main/resources/templates/${page}.html" \
       "src/main/resources/templates/${page}.html.bak"
    
    echo "已备份 ${page}.html"
done

echo "迁移准备完成"
```

## 验证迁移

测试所有页面：

```bash
# 启动应用
mvn spring-boot:run

# 测试路由
curl http://localhost:8080/
curl http://localhost:8080/attorney
curl http://localhost:8080/service
# ... 其他页面
```

## 获取帮助

- 查看 `THYMELEAF_LAYOUT_GUIDE.md` 了解详细用法
- 查看 `QUICK_REFERENCE.md` 获取快速语法参考
- 参考 `attorney-new.html` 和 `index-new.html` 的示例实现
