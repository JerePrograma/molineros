<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
	response.setHeader("Cache-Control", "no-store"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", 0); //prevents caching at the proxy server
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysFarmaciaOspim.ROL_MEDICAMENTOS_FARMACIA_OSPIM  );
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/farmaciaospim/view");
	showABMButtons=true;
	Calendar fechaRegistro = CalendarFactoryUtil.getCalendar();
	fechaRegistro.setTime(new Date());
	Calendar current = CalendarFactoryUtil.getCalendar();	
%>
<form action="<%=portletURL%>" method="get"
	name="<portlet:namespace />fm"
	onSubmit="submitForm(this); return false;">
	<liferay-portlet:renderURLParams varImpl="portletURL" />
 
 <fieldset class="block-labels">
		<legend>
			<liferay-ui:message key="medicamentos-ospim" />
		</legend>	
 		<table>		
		<tr>
		<td colspan="12">&nbsp;</td>
		</tr>
		<tr>
			<td> <label><liferay-ui:message key="registro" /> :&nbsp;&nbsp;</label></td>
			<td>			 
			 <input id="<portlet:namespace />nroregistroMedicamento" name="<portlet:namespace />nroregistroMedicamento" size="7" maxlength="7" type="text" value="" onkeydown="allowOnlyDigits(event);" />
			</td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td> <label><liferay-ui:message key="troquel" /> :&nbsp;&nbsp;</label></td>
			<td>			 
			 <input id="<portlet:namespace />nroTroquelMedicamento" name="<portlet:namespace />nroTroquel" size="7" maxlength="7" type="text" value=""  onkeydown="allowOnlyDigits(event);" />
			 </td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td><label><liferay-ui:message key="periodo" /> :</label>&nbsp;
			<liferay-ui:input-date dayParam="fechaArchivoDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="mediPeriodoMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="mediPeriodoYear"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaRegistro.get(Calendar.YEAR)  -1%>"
			yearRangeEnd="<%= fechaRegistro.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaRegistro.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" /> </td>			
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td><label><liferay-ui:message key="nombre" /> :&nbsp;&nbsp;</label></td>
			<td>			 
			 <input id="<portlet:namespace />nombreMedicamento" name="<portlet:namespace />nombreMedicamento" size="35" maxlength="25" type="text" value="" />
			 </td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td><label><liferay-ui:message key="Código barras" />:&nbsp;&nbsp;</label></td>
			<td>			 
			 <input id="<portlet:namespace />codigobarraMedicamento" name="<portlet:namespace />codigobarraMedicamento" size="13" maxlength="13" type="text" value="" onkeydown="allowOnlyDigits(event);" />
			</td>
			</tr>
		</table>
		<table>		
		<tr><td colspan="12">&nbsp;</td></tr>	
		<tr>			 
			<td><label><liferay-ui:message key="presentacion" />:&nbsp;&nbsp;</label></td>
			<td>			 
			 <input id="<portlet:namespace />presentacionMedicamento" name="<portlet:namespace />presentacionMedicamento" size="35" maxlength="25" type="text" value="" />
			</td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td><label><liferay-ui:message key="laboratorio" />:&nbsp;&nbsp;</label></td>
			<td>			 
			 <input id="<portlet:namespace />laboratorioMedicamento" name="<portlet:namespace />laboratorioMedicamento" size="35" maxlength="25" type="text" value="" />
			</td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td><label><liferay-ui:message key="droga" />:&nbsp;&nbsp;</label></td>
			<td>			 
			 <input id="<portlet:namespace />drogaMedicamento" name="<portlet:namespace />drogaMedicamento" size="15" maxlength="20" type="text" value="" />
			</td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td colspan="1"><label>Solo Manual DAT</label></td>
			<td colspan="12">&nbsp;</td>		
		    <td colspan="5">
		    <input type="checkbox"id="<portlet:namespace />manualdat" name="<portlet:namespace />manualdat">
		    </td>
		    <td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td colspan="1"><label>Incluye Bajas</label></td>
			<td colspan="12">&nbsp;</td>		
		    <td colspan="5">
		    <input type="checkbox"id="<portlet:namespace />incluyebajas" name="<portlet:namespace />incluyebajas">
		    </td>
		</tr>				
		</table>
</fieldset>
		<table>
		<tr><td colspan="12">&nbsp;</td></tr>
		<tr>
			<td colspan="1"><input id="<portlet:namespace />buscar"
				value="<liferay-ui:message key="buscar"/>"
				title="<liferay-ui:message key="buscar-medicamento" />"
				type="button" /></td>
			<td colspan="12">&nbsp;&nbsp;&nbsp;</td>
			<td colspan="2"><c:if test="<%=showABMButtons%>">
					<input type="button"
						value="<liferay-ui:message key="compose"/>"
						title="<liferay-ui:message key="nueva-medicacion-ospim" />"
						onClick="<portlet:namespace />altaMedicacionOspim();" />
				</c:if></td>
			<td colspan="12">&nbsp;&nbsp;&nbsp;</td>	
			<td colspan="2"><c:if test="<%=showABMButtons%>">
					<input id="<portlet:namespace />exportar-busqueda"
						value="<liferay-ui:message key="exportar-busqueda"/>"
						title="<liferay-ui:message key="exportar-busqueda" />"
						type="button" />
				</c:if></td>
		</tr>
		<tr><td colspan="12">&nbsp;</td></tr>
	   </table>
	<fieldset class="block-labels">
		<div align="center" id="<portlet:namespace />buscando">
			<table style="align: center;">
				<tr>
					<td><liferay-ui:message key='buscando' /></td>
					<td align="center"><img
						alt="<liferay-ui:message key='buscando'/>"
						src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
					</td>
				</tr>
			</table>
		</div>
		<div align="center"
			id="<portlet:namespace />busquedaMedicamentosOspim">
			</div>
	</fieldset>
</form>

<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
	value="" />

<input  type="hidden" name="pagina" id="pagina" value="x" />
<script type="text/javascript">
	
    jQuery("#<portlet:namespace />fechaArchivoDia").hide();
	jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/farmaciaospim/buscar_medicamentos_registros_sesion';
	jQuery('#<portlet:namespace />busquedaMedicamentosOspim').load(url); 
	jQuery('#<portlet:namespace />buscando').hide();
	jQuery('#<portlet:namespace />buscar').click(function(){<portlet:namespace />busquedaMedicamentosOpsim(0);});
	
	function <portlet:namespace />busquedaMedicamentosOpsim(nroPagina){
		jQuery('#<portlet:namespace />buscando').show();   
		var pagina_sel=0;
		if (nroPagina!=0) {
			pagina_sel =jQuery("#<portlet:namespace/>pagina_sel").val();	
		}
		
		
		jQuery("#pagina").val(pagina_sel);
		var chk_incluyebajas =jQuery("#<portlet:namespace />incluyebajas").is(':checked');
		var mediPeriodoYear=jQuery('#<portlet:namespace />mediPeriodoYear').val(); 
		var mediPeriodoMes=jQuery('#<portlet:namespace />mediPeriodoMes').val();
		var mediTroquel=jQuery('#<portlet:namespace />nroTroquelMedicamento').val();
		var mediNombre=jQuery('#<portlet:namespace />nombreMedicamento').val(); 
		var mediPresentacion=jQuery('#<portlet:namespace />presentacionMedicamento').val(); 
		var mediLaboratorio=jQuery('#<portlet:namespace />laboratorioMedicamento').val(); 
		var mediDroga=jQuery('#<portlet:namespace />drogaMedicamento').val(); 
		var mediRegistro=jQuery('#<portlet:namespace />nroregistroMedicamento').val(); 
		var mediCodBarra=jQuery('#<portlet:namespace />codigobarraMedicamento').val();		 
		var chk_manualdat =jQuery("#<portlet:namespace />manualdat").is(':checked');		
		
		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/farmaciaospim/buscar_medicamentos_ospim&mediTroquel='+mediTroquel+		
		'&mediNombre='+mediNombre+'&mediPeriodoMes='+mediPeriodoMes+ '&mediPeriodoYear='+mediPeriodoYear+'&mediPresentacion='+encodeURI(mediPresentacion)+'&mediLaboratorio='+mediLaboratorio+
		'&mediDroga='+mediDroga+'&mediRegistro='+mediRegistro+'&mediCodBarra='+mediCodBarra+'&manualDat='+chk_manualdat+'&pagina_sel='+pagina_sel+'&incluyeBajas='+chk_incluyebajas;
        jQuery('#<portlet:namespace />busquedaMedicamentosOspim').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	}
	
	function <portlet:namespace />altaMedicacionOspim () {		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_medicamentos_entry" /></portlet:renderURL>';		
		document.<portlet:namespace />fm.method = 'post';
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	    jQuery('#<portlet:namespace />exportar-busqueda').click(function exportarBusqueda(){
		var mediPeriodoYear=jQuery('#<portlet:namespace />mediPeriodoYear').val(); 
		var mediPeriodoMes=jQuery('#<portlet:namespace />mediPeriodoMes').val();
		var mediTroquel=jQuery('#<portlet:namespace />nroTroquelMedicamento').val();
		var mediNombre=jQuery('#<portlet:namespace />nombreMedicamento').val(); 
		var mediPresentacion=jQuery('#<portlet:namespace />presentacionMedicamento').val(); 
		var mediLaboratorio=jQuery('#<portlet:namespace />laboratorioMedicamento').val(); 
		var mediDroga=jQuery('#<portlet:namespace />drogaMedicamento').val(); 
		var mediRegistro=jQuery('#<portlet:namespace />nroregistroMedicamento').val(); 
		var mediCodBarra=jQuery('#<portlet:namespace />codigobarraMedicamento').val();
		var chk_manualdat =jQuery("#<portlet:namespace />manualdat").is(':checked');
		var chk_incluyebajas =jQuery("#<portlet:namespace />incluyebajas").is(':checked');
		
		window.location.href ='/xlsservlet/?reporte=REPORTE_RESULT_BUSQUEDA_MEDICAMENTOS_OSPIM'
		+ '&mediPeriodoYear='+mediPeriodoYear+'&mediPeriodoMes='+mediPeriodoMes+'&mediTroquel='+mediTroquel+'&manualDat='+chk_manualdat+'&mediNombre='+mediNombre+'&mediPresentacion='+mediPresentacion+'&mediLaboratorio='+mediLaboratorio+'&mediDroga='+mediDroga+'&mediRegistro='+mediRegistro+'&mediCodBarra='+mediCodBarra+'&incluyeBajas='+chk_incluyebajas; 
		
	});
	
	
</script>