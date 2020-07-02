package cn.lxt6.bgmanage_rule;

import static cn.lxt6.util.StringUtil.randomInt;
import static cn.lxt6.util.TimeUtil.nowLong;

/**
 * @author chenzy
 * @since 2020-07-02
 */
public class BGManageUtil {
    private BGManageUtil(){}

    //得到主键id
    public static Long getId() {
        return nowLong()*100000+randomInt(5);
    }

    public static void main(String[] args) {
        System.out.println(getId());
    }
}
