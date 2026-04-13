<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server


		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysFarmaciaOspim.ROL_VADEMECUM_FARMACIA_OSPIM);
		PortletURL portletURL = renderResponse.createRenderURL();
		portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
		portletURL.setParameter("struts_action", "/farmaciaospim/view");
		
		Calendar fechaRegistro = CalendarFactoryUtil.getCalendar();
		fechaRegistro.setTime(new Date());
		Calendar current = CalendarFactoryUtil.getCalendar();
		BusquedaVademecumFiltro filtroVade = (BusquedaVademecumFiltro)request.getSession().getAttribute(WebKeysFarmaciaOspim.FILTRO_BUSQUEDA_VADEMECUM);
%>	

<form action="<%=portletURL%>" method="get"
	name="<portlet:namespace />fm"
	onSubmit="submitForm(this); return false;">
	<liferay-portlet:renderURLParams varImpl="portletURL" />
 
<fieldset class="block-labels">
	<legend>
			<liferay-ui:message key="vademecum" />
	</legend>	
 	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">		
		<tr>
			<td><label><liferay-ui:message key="registro" /> :</label></td>
			<td>			 
				<input id="<portlet:namespace />nroregistroMedicamento" name="<portlet:namespace />nroregistroMedicamento" size="7" maxlength="7" type="text" value="" onkeydown="allowOnlyDigits(event);" />
			</td>
			<td><label><liferay-ui:message key="troquel" /> :</label></td>
			<td>			 
				<input id="<portlet:namespace />nroTroquelMedicamento" name="<portlet:namespace />nroTroquel" size="7" maxlength="7" type="text" value=""  onkeydown="allowOnlyDigits(event);" />
			</td>
			<td>&nbsp;</td>			
			<td><label><liferay-ui:message key="nombre" /> :</label></td>
			<td>			 
				<input id="<portlet:namespace />nombreMedicamento" name="<portlet:namespace />nombreMedicamento" size="35" maxlength="25" type="text" value="" />
			</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="presentacion" />:</label></td>
			<td>			 
				<input id="<portlet:namespace />presentacionMedicamento" name="<portlet:namespace />presentacionMedicamento" size="35" maxlength="25" type="text" value="" />
			</td>
			<td colspan="3"><label>Solo Informados por la SSS :&nbsp;&nbsp;</label>
				<input type="checkbox"id="<portlet:namespace />informados_por_sss" name="<portlet:namespace />informados_por_sss" >
			</td>
			<td colspan="3"><label>Solo Nuevas Altas :&nbsp;&nbsp;</label>
				<input type="checkbox"id="<portlet:namespace />nuevas_altas" name="<portlet:namespace />nuevas_altas">
			</td>
		</tr>	
		<tr>			 
			<td><label><liferay-ui:message key="laboratorio" /> :</label></td>
			<td>			 
				<input id="<portlet:namespace />laboratorioMedicamento" name="<portlet:namespace />laboratorioMedicamento" size="35" maxlength="25" type="text" value="" />
			</td>
			<td><label><liferay-ui:message key="droga" /> :</label></td>
			<td>			 
				<input id="<portlet:namespace />drogaMedicamento" name="<portlet:namespace />drogaMedicamento" size="15" maxlength="20" type="text" value="" />
			</td>
			<td colspan="4">&nbsp;</td>						
		</tr>
		<tr>
			<td colspan="8">
				<table>
					<tr> 		
					    <td valign="top"><label>Tipo Padrón :</label></td>
						<td valign="top"><label>Pmi Madre&nbsp;&nbsp;</label>
							<input type="checkbox"id="<portlet:namespace />pmi_madre" name="<portlet:namespace />pmi_madre" onclick="desactivarAnticoncepcion();">
						</td>	
						<td valign="top"><label>Pmi Hijo&nbsp;&nbsp;</label>
							<input type="checkbox"id="<portlet:namespace />pmi_hijo" name="<portlet:namespace />pmi_hijo" onclick="desactivarAnticoncepcion();">
						</td>
						<td valign="top"><label>Anticoncepción&nbsp;&nbsp;</label>
							<input type="checkbox"id="<portlet:namespace />aco" name="<portlet:namespace />aco" onclick="checkExclusive(this);desactivarPmi();">
						</td>
						<td valign="top"><label>General&nbsp;&nbsp;</label>
							<input type="checkbox"id="<portlet:namespace />pmi_gral" title='Gral'  name="<portlet:namespace />pmi_gral">
						</td>
						<td valign="top"><label>Padrón Molineros&nbsp;&nbsp;</label>
							<input type="checkbox"id="<portlet:namespace />padron_molineros" name="<portlet:namespace />padron_molineros">
						</td>
					</tr>
				</table>	
			</td>		
		</tr>				
	</table>
</fieldset>
	<table>
		<tr>
			<td colspan="1"><label style="height:240px; color: red;" id="<portlet:namespace/>mensajeBusquedaHistorico"></label></td>
		</tr>
	</table>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		<tr>
			<td colspan="1"><input id="<portlet:namespace />buscar"
				value="<liferay-ui:message key="buscar"/>"
				title="<liferay-ui:message key="buscar-medicamento" />"
				type="button" /></td>
			<% if (showABMButtons) {%>	
				<td>
					<input type="button"
						value="<liferay-ui:message key="new"/>"
						title="<liferay-ui:message key="nuevo-vademecum" />"
						onClick="<portlet:namespace />altaVademecumOspim();" />
				</td>
			<%} %>	
			<td colspan="1"><input id="<portlet:namespace />buscar_en_historico"
				value="<liferay-ui:message key="buscar-historico-vademecum"/>"
				title="<liferay-ui:message key="buscar-historico-vademecum" />"
				type="button" /></td>
			<td colspan="1"><label>Exporta Padrón:</label></td>
			<td>
				<select id="listaPadrones">
	 				<option value="padron 1">PMI Madre</option>
	 				<option value="padron 2">PMI Hijo</option>
					<option value="padron 3">Anticoncepción</option>
	 				<option value="padron 4">General</option>
	 				<option value="padron 5">Molineros</option>
				</select>
			</td>
			<td colspan="1"><input id="<portlet:namespace />exportar"
				value="Exportación Vademecum."
				title="Exportación Vademecum."
				type="button" /></td>
			
		</tr>	
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
<input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>"  	value="" />
<input type="hidden" name="<portlet:namespace />vademecum_historico" id="<portlet:namespace />vademecum_historico" value=0 />
<input type="hidden" name="pagina" id="pagina" value="x" />

<script type="text/javascript">
	
    jQuery("#<portlet:namespace />fechaArchivoDia").hide();
	jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/farmaciaospim/buscar_vademecum_registros_sesion';
	jQuery('#<portlet:namespace />busquedaMedicamentosOspim').load(url); 
	jQuery('#<portlet:namespace />buscando').hide();
	jQuery('#<portlet:namespace />buscar').click(function(){<portlet:namespace />busquedaVademecumHistorico(false);});
	jQuery('#<portlet:namespace />exportar').click(function(){<portlet:namespace />exportacionPadronSeleccionado();});
	
	
	
	jQuery('#<portlet:namespace />buscar_en_historico').click(function(){<portlet:namespace />busquedaVademecumHistorico(true);});
	
	seteaValorLabel(<%=filtroVade!=null?filtroVade.isBuscaEnHistoricoDeVademecum():false%>); 
	
	
	
	function <portlet:namespace />busquedaVademecumHistorico(valor){
		seteaValorLabel(valor);
		<portlet:namespace />busquedaVademecum(0);
	}
	
	function seteaValorLabel(valor){
		jQuery("#<portlet:namespace/>mensajeBusquedaHistorico").html("");
		jQuery("#<portlet:namespace />vademecum_historico").val(valor);
		if (valor){
			jQuery("#<portlet:namespace/>mensajeBusquedaHistorico").html("	BUSQUEDA EN HISTORICO");
		}
	}
		
	function <portlet:namespace />busquedaVademecum(nroPagina){
		jQuery('#<portlet:namespace />buscando').show();   
		var pagina_sel=0;
		if (nroPagina!=0) {
			pagina_sel =jQuery("#<portlet:namespace/>pagina_sel").val();	
		}
		
		jQuery("#pagina").val(pagina_sel);
		var mediTroquel=jQuery('#<portlet:namespace />nroTroquelMedicamento').val();
		var mediNombre=jQuery('#<portlet:namespace />nombreMedicamento').val(); 
		var mediPresentacion=jQuery('#<portlet:namespace />presentacionMedicamento').val(); 
		var mediLaboratorio=jQuery('#<portlet:namespace />laboratorioMedicamento').val(); 
		var mediDroga=jQuery('#<portlet:namespace />drogaMedicamento').val(); 
		var mediRegistro=jQuery('#<portlet:namespace />nroregistroMedicamento').val();		
		var chk_pmihijo =jQuery("#<portlet:namespace />pmi_hijo").is(':checked');
		var chk_pmimadre  =jQuery("#<portlet:namespace />pmi_madre").is(':checked');
		var chk_anticoncepcion   =jQuery("#<portlet:namespace />aco").is(':checked');
		var chk_general  =jQuery("#<portlet:namespace />pmi_gral").is(':checked');
		var todos_los_tipos = !(chk_pmihijo || chk_pmimadre || chk_anticoncepcion || chk_general);		
		var chk_soloInformadosSuper =jQuery("#<portlet:namespace />informados_por_sss").is(':checked');
		var buscaEnHistorico =jQuery('#<portlet:namespace />vademecum_historico').val();
		var chk_soloNuevasAltas =jQuery("#<portlet:namespace />nuevas_altas").is(':checked');
		var chk_molineros  =jQuery("#<portlet:namespace />padron_molineros").is(':checked');
		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/farmaciaospim/buscar_vademecum&mediTroquel='+mediTroquel+		
		'&mediNombre='+encodeURI(mediNombre)+'&mediPresentacion='+encodeURI(mediPresentacion)+'&mediLaboratorio='+encodeURI(mediLaboratorio)+
		'&mediDroga='+encodeURI(mediDroga)+'&mediRegistro='+mediRegistro+'&pagina_sel='+pagina_sel+'&todosLosTipos='+todos_los_tipos+'&soloInformadosxSuper='+chk_soloInformadosSuper+
		'&pmiHijo='+chk_pmihijo+'&pmiMadre='+chk_pmimadre+'&aco='+chk_anticoncepcion+'&gral='+chk_general+'&buscaEnHistorico='+buscaEnHistorico+'&soloNuevasAltas='+chk_soloNuevasAltas+'&molineros='+chk_molineros;
				
        jQuery('#<portlet:namespace />busquedaMedicamentosOspim').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	}
	
	function <portlet:namespace />altaVademecumOspim () {		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/farmaciaospim/editar_borrar_vademecum_entry" /></portlet:renderURL>';		
		document.<portlet:namespace />fm.method = 'post';
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');
		submitForm(document.<portlet:namespace />fm, url);
	}	
    
	function checkExclusive(obj){
    	var that = obj;
    	if (document.getElementById(that.id).checked){
    		document.getElementById('<portlet:namespace />pmi_madre').checked = false;
    		document.getElementById('<portlet:namespace />pmi_hijo').checked = false;
    		document.getElementById('<portlet:namespace />aco').checked = false;
    		document.getElementById(that.id).checked = true;
    	}else {
    		document.getElementById(that.id).checked = false;
    	}	
	}
     
    function desactivarAnticoncepcion() {
    	if ( document.getElementById("<portlet:namespace />pmi_hijo").checked || document.getElementById("<portlet:namespace />pmi_madre").checked ) {
    		document.getElementById('<portlet:namespace />aco').disabled = true ;
    	}else{
    		document.getElementById('<portlet:namespace />aco').disabled = false;	
    	}
    }
    function desactivarPmi() {
    		document.getElementById('<portlet:namespace />pmi_madre').disabled = document.getElementById("<portlet:namespace />aco").checked;
    		document.getElementById('<portlet:namespace />pmi_hijo').disabled = document.getElementById("<portlet:namespace />aco").checked;	
    }
	
	function <portlet:namespace />exportacionPadronSeleccionado() {
		var comboPadrones = [false,false,false,false,false];
		comboPadrones [document.getElementById("listaPadrones").selectedIndex] = true ;
		var todos_los_tipos = !( comboPadrones[0] || comboPadrones[1] || comboPadrones[2] || comboPadrones[3] || comboPadrones[4]);
		
		window.location.href ='/xlsservlet/?reporte=EXPORTACION_PADRON_VADEMECUM'
							+'&todosLosPadrones='+todos_los_tipos+'&pmiHijo='+comboPadrones[1]+'&pmiMadre='+comboPadrones[0]+
							'&molineros='+comboPadrones[4]+'&anticonceptivo='+comboPadrones[2]+'&vadeGral='+comboPadrones[3];
			
	}

		
		
</script>