package cn.lxt6.util.json;


import cn.lxt6.config.enums.WashEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Optional;

/**
 * 序列化，将enum枚举转为json
 * @author chenzy
 * @date 2019.12.19
 */
public class WashEnumSerializer extends JsonSerializer<WashEnum> {
    @Override
    public void serialize(WashEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Optional<WashEnum> data = Optional.of(value);
        if (data.isPresent()) {//非空
            gen.writeObject(data.get().getValue());
        } else {
//            gen.writeString("");
        }
    }

}
