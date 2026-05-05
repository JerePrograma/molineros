<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.interes.Interes"%>

<%@ page import="ar.com.ospim.tesoreria.WebKeysInteres"%>

<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="ar.com.ospim.global.beans.Seccional"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>	

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	String newStr = (String)request.getAttribute("new");
	Interes _interes=(Interes)request.getSession().getAttribute(WebKeysInteres.INTERES_EN_EDICION);
	String editStr = (String)request.getSession().getAttribute(WebKeysInteres.ACCION);
		
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().toUpperCase().equals("VIEW")){
		esEdicion = false;
	}
	if (editStr != null && editStr.trim().toUpperCase().equals(WebKeysInteres.ACCION_EDIT)){
		esEdicion = true;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	
	if(_interes==null){
		_interes= new Interes();
	} 
	
	String origFechaIni = _interes!=null && _interes.getFechaInicio() !=null ? _interes.getFechaInicio(): "";
	String origFechaFin = _interes!=null && _interes.getFechaFin() !=null ? _interes.getFechaFin(): "";
	
	String auxFechaIni = null;
	String auxFechaFin = null;
	String auxInteres = null;	
	Date auxDate;
	
	NumberFormat formatter = new DecimalFormat("#0.00000");
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	if (origFechaIni != "") {
		auxDate = sdfFormat.parse(origFechaIni);
		origFechaIni = sdf.format(auxDate);		
	}
	if (origFechaFin != "") {
		auxDate = sdfFormat.parse(origFechaFin);
		origFechaFin = sdf.format(auxDate);		
	}
	
	Integer entidad = WebKeysGlobal.OSPIM;
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		entidad = WebKeysGlobal.UOMA;
		portlet_name = "uoma";
	}	
	// Format FechaIni
	if (_interes.getFechaInicio() != null) {
		auxDate = sdfFormat.parse(_interes.getFechaInicio());
		auxFechaIni = sdf.format(auxDate);		
	}
		
	// Format FechaFin
	if (_interes.getFechaFin() != null) {
		auxDate = sdfFormat.parse(_interes.getFechaFin());
		auxFechaFin = sdf.format(auxDate);		
	}
	// Format Interes
	if (_interes.getInteresDia() != null) {
		auxInteres = formatter.format(_interes.getInteresDia());			
	}
			
%>

<form action="" method="post" name="<portlet:namespace />fmNTRS">

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />

	<fieldset class="block-labels">
		<legend>Interes Afip</legend>

		<table class="lfr-table">
			<tr>
				
				<td><label><liferay-ui:message key="fecha-inicio" />:</label>
					<input id="<portlet:namespace />fechaInicioInteres"
					name="<portlet:namespace />fechaInicioInteres" size="10"
					maxlength="10" type="text" disable value='<%=(auxFechaIni == null) ? "" : auxFechaIni %>' /></td>
				<td><label><liferay-ui:message key="fecha-fin" />:</label> <input
					id="<portlet:namespace />fechaFinInteres"
					name="<portlet:namespace />fechaFinInteres" size="10"
					maxlength="10" type="text" value='<%=(auxFechaFin == null) ? "" : auxFechaFin %>' /></td>
				<td><label><liferay-ui:message key="interes-por-dia" />:</label></td>
				<td><input id="<portlet:namespace />importeInteresDia"
					name="<portlet:namespace />importeInteresDia" size="10"
					maxlength="10" type="number" value='<%=(auxInteres == null) ? "" : auxInteres %>' /></td>
				<td>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>

	</fieldset>
	<br> <input type="hidden"
		name="<portlet:namespace />altaAction"
		id="<portlet:namespace />altaAction" value="<%=true%>" />
	<input type="hidden" name="<portlet:namespace />fecha_inicio"
		id="<portlet:namespace />fecha_inicio" value="<%=origFechaIni%>" />
	<input type="hidden" name="<portlet:namespace />fecha_fin"
		id="<portlet:namespace />fecha_fin" value="<%=origFechaFin%>" />	
	<input type="hidden" value="" name="view" id="view" /> <input
		id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" />

</form>

<script type="text/javascript">

var popupNM;
<portlet:namespace />initDateFields();

jQuery('#<portlet:namespace />fechaInicioInteres').datepicker(jQuery.datepicker.regional['es']);
jQuery('#<portlet:namespace />fechaFinInteres').datepicker(jQuery.datepicker.regional['es']);

function <portlet:namespace />initDateFields(){
  if(<%=_interes!=null && _interes.getFechaInicio()!=null && _interes.getFechaInicio() != ""%> ){
    jQuery("#<portlet:namespace />fechaInicioInteres").val('<%=auxFechaIni %>');
    jQuery("#<portlet:namespace />fechaFinInteres").val('<%=auxFechaFin %>');
    jQuery("#<portlet:namespace />importeInteresDia").val('<%=auxInteres %>');    
  }  
}

function <portlet:namespace />salvarEdicion(){
	if (<portlet:namespace />validarCampos()) {			
		document.getElementById("<portlet:namespace/>fechaInicioInteres").disabled=false;
		document.getElementById("<portlet:namespace/>fechaFinInteres").disabled=false;
		document.getElementById("<portlet:namespace/>importeInteresDia").disabled=false;
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.UPDATE %>";
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_interes';
		url = url + params;
		submitForm(document.<portlet:namespace />fmNTRS, url);	
	}
	return false;		
}

function <portlet:namespace />validarCampos(){
	var result = true;
	var portlet = '<%=portlet_name%>';
	var fDesde = jQuery("#<portlet:namespace/>fechaInicioInteres").val();
	var fHasta = jQuery("#<portlet:namespace/>fechaFinInteres").val();
	var intDia = jQuery('#<portlet:namespace />importeInteresDia').val();
		
	if (fDesde==""){
		result=false;
		alert("Debe ingresar Fecha de Inicio");
	} else if (fHasta==""){
		result=false;
		alert("Debe ingresar Fecha de Fin");		
	} else if (fDesde > fHasta) {
		result=false;
		alert("La Fecha de Fin debe ser superior a la fecha de Inicio");
	} else if (!IsNumeric(intDia)) {
		result=false;
		alert("Debe ingresar una valor numérico de Interes por Dia");
	} else if (intDia <= 0 ){
		result=false;
		alert("Debe ingresar Interes por Dia mayor que cero");
	} 	
	return result;
}


</script>

