<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%@ include file="/html/portlet/farmacia_ospim/medicamentos/init.jsp"%>
<%
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysFarmaciaOspim.ROL_FARMACIA_OSPIM);
String nroRegistroTroquel = "";
boolean esEdicion = true;

String cmd = (String) request.getAttribute(Constants.CMD);

Calendar fechaDia  =Calendar.getInstance(); 		
fechaDia.setTime(new Date());

Calendar fechaReg = null;


Calendar fechaPeriodo = CalendarFactoryUtil.getCalendar();
fechaPeriodo.setTime(DateUtils.getLastDateOfYear(new Date(), true));

Vademecum medicacion = (Vademecum)request.getSession().getAttribute(WebKeysFarmaciaOspim.VADEMECUM_EN_EDICION  );

if (cmd != null && cmd.equalsIgnoreCase(Constants.VIEW)){
	esEdicion= false;
}


if(medicacion != null  ){
	nroRegistroTroquel ="Nro Registro : "+  String.valueOf(medicacion.getRegistro()  ) + ("<br/>") + "Nro. Troquel : " + String.valueOf(medicacion.getTroquel() );
	if (medicacion.getOrigenDeLosDatos()!=null &&  medicacion.getOrigenDeLosDatos().equals("SSS")){
		esEdicion= false;
	}
		
	if (medicacion.getPeriodoAltasBajas() != null) {
		fechaReg = Calendar.getInstance();
		fechaReg.setTime(medicacion.getPeriodoAltasBajas() );
	}
}


//NUEVO
//Para que vuelva a la vista anterior con el ancho de pantalla MAXIMIZED
PortletURL backURL = renderResponse.createRenderURL();
backURL.setWindowState(LiferayWindowState.MAXIMIZED);
backURL.setParameter("struts_action", "/farmaciaospim/view");
backURL.setParameter("tabs1", "vademecum");

themeDisplay.getPortletDisplay().setShowBackIcon(true);
themeDisplay.getPortletDisplay().setURLBack(backURL.toString());

		
%>
<style>

div.divHeaderNro {
  position: absolute;
  top: 210px;
  right:30;
  left:1000px;
  background-color: #cccccc;
  width:200px;
  height:20px;
  border:1px solid black;
  font-size:145%;
  font-weight: bold;
}
</style>

<form name="<portlet:namespace />vade_fm" id="<portlet:namespace />vade_fm" >
    
 	<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
 	<input  type="hidden" id="<portlet:namespace />idRegistro"	name="<portlet:namespace />idRegistro" size="8"  value="<%=Validator.isNotNull(medicacion)  ? medicacion.getRegistro()    : "0"  %>" />
    <input  type="hidden" id="<portlet:namespace />okTroquelRegistro"	name="<portlet:namespace />okTroquelRegistro" size="8"  value ='Okdd'  />
	<input  type="hidden" id="<portlet:namespace />origenDelDato"	name="<portlet:namespace />origenDelDato" size="8"  value =<%=Validator.isNotNull(medicacion)  ? medicacion.getOrigenDeLosDatos()     : "0"  %>" />	
<fieldset class="block-labels">
	<legend>		
		<liferay-ui:message key="reg-medicamento" /> 
	</legend>
	
	<div class="divHeaderNro">		     
		  <label><b> <%=nroRegistroTroquel%> </b>  </label>   
    </div>
		
<table class="lfr-table">
    <tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
		<tr>
		
		<td ><label><liferay-ui:message key="periodo" />:</label> </td>
		
			<% if (fechaReg!=null) {%>
			<td><liferay-ui:input-date dayParam="periodoDia"
				dayValue="1"
				dayNullable="<%= true %>" monthParam="periodoMes"
				monthValue="<%= fechaReg.get(Calendar.MONTH)%>"
				monthNullable="<%= true %>" yearParam="periodoAnio"
				yearValue="<%= fechaReg.get(Calendar.YEAR)%>"
				yearNullable="<%= true %>"
				yearRangeStart="<%= fechaDia.get(Calendar.YEAR)  -1%>"
				yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  %>"
				firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
				disabled="<%= !esEdicion %>" /> </td>
			<%}else{ %>
			<td> <liferay-ui:input-date dayParam="periodoDia"
				dayValue="1"
				dayNullable="<%= true %>" monthParam="periodoMes"
				monthValue="<%= fechaDia.get(Calendar.MONTH)%>"
				monthNullable="<%= true %>" yearParam="periodoAnio"
				yearValue="<%= fechaDia.get(Calendar.YEAR)%>"
				yearNullable="<%= true %>"
				yearRangeStart="<%= fechaDia.get(Calendar.YEAR)%>"
				yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  +1 %>"
				firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
				disabled="<%= !esEdicion %>" /></td>
			<%} %>
			
	
		<td colspan="1">&nbsp;</td>				
				
		
		<td colspan="1"><label><liferay-ui:message key="registro" /> :</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />registro"
			name="<portlet:namespace />registro" size="7" maxlength="7" onblur="validarRegistroTroquel();" 
			type="text" onkeydown="allowOnlyDigits(event);"
			 <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%> 
			value="<%= medicacion != null &&  medicacion.getRegistro()>0  ? medicacion.getRegistro()  : "" %>" />			
		</td>
		<td colspan="1"><label><liferay-ui:message key="troquel" /> :</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />troquel"
			name="<portlet:namespace />troquel" size="7" maxlength="7"
			type="text" onkeydown="allowOnlyDigits(event);"  
			value="<%= medicacion != null &&  medicacion.getTroquel()>0  ? medicacion.getTroquel()  : "" %>" />			
		</td>
		<td colspan="1"><label style="height:160px; color: red;" id="<portlet:namespace/>mensajeOrigenDato"></label></td>
		
		</tr>
	</table>
	
	<table class="lfr-table">
    	<tr>
			<td colspan="12">&nbsp;</td>
		</tr>	
		<tr>
		
		<td colspan="1"><label><liferay-ui:message key="nombre" /> :</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>  id="<portlet:namespace />nombre"
			name="<portlet:namespace />nombre" size="50" maxlength="50"
			type="text"
			value="<%= medicacion != null &&  medicacion.getNombre()!=null ? medicacion.getNombre():"" %>" />			
		</td>
		
		<td colspan="1"><label>Presentaci&oacute;n :</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%> id="<portlet:namespace />presentacion"
			name="<portlet:namespace />presentacion" size="50" maxlength="100"
			type="text"
			value="<%= medicacion != null &&  medicacion.getPresentacion()!=null ? medicacion.getPresentacion():"" %>" />			
		</td>		
		</tr>
		<tr>
		<td colspan="12">&nbsp;</td>
	</tr>	
	<tr>
		<td colspan="1"><label><liferay-ui:message key="laboratorio" /> :</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%> id="<portlet:namespace />laboratorio"
			name="<portlet:namespace />laboratorio" size="50" maxlength="100"
			type="text"
			value="<%= medicacion != null &&  medicacion.getLaboratorio() !=null   ? medicacion.getLaboratorio()   : "" %>" />			
		</td>
		<td colspan="1"><label>Acci&oacute;n</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>  id="<portlet:namespace />accion"
			name="<portlet:namespace />accion" size="50" maxlength="100"
			type="text"			
			value="<%= medicacion != null &&  medicacion.getAccion()  !=null   ? medicacion.getAccion()   : "" %>" />			
		</td>
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"><label><liferay-ui:message key="droga" /> :</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%> id="<portlet:namespace />droga"
			name="<portlet:namespace />droga" size="50" maxlength="100"
			type="text"
			value="<%= medicacion != null &&  medicacion.getDroga()  !=null   ? medicacion.getDroga()   : "" %>" />			
		</td>
		<td colspan="1"><label>PMO :</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />pmo"
			name="<portlet:namespace />pmo" size="10" maxlength="100"
			type="text" onkeydown="allowOnlyDigits(event);"
			value="<%= medicacion != null &&  medicacion  !=null   ? medicacion.getPmoe_n()   : "" %>" />			
		</td>
		
		<td colspan="1"><label>% SSSalud :</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%> id="<portlet:namespace />sssalud"
			name="<portlet:namespace />sssalud" size="7" maxlength="100"
			type="text" onkeydown="allowOnlyDigits(event);"
			value="<%= medicacion != null &&  medicacion !=null   ? medicacion.getPorc_sssalud()    : "" %>" />			
		</td>
		
	</tr>    
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td> 
			<liferay-ui:message key="PMI Madre" />:</label> 
			<input type="checkbox"id="<portlet:namespace />pmi_madre" name="<portlet:namespace />pmi_madre"  						
			<%=Validator.isNotNull(medicacion) && medicacion.isPmiMadre()   ? "checked" : "Unchecked" %>			
			<% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>   onclick="desactivarAnticoncepcion();"  >
		</td>
		<td>&nbsp;</td>
		<td> 
			<liferay-ui:message key="PMI Hijo" />:</label>
			 
			<input type="checkbox"id="<portlet:namespace />pmi_hijo" name="<portlet:namespace />pmi_hijo" 			
			<%=Validator.isNotNull(medicacion) && medicacion.isPmiHijo()    ? "checked" : "Unchecked" %>			
			<% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>  onclick="desactivarAnticoncepcion();" >
		</td>
		<td>&nbsp;</td>
		<td> 
			<liferay-ui:message key="Anticoncepción" />:</label> 
			<input type="checkbox"id="<portlet:namespace />anticoncepcion" name="<portlet:namespace />anticoncepcion"			
			<%=Validator.isNotNull(medicacion) && medicacion.isAnticoncepcion()   ? "checked" : "Unchecked" %>			
			<% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>  onclick="checkExclusive(this);desactivarPmi();" >
		</td>
		<td>&nbsp;</td>
		<td> 
			<liferay-ui:message key="Vademecum Gral" />:</label> 
			<input type="checkbox"id="<portlet:namespace />vade_gral" name="<portlet:namespace />vade_gral"			
			<%=Validator.isNotNull(medicacion) && medicacion.isVademecumGral()    ? "checked" : "Unchecked" %>			
			<% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>   > 
		</td>
	</tr>
	    
		<%-- <td colspan="2"><label>% Ospim :</label></td>
		<td colspan="1"><input <% if (medicacion != null) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />ospim"
			name="<portlet:namespace />ospim" size="7" maxlength="100"
			type="text" onkeydown="allowOnlyDigits(event);"
			value="<%= medicacion != null &&  medicacion !=null   ? medicacion.getPorc_ospim()    : "" %>" />			
		</td>
		<td>&nbsp;</td>				
		<td colspan="2"><label>% Amtima :</label></td>
		<td colspan="1"><input <% if (medicacion != null) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />amtima"
			name="<portlet:namespace />amtima" size="7" maxlength="100"
			type="text" onkeydown="allowOnlyDigits(event);"
			value="<%= medicacion != null &&  medicacion !=null   ? medicacion.getPorc_amtima()  : "" %>" />			
		</td> --%>
		
	
	<tr>
	    <td>&nbsp;</td>
	</tr>
		
</table>       
<% if(medicacion != null){%>
		<div>
			<a href="javascript:mostrarHistoricoPrecios();" id="<portlet:namespace />mostrarHistoricoPreciosLink" >" <liferay-ui:message key="ver-historico-precios" /></a>
				<div align="left" id="<portlet:namespace />histo_precios">
				   <a href="javascript:ocultarHistoricoPrecios();" id="<portlet:namespace />ocultarHistoricoPreciosLink" >" <liferay-ui:message key="ocultar-historico-precios" /></a>						   
				   <liferay-util:include page='/html/portlet/farmacia_ospim/medicamentos/lista_precios_medicamento.jsp' />
				</div>
		</div>		
<%} %>				
</fieldset>    
<br/>

<div id="<portlet:namespace />saveVademecumDiv" align="left">
<p>
<%if(esEdicion && medicacion == null){ %>
<input type="button" value="<liferay-ui:message key="Grabar" />"
	onClick="<portlet:namespace />saveMedicacion();"/>	
	
<%}else if(esEdicion && medicacion != null){ %>	
<input type="button" value="<liferay-ui:message key="Actualizar" />"		
	onClick="<portlet:namespace />editaMedicacion();"/>
<%} %>		
&nbsp;
</p>
</div>

<%-- <div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div> --%>
</form>

<script type="text/javascript">

/* var popupMD; */

	jQuery('#<portlet:namespace />buscando').hide();
	jQuery("#<portlet:namespace />printButton").hide();
	jQuery("#<portlet:namespace />periodoDia").hide();
	jQuery('#<portlet:namespace />diveditVademecum').hide();
	
	ocultarHistoricoPrecios();
	<%if(medicacion == null ){%>
		seteaFechaPeriodo();	
		jQuery("#<portlet:namespace/>mensajeOrigenDato").html("");
	<%}%>
	
	<%if (medicacion!=null && medicacion.getOrigenDeLosDatos()!=null   && medicacion.getOrigenDeLosDatos().equals("SSS")){%>
		jQuery("#<portlet:namespace/>mensajeOrigenDato").html("Dato Informado por la SSS.");
	<%}%>
	
	document.getElementById("<portlet:namespace />periodoAnio").disabled = true;
	document.getElementById("<portlet:namespace />periodoMes").disabled = true;
	
	
	function activarControles(){
		document.getElementById("<portlet:namespace />droga").disabled = "";
		document.getElementById("<portlet:namespace />nombre").disabled = "";
		document.getElementById("<portlet:namespace />accion").disabled = "";
		document.getElementById("<portlet:namespace />presentacion").disabled = "";
		document.getElementById("<portlet:namespace />laboratorio").disabled = "";
		document.getElementById("<portlet:namespace />troquel").disabled = "";
		document.getElementById("<portlet:namespace />registro").disabled = "";
	}
	
	function <portlet:namespace />saveMedicacion() {
				
		if ( validaDatos())  {
			var accionEnCurso = document.<portlet:namespace />vade_fm.<portlet:namespace /><%= Constants.CMD %>.value;
			document.<portlet:namespace />vade_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.SAVE  %>';
			var xportletUrl = '/farmaciaospim/editar_borrar_vademecum_entry';		
			var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+
			'<liferay-portlet:param name="esDatosTab" value="true"/>'+
			'</liferay-portlet:actionURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__accionEnCurso", accionEnCurso);  	      
	  	    // habilita el periodo
	  	    document.getElementById("<portlet:namespace />periodoAnio").disabled = false;
	  	    document.getElementById("<portlet:namespace />periodoMes").disabled = false;
			document.<portlet:namespace />vade_fm.method = 'post';
			activarControles();
			submitForm(document.<portlet:namespace />vade_fm, url);
		}	
	}	
	
	function <portlet:namespace />editaMedicacion(){
		<% if ( medicacion!=null ) { %>
	     if (validaDatos())	{
				var accionEnCurso = document.<portlet:namespace />vade_fm.<portlet:namespace /><%= Constants.CMD %>.value;
				document.<portlet:namespace />vade_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.UPDATE %>';
				var xportletUrl = '/farmaciaospim/editar_borrar_vademecum_entry';
				document.getElementById("<portlet:namespace />periodoAnio").disabled = false;
		  	    document.getElementById("<portlet:namespace />periodoMes").disabled = false;
				var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
				'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
				'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+
				'<liferay-portlet:param name="esDatosTab" value="false"/>'+
				'</liferay-portlet:actionURL>';
			    url = url.replace("__xportletUrl",xportletUrl); 
		  	    url = url.replace("__accionEnCurso", accionEnCurso);
				document.<portlet:namespace />vade_fm.method = 'post';
				activarControles();
				submitForm(document.<portlet:namespace />vade_fm, url);
	   	 }				
	     <%}%>
	}
	
	
	function validarNroRegistrooTroquel(esTroquel){
		validarRegistroTroquel();
	}
		
	function validaDatos(){
		
		// periodo
		var mesExist1  = isNaN(parseInt(jQuery("#<portlet:namespace />periodoMes").val()));
		var anioExist1 = isNaN(parseInt(jQuery("#<portlet:namespace />periodoAnio").val()));	
		var datos = document.getElementById("<portlet:namespace />okTroquelRegistro").value;
		var nombreMedic = jQuery('#<portlet:namespace />nombre').val();
		
		if(mesExist1 || anioExist1){
			alert("Debe ingresar el Período.");
			return false ;
		}
		
		if (datos=='Bad Troquel'){
	    	alert('El nro de Troquel esta asociado a otro medicamento.');
	    	return false ;
	    }
		if (datos=='Bad Registro'){
	    	alert('El nro de Registro esta asociado a otro medicamento.');
	    	return false ;
	    }
		
		var nroRegistro=jQuery('#<portlet:namespace />registro').val();
		
	    if (nroRegistro==""){		
	    	
			alert ('Debe ingresar el nro de registro.');		
			jQuery('#<portlet:namespace />registro').focus();
			return false;
		}
		
	    var nroTroquel =jQuery('#<portlet:namespace />troquel').val();
		
	    if (nroTroquel==""){
	    	
			alert ('Debe ingresar el troquel.');		
			jQuery('#<portlet:namespace />troquel').focus();
			return false;
		}
	    
	    /* if ( nroTroquel.length<7 && respuesta ){
	    	alert ('El troquel debe tener 7 digitos.');		
			jQuery('#<portlet:namespace />troquel').focus();
			return false;
	    } */
	    
	    if (nombreMedic=="")  {    	
	    	alert ('Debe ingresar el nombre del medicamento.');
			jQuery('#<portlet:namespace />nombre').focus();
			return false;
	    }    
	    	
		return true;
	}
	
	function cleanDatos(){
		jQuery('#<portlet:namespace />droga').val("");
		jQuery('#<portlet:namespace />nombre').val("");
		jQuery('#<portlet:namespace />accion').val("");
		jQuery('#<portlet:namespace />presentacion').val("");
		jQuery('#<portlet:namespace />laboratorio').val("");
		jQuery('#<portlet:namespace />troquel').val("");
	}
	
	function validarRegistroTroquel(){
		
		return true;
		
		var params="";
		var nroRegistro=jQuery('#<portlet:namespace />registro').val();
		var nroTroquel =jQuery('#<portlet:namespace />troquel').val();
		var idMedicamento=0;
		
		cleanDatos();
		
		  <%if (medicacion!=null && medicacion.getRegistro()>0){%>
		  	var idMedicamento=<%=medicacion.getRegistro()%>;
		  <%}%>
		  jQuery('#<portlet:namespace />okTroquelRegistro').val("Ok");
			var xportletUrl = '/farmaciaospim/consultar_nro_registro_medicamento';
			var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="nroRegistro" value="__nroRegistro"/>'+
			 '<liferay-portlet:param name="esVademecum" value="__esVademecum"/>'+
			'</liferay-portlet:renderURL>';
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__nroRegistro", nroRegistro);
	  	    url = url.replace("__esVademecum","true");  	  
	  	  	
		 jQuery.ajax({   
			url: url,
			async: false,
			success: function(data) {
				var obj = jQuery.parseJSON(data);
				var nombre = obj.nombreMedicacion;
				var estaSoloEnMedicamentos  = obj.nroRegistroSoloEnMedicamentos ;
				var estaEnVademecum = obj.estaEnVademecum;
				
				if (estaEnVademecum== 'true'){
					alert('El Número Registro existe en Vademecum y esta asociado al medicamento ' + nombre);
					jQuery('#<portlet:namespace />okTroquelRegistro').val("Bad Registro");
				} else if (estaSoloEnMedicamentos=='true'){
					jQuery('#<portlet:namespace />droga').val(obj.droga);
					jQuery('#<portlet:namespace />nombre').val(obj.nombreMedicacion);
					jQuery('#<portlet:namespace />accion').val(obj.accion);
					jQuery('#<portlet:namespace />presentacion').val(obj.presentacion);
					jQuery('#<portlet:namespace />laboratorio').val(obj.laboratorio);
					jQuery('#<portlet:namespace />troquel').val(obj.troquel);
				} else {
					alert('El Número Registro no existe.');
					jQuery('#<portlet:namespace />okTroquelRegistro').val("Bad Registro");
				}
			}
		 });
	
		 return false;
	}
	
	function mostrarHistoricoPrecios(){
		jQuery('#<portlet:namespace />mostrarHistoricoPreciosLink').hide();
		jQuery('#<portlet:namespace />histo_precios').show();
	}
	
	function ocultarHistoricoPrecios() {
		jQuery('#<portlet:namespace />histo_precios').hide();
		jQuery('#<portlet:namespace />mostrarHistoricoPreciosLink').show();
	}
	
	
	function seteaFechaPeriodo(){	
	 	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmaciaospim/recuperar_periodo_vademecum';		
			jQuery.ajax({   
				url: url,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					var fechaVto = obj.fechaPeriodo;
					if(fechaVto !=null){	
						var vVto = fechaVto.split("-");
						var vto = new Date(vVto[2],vVto[1],vVto[0]);
					    jQuery("#<portlet:namespace />periodoAnio").each(function() { 
					    	this.selected = (this.text ==vVto[2] ); });
					}
					document.getElementById("<portlet:namespace />periodoMes").options[parseInt(vVto[1])].selected=true;
				}
			});
			return true;
	}
	
	
	function checkExclusive(obj){
		var that = obj;
		if (document.getElementById(that.id).checked){
			document.getElementById('<portlet:namespace />pmi_madre').checked = false;
			document.getElementById('<portlet:namespace />pmi_hijo').checked = false;
			document.getElementById('<portlet:namespace />anticoncepcion').checked = false;
			document.getElementById(that.id).checked = true;
		}else {
			document.getElementById(that.id).checked = false;
		}	
	}
	 
	function desactivarAnticoncepcion() {
		if ( document.getElementById("<portlet:namespace />pmi_hijo").checked || document.getElementById("<portlet:namespace />pmi_madre").checked ) {
			document.getElementById('<portlet:namespace />anticoncepcion').disabled = true ;
		}else{
			document.getElementById('<portlet:namespace />anticoncepcion').disabled = false;	
		}
	}
	
	function desactivarPmi() {
			document.getElementById('<portlet:namespace />pmi_madre').disabled = document.getElementById("<portlet:namespace />anticoncepcion").checked;
			document.getElementById('<portlet:namespace />pmi_hijo').disabled = document.getElementById("<portlet:namespace />anticoncepcion").checked;	
	}

</script>

