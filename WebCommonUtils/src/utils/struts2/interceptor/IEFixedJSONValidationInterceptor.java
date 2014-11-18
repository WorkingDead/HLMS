package utils.struts2.interceptor;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/*
 * This class is the copy of JSONValidationInterceptor in struts version 2.3.1.2
 * to fixed ie will download as file when content type is "application/json"
 * if struts 2 version is updated, this class will need to update the code from struts 2 source
 *  
 */

public class IEFixedJSONValidationInterceptor extends MethodFilterInterceptor {
	
	//ie will download as file when content type is "application/json"
	//just change the content type to text/plain to prevent the issue occur
	private static final String contentType = "text/plain";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7933151579948447945L;

	private static final Logger LOG = LoggerFactory.getLogger(IEFixedJSONValidationInterceptor.class);

	private static final String VALIDATE_ONLY_PARAM = "struts.validateOnly";
	private static final String VALIDATE_JSON_PARAM = "struts.enableJSONValidation";
	private static final String NO_ENCODING_SET_PARAM = "struts.JSONValidation.no.encoding";

	private static final String DEFAULT_ENCODING = "UTF-8";

	private int validationFailedStatus = -1;

	/**
	 * HTTP status that will be set in the response if validation fails
	 * @param validationFailedStatus
	 */
	public void setValidationFailedStatus(int validationFailedStatus) {
		this.validationFailedStatus = validationFailedStatus;
	}

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();

		Object action = invocation.getAction();

		if (isJsonEnabled(request)) {
			if (action instanceof ValidationAware) {
				// generate json
				ValidationAware validationAware = (ValidationAware) action;
				if (validationAware.hasErrors()) {
					return generateJSON(request, response, validationAware);
				}
			}
			if (isValidateOnly(request)) {
				//there were no errors
				setupEncoding(response, request);
				response.getWriter().print("{}");
//				response.setContentType("application/json");//will have issue in ie
				response.setContentType(contentType);
				return Action.NONE;
			} else {
				return invocation.invoke();
			}
		} else
			return invocation.invoke();
	}

	private void setupEncoding(HttpServletResponse response, HttpServletRequest request) {
		if (isSetEncoding(request)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Default encoding not set!");
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Setting up encoding to: [" + DEFAULT_ENCODING + "]!");
			}
			response.setCharacterEncoding(DEFAULT_ENCODING);
		}
	}

	private String generateJSON(HttpServletRequest request, HttpServletResponse response, ValidationAware validationAware)
			throws IOException {
		if (validationFailedStatus >= 0) {
			response.setStatus(validationFailedStatus);
		}
		setupEncoding(response, request);
		response.getWriter().print(buildResponse(validationAware));
//		response.setContentType("application/json");
		response.setContentType(contentType);
		return Action.NONE;
	}

	private boolean isJsonEnabled(HttpServletRequest request) {
		return "true".equals(request.getParameter(VALIDATE_JSON_PARAM));
	}

	private boolean isValidateOnly(HttpServletRequest request) {
		return "true".equals(request.getParameter(VALIDATE_ONLY_PARAM));
	}

	private boolean isSetEncoding(HttpServletRequest request) {
		return "true".equals(request.getParameter(NO_ENCODING_SET_PARAM));
	}

	/**
	 * @return JSON string that contains the errors and field errors
	 */
	@SuppressWarnings("unchecked")
	protected String buildResponse(ValidationAware validationAware) {
		//should we use FreeMarker here?
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");

		if (validationAware.hasErrors()) {
			//action errors
			if (validationAware.hasActionErrors()) {
				sb.append("\"errors\":");
				sb.append(buildArray(validationAware.getActionErrors()));
			}

			//field errors
			if (validationAware.hasFieldErrors()) {
				if (validationAware.hasActionErrors())
					sb.append(",");
				sb.append("\"fieldErrors\": {");
				Map<String, List<String>> fieldErrors = validationAware
						.getFieldErrors();
				for (Map.Entry<String, List<String>> fieldError : fieldErrors
						.entrySet()) {
					sb.append("\"");
					//if it is model driven, remove "model." see WW-2721
					String fieldErrorKey = fieldError.getKey();
					sb.append(((validationAware instanceof ModelDriven) &&  fieldErrorKey.startsWith("model."))? fieldErrorKey.substring(6)
							: fieldErrorKey);
					sb.append("\":");
					sb.append(buildArray(fieldError.getValue()));
					sb.append(",");
				}
				//remove trailing comma, IE creates an empty object, duh
				sb.deleteCharAt(sb.length() - 1);
				sb.append("}");
			}
		}

		sb.append("}");
		/*response should be something like:
		 * {
		 *      "errors": ["this", "that"],
		 *      "fieldErrors": {
		 *            field1: "this",
		 *            field2: "that"
		 *      }
		 * }
		 */
		return sb.toString();
	}

	private String buildArray(Collection<String> values) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (String value : values) {
			sb.append("\"");
			sb.append(StringEscapeUtils.escapeJavaScript(value));
			sb.append("\",");
		}
		if (values.size() > 0)
			sb.deleteCharAt(sb.length() - 1);
		sb.append("]");
		return sb.toString();
	}
}
