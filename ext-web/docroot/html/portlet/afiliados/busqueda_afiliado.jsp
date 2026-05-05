<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
 		boolean showABMOpciones = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_OPCIONES);
 		boolean showABMCrm = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM);
 		boolean showABMCrmLegales = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM_LEGALES);
 		boolean showConfigFechasOpcionSSS = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_CONFIG_FECHAS_OPCION_SSS);
 		
 		session.removeAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
 		session.removeAttribute("cmd");
 		String opciones=request.getParameter("opciones"); 		
 		Calendar cal = Calendar. getInstance();
		cal.setTime(FechaOpcionSSSUtil.buscarProximaFechaPressSuper());
		Calendar fechaDesde = cal;
		Calendar current= CalendarFactoryUtil.getCalendar();
 	
 		
%>

<liferay-ui:error
	exception="<%=IntegranteGrupoNoBorrableException.class %>"
	message="the-integrante-no-puede-ser-borrado" />
 	<fieldset class="block-labels">
 <table>
	<tr>
  <td>
  
 
	
			<%if(null!=opciones && opciones.trim().equals("true")){%>
				<legend><liferay-ui:message key="grupo-filtro-busqueda-opciones" /></legend>
			<%}else {%>
				<legend>
				<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />
				<% if(seccionalFijada!=0){%>
					en Seccional <%=seccionalString%>
				<%}%>
				</legend>				
			<%}%>
				<table class="lfr-table"> 
				
					<tr>
					<%if(null!=opciones && opciones.trim().equals("true")){%>
						<td colspan="4">
							<input type="hidden" name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad" />
							<input type="hidden" id="<portlet:namespace />numero_afi" name="<portlet:namespace />numero_afi"/>
						</td>
					<%}else {%>
						<td><label><liferay-ui:message key="entidad" />:</label></td>
						<td>
							<select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad">
									<%for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {	%>
										<c:if test="<%=((entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_OSPIM)) ||
														(entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_AMTIMA)) ||
														(entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_UOMA)))%>">									
											<option value="<%= entidad %>"><%=entidad%></option>
										</c:if>
									<%}%>
							</select>
						</td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="numero-afi" />:</label></td>
						<td><input id="<portlet:namespace />numero_afi" name="<portlet:namespace />numero_afi" size="6" maxlength="10" type="text" value=""/></td>
						<td><label><liferay-ui:message key="nro-socio-prevencion" />:</label></td>
						<td><input id="<portlet:namespace />numero_socio_prev" name="<portlet:namespace />numero_socio_prev" size="6" maxlength="6" type="text" value=""/></td>
						<td><label><liferay-ui:message key="nro-credencial-prevencion" />:</label></td>
						<td><input id="<portlet:namespace />numero_credencial_prev" name="<portlet:namespace />numero_credencial_prev" size="8" maxlength="11" type="text" value=""/></td>
					<%}%>	
					</tr>
					
					<tr>
						<td colspan="16">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="cuil" />:</label></td>
						<td><input id="<portlet:namespace />cuil" name="<portlet:namespace />cuil" size="13" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;</td>
						<%if(null!=opciones && opciones.trim().equals("true")){%>
							<td><label><liferay-ui:message key="formNro" />:</label></td>
							<td><input type="text" id="<portlet:namespace />formNro" maxlength="9" name="<portlet:namespace />formNro" value="" 
					  				 size="10" maxlength="10" onkeydown="allowOnlyDigits(event);" /></td>
							<td colspan="1"><label><liferay-ui:message key="libro" />:</label></td>
							<td colspan="1"><input id="<portlet:namespace />libro" name="<portlet:namespace />libro" size="10" maxlength="5" type="text" value="" /></td>
							<td colspan="2">
							<input type="hidden" id="<portlet:namespace />inte" name="<portlet:namespace />inte" value=""/>
							<input type="hidden" id="<portlet:namespace />tipoDoc" name="<portlet:namespace />tipoDoc" value=""/>
							<input type="hidden" id="<portlet:namespace />nroDoc" name="<portlet:namespace />nroDoc" value=""/>
							</td>
						<%}else{%>
							<td><label><liferay-ui:message key="integrante" />:</label></td>
							<td><input id="<portlet:namespace />inte" name="<portlet:namespace />inte" size="2" maxlength="2" type="text" value="" /></td>
							<td><label><liferay-ui:message key="tipo-documento" />:</label></td>
							<td>
								<select name="<portlet:namespace/>tipoDoc" id="<portlet:namespace/>tipoDoc">
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
							<td><input id="<portlet:namespace />nroDoc" name="<portlet:namespace />nroDoc" size="9" maxlength="8" type="text" value="" /></td>
						<%}%>
						<% if(seccionalFijada==0){%>
							<td><label><liferay-ui:message key="seccional" />:</label></td>
							<td colspan="2" rowspan="3" style="vertical-align:top" >
								<liferay-util:include page="/html/portlet/afiliados/busqueda_seccional.jsp"/> &nbsp;&nbsp;&nbsp;
							</td>
						<%}else{%>
							<td colspan="3">&nbsp;</td>
						<%} %>
					</tr>
					<tr>
						<td colspan="16">&nbsp;</td>
					</tr>
					
						<%if(null!=opciones && opciones.trim().equals("true")){%>
							<td><label><liferay-ui:message key="apellido" />:</label></td>
							<td colspan="2"><input id="<portlet:namespace />apellido" name="<portlet:namespace />apellido" size="20" maxlength="100" type="text" value="" /></td>
							<td><label><liferay-ui:message key="nombre" />:</label></td>
							<td colspan="2"><input id="<portlet:namespace />nombre" name="<portlet:namespace />nombre" size="20" maxlength="100" type="text" value="" /></td>
							<td><input type="checkbox" id="<portlet:namespace />incluyeBajas" name="<portlet:namespace />incluyeBajas"/>&nbsp;&nbsp;Incluye bajas</td>						<%}else{%>
							<td><label><liferay-ui:message key="apellido" />:</label></td>
							<td colspan="2"><input id="<portlet:namespace />apellido" name="<portlet:namespace />apellido" size="20" maxlength="100" type="text" value="" /></td>
							<td><label><liferay-ui:message key="nombre" />:</label></td>
							<td colspan="2"><input id="<portlet:namespace />nombre" name="<portlet:namespace />nombre" size="20" maxlength="100" type="text" value="" /></td>
							<td><input id="<portlet:namespace />libro" name="<portlet:namespace />libro" type="hidden" value="" /></td>
						<%}%>
										
					<% if(null!=opciones) { %>	  	
											
						<tr>
							<td colspan="6">&nbsp;</td>
						</tr>
					<%} %>
					 
					<% if(null!=opciones) { %>	  
						<tr>
					<%} %>
						<td colspan="6">							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						    &nbsp;&nbsp;
							<% if(showABMButtons && (null==opciones || !opciones.trim().equals("true"))) { %>								
								<input type="button" value="<liferay-ui:message key="alta-afiliado" />" onClick="<portlet:namespace />altaAfiliado();" />
							<%} %>
							<% if(showABMOpciones && (null!=opciones && opciones.trim().equals("true"))) { %>								
								<input type="button" value="<liferay-ui:message key="alta-opcion" />" onClick="<portlet:namespace />altaOpcionAfi();" />
							<%} %>
							<% if(showABMCrm) { %>								
								<input type="button" value="<liferay-ui:message key="nuevo-contacto-noafi" />" onClick="javascript:nuevoContactoNoAfi();" />
							<%} %>
							<% if(showABMCrmLegales) { %>								
								<input type="button" value="<liferay-ui:message key="nuevo-doc-legal-noafi" />" onClick="javascript:nuevoReclamoNoAfi();" />
							<%} %>
							
									
							<input id="<portlet:namespace />limpiar-campos" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="limpiar-campos" />" type="button" onClick='javascript:<portlet:namespace />limpiarCampos()'/>		
						</td>	
					
					</tr>
					<% if(showABMOpciones && (null!=opciones && opciones.trim().equals("true"))) { %>	
				    
					<tr>
						<td colspan="6">&nbsp;</td>
					</tr>
					
						<tr>
						  
							<td colspan="11" align="left">							
								<input type="button" value="<liferay-ui:message key="exportar-opciones" />" 
									onClick="<portlet:namespace />exportOpcionesSSS();" />
								
								<input type="button" id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" 
								onClick="<portlet:namespace />reporteXLS();" />
								
								<input type="button" value="<liferay-ui:message key="volver-exportar-opciones" />" 
								onClick="<portlet:namespace />volverAtrasUltimaExportacion();" />  
							</td>	
						</tr>
					
					<%} %>
				
					
					<tr>
						<td colspan="16">
							&nbsp;(<liferay-ui:message key="refine-busqueda" />)
						</td>
					</tr>
				</table>	
    	
    	<%if (showConfigFechasOpcionSSS &&  (null!=opciones && opciones.trim().equals("true"))){%>
	    	<td  colspan="5">&nbsp;&nbsp;&nbsp;</td>
		    <td  colspan="5">&nbsp;&nbsp;&nbsp;</td>
	   	    <td  colspan="5">&nbsp;&nbsp;&nbsp;</td>
	   	    <td  colspan="5">&nbsp;&nbsp;&nbsp;</td>
	   	    <td  colspan="5">&nbsp;&nbsp;&nbsp;</td>
	   	    <td  colspan="5">&nbsp;</td>
	   	    
		<%}%>
		     
		    
	     <td align="left">
	     		  
	     		<%if(showConfigFechasOpcionSSS &&  (null!=opciones && opciones.trim().equals("true"))){%>
					   	
		         		           
		           <div 
				      id="<portlet:namespace />busquedaFechaOpcionSSS">
								<liferay-util:include page="/html/portlet/afiliados/busquedaFechaOpcionSSS.jsp"/>				
					    <liferay-ui:input-date
						dayParam="fechaOpcionDia"
 						dayNullable="<%= true %>" 
						dayValue="<%= fechaDesde.get(Calendar.DAY_OF_MONTH)%>"
						monthParam="fechaOpcionMes"
						monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
						yearParam="fechaOpcionAnio"
						yearValue="<%= fechaDesde.get(Calendar.YEAR)  %>"
						yearRangeStart="<%= current.get(Calendar.YEAR) - 1 %>"
						yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)+1 %>"
						firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
						monthAndYearNullable="<%= false %>"
						disabled="<%= false %>" />
						&nbsp;
						<input type="button" id="<portlet:namespace />confirm" value="<liferay-ui:message key="confirm"/>" title="<liferay-ui:message key="confirm" />" onClick="<portlet:namespace />altaFechaPressOpcionSSS()" />
									  	  
					</div>
					
				<%}%>
		    
	  </td>
  </tr>
		
 </table>
			</fieldset>
		<fieldset class="block-labels">
			<div align="center" id="<portlet:namespace />buscando">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key="buscando"/></td>
						<td align="center">
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
			<%if(null==opciones || !opciones.trim().equals("true")){%>
				<div align="center" id="<portlet:namespace />busquedaAfiliadoDiv">
				</div>
			<%}%>
			
			<%if(null!=opciones && opciones.trim().equals("true")){%>
				<div align="center" id="<portlet:namespace />busquedaAfiliadoOpcionesDiv">
				</div>
			<%}%>
		</fieldset> 
			
<script type="text/javascript">

	jQuery('#<portlet:namespace />buscando').hide();	
	var url = "<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliados_sesion"
	jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url);
	
	jQuery('#<portlet:namespace />buscar').click(function(){<portlet:namespace />busqueda();});
	
	function <portlet:namespace />busqueda(){
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();		
		var tipoDoc=jQuery('#<portlet:namespace />tipoDoc').val();		
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();		
		<% if(seccionalFijada==0){%>
			var seccional=jQuery('#<portlet:namespace />id_seccional').val();		
			var seccional_nombre=jQuery('#<portlet:namespace />seccional').val();
		<%}else{%>
			var seccional="<%=seccionalFijada%>";		
			var seccional_nombre="";
		<%}%>
		var apellido=jQuery('#<portlet:namespace />apellido').val();		
		var nombre=jQuery('#<portlet:namespace />nombre').val();		
		var entidad=jQuery('#<portlet:namespace />entidad').val();		
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		var libro=jQuery('#<portlet:namespace />libro').val();
		var formulario=jQuery('#<portlet:namespace />formNro').val();
		var nroSocioPrev =jQuery('#<portlet:namespace />numero_socio_prev').val();
		var nroCredencialPrev =jQuery('#<portlet:namespace />numero_credencial_prev').val();
		var incluBajas = "";
		<%if(null==opciones || opciones.trim().equals("false")){%>
			if(!<portlet:namespace />validarBusqueda(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi,nroSocioPrev,nroCredencialPrev)){
				return false;
			}
		<%}else{%>
			incluBajas=jQuery("#<portlet:namespace/>incluyeBajas").is(':checked');
		<%}%>
		
		if(cuil.length>0){
			if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil').focus();
				return false;
			}
		}
		
		jQuery('#<portlet:namespace />buscando').show();
		
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
			jQuery("#<portlet:namespace />seccional").val("");
			jQuery("#<portlet:namespace />id_seccional").val("");
		}
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliados';
		var paramsAfi = {"cuil" : cuil, "inte" : inte, "tipoDoc" : tipoDoc, "nroDoc" : escape(nroDoc), "seccional" : seccional, 
						"nombre" : nombre, "apellido" : apellido, "entidad" : entidad, "numero_afi" : numero_afi,
						"nroSocioPrevencion" : nroSocioPrev, "nroCredencialPrevencion" : nroCredencialPrev}
		
		var paramsOpc = {"cuil" : cuil, "inte" : inte, "tipoDoc" : tipoDoc, "nroDoc" : escape(nroDoc), "seccional" : seccional, 
				"nombre" : nombre, "apellido" : apellido, "entidad" : entidad, "numero_afi" : numero_afi,
				"seccional_nombre" : escape(seccional_nombre), "libro" : escape(libro), "nroFormulario" : formulario, 
				"opciones" : 'true', "incluyeBajas" : incluBajas}
			
		<%if(null!=opciones && opciones.trim().equals("true")){%>
			jQuery('#<portlet:namespace />busquedaAfiliadoOpcionesDiv').load(url, paramsOpc, function() {
	        																jQuery('#<portlet:namespace />buscando').hide();            															
	        															  }
	        );
		<%}else{%>			
	        jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, paramsAfi, function() {
	        																jQuery('#<portlet:namespace />buscando').hide();            															
	        															  }
	        );
	    <%}%>
	}
	
	function <portlet:namespace/>buscaGrupo(cuil){
		jQuery('#<portlet:namespace />buscando').show();				
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliados&cuil='+cuil;		
		//jQuery('#userlist').remove();
        jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );			
	}
	function <portlet:namespace />validarBusqueda(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi, nroSocioPrev, nroCredPrev){
		if(trim(cuil.length)==0 && trim(inte.length)==0 && trim(tipoDoc.length)==0 && trim(nroDoc.length)==0 && trim(seccional.length)==0 &&  
		   trim(apellido.length)==0 && trim(nombre.length)==0 && trim(entidad.length)==0 && trim(numero_afi.length)==0
		   && trim(nroSocioPrev.length)==0 && trim(nroCredPrev.length)==0 ){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}
	
	function <portlet:namespace />limpiarCampos(){
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');
		jQuery('#<portlet:namespace />tipoDoc').val('');
		jQuery('#<portlet:namespace />nroDoc').val('');
		<% if(seccionalFijada==0){%>
			jQuery('#<portlet:namespace />id_seccional').val('');		
			jQuery('#<portlet:namespace />seccional').val('');
		<%}%>
		jQuery('#<portlet:namespace />apellido').val('');
		jQuery('#<portlet:namespace />nombre').val('');
		jQuery('#<portlet:namespace />entidad').val('');
		jQuery('#<portlet:namespace />numero_afi').val('');
		jQuery('#<portlet:namespace />numero_socio_prev').val('');
		jQuery('#<portlet:namespace />numero_credencial_prev').val('');
	}

	function altaOpcion(cuil,nroFormulario){				
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/afiliados/editar_afiliado_entry" /></portlet:renderURL>';
		url=url+'&cuil_titular='+cuil+'&opciones=true&cmd=update&nroFormulario='+nroFormulario;	
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	
	function <portlet:namespace />altaFechaPressOpcionSSS(){				
		
		var diaOpc=parseInt(jQuery('#<portlet:namespace />fechaOpcionDia').val());
		var mesOpc=parseInt(jQuery('#<portlet:namespace />fechaOpcionMes').val())+1;
		var anioOpc=parseInt(jQuery('#<portlet:namespace />fechaOpcionAnio').val());
		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/afiliados/alta_opcion_fecha_sss_press" /></portlet:renderURL>';
		url=url+'&fechaOpcionDia='+diaOpc+'&fechaOpcionMes='+mesOpc+'&fechaOpcionAnio='+anioOpc;	
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}

	function eliminarOpcionSSS(cuil,nroformulario) {	 
		var params = "&editaropcion=" + "<%= Constants.DELETE %>" + "&cuil_titular="+cuil + "&nro_formulario="+nroformulario;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_opcion_entry';				
		url = url + params;
	 	jQuery('#<portlet:namespace />busquedaAfiliadoOpcionesDiv').load(url);
		/* submitForm(document.<portlet:namespace />fm, url);	 */
	 }
	
	function recuperarOpcionSSS(cuil,nroformulario) {	 
		var params = "&editaropcion=" + "<%= Constants.RESTORE %>" + "&cuil_titular="+cuil + "&nro_formulario="+nroformulario;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_opcion_entry';				
		url = url + params;
	 	jQuery('#<portlet:namespace />busquedaAfiliadoOpcionesDiv').load(url);
		/* submitForm(document.<portlet:namespace />fm, url);	 */
	 }
	
	/*Este .txt son las opciones para exportar al V6, se tira una vez xq se actualizan los registros a estado exportado*/
	function <portlet:namespace />exportOpcionesSSS(){
		if(!confirm("<liferay-ui:message key='desea-exportar-opciones'/>")){
			return false;
		}
		window.location.href ='/txtservlet/?reporte=EXPORTACION_NUEVAS_OPCIONES_SSS';
	}
	/*Este .xls son las opciones para exportar al V6, se tira para control las veces q sea necesario, no actualiza registros*/
	function <portlet:namespace />reporteXLS(){

		 window.location.href ='/xlsservlet/?reporte=EXPORTACION_NUEVAS_OPCIONES_SSS';
	}	 
	
	var popupCRM;
	
	<%-- function nuevoCrmContacto(cuil_titu,integ) {
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/afiliados/editar_contacto_entry" /></portlet:renderURL>';
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/editar_contacto_entry';  
		url=url+'&cuil_titular='+cuil_titu+'&integ='+integ+'&<%=Constants.CMD%>='+'<%=Constants.ADD %>' ;
		url=url+'&cuil_titular='+cuil_titu+'&integ='+integ;
		url=url+'&cmd=ADD&noAfiliado=false';
		document.<portlet:namespace />fm.method = 'post';
		
		submitForm(document.<portlet:namespace />fm, url);
	} --%>

	function editarCrmContacto(idContSerial, mensaje) {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT%>";
		params = params + '&idContactoSerial='+idContSerial+'&msgReturn='+mensaje;
		
		popupCRM = new Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true, width: 950, position:['center',30]});

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_contacto_entry';   		       	
		url = url + params;
		jQuery(popupCRM).load(url);	
	}

	function nuevoReclamoNoAfi() {
 		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>">
						<portlet:param name="struts_action" value="/afiliados/editar_crm_legales_entry" />
						<portlet:param name="cmd" value="add" />
						<portlet:param name="noAfiliado" value="true" />
					</portlet:renderURL>';		

		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url); 
		
	}
	
	function nuevoContactoNoAfi() {
 		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>">
						<portlet:param name="struts_action" value="/afiliados/editar_contacto_entry" />
						<portlet:param name="cmd" value="add" />
						<portlet:param name="noAfiliado" value="true" />
					</portlet:renderURL>';		

		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url); 
		
	}
	
	function <portlet:namespace />volverAtrasUltimaExportacion(){
		var cantidadExpor = 0;
		var procesoOk = "";
		var confirmar = "";
		
		/* primero calculamos cantidad de opciones del dia que fueron exportadas a la sss */
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/volver_atras_exportacion_sss&calcular=true';
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				cantidadExpor = obj.cantidad;	
				confirmar = confirm("Desea volver atrás la exportación de "+ cantidadExpor + " formularios de Opción de la SSS?");
				
				if (!confirmar) {
					return false;
				}else{
					/* Disparamos la actualizacion de la exportacion, volviendo habilitados los formularios para exportar nuevamente */
					<portlet:namespace />procesarVueltaAtrasExportacionSSS();
				}		
			}				                                                                                                                                                                                                                                                            
			
		});
	}

	function <portlet:namespace />procesarVueltaAtrasExportacionSSS(){
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/volver_atras_exportacion_sss&procesar=true';
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				var procesoOk = obj.resultado;	
				if(procesoOk = '1'){
					alert('Los últimos formularios de opción exportados a la SSS se volvieron atrás. ' + 
							'A continuación puede repetir la exportación si lo desea');
				}else{
					alert('Error de procesamiento');
				}
			}				                                                                                                                                                                                                                                                            
			
		});
	}
	
	
	
	function marcarAntecedentes(cuil,inte){
		
		if(!confirm("Está seguro de Marcar al Afiliado con ANTECEDENTES JUDICIALES?")){
			return false;
		}
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/aplicar_suspension_cobertura';
		
		var params = {"cuil_titular":cuil,"inte":inte,"accion":"antecedentes"} 
		
		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, params, function() {																																											
											Liferay.Popup.close(popup);
									  }); 
	}

</script>

