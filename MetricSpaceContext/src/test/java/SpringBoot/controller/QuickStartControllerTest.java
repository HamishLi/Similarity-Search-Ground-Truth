package SpringBoot.controller;

import SpringBoot.dao.FeedbackMapper;
import SpringBoot.service.InsertFeedbackService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest
public class QuickStartControllerTest extends TestCase {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InsertFeedbackService insertFeedbackService;

    @MockBean
    private FeedbackMapper feedbackMapper;

    @Test
    public void testGetGroundTruthByImage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getImages/0")).andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testGetPoweredGroundTruthByImage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getPoweredImages/3.5/788")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGenerateRandomImagesInfo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/generateRandomImageIds")).andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testGenerateRandomFeedbackId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/generateRandomFeedbackId")).andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testPostFeedback() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/postFeedback").param("imageId", "0").param("result", "test"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}