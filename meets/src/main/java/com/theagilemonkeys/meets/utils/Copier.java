package com.theagilemonkeys.meets.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public class Copier {

    private Set<Class> ignoreInstancesOf = new HashSet<Class>();
    private boolean ignoreNulls = false;

    /**
     * Set the classes whose instances won't be touched, that is, each field in the source object whose
     * declared class is among the arguments passed to this function won't assigned in the destination object.
     * NOTE: This works with instances which are subclasses of the passed ones too.
     * @param classes One or many Class objects.
     * @return this for chaining purposes
     */
    public Copier ignoreInstancesOf(Class... classes){
        for (Class c : classes){
            ignoreInstancesOf.add(c);
        }
        return this;
    }

    /**
     * Set whether nulls fields on the source object are copied to destination object. True by default.
     * @param ignoreNulls
     * @return this for chaining purposes
     */
    public Copier setIgnoreNulls(boolean ignoreNulls) {
        this.ignoreNulls = ignoreNulls;
        return this;
    }

    /**
     * Make a shallow copy of all fields in src object to dst object. The fields are copied if the have the
     * same name and its types are assignable.
     * @param dst Destination object
     * @param src Source object
     * @return this for chaining purposes
     * @throws IllegalAccessException
     */
    public Copier copyProperties(Object dst, Object src) {
        List<Field> dstFields = Arrays.asList(dst.getClass().getDeclaredFields());

        try{
            for(Field field : src.getClass().getDeclaredFields()){
                field.setAccessible(true);
                Object value = field.get(src);

                if ( ignoreFieldValue(value) ) continue;
                if ( ! dstFields.contains(field) ) continue;

                field.set(dst, value);
            }
        } catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }

        return this;
    }

    private boolean ignoreFieldValue(Object value) {
        if (ignoreNulls && value == null) return true;

        for ( Class ignoredClassTree : ignoreInstancesOf ){
            if ( ignoredClassTree.isInstance(value) ) return true;
        }

        return false;
    }

}
