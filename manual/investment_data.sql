/*
 Navicat MySQL Data Transfer

 Source Server         : bob
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : *.*.*.*:3306
 Source Schema         : investment_data

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 10/02/2022 21:23:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for fund_asset
-- ----------------------------
DROP TABLE IF EXISTS `fund_asset`;
CREATE TABLE `fund_asset`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `fund_code` mediumint(6) UNSIGNED ZEROFILL NOT NULL COMMENT '基金代码',
  `quarter` smallint UNSIGNED NOT NULL COMMENT '基金持仓季度2020-1为1季度',
  `stock_proportion` float NULL DEFAULT NULL COMMENT '股票资产比例',
  `bond_proportion` float NULL DEFAULT NULL COMMENT '债券资产比例',
  `cash_proportion` float UNSIGNED NULL DEFAULT NULL COMMENT '现金资产比例',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fund_code_index`(`fund_code`) USING BTREE COMMENT '对基金代码进行排序',
  INDEX `quarter_index`(`quarter`) USING BTREE COMMENT '对季度进行排序'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fund_base_info
-- ----------------------------
DROP TABLE IF EXISTS `fund_base_info`;
CREATE TABLE `fund_base_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `fund_code` mediumint(6) UNSIGNED ZEROFILL NOT NULL COMMENT '基金代码',
  `fund_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '基金名称',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fund_code_index`(`fund_code`) USING BTREE COMMENT '对基金代码进行排序'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fund_position
-- ----------------------------
DROP TABLE IF EXISTS `fund_position`;
CREATE TABLE `fund_position`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `fund_code` mediumint(6) UNSIGNED ZEROFILL NOT NULL COMMENT '基金代码',
  `quarter` smallint UNSIGNED NOT NULL COMMENT '基金持仓季度2020-1为1季度',
  `asset_property` bit(1) NOT NULL DEFAULT b'0' COMMENT '属性:0-未知;1-股票;2-债券',
  `asset_code` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '持仓代码,适用英文/数字',
  `asset_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '股票/债券的名字',
  `asset_proportion` float UNSIGNED NOT NULL COMMENT '持仓比例',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fund_code_index`(`fund_code`) USING BTREE COMMENT '对基金代码进行排序',
  INDEX `quarter_index`(`quarter`) USING BTREE COMMENT '对季度进行排序'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fund_value
-- ----------------------------
DROP TABLE IF EXISTS `fund_value`;
CREATE TABLE `fund_value`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识码',
  `fund_code` mediumint(6) UNSIGNED ZEROFILL NOT NULL COMMENT '基金代码',
  `date` smallint UNSIGNED NOT NULL COMMENT '历史净值对应的日期,2000/1/1年对应1',
  `net_value` float UNSIGNED NOT NULL COMMENT '每日净值',
  `total_value` float UNSIGNED NOT NULL COMMENT '累计净值',
  `day_increase_rate` float NOT NULL COMMENT '每日增长率',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fund_code_index`(`fund_code`) USING BTREE COMMENT '以基金代码进行排序'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for index_base_info
-- ----------------------------
DROP TABLE IF EXISTS `index_base_info`;
CREATE TABLE `index_base_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `index_code` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '指数代码',
  `index_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '指数名称',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_code_index`(`index_code`) USING BTREE COMMENT '对基金代码进行排序'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for index_value
-- ----------------------------
DROP TABLE IF EXISTS `index_value`;
CREATE TABLE `index_value`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识码',
  `index_code` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '指数代码',
  `date` smallint UNSIGNED NOT NULL COMMENT '指数对应的日期,2000/1/1年对应1',
  `index_value` float UNSIGNED NOT NULL COMMENT '每日指数',
  `day_increase_rate` float NOT NULL COMMENT '每日增长率',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_code_index`(`index_code`) USING BTREE COMMENT '对指数代码进行排序'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for manager_base_info
-- ----------------------------
DROP TABLE IF EXISTS `manager_base_info`;
CREATE TABLE `manager_base_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `manager_code` int UNSIGNED NOT NULL COMMENT '基金经理代码',
  `manager_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '基金经理名称',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `manager_code_index`(`manager_code`) USING BTREE COMMENT '对基金代码进行排序'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for manager_fund
-- ----------------------------
DROP TABLE IF EXISTS `manager_fund`;
CREATE TABLE `manager_fund`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `manager_code` int UNSIGNED NOT NULL COMMENT '基金经理代码',
  `fund_code` mediumint(6) UNSIGNED ZEROFILL NOT NULL COMMENT '基金代码',
  `ismanage` bit(1) NOT NULL COMMENT '是否在管,0-no,1-yes',
  `startdate` smallint UNSIGNED NOT NULL COMMENT '开始管理日期，2000/1/1为1',
  `endDate` smallint UNSIGNED NULL DEFAULT NULL COMMENT '结束管理日期，为空表明在管',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `manager_code_index`(`manager_code`) USING BTREE COMMENT '以基金经理代码进行排序'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stock_base_info
-- ----------------------------
DROP TABLE IF EXISTS `stock_base_info`;
CREATE TABLE `stock_base_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `stock_code` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票代码,含美股/港股/德股',
  `company_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司名称',
  `company_industry` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公司所属行业',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `stock_code_index`(`stock_code`) USING BTREE COMMENT '按股票代码排序'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
