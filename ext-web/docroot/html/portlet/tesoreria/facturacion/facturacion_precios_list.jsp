<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<%@ page import="ar.com.ospim.global.beans.Parentesco" %>
<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.tesoreria.beans.PrecioPlanSuperador" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/tesoreria/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	
	//verificar los calendars
	Calendar current = CalendarFactoryUtil.getCalendar();

	List<Plan> planes=TraeListasServiceUtil.getPlanesFacturables();
	List<Parentesco> parentescos = TraeListasServiceUtil.getParentescosFacturables();
	List<Provincia> provinciasPrecio=TraeListasServiceUtil.getProvinciasFacturables();
	boolean popup=ParamUtil.getBoolean(request, "popup", false);
	
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
		<legend>Lista de Valores Planes Superadores</legend>
		
		<table class="lfr-table" style="border-collapse: separate; border-spacing:5px;">
			<tr>
		      <td>
			   <fieldset class="block-labels">
			      <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
			        <tr>
			            <td><label>Vigente al :</label></td>
						<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaDesdeDiaFiltro"
							dayValue="-1" 
							dayNullable="<%= true %>"
							monthParam="fechaDesdeMesFiltro"
							monthValue="-1"	
							monthNullable="<%= true %>"			
							yearParam="fechaDesdeAnioFiltro"
							yearValue="-1"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
						
						
			           <td>Plan:</td><td> <select name="<portlet:namespace />plan_filtro"
			                   id="<portlet:namespace />plan_filtro" 
			                   onchange="">
			                <option value="">Seleccione plan</option>
			                <%for(Plan p :planes ) {%>
						     <option value="<%=p.getId() %>"><%=p.getDescripcion() %></option>
						    <%}%>
		                  </select>
		               </td>
		               
		               
			           <td>Parentesco:</td><td> <select name="<portlet:namespace />parentesco_filtro"
			                   id="<portlet:namespace />parentesco_filtro" 
			                   onchange="">
			                <option value="">Seleccione parentesco</option>
			                <%for(Parentesco p :parentescos ) {%>
						     <option value="<%=p.getCodigo() %>"><%=p.getDescripcion() %></option>
						    <%}%>
		                  </select>
		               </td>
		               
		               <td>Provincia:</td><td> <select name="<portlet:namespace />provincia_filtro"
			                   id="<portlet:namespace />provincia_filtro" 
			                   onchange="">
			                <option value="">Seleccione provincia</option>
			                <%for(Provincia p :provinciasPrecio ) {%>
						     <option value="<%=p.getId() %>"><%=p.getDescripcion() %></option>
						    <%}%>
		                  </select>
		               </td>
				  </tr>
				    
				   
				  <tr>
				     <td><label><liferay-ui:message key="id-preaut"/>:</label></td>
				     <td colspan="2"><input id="<portlet:namespace />idPrecio_filtro" 
				     	name="<portlet:namespace />idPrecio_filtro" size="20" maxlength="20" 
				     	type="text" value='' onkeydown="allowOnlyDigits(event);" /></td>
				     	
				      <td><label>Descripción:</label></td>
				     <td colspan="2"><input id="<portlet:namespace />descripcion_filtro" 
				     	name="<portlet:namespace />descripcion_filtro" size="50" maxlength="200" 
				     	type="text" value=''/></td>	
				       
		          </tr>

			    </table>
			   </fieldset>
			  </td>
		  </tr>
		</table>
		
		
		<table>
			 <tr align="left">
			    <td>&nbsp;</td>
				<td align="left" width="100%">						
					<input id="<portlet:namespace />buscar"
					value="<liferay-ui:message key="buscar"/>"
					title="<liferay-ui:message key="buscar" />"
					onClick="javascript: <portlet:namespace />buscarPrecio();"
					type="button" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					  <input type="button" value="Nuevo" onClick="<portlet:namespace />nuevoPrecio();"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					 <input type="button" value="Reporte" onClick="<portlet:namespace />reportePrecios();"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					
				</td>
				
				<td>&nbsp;</td>
				
				
				<td colspan="6">&nbsp;</td>
					<td colspan="6">&nbsp;</td>
					<td colspan="6">&nbsp;</td>
					
					<td>
						<input id="<portlet:namespace />marcar" value="Marcar Todos" title="Marcar todos" type="button"
						onclick="javascript:<portlet:namespace />marcarCptes(true);"/>							
					</td>
					<td>
					&nbsp;
					</td>	
					<td>
						<input id="<portlet:namespace />desmarcar" value="Desmarcar Todos" title="Desmarcar" type="button"
						onclick="javascript:<portlet:namespace />marcarCptes(false);"/>							
					</td>
					<td>
					&nbsp;
					</td>
				
	  		</tr>				 
		</table>
	</fieldset>
	
	<fieldset class="block-labels">
		<legend>Actualizaciones Masivas</legend>	
		<table>		
				<tr>
				    <td><label>Nueva Vigencia Desde :</label></td>
					<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaDesdeDiaVigencia"
							dayValue="-1" 
							dayNullable="<%= true %>"
							monthParam="fechaDesdeMesVigencia"
							monthValue="-1"	
							monthNullable="<%= true %>"			
							yearParam="fechaDesdeAnioVigencia"
							yearValue="-1"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
					</td>
				
				   <td><label>Nueva Vigencia Hasta :</label></td>
				   <td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaHastaDiaVigencia"
							dayValue="-1" 
							dayNullable="<%= true %>"
							monthParam="fechaHastaMesVigencia"
							monthValue="-1"	
							monthNullable="<%= true %>"			
							yearParam="fechaHastaAnioVigencia"
							yearValue="-1"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
					</td>
				
				
				    <td><label>Porcentaje de Incremento:</label></td>
				    <td colspan="2"><input id="<portlet:namespace />porcentaje" 
				     	name="<portlet:namespace />porcentaje" size="20" maxlength="20" 
				     	type="text" value='' onkeydown="allowOnlyDigits(event);" /></td>
				
				
				    <td>
						<input id="<portlet:namespace />aplicar" value="Aplicar" title="Aplicar Cambios" type="button"
						onclick="javascript:<portlet:namespace />generarPrecios();"/>							
					</td>
					
					<td>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</td>	
					<td>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</td>
					<td>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</td>		
				
					
				</tr>
		  		
		   </table>
		</fieldset>
	
	
	<fieldset class="block-labels">
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
		
		<div id="<portlet:namespace />preciosDiv"
	     	style="
	        max-width: 1100px;
	        max-height: 400px;
	        overflow-x: auto;
	        overflow-y: auto;
	        border: 1px solid #ccc;
	        border-radius: 6px;
	        background: #fff;">
            <jsp:include page='/html/portlet/tesoreria/facturacion/facturacion_precios_result.jsp' />  
        </div>

		
	</fieldset>
	
</form>		

<script type="text/javascript">
	
	jQuery('#<portlet:namespace />buscando').hide();	
	var popupMD;
	
	<portlet:namespace />initDateFields();
	
	function <portlet:namespace />initDateFields(){
	}
	
	function <portlet:namespace />nuevoPrecio() {
		var params = "&<%= Constants.CMD %>=NEW" ;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/tesoreria/facturacion_precios" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	
	function <portlet:namespace />buscarPrecio(){
		
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaFiltro");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMesFiltro");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioFiltro");
		
		
		var plan=jQuery('#<portlet:namespace />plan_filtro').val();
		var parentesco=jQuery('#<portlet:namespace />parentesco_filtro').val();
		var provincia=jQuery('#<portlet:namespace />provincia_filtro').val();
		var id=jQuery('#<portlet:namespace />idPrecio_filtro').val();
		var descripcion=jQuery('#<portlet:namespace />descripcion_filtro').val();
		
		jQuery('#<portlet:namespace />buscando').show();
	 	var busquedaNom = {"id_precio":id,"cmd":"filter","fechadesdedia":fechaDesdeDia.value,"fechadesdemes":fechaDesdeMes.value,
		                   "fechadesdeanio":fechaDesdeAnio.value,"plan":plan,"parentesco":parentesco,"provincia":provincia,"descripcion":descripcion};

	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/tesoreria/facturacion_precios" /></portlet:renderURL>';
		jQuery('#<portlet:namespace />preciosDiv').load(url,busquedaNom, function(){
															jQuery('#<portlet:namespace />buscando').hide();      
		});	
		
	}
	
	function <portlet:namespace />reportePrecios(){
		
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaFiltro");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMesFiltro");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioFiltro");
		
		
		var plan=jQuery('#<portlet:namespace />plan_filtro').val();
		var parentesco=jQuery('#<portlet:namespace />parentesco_filtro').val();
		var provincia=jQuery('#<portlet:namespace />provincia_filtro').val();
		var id=jQuery('#<portlet:namespace />idPrecio_filtro').val();
		
		var url = '/xlsservlet/?reporte=REPORTE_PRECIOS_FACTURACION';
		
		url += '&plan='+ plan;
		url += '&parentesco='+parentesco;
		url += '&fechadesdedia='+fechaDesdeDia.value;
		url += '&fechadesdemes='+fechaDesdeMes.value;
		url += '&fechadesdeanio='+fechaDesdeAnio.value;
		url += '&provincia='+provincia;
		url += '&id='+id;
		window.location.href =url;
	}
	
	
	function <portlet:namespace />marcarCptes(valor){	
		  var checkboxes = document.getElementsByName('precio');
		  for(i=0;i<checkboxes.length;i++){
				if(checkboxes[i].type == "checkbox"){
					checkboxes[i].checked=valor;
				}
		  }
	}
	
	
	function <portlet:namespace />generarPrecios(){
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaVigencia");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMesVigencia");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioVigencia");
		
		var fechaHastaDia  = document.getElementById("<portlet:namespace />fechaHastaDiaVigencia");
		var fechaHastaMes= document.getElementById("<portlet:namespace />fechaHastaMesVigencia");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioVigencia");
		
		if(fechaDesdeDia=="" || fechaDesdeMes=="" || fechaDesdeAnio==""){
			alert("Debe seleccionar una nueva fecha de Vigencia Desde ");
			return false;	
		}
		
		var porcentaje=jQuery('#<portlet:namespace />porcentaje').val();
		
		if(porcentaje==""){
			alert("Debe ingresar un porcentaje a aplicar ");
			return false;	
		}
		
		var trat = document.getElementsByName('precio');
		var tratValue = "";
		var i = 0;
		for (i = 0; i<trat.length; i++){
			if (trat[i].checked) {					
				tratValue= tratValue+trat[i].value+";"; 
			}
		}
		
		if(tratValue == ""){
			alert("Debe seleccionar items para realizar la operación");
			return false;	
		}
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_precios';
		url += '&cmd=generarPrecios&ids='+tratValue;
		url += '&porcentaje='+porcentaje;
		url += '&fechadesdedia='+fechaDesdeDia.value;
		url += '&fechadesdemes='+fechaDesdeMes.value;
		url += '&fechadesdeanio='+fechaDesdeAnio.value;
		
		url += '&fechahastadia='+fechaHastaDia.value;
		url += '&fechahastames='+fechaHastaMes.value;
		url += '&fechahastaanio='+fechaHastaAnio.value;
		
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />buscando').show();
		jQuery('#<portlet:namespace />preciosDiv').load(url, function() {jQuery('#<portlet:namespace />buscando').hide();});
		
		
	}
	
</script>

<style>
 table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 5px;
 }  
</style>

