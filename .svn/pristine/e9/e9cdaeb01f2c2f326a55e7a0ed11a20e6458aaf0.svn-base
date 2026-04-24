<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/uoma/init.jsp"%>

<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="ar.com.ospim.global.beans.Seccional" %>
<%@ page import="java.util.Calendar" %>
<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	CentroCosto centroCosto=(CentroCosto)request.getSession().getAttribute(WebKeysUOMA.CENTRO_COSTO_EN_EDICION);
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	
	int id_centro_costo=centroCosto!=null && centroCosto.getId() !=null ?(int)centroCosto.getId():0;
	if(centroCosto==null){
		centroCosto= new CentroCosto();
	} 
	
	Integer entidad = WebKeysGlobal.OSPIM;
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		entidad = WebKeysGlobal.UOMA;
		portlet_name = "uoma";
	}
	
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		entidad = WebKeysGlobal.UOMA;
		portlet_name = "farmacia";
	}
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
			
    Calendar fechaVigenciaDde = CalendarFactoryUtil.getCalendar();
    if(centroCosto==null || centroCosto.getVigenciaDde() ==null){
	    fechaVigenciaDde.setTime(new Date());
    }else{
    	fechaVigenciaDde.setTime(centroCosto.getVigenciaDde());
    }  
	
    Calendar fechaVigenciaHta = CalendarFactoryUtil.getCalendar();
    if(centroCosto==null || centroCosto.getVigenciaHta() ==null){
	    fechaVigenciaHta.setTime(new Date());
    }else{
    	fechaVigenciaHta.setTime(centroCosto.getVigenciaHta());
    } 
	
%>

<form action="" method="post" name="<portlet:namespace />fmCCTO">

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	
	<fieldset class="block-labels">
		<legend>Centro de Costo</legend>

		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
			<tr>
			   <td><label><liferay-ui:message key="caja-chica-nombre" />:</label></td>
				<td><input id="<portlet:namespace />descripcionCentroCosto"
					name="<portlet:namespace />descripcionCentroCosto" size="145"
					maxlength="145" type="text"
					value='<%=centroCosto.getDescripcion()==null?"":centroCosto.getDescripcion() %>' />
				</td>
			</tr>	
		</table>	
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">	
			<tr>
	            <td><label>Desde:</label></td>
				<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaDesdeDia"
							dayValue="<%=centroCosto !=null && centroCosto.getVigenciaDde() !=null?fechaVigenciaDde.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
							dayNullable="<%= false %>"
							monthParam="fechaDesdeMes"
							monthValue="<%=centroCosto !=null && centroCosto.getVigenciaDde()!=null?fechaVigenciaDde.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
							monthNullable="<%= false %>"			
							yearParam="fechaDesdeAnio"
							yearValue="<%=centroCosto !=null && centroCosto.getVigenciaDde()!=null?fechaVigenciaDde.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
							yearNullable="<%= false %>"
							yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
			  </td>
			
			  <td><label>Hasta:</label></td>
				<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaHastaDia"
							dayValue="<%=centroCosto !=null && centroCosto.getVigenciaHta() !=null?fechaVigenciaHta.get(Calendar.DAY_OF_MONTH ):-1%>"
							dayNullable="<%= true %>"
							monthParam="fechaHastaMes"
							monthValue="<%=centroCosto !=null && centroCosto.getVigenciaHta()!=null?fechaVigenciaHta.get(Calendar.MONTH ):-1%>"
							monthNullable="<%= true %>"			
							yearParam="fechaHastaAnio"
							yearValue="<%=centroCosto !=null && centroCosto.getVigenciaHta()!=null?fechaVigenciaHta.get(Calendar.YEAR ):-1 %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
							yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR)+50%>"
							firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
			  </td>
			
			  <td><label><liferay-ui:message key="presupuesto" />:</label></td>
				<td><input id="<portlet:namespace />presupuestoCentroCosto"
					name="<portlet:namespace />presupuestoCentroCosto" size="20"
					maxlength="20" type="text"
					value='<%=centroCosto.getPresupuesto()==null?"": centroCosto.getPresupuesto()%>' /></td>
			   <td>
			   
			   <td>
				             &nbsp;&nbsp; <label>Uso Contable:</label>
			   </td>
			   <td><input type="checkbox" id="<portlet:namespace />contable" name="<portlet:namespace />contable" <%=centroCosto!=null && centroCosto.getEsContable()!=null && centroCosto.getEsContable() ?"checked=\"checked\"":"" %>/></td>
			</tr>
			
			
		</table>
			
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">	
			<tr>
			   <td colspan="1" valign="top"><label><liferay-ui:message key="observaciones"/>:</label></td>
	           <td colspan="7"><textarea rows="5" cols="140" maxlength="20000" 
		               id="<portlet:namespace />observacionesCentroCosto" 
					   name="<portlet:namespace />observacionesCentroCosto"
					   style="resize: none;"><%=centroCosto.getObservaciones()==null?"": centroCosto.getObservaciones()%></textarea>
		       </td>	
			</tr>
		</table>
		
		
		
		
	</fieldset>
	<br>
	<input type="hidden" name="<portlet:namespace />id_centro_costo"
		id="<portlet:namespace />id_centro_costo" value="<%=id_centro_costo%>" />
	<input type="hidden" value="" name="view" id="view" /> 

    <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" 
	 />
	 
	 <input id="<portlet:namespace />volver"
		value="Atras"
		title="<liferay-ui:message key="atras" />"
		onClick="javascript: <portlet:namespace />atras();"
		type="button" 
		 />
   
</form>

<script type="text/javascript">

var popupNM;
<portlet:namespace />initDateFields();

function <portlet:namespace />initDateFields(){}

function <portlet:namespace />salvarEdicion(){
	
	if (<portlet:namespace />validarCampos()) {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.UPDATE %>";
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/centro_costo_edicion';
		url = url + params;
		submitForm(document.<portlet:namespace />fmCCTO, url);	
	}
	return false;		
}

function <portlet:namespace />validarCampos(){

	var result = true;
	var portlet = '<%=portlet_name%>';
	if (jQuery("#<portlet:namespace/>descripcionCentroCosto").val()==""){
		result=false;
		alert("Debe ingresar la Descripción");
	}else{
		if (jQuery('#<portlet:namespace />conceptoCajaChica').val()==0 ){
			result=false;
			alert("Debe Seleccionar un Concepto");
		} else if (jQuery("#<portlet:namespace/>presupuestoCentroCosto").val()=="" || jQuery("#<portlet:namespace/>presupuestoCentroCosto").val()==0){
			/*
			result=false;
			alert("Debe ingresar el Presupuesto");
			*/
		}	
	}
	return result;
}

function <portlet:namespace />atras(){
	
		var params = "&<%= Constants.CMD %>=" + "atras";
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/centro_costo_edicion';
		url = url + params;
		submitForm(document.<portlet:namespace />fmCCTO, url);	
		
}
</script>

