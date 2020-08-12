package SpringBoot.dao;

import SpringBoot.entity.Feedback;

public interface FeedbackMapper {
    int deleteByPrimaryKey(Integer recordNo);

    int insert(Feedback record);

    int insertSelective(Feedback record);

    Feedback selectByPrimaryKey(Integer recordNo);

    int updateByPrimaryKeySelective(Feedback record);

    int updateByPrimaryKey(Feedback record);
}