package cn.lxt6.bgmanage_rule.model.db;

import cn.lxt6.bgmanage_rule.model.enums.OperationTypeEnum;
import cn.lxt6.config.core.annotation.db.Table;
import cn.lxt6.config.enums.APPEnum;
import cn.lxt6.util.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author chenzy
 * @since 2020-06-30
 * 运营基本管理操作记录表(operationmanager->base_manager_log)
 */
@Table("base_manager_log")
public class BaseManagerLogPO {
    private Long id;
    //投资人ID 必填
    @JsonProperty("investor_id")
    private Long investorId;
    //学校ID 必填
    @JsonProperty("school_id")
    private Long schoolId;
    //应用ID 必填
    @JsonProperty("type_id")
    private APPEnum appEnum;

    //必填 1 创建信息，2为修改，3为删除s
    private OperationTypeEnum operationType;
    //必填 base_manager表主健
    @JsonProperty("bm_id")
    private Long baseManagerId;
    //必填 操作者ID
    @JsonProperty("handler_id")
    private Long handlerId;
    //必填 操作者姓名
    @JsonProperty("handler_name")
    private String handlerName;
    //必填 操作时间 时间格式 yyyy-MM-dd HH:mm:ss
    @JsonProperty("handler_time")
    private String handlerTime;

    /*原内容 operation_type=1或3时可不填充，
            operation_type=2时，需要把需要修改原信息填写可以用整个JSON部分
    */
    @JsonProperty("content")
    private String content;

    /*必填 变更容 operation_type=1新建用base_manager所有内容填写
            operation_type=2需要比对数据库对像将变更内容的部分填充
            operation_type=3将base_manager表中所有内容的部分填充
    */
    @JsonProperty("next_content")
    private String nextContent;

    @Override
    public String toString() {
        return JsonUtil.model2Str(this);
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


    public OperationTypeEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationTypeEnum operationType) {
        this.operationType = operationType;
    }

    public Long getBaseManagerId() {
        return baseManagerId;
    }

    public void setBaseManagerId(Long baseManagerId) {
        this.baseManagerId = baseManagerId;
    }

    public Long getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(Long handlerId) {
        this.handlerId = handlerId;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getHandlerTime() {
        return handlerTime;
    }

    public void setHandlerTime(String handlerTime) {
        this.handlerTime = handlerTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNextContent() {
        return nextContent;
    }

    public void setNextContent(String nextContent) {
        this.nextContent = nextContent;
    }
}
