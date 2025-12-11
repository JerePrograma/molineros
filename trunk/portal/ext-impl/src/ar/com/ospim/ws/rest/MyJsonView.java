package ar.com.ospim.ws.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.json.JsonErrorHandler;
import org.springframework.web.servlet.view.json.JsonStringWriter;
import org.springframework.web.servlet.view.json.JsonWriterConfiguratorTemplateRegistry;
import org.springframework.web.servlet.view.json.writer.sojo.SojoJsonStringWriter;

public class MyJsonView extends AbstractView implements
		org.springframework.web.servlet.View {
	private static final String DEFAULT_ENCODING = "UTF-8";

	private static final String DEFAULT_JSON_CONTENT_TYPE = "application/json";
	private static final String REQUEST_CONTEXT_ATTRIBUTE = RequestContext.class
			.toString();

	private List<JsonErrorHandler> jsonErrors = new ArrayList<JsonErrorHandler>();
	private JsonStringWriter jsonWriter = new SojoJsonStringWriter();
	private String encoding;

	public MyJsonView() {
		super();
		setRequestContextAttribute(REQUEST_CONTEXT_ATTRIBUTE);
		setContentType(DEFAULT_JSON_CONTENT_TYPE);
		setEncoding(DEFAULT_ENCODING);
	}

	public void renderMergedOutputModel(Map model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setCharacterEncoding(encoding);
		RequestContext rc = getRequestContext(model);
		BindingResult br = getBindingResult(model);
		JsonWriterConfiguratorTemplateRegistry configuratorTemplateRegistry = getConfiguratorTemplateRegistry(request);

		if (hasErrors(rc, br)) {
			populateErrors(model, rc, br);
			triggerJsonErrors(model, rc, br, request, response);
		}

		jsonWriter.convertAndWrite(model, configuratorTemplateRegistry,
				response.getWriter(), br);
	}

	protected void populateErrors(Map model, RequestContext rc, BindingResult br) {

		List<String> globalErrors = new ArrayList<String>();
		for (Object er : br.getGlobalErrors()) {
			ObjectError error = (ObjectError) er;
			String message = rc.getMessage(error);
			globalErrors.add(message);
		}

		model.put("hasGlobalErrors", br.hasGlobalErrors());
		if (!globalErrors.isEmpty())
			model.put("globalerrors", globalErrors);

		Map<String, String> feldErrors = new HashMap<String, String>();
		for (Object er : br.getFieldErrors()) {
			FieldError error = (FieldError) er;
			String objName = error.getField();
			String message = rc.getMessage(error);
			feldErrors.put(objName, message);
		}

		model.put("hasFieldErrors", br.hasFieldErrors());
		if (!feldErrors.isEmpty())
			model.put("fielderrors", feldErrors);

	}

	protected boolean hasErrors(RequestContext rc, BindingResult br) {
		if (br == null)
			return false;
		return br.hasErrors();
	}

	protected void triggerJsonErrors(Map model, RequestContext rc,
			BindingResult br, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (jsonErrors == null || jsonErrors.size() == 0)
			return;

		for (JsonErrorHandler error : jsonErrors) {
			error.triggerError(model, rc, br, request, response);
		}

	}

	protected BindingResult getBindingResult(Map model) {
		for (Object key : model.keySet()) {
			if (((String) key).startsWith(BindingResult.MODEL_KEY_PREFIX))
				return (BindingResult) model.remove(key);
		}
		return null;
	}

	protected RequestContext getRequestContext(Map model) {
		return (RequestContext) model.remove(getRequestContextAttribute());
	}

	protected JsonWriterConfiguratorTemplateRegistry getConfiguratorTemplateRegistry(
			HttpServletRequest request) {
		return JsonWriterConfiguratorTemplateRegistry.load(request);
	}

	public List<JsonErrorHandler> getJsonErrors() {
		return jsonErrors;
	}

	public void setJsonErrors(List<JsonErrorHandler> jsonErrors) {
		this.jsonErrors = jsonErrors;
	}

	public JsonStringWriter getJsonWriter() {
		return jsonWriter;
	}

	public void setJsonWriter(JsonStringWriter jsonWriter) {
		this.jsonWriter = jsonWriter;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

}