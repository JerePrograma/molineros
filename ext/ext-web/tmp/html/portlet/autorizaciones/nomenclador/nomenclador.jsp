<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/autorizaciones/view");
	List<TipoNomenclador> tipoNomencladorList=TraeListasServiceUtil.getTiposNomenclador();
	
	List<Especialidad> especialidadesList=TraeListasServiceUtil.getEspecialidadesNomenclador();
	
	//verificar los calendars
	Calendar fechaReceta = CalendarFactoryUtil.getCalendar();
	fechaReceta.setTime(new Date()); 
	Calendar current = CalendarFactoryUtil.getCalendar();
	String usuario_modi = user.getScreenName();
	
	
	boolean popup=ParamUtil.getBoolean(request, "popup", false);
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
		<legend><liferay-ui:message key="nomenclador" /></legend>
		<table class="lfr-table">	
			<tr>
			    <td>
					<liferay-ui:message key="tipo-nomenclador" />
				</td>
				<td>
					<select name="<portlet:namespace/>tipoNomencladorfiltro" id="<portlet:namespace/>tipoNomencladorfiltro">
						<option value="0">Seleccione un nomenclador</option>
						<%	for (TipoNomenclador tnom : tipoNomencladorList) { %>
								<option value="<%= tnom.getId_tipo_nomenclador()%>"><%=tnom.getDescripcion()%></option>
						<%	} %>
					</select>
				</td>
			    
			    <td><label><liferay-ui:message key="codigo"/>:</label></td>
				<td><input id="<portlet:namespace />codigoNomencladorfiltro" name="<portlet:namespace />codigoNomencladorfiltro" size="10" maxlength="10" type="text" value=''/></td>
			
				<td><label><liferay-ui:message key="descripcion"/>:</label></td>
				
				<td><input id="<portlet:namespace />descripcionNomencladorfiltro" name="<portlet:namespace />descripcionNomencladorfiltro" size="70" maxlength="200" type="text" value=''
				onKeyUp="javascript:<portlet:namespace />buscarNomencladorOnDiv(event)"	
				/></td>
				
				<td><div id="<portlet:namespace />divBtnBuscaNomenclador">
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar();" tabindex="-1">Buscar</a>
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();" tabindex="-1">Limpiar</a>
					</div> </td>
			</tr>
			<tr><td>&nbsp;</td></tr>	
				<tr>
				<td>
					<liferay-ui:message key="especialidad" />
				</td>
				<td colspan="7">
					<select name="<portlet:namespace/>especialidadfiltro" id="<portlet:namespace/>especialidadfiltro" >
						<option value="0">Seleccione una especialidad</option>
						<%	for (Especialidad tnom : especialidadesList) { %>
								<option value="<%= tnom.getId_especialidad()%>"><%=tnom.getDescripcion()%></option>
						<%	} %>
					</select>
				</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
				   <td><label><liferay-ui:message key="resolucion"/>:</label></td>
				   <td><input id="<portlet:namespace />resolucionNomencladorfiltro" name="<portlet:namespace />resolucionNomencladorfiltro" size="20" maxlength="15" type="text" value=''/></td>
				   <td colspan="10">Recupera SUR: <input type="checkbox"  name="<portlet:namespace />recuperaSURfiltro" 
							 id="<portlet:namespace />recuperaSURfiltro"></td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr></tr>
		</table>
		<table>
				 <tr align="right">
				    <td>&nbsp;</td>
					<td align="right" width="100%">						
						<input id="<portlet:namespace />buscar"
						value="<liferay-ui:message key="buscar"/>"
						title="<liferay-ui:message key="buscar" />"
						onClick="javascript: <portlet:namespace />buscarNomenclador();"
						type="button" />
						<input type="button" value="Nuevo" onClick="<portlet:namespace />nuevoNomenclador();" />
						<input type="button" value="Limpiar" onClick="<portlet:namespace />initDateFields();" />&nbsp;
					</td>
				 </tr>
		</table>
		<div id='divNomenclador' style="float:left;">
		</div>
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
		<div id="<portlet:namespace />listado_nomenclador">
		<jsp:include page='/html/portlet/autorizaciones/nomenclador/nomenclador_result.jsp' />  
		</div>
	</fieldset>
	<input id="<portlet:namespace />nom_seleccionado" name="<portlet:namespace />nom_seleccionado" type="hidden" value=""/>
</form>		

<script type="text/javascript">
	
	jQuery('#<portlet:namespace />buscando').hide();		
	var autorizacionGenerada;
	var popupMD;
	
	function <portlet:namespace />buscarNomenclador(){
		var tipoNomenclador=jQuery('#<portlet:namespace />tipoNomencladorfiltro').val();
		var descripcionNomenclador=jQuery('#<portlet:namespace />descripcionNomencladorfiltro').val();
		var especialidad=jQuery('#<portlet:namespace />especialidadfiltro').val();
		var codigoNomenclador=jQuery('#<portlet:namespace />codigoNomencladorfiltro').val();
		var recuperaSUR=jQuery("#<portlet:namespace/>recuperaSURfiltro").is(':checked');
		var resolucionNomenclador=jQuery('#<portlet:namespace />resolucionNomencladorfiltro').val();
		
		jQuery('#<portlet:namespace />buscando').show();
	 	var busquedaNom = {"tipoNomenclador":tipoNomenclador,"descripcionNomenclador":descripcionNomenclador,"especialidad":especialidad,
	 			"codigoNomenclador":codigoNomenclador,"recuperaSUR":recuperaSUR,"resolucionNomenclador":resolucionNomenclador};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/buscarNomenclador" /></portlet:renderURL>';
		jQuery('#<portlet:namespace />listado_nomenclador').load(url,busquedaNom, function(){
															jQuery('#<portlet:namespace />buscando').hide();      
														  });	
	}
	
	<portlet:namespace />initDateFields();

	function <portlet:namespace />initDateFields(){
		jQuery('#<portlet:namespace />tipoNomencladorfiltro').val("");
		jQuery('#<portlet:namespace />descripcionNomencladorfiltro').val("");
		jQuery('#<portlet:namespace />especialidadfiltro').val("");
		jQuery('#<portlet:namespace />codigoNomencladorfiltro').val("");
//		jQuery('#<portlet:namespace />recuperaSURfiltro').val("");
		jQuery('#<portlet:namespace />resolucionNomencladorfiltro').val("");
		document.getElementById('<portlet:namespace />recuperaSURfiltro').checked=false; 
	}
	
	function <portlet:namespace />nuevoNomenclador() {
		<%-- var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
		params+="&accion=edit";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_nomenclador" /></portlet:renderURL>';
		url = url + params; --%>
		
		var cmd_ = '<%= Constants.WRITE %>';
		var accion = '<%= Constants.EDIT %>';
		var xportletUrl = '/autorizaciones/editar_nomenclador';
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="cmd" value="__cmd"/>'+
		'<liferay-portlet:param name="accion" value="__accion"/>'+
	    '</liferay-portlet:renderURL>';

	    url = url.replace("__xportletUrl",xportletUrl); 
	    url = url.replace("__cmd",cmd_);
	    url = url.replace("__accion",accion);
		
		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function <portlet:namespace />buscarNomencladorAutocompletar(){
		var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionNomencladorfiltro").val();
		var codigo_nomenclador=jQuery("#<portlet:namespace />codigoNomencladorfiltro").val();
		if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
	        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
	    }else {
	    	if(popupMD==null)
	    		popupMD = Liferay.Popup({title:"B�squeda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
		    <%-- var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
		    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&popup=autocompletar'+'&codigonomenclador='+encodeURI(codigo_nomenclador); --%>
			
			var xportletUrl = '/autorizaciones/buscar_nomenclador';
			
			var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="descripcionnomenclador" value="__descripcionnomenclador"/>'+
			'<liferay-portlet:param name="popup" value="autocompletar"/>'+
			'<liferay-portlet:param name="codigonomenclador" value="__codigonomenclador"/>'+
		    '</liferay-portlet:renderURL>';

		    url = url.replace("__xportletUrl",xportletUrl); 
		    url = url.replace("__descripcionnomenclador",encodeURI(nombre_nomenclador));
		    url = url.replace("__codigonomenclador",encodeURI(codigo_nomenclador));
		    
		    jQuery(popupMD).load(url);
	    }
	}
	
	function pasarParametrosAParentNm(tipoNomenclador,codigo,descripcion) {	
		seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
	    <portlet:namespace />cerrarNm();
	}
	
	function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
		jQuery('#<portlet:namespace />tipoNomencladorfiltro').val(tipoNomenclador);
		jQuery('#<portlet:namespace />codigoNomencladorfiltro').val(codigo);
		jQuery("#<portlet:namespace />descripcionNomencladorfiltro").val(descripcion);
		jQuery("#<portlet:namespace />nom_seleccionado").val("1");
		
//	    jQuery("#<portlet:namespace />divBtnBuscaNomenclador").hide();
	}
	
	function <portlet:namespace />cerrarDivNm(){
		jQuery("#divNomenclador").hide("slow");
	}

	function <portlet:namespace />cerrarNm(){
		<portlet:namespace />cerrarDivNm();
		if(popupMD){
			Liferay.Popup.close(popupMD);
		}
	}
	
	
	function <portlet:namespace />buscarNomencladorOnDiv(e){
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
				
		if(jQuery("#<portlet:namespace />nom_seleccionado").val() == "1" && (keyPressed==8 || keyPressed==46)){
			jQuery('#<portlet:namespace />tipoNomencladorfiltro').val("");
			jQuery('#<portlet:namespace />codigoNomencladorfiltro').val("");
			jQuery("#<portlet:namespace />nom_seleccionado").val("");
			return false;
		}
		
	    var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionNomencladorfiltro").val();
	    if (nombre_nomenclador==null){
	    	nombre_nomenclador = '';
	    }    
	    if(jQuery("#<portlet:namespace />nom_seleccionado").val() != "1" && nombre_nomenclador.length>=6 ){
	    	if(popupMD==null)
	    	    popupMD = Liferay.Popup({title:"B�squeda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
	    	
	    	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
		    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&popup=autocompletar';
			jQuery(popupMD).load(url);
			
	    }else{
//	    	jQuery("#divNomenclador").hide("slow");
	    }
	}
	
	function <portlet:namespace />pierdeFocoNm(){
		var seleccionada=jQuery("#<portlet:namespace />nom_seleccionada").val();	
		if(seleccionada=="1"){
			<portlet:namespace />cerrarDivNm();
			return false;
		}else{
			return false;
		}
	}
	
	function <portlet:namespace />limpiarNomencladorAutocompletar(){
		jQuery("#<portlet:namespace />descripcionNomencladorfiltro").val('');
		jQuery("#<portlet:namespace />codigoNomencladorfiltro").val('');
	}
	
</script>

