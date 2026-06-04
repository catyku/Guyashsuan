# CWE 安全漏洞修復 Migration Result

> **Executive Summary**\
> 成功修復 Guyashsuan 法律事務所後台管理系統中的 9 項 CWE 安全漏洞（CWE-22/23/36/434/682/732/772/775/778）。所有修復均通過建置驗證與單元測試，無新增 CVE，程式碼行為保持一致。

---

## 1. Migration Improvements

成功修復 Java Spring Boot 應用中所有指定的 CWE 安全漏洞。主要改善涵蓋路徑穿越防護、檔案上傳驗證、整數溢位計算、API 存取控制與安全稽核日誌。

| Area | Before | After | Improvement |
|------|--------|-------|-------------|
| 路徑穿越 (CWE-22/23/36) | 未驗證 subDir 及 relativePath 可直接穿越目錄 | 路徑正規化 + `startsWith(resolvedRoot)` 雙重驗證 | 防止攻擊者讀取/寫入上傳目錄以外的檔案 |
| 檔案上傳 (CWE-434) | 僅檢查 Content-Type 標頭（可偽造），允許 SVG | Magic bytes 驗證 + 副檔名白名單（已移除 SVG） | 防止偽造副檔名與 SVG XSS 注入攻擊 |
| 資源洩漏 (CWE-772/775) | 縮圖時 `InputStream` 未以 try-with-resources 包覆 | 改用 `try (InputStream is = ...)` 確保一定關閉 | 消除 InputStream 洩漏風險 |
| 整數溢位 (CWE-682) | `int offset = (page-1) * size`（大頁碼時溢位） | `long offset = Math.max((long)(page-1) * size, 0L)` | 防止分頁計算產生負偏移導致非預期查詢結果 |
| 存取控制 (CWE-732) | `/api/dev/**` 允許所有人存取 | 移除此規則，預設需驗證身份 | 消除未授權使用者可呼叫開發 API 的風險 |
| 安全日誌 (CWE-778) | 管理員建立/修改/刪除無稽核日誌；上傳無日誌 | 所有敏感操作加入 `logger.info/warn` + 獨立 security.log | 符合安全稽核要求，可追蹤攻擊行為 |

---

## 2. Build and Validation

所有來源檔案以 Java 25 + Spring Boot 4.0.6 成功編譯。單元測試全數通過，無功能性回歸。

### Build Validation

| Field | Value |
|-------|-------|
| Status | ✅ Success |
| Build Tool | Maven (D:\netbeans\java\maven) |
| JDK | Java 25 (D:\jdk-25+36) |
| Result | 首次建置即成功，無需額外修正 |

### Test Validation

| Field | Value |
|-------|-------|
| Status | ✅ Success |
| Total Tests | All passed |
| Failed | 0 |
| Test Framework | JUnit (Spring Boot Test) |

### Code Quality Validation

| Check | Status | Details |
|-------|--------|---------|
| CVE Scan | ✅ Success | 修復後無新增 CVE；所有依賴均通過掃描 |
| Consistency Check | ✅ Success | Critical: 0, Major: 0, Minor: 1（`alt` 欄位從原始檔名改為空字串，屬設計決策） |
| Completeness Check | ✅ Success | 完整性掃描發現 CaseService、ShareService 及其 Repository 遺漏的 CWE-682，已全部修復；最終剩餘問題: 0 |

---

## 3. Recommended Next Steps

I. **Code Review**: 建立 Pull Request，將 `modernize/java-20260604142935` 合併回 `main`，讓團隊成員審查安全修復。

II. **整合測試**: 在 staging 環境部署後，手動測試圖片上傳功能，確認 magic bytes 驗證不影響正常圖片上傳流程。

III. **監控安全日誌**: 部署後觀察 `logs/security.log`，確認 `[SECURITY]`、`[UPLOAD]`、`[ADMIN]`、`[DELETE]` 標籤正常寫入。

IV. **確認 `/api/dev/**` 端點**: 確認開發環境中是否有用到 `/api/dev/**` 路由，若有需求應改用需驗證的方式或獨立開發 profile。

V. **定期安全掃描**: 建議加入 OWASP Dependency-Check 到 CI/CD 流程，持續監控新增 CVE。

---

## 4. Additional Details

<details><summary>Click to expand for migration details</summary>

### Project Details

| Field | Value |
|-------|-------|
| Session ID | `7b635c4b-fc56-44d8-8285-40466b75b3f2` |
| Migration executed by | CR |
| Migration performed by | GitHub Copilot |
| Project Pathname | d:\GitHub\Guyashsuan |
| Language | Java |
| Files modified | 12 |
| Branch created | `modernize/java-20260604142935` |

### Version Control Summary

| Field | Value |
|-------|-------|
| Version Control System | Git |
| Total Commits | 2 |
| Uncommitted Changes | None |

**Commits:**
1. `645c18d` — Code migration: Fix CWE-22/23/36/434/682/732/772/775/778 security vulnerabilities
2. `aec29fca` — Completeness fixes: CWE-682 long offset in ShareService, CaseService and their repositories

### Code Changes

**Service Files (2)**
- web/src/main/java/com/law/admin/service/FileUploadService.java — 路徑穿越防護、magic bytes 驗證、try-with-resources、安全日誌
- web/src/main/java/com/law/admin/service/CaseService.java — CWE-682 long offset
- web/src/main/java/com/law/admin/service/ShareService.java — CWE-682 long offset

**Controller Files (5)**
- web/src/main/java/com/law/admin/controller/UploadController.java — subDir 白名單、安全日誌
- web/src/main/java/com/law/admin/controller/CaseController.java — CWE-682 long offset
- web/src/main/java/com/law/admin/controller/ShareController.java — CWE-682 long offset
- web/src/main/java/com/law/admin/controller/ConsultationController.java — CWE-682 long offset
- web/src/main/java/com/law/admin/controller/ServiceController.java — CWE-682 long offset
- web/src/main/java/com/law/admin/controller/AdminUserController.java — CWE-778 管理員稽核日誌

**Repository Files (2)**
- web/src/main/java/com/law/admin/repository/CaseRepository.java — `int offset` → `long offset` 參數型別
- web/src/main/java/com/law/admin/repository/ShareRepository.java — `int offset` → `long offset` 參數型別

**Config Files (1)**
- web/src/main/java/com/law/admin/config/SecurityConfig.java — 移除 `/api/dev/**` 公開存取

**Resources (1)**
- web/src/main/resources/logback-spring.xml — 新增 SECURITY_FILE 獨立安全稽核日誌 appender

### Dependency Changes

**Removed:**
- 無移除依賴

**Added:**
- 無新增依賴（所有修復均使用 JDK 標準庫與現有框架）

### Tasks

- 修復 CWE-22/23/36 路徑穿越漏洞（FileUploadService、UploadController）
- 修復 CWE-434 不受限制檔案上傳（magic bytes 驗證、移除 SVG 支援）
- 修復 CWE-772/775 資源洩漏（try-with-resources）
- 修復 CWE-682 整數溢位（6 個 Controller/Service 的 pagination offset）
- 修復 CWE-732 不正確權限設定（移除 /api/dev/** 公開存取）
- 修復 CWE-778 日誌不足（管理員操作、上傳/刪除操作、安全稽核日誌）
- 完整性掃描：發現並修復 CaseService、ShareService 及其 Repository 的遺漏 CWE-682

### Knowledge Base Applied

0 個外部 KB 規則應用（此為直接 CWE 安全修復，無技術遷移 KB）。

| Migration Area | Description |
|----------------|-------------|
| CWE-22/23/36 路徑穿越 | Path.normalize() + startsWith(resolvedRoot) 雙重驗證 |
| CWE-434 檔案上傳 | Magic bytes 驗證、副檔名白名單、移除 SVG |
| CWE-682 整數計算 | long 型別 pagination offset 計算 |
| CWE-732 存取控制 | 移除未授權公開 API 路由 |
| CWE-772/775 資源洩漏 | try-with-resources 確保 InputStream 關閉 |
| CWE-778 日誌 | SLF4J 安全稽核日誌 + 獨立 security.log |

### Issues Fixed During Migration

| Severity | Issue | Resolution |
|----------|-------|------------|
| Critical | CWE-22/23/36: 路徑穿越攻擊可讀寫任意目錄 | 路徑正規化 + 邊界驗證 |
| Critical | CWE-434: 偽造副檔名可上傳惡意檔案 | Magic bytes 驗證 + SVG 禁止 |
| Major | CWE-682: 大頁碼整數溢位導致負 OFFSET | long 運算取代 int |
| Major | CWE-732: /api/dev/** 任何人可存取 | 移除 permitAll 規則 |
| Major | CWE-772/775: InputStream 未關閉 | try-with-resources |
| Minor | CWE-778: 管理員/上傳操作無稽核日誌 | 新增 logger.info/warn 及 security.log |
| Minor | 完整性: ShareService/CaseService 遺漏 CWE-682 修復 | long offset + Repository 型別同步 |

</details>
