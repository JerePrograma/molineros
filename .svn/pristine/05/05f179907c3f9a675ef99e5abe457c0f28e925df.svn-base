<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<liferay-ui:error exception="<%= DuplicateActaIdException.class %>" message="acta-duplicada" />
<liferay-ui:error exception="<%= ar.com.ospim.tesoreria.FaltaFechaCierreActaException.class %>" message="falta-fecha-cierre" />
<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="acta-menor-fecha-contable" />
<portlet:defineObjects/>

<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_ABM_ESTUDIO_ISIDRO);
 		Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
		fechaHoy.setTime(new Date());
		String sufi="segui_";
		LlamadosEstudio llest=(LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
		
		Empresa empresa=llest.getEmpresa();
		
		List<EstadoGestion> estadosGestion = (ArrayList<EstadoGestion>) request.getSession().getAttribute(WebKeysEstudioIsidro.ESTADOS_GESTION);
		
		String portlet_name = ParamUtil.getString(request, "portlet_name");

		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "empresas";
		}
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
		}
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}
		if(renderResponse.getNamespace().equals("_AFI_1_")){
			portlet_name = "afiliados";
		}
		if(renderResponse.getNamespace().equals("_CGT_1_")){
			portlet_name = "cgt";
		}
		if(renderResponse.getNamespace().equals("_EST_1_")){
			portlet_name = "estudio_isidro";
		}
		
		if(renderResponse.getNamespace().equals("_EMP_1_")){
			portlet_name = "empresas";
		}
		
%>
<div id="allPage">
	<div align="center" id="<portlet:namespace />nuevoLlamado">			
											
			<liferay-util:include page="/html/portlet/empresas/editar_empleadores_entry.jsp">	
				<liferay-util:param name="esEdicion" value="false" />							
			</liferay-util:include>						
								
			
	</div>	
	<div align="center" id="<portlet:namespace />busquedaLlamados">		
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/llamados_search_result.jsp">
						<liferay-util:param name="esEditable" value='true'/>						  		
				</liferay-util:include>							
	</div>
	<div align="center" id="<portlet:namespace />buscandoCalculoDiv">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
	</div>
	<div align="center" id="<portlet:namespace />busquedaCalculoDiv">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/deuda_search_result.jsp">
						<liferay-util:param name="esEditable" value='true'/>												  		
				</liferay-util:include>				
	</div>
	
	<div align="center" id="<portlet:namespace />buscandoActaDiv">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
	</div>
	<div align="center" id="<portlet:namespace />busquedaActaDiv">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/actas_acuerdo_search_result_seguimiento.jsp">
						<liferay-util:param name="esEditable" value='true'/>						  		
				</liferay-util:include>
	</div>
	<div align="center" id="<portlet:namespace />buscandoRecibosDiv">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
	</div>
	<div align="center" id="<portlet:namespace />busquedaRecibosDiv">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/recibos_search_result_seguimiento.jsp">
						<liferay-util:param name="esEditable" value='true'/>						  		
				</liferay-util:include>
	</div>
	
	<div align="center" id="<portlet:namespace />buscandoChequesDiv">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
	</div>
	<div align="center" id="<portlet:namespace />busquedaChequesDiv">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/cheques_seguimiento_search_result.jsp">
						<liferay-util:param name="esEditable" value='true'/>						  		
				</liferay-util:include>
	</div>
	
	<div align="center" id="<portlet:namespace />buscandoReporte">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
	</div>
	<div align="center" id="<portlet:namespace />reporteEntidadRemu">	
	</div>		
	
</div>
<script type="text/javascript">

	jQuery('#<portlet:namespace />buscandoReporte').hide();	
	jQuery('#<portlet:namespace />buscandoRecibos').hide();
	jQuery('#<portlet:namespace />buscandoCalculoDiv').hide();	
	jQuery('#<portlet:namespace />buscandoActaDiv').hide();
	jQuery('#<portlet:namespace />buscandoChequesDiv').hide();
	jQuery('#<portlet:namespace />buscandoRecibosDiv').hide();
	<%if(portlet_name.equals("estudio_isidro")){%>	
		jQuery('#<portlet:namespace />busquedaLlamados').css('display','none');	
		jQuery('#<portlet:namespace />busquedaCalculoDiv').css('display','none');		
		jQuery('#<portlet:namespace />busquedaActaDiv').css('display','none');
		jQuery('#<portlet:namespace />busquedaChequesDiv').css('display','none');
		jQuery('#<portlet:namespace />busquedaRecibosDiv').css('display','none');
	<%}%>
	function <portlet:namespace />showHideDivGestiones(){		
		if (jQuery("#<portlet:namespace />busquedaLlamados").css('display') === 'none') {
			jQuery('#<portlet:namespace />busquedaLlamados').css('display','block');
			jQuery('#<portlet:namespace />busquedaLlamados').css('height','240px');
			jQuery('#<portlet:namespace />busquedaLlamados').css('overflow','scroll');		
			jQuery('#<portlet:namespace />arrow_gestiones').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
		}else{
			jQuery('#<portlet:namespace />busquedaLlamados').css('display','none');
			jQuery('#<portlet:namespace />arrow_gestiones').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
		}
	}
	
	function <portlet:namespace />showHideDivCalculoDeuda(){		
		if (jQuery("#<portlet:namespace />busquedaCalculoDiv").css('display') === 'none') {
			jQuery('#<portlet:namespace />buscandoCalculoDiv').show();
			buscarCalculosDeuda();			
			jQuery('#<portlet:namespace />arrow_calculos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
		}else{
			jQuery('#<portlet:namespace />busquedaCalculoDiv').css('display','none');
			jQuery('#<portlet:namespace />arrow_calculos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
		}
	}
	function <portlet:namespace />showHideDivActa(tipo, entidad){		
		if (jQuery("#<portlet:namespace />busquedaActaDiv").css('display') === 'none' || entidad!=0) {
			jQuery('#<portlet:namespace />busquedaActaDiv').show();		
			buscarActas(tipo, entidad);					
			jQuery('#<portlet:namespace />arrow_actas_acuerdos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
		}else{
			jQuery('#<portlet:namespace />busquedaActaDiv').css('display','none');
			jQuery('#<portlet:namespace />arrow_actas_acuerdos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
		}
	}
	
	function <portlet:namespace />showHideDivCheques(){
		if (jQuery("#<portlet:namespace />busquedaChequesDiv").css('display') === 'none') {
			jQuery('#<portlet:namespace />busquedaChequesDiv').show();		
			buscarCheques();					
			jQuery('#<portlet:namespace />arrow_cheques').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
		}else{
			jQuery('#<portlet:namespace />busquedaChequesDiv').css('display','none');
			jQuery('#<portlet:namespace />arrow_cheques').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
		}
	}
	
	function <portlet:namespace />showHideDivRecibos(){
		if (jQuery("#<portlet:namespace />busquedaRecibosDiv").css('display') === 'none') {
			jQuery('#<portlet:namespace />busquedaRecibosDiv').show();		
			buscarRecibos();
			jQuery('#<portlet:namespace />arrow_recibos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
		}else{
			jQuery('#<portlet:namespace />busquedaRecibosDiv').css('display','none');
			jQuery('#<portlet:namespace />arrow_recibos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
		}
	}
	
	function cambiaCuit<%=sufi%>(){
	}
	
	/* function buscarEmpresaEstado(cuit, estado){		
		jQuery('#estado').val(estado);
		buscarEmpresa(cuit);
	}	 */

	
	function busquedaEmpresa() {
		jQuery('#<portlet:namespace />busquedaEmpresa').show();
	}
	
	function ocultaBusquedaEmpresa() {
		jQuery('#<portlet:namespace />busquedaEmpresa').hide();
	}
	
	function verInfoEmpresaSinParametros() {	
		var cuit_empleador=jQuery("#<portlet:namespace />cuit_entidad<%=sufi%>").val();		
		var sucu_empleador=jQuery("#<portlet:namespace />sucursal_entidad<%=sufi%>").val();
	    popup = Liferay.Popup({title:"<liferay-ui:message key="ver-info-empresa" />",modal:true,width:920});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/editar_empleadores_entry&cuit='+cuit_empleador+'&sucursal='+sucu_empleador;
		jQuery(popup).load(url);
	}
	
	function verInfoEmpresa(cuit_empleador, sucu_empleador) {
	    popup = Liferay.Popup({title:"<liferay-ui:message key="ver-info-empresa" />",modal:true,width:1200});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_empleadores_entry&cuit='+cuit_empleador+'&sucursal='+sucu_empleador;
		jQuery(popup).load(url);
	}
	var popupActa;
	function <portlet:namespace />altaActa() {		
		var cuit='<%=empresa.getCuit()%>';
		var molinera='<%=empresa.isMolinera()%>';
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/editar_actas_entry&fromBusquedaDeuda=fromBusquedaDeuda&cuit='+cuit+'&molinera='+molinera;
		//document.<portlet:namespace />fm.method = 'post';
		popupActa = Liferay.Popup({title:"<liferay-ui:message key="calculo-deuda" />",modal:true,width:920});
		jQuery(popupActa).load(url);
		//submitForm(document.<portlet:namespace />fm, url);
	}    
	
	function <portlet:namespace />altaActaMolinera() {
		var cuit='<%=empresa.getCuit()%>';
		var molinera='<%=empresa.isMolinera()%>';		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/edit_actas_no_os_entry&fromBusquedaDeuda=true&cuit='+cuit;
		//document.<portlet:namespace />fm.method = 'post';
		popupActa = Liferay.Popup({title:"<liferay-ui:message key="calculo-deuda" />",modal:true,width:920});
		jQuery(popupActa).load(url);
		//submitForm(document.<portlet:namespace />fm, url);
	}
	
	function editarRecibo(id, entidad) {
				
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/editar_recibos_entry&recibo_id='+id+'entidad_rec'+entidad;
		//document.<portlet:namespace />fm.method = 'post';
		popupRecibo = Liferay.Popup({title:"<liferay-ui:message key="recibo" />",modal:true,width:920});
		jQuery(popupRecibo).load(url);
		//submitForm(document.<portlet:namespace />fm, url);
	}   
	
	function anularRecibo(id) {				
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/anular_recibos_entry&recibo_id='+id;
		//document.<portlet:namespace />fm.method = 'post';
		popupRecibo = Liferay.Popup({title:"<liferay-ui:message key="recibo" />",modal:true,width:920});
		jQuery(popupRecibo).load(url);
		//submitForm(document.<portlet:namespace />fm, url);
	} 
	
	function refrescarDatosEmpresa(cuit) {
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/refrescar_empresa_molinera';
		 if(popup!=null){
             Liferay.Popup.close(popup);
             jQuery('#<portlet:namespace />infoEmpresa').load(url);
         }   
	}
	
	function buscarReportePorEntidad() {	
		jQuery('#<portlet:namespace />buscandoReporte').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/reporte_entidad_camara_masa';
		url+='&cuit=<%=empresa.getCuit()%>&sucur=<%=empresa.getSucursal()%>';
        url += '&rnd=' + Math.floor(Math.random()*100);		
		jQuery('#<portlet:namespace />reporteEntidadRemu').load(url, function() {
																		jQuery('#<portlet:namespace />buscandoReporte').hide();	
        															  }
        ); 
		
	}
	
	function buscarCalculosDeuda(){		
		var cuit='<%=empresa.getCuit()%>'
		jQuery('#<portlet:namespace />buscandoCalculoDiv').show();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_seguimiento_molinera' +
		'&cuit='+cuit+'&buscar=deuda';
		 url += '&rnd=' + Math.floor(Math.random()*100);	
		jQuery('#<portlet:namespace />busquedaCalculoDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscandoCalculoDiv').hide();       
        																jQuery('#<portlet:namespace />busquedaCalculoDiv').css('display','block');
        																jQuery('#<portlet:namespace />busquedaCalculoDiv').css('height','240px');
        																jQuery('#<portlet:namespace />busquedaCalculoDiv').css('overflow','scroll');
        															  }
        );	
	}
	
	function buscarActas(tipo, entidad){		
		var cuit='<%=empresa.getCuit()%>'
		jQuery('#<portlet:namespace />buscandoActaDiv').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_seguimiento_molinera' +
		'&cuit='+cuit+'&buscar=actasAcuerdos'+'&entidad='+entidad+'&tipo='+tipo;
		 url += '&rnd=' + Math.floor(Math.random()*100);	
		jQuery('#<portlet:namespace />busquedaActaDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscandoActaDiv').hide();       
        																jQuery('#<portlet:namespace />busquedaActaDiv').css('display','block');
        																jQuery('#<portlet:namespace />busquedaActaDiv').css('height','240px');
        																jQuery('#<portlet:namespace />busquedaActaDiv').css('overflow','scroll');
        															  }
        );	
	}
	
	function buscarCheques(){
		var cuit='<%=empresa.getCuit()%>'
		jQuery('#<portlet:namespace />buscandoChequesDiv').show();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_seguimiento_molinera' +
		'&cuit='+cuit+'&buscar=cheques';
		 url += '&rnd=' + Math.floor(Math.random()*100);	
		jQuery('#<portlet:namespace />busquedaChequesDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscandoChequesDiv').hide();       
        																jQuery('#<portlet:namespace />busquedaChequesDiv').css('display','block');
        																jQuery('#<portlet:namespace />busquedaChequesDiv').css('height','240px');
        																jQuery('#<portlet:namespace />busquedaChequesDiv').css('overflow','scroll');
        															  }
        );	
	}
	
	function buscarRecibos(){		
		var cuit='<%=empresa.getCuit()%>'
		jQuery('#<portlet:namespace />buscandoRecibosDiv').show();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_seguimiento_molinera' +
		'&cuit='+cuit+'&buscar=recibos';
		 url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />busquedaRecibosDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscandoRecibosDiv').hide();       
        																jQuery('#<portlet:namespace />busquedaRecibosDiv').css('display','block');
        																jQuery('#<portlet:namespace />busquedaRecibosDiv').css('height','240px');
        																jQuery('#<portlet:namespace />busquedaRecibosDiv').css('overflow','scroll');
        															  }
        );	
	}
	
	var popup;
	function popupRecibo(recibo_id, entidad){
		popup= Liferay.Popup({title:"<liferay-ui:message key="recibo" />",modal:true,position:[150,50],xy: ['center', 100],width:1000});		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_recibos_entry&recibo_id='+recibo_id+'&entidad_rec='+entidad;		
		jQuery(popup).load(url); 
	}
	
	buscarReportePorEntidad();
	
</script>
		