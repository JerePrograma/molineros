<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include
	file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>
<%@ page import="ar.com.ospim.afiliados.beans.AfiCuentasBancarias" %>
<%@ page import="ar.com.ospim.afiliados.services.AfiCuentasBancariasServiceUtil" %>

	<div id="helpCuenta" class="containerPlus draggable {buttons:'c', skin:'default', width:'700',title:'Ayuda',closed:'true'}" style="top: 500px; left: 200px">
			Titular: Esta opción es para cuando el afiliado titular se le efectuara el reintegro, se necesita el <br>
			número de CBU y adjuntar el comprobante del mismo.   <br>
			Apoderado: Esta opción es cuando el afiliado titular autoriza a otra persona a recibir el pago,   <br>
			se necesita el número de  CBU, apellido y nombre por otro lado   adjuntar el  comprobante del   <br>
			CBU  y la  Nota Autorizada. <br>
			En todos los casos se solicitará el mail y se le avisara del reintegro por ese medio. <br>
	</div>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")) {
		portlet_name = "autorizaciones";
	}
	ReclamoPrestacional reclamoprestacional = (ReclamoPrestacional) session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);

	
	String nro_reclamo = reclamoprestacional != null && String.valueOf(reclamoprestacional.getId_reclamo()) != null
							? String.valueOf(reclamoprestacional.getId_reclamo()) : "ZZZZZZZZZZZZ";

	Afiliado afiliadoTitular = reclamoprestacional != null ? reclamoprestacional.getAfiliadoTitular() : null;
	
	//verificamos si el afiliado titular tiene cuentas bancarias registradas
	boolean tieneCuentas = false;

	if (afiliadoTitular != null) {
	    List<AfiCuentasBancarias> cuentas = AfiCuentasBancariasServiceUtil.getCuentas(
	        afiliadoTitular.getCuil_titular(), afiliadoTitular.getInte());
	    tieneCuentas = (cuentas != null && !cuentas.isEmpty());
	}
	
	//ReclamoPrestacionalCuenta cuenta = reclamoprestacional != null ? reclamoprestacional.getCuenta() : null;
	
	//obtiene la cuenta seleccionada desde sesión
	AfiCuentasBancarias cuenta = null;
	
	Integer idCuentaSeleccionada = (Integer) renderRequest.getPortletSession().getAttribute("ID_CUENTA_BANCARIA_SELECCIONADA");	

	//si hay una cuenta seleccionada, la cargamos
	if (idCuentaSeleccionada != null && idCuentaSeleccionada > 0) {
	    AfiCuentasBancarias tmp = AfiCuentasBancariasServiceUtil.getCuentaPorId(idCuentaSeleccionada);
	    if (tmp != null && afiliadoTitular != null &&
	        tmp.getCuilTitular().equals(afiliadoTitular.getCuil_titular())) {
	        cuenta = tmp;
	    } else {
	        renderRequest.getPortletSession().removeAttribute("ID_CUENTA_BANCARIA_SELECCIONADA");
	    }
	}
	
	//si no hay cuenta seleccionada pero el reclamo tiene una guardada
	if (cuenta == null && reclamoprestacional != null && reclamoprestacional.getCuenta() != null) {

	    ReclamoPrestacionalCuenta cta = reclamoprestacional.getCuenta();

	    //si es titular
	    if ("0".equals(cta.getCmbTitular())) {

	        AfiCuentasBancarias afi = new AfiCuentasBancarias();

	        afi.setId(0);
	        afi.setCuilTitular(cta.getCuil());
	        afi.setCbu(cta.getCbu());
	        afi.setEmail(cta.getEmail());
	        afi.setApellido(cta.getApellido());
	        afi.setNombre(cta.getNombre());
	        afi.setTitular(true);
	        afi.setFileCbu(cta.getImagenCBU());
	        afi.setFileNotaAutorizada(cta.getImagenNotaAutorizada());

	        cuenta = afi;

	    } else {
	        //es apoderado recupera la cuenta original
	        List<AfiCuentasBancarias> cuentasAfi =  
	                AfiCuentasBancariasServiceUtil.getCuentas(cta.getCuil(), 0);

	        AfiCuentasBancarias afiOriginal = null;

	        for (AfiCuentasBancarias x : cuentasAfi) {
	            if (x.getCbu().equals(cta.getCbu())) {
	                afiOriginal = x;
	                break;
	            }
	        }

	        //si la encuentra, la usa
	        if (afiOriginal != null) {
	            cuenta = afiOriginal;
	        } else {
	            AfiCuentasBancarias afi = new AfiCuentasBancarias();
	            afi.setCuilTitular(cta.getCuil());
	            afi.setCbu(cta.getCbu());
	            afi.setEmail(cta.getEmail());
	            afi.setApellido(cta.getApellido());
	            afi.setNombre(cta.getNombre());
	            afi.setTitular(false);
	            afi.setFileCbu(cta.getImagenCBU());
	            afi.setFileNotaAutorizada(cta.getImagenNotaAutorizada());
	            cuenta = afi;
	        }
	    }
	}

	
	//si no hay cuenta seleccionada, mostramos la primera del titular
	/*if (cuenta == null && afiliadoTitular != null) {
    List<AfiCuentasBancarias> cuentas = AfiCuentasBancariasServiceUtil.getCuentas(
        afiliadoTitular.getCuil_titular(), 0);

    //filtra solo las vigentes (sin baja fecha)
    List<AfiCuentasBancarias> vigentes = new ArrayList<AfiCuentasBancarias>();
    for (AfiCuentasBancarias c : cuentas) {
        if (c.getBajaFecha() == null) {
            vigentes.add(c);
        }
    }

    //toma la ultima vigente
    if (!vigentes.isEmpty()) {
        cuenta = vigentes.get(vigentes.size() - 1);
    }
}*/

	String modoConsulta = (String) request.getAttribute("ModoConsulta");	
	ReclamoPrestacionalCuenta cuentaTemp = (ReclamoPrestacionalCuenta) session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_CUENTA_SELECT);
	request.setAttribute("solapa_cuenta", "cta_bancaria");
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
%>

<liferay-ui:error key="errorCtaBancaria" message="<%=(String)request.getAttribute(\"msgCtaBancaria\") %>" />


<input
		type="hidden" id="<portlet:namespace />id_reclamosel"
		name="<portlet:namespace />id_reclamosel" size="8"
		value="<%=Validator.isNotNull(reclamoprestacional)  ? reclamoprestacional.getId_reclamo()  : "0"  %>" />		
		
<input type="hidden" id="<portlet:namespace />file_cbu" name="<portlet:namespace />file_cbu" size="12" value="0" />
		
<input type="hidden" id="<portlet:namespace />file_nota_autorizada" name="<portlet:namespace />file_nota_autorizada" size="12" value="0" />

<!-- form action="UploadImagenesReclamosAction" method="post" name="<portlet:namespace />reclamo_fm" id="<portlet:namespace />reclamo_fm" enctype="multipart/form-data"/-->

	<fieldset class="block-labels">
	<legend>Cuenta Bancaria Reclamo Prestacional</legend>

<form action="" 
method="post" name="<portlet:namespace />reclamo_fm" 
id="<portlet:namespace />reclamo_fm" enctype="multipart/form-data"/>

	<td width="100%;">
			<div align="center" id="<portlet:namespace />cta_grupo_fliar" style="height:120px; overflow: scroll; overflow-x: hidden;">
					<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/cta_bancaria_reclamos_seccional_search_result.jsp">
					</liferay-util:include>
			</div>	
	</td>

	<liferay-ui:error key="errorUploadFile" message="<%=(String) request.getAttribute(\"msgInsertError\")%>" />

	<div id="<portlet:namespace />ctaBcria">				       
	  <fieldset class="block-labels">	  
		  <input type="button"
					       id="<portlet:namespace />btnCerrarPopUp"
					       value="Cerrar Cuentas Bancarias"
					       onClick="<portlet:namespace />cerrarPopUp();"
					       title="Cerrar Cuentas Bancarias" />
				       
	      <liferay-util:include page="/html/portlet/afiliados/cuentas_bancarias.jsp">
	      <liferay-portlet:param name="cuil" value="<%= afiliadoTitular.getCuil_titular() %>"/>
	      <liferay-portlet:param name="inte" value="<%= afiliadoTitular.getInteAsString() %>"/>
	      <liferay-portlet:param name="nombre" value="<%= afiliadoTitular.getNombre()%>"/>
	      <liferay-portlet:param name="apellido" value="<%= afiliadoTitular.getApellido()%>"/>
	      <liferay-portlet:param name="email" value="<%= afiliadoTitular.getEmail()%>"/>
	      <liferay-portlet:param name="modo" value="editar"/>
		  </liferay-util:include>
      </fieldset>         											  
	</div>

	<div id="<portlet:namespace />datos_prestacion_ingreso">

	<table class="lfr-table" style="border-collapse: separate; border-spacing: 2px; width: 100%;">
		<tr>
		<td colspan="15">
		<fieldset class="block-labels">
		<legend>
				<liferay-ui:message key="Datos Cuenta Bancaria" />
		</legend>
		<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 5px;">
				<tr>
					<td colspan="10">
						<table class="lfr-table"
							style="border-collapse: separate; border-spacing: 3px;">
							<tr>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td><label><liferay-ui:message key="Titular" /> :</label></td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>
										<select name="<portlet:namespace />cmb_titular"
										id="<portlet:namespace />cmb_titular">
										</select>
									</td>
									
									 <td>
									        <a href="javascript:void(0)" onclick="help(event, 'helpCuenta')"><img style="height: 25px; width: 25px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
									</td>
								</tr>
					</table>		
						    
								<div id="<portlet:namespace />datos_titular">

									<table>

										<tr>
											<td>CBU:</td>
											<td><input id="<portlet:namespace />cuenta_cbu"
												name="<portlet:namespace />cuenta_cbu" size="24"
												onkeydown="allowOnlyDigits(event);" maxlength="22"
												type="text" value=""
												readonly="readonly"/>
											</td>
										</tr>
										</tr>
										<tr>
											<td>Email:</td>
											<td><input id="<portlet:namespace />cuenta_email"
												name="<portlet:namespace />cuenta_email" size="40"
												maxlength="40" type="text"
												value=""
												readonly="readonly"/></td>
										</tr>
										<tr>
											<td>&nbsp;</td>
										</tr>
										<tr>
											<td><label><liferay-ui:message key="Afiliado Titular" />:</label></td>
											<td><input id="<portlet:namespace />denominacion"
												name="<portlet:namespace />denominacion" size="30"
												maxlength="30" type="text"
												value="" readonly="readonly"/></td>
										</tr>
										<tr id="<portlet:namespace />tr_cuil_titular">
											<td><label><liferay-ui:message
														key="Cuil Titular" />:</label></td>
											<td colspan="3"><input
												id="<portlet:namespace />cuil_titular_cuenta"
												name="<portlet:namespace />cuil_titular_cuenta" size="20"
												maxlength="11" type="text"
												onkeydown="allowOnlyDigits(event);"
												value=""
												readonly="readonly" /></td>
										</tr>
										<tr>
											<td>&nbsp;</td>
										</tr>
										
									</table>

								</div>

							<div id="<portlet:namespace />datos_autorizado">
								<table>
									<tr>
										<td>CBU:</td>
										<td><input id="<portlet:namespace />cuenta_cbu_autorizado"
											name="<portlet:namespace />cuenta_cbu_autorizado" size="24"
											onkeydown="allowOnlyDigits(event);" maxlength="22"
											type="text" value="" readonly="readonly"/></td>
									</tr>
										</tr>
										<tr>
											<td>Email:</td>
											<td><input id="<portlet:namespace />cuenta_email_autorizado"
												name="<portlet:namespace />cuenta_email_autorizado" size="40"
												maxlength="40" type="text"
												value=""
												readonly="readonly"/></td>
										</tr>
										<tr>
											<td>&nbsp;</td>
										</tr>
										<tr>
											<td><label><liferay-ui:message key="Apellido Autorizado" />:</label></td>
											<td><input id="<portlet:namespace />apellido_autorizado"
												name="<portlet:namespace />apellido_autorizado" size="20"
												maxlength="100" type="text" value="<%=""%>" readonly="readonly"/></td>
											<td><label><liferay-ui:message key="Nombre" />:</label></td>
											<td><input id="<portlet:namespace />nombre_autorizado"
												name="<portlet:namespace />nombre_autorizado" maxlength="100"
												type="text" value="" readonly="readonly"/></td>
										</tr>
			
										<tr>
			
											<tr>
											<td><label><liferay-ui:message key="Cuil Autorizado" />:</label></td>
											<td colspan="3"><input
												id="<portlet:namespace />cuil_autorizado" name="<portlet:namespace />cuil_autorizado" size="20"
												maxlength="11" type="text" onkeydown="allowOnlyDigits(event);" value=""
												readonly="readonly"/>
											</td>
									</td>
								</tr>														  	
			  				
							<tr>
								<td>&nbsp;</td>
							</tr>
						
						   </div>						   										   
						</table>
	  				  
						</table>
						<div id="<portlet:namespace />datos_pago_seccional" align="center">
							
								<legend>
									<liferay-ui:message key="Pago a Seccional" />
								</legend>
								</br>
								</br>
								</br>
						</div>				
<%
boolean hayCuentaSeleccionada = (idCuentaSeleccionada != null && idCuentaSeleccionada > 0);
%>   

<input type="hidden"
       id="<portlet:namespace />id_cuenta_seleccionada"
       name="<portlet:namespace />id_cuenta_seleccionada"
       value="<%= hayCuentaSeleccionada ? idCuentaSeleccionada.toString() : "0" %>" />
       
<div id="<portlet:namespace />botoneditareclamo" align="center" style="height: 80px; overflow-x: hidden;">
		<table>
			<tr>
				<td>
					<input type="button"
				       id="<portlet:namespace />btnActualizarCuenta"
				       value="Guardar Cuenta"
				       onClick="<portlet:namespace />altaCuenta();"
				       title="Guardar Cuenta" />
				</td>
				
				<td>
					<input type="button"
				       id="<portlet:namespace />btnAbrirPopUp"
				       value="Ir a Cuentas Bancarias"
				       onClick="<portlet:namespace />abrirPopUp();"
				       title="Ir a Cuentas Bancarias" />
				</td>		        
		
				<td>
					<input type="button" value="<liferay-ui:message key="next" />"
					onClick="<portlet:namespace />siguienteSolapa();" /></td>
				<td>
				    <input type="button" value="<liferay-ui:message key="back" />"
	   				onClick="<portlet:namespace />anteriorSolapa();" />
				</td>
				<td>				
					
					<input id="<portlet:namespace />eMail"
						value="<liferay-ui:message key="email-large"/>"
				    	title="<liferay-ui:message key="email-large" />"
					   onClick="javascript: <portlet:namespace />emailPreCarga(<%=reclamoprestacional.getId_String()%>);"
						type="button" /> <%
					 	 if (reclamoprestacional != null && reclamoprestacional.getId_reclamo() != 0
					 			&& reclamoprestacional.getFechaMailSeccional() != null) {
							 %>
				
							<td><label><font size=3 color="#0000ff">&nbsp;&nbsp;
										Email enviado <%=sdf.format(reclamoprestacional.getFechaMailSeccional())%></font></label>
							</td>
							<td colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<%
					 	}
					 %>
				</td>

			</tr>
		</table>
	</div>
			</table>
	</fieldset>

	<div id="<portlet:namespace />listado_imagenes">		
		<jsp:include page='/html/portlet/autorizaciones/reclamos_prestacionales/reclamo_prestacional_imagenes_search_documentos_cuenta.jsp' />  					
	</div>
<!--  /form-->

<script type="text/javascript">

jQuery("#<portlet:namespace />datos_autorizado").hide();
jQuery("#<portlet:namespace />imagenes").show();


jQuery("#<portlet:namespace />cmb_titular").change(function(){
	 filtrarTipoComprobante();
	
	var cmbTitular = jQuery("#<portlet:namespace/>cmb_titular").val();

	jQuery("#<portlet:namespace />div_afi_titular").show();
	
	mostrarTipoComprobante();	
});

jQuery("#<portlet:namespace />cuenta_email").keyup(function() {
	var str = jQuery("#<portlet:namespace/>cuenta_email").val();
	var res = str.toLocaleLowerCase();
	jQuery("#<portlet:namespace />cuenta_email").val(res);
});

jQuery("#<portlet:namespace />cuenta_email_autorizado").keyup(function() {
	var str = jQuery("#<portlet:namespace/>cuenta_email_autorizado").val();
	var res = str.toLocaleLowerCase();
	jQuery("#<portlet:namespace />cuenta_email_autorizado").val(res);
});

function mostrarTipoComprobante() {
	var cmbTitular = jQuery("#<portlet:namespace/>cmb_titular").val();
	
	if (cmbTitular == '0'){
		jQuery("#<portlet:namespace />datos_titular").show();
		jQuery("#<portlet:namespace />datos_autorizado").hide();
		jQuery("#<portlet:namespace />datos_pago_seccional").hide();
		jQuery("#<portlet:namespace />imagenes").show();
	}else if(cmbTitular == '1'){
		 jQuery("#<portlet:namespace />datos_titular").hide();
		 jQuery("#<portlet:namespace />datos_autorizado").show();
		 jQuery("#<portlet:namespace />datos_pago_seccional").hide();
		jQuery("#<portlet:namespace />imagenes").show();
	}else if(cmbTitular == '2'){
		jQuery("#<portlet:namespace />datos_titular").hide();
		jQuery("#<portlet:namespace />datos_autorizado").hide();
		jQuery("#<portlet:namespace />datos_pago_seccional").show();
		jQuery("#<portlet:namespace />imagenes").hide();
	}
}

jQuery(document).ready(function() {
    filtrarTipoPago();
    <% if (hayCuentaSeleccionada || cuenta != null) { %>
       // Hay una cuenta seleccionada o una cuenta grabada en el reclamo
       <portlet:namespace />preCargarDatosTitular();
       <portlet:namespace />loadDatosReclamoCuenta();
    <% } else { %>
       // Solo queda usar la cuenta temporal que estaba en sesión (precarga)
       <portlet:namespace />loadDatosReclamoCuentaTemp();
       <% session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_CUENTA_SELECT); %>
    <% } %>
    filtrarTipoComprobante();
});


function <portlet:namespace />preCargarDatosTitular(){
	<% if (afiliadoTitular != null /*&& cuenta == null*/) { %>
	jQuery('#<portlet:namespace />cuenta_email').val("<%=
		    (afiliadoTitular.getEmail() != null ? afiliadoTitular.getEmail() : "")
		%>");
    jQuery('#<portlet:namespace />denominacion').val("<%=afiliadoTitular.getApeNombre()%>");
    jQuery('#<portlet:namespace />cuil_titular_cuenta').val("<%=afiliadoTitular.getCuil_titular()%>");
	<% } %>	
}

function <portlet:namespace />loadDatosReclamoCuenta() {
    <% if (cuenta != null) { %>
        //seteo si es titular o apoderado
        jQuery('#<portlet:namespace />cmb_titular').val("<%=cuenta.isTitular() ? "0" : "1"%>");

        <% if (cuenta.isTitular()) { %>
            // Titular
            jQuery('#<portlet:namespace />cuenta_cbu').val("<%=cuenta.getCbu()%>");
            jQuery('#<portlet:namespace />cuenta_email').val("<%=
                    (cuenta.getEmail() != null ? cuenta.getEmail() : "")
                %>");
        <%
            String apeTit = (cuenta != null) ? cuenta.getApellido() : "";
            String nomTit = (cuenta != null) ? cuenta.getNombre() : "";

            if ((apeTit == null || apeTit.trim().isEmpty() || nomTit == null || nomTit.trim().isEmpty())
	            && afiliadoTitular != null) {
	            apeTit = afiliadoTitular.getApellido();
	            nomTit = afiliadoTitular.getNombre();
	           }
        %>
           	jQuery('#<portlet:namespace />denominacion').val("<%= apeTit + ", " + nomTit %>");
            
            jQuery('#<portlet:namespace />cuil_titular_cuenta').val("<%=cuenta.getCuilTitular()%>");
        <% } else { %>
            // Apoderado
            jQuery('#<portlet:namespace />cuenta_cbu_autorizado').val("<%=cuenta.getCbu()%>");
            jQuery('#<portlet:namespace />cuenta_email_autorizado').val("<%=
                    (cuenta.getEmail() != null ? cuenta.getEmail() : "")
                %>");
            jQuery('#<portlet:namespace />apellido_autorizado').val("<%=cuenta.getApellido()%>");
            jQuery('#<portlet:namespace />nombre_autorizado').val("<%=cuenta.getNombre()%>");
            jQuery('#<portlet:namespace />cuil_autorizado').val("<%=cuenta.getCuilCbu() != null && !cuenta.getCuilCbu().isEmpty()
        ? cuenta.getCuilCbu()
        : cuenta.getCuilTitular() %>");
        <% } %>
    <% } %>

    mostrarTipoComprobante();
}

function <portlet:namespace />loadDatosReclamoCuentaTemp(){
	 	
		<%if (cuentaTemp != null){%>
			//titular
			<%if ("0".equals(cuentaTemp.getCmbTitular())){%>
				jQuery('#<portlet:namespace />cuenta_cbu').val("<%=cuentaTemp.getCbu()%>");
				jQuery('#<portlet:namespace />cuenta_email').val("<%=
		                (cuentaTemp.getEmail() != null ? cuentaTemp.getEmail() : "")
		            %>");
				jQuery('#<portlet:namespace />denominacion').val("<%=cuentaTemp.getApellido() + ", " + cuentaTemp.getNombre()%>");		
				jQuery('#<portlet:namespace />cuil_titular_cuenta').val("<%=cuentaTemp.getCuil()%>");	
				jQuery('#<portlet:namespace />file_cbu').val("<%=cuentaTemp.getImagenCBU() != null ? cuentaTemp.getImagenCBU() : "" %>");
			<%}else{%>		
				jQuery('#<portlet:namespace />cmb_titular').val("<%=cuentaTemp.getCmbTitular()%>");
				jQuery('#<portlet:namespace />cuenta_cbu_autorizado').val("<%=cuentaTemp.getCbu()%>");
				jQuery('#<portlet:namespace />cuenta_email_autorizado').val("<%=
		                (cuentaTemp.getEmail() != null ? cuentaTemp.getEmail() : "")
		            %>");
				jQuery('#<portlet:namespace />apellido_autorizado').val("<%=cuentaTemp.getApellido()%>");		
				jQuery('#<portlet:namespace />nombre_autorizado').val("<%=cuentaTemp.getNombre()%>");		
				jQuery('#<portlet:namespace />cuil_autorizado').val("<%=cuentaTemp.getCuil()%>");	
				jQuery('#<portlet:namespace />file_cbu').val("<%=cuentaTemp.getImagenCBU() != null ? cuentaTemp.getImagenCBU() : "" %>");
				jQuery('#<portlet:namespace />file_nota_autorizada').val("<%=cuentaTemp.getImagenNotaAutorizada() != null ? cuentaTemp.getImagenNotaAutorizada() : ""  %>");
			<%}%>
			<%if (cuentaTemp.getId() !=  0){%>
				 jQuery("#<portlet:namespace />imagenes").hide();
			<%}%> 
			
			mostrarTipoComprobante();
			 
		<%}%>
	}

function filtrarTipoComprobante() {
	var Titular = jQuery("#<portlet:namespace/>cmb_titular").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/filtrarTipoComprobante&tipo_doc='+Titular;
	jQuery("#<portlet:namespace/>descripcionFile").attr('disabled', 'disabled');
	
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			//document.getElementById("<portlet:namespace/>descripcionFile").length = 0;
			//jQuery("#<portlet:namespace/>descripcionFile").removeAttr('disabled');
			//var obj = jQuery.parseJSON(data);
			jQuery('#<portlet:namespace />descripcionFile').html(data).fadeIn();
			
			mostrarTipoComprobante();
		}
	});
}

function filtrarTipoPago() {
	
	var seccional =  "<%=reclamoprestacional.getIdSeccional()%>";
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/filtrarTipoPago&id_seccional='+seccional;
	jQuery("#<portlet:namespace/>cmb_titular").attr('disabled', 'disabled');
	
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			//document.getElementById("<portlet:namespace/>cmb_titular").length = 0;
			//jQuery("#<portlet:namespace/>cmb_titular").removeAttr('disabled');
			//var obj = jQuery.parseJSON(data);
			jQuery('#<portlet:namespace />cmb_titular').html(data).fadeIn();
			
			mostrarTipoComprobante();
		}
	});
}

function validarDatosObligatoriosCuenta() {
	var cmbTitular = jQuery("#<portlet:namespace/>cmb_titular").val();
	var cuentaCBU = '';
	var cuentaEmail = '';
	var cuilTitular = '';
	var apellido = '';
	var nombre = '';
	
	if (cmbTitular == '0'){
		cuentaCBU = jQuery("#<portlet:namespace/>cuenta_cbu").val();
		cuentaEmail = jQuery("#<portlet:namespace/>cuenta_email").val();
		cuilTitular = jQuery("#<portlet:namespace/>cuil_titular_cuenta").val();
	}else if (cmbTitular == '1') {
		cuentaCBU = jQuery("#<portlet:namespace/>cuenta_cbu_autorizado").val();
		cuentaEmail = jQuery("#<portlet:namespace/>cuenta_email_autorizado").val();
		apellido = jQuery("#<portlet:namespace/>apellido_autorizado").val();
		nombre = jQuery("#<portlet:namespace/>nombre_autorizado").val();
		cuilTitular = jQuery("#<portlet:namespace/>cuil_autorizado").val();				
	}	

	if(cuentaCBU==null || cuentaCBU==''){
	  	alert('Debe ingresar el CBU');
		return false;
	}

	if(cuentaCBU.length != 22){
	  	alert('El CBU debe tener 22 dígitos');
		return false;
	}

	if(cuentaEmail==null || cuentaEmail==''){
	  	alert('Debe ingresar el Email');
		return false;
	}	
	
	if(cuilTitular==null || cuilTitular==''){
	  	alert('Debe ingresar el cuil titular');
		return false;
	}
	
	if (cmbTitular == '1' && 
		    ((apellido == null || apellido.trim() === '') || (nombre == null || nombre.trim() === ''))) {
		    alert("Debe completar el nombre y apellido del apoderado en la sección de ABM cuentas bancarias.");
		    return false;
		}

	if(!validarCBU(cuentaCBU, "<liferay-ui:message key='valida-cbu'/>")){
		return false;
	}
	
	
	if(!validarEmail(cuentaEmail)){
		alert('Debe ingresar un email valido');
		return false;
	}
	
	return true;
}

function  <portlet:namespace />altaCuenta() {
	const btnActualizar = jQuery("#<portlet:namespace />btnActualizarCuenta");

	//obtenemos el ID de cuenta seleccionada desde el hidden
	var idSel = jQuery("#<portlet:namespace />id_cuenta_seleccionada").val();

	//si no hay cuenta seleccionada, mostramos alerta
	  if (!idSel || idSel === "0") {
	    alert("Debe seleccionar una cuenta antes de actualizar.");
	    return false;
	  }
	
	var cmbTitular = jQuery("#<portlet:namespace/>cmb_titular").val();
	
	var validacion = true;
	
	 
	 if (cmbTitular == "2" ){
		 validacion == true;
	 }else{
		 validacion = validarDatosObligatoriosCuenta();
	 }
	
	if ( validacion)  {	  			
		
		  var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";
		  params = params + "&<%= Constants.CMD %>=" + "<%=WebKeysAutorizaciones.CUENTA%>";
		  params = params + "&cuil_grupo_familar=" + "<%=afiliadoTitular.getCuil_titular()%>";
		  
		  if (cmbTitular == "2" ){
			  params = params + "&id_seccional=" + "<%=reclamoprestacional.getIdSeccional()%>";
		  }
		  
		  var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" /></portlet:actionURL>';
		  url = url + "&esDatosTab=true";
		  url = url + params;
		  document.<portlet:namespace />reclamo_fm.method = 'post';
		
		  submitForm(document.<portlet:namespace />reclamo_fm, url);
			
	}		
}

function <portlet:namespace />siguienteSolapa() {

    var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";
	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" /></portlet:actionURL>';
	url = url + '&moverATab=archivos' +  '&<%= Constants.CMD %>=<%=Constants.MOVE %>';
    url = url + params;

	document.<portlet:namespace />reclamo_fm.method = 'post';
	submitForm(document.<portlet:namespace />reclamo_fm, url);
}

function <portlet:namespace />anteriorSolapa() {

    var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";
	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" /></portlet:actionURL>';
	url = url + '&moverATab=datos' +  '&<%= Constants.CMD %>=<%=Constants.MOVE %>';
    url = url + params;

	document.<portlet:namespace />reclamo_fm.method = 'post';
	submitForm(document.<portlet:namespace />reclamo_fm, url);
}

function validarEmail(email) {
	var emailReg = /^([\da-z_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
	
	if(trim(email).length > 0){	
		if( !emailReg.test( email ) ) {
			return false;
		} else {
			return true;
		}
	}else{
		return true;
	}	
}

function <portlet:namespace />emailPreCarga(id_reclamo){	

	var accion = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";	
	
	 var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" />'+
		'<liferay-portlet:param name="cmd" value="email"/>'+
		'<liferay-portlet:param name="id_reclamosel" value="__id_reclamosel"/>'+
		'<liferay-portlet:param name="tab_seleccionada" value="cta_bancaria"/>'+

		'<liferay-portlet:param name="view" value="__view"/>'+
		'</liferay-portlet:renderURL>';		
		
		 url = url.replace("__id_reclamosel",id_reclamo);
		 url = url + accion;

		 document.<portlet:namespace />reclamo_fm.method = 'post';
		 submitForm(document.<portlet:namespace />reclamo_fm, url); 
}


jQuery("#<portlet:namespace />ctaBcria>").hide();
function <portlet:namespace />abrirPopUp() {
    jQuery("#<portlet:namespace />ctaBcria>").show();
}

function <portlet:namespace />cerrarPopUp() {
	jQuery("#<portlet:namespace />ctaBcria>").hide();
}

</script>
