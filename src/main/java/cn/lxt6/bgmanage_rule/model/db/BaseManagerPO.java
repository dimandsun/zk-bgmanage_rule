package cn.lxt6.bgmanage_rule.model.db;

import cn.lxt6.bgmanage_rule.model.enums.ServiceTimeEnum;
import cn.lxt6.config.core.annotation.VarExplain;
import cn.lxt6.config.core.annotation.db.Table;
import cn.lxt6.config.enums.APPEnum;
import cn.lxt6.util.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cn.lxt6.util.StringUtil.str2JsonStr;

/**
 * @author chenzy
 * @since 2020-06-30
 * 运营基本管理表(operationmanager->base_manager)
 */
@Table("base_manager")
public class BaseManagerPO {
    //Long(时间戳+'6随机数')
    @VarExplain("主键")
    private Long id;
    //投资人ID 必填
    @VarExplain("投资人ID")
    @JsonProperty("investor_id")
    private Long investorId;

    //学校ID 必填
    @VarExplain("学校ID")
    @JsonProperty("school_id")
    private Long schoolId;

    //应用ID 必填
    @VarExplain("应用ID")
    @JsonProperty("type_id")
    private APPEnum appEnum;

    //常规信息 常用基本信息 必填
    @VarExplain("常用基本信息")
    @JsonProperty("routine_json")
    private String routineJson;

    //钱包充值支付选择 必填
    @VarExplain("钱包充值支付选择")
    @JsonProperty("wallet_rechange_json")
    private String walletRechangeJson;

    //钱包退款标志 必填 0为不开启，1为开启。true为1，false为0
    @VarExplain("钱包退款标志")
    @JsonProperty("wallet_refund_flag")
    private Boolean openWalletRefund;

    //钱包退款规则信息
    @VarExplain("钱包退款规则信息")
    @JsonProperty("wallet_refund_json")
    private String walletRefundJson;

    //根据type_id做补充信息功能
    @VarExplain("扩展功能信息")
    @JsonProperty("extend_json")
    private String extendJson;

    //押金退款标志 必填 0为不开启，1为开启。true为1，false为0
    @VarExplain("押金退款标志")
    @JsonProperty("deposit_refund_flag")
    private Boolean openDepositRefund;

    //押金退款规则信息
    @VarExplain("押金退款规则信息")
    @JsonProperty("deposit_refund_json")
    private String depositRefundJson;


    //运营时间标志 必填 默认是0, 0 为全天开放,1 为自定义时间
    @VarExplain("运营时间标志")
    @JsonProperty("service_time_flag")
    private ServiceTimeEnum serviceTimeFlag;

    //运营时间规则
    @VarExplain("运营时间规则")
    @JsonProperty("service_time_json")
    private String serviceTimeJson;

    @VarExplain("唯一约束")
    //唯一约束 必填 用school_id+'_'+type_id保证系统唯一性
    @JsonProperty("unique_system")
    private String uniqueSystem;

    //备用字段 json
    private String j1;
    //备用字段 json
    private String j2;
    //备用字段 json
    private String j3;

    @Override
    public String toString() {
        return JsonUtil.model2Str(this);
    }
    public void setUniqueSystem() {
        if (uniqueSystem==null){
            this.uniqueSystem = schoolId + "_" + appEnum.getValue();
        }
    }
    public String getExtendJson() {
        return extendJson;
    }

    public void setExtendJson(String extendJson) {
        this.extendJson = str2JsonStr(extendJson);
    }

    public Boolean getOpenDepositRefund() {
        return openDepositRefund;
    }

    public void setOpenDepositRefund(Boolean openDepositRefund) {
        this.openDepositRefund = openDepositRefund;
    }

    public String getDepositRefundJson() {
        return depositRefundJson;
    }

    public void setDepositRefundJson(String depositRefundJson) {
        this.depositRefundJson = str2JsonStr(depositRefundJson);
    }

    public String getUniqueSystem() {
        return uniqueSystem;
    }

    public void setUniqueSystem(String uniqueSystem) {
        this.uniqueSystem = uniqueSystem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvestorId() {
        return investorId;
    }

    public void setInvestorId(Long investorId) {
        this.investorId = investorId;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public APPEnum getAppEnum() {
        return appEnum;
    }

    public void setAppEnum(APPEnum appEnum) {
        this.appEnum = appEnum;
    }

    public String getRoutineJson() {
        return routineJson;
    }

    public void setRoutineJson(String routineJson) {
        this.routineJson = str2JsonStr(routineJson);
    }

    public String getWalletRechangeJson() {
        return walletRechangeJson;
    }

    public void setWalletRechangeJson(String walletRechangeJson) {
        this.walletRechangeJson = str2JsonStr(walletRechangeJson);
    }

    public Boolean getOpenWalletRefund() {
        return openWalletRefund;
    }

    public void setOpenWalletRefund(Boolean openWalletRefund) {
        this.openWalletRefund = openWalletRefund;
    }

    public String getWalletRefundJson() {
        return walletRefundJson;
    }

    public void setWalletRefundJson(String walletRefundJson) {
        this.walletRefundJson = str2JsonStr(walletRefundJson);
    }

    public ServiceTimeEnum getServiceTimeFlag() {
        return serviceTimeFlag;
    }

    public void setServiceTimeFlag(ServiceTimeEnum serviceTimeFlag) {
        this.serviceTimeFlag = serviceTimeFlag;
    }

    public String getServiceTimeJson() {
        return serviceTimeJson;
    }

    public void setServiceTimeJson(String serviceTimeJson) {
        this.serviceTimeJson = str2JsonStr(serviceTimeJson);
    }

    public String getJ1() {
        return j1;
    }

    public void setJ1(String j1) {
        this.j1 = j1;
    }

    public String getJ2() {
        return j2;
    }

    public void setJ2(String j2) {
        this.j2 = j2;
    }

    public String getJ3() {
        return j3;
    }

    public void setJ3(String j3) {
        this.j3 = j3;
    }
}
