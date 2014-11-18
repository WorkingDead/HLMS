package utils.convertor.struts2;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public class StringListTypeConvertor extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		if (values == null || values.length == 0 ) {
			new LinkedList<String>();
        }

        try {
        	List<String> list = new LinkedList<String>();
        	
        	for ( int i = 0; i < values.length; i++ ) {
        		//System.out.println("value " + i + " = " + values[i]);
        		
        		list.add( (String)values[i] );
        	}
        	
    		return list;
        } catch (Exception e) {
            throw new TypeConversionException("Unable to convert given object to List<String>");
        }
	}

	@Override
	public String convertToString(Map context, Object cal) {
		if (cal != null && cal instanceof List) {

			List<String> list = (List<String>)cal;
			
			StringBuilder sb = new StringBuilder();
			
			if ( list.size() == 1 ) {
				sb.append( list.get(0) );
			}
			else {
				for ( int i = 0; i < list.size()-1; i++ ) {
					sb.append(list.get(i) + ", ");
				}
				
				sb.append( list.get( list.size()-1 ) );
			}

			return sb.toString();
        } else {
            return null;
        }
	}
}
