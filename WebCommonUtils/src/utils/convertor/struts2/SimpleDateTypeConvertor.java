package utils.convertor.struts2;

import java.util.Date;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import utils.convertor.DateConverter;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public class SimpleDateTypeConvertor extends StrutsTypeConverter {
	
	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		if (values == null || values.length == 0 || values[0].trim().length() == 0) {
            return null;
        }

        try {
            return DateConverter.toDate(values[0], DateConverter.DATE_FORMAT_REVERSE);
        } catch (Exception e) {
            throw new TypeConversionException("Unable to convert given object to date: " + values[0]);
        }
	}

	@Override
	public String convertToString(Map context, Object date) {
		if (date != null && date instanceof Date) {         
            return DateConverter.format((Date)date, DateConverter.DATE_FORMAT_REVERSE);
        } else {
            return null;
        }
	}

}
