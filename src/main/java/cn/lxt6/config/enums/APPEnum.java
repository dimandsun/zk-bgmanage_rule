package cn.lxt6.config.enums;



/**
 * typeId枚举
 * 应用功能类型
 *
 * @author chenzy
 * @date 2019.12.23
 */
public enum APPEnum implements IEnum<Integer> {
    Bath(1, "洗澡")
    , Shopping(2, "购物")
    , Wash(3, "洗衣")
    , Other(4, "其它")
    , Dining(5, "用餐")
    , Drink(6, "饮水")
    , Charge(7, "充电")
    , Hairdryer(8, "吹风")
    , SellCard(9, "售卡")
    , GateBan(10, "门禁")
    , BathHouse(11, "公共澡堂")
    , DirectDrink(12, "直饮水")
    , BindCard(13, "绑卡")
    , CardCharge(14, "卡片充值")
    , MultiAPP(15, "多应用")
    , Drying(16, "干衣")
    , CodeShower(17, "扫码淋浴")
    , RWaterMeter(18, "远传水表")
    , AirConditionerControl(19,"空调控制")
    ,CashMachine(20,"领款机")
    ,;
    private Integer id;
    private String msg;


    APPEnum(Integer id, String msg) {
        this.id = id;
        this.msg = msg;
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
