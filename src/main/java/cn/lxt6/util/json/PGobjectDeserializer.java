package cn.lxt6.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.postgresql.util.PGobject;

import java.io.IOException;

public class PGobjectDeserializer extends JsonDeserializer<PGobject> {
    @Override
    public PGobject deserialize(JsonParser p, DeserializationContext ctxt)  {
        try {
            String value = p.getText();
            if (value.contains("{")&&value.contains("}")){
                return JsonUtil.str2Model(value,PGobject.class).get();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
