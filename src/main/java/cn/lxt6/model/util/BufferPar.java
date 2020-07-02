package cn.lxt6.model.util;

/**
 * @author chenzy
 * @since 2020-03-24
 *  对拼接请求参数的简化，参数以#?分割
 简化前：
StringBuilder par = new StringBuilder("client=").append(clientTypeEnum.getValue())
.append("#?orderId=").append(unPay.getOrderId())
.append("#?typeId=").append(appEnum)
.append("#?schoolId=").append(schoolId)
.append("#?consumeAmount=").append(unPay.getPay())
.append("#?serialNumber=").append(machineId)
.append("#?consumeTime=").append(DateUtil.data2Str(unPay.getPosTime()));
 简化后：
BufferPar par = new BufferPar("client",clientTypeEnum.getValue()).add("orderId",unPay.getOrderId())
.add("typeId",appEnum).add("schoolId",schoolId).add("consumeAmount",unPay.getPay())
.add("serialNumber",machineId).add("consumeTime",DateUtil.data2Str(unPay.getPosTime()));
 */
public class BufferPar {
    private StringBuffer sb;

    public BufferPar() {
        this.sb =new StringBuffer();
    }

    public BufferPar(String key, Object value) {
        this.sb =new StringBuffer(key+"="+value);
    }
    @Override
    public String toString() {
        return sb==null?"":sb.toString();
    }
    /**
     * @param key
     * @param value
     * @return
     */
    public BufferPar add(String key, Object value){
        sb.append("#?"+key+"="+value);
        return this;
    }
}
