<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="ar.com.ospim.global.beans.Seccional" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.global.beans.Comprobante"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	
	NumberFormat formatter = new DecimalFormat("#0.00");
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	
	Integer entidad = WebKeysGlobal.OSPIM;
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		entidad = WebKeysGlobal.UOMA;
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	CajaChica cajaChica=(CajaChica)request.getSession().getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
	if(cajaChica==null){
		cajaChica= new CajaChica();
	} 
	
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	%>

<form action="" method="post" name="<portlet:namespace />fmCJCHIR">

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	<fieldset class="block-labels">
		<legend>Caja Chica</legend>

		<table class="lfr-table">
			<tr>
			
			   <td><label><liferay-ui:message key="caja-chica-nombre" />:</label></td>
				<td><input id="<portlet:namespace />descripcionCajaChica"
					name="<portlet:namespace />descripcionCajaChica" size="70"
					maxlength="70" type="text"
					value='<%=cajaChica.getDescripcion()==null?"":cajaChica.getDescripcion() %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/></td>
			   <td>
					<liferay-ui:message key="Estado" />
				</td>
				<td>
				   <input id="<portlet:namespace />estadoCajaChica"
					name="<portlet:namespace />estadoCajaChica" size="40"
					maxlength="40" type="text" 
					value='<%=cajaChica.getDescripcion()==null?"":cajaChica.getEstado().getDescripcion() %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
				</td>	
					
				<td>
				   <input id="<portlet:namespace />estadoFechaCajaChica"
					name="<portlet:namespace />estadoFechaCajaChica" size="20"
					maxlength="20" type="text"
					value='<%=cajaChica.getEstado().getFecha() ==null?"":sdf.format(cajaChica.getEstado().getFecha()) %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
				</td>
				<td>
					<liferay-ui:message key="Saldo" />
				</td>
				<td>
				
				   <input id="<portlet:namespace />saldoCajaChica"  style="background-color: #72A4D2;"
					name="<portlet:namespace />saldoCajaChica" size="20" 
					maxlength="20" type="text"
					value='<%=formatter.format(cajaChica.getSaldo()) %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
					
				</td>		
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>	
	</fieldset>
	<br>
	<fieldset class="block-labels">
		

		<table class="lfr-table">
		    <tr>
		           <td><label><liferay-ui:message key="fecha" />:</label></td>
		           <td>  
					    <liferay-ui:input-date
					         dayParam="fechaReposicionCajaChicaDia"
					         dayValue="<%=fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
					         dayNullable="<%= true %>" monthParam="fechaReposicionCajaChicaMes"
					         monthValue="<%=fechaHasta.get(Calendar.MONTH )%>"
					         monthNullable="<%= true %>" yearParam="fechaReposicionCajaChicaAnio"
					         yearValue="<%=fechaHasta.get(Calendar.YEAR )%>"
					         yearNullable="<%= true %>"
					         yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
					         yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
					         firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
					         disabled="<%= false %>"/>
				  </td>
			 </tr>
		    
		    <tr>
				<td>&nbsp;</td>
			</tr>
	        <tr>
				  <td><input id="<portlet:namespace />ingresaReposicion"
		              value="<liferay-ui:message key="guardar"/>"
		              title="<liferay-ui:message key="guardar" />"
		              onClick="javascript:<portlet:namespace />ingresarReposicionCaja();"
		              type="button"/>
		          </td>
			</tr>
	    </table>
		
	</fieldset>
	
	<input type="hidden" name="<portlet:namespace />id_caja_chica"
		id="<portlet:namespace />id_caja_chica" value="<%=cajaChica.getId()%>" />
    	<input type="hidden" value="" name="view" id="view" /> 

    
   
</form>

<script type="text/javascript">

var popupCJ;
var auxiliar;

<portlet:namespace />initDateFields();

function <portlet:namespace />initDateFields(){
  if(<%=cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()>0%> ){
    
  }
}

function <portlet:namespace />validarCampos(){
	var result = true;
	if(jQuery("#<portlet:namespace/>fechaReposicionCajaChicaDia").val()=="" || jQuery("#<portlet:namespace/>fechaReposicionCajaChicaMes").val()=="" ||
			jQuery("#<portlet:namespace/>fechaReposicionCajaChicaAnio").val()==""){
		result=false;
		alert("Debe ingresar una Fecha");
	}	
	return result;
}


function <portlet:namespace />ingresarReposicionCaja(){
	if (<portlet:namespace />validarCampos()) {
		var params = "&<%= Constants.CMD %>=" + "saveingresareposicion";
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_caja_chica';
		url = url + params;
		submitForm(document.<portlet:namespace />fmCJCHIR, url);	
	}
	return false;		
}


</script>

