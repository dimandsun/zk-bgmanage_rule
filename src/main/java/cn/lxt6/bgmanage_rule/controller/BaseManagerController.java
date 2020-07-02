package cn.lxt6.bgmanage_rule.controller;

import cn.lxt6.bgmanage_rule.model.db.BaseManagerPO;
import cn.lxt6.bgmanage_rule.service.IBaseManagerService;
import cn.lxt6.config.core.annotation.Auto;
import cn.lxt6.config.core.annotation.Par;
import cn.lxt6.config.core.annotation.bean.Controller;
import cn.lxt6.config.core.annotation.mapping.*;
import cn.lxt6.config.enums.ResCodeEnum;
import cn.lxt6.model.ResultVO;
import cn.lxt6.util.DataUtil;
import cn.lxt6.util.StringUtil;

/**
 * @author chenzy
 * @since 2020-06-30
 */
@Controller
@Mapping("baseManager")
public class BaseManagerController {

    @Auto
    private IBaseManagerService baseManagerService;


    private ResultVO argVerify(BaseManagerPO baseManager,String[] fieldNames,Long handlerId, String handlerName) {
        String msg = "";
        if (StringUtil.isBlank(handlerId)) {
            msg += "操作者id不能为空,";
        }
        if (StringUtil.isBlank(handlerName)) {
            msg += "操作者姓名不能为空,";
        }
        if (!"".equals(msg)) {
            return new ResultVO(ResCodeEnum.ArgNoCorrect, "参数异常：" + msg);
        }
        return DataUtil.validateField(baseManager, fieldNames);
    }
    @PostMapping
    public ResultVO insert(BaseManagerPO baseManager, @Par("handler_id") Long handlerId,@Par("handler_name") String handlerName){
        //业务数据简单校验
        String[] fieldNames = {"investorId", "schoolId", "appEnum", "routineJson", "walletRechangeJson"
                , "openWalletRefund", "serviceTimeFlag","openDepositRefund"};
        ResultVO resultVO=argVerify(baseManager,fieldNames,handlerId,handlerName);
        if (resultVO.isNotNormal()) {
            return resultVO;
        }
        return baseManagerService.insert(baseManager,handlerId,handlerName);
    }

    @PutMapping
    public ResultVO update(BaseManagerPO baseManager, @Par("handler_id") Long handlerId,@Par("handler_name") String handlerName){
        //业务数据简单校验
        String[] fieldNames = {"id","investorId", "schoolId", "appEnum", "routineJson", "walletRechangeJson"
                , "openWalletRefund", "serviceTimeFlag","openDepositRefund"};
        ResultVO resultVO=argVerify(baseManager,fieldNames,handlerId,handlerName);
        if (resultVO.isNotNormal()) {
            return resultVO;
        }
        return baseManagerService.update(baseManager,handlerId,handlerName);
    }

    @DeleteMapping
    public ResultVO delete(@Par("id") Long id, @Par("handler_id") Long handlerId,@Par("handler_name") String handlerName){
        //业务数据简单校验
        if (StringUtil.isBlankOr(id,handlerId,handlerName)) {
            return new ResultVO(ResCodeEnum.ArgEmpty,"参数：id/handler_id/handler_name/不能为空");
        }
        return baseManagerService.delete(id,handlerId,handlerName);
    }

    @GetMapping
    public ResultVO get(@Par("id") Long id){
        //业务数据简单校验
        if (StringUtil.isBlankOr(id)) {
            return new ResultVO(ResCodeEnum.ArgEmpty,"参数：id不能为空");
        }
        return baseManagerService.selectById(id);
    }

}
