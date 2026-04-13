<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
String portlet_name=null;
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
Calendar emisionFecha = CalendarFactoryUtil.getCalendar();
BusquedaFacturasFiltro filtro = (BusquedaFacturasFiltro) request.getSession().getAttribute(WebKeysUOMA.FILTRO_BUSQUEDA_FACTURAS);
if(filtro==null){
	filtro = new BusquedaFacturasFiltro();
}
String ptoVtaAfip="00000";

try{
	ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
}catch(Exception e){
	ptoVtaAfip="00000";
}
%>
<portlet:defineObjects />

	<fieldset class="block-labels">
			<legend><liferay-ui:message key="facturacion" /></legend>
			<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
				<tr>
					<td><liferay-ui:message key="fc-tipo" /> </td> 
						<td><select name="<portlet:namespace />fc_tipoBusq" id="<portlet:namespace />fc_tipoBusq">
								<option value="<%=WebKeysGlobal.COMPROBANTE_FACTURA%>" <%if(filtro.getTipo()!=null && filtro.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)){ %> selected="selected" <%} %> ><%=WebKeysGlobal.COMPROBANTE_FACTURA %></option>
								<option value="<%=WebKeysGlobal.COMPROBANTE_NOTA_CREDITO%>" <%if(filtro.getTipo()!=null && filtro.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO)){ %> selected="selected" <%} %>><%=WebKeysGlobal.COMPROBANTE_NOTA_CREDITO %></option>
                                <option value="<%=WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO%>" <%if(filtro.getTipo()!=null && filtro.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)){ %> selected="selected" <%} %> ><%=WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO %></option>
                                <option value="<%=WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA%>" <%if(filtro.getTipo()!=null && 
                                filtro.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA)){ %> selected="selected" <%} %>><%=WebKeysGlobal.COMPROBANTE_NOTA_CREDITO_ELECTRONICA %></option>    								
                       		</select>  
					</td>
					<td><liferay-ui:message key="fc-letra" />:
					<select name="<portlet:namespace />fc_letraBusq" id="<portlet:namespace />fc_letraBusq">
							<option value="" <%if(filtro.getLetra()==null ){ %> selected="selected" <%} %> >TODAS LAS LETRAS</option>
							<option value="B" <%if(filtro.getLetra()!=null && filtro.getLetra().equalsIgnoreCase("B")){ %> selected="selected" <%} %> >B</option>
							<option value="A" <%if(filtro.getLetra()!=null && filtro.getLetra().equalsIgnoreCase("A")){ %> selected="selected" <%} %>>A</option>
							<option value="T" <%if(filtro.getLetra()!=null && filtro.getLetra().equalsIgnoreCase("T")){ %> selected="selected" <%} %>>T</option>
						</select>  
					</td>
					<%-- <td><liferay-ui:message key="fc-suc" />: 
					<td><select name="<portlet:namespace />fc_sucursalBusq" id="<portlet:namespace />fc_sucursalBusq">
						<!--  
							<option value="" <%if(filtro.getSucursal()==null){ %> selected="selected" <%} %>>TODAS LAS SUCURSALES</option>
							<option value="000" <%if(filtro.getSucursal()!=null && filtro.getSucursal().equalsIgnoreCase("00010")){ %> selected="selected" <%} %>>LOS DIQUES</option>
							<option value="001" <%if(filtro.getSucursal()!=null && filtro.getSucursal().equalsIgnoreCase("00020")){ %> selected="selected" <%} %>>EVA PERON</option>
							<option value="002" <%if(filtro.getSucursal()!=null && filtro.getSucursal().equalsIgnoreCase("00030")){ %> selected="selected" <%} %>>MAR DEL PLATA</option>
						-->	
							<%for(int i=0; i<WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES.length;i++){ %>
								<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(filtro.getSucursal()!=null && filtro.getSucursal().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
							<%} %>
						</select>
					</td>	 --%>
					<td><select name="<portlet:namespace />fc_sucursalBusq" id="<portlet:namespace />fc_sucursalBusq">
								<!-- 
								<%if(filtro.getSucursal()!=null && filtro.getSucursal().equalsIgnoreCase("00010")){ %> selected="selected" <%} %>>EVA PERON</option>
								<%if(filtro.getSucursal()!=null && filtro.getSucursal().equalsIgnoreCase("00020")){ %> selected="selected" <%} %>>LOS DIQUES</option>
								<%if(filtro.getSucursal()!=null && filtro.getSucursal().equalsIgnoreCase("00030")){ %> selected="selected" <%} %>>MAR DEL PLATA</option>
								 -->
								<%for(int i=0; i<WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES.length;i++){ %>
									<%if(ptoVtaAfip.equalsIgnoreCase("9999")){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(filtro.getSucursal()!=null && filtro.getSucursal().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%}else if(ptoVtaAfip.equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(filtro.getSucursal()!=null && filtro.getSucursal().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%} %>
								<%} %>
							</select></td>
							
					<td><liferay-ui:message key="fc-nro" />: </td>
					<td><input type="text" name="<portlet:namespace />fc_numeroBusq" id="<portlet:namespace />fc_numeroBusq" value="<%=filtro.getNumero()!=null?filtro.getNumero():"" %>">  </td> 
				</tr>
				<tr>	
					<td colspan="3">
						<label><liferay-ui:message key="fecha-emision" /> Desde:</label>
								<liferay-ui:input-date
									dayParam="fechaEmisionDiaDsdBusq"
									dayValue="<%= emisionFecha.get(Calendar.DATE)%>"
									monthParam="fechaEmisionMesDsdBusq"
									monthValue="<%= emisionFecha.get(Calendar.MONTH) %>"
									yearParam="fechaEmisionAnioDsdBusq"
									yearValue="<%= emisionFecha.get(Calendar.YEAR) %>"
									yearRangeStart="<%= emisionFecha.get(Calendar.YEAR)-10 %>"
									yearRangeEnd="<%= emisionFecha.get(Calendar.YEAR)  %>"
									firstDayOfWeek="<%= emisionFecha.getFirstDayOfWeek()%>"
									/> 
					</td>	
					<td colspan="3">
						<label><liferay-ui:message key="fecha-emision" /> Hasta:</label>
								<liferay-ui:input-date
									dayParam="fechaEmisionDiaHtaBusq"
									dayValue="<%= emisionFecha.get(Calendar.DATE)%>"
									monthParam="fechaEmisionMesHtaBusq"
									monthValue="<%= emisionFecha.get(Calendar.MONTH) %>"
									yearParam="fechaEmisionAnioHtaBusq"
									yearValue="<%= emisionFecha.get(Calendar.YEAR) %>"
									yearRangeStart="<%= emisionFecha.get(Calendar.YEAR)-10 %>"
									yearRangeEnd="<%= emisionFecha.get(Calendar.YEAR) %>"
									firstDayOfWeek="<%= emisionFecha.getFirstDayOfWeek()%>"
									/> 
					</td>			
				</tr>
				<tr>
					<td colspan="7">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="2">							
						<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" 
						title="<liferay-ui:message key="buscar" />" type="button" 
						onclick="<portlet:namespace />buscarFacturas();"/>							
					</td>
					<td colspan="2">
						<input type="button" value="<liferay-ui:message key="alta-factura" />" onClick="<portlet:namespace />altaFactura();" />
					</td>	
					
					<td colspan="2">
						<input type="button" value="<liferay-ui:message key="obtener-excel" />" onClick="<portlet:namespace />reporteFactura();" />
					</td>
					<% if(user.getScreenName().equalsIgnoreCase("uoma")){ %>
					  <td>
	                   <input  value="Test AFIP"                   
		               onClick="javascript: <portlet:namespace />testAFIP();"
		               type="button"  />
		              </td> 
                   <%}%>
						
				</tr>
				<tr>
					<td colspan="6">&nbsp;</td>
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
		<div align="center" id="<portlet:namespace />busquedaFacturasDiv">
		</div>	
	</fieldset>

	
			
<script type="text/javascript">

	jQuery('#<portlet:namespace />buscando').hide();	
	
	function <portlet:namespace />buscarFacturas(){

		var diaDesde=jQuery('#<portlet:namespace />fechaEmisionDiaDsdBusq').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaEmisionMesDsdBusq').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaEmisionAnioDsdBusq').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		
	    var diaHasta=jQuery('#<portlet:namespace />fechaEmisionDiaHtaBusq').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaEmisionMesHtaBusq').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaEmisionAnioHtaBusq').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;
		
	    var tipo=jQuery('#<portlet:namespace />fc_tipoBusq').val();
	    var letra=jQuery('#<portlet:namespace />fc_letraBusq').val();
	    var sucursal=jQuery('#<portlet:namespace />fc_sucursalBusq').val();
	    var numero=jQuery('#<portlet:namespace />fc_numeroBusq').val();
	    /* var total_reg = jQuery('#<portlet:namespace />total_registros').val(); */
		var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val();
		/* var viene_de = jQuery('#<portlet:namespace />viene_de').val(); */
		
		jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_editar';
		var params = {"fechaDesde" : fechaDesdeFinal,
					  "fechaHasta" : fechaHastaFinal,
					  "tipo"      : tipo,
					  "letra"      : letra,
					  "sucursal"   : sucursal,
					  "numero"     : numero,
					  "cmd"        : "search",
					  "pagina"     : offset_reg } ;
		
		jQuery('#<portlet:namespace />busquedaFacturasDiv').load(url, params, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  } );	
	}
	
	function <portlet:namespace />altaFactura() {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.ADD %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_editar';				
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function <portlet:namespace />reporteFactura(){
		
		var diaDesde=jQuery('#<portlet:namespace />fechaEmisionDiaDsdBusq').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaEmisionMesDsdBusq').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaEmisionAnioDsdBusq').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		
	    var diaHasta=jQuery('#<portlet:namespace />fechaEmisionDiaHtaBusq').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaEmisionMesHtaBusq').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaEmisionAnioHtaBusq').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;
		
	    var tipo=jQuery('#<portlet:namespace />fc_tipoBusq').val();
	    var letra=jQuery('#<portlet:namespace />fc_letraBusq').val();
	    var sucursal=jQuery('#<portlet:namespace />fc_sucursalBusq').val();
	    var numero=jQuery('#<portlet:namespace />fc_numeroBusq').val();
		
		window.location.href ='/xlsservlet/?reporte=REPORTE_FACTURAS'
			+ "&fechaDesde=" + fechaDesdeFinal
			+  "&fechaHasta=" + fechaHastaFinal
			+  "&tipo="+ tipo
			+  "&letra="+ letra
			+  "&sucursal=" +sucursal
			+  "&numero=" + numero;
	}
	
	function <portlet:namespace />testAFIP(){
		
		var sucursal=jQuery('#<portlet:namespace />fc_sucursalBusq').val();
		var tipo=jQuery('#<portlet:namespace />fc_tipoBusq').val();
	    var letra=jQuery('#<portlet:namespace />fc_letraBusq').val();
		
        jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_editar';
		var params = { "sucursal"   : sucursal, "cmd"        : "testws",
				 "tipo"      : tipo,
				  "letra"      : letra} ;
		
		jQuery('#<portlet:namespace />busquedaFacturasDiv').load(url, params, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  } );
				
	}

	
</script>
