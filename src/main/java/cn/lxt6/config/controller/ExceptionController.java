package cn.lxt6.config.controller;


import cn.lxt6.config.core.annotation.bean.Controller;
import cn.lxt6.config.core.annotation.mapping.Mapping;
import cn.lxt6.config.enums.ResCodeEnum;
import cn.lxt6.model.ResultVO;

/**
 * @author chenzy
 * @since 2020-02-27
 *  拦截404、500错误
 */
@Controller
@Mapping("/error")
public class ExceptionController {

    @Mapping("/error_404")
    public ResultVO error_404(){
        return new ResultVO(ResCodeEnum.NotFound);
    }

    @Mapping("/error_500")
    public ResultVO error_500(){
        return new ResultVO(ResCodeEnum.DBExce);
    }
}
