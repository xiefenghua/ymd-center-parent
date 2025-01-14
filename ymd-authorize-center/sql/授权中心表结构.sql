/*
Navicat MySQL Data Transfer

Source Server         : 109临时测试环境
Source Server Version : 50742
Source Host           : 192.168.10.109:3306
Source Database       : authorize_center

Target Server Type    : MYSQL
Target Server Version : 50742
File Encoding         : 65001

Date: 2024-01-02 14:43:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_authorize_center_authorize_lock_change
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_authorize_lock_change`;
CREATE TABLE `t_authorize_center_authorize_lock_change` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `lock_id` varchar(64) NOT NULL COMMENT '锁id',
  `card_has_change_version` int(10) DEFAULT '0' COMMENT '卡片平台版本号',
  `card_pre_change_version` int(10) DEFAULT '0' COMMENT '卡片硬件上报版本号',
  `pwd_has_change_version` int(10) DEFAULT '0' COMMENT '密码平台版本号',
  `pwd_pre_change_version` int(10) DEFAULT '0' COMMENT '密码硬件上报版本号',
  `face_has_change_version` int(10) DEFAULT '0' COMMENT '人脸平台版本号',
  `face_pre_change_version` int(10) DEFAULT '0' COMMENT '人脸硬件上报版本号',
  `finger_has_change_version` int(10) DEFAULT '0' COMMENT '指纹平台版本号',
  `finger_pre_change_version` int(10) DEFAULT '0' COMMENT '指纹硬件上报版本号',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_lock` (`lock_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='授权版本号';

-- ----------------------------
-- Records of t_authorize_center_authorize_lock_change
-- ----------------------------
INSERT INTO `t_authorize_center_authorize_lock_change` VALUES ('5', '005448489A47497AA59941EC6D82', '0', '0', '2', '0', '0', '0', '0', '0', '2023-12-20 13:24:05', '2023-12-20 13:23:56', '13161337653', '13161337653', null);

-- ----------------------------
-- Table structure for t_authorize_center_authorize_log
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_authorize_log`;
CREATE TABLE `t_authorize_center_authorize_log` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `lock_id` varchar(64) DEFAULT NULL,
  `module` varchar(50) DEFAULT NULL COMMENT 'module',
  `operate_type` varchar(16) DEFAULT NULL COMMENT 'operate_type',
  `operate_name` varchar(50) DEFAULT NULL COMMENT 'operate_name',
  `operation` varchar(50) DEFAULT NULL COMMENT 'operation',
  `req_param` varchar(1050) DEFAULT NULL COMMENT 'req_param',
  `resp_result` varchar(1050) DEFAULT NULL COMMENT 'resp_result',
  `partition_date` int(10) NOT NULL COMMENT '表分区日期格式yyyyMM',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`,`partition_date`),
  KEY `idx_lock_id` (`lock_id`)
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8mb4 COMMENT='授权操作日志表'
/*!50100 PARTITION BY RANGE (partition_date)
(PARTITION p202311 VALUES LESS THAN (202311) ENGINE = InnoDB,
 PARTITION p202401 VALUES LESS THAN (202401) ENGINE = InnoDB,
 PARTITION p202403 VALUES LESS THAN (202403) ENGINE = InnoDB,
 PARTITION p202406 VALUES LESS THAN (202406) ENGINE = InnoDB,
 PARTITION p202409 VALUES LESS THAN (202409) ENGINE = InnoDB,
 PARTITION p202412 VALUES LESS THAN (202412) ENGINE = InnoDB,
 PARTITION p202501 VALUES LESS THAN (202501) ENGINE = InnoDB,
 PARTITION p202503 VALUES LESS THAN (202503) ENGINE = InnoDB,
 PARTITION p202506 VALUES LESS THAN (202506) ENGINE = InnoDB,
 PARTITION p202509 VALUES LESS THAN (202509) ENGINE = InnoDB,
 PARTITION p202512 VALUES LESS THAN (202512) ENGINE = InnoDB,
 PARTITION p202601 VALUES LESS THAN (202601) ENGINE = InnoDB,
 PARTITION p202603 VALUES LESS THAN (202603) ENGINE = InnoDB,
 PARTITION p202606 VALUES LESS THAN (202606) ENGINE = InnoDB,
 PARTITION p202609 VALUES LESS THAN (202609) ENGINE = InnoDB,
 PARTITION p202612 VALUES LESS THAN (202612) ENGINE = InnoDB,
 PARTITION p202701 VALUES LESS THAN (202701) ENGINE = InnoDB,
 PARTITION p202703 VALUES LESS THAN (202703) ENGINE = InnoDB,
 PARTITION p202706 VALUES LESS THAN (202706) ENGINE = InnoDB,
 PARTITION p202709 VALUES LESS THAN (202709) ENGINE = InnoDB,
 PARTITION p202712 VALUES LESS THAN (202712) ENGINE = InnoDB,
 PARTITION p202801 VALUES LESS THAN (202801) ENGINE = InnoDB,
 PARTITION p202803 VALUES LESS THAN (202803) ENGINE = InnoDB,
 PARTITION p202806 VALUES LESS THAN (202806) ENGINE = InnoDB,
 PARTITION p202809 VALUES LESS THAN (202809) ENGINE = InnoDB,
 PARTITION p202812 VALUES LESS THAN (202812) ENGINE = InnoDB,
 PARTITION p202901 VALUES LESS THAN (202901) ENGINE = InnoDB,
 PARTITION p202903 VALUES LESS THAN (202903) ENGINE = InnoDB,
 PARTITION p202906 VALUES LESS THAN (202906) ENGINE = InnoDB,
 PARTITION p202909 VALUES LESS THAN (202909) ENGINE = InnoDB,
 PARTITION p202912 VALUES LESS THAN (202912) ENGINE = InnoDB,
 PARTITION p203001 VALUES LESS THAN (203001) ENGINE = InnoDB,
 PARTITION p203003 VALUES LESS THAN (203003) ENGINE = InnoDB,
 PARTITION p203006 VALUES LESS THAN (203006) ENGINE = InnoDB,
 PARTITION p203009 VALUES LESS THAN (203009) ENGINE = InnoDB,
 PARTITION p203012 VALUES LESS THAN (203012) ENGINE = InnoDB,
 PARTITION p203101 VALUES LESS THAN (203101) ENGINE = InnoDB,
 PARTITION p203103 VALUES LESS THAN (203103) ENGINE = InnoDB,
 PARTITION p203106 VALUES LESS THAN (203106) ENGINE = InnoDB,
 PARTITION p203109 VALUES LESS THAN (203109) ENGINE = InnoDB,
 PARTITION p203112 VALUES LESS THAN (203112) ENGINE = InnoDB,
 PARTITION p203812 VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;

-- ----------------------------
-- Records of t_authorize_center_authorize_log
-- ----------------------------
INSERT INTO `t_authorize_center_authorize_log` VALUES ('307', '005448489A47497AA59941EC6D82', '授权同步', '网关层mqtt同步', 'C3433B2E5F3C使用cmd=93进行密码授权同步', null, '{\"authCount\":1,\"authVersion\":2,\"package\":0,\"count\":1,\"authCrc\":1916,\"listCount\":1,\"list\":[{\"code\":2,\"pwdType\":56,\"start\":1702940118000,\"end\":1704559800000,\"nfcId\":\"b6954626\",\"pwd\":\"313335343235\",\"authId\":8}],\"mac\":\"C3433B2E5F3C\",\"listCrc\":1916}', 'Kq3HfIwOPDmxbTWfKBrsqBFBmnbvfdEO1njLE2BnfmiPa80C1U0epOuIW+c6QIA+2i9umYd81w2cIli+esw5T9xo2QcPdHaqcuckE1CXn/hUPaicfPFc4v0bTOtz2+Hkt956VyBW6XJnsqWNIkTPXnm13Rlp4O4kDD9wyT0NcIqgi8mZlkKjh+9nOgN8eGUd/WS80o++8i47pwIgFnkombR2WOF4Z+OHGLkXVYtimpLEb1djFFN6+5O//pGEEFFyL02VKFRV3aBQUVMjl5KAmIgtq1OQ1OfsqXqfjZEwyLeE1R7hyjIBzg1esg1PbQwehIS9rF0vGo++6E4jlGPvhw==', '202312', '2023-12-20 15:52:30', null, null, null, null);
INSERT INTO `t_authorize_center_authorize_log` VALUES ('308', '005448489A47497AA59941EC6D82', '授权同步', '系统更新同步状态', '13161337653账号对C3433B2E5F3C锁进行删除密码授权增量同步', '13161337653', '{\"authChannelAlias\":\"13161337653\",\"authChannelModuleType\":\"1\",\"authChannelName\":\"管理员密码\",\"authChannelType\":\"101\",\"authChannelValue\":\"135425\",\"authDesc\":\"授权生成成功\",\"authEqualUuid\":\"B6123A3084E14B51A2E93FD93CD80B8520231220132404668\",\"authResource\":\"介质授权\",\"createBy\":\"13161337653\",\"createTime\":1703049899000,\"endTime\":1704559800000,\"flag\":1,\"freezeStatus\":1,\"id\":8,\"ids\":[],\"lockId\":\"005448489A47497AA59941EC6D82\",\"lockMac\":\"C3433B2E5F3C\",\"partitionDate\":202312,\"remark\":\"增量\",\"startTime\":1702940718000,\"status\":1,\"sync\":0,\"uid\":\"b6954626\",\"updateBy\":\"13161337653\",\"updateTime\":1703049899000,\"userAccount\":\"13161337653\",\"userName\":\"13161337653\"}', '更新状态成功', '202312', '2023-12-20 15:52:31', null, '13161337653', null, null);
INSERT INTO `t_authorize_center_authorize_log` VALUES ('309', '005448489A47497AA59941EC6D82', '授权同步', '网关层mqtt同步', 'C3433B2E5F3C使用cmd=93进行密码授权同步', null, '{\"authCount\":1,\"authVersion\":2,\"package\":0,\"count\":1,\"authCrc\":1916,\"listCount\":1,\"list\":[{\"code\":2,\"pwdType\":56,\"start\":1702940118000,\"end\":1704559800000,\"nfcId\":\"b6954626\",\"pwd\":\"313335343235\",\"authId\":8}],\"mac\":\"C3433B2E5F3C\",\"listCrc\":1916}', 'wAedxOSp4ZQ2wZJN8jb1E/nFKm4PVUbZ4mGZXJCO9S3HTEJhEqACO9kRVZw+ROiaIUt3S2Dhr7wG3s6nZVhoUSykAj1CkOPnCOIYdvjgH6evq7SZVgws2hIt+1W3uLUAo96uP/KQnf/LyITH2x2UdXnZw5OZ5ZFqZsd+BgRQTTW5YyWXYBi2s04I1yCySI9k6dvl3rFFDPe48rb9sU14G4Gtqg40BzCCGHQiu/V+BQaiW+XdK1Lcpf52jUkRW9b7J60pq0jSTzwfqCsQc5vASSbKQJ4Db3hktRDsvVITaHSwPje7M2Fjt2hkPkaDORrVrD6JvWMeCTlE++hYTS5Dwg==', '202312', '2023-12-20 15:52:59', null, null, null, null);
INSERT INTO `t_authorize_center_authorize_log` VALUES ('310', '005448489A47497AA59941EC6D82', '授权同步', '系统更新同步状态', '13161337653账号对C3433B2E5F3C锁进行删除密码授权增量同步', '13161337653', '{\"authChannelAlias\":\"13161337653\",\"authChannelModuleType\":\"1\",\"authChannelName\":\"管理员密码\",\"authChannelType\":\"101\",\"authChannelValue\":\"135425\",\"authDesc\":\"授权生成成功\",\"authEqualUuid\":\"B6123A3084E14B51A2E93FD93CD80B8520231220132404668\",\"authResource\":\"介质授权\",\"createBy\":\"13161337653\",\"createTime\":1703049899000,\"endTime\":1704559800000,\"flag\":1,\"freezeStatus\":1,\"id\":8,\"ids\":[],\"lockId\":\"005448489A47497AA59941EC6D82\",\"lockMac\":\"C3433B2E5F3C\",\"partitionDate\":202312,\"remark\":\"增量\",\"startTime\":1702940718000,\"status\":1,\"sync\":0,\"uid\":\"b6954626\",\"updateBy\":\"13161337653\",\"updateTime\":1703049899000,\"userAccount\":\"13161337653\",\"userName\":\"13161337653\"}', '更新状态成功', '202312', '2023-12-20 15:52:59', null, '13161337653', null, null);
INSERT INTO `t_authorize_center_authorize_log` VALUES ('311', '005448489A47497AA59941EC6D82', '授权同步', '网关层mqtt同步', 'C3433B2E5F3C使用cmd=93进行密码授权同步', null, '{\"authCount\":1,\"authVersion\":2,\"package\":0,\"count\":1,\"authCrc\":1916,\"listCount\":1,\"list\":[{\"code\":2,\"pwdType\":56,\"start\":1702940118000,\"end\":1704559800000,\"nfcId\":\"b6954626\",\"pwd\":\"313335343235\",\"authId\":8}],\"mac\":\"C3433B2E5F3C\",\"listCrc\":1916}', 'VitF7GrpirRT4BtJNn3x0hhvasqZWIbPUrF2wbBBoxrVib08WGrcw2S3jb01/Y5aIedPNy4TSQn+lS1y6PpBCqHdMQ+Qde63d3kWLZrNJJeSZF/ylzE4tfKeLLmrL9MQXNHSOhtDYGQi62zp2zctrbW8NxVN1hwcdGNpyH2AQ2amYLnGfWl6m/WpzVyeQKHhdSRnnWsBTVpvf5OlScG1FPGb3EiGpEIa+4cAcvE5WvQg37VarRSH4klH2XkMksWftN+040nnCvYy5SSjEwj4JRwgIDFCXryZpnFSmb2lk8VW1yr3E+wixB8q4L6Df74JHtNpxC9SiQw84uS7vJBXvQ==', '202312', '2023-12-20 15:53:32', null, null, null, null);
INSERT INTO `t_authorize_center_authorize_log` VALUES ('312', '005448489A47497AA59941EC6D82', '授权同步', '系统更新同步状态', '13161337653账号对C3433B2E5F3C锁进行删除密码授权增量同步', '13161337653', '{\"authChannelAlias\":\"13161337653\",\"authChannelModuleType\":\"1\",\"authChannelName\":\"管理员密码\",\"authChannelType\":\"101\",\"authChannelValue\":\"135425\",\"authDesc\":\"授权生成成功\",\"authEqualUuid\":\"B6123A3084E14B51A2E93FD93CD80B8520231220132404668\",\"authResource\":\"介质授权\",\"createBy\":\"13161337653\",\"createTime\":1703049899000,\"endTime\":1704559800000,\"flag\":1,\"freezeStatus\":1,\"id\":8,\"ids\":[],\"lockId\":\"005448489A47497AA59941EC6D82\",\"lockMac\":\"C3433B2E5F3C\",\"partitionDate\":202312,\"remark\":\"增量\",\"startTime\":1702940718000,\"status\":1,\"sync\":0,\"uid\":\"b6954626\",\"updateBy\":\"13161337653\",\"updateTime\":1703049899000,\"userAccount\":\"13161337653\",\"userName\":\"13161337653\"}', '更新状态成功', '202312', '2023-12-20 15:53:32', null, '13161337653', null, null);
INSERT INTO `t_authorize_center_authorize_log` VALUES ('313', '005448489A47497AA59941EC6D82', '授权同步', '系统更新同步状态', '13161337653账号对C3433B2E5F3C锁进行删除密码授权增量同步', '13161337653', '{\"authChannelAlias\":\"13161337653\",\"authChannelModuleType\":\"1\",\"authChannelName\":\"管理员密码\",\"authChannelType\":\"101\",\"authChannelValue\":\"135425\",\"authDesc\":\"授权生成成功\",\"authEqualUuid\":\"B6123A3084E14B51A2E93FD93CD80B8520231220132404668\",\"authResource\":\"介质授权\",\"createBy\":\"13161337653\",\"createTime\":1703049899000,\"endTime\":1704559800000,\"flag\":1,\"freezeStatus\":1,\"id\":8,\"ids\":[],\"lockId\":\"005448489A47497AA59941EC6D82\",\"lockMac\":\"C3433B2E5F3C\",\"partitionDate\":202312,\"remark\":\"增量\",\"startTime\":1702940718000,\"status\":1,\"sync\":0,\"uid\":\"b6954626\",\"updateBy\":\"13161337653\",\"updateTime\":1703049899000,\"userAccount\":\"13161337653\",\"userName\":\"13161337653\"}', '更新状态成功', '202312', '2023-12-20 15:54:03', null, '13161337653', null, null);

-- ----------------------------
-- Table structure for t_authorize_center_authorize_record
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_authorize_record`;
CREATE TABLE `t_authorize_center_authorize_record` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `person_auth_id` bigint(64) DEFAULT NULL COMMENT '人员授权id',
  `auth_equal_uuid` varchar(64) DEFAULT NULL COMMENT '判断lockId+user_account+auth_channel_type+auth_channel_value 是否相同的数据并且含时间交集的时间点判断为同一数据',
  `uid` varchar(8) NOT NULL COMMENT 'uid每个授权唯一id 8位字符数据',
  `lock_id` varchar(32) NOT NULL COMMENT '锁主键id',
  `user_account` varchar(32) NOT NULL COMMENT '授权用户账号',
  `user_name` varchar(32) DEFAULT NULL COMMENT '授权用户姓名',
  `auth_channel_module_type` varchar(6) NOT NULL COMMENT '授权介质模块类型',
  `auth_channel_type` varchar(6) NOT NULL COMMENT '授权介质类别如比',
  `auth_channel_name` varchar(8) DEFAULT NULL COMMENT '授权类型',
  `auth_channel_value` mediumtext COMMENT '授权特征值',
  `auth_channel_alias` varchar(16) DEFAULT NULL COMMENT '授权介质别名',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '授权开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '授权结束时间',
  `status` tinyint(2) NOT NULL COMMENT '授权伪删除标识 1正常 0删除',
  `flag` tinyint(2) NOT NULL COMMENT '授权是否有效 1=有效 0=无效',
  `freeze_status` tinyint(2) NOT NULL COMMENT '冻结状态 1正常 0冻结',
  `auth_desc` varchar(100) DEFAULT NULL COMMENT '授权备注',
  `auth_resource` varchar(100) DEFAULT NULL COMMENT '授权来源',
  `is_group` tinyint(2) DEFAULT NULL COMMENT '是否为授权组操作 1=是 0否',
  `auth_extend` varchar(250) DEFAULT NULL COMMENT '授权扩展字段(json 如介质安全等级)',
  `partition_date` int(10) NOT NULL COMMENT '表分区日期格式yyyyMM',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`,`partition_date`),
  KEY `idx_person_auth_id` (`person_auth_id`),
  KEY `idx_lock_user` (`lock_id`,`user_account`),
  KEY `idx_auth_equal_uuid` (`auth_equal_uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='授权表'
/*!50100 PARTITION BY RANGE (partition_date)
(PARTITION p202311 VALUES LESS THAN (202311) ENGINE = InnoDB,
 PARTITION p202401 VALUES LESS THAN (202401) ENGINE = InnoDB,
 PARTITION p202403 VALUES LESS THAN (202403) ENGINE = InnoDB,
 PARTITION p202406 VALUES LESS THAN (202406) ENGINE = InnoDB,
 PARTITION p202409 VALUES LESS THAN (202409) ENGINE = InnoDB,
 PARTITION p202412 VALUES LESS THAN (202412) ENGINE = InnoDB,
 PARTITION p202501 VALUES LESS THAN (202501) ENGINE = InnoDB,
 PARTITION p202503 VALUES LESS THAN (202503) ENGINE = InnoDB,
 PARTITION p202506 VALUES LESS THAN (202506) ENGINE = InnoDB,
 PARTITION p202509 VALUES LESS THAN (202509) ENGINE = InnoDB,
 PARTITION p202512 VALUES LESS THAN (202512) ENGINE = InnoDB,
 PARTITION p202601 VALUES LESS THAN (202601) ENGINE = InnoDB,
 PARTITION p202603 VALUES LESS THAN (202603) ENGINE = InnoDB,
 PARTITION p202606 VALUES LESS THAN (202606) ENGINE = InnoDB,
 PARTITION p202609 VALUES LESS THAN (202609) ENGINE = InnoDB,
 PARTITION p202612 VALUES LESS THAN (202612) ENGINE = InnoDB,
 PARTITION p202701 VALUES LESS THAN (202701) ENGINE = InnoDB,
 PARTITION p202703 VALUES LESS THAN (202703) ENGINE = InnoDB,
 PARTITION p202706 VALUES LESS THAN (202706) ENGINE = InnoDB,
 PARTITION p202709 VALUES LESS THAN (202709) ENGINE = InnoDB,
 PARTITION p202712 VALUES LESS THAN (202712) ENGINE = InnoDB,
 PARTITION p202801 VALUES LESS THAN (202801) ENGINE = InnoDB,
 PARTITION p202803 VALUES LESS THAN (202803) ENGINE = InnoDB,
 PARTITION p202806 VALUES LESS THAN (202806) ENGINE = InnoDB,
 PARTITION p202809 VALUES LESS THAN (202809) ENGINE = InnoDB,
 PARTITION p202812 VALUES LESS THAN (202812) ENGINE = InnoDB,
 PARTITION p202901 VALUES LESS THAN (202901) ENGINE = InnoDB,
 PARTITION p202903 VALUES LESS THAN (202903) ENGINE = InnoDB,
 PARTITION p202906 VALUES LESS THAN (202906) ENGINE = InnoDB,
 PARTITION p202909 VALUES LESS THAN (202909) ENGINE = InnoDB,
 PARTITION p202912 VALUES LESS THAN (202912) ENGINE = InnoDB,
 PARTITION p203001 VALUES LESS THAN (203001) ENGINE = InnoDB,
 PARTITION p203003 VALUES LESS THAN (203003) ENGINE = InnoDB,
 PARTITION p203006 VALUES LESS THAN (203006) ENGINE = InnoDB,
 PARTITION p203009 VALUES LESS THAN (203009) ENGINE = InnoDB,
 PARTITION p203012 VALUES LESS THAN (203012) ENGINE = InnoDB,
 PARTITION p203101 VALUES LESS THAN (203101) ENGINE = InnoDB,
 PARTITION p203103 VALUES LESS THAN (203103) ENGINE = InnoDB,
 PARTITION p203106 VALUES LESS THAN (203106) ENGINE = InnoDB,
 PARTITION p203109 VALUES LESS THAN (203109) ENGINE = InnoDB,
 PARTITION p203112 VALUES LESS THAN (203112) ENGINE = InnoDB,
 PARTITION p203812 VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;

-- ----------------------------
-- Records of t_authorize_center_authorize_record
-- ----------------------------
INSERT INTO `t_authorize_center_authorize_record` VALUES ('8', null, 'B6123A3084E14B51A2E93FD93CD80B8520231220132404668', 'b6954626', '005448489A47497AA59941EC6D82', '13161337653', '13161337653', '1', '101', '管理员密码', '135425', '13161337653', '2023-12-19 07:05:18', '2024-01-07 00:50:00', '1', '1', '1', '授权生成成功', '介质授权', null, null, '202312', '2023-12-20 13:24:59', '2023-12-20 13:24:59', '13161337653', '13161337653', null);

-- ----------------------------
-- Table structure for t_authorize_center_authorize_sync
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_authorize_sync`;
CREATE TABLE `t_authorize_center_authorize_sync` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `auth_id` bigint(64) NOT NULL COMMENT '授权id',
  `auth_equal_uuid` varchar(64) DEFAULT NULL,
  `sync_opt_type` varchar(16) NOT NULL COMMENT '同步操作类型',
  `sync_name` varchar(1024) DEFAULT NULL COMMENT '同步名称',
  `sync` tinyint(2) DEFAULT NULL COMMENT '同步状态 1同步 0未同步',
  `upload_status` tinyint(2) DEFAULT NULL COMMENT '上报同步状态  1已上报 0未上报',
  `push_status` tinyint(2) DEFAULT NULL COMMENT '下发同步状态 1下发 0未下发',
  `partition_date` int(10) NOT NULL COMMENT '表分区日期格式yyyyMM',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`,`partition_date`),
  KEY `idx-auth_id` (`auth_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='授权同步表'
/*!50500 PARTITION BY RANGE  COLUMNS(partition_date)
(PARTITION p202311 VALUES LESS THAN (202311) ENGINE = InnoDB,
 PARTITION p202401 VALUES LESS THAN (202401) ENGINE = InnoDB,
 PARTITION p202403 VALUES LESS THAN (202403) ENGINE = InnoDB,
 PARTITION p202406 VALUES LESS THAN (202406) ENGINE = InnoDB,
 PARTITION p202409 VALUES LESS THAN (202409) ENGINE = InnoDB,
 PARTITION p202412 VALUES LESS THAN (202412) ENGINE = InnoDB,
 PARTITION p202501 VALUES LESS THAN (202501) ENGINE = InnoDB,
 PARTITION p202503 VALUES LESS THAN (202503) ENGINE = InnoDB,
 PARTITION p202506 VALUES LESS THAN (202506) ENGINE = InnoDB,
 PARTITION p202509 VALUES LESS THAN (202509) ENGINE = InnoDB,
 PARTITION p202512 VALUES LESS THAN (202512) ENGINE = InnoDB,
 PARTITION p202601 VALUES LESS THAN (202601) ENGINE = InnoDB,
 PARTITION p202603 VALUES LESS THAN (202603) ENGINE = InnoDB,
 PARTITION p202606 VALUES LESS THAN (202606) ENGINE = InnoDB,
 PARTITION p202609 VALUES LESS THAN (202609) ENGINE = InnoDB,
 PARTITION p202612 VALUES LESS THAN (202612) ENGINE = InnoDB,
 PARTITION p202701 VALUES LESS THAN (202701) ENGINE = InnoDB,
 PARTITION p202703 VALUES LESS THAN (202703) ENGINE = InnoDB,
 PARTITION p202706 VALUES LESS THAN (202706) ENGINE = InnoDB,
 PARTITION p202709 VALUES LESS THAN (202709) ENGINE = InnoDB,
 PARTITION p202712 VALUES LESS THAN (202712) ENGINE = InnoDB,
 PARTITION p202801 VALUES LESS THAN (202801) ENGINE = InnoDB,
 PARTITION p202803 VALUES LESS THAN (202803) ENGINE = InnoDB,
 PARTITION p202806 VALUES LESS THAN (202806) ENGINE = InnoDB,
 PARTITION p202809 VALUES LESS THAN (202809) ENGINE = InnoDB,
 PARTITION p202812 VALUES LESS THAN (202812) ENGINE = InnoDB,
 PARTITION p202901 VALUES LESS THAN (202901) ENGINE = InnoDB,
 PARTITION p202903 VALUES LESS THAN (202903) ENGINE = InnoDB,
 PARTITION p202906 VALUES LESS THAN (202906) ENGINE = InnoDB,
 PARTITION p202909 VALUES LESS THAN (202909) ENGINE = InnoDB,
 PARTITION p202912 VALUES LESS THAN (202912) ENGINE = InnoDB,
 PARTITION p203001 VALUES LESS THAN (203001) ENGINE = InnoDB,
 PARTITION p203003 VALUES LESS THAN (203003) ENGINE = InnoDB,
 PARTITION p203006 VALUES LESS THAN (203006) ENGINE = InnoDB,
 PARTITION p203009 VALUES LESS THAN (203009) ENGINE = InnoDB,
 PARTITION p203012 VALUES LESS THAN (203012) ENGINE = InnoDB,
 PARTITION p203101 VALUES LESS THAN (203101) ENGINE = InnoDB,
 PARTITION p203103 VALUES LESS THAN (203103) ENGINE = InnoDB,
 PARTITION p203106 VALUES LESS THAN (203106) ENGINE = InnoDB,
 PARTITION p203109 VALUES LESS THAN (203109) ENGINE = InnoDB,
 PARTITION p203112 VALUES LESS THAN (203112) ENGINE = InnoDB,
 PARTITION p203812 VALUES LESS THAN (MAXVALUE) ENGINE = InnoDB) */;

-- ----------------------------
-- Records of t_authorize_center_authorize_sync
-- ----------------------------
INSERT INTO `t_authorize_center_authorize_sync` VALUES ('6', '8', 'B6123A3084E14B51A2E93FD93CD80B8520231220132404668', '添加', '13161337653账号对 C3433B2E5F3C锁进行添加密码授权同步', '0', '0', '1', '202312', '2023-12-20 13:24:05', '2023-12-20 15:54:03', '13161337653', '13161337653', null);

-- ----------------------------
-- Table structure for t_authorize_center_authorize_template_data
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_authorize_template_data`;
CREATE TABLE `t_authorize_center_authorize_template_data` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `auth_id` bigint(64) NOT NULL COMMENT '授权id',
  `feature_type` varchar(8) DEFAULT NULL COMMENT '模版id，属于什么介质模版 face，finger',
  `template_feature_id` bigint(64) NOT NULL COMMENT '介质模板id',
  `create_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权模版';

-- ----------------------------
-- Records of t_authorize_center_authorize_template_data
-- ----------------------------

-- ----------------------------
-- Table structure for t_authorize_center_authorize_third_sync_record
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_authorize_third_sync_record`;
CREATE TABLE `t_authorize_center_authorize_third_sync_record` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `auth_id` bigint(64) NOT NULL COMMENT '授权记录id',
  `vendor` varchar(12) NOT NULL COMMENT '厂商',
  `resouce` varchar(12) DEFAULT NULL COMMENT '来源',
  `uuid` varchar(64) DEFAULT NULL COMMENT 'uuid',
  `sync` tinyint(2) DEFAULT NULL COMMENT 'sync',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方授权同步信息';

-- ----------------------------
-- Records of t_authorize_center_authorize_third_sync_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_authorize_center_group_authorize_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_group_authorize_relation`;
CREATE TABLE `t_authorize_center_group_authorize_relation` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `auth_id` bigint(64) NOT NULL COMMENT 'auth_id',
  `group_auth_id` bigint(64) NOT NULL COMMENT 'group_auth_id',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组授权与授权关系表';

-- ----------------------------
-- Records of t_authorize_center_group_authorize_relation
-- ----------------------------

-- ----------------------------
-- Table structure for t_authorize_center_group_relation_comparison
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_group_relation_comparison`;
CREATE TABLE `t_authorize_center_group_relation_comparison` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_auth_id` bigint(64) DEFAULT NULL COMMENT 'group_auth_id',
  `group_type` tinyint(2) DEFAULT NULL COMMENT 'group_type',
  `group_id` bigint(64) DEFAULT NULL COMMENT 'group_id',
  `group_user_lock_id` varchar(64) DEFAULT NULL COMMENT 'group_user_lock_id',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组授权-组员信息比对版本表';

-- ----------------------------
-- Records of t_authorize_center_group_relation_comparison
-- ----------------------------

-- ----------------------------
-- Table structure for t_authorize_center_group_user_device_authorize
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_group_user_device_authorize`;
CREATE TABLE `t_authorize_center_group_user_device_authorize` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `org_id` varchar(32) NOT NULL COMMENT 'org_id',
  `name` varchar(32) DEFAULT NULL COMMENT 'name',
  `group_type` varchar(255) DEFAULT NULL COMMENT 'group_type',
  `user_group_id` bigint(64) NOT NULL COMMENT 'user_group_id',
  `device_group_id` bigint(64) NOT NULL COMMENT 'device_group_id',
  `start_time` timestamp NULL DEFAULT NULL COMMENT 'start_time',
  `end_time` timestamp NULL DEFAULT NULL COMMENT 'end_time',
  `last_auth_update_time` timestamp NULL DEFAULT NULL COMMENT 'last_auth_update_time',
  `action_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'action_status 0=创建 1=进行中 2=成功  3=失败',
  `delay_task_no` varchar(64) NOT NULL COMMENT 'delay_task_no',
  `sync_task_no` mediumtext NOT NULL COMMENT 'sync_task_no',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组授权表';

-- ----------------------------
-- Records of t_authorize_center_group_user_device_authorize
-- ----------------------------

-- ----------------------------
-- Table structure for t_authorize_center_person_authorize
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_person_authorize`;
CREATE TABLE `t_authorize_center_person_authorize` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `org_id` varchar(32) DEFAULT NULL,
  `lock_id` varchar(32) NOT NULL COMMENT '锁主键id',
  `user_account` varchar(32) NOT NULL COMMENT '授权用户账号',
  `user_name` varchar(32) DEFAULT NULL COMMENT '授权用户姓名',
  `user_identity` varchar(16) DEFAULT NULL COMMENT '授权用户身份',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '授权开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '授权结束时间',
  `status` tinyint(2) NOT NULL COMMENT '授权伪删除标识 1正常 0删除',
  `partition_date` int(10) NOT NULL COMMENT '表分区日期格式yyyyMM',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`,`partition_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权表'
/*!50100 PARTITION BY RANGE (partition_date)
(PARTITION p202311 VALUES LESS THAN (202311) ENGINE = InnoDB,
 PARTITION p202401 VALUES LESS THAN (202401) ENGINE = InnoDB,
 PARTITION p202403 VALUES LESS THAN (202403) ENGINE = InnoDB,
 PARTITION p202406 VALUES LESS THAN (202406) ENGINE = InnoDB,
 PARTITION p202409 VALUES LESS THAN (202409) ENGINE = InnoDB,
 PARTITION p202412 VALUES LESS THAN (202412) ENGINE = InnoDB,
 PARTITION p202501 VALUES LESS THAN (202501) ENGINE = InnoDB,
 PARTITION p202503 VALUES LESS THAN (202503) ENGINE = InnoDB,
 PARTITION p202506 VALUES LESS THAN (202506) ENGINE = InnoDB,
 PARTITION p202509 VALUES LESS THAN (202509) ENGINE = InnoDB,
 PARTITION p202512 VALUES LESS THAN (202512) ENGINE = InnoDB,
 PARTITION p202601 VALUES LESS THAN (202601) ENGINE = InnoDB,
 PARTITION p202603 VALUES LESS THAN (202603) ENGINE = InnoDB,
 PARTITION p202606 VALUES LESS THAN (202606) ENGINE = InnoDB,
 PARTITION p202609 VALUES LESS THAN (202609) ENGINE = InnoDB,
 PARTITION p202612 VALUES LESS THAN (202612) ENGINE = InnoDB,
 PARTITION p202701 VALUES LESS THAN (202701) ENGINE = InnoDB,
 PARTITION p202703 VALUES LESS THAN (202703) ENGINE = InnoDB,
 PARTITION p202706 VALUES LESS THAN (202706) ENGINE = InnoDB,
 PARTITION p202709 VALUES LESS THAN (202709) ENGINE = InnoDB,
 PARTITION p202712 VALUES LESS THAN (202712) ENGINE = InnoDB,
 PARTITION p202801 VALUES LESS THAN (202801) ENGINE = InnoDB,
 PARTITION p202803 VALUES LESS THAN (202803) ENGINE = InnoDB,
 PARTITION p202806 VALUES LESS THAN (202806) ENGINE = InnoDB,
 PARTITION p202809 VALUES LESS THAN (202809) ENGINE = InnoDB,
 PARTITION p202812 VALUES LESS THAN (202812) ENGINE = InnoDB,
 PARTITION p202901 VALUES LESS THAN (202901) ENGINE = InnoDB,
 PARTITION p202903 VALUES LESS THAN (202903) ENGINE = InnoDB,
 PARTITION p202906 VALUES LESS THAN (202906) ENGINE = InnoDB,
 PARTITION p202909 VALUES LESS THAN (202909) ENGINE = InnoDB,
 PARTITION p202912 VALUES LESS THAN (202912) ENGINE = InnoDB,
 PARTITION p203001 VALUES LESS THAN (203001) ENGINE = InnoDB,
 PARTITION p203003 VALUES LESS THAN (203003) ENGINE = InnoDB,
 PARTITION p203006 VALUES LESS THAN (203006) ENGINE = InnoDB,
 PARTITION p203009 VALUES LESS THAN (203009) ENGINE = InnoDB,
 PARTITION p203012 VALUES LESS THAN (203012) ENGINE = InnoDB,
 PARTITION p203101 VALUES LESS THAN (203101) ENGINE = InnoDB,
 PARTITION p203103 VALUES LESS THAN (203103) ENGINE = InnoDB,
 PARTITION p203106 VALUES LESS THAN (203106) ENGINE = InnoDB,
 PARTITION p203109 VALUES LESS THAN (203109) ENGINE = InnoDB,
 PARTITION p203112 VALUES LESS THAN (203112) ENGINE = InnoDB,
 PARTITION p203812 VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;

-- ----------------------------
-- Records of t_authorize_center_person_authorize
-- ----------------------------

-- ----------------------------
-- Table structure for t_authorize_center_task
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_task`;
CREATE TABLE `t_authorize_center_task` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `org_id` varchar(64) NOT NULL COMMENT 'org_id',
  `task_no` varchar(64) NOT NULL COMMENT 'task_no',
  `operator` varchar(32) DEFAULT NULL COMMENT 'operator',
  `lock_id` text COMMENT 'lock_id',
  `user_target` text COMMENT 'user_target',
  `auth_start_time` timestamp NULL DEFAULT NULL COMMENT 'auth_start_time',
  `auth_end_time` timestamp NULL DEFAULT NULL COMMENT 'auth_end_time',
  `task_count` int(16) DEFAULT '0' COMMENT 'task_auth_count',
  `auth_success_count` int(16) DEFAULT '0' COMMENT 'auth_success_count',
  `auth_failure_count` int(16) DEFAULT '0' COMMENT 'auth_failure_count',
  `vali_failure_count` int(16) DEFAULT '0' COMMENT 'vali_failure_count',
  `retry_count` int(16) DEFAULT '0',
  `task_type` varchar(16) NOT NULL COMMENT 'task_type',
  `task_status` tinyint(2) NOT NULL COMMENT 'task_status',
  `exception_desc` mediumtext COMMENT 'exception_desc',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COMMENT='授权任务表';

-- ----------------------------
-- Records of t_authorize_center_task
-- ----------------------------
INSERT INTO `t_authorize_center_task` VALUES ('26', '000000001', 'FE4069F8C0554D0CA9ACDAFA5C00D51A20231220132404428', '13161337653', '005448489A47497AA59941EC6D82,', '13161337653,', '2023-12-19 07:05:18', '2024-01-07 00:50:00', '1', '0', '1', '0', '0', '介质授权', '1', '处理成功', '2023-12-20 13:24:04', '2023-12-20 13:23:02', '13161337653', '13161337653', null);
INSERT INTO `t_authorize_center_task` VALUES ('27', '000000001', 'A9C79B301F194E308FAC594B747D937520231220132458859', '13161337653', '005448489A47497AA59941EC6D82,', '13161337653,', '2023-12-19 07:05:18', '2024-01-07 00:50:00', '1', '0', '1', '0', '0', '介质授权', '1', '处理成功', '2023-12-20 13:24:59', '2023-12-20 13:23:56', '13161337653', '13161337653', null);

-- ----------------------------
-- Table structure for t_authorize_center_task_notes
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_task_notes`;
CREATE TABLE `t_authorize_center_task_notes` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `task_no` varchar(80) NOT NULL COMMENT 'task_no',
  `auth_no` varchar(80) NOT NULL COMMENT 'auth_no',
  `auth_content` mediumtext COMMENT 'auth_content',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COMMENT='授权任务授权处理记录表';

-- ----------------------------
-- Records of t_authorize_center_task_notes
-- ----------------------------

-- ----------------------------
-- Table structure for t_authorize_center_task_result
-- ----------------------------
DROP TABLE IF EXISTS `t_authorize_center_task_result`;
CREATE TABLE `t_authorize_center_task_result` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `auth_id` varchar(64) NOT NULL COMMENT 'auth_id',
  `task_no` varchar(64) NOT NULL COMMENT 'task_no',
  `auth_no` varchar(64) NOT NULL COMMENT 'auth_no',
  `lock_id` varchar(64) NOT NULL COMMENT 'lock_id',
  `user_account` varchar(32) NOT NULL COMMENT 'user_account',
  `channel_type` varchar(4) NOT NULL COMMENT 'channel_type',
  `channel_value` mediumtext NOT NULL COMMENT 'channel_value',
  `result` mediumtext COMMENT 'result',
  `status` int(1) DEFAULT NULL COMMENT '0=结果失败  1=结果成功',
  `partition_date` int(10) NOT NULL COMMENT '表分区日期格式yyyyMM',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(32) DEFAULT NULL COMMENT '操作人',
  `update_by` varchar(32) DEFAULT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`,`partition_date`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COMMENT='授权任务授权结果记录表'
/*!50100 PARTITION BY RANGE (partition_date)
(PARTITION p202311 VALUES LESS THAN (202311) ENGINE = InnoDB,
 PARTITION p202401 VALUES LESS THAN (202401) ENGINE = InnoDB,
 PARTITION p202403 VALUES LESS THAN (202403) ENGINE = InnoDB,
 PARTITION p202406 VALUES LESS THAN (202406) ENGINE = InnoDB,
 PARTITION p202409 VALUES LESS THAN (202409) ENGINE = InnoDB,
 PARTITION p202412 VALUES LESS THAN (202412) ENGINE = InnoDB,
 PARTITION p202501 VALUES LESS THAN (202501) ENGINE = InnoDB,
 PARTITION p202503 VALUES LESS THAN (202503) ENGINE = InnoDB,
 PARTITION p202506 VALUES LESS THAN (202506) ENGINE = InnoDB,
 PARTITION p202509 VALUES LESS THAN (202509) ENGINE = InnoDB,
 PARTITION p202512 VALUES LESS THAN (202512) ENGINE = InnoDB,
 PARTITION p202601 VALUES LESS THAN (202601) ENGINE = InnoDB,
 PARTITION p202603 VALUES LESS THAN (202603) ENGINE = InnoDB,
 PARTITION p202606 VALUES LESS THAN (202606) ENGINE = InnoDB,
 PARTITION p202609 VALUES LESS THAN (202609) ENGINE = InnoDB,
 PARTITION p202612 VALUES LESS THAN (202612) ENGINE = InnoDB,
 PARTITION p202701 VALUES LESS THAN (202701) ENGINE = InnoDB,
 PARTITION p202703 VALUES LESS THAN (202703) ENGINE = InnoDB,
 PARTITION p202706 VALUES LESS THAN (202706) ENGINE = InnoDB,
 PARTITION p202709 VALUES LESS THAN (202709) ENGINE = InnoDB,
 PARTITION p202712 VALUES LESS THAN (202712) ENGINE = InnoDB,
 PARTITION p202801 VALUES LESS THAN (202801) ENGINE = InnoDB,
 PARTITION p202803 VALUES LESS THAN (202803) ENGINE = InnoDB,
 PARTITION p202806 VALUES LESS THAN (202806) ENGINE = InnoDB,
 PARTITION p202809 VALUES LESS THAN (202809) ENGINE = InnoDB,
 PARTITION p202812 VALUES LESS THAN (202812) ENGINE = InnoDB,
 PARTITION p202901 VALUES LESS THAN (202901) ENGINE = InnoDB,
 PARTITION p202903 VALUES LESS THAN (202903) ENGINE = InnoDB,
 PARTITION p202906 VALUES LESS THAN (202906) ENGINE = InnoDB,
 PARTITION p202909 VALUES LESS THAN (202909) ENGINE = InnoDB,
 PARTITION p202912 VALUES LESS THAN (202912) ENGINE = InnoDB,
 PARTITION p203001 VALUES LESS THAN (203001) ENGINE = InnoDB,
 PARTITION p203003 VALUES LESS THAN (203003) ENGINE = InnoDB,
 PARTITION p203006 VALUES LESS THAN (203006) ENGINE = InnoDB,
 PARTITION p203009 VALUES LESS THAN (203009) ENGINE = InnoDB,
 PARTITION p203012 VALUES LESS THAN (203012) ENGINE = InnoDB,
 PARTITION p203101 VALUES LESS THAN (203101) ENGINE = InnoDB,
 PARTITION p203103 VALUES LESS THAN (203103) ENGINE = InnoDB,
 PARTITION p203106 VALUES LESS THAN (203106) ENGINE = InnoDB,
 PARTITION p203109 VALUES LESS THAN (203109) ENGINE = InnoDB,
 PARTITION p203112 VALUES LESS THAN (203112) ENGINE = InnoDB,
 PARTITION p203812 VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;

-- ----------------------------
-- Records of t_authorize_center_task_result
-- ----------------------------
INSERT INTO `t_authorize_center_task_result` VALUES ('19', '8', 'FE4069F8C0554D0CA9ACDAFA5C00D51A20231220132404428', '79CA9C254CEB41CCB128D86F7551F4CF20231220132404553', '005448489A47497AA59941EC6D82', '13161337653', '101', '135425', '介质:[13161337653(135425)],锁名称：[C3433B2E5F3C(C3433B2E5F3C)] 处理结果：[授权生成流程,系统处理成功！！！]；', '1', '202312', '2023-12-20 13:24:05', null, '13161337653', null, null);
INSERT INTO `t_authorize_center_task_result` VALUES ('20', '8', 'A9C79B301F194E308FAC594B747D937520231220132458859', '14A18AC3EB384E7792C3D23EF0FAFE0C20231220132458951', '005448489A47497AA59941EC6D82', '13161337653', '101', '135425', '介质:[13161337653(135425)],锁名称：[C3433B2E5F3C(C3433B2E5F3C)] 处理结果：[授权生成流程,系统处理成功！！！]；', '1', '202312', '2023-12-20 13:24:59', null, '13161337653', '13161337653', null);

-- ----------------------------
-- Table structure for t_third_server_auth_return_log
-- ----------------------------
DROP TABLE IF EXISTS `t_third_server_auth_return_log`;
CREATE TABLE `t_third_server_auth_return_log` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `msgId` bigint(64) DEFAULT NULL COMMENT '消息id',
  `userAccount` varchar(64) NOT NULL COMMENT '账号',
  `phone` varchar(64) DEFAULT NULL COMMENT '手机号',
  `serialNo` varchar(32) NOT NULL COMMENT '设备序列号也可存mac',
  `method` varchar(255) NOT NULL COMMENT '方法',
  `msgJson` text,
  `resultContent` varchar(4000) DEFAULT NULL,
  `resultCode` char(1) DEFAULT NULL COMMENT '软件版本',
  `createTime` timestamp NULL DEFAULT NULL,
  `deviceOnline` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_msgId_mac` (`msgId`,`serialNo`,`method`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_third_server_auth_return_log
-- ----------------------------
