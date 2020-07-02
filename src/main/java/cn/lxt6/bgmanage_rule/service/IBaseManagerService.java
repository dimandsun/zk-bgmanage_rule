package cn.lxt6.bgmanage_rule.service;

import cn.lxt6.bgmanage_rule.model.db.BaseManagerPO;
import cn.lxt6.model.ResultVO;

/**
 * @author chenzy
 * @since 2020-06-30
 */
public interface IBaseManagerService {
    ResultVO insert(BaseManagerPO baseManager, Long handlerId, String handlerName);

    ResultVO update(BaseManagerPO baseManager, Long handlerId, String handlerName);

    ResultVO selectById(Long id);

    ResultVO delete(Long id, Long handlerId, String handlerName);
}
