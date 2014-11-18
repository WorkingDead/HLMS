package utils.convertor.struts2;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.Locale;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public class BigDecimalTypeConvertor extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		if (values == null || values.length == 0 || values[0].trim().length() == 0) {
            return null;
        }

        try {
    		DecimalFormat format = (DecimalFormat) NumberFormat.getInstance( Locale.TRADITIONAL_CHINESE ); 
    		format.setParseBigDecimal(true); 
        	
    		return (BigDecimal) format.parse( values[0] );
        } catch (Exception e) {
            throw new TypeConversionException("Unable to convert given object to big decimal: " + values[0]);
        }
	}

	@Override
	public String convertToString(Map context, Object cal) {
		if (cal != null && cal instanceof BigDecimal) {
            return ((BigDecimal)cal).toString();
        } else {
            return null;
        }
	}
	
}
