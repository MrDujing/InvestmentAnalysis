/*
 Navicat MySQL Data Transfer

 Source Server         : bob
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : 47.102.123.219:3306
 Source Schema         : investment_data

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 23/11/2021 18:48:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for asset_history_value
-- ----------------------------
DROP TABLE IF EXISTS `asset_history_value`;
CREATE TABLE `asset_history_value`  (
  `asset_code` mediumint(6) UNSIGNED ZEROFILL NOT NULL COMMENT '基金/指数/组合的代码',
  `value_date` smallint UNSIGNED NOT NULL COMMENT '历史净值对应的时间',
  `asset_property` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '资产属性:0-未知;1-基金;2-指数;3-组合',
  `net_value` float UNSIGNED NOT NULL COMMENT '每日净值,去掉分红后净值',
  `total_value` float UNSIGNED NOT NULL COMMENT '累计净值,只用于分红类资产;其他资产，值同day_value',
  `day_increase_rate` float NOT NULL COMMENT '每日增长率',
  PRIMARY KEY (`asset_code`, `value_date`) USING BTREE,
  INDEX `code_date`(`asset_code`) USING BTREE COMMENT '将同类资产组合在一起'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fund_position
-- ----------------------------
DROP TABLE IF EXISTS `fund_position`;
CREATE TABLE `fund_position`  (
  `fund_code` mediumint(6) UNSIGNED ZEROFILL NOT NULL COMMENT '基金代码',
  `quarter_count` smallint UNSIGNED NOT NULL COMMENT '基金持仓季度',
  `asset_property` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '属性:0-未知;1-股票;2-债券',
  `asset_code` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '持仓代码,适用英文/数字',
  `asset_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '股票/债券的名字',
  `asset_proportion` float UNSIGNED NOT NULL COMMENT '持仓比例',
  PRIMARY KEY (`fund_code`, `quarter_count`, `asset_code`) USING BTREE,
  INDEX `code_index`(`fund_code`) USING BTREE COMMENT '对基金代码进行排序'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
