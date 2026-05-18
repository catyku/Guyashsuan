# Thymeleaf 布局重构总结

## 项目改动总结

本次重构将古雅軒法律事務所网站的所有页面共用部分提取到 Thymeleaf fragments 中，提高了代码的可维护性和可重用性。

## 创建的文件

### 1. Fragments（共用模板）
位置：`src/main/resources/templates/fragments/`

| 文件名 | 说明 | 用途 |
|-------|------|------|
| **layout.html** | 主布局模板 | 包含 HTML 文档结构、meta 标签、CSS/JS 引入、header/footer |
| **header.html** | 导航头部 | 提取的导航菜单，支持参数化配置 |
| **footer.html** | 页脚 | 提取的页脚信息、联系方式、导航链接 |
| **page-title.html** | 页面标题 | 可选的页面标题和面包屑导航 |

### 2. 示例页面（供参考）
位置：`src/main/resources/templates/`

| 文件名 | 说明 |
|-------|------|
| **attorney-new.html** | 律师页面示例 |
| **index-new.html** | 首页示例 |

### 3. 文档文件
| 文件名 | 说明 |
|-------|------|
| **THYMELEAF_LAYOUT_GUIDE.md** | 详细的使用指南和技术说明 |
| **MAVEN_SETUP.md** | Maven 依赖和配置说明 |

## 修改的文件

### pom.xml
添加了 `thymeleaf-layout-dialect` 依赖：
```xml
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
    <version>3.2.1</version>
</dependency>
```

## 关键改进

### 1. 消除代码重复
- **原来**：每个页面都包含完整的 HTML 文档声明、meta 标签、CSS/JS 链接
- **现在**：所有共用部分在 `layout.html` 中集中管理

### 2. 提高可维护性
- 修改 header 或 footer 只需改一个文件
- 添加新的 CSS/JS 库只需在 layout 中修改
- 统一的 HTML 结构规范

### 3. 灵活的参数化配置
Header 支持动态参数：
- `${headerStyle}` - 控制 header 样式
- `${searchBg}` - 控制搜索框背景色
- `${togglerBg}` - 控制移动菜单按钮样式

## 使用流程

### 第一步：更新 pom.xml 依赖
已完成，无需额外操作。

### 第二步：配置 application.properties
添加以下配置：
```properties
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.cache=false
```

### 第三步：创建 Spring Boot 控制器
参考 `MAVEN_SETUP.md` 中的控制器示例。

### 第四步：将现有页面改写为使用新布局
参考 `attorney-new.html` 和 `index-new.html` 的结构。

## 文件对比

### 改写前（attorney.html）
```html
<!DOCTYPE html>
<html lang="zh-TW">
<head>
    <!-- 重复的 meta 标签 -->
    <!-- 重复的 CSS 链接 -->
</head>
<body>
    <div id="preloader"></div>
    <div class="main-wrapper">
        <!-- 重复的 header 代码 -->
        <!-- 页面特定内容 -->
        <!-- 重复的 footer 代码 -->
    </div>
    <!-- 重复的 JS 脚本 -->
</body>
</html>
```
**文件大小**: ~350 行

### 改写后（attorney-new.html）
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
    <!-- 仅包含页面特定内容 -->
</th:block>
</body>
</html>
```
**文件大小**: ~100 行（减少 ~70%）

## 下一步

1. **逐步迁移现有页面**
   - 备份原 HTML 文件
   - 参考新建的示例页面改写
   - 测试功能正常

2. **建立命名规范**
   - 使用 `*-new.html` 作为新版本
   - 旧版本保留作为参考

3. **完整迁移后**
   - 删除所有 `-new.html` 文件的后缀
   - 删除原始的 HTML 文件
   - 清理 Git 历史

## 技术栈

- **Spring Boot**: 4.0.6
- **Thymeleaf**: 3.1+
- **Thymeleaf Layout Dialect**: 3.2.1
- **Java**: 25

## 常见问题

### Q: 为什么某些页面的 header 样式不同？
A: 通过在控制器中设置不同的模型属性来实现：
```java
model.addAttribute("headerStyle", "1");
model.addAttribute("searchBg", "bg-primary");
```

### Q: 如何在新页面中添加自定义 CSS/JS？
A: 使用 `layout:fragment` 属性：
```html
<th:block layout:fragment="extra-css">
    <link rel="stylesheet" href="custom.css">
</th:block>
```

### Q: 模板缓存问题如何处理？
A: 在开发环境中设置 `spring.thymeleaf.cache=false`，生产环境改为 `true`。

## 参考资源

- [Thymeleaf 官方文档](https://www.thymeleaf.org/)
- [Thymeleaf Layout Dialect 文档](https://ultraq.github.io/thymeleaf-layout-dialect/)
- [Spring Boot 集成 Thymeleaf](https://spring.io/guides/gs/serving-web-content/)

---

**创建时间**: 2026-05-18
**改革者**: GitHub Copilot
**状态**: 就绪测试
