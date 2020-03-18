/*
 Navicat Premium Data Transfer

 Source Server         : zhongdu-online
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : rm-bp1754k7d4rk0k8ag0o.mysql.rds.aliyuncs.com
 Source Database       : ntmx

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : utf-8

 Date: 03/17/2020 15:29:34 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `ntm_api`
-- ----------------------------
DROP TABLE IF EXISTS `ntm_api`;
CREATE TABLE `ntm_api` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `unique` varchar(64) NOT NULL,
  `name` varchar(50) NOT NULL COMMENT '名称',
  `version` int(4) NOT NULL DEFAULT '1' COMMENT '版本',
  `method` tinyint(2) NOT NULL COMMENT '请求方式 1-GET 2-POST 3-PUT 4-PATCH 5-DELETE',
  `appid` varchar(32) NOT NULL DEFAULT '0' COMMENT '应用标识',
  `inner_interface` varchar(128) DEFAULT NULL COMMENT '业务传输对象',
  `inner_method` varchar(128) DEFAULT NULL,
  `is_ia` bit(1) NOT NULL COMMENT '是否需要认证授权 0-不需要 1-需要',
  `is_async` bit(1) NOT NULL COMMENT '是否需要异步 0-同步 1-异步',
  `daily_flow_limit` int(8) NOT NULL DEFAULT '0',
  `result` varchar(3072) DEFAULT NULL COMMENT '返回结果',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '状态 0-失效 1-生效',
  `create_time` datetime NOT NULL,
  `modify_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `ntm_api`
-- ----------------------------
BEGIN;
INSERT INTO `ntm_api` VALUES ('1', 'ntm.api.list', '网关API-列表', '1', '1', 'ntm', 'innerApiList', null, b'0', b'0', '1000', null, '1', '2020-03-17 06:31:29', '2020-03-17 06:31:30'), ('2', 'ntm.api.add', '网关API-增加', '1', '2', 'ntm', 'innerApiAdd', null, b'0', b'0', '1000', null, '1', '2020-03-17 06:31:29', '2020-03-17 06:31:29'), ('3', 'ntm.api.modify', '网关API-修改', '1', '2', 'ntm', 'innerApiModify', null, b'0', b'0', '1000', null, '1', '2020-03-17 06:31:29', '2020-03-17 06:31:29'), ('4', 'ntm.api.delete', '网关API-删除', '1', '2', 'ntm', 'innerApiDelete', null, b'0', b'0', '1000', null, '1', '2020-03-17 06:31:29', '2020-03-17 06:31:29'), ('5', 'ntm.api.param.list', '网关API参数-列表', '1', '1', 'ntm', 'innerApiParamsList', null, b'0', b'0', '1000', null, '1', '2020-03-17 06:31:29', '2020-03-17 06:31:29'), ('6', 'ntm.api.param.add', '网关API参数-增加', '1', '2', 'ntm', 'innerApiParamsAdd', null, b'0', b'0', '1000', null, '1', '2020-03-17 06:31:29', '2020-03-17 06:31:29'), ('7', 'ntm.api.param.modify', '网关API参数-修改', '1', '2', 'ntm', 'innerApiParamsModify', null, b'0', b'0', '1000', null, '1', '2020-03-17 06:31:29', '2020-03-17 06:31:29'), ('8', 'ntm.api.param.delete', '网关API参数-删除', '1', '2', 'ntm', 'innerApiParamsDelete', null, b'0', b'0', '1000', null, '1', '2020-03-17 06:31:29', '2020-03-17 06:31:29');
COMMIT;

-- ----------------------------
--  Table structure for `ntm_api_param`
-- ----------------------------
DROP TABLE IF EXISTS `ntm_api_param`;
CREATE TABLE `ntm_api_param` (
  `id` bigint(20) NOT NULL,
  `pid` bigint(20) NOT NULL DEFAULT '0' COMMENT '判断当前参数的前置判断条件',
  `api_id` bigint(20) NOT NULL,
  `name` varchar(32) NOT NULL COMMENT '字段名称',
  `status` tinyint(2) NOT NULL COMMENT '状态 0-失效 1-生效',
  `type` tinyint(2) NOT NULL COMMENT '类型 具体的查看ApiParamTypeEnum',
  `is_required` bit(1) NOT NULL COMMENT '是否必须 0-非必须 1-必须',
  `err_msg` varchar(128) DEFAULT NULL,
  `desp` varchar(128) DEFAULT NULL,
  `example` varchar(512) DEFAULT NULL,
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `ntm_config_setting`
-- ----------------------------
DROP TABLE IF EXISTS `ntm_config_setting`;
CREATE TABLE `ntm_config_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pid` bigint(20) NOT NULL DEFAULT '0',
  `config_key` varchar(64) NOT NULL,
  `config_value` varchar(512) DEFAULT NULL,
  `content` varchar(64) DEFAULT NULL,
  `status` tinyint(2) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL,
  `modify_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `ntm_config_setting`
-- ----------------------------
BEGIN;
INSERT INTO `ntm_config_setting` VALUES ('1', '0', 'CHECK_SIGN', 'false', '是否校验接口加密签名', '1', '2020-03-17 06:44:44', '2020-03-17 06:44:45');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
