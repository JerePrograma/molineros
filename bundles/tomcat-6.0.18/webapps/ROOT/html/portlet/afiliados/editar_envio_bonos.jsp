<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
	portletSession.removeAttribute(WebKeysAfiliados.ENVIO_BONOS,PortletSession.APPLICATION_SCOPE);
	String accion=request.getParameter("accion");
	String tipo_bono_param=request.getParameter("tipo_bono_hidden");	
	String bonoDesde=request.getParameter("bono_desde_hidden");
	String bonoHasta=request.getParameter("bono_hasta_hidden");
	String fechaEnvio=request.getParameter("fecha_envio_hidden");
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
	fechaDesde.setTime(new Date());
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date()); 		
	Calendar periodoDesde = CalendarFactoryUtil.getCalendar(); 		
	periodoDesde.setTime(new Date());
	Calendar periodoHasta = CalendarFactoryUtil.getCalendar(); 		
	periodoHasta.setTime(new Date());	
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(WindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/afiliados/view");	
	currentURL = PortalUtil.getCurrentURL(request);
	
	List<TipoBono> tiposBonos = TraeListasServiceUtil.getTiposDeBonos();
%>


<script>
//habilita/inhabilita Controles de Pantalla  
		function Off_OnControlsPantalla(desactiva)
		{		
			document.getElementById("<portlet:namespace/>tipo_bono").disabled=desactiva;
			document.getElementById("<portlet:namespace/>bono_desde").disabled=desactiva;
			document.getElementById("<portlet:namespace/>bono_hasta").disabled=desactiva;
			document.getElementById("<portlet:namespace/>id_seccional").disabled=desactiva;
			document.getElementById("<portlet:namespace/>seccional").disabled=desactiva;
	    }    
</script>


<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">	
	<fieldset class="block-labels">
	<c:choose>
		<c:when test='<%= null!=accion && accion.equals("rendir") %>'>
			<legend><liferay-ui:message key="rendicion-bonos" /></legend>
		</c:when>
		<c:when test='<%= null!=accion && accion.equals("anular") %>'>
			<legend><liferay-ui:message key="anulacion-bonos" /></legend>
		</c:when>
		<c:when test='<%= null==accion%>'>
			<legend><liferay-ui:message key="envio-bonos" /></legend>
		</c:when>
		<c:when test='<%= null!=accion  || (null!= accion && accion.equals("cargar"))%>'>
			<legend><liferay-ui:message key="carga-bonos" /></legend>
		</c:when>		
		
	</c:choose>	
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="tipo-bono" />:</label></td>
						<td>
							<select name="<portlet:namespace/>tipo_bono" id="<portlet:namespace/>tipo_bono" >								
									<%
										for (TipoBono tb : tiposBonos) {
									%>
										<option value="<%=tb.getTipo_bono()+"-"+tb.getTipo_bono_string().trim() %>"							 										     
											
										<% 	if (null != accion && accion.equals("anular")) { %>
												<%if (tb.getTipo_bono() == Integer.parseInt(tipo_bono_param.split("-")[0])) {%> 
														selected="selected"
											 	<%}%>
								 		<%}%>
 																																										 																										     									     								 
											 >										     
											 <%=tb.getTipo_bono()+"-"+tb.getTipo_bono_string().trim()%>											
										</option>										
									<%
									}
									%>							
							</select>
						</td>
						<c:choose>
							<c:when test='<%= null==accion  || (null!= accion && !accion.equals("cargar"))%>'>
								<td><label><liferay-ui:message key="seccional" />:</label></td>									
								<td colspan="2" rowspan="2" style="vertical-align:top" ><jsp:include page='/html/portlet/afiliados/busqueda_seccional.jsp' /></td>
							</c:when>
						</c:choose>	
						<c:choose>
							<c:when test='<%= null==accion  || (null!= accion && !accion.equals("rendir") && !accion.equals("cargar") && !accion.equals("anular")   )%>'>
								<td><label><liferay-ui:message key="fecha-envio" />:</label></td>
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
										yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 2 %>"
										yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
										firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
										disabled="<%= false %>" />
								</td>
							</c:when>
							<c:when test='<%= null!=accion && ( accion.equals("rendir") || accion.equals("anular") )  %>'>
								<td colspan="2">&nbsp;</td>
							</c:when>
						</c:choose>						
					</tr>
					<tr>
						<td colspan="7">&nbsp;</td>
					</tr>
						
					<tr>
						<td><label><liferay-ui:message key="bono-desde" />: </label></td>
						<td><input id="<portlet:namespace />bono_desde" name="<portlet:namespace />bono_desde" size="8" maxlength="8" type="text" value="<%=bonoDesde%>" /></td>
						<td><label><liferay-ui:message key="bono-hasta" />:</label></td>
						<td><input id="<portlet:namespace />bono_hasta" name="<portlet:namespace />bono_hasta" size="8" maxlength="8" type="text" value="<%=bonoHasta%>" /></td>
						<c:choose>
							<c:when test='<%= null!=accion &&  accion.equals("rendir")   %>'>
								<td><label><liferay-ui:message key="fecha-rendicion" />: </label></td>
								<td>
									<liferay-ui:input-date
										dayParam="fechaRendicionDia"
										dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
										dayNullable="<%= true %>" 
										monthParam="fechaRendicionMes"
										monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
										monthNullable="<%= true %>"				
										yearParam="fechaRendicionAnio"
										yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
										yearNullable="<%= true %>"
										yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 2 %>"
										yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
										firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
										disabled="<%= false %>" />
								</td>
							</c:when>
							
							<c:when test='<%= null!=accion &&   accion.equals("anular")  %>'>
								<td><label><liferay-ui:message key="fecha-anular" />: </label></td>
								<td>
									<liferay-ui:input-date
										dayParam="fechaAnularDia"
										dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
										dayNullable="<%= true %>" 
										monthParam="fechaAnularMes"
										monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
										monthNullable="<%= true %>"				
										yearParam="fechaAnularAnio"
										yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
										yearNullable="<%= true %>"
										yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 2 %>"
										yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
										firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
										disabled="<%= false %>" />
								</td>
							</c:when>
							
							<c:when test='<%= null==accion || (null!= accion && (!accion.equals("rendir") && !accion.equals("rendir")  ) ) %>'>
								<td colspan="2">&nbsp;</td>
							</c:when>
							
						</c:choose>
						<td>
							<c:choose>
								<c:when test='<%= null==accion || (null!= accion && !accion.equals("rendir") && !accion.equals("cargar") && !accion.equals("anular") ) %>'>
									<input id="<portlet:namespace />send" value="<liferay-ui:message key="send"/>" title="<liferay-ui:message key="send" />" type="button" onClick="javascript:enviarBonos();"/>									
								</c:when>														
								<c:when test='<%= null!= accion && accion.equals("cargar") %>'>
									<input id="<portlet:namespace />save" value="<liferay-ui:message key="save"/>" title="<liferay-ui:message key="save" />" type="button" onClick="javascript:enviarBonos('cargar');"/>
								</c:when>
								<c:when test='<%= null!=accion && accion.equals("rendir") %>'>
									<input id="<portlet:namespace />rendir" value="<liferay-ui:message key="rendir"/>" title="<liferay-ui:message key="rendir" />" type="button" onClick="javascript:rendirBonos();"/>
								</c:when>
								<c:when test='<%= null!=accion && accion.equals("anular") %>'>
									<input id="<portlet:namespace />anular" value="<liferay-ui:message key="anular"/>" title="<liferay-ui:message key="anular" />" type="button" onClick="javascript:anularBonos();"/>
								</c:when>
								
							</c:choose>							
							<input type="hidden" id="<portlet:namespace />fecha_envio_hidden" name="<portlet:namespace />fecha_envio_hidden"/>
							<input type="hidden" id="<portlet:namespace />tipo_bono_hidden" name="<portlet:namespace />tipo_bono_hidden"/>					
							<input type="hidden" id="<portlet:namespace />bono_hasta_hidden" name="<portlet:namespace />bono_hasta_hidden"/>
							<input type="hidden" id="<portlet:namespace />bono_desde_hidden" name="<portlet:namespace />bono_desde_hidden"/>
						</td>
					</tr>									
				</table>				
				<div align="center" id="<portlet:namespace />busquedaAfiliadoDiv">						
				</div>
	</fieldset>
</form>

<% 	if (null != accion && accion.equals("anular")) { %>
<script>
   // inhabilita los controles si esta anulando 
   Off_OnControlsPantalla(true);   
</script>
<% 	} %>

<script type="text/javascript">
	var popupAfill;
	
    
	function enviarBonos(accion){
		var parametro_accion;
		if(accion!=null){
			parametro_accion='&accion='+accion;			
		}
		var tipo_bono=jQuery('#<portlet:namespace />tipo_bono').val();
		var bono_desde=	jQuery("#<portlet:namespace/>bono_desde").val();
		var bono_hasta=	jQuery("#<portlet:namespace/>bono_hasta").val();
	
		if(accion==null || accion!='cargar') {
			var seccional=jQuery('#<portlet:namespace />id_seccional').val();
			var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
			var desde_mes= parseInt(jQuery("#<portlet:namespace/>fechaDesdeMes").val())+1;
			var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();			
		}
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/enviar_bonos&tipo_bono='+escape(tipo_bono)+'&bono_desde='+bono_desde+'&bono_hasta='+bono_hasta;
		if (parametro_accion!=null){
			url=url+parametro_accion
		}
		if(accion==null || accion!='cargar') {
			url=url+'&id_seccional='+seccional+'&fecha_envio='+desde_dia+'/'+desde_mes+'/'+desde_anio;
		}	
		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, limpiar_campos());	
	}

	function limpiar_campos(){		
		jQuery("#<portlet:namespace/>bono_desde").val("");
		jQuery("#<portlet:namespace/>bono_hasta").val("");
	}
	
	function rendirBonos(){
		var tipo_bono=jQuery('#<portlet:namespace />tipo_bono').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional').val();
		var desde_dia=jQuery("#<portlet:namespace/>fechaRendicionDia").val();	
		var desde_mes= parseInt(jQuery("#<portlet:namespace/>fechaRendicionMes").val())+1;
		var desde_anio=jQuery("#<portlet:namespace/>fechaRendicionAnio").val();
		var bono_desde=	jQuery("#<portlet:namespace/>bono_desde").val();
		var bono_hasta=	jQuery("#<portlet:namespace/>bono_hasta").val();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/enviar_bonos&tipo_bono='+escape(tipo_bono)+'&id_seccional='+seccional+
		'&fecha_rendicion='+desde_dia+'/'+desde_mes+'/'+desde_anio+'&bono_desde='+bono_desde+'&bono_hasta='+bono_hasta+'&accion=rendir';		
		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url);
	}
	
	function anularBonos(){
		var tipo_bono=jQuery('#<portlet:namespace />tipo_bono').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional').val();
		var desde_dia=jQuery("#<portlet:namespace/>fechaAnularDia").val();	
		var desde_mes= parseInt(jQuery("#<portlet:namespace/>fechaAnularMes").val())+1;
		var desde_anio=jQuery("#<portlet:namespace/>fechaAnularAnio").val();
		var bono_desde=	jQuery("#<portlet:namespace/>bono_desde").val();
		var bono_hasta=	jQuery("#<portlet:namespace/>bono_hasta").val();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/enviar_bonos&tipo_bono='+escape(tipo_bono)+'&id_seccional='+seccional+
		'&fecha_anula='+desde_dia+'/'+desde_mes+'/'+desde_anio+'&bono_desde='+bono_desde+'&bono_hasta='+bono_hasta+'&accion=anular';
		// habilita los controles para el pasaje de parametros   
		Off_OnControlsPantalla(false);		
		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url);
		Off_OnControlsPantalla(true);
	}
	
	function liberaBonos(id_envio){		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/enviar_bonos&id_envio='+id_envio+'&accion=liberar';		
		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url);
	}

	function rendirBonos(tipoBono, idSeccional, seccional, fechaEnvio, bonoDesde, bonoHasta){				
		if(fechaEnvio==null){
			fechaEnvio='<%=fechaEnvio%>';
		}			
		if(tipoBono==null){		
			var tipo_bono=jQuery('#<portlet:namespace />tipo_bono').val();
			var seccional=jQuery('#<portlet:namespace />id_seccional').val();
			var desde_dia=jQuery("#<portlet:namespace/>fechaRendicionDia").val();	
			var desde_mes= parseInt(jQuery("#<portlet:namespace/>fechaRendicionMes").val())+1;
			var desde_anio=jQuery("#<portlet:namespace/>fechaRendicionAnio").val();
			var bono_desde=	jQuery("#<portlet:namespace/>bono_desde").val();
			var bono_hasta=	jQuery("#<portlet:namespace/>bono_hasta").val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/enviar_bonos&tipo_bono='+escape(tipo_bono)+'&id_seccional='+seccional+
			'&fecha_rendicion='+desde_dia+'/'+desde_mes+'/'+desde_anio+'&bono_desde='+bono_desde+'&bono_hasta='+bono_hasta+'&fecha_envio='+fechaEnvio+'&accion=rendir';					
			jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url);
		}else{
			jQuery("#<portlet:namespace/>tipo_bono_hidden").val(tipoBono);		
			jQuery("#<portlet:namespace/>id_seccional").val(idSeccional);		
			jQuery("#<portlet:namespace/>seccional").val(seccional);
			jQuery("#<portlet:namespace/>fecha_envio_hidden").val(fechaEnvio);
			jQuery("#<portlet:namespace/>bono_desde_hidden").val(bonoDesde);
			jQuery("#<portlet:namespace/>bono_hasta_hidden").val(bonoHasta);		
			var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>">'
					  '<portlet:param name="struts_action" value="/afiliados/editar_envio_bonos" />'+ 
					  '<portlet:param name="accion" value="rendir" />'+			  				   
					  '</portlet:renderURL>';		
			document.<portlet:namespace />fm.method = 'post';
			submitForm(document.<portlet:namespace />fm, url);
		}
	}

	function exportarExcel(tipoBono, idSeccional, seccional,fechaEnvio, bonoDesde, bonoHasta){		
		window.location.href ='/xlsservlet/?reporte=REPORTE_BONOS_SECCIONAL'
			+'&tipoBono='+tipoBono
			+'&id_seccional='+idSeccional
			+'&seccional='+seccional
			+'&bono_desde='+bonoDesde
			+'&bono_hasta='+bonoHasta;			
	}
		
</script>
