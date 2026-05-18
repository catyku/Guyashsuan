# 需要添加的 Maven 依赖

将以下内容添加到项目的 `pom.xml` 中的 `<dependencies>` 部分：

```xml
<!-- Spring Boot Thymeleaf -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Thymeleaf Layout Dialect (用于布局管理) -->
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
    <version>3.2.1</version>
</dependency>
```

## 配置 (application.properties / application.yml)

### application.properties
```properties
# Thymeleaf 配置
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.servlet.content-type=text/html
spring.thymeleaf.cache=false # 开发环境禁用缓存，方便调试
```

### application.yml
```yaml
spring:
  thymeleaf:
    mode: HTML
    encoding: UTF-8
    servlet:
      content-type: text/html
    cache: false  # 开发环境禁用缓存，方便调试
```

## Spring Boot 控制器示例

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

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "GUYAHSUAN Law Office | 古雅軒法律事務所");
        model.addAttribute("headerStyle", "2");
        model.addAttribute("searchBg", "bg-secondary");
        return "index";
    }

    @GetMapping("/service")
    public String service(Model model) {
        model.addAttribute("pageTitle", "GUYAHSUAN Law Office | 古雅軒法律事務所 | Business Areas 業務領域");
        return "service";
    }
}
```

## 目录结构

Thymeleaf 模板应该位于以下目录：
```
src/main/resources/
└── templates/
    ├── fragments/
    │   ├── layout.html
    │   ├── header.html
    │   ├── footer.html
    │   └── page-title.html
    ├── attorney.html
    ├── index.html
    ├── service.html
    └── ... （其他页面）
```

## 静态资源

静态资源（CSS、JS、图片）应该位于：
```
src/main/resources/
└── static/
    ├── css/
    ├── js/
    ├── img/
    └── fonts/
```

在 HTML 中引用时使用相对路径：
```html
<link href="css/styles.css" rel="stylesheet">
<script src="js/jquery.min.js"></script>
<img src="img/logos/logo.png" alt="logo">
```

## 注意事项

1. **命名空间**：确保在 HTML 标签中添加 Thymeleaf 命名空间：
   ```html
   xmlns:th="http://www.thymeleaf.org"
   xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
   ```

2. **Fragment 引用**：使用正确的路径前缀 `~{fragments/...}`

3. **开发调试**：在 `application.properties` 中设置 `spring.thymeleaf.cache=false`，这样更改模板后无需重启应用

4. **生产环境**：在生产环境中将 `spring.thymeleaf.cache=true` 以提高性能
