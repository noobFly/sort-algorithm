package com.noob.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;

import org.hibernate.validator.constraints.Length;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import com.alibaba.fastjson.JSON;
import com.noob.controller.NoteController.GroupTestDTO.InitAction;
import com.noob.controller.NoteController.GroupTestDTO.MegreAction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/note")
@RestController
public class NoteController {

	private BService service;

	/*
	 * @Autowired public void setService(BService service) { this.service = service;
	 * System.out.println("这里是@Autowired");
	 * 
	 * }
	 */

	/*
	 * public NoteController(BService b) { System.out.println("这里是Constructor"); }
	 */

	@PostConstruct
	public void init() {
		System.out.println("这里是@PostConstruct");
	}

	private static final String TMP = System.getProperty("java.io.tmpdir") + File.separator;

	/**
	 * 可支持多文件上传。
	 * <p>
	 * #文件写入磁盘的阈值 spring.servlet.multipart.file-size-threshold: 0
	 * <p>
	 * # 最大文件大小 spring.servlet.multipart.max-file-size: 200MB
	 * <p>
	 * # 最大请求大小 spring.servlet.multipart.max-request-size: 215MB
	 * {@link MultipartAutoConfiguration}
	 * 
	 * @param multipartFile
	 */
	@PostMapping(value = "/testMultipartFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String testMultipartFile(
			@RequestParam(value = "file[]", required = true) List<MultipartFile> multipartFile) {
		String fileName = multipartFile.get(0).getOriginalFilename();
		String pathname = TMP + fileName;
		try {
			multipartFile.get(0).transferTo(new File(pathname));
		} catch (Exception e) {
			log.error("testMultipartFile exception!", e);
		}
		return fileName;
	}

	@PostMapping(value = "/upload")
	public String upload(HttpServletRequest request) {
		MultipartHttpServletRequest multipartRequest = WebUtils.getNativeRequest(request,
				MultipartHttpServletRequest.class);
		multipartRequest.getFileNames().forEachRemaining(t -> log.info(t));
		List<MultipartFile> multipartFiles = multipartRequest.getFiles("file[]");
		MultipartFile[] multipartFile = multipartFiles.toArray(new MultipartFile[0]);
		String fileName = multipartFile[0].getOriginalFilename();
		String pathname = TMP + fileName;
		return pathname;
	}

	/**
	 * GET POST 都可以正确读入
	 * <p>
	 * 使用 @RequestBody
	 * 一定要指定contentType(MediaType.APPLICATION_JSON_UTF8)。否则在执行到AbstractMessageConverterMethodArgumentResolver.readWithMessageConverters时默认设置为MediaType.APPLICATION_OCTET_STREAM.
	 * 因没有支持的HttpMessageConverter，导致解析不了入参所以 报错HttpMediaTypeNotSupportedException
	 * <p>
	 * 对 @RequestBody | @RequestParam 的入参解析的路由在
	 * InvocableHandlerMethod.getMethodArgumentValues时执行HandlerMethodArgumentResolverComposite.resolveArgument选择HandlerMethodArgumentResolver来解析
	 * <li>@RequestBody 执行了RequestResponseBodyMethodProcessor ,
	 * <li>@RequestParam 执行了 RequestParamMethodArgumentResolver ,
	 * <li>@PathVariable 执行了 PathVariableMethodArgumentResolver
	 * 
	 * @param map
	 * @param param1
	 * @param request
	 */
	@RequestMapping("/test1")
	public void test1(@RequestBody Map<String, String> map, @RequestParam String param1, HttpServletRequest request) {
		log.info("requestMethod: {}", request.getMethod());
		log.info("contentType: {}", request.getContentType());

		log.info(JSON.toJSONString(map));
		log.info(param1);

	}

	/**
	 * GET POST 都可以正确读入
	 * <p>
	 * 通常@RequestBody的方式会做成DTO对象,便于前端传值.
	 * 
	 * @param list
	 * @param request
	 */
	@RequestMapping("/test2")
	public void test2(@RequestBody List<String> list, @RequestParam List<String> list2, HttpServletRequest request) {
		log.info("requestMethod: {}", request.getMethod());
		log.info("contentType: {}", request.getContentType());
		log.info(JSON.toJSONString(list));
		log.info(JSON.toJSONString(list2));

	}

	@RequestMapping("/test3/{info}")
	public void test3(@PathVariable String info, HttpServletRequest request) {

		log.info("requestMethod: {}", request.getMethod());
		log.info("contentType: {}", request.getContentType());
		log.info(info);
	}

	@RequestMapping("/testGroupDefault")
	public String testGroupDefault(@RequestBody @Validated GroupTestDTO test) {
		return "testGroupDefault";
	}

	@RequestMapping("/testGroupParent")
	public String testGroupNormal(@RequestBody @Validated(InitAction.class) GroupTestDTO test) {
		return "testGroupParent";
	}

	@RequestMapping("/testGroupExtends")
	public String testGroupExtends(@RequestBody @Validated(MegreAction.class) GroupTestDTO test) {
		return "testGroupExtends";
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class GroupTestDTO {
		@Length(max = 1, message = "address错误")
		public String address;
		@Length(max = 1, message = "name错误", groups = { Default.class, InitAction.class })
		public String name;
		@Length(max = 1, message = "code错误", groups = { InitAction.class })
		public String code;
		@Length(max = 1, message = "phone错误", groups = { MegreAction.class })
		public String phone;

		public interface InitAction {}

		public interface MegreAction extends InitAction {}
	}

}
