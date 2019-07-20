package org.roach.intelligentagents;

import com.beust.jcommander.IStringConverter;

public class ClassConverter implements IStringConverter<Class<?>> {
    @Override
    public Class<?> convert(String value) {
        try {
            return Class.forName(value);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
