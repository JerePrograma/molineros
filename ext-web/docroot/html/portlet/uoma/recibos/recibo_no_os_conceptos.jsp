<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<% 
String esEdicionStr = ParamUtil.getString(request, "esEdicion");
if (esEdicionStr == null || esEdicionStr.equals("false")){
	esEdicionStr ="false";
}
boolean esEdicion = Boolean.parseBoolean(esEdicionStr);

Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
if (recibo != null && recibo.getId()!=0){
 	esEdicion = false;
}

List<Concepto> conceptos = (List<Concepto>)request.getSession().getAttribute(WebKeysLiquidaciones.CONCEPTOS_INGRESO);
%>
<liferay-ui:error exception="<%= ReciboConceptoSinImporteException.class %>" message="recibo-concepto-sin-importe" />

<fieldset class="block-labels">
<legend><liferay-ui:message	key="conceptos" /></legend>
<input type="hidden" id="ids_actas" value="0"/>
<input type="hidden" id="ids_convenios" value="0"/>
<input type="hidden" id="total_cheques_rechazados" value="0"/>
<input type="hidden" id="total_cheques_no_depositados" value="0"/>
<input type="hidden" id="total_otros" value="0"/>

<table class="lfr-table" width="100%">
	<tr>
		<td colspan="4">
				<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
		  			<liferay-util:param name="cuit" value='<%= recibo!=null && recibo.getEmpresa() != null ? recibo.getEmpresa().getCuit() :	"" %>'/>
		  			<liferay-util:param name="sucu" value='<%=recibo!=null && recibo.getEmpresa() != null ? recibo.getEmpresa().getSucursal() :"" %>'/>
		  			<liferay-util:param name="razon" value='<%=recibo!=null && recibo.getEmpresa() != null ? recibo.getEmpresa().getRazon_soc() :"" %>'/>
		  			<liferay-util:param name="id_seccional" value='<%=recibo != null && recibo.getSeccional() != null ? String.valueOf(recibo.getSeccional().getId()) : new String("") %>'/>
		  			<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
			  		<liferay-util:param name="portlet_name" value='tesoreria'/>
				</liferay-util:include>
		</span>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td valign="top" colspan="4" width="50%">
			Actas&nbsp;<img src="/html/themes/classic/images/application/handle_se.png" title="Mostrar/Ocultar" onclick="cambiarEstado(jQuery('#<portlet:namespace />divActas'));" />
			<div id="<portlet:namespace />divActas">
				<%if (esEdicion){%>
				<input type="button"  value="<liferay-ui:message key="buscar-actas-no-os"/>" onClick="<portlet:namespace />buscarActas();" ><br/>
				<%} %>
				<div align="center" id="<portlet:namespace />agregandoActas">
				<table style="align: center;">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
				<div align="center" id="<portlet:namespace />actas">
					<liferay-util:include page="/html/portlet/uoma/recibos/recibo_no_os_actas.jsp"/>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td valign="top" colspan="4" width="50%">
		Convenios&nbsp;<img src="/html/themes/classic/images/application/handle_se.png" title="Mostrar/Ocultar" onclick="cambiarEstado(jQuery('#<portlet:namespace />divConvenios'));" />
		  <div id="<portlet:namespace />divConvenios">
			<%if (esEdicion){%>
			<input type="button"  value="<liferay-ui:message key="buscar-convenios" />" onClick="<portlet:namespace />buscarConvenios();" ><br/>
			<%} %>
			<div align="center" id="<portlet:namespace />agregandoConvenios">
				<table style="align: center;">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
				<div align="center" id="<portlet:namespace />convenios">
				<liferay-util:include page="/html/portlet/uoma/recibos/recibo_no_os_convenios.jsp"/>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td valign="top" colspan="4">
		Cheques No Depositados&nbsp;<img src="/html/themes/classic/images/application/handle_se.png" title="Mostrar/Ocultar" onclick="cambiarEstado(jQuery('#<portlet:namespace />divChqNoDepo'));" />
		  <div id="<portlet:namespace />divChqNoDepo">
			<%if (esEdicion){%>
				<input type="button"  value="<liferay-ui:message key="buscar-cheques-a-sustituir" />" onClick="<portlet:namespace />buscarChequeASustituir();" ><br/>
			<%} %>
				<div align="center" id="<portlet:namespace />agregandoChequeSust">
				<table style="align: center;">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
				<div align="center" id="<portlet:namespace />cheques_a_sust"> 
				<liferay-util:include page="/html/portlet/uoma/recibos/recibo_no_os_cheques_a_sustituir.jsp"/>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td valign="top" colspan="4">
			Cheques Rechazados&nbsp;<img src="/html/themes/classic/images/application/handle_se.png" title="Mostrar/Ocultar" onclick="cambiarEstado(jQuery('#<portlet:namespace />divChqRhz'));" />
		  <div id="<portlet:namespace />divChqRhz">
			<%if (esEdicion){%>
				<input type="button"  value="<liferay-ui:message key="buscar-cheques-rechazados" />" onClick="<portlet:namespace />buscarChequeRechazado();" ><br/>
			<%} %>
				<div align="center" id="<portlet:namespace />agregandoChequeRech">
				<table style="align: center;">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
				<div align="center" id="<portlet:namespace />cheques_rechazados">
				<liferay-util:include page="/html/portlet/uoma/recibos/recibo_no_os_cheques_rechazados.jsp"/>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td colspan="4">
		  Otros&nbsp;<img src="/html/themes/classic/images/application/handle_se.png" title="Mostrar/Ocultar" onclick="cambiarEstado(jQuery('#<portlet:namespace />divOtros'));" />
		  <div id="<portlet:namespace />divOtros">
				<%if (esEdicion){%>
				<select id="<portlet:namespace />otro_concepto" name="<portlet:namespace />otro_concepto">
					<option value=""></option>
					<% for (Concepto cc : conceptos) { %>
						<option value="<%=cc.getId() %>"><%= cc.getDescripcion()%></option>
					<% }%>
				</select>&nbsp;&nbsp;
				<liferay-ui:message key="importe"/>:&nbsp;
				<input type="text" id="<portlet:namespace />otro_importe" name="<portlet:namespace />otro_importe" size="20" maxlength="20" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"/>
				<input type="button" onclick="javascript:agregarOtroConcepto();" value="Agregar"/>
				<%} %>
				<div align="center" id="<portlet:namespace />agregandoOtros">
					<table style="align: center;">
						<tr>
							<td><liferay-ui:message key='buscando' /></td>
							<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>
				</div>
				<div align="center" id="<portlet:namespace />otros_result">
					<liferay-util:include page="/html/portlet/uoma/recibos/otros_no_os_search_result.jsp"/>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;
		</td>
	</tr>
</table>

</fieldset>
<script type="text/javascript">
	function <portlet:namespace />buscarActas(){		
		jQuery('#<portlet:namespace />agregandoActas').show();
		var entidad=jQuery('#<portlet:namespace/>entidad_bla').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/abm_recibo_no_os_actas'
			+'&cuit=' +trim(document.getElementById("<portlet:namespace />cuit_entidad").value)+'&entidad='+trim(entidad);

		var inputs = document.getElementsByTagName("input");
		for (var i=0; i<inputs.length; i++){
			if (inputs[i].name.indexOf("acta_") == 0){
				url += '&' + inputs[i].name + '=' + document.getElementById(inputs[i].name).value;
			}
		}
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />actas').load(url, function() {
					recargarIngresos();
					jQuery('#<portlet:namespace />agregandoActas').hide();
					sumarConceptos();
		}); 
	}
	
	function borraActa(id){
		jQuery('#<portlet:namespace />agregandoActas').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/abm_recibo_no_os_actas'
			+'&borrar=borrar'
			+'&acta_id=' + id;

		var inputs = document.getElementsByTagName("input");
		for (var i=0; i<inputs.length; i++){
			if (inputs[i].name.indexOf("acta_") == 0){
				url += '&' + inputs[i].name + '=' + document.getElementById(inputs[i].name).value;
			}
		}
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />actas').load(url, function() {
					recargarIngresos();
					jQuery('#<portlet:namespace />agregandoActas').hide();
					sumarConceptos();
		}); 
	}
	
	function <portlet:namespace />buscarConvenios(){
		jQuery('#<portlet:namespace />agregandoConvenios').show();
		var entidad=jQuery('#<portlet:namespace/>entidad_bla').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/abm_recibo_no_os_convenios'
			+'&cuit=' +trim(document.getElementById("<portlet:namespace />cuit_entidad").value)+'&entidad='+trim(entidad);;

		var inputs = document.getElementsByTagName("input");
		for (var i=0; i<inputs.length; i++){
			if (inputs[i].name.indexOf("convenio_") == 0){
				url += '&' + inputs[i].name + '=' + document.getElementById(inputs[i].name).value;
			}
		}
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />convenios').load(url, function() {
			recargarIngresos();
			jQuery('#<portlet:namespace />agregandoConvenios').hide();
			sumarConceptos();
			});
	}
	
	function borraConvenio(id){
		jQuery('#<portlet:namespace />agregandoConvenios').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/abm_recibo_no_os_convenios'
			+'&borrar=borrar'
			+'&convenio_id=' + id;

		var inputs = document.getElementsByTagName("input");
		for (var i=0; i<inputs.length; i++){
			if (inputs[i].name.indexOf("convenio_") == 0){
				url += '&' + inputs[i].name + '=' + document.getElementById(inputs[i].name).value;
			}
		}
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />convenios').load(url, function() {
					recargarIngresos();
					jQuery('#<portlet:namespace />agregandoConvenios').hide();
					sumarConceptos();
		}); 
	}
	
	function <portlet:namespace />buscarChequeASustituir(){
		jQuery('#<portlet:namespace />agregandoChequeSust').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/abm_recibo_cheques_a_sustituir'
			+'&cuit=' +trim(document.getElementById("<portlet:namespace />cuit_entidad").value);
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_a_sust').load(url, function() {
			recargarIngresos();
			jQuery('#<portlet:namespace />agregandoChequeSust').hide();
			sumarConceptos();
			});
	}
	
	function borraChequeADepositar(cheque_nro, id_banco){
		jQuery('#<portlet:namespace />agregandoChequeSust').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/abm_recibo_cheques_a_sustituir'
			+'&borrar=borrar'
			+'&cheque_nro=' + cheque_nro
			+'&id_banco=' + id_banco;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_a_sust').load(url, function() {
					recargarIngresos();
					jQuery('#<portlet:namespace />agregandoChequeSust').hide();
					sumarConceptos();
		}); 
	}
	
	function <portlet:namespace />buscarChequeRechazado(){
		jQuery('#<portlet:namespace />agregandoChequeRech').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/abm_recibo_cheques_rechazados'
			+'&cuit=' +trim(document.getElementById("<portlet:namespace />cuit_entidad").value);
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_rechazados').load(url, function() {
			recargarIngresos();
			jQuery('#<portlet:namespace />agregandoChequeRech').hide();
			sumarConceptos();
			});
	}
	
	function borraChequeChequeRechazado(cheque_nro, id_banco){
		jQuery('#<portlet:namespace />agregandoChequeRech').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/abm_recibo_cheques_rechazados'
			+'&borrar=borrar'
			+'&cheque_nro=' + cheque_nro
			+'&id_banco=' + id_banco;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_rechazados').load(url, function() {
					recargarIngresos();
					jQuery('#<portlet:namespace />agregandoChequeRech').hide();
					sumarConceptos();
		}); 
	}

	function sumarActa(cheques, limite, actaId, nro){
		var adicional = parseFloat(document.getElementById("acta_" + actaId).value);
		document.getElementById("total_acta_"+ actaId).value = Math.round((Math.round(cheques *100) / 100 
				+ Math.round(adicional *100) / 100 )*100)/100;
		if ( parseFloat(limite) < parseFloat(document.getElementById("acta_" + actaId).value) ){
			document.getElementById("acta_" + actaId).value= "0";
			document.getElementById("total_acta_" + actaId).value= cheques;
			alert("El valor total a ingresar para el acta " + nro + " no puede superar: " + limite + " (deuda - ingreso por cheques)");
		} 
		sumarConceptos();
	}

	function sumarConvenio(cheques, limite, convId, nro){		
		var adicional = parseFloat(document.getElementById("convenio_" + convId).value);		
		document.getElementById("total_convenio_"+ convId).value = Math.round((Math.round(cheques *100) / 100 
				+ Math.round(adicional *100) / 100 )*100)/100;
				
		if ( parseFloat(limite) < parseFloat(document.getElementById("convenio_" + convId).value) ){
			document.getElementById("convenio_" + convId).value= "0";
			document.getElementById("total_convenio_" + convId).value= cheques;
			alert("El valor total a ingresar para el convenio " + nro + " no puede superar: " + limite + " (deuda - ingreso por cheques)");
		} 
		sumarConceptos();
	}

	function agregarOtroConcepto(){
		if (trim(document.getElementById("<portlet:namespace />otro_concepto").value) == "") {
			alert("Debe seleccionar un concepto");
			return false;
		}

		var importe = trim(document.getElementById("<portlet:namespace />otro_importe").value);
		if (importe == "") {
			alert("Debe ingresar un importe");
			document.getElementById("<portlet:namespace />otro_importe").focus();
			return false;
		}

		jQuery('#<portlet:namespace />agregandoOtros').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/abm_recibo_no_os_otros_conceptos'
			+'&concepto_id=' + trim(document.getElementById("<portlet:namespace />otro_concepto").value)
			+'&importe=' + importe;
		url += '&rnd=' + Math.floor(Math.random()*100);
		
		
		jQuery('#<portlet:namespace />otros_result').load(url, function() {
					jQuery('#<portlet:namespace />agregandoOtros').hide();
					sumarConceptos();
		}); 

	}
	
	function borrarOtroConcepto( id) {
		jQuery('#<portlet:namespace />agregandoOtros').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/abm_recibo_no_os_otros_conceptos'
			+'&borrar=borrar'
			+'&oc_id=' + id;
			url += '&rnd=' + Math.floor(Math.random()*100);

		jQuery('#<portlet:namespace />otros_result').load(url, function() {
					jQuery('#<portlet:namespace />agregandoOtros').hide();
					sumarConceptos();
		}); 
	}


	function cambiarEstado(divElement){
		divElement.slideToggle('slow');
	}

	<%if (recibo != null && recibo.getActas() != null && recibo.getActas().size()>0){%>
		jQuery('#<portlet:namespace />divActas').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divActas').hide();
	<%}%>

	<%if (recibo != null && recibo.getConvenios() != null && recibo.getConvenios().size()>0){%>
		jQuery('#<portlet:namespace />divConvenios').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divConvenios').hide();
	<%}%>

	<%if (recibo != null && recibo.getChequesNoDepositados() != null && recibo.getChequesNoDepositados().size()>0){%>
		jQuery('#<portlet:namespace />divChqNoDepo').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divChqNoDepo').hide();
	<%}%>

	<%if (recibo != null && recibo.getChequesRechazados() != null && recibo.getChequesRechazados().size()>0){%>
		jQuery('#<portlet:namespace />divChqRhz').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divChqRhz').hide();
	<%}%>

	<%if (recibo != null && recibo.getOtrosConceptos() != null && recibo.getOtrosConceptos().size()>0){%>
	jQuery('#<portlet:namespace />divOtros').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divOtros').hide();
	<%}%>
	
	jQuery('#<portlet:namespace />agregandoActas').hide();
	jQuery('#<portlet:namespace />agregandoConvenios').hide();
	jQuery('#<portlet:namespace />agregandoChequeSust').hide();
	jQuery('#<portlet:namespace />agregandoChequeRech').hide();
	jQuery('#<portlet:namespace />agregandoOtros').hide();

	function cambiaCuit(){
	}
</script>