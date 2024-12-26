package com.devsuperior.dscommerce.dto;

public class FieldMessage {
    private String fieldName;
    private String name;

    public FieldMessage(String fieldName, String name) {
        this.fieldName = fieldName;
        this.name = name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getName() {
        return name;
    }
}
