<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%
	String edit_mode = ParamUtil.getString(request, "edit_mode", null);
	String discapacidad = ParamUtil.getString(request, "discapacidad", null);
	String pag_reintegro = ParamUtil.getString(request, "pag_reintegro", null);
	String prefijo=ParamUtil.getString(request, "origen","");

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
	
%>

<portlet:defineObjects/>
<div id="<portlet:namespace />contenedorAfiliado<%=prefijo%>">
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		<style type="text/css">
	.afiliado-con-antecedentes {
		background: #ff4d4d !important;
		padding: 6px;
	}

	.afiliado-con-antecedentes label,
	.afiliado-con-antecedentes span,
	.afiliado-con-antecedentes td {
		color: #ffffff !important;
	}

	.afiliado-con-antecedentes input[type="text"],
	.afiliado-con-antecedentes select {
		background: #fff5f5 !important;
		color: #000000 !important;
		border-color: #cc0000 !important;
	}

	.afiliado-con-antecedentes #<portlet:namespace />incidente label,
	.afiliado-con-antecedentes #<portlet:namespace />fechaIncidente {
		color: #ffffff !important;
	}
</style>
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
					<tr>
						<td><label><liferay-ui:message key="entidad" />:</label></td>
						<td>
							<select name="<portlet:namespace/>entidad<%=prefijo%>" id="<portlet:namespace/>entidad<%=prefijo%>" <%= !Boolean.parseBoolean(edit_mode) ? " disabled='true'" : ""  %>>
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
						<td><input id="<portlet:namespace />numero_afi<%=prefijo%>" name="<portlet:namespace />numero_afi<%=prefijo%>" size="6" maxlength="10" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td><label><liferay-ui:message key="cuil" />:</label></td>
						<td><input id="<portlet:namespace />cuil<%=prefijo%>" name="<portlet:namespace />cuil<%=prefijo%>" size="13" maxlength="11" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td><label><liferay-ui:message key="integrante" />:</label></td>
						<td><input id="<portlet:namespace />inte<%=prefijo%>" name="<portlet:namespace />inte<%=prefijo%>" size="2" maxlength="2" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td><label><liferay-ui:message key="tipo-documento" />:</label>
							<select name="<portlet:namespace/>tipoDoc<%=prefijo%>" id="<portlet:namespace/>tipoDoc<%=prefijo%>">
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
						<td><input id="<portlet:namespace />nroDoc<%=prefijo%>" name="<portlet:namespace />nroDoc<%=prefijo%>" size="9" maxlength="8" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="seccional" />:</label></td>														
						<td colspan="4" style="vertical-align:top" >
						       <liferay-util:include page='/html/portlet/autorizaciones/busqueda_seccional.jsp'>
						       <liferay-util:param value="<%=prefijo%>" name="prefijo" />
						       </liferay-util:include>
						</td>
						
						<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
						<td colspan="4">
							<table>
								<tr>
									<td><label><liferay-ui:message key="plan" />:</label></td> 
									<td><input type="text" readonly="readonly" id="<portlet:namespace />nombre_plan<%=prefijo%>" name="<portlet:namespace />nombre_plan<%=prefijo%>" /></td>
									<td><label>Tercerizadora:</label></td> 
									<td><input type="text" readonly="readonly" id="<portlet:namespace />afi_tercerizadora<%=prefijo%>" name="<portlet:namespace />afi_tercerizadora<%=prefijo%>" /></td>
								</tr>
							</table>
						</td>			
						</c:if>
						<c:if test="<%= !Boolean.parseBoolean(pag_reintegro) %>">
							<td colspan="4">&nbsp;</td>
						</c:if>
						<td><label id="<portlet:namespace />discapacidad" style="display: none;"><font style="color: red">Discapacitado</font></label></td>
						<td><label id="<portlet:namespace />discapacidad_vto" style="display: none;">Vto. Certificado: </font></label></td>
						
					</tr>
					
					<tr>
						<td><label><liferay-ui:message key="apellido" />:</label></td>
						<td colspan="2"><input id="<portlet:namespace />apellido<%=prefijo%>" name="<portlet:namespace />apellido<%=prefijo%>" size="20" maxlength="100" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td><label><liferay-ui:message key="nombre" />:</label></td>
						<td colspan="2"><input id="<portlet:namespace />nombre<%=prefijo%>" name="<portlet:namespace />nombre<%=prefijo%>" size="20" maxlength="100" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<%--
						<td colspan="1"><label><liferay-ui:message key="nro-socio-prevencion" />:</label></td>
						<td ><input id="<portlet:namespace />nroSocioPrevencion<%=prefijo%>" name="<portlet:namespace />nroSocioPrevencion<%=prefijo%>"size="6" maxlength="10" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						<td colspan="1" ><label><liferay-ui:message key="nro-credencial-prevencion" />:</label></td>
						<td ><input id="<portlet:namespace />nroCredencialPrevencion<%=prefijo%>" name="<portlet:namespace />nroCredencialPrevencion<%=prefijo%>" size="11" maxlength="11" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
						 --%>
						<td colspan="4">
							<input id="<portlet:namespace />nroSocioPrevencion<%=prefijo%>" name="<portlet:namespace />nroSocioPrevencion<%=prefijo%>" type="hidden" value=""/>
							<input id="<portlet:namespace />nroCredencialPrevencion<%=prefijo%>" name="<portlet:namespace />nroCredencialPrevencion<%=prefijo%>" type="hidden" value=""/>
						<label><liferay-ui:message key="baja-fecha" />:</label>
						<!-- </td>
						<td>  -->
							<input type="text" readonly="readonly" id="<portlet:namespace />baja_fecha<%=prefijo%>" name="<portlet:namespace />baja_fecha<%=prefijo%>" />
						</td>
						<td colspan="3">&nbsp;</td>	
					</tr>
					
					     <tr>	
						<!-- td  colspan="8" align="right" id="<portlet:namespace />incidente">
                        <span style="font-size: 9pt; color: green;  " id="<portlet:namespace />incidente"><label  id="<portlet:namespace />fechaIncidente"><b>  </b></label></span>
		            	</td--> 
						<td colspan="12" align="right">						
						<span style="font-size: 9pt; color: green;  " id="<portlet:namespace />incidente"><label  id="<portlet:namespace />fechaIncidente"><b>  </b></label></span>
						<c:if test="<%= Boolean.parseBoolean(edit_mode) %>">
						<input id="<portlet:namespace />buscarAfiliado" value="<liferay-ui:message key="buscar-afiliado"/>" title="<liferay-ui:message key="buscar-afiliado" />" type="button" onClick="javascript:<portlet:namespace />buscarAfiliados<%=prefijo%>();"/>
						&nbsp;&nbsp;&nbsp;					
						<input id="<portlet:namespace />limpiarCampos" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="buscar-afiliado" />" type="button" onClick="javascript:<portlet:namespace />limpiarCamposAfiliado<%=prefijo%>();"/>
						&nbsp;&nbsp;&nbsp;
						
						</c:if>
						<c:if test="<%= !Boolean.parseBoolean(edit_mode) %>">
							&nbsp;&nbsp;&nbsp;
						</c:if>
						<c:if test="<%= Boolean.parseBoolean(discapacidad) %>">
							<input id="<portlet:namespace />detalle_discapacidad" value="Detalle Discapacidad" title="Detalle Discapacidad" type="button" onClick="javascript:<portlet:namespace />detalleDiscapacidad<%=prefijo%>();"/>	
						</c:if>
						<c:if test="<%= !Boolean.parseBoolean(discapacidad) %>">	
							&nbsp;
						</c:if>
					</tr> 
				</table>
		<input id="<portlet:namespace />fecha_alta_af<%=prefijo%>" value="" type="hidden" name="<portlet:namespace />fecha_alta_af<%=prefijo%>"/>
		<input id="<portlet:namespace />incapacidad_af<%=prefijo%>" value="" type="hidden" name="<portlet:namespace />incapacidad_af<%=prefijo%>"/>
		<input id="<portlet:namespace />id_tercerizadora<%=prefijo%>" value="" type="hidden" name="<portlet:namespace />id_tercerizadora<%=prefijo%>"/>
		<input id="<portlet:namespace />tieneAntecedentes<%=prefijo%>" value="0" type="hidden" name="<portlet:namespace />tieneAntecedentes<%=prefijo%>"/>

</div>

<script type="text/javascript">
	function <portlet:namespace />aplicarMarcaAntecedentes<%=prefijo%>(tieneAntecedentes){
		var $contenedor = jQuery('#<portlet:namespace />contenedorAfiliado<%=prefijo%>');
		var valor = String(tieneAntecedentes);

		if (valor === '1' || valor.toLowerCase() === 'true') {
			$contenedor.addClass('afiliado-con-antecedentes');
		} else {
			$contenedor.removeClass('afiliado-con-antecedentes');
		}
	}
	
	function <portlet:namespace />buscarAfiliados<%=prefijo%>(){
		var cuil=jQuery('#<portlet:namespace />cuil<%=prefijo%>').val();
		var inte=jQuery('#<portlet:namespace />inte<%=prefijo%>').val();
		var tipoDoc=jQuery('#<portlet:namespace />tipoDoc<%=prefijo%>').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc<%=prefijo%>').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional<%=prefijo%>').val();		
		var apellido=jQuery('#<portlet:namespace />apellido<%=prefijo%>').val();
		var nombre=jQuery('#<portlet:namespace />nombre<%=prefijo%>').val();
		var entidad=jQuery('#<portlet:namespace />entidad<%=prefijo%>').val();
		var numero_afi=jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').val();
		var nroCredencialPrevencion=jQuery('#<portlet:namespace />nroCredencialPrevencion<%=prefijo%>').val();
		var nroSocioPrevencion=jQuery('#<portlet:namespace />nroSocioPrevencion<%=prefijo%>').val();
		
		
		
		if(!<portlet:namespace />validarBusqueda<%=prefijo%>(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi)){
			return false;
		}
		if(cuil.length>0){
			if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil-mensaje-limpiar'/>")){
				jQuery('#<portlet:namespace />cuil<%=prefijo%>').focus();
				return false;
			}
		}
		
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val()!="1"){
			jQuery("#<portlet:namespace />seccional<%=prefijo%>").val("");
			jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val("");
		}
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />",modal:true,width:830});
		<c:if test="<%= !Boolean.parseBoolean(pag_reintegro) %>">
			var fecha_prestacion = 'null';
			try {
				fecha_prestacion = jQuery("#<portlet:namespace />fprest<%=prefijo%>").val();
			}
				catch (err) 
				{
					fecha_prestacion = 'null'	; 
				}											
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+'&popup=true&fecha_referencia='+fecha_prestacion;
			
		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COR_1_"))%>'>	
	        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
			'&fecha_referencia='+fecha_prestacion+'&popup=true';
	    </c:if>
	    /* Para zafar con crm */
	    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_AFI_1_"))%>'>	
        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliados&cuil='+cuil+
		'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
		'&fecha_referencia='+fecha_prestacion+'&popup=true';
    	</c:if>
        /*fin zafar*/
        <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_TES_1_"))%>'>
               url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/buscar_afiliados&cuil='+cuil+
		       '&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
		       '&fecha_referencia='+fecha_prestacion+'&origen=<%=prefijo%>&popup=true';
            </c:if>
        
        </c:if>
        <c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">

        	var fecha_prestacion = 'null';
        	<c:if test="<%= !Boolean.parseBoolean(discapacidad) %>">
        		fecha_prestacion = jQuery("#<portlet:namespace />fprest").val();        				
			</c:if>
			<c:if test='<%= Boolean.parseBoolean(discapacidad) || renderResponse.getNamespace().equals("_AUT_1_")%>'>
				var d = new Date();
				var curr_date = d.getDate();
			    var curr_month = d.getMonth() + 1; //Months are zero based
			    var curr_year = d.getFullYear();
			    fecha_prestacion = curr_date + "/" + curr_month + "/" + curr_year;								
			</c:if>
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
			'&fecha_referencia='+fecha_prestacion+'&nroCredencialPrevencion='+nroCredencialPrevencion+'&nroSocioPrevencion='+nroSocioPrevencion+'&popup=true';
			<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COR_1_"))%>'>	
		        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/buscar_afiliados&cuil='+cuil+
				'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
				'&fecha_referencia='+fecha_prestacion+'&popup=true';
		    </c:if>
		    
		    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_AUT_1_"))%>'>
	            url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliados&cuil='+cuil+
			    '&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
			    '&fecha_referencia='+fecha_prestacion+'&nroCredencialPrevencion='+nroCredencialPrevencion+'&nroSocioPrevencion='+nroSocioPrevencion
			    +'&origen=<%=prefijo%>&popup=true';
	        </c:if>
			
	        <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_TES_1_"))%>'>
               url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/buscar_afiliados&cuil='+cuil+
		       '&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
		       '&fecha_referencia='+fecha_prestacion+'&origen=<%=prefijo%>&popup=true';
            </c:if>
	        
        </c:if>
       jQuery(popupAfill).load(url);
       

  	}


	function <portlet:namespace />buscarAfiliados_<%=prefijo%>(fecha_prest){
		//alert('funciï¿½n buscando, fecha' + fecha_prest);
		var cuil=jQuery('#<portlet:namespace />cuil<%=prefijo%>').val();
		var inte=jQuery('#<portlet:namespace />inte<%=prefijo%>').val();
		var tipoDoc=jQuery('#<portlet:namespace />tipoDoc<%=prefijo%>').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc<%=prefijo%>').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional<%=prefijo%>').val();		
		var apellido=jQuery('#<portlet:namespace />apellido<%=prefijo%>').val();
		var nombre=jQuery('#<portlet:namespace />nombre<%=prefijo%>').val();
		var entidad=jQuery('#<portlet:namespace />entidad<%=prefijo%>').val();
		var numero_afi=jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').val();
		var nroCredencialPrevencion=jQuery('#<portlet:namespace />nroCredencialPrevencion<%=prefijo%>').val();
		var nroSocioPrevencion=jQuery('#<portlet:namespace />nroSocioPrevencion<%=prefijo%>').val();
		if(!<portlet:namespace />validarBusqueda<%=prefijo%>(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi)){
			return false;
		}
		if(cuil.length>0){
			if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil-mensaje-limpiar'/>")){
				jQuery('#<portlet:namespace />cuil<%=prefijo%>').focus();
				return false;
			}
		}
		//alert ('buscar i');
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val()!="1"){
			jQuery("#<portlet:namespace />seccional<%=prefijo%>").val("");
			jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val("");
		}
		var fecha_prestacion = fecha_prest;
		try {
			fecha_prestacion = jQuery("#<portlet:namespace />fprest<%=prefijo%>").val();
		}
			catch (err) 
			{
				fecha_prestacion = 'null'; 
			}			
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />",modal:true,width:830});
		<c:if test="<%= !Boolean.parseBoolean(pag_reintegro) %>">		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+'&origen=<%=prefijo%>&popup=true&fecha_referencia='+fecha_prestacion;
			//alert ('no reintegros');
        </c:if>
        <c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">        		
    		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
    		var ext = '';
    		<c:if test="<%= tipo_reintegro != null && tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) %>">
				ext = '&ext=1';
    		</c:if>
    		//alert ('buscar');
    		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
			'&fecha_referencia='+fecha_prestacion+'&origen=<%=prefijo%>&popup=true'+ext;
		
    		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COR_1_"))%>'> 
		        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/buscar_afiliados&cuil='+cuil+
				'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
				'&fecha_referencia='+fecha_prestacion+'&origen=<%=prefijo%>&popup=true'+ext;
		     </c:if>
		     <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_AUT_1_"))%>'> 
		        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliados&cuil='+cuil+
				'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
				'&fecha_referencia='+fecha_prestacion+'&origen=<%=prefijo%>&popup=true'+ext;
		     </c:if>
        </c:if>
        jQuery(popupAfill).load(url);
	}
	
	function <portlet:namespace />validarBusqueda<%=prefijo%>(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi){			
		if(trim(cuil.length)==0 && trim(inte.length)==0 && trim(tipoDoc.length)==0 && trim(nroDoc.length)==0 && trim(seccional.length)==0 &&  
		   trim(apellido.length)==0 && trim(nombre.length)==0 && trim(entidad.length)==0 && trim(numero_afi.length)==0){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}

	function seleccionaAfiliado<%=prefijo%>(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af,id_tercerizadora ,afi_tercerizadora, reclamoPrestacional,nroSocioPrev, nroCredenPrev, fechaRecepcion, tieneAntecedentes){		var clase=jQuery("#<portlet:namespace />claseExpediente").val();
		if(clase!=null && clase=='DI'){
	      if (incapacidad_af != '1'){
	         alert("El Afiliado no es Discapacitado"); 
	         Liferay.Popup.close(popupAfill);
//		     return false;
	      }		
		}
		seleccionaCamposAfiliado<%=prefijo%>(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af,id_tercerizadora ,afi_tercerizadora, reclamoPrestacional ,nroSocioPrev, nroCredenPrev, fechaRecepcion, tieneAntecedentes);
		Liferay.Popup.close(popupAfill);
	}
	
	function seleccionaCamposAfiliado<%=prefijo%>(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af,id_tercerizadora,afi_tercerizadora,reclamoPrestacional, nroSocioPrev, nroCredenPrev, fechaRecepcion, tieneAntecedentes){
		jQuery('#<portlet:namespace />cuil<%=prefijo%>').val(cuil);
		jQuery('#<portlet:namespace />inte<%=prefijo%>').val(inte);
		jQuery('#<portlet:namespace />tipoDoc<%=prefijo%>').val(docu_tipo);
		jQuery('#<portlet:namespace />nroDoc<%=prefijo%>').val(docu_nro);
		jQuery('#<portlet:namespace />id_seccional<%=prefijo%>').val(id_secc);
		jQuery('#<portlet:namespace />seccional<%=prefijo%>').val(desc_secc);		
		jQuery('#<portlet:namespace />apellido<%=prefijo%>').val(apellido);
		jQuery('#<portlet:namespace />nombre<%=prefijo%>').val(nombre);
		jQuery('#<portlet:namespace />nroSocioPrevencion<%=prefijo%>').val(nroSocioPrev);
		jQuery('#<portlet:namespace />tieneAntecedentes<%=prefijo%>').val(
				(tieneAntecedentes == null || tieneAntecedentes == 'null' || tieneAntecedentes == '') ? '0' : tieneAntecedentes
		);
		<portlet:namespace />aplicarMarcaAntecedentes<%=prefijo%>(tieneAntecedentes);
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
        '&struts_action=/autorizaciones/buscar_afiliado_datos&cuil_titular=' + cuil +
        '&inte=' + inte;

		jQuery.ajax({   
			url: url,
			async: false,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				var actualizaDomicilio = obj.actualizadomicilio;
				var actualizaTelefono = obj.actualizatelefono;
				if (actualizaDomicilio === true || actualizaDomicilio === "true" || actualizaTelefono === true || actualizaTelefono === "true") {
					jQuery("#<portlet:namespace />divBotonActualizar").show();
					jQuery("#<portlet:namespace />divResultadoActualizarOK").hide();
					jQuery("#<portlet:namespace />seccionVerificarDomicilio").show();					
				} else {
					jQuery("#<portlet:namespace />divBotonActualizar").hide();
					jQuery("#<portlet:namespace />divResultadoActualizarOK").show();
					jQuery("#<portlet:namespace />seccionVerificarDomicilio").show();
				}
			}
		});
		
		if (nroCredenPrev == 'null') { 
			nroCredenPrev = '';
		}
		jQuery('#<portlet:namespace />nroCredencialPrevencion<%=prefijo%>').val(nroCredenPrev);
		
		if ( '0' != fechaRecepcion){
			jQuery('#<portlet:namespace />fechaIncidente').text('Caso U.O. fecha: '  + fechaRecepcion);
			jQuery('#<portlet:namespace />incidente').show();
		}
		
		if (jQuery('#<portlet:namespace />entidad<%=prefijo%>').val() == '<%= WebKeysGlobal.ENTIDADES_UOMA[0] %>') {
			jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').val(ospim);
		}
		if (jQuery('#<portlet:namespace />entidad<%=prefijo%>').val() == '<%= WebKeysGlobal.ENTIDADES_UOMA[1] %>') {
			jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').val(uoma);
		}
		if (jQuery('#<portlet:namespace />entidad<%=prefijo%>').val() == '<%= WebKeysGlobal.ENTIDADES_UOMA[2] %>') {
			jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').val(amtima);
		}
		jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val("1");
		document.getElementById("<portlet:namespace />baja_fecha<%=prefijo%>").style.background="white";
		document.getElementById("<portlet:namespace />baja_fecha<%=prefijo%>").style.color="black";
		if (document.getElementById("<portlet:namespace />baja_fecha<%=prefijo%>")!= null && bajaFecha!= null){
			document.getElementById("<portlet:namespace />baja_fecha<%=prefijo%>").value = bajaFecha;	
			if(""!=bajaFecha){
				 document.getElementById("<portlet:namespace />baja_fecha<%=prefijo%>").style.background="red";
				 document.getElementById("<portlet:namespace />baja_fecha<%=prefijo%>").style.color="white";
			} 
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
			if (afi_tercerizadora == 'null') {
				afi_tercerizadora = ''
			}
			jQuery("#<portlet:namespace />nombre_plan<%=prefijo%>").val(nombre_plan);
			jQuery("#<portlet:namespace />afi_tercerizadora<%=prefijo%>").val(afi_tercerizadora);
		</c:if>
		jQuery("#<portlet:namespace />fecha_alta_af<%=prefijo%>").val(fecha_alta_af);

		jQuery("#<portlet:namespace />incapacidad_af<%=prefijo%>").val(incapacidad_af);
		jQuery("#<portlet:namespace />id_tercerizadora<%=prefijo%>").val(id_tercerizadora);
		
		try {			
			if (jQuery("#<portlet:namespace />incapacidad_af").val() == '1') {
				jQuery('#<portlet:namespace />div_tratamientos_discapacidad').show();
				jQuery('#<portlet:namespace />discapacidad').show();
				jQuery('#<portlet:namespace />discapacidad_vto').show();
				var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliado_fecha_vto_documentacion&cuil_titular='+cuil+'&inte='+inte;		
				
				jQuery.ajax({   
					url: url,
					success: function(data){
						var obj = jQuery.parseJSON(data);
						var fechaVto = obj.fechaVto;
						if(fechaVto !=null){	
							var hoy = new Date();
							var vVto = fechaVto.split("-");
							var vto = new Date(vVto[2],vVto[1],vVto[0]);
							jQuery('#<portlet:namespace/>discapacidad_vto').html("Vto.Documentaciï¿½n "+fechaVto);
							if(vto<hoy ){
								alert("El certificado de Discapacidad Esta Vencido desde el " +fechaVto)
							}
						}else{
							jQuery('#<portlet:namespace/>discapacidad_vto').html('');
						}
					}				                                                                                                                                                                                                                                                            
					
				});
				
				
			} else {
				jQuery('#<portlet:namespace />div_tratamientos_discapacidad').hide();
				jQuery('#<portlet:namespace />discapacidad').hide();
				jQuery('#<portlet:namespace />discapacidad_vto').hide();
			}
			
			
			var esART=jQuery('#<portlet:namespace />artChk').attr('checked');

			if(!esART){
				  var pedirConfirmacion=false;
				  var msg=""
				  var url = "";
				  url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/verificar_art_preautorizacion&cuil_titular=';
				  url += cuil;
				  url +="&inte="+inte;
			      jQuery.ajax({   
			      url: url,
			      async:false,
			      success: function(data){
				    var obj = jQuery.parseJSON(data);
				    var inconsistencia = obj.tieneART;
				    msg =obj.mensaje;
				    pedirConfirmacion= (inconsistencia === 'true');
				  }});

			      if(pedirConfirmacion ){
			    	  var rpa = false; 
					   rpa = confirm (msg);
					   if (rpa == true){
						   jQuery('#<portlet:namespace />artChk').attr("checked",true);
					   }  
				  }
			 }

			
			
		}
		catch (err) {}
		<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
			//llamar script que busca los tratamientos del afiliado en la pï¿½gina
		</c:if>			
	}

	function <portlet:namespace />resetValid<%=prefijo%>() {
		if (jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val() != "") {
			jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val("1")
		}
	}

	var cuilJS = "<%= cuil%>";
	var inteJS = "<%= inte%>";
	if (trim(cuilJS) != "" && trim(inteJS) != ""){
		document.getElementById("<portlet:namespace />cuil<%=prefijo%>").value = cuilJS;
		document.getElementById("<portlet:namespace />inte<%=prefijo%>").value = inteJS;
		<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
			//alert ('cargando afiliado, fecha no puede ser undefined' + jQuery("#<portlet:namespace />fprest").val());
			<portlet:namespace />buscarAfiliados_<%=prefijo%>(jQuery("#<portlet:namespace />fprest<%=prefijo%>").val());
		</c:if>
		<c:if test="<%= !Boolean.parseBoolean(pag_reintegro) %>">
			//alert ('undefined' + jQuery("#<portlet:namespace />fprest").val());
			<portlet:namespace />buscarAfiliados<%=prefijo%>();
		</c:if>
		
		
		
		
	}
	
	<portlet:namespace />resetValid<%=prefijo%>();

	function <portlet:namespace />limpiarCamposAfiliado<%=prefijo%>() {
		jQuery('#<portlet:namespace />cuil<%=prefijo%>').val('');
		jQuery('#<portlet:namespace />inte<%=prefijo%>').val('');
		jQuery('#<portlet:namespace />tipoDoc<%=prefijo%>').val('');
		jQuery('#<portlet:namespace />nroDoc<%=prefijo%>').val('');
		jQuery('#<portlet:namespace />id_seccional<%=prefijo%>').val('');
		jQuery('#<portlet:namespace />seccional<%=prefijo%>').val('');		
		jQuery('#<portlet:namespace />apellido<%=prefijo%>').val('');
		jQuery('#<portlet:namespace />nombre<%=prefijo%>').val('');
		//jQuery('#<portlet:namespace />entidad').val('');
		document.getElementById('<portlet:namespace />entidad<%=prefijo%>').selectedIndex  = 0;
		jQuery('#<portlet:namespace />numero_afi<%=prefijo%>').val('');
		jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val("1");
		jQuery("#<portlet:namespace />baja_fecha<%=prefijo%>").val('');
		<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
			jQuery("#<portlet:namespace />nombre_plan<%=prefijo%>").val('');
			jQuery("#<portlet:namespace />afi_tercerizadora<%=prefijo%>").val('');
		</c:if>
		jQuery("#<portlet:namespace />fecha_alta_af<%=prefijo%>").val('');
		jQuery("#<portlet:namespace />incapacidad_af<%=prefijo%>").val('');	
		jQuery("#<portlet:namespace />discapacidad<%=prefijo%>").hide();
		jQuery("#<portlet:namespace />discapacidad_vto<%=prefijo%>").hide();
		jQuery("#<portlet:namespace />nroCredencialPrevencion<%=prefijo%>").val('');
		jQuery("#<portlet:namespace />nroSocioPrevencion<%=prefijo%>").val('');	
		document.getElementById("<portlet:namespace />baja_fecha<%=prefijo%>").style.background="white";
		document.getElementById("<portlet:namespace />baja_fecha<%=prefijo%>").style.color="black";
		jQuery('#<portlet:namespace />fechaIncidente').text('');
		jQuery('#<portlet:namespace />incidente').hide();
		jQuery("#<portlet:namespace />tieneAntecedentes<%=prefijo%>").val('0');
		<portlet:namespace />aplicarMarcaAntecedentes<%=prefijo%>('0');
		
	}

	function <portlet:namespace />detalleDiscapacidad<%=prefijo%>() {
		if (jQuery("#<portlet:namespace />incapacidad_af<%=prefijo%>").val() != '1') {
			alert ("Debe seleccionar un afiliado discapacitado");
			return false;
		}
		var cuil = jQuery('#<portlet:namespace />cuil<%=prefijo%>').val();
		var inte = jQuery('#<portlet:namespace />inte<%=prefijo%>').val();
		if (trim(cuil).length == 0 || trim(inte).length == 0) {
			alert ("Primero debe seleccionar un afiliado");
			return false;
		}
		popupdd = Liferay.Popup({title:"Detalle Discapacidad",modal:true,width:850});
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/detalle_discapacidad&cuil_titular='+cuil+'&inte='+inte+'&path=/autorizaciones/grabar_detalle_discapacidad';
		jQuery(popupdd).load(url);
	}

	function <portlet:namespace />reloadPopupDetalle<%=prefijo%>() {
		Liferay.Popup.close(popupdd);
		<portlet:namespace />detalleDiscapacidad();
	}
	
	
	jQuery(document).ready(function(){
																
	});	
	
</script>
