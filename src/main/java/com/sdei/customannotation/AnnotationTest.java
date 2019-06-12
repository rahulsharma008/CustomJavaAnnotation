package com.sdei.customannotation;

import com.sdei.customannotation.annotation.Init;
import com.sdei.customannotation.annotation.JsonElement;
import com.sdei.customannotation.annotation.JsonSerializable;
import com.sdei.customannotation.data.Person;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnotationTest {

    public static void main(String[] arg) {
        try {
            Person person = new Person("soufiane", "cheouati", "34");
            checkIfSerializable(person);
            initializeObject(person);
            String json = getJsonString(person);
            System.out.print("JSON: " + json);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void checkIfSerializable(Object object) throws JsonSerializationException {

        if (Objects.isNull(object)) {
            throw new JsonSerializationException("The object to serialize is null");
        }

        Class<?> clazz = object.getClass();
        if (!clazz.isAnnotationPresent(JsonSerializable.class)) {
            throw new JsonSerializationException("The class "
                    + clazz.getSimpleName()
                    + " is not annotated with JsonSerializable");
        }

    }

    private static void initializeObject(Object object) throws Exception {
        Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Init.class)) {
                method.setAccessible(true);
                method.invoke(object);
            }
        }
    }

    private static String getJsonString(Object object) throws Exception {
        Class<?> clazz = object.getClass();
        Map<String, String> jsonElementsMap = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(JsonElement.class)) {
                jsonElementsMap.put(getKey(field), (String) field.get(object));
            }
        }

        String jsonString = jsonElementsMap.entrySet()
                .stream()
                .map(entry -> "\"" + entry.getKey() + "\":\""
                        + entry.getValue() + "\"")
                .collect(Collectors.joining(","));
        return "{" + jsonString + "}";
    }

    private static String getKey(Field field) {
        String value = field.getAnnotation(JsonElement.class)
                .key();
        return value.isEmpty() ? field.getName() : value;
    }

}
