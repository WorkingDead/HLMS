package utils.convertor.struts2;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public class BooleanTypeConvertor extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		if (values == null || values.length == 0 || values[0].trim().length() == 0) {
            return false;
        }

        try {
        	if ( values[0].equals("on") )
        		return true;
        	else
        		return false;
        } catch (Exception e) {
            throw new TypeConversionException("Unable to convert given object to Boolean: " + values[0]);
        }
	}

	@Override
	public String convertToString(Map context, Object cal) {
		if (cal != null && cal instanceof Boolean) {
            return ((Boolean)cal).toString();
        } else {
            return null;
        }
	}
}
