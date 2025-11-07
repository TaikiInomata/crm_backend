package com.MD.CRM.converter;

import com.MD.CRM.entity.ActivityType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ActivityTypeConverter implements AttributeConverter<ActivityType, String> {

    @Override
    public String convertToDatabaseColumn(ActivityType attribute) {
        if (attribute == null) return null;
        return attribute.name().toLowerCase();
    }

    @Override
    public ActivityType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return ActivityType.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
