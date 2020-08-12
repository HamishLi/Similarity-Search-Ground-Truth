package SpringBoot.dao;

import SpringBoot.entity.Feedback;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FeedbackMapperTest extends TestCase {
    @Autowired
    FeedbackMapper feedbackMapper;

    public void testDeleteByPrimaryKey() {
    }

    @Test
    @Rollback
    public void testInsert() {
        Feedback feedback = new Feedback();
        feedback.setRecordNo(1);
        feedback.setImageId(0);
        feedback.setResult("test");
        assertEquals(1, feedbackMapper.insert(feedback));
    }

    public void testInsertSelective() {
    }

    public void testSelectByPrimaryKey() {
    }

    public void testUpdateByPrimaryKeySelective() {
    }

    public void testUpdateByPrimaryKey() {
    }
}