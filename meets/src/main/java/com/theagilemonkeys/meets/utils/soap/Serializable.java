package com.theagilemonkeys.meets.utils.soap;

import com.google.api.client.util.Key;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedHashMap;

/**
 * Created by kloster on 14/11/13.
 */
public class Serializable {

    /**
     * Annotation to allow specify the type of collections
     */
    public @Retention(RetentionPolicy.RUNTIME) @interface PropertyName {
        String value();
    }

    public static class Object implements KvmSerializable {
        private static final java.util.List unserializableTypes = Arrays.asList(Float.class, float.class, Double.class, double.class);
        private java.util.List<Field> fields = null;

        private java.util.List<Field> getFields(){
            if (fields != null) return fields;

            fields = new ArrayList<Field>();

            for(Field field : getClass().getDeclaredFields() ){
                Key keyAnnotation = field.getAnnotation(Key.class);
                if ( keyAnnotation != null ) fields.add(field);
            }

            return fields;
        }


        private java.lang.Object getPropertyValue(Field field) throws IllegalAccessException {
            // This is fix the serialization problems Soap has with doubles and floats
            java.lang.Object val = field.get(this);
            if ( unserializableTypes.contains(field.getType()) ) {
                val = String.valueOf(val);
            }
            return val;
        }

        @Override
        public java.lang.Object getProperty(int i) {
            try{
                Field field = getFields().get(i);
                field.setAccessible(true);
                return getPropertyValue(field);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        @Override
        public int getPropertyCount() {
            return getFields().size();
        }

        @Override
        public void setProperty(int i, java.lang.Object o) {
            try{
                Field field = getFields().get(i);
                field.setAccessible(true);
                field.set(this, o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        @Override
        public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
            Field field = getFields().get(i);

            //Check if this field has a specific name
            PropertyName propertyName = field.getAnnotation(PropertyName.class);
            if (propertyName != null)
                propertyInfo.setName(propertyName.value());
            else
                propertyInfo.setName(field.getName());

            propertyInfo.setType(field.getType());
        }
    }

    public static class List<TYPE> extends ArrayList<TYPE> implements KvmSerializable {
        protected String listItemName = "item";

        public List() {}

        public List(Collection<TYPE> c) {
            super(c);
        }

        @Override
        public java.lang.Object getProperty(int i) {
            return get(i);
        }

        @Override
        public int getPropertyCount() {
            return size();
        }

        @Override
        public void setProperty(int i, java.lang.Object o) {
            add((TYPE) o);
        }

        @Override
        public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
            propertyInfo.setName(listItemName);
            propertyInfo.setType(get(i).getClass());
        }
    }

    public static class Map<KEY_TYPE, VALUE_TYPE> extends LinkedHashMap<KEY_TYPE, VALUE_TYPE> implements KvmSerializable {
        private java.util.List<VALUE_TYPE> values = null;
        private java.util.List<KEY_TYPE> keys = null;

        private java.util.List<VALUE_TYPE> getMapValues(){
            if (values != null) return values;
            values = new ArrayList<VALUE_TYPE>(values());
            return values;
        }

        private java.util.List<KEY_TYPE> getMapKeys(){
            if (keys != null) return keys;
            keys = new ArrayList<KEY_TYPE>(keySet());
            return keys;
        }

        @Override
        public java.lang.Object getProperty(int i) {
            return getMapValues().get(i);
        }

        @Override
        public int getPropertyCount() {
            return size();
        }

        @Override
        public void setProperty(int i, java.lang.Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
            KEY_TYPE key = getMapKeys().get(i);
            propertyInfo.setName(String.valueOf(key));
            propertyInfo.setType(get(key).getClass());
        }
    }
}
