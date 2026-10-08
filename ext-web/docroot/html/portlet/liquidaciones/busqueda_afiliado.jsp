<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil" %>

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
	
	int tieneAntecedentesInicial = 0;
	String colorAntecedenteInicial = "";
	String codigoAntecedenteInicial = "";
	
	if (cuil != null && !cuil.trim().equals("")) {
	    try {
	        tieneAntecedentesInicial = BusquedaAfiliadoServiceUtil.getInstance().buscarTieneAntecedentesGrupoFamiliar(cuil);
	        String colorTmp = BusquedaAfiliadoServiceUtil.getInstance().buscarColorAntecedenteGrupoFamiliar(cuil);
	        String codigoTmp = BusquedaAfiliadoServiceUtil.getInstance().buscarCodigoAntecedenteGrupoFamiliar(cuil);
	        
	        if (colorTmp != null) {
	            colorAntecedenteInicial = colorTmp.trim();
	        }
	        
	        if (codigoTmp != null) {
	        	codigoAntecedenteInicial = codigoTmp.trim();
	        }
	    } catch (Exception e) {
	        tieneAntecedentesInicial = 0;
	        colorAntecedenteInicial = "";
	        codigoAntecedenteInicial = "";
	    }
	}
	
	String seccionalString=null;
	String seccionalDefecto=user.getExpandoBridge().getAttribute("id_seccional").toString(); 		
	int seccionalFijada=null!=seccionalDefecto&& !seccionalDefecto.trim().equals("")&& !seccionalDefecto.trim().equals("0")?Integer.parseInt(seccionalDefecto):0;
	if(seccionalFijada!=0){
		seccionalString=user.getExpandoBridge().getAttribute("seccional").toString();
	}
%>

<style type="text/css">

    #<portlet:namespace />panelDatosAfiliado {
        position: relative;
    }

    #<portlet:namespace />panelDatosAfiliado.afiliado-con-antecedentes-panel {
        border-radius: 4px;
        padding: 6px;
        padding-top: 34px;
    }

    #<portlet:namespace />panelDatosAfiliado.afiliado-con-antecedentes-panel td,
    #<portlet:namespace />panelDatosAfiliado.afiliado-con-antecedentes-panel span,
    #<portlet:namespace />panelDatosAfiliado.afiliado-con-antecedentes-panel b,
    #<portlet:namespace />panelDatosAfiliado.afiliado-con-antecedentes-panel label {
        color: #333333 !important;
    }

    #<portlet:namespace />antecedentesJudicialesBox {
        display: none;
        position: absolute;
        top: 6px;
        right: 12px;
        z-index: 2;
        white-space: nowrap;
        font-weight: bold;
    }

    #<portlet:namespace />antecedentesJudicialesLabel {
        display: inline-block;
        padding: 2px 8px;
        border-radius: 4px;
        line-height: 1.2;
    }
</style>

<div id="<portlet:namespace />panelDatosAfiliado">

    <div id="<portlet:namespace />antecedentesJudicialesBox">

	    <span id="<portlet:namespace />antecedentesJudicialesLabel">
	        Antecedentes Judiciales
	    </span>
	
	    <a href="javascript:void(0)"
	       onclick="help(event, '<portlet:namespace />helpAntecedentesJudiciales')"
	       style="display:inline-block; margin-left:4px; vertical-align:middle;">
	
	        <img
	            style="height:16px; width:16px; vertical-align:middle;"
	            src="/html/images/help.png"
	            title="Referencia de antecedentes judiciales"
	            alt="Ayuda"
	        />
	
	    </a>
	
	</div>
    
<portlet:defineObjects/>							
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
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<% if(seccionalFijada==0){%>
						<td><label><liferay-ui:message key="seccional" />:</label></td>														
						<td colspan="4" style="vertical-align:top" >
						<liferay-util:include page="/html/portlet/afiliados/busqueda_seccional.jsp"/>
						<%}else { %>	
							<input type="hidden" id="<portlet:namespace />id_seccional" name="<portlet:namespace />id_seccional" value="<%=seccionalFijada%>">
						<%} %>
					
						<%-- <jsp:include page='/html/portlet/liquidaciones/busqueda_seccional.jsp'/></td> --%>
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
						&nbsp;
						<label id="<portlet:namespace />discapacidad_vto" style="display: none;">Vto. Certificado: </font></label>
						</td>
						
					</tr>
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
</div>
				<input id="<portlet:namespace />fecha_alta_af" value="" type="hidden" name="<portlet:namespace />fecha_alta_af"/>
				<input id="<portlet:namespace />incapacidad_af" value="" type="hidden" name="<portlet:namespace />incapacidad_af"/>

				<input id="<portlet:namespace />tieneAntecedentes" value="<%=tieneAntecedentesInicial%>" type="hidden" name="<portlet:namespace />tieneAntecedentes"/>
				<input id="<portlet:namespace />colorAntecedente" value="<%=colorAntecedenteInicial%>" type="hidden" name="<portlet:namespace />colorAntecedente"/>
				<input id="<portlet:namespace />codigoAntecedente" value="<%=codigoAntecedenteInicial%>" type="hidden" name="<portlet:namespace />codigoAntecedente"/>
				
<div
    id="<portlet:namespace />helpAntecedentesJudiciales"
    class="containerPlus draggable {buttons:'c', skin:'default', width:'650',title:'Ayuda',closed:'true'}"
    style="top:100px; left:250px;">

    <liferay-util:include
        page="/html/portlet/crm/leyenda_antecedentes_judiciales.jsp"
    />

</div>
								
<script type="text/javascript">
	var popupAfill; 		
	var popupdd;

	function <portlet:namespace />aplicarAntecedentesAfiliado(tieneAntecedentes,colorAntecedente,codigoAntecedente){
		    var flag = (String(tieneAntecedentes) == '1');
		    var color = colorAntecedente != null ? String(colorAntecedente).replace(/^\s+|\s+$/g, '') : '';
		    var tieneColor = /^#[0-9a-fA-F]{6}$/.test(color);
		    var mostrarAntecedente = flag || tieneColor;

		    jQuery('#<portlet:namespace />tieneAntecedentes').val(flag ? '1' : '0');
		    jQuery('#<portlet:namespace />colorAntecedente').val(color);

		    var codigo =
		        codigoAntecedente != null
		            ? String(codigoAntecedente).replace(/^\s+|\s+$/g, '')
		            : '';

		    if (codigo == 'null') {
		        codigo = '';
		    }

		    jQuery('#<portlet:namespace />codigoAntecedente').val(codigo);

		    var textoAntecedente = 'Antecedentes Judiciales';

		    if (codigo != '') {
		        textoAntecedente += ' - ' + codigo;
		    }

		    jQuery(
		        '#<portlet:namespace />antecedentesJudicialesLabel'
		    ).text(textoAntecedente);
		    
		    var panel = jQuery('#<portlet:namespace />panelDatosAfiliado');

		    if (mostrarAntecedente) {
		        panel.addClass('afiliado-con-antecedentes-panel');
		        jQuery('#<portlet:namespace />antecedentesJudicialesBox').show();

		        if (tieneColor) {

		            var r = parseInt(color.substring(1, 3), 16);
		            var g = parseInt(color.substring(3, 5), 16);
		            var b = parseInt(color.substring(5, 7), 16);

		            var colorSuave = 'rgba(' + r + ',' + g + ',' + b + ',0.16)';
		            var colorBorde = 'rgba(' + r + ',' + g + ',' + b + ',0.45)';
		            var luminosidad = (r * 299 + g * 587 + b * 114) / 1000;
		            var colorTextoBadge = luminosidad > 160 ? '#333333' : '#ffffff';

		            panel.css('background-color',colorSuave);
		            panel.css('border','1px solid ' + colorBorde);
		            panel.css('border-left','6px solid ' + color);
		            jQuery('#<portlet:namespace />antecedentesJudicialesLabel')
			            .css({
			                'background-color': color,
			                'border': '1px solid ' + colorBorde,
			                'color': colorTextoBadge
			            });

		        } else {
		            panel.css('background-color','');
		            panel.css('border','');
		            panel.css('border-left','');

		            jQuery('#<portlet:namespace />antecedentesJudicialesLabel')
			            .css({
			                'background-color': '',
			                'border': '',
			                'color': ''
			            });
		        }

		    } else {
		        panel.removeClass('afiliado-con-antecedentes-panel');
		        panel.css('background-color','');
		        panel.css('border','');
		        panel.css('border-left','');
				
		        jQuery('#<portlet:namespace />antecedentesJudicialesLabel'
		        	).css({
		        	    'background-color': '',
		        	    'border': '',
		        	    'color': ''
		        	});
		        
		        jQuery('#<portlet:namespace />antecedentesJudicialesBox').hide();
		    }
		}
	
	function <portlet:namespace />buscarAfiliados(){
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
		
		
	
		<%
		String pag_reintegro_reclamo = ParamUtil.getString(request, "pag_reintegro_reclamo", null);
		%>
		var reintegro_reclamo =<%=pag_reintegro_reclamo==null ? 0 :pag_reintegro_reclamo %>

		
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
			jQuery("#<portlet:namespace />seccional").val("");
			jQuery("#<portlet:namespace />id_seccional").val("");
		}
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />",modal:true,width:830});
		<c:if test="<%= !Boolean.parseBoolean(pag_reintegro) %>">
			var fecha_prestacion = 'null';
			try {
				fecha_prestacion = jQuery("#<portlet:namespace />fprest").val();
			}
				catch (err) 
				{
					fecha_prestacion = 'null'; 
				}											
			
	
				
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+'&popup=true&fecha_referencia='+fecha_prestacion+'&reintegro_reclamo='+reintegro_reclamo;
			
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
    	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>	
          url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/buscar_afiliados&cuil='+cuil+
  		'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
  		'&fecha_referencia='+fecha_prestacion+'&popup=true';
      	</c:if>	
        /*fin zafar*/
        
        
        <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_JUD_1_"))%>'>	
	        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/judicial/buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
			'&fecha_referencia='+fecha_prestacion+'&popup=true';
	    </c:if>
        
	      <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_AUT_1_"))%>'>	
             url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliados&cuil='+cuil+
		    '&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
		    '&fecha_referencia='+fecha_prestacion+'&popup=true';
    	  </c:if>
	    
        </c:if>
        <c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
        	var fecha_prestacion = 'null';
        	<c:if test="<%= !Boolean.parseBoolean(discapacidad) %>">
        		fecha_prestacion = jQuery("#<portlet:namespace />fprest").val();        				
			</c:if>
			<c:if test="<%= Boolean.parseBoolean(discapacidad) %>">
				var d = new Date();
				var curr_date = d.getDate();
			    var curr_month = d.getMonth() + 1; //Months are zero based
			    var curr_year = d.getFullYear();
			    fecha_prestacion = curr_date + "/" + curr_month + "/" + curr_year;								
			</c:if>

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
			'&fecha_referencia='+fecha_prestacion+'&popup=true'+'&reintegro_reclamo='+reintegro_reclamo;
		
			<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COR_1_"))%>'>	
		        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/buscar_afiliados&cuil='+cuil+
				'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
				'&fecha_referencia='+fecha_prestacion+'&popup=true';
		    </c:if>
		    /* Para zafar con pre_carga */
		    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_AFI_1_"))%>'>	
		    var d = new Date();
		    var fecha_prestacion = d.toLocaleDateString();

		    url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/pre_afiliados_buscar_afiliados&cuil='+cuil+
			'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+'&fecha_referencia='+fecha_prestacion+'&popup=true';
	    	</c:if>
	        /*fin zafar*/
	        
	        <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_JUD_1_"))%>'>	
		        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/judicial/buscar_afiliados&cuil='+cuil+
				'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
				'&fecha_referencia='+fecha_prestacion+'&popup=true';
		    </c:if>
		    
		    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_AUT_1_"))%>'>	
                url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliados&cuil='+cuil+
		        '&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+
		        '&fecha_referencia='+fecha_prestacion+'&popup=true';
   	        </c:if>
        </c:if>
        jQuery(popupAfill).load(url);
	}

	function <portlet:namespace />buscarAfiliados_(fecha_prest){
		//alert('funci?n buscando, fecha' + fecha_prest);
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
		     <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_JUD_1_"))%>'> 
		        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/judicial/buscar_afiliados&cuil='+cuil+
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
	
	function seleccionaAfiliado(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af,id_tercerizadora, afi_tercerizadora,conreclamo,nroSocioPrev,nroCredenPrev,fechaRecepcion,tieneAntecedentes,colorAntecedente,codigoAntecedente){
		seleccionaCamposAfiliado(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af,id_tercerizadora,afi_tercerizadora,conreclamo,nroSocioPrev,nroCredenPrev,fechaRecepcion,tieneAntecedentes,colorAntecedente,codigoAntecedente);
		Liferay.Popup.close(popupAfill);
	}
	
	function seleccionaCamposAfiliado(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha,nombre_plan,id_plan,fecha_alta_af,incapacidad_af,id_tercerizadora,afi_tercerizadora,conreclamo,nroSocioPrev,nroCredenPrev,fechaRecepcion,tieneAntecedentes,colorAntecedente,codigoAntecedente){
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
		<portlet:namespace />aplicarAntecedentesAfiliado(tieneAntecedentes,colorAntecedente,codigoAntecedente);
		
		try {			
			if (jQuery("#<portlet:namespace />incapacidad_af").val() == '1') {
				jQuery('#<portlet:namespace />div_tratamientos_discapacidad').show();
				jQuery('#<portlet:namespace />discapacidad').show();
				jQuery('#<portlet:namespace />discapacidad_vto').show();
                var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_afiliado_fecha_vto_documentacion&cuil_titular='+cuil+'&inte='+inte;		
				
				jQuery.ajax({   
					url: url,
					success: function(data){
						var obj = jQuery.parseJSON(data);
						var fechaVto = obj.fechaVto;
						if(fechaVto !=null){	
							var hoy = new Date();
							var vVto = fechaVto.split("-");
							var vto = new Date(vVto[2],vVto[1],vVto[0]);
							jQuery('#<portlet:namespace/>discapacidad_vto').html("Vto. Documentación "+fechaVto);
							if(vto<hoy ){
								alert("El certificado de Discapacidad Esta Vencido")
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
		}
		catch (err) {}

		jQuery("#<portlet:namespace />con_reclamo_prestacional").val(conreclamo);
		try {	
			
			if (jQuery("#<portlet:namespace />con_reclamo_prestacional").val() == '1') {
				jQuery('#<portlet:namespace />div_boton_reclamos_prestaciones').show();
				jQuery('#<portlet:namespace />div_reclamos_prestaciones').hide();
				jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").hide();
				
			} else {
				jQuery("#<portlet:namespace />div_boton_oculta_reclamos_prestaciones").hide();
				jQuery('#<portlet:namespace />div_boton_reclamos_prestaciones').hide();
				jQuery('#<portlet:namespace />div_reclamos_prestaciones').hide();
			}
		}
		catch (err) {}
		
		<c:if test="<%= Boolean.parseBoolean(pag_reintegro) %>">
			//llamar script que busca los tratamientos del afiliado en la p?gina
		</c:if>			
	}

		<portlet:namespace />aplicarAntecedentesAfiliado(
			jQuery('#<portlet:namespace />tieneAntecedentes').val(),
		    jQuery('#<portlet:namespace />colorAntecedente').val(),
		    jQuery('#<portlet:namespace />codigoAntecedente').val()
		);
	
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
		
		jQuery('#<portlet:namespace />tieneAntecedentes').val('0');
		jQuery('#<portlet:namespace />colorAntecedente').val('');
		jQuery('#<portlet:namespace />codigoAntecedente').val('');
		<portlet:namespace />aplicarAntecedentesAfiliado('0','','');
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
