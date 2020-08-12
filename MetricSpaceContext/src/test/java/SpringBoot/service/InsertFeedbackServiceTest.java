package SpringBoot.service;

import SpringBoot.dao.FeedbackMapper;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class InsertFeedbackServiceTest extends TestCase {
    @MockBean
    private FeedbackMapper feedbackMapper;

    @Autowired
    private InsertFeedbackService insertFeedbackService;

    @Test
    @Rollback
    public void testInsertFeedback() {
        assertTrue(insertFeedbackService.insertFeedback(0, "test"));
        assertFalse(insertFeedbackService.insertFeedback(0,"notChosen"));
    }
}