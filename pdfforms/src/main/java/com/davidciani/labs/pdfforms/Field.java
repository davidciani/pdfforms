// Field.java
// Copyright 2021 David Ciani. All Rights Reserved.
// SPDX-License-Identifier: MIT

package com.davidciani.labs.pdfforms;


import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvNumber;

public class Field {
    
    @CsvBindByPosition(position = 0)
    private String fieldName;

    @CsvBindByPosition(position = 1)
    private String fieldType;

    @CsvBindByPosition(position = 2)
    @CsvNumber("0")
    private float x;

    @CsvBindByPosition(position = 3)
    @CsvNumber("0")
    private float y;

    @CsvBindByPosition(position = 4)
    @CsvNumber("0")
    private float width;

    @CsvBindByPosition(position = 5)
    @CsvNumber("0")
    private float height;
    
    public Field() {
        
    }

    public Field(String fieldName, String fieldType, float x, float y, float width, float height) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Getters and setters go here.
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = prime * result + ((fieldType == null) ? 0 : fieldType.hashCode());
        result = prime * result + Float.floatToIntBits(height);
        result = prime * result + Float.floatToIntBits(width);
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Field other = (Field) obj;
        if (fieldName == null) {
            if (other.fieldName != null)
                return false;
        } else if (!fieldName.equals(other.fieldName))
            return false;
        if (fieldType == null) {
            if (other.fieldType != null)
                return false;
        } else if (!fieldType.equals(other.fieldType))
            return false;
        if (Float.floatToIntBits(height) != Float.floatToIntBits(other.height))
            return false;
        if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width))
            return false;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
            return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Field [fieldName=" + fieldName + ", fieldType=" + fieldType + ", height=" + height + ", width=" + width
                + ", x=" + x + ", y=" + y + "]";
    }


}
