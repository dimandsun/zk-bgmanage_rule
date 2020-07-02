package cn.lxt6.util.json;

import cn.lxt6.config.enums.WashEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * json=>对象
 */
public class WashEnumDeserializer extends JsonDeserializer<WashEnum>{
    @Override
    public WashEnum deserialize(JsonParser p, DeserializationContext ctxt) {
            try {
                String value = p.getText();
                return WashEnum.getEnum(value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }

}
