<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/equipo_interdisciplinario/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
	response.setHeader("Cache-Control", "no-store"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", 0); //prevents caching at the proxy server
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_EQUIPO_INTERDISCIPLINARIO);
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/autorizaciones/view");	
	//verificar los calendars
	Calendar fechaRegistro = CalendarFactoryUtil.getCalendar();
	fechaRegistro.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
	
%>

<form action="<%=portletURL%>" method="get"
	name="<portlet:namespace />fm"
	onSubmit="submitForm(this); return false;"><liferay-portlet:renderURLParams
	varImpl="portletURL" /> <%
 	boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ENTIDAD_OSPIM);
 	boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ENTIDAD_AMTIMA);
 	boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ENTIDAD_UOMA);
 %> 
	<fieldset class="block-labels">
	<legend> <liferay-ui:message key="Datos Registro de Equipo Interdisciplinario" /> 
	</legend>

<table>		
			<tr>
	<td colspan="12">&nbsp;</td>
	</tr>
		<tr>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td> 			<label>Nro Registro:&nbsp;&nbsp;</label> 			 </td>
			<td>			 
			 <input id="<portlet:namespace />nroReclamoEquipoInter" name="<portlet:namespace />nroReclamoEquipoInter" size="10" maxlength="10" type="text" value="" />
			 </td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>			
			<td> 			<label><liferay-ui:message key="fecha-reg-equipointerdisciplinario" />:</label>			</td>
			<td colspan="12">
			 <liferay-ui:input-date dayParam="fechaRegEquiDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaRegEquiMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaRegEquiAnio"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaRegistro.get(Calendar.YEAR)  -1%>"
			yearRangeEnd="<%= fechaRegistro.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaRegistro.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" />			  
			</td>
			<td colspan="12">&nbsp;</td>
			<td colspan="12">&nbsp;</td>
			<td>&nbsp;&nbsp;&nbsp;&nbsp;<label><liferay-ui:message key="Estado" />:</label> </td>
			<td colspan="12">			
			<select name="<portlet:namespace/>estado"
						id="<portlet:namespace />estado" > 
						<option value="SELECCIONE">SELECCIONE</option>
						<option value="CARGADO">CARGADO</option>
						<option value="CERRADO">CERRADO</option>
					</select>
			 </td>
			 <td>&nbsp;&nbsp;&nbsp;&nbsp;			  
			 <label id="<portlet:namespace />motivolabel" name="<portlet:namespace/>motivolabel" ><liferay-ui:message key="Motivo" /> :</label>
			 </td>
			<td colspan="12">			
			<select name="<portlet:namespace />motivo"
						id="<portlet:namespace />motivo" > 
						<option value="TODOS">TODOS</option>
						<option value="AUTORIZADO">AUTORIZADO</option>
						<option value="EXCEPCION">EXCEPCION</option>
						<option value="RECHAZADO">RECHAZADO</option>
					</select>
			 </td>
		</tr>
		</table>
</fieldset>
	
<table align="center">
	<tr>
	<td colspan="12">&nbsp;</td>
	</tr>	
	<tr>
		<td colspan="12">
		<fieldset class="block-labels"><legend><liferay-ui:message
			key="datos-afiliado" /></legend> <liferay-util:include
			page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>
			<liferay-util:param value="<%=String.valueOf(true)%>"
				name="edit_mode" />
			<liferay-util:param value="<%=String.valueOf(true)%>"
				name="discapacidad" />
			<liferay-util:param name="pag_reintegro" value='1' />				
		</liferay-util:include></fieldset>
		</td>
	</tr>
</table>

<div id="<portlet:namespace />busqueda_prestaciones">
       <label>B&uacute;squeda de Prestaciones Discapacidad</label>
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">	
			<tr>
		    <td><label><liferay-ui:message key="codigo-presentado"/>:</label></td>
				<td><input id="<portlet:namespace />codigoSeguimiento_filtro" name="<portlet:namespace />codigoSeguimiento_filtro" size="10" maxlength="20" type="text" value=''/></td>
				<td><input id="<portlet:namespace />descripcionSeguimiento_filtro" name="<portlet:namespace />descripcionSeguimiento_filtro" size="80" maxlength="200" type="text" value=''					
				/></td>
				<td><div id="<portlet:namespace />divBtnBusca">
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar();" tabindex="-1">Buscar</a>
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();" tabindex="-1">Limpiar</a>
				</div> </td>
			</tr>
		</table>		
  </div>
	    
<table>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"  ><input id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar-registros-equipo-interdisciplinario"/>"
			title="<liferay-ui:message key="buscar-registros-equipo-interdisciplinario" />" type="button"
			/> </td>
		<td colspan="12">&nbsp;&nbsp;&nbsp;</td>	
		<td colspan="2"  >

			<input type="button" value="<liferay-ui:message key="nuevo-registro-equipo-interdisciplinario"/>"
				title="<liferay-ui:message key="nuevo-registro-equipo-interdisciplinario" />" 
				onClick="<portlet:namespace />altaRegistroEquipoInterdisciplinario();" />				
				</td>
				
		
		<td colspan="2"  >
		<c:if test="<%=showABMButtons%>">		
		<input id="<portlet:namespace />exportar-busqueda" value="<liferay-ui:message key="exportar-busqueda"/>" 
		title="<liferay-ui:message key="exportar-busqueda" />" type="button" onClick="javascript:exportarBusqueda();"/>
		</c:if></td>
				
				
	</tr>
	<tr>
			<td colspan="12">&nbsp;</td>
		</tr>
</table>
</fieldset>


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
<div align="center" id="<portlet:namespace />busquedaRegistrosEquInter">
</div>
</fieldset>
</form>
<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />
<input id="<portlet:namespace />nom_seleccionado" name="<portlet:namespace />nom_seleccionado" type="hidden" value=""/>

<input id="<portlet:namespace />codigogestion" name="<portlet:namespace />codigogestion" type="hidden" value=""/>


<script type="text/javascript">
  
    jQuery("#<portlet:namespace />busqueda_prestaciones").hide();
    jQuery('#<portlet:namespace />buscando').hide();	
    jQuery('#<portlet:namespace />buscando').show();

    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_equipos_interdisciplinarios_registros_sesion';
	jQuery('#<portlet:namespace />busquedaRegistrosEquInter').load(url);
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){<portlet:namespace />busquedaRegistrosEquipoInterdisciplinario();});
	 
	jQuery("#<portlet:namespace/>estado").click(function() {	validaEstado(); } );
	
	// oculta motivo cierre 
	jQuery('#<portlet:namespace />motivolabel').hide();
	jQuery('#<portlet:namespace />motivo').hide();
	
//TODO A�ADIR CAMPOS NUEVOS
	function <portlet:namespace />busquedaRegistrosEquipoInterdisciplinario(){
	    
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var fechaDia=jQuery('#<portlet:namespace />fechaRegEquiDia').val();
		var fechaMes=jQuery('#<portlet:namespace />fechaRegEquiMes').val();
		var fechaAnio=jQuery('#<portlet:namespace />fechaRegEquiAnio').val();		
		var estado;
		var nroRegistro =jQuery('#<portlet:namespace />nroReclamoEquipoInter').val();
		var motivo=document.getElementById("<portlet:namespace />motivo").value
		
		var estadosel   =document.getElementById("<portlet:namespace/>estado");
		
		estado= document.getElementById("<portlet:namespace/>estado").value
		
		
		if (estadosel.selectedIndex==0){ // SELECCIONE
			estado="";
		}

		jQuery('#<portlet:namespace />buscando').show();
	
		
		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_equipos_interdisciplinario&cuil='+cuil+		
		'&inte='+inte+'&fechaDia='+fechaDia+'&fechaMes='+fechaMes+'&fechaAnio='+fechaAnio+'&estado='+estado+'&motivo='+motivo+'&nroRegistro='+nroRegistro;     
		
        jQuery('#<portlet:namespace />busquedaRegistrosEquInter').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
        
    	
	}
	
	
	function <portlet:namespace />altaRegistroEquipoInterdisciplinario() {
		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_registro_equipointerdisciplanrio_entry" /></portlet:renderURL>';		
		document.<portlet:namespace />fm.method = 'post';
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	
	function manejarTipoPrestacion ()
	{
	var tipoSelect  =document.getElementById("<portlet:namespace />tipoprestacion");
     
	jQuery("#<portlet:namespace />busqueda_prestaciones").hide();
	jQuery("#<portlet:namespace />busqueda_farmacia").hide();
	
	   				if ( tipoSelect.selectedIndex==1 )
	  				        {  					       					      
	   							jQuery("#<portlet:namespace />busqueda_farmacia").show();
	   				        }
	   				if ( tipoSelect.selectedIndex==2 )
				            {  	       
							     jQuery("#<portlet:namespace />busqueda_prestaciones").show();
 				            }	  
	   				
	   				jQuery('#<portlet:namespace />troquel').val(""); // farmacia
	   			    jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val("");// prestaciones medicas
	   			    
	}


	
	
	function <portlet:namespace />buscarNomencladorAutocompletar(){
		var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val();
		var codigo_nomenclador=jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val();

		 
		
		if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
	        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
	    }else {
	    
	    	if(popupMD==null)
	    		popupMD = Liferay.Popup({title:"B�squeda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
	    
		    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
		    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&codigonomenclador='+encodeURI(codigo_nomenclador);
			jQuery(popupMD).load(url);
	    }
		
	}

	function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
		
		jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val(codigo);
		jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val(descripcion);
		jQuery("#<portlet:namespace />nom_seleccionado").val("1"); // selecciona el tipo de nomenclador	 
		jQuery('#<portlet:namespace />tipoNomenclador').val(tipoNomenclador);
		
	}

	function pasarParametrosAParentNm(tipoNomenclador,codigo,descripcion) {	
		seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
	    <portlet:namespace />cerrarNm();
	}

	function <portlet:namespace />cerrarDivNm(){
		jQuery("#divSeguimientoSur").hide("slow");
	}

	function <portlet:namespace />cerrarNm(){
		<portlet:namespace />cerrarDivNm();
		if(popupMD){
			Liferay.Popup.close(popupMD);
		}
	}
	
	function <portlet:namespace />limpiarNomencladorAutocompletar(){	
		jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val('');
		jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val('');
	}

	
jQuery('#<portlet:namespace />exportar-busqueda').click(function exportarBusqueda(){
		
		
	var cuil=jQuery('#<portlet:namespace />cuil').val();
	var inte=jQuery('#<portlet:namespace />inte').val();
	var fechaDia=jQuery('#<portlet:namespace />fechaRegEquiDia').val();
	var fechaMes=jQuery('#<portlet:namespace />fechaRegEquiMes').val();
	var fechaAnio=jQuery('#<portlet:namespace />fechaRegEquiAnio').val();
	var nroRegistro =jQuery('#<portlet:namespace />nroReclamoEquipoInter').val();
	var motivo=document.getElementById("<portlet:namespace />motivo").value	
	var estadosel   =document.getElementById("<portlet:namespace/>estado");	
	var estado= document.getElementById("<portlet:namespace/>estado").value
	
	if (estadosel.selectedIndex==0){ // SELECCIONE
		estado="";
	}     
	if(trim(cuil).length != 0 && !validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
			alert("Cuil inv�lido");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
	}
	
		window.location.href ='/xlsservlet/?reporte=REPORTE_RESULT_BUSQUEDA_EQUIPOS_INTERDISCIPLINARIOS'
		+'&cuil_titular='+cuil+
		'&inte='+inte+
		'&fechaDia='+fechaDia+'&fechaMes='+fechaMes+'&fechaAnio='+fechaAnio+
		'&estado='+estado+'&nroRegistro='+nroRegistro+
		'&motivo='+motivo;    
		
	
	});

	
	
	function validaEstado() {	
		valor=jQuery("#<portlet:namespace/>estado").val();
		if (valor=='CERRADO'){
			jQuery('#<portlet:namespace />motivolabel').show();
			jQuery('#<portlet:namespace />motivo').show();
			jQuery('#<portlet:namespace />motivo').val('TODOS');
		}else{
			jQuery('#<portlet:namespace />motivolabel').hide();
			jQuery('#<portlet:namespace />motivo').hide();		
		}		
	}

	
	
	
</script>