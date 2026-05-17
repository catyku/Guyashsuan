# 古雅軒法律事務所 — 部署說明

## 目錄結構

```
deploy/
├── docker-compose.yaml   # Docker Compose 設定檔
├── init.sql               # 資料庫初始化 SQL
├── .env.example           # 環境變數範本
├── deploy.sh              # 一鍵部署腳本
└── sas.war                # Spring Boot WAR 包 (build 後產生)
```

## 快速部署

### 1. 設定環境變數

```bash
cp .env.example .env
# 編輯 .env 修改資料庫密碼等設定
```

### 2. Build WAR 包

```bash
./deploy.sh build
# 或手動: cd .. && mvn clean package -DskipTests && cp target/sas.war deploy/sas.war
```

### 3. 啟動服務

```bash
./deploy.sh up
```

### 4. 確認服務狀態

```bash
./deploy.sh status
```

### 5. 查看日誌

```bash
./deploy.sh logs           # 所有服務
./deploy.sh logs guyahsuan-app   # 只看應用日誌
./deploy.sh logs guyahsuan-db    # 只看資料庫日誌
```

## 服務位址

| 服務 | URL |
|------|-----|
| 前台首頁 | http://localhost:8092/ |
| 後台管理 | http://localhost:8092/admin/ |
| MariaDB | localhost:3306 |

後台登入：admin / 12345

## 環境變數

| 變數 | 說明 | 預設值 |
|------|------|--------|
| `DB_ROOT_PASSWORD` | MariaDB root 密碼 | tc12834843 |
| `DB_NAME` | 資料庫名稱 | guyahsuan |
| `DB_PORT` | MariaDB 對外埠 | 3306 |
| `APP_PORT` | 應用對外埠 | 8092 |
| `APP_VERSION` | 應用版本標籤 | latest |

## 常用指令

```bash
# 啟動
./deploy.sh up

# 停止
./deploy.sh down

# 重新啟動
./deploy.sh restart

# 重新 build 並部署
./deploy.sh build
./deploy.sh down
./deploy.sh up

# 查看狀態
./deploy.sh status
```

## 資料持久化

- `guyahsuan-db-data` — MariaDB 資料卷
- `guyahsuan-uploads` — 上傳檔案卷

這些 Docker volumes 在 `down` 時不會被刪除。如需完全清除：

```bash
docker compose -f deploy/docker-compose.yaml --project-name guyahsuan down -v
```

## 更新應用

```bash
# 1. 重新 build
cd /home/eric/GitHub/Law/admin
mvn clean package -DskipTests -q
cp target/sas.war deploy/sas.war

# 2. 重啟應用容器
docker compose -f deploy/docker-compose.yaml --project-name guyahsuan restart guyahsuan-app
```

## 架構說明

```
┌─────────────────────────────────────────┐
│          Docker Compose Network          │
│                                         │
│  ┌──────────────┐  ┌─────────────────┐  │
│  │  guyahsuan-db │  │  guyahsuan-app │  │
│  │  MariaDB 10   │  │  Tomcat 11     │  │
│  │  Port: 3306   │  │  JDK 25        │  │
│  │               │  │  Port: 8080    │  │
│  └──────────────┘  └────────┬────────┘  │
│                              │            │
│         ┌────────────────────┘            │
│         │  Host Port: 8092 → 8080        │
└─────────┼────────────────────────────────┘
          │
    http://localhost:8092
```

- **前台** (Thymeleaf): `http://localhost:8092/`
- **後台** (Vue 3 + Element Plus): `http://localhost:8092/admin/`
- **API**: `http://localhost:8092/api/*`
- **公開諮詢 API**: `http://localhost:8092/api/consultation/public`