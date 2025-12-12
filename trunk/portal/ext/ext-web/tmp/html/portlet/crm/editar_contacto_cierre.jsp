<%@page import="ar.com.ospim.util.StringUtils"%>
<%@ include file="/html/portlet/crm/init.jsp"%>
<%
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "afiliados";
	}
	if(renderResponse.getNamespace().equals("_CAI_1_")){
		portlet_name = "cai";
	} 	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	PortletURL portletURL = renderResponse.createRenderURL();
	

	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM);
	
	ContactoCRM contacto = null;
	
	boolean esView = ParamUtil.getBoolean(request, "esView");
	
	if(esView){
		contacto = (ContactoCRM) request.getAttribute(WebKeysCrm.CRM_CONTACTO_EN_VIEW);
	}else{
		contacto = (ContactoCRM) request.getSession().getAttribute(WebKeysCrm.CRM_CONTACTO_EN_EDICION);
	}

	String comentarioCierrePredet = (String) request.getAttribute(WebKeysCrm.CRM_CIERRE_CONTACTO_PREDET);
	if(StringUtils.checkEmpty(comentarioCierrePredet)){
		comentarioCierrePredet ="";
	}
%>
<%if(contacto!=null&&contacto.getComentarioCierre()!=null){ %>
<textarea rows="5" cols="100" maxlength="90000" 
				id="<portlet:namespace />comentarios_contacto" 
				name="<portlet:namespace />comentarios_contacto"
				style="resize: none;"
				<% if (esView) { %> disabled="disabled" <%} %> ><%=contacto.getComentarioCierre() %>
				</textarea>
<%}else{ %>
<textarea rows="5" cols="100" maxlength="90000" 
				id="<portlet:namespace />comentarios_contacto" 
				name="<portlet:namespace />comentarios_contacto"
				style="resize: none;"
				<% if (esView) { %> disabled="disabled" <%} %> ><%=comentarioCierrePredet %></textarea>
<%} %>
				
<%-- <textarea rows="5" cols="100" maxlength="90000" 
				id="<portlet:namespace />comentarios_contacto" 
				name="<portlet:namespace />comentarios_contacto"
				style="resize: none;"
				<% if (esView) { %> disabled="disabled" <%} %> ><%= contacto!=null
				&&contacto.getComentarioCierre()!=null
				?contacto.getComentarioCierre() : comentarioCierrePredet != null?comentarioCierrePredet.trim():new String("")%>
				</textarea> --%>
	
