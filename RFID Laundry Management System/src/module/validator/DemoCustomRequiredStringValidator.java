package module.validator;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

public class DemoCustomRequiredStringValidator extends FieldValidatorSupport  {

	 private boolean doTrim = true;


	    public void setTrim(boolean trim) {
	        doTrim = trim;
	    }

	    public boolean getTrim() {
	        return doTrim;
	    }

	    public void validate(Object object) throws ValidationException {
	        String fieldName = getFieldName();
	        
	        System.out.println("Here is DemoRequiredStringValidator");
	        System.out.println("fieldName = " + fieldName);
	        System.out.println("object = " + object);
	        System.out.println("object = " + object.getClass().getName());
	        System.out.println("object = " + object.getClass().getSimpleName());
	        
	        Object value = this.getFieldValue(fieldName, object);
	        
	        System.out.println("value = " + value);
	        
	        if (!(value instanceof String)) {
	            addFieldError(fieldName, object);
	        } else {
	            String s = (String) value;

	            if (doTrim) {
	                s = s.trim();
	            }

	            if (s.length() == 0) {
	                addFieldError(fieldName, object);
	            }
	        }
	    }
	    

}
