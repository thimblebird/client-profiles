package io.thimblebird.clientprofiles.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassUtils {
    public static ArrayList<String> getFieldNames(Object getFromClass) {
        ArrayList<String> fieldNames = new ArrayList<>();

        List<Field> availableFields = Arrays.stream(getFromClass.getClass().getDeclaredFields()).toList();
        availableFields.forEach(field -> fieldNames.add(field.getName()));

        return fieldNames;
    }
}
