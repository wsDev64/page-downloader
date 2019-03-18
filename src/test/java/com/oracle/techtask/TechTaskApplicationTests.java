package com.oracle.techtask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.techtask.dto.api.ResourceInfo;
import com.oracle.techtask.dto.api.TotalResourceInfo;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TechTaskApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void contextLoads() {
	}

	@Test
	public void testFileSizeWODonloadingBig() throws Exception {
		testFileSize("/file/size?url=http://mirror.waia.asn.au/ubuntu-releases/18.10/ubuntu-18.10-desktop-amd64.iso", 1999503360);
	}

	@Test
	public void testFileSizeWODonloadingNotFound() throws Exception {
		testFileSize("/file/size?url=http://mirror.waia.asn.au/ubuntu-releases/18.10/ubuntu-18.10-desktop-amd64.iso", 1999503360);
	}

	@Test
	public void testFileSizeNotFound() throws Exception {
		this.mockMvc.perform(get("/file/size?url=https://www.google.com"))
				.andDo(print())
				.andExpect(status().isNotFound());

	}

	@Test
	public void testFileBadUrl() throws Exception {
		this.mockMvc.perform(get("/file/size?url=badUrl"))
				.andDo(print())
				.andExpect(status().isBadRequest());

	}




	@Test
	public void testPageSize() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(get("/page/size?url=https://www.google.com"))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("resourcesInfo.length()").value(3))
				.andReturn();

		TotalResourceInfo totalResourceInfo = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TotalResourceInfo.class);

		assertEquals(totalResourceInfo.getTotalBytes(), (Long) totalResourceInfo.getResourcesInfo()
				.stream()
				.mapToLong(ResourceInfo::getSize)
				.sum());
	}

	private void testFileSize(String s, int i) throws Exception {
		this.mockMvc.perform(get(s))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("size")
						.value(Matchers.equalTo(i)));
	}


}
