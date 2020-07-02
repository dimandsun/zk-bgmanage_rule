package cn.lxt6.bgmanage_rule.service.impl;

import cn.lxt6.bgmanage_rule.BGManageUtil;
import cn.lxt6.bgmanage_rule.dao.IBGManageDao;
import cn.lxt6.bgmanage_rule.dao.IBaseManagerDao;
import cn.lxt6.bgmanage_rule.model.cash.BaseManagerDTOFactory;
import cn.lxt6.bgmanage_rule.model.db.BaseManagerPO;
import cn.lxt6.bgmanage_rule.service.IBaseManagerLogService;
import cn.lxt6.bgmanage_rule.service.IBaseManagerService;
import cn.lxt6.config.core.annotation.Auto;
import cn.lxt6.config.core.annotation.bean.Service;
import cn.lxt6.config.db.DataSourceHolder;
import cn.lxt6.config.enums.APPEnum;
import cn.lxt6.config.enums.ResCodeEnum;
import cn.lxt6.config.util.TableUtil;
import cn.lxt6.model.ResultVO;
import cn.lxt6.model.util.OutPar;
import cn.lxt6.model.util.StringMap;
import cn.lxt6.service.IDefaultRedisService;
import cn.lxt6.util.DataUtil;
import cn.lxt6.util.StringUtil;
import cn.lxt6.util.json.JsonUtil;
import org.slf4j.Logger;

import java.util.Map;

import static cn.lxt6.config.enums.ResCodeEnum.DBError;
import static cn.lxt6.config.enums.ResCodeEnum.DBExce;

/**
 * @author chenzy
 * @since 2020-06-30
 */
@Service
public class BaseManagerServiceImpl implements IBaseManagerService {
    @Auto
    private IBaseManagerLogService baseManagerLogService;
    @Auto
    private IBGManageDao bgManageDao;
    @Auto
    private IBaseManagerDao baseManagerDao;


    @Auto
    private Logger dbInfoLog;

    @Auto
    private IDefaultRedisService defaultRedisService;

    private ResultVO insertArgVerify(BaseManagerPO baseManager, Long handlerId, String handlerName) {
        String msg = "";
        Long id = baseManager.getId();
        if (StringUtil.isNotBlank(id)) {
            msg += "主键不能有值";
        }
        if (!"".equals(msg)) {
            return new ResultVO(ResCodeEnum.ArgNoCorrect, "参数异常：" + msg);
        }
        return null;
    }


    @Override
    public ResultVO insert(BaseManagerPO baseManager, Long handlerId, String handlerName) {

        //参数校验
        ResultVO verifyResult = insertArgVerify(baseManager, handlerId, handlerName);
        if (verifyResult != null) {
            return verifyResult;
        }
        //新增
        ResultVO insertResult = this.insert(baseManager);
        if (insertResult.isNotNormal()) {
            return insertResult;
        }
        //记录缓存
        ResultVO cashResult = setRedis(baseManager);
        if (cashResult.isNotNormal()) {
            return cashResult;
        }
        //新增日志
        ResultVO<Long> logResult = baseManagerLogService.insert(baseManager, handlerId, handlerName);
        if (logResult.isNotNormal()) {
            return logResult;
        }
        return new ResultVO(new StringMap<>(1, "id", baseManager.getId()));
    }

    @Override
    public ResultVO update(BaseManagerPO afterAll, Long handlerId, String handlerName) {
        Long id = afterAll.getId();

        //修改前判断是否存在唯一键重复
        afterAll.setUniqueSystem();
        ResultVO verifyResult2 = verifyUniqueUpdate(id, afterAll.getUniqueSystem());
        if (verifyResult2.isNotNormal()) {
            return verifyResult2;
        }
        System.out.println(afterAll);
        //与数据库中的数据一个字段一个字段比较，得到有更改字段的前后值
        OutPar<BaseManagerPO> afterChangeValue = new OutPar<>(new BaseManagerPO());
        ResultVO<BaseManagerPO> beforChangeValue = getChangeValue(afterAll, afterChangeValue);
        if (beforChangeValue.isNotNormal()) {
            return beforChangeValue;
        }
        //修改
        ResultVO updateResult = this.update(afterAll);
        if (updateResult.isNotNormal()) {
            return updateResult;
        }
        //记录日志
        ResultVO<Long> logResult = baseManagerLogService.insert(afterAll, beforChangeValue.getData(), afterChangeValue.get(), handlerId, handlerName);
        if (logResult.isNotNormal()) {
            return logResult;
        }
        //更新缓存
        ResultVO cashResult = setRedis(afterAll);
        if (cashResult.isNotNormal()) {
            return cashResult;
        }
        return new ResultVO();
    }

    @Override
    public ResultVO<BaseManagerPO> selectById(Long id) {
        DataSourceHolder.getInstance().setDefault();
        Map oldBean = baseManagerDao.selectById(id);
        if (oldBean == null) {
            return new ResultVO<>(ResCodeEnum.DBExce, "在数据库中没有查到指定记录,请重新刷新页面！");
        }
        return new ResultVO(JsonUtil.map2Model(oldBean,BaseManagerPO.class).get());
    }

    @Override
    public ResultVO delete(Long id, Long handlerId, String handlerName) {
        ResultVO<BaseManagerPO> beanResult = selectById(id);
        if (beanResult.isNotNormal()) {
            return beanResult;
        }
        BaseManagerPO bean = beanResult.getData();
        //删除
        delete(id);
        //新增日志
        ResultVO<Long> logResult = baseManagerLogService.insert(bean, handlerId, handlerName);
        if (logResult.isNotNormal()) {
            return logResult;
        }
        //删除缓存
        ResultVO cashResult = deleteRedis(bean);
        if (cashResult.isNotNormal()) {
            return cashResult;
        }
        return new ResultVO();
    }

    /**
     * 一个字段一个字段比较，得到字段改变前后的值
     * 返回的是字段改变前的值
     *
     * @param afterAll
     * @param afterChangeValue 字段改变后的值
     * @return
     */
    private ResultVO<BaseManagerPO> getChangeValue(BaseManagerPO afterAll, OutPar<BaseManagerPO> afterChangeValue) {
        ResultVO<BaseManagerPO> beforeResult = selectById(afterAll.getId());
        if (beforeResult.isNotNormal()) {
            return beforeResult;
        }
        ResultVO<BaseManagerPO> beforChangeValue = DataUtil.getChangeValue(beforeResult.getData(), afterAll, afterChangeValue);
        if (beforChangeValue.getData() == null) {
            beforChangeValue.setCode(ResCodeEnum.BusInfo);
            beforChangeValue.setMessage("没有修改任何值！");
        }
        return beforChangeValue;
    }

    private ResultVO setRedis(BaseManagerPO bean) {
        BaseManagerDTOFactory factory = BaseManagerDTOFactory.getFactory(bean);
        defaultRedisService.set(factory.getBaseKey(), factory.getBaseJson());
        defaultRedisService.set(factory.getRoutineKey(), factory.getRoutineJson());
        defaultRedisService.set(factory.getDepositRefundKey(), factory.getDepositRefundJson());
        defaultRedisService.set(factory.getServiceTimeKey(), factory.getServiceTimeJson());
        defaultRedisService.set(factory.getWalletRechangeKey(), factory.getWalletRechangeJson());
        defaultRedisService.set(factory.getWalletRefundKey(), factory.getWalletRefundJson());
        defaultRedisService.set(factory.getExtendKey(), factory.getExtendJson());
        return new ResultVO();
    }

    private ResultVO deleteRedis(BaseManagerPO bean) {
        BaseManagerDTOFactory factory = BaseManagerDTOFactory.getFactory(bean);
        defaultRedisService.del(factory.getBaseKey());
        defaultRedisService.del(factory.getRoutineKey());
        defaultRedisService.del(factory.getDepositRefundKey());
        defaultRedisService.del(factory.getServiceTimeKey());
        defaultRedisService.del(factory.getWalletRechangeKey());
        defaultRedisService.del(factory.getWalletRefundKey());
        defaultRedisService.del(factory.getExtendKey());
        return new ResultVO();
    }

    /**
     * 修改前判断是否存在唯一键重复
     *
     * @param id
     * @return
     */
    private ResultVO verifyUniqueUpdate(Long id, String uniqueSystem) {
        DataSourceHolder.getInstance().setDefault();
        Integer status = baseManagerDao.verifyUniqueUpdate(id, uniqueSystem);
        if (Integer.valueOf(1).equals(status)) {
            return new ResultVO(ResCodeEnum.ArgNoCorrect, "对应学校的对应应用已存在投资人，请您检查输入是否有误！");
        }
        return new ResultVO();
    }

    /**
     * 新增前判断是否存在唯一键重复
     *
     * @return
     */
    private ResultVO verifyUniqueInsert(String uniqueSystem) {
        DataSourceHolder.getInstance().setDefault();
        Integer status = baseManagerDao.verifyUniqueInsert(uniqueSystem);
        if (Integer.valueOf(1).equals(status)) {
            return new ResultVO(ResCodeEnum.ArgNoCorrect, "对应学校的对应应用已存在投资人，请您检查输入是否有误！");
        }
        return new ResultVO();
    }


    public ResultVO delete(Long id) {
        //表名
        String tableName = TableUtil.getTableName(BaseManagerPO.class);
        DataSourceHolder.getInstance().setDefault();
        Integer status = bgManageDao.delete(new StringMap<>(1, "id", id), tableName);
        if (!Integer.valueOf(1).equals(status)) {
            return new ResultVO(DBExce, "删除异常：数据库删除失败！");
        }
        return new ResultVO();
    }

    private ResultVO update(BaseManagerPO bean) {
        //表名
        String tableName = TableUtil.getTableName(bean.getClass());
        Map<String, Object> par = JsonUtil.model2Model(bean, Map.class).get();
        par.remove("id");
        DataSourceHolder.getInstance().setDefault();
        Integer status = bgManageDao.update(par, new StringMap<>(1, "id", bean.getId()), tableName);
        if (!Integer.valueOf(1).equals(status)) {
            return new ResultVO(DBExce, "修改异常：数据库修改失败！");
        }
        return new ResultVO();
    }

    private ResultVO insert(BaseManagerPO bean) {
        Long schoolId = bean.getSchoolId();
        APPEnum appEnum = bean.getAppEnum();

        if (bean.getId()==null){
            bean.setId(BGManageUtil.getId());
        }
        bean.setUniqueSystem();
        ResultVO verifyResult = verifyUniqueInsert(bean.getUniqueSystem());
        if (verifyResult.isNotNormal()) {
            return verifyResult;
        }
        //表名
        String tableName = TableUtil.getTableName(bean.getClass());

        Map<String, Object> par = JsonUtil.model2Model(bean, Map.class).get();
        DataSourceHolder.getInstance().setDefault();
        try {
            Integer status = bgManageDao.insert(par,tableName);
            return new ResultVO(status);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (StringUtil.isBlank(msg)){
                msg=e.getCause().getMessage();
            }
            if (msg.contains("错误: 重复键违反唯一约束")) {
                dbInfoLog.info("数据异常:重复键违反唯一约束{},唯一约束：{}", e.getMessage(), bean.getUniqueSystem());
                return new ResultVO(ResCodeEnum.DuplicateInsert, 0, msg);
            }
            try {
                Integer status = createTable(schoolId, appEnum);
                if (Integer.valueOf(1).equals(status)) {
                    status = bgManageDao.insert(par, tableName);
                    return new ResultVO(status);
                }
            }catch (Exception e1){

            }
            dbInfoLog.info("数据表新增异常:{},表：{},业务信息：{}", msg, tableName, bean.toString());
            return new ResultVO(DBError, "数据表新增异常:表" + tableName);
        }
    }

    private Integer createTable(Long schoolId, APPEnum appEnum) {
        DataSourceHolder.getInstance().setDefault();
        String procedureName = " LXT_base_manager_service";
        StringMap map = new StringMap(2, "schoolId", schoolId).add("appEnum", appEnum.getValue());
        Integer status = bgManageDao.createTable(procedureName, map);
        return status;
    }
}
