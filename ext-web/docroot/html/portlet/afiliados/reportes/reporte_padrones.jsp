<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
List<TipoAporte> tiposAporte = (List<TipoAporte>) portletSession.getAttribute(WebKeysAfiliados.TIPOS_APORTE_EN_SESSION,	PortletSession.APPLICATION_SCOPE);
List<Plan> planList = (List<Plan>) portletSession.getAttribute(WebKeysAfiliados.PLANES_EN_SESSION,PortletSession.APPLICATION_SCOPE);
List<TercerizadoraServicio> tercServList=TraeListasServiceUtil.getTercerizadoraServicio();

Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
Calendar fechaFin = CalendarFactoryUtil.getCalendar();
		
fechaInicio.add(Calendar.MONTH, -1);
Calendar current = CalendarFactoryUtil.getCalendar();

List<Seccional> seccionales = (ArrayList<Seccional>) portletSession
.getAttribute(WebKeysGlobal.SECCIONALES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (seccionales == null) {
	seccionales = TraeListasServiceUtil.getSeccionales();
	portletSession.setAttribute(WebKeysGlobal.SECCIONALES_EN_SESSION,
	seccionales,PortletSession.APPLICATION_SCOPE);
}
%>
<fieldset class="block-labels">
	<legend><liferay-ui:message key="listado-padrones" /></legend>
	<table class="lfr-table">
		<tr>
			<td><liferay-ui:message	key="empresa" />:</td>
			<td colspan="5">
			<liferay-util:include page="/html/portlet/afiliados/reportes/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<liferay-util:param name="portlet_name" value='afiliados'/>
				</liferay-util:include>
			</td>
		</tr>
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td><liferay-ui:message	key="entre-edades" />:</td>
			<td colspan="3" align="left">
				<input type="text" id="<portlet:namespace />edadIni" name="<portlet:namespace />edadIni" size="4"/>
				&nbsp;-&nbsp;
				<input type="text" id="<portlet:namespace />edadFin" name="<portlet:namespace />edadFin" size="4"/>
				&nbsp;
				<label><liferay-ui:message key="escala-salarial" /></label>
				&nbsp;
				<select name="<portlet:namespace/>escala_salarial"
					id="<portlet:namespace/>escala_salarial">
					<option value=""></option>
					<option value="A">A</option>
					<option value="B">B</option>
					<option value="C">C</option>
					<option value="D">D</option>
					<option value="E">E</option>
				</select>
			</td>
			<td><label>Proyecto:</label></td>
			<td><select name="<portlet:namespace />proyecto" id="<portlet:namespace />proyecto"> 
					<option value=""></option>
					<option value="VOLVER2016" >VOLVER 2016</option> 
					<option value="INFOXAFIP" >Informados por AFIP</option>
					<option value="VIGENEXCEP" >VIGENTE POR EXCEPCION</option>
					<option value="INFOXSSS" >Informados por la SSS</option>
					<option value="MEJORPERT" >MEJOR PERTENECER</option>
				</select>
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
			<td>
				<select name="<portlet:namespace/>parentesco" id="<portlet:namespace/>parentesco" >
					<option value=''></option>
					<%	for (Parentesco parentesco : parentescos) { %>
					<option value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%></option>
					<%	} %>
				</select>
			</td>
			<%-- <td><label><liferay-ui:message key="motivo-baja" />:</label></td>
			<td>						
				<select name="<portlet:namespace/>motivo_baja" id="<portlet:namespace/>motivo_baja" multiple="multiple" size="5">					
						<% for (MotivoBaja motivoBaja : motivos) {	%>
								<option value="<%= motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion().trim()%></option>		
						<% }%>
				</select>
			</td> --%>
			<td colspan="2">&nbsp;</td>
			
		</tr>
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td><label><liferay-ui:message key="seccional" />:</label></td>
			<td>				
				<select name="<portlet:namespace/>id_seccional" id="<portlet:namespace/>id_seccional" multiple="multiple" size="5">
					<%for (Seccional seccional : seccionales) {%>
						<option value="<%= seccional.getId()%>"><%=seccional.getDescripcion().trim()%></option>
					<%	}%>
				</select>	
			</td>
			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td>
				<select  name="<portlet:namespace/>provincia" multiple="multiple" size="5" id="<portlet:namespace/>provincia" onchange="javascript:filtrarLocalidad();">				
				<% for (Provincia provincia : provincias) { %>
					<option	value="<%= provincia.getId() %>"><%=provincia.getDescripcion().trim()%></option>
				<% } %>
				</select>
			</td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
			<td>
				<div class="selector-localidad" style="vertical-align: top;">
				  	<select id="<portlet:namespace/>localidad" name="<portlet:namespace/>localidad" style="width: 250px;" multiple="multiple">
							<option selected value="">Seleccione una provincia primero</option>
					</select>	
			 </div>
			</td>
															
		</tr>
		
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td><label><liferay-ui:message key="tercerizadora-servicio" />:</label></td>					
			<td>						
				<select name="<portlet:namespace/>tercerizadora" id="<portlet:namespace/>tercerizadora" multiple="multiple" size="5">
					<%for (TercerizadoraServicio terce : tercServList) {%>
						<option value="<%= terce.getId_tercerizadora()%>"><%=terce.getDescripcion().trim()%></option>
					<%	}%>
				</select>						
			</td>
			<td><label><liferay-ui:message key="plan" />:</label></td>					
			<td>						
				<select name="<portlet:namespace/>plan" id="<portlet:namespace/>plan" multiple="multiple" size="5">													
						<%for (Plan plan : planList) {%>
							<option value="<%= plan.getId()%>"><%=plan.getDescripcion().trim()%></option>
						<%}	%>
				</select>						
			</td>	
			<td><label><liferay-ui:message key="tipo-aporte" />:</label></td>
			<td>
				<select id="<portlet:namespace/>tipo_aporte" name="<portlet:namespace/>tipo_aporte" multiple="multiple" size="5">					
					<%for (TipoAporte tipo : tiposAporte) {%>
						<option value="<%= tipo.getId_aporte()%>"><%=tipo.getDescripcion().trim()%></option>
					<%}	%>
						
				</select>											
			</td>					
		</tr>				
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td><label><liferay-ui:message key="tipo-busqueda" />:</label></td>
			<td>
				<select id="<portlet:namespace/>tipo_busqueda" name="<portlet:namespace/>tipo_busqueda">
					<option value="1">ALTAS</option>
					<!-- <option value="2">BAJAS</option> -->
					<option value="3" selected>VIGENTES</option>
					<!-- <option value="4">GESTION SECCIONAL</option> -->
				</select>
			</td>
			<td colspan="2">
				<liferay-ui:message key="desde" />:&nbsp;
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
			<td colspan="2">
				<liferay-ui:message key="hasta" />:&nbsp;
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
		</tr>
		<%-- <tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td colspan="2">* Fechas de Proceso para Bajas unicamente</td>
			<td colspan="2">
				<!-- <liferay-ui:message key="process-fecha" /> -->Proceso desde :&nbsp;
				<liferay-ui:input-date
				dayParam="fechaProcDesdeDia1"
				dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
				monthParam="fechaProcDesdeMes1"
				monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
				yearParam="fechaProcInicioAnio1"
				yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
				yearRangeStart="<%= current.get(Calendar.YEAR) - 50 %>"
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
				firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
				disabled="false" />
			</td>			
			<td colspan="2">
				<!-- <liferay-ui:message key="process-fecha" /> -->Proceso hasta :&nbsp;
				<liferay-ui:input-date
				dayParam="fechaProcHastaDia2"
				dayValue="<%= fechaFin.get(Calendar.DATE) %>" 
				monthParam="fechaProcHastaMes2"
				monthValue="<%= fechaFin.get(Calendar.MONTH) %>"				
				yearParam="fechaProcHastaAnio2"
				yearValue="<%= fechaFin.get(Calendar.YEAR) %>"
				yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
				firstDayOfWeek="<%= fechaFin.getFirstDayOfWeek() - 1 %>"
				disabled="false" />
			</td>			
		</tr> --%>
		<tr><td colspan="2">&nbsp;</td></tr>
		<table>
		<tr>
			<td>				
				<input id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" title="<liferay-ui:message key="buscar" />" type="button"/>&nbsp;
			</td>
				<td>&nbsp;</td>	
			<td  colspan="3" style="background-color:#AEB6BF">	
				<liferay-ui:message key="total-por-tercerizadora" />:
				<input type="checkbox" id="<portlet:namespace />agrupar_tercerizadora" name="<portlet:namespace />agrupar_tercerizadora" value="false" />
			</td>
				<td>&nbsp;</td>
			<td  colspan="6" style="background-color:#AEB6BF">	
				<liferay-ui:message key="total-por-seccional" />:
				<input type="checkbox" id="<portlet:namespace />agrupar_seccional" name="<portlet:namespace />agrupar_seccional" value="false" />
			</td>	
				<td>&nbsp;</td>
			<td  colspan="6" style="background-color:#AEB6BF">	
				<liferay-ui:message key="total-por-plan" />:
				<input type="checkbox" id="<portlet:namespace />agrupar_plan" name="<portlet:namespace />agrupar_plan" value="false" />
			</td>
				<td>&nbsp;</td>	
			<td  colspan="6" style="background-color:#AEB6BF">
				<liferay-ui:message key="total-por-empresa" />:
				<input type="checkbox" id="<portlet:namespace />agrupar_empresa" name="<portlet:namespace />agrupar_empresa" value="false" />
			</td>
				<td>&nbsp;</td>	
			<td  colspan="6" style="background-color:#AEB6BF">
				<liferay-ui:message key="total-por-entidad" />:
				<input type="checkbox" id="<portlet:namespace />agrupar_entidad" name="<portlet:namespace />agrupar_entidad" value="false" />
			</td>
				<td  colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				
			<td style="background-color:#AEB6BF">
				<liferay-ui:message key="vista-tercerizadora" />:
				<input type="checkbox" id="<portlet:namespace />vista_tercerizadora" name="<portlet:namespace />vista_tercerizadora" value=""  />
			</td> 

			<td  colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

			<td style="background-color:#AEB6BF">
				<liferay-ui:message key="Vista Admifarm" />:
				<input type="checkbox" id="<portlet:namespace />vista_admifarm" name="<portlet:namespace />vista_admifarm" value=""  />
			</td>
			<td>&nbsp;</td>
		</tr>
	</table>
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
		var fechaDesdeDia = document.getElementById("<portlet:namespace />fechaDesdeDia1");
		var fechaDesdeMes = document.getElementById("<portlet:namespace />fechaDesdeMes1");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");
		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
		var cuit=jQuery("#<portlet:namespace />cuit_entidad").val();
		var sucursal=jQuery("#<portlet:namespace />sucursal_entidad").val();
		var razonSocial=jQuery("#<portlet:namespace />entidad").val();
		var edadIni = document.getElementById("<portlet:namespace />edadIni");
		var edadFin = document.getElementById("<portlet:namespace />edadFin");
		var tituYFliares = document.getElementById("<portlet:namespace/>titularesYFliares");
		var tituYFliaresDesc = jQuery("#<portlet:namespace/>titularesYFliares option:selected").html();

		var idTerce = jQuery("#<portlet:namespace/>tercerizadora").val();
		var idLoca = jQuery("#<portlet:namespace/>localidad").val();
		var idProv = jQuery("#<portlet:namespace/>provincia").val();
		var idPlan = jQuery("#<portlet:namespace/>plan").val();
		var tipoAporte = jQuery("#<portlet:namespace/>tipo_aporte").val();
		var parentesco = document.getElementById("<portlet:namespace/>parentesco");
		var parentescoDesc = jQuery("#<portlet:namespace/>parentesco option:selected").html();
		var idSeccional = jQuery("#<portlet:namespace/>id_seccional").val();
		var escala_salarial = document.getElementById("<portlet:namespace/>escala_salarial");
		var total_tercerizadora=jQuery("#<portlet:namespace/>agrupar_tercerizadora").is(':checked');
		var total_plan=	jQuery("#<portlet:namespace/>agrupar_plan").is(':checked');
		var total_seccional=jQuery("#<portlet:namespace/>agrupar_seccional").is(':checked');
		var total_empresa=jQuery("#<portlet:namespace/>agrupar_empresa").is(':checked');
		var total_entidad=jQuery("#<portlet:namespace/>agrupar_entidad").is(':checked');
		var para_tercerizadora=jQuery("#<portlet:namespace/>vista_tercerizadora").is(':checked');
		var para_admifarm=jQuery("#<portlet:namespace/>vista_admifarm").is(':checked');
		var tipoBusqueda= jQuery("#<portlet:namespace/>tipo_busqueda").val();
		var descTipoBusqueda= jQuery("#<portlet:namespace/>tipo_busqueda option:selected").html();
		var motivo_baja= jQuery("#<portlet:namespace/>motivo_baja").val();
		var proyectos= jQuery("#<portlet:namespace/>proyecto").val();
//		jQuery('#<portlet:namespace />buscando').show();
	/* 	var fechaProcDesdeDia = document.getElementById("<portlet:namespace />fechaProcDesdeDia1");
		var fechaProcDesdeMes = document.getElementById("<portlet:namespace />fechaProcDesdeMes1");
		var fechaProcDesdeAnio = document.getElementById("<portlet:namespace />fechaProcInicioAnio1");
		var fechaProcHastaDia = document.getElementById("<portlet:namespace />fechaProcHastaDia2");
		var fechaProcHastaMes = document.getElementById("<portlet:namespace />fechaProcHastaMes2");
		var fechaProcHastaAnio = document.getElementById("<portlet:namespace />fechaProcHastaAnio2"); */
		var fechaProcDesdeDia = '01';
		var fechaProcDesdeMes = '00';
		var fechaProcDesdeAnio = '2000';
		var fechaProcHastaDia = '01';
		var fechaProcHastaMes = '00';
		var fechaProcHastaAnio = '2000';

		/*Tomamos las descripciones seleccionadas*/
		/* var foo = []; 
		$('#multiple :selected').each(function(i, selected){ 
		  foo[i] = $(selected).text(); 
		}); */
		var motivosBajaDesc = []; 
		jQuery('#<portlet:namespace/>motivo_baja :selected').each(function(i, selected){ 
			motivosBajaDesc[i] = jQuery(selected).text(); 
		});
		var seccionalesDesc = []; 
		jQuery('#<portlet:namespace/>id_seccional :selected').each(function(i, selected){ 
			seccionalesDesc[i] = jQuery(selected).text(); 
		});
		var provinciasDesc = []; 
		jQuery('#<portlet:namespace/>provincia :selected').each(function(i, selected){ 
			provinciasDesc[i] = jQuery(selected).text(); 
		});
		var localidadesDesc = []; 
		jQuery('#<portlet:namespace/>localidad :selected').each(function(i, selected){ 
			localidadesDesc[i] = jQuery(selected).text(); 
		});
		var tercerizdoraDesc = []; 
		jQuery('#<portlet:namespace/>tercerizadora :selected').each(function(i, selected){ 
			tercerizdoraDesc[i] = jQuery(selected).text(); 
		});
		var planesDesc = []; 
		jQuery('#<portlet:namespace/>plan :selected').each(function(i, selected){ 
			planesDesc[i] = jQuery(selected).text(); 
		});
		var aportesDesc = []; 
		jQuery('#<portlet:namespace/>tipo_aporte :selected').each(function(i, selected){ 
			aportesDesc[i] = jQuery(selected).text(); 
		});
		
		window.location.href ='/xlsservlet/?reporte=LISTADO_PADRON'
			+'&cuit='+cuit
			+'&sucursal='+sucursal
			+'&razonSocial='+razonSocial
			+'&fechaDesdeDia='+fechaDesdeDia.value
			+'&fechaDesdeMes='+fechaDesdeMes.value
			+'&fechaDesdeAnio='+fechaDesdeAnio.value
			+'&fechaHastaDia='+fechaHastaDia.value
			+'&fechaHastaMes='+fechaHastaMes.value
			+'&fechaHastaAnio='+fechaHastaAnio.value
			+'&edadIni='+edadIni.value
			+'&edadFin='+edadFin.value
			+'&tituYFliares='+tituYFliares.value
			+'&tituYFliaresDesc='+tituYFliaresDesc
			+'&idTercerizadora='+idTerce
			+'&descTercerizadora='+tercerizdoraDesc
			+'&idLoca='+idLoca
			+'&descLocalidades='+localidadesDesc
			+'&idProv='+idProv
			+'&descProvincias='+provinciasDesc
			+'&idPlan='+idPlan
			+'&descPlanes='+planesDesc
			+'&tipoAporte='+tipoAporte
			+'&descTiposAporte='+aportesDesc
			+'&parentesco='+parentesco.value
			+'&descParentesco='+parentescoDesc
			+'&idSeccional='+idSeccional
			+'&descSeccionales='+seccionalesDesc			
			+'&escala_salarial='+escala_salarial.value
			+'&total_tercerizadora='+total_tercerizadora
			+'&total_plan='+total_plan
			+'&total_seccional='+total_seccional
			+'&total_empresa='+total_empresa
			+'&total_entidad='+total_entidad
			+'&tipoBusqueda='+tipoBusqueda
			+'&descTipoBusqueda='+descTipoBusqueda
 			+'&fechaProcDesdeDia=01'
			+'&fechaProcDesdeMes=00'
			+'&fechaProcDesdeAnio=1900'
			+'&fechaProcHastaDia=01'
			+'&fechaProcHastaMes=00'
			+'&fechaProcHastaAnio=1900'
			+'&vistaTercerizadora='+para_tercerizadora
			+'&vistaAdmifarm='+para_admifarm
			+'&idsMotivoBaja='+motivo_baja
			+'&motivosBajaDesc='+motivosBajaDesc
			+'&proyecto='+proyectos;  
			

	});

	function filtrarLocalidad() {
		var idProvincia = jQuery('#<portlet:namespace/>provincia').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_provincia_localidad&idProvincia='+idProvincia;
		jQuery("#<portlet:namespace/>localidad").attr('disabled', 'disabled');
		jQuery.ajax({   
			url: url,
			async:false,
			success: function(data){
				document.getElementById("<portlet:namespace/>localidad").length = 0;
				jQuery("#<portlet:namespace/>localidad").removeAttr('disabled');
				var obj = jQuery.parseJSON(data);
//				for(var i =0;i< obj.listaFiltrada.length; i++){	
//					jQuery("#<portlet:namespace/>localidad").append(obj.listaFiltrada[i]);
//				}
				
				jQuery('.selector-localidad select').html(data).fadeIn();

			}
		});
	}

	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}

	jQuery('#<portlet:namespace />borrar').click(function(){		
	    var i; 
	    var select = document.getElementById("<portlet:namespace/>tipo_aporte"); 
	    for(i=1;i<select.options.length;i++) { 
	        select.options[i].selected=false; 
	    } 
	});	
	
	function cambiaCuit(){
	}
</script>
