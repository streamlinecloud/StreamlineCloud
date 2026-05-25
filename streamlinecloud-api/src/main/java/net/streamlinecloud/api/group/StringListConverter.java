package net.streamlinecloud.api.group;

import com.google.gson.Gson;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return (attribute == null) ? "[]" : gson.toJson(attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return List.of();
        return gson.fromJson(dbData, List.class);
    }
}
