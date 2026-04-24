<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
	response.setHeader("Cache-Control", "no-store"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", 0); //prevents caching at the proxy server
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/autorizaciones/view");	
	//verificar los calendars
	Calendar fechaOspim = CalendarFactoryUtil.getCalendar();
	fechaOspim.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
	Calendar fechaOspimHta = CalendarFactoryUtil.getCalendar();
	fechaOspimHta.setTime(DateUtils.getLastDateOfYear(new Date(), true));	
	Calendar fechaCierreReclamo = CalendarFactoryUtil.getCalendar();
	fechaCierreReclamo.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
	Calendar fechaCierreReclamo1 = CalendarFactoryUtil.getCalendar();
	fechaCierreReclamo1.setTime(DateUtils.getLastDateOfYear(new Date(), true));
	Integer nroLote = ReclamosPrestacionesServiceUtil.getLoteVigenteReclamoPrestacional(); 	
	
	Calendar fechaseccional  = Calendar.getInstance();
	
	String  isSecc = WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL;
	
	

	BusquedaReclamoSeccionalFiltro filtro = (BusquedaReclamoSeccionalFiltro) request.getSession().getAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS_SECCIONAL);
	Calendar fechaDesde =null; 		
	Calendar fechaHasta = null;
	String nroReclamo = "";
	String tipoPedidoSel ="";
	String sectorSel = "";
	String nroAfilido="";
	String cuil="";
	String integrante="";
	String tipoDocumento="";
	String nroDocumento="";
	String idSeccional="";
	String apellido="";
	String nombre="";
	String idSeccionalAfiSel="";
	String descSeccionalAfiSel="";
	
	if(filtro != null){
		if(filtro.getFechaDesde()!=null){
			fechaDesde =  Calendar.getInstance();
			fechaDesde.setTime(filtro.getFechaDesde());
		}
		if(filtro.getFechaHasta()!=null){
			fechaHasta =  Calendar.getInstance();
			fechaHasta.setTime(filtro.getFechaHasta());
		}	
		nroReclamo=filtro.getNroReclamo();
		tipoPedidoSel=filtro.getTipoPedido();
		sectorSel=filtro.getSector();
		nroAfilido=filtro.getNroAfilido();
		cuil=filtro.getCuil();
		integrante=filtro.getIntegrante();
		tipoDocumento=filtro.getTipoDocumentoSel();
		nroDocumento=filtro.getNroDocumento();
		apellido = filtro.getApellido();
		nombre = filtro.getNombre();
		idSeccionalAfiSel = filtro.getIdSeccionalAfiliado();
		descSeccionalAfiSel = filtro.getDescSeccionalAfiliado();
	}
	
	
	
	
%>

<form action="<%=portletURL%>" method="post"
	name="<portlet:namespace />fm"
	onSubmit="submitForm(this); return false;">
	
<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />	
	
	<liferay-portlet:renderURLParams varImpl="portletURL" />
	<fieldset class="block-labels">
	<legend> <liferay-ui:message key="Datos del Reclamo Prestacional" /> 
	</legend>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		<tr>
			<td><label>Nro Reclamo:</label></td>
			<td><input id="<portlet:namespace />nroReclamoFiltro" name="<portlet:namespace />nroReclamoFiltro" size="10" maxlength="10" type="text" value="" /></td>
			<td>&nbsp;</td>
			<td><label><liferay-ui:message key="Tipo Pedido" />:</label></td>
			<td>
				<select name="<portlet:namespace />tipopedido" id="<portlet:namespace />tipopedido" >
					<option value="REINTEGRO">REINTEGRO</option>
				</select>					
			</td>
			<td>&nbsp;</td>
				
			
			<td><label><liferay-ui:message key="Sector" />:</label></td>	
			<td><table>
					<select name="<portlet:namespace/>sector"
						id="<portlet:namespace/>sector" >
						<option value="SELECCIONE" >SELECCIONE</option>
						<option value="DISCAPACIDAD">DISCAPACIDAD</option>
						<option value="PRESTACIONES MEDICAS">PRESTACIONES MEDICAS</option>
						<option value="FARMACIA">FARMACIA</option>
						<option value="ODONTOLOGIA">ODONTOLOGIA</option>
					</select>					
				</table></td>
			<td>&nbsp;</td>
					
		
		<tr>
			<td><label><liferay-ui:message key="Estados" />: &nbsp;&nbsp;&nbsp;&nbsp;</label></td>
			<td><select   
				 name="<portlet:namespace/>estado"
				id="<portlet:namespace/>estado">
					<option  value="-1">TODOS</option>
					<% for (EstadosReclamosPrestacionales estados : listaestados) { %>
						<% if ((estados.getId() == 0) || (estados.getId() == 5) || (estados.getId() == 6)) {%>
						
						<option
							value="<%= estados.getId() %>"><%=estados.getDescripcion()%>
							</option>
						<% } %>
					<% } %>										
			</select>	
			 </td> 
			<td colspan="2">&nbsp;&nbsp;&nbsp;</td>				
		</tr>
		
		<tr>			
			<td><label><liferay-ui:message key="fecha-desde-ospim" />:</label> </td>
			<td colspan="2"><liferay-ui:input-date dayParam="fechaSeccionalDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaSeccionalMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaSeccionalAnio"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaOspim.get(Calendar.YEAR)  -2%>"
			yearRangeEnd="<%= fechaOspim.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaOspim.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" /> </td>
							
			<td><label><liferay-ui:message key="fecha-hasta-ospim" />:</label> </td>
			<td colspan="2"> <liferay-ui:input-date dayParam="fechaSeccionalDiaHta"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaSeccionalMesHta"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaSeccionalAnioHta"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaOspimHta.get(Calendar.YEAR) -2%>"
			yearRangeEnd="<%= fechaOspimHta.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaOspimHta.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" /></td>
		
		</tr>	
</table>
</fieldset>

	
<table class="tabla-afiliado" >

	<tr>
		<td>
		<fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend> 
			
			<liferay-util:include page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>
				<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
				<liferay-util:param value="<%=String.valueOf(true)%>" name="discapacidad" />
				<liferay-util:param name="pag_reintegro" value='1' />				
			</liferay-util:include>
		</fieldset>
		</td>
	</tr>
</table>


  
<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
	<tr>
		<td colspan="4" align="left"><input id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar-reclamo-prestacion" />" type="button" />
		</td>	
		
	
		
		<td colspan="4" align="left">
			<input type="button" value="<liferay-ui:message key="limpiar"/>"
 			title="<liferay-ui:message key="limpiar" />" 
			onClick="<portlet:namespace />limpiarFiltros();" />
		</td>
		
		
		
		
		<td colspan="4" align="left">
			<input type="button" value="<liferay-ui:message key="Nuevo"/>"
 			title="<liferay-ui:message key="nuevo-reclamo-prestacion" />" 
			onClick="<portlet:namespace />altaReclamoPrestacional();" />
		</td>
						
	</tr>

</table>

<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
<div align="center" id="<portlet:namespace />busquedaReclamoPrestaDiv">
</div>
</fieldset>
</form>

<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />
<input id="<portlet:namespace />nom_seleccionado" name="<portlet:namespace />nom_seleccionado" type="hidden" value=""/>
<input id="<portlet:namespace />codigogestion" name="<portlet:namespace />codigogestion" type="hidden" value=""/>
<!-- <input type="hidden"   name="pagina" id="pagina" value="5"/> -->
<script type="text/javascript">
  
    jQuery("#<portlet:namespace />busqueda_prestaciones").hide();        
    jQuery("#<portlet:namespace />busqueda_farmacia").hide();  
    jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_reclamos_prestaciones_seccional_sesion';
	jQuery('#<portlet:namespace />busquedaReclamoPrestaDiv').load(url);
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){<portlet:namespace />busquedaReclamosPrestacionales();});
	
// rutinas de botones de la grilla de busqueda 
function borrarReclamoPrestacional(id_reclamo) {
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-reclamoprestacional'/>")){
			return false;
		}else{			
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE
				.toString()%>"/>&struts_action=/autorizaciones/editar_reclamosprestaciones_entry&id_reclamosel='+id_reclamo;						
	jQuery('#<portlet:namespace />busquedaReclamoPrestaDiv').load(url, function() {				
		<portlet:namespace />busquedaReclamosPrestacionales();
			});
		}
	}

	jQuery(document).ready(function() {
		
		<portlet:namespace />loadFiltros();
	
	});


	function <portlet:namespace />loadFiltros() {
		
		 jQuery('#<portlet:namespace />nroReclamoFiltro').val(<%=nroReclamo%>);
		
		var cmbSector = "<%=sectorSel%>";
		jQuery('#<portlet:namespace />sector').val(cmbSector);
		jQuery('#<portlet:namespace />numero_afi').val(<%=nroAfilido%>);
		var cmbTipoDoc = "<%=tipoDocumento%>";
		jQuery('#<portlet:namespace />tipoDoc').val(cmbTipoDoc);
		jQuery('#<portlet:namespace />nroDoc').val(<%=nroDocumento%>);
		var txtApellido  = "<%=apellido%>";
		jQuery('#<portlet:namespace />apellido').val(txtApellido);
		var txtNombre  = "<%=nombre%>";
		jQuery('#<portlet:namespace />nombre').val(txtNombre);

		jQuery('#<portlet:namespace />cuil').val(<%=cuil%>);
		jQuery('#<portlet:namespace />inte').val(<%=integrante%>);
		jQuery('#<portlet:namespace />id_seccional').val(<%=idSeccionalAfiSel%>);
		var seccional = "<%=descSeccionalAfiSel%>";
		jQuery('#<portlet:namespace />seccional').val(seccional);
		
		jQuery('#<portlet:namespace />fechaSeccionalDia').val(<%=fechaDesde != null ? fechaDesde.get(Calendar.DAY_OF_MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaSeccionalMes').val(<%=fechaDesde != null ? fechaDesde.get(Calendar.MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaSeccionalAnio').val(<%=fechaDesde != null ? fechaDesde.get(Calendar.YEAR) :""%>);

		jQuery('#<portlet:namespace />fechaSeccionalDiaHta').val(<%=fechaHasta != null ? fechaHasta.get(Calendar.DAY_OF_MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaSeccionalMesHta').val(<%=fechaHasta != null ? fechaHasta.get(Calendar.MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaSeccionalAnioHta').val(<%=fechaHasta != null ? fechaHasta.get(Calendar.YEAR) :""%>);
		
	}
	
	function <portlet:namespace />limpiarFiltros(){
		var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";

		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_reclamos_prestacionales_seccional'
		url = url + "&<%= Constants.CMD %>=<%=Constants.EXPIRE %>";
		url = url + params;
		

		jQuery('#<portlet:namespace />busquedaReclamoPrestaDiv').load(url, function() {
				jQuery('#<portlet:namespace />buscando').hide();            															
			  }
		);
        		
		jQuery('#<portlet:namespace />estado').val('');
		jQuery('#<portlet:namespace />nroReclamoFiltro').val('');
		jQuery('#<portlet:namespace />sector').val('');
		jQuery('#<portlet:namespace />numero_afi').val('');
		jQuery('#<portlet:namespace />tipoDoc').val('');
		jQuery('#<portlet:namespace />nroDoc').val('');
		jQuery('#<portlet:namespace />apellido').val('');
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');
		jQuery('#<portlet:namespace />id_seccional').val('');
		jQuery('#<portlet:namespace />fechaSeccionalDia').val('');
		jQuery('#<portlet:namespace />fechaSeccionalMes').val('');
		jQuery('#<portlet:namespace />fechaSeccionalAnio').val('');
		jQuery('#<portlet:namespace />fechaSeccionalDiaHta').val('');
		jQuery('#<portlet:namespace />fechaSeccionalMesHta').val('');
		jQuery('#<portlet:namespace />fechaSeccionalAnioHta').val('');
		jQuery('#<portlet:namespace />seccional').val('');	
		<portlet:namespace />limpiarCamposAfiliado();

	}

	
	
	
	function <portlet:namespace />busquedaReclamosPrestacionales(){

	
		
		
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();

		var fechaSeccionalDia=jQuery('#<portlet:namespace />fechaSeccionalDia').val();
		var fechaSeccionalMes=jQuery('#<portlet:namespace />fechaSeccionalMes').val();
		var fechaSeccionalAnio=jQuery('#<portlet:namespace />fechaSeccionalAnio').val();
		
		var fechaSeccionalDiaHta=jQuery('#<portlet:namespace />fechaSeccionalDiaHta').val();
		var fechaSeccionalMesHta=jQuery('#<portlet:namespace />fechaSeccionalMesHta').val();
		var fechaSeccionalAnioHta=jQuery('#<portlet:namespace />fechaSeccionalAnioHta').val();
		
		// -1 Todos, deberia traer solo 0 Precarga y 5 observado
		// Luego, permitir filtrar solo por 0 o 5
		var estado=jQuery('#<portlet:namespace/>estado').val();
		
		var resolucion=jQuery('#<portlet:namespace/>resolucion').val();
		var tipoPedido = jQuery("#<portlet:namespace />tipopedido").val();
		var sectorSeleccionado =jQuery("#<portlet:namespace/>sector").val();   
		
		
		if (sectorSeleccionado=="SELECCIONE"){
			sectorSeleccionado="";
		}
		
		var nroAutorizacion=jQuery('#<portlet:namespace />nroAutorizacionFiltro').val();
		var nroReclamo=jQuery('#<portlet:namespace />nroReclamoFiltro').val();	
	     
		if(trim(cuil).length != 0 && !validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
			alert("Cuil inválido");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		
	    	    
	    jQuery("#<portlet:namespace />nom_seleccionado").val('0')
	 
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		
		var tipoDoc = jQuery('#<portlet:namespace />tipoDoc').val();
		var nroDoc = jQuery('#<portlet:namespace />nroDoc').val();
		var apellido = jQuery('#<portlet:namespace />apellido').val();
		var nombre = jQuery('#<portlet:namespace />nombre').val();
		var seccionalSelAfi = jQuery('#<portlet:namespace />id_seccional').val();
		var descSeccionalSelAfi = jQuery('#<portlet:namespace />seccional').val();	
		
	    var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();			
		jQuery("#pagina").val(pagina_sel);		
		var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";
		
		jQuery('#<portlet:namespace />buscando').show();
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_reclamos_prestacionales_seccional&entidad='+entidad+		
		'&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+numero_afi+'&estado='+estado+
		'&fechaSeccionalDia='+fechaSeccionalDia+'&fechaSeccionalMes='+fechaSeccionalMes+'&fechaSeccionalAnio='+fechaSeccionalAnio+
		'&fechaSeccionalDiaHta='+fechaSeccionalDiaHta+'&fechaSeccionalMesHta='+fechaSeccionalMesHta+'&fechaSeccionalAnioHta='+fechaSeccionalAnioHta+
		'&numero=0'+'&nroautorizacion='+nroAutorizacion+'&nroReclamo='+nroReclamo+ 
		'&sectorSel='+encodeURI(sectorSeleccionado)+'&pagina='+pagina_sel+'&codigotipogestion='+'0'
		+'&tipoPedido='+tipoPedido+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&apellido='+encodeURI(apellido)
		+'&nombre='+encodeURI(nombre)+'&seccionalSelAfi='+seccionalSelAfi+'&inteFiltro='+inte
		+'&nroReclamoFiltro='+nroReclamo+'&descSeccionalSelAfi='+encodeURI(descSeccionalSelAfi);    
		
		url = url + params;
		
        jQuery('#<portlet:namespace />busquedaReclamoPrestaDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	}
	

	
	function <portlet:namespace />altaReclamoPrestacional() {		
		var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";
		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" /></portlet:renderURL>';		
		document.<portlet:namespace />fm.method = 'post';
		
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');

		url = url + params;
		
		submitForm(document.<portlet:namespace />fm, url);
	}
		

	

	
</script>

<style>
  .tabla-afiliado{ 
  	width: 100%;
    box-sizing: border-box;
    padding: 10px;
    margin: 0 auto 10px auto;
    }
</style>