package cn.lxt6.util;

import cn.lxt6.config.core.model.ProjectInfo;
import cn.lxt6.config.enums.ResCodeEnum;
import cn.lxt6.model.ResultVO;

/**
 * @author chenzy
 *
 * @since 2020-03-25
 */
public class ExceptionUtil {

    private ExceptionUtil() {
    }


    public static String getMSG(Throwable throwable) {
        String groupId = ProjectInfo.getInstance().getProjectGroupId();
        String msg = throwable.getMessage() + ":" + throwable.getClass().getSimpleName();
        for (StackTraceElement element : throwable.getStackTrace()) {
            if (element.getClassName().startsWith(groupId)) {
                return msg + "-->" + element.getClassName().substring(element.getClassName().lastIndexOf(".") + 1)
                        + "." + element.getMethodName() + "-->行：" + element.getLineNumber();
            }
        }
        return msg;
    }

    public static ResultVO getResult(Throwable throwable) {
        return new ResultVO(ResCodeEnum.UnknownExce, getMSG(throwable));
    }
}
