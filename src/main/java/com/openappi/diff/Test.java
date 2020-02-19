package com.openappi.diff;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import io.swagger.v3.oas.models.media.Schema;

public class Test {

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Schema s = new Schema<>(); 
		s.set$ref("Ref Attached");
		s.setDescription("Desc");
		s.setExclusiveMaximum(true);
		for (Field field : s.getClass().getDeclaredFields()) {
//			if (Modifier.isPrivate(field.getModifiers())) {
         	   field.setAccessible(true);
         	   
         	   if(field.getType().equals(Boolean.class)) {
         		   BeanUtils.setProperty(s, field.getName(), true);
         	   }
         	   
         	   if(field.get(s) instanceof Boolean) {
         		  System.out.println(field.get(s));
         	   }
		}
	}
}
