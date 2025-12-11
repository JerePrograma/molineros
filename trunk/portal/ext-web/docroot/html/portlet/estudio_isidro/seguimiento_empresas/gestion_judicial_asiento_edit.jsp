<%@ include file="/html/portlet/estudio_isidro/init.jsp"%>
<%@page import="ar.com.ospim.estudioisidro.service.DemandaJudicialServiceUtil"%>
<%@page import="ar.com.ospim.tesoreria.beans.contabilidad.Asiento"%>
<%@page import="ar.com.ospim.global.beans.PlanCuentas"%>
<%@page import="ar.com.ospim.global.services.TraeListasServiceUtil"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	DemandaJudicial demanda=(DemandaJudicial)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
	Asiento asiento=(Asiento)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_EN_EDICION);
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	NumberFormat format2D = new DecimalFormat("#0.00");
	boolean esEdicion = true;
	if(viewStr==null){
		viewStr=ParamUtil.getString(request, "view");
	}
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "estudio_isidro";
	}
	
	int id_demanda=asiento!=null ?(int)asiento.getId():0;
	if(asiento==null){
		asiento= new Asiento();
	} 
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	if(asiento.getFecha()==null){
		fecha.setTime(new Date());
	}else{
	  fecha.setTime(asiento.getFecha());
	} 
	
	String organizacionId = user.getOrganizations().size()>0?String.valueOf(user.getOrganizations().get(0).getOrganizationId()):"";
	String tabValue = ParamUtil.getString(request, "tab", null); // "datos"
	
	Integer entidad =0;
	if("A".equals(demanda.getEntidad())){
		entidad=WebKeysGlobal.AMTIMA;
	}else if("O".equals(demanda.getEntidad())){
		entidad=WebKeysGlobal.OSPIM;
	}else if("U".equals(demanda.getEntidad())){
		entidad=WebKeysGlobal.UOMA;
	}
	List<PlanCuentas> pcuentas=TraeListasServiceUtil.getPlanCuentasImputables(fecha.getTime(), entidad);
		
%>


<form action="" method="post" name="<portlet:namespace />fmS">

	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
		value="" />
    <input type="hidden" name="<portlet:namespace />tab_seleccionada"  value="<%=tabValue%>" />
  
	<liferay-ui:success key="insertCabOkAsi"
		message="<%=(String)request.getAttribute(\"msgCabOkAsi\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOkAsi\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	<liferay-ui:error key="errorAfiliadoNull"
		message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
	
		
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
		

	<fieldset class="block-labels"> 
		<legend>Demanda</legend>
		
		 
		
		<table class="lfr-table">
		   <tr>
		   
		        <td><liferay-ui:message key="id" />:</td> 
			    <td><input id="<portlet:namespace />nroAsiento"
					name="<portlet:namespace />nroAsiento" size="15"
					maxlength="20" type="text" readonly="readonly" tabindex="-1"
					value="<%=asiento.getId() ==0?0:asiento.getId() %>" />
				</td>
		    
			   <td>
			       <label><liferay-ui:message key="fecha" />:</label>
			   </td>
			   <td>  
			         <liferay-ui:input-date
						 dayParam="fechaAsientoDia"
						 dayValue="<%=asiento.getFecha()!=null?fecha.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						 dayNullable="<%= false %>" monthParam="fechaAsientoMes"
						 monthValue="<%=asiento.getFecha()!=null?fecha.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						 monthNullable="<%= false %>" yearParam="fechaAsientoAnio"
						 yearValue="<%=asiento.getFecha()!=null?fecha.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						 yearNullable="<%= false %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/>
			    </td>
			    
			    
			    <td>Descripción:</td> 
			    <td><input id="<portlet:namespace />descripcionAsiento"
					name="<portlet:namespace />descripcionAsiento" size="90"
					maxlength="2000" type="text"  tabindex="-1"
					value='<%=asiento!=null && asiento.getDescripcion()!=null?asiento.getDescripcion():""%>' />
				</td>
			
				
			
			</tr>
			
			<tr>
				<td>&nbsp;</td>
			</tr>
						                
		</table>
		
		<fieldset class="block-labels"> 
		  <legend>Detalle</legend>
		  
		  <table class="lfr-table">
		   <tr>
		    <td>
			   <label>Cuenta:</label>
			</td>
		    <td>
		        <select name="<portlet:namespace />cuenta_1" id="<portlet:namespace />cuenta_1" style="width: 580px">
					<% for(PlanCuentas pc : pcuentas){
						if (pc.isImputable()){%>
						     <option value="<%=pc.getId()%>"><%=pc.getNumero() + " - " + pc.getCuenta()%></option>
					<%}} %>
				</select>
		    </td>
		    
			<td>
			<label>Debe:</label>
			</td>
			
			<td>
				<input type="text" id="<portlet:namespace />debe" name="<portlet:namespace />debe"  size="20" value="0"  maxlength="60" 
				onkeydown="allowOnlyDigitsAndDecimals(event)"/>
			</td>
			
			<td>
			<label>Haber:</label>
			</td>
			
			<td>
				<input type="text" id="<portlet:namespace />haber" name="<portlet:namespace />haber"  size="20" value="0"  maxlength="60"
				onkeydown="allowOnlyDigitsAndDecimals(event)" />
			</td>
			
					
			<td>
			  <input id="<portlet:namespace />agregarDetalle" value="Agregar Detalle" onClick="javascript:agregarDetalle();"
		          type="button"	 />
			</td>	  
		  </tr>
		  <tr> 
		   <td colspan="20">
		    <div align="center" id="<portlet:namespace />detallesDiv">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_asiento_detalle_result.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEdicion)%>" />
				</liferay-util:include>
			</div>	
		   </td>
		  
		  </tr>	
		  
		</table>
		
		
		
		
		
		
		
		</fieldset>
				 
	</fieldset>
	<br>
	

    <div align="center" id="<portlet:namespace />buscando">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
	</div>	
		    
	<br>
	<input type="hidden" name="<portlet:namespace />id_demanda"
		id="<portlet:namespace />id_demanda" value="<%=id_demanda%>" />
		
	<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" /> 
	

  <table>
	 <tr>
	 <td>
	 <%if (esEdicion){ %> 
      <input id="<portlet:namespace />guardar"
		value="Confirmar Carga"
		title="Confirma CARGA"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" 
		 />
	 <%}%>
	</td>
  	</tr>
  </table> 
   <input type="hidden" value="" name="view" id="view" />
   
</form>

<script type="text/javascript">

jQuery('#<portlet:namespace />buscando').hide();

function agregarDetalle(){	
	var cuenta=jQuery('#<portlet:namespace />cuenta_1').val();
	var cuentaDescrip=jQuery('#<portlet:namespace />cuenta_1').find('option:selected').text();
	var debe=jQuery('#<portlet:namespace />debe').val();
	var haber=jQuery('#<portlet:namespace />haber').val();

    if(isNaN(debe) || jQuery.trim(debe) == "" ) debe=0;
    if(isNaN(haber) || jQuery.trim(haber) == "" ) haber=0;
   	
	if ((jQuery.trim(haber) == "" && jQuery.trim(debe) == "") || (parseFloat(haber) == 0 &&  parseFloat(debe) == 0 )){
		alert("Solo debe completarse el valor del DEBE o el HABER");
		return false;
	}
	
	//jQuery('#<portlet:namespace />buscando').show();

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
	url += '&cmd=agregarAsientoDetalle&cuenta='+cuenta+"&cuentaDesc="+encodeURI(cuentaDescrip)+"&debe_1="+debe+"&haber_1="+haber;
	url += '&rnd=' + Math.floor(Math.random()*100);

	jQuery('#<portlet:namespace />detallesDiv').load(url, function() {
		    jQuery('#<portlet:namespace />cuenta').val("");
		    jQuery('#<portlet:namespace />debe').val("0");
		    jQuery('#<portlet:namespace />haber').val("0");
			//jQuery('#<portlet:namespace />buscando').hide();
		}
	);
}


function <portlet:namespace />salvarEdicion(){
	var p ='<%=portlet_name%>';
	if (<portlet:namespace />validarCampos()) {
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/__portlet/demandas_editar" />'+
		'<liferay-portlet:param name="cmd" value="updateAsiento"/>'+
		'</liferay-portlet:renderURL>';
        url = url.replace("__portlet",p);
        
        jQuery('#<portlet:namespace />buscando').show();
        
	
		var form = jQuery(document.<portlet:namespace />fmS);
		var that = this;
		
		form.ajaxForm(
				{
					url: url,
			    	target: popupE,
			        type: "POST",
			        beforeSubmit: function() {			        
			        },
			        success: function() {
			        	jQuery('#<portlet:namespace />buscando').hide();
			        }
			    }
			);
		form.submit();
	}
	return false;		
}


function <portlet:namespace />validarCampos(){
	var des =jQuery("#<portlet:namespace />descripcionAsiento").val();
	var totaldebe=jQuery("#<portlet:namespace />total_debe").val();
	var totalhaber=jQuery("#<portlet:namespace />total_haber").val();
	if(des==""){
		alert("Debe Ingresar descripción del Registro Contable");
		return false
	}
	if(parseFloat(totaldebe)==0 && parseFloat(totalhaber)==0){
		alert("Debe Ingresar algún Detalle");
		return false
	}
	if(parseFloat(totaldebe.replace(",",".")).toFixed(2)!=parseFloat(totalhaber.replace(",",".")).toFixed(2)){
		var sigue=confirm("Registro Contable no Balancea");
		return sigue;
	}
	return true;	
	
}

</script>

