<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ page import="ar.com.uoma.beans.SaldoInicial"%>

<%@ page import="ar.com.uoma.WebKeysUOMA"%>

<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="ar.com.ospim.global.beans.Seccional"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>	

<%
	Calendar current = CalendarFactoryUtil.getCalendar();
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	String newStr = (String)request.getAttribute("new");
	SaldoInicial _saldo=(SaldoInicial)request.getSession().getAttribute(WebKeysUOMA.SALDOINICIAL_EN_EDICION);
	String editStr = (String)request.getSession().getAttribute(WebKeysUOMA.ACCION);
		
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().toUpperCase().equals("VIEW")){
		esEdicion = false;
	}
	if (editStr != null && editStr.trim().toUpperCase().equals(WebKeysUOMA.ACCION_EDIT)){
		esEdicion = true;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	portlet_name = "uoma";
	
	if(_saldo==null){
		_saldo= new SaldoInicial();
	} 
	
	String origPeriodo = _saldo!=null && _saldo.getPeriodo_yyyymm() !=null ? _saldo.getPeriodo_yyyymm(): "";
	origPeriodo = origPeriodo + "-01";
	
	Integer origId = _saldo!=null && _saldo.getId() !=null ? _saldo.getId(): 0;
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
	
	if (origPeriodo != "") {
		auxPeriodoAnio = origPeriodo.split("-")[0].trim();
		auxPeriodoMes = origPeriodo.split("-")[1].trim();
		//origPeriodo = sdf.format(auxDate);		
	}
	
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
				<td><label><liferay-ui:message key="saldoinicial-tipocuenta" />:</label></td>
				<td>
				   <select name="<portlet:namespace/>tipocuenta_lista" id="<portlet:namespace/>tipocuenta_lista">
				   <option value="1" >Cuota Social Amtima</option>
				   <option value="2" >Cuota Social</option>
				   <option value="3" >Usufructo</option>
				   <option value="4" >Art.46</option>
				   <option value="5" >Aporte Solidario</option>
				   </select> 
				</td>			
				<td>
				
				<td colspan="2"><label>Período:</label></td>
				<td colspan="4"> <liferay-ui:input-date
						dayParam="periodoDia"
						dayValue="1" 
						dayNullable="<%= false %>"
						monthParam="periodoMes"
						monthValue="-1"	
						monthNullable="<%= true %>"			
						yearParam="periodoAnio"
						yearValue="-1"
						yearNullable="<%= true %>"
						yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= current.get(Calendar.YEAR)+2%>"
						firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
						disabled="false" />
				</td>				
								
				<td><label><liferay-ui:message key="saldoinicial-monto" />:</label></td>
				<td><input id="<portlet:namespace />monto"
					name="<portlet:namespace />monto" size="10"
					maxlength="10" type="number" value='<%=(auxMonto == null) ? "" : auxMonto %>' /></td>
				<td>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>

	</fieldset>
	<br> <input type="hidden"
		name="<portlet:namespace />altaAction"
		id="<portlet:namespace />altaAction" value="<%=true%>" />
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
		id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" />

</form>

<script type="text/javascript">

var popupNM;
<portlet:namespace />initFields();

jQuery("#view").val('<%= WebKeysUOMA.ACCION_NEW %>');

if (<%=auxPeriodoAnio!=null && auxPeriodoAnio != ""%>) {
	_mes = parseInt(<%=auxPeriodoMes%>) - 1;
	jQuery("#<portlet:namespace />periodoMes").val(_mes);
	jQuery("#<portlet:namespace />periodoAnio").val(<%=auxPeriodoAnio%>);
	jQuery("#<portlet:namespace />periodoDia").val(1);
	jQuery("#view").val('<%= WebKeysUOMA.ACCION_EDIT %>');
}

if (<%=origCtaNom !=null && origCtaNom !="" %>) {
	var auxcta  = document.getElementById("<portlet:namespace/>tipocuenta_lista");
	auxcta.value = <%=origTipoCta%>;
	jQuery("#view").val('<%= WebKeysUOMA.ACCION_EDIT %>');
}

jQuery('#<portlet:namespace />periodo').datepicker(jQuery.datepicker.regional['es']);

function <portlet:namespace />initFields(){
  if(<%=_saldo!=null && _saldo.getPeriodo_yyyymm()!=null && _saldo.getPeriodo_yyyymm() != ""%> ){
    jQuery("#<portlet:namespace />monto").val('<%=auxMonto %>');
  }  
}

function zfill(num, len) {return (Array(len).join("0") + num).slice(-len);}

function <portlet:namespace />salvarEdicion(){
	
	var auxcta  = document.getElementById("<portlet:namespace/>tipocuenta_lista");
	
	if (<portlet:namespace />validarCampos()) {			
		
		var periodoDia=1;
		var periodoMes=jQuery("#<portlet:namespace />periodoMes").val();
		var periodoAnio=jQuery("#<portlet:namespace />periodoAnio").val();
		periodoMes = (parseInt(periodoMes) + 1);
		periodoMes = zfill(periodoMes++, 2);
			
		document.getElementById("<portlet:namespace />monto").disabled=false;
		document.getElementById("<portlet:namespace/>tipocuenta_lista").disabled=false;
		
		var _per = periodoAnio + '-' + periodoMes + '-01';
		var _mon = jQuery("#<portlet:namespace />monto").val();
		var _cta = auxcta.value;

	    jQuery("#<portlet:namespace />campo_periodo").val(_per);
	    jQuery("#<portlet:namespace />campo_monto").val(_mon);
	    jQuery("#<portlet:namespace />campo_tipocta").val(_cta);    
			  
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.UPDATE %>";
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_saldoinicial';
		url = url + params;
				
		submitForm(document.<portlet:namespace />fmNTRS, url);	
	}
	return false;		
}

function <portlet:namespace />validarCampos(){
	
	var result = true;
	var portlet = '<%=portlet_name%>';
	var _monto = jQuery("#<portlet:namespace />monto").val();
	var auxcta  = document.getElementById("<portlet:namespace/>tipocuenta_lista");
	var _tipocta = auxcta.value;
	var _perMes=jQuery("#<portlet:namespace />periodoMes").val();
	var _perAnio=jQuery("#<portlet:namespace />periodoAnio").val();
		
	if ((_perMes=="") || (_perAnio=="")){
		result=false;
		alert("Debe ingresar Periodo");
	} else if (_monto==""){
		result=false;
		alert("Debe ingresar Monto");		
	} else if (_tipocta==""){
		result=false;
		alert("Debe ingresar Tipo de Cuenta");		
	} else if (!IsNumeric(_monto)) {
		result=false;
		alert("Debe ingresar una valor numérico de Monto");
	} else if (_monto <= 0 ){
		result=false;
		alert("Debe ingresar Monto mayor que cero");
	} 	
	return result;
}

</script>