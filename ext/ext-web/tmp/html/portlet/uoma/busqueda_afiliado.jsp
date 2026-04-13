<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<% 
	String edit_mode = ParamUtil.getString(request, "edit_mode", null); 
	String pag_reintegro = ParamUtil.getString(request, "pag_reintegro_farmacia", null);	
	if (pag_reintegro != null) {
		pag_reintegro = "true";
	}
	else {
		pag_reintegro = "false";
	}
	Afiliado afiliado = (Afiliado) request.getAttribute("afiliado_edit");
		
	String cuil = ParamUtil.getString(request, "cuil", "");
	String inte = ParamUtil.getString(request, "inte", "");
	String apellido = ParamUtil.getString(request, "apellido", "");
	String nombre = ParamUtil.getString(request, "nombre", "");
	//String entiAfi = ParamUtil.getString(request, "entiAfi", "");
	String nroAfi = ParamUtil.getString(request, "nroAfi", "");
	String tipoDocReq = ParamUtil.getString(request, "tipoDoc", "");
	String nroDoc = ParamUtil.getString(request, "nroDoc", "");
	String baja_Fecha = ParamUtil.getString(request,"bajaFecha", "");
	String id_seccional=ParamUtil.getString(request,"id_seccional","");
	
	String portlet_name=null;
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "liquidaciones";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}
	String prefijo=ParamUtil.getString(request,"prefijo","");
%>
<portlet:defineObjects/>							
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="entidad" />:</label></td>
						<td>
							<select name="<portlet:namespace/>entidad<%=prefijo%>" id="<portlet:namespace/>entidad<%=prefijo%>" <%= !Boolean.parseBoolean(edit_mode) ? " disabled='true'" : ""  %>>
									<%

									for (String entidad : WebKeysPortal.ENTIDADES_UOMA) {
									%>
																			
										<option value="<%= entidad %>" <%if((portlet_name.equals("uoma") && entidad.equals("U.O.M.A.")) ||
																		    (portlet_name.equals("farmacia") && entidad.equals("A.M.T.I.M.A."))){ %>
																		selected<%}%>><%=entidad%></option>											
										
									<%
										}
									%>
							</select>
						</td>
						<td><label><liferay-ui:message key="numero-afi" />:</label></td>
						<td><input id="<portlet:namespace />numero_afi<%=prefijo%>" name="<portlet:namespace />numero_afi<%=prefijo%>" size="6" maxlength="10" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="cuil" />:</label></td>
						<td><input id="<portlet:namespace />cuil<%=prefijo%>" name="<portlet:namespace />cuil<%=prefijo%>" size="13" maxlength="11" type="text" value="<%=afiliado!=null?afiliado.getCuil_titular():""%>" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="integrante" />:</label></td>
						<td><input id="<portlet:namespace />inte<%=prefijo%>" name="<portlet:namespace />inte<%=prefijo%>" size="2" maxlength="2" type="text" value="<%=afiliado!=null?afiliado.getInte():"0"%>" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td><label><liferay-ui:message key="tipo-documento" />:</label>&nbsp;
							<select name="<portlet:namespace/>tipoDoc<%=prefijo%>" id="<portlet:namespace/>tipoDoc<%=prefijo%>">
									<option value=""></option>
									<%
										for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {
									%>
										<option value="<%= tipoDoc %>"<%if(null!=afiliado && tipoDoc.equals(afiliado.getDocumento_tipo())){%> selected<%}%>><%=tipoDoc%></option>
									<%
									}
									%>
							</select>
						</td>
						<td><label><liferay-ui:message key="nro-documento" />:</label>&nbsp;<input id="<portlet:namespace />nroDoc<%=prefijo%>" name="<portlet:namespace />nroDoc<%=prefijo%>" size="9" maxlength="13" type="text" value="<%=afiliado!=null?afiliado.getDocu_numero():""%>" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td><label><liferay-ui:message key="seccional" />:</label></td>		
						<td colspan="4" rowspan="3" style="vertical-align:top" >							
							<liferay-util:include page='/html/portlet/uoma/busqueda_seccional.jsp'>
								<liferay-util:param name="id_seccional" value='<%= afiliado != null?String.valueOf(afiliado.getSeccional().getId()):""%>'/>								
		  						<liferay-util:param name="seccional" value='<%= afiliado != null?afiliado.getSeccional().getDescripcion():""%>'/>
							</liferay-util:include>
						</td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="apellido" />: </label></td>
						<td colspan="2"><input id="<portlet:namespace />apellido<%=prefijo%>" name="<portlet:namespace />apellido<%=prefijo%>" size="20" maxlength="100" type="text" value="<%=afiliado!=null?afiliado.getApellido():""%>" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td><label><liferay-ui:message key="nombre" />:</label></td>
						<td colspan="2"><input id="<portlet:namespace />nombre<%=prefijo%>" name="<portlet:namespace />nombre<%=prefijo%>" size="20" maxlength="100" type="text" value="<%=afiliado!=null?afiliado.getNombre():""%>" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td colspan="8">						
												
						<label><liferay-ui:message key="baja-fecha" />:</label><input type="text" readonly="readonly" id="<portlet:namespace />baja_fecha<%=prefijo%>" value="<%=(afiliado!=null&&null!=afiliado.getBaja_fecha())?afiliado.getBaja_fecha():""%>" name="<portlet:namespace />baja_fecha<%=prefijo%>" />
						<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
							<label><liferay-ui:message key="plan" />:</label><input type="text" readonly="readonly" id="<portlet:namespace />nombre_plan<%=prefijo%>" name="<portlet:namespace />nombre_plan<%=prefijo%>" />
						</c:if>						
						</td>
						<td coslpan="2">
						<c:if test="<%= Boolean.parseBoolean(edit_mode) %>">
							<input id="<portlet:namespace />buscarAfiliado<%=prefijo%>" value="<liferay-ui:message key="buscar-afiliado"/>" title="<liferay-ui:message key="buscar-afiliado" />" type="button" onClick="javascript:<portlet:namespace />buscarAfiliados();"/>
						</c:if>
						&nbsp;
						<c:if test="<%= Boolean.parseBoolean(edit_mode) %>">
							<input id="<portlet:namespace />limpiarCampos<%=prefijo%>" value="Limpiar" title="<liferay-ui:message key="buscar-afiliado" />" type="button" onClick="javascript:<portlet:namespace />limpiarCamposAfiliado();"/>
						</c:if>
						&nbsp;
						</td>						
					</tr>
				</table>
				<input id="<portlet:namespace />fecha_alta_af<%=prefijo%>" value="" type="hidden" name="<portlet:namespace />fecha_alta_af<%=prefijo%>"/>
				<input id="<portlet:namespace />incapacidad_af<%=prefijo%>" value="" type="hidden" name="<portlet:namespace />incapacidad_af<%=prefijo%>"/>
			    
<script type="text/javascript">
	var popupAfill;		
	
	function <portlet:namespace />buscarAfiliados(){
		var cuil=jQuery('#<portlet:namespace />cuil<%=prefijo%>').val();
		var inte=jQuery('#<portlet:namespace />inte<%=prefijo%>').val();
		var tipoDoc=jQuery('#<portlet:namespace />tipoDoc<%=prefijo%>').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc<%=prefijo%>').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional_afiliado<%=prefijo%>').val();		
		var apellido=jQuery('#<portlet:namespace />apellido<%=prefijo%>').val();
		var nombre=jQuery('#<portlet:namespace />nombre<%=prefijo%>').val();
		var entidad=jQuery('#<portlet:namespace />entidad<%=prefijo%>').val();		
		var numero_afi=jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').val();		
		if(!<portlet:namespace />validarBusquedaAfiliado(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi)){
			return false;
		}		
		if(cuil.length>0){
			if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil').focus();
				return false;
			}
		}		
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada_afiliado<%=prefijo%>").val()!="1"){
			jQuery("#<portlet:namespace />seccional_afiliado<%=prefijo%>").val("");			
			jQuery("#<portlet:namespace />id_seccional_afiliado<%=prefijo%>").val("");
		}
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />",modal:true,width:830});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/buscar_afiliados&cuil='+cuil+
		'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+'&popup=true';			
        jQuery(popupAfill).load(url);
	}
	
	function <portlet:namespace />validarBusquedaAfiliado(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi){		
		if(trim(cuil.length)==0 && trim(inte.length)==0 && trim(tipoDoc.length)==0 && trim(nroDoc.length)==0 && trim(seccional.length)==0 &&  
		   trim(apellido.length)==0 && trim(nombre.length)==0 && trim(entidad.length)==0 && trim(numero_afi.length)==0){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}

	var popupda;	
	function <portlet:namespace />documentacionAdjunta() {
		var cuil = jQuery('#<portlet:namespace />cuil<%=prefijo%>').val();
		var inte = jQuery('#<portlet:namespace />inte<%=prefijo%>').val();		
		if (cuil == '') {
			return;
		}
		if (inte == '') {
			inte = 0;
		}
		popupda = Liferay.Popup({title:"<liferay-ui:message key="documentacion-adjunta" />",modal:true,width:900});
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/documentacion_adjunta&cuil_titular='+cuil+'&inte='+inte+'&view=true';
		jQuery(popupda).load(url);
	}
	
	function seleccionaAfiliado(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af){
		seleccionaCamposAfiliado(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af);
		Liferay.Popup.close(popupAfill);
	}
	
	function seleccionaCamposAfiliado(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af){	
		jQuery('#<portlet:namespace />cuil<%=prefijo%>').val(cuil);
		jQuery('#<portlet:namespace />inte<%=prefijo%>').val(inte);
		jQuery('#<portlet:namespace />tipoDoc<%=prefijo%>').val(docu_tipo);
		jQuery('#<portlet:namespace />nroDoc<%=prefijo%>').val(docu_nro);		
		jQuery('#<portlet:namespace />id_seccional_afiliado<%=prefijo%>').val(id_secc);
		jQuery('#<portlet:namespace />seccional_afiliado<%=prefijo%>').val(desc_secc);		
		jQuery('#<portlet:namespace />apellido<%=prefijo%>').val(apellido);
		jQuery('#<portlet:namespace />nombre<%=prefijo%>').val(nombre);
		
		if (jQuery('#<portlet:namespace />entidad<%=prefijo%>').val() == '<%= WebKeysPortal.ENTIDADES_UOMA[0] %>') {
			jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').val(ospim);
		}
		if (jQuery('#<portlet:namespace />entidad<%=prefijo%>').val() == '<%= WebKeysPortal.ENTIDADES_UOMA[1] %>') {
			jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').val(uoma);
		}
		if (jQuery('#<portlet:namespace />entidad<%=prefijo%>').val() == '<%= WebKeysPortal.ENTIDADES_UOMA[2] %>') {
			jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').val(amtima);
		}
		jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val("1");
		if (document.getElementById("<portlet:namespace />baja_fecha<%=prefijo%>")!= null && bajaFecha!= null){
			document.getElementById("<portlet:namespace />baja_fecha<%=prefijo%>").value = bajaFecha;
		}
		<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
			if (jQuery("#<portlet:namespace />id_seccional_r<%=prefijo%>").val() == "") {
				jQuery("#<portlet:namespace />id_seccional_r<%=prefijo%>").val(id_secc);
    			jQuery("#<portlet:namespace />seccional_r<%=prefijo%>").val(desc_secc);
    			jQuery("#<portlet:namespace />secc_seleccionada_r<%=prefijo%>").val("1");
			}
			if (nombre_plan == 'null') { 
				nombre_plan = '';
			}
			jQuery("#<portlet:namespace />nombre_plan<%=prefijo%>").val(nombre_plan);			
		</c:if>
		jQuery("#<portlet:namespace />fecha_alta_af<%=prefijo%>").val(fecha_alta_af);
		jQuery("#<portlet:namespace />incapacidad_af<%=prefijo%>").val(incapacidad_af);
		//DESHABILITO
		jQuery('#<portlet:namespace />entidad<%=prefijo%>').attr('disabled',true);
		jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').attr('readonly', true);  
		jQuery("#<portlet:namespace />cuil<%=prefijo%>").attr('readonly', true);
		jQuery("#<portlet:namespace />inte<%=prefijo%>").attr('readonly', true);
		jQuery('#<portlet:namespace />tipoDoc<%=prefijo%>').attr('disabled',true);
		jQuery("#<portlet:namespace />nroDoc<%=prefijo%>").attr('readonly', true);		
		jQuery("#<portlet:namespace />id_seccional_afiliado<%=prefijo%>").attr('readonly', true);
		jQuery("#<portlet:namespace />seccional_afiliado<%=prefijo%>").attr('readonly', true);
		jQuery("#<portlet:namespace />apellido<%=prefijo%>").attr('readonly', true);
		jQuery("#<portlet:namespace />nombre<%=prefijo%>").attr('readonly', true);
		jQuery("#<portlet:namespace />baja_fecha<%=prefijo%>").attr('readonly', true);		
	}

	function <portlet:namespace />resetValid() {
		if (jQuery("#<portlet:namespace />id_seccional_afiliado<%=prefijo%>").val() != "") {
			jQuery("#<portlet:namespace />secc_seleccionada_afiliado<%=prefijo%>").val("1")
		}
	}
	
	var cuilJS = "<%= cuil%>";
	var inteJS = "<%= inte%>";
	if (trim(cuilJS) != "" && trim(inteJS) != ""){		
		document.getElementById("<portlet:namespace />cuil<%=prefijo%>").value = cuilJS;
		document.getElementById("<portlet:namespace />inte<%=prefijo%>").value = inteJS;
		<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
			//alert ('cargando afiliado, fecha no puede ser undefined' + jQuery("#<portlet:namespace />fprest").val());
			<portlet:namespace />buscarAfiliados(jQuery("#<portlet:namespace />fprest<%=prefijo%>").val());
		</c:if>
		<c:if test="<%= !Boolean.parseBoolean(pag_reintegro) %>">
			//alert ('undefined' + jQuery("#<portlet:namespace />fprest").val());
			<portlet:namespace />buscarAfiliados();
		</c:if>
	} else {		
		//jQuery('#<portlet:namespace />id_seccional').val('');
		//jQuery('#<portlet:namespace />seccional').val('');		
		//jQuery("#<portlet:namespace />secc_seleccionada").val("");
	}
	<portlet:namespace />resetValid();

	function <portlet:namespace />limpiarCamposAfiliado() {
		if('<%=pag_reintegro%>'=='true'){
			if(!confirm('<liferay-ui:message key="desea-cambiar-afiliado-y-perder-lista-medicamentos"/>')){
				return false;
			}
			<portlet:namespace />limpiarCamposMedicamento();
			borraMedicamento('all'); 
		}
		jQuery('#<portlet:namespace />cuil<%=prefijo%>').val('');
		jQuery('#<portlet:namespace />inte<%=prefijo%>').val('');
		jQuery('#<portlet:namespace />tipoDoc<%=prefijo%>').val('');
		jQuery('#<portlet:namespace />nroDoc<%=prefijo%>').val('');
		
		jQuery('#<portlet:namespace />id_seccional_afiliado<%=prefijo%>').val('');
		jQuery('#<portlet:namespace />seccional_afiliado<%=prefijo%>').val('');		
		jQuery('#<portlet:namespace />apellido<%=prefijo%>').val('');
		jQuery('#<portlet:namespace />nombre<%=prefijo%>').val('');
		//jQuery('#<portlet:namespace />entidad').val('');
		document.getElementById('<portlet:namespace />entidad<%=prefijo%>').selectedIndex  = 0;
		jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').val('');
		jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val("1");
		jQuery("#<portlet:namespace />baja_fecha<%=prefijo%>").val('');
		<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
			jQuery("#<portlet:namespace />nombre_plan<%=prefijo%>").val('');
		</c:if>
		jQuery("#<portlet:namespace />fecha_alta_af<%=prefijo%>").val('');
		jQuery("#<portlet:namespace />incapacidad_af<%=prefijo%>").val('');
		//HABILITO
		jQuery('#<portlet:namespace />entidad<%=prefijo%>').attr('disabled',false);
		jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').attr('readonly', false);  
		jQuery("#<portlet:namespace />cuil<%=prefijo%>").attr('readonly', false);
		jQuery("#<portlet:namespace />inte<%=prefijo%>").attr('readonly', false);
		jQuery('#<portlet:namespace />tipoDoc<%=prefijo%>').attr('disabled',false);
		jQuery("#<portlet:namespace />nroDoc<%=prefijo%>").attr('readonly', false);
		
		jQuery("#<portlet:namespace />id_seccional_afiliado<%=prefijo%>").attr('readonly', false);
		jQuery("#<portlet:namespace />seccional_afiliado<%=prefijo%>").attr('readonly', false);
		jQuery("#<portlet:namespace />apellido<%=prefijo%>").attr('readonly', false);
		jQuery("#<portlet:namespace />nombre<%=prefijo%>").attr('readonly', false);
		jQuery("#<portlet:namespace />baja_fecha<%=prefijo%>").attr('readonly', false);					
	}
</script>
