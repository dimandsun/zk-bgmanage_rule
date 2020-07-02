package cn.lxt6.bgmanage_rule.service.impl;

import cn.lxt6.bgmanage_rule.BGManageUtil;
import cn.lxt6.bgmanage_rule.dao.IBGManageDao;
import cn.lxt6.bgmanage_rule.model.db.BaseManagerLogPO;
import cn.lxt6.bgmanage_rule.model.db.BaseManagerPO;
import cn.lxt6.bgmanage_rule.model.enums.OperationTypeEnum;
import cn.lxt6.bgmanage_rule.service.IBaseManagerLogService;
import cn.lxt6.config.core.annotation.Auto;
import cn.lxt6.config.core.annotation.bean.Service;
import cn.lxt6.config.db.DataSourceHolder;
import cn.lxt6.config.enums.APPEnum;
import cn.lxt6.config.util.TableUtil;
import cn.lxt6.model.ResultVO;
import cn.lxt6.model.util.StringMap;
import cn.lxt6.util.TimeUtil;
import cn.lxt6.util.json.JsonUtil;
import org.slf4j.Logger;

import java.util.Map;

import static cn.lxt6.config.enums.ResCodeEnum.DBError;

/**
 * @author chenzy
 * @since 2020-06-30
 */
@Service
public class BaseManagerLogServiceImpl implements IBaseManagerLogService {
    @Auto private Logger dbInfoLog;
    @Auto private IBGManageDao bgManageDao;


    @Override
    public ResultVO<Long> insert(BaseManagerPO baseManager, Long handlerId, String handlerName) {
        return this.insert(baseManager,null,baseManager,handlerId,handlerName);
    }

    @Override
    public ResultVO<Long> insert(BaseManagerPO baseManager, BaseManagerPO content, BaseManagerPO nextContent, Long handlerId, String handlerName) {
        Long schoolId=baseManager.getSchoolId();
        APPEnum appEnum = baseManager.getAppEnum();
        //封装对象
        BaseManagerLogPO bean = new BaseManagerLogPO();
        bean.setAppEnum(baseManager.getAppEnum());
        bean.setBaseManagerId(baseManager.getId());
        bean.setHandlerId(handlerId);
        bean.setHandlerName(handlerName);
        bean.setHandlerTime(TimeUtil.nowStr());
        bean.setInvestorId(baseManager.getInvestorId());
        bean.setOperationType(OperationTypeEnum.Insert);
        bean.setSchoolId(schoolId);
        if (bean.getId()==null){
            bean.setId(BGManageUtil.getId());
        }
        if (content!=null){
            bean.setContent(content.toString());
        }
        if (nextContent!=null){
            bean.setNextContent(nextContent.toString());
        }
        String tableName = TableUtil.getTableName(bean.getClass());//表名
        Map<String, Object> par = JsonUtil.model2Model(bean, Map.class).get();
        //新增
        DataSourceHolder.getInstance().setDefault();
        try {
            Integer status= bgManageDao.insert(par, tableName);
            return new ResultVO(status);
        } catch (Exception e) {
            try {
                Integer status = createTable(schoolId, appEnum);
                if (Integer.valueOf(1).equals(status)) {
                    status= bgManageDao.insert(par, tableName);
                    return new ResultVO(status);
                }
            }catch (Exception e1){

            }
            dbInfoLog.info("数据表新增异常:{},表：{},业务信息：{}", e.getMessage(), tableName, bean.toString());
            return new ResultVO(DBError, "数据表新增异常:表" + tableName);

        }
    }

    private Integer createTable(Long schoolId, APPEnum appEnum) {
        DataSourceHolder.getInstance().setDefault();
        String procedureName = " LXT_base_manager_log_service";
        StringMap map = new StringMap(2, "schoolId", schoolId).add("appEnum", appEnum.getValue());
        Integer status = bgManageDao.createTable(procedureName, map);
        return status;
    }
}
