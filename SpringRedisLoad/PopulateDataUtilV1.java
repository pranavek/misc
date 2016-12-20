package com.prnv.load;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
/**
 * 
 * @author @pranavek
 *
 */
public class PopulateDataUtilV1 {

	public Date dateFor(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}

	/**
	 * 
	 * @param className eg: com.prnv.model.StubModel
	 * @return Oject - Cast it for accessing the fields
	 */
	public Object generateData(String className) {
		Object obj = null;
		try {
			Class<?> clazz = Class.forName(className);
			obj = clazz.newInstance();
			Field[] allFields = clazz.getDeclaredFields();
			for (Field field : allFields) {
				String fieldType = field.getType().getName();
				String fieldName = field.getName();
				field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				if (fieldType.indexOf("String") != -1) {				
					field.set(obj, "I'm a String, wbu?");
				} else if (fieldType.indexOf("int") != -1) {
					field.set(obj, 10);
				} else if (fieldType.indexOf("double") != -1) {
					field.set(obj, 10.0);
				} else if (fieldType.indexOf("Date") != -1) {
					field.set(obj, dateFor(2016, 10, 10));
				} else if (fieldType.indexOf("boolean") != -1) {
					field.set(obj, true);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return obj;
	}
 
}
