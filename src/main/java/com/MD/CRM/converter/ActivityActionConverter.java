package com.MD.CRM.converter;

import com.MD.CRM.entity.ActivityAction;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ActivityActionConverter implements AttributeConverter<ActivityAction, String> {

    @Override
    public String convertToDatabaseColumn(ActivityAction attribute) {
        if (attribute == null) return null;
        return attribute.name().toLowerCase();
    }

    @Override
    public ActivityAction convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return ActivityAction.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
