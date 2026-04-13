<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ page import="ar.com.uoma.beans.SaldoInicial"%>

<%@ page import="ar.com.uoma.WebKeysUOMA"%>

<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>	

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	String newStr = (String)request.getAttribute("new");
	SaldoInicial _saldo=(SaldoInicial)request.getSession().getAttribute(WebKeysUOMA.SALDOINICIAL_EN_EDICION);
	String editStr = (String)request.getSession().getAttribute(WebKeysUOMA.ACCION);
		
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().toUpperCase().equals("VIEW")){
		esEdicion = false;
	}
	if (editStr != null && editStr.trim().toUpperCase().equals(WebKeysUOMA.ACCION_DELETE)){
		esEdicion = true;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "uoma";
	}
	
	if(_saldo==null){
		_saldo= new SaldoInicial();
	} 

	Integer origId = _saldo!=null && _saldo.getId() !=null ? _saldo.getId(): 0;
	String origPeriodo = _saldo!=null && _saldo.getPeriodo_yyyymm() !=null ? _saldo.getPeriodo_yyyymm(): "";
	Double origMonto = _saldo!=null && _saldo.getMonto() !=null ? _saldo.getMonto(): 0;
	Integer origTipoCta = _saldo!=null && _saldo.getTipoBoleta() !=null ? _saldo.getTipoBoleta(): 0;
	String origCtaNom = _saldo!=null && _saldo.getCuentaNombre() !=null ? _saldo.getCuentaNombre(): "";
	String origCuit = _saldo!=null && _saldo.getCuit() !=null ? _saldo.getCuit(): "";
	String origSuc = _saldo!=null && _saldo.getSucursal() !=null ? _saldo.getSucursal(): "";
	
	String auxPeriodo = null;
	String auxPeriodoMes = null;
	String auxPeriodoAnio = null;
	String auxMonto = null;	
	String auxTipoCuenta = null;
	Date auxDate;
	
	NumberFormat formatter = new DecimalFormat("#0.00");
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd");
			
	// Format Interes
	if (_saldo.getMonto() != null) {
		auxMonto = formatter.format(_saldo.getMonto());			
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
		<legend>Saldo Inicial</legend>

		<table class="lfr-table">
			<tr>
				<td><label><liferay-ui:message key="saldoinicial-tipocuenta" />:</label> <input
					id="<portlet:namespace />tipocuenta"
					name="<portlet:namespace />tipocuenta" size="20"
					maxlength="20" type="text"  disable value='<%=(origCtaNom == null) ? "" : origCtaNom %>' /></td>
				<td><label><liferay-ui:message key="Periodo" />:</label>
					<input id="<portlet:namespace />periodo"
					name="<portlet:namespace />periodo" size="10"
					maxlength="10" type="text" disable value='<%=(origPeriodo == null) ? "" : origPeriodo %>' /></td>					
				<td><label><liferay-ui:message key="saldoinicial-monto" />:</label></td>
				<td><input id="<portlet:namespace />monto"
					name="<portlet:namespace />monto" size="10"
					maxlength="10" type="number"  disable value='<%=(origMonto == null) ? "" : origMonto %>' /></td>
				<td>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>

	</fieldset>
	<br> 
	<input type="hidden" name="<portlet:namespace />campo_id"
		id="<portlet:namespace />campo_id" value="<%=origId%>" />
	<input type="hidden" name="<portlet:namespace />campo_cuit"
		id="<portlet:namespace />campo_cuit" value="<%=origCuit%>" />
	<input type="hidden" name="<portlet:namespace />campo_suc"
		id="<portlet:namespace />campo_suc" value="<%=origSuc%>" />
	<input type="hidden" name="<portlet:namespace />campo_periodo"
		id="<portlet:namespace />campo_periodo" value="<%=origPeriodo%>" />
	<input type="hidden" name="<portlet:namespace />campo_monto"
		id="<portlet:namespace />campo_monto" value="<%=origMonto%>" />	
	<input type="hidden" name="<portlet:namespace />campo_tipocta"
		id="<portlet:namespace />campo_tipocta" value="<%=origTipoCta%>" />		
	<input type="hidden" value="" name="view" id="view" /> <input
		id="<portlet:namespace />action.DELETE"
		value="<liferay-ui:message key="action.DELETE"/>"
		title="<liferay-ui:message key="action.DELETE" />"
		onClick="javascript: <portlet:namespace />eliminarInteres();"
		type="button" />

</form>

<script type="text/javascript">

var popupNM;

function <portlet:namespace />eliminarInteres(){
	if (<portlet:namespace />validarCampos()) {	
		document.getElementById("<portlet:namespace/>tipocuenta").disabled=false;
		document.getElementById("<portlet:namespace/>periodo").disabled=false;
		document.getElementById("<portlet:namespace/>monto").disabled=false;
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.DELETE %>";
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_saldoinicial';
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

