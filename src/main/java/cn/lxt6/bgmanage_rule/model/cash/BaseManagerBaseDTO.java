package cn.lxt6.bgmanage_rule.model.cash;

import cn.lxt6.bgmanage_rule.model.enums.ServiceTimeEnum;
import cn.lxt6.util.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author chenzy
 * @since 2020-06-30
 * 缓存KEY:——》operation:学校ID:应用id:base_manager:base;
 *     value:——》{'id':0L,'wallet_refund_flag':0,'perservice_time_flag':0}
 */
public class BaseManagerBaseDTO {
    private Long id;

    //钱包退款标志 0为不开启，1为开启
    @JsonProperty("wallet_refund_flag")
    private Boolean openWalletRefund;

    //押金退款标志 0为不开启，1为开启
    @JsonProperty("deposit_refund_flag")
    private Boolean openDepositRefund;


    //运营时间标志0 为全天开放,1 为自定义时间
    @JsonProperty("service_time_flag")
    private ServiceTimeEnum serviceTimeFlag;

    public String toString(){
        return JsonUtil.model2Str(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getOpenWalletRefund() {
        return openWalletRefund;
    }

    public void setOpenWalletRefund(Boolean openWalletRefund) {
        this.openWalletRefund = openWalletRefund;
    }

    public Boolean getOpenDepositRefund() {
        return openDepositRefund;
    }

    public void setOpenDepositRefund(Boolean openDepositRefund) {
        this.openDepositRefund = openDepositRefund;
    }

    public ServiceTimeEnum getServiceTimeFlag() {
        return serviceTimeFlag;
    }

    public void setServiceTimeFlag(ServiceTimeEnum serviceTimeFlag) {
        this.serviceTimeFlag = serviceTimeFlag;
    }
}
