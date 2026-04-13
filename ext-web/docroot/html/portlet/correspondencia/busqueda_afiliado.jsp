<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%
	String edit_mode = ParamUtil.getString(request, "edit_mode", null);
	String discapacidad = ParamUtil.getString(request, "discapacidad", null);
	String pag_reintegro = ParamUtil.getString(request, "pag_reintegro", null);

	if (pag_reintegro != null) {
		pag_reintegro = "true";
	}
	else {
		pag_reintegro = "false";
	}
	if (discapacidad != null) {
		discapacidad = "true";
	}
	else {
		discapacidad = "false";
	}
	
	String fecha_prestacion = ParamUtil.getString(request, "fecha_prestaci", "");
	String tipo_reintegro = (String)request.getAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION);
	 		
	boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
	boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_AMTIMA);
	boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_UOMA);
		
	String cuil = ParamUtil.getString(request, "cuil", "");
	String inte = ParamUtil.getString(request, "inte", "");
	
	String seccionalString=null;
	String seccionalDefecto=user.getExpandoBridge().getAttribute("id_seccional").toString(); 		
	int seccionalFijada=null!=seccionalDefecto&& !seccionalDefecto.trim().equals("")&& !seccionalDefecto.trim().equals("0")?Integer.parseInt(seccionalDefecto):0;
	if(seccionalFijada!=0){
		seccionalString=user.getExpandoBridge().getAttribute("seccional").toString();
	}
%>
<portlet:defineObjects/>	
				<legend>
				<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />
				<% if(seccionalFijada!=0){%>
					en Seccional <%=seccionalString%>
				<%}%>
				</legend>						
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="entidad" />:</label></td>
						<td>
							<select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad" <%= !Boolean.parseBoolean(edit_mode) ? " disabled='true'" : ""  %>>
									<%
										if (Boolean.parseBoolean(pag_reintegro) && tipo_reintegro != null && (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS))) 
										{
										%>	
											<option value="<%= WebKeysGlobal.ENTIDAD_UOMA %>"><%=WebKeysGlobal.ENTIDAD_UOMA%></option>
										<%	
										}
										else if (Boolean.parseBoolean(pag_reintegro) && tipo_reintegro != null && (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA))) 
										{
										%>	
											<option value="<%= WebKeysGlobal.ENTIDAD_OSPIM %>"><%=WebKeysGlobal.ENTIDAD_OSPIM%></option>
										<%
										}
										else {
											for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {
									%>
										<c:if test="<%=((showOspim && entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_OSPIM)) ||
														(showAmtima && entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_AMTIMA)) ||
														(showUoma && entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_UOMA)))%>">									
											<option value="<%= entidad %>"><%=entidad%></option>
											<%= entidad == WebKeysLiquidaciones.ID_DEFAULT_ENTIDAD ? "selected" : ""  %>
										</c:if>
									<%
											}
										}
									%>
							</select>
						</td>
						<td><label><liferay-ui:message key="numero-afi" />:</label></td>
						<td><input id="<portlet:namespace />numero_afi" name="<portlet:namespace />numero_afi" size="6" maxlength="10" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td><label><liferay-ui:message key="cuil" />:</label></td>
						<td><input id="<portlet:namespace />cuil" name="<portlet:namespace />cuil" size="13" maxlength="11" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td><label><liferay-ui:message key="integrante" />:</label></td>
						<td><input id="<portlet:namespace />inte" name="<portlet:namespace />inte" size="2" maxlength="2" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td><label><liferay-ui:message key="tipo-documento" />:</label>&nbsp;&nbsp;
							<select name="<portlet:namespace/>tipoDoc" id="<portlet:namespace/>tipoDoc">
									<option value=""></option>
									<%
										for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {
									%>
										<option value="<%= tipoDoc %>"><%=tipoDoc%></option>
									<%
									}
									%>
							</select>&nbsp;&nbsp;&nbsp;&nbsp;						
						<label><liferay-ui:message key="nro-documento" />:</label>&nbsp;&nbsp;
						<input id="<portlet:namespace />nroDoc" name="<portlet:namespace />nroDoc" size="9" maxlength="8" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
					</tr>
				
					<% if(seccionalFijada==0){%>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="seccional" />:</label></td>														
						<td colspan="4" style="vertical-align:top" ><jsp:include page='/html/portlet/liquidaciones/busqueda_seccional.jsp'/></td>
						<td colspan="4">
						<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
							<label><liferay-ui:message key="plan" />:</label>&nbsp;&nbsp;<input type="text" readonly="readonly" id="<portlet:namespace />nombre_plan" name="<portlet:namespace />nombre_plan" />
						</c:if>
						&nbsp;&nbsp;
						<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
							<label>Tercerizadora:</label>&nbsp;&nbsp;<input type="text" readonly="readonly" id="<portlet:namespace />afi_tercerizadora" name="<portlet:namespace />afi_tercerizadora" />
						</c:if>
						&nbsp;
						<label id="<portlet:namespace />discapacidad" style="display: none;"><font style="color: red">Discapacitado</font></label>
						</td>
						
					</tr>
					<%} %>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="apellido" />: </label></td>
						<td colspan="2"><input id="<portlet:namespace />apellido" name="<portlet:namespace />apellido" size="20" maxlength="100" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td><label><liferay-ui:message key="nombre" />:</label></td>
						<td colspan="2"><input id="<portlet:namespace />nombre" name="<portlet:namespace />nombre" size="20" maxlength="100" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td colspan="2">												
						<label><liferay-ui:message key="baja-fecha" />:</label>&nbsp;&nbsp;<input type="text" readonly="readonly" id="<portlet:namespace />baja_fecha" name="<portlet:namespace />baja_fecha" />
						</td>
						<td colspan="2">
						<c:if test="<%= Boolean.parseBoolean(edit_mode) %>">
							<input id="<portlet:namespace />buscarAfiliado" value="<liferay-ui:message key="buscar-afiliado"/>" title="<liferay-ui:message key="buscar-afiliado" />" type="button" onClick="javascript:<portlet:namespace />buscarAfiliados();"/>
						</c:if>
						&nbsp;
						<c:if test="<%= Boolean.parseBoolean(edit_mode) %>">
							<input id="<portlet:namespace />limpiarCampos" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="buscar-afiliado" />" type="button" onClick="javascript:<portlet:namespace />limpiarCamposAfiliado();"/>
						</c:if>
						<c:if test="<%= Boolean.parseBoolean(discapacidad) %>">
							<input id="<portlet:namespace />detalle_discapacidad" value="Detalle Discapacidad" title="Detalle Discapacidad" type="button" onClick="javascript:<portlet:namespace />detalleDiscapacidad();"/>
						</c:if>
						</td>
					</tr>
				</table>
				<input id="<portlet:namespace />fecha_alta_af" value="" type="hidden" name="<portlet:namespace />fecha_alta_af"/>
				<input id="<portlet:namespace />incapacidad_af" value="" type="hidden" name="<portlet:namespace />incapacidad_af"/>
			
<script type="text/javascript">
	var popupAfill; 		
	var popupdd;
	
	function <portlet:namespace />buscarAfiliados(){
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var tipoDoc=jQuery('#<portlet:namespace />tipoDoc').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();
		/* var seccional=jQuery('#<portlet:namespace />id_seccional').val(); */		
		var apellido=jQuery('#<portlet:namespace />apellido').val();
		var nombre=jQuery('#<portlet:namespace />nombre').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();		
		var fecha_prestacion;
		
		<% if(seccionalFijada==0){%>
			var seccional=jQuery('#<portlet:namespace />id_seccional').val();		
			var seccional_nombre=jQuery('#<portlet:namespace />seccional').val();
		<%}else{%>
			var seccional="<%=seccionalFijada%>";		
			var seccional_nombre="";
		<%}%>
	
		if(!<portlet:namespace />validarBusqueda(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi)){
			return false;
		}
		if(cuil.length>0){
			if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil').focus();
				return false;
			}
		}
		
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
			jQuery("#<portlet:namespace />seccional").val("");
			jQuery("#<portlet:namespace />id_seccional").val("");
		}
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />",modal:true,width:830});
		
		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COR_1_"))%>'>	
	        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
			'&fecha_referencia='+fecha_prestacion+'&popup=true';
	    </c:if>
     
        jQuery(popupAfill).load(url);
	}

	function <portlet:namespace />buscarAfiliados_(fecha_prest){
		//alert('función buscando, fecha' + fecha_prest);
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var tipoDoc=jQuery('#<portlet:namespace />tipoDoc').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional').val();		
		var apellido=jQuery('#<portlet:namespace />apellido').val();
		var nombre=jQuery('#<portlet:namespace />nombre').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		if(!<portlet:namespace />validarBusqueda(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi)){
			return false;
		}
		if(cuil.length>0){
			if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil').focus();
				return false;
			}
		}		
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
			jQuery("#<portlet:namespace />seccional").val("");
			jQuery("#<portlet:namespace />id_seccional").val("");
		}
		var fecha_prestacion = fecha_prest;
		try {
			fecha_prestacion = jQuery("#<portlet:namespace />fprest").val();
		}
			catch (err) 
			{
				fecha_prestacion = 'null'; 
			}			
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />",modal:true,width:830});
		<c:if test="<%= !Boolean.parseBoolean(pag_reintegro) %>">		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+'&popup=true&fecha_referencia='+fecha_prestacion;
			//alert ('no reintegros');
        </c:if>
        <c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">        		
    		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
    		var ext = '';
    		<c:if test="<%= tipo_reintegro != null && tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) %>">
				ext = '&ext=1';
    		</c:if>

    		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
			'&fecha_referencia='+fecha_prestacion+'&popup=true'+ext;
			
    		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COR_1_"))%>'> 
		        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/buscar_afiliados&cuil='+cuil+
				'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
				'&fecha_referencia='+fecha_prestacion+'&popup=true'+ext;
		     </c:if>			
        </c:if>
        jQuery(popupAfill).load(url);
	}
	
	function <portlet:namespace />validarBusqueda(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi){			
		if(trim(cuil.length)==0 && trim(inte.length)==0 && trim(tipoDoc.length)==0 && trim(nroDoc.length)==0 && trim(seccional.length)==0 &&  
		   trim(apellido.length)==0 && trim(nombre.length)==0 && trim(entidad.length)==0 && trim(numero_afi.length)==0){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}				
	
	function seleccionaAfiliado(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af,afi_tercerizadora){
		seleccionaCamposAfiliado(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af,afi_tercerizadora);
		Liferay.Popup.close(popupAfill);
	}
	
	function seleccionaCamposAfiliado(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af,afi_tercerizadora){			
		jQuery('#<portlet:namespace />cuil').val(cuil);
		jQuery('#<portlet:namespace />inte').val(inte);
		jQuery('#<portlet:namespace />tipoDoc').val(docu_tipo);
		jQuery('#<portlet:namespace />nroDoc').val(docu_nro);
		jQuery('#<portlet:namespace />id_seccional').val(id_secc);
		jQuery('#<portlet:namespace />seccional').val(desc_secc);		
		jQuery('#<portlet:namespace />apellido').val(apellido);
		jQuery('#<portlet:namespace />nombre').val(nombre);
		if (jQuery('#<portlet:namespace />entidad').val() == '<%= WebKeysGlobal.ENTIDADES_UOMA[0] %>') {
			jQuery('#<portlet:namespace />numero_afi').val(ospim);
		}
		if (jQuery('#<portlet:namespace />entidad').val() == '<%= WebKeysGlobal.ENTIDADES_UOMA[1] %>') {
			jQuery('#<portlet:namespace />numero_afi').val(uoma);
		}
		if (jQuery('#<portlet:namespace />entidad').val() == '<%= WebKeysGlobal.ENTIDADES_UOMA[2] %>') {
			jQuery('#<portlet:namespace />numero_afi').val(amtima);
		}
		jQuery("#<portlet:namespace />secc_seleccionada").val("1");
		if (document.getElementById("<portlet:namespace />baja_fecha")!= null && bajaFecha!= null){
			document.getElementById("<portlet:namespace />baja_fecha").value = bajaFecha;		
		}
		<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
			if (jQuery("#<portlet:namespace />id_seccional_r").val() == "") {
				jQuery("#<portlet:namespace />id_seccional_r").val(id_secc);
    			jQuery("#<portlet:namespace />seccional_r").val(desc_secc);
    			jQuery("#<portlet:namespace />secc_seleccionada_r").val("1");
			}
			if (nombre_plan == 'null') { 
				nombre_plan = '';
			}
			if (afi_tercerizadora == 'null') {
				afi_tercerizadora = ''
			}
			jQuery("#<portlet:namespace />nombre_plan").val(nombre_plan);
			jQuery("#<portlet:namespace />afi_tercerizadora").val(afi_tercerizadora);
		</c:if>
		jQuery("#<portlet:namespace />fecha_alta_af").val(fecha_alta_af);

		jQuery("#<portlet:namespace />incapacidad_af").val(incapacidad_af);
		try {			
			if (jQuery("#<portlet:namespace />incapacidad_af").val() == '1') {
				jQuery('#<portlet:namespace />div_tratamientos_discapacidad').show();
				jQuery('#<portlet:namespace />discapacidad').show(); 
			} else {
				jQuery('#<portlet:namespace />div_tratamientos_discapacidad').hide();
				jQuery('#<portlet:namespace />discapacidad').hide();
			}
		}
		catch (err) {}
		<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
			//llamar script que busca los tratamientos del afiliado en la página
		</c:if>			
	}

	function <portlet:namespace />resetValid() {
		if (jQuery("#<portlet:namespace />id_seccional").val() != "") {
			jQuery("#<portlet:namespace />secc_seleccionada").val("1")
		}
	}

	var cuilJS = "<%= cuil%>";
	var inteJS = "<%= inte%>";
	if (trim(cuilJS) != "" && trim(inteJS) != ""){
		document.getElementById("<portlet:namespace />cuil").value = cuilJS;
		document.getElementById("<portlet:namespace />inte").value = inteJS;
		<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
			//alert ('cargando afiliado, fecha no puede ser undefined' + jQuery("#<portlet:namespace />fprest").val());
			<portlet:namespace />buscarAfiliados_(jQuery("#<portlet:namespace />fprest").val());
		</c:if>
		<c:if test="<%= !Boolean.parseBoolean(pag_reintegro) %>">
			//alert ('undefined' + jQuery("#<portlet:namespace />fprest").val());
			<portlet:namespace />buscarAfiliados();
		</c:if>
	}
	 
	<portlet:namespace />resetValid();

	function <portlet:namespace />limpiarCamposAfiliado() {
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');
		jQuery('#<portlet:namespace />tipoDoc').val('');
		jQuery('#<portlet:namespace />nroDoc').val('');
		jQuery('#<portlet:namespace />id_seccional').val('');
		jQuery('#<portlet:namespace />seccional').val('');		
		jQuery('#<portlet:namespace />apellido').val('');
		jQuery('#<portlet:namespace />nombre').val('');
		//jQuery('#<portlet:namespace />entidad').val('');
		document.getElementById('<portlet:namespace />entidad').selectedIndex  = 0;
		jQuery('#<portlet:namespace />numero_afi').val('');
		jQuery("#<portlet:namespace />secc_seleccionada").val("1");
		jQuery("#<portlet:namespace />baja_fecha").val('');
		<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
			jQuery("#<portlet:namespace />nombre_plan").val('');
			jQuery("#<portlet:namespace />afi_tercerizadora").val('');
		</c:if>
		jQuery("#<portlet:namespace />fecha_alta_af").val('');
		jQuery("#<portlet:namespace />incapacidad_af").val('');	
		jQuery("#<portlet:namespace />discapacidad").hide();	
	}

	function <portlet:namespace />detalleDiscapacidad() {
		if (jQuery("#<portlet:namespace />incapacidad_af").val() != '1') {
			alert ("Debe seleccionar un afiliado discapacitado");
			return false;
		}
		var cuil = jQuery('#<portlet:namespace />cuil').val();
		var inte = jQuery('#<portlet:namespace />inte').val();
		if (trim(cuil).length == 0 || trim(inte).length == 0) {
			alert ("Primero debe seleccionar un afiliado");
			return false;
		}
		popupdd = Liferay.Popup({title:"<liferay-ui:message key="det-discap" />",modal:true,width:870});
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/detalle_discapacidad&cuil_titular='+cuil+'&inte='+inte+'&path=/liquidaciones/grabar_detalle_discapacidad';
		jQuery(popupdd).load(url);
	}

	function <portlet:namespace />reloadPopupDetalle() {
		Liferay.Popup.close(popupdd);
		<portlet:namespace />detalleDiscapacidad();
	}
			
</script>
