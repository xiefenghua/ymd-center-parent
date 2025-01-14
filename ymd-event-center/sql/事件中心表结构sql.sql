/*
Navicat MySQL Data Transfer

Source Server         : 109临时测试环境
Source Server Version : 50742
Source Host           : 192.168.10.109:3306
Source Database       : event_center

Target Server Type    : MYSQL
Target Server Version : 50742
File Encoding         : 65001

Date: 2024-01-02 14:42:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ymd_event_center_log
-- ----------------------------
DROP TABLE IF EXISTS `ymd_event_center_log`;
CREATE TABLE `ymd_event_center_log` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `topic` varchar(128) NOT NULL COMMENT '主题名称topic',
  `task_no` varchar(128) NOT NULL COMMENT '事件号',
  `log_type` varchar(64) DEFAULT NULL COMMENT '日志分类',
  `weight` bigint(64) DEFAULT '0' COMMENT '权重',
  `post_content` varchar(4000) DEFAULT NULL COMMENT '操作类和方法',
  `param_body` longtext COMMENT '任务参数',
  `result_code` varchar(8) DEFAULT '1' COMMENT '0=结果失败  1=结果成功',
  `result` longtext COMMENT '同步结果',
  `post_time` bigint(64) DEFAULT NULL COMMENT '响应时间',
  `retry_count` int(16) DEFAULT '0' COMMENT '尝试次数',
  `create_auth_user_id` varchar(64) DEFAULT NULL COMMENT '创建者userid',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `partition_date` int(10) NOT NULL COMMENT '表分区日期格式yyyyMM',
  PRIMARY KEY (`id`,`partition_date`),
  KEY `idx-topic` (`topic`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=261 DEFAULT CHARSET=utf8mb4 COMMENT='事件中心--事件日志表(t_event_center_log)'
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
-- Records of ymd_event_center_log
-- ----------------------------

-- ----------------------------
-- Table structure for ymd_event_center_rule
-- ----------------------------
DROP TABLE IF EXISTS `ymd_event_center_rule`;
CREATE TABLE `ymd_event_center_rule` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `rule_name` varchar(32) DEFAULT NULL COMMENT '规则名称',
  `rule_remark` varchar(2048) DEFAULT NULL COMMENT '规则描述',
  `rule_level` varchar(16) DEFAULT NULL COMMENT '规则优先级,优先级范围为0-15，数字越大优先级越高',
  `rule_action` varchar(2048) DEFAULT NULL COMMENT '规则语句 规则语句：',
  `rule_status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '规则状态 1=有效 0=无效',
  `rule_is_all` tinyint(2) DEFAULT '0' COMMENT '是否全局（1=全局，0=局部（默认）全局规则只能允许一条）',
  `create_auth_user_id` varchar(64) DEFAULT NULL COMMENT '创建者userId',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='事件中心--规则配置表 ';

-- ----------------------------
-- Records of ymd_event_center_rule
-- ----------------------------
INSERT INTO `ymd_event_center_rule` VALUES ('2', '云门道事件中心全局规则', '云门道事件中心全局规则', '15', '{\"ack\":\"AUTO\",\"durable\":\"true\",\"retry_count\":3}', '1', '1', '36BCB20221111172539463555', '2023-08-28 17:13:26', null);

-- ----------------------------
-- Table structure for ymd_event_center_topic_register
-- ----------------------------
DROP TABLE IF EXISTS `ymd_event_center_topic_register`;
CREATE TABLE `ymd_event_center_topic_register` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `topic` varchar(128) NOT NULL COMMENT '主题名称topic(delay_业务类型_主题类别_操作类型_唯一码_topic)',
  `job_type` varchar(255) NOT NULL COMMENT '业务分类 ',
  `type` varchar(255) NOT NULL COMMENT '主题类别',
  `opt_type` varchar(255) NOT NULL COMMENT '主题类别操--作类型',
  `mqtt_type` varchar(8) NOT NULL DEFAULT 'mqtt' COMMENT 'mqtt类型(mqtt,rabbitmq)',
  `call_back_type` tinyint(2) DEFAULT '1' COMMENT '回调接口形式   内部调用=1  http形式=2',
  `http_url` varchar(4000) DEFAULT NULL COMMENT 'http地址，CallBackType=2 此参数必填    接口要求 post 参数以body形式传输json',
  `class_name` varchar(1000) DEFAULT NULL COMMENT 'spring业务接口类路径，如果没有业务接口类只实现EventCenterConsumerService的话就填业务实现类， CallBackType=1 此参数必填 ',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT 'topic注册状态 0=删除 1=开放 ',
  `remark` varchar(2000) DEFAULT NULL,
  `create_auth_user_id` varchar(64) DEFAULT NULL COMMENT '操作者userid',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx-topic` (`topic`) USING HASH,
  UNIQUE KEY `idx-type` (`type`) USING BTREE,
  KEY `idx-topic-type` (`topic`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='事件中心--事件中心topic注册表';

-- ----------------------------
-- Records of ymd_event_center_topic_register
-- ----------------------------
INSERT INTO `ymd_event_center_topic_register` VALUES ('4', 'ymd_event_center_event_consumer_topic', 'event_center', 'event', 'consumer', 'rabbitmq', '2', 'http://localhost:9885/ymd/web/center/authorizeCenter/consumer/rabbitmq/eventCenterMqConsumer', '已用', '1', '测试topic', '36BCB20221111172539463555', '2023-08-29 16:49:25', '2023-12-18 13:50:31');
INSERT INTO `ymd_event_center_topic_register` VALUES ('10', 'ymd_authorize_center_channel_1_group_authorize_generate_push_topic', 'authorize_center_channel_1', 'group_authorize_generate', 'push', 'rabbitmq', '2', 'http://localhost:9885/ymd/web/center/authorizeCenter/consumer/rabbitmq/groupAuthorizeGenerateConsumer', '已用', '1', '组授权任务topic', '36BCB20221111172539463555', '2023-08-30 14:52:09', '2023-12-18 13:50:31');
INSERT INTO `ymd_event_center_topic_register` VALUES ('11', 'ymd_authorize_center_channel_2_user_authorize_sync_topic', 'authorize_center_channel_2', 'user_authorize_sync', 'push', 'rabbitmq', '2', 'http://localhost:9885/ymd/web/center/authorizeCenter/consumer/rabbitmq/userAuthorizeSyncConsumer', '已用', '1', '授权立即同步topic', '36BCB20221111172539463555', '2023-08-30 14:55:22', '2023-12-18 13:50:31');
INSERT INTO `ymd_event_center_topic_register` VALUES ('13', 'ymd_authorize_center_channel_2_third_register_device_up_topic', 'authorize_center_channel_2', 'third_register_device', 'up', 'mqtt', '2', 'http://localhost:9885/ymd/web/center/authorizeCenter/consumer/mqtt/thirdRegisterDeviceConsumer', '已用', '1', '第三方设备设备注册', '36BCB20221111172539463555', '2023-10-24 10:10:34', '2023-12-18 13:50:31');
INSERT INTO `ymd_event_center_topic_register` VALUES ('14', 'ymd_authorize_center_channel_3_third_sync_auth_down_topic/#', 'authorize_center_channel_3', 'third_sync_auth', 'down', 'mqtt', '2', 'http://localhost:9885/ymd/web/center/authorizeCenter/consumer/mqtt/thirdSyncAuthConsumer', '已用', '1', '第三方设备授权同步', '36BCB20221111172539463555', '2023-10-24 10:12:23', '2023-12-18 13:50:31');
INSERT INTO `ymd_event_center_topic_register` VALUES ('15', 'ymd_authorize_center_channel_2_third_sync_config_down_topic/#', 'authorize_center_channel_2', 'third_sync_config', 'down', 'mqtt', '2', 'http://localhost:9885/ymd/web/center/authorizeCenter/consumer/mqtt/thirdSyncConfigConsumer', '已用', '1', '第三方设备配置同步', '36BCB20221111172539463555', '2023-10-24 10:12:40', '2023-12-18 13:50:31');
INSERT INTO `ymd_event_center_topic_register` VALUES ('16', 'ymd_authorize_center_channel_2_third_sync_heart_up_topic', 'authorize_center_channel_2', 'third_sync_heart', 'up', 'mqtt', '2', 'http://localhost:9885/ymd/web/center/authorizeCenter/consumer/mqtt/thirdSyncHeartConsumer', '已用', '1', '第三方设备心跳上报', '36BCB20221111172539463555', '2023-10-24 10:12:53', '2023-12-18 13:50:31');
INSERT INTO `ymd_event_center_topic_register` VALUES ('17', 'ymd_authorize_center_channel_2_third_sync_open_door_record_up_topic', 'authorize_center_channel_2', 'third_sync_open_door_record', 'up', 'mqtt', '2', 'http://localhost:9885/ymd/web/center/authorizeCenter/consumer/mqtt/thirdSyncOpenDoorRecordConsumer', '已用', '1', '第三方设备开门上报', '36BCB20221111172539463555', '2023-10-24 10:13:18', '2023-12-18 13:50:31');
INSERT INTO `ymd_event_center_topic_register` VALUES ('19', 'ymd_authorize_center_channel_3_third_sync_auth_return_up_topic', 'authorize_center_channel_3', 'third_sync_auth_return', 'up', 'mqtt', '2', 'http://localhost:9885/ymd/web/center/authorizeCenter/consumer/mqtt/thirdSyncAuthReturnConsumer', '已用', '1', '第三方设备授权同步上报', '36BCB20221111172539463555', '2023-10-24 10:13:49', '2023-12-18 13:50:31');
INSERT INTO `ymd_event_center_topic_register` VALUES ('20', 'ymd_authorize_center_channel_4_channel_auth_push_topic', 'authorize_center_channel_4', 'channel_auth', 'push', 'mqtt', '2', 'http://localhost:9885/ymd/web/center/authorizeCenter/consumer/mqtt/channelAuthConsumer', '已用', '1', '授权任务notes授权处理', '36BCB20221111172539463555', '2023-10-24 10:14:20', '2023-12-18 13:50:31');
INSERT INTO `ymd_event_center_topic_register` VALUES ('22', 'ymd_authorize_center_channel_5_auth_thread_to_mq_push_topic', 'authorize_center_channel_5', 'auth_thread_to_mq', 'push', 'mqtt', '2', 'http://localhost:9885/ymd/web/center/authorizeCenter/consumer/mqtt/authThreadToMqConsumer', '已用', '1', '授权任务mqtt开始执行', '36BCB20221111172539463555', '2023-10-24 10:16:42', '2023-12-18 13:50:31');
INSERT INTO `ymd_event_center_topic_register` VALUES ('26', '/68321000001/zzj/00000010/update_auth/#', 'event_center', 'bt_face_auth_event', 'consumer', 'mqtt', '1', null, '已用', '1', '比特设备人脸授权推送主题', '36BCB20221111172539463555', '2023-12-07 19:05:44', '2023-12-18 13:51:24');

-- ----------------------------
-- Table structure for ymd_event_center_topic_rule
-- ----------------------------
DROP TABLE IF EXISTS `ymd_event_center_topic_rule`;
CREATE TABLE `ymd_event_center_topic_rule` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(64) NOT NULL COMMENT 'topic注册表id',
  `rule_id` bigint(64) NOT NULL COMMENT '规则id',
  `create_auth_user_id` varchar(64) DEFAULT NULL COMMENT '操作者userId',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_topic_rule_id` (`topic_id`,`rule_id`) USING BTREE,
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_rule_id` (`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='事件中心--事件topic与规则关系表';

-- ----------------------------
-- Records of ymd_event_center_topic_rule
-- ----------------------------

-- ----------------------------
-- Table structure for ymd_event_center_topic_task_record
-- ----------------------------
DROP TABLE IF EXISTS `ymd_event_center_topic_task_record`;
CREATE TABLE `ymd_event_center_topic_task_record` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `topic` varchar(128) NOT NULL COMMENT '主题名称topic',
  `task_name` varchar(128) DEFAULT NULL COMMENT '任务名称',
  `task_no` varchar(128) NOT NULL COMMENT '任务唯一code',
  `push_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '延迟队列推送时间点',
  `mq_return_code` varchar(64) DEFAULT NULL,
  `return_body` longtext,
  `retry_count` int(16) DEFAULT '0' COMMENT '重试次数',
  `error_retry_count` int(16) DEFAULT '0' COMMENT '失败重试次数',
  `exchange` varchar(255) DEFAULT NULL COMMENT '交换机',
  `routing_key` varchar(255) DEFAULT NULL COMMENT '路由键',
  `weight` bigint(64) DEFAULT NULL COMMENT '权重',
  `delay_time` int(64) NOT NULL COMMENT '延迟时长 单位秒',
  `task_msg_body` longtext NOT NULL COMMENT '队列报文',
  `mq_message_id` varchar(64) DEFAULT NULL,
  `push_status` tinyint(2) DEFAULT NULL COMMENT '推送状态 1=已推送 0=未推送',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `create_auth_user_id` varchar(255) DEFAULT NULL COMMENT '操作者',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_task_no` (`task_no`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ymd_event_center_topic_task_record
-- ----------------------------
