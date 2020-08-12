package SpringBoot.service;

import SpringBoot.dao.FeedbackMapper;
import SpringBoot.entity.Feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class InsertFeedbackService {
    @Autowired
    FeedbackMapper feedbackMapper;

    public boolean insertFeedback(Integer imageId, String result) {
        Feedback record = new Feedback();
        record.setImageId(imageId);
        record.setResult(result);
        if (!result.equals("notChosen")) {
            feedbackMapper.insert(record);
            return true;
        }
        return false;
    }
}
