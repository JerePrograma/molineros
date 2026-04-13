<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="ar.com.ospim.novedades.exception.PeriodoArchivoDuplicadoException"%>
<portlet:defineObjects/>
<liferay-ui:error exception="<%= java.text.ParseException.class %>" message="archivo-parse-exception" />
<liferay-ui:error exception="<%= org.postgresql.util.PSQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.sql.SQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.io.IOException.class %>" message="afip-io-exception" />
<liferay-ui:error exception="<%= ar.com.ospim.tesoreria.afip.ErrorProcesandoArchivosAfipException.class%>" message="error-procesando-archivos-afip" />
<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.RendicionBancoNacionRegistroDuplicado.class%>" message="rendicion-duplicada" />
<liferay-ui:error exception="<%= PeriodoArchivoDuplicadoException.class%>" message="error-periodo-archivos-sss" />
<c:choose>
	<c:when
		test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<liferay-ui:success key="request_processed" message="grabar-exitoso" />		
	</c:when>
</c:choose>

<form action="" method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;" enctype="multipart/form-data">
<liferay-portlet:renderURLParams varImpl="portletURL" />
<%		

		String portlet_name = ParamUtil.getString(request, "portlet_name");
		int entidad=WebKeysGlobal.OSPIM;
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "tesoreria";
		}
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
			entidad=WebKeysGlobal.AMTIMA;
		}
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
			entidad=WebKeysGlobal.UOMA;
		}
		 

		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_MOVIMIENTOS_BANCARIOS) || PermissionUtil.userContainsRole(user,"ABM_Farmacia") || portlet_name.equals("uoma");

		List<CuentaBancaria> ctas=null;
		ctas = (ArrayList<CuentaBancaria>) portletSession.getAttribute(WebKeysTesoreria.CUENTAS_BCRIAS,PortletSession.APPLICATION_SCOPE);
		if(null==ctas){
			ctas=TraeListasServiceUtil.getCtasBcrias();
			portletSession.setAttribute(WebKeysTesoreria.CUENTAS_BCRIAS, ctas, PortletSession.APPLICATION_SCOPE);
		}
		
		List<TipoMovBcrio> movs=null;
		movs = (ArrayList<TipoMovBcrio>) request.getAttribute(WebKeysTesoreria.TIPOS_MOV_BCRIO_EN_REQUEST);
		if(null==movs){
			movs=TraeListasServiceUtil.getInstance().getTipoMovBcrio(new Date(), entidad);
			request.setAttribute(WebKeysTesoreria.TIPOS_MOV_BCRIO_EN_REQUEST, movs);
		}
		//verificar los calendars
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 		
 		Calendar periodoDesde = CalendarFactoryUtil.getCalendar(); 		
 		periodoDesde.setTime(new Date());
 		Calendar periodoHasta = CalendarFactoryUtil.getCalendar(); 		
 		periodoHasta.setTime(new Date());
 		List<String> errores = (List<String>)request.getAttribute("errores");
 		
 		if (errores != null && !errores.isEmpty()){
 			%>
 			<table>
 			<%
 			for (String error : errores){
 				%>
 				<tr><td>
 				<%=error%>
 				</td></tr>
 				<%
 			}
 			%>
 			</table>
 			<%
 		}
 		boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
 		
%>	
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-mov-bcrio" /></legend>				
				<table class="lfr-table">			
					<tr>	
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								dayNullable="<%= true %>" 
								monthParam="fechaDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"				
								yearParam="fechaDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
								<label><liferay-ui:message key="fecha-hasta" />:</label>&nbsp;
								<liferay-ui:input-date
								dayParam="fechaHastaDia"																					
								dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
								dayNullable="<%= true %>"
								monthParam="fechaHastaMes"
								monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"
								yearParam="fechaHastaAnio"
								yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 120 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td colspan="2">&nbsp;</td>
						<td colspan="2">
							<%if(!soloVer){%>
							<b>Subir desde archivo:</b>&nbsp;
							<%} %>
						</td>
					</tr>						
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					<tr>							
							<td><label><liferay-ui:message key="cta-bcria" />:</label></td>
							<td>
								<select name="<portlet:namespace/>cta_bancaria" id="<portlet:namespace/>cta_bancaria">
									<option value="">TODAS</option>									
									<%
										for (CuentaBancaria ctaBcria : ctas) {
											if(portlet_name.equals("farmacia") && ctaBcria.getEntidad().equals("A")){
										%>
												<option value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>											
										<%  }else if(portlet_name.equals("tesoreria") && ctaBcria.getEntidad().equals("O")){%>
												<option value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>											
										<%  }else if(portlet_name.equals("uoma") && ctaBcria.getEntidad().equals("U")){%>
												<option value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>
										<%  }
									}
									%>
								</select>
							</td>		
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							
							<td>
								<%if(!soloVer){%>
								 	<input type="file" name="archivo"/>
								 <%}%>
							</td>
							<td align="center">
								<%if(!soloVer){%>
									<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivo()"/>
								<%}else{%>
								&nbsp;
								<%} %>
							</td>
							
					</tr>		
					<tr><td colspan="6">&nbsp;</td></tr>		
					<tr>
						<td><label><liferay-ui:message key="movimiento" />:</label></td>
						<td>
								<select name="<portlet:namespace/>tipo_mov" id="<portlet:namespace/>tipo_mov" onchange="cambiarMovimiento();">
									<option value="0"></option>									
									<% for (TipoMovBcrio mov : movs) { %>
											<option value="<%= mov.getId_tipo_mov()%>"><%=mov.getDescripcion()%></option>											
									<% } %>
								</select>
						</td>
						<td colspan="4">&nbsp;</td>
					</tr>		
					<tr><td colspan="6">&nbsp;</td></tr>		
					<tr>
							<td><label><liferay-ui:message key="descripcion" />:</label></td>
							<td>
								<input id="<portlet:namespace />descripcion" name="<portlet:namespace />descripcion" size="40" maxlength="40" type="text" value="" />
							</td>							
							<td>							
								<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="javascript:<portlet:namespace />buscarMovimientos();"/>
								<% if (showABMButtons && !soloVer) { %>							
								<input id="<portlet:namespace />nuevoMov" value="<liferay-ui:message key="nuevo-movimiento"/>" title="<liferay-ui:message key="nuevo-movimiento" />" type="button" onClick="javascript:<portlet:namespace />nuevoMovimiento();"/>
								<%} %>
							</td>
							<td colspan="3">&nbsp;</td>						
							
					</tr>	
					<tr>
							<td colspan="6">
								&nbsp;(<liferay-ui:message key="refine-busqueda" />)
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
			<div align="center" id="<portlet:namespace />busquedaMovimientoDiv">						
			</div>
		</fieldset>
</form>	
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();	

function <portlet:namespace />uploadArchivo() {	
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_archivo_mov_bcrio';
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}

function <portlet:namespace />buscarMovimientos(){
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cta_bcria=jQuery("#<portlet:namespace/>cta_bancaria").val();
	var descripcion=jQuery("#<portlet:namespace />descripcion").val();	
	var tipo_mov=jQuery("#<portlet:namespace />tipo_mov").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_movimiento_bcrio';
	jQuery('#<portlet:namespace />buscando').show();		
	jQuery("#<portlet:namespace/>busquedaMovimientoDiv").load(url,{desde_dia:desde_dia, desde_mes:desde_mes, desde_anio:desde_anio, hasta_dia:hasta_dia,
		hasta_mes:hasta_mes, hasta_anio:hasta_anio, cta_bcria:cta_bcria, descripcion:descripcion, tipo_mov:tipo_mov}, function(){jQuery('#<portlet:namespace />buscando').hide();});
	
}

function editaMovBcrio(id_mov){	
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_movimiento_bcrio&id_mov='+id_mov;
	
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);	
}
function borraMovBcrio(id_mov){
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cta_bcria=jQuery("#<portlet:namespace/>cta_bancaria").val();
	var descripcion=jQuery("#<portlet:namespace />descripcion").val();
	var tipo_mov=jQuery("#<portlet:namespace />tipo_mov").val();			
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/borrar_movimiento_bcrio';
	if(confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
		jQuery('#<portlet:namespace />buscando').show();			
		jQuery("#<portlet:namespace/>busquedaMovimientoDiv").load(url,{desde_dia:desde_dia, desde_mes:desde_mes, desde_anio:desde_anio, hasta_dia:hasta_dia,
			hasta_mes:hasta_mes, hasta_anio:hasta_anio, cta_bcria:cta_bcria, descripcion:descripcion,id_movimiento:id_mov, tipo_mov:tipo_mov}, function(){jQuery('#<portlet:namespace />buscando').hide();});
	}else{
		return false;
	}
}

function <portlet:namespace />nuevoMovimiento(){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_movimiento_bcrio';
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}     
	
</script>