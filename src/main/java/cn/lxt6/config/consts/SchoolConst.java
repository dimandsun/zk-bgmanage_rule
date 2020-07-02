package cn.lxt6.config.consts;

import java.util.Arrays;
import java.util.List;

/**
 * @author chenzy
 * @since 2020-04-08
 */
public interface SchoolConst {
    List<Integer> nocardSchoolList = Arrays.asList(10038, 10039, 10064, 10073, 10045);
    //使用免密支付的学校
    List<Integer> alipaySchoolList = Arrays.asList(10038); //[10038,10001]
    //自动生成子表的学校
    List<Integer> aotuGenerateTableSchoolList = Arrays.asList(1, 10039, 10038, 10045);
    // 20002 华南农业大学|20003 四川信息职业技术学院|20005 河北政法职业学院 都是没有存储学生信息的
    List<Integer> noStudentIdSchoolList = Arrays.asList(20002,20003,20005);
}
