-- phpMyAdmin SQL Dump
-- version 4.3.11
-- http://www.phpmyadmin.net
--
-- 主機: 127.0.0.1
-- 產生時間： 2015 �?07 ??20 ??11:35
-- 伺服器版本: 5.6.24
-- PHP 版本： 5.6.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 資料庫： `lightdb`
--

-- --------------------------------------------------------

--
-- 資料表結構 `light`
--

CREATE TABLE IF NOT EXISTS `light` (
  `sid` int(11) NOT NULL,
  `value` int(11) NOT NULL DEFAULT '0' COMMENT '光敏電阻',
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上傳時間'
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

--
-- 資料表的匯出資料 `light`
--

INSERT INTO `light` (`sid`, `value`, `time`) VALUES
(1, 578, '2015-07-20 09:22:24'),
(2, 910, '2015-07-20 09:22:38'),
(3, 45, '2015-07-20 09:23:05'),
(4, 1000, '2015-07-20 09:23:18'),
(5, 400, '2015-07-20 09:23:35'),
(6, 400, '2015-07-20 09:29:06'),
(7, 400, '2015-07-20 09:29:21'),
(8, 400, '2015-07-20 09:29:40'),
(9, 400, '2015-07-20 09:29:44'),
(10, 400, '2015-07-20 09:29:44'),
(11, 400, '2015-07-20 09:29:45'),
(12, 400, '2015-07-20 09:29:45'),
(13, 400, '2015-07-20 09:29:45');

--
-- 已匯出資料表的索引
--

--
-- 資料表索引 `light`
--
ALTER TABLE `light`
  ADD PRIMARY KEY (`sid`);

--
-- 在匯出的資料表使用 AUTO_INCREMENT
--

--
-- 使用資料表 AUTO_INCREMENT `light`
--
ALTER TABLE `light`
  MODIFY `sid` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=14;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
