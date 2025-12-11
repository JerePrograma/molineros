<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%

		String portlet_name = "uoma";
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}
		
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 
 		
 		Date fIni= (Date)session.getAttribute("CONTROL_DECLARADOS_N1_FECHA_INICIAL");
 		if(fIni!=null) fechaDesde.setTime(fIni);
 		Date fFin= (Date)session.getAttribute("CONTROL_DECLARADOS_N1_FECHA_FINAL");
 		if(fFin!=null) fechaHasta.setTime(fFin);
%>	
		<fieldset class="block-labels">
				<legend>
					<liferay-ui:message key="cuenta-corriente-empresa" />
				</legend>
				<table class="lfr-table">			
					<tr>	
						<td><label><liferay-ui:message key="empresa" />:</label></td>
						<td colspan="6">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<liferay-util:param name="portlet_name" value='tesoreria'/>
						  		<liferay-util:param name="cuit" value=''/>
							</liferay-util:include>
						</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="1"
								monthParam="fechaDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								yearParam="fechaDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 30 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaHastaDia"																					
								dayValue="1"
								dayNullable="<%= true %>"
								monthParam="fechaHastaMes"
								monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"
								yearParam="fechaHastaAnio"
								yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 30 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<%-- 
						<td>							
							Solo con saldo&nbsp;<input type="checkbox" name="soloConSaldo" id="soloConSaldo" value="true" disabled/>
						</td>
						<td>							
							Consolidado&nbsp;<input type="checkbox" name="consolidado" id="consolidado" value="true" disabled/>
						</td>
						--%>
						<td>							
							Procesar&nbsp;<input type="checkbox" name="procesarConsulta" id="procesarConsulta" value="false"/>
						</td>
						<td>
							<input type="hidden" name="aportesContrib" id="aportesContrib"/>
						</td>
						<td>
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="javascript:<portlet:namespace />buscar_vista_0();"/>							
						</td>
						<td>
							<input id="<portlet:namespace />anterior" value="<liferay-ui:message key="Anterior"/>" title="<liferay-ui:message key="Anterior" />" type="button" onClick="javascript:<portlet:namespace />buscar_anterior();" hidden/>
						</td>
						<td>
							<input id="<portlet:namespace />exportar_v0" value="<liferay-ui:message key="Exportar"/>" title="<liferay-ui:message key="Exportar" />" type="button" onClick="javascript:<portlet:namespace />exportar_v0_xls();" hidden/>
							<input id="<portlet:namespace />exportar_v1" value="<liferay-ui:message key="Exportar"/>" title="<liferay-ui:message key="Exportar" />" type="button" onClick="javascript:<portlet:namespace />exportar_v1_xls();" hidden/>
							<input id="<portlet:namespace />exportar_v2" value="<liferay-ui:message key="Exportar"/>" title="<liferay-ui:message key="Exportar" />" type="button" onClick="javascript:<portlet:namespace />exportar_v2_xls();" hidden/>							
						</td>						
					</tr>						
					<tr>
						<td>							
							UOMA&nbsp;<input type="checkbox" name="soloUoma" 
							id="soloUoma" checked=checked value="true"
							onchange="<portlet:namespace />changeSoloUoma()"/>
						</td>
						<td>							
							AMTIMA&nbsp;<input type="checkbox" name="soloAmtima" 
							id="soloAmtima" checked=checked value="true"
							onchange="<portlet:namespace />changeSoloAmtima()"/>
							<input id="<portlet:namespace />exportar_actas_uoma" value="<liferay-ui:message key="Exportar Actas Uoma"/>" title="<liferay-ui:message key="Exportar Actas Uoma" />" type="button" onClick="javascript:<portlet:namespace />exportar_actas_uoma_xls();" hidden/>
							<input id="<portlet:namespace />exportar_actas_amtima" value="<liferay-ui:message key="Exportar Actas Amtima"/>" title="<liferay-ui:message key="Exportar Actas Amtima" />" type="button" onClick="javascript:<portlet:namespace />exportar_actas_amtima_xls();" hidden/>
						</td>
						<td>							
						</td>
						<td>																											
							
						</td>						
					</tr>
					<tr>
						<td colspan="7">&nbsp;</td>
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
						
			<div align="center" id="<portlet:namespace />busquedaCtaCteDiv">						
			</div>
			<div hidden>
			    <input id="<portlet:namespace/>nav" type="text" value=0>
			    <input id="<portlet:namespace/>nav_cuit" type="text" value="">
			    <input id="<portlet:namespace/>nav_cta" type="text" value="">
			</div>
		</fieldset>
			
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();

function <portlet:namespace />changeSoloUoma() {
	// Agrega Return porque se define que puedan tildarse ambos
	return;
	var _valUo = document.getElementById("soloUoma");
	var _valAm = document.getElementById("soloAmtima");
	if (_valUo.checked){
		_valAm.checked = false;
		
	} else{
		_valAm.checked = true;
	}	
}

function <portlet:namespace />changeSoloAmtima() {
	// Agrega Return porque se define que puedan tildarse ambos
	return;
	var _valUo = document.getElementById("soloUoma");
	var _valAm = document.getElementById("soloAmtima");
	if (_valAm.checked){
		_valUo.checked = false;
		
	} else{
		_valuo.checked = true;
	}	
}

function <portlet:namespace />buscarConsolidado(){
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();
	var soloConSaldo = document.getElementById("soloConSaldo");
	var procesarConsulta = document.getElementById("procesarConsulta");

	var soloUoma = document.getElementById("soloUoma");
	var soloAmtima = document.getElementById("soloAmtima");
	// var consolidado = document.getElementById("consolidado");

	if ((!soloUoma.checked) && (!soloAmtima.checked)) {
		alert("Debe seleccionar al menos Uoma o Amtima");	
	}
	
	jQuery('#<portlet:namespace />buscando').show();
	
	// Query para solo consulta
	// Proc para proceso y consulta
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/cuentacorriente_editar&cuit_entidad='+cuit_entidad+
	'&sucursal='+sucursal_entidad + 
	'&desde_mes=' + desde_mes + '&desde_anio=' + desde_anio + 
	'&hasta_mes=' + hasta_mes + '&hasta_anio=' + hasta_anio + 
	'&cmd=header' + '&procesar_consulta=' + (procesarConsulta.checked ? 'true' : 'false') +
	'&consolidado=0' + //(consolidado.checked ? '1' : '0') +
	'&solo_uoma=' + (soloUoma.checked ? '1' : '0') + 
	'&solo_amtima=' + (soloAmtima.checked ? '1' : '0');
	
	//alert(url);
	
	jQuery('#<portlet:namespace />busquedaCtaCteDiv').load(url, function() {
    	jQuery('#<portlet:namespace />buscando').hide();            															
      }
    );		
		
	
}

function <portlet:namespace />buscar_anterior(){
  var _nav=jQuery("#<portlet:namespace/>nav").val();
  
  if (_nav == "1") {	 
	  <portlet:namespace />buscar_vista_0();	  
  } else if ((_nav == "2") || (_nav == "3")) {

	  var _cuit = jQuery("#<portlet:namespace/>nav_cuit").val();
	  buscar_vista_1(_cuit);  
  }
  
}

// Agrupado Total o agrupado segun CUIT filtrado
function <portlet:namespace />buscar_vista_0(){		
	
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();
	var soloConSaldo = document.getElementById("soloConSaldo");
	var procesarConsulta = document.getElementById("procesarConsulta");

	var soloUoma = document.getElementById("soloUoma");
	var soloAmtima = document.getElementById("soloAmtima");
	// var consolidado = document.getElementById("consolidado");
			
	if ((!soloUoma.checked) && (!soloAmtima.checked)) {
		alert("Debe seleccionar al menos Uoma o Amtima");	
	}

	if ((procesarConsulta.checked) && (cuit_entidad.length == 0)) {
		alert("No se permite Procesar sin ingresar Cuit de Empresa");
		procesarConsulta.checked =false;
		return;
	}

	var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();
	if (pagina_sel == null)
		pagina_sel = 1;
	
	jQuery("#pagina").val(pagina_sel);	
	
	jQuery('#<portlet:namespace />buscando').show();

    var _nav_cuit = jQuery("#<portlet:namespace/>nav_cuit").val();

    if ((cuit_entidad == "") && (_nav_cuit != "")) {
    	pCuit =  _nav_cuit;    	    	
    }
    
	vista = 0;
	pCuit = cuit_entidad;
	
	// Query para solo consulta
	// Proc para proceso y consulta
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/cuentacorriente_editar&cuit_entidad='+pCuit+
	'&sucursal='+sucursal_entidad + 
	'&desde_mes=' + desde_mes + '&desde_anio=' + desde_anio + 
	'&hasta_mes=' + hasta_mes + '&hasta_anio=' + hasta_anio + 
	'&vista=' + vista + '&procesar_consulta=' + (procesarConsulta.checked ? 'true' : 'false') +
	'&consolidado=0' + // (consolidado.checked ? '1' : '0') +
	'&solo_uoma=' + (soloUoma.checked ? '1' : '0') + 
	'&solo_amtima=' + (soloAmtima.checked ? '1' : '0') +
	'&pagina='+pagina_sel;
	
	jQuery('#<portlet:namespace />busquedaCtaCteDiv').load(url, function() {
    																jQuery('#<portlet:namespace />buscando').hide();            															
    															  }
    );		
		
}

//Agrupado Total o agrupado segun CUIT filtrado
//Muestra Tipo de Cuenta
function buscar_vista_1(pCuit){		
	
	//alert('v1:' + pCuit);
	
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();
	var soloConSaldo = document.getElementById("soloConSaldo");
	var procesarConsulta = document.getElementById("procesarConsulta");

	var soloUoma = document.getElementById("soloUoma");
	var soloAmtima = document.getElementById("soloAmtima");
	// var consolidado = document.getElementById("consolidado");
	
	if ((!soloUoma.checked) && (!soloAmtima.checked)) {
		alert("Debe seleccionar al menos Uoma o Amtima");	
	}
	
	var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();
	if (pagina_sel == null)
		pagina_sel = 1;
	
	jQuery("#pagina").val(pagina_sel);	

	jQuery('#<portlet:namespace />buscando').show();
	
	vista = 1;
	
	if ((pCuit == null) || (pCuit == "") && (cuit_entidad != ""))
		pCuit = cuit_entidad;

	//alert("Asigna Cuit: " + pCuit);
	jQuery("#<portlet:namespace/>nav_cuit").val(pCuit);

	// Query para solo consulta
	// Proc para proceso y consulta
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/cuentacorriente_editar&cuit_entidad='+pCuit+
	'&sucursal='+sucursal_entidad + 
	'&desde_mes=' + desde_mes + '&desde_anio=' + desde_anio + 
	'&hasta_mes=' + hasta_mes + '&hasta_anio=' + hasta_anio + 
	'&vista=' + vista + '&procesar_consulta=' + (procesarConsulta.checked ? 'true' : 'false') +
	'&consolidado=0' + //(consolidado.checked ? '1' : '0') +
	'&solo_uoma=' + (soloUoma.checked ? '1' : '0') + 
	'&solo_amtima=' + (soloAmtima.checked ? '1' : '0');
	
	//alert(url);
	
	jQuery('#<portlet:namespace />busquedaCtaCteDiv').load(url, function() {
    																jQuery('#<portlet:namespace />buscando').hide();            															
    															  }
    );		
		
}

//Agrupado Total o agrupado segun CUIT filtrado
//Muestra Tipo de Cuenta
//Muestra Periodo
function buscar_vista_2(pCuit, pTipoCuenta){		
	
    //alert('v2:' + pCuit + ' ' + pTipoCuenta);
	
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();
	var soloConSaldo = document.getElementById("soloConSaldo");
	var procesarConsulta = document.getElementById("procesarConsulta");

	var soloUoma = document.getElementById("soloUoma");
	var soloAmtima = document.getElementById("soloAmtima");
	// var consolidado = document.getElementById("consolidado");

	if ((!soloUoma.checked) && (!soloAmtima.checked)) {
		alert("Debe seleccionar al menos Uoma o Amtima");	
	}

	var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();
	if (pagina_sel == null)
		pagina_sel = 1;
	
	jQuery("#pagina").val(pagina_sel);
	
	jQuery('#<portlet:namespace />buscando').show();
	
	vista = 2;
	
	if ((pCuit == null) || (pCuit == "") && (cuit_entidad != ""))
		pCuit = cuit_entidad;

	if (pTipoCuenta == null) {
		_tipo_cuenta = jQuery("#<portlet:namespace/>nav_cta").val();
		pTipoCuenta = _tipo_cuenta;
	}
	
	jQuery("#<portlet:namespace/>nav_cta").val(pTipoCuenta);
	
	// Query para solo consulta
	// Proc para proceso y consulta
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/cuentacorriente_editar&cuit_entidad='+pCuit+
	'&sucursal='+sucursal_entidad + 
	'&desde_mes=' + desde_mes + '&desde_anio=' + desde_anio + 
	'&hasta_mes=' + hasta_mes + '&hasta_anio=' + hasta_anio + 
	'&vista=' + vista + '&procesar_consulta=' + (procesarConsulta.checked ? 'true' : 'false') +
	'&tipo_boleta=' + pTipoCuenta + 
	'&consolidado=0' + //(consolidado.checked ? '1' : '0') +
	'&solo_uoma=' + (soloUoma.checked ? '1' : '0') + 
	'&solo_amtima=' + (soloAmtima.checked ? '1' : '0') +
	'&pagina='+ pagina_sel;
	
	// alert(url);
	
	jQuery('#<portlet:namespace />busquedaCtaCteDiv').load(url, function() {
    																jQuery('#<portlet:namespace />buscando').hide();            															
    															  }
    );		
		
}

function doExport(pVista) {
	  // alert("doExport Vista: " + pVista);

	  var _nav=jQuery("#<portlet:namespace/>nav").val();
	  var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	  var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	  var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	  var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	  var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	  var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	  var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	  var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	  var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();
	  var soloConSaldo = document.getElementById("soloConSaldo");
	  var procesarConsulta = document.getElementById("procesarConsulta");

	  var soloUoma = document.getElementById("soloUoma");
	  var soloAmtima = document.getElementById("soloAmtima");
	  // var consolidado = document.getElementById("consolidado");

	  if ((!soloUoma.checked) && (!soloAmtima.checked)) {
	    alert("Debe seleccionar al menos Uoma o Amtima");	
	  }

	  // jQuery('#<portlet:namespace />buscando').show();
		
	  vista = pVista;
		  
	  var pTipoCuenta = jQuery("#<portlet:namespace/>nav_cta").val();
	  var pCuit = jQuery("#<portlet:namespace/>nav_cuit").val();
	  	  
	  // alert("pCuit: " + pCuit);
	  // alert("cuit_ent: " + cuit_entidad);
	  if (((cuit_entidad != null) || (cuit_entidad.toString().trim() != "")) && 
	      ((pCuit == null) || (pCuit.toString().trim() == ""))
	     ) {
		// alert("Asinga");
		pCuit = cuit_entidad;  
	  }
	  
	  var url = '/xlsservlet/?reporte=REPORTE_DEUDA_CUENTACORRIENTE' +
	    '&cuit_entidad='+pCuit+
	    '&sucursal='+sucursal_entidad + 
	    '&desde_mes=' + desde_mes + '&desde_anio=' + desde_anio + 
		'&hasta_mes=' + hasta_mes + '&hasta_anio=' + hasta_anio + 
		'&vista=' + vista + '&procesar_consulta=' + (procesarConsulta.checked ? 'true' : 'false') +
		'&tipo_boleta=' + pTipoCuenta + 
		'&consolidado=0' + //(consolidado.checked ? '1' : '0') +
		'&solo_uoma=' + (soloUoma.checked ? '1' : '0') + 
		'&solo_amtima=' + (soloAmtima.checked ? '1' : '0');
	  
	  // alert (url);
	  
	  window.location.href = url;
	
}

function <portlet:namespace />exportar_v0_xls(){
	doExport(0);
}

function <portlet:namespace />exportar_v1_xls(){
	doExport(1);
}

function <portlet:namespace />exportar_v2_xls(){
  doExport(2);
}

function <portlet:namespace />exportar_actas_uoma_xls(){
	
	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();		
	var suc_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	
 	popupConfig = Liferay.Popup({title:"<liferay-ui:message key="Export UOMA"/>",modal:true,width:700,position:[150,10],xy: ['center', 100], 
 		onClose: function() {}});
 	
 	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/uoma/cuentacorriente_exportar';
 	url += '&accion=exp1';
 	url += '&cuit_entidad=' + cuit_entidad;
 	url += '&suc_entidad=' + suc_entidad;
 	
 	url += '&desde_mes=' + desde_mes + '&desde_anio=' + desde_anio + 
	       '&hasta_mes=' + hasta_mes + '&hasta_anio=' + hasta_anio 
 	
 	url += '&rnd=' + Math.floor(Math.random()*100);
 		
	//alert(url);
	
	jQuery(popupConfig).load(url);					
}

function <portlet:namespace />exportar_actas_amtima_xls(){
	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();
	var suc_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	
 	popupConfig = Liferay.Popup({title:"<liferay-ui:message key="Export AMTIMA"/>",modal:true,width:700,position:[150,10],xy: ['center', 100], 
 		onClose: function() {}});
 	
 	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/uoma/cuentacorriente_exportar';
 	url += '&accion=exp2';
 	url += '&cuit_entidad=' + cuit_entidad;
 	url += '&suc_entidad=' + suc_entidad;
 	
 	url += '&desde_mes=' + desde_mes + '&desde_anio=' + desde_anio + 
	       '&hasta_mes=' + hasta_mes + '&hasta_anio=' + hasta_anio 
 	
 	url += '&rnd=' + Math.floor(Math.random()*100);
 		
	//alert(url);
	
	jQuery(popupConfig).load(url);	
}


//Agrupado Total o agrupado segun CUIT filtrado y Periodo
//Muestra detalle
function buscar_vista_3(pCuit, pTipoCuenta, pPeriodo){		
	
    // alert('v3:' + pCuit + ' ' + pTipoCuenta + ' ' + pPeriodo);
	
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var id_seccional=jQuery("#<portlet:namespace/>id_seccional").val();
	var soloConSaldo = document.getElementById("soloConSaldo");
	var procesarConsulta = document.getElementById("procesarConsulta");

	var soloUoma = document.getElementById("soloUoma");
	var soloAmtima = document.getElementById("soloAmtima");
	// var consolidado = document.getElementById("consolidado");

	if ((!soloUoma.checked) && (!soloAmtima.checked)) {
		alert("Debe seleccionar al menos Uoma o Amtima");	
	}
	
	jQuery('#<portlet:namespace />buscando').show();
	
	vista = 3;
	
	if ((pCuit == null) || (pCuit == "") && (cuit_entidad != ""))
		pCuit = cuit_entidad;
	
	// Query para solo consulta
	// Proc para proceso y consulta
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/cuentacorriente_editar&cuit_entidad='+pCuit+
	'&sucursal='+sucursal_entidad + 
	'&desde_mes=' + desde_mes + '&desde_anio=' + desde_anio + 
	'&hasta_mes=' + hasta_mes + '&hasta_anio=' + hasta_anio + 
	'&vista=' + vista + '&procesar_consulta=' + (procesarConsulta.checked ? 'true' : 'false') +
	'&tipo_boleta=' + pTipoCuenta +
	'&periodo=' + pPeriodo +
	'&consolidado=0' +// (consolidado.checked ? '1' : '0') +
	'&solo_uoma=' + (soloUoma.checked ? '1' : '0') + 
	'&solo_amtima=' + (soloAmtima.checked ? '1' : '0');
	
	// alert(url);
	
	jQuery('#<portlet:namespace />busquedaCtaCteDiv').load(url, function() {
  																jQuery('#<portlet:namespace />buscando').hide();            															
  															  }
  );		
		
}

</script>