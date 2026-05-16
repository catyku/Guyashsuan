-- ============================================================
-- 古雅軒法律事務所 (GUYAHSUAN Law Office) — 資料庫初始化腳本
-- Database: guyahsuan
-- Charset: utf8mb4 / Collation: utf8mb4_unicode_ci
-- ============================================================

CREATE DATABASE IF NOT EXISTS guyahsuan
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE guyahsuan;

-- ==================== 管理員 ====================
CREATE TABLE IF NOT EXISTS lw_admin (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    display_name VARCHAR(50),
    role        VARCHAR(20)  DEFAULT 'ADMIN',
    is_enabled  CHAR(1)      DEFAULT 'Y',
    inptime     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updid       VARCHAR(50),
    updtime     DATETIME
);

-- ==================== 律師 ====================
CREATE TABLE IF NOT EXISTS lw_attorney (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    title       VARCHAR(50),
    license_no  VARCHAR(50),
    photo       VARCHAR(255),
    specialty   TEXT,
    education   TEXT,
    experience  TEXT,
    description TEXT,
    sort_order  INT          DEFAULT 0,
    is_show     CHAR(1)      DEFAULT 'Y',
    inptime     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updid       VARCHAR(50),
    updtime     DATETIME
);

-- ==================== 業務領域 ====================
CREATE TABLE IF NOT EXISTS lw_service (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    name_en     VARCHAR(100),
    icon        VARCHAR(100),
    description TEXT,
    sort_order  INT          DEFAULT 0,
    is_show     CHAR(1)      DEFAULT 'Y',
    inptime     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updid       VARCHAR(50),
    updtime     DATETIME
);

-- ==================== 案件實績 ====================
CREATE TABLE IF NOT EXISTS lw_case (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    category    VARCHAR(50)  NOT NULL,
    title       VARCHAR(255) NOT NULL,
    content     TEXT,
    case_date   DATE,
    image       VARCHAR(255),
    is_show     CHAR(1)      DEFAULT 'Y',
    inptime     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updid       VARCHAR(50),
    updtime     DATETIME
);

-- ==================== 情報分享 ====================
CREATE TABLE IF NOT EXISTS lw_share (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    content     TEXT,
    share_date  DATE,
    image       VARCHAR(255),
    is_show     CHAR(1)      DEFAULT 'Y',
    inptime     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updid       VARCHAR(50),
    updtime     DATETIME
);

-- ==================== 法律諮詢 ====================
CREATE TABLE IF NOT EXISTS lw_consultation (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(255),
    subject     VARCHAR(255),
    content     TEXT,
    status      CHAR(1)      DEFAULT 'P',
    reply       TEXT,
    inptime     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updid       VARCHAR(50),
    updtime     DATETIME
);

-- ==================== 網站設定 ====================
CREATE TABLE IF NOT EXISTS lw_site (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    site_key    VARCHAR(50)  NOT NULL UNIQUE,
    site_value  TEXT,
    remark      VARCHAR(255),
    updtime     DATETIME
);

-- ==================== 輪播圖 ====================
CREATE TABLE IF NOT EXISTS lw_banner (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255),
    subtitle    TEXT,
    image       VARCHAR(255) NOT NULL,
    link_url    VARCHAR(500),
    sort_order  INT          DEFAULT 0,
    is_show     CHAR(1)      DEFAULT 'Y',
    inptime     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updid       VARCHAR(50),
    updtime     DATETIME
);

-- ============================================================
-- 種子資料 (Seed Data)
-- ============================================================

-- 管理員帳號 (密碼: 12345, BCrypt 加密)
INSERT INTO lw_admin (username, password, display_name, role, is_enabled) VALUES
('admin', '$2a$10$yDr4o9yc/uPJ6Y.de9gOXuP0jCnUk/0BEnk82AhRvBeDLqbI/Yqfi', '管理員', 'ADMIN', 'Y');

-- 律師
INSERT INTO lw_attorney (name, title, specialty, sort_order, is_show, updid) VALUES
('黃煦詮', '所長', '刑事辯護、民事訴訟、非訟事件', 1, 'Y', 'system'),
('鄭文朋', '律師', '民事訴訟、債務清理、法律顧問', 2, 'Y', 'system'),
('陳庭浩', '律師', '刑事辯護、調解談判、撰狀擬約', 3, 'Y', 'system');

-- 業務領域
INSERT INTO lw_service (name, name_en, icon, description, sort_order, is_show, updid) VALUES
('各類訴訟', 'Litigation', 'fas fa-balance-scale', '民事、刑事、行政訴訟', 1, 'Y', 'system'),
('預防諮詢', 'Preventive Counseling', 'fas fa-shield-alt', '法律諮詢與預防性法律服務', 2, 'Y', 'system'),
('調解談判', 'Mediation Negotiation', 'fas fa-handshake', '調解、和解與談判服務', 3, 'Y', 'system'),
('非訟強執', 'Non-litigation Enforcement', 'fas fa-gavel', '強制執行與非訟事件', 4, 'Y', 'system'),
('債務清理', 'Debt Settlement', 'fas fa-file-invoice-dollar', '債務清理與破產程序', 5, 'Y', 'system'),
('法律顧問', 'Legal Counsel', 'fas fa-user-tie', '企業及個人法律顧問', 6, 'Y', 'system'),
('撰狀擬約', 'Drafting', 'fas fa-file-alt', '撰寫狀紙與合約擬定', 7, 'Y', 'system'),
('遺囑見證', 'Will Witness', 'fas fa-scroll', '遺囑見證與繼承規劃', 8, 'Y', 'system'),
('檢警陪偵', 'Police Accompaniment', 'fas fa-user-shield', '檢警偵訊陪同', 9, 'Y', 'system'),
('羈押律見', 'Detention Visit', 'fas fa-lock', '羈押期間律師接見', 10, 'Y', 'system');

-- 案件實績
INSERT INTO lw_case (category, title, content, case_date, is_show, updid) VALUES
('刑事', '毒品危害防制條例案件 — 成功為被告爭取緩起訴', '<p>本所律師在毒品危害防制條例案件中，成功為被告爭取緩起訴處分，避免被告留下前科紀錄，並協助被告進入戒癮治療程序，獲得重新開始的機會。</p><p>本案重點在於被告為初犯，且持有數量甚微，經律師積極與檢方溝通，提出被告有就學需求及家庭照顧責任等情狀，最終獲得檢察官同意緩起訴。</p>', '2025-03-15', 'Y', 'admin'),
('民事', '夫妻剩餘財產分配訴訟 — 成功為當事人爭取合理分配', '<p>本所律師代理當事人進行夫妻剩餘財產分配訴訟，經過詳細調查雙方財產狀況，提出對當事人最有利的主張，最終成功爭取到合理之財產分配比例。</p><p>本案涉及不動產、股票、存款等多種財產類型，律師透過專業的財產調查與計算，確保當事人的權益獲得充分保障。</p>', '2025-01-20', 'Y', 'admin'),
('民事', '車禍過失傷害賠償 — 成功調解獲得合理賠償', '<p>當事人因車禍導致骨折及腦震盪，本所律師協助向肇事者及其保險公司請求損害賠償，經過調解程序，成功為當事人爭取到醫療費用、工作損失及精神慰撫金等合理賠償。</p>', '2024-11-08', 'Y', 'admin'),
('行政', '稅捐稽徵爭議 — 成功撤銷違法課稅處分', '<p>當事人收到稅捐機關之補繳稅單，經本所律師審查後發現課稅處分有計算錯誤及程序瑕疵，依法提起訴願及行政訴訟，最終成功撤銷該違法課稅處分，為當事人節省數十萬元稅款。</p>', '2024-08-22', 'Y', 'admin'),
('刑事', '詐欺案件 — 成功為被告爭取無罪判決', '<p>本所律師在詐欺案件中，透過詳盡的證據調查與交叉詰問，成功證明被告並無詐欺故意，法院最終判決被告無罪，當事人得以洗清冤屈。</p>', '2025-02-10', 'Y', 'admin');

-- 情報分享
INSERT INTO lw_share (title, content, share_date, is_show, updid) VALUES
('什麼是緩起訴？律師帶你一次搞懂', '<p>緩起訴是檢察官在偵查終結後，認為被告所犯之罪為死刑、無期徒刑或最輕本刑三年以上有期徒刑以外之罪，且參酌刑法第57條各款所列事項，認為以緩起訴為適當者，得定一年以上三年以下之期間為緩起訴期間。</p><h3>緩起訴的好處</h3><ul><li>不會留下前科紀錄</li><li>有機會重新開始</li><li>可附帶條件如向被害人道歉、立悔過書等</li></ul><p>若您正面臨刑事案件，建議盡早尋求專業律師協助，評估是否適合爭取緩起訴。</p>', '2025-04-01', 'Y', 'admin'),
('離婚協議書怎麼寫？注意事項大公開', '<p>離婚協議書是雙方協議離婚的重要文件，內容應包含以下要點：</p><ol><li><strong>監護權歸屬</strong>：明確約定未成年子女的監護權歸屬及探視權安排</li><li><strong>扶養費</strong>：約定每月扶養費金額及給付方式</li><li><strong>財產分配</strong>：夫妻剩餘財產的分配方式</li><li><strong>贍養費</strong>：如有需要，約定贍養費金額及期間</li></ol><p>建議在簽署離婚協議書前，先諮詢專業律師，確保自身權益獲得保障。</p>', '2025-03-15', 'Y', 'admin'),
('車禍發生後的第一時間該怎麼做？', '<p>車禍發生後的正確處理步驟，關係到後續求償的成敗：</p><ol><li><strong>確認安全</strong>：開啟雙黃燈，放置警告標誌</li><li><strong>報警處理</strong>：撥打110報警，不要私下和解</li><li><strong>蒐證</strong>：拍照記錄車損、路面狀況、號誌</li><li><strong>就醫</strong>：即使覺得沒事也要就醫檢查</li><li><strong>保留證據</strong>：就醫收據、診斷書、修車估價單等</li></ol><p>如有車禍相關法律問題，歡迎至本所諮詢頁面預約免費法律諮詢。</p>', '2025-02-20', 'Y', 'admin'),
('租屋糾紛常見問題與解決方法', '<p>租屋糾紛是常見的民事爭議類型，以下為最常見的幾種情形：</p><h3>押金爭議</h3><p>房東不返還押金是最常見的租屋糾紛。依據民法規定，押金不得超過二個月租金，且租約終止後房東應返還押金。</p><h3>修繕責任</h3><p>租賃物之修繕，除契約另有約定外，由出租人負擔。但承租人如有重大過失導致損壞，則應自行修繕。</p><p>建議租屋前簽訂詳細租約，並拍照存證屋況，以避免後續爭議。</p>', '2024-12-10', 'Y', 'admin'),
('勞工權益知多少？加班費計算方式詳解', '<p>加班費是勞工重要的權益，但許多勞工朋友不清楚該如何計算：</p><ul><li><strong>平日加班</strong>：前2小時加給1/3，第3小時起加給2/3</li><li><strong>休息日加班</strong>：2小時以內加給1又1/3，2小時以上加給1又2/3</li><li><strong>國定假日加班</strong>：加給1倍工資</li></ul><p>如果雇主未依法給付加班費，勞工可向勞工局申訴或提起勞資爭議調解。本所提供免費法律諮詢，歡迎來電預約。</p>', '2024-10-05', 'Y', 'admin');

-- 網站設定
INSERT INTO lw_site (site_key, site_value, remark) VALUES
('office_name', '古雅軒法律事務所', '事務所名稱'),
('office_name_en', 'GUYAHSUAN Law Office', '事務所英文名'),
('phone', '04-25353236', '電話'),
('address', '臺中市北屯區', '地址'),
('email', '', 'Email'),
('service_time', '週一至週五 09:00-18:00', '服務時間'),
('description', '古雅軒法律事務所－距離臺中地院25分鐘－為一處遠離市中心紛擾，鄰近台鐵頭家厝站、松竹站及中捷松竹站旁交通便捷的事務所。', '事務所描述');