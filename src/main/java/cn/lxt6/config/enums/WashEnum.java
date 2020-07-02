package cn.lxt6.config.enums;


import cn.lxt6.util.StringUtil;

/**
 * 洗衣机模式:1脱水 2快速洗 3标准洗 4大件洗
 * 干衣机模式:1晾干 2快干 3标准干 4特干
 *
 * @author chenzy
 * @date 2019.12.23
 */
public enum WashEnum implements IEnum<Integer> {
    Test(0, "工厂模式(洗衣测试)", ""), Slowly(1, "衣服脱水", "晾干"), Fast(2, "快速洗衣", "快干"), Standard(3, "标准洗衣", "标准干"), Super(4, "大件洗衣", "特干")
    ,BarrelCleaning(5,"桶自洁",""),EnergyHalf(6,"能效半截",""),EnergyFull(7,"能效全截",""),WashingUncovering(8,"洗衣机解镜","")
    ,ComputerInspection(9,"电脑版自检",""),Other(4095,"其他","");

    private Integer id;
    private String msg;
    private String msg2;

    WashEnum(Integer id, String msg, String msg2) {
        this.id = id;
        this.msg = msg;
        this.msg2 = msg2;
    }
    public static WashEnum getEnum(String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }
        try {
            Integer v = Integer.valueOf(value);
            for (WashEnum washEnum : WashEnum.values()) {
                if (Integer.valueOf(washEnum.getValue()).equals(v)) {
                    return washEnum;
                }
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }


    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public Integer getValue() {
        return this.id;
    }
}

