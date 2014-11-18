package utils.convertor.struts2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import utils.convertor.DateConverter;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public class SimpleDateTimeToCalendarTypeConvertor extends StrutsTypeConverter {
	
	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		if (values == null || values.length == 0 || values[0].trim().length() == 0) {
            return null;
        }

        try {
            return DateConverter.toCalendar(values[0], DateConverter.DATE_FORMAT_REVERSE);
        } catch (Exception e) {
            throw new TypeConversionException("Unable to convert given object to date: " + values[0]);
        }
	}

	@Override
	public String convertToString(Map context, Object cal) {
		if (cal != null && cal instanceof Calendar) {
            return DateConverter.format((Calendar) cal, DateConverter.DATE_FORMAT_REVERSE);
        } else {
            return null;
        }
	}

}
