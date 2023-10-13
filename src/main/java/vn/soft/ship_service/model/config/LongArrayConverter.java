package vn.soft.ship_service.model.config;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;

@Converter
public class LongArrayConverter implements AttributeConverter<Long[], String> {
    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(Long[] attribute) {
        if (attribute == null) {
            return null;
        }
        return String.join(DELIMITER, Arrays.toString(attribute));
    }

    @Override
    public Long[] convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        dbData = dbData.replaceAll("\\[|\\]", ""); // Remove square brackets
        String[] values = dbData.split(DELIMITER);
        Long[] result = new Long[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Long.parseLong(values[i].trim());
        }
        return result;
    }
}

