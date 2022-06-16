package com.jeeplus.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 系统配置Entity
 */
@TableName("sys_config")
public class SysConfig implements Serializable {
	@TableId(type = IdType.ASSIGN_UUID)
	private String id;
	/*
		邮箱配置信息
	 */
	private String smtp;		// 邮箱服务器地址
	private String port;		// 邮箱服务器端口
	@TableField("mailname")
	private String mailName;		// 系统邮箱地址
	@TableField("mailpassword")
	private String mailPassword;		// 系统邮箱密码
	/*
		阿里大鱼配置信息
	 */
	@TableField("accesskeyid")
	private String accessKeyId;// 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找);
	@TableField("accesskeysecret")
	private String accessKeySecret; // 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
	@TableField("signature")
	private String signature; //必填:短信签名-可在短信控制台中找到
	@TableField("templatecode")
	private String templateCode;//必填:短信模板-可在短信控制台中找到-->
	/*
	   外观配置
	 */
	@TableField("defaulttheme")
	private String defaultTheme;//默认主题
	@TableField("defaultlayout")
	private String defaultLayout;
	@TableField("productname")
	private String productName;//产品名称
	private String logo;//产品logo;


	/**
	 * 首页配置
	 */
	private String homeUrl;

	/*
	  登录配置
	 */
	@TableField("multiaccountlogin")
	private String multiAccountLogin;//允许多登录1，不允许0
	@TableField("singlelogintype")
	private String singleLoginType; //后登陆踢出先登录1，已经登陆禁止再登陆2.

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getMailName() {
		return mailName;
	}

	public void setMailName(String mailName) {
		this.mailName = mailName;
	}

	public String getMailPassword() {
		return mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getDefaultTheme() {
		return defaultTheme;
	}

	public void setDefaultTheme(String defaultTheme) {
		this.defaultTheme = defaultTheme;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getMultiAccountLogin() {
		return multiAccountLogin;
	}

	public void setMultiAccountLogin(String multiAccountLogin) {
		this.multiAccountLogin = multiAccountLogin;
	}

	public String getSingleLoginType() {
		return singleLoginType;
	}

	public void setSingleLoginType(String singleLoginType) {
		this.singleLoginType = singleLoginType;
	}

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}

	public String getDefaultLayout() {
		return defaultLayout;
	}

	public void setDefaultLayout(String defaultLayout) {
		this.defaultLayout = defaultLayout;
	}
}
