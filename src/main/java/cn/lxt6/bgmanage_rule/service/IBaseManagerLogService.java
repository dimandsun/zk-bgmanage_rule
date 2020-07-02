package cn.lxt6.bgmanage_rule.service;

import cn.lxt6.bgmanage_rule.model.db.BaseManagerPO;
import cn.lxt6.model.ResultVO;

/**
 * @author chenzy
 * @since 2020-06-30
 */
public interface IBaseManagerLogService {

    ResultVO<Long> insert(BaseManagerPO baseManager, Long handlerId, String handlerName);

    ResultVO<Long> insert(BaseManagerPO baseManager, BaseManagerPO data, BaseManagerPO baseManagerPO, Long handlerId, String handlerName);
}
