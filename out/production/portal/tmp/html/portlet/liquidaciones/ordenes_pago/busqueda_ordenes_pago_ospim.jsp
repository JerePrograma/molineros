<%@ page import="ar.com.ospim.global.services.ChequesReutilizadosException" %>
<%@ page import="ar.com.ospim.global.services.ComprobantesYaPagadosException" %>
<%@ page import="ar.com.ospim.liquidaciones.AnticiposUtilizadosException" %>
<%@ page import="ar.com.ospim.liquidaciones.ComprobantesAnuladosException" %>
<%@ page import="ar.com.ospim.liquidaciones.AnticiposNoPagadosException" %>
<%@ page import="ar.com.ospim.liquidaciones.FechaBajaMenorQueAltaException" %>
<%@ page import="ar.com.ospim.liquidaciones.AnticiposReUtilizadosException" %>
<%@ page import="ar.com.ospim.tesoreria.OpCreadaEnCanjeException" %>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<liferay-ui:error exception="<%= ar.com.ospim.liquidaciones.ListasReintegrosNoEncontradasException.class %>" message="lista-reintegros-no-encontrada" />
	<liferay-ui:error exception="<%= AnticiposUtilizadosException.class %>" message="exception-anticipos_utilizados" />
	<liferay-ui:error exception="<%= ChequesReutilizadosException.class %>" message="exception-cheques-reutilizados" />
	<liferay-ui:error exception="<%= ComprobantesYaPagadosException.class %>" message="exception-comprobantes-ya-pagados" />
	<liferay-ui:error exception="<%= ComprobantesAnuladosException.class %>" message="exception-comprobantes-anulados" />
	<liferay-ui:error exception="<%= AnticiposNoPagadosException.class %>" message="exception-anticipos-no-pagados" />
	<liferay-ui:error exception="<%= FechaBajaMenorQueAltaException.class %>" message="exception-fecha-baja-menor-que-alta" />
	<liferay-ui:error exception="<%= AnticiposReUtilizadosException.class %>" message="exception-anticipos-reutilizados" />
	<liferay-ui:error exception="<%= OpCreadaEnCanjeException.class %>" message="exception-op-creada-en-canje" />
		<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="baja-menor-fecha-contable" />
<%
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaFin = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
		
		Integer ini = (Integer)request.getAttribute("ordenIniId");
		Integer fin = (Integer)request.getAttribute("ordenFinId");

 		
 		boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_OP);
 		boolean rolVER = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_VER_OP);
 		
 		Calendar fecha = CalendarFactoryUtil.getCalendar();
 		fecha.setTime(new Date());
 		
 		if (rolABM) {
 			rolVER = true;
 		}
 		
%>
</form>
<form action="" method="post" name="<portlet:namespace />fmUpload" id="<portlet:namespace />fmUpload" enctype="multipart/form-data">
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="busqueda-ordenes-pago" /></legend>
				<table class="lfr-table" width="100%">
					<tr>
						<td><label><liferay-ui:message key="numero" />:</label></td>
						<td><input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="15" maxlength="11" type="text" value="" /></td>						
						<td><label><liferay-ui:message key="cheque-nro" />:</label></td>
						<td><input id="<portlet:namespace />cheque_numero" name="<portlet:namespace />cheque_numero" size="15" maxlength="11" type="text" value="" /></td>
						<td>		
							<legend> <liferay-ui:message key="acreedor" /></legend>
							<table>
									<tr>									
										<td>
											<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
										  		<liferay-util:param name="esEditable" value='true'/>
										  		<liferay-util:param name="portlet_name" value='liquidaciones'/>
										  		<liferay-util:param name="suf" value='_busqueda'/>
										  		<liferay-util:param name="suf_entidad" value='_busqueda'/>														  		
											</liferay-util:include>
										</td>									
									</tr>
							</table>
							
						</td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="5">
							
						</td>						
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="5"><label><liferay-ui:message key="fecha-desde" />:</label>
						&nbsp;&nbsp;
						<liferay-ui:input-date 
										monthNullable="true" 
										dayNullable="true"
										yearNullable="true"
										dayParam="fechaEmisionDesdeDia"
										monthParam="fechaEmisionDesdeMes"
										yearParam="fechaEmisionDesdeAnio"
										yearRangeStart="<%= fecha.get(Calendar.YEAR) - 20 %>"
										yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 20 %>"
										firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
										disabled="<%= false %>" />
							&nbsp;&nbsp;
						<label><liferay-ui:message key="fecha-hasta" />:</label>
						&nbsp;&nbsp;
						<span id="recep"><liferay-ui:input-date 
										monthNullable="true" 
										dayNullable="true"
										yearNullable="true"
										dayParam="fechaEmisionHastaDia"
										monthParam="fechaEmisionHastaMes"
										yearParam="fechaEmisionHastaAnio"
										yearRangeStart="<%= fecha.get(Calendar.YEAR) - 20 %>"
										yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 20 %>"
										firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
										disabled="<%= false %>" /></span>
										
						&nbsp;&nbsp;		
					   <label><liferay-ui:message key="cbu" />:</label>
					  <input id="<portlet:namespace />cbu" name="<portlet:namespace />cbu" size="22" maxlength="22" type="text" value="" />						
						
						
						</td>
						
					 
						
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
						
					</tr>
					<tr>						
						<td colspan="5" align="center">							
							<% if(rolVER) { %>
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
							<%} %>&nbsp;&nbsp;
							<% if(rolABM) { %>
								<input type="button" value="<liferay-ui:message key="alta-orden-pago" />" onClick="<portlet:namespace />altaOP();" />
							<%} %>&nbsp;&nbsp;
							<% if(rolABM) { %>
								<input type="button" value="<liferay-ui:message key="cerrar-lote" />" onClick="<portlet:namespace />cerrarLoteAbrirDiv();" />
							<%} %>&nbsp;&nbsp;
							<b><div id="<portlet:namespace />nro_lote" name="<portlet:namespace />nro_lote"> <liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/div_lote.jsp"/></div></b>
						</td>						
						
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>					
				</table>	      	  
		</fieldset>
		<table>
			<tr>
			<td colspan="6">
				<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/crear_orden_pago_from_lista.jsp">
				</liferay-util:include>
			</td>
			</tr>
		</table>
	
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
			<div align="center" id="<portlet:namespace />busquedaChequeDiv">
			</div>
		</fieldset>
</form>			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var numero=jQuery('#<portlet:namespace />numero').val();
		var chequeNumero=jQuery('#<portlet:namespace />cheque_numero').val();
		
		var fechaEmisionDesdeDia=jQuery('#<portlet:namespace />fechaEmisionDesdeDia').val();
		var fechaEmisionDesdeMes=jQuery('#<portlet:namespace />fechaEmisionDesdeMes').val();
		var fechaEmisionDesdeAnio=jQuery('#<portlet:namespace />fechaEmisionDesdeAnio').val();
		
		var fechaEmisionHastaDia=jQuery('#<portlet:namespace />fechaEmisionHastaDia').val();
		var fechaEmisionHastaMes=jQuery('#<portlet:namespace />fechaEmisionHastaMes').val();
		var fechaEmisionHastaAnio=jQuery('#<portlet:namespace />fechaEmisionHastaAnio').val();
		
		var cuit=jQuery("#<portlet:namespace />cuit_entidad_busqueda").val();
		var sucu=jQuery("#<portlet:namespace />sucursal_entidad_busqueda").val();
		var idSeccional=jQuery("#<portlet:namespace />id_seccional_busqueda").val();
		var cbu=jQuery("#<portlet:namespace />cbu").val();

		
		if(!<portlet:namespace />validarBusqueda(numero,chequeNumero,cuit, sucu, fechaEmisionDesdeMes, fechaEmisionHastaMes,cbu)){
			return false;
		}
		jQuery('#<portlet:namespace />buscando').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_ordenes_pago_ospim_entry&numero='+numero+'&cheque_numero='+chequeNumero;
		url=url+'&desdeDia='+fechaEmisionDesdeDia+'&desdeMes='+fechaEmisionDesdeMes+'&desdeAnio='+fechaEmisionDesdeAnio;
		url=url+'&hastaDia='+fechaEmisionHastaDia+'&hastaMes='+fechaEmisionHastaMes+'&hastaAnio='+fechaEmisionHastaAnio;
		url=url+'&cuit='+cuit+'&sucu='+sucu+'&idSeccional='+idSeccional+'&cbu='+cbu;		
		
		jQuery('#<portlet:namespace />busquedaChequeDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});
	var popup;
	function <portlet:namespace />cerrarLoteAbrirDiv(){
		popup= Liferay.Popup({title:"<liferay-ui:message key="cerrar-lote" />",modal:true,width:700});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/cerrar_lote';   		       	
		jQuery(popup).load(url);
	}
	
	function cierraLote(cuil,inte){		
		var diaBaja=jQuery('#<portlet:namespace />fechaBajaDia').val();
		var mesBaja=parseInt(jQuery('#<portlet:namespace />fechaBajaMes').val());
		var anioBaja=jQuery('#<portlet:namespace />fechaBajaAnio').val();
		
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/firma_lote"/></portlet:actionURL>';
			
		jQuery('#<portlet:namespace />nro_lote').load(url,{baja_dia:diaBaja,baja_mes:mesBaja,baja_anio:anioBaja}, function() {Liferay.Popup.close(popup);});
	}
	
	
	function <portlet:namespace />validarBusqueda(numero,chequeNumero,cuit, sucu, fechaEmisionDesdeMes, fechaEmisionHastaMes,cbu){
		if(trim(numero.length)==0 && trim(chequeNumero.length)==0 && trim(cuit.length)==0 && trim(sucu.length)==0 && trim(fechaEmisionDesdeMes.length)==0
				&& trim(fechaEmisionHastaMes.length)==0 && trim(cbu.length)==0){	
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}

	function <portlet:namespace />altaOP() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_orden_pago_ospim_entry" /></portlet:renderURL>';
//		document.<portlet:namespace />fm.method = 'post';
//		submitForm(document.<portlet:namespace />fm, url);
        document.<portlet:namespace />fmUpload.method = 'post';
		submitForm(document.<portlet:namespace />fmUpload, url);
	}     
		

	function <portlet:namespace />altaOPFromLista(){
		var tipo=jQuery('#<portlet:namespace/>tipo_reintegro').val();
		var id_seccional=jQuery('#<portlet:namespace/>id_seccional').val();
		
		if(id_seccional==null || id_seccional==""){
			alert('Debe seleccionar una seccional');
			return false;
		}
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia1').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes1').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio1').val();
		
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia2').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes2').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio2').val();
		
		<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/liquidaciones/editar_orden_pago_ospim_from_reintegros&tipo_lista='+tipo; --%>
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_orden_pago_ospim_from_reintegros" /></portlet:renderURL>';

		var params = {"fechaDesdeDia1":fechaDesdeDia,"fechaDesdeMes1":fechaDesdeMes,"fechaDesdeAnio1":fechaDesdeAnio,
				     "fechaHastaDia2":fechaHastaDia,"fechaHastaMes2":fechaHastaMes,"fechaHastaAnio2":fechaHastaAnio,
				     "tipo_lista":tipo,"id_seccional":id_seccional, "cmd":'<%=Constants.SEARCH %>'}
			
		jQuery('#<portlet:namespace />busquedaListasReintegrosDiv').load(url, params ,function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  } );	
	}
	
	function <portlet:namespace />selecOPFromLista() {
		var tipo=jQuery('#<portlet:namespace/>tipo_reintegro').val();
		var id_seccional=jQuery('#<portlet:namespace/>id_seccional').val();
		
		if(id_seccional==null || id_seccional==""){
			alert('Debe seleccionar una seccional');
			return false;
		}		
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/liquidaciones/editar_orden_pago_ospim_from_reintegros&cmd=add&tipo_lista='+tipo;
				
		//document.<portlet:namespace />fm.method = 'post';
		//submitForm(document.<portlet:namespace />fm, url);

		document.<portlet:namespace />fmUpload.method = 'post';
		submitForm(document.<portlet:namespace />fmUpload, url);
	}  
	
	
	
	function <portlet:namespace />selecOPFromListaPagoAfiliado() {
		var tipo=jQuery('#<portlet:namespace/>tipo_reintegro').val();
		var id_seccional=jQuery('#<portlet:namespace/>id_seccional').val();
		
		var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysLiquidaciones.ADD_FORMA_PAGO %>";
		
		if(id_seccional==null || id_seccional==""){
			alert('Debe seleccionar una seccional');
			return false;
		}		
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/liquidaciones/editar_orden_pago_ospim_from_reintegros&cmd=add&tipo_lista='+tipo;
		url = url + params;				
		//document.<portlet:namespace />fm.method = 'post';
		//submitForm(document.<portlet:namespace />fm, url);
		
		document.<portlet:namespace />fmUpload.method = 'post';
		submitForm(document.<portlet:namespace />fmUpload, url);
		
	}  
	
	
	function <portlet:namespace />uploadOrdenPago() {
		url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/upload_ordenes_pago' /></portlet:actionURL>";
		
        document.<portlet:namespace />fmUpload.method = 'post';
		submitForm(document.<portlet:namespace />fmUpload, url);
		return true;
	}

	function <portlet:namespace />uploadFileLiq() {
		url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/editar_orden_pago_ospim_entry' /></portlet:actionURL>";
		var params = "&cmd=uploadLiq" ;
		url = url + params;	
		submitForm(document.<portlet:namespace />fmUpload, url);
		return true;
	}
</script>
