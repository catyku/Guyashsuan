/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.8.6-MariaDB, for debian-linux-gnu (aarch64)
--
-- Host: 192.168.0.172    Database: guyahsuan
-- ------------------------------------------------------
-- Server version	11.3.2-MariaDB-1:11.3.2+maria~ubu2204

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `lw_admin`
--

DROP TABLE IF EXISTS `lw_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lw_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '帳號',
  `password` varchar(255) NOT NULL COMMENT '密碼(BCrypt)',
  `display_name` varchar(50) DEFAULT NULL COMMENT '顯示名稱',
  `role` varchar(20) DEFAULT 'ADMIN' COMMENT '角色',
  `is_enabled` char(1) DEFAULT 'Y' COMMENT '是否啟用',
  `inptime` datetime DEFAULT current_timestamp(),
  `updid` varchar(50) DEFAULT NULL,
  `updtime` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='後台管理員';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lw_admin`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `lw_admin` WRITE;
/*!40000 ALTER TABLE `lw_admin` DISABLE KEYS */;
INSERT INTO `lw_admin` VALUES
(3,'admin','$2a$10$yDr4o9yc/uPJ6Y.de9gOXuP0jCnUk/0BEnk82AhRvBeDLqbI/Yqfi','管理員','ADMIN','Y','2026-05-16 07:12:28',NULL,'2026-05-16 07:15:47');
/*!40000 ALTER TABLE `lw_admin` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `lw_attorney`
--

DROP TABLE IF EXISTS `lw_attorney`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lw_attorney` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `title` varchar(50) DEFAULT NULL COMMENT '職稱（所長/律師/法務等）',
  `license_no` varchar(50) DEFAULT NULL COMMENT '證書字號',
  `photo` varchar(255) DEFAULT NULL COMMENT '照片路徑',
  `specialty` text DEFAULT NULL COMMENT '專長領域',
  `education` text DEFAULT NULL COMMENT '學歷',
  `experience` text DEFAULT NULL COMMENT '經歷',
  `description` text DEFAULT NULL COMMENT '簡介描述',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `is_show` char(1) DEFAULT 'Y' COMMENT '是否顯示',
  `inptime` datetime DEFAULT current_timestamp(),
  `updid` varchar(50) DEFAULT NULL,
  `updtime` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='律師';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lw_attorney`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `lw_attorney` WRITE;
/*!40000 ALTER TABLE `lw_attorney` DISABLE KEYS */;
INSERT INTO `lw_attorney` VALUES
(4,'黃煦詮','所長','','/uploads/attorney/poto1.jpg','','東吳大學法律學系畢業','103年律師高考及格\n陸軍第十軍團指揮部人事評審委員會委員\n台灣勞工大聯盟總工會、臺中市職業總工會及各職業工會法律顧問\n臺中市政府警察局豐原分局、大雅分局國家賠償事件處理委員會委員\n臺中市和平區公所法律顧問','',1,'Y','2026-05-17 19:51:40','system',NULL),
(5,'鄭文朋','律師','','/uploads/attorney/poto2.jpg','','國立中正大學財經法律系畢業\n國立中正大學財經法律所 財經法組畢業','','',2,'Y','2026-05-17 19:51:40','system',NULL),
(6,'陳庭浩','合署律師','','/uploads/attorney/poto3.jpg','','國立臺北大學法學學士\n國立臺北大學法學碩士','108年律師高考合格\n日本神戶大學法學研究科交換生\n國立臺北大學法律服務社諮詢律師\n南台中家扶中心諮詢律師\n《身分法爭點整理》司法考試用書作者\n前臺北律師公會成年監護修法小組成員\n前志光公職補習班高普考講師\n信託業業務人員/高齡金融規劃顧問師合格\nJLPT日本語能力試驗N1合格','著作：\n科技部專題計畫-《我國成年監護制度之困境與修法趨勢》\n國立臺北大學碩士論文-《從日本立法例論我國意定監護制度下主觀利益與客觀利益之調和》',3,'Y','2026-05-17 19:51:40','system',NULL),
(7,'陳姿伃','法務','','/uploads/attorney/poto4.jpg','','靜宜大學法律學系畢業','114年度勞資事務師考試及格','',4,'Y','2026-05-17 19:51:40','system',NULL),
(8,'郭玟妤','行政','','/uploads/attorney/poto5.jpg','','','','',5,'Y','2026-05-17 19:51:40','system',NULL);
/*!40000 ALTER TABLE `lw_attorney` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `lw_banner`
--

DROP TABLE IF EXISTS `lw_banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lw_banner` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL COMMENT '標題',
  `subtitle` text DEFAULT NULL COMMENT '副標題',
  `image` varchar(255) NOT NULL COMMENT '圖片路徑',
  `link_url` varchar(500) DEFAULT NULL COMMENT '連結',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `is_show` char(1) DEFAULT 'Y' COMMENT '是否顯示',
  `inptime` datetime DEFAULT current_timestamp(),
  `updid` varchar(50) DEFAULT NULL,
  `updtime` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首頁輪播';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lw_banner`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `lw_banner` WRITE;
/*!40000 ALTER TABLE `lw_banner` DISABLE KEYS */;
INSERT INTO `lw_banner` VALUES
(1,'GUYAHSUAN Law Office','鉅細靡遺蒐羅全臺各法院裁判，輔以學說見解發展，以與社會脈動相結合的法律詮釋，大數據分析提出符合當事人最佳利益的見解。','/uploads/banner/banner-05.jpg','about.html',1,'Y','2026-05-17 19:51:40','system',NULL);
/*!40000 ALTER TABLE `lw_banner` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `lw_case`
--

DROP TABLE IF EXISTS `lw_case`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lw_case` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(50) NOT NULL COMMENT '案件類別（刑事/民事/行政等）',
  `title` varchar(255) NOT NULL COMMENT '案件標題',
  `content` text DEFAULT NULL COMMENT '案件內容',
  `case_date` date DEFAULT NULL COMMENT '案件日期',
  `image` varchar(255) DEFAULT NULL COMMENT '圖片路徑',
  `is_show` char(1) DEFAULT 'Y' COMMENT '是否顯示',
  `inptime` datetime DEFAULT current_timestamp(),
  `updid` varchar(50) DEFAULT NULL,
  `updtime` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案件實績';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lw_case`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `lw_case` WRITE;
/*!40000 ALTER TABLE `lw_case` DISABLE KEYS */;
INSERT INTO `lw_case` VALUES
(6,'刑事','『竊盜』案件，再議成功。檢察官提起公訴','<p>在刑事案件中，檢察官是否「起訴」當事人，往往決定了案件是否會進入法院審理。<br>許多民眾在遇到刑事糾紛時，常常聽到「不起訴處分」、「聲請再議」甚至「發回續行偵查」這些專業用語，卻不清楚其中的意義與關聯。本文由謙聖國際法律事務所為您說明這三個常見程序，讓您在面對刑事案件時更有方向。<br>一、什麼是「不起訴處分」？<br> 當檢察官偵查完一件刑案後，若認為證據不足、罪嫌不足，或是符合《刑事訴訟法》第252條規定的其他情況（如行為不罰、時效已過、被害人撤回告訴等），就可能做出「不起訴處分」。<br> ➙簡單來說，就是檢察官認為沒有必要將案件移送法院審理，選擇結束偵查程序。<br><br><img src=\"img/case-01.jpg\"><br><br>enlightened常見的不起訴原因包括： • 證據不足，無法證明犯罪成立 • 行為不構成犯罪 • 犯罪已逾追訴時效 • 被害人撤回告訴（限於告訴乃論之罪）</p>','2023-02-28','/uploads/case/case-01.jpg','Y','2026-05-17 19:51:40','system',NULL);
/*!40000 ALTER TABLE `lw_case` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `lw_consultation`
--

DROP TABLE IF EXISTS `lw_consultation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lw_consultation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '電話',
  `email` varchar(255) DEFAULT NULL COMMENT 'Email',
  `subject` varchar(255) DEFAULT NULL COMMENT '諮詢主題',
  `content` text DEFAULT NULL COMMENT '諮詢內容',
  `status` char(1) DEFAULT 'P' COMMENT '處理狀態: P=待處理, D=處理中, C=已結案',
  `reply` text DEFAULT NULL COMMENT '回覆內容',
  `inptime` datetime DEFAULT current_timestamp(),
  `updid` varchar(50) DEFAULT NULL,
  `updtime` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='法律諮詢';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lw_consultation`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `lw_consultation` WRITE;
/*!40000 ALTER TABLE `lw_consultation` DISABLE KEYS */;
/*!40000 ALTER TABLE `lw_consultation` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `lw_service`
--

DROP TABLE IF EXISTS `lw_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lw_service` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '業務名稱',
  `name_en` varchar(100) DEFAULT NULL COMMENT '英文名稱',
  `icon` varchar(100) DEFAULT NULL COMMENT '圖示class',
  `description` text DEFAULT NULL COMMENT '描述',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `is_show` char(1) DEFAULT 'Y' COMMENT '是否顯示',
  `inptime` datetime DEFAULT current_timestamp(),
  `updid` varchar(50) DEFAULT NULL,
  `updtime` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='業務領域';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lw_service`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `lw_service` WRITE;
/*!40000 ALTER TABLE `lw_service` DISABLE KEYS */;
INSERT INTO `lw_service` VALUES
(11,'各類訴訟','Litigation','fas fa-balance-scale','民事、刑事、行政訴訟',1,'Y','2026-05-17 19:51:40','system',NULL),
(12,'預防諮詢','Preventive Counseling','fas fa-shield-alt','法律諮詢與預防性法律服務',2,'Y','2026-05-17 19:51:40','system',NULL),
(13,'調解談判','Mediation Negotiation','fas fa-handshake','調解、和解與談判服務',3,'Y','2026-05-17 19:51:40','system',NULL),
(14,'非訟強執','Non-litigation Enforcement','fas fa-gavel','強制執行與非訟事件',4,'Y','2026-05-17 19:51:40','system',NULL),
(15,'債務清理','Debt Settlement','fas fa-file-invoice-dollar','債務清理與破產程序',5,'Y','2026-05-17 19:51:40','system',NULL),
(16,'法律顧問','Legal Counsel','fas fa-user-tie','企業及個人法律顧問',6,'Y','2026-05-17 19:51:40','system',NULL),
(17,'撰狀擬約','Drafting','fas fa-file-alt','撰寫狀紙與合約擬定',7,'Y','2026-05-17 19:51:40','system',NULL),
(18,'遺囑見證','Will Witness','fas fa-scroll','遺囑見證與繼承規劃',8,'Y','2026-05-17 19:51:40','system',NULL),
(19,'檢警陪偵','Police Accompaniment','fas fa-user-shield','檢警偵訊陪同',9,'Y','2026-05-17 19:51:40','system',NULL),
(20,'羈押律見','Detention Visit','fas fa-lock','羈押期間律師接見',10,'Y','2026-05-17 19:51:40','system',NULL);
/*!40000 ALTER TABLE `lw_service` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `lw_share`
--

DROP TABLE IF EXISTS `lw_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lw_share` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '文章標題',
  `content` text DEFAULT NULL COMMENT '文章內容',
  `share_date` date DEFAULT NULL COMMENT '發佈日期',
  `image` varchar(255) DEFAULT NULL COMMENT '封面圖',
  `is_show` char(1) DEFAULT 'Y' COMMENT '是否顯示',
  `inptime` datetime DEFAULT current_timestamp(),
  `updid` varchar(50) DEFAULT NULL,
  `updtime` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='情報分享';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lw_share`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `lw_share` WRITE;
/*!40000 ALTER TABLE `lw_share` DISABLE KEYS */;
INSERT INTO `lw_share` VALUES
(6,'酒駕相關民刑事責任-者即符合「不能安全駕 駛」也就是俗 稱「酒駕」。','<p>根據刑法第185條之3第1項第1款規定，駕駛人「吐氣所含酒精濃度達每公升0.25毫克或血液中酒精濃度達百分之0.05以上」者即符合「不能安全駕駛」也就是俗稱「酒駕」。<br><br>因此，駕駛人違反前開規定致被害人受有損害時，以下案例解釋在不同事件態樣下的法律效果及其適用法條。<br>根據刑法第185條之3第1項第1款規定，駕駛人「吐氣所含酒精濃度達每公升0.25毫克或血液中酒精濃度達百分之0.05以上」者即符合「不能安全駕駛」也就是俗稱「酒駕」。<br>定，駕駛人「吐氣所含酒精濃度達每公升0.25毫克或血液中酒精濃度達百分之0.05以上」者即符合「不能安全駕駛」也就是俗稱「酒駕」。</p>','2023-02-28','/uploads/share/share-01.jpg','Y','2026-05-17 19:51:40','system',NULL);
/*!40000 ALTER TABLE `lw_share` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `lw_site`
--

DROP TABLE IF EXISTS `lw_site`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lw_site` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_key` varchar(50) NOT NULL COMMENT '設定鍵',
  `site_value` text DEFAULT NULL COMMENT '設定值',
  `remark` varchar(255) DEFAULT NULL COMMENT '備註',
  `updtime` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `site_key` (`site_key`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='網站設定';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lw_site`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `lw_site` WRITE;
/*!40000 ALTER TABLE `lw_site` DISABLE KEYS */;
INSERT INTO `lw_site` VALUES
(8,'office_name','古雅軒法律事務所','事務所中文名稱，取自 index.html title 及 about.html 簡介區',NULL),
(9,'office_name_en','GUYAHSUAN Law Office','事務所英文名稱，取自 index.html title 及 about.html 頁面標題',NULL),
(10,'phone','04-25353236','事務所電話，取自 footer Contact us 區塊 (TEL) 及 about.html 聯絡資訊',NULL),
(11,'address','台中市潭子區中山路一段1巷58號','事務所地址，取自 footer Contact us 區塊 (fa-map-marker-alt)',NULL),
(12,'email','guyahsuan@gmail.com','事務所 Email，取自 footer Contact us 區塊 (mailto)',NULL),
(13,'service_time','曾朝榮議員服務處：臺中市北屯區東山路一段156-6號，周六上午10：00 ~ 12：00，敬請事先預約：04 2436 2995；三分埔松聖宮：臺中市北屯區松竹路二段308號，每週二晚上7:30-9:00，敬請事先預約：04 2242 8291；黃國書立委服務處：時間和地點都不太確定每月只有一次','服務時間，取自 consultation.html Service Time 區塊',NULL),
(14,'description','古雅軒法律事務所－距離臺中地院25分鐘－為一處遠離市中心紛擾，鄰近台鐵頭家厝站、松竹站及中捷松竹站旁交通便捷的事務所，於此您可以安心交由我們出謀劃策。本所同仁具備以下優勢，提供當事人專業法律服務：一、配合當事人需求提供著重效率的法律服務，控制當事人成本解決糾紛。二、直接坦率分析案情情勢、預測結果，引導當事人完整陳述提供事實資訊，使當事人掌握與律師同步對等資訊。三、鉅細靡遺蒐羅全臺各法院裁判，輔以學說見解發展，以與社會脈動相結合的法律詮釋，大數據分析提出符合當事人最佳利益的見解。四、熟稔法庭活動及法院心證形成過程的喜好及忌諱，注意並輔導當事人心態、形象及法庭內外表現。','事務所描述，取自 about.html About Us 區塊完整簡介',NULL);
/*!40000 ALTER TABLE `lw_site` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Dumping routines for database 'guyahsuan'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2026-05-17 19:58:32
