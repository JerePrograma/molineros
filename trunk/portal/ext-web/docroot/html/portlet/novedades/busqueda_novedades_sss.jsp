<%@ include file="/html/portlet/novedades/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.Locale" %>
<portlet:defineObjects/>

<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
/*  	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO); 
  		session.removeAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION); */

  		ArrayList<TipoNovedad> tiposNov = (ArrayList<TipoNovedad>) TraeListasServiceUtil.getTiposNovedadSss();
  		ArrayList<ArchivoNovedad> archivosProc = (ArrayList<ArchivoNovedad>) TraeListasServiceUtil.getFechasArchivosNovedades(WebKeysAfiliados.TIPOS_ORIGEN[3]);
  		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
  		SimpleDateFormat sdf2 = new SimpleDateFormat("MMM/yyyy",  new Locale("es", "ES"));

  		
  		Calendar fechaHoy = Calendar.getInstance();
%>
		<fieldset class="block-labels">
			<legend><liferay-ui:message key="grupo-filtro-busqueda-novedades" /></legend>				
				
				<table class="lfr-table"> 
					<tr>
						<td><label><liferay-ui:message key="cuil-titular" />:</label></td>
						<td><input id="<portlet:namespace />b_cuil_titular" name="<portlet:namespace />b_cuil_titular" size="13" maxlength="11" type="text" value="" /></td>
						<td><label><liferay-ui:message key="cuil" />:</label></td>
						<td><input id="<portlet:namespace />b_cuil" name="<portlet:namespace />b_cuil" size="13" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
					<tr>	
						<td><label><liferay-ui:message key="tipo-documento" />:</label></td>
						<td>
							<select name="<portlet:namespace/>b_tipoDoc" id="<portlet:namespace/>b_tipoDoc">
									<option value=""></option>
									<%
										for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {
									%>
										<option value="<%= tipoDoc %>"><%=tipoDoc%></option>
									<%
									}
									%>
							</select>
						</td>
						<td><label><liferay-ui:message key="nro-documento" />:</label></td>
						<td><input id="<portlet:namespace />b_nroDoc" name="<portlet:namespace />b_nroDoc" size="9" maxlength="8" type="text" value="" /></td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="apellido" />:</label></td>
						<td><input id="<portlet:namespace />b_apellido" name="<portlet:namespace />b_apellido" size="20" maxlength="100" type="text" value="" /></td>
						<td><label><liferay-ui:message key="nombre" />:</label></td>
						<td><input id="<portlet:namespace />b_nombre" name="<portlet:namespace />b_nombre" size="20" maxlength="100" type="text" value="" /></td>						
						<td>&nbsp;</td>						
					</tr>
					<!-- <tr>
						<td colspan="12">
							&nbsp;(<liferay-ui:message key="refine-busqueda" />)
						</td>
					</tr> -->
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="tipo-origen" />:</label></td>
						<td colspan="1">
							<select name="<portlet:namespace/>b_tipoOri" id="<portlet:namespace/>b_tipoOri"
							onchange="javascript:<portlet:namespace />mostrarPeriodoFiltro();" >
									<%
										for (String tipoOri : WebKeysAfiliados.TIPOS_ORIGEN) {
									%>
										<option value="<%= tipoOri %>" <%=tipoOri.equalsIgnoreCase("NOVEDADES")?"selected=selected":"" %> ><%=tipoOri%></option>
									<%
									}
									%>
							</select>
						</td>
						<td colspan="2">
							<div id="<portlet:namespace/>b_fechasNov">
								<table class="lfr-table">
									<tr>	
										<td><label><liferay-ui:message key="periodo-novedades" />:</label></td>
										<td colspan="1">
											<select name="<portlet:namespace/>b_fecha_nov" id="<portlet:namespace/>b_fecha_nov">
													<option value=""></option>
													<%
														for (ArchivoNovedad archProc : archivosProc) {
													%>
														<option value="<%=sdf1.format(archProc.getFechaArchivo()) %>" ><%= sdf2.format(archProc.getFechaArchivo())%></option>
													<%
													}
													%>
											</select>
										</td>
									</tr>	
								</table>	
							</div>
							<div id="<portlet:namespace/>b_periodoEmp">
								<table class="lfr-table">
									<tr>
										<td><label><liferay-ui:message key="periodo-hasta" />:</label></td>
										<td>
											<liferay-ui:input-date
												dayParam="b_periodo_hastaDia"
												dayValue="1" 
												monthParam="b_periodo_hastaMes"
												monthValue="<%=fechaHoy.get(Calendar.MONTH) %>"				
												yearParam="b_periodo_hastaAnio"
												yearValue="<%=fechaHoy.get(Calendar.YEAR) %>"
												yearRangeStart="<%=fechaHoy.get(Calendar.YEAR)-2 %>"
												yearRangeEnd="<%=fechaHoy.get(Calendar.YEAR)%>"
												firstDayOfWeek="<%=fechaHoy.getFirstDayOfWeek() %>"
												disabled="<%= false %>" />
										</td>
										
										<td><select id="<portlet:namespace/>b_tipo_novedad_Emp">
												<optgroup label="Tipo Novedad">
													<option value="ALTAS" selected="selected">Altas</option>
													<option value="CAMBIOSPLAN">Cambios de Plan</option>
													<option value="BAJAS">Bajas</option>
												</optgroup>
											</select>
										</td>
									</tr>
								</table>		
							</div>
						</td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="5">	
							<div id="<portlet:namespace/>b_tipoNovedad" >
							<table class="lfr-table">
								<tr>		
								<td><label><liferay-ui:message key="tipo-novedad" />:</label></td>
								<td colspan="4">
									<select name="<portlet:namespace/>b_tipoNov" id="<portlet:namespace/>b_tipoNov">
											<option value=""></option>
											<%
												for (TipoNovedad tipoNov : tiposNov) {
											%>
												<option value="<%= tipoNov.getCodigo() %>">(<%= tipoNov.getCodigo() %>) <%=tipoNov.getDescripcion()%></option>
											<%
											}
											%>
									</select>
								</td>
								</tr>
							</table>	
							</div>
						</td>
					</tr>
					
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="5" align="left">							
							<input type="button" id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" 
							title="<liferay-ui:message key="buscar" />" 
							onClick="javascript: <portlet:namespace />buscarNovedades();"/>							
						   
						    &nbsp;&nbsp;
							<input id="<portlet:namespace />limpiar-campos" value="<liferay-ui:message key="limpiar-campos"/>" 
							title="<liferay-ui:message key="limpiar-campos" />" type="button" 
							onClick='javascript:<portlet:namespace />limpiarCampos()'/>							
							
							&nbsp;&nbsp;
							<input type="button" id="<portlet:namespace />reporte" 
								   value="<liferay-ui:message key="obtener-excel"/>" 
								   onClick="<portlet:namespace />reporteXLS();" /> 
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
			<div align="center" id="<portlet:namespace />busquedaNovedadDiv">
			</div>
		</fieldset> 

<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	
	jQuery('#<portlet:namespace />b_periodo_hastaDia').hide();
	
	jQuery('#<portlet:namespace />b_periodoEmp').hide();
	/* jQuery('#<portlet:namespace />b_fechasNov').hide();
	jQuery('#<portlet:namespace />b_tipoNovedad').hide(); */
	jQuery('#<portlet:namespace />b_fechasNov').show();
	jQuery('#<portlet:namespace />b_tipoNovedad').show();
	
	function <portlet:namespace />buscarNovedades(){

		var cuil_titular=jQuery('#<portlet:namespace />b_cuil_titular').val();
		var cuil=jQuery('#<portlet:namespace />b_cuil').val();
		var tipoDoc=jQuery('#<portlet:namespace />b_tipoDoc').val();		
		var nroDoc=jQuery('#<portlet:namespace />b_nroDoc').val();			
		var apellido=jQuery('#<portlet:namespace />b_apellido').val();		
		var nombre=jQuery('#<portlet:namespace />b_nombre').val();
		var tipoOri=jQuery('#<portlet:namespace />b_tipoOri').val();
		var fechaProc=jQuery('#<portlet:namespace />b_fecha_nov').val();
		var tipoNov=jQuery('#<portlet:namespace />b_tipoNov').val();

		/* var diaHasta=jQuery('#b_periodo_hastaDia').val(); */	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />b_periodo_hastaMes').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />b_periodo_hastaAnio').val();
	    var tipoNovEmp=jQuery('#<portlet:namespace />b_tipo_novedad_Emp').val();
		/* if(!<portlet:namespace />validarBusqueda(cuil_titular,cuil,tipoDoc,nroDoc,apellido,nombre,tipoNov)){
			return false;
		}

		if(cuil_titular.length>0){
			if(!validarCuil(cuil_titular,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil_titular').focus();
				return false;
			}
		}
		if(cuil.length>0){
			if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil').focus();
				return false;
			}
		} */
		
		
		var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val(); 
	
		jQuery('#<portlet:namespace />buscando').show();

		var url = "";
		var tipoOrigen=jQuery('#<portlet:namespace/>b_tipoOri').val();
		
		if (tipoOrigen == 'NOVEDADES') {
			url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_novedades&cuil_titular='+cuil_titular+
			'&cuil='+cuil+'&tipoDoc='+tipoDoc+'&nroDoc='+escape(nroDoc)+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+
			'&tipoOri='+tipoOri+'&tipoNov='+tipoNov+'&fechaProc='+encodeURI(fechaProc)+'&pagina='+offset_reg;
		}
		
		if (tipoOrigen == 'EMPLEADORES') {
			url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_novedades';
			url = url + '&mesHasta='+mesHasta+'&anioHasta='+anioHasta+'&tipoOri='+tipoOri+'&tipoNoveEmpl='+tipoNovEmp+'&pagina='+offset_reg;
		}
		
		if (tipoOrigen == 'PADRON CONSOLIDADO') {
			jQuery('#<portlet:namespace />buscando').hide(); 							
			alert('No existe esta funcionalidad, Obtener Planilla de Cálculo');
	        return;
		}


	    jQuery('#<portlet:namespace />busquedaNovedadDiv').load(url, function() {
	    							jQuery('#<portlet:namespace />buscando').hide();            															
	        				});
	}
	
	function <portlet:namespace />validarBusqueda(cuil_titular,cuil,tipoDoc,nroDoc,apellido,nombre,tipopNov,tipoOri,fechaProc){
		if(trim(cuil_titular.length)==0 && trim(cuil.length)==0 && trim(tipoDoc.length)==0 && trim(nroDoc.length)==0 
				&& trim(apellido.length)==0 && trim(nombre.length)==0 && trim(tipoNov.length)==0
				&& trim(tipoOri.length)==0 && trim(fechaProc.length)==0){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}
	
	function <portlet:namespace />limpiarCampos(){
		jQuery('#<portlet:namespace />b_cuil_titular').val('');
		jQuery('#<portlet:namespace />b_cuil').val('');
		jQuery('#<portlet:namespace />b_tipoDoc').val('');
		jQuery('#<portlet:namespace />b_nroDoc').val('');	
		jQuery('#<portlet:namespace />b_apellido').val('');
		jQuery('#<portlet:namespace />b_nombre').val('');
		jQuery('#<portlet:namespace />b_tipoNov').val('');
		jQuery('#<portlet:namespace />b_tipoOri').val('');
		jQuery('#<portlet:namespace />b_fecha_nov').val('');

		
	}

	var popupNov, popupCuil ;
	
	function mostrarDetalleNovedad(idNov){
		popupNov= Liferay.Popup({title:"<liferay-ui:message key="detalle-novedad" />",modal:true,width:900,height:650});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/ver_detalle_novedad&id_novedad='+idNov;   		       	
		jQuery(popupNov).load(url); 
		
	}
	
	function mostrarDetalleNovedadEmpl(cuil_tit,inte,peri){
		popupNov= Liferay.Popup({title:"<liferay-ui:message key="detalle-novedad" />",modal:true,width:900,height:350});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/ver_detalle_novedad_empl&cuil_titular='+cuil_tit+'&inte='+inte+'&periodo='+peri;   		       	
		jQuery(popupNov).load(url); 
		
	}
	
	function mostrarCambioCuil(idNov){
		popupCuil= Liferay.Popup({title:"<liferay-ui:message key="detalle-novedad" />",modal:true,width:900,height:360});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/cambio_cuil_novedad&id_novedad='+idNov;   		       	
		jQuery(popupCuil).load(url);
		/* jQuery().load(url, function() {																																										
			 if(popupCuil!=null){
				Liferay.Popup.close(popupCuil);
			}	 																														
		 }); */		
		
	}
	
	function editarAfiliado(cuil,inte){

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_afiliado_entry&cuil_titular='+cuil+
		'&inte='+inte;  

	 	popupAfi= Liferay.Popup({title:"<liferay-ui:message key="afiliado" />",modal:true,width:1300,height:900});
 		jQuery(popupAfi).load(url); 

  	}

	function confirmaCambioCuil(){

		var idNov=jQuery("#<portlet:namespace/>b_idNov").val();
		var tDoc=jQuery("#<portlet:namespace/>a_documento_tipo").val();
		var nDoc=jQuery("#<portlet:namespace />a_nroDoc").val();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/cambio_cuil_novedad&id_novedad='+idNov;   		       	

		jQuery.post(url,{id_novedad:idNov, tipoDoc:tDoc, nroDoc:nDoc}, function() {																																											
			if(popupCuil!=null){
				Liferay.Popup.close(popupCuil); 
			}	 
 		});
	}
	
	function <portlet:namespace />mostrarPeriodoFiltro() {

		jQuery('#<portlet:namespace />b_periodoEmp').hide();
		jQuery('#<portlet:namespace />b_fechasNov').hide();
		jQuery('#<portlet:namespace />b_tipoNovedad').hide();
	
		var tipoOrigen=jQuery('#<portlet:namespace/>b_tipoOri').val();
		
		if (tipoOrigen == 'EMPLEADORES') {
			jQuery('#<portlet:namespace />b_periodoEmp').show();
		}
		if (tipoOrigen == 'NOVEDADES') {
			jQuery('#<portlet:namespace />b_fechasNov').show();
			jQuery('#<portlet:namespace />b_tipoNovedad').show();
		}
		if (tipoOrigen == 'PADRON CONSOLIDADO') {
			jQuery('#<portlet:namespace />b_fechasNov').show();
		}
	}
	
	function <portlet:namespace />reporteXLS(){
/*		var cuil_titular=jQuery('#<portlet:namespace />b_cuil_titular').val();
		var cuil=jQuery('#<portlet:namespace />b_cuil').val();
		var tipoDoc=jQuery('#<portlet:namespace />b_tipoDoc').val();		
		var nroDoc=jQuery('#<portlet:namespace />b_nroDoc').val();			
		var apellido=jQuery('#<portlet:namespace />b_apellido').val();		
		var nombre=jQuery('#<portlet:namespace />b_nombre').val(); */
		var tipoOri=jQuery('#<portlet:namespace />b_tipoOri').val();
 		var fechaProc=jQuery('#<portlet:namespace />b_fecha_nov').val();
 		var tipoNov=jQuery('#<portlet:namespace />b_tipoNov').val(); 
 		/* var tipoNovDesc=jQuery('#<portlet:namespace />b_tipoNov').text();  */
 		var tipoNovDesc=jQuery('#<portlet:namespace />b_tipoNov option:selected').html();
 		
		var mesHasta=parseInt(jQuery('#<portlet:namespace />b_periodo_hastaMes').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />b_periodo_hastaAnio').val();
	    var tipoNovEmp=jQuery('#<portlet:namespace />b_tipo_novedad_Emp').val();

		var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val(); 
	
		var url = "";
		
<%-- 		if (tipoOrigen == 'NOVEDADES') {
			url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_novedades&cuil_titular='+cuil_titular+
			'&cuil='+cuil+'&tipoDoc='+tipoDoc+'&nroDoc='+escape(nroDoc)+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+
			'&tipoOri='+tipoOri+'&tipoNov='+tipoNov+'&fechaProc='+encodeURI(fechaProc)+'&pagina='+offset_reg;
		} --%>
		if (tipoOri == 'EMPLEADORES') {
			<%-- url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_novedades';
			url = url + '&mesHasta='+mesHasta+'&anioHasta='+anioHasta+'&tipoOri='+tipoOri+'&tipoNoveEmpl='+tipoNovEmp+'&pagina='+offset_reg; --%>
			window.location.href ='/xlsservlet/?reporte=REPORTE_BUSQUEDA_NOVEDADES_EMPLEADORES'
				 +'&mesHasta='+mesHasta
				 +'&anioHasta='+anioHasta
				 +'&tipoOri='+tipoOri
				 +'&tipoNoveEmpl='+tipoNovEmp
				 +'&pagina='+offset_reg;
		}else if (tipoOri == 'PADRON CONSOLIDADO'){
			window.location.href ='/xlsservlet/?reporte=REPORTE_NOVEDAD_PADRON_CONSOLIDADO_SSS'
				 +'&mesHasta='+mesHasta
				 +'&anioHasta='+anioHasta
				 +'&tipoNovDesc='+encodeURI(tipoNovDesc)
				 +'&tipoOri='+tipoOri
				 +'&fechaProc='+fechaProc;			
		}else{
			/* alert('Solo disponible para Novedades del Portal Empleadores');
			return false; */
			window.location.href ='/xlsservlet/?reporte=REPORTE_BUSQUEDA_NOVEDADES_SSS'
				 +'&mesHasta='+mesHasta
				 +'&anioHasta='+anioHasta
				 +'&tipoNov='+tipoNov
				 +'&tipoNovDesc='+encodeURI(tipoNovDesc)
				 +'&tipoOri='+tipoOri
				 +'&fechaProc='+fechaProc;
				/*  +'&pagina='+offset_reg; */
		}	
		
		
	}

	
	
</script>
