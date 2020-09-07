package com.noob.test.mock;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

// 测试API各种访问方式的Mock
public class ControllerTest extends BaseTest {
	@Test
	public void test() {
		testMultipartFile();
		test1();
		test2();
		test3();
	}

	public void testMultipartFile() {
		MockMultipartHttpServletRequestBuilder mockMulHttpServletRequestBuilder;
		try {
			File testFile = new File("C:\\Users\\admin\\Desktop\\TiDB 适用场景.pdf");

			mockMulHttpServletRequestBuilder = MockMvcRequestBuilders.multipart(new URI("/note/testMultipartFile"));
			MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "TiDB 适用场景.pdf",
					MediaType.APPLICATION_PDF_VALUE, new FileInputStream(testFile));
			mockMulHttpServletRequestBuilder.file(mockMultipartFile);

			System.out.println(mockMvc.perform(mockMulHttpServletRequestBuilder)
					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// GET & POST 都可以
	public void test1() {
		while (true) {
			Map<String, String> map = Maps.newHashMap();
			map.put("param_1", "test1");
			map.put("param_2", "test2");

			try {
				System.out.println(mockMvc
						.perform(MockMvcRequestBuilders.post("/note/test1").content(JSON.toJSONString(map))
								.param("param1", "param1"))
						.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print())
						.andReturn().getResponse().getContentAsString());

				System.out.println(mockMvc
						.perform(MockMvcRequestBuilders.get("/note/test1").contentType(MediaType.APPLICATION_JSON_UTF8)
								.param("param1", "param1").content(JSON.toJSONString(map)))
						.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print())
						.andReturn().getResponse().getContentAsString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// GET & POST 都可以
	public void test2() {
		try {
			System.out.println(mockMvc
					.perform(MockMvcRequestBuilders.post("/note/test2").contentType(MediaType.APPLICATION_JSON_UTF8)
							.content(JSON.toJSONString(Lists.newArrayList("1", "2", "3")))
							.param("list2", new String[] { "4", "5", "6" }))
					.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn()
					.getResponse().getContentAsString());
			System.out.println(mockMvc
					.perform(MockMvcRequestBuilders.get("/note/test2").contentType(MediaType.APPLICATION_JSON_UTF8)
							.content(JSON.toJSONString(Lists.newArrayList("1", "2", "3")))
							.param("list2", new String[] { "4", "5", "6" }))
					.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn()
					.getResponse().getContentAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// GET & POST 都可以
	public void test3() {
		try {
			System.out.println(mockMvc.perform(MockMvcRequestBuilders.get("/note/test3/" + 11))
					.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn()
					.getResponse().getContentAsString());
			System.out.println(mockMvc.perform(MockMvcRequestBuilders.post("/note/test3/" + 22))
					.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn()
					.getResponse().getContentAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
