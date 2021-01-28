-- MySQL dump 10.13  Distrib 5.7.30, for macos10.14 (x86_64)
--
-- Host: localhost    Database: user_center
-- ------------------------------------------------------
-- Server version	5.7.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `app`
--

DROP TABLE IF EXISTS `app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `appName` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名称',
  `appUrl` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'app链接地址',
  `appId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'appId,应用唯一编号',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `role` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '绑定的角色',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除  0:未删除, 1:已删除',
  `createBy` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `createDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `updateBy` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `updateDate` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='appId';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `app_resource`
--

DROP TABLE IF EXISTS `app_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `appId` int(11) DEFAULT NULL COMMENT '应用id',
  `resource` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源key',
  `description` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
  `createBy` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建人',
  `createDate` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateBy` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新人',
  `updateDate` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_app_resource`
--

DROP TABLE IF EXISTS `role_app_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_app_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `roleId` int(11) NOT NULL COMMENT '角色id',
  `appId` int(11) NOT NULL COMMENT 'app应用id: 冗余字段 app#id',
  `resourceId` int(11) DEFAULT NULL COMMENT 'app资源id: app_resource#id',
  `deleted` int(11) DEFAULT NULL COMMENT '是否删除 0:未删除 1:已删除',
  `createDate` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `createBy` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建人',
  `updateDate` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateBy` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色-应用资源';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `staff` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userName` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '用户登录名: 形如 yuanchengzong',
  `email` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '邮箱',
  `realName` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '真实姓名',
  `idNumber` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '证件号: 基本上是身份证号',
  `mobile` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '手机号码',
  `sex` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '性别',
  `birthDay` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '出生日期',
  `ntPlaceCode` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '户籍所在地编码',
  `ntPlaceName` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '户籍所在地',
  `birthPlaceCode` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '出生地编码',
  `birthPlaceName` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '出生地',
  `nation` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '民族',
  `marriage` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '婚姻情况',
  `permanreside` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '户口所在地',
  `address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '居住地址',
  `technical` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '技术方向',
  `psnCode` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '唯一编号',
  `role` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '角色id,逗号隔开',
  `status` int(11) DEFAULT '0' COMMENT '状态: 0:正常 1:禁用',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除 0:未删 1:已删',
  `createDate` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `createBy` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '创建人',
  `updateDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `updateBy` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工雇员库';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `staff_role`
--

DROP TABLE IF EXISTS `staff_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `staff_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
  `description` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `remark` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
  `createBy` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `createDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `updateBy` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `updateDate` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userName` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
  `password` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
  `role` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色',
  `email` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号码',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态: 0:正常,1:禁用',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0: 否 1: 是',
  `createDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `updateDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `createBy` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateBy` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-01-28 17:49:57
