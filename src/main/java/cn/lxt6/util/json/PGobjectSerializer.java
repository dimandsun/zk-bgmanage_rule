package cn.lxt6.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.postgresql.util.PGobject;

import java.io.IOException;

public class PGobjectSerializer extends JsonSerializer<PGobject> {
    @Override
    public void serialize(PGobject value, JsonGenerator gen, SerializerProvider serializers) {
        try {
            gen.writeString(value.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
