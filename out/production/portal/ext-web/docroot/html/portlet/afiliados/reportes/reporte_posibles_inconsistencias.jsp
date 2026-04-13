<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
List<TipoAporte> tiposAporte = (List<TipoAporte>) portletSession.getAttribute(WebKeysAfiliados.TIPOS_APORTE_EN_SESSION,	PortletSession.APPLICATION_SCOPE);
List<Plan> planList = (List<Plan>) portletSession.getAttribute(WebKeysAfiliados.PLANES_EN_SESSION,PortletSession.APPLICATION_SCOPE);
List<Localidad> localidades = (List<Localidad>) portletSession.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION,PortletSession.APPLICATION_SCOPE);
List<Provincia> provincias = (List<Provincia>) portletSession.getAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,	PortletSession.APPLICATION_SCOPE);
List<TercerizadoraServicio> tercServList=TraeListasServiceUtil.getTercerizadoraServicio();

		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaFin = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
%>
<fieldset class="block-labels">
	<legend><liferay-ui:message key="listado-padrones" /></legend>
	<table class="lfr-table">
		<tr>
			<td><liferay-ui:message	key="empresa" />:</td>
			<td colspan="5">
				<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
				  	<liferay-util:param name="esEditable" value='true'/>
			  		<liferay-util:param name="portlet_name" value='afiliados'/>
				</liferay-util:include>
			</td>
		</tr>
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td><liferay-ui:message	key="entre-edades" />:</td>
			<td colspan="5" align="left">
				<input type="text" id="<portlet:namespace />edadIni" name="<portlet:namespace />edadIni" size="4"/>
				&nbsp;-&nbsp;
				<input type="text" id="<portlet:namespace />edadFin" name="<portlet:namespace />edadFin" size="4"/>
			</td>
		</tr>
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td><label><liferay-ui:message key="titulares-familiares" />:</label></td>
			<td>
				<select name="<portlet:namespace/>titularesYFliares" id="<portlet:namespace/>titularesYFliares">
					<option value="0">Titulares y Familiares</option>
					<option value="1">Solo titulares</option>
					<option value="2">Solo familiares</option>
				</select>
			</td>
			<td><label><liferay-ui:message key="parentesco" />:</label></td>
			<td colspan="2">
				<select name="<portlet:namespace/>parentesco" id="<portlet:namespace/>parentesco">
					<option value=''></option>
					<%for (String parentesco : WebKeysAfiliados.PARENTESCOS_INTEGRANTES) {	%>
						<option value="<%= parentesco %>"><%=parentesco%></option>
					<%	}	%>
				</select>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td>
				<select  name="<portlet:namespace/>provincia" id="<portlet:namespace/>provincia">
				<option value=''></option>
				<% for (Provincia provincia : provincias) { %>
					<option	value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
				<% } %>
				</select>
			</td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
			<td>
				<select name="<portlet:namespace/>localidad" id="<portlet:namespace/>localidad">
				<option value=''></option>
				<% for (Localidad localidad : localidades) { %>
					<option	value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
				<% } %>
			</select>
			</td>
			<td><label><liferay-ui:message key="tercerizadora-servicio" /></label></td>					
			<td>						
				<select name="<portlet:namespace/>tercerizadora" id="<portlet:namespace/>tercerizadora">		
					<option value=''></option>								
					<%for (TercerizadoraServicio terce : tercServList) {%>
						<option value="<%= terce.getId_tercerizadora()%>"><%=terce.getDescripcion()%></option>
					<%	}%>
				</select>						
			</td>												
		</tr>
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td><label><liferay-ui:message key="seccional" />:</label></td>
			<td>
				<liferay-util:include page="/html/portlet/afiliados/busqueda_seccional.jsp">
				</liferay-util:include>
			</td>
			<td><label><liferay-ui:message key="plan" /></label></td>					
			<td>						
				<select name="<portlet:namespace/>plan" id="<portlet:namespace/>plan">
						<option value='0'></option>									
						<%for (Plan plan : planList) {%>
							<option value="<%= plan.getId()%>"><%=plan.getDescripcion()%></option>
						<%}	%>
				</select>						
			</td>
			<td><label><liferay-ui:message key="tipo-aporte" /></label></td>					
			<td>						
				<select name="<portlet:namespace/>tipo_aporte" id="<portlet:namespace/>tipo_aporte">
						<option value='0'></option>
						<%for (TipoAporte tipo : tiposAporte) {%>
							<option value="<%= tipo.getId_aporte()%>"><%=tipo.getDescripcion()%></option>
						<%}	%>
				</select>						
			</td>
		</tr>
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td><label><liferay-ui:message key="vigente-entre-fechas" />:</label></td>
			<td>
				<liferay-ui:input-date
				dayParam="fechaDesdeDia1"
				dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
				monthParam="fechaDesdeMes1"
				monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
				yearParam="fechaInicioAnio1"
				yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
				yearRangeStart="<%= current.get(Calendar.YEAR) - 50 %>"
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
				firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
				disabled="false" />
			</td>
			<td>-</td>
			<td>
				<liferay-ui:input-date
				dayParam="fechaHastaDia2"
				dayValue="<%= fechaFin.get(Calendar.DATE) %>" 
				monthParam="fechaHastaMes2"
				monthValue="<%= fechaFin.get(Calendar.MONTH) %>"				
				yearParam="fechaHastaAnio2"
				yearValue="<%= fechaFin.get(Calendar.YEAR) %>"
				yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
				firstDayOfWeek="<%= fechaFin.getFirstDayOfWeek() - 1 %>"
				disabled="false" />
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td colspan="6">
				<input id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
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
	<div align="center" id="<portlet:namespace />busquedaReporteDiv">						
	</div>
</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	

	jQuery('#<portlet:namespace />reporte').click(function(){

		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");

		var cuit = document.getElementById("<portlet:namespace />cuit_entidad");
		var edadIni = document.getElementById("<portlet:namespace />edadIni");
		var edadFin = document.getElementById("<portlet:namespace />edadFin");
		var tituYFliares = document.getElementById("<portlet:namespace/>titularesYFliares");
		var terce = document.getElementById("<portlet:namespace/>tercerizadora");
		var loca = document.getElementById("<portlet:namespace/>localidad");
		var prov= document.getElementById("<portlet:namespace/>provincia");
		var plan = document.getElementById("<portlet:namespace/>plan");
		var tipoAporte = document.getElementById("<portlet:namespace/>tipo_aporte");
		var parentesco = document.getElementById("<portlet:namespace/>parentesco");
		var idSeccional = document.getElementById("<portlet:namespace/>id_seccional");
		
		
		jQuery('#<portlet:namespace />buscando').show();
		
		
		jQuery('#<portlet:namespace />buscando').show();
		window.location.href ='/zipservlet/?reporte=LISTADO_PADRON'
			+'&cuit='+cuit.value
			+'&fechaDesdeDia='+fechaDesdeDia.value
			+'&fechaDesdeMes='+fechaDesdeMes.value
			+'&fechaDesdeAnio='+fechaDesdeAnio.value
			+'&fechaHastaDia='+fechaHastaDia.value
			+'&fechaHastaMes='+fechaHastaMes.value
			+'&fechaHastaAnio='+fechaHastaAnio.value
			+'&edadIni='+edadIni.value
			+'&edadFin='+edadFin.value
			+'&tituYFliares='+tituYFliares.value
			+'&terce='+terce.value
			+'&loca='+loca.value
			+'&prov='+prov.value
			+'&plan='+plan.value
			+'&tipoAporte='+tipoAporte.value
			+'&parentesco='+parentesco.value
			+'&idSeccional='+idSeccional.value;
	});
	 function cambiaCuit(){
		}
	
	
</script>

