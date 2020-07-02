package cn.lxt6.bgmanage_rule.model.cash;

import cn.lxt6.bgmanage_rule.model.db.BaseManagerPO;
import cn.lxt6.util.json.JsonUtil;

/**
 * @author chenzy
 * @since 2020-06-30
 */
public class BaseManagerDTOFactory {
    private BaseManagerPO bean;
    private Long schoolId;
    private Integer appEnumValue;
    public static BaseManagerDTOFactory getFactory(BaseManagerPO baseManagerPO){
        BaseManagerDTOFactory factory = new BaseManagerDTOFactory();
        factory.bean=baseManagerPO;
        factory.schoolId=baseManagerPO.getSchoolId();
        factory.appEnumValue=baseManagerPO.getAppEnum().getValue();
        return factory;
    }

    /**
     * 基础信息
     * KEY:operation:学校ID:应用id:base_manager:base;
     * 例: operation:1:1:base_manager:base;
     * @return
     */
    public String getBaseJson() {
       return JsonUtil.model2Model(bean,BaseManagerBaseDTO.class).get().toString();
    }
    public String getBaseKey() {
        return "operation:"+schoolId+":"+appEnumValue+":base_manager:base";
    }

    /**
     * 常规信息
     * KEY:operation:学校ID:应用id:base_manager:routine_json;
     * 例: operation:1:1:base_manager:routine_json;
     * @return
     */
    public String getRoutineJson() {
        return bean.getRoutineJson().toString();
    }

    public String getRoutineKey() {
        return "operation:"+schoolId+":"+appEnumValue+":base_manager:routine_json";
    }
    /**
     * 钱包充值支付选择
     * KEY:operation:学校ID:应用id:base_manager:wallet_rechange_json;
     * 例: operation:1:1:base_manager:wallet_rechange_json;
     * @return
     */
    public String getWalletRechangeJson() {
        return bean.getWalletRechangeJson();
    }

    public String getWalletRechangeKey() {
        return "operation:"+schoolId+":"+appEnumValue+":base_manager:wallet_rechange_json";
    }
    /**
     * 钱包退款选择
     * KEY:operation:学校ID:应用id:base_manager:wallet_refund_json;
     * 例: operation:1:1:base_manager:wallet_refund_json;
     * @return
     */
    public String getWalletRefundJson() {
        return bean.getWalletRefundJson();
    }

    public String getWalletRefundKey() {
        return "operation:"+schoolId+":"+appEnumValue+":base_manager:wallet_refund_json";
    }
    /**
     * 押金退款选择deposit_refund_json信息部分
     * KEY:operation:学校ID:应用id:base_manager:deposit_refund_json;
     * 例: operation:1:1:base_manager:deposit_refund_json;
     * @return
     */
    public String getDepositRefundJson() {
        return bean.getDepositRefundJson();
    }

    public String getDepositRefundKey() {
        return "operation:"+schoolId+":"+appEnumValue+":base_manager:deposit_refund_json";
    }
    /**
     * 扩展功能extend_json信息部分
     * KEY:operation:学校ID:应用id:base_manager:extend_json;
     * 例: operation:1:1:base_manager:extend_json;
     * @return
     */
    public String getExtendJson() {
        return bean.getExtendJson();
    }

    public String getExtendKey() {
        return "operation:"+schoolId+":"+appEnumValue+":base_manager:extend_json";
    }
    /**
     * 运行时间service_time_json信息部分
     * KEY:operation:学校ID:应用id:base_manager:service_time_json;
     * 例: operation:1:1:base_manager:service_time_json;
     * @return
     */
    public String getServiceTimeJson() {
        return bean.getServiceTimeJson();
    }

    public String getServiceTimeKey() {
        return "operation:"+schoolId+":"+appEnumValue+":base_manager:service_time_json";
    }
}
