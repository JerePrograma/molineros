<%@include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/hoteles/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "hoteles";
	}
	
	String ptoVtaAfip="00030";

	try{
		ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
	}catch(Exception e){
		//ptoVtaAfip="0000";
		ptoVtaAfip="00030";
	}
	
	List<Habitacion> habitaciones =  HotelesServiceUtil.getHabitaciones(ptoVtaAfip, null);

	session.setAttribute(WebKeysHoteles.HABITACIONES_RESULT,habitaciones);
	
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data">
    <input name="<portlet:namespace />id_hotel" type="hidden"	value="<%=ptoVtaAfip%>" /> 
	<fieldset class="block-labels">
		<legend>ABM Habitaciones</legend>
		
		<input type="button" value="Nuevo" onClick="<portlet:namespace />nuevaHabitacion();"/>&nbsp;
		
		

	  
		<div id="<portlet:namespace />div_habitaciones">
			<jsp:include page='/html/portlet/hoteles/habitaciones_result.jsp' />  	
		</div>
		
	</fieldset>
</form>		

<script type="text/javascript">
	
	<portlet:namespace />initDateFields();
	
	function <portlet:namespace />initDateFields(){
	}
	
	function <portlet:namespace />nuevaHabitacion() {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_habitaciones_abm" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
		
	
</script>

