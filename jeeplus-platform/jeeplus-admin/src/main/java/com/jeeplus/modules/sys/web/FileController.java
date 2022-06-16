/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.web;

import com.google.common.collect.Lists;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.FileUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.config.properties.FileProperties;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.FileData;
import com.jeeplus.modules.sys.utils.FileKit;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * 文件管理Controller
 * @author liugf
 * @version 2018-01-21
 */
@RestController
@RequestMapping("/sys/file")
public class FileController extends BaseController {

	@Autowired
	FileProperties fileProperties;

	public void init() {
		FileUtils.createDirectory(FileKit.getAttachmentDir());
		FileUtils.createDirectory(FileKit.getMyDocDir());
		FileUtils.createDirectory(FileKit.getShareBaseDir());
	}

	/**
	 * 文件管理列表数据
	 */
	@RequiresPermissions("user")
	@RequestMapping("list")
	public List<FileData> data(HttpServletRequest request, HttpServletResponse response, Model model) {
	    init();
		List <File> targetFiles = Lists.newArrayList();
		targetFiles.add(new File(FileKit.getAttachmentDir()));
		targetFiles.add(new File(FileKit.getMyDocDir()));
		targetFiles.add(new File(FileKit.getShareBaseDir()));
		return FileKit.getFileList("files", Lists.newArrayList(targetFiles));
	}


	/**
	 * 移动文件或文件夹
	 */
	@RequiresPermissions("user")
	@RequestMapping("move")
	public List move(@Param("source") String source, @Param("target") String target) throws IOException{
		List list = Lists.newArrayList();
		target = FileKit.getFileDir (target);
		String[] sourceArra = source.split(",");
		for(String s:sourceArra){
			s = FileKit.getFileDir (s);
			String fileName = StringUtils.substringAfterLast(s.replace("\\","/"),"/");
			if(FileUtils.isFolder(s)){
				File targetFolder = FileUtils.getAvailableFolder(target+"/"+fileName, 0);
				FileUtils.moveDirectory(new File(s),targetFolder);
				Map map = new HashMap();
				map.put("id", FileKit.transDirToUrl(targetFolder.getAbsolutePath()));
				map.put("value", targetFolder.getName());
				list.add(map);
			}else{
					File targetFile = FileUtils.getAvailableFile(target+"/"+fileName, 0);
					FileUtils.moveFile(new File(s), targetFile);
					new File(s).deleteOnExit();
					Map map = new HashMap();
					map.put("id",FileKit.transDirToUrl(targetFile.getAbsolutePath()));
					map.put("value", targetFile.getName());
					list.add(map);
			}

		}

		return list;
	}

	/**
	 * copy文件文件夹
	 */
	@RequiresPermissions("user")
	@RequestMapping("copy")
	public List copy(@Param("source") String source, @Param("target") String target) {
		List list = Lists.newArrayList();
		String[] sourceArra = source.split(",");
		for(String s:sourceArra){
			s = FileKit.getFileDir (s);
			String fileName = StringUtils.substringAfterLast(s.replace("\\","/"),"/");
			if(FileUtils.isFolder(s)){
				File targetFolder =  FileUtils.getAvailableFolder(target+"/"+fileName, 0);
				if(FileUtils.copyDirectory(s, targetFolder.getAbsolutePath())){
					Map map = new HashMap();
					map.put("id",FileKit.transDirToUrl(targetFolder.getAbsolutePath()));
					map.put("value", targetFolder.getName());
					list.add(map);
				}
			}else{
				File targetFile = FileUtils.getAvailableFile(target+"/"+fileName, 0);
				if(FileUtils.copyFile(s,targetFile.getAbsolutePath())){
					Map map = new HashMap();
					map.put("id",FileKit.transDirToUrl(targetFile.getAbsolutePath()));
					map.put("value", targetFile.getName());
					list.add(map);
				}
			}

		}

		return list;
	}

	/**
	 * 删除文件管理
	 */
	@RequiresPermissions("user")
	@RequestMapping("download")
	public void download(@Param("source") String source, HttpServletRequest request, HttpServletResponse response) throws Exception{
		AjaxJson j = new AjaxJson();
		source = FileKit.getFileDir (source);
		File file = new File(source);
		if (file == null || !file.exists()) {
			throw new FileNotFoundException("请求的文件不存在");
		}
		OutputStream out = null;
		try {
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			String agent = (String)request.getHeader("USER-AGENT");
			String fileName = file.getName();
			if(agent != null && agent.indexOf("MSIE") == -1) {
// FF
				String enableFileName = "=?UTF-8?B?" + (new String(Base64.getEncoder().encode(fileName.getBytes("UTF-8")))) + "?=";
				response.setHeader("Content-Disposition", "attachment; filename=" + enableFileName); }
			else {
// IE
				String enableFileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
				response.setHeader("Content-Disposition", "attachment; filename=" + enableFileName);
			}
//			response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
			out = response.getOutputStream();
			out.write(FileUtils.readFileToByteArray(file));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	/**
	 * 删除文件管理
	 */
	@RequiresPermissions("user")
	@RequestMapping("remove")
	public List delete(@Param("source") String source) {
		List list = Lists.newArrayList();
		//先删除文件
		String[] sourceArra = source.split(",");
		for(String s:sourceArra){
			s = FileKit.getFileDir (s);
			FileUtils.delFile(s);
			Map map = new HashMap();
			map.put("id",FileKit.transDirToUrl(s));
			map.put("value", StringUtils.substringAfterLast(s.replace("\\","/"),"/"));
			list.add(map);
		}

		return list;
	}

	@RequiresPermissions("user")
	@RequestMapping("createFolder")
	public Map create(@Param("source") String source, @Param("target") String target) {
		Map map = new HashMap();
		target = FileKit.getFileDir (target);
		String targetFolderPath = target + "/" + source;
		File targetFolder = FileUtils.getAvailableFolder(targetFolderPath, 0);
		boolean result = FileUtils.createDirectory(targetFolder.getAbsolutePath());
		if(result){
			map.put("id", FileKit.transDirToUrl(targetFolder.getAbsolutePath()));
			map.put("value", targetFolder.getName());

		}
		return map;
	}

	@RequiresPermissions("user")
	@RequestMapping("rename")
	public Map rename(@Param("source") String source, @Param("target") String target) {
		Map map = new HashMap();
		source = FileKit.getFileDir (source);
		File sourceFile = new File(source);
		File targetFile = new File(sourceFile.getParent()+"/"+target);
		if(sourceFile.isDirectory()){
			targetFile = FileUtils.getAvailableFolder(targetFile.getAbsolutePath(),0);
		}else{
			targetFile = FileUtils.getAvailableFile(targetFile.getAbsolutePath(), 0);
		}
		boolean result = sourceFile.renameTo(targetFile);
		if(result){
			map.put("id", FileKit.transDirToUrl(targetFile.getAbsolutePath()));
			map.put("value", targetFile.getName());

		}
		return map;
	}


	/**
	 * 上传文件
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@RequiresPermissions("user")
	@RequestMapping("upload")
	public Map upload( HttpServletRequest request, HttpServletResponse response,MultipartFile upload) throws Exception {
		String target = request.getParameter("target");
		String realPath = FileKit.getFileDir (target);
		Map map = new HashMap();
		// 判断文件是否为空
		if (!upload.isEmpty()) {
			String name = upload.getOriginalFilename();
			if(fileProperties.isAvailable (name)){
				// 文件保存路径
				// 转存文件
				FileUtils.createDirectory(realPath);
				String filePath = realPath +"/"+  name;
				File newFile = FileUtils.getAvailableFile(filePath,0);
				upload.transferTo(newFile);
				map.put("id", FileKit.transDirToUrl(newFile.getAbsolutePath()));
				map.put("value", newFile.getName());
				map.put("type", FileKit.getFileType(newFile.getName()));
			}else{
				map.put("id", "");
				map.put("value", "");
				map.put("type", "");
				System.out.println ("非法文件，不允许上传!");
			}
		}


		return map;
	}


	/**
	 * 获取文件网络地址
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@RequiresPermissions("user")
	@RequestMapping("getUrl")
	public AjaxJson getUrl(@Param("dir") String dir) throws IllegalStateException, IOException {
		String type = FileKit.getFileType(dir);
		return AjaxJson.success().put("url", dir).put("type",type);
	}

	/**
	 * 下载文件
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@RequiresPermissions("user")
	@RequestMapping("webupload/upload")
	public AjaxJson webupload( HttpServletRequest request, HttpServletResponse response,MultipartFile file) throws IllegalStateException, IOException {
        AjaxJson j = new AjaxJson();

		String uploadPath = request.getParameter("uploadPath");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH )+1;
		String fileUrl = FileKit.getAttachmentUrl()+uploadPath+"/"+year+"/"+month+"/";
		String fileDir = FileKit.getAttachmentDir()+uploadPath+"/"+year+"/"+month+"/";
		// 判断文件是否为空
		if (!file.isEmpty()) {
			String name = file.getOriginalFilename ();
			if(fileProperties.isAvailable (name)) {
				// 文件保存路径
				// 转存文件
				FileUtils.createDirectory (fileDir);
				String filePath = fileDir + name;
				File newFile = FileUtils.getAvailableFile (filePath, 0);
				file.transferTo (newFile);
				j.put ("id", FileKit.transDirToUrl (newFile.getAbsolutePath ()));
				j.put ("url", fileUrl + newFile.getName ());
				return j;
			}else{
				return AjaxJson.error ("请勿上传非法文件!");
			}
		}else {
			return AjaxJson.error ("文件不存在!");
		}
	}
	/**
	 * 批量删除文件管理 按id
	 */
	@RequiresPermissions("user")
	@RequestMapping("webupload/delete")
	public AjaxJson delFile(String id) {
		id = FileKit.getFileDir (id);
		if(FileUtils.delFile(id)){
			return AjaxJson.success("删除文件成功");
		}else{
			return AjaxJson.error("删除文件失败");
		}
	}

	/**
	 * 批量删除文件管理 按url
	 */
	@RequiresPermissions("user")
	@RequestMapping("webupload/deleteByUrl")
	public AjaxJson delFileByUrl(String url) {
		String id = FileKit.getFileDir(url);
		if(FileUtils.delFile(id)){
			return AjaxJson.success("删除文件成功");
		}else{
			return AjaxJson.error("删除文件失败");
		}

	}



}
