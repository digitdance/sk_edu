
package org.springcrazy.modules.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.edu.entity.CourseExam;
import org.springcrazy.modules.edu.entity.ScoreRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 章节资料信息 Mapper 接口
 *
 * @author TongZhou
 * @since 2020-10-14
 */
public interface ScoreRecordMapper extends BaseMapper<ScoreRecord> {


    List<Integer> selectDistinctUsers();

    BigDecimal selectUserTypeScore(@Param("scoreRecord") ScoreRecord scoreRecord);

    List<ScoreRecord> selectUserScores(@Param("scoreRecord") ScoreRecord scoreRecord);

    List<ScoreRecord> selectUserSumScores(@Param("scoreRecord") ScoreRecord scoreRecord);

    List<ScoreRecord> selectStudentsScoreInClazz(Map<String,Object> scoreRecord);


}
