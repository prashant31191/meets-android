package com.theagilemonkeys.meets.utils.soap;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kloster on 30/09/13.
 */
public class SoapParser {
    /**
     * Annotation to allow specify the type of collections
     */
    public @Retention(RetentionPolicy.RUNTIME) @interface ListType {
        Class value();
    }
    /**
     * Annotation to allow specify the type of map
     */
    public @Retention(RetentionPolicy.RUNTIME) @interface MapType {
        Class value();
    }
    /**
     * A list with all classes known to be immutables. They will be assigned directly from SoapObject
     * to target object
     */
    private static final List immutables = Arrays.asList(
            String.class, Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class, Boolean.class, BigInteger.class, BigDecimal.class
    );
//
//    /**
//     * A list with all decimal types which are incompatible with Soap and are sent as Strings
//     */
//    private static final List decimalTypes = Arrays.asList(
//            Float.class, Double.class, double.class, float.class
//    );

    /**
     * Parse a SoapObject into any object.
     * @param in The SoapObject
     * @param out The target object. This will be used as a "template" to correctly parse the SoapObject, so field names
     *            must match and types must be compatible. All types are supported but native array. Any subclass of Collection
     *            can be used in that case (ArrayList, for example). If that subclass is an interface or an abstract class, an
     *            annotation must be used to specify the concrete collection type. Only List and AbstractList classes are supported
     *            without annotation (ArrayList is used in both cases)
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void parse(SoapObject in, Object out) throws IllegalAccessException, InstantiationException {
        if ( isCollection(out.getClass())) {
            parseCollection(in, (Collection) out);
        }
        else if (isMap(out.getClass())) {
            parseMap(in, (Map) out);
        }
        else {
            parseObject(in, out);
        }
    }

//    public static Map<String,Object> parseAsMap(SoapObject in){
//        int n = in.getPropertyCount();
//        for (int i = 0; i < n; ++i) {
//            PropertyInfo propInfo = new PropertyInfo();
//            in.getPropertyInfo(i, propInfo);
//
//            Object prop = in.getProperty(i);
//            int i = 0;
//             i = 9;
//
//        }
//        return null;
//    }

    private static Object getPrimitiveValue(Class valueClass, Object value) {
        if ( valueClass == Float.class || valueClass == float.class ){
            return Float.parseFloat(value.toString());
        }
        else if ( valueClass == Double.class || valueClass == double.class ){
            return Double.parseDouble(value.toString());
        }
        return value;
    }

    private static void parseCollection(SoapObject in, Collection outCollection) throws IllegalAccessException, InstantiationException {
        // Get the type of the elements in the collection
        ParameterizedType fieldParametrizedType = (ParameterizedType) outCollection.getClass().getGenericSuperclass();
        Class componentType = (Class) fieldParametrizedType.getActualTypeArguments()[0];

        int n = in.getPropertyCount();

        for (int i = 0; i < n; ++i){
            Object soapElem = in.getProperty(i);
            Object elem;
            if ( canBeAssignedDirectly(soapElem.getClass()) ) {
                elem = soapElem;
            }
            else {
                elem = componentType.newInstance();
                parse((SoapObject) soapElem, elem);
            }
            outCollection.add(elem);
        }
    }

    private static void parseMap(SoapObject in, Map outMap) throws IllegalAccessException, InstantiationException {
        // Get the type of the elements in the collection
        ParameterizedType fieldParametrizedType = (ParameterizedType) outMap.getClass().getGenericSuperclass();
        Class valType = (Class) fieldParametrizedType.getActualTypeArguments()[1];

        int n = in.getPropertyCount();

        for (int i = 0; i < n; ++i){
            PropertyInfo propInfo = new PropertyInfo();
            in.getPropertyInfo(i, propInfo);

            Object soapElem = in.getProperty(i);
            String key = propInfo.getName();
            Object elem;

            if ( soapElem == null || canBeAssignedDirectly(soapElem.getClass()) ) {
                elem = soapElem;
            }
            else {
                elem = valType.newInstance();
                parse((SoapObject) soapElem, elem);
            }

            outMap.put(key, elem);
        }
    }

    private static void parseObject(SoapObject in, Object out) throws IllegalAccessException, InstantiationException {

        for(Field field : out.getClass().getDeclaredFields()){
            field.setAccessible(true);
            String fieldName = field.getName();

            // Ignore fields that doesn't exist in SoapObject
            if ( ! in.hasProperty(fieldName) ) continue;

            Object valueToParse = in.getProperty(fieldName);
            Class fieldClass = field.getType();

            // If the type of field is Object, primitive or immutable, assign it directly
            if ( canBeAssignedDirectly(fieldClass) ){
                field.set(out, getPrimitiveValue(fieldClass, valueToParse));
            }
            else if ( isCollection(fieldClass) ) {
                fieldClass = getSpecificCollectionClass(field);
                Collection c = (Collection) fieldClass.newInstance();
                parse((SoapObject) valueToParse,c);
                field.set(out, c);
            }
            else if ( isMap(fieldClass) ) {
                fieldClass = getSpecificMapClass(field);
                Map map = (Map) fieldClass.newInstance();
                parse((SoapObject) valueToParse,map);
                field.set(out, map);
            }
            else{
                Object fieldObject = fieldClass.newInstance();
                parse((SoapObject) valueToParse,fieldObject);
                field.set(out, fieldObject);
            }
        }
    }

    private static Class getSpecificCollectionClass(Field field) {
        Class fieldType = field.getType();
        int modifiers = fieldType.getModifiers();

        // Only in these cases an specific class is needed
        if ( Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) ){
            ListType listTypeAnnotation = field.getAnnotation(ListType.class);

            if ( listTypeAnnotation != null )
                fieldType = listTypeAnnotation.value();
             else
                fieldType = ArrayList.class;
        }
        return fieldType;
    }

    private static Class getSpecificMapClass(Field field) {
        Class fieldType = field.getType();
        int modifiers = fieldType.getModifiers();

        // Only in these cases an specific class is needed
        if ( Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) ){
            MapType mapTypeAnnotation = field.getAnnotation(MapType.class);

            if ( mapTypeAnnotation != null )
                fieldType = mapTypeAnnotation.value();
             else
                fieldType = HashMap.class;
        }
        return fieldType;
    }

    private static boolean canBeAssignedDirectly(Class c){
        return isPrimitiveOrInmutable(c) || c == Object.class;
    }

    private static boolean isCollection(Class klass){
        return Collection.class.isAssignableFrom(klass);
    }

    private static boolean isMap(Class klass){
        return Map.class.isAssignableFrom(klass);
    }

    // Public useful methods

    /**
     * Returns true if the class passed is primitive or immutable (the Java known immutables: Integer, Long, String, etc.)
     * @param c The class to check
     * @return
     */
    public static boolean isPrimitiveOrInmutable(Class c){
        return c.isPrimitive() || immutables.contains(c);
    }

}
