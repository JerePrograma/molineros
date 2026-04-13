<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimSinComprobantes" %>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimSinPagos" %>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimTotalPagosMenorQueComprobantesException" %>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimTotalPagosNoAnticipoMenorIgualQueComprobantesException" %>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimCreacionNuevoAnticipoException" %>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimAnticiposNoUsadosException" %>

<%
	String portlet_name=null;
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "liquidaciones";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}
	if(renderResponse.getNamespace().equals("_LIQ_1_")){
		portlet_name = "liquidaciones";
	}
	if(renderResponse.getNamespace().equals("_TES_1_")){
		portlet_name = "tesoreria";
	}

	OrdenPago ordenPago = (OrdenPago) request.getSession().getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);

	String esEdicionStr = (String) request.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EDICION);
	boolean esEdicion = false;
	if (ordenPago == null || ordenPago.getId() == null
			|| ordenPago.getId().equals(0) || esEdicionStr != null) {
		esEdicion = true;
	}
	String cuit = ordenPago != null && ordenPago.getAcreedor() != null ?  ordenPago.getAcreedor().getCuit() : "";
	String sucu = ordenPago != null && ordenPago.getAcreedor() != null ? ordenPago.getAcreedor().getSucursal() : "";
	String razon = ordenPago != null && ordenPago.getAcreedor() != null ? ordenPago.getAcreedor().getRazon_soc() : "";
	
	String from=(String) request.getAttribute(WebKeysLiquidaciones.FROM_REINTEGROS);
	
	if(from==null || from.trim().length()==0){
		from=ordenPago.isFarmacia()?"FROM_REINTEGROS_FARMACIA":null;
	}
	
	boolean mostrarBusquedaComp = (ordenPago == null || ordenPago.getId() == null || ordenPago.getId().intValue() == 0);
	String noMostrar = (String) request.getAttribute(WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES);
	if (noMostrar != null && noMostrar.equals(WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES)){
		mostrarBusquedaComp = false;
	}
	int nro_lote=0;
	if(portlet_name.equals("liquidaciones") && ordenPago!=null && ordenPago.getId()>0){
		nro_lote=ordenPago.getIdLote();
	}else{
		nro_lote=OrdenPagoServiceUtil.getLoteOrdenPago();
	}

	Calendar altaFecha = CalendarFactoryUtil.getCalendar();
	
	if(ordenPago!=null && ordenPago.getAlta_fecha()!=null){
		altaFecha.setTime(ordenPago.getAlta_fecha());
	}
	
	String formaPago = (String) request.getAttribute("formaPagoAfiliado");
	
	Integer proxIDdeOP = (Integer)request.getAttribute("PROXIMOIDORDENPAGO"); 
%>

<liferay-ui:error exception="<%= java.io.IOException.class %>" message="exception-archivo-zip" />
<liferay-ui:error exception="<%=ar.com.ospim.liquidaciones.DuplicateNumeroChequeException.class %>" message="duplicate-cheque" />
<liferay-ui:error exception="<%= ar.com.ospim.liquidaciones.DuplicateNumeroComprobanteException.class %>" message="duplicate-comprobante" />

<liferay-ui:error exception="<%= OrdenPagoOspimSinComprobantes.class %>" message="op-ospim-sin-comprobantes" />
<liferay-ui:error exception="<%= OrdenPagoOspimSinPagos.class %>" message="op-ospim-sin-pagos" />
<liferay-ui:error exception="<%= OrdenPagoOspimTotalPagosMenorQueComprobantesException.class %>" message="op-ospim-pagos-menor-comprobantes" />
<liferay-ui:error exception="<%= OrdenPagoOspimTotalPagosNoAnticipoMenorIgualQueComprobantesException.class %>" message="op-ospim-pagos-no-antic-menor-comprobantes" />
<liferay-ui:error exception="<%= OrdenPagoOspimCreacionNuevoAnticipoException.class %>" message="op-ospim-pagos-nuevo-anticipo" />
<liferay-ui:error exception="<%= OrdenPagoOspimAnticiposNoUsadosException.class %>" message="op-ospim-pagos-anticipos-sin-usar" />


<form action="" method="post" name="<portlet:namespace />fm">
<input name="<portlet:namespace /><%=Constants.CMD%>" type="hidden" value="" />
<!--  <input name="<portlet:namespace />email_cbu"  id="<portlet:namespace />email_cbu" type="hidden" value="" /> -->
<input type="hidden" id="<%=WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES %>" name="<%=WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES %>" value="<%=(String)request.getAttribute(WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES)%>"/>

<fieldset class="block-labels">
<legend>
	<%if(portlet_name.equals("uoma")){%>
		<liferay-ui:message key="alta-orden-pago" />
	<%}else{%>
		<liferay-ui:message key="alta-orden-pago-ospim" />
	<%}%>
</legend>
<table class="lfr-table" width="100%">
	<tr>
		<td><label><liferay-ui:message key="numero" />:</label></td>
		<td><input type="text" <%if( !esEdicion ){%> readonly="readonly" <%} %> 
				 name="orden_pago_id" id="orden_pago_id" size = "15" 
				 value="<%=ordenPago != null 
				 		&& ordenPago.getId() != null 
				 		&& ordenPago.getId()>0? ordenPago.getId().toString(): proxIDdeOP%>"/></td>
		<td><label><liferay-ui:message key="fecha-alta" />:</label></td>
		<td colspan="1">
			<liferay-ui:input-date dayParam="altaFechaDia"
				dayValue="<%= altaFecha.get(Calendar.DATE) %>"
				monthParam="altaFechaMes"
				monthValue="<%= altaFecha.get(Calendar.MONTH) %>"
				yearParam="altaFechaAnio"
				yearValue="<%= altaFecha.get(Calendar.YEAR) %>"
				yearRangeStart="<%= altaFecha.get(Calendar.YEAR) - 20 %>"
				yearRangeEnd="<%= altaFecha.get(Calendar.YEAR) + 20 %>"
				firstDayOfWeek="<%= altaFecha.getFirstDayOfWeek()%>"
				disabled="<%= !esEdicion %>" />
		</td>					
		<td colspan="2" align="right"><b><%if(portlet_name.equals("liquidaciones") && nro_lote>0){%><liferay-ui:message key="lote-actual" />:<input type="text" size="6" id="<portlet:namespace />nro_lote" name="<portlet:namespace />nro_lote"  value="<%=nro_lote%>"/><%}%></b></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="acreedor" />:</label></td>
		<td colspan="5">
			<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
		  		<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion && mostrarBusquedaComp) %>'/>
		  		<liferay-util:param name="cuit" value='<%= ordenPago != null && ordenPago.getAcreedor() != null ? ordenPago.getAcreedor().getCuit() : new String("")%>'/>
		  		<liferay-util:param name="sucu" value='<%=ordenPago != null && ordenPago.getAcreedor() != null ? ordenPago.getAcreedor().getSucursal() : new String("") %>'/>
		  		<liferay-util:param name="razon" value='<%=ordenPago != null && ordenPago.getAcreedor() != null ? ordenPago.getAcreedor().getRazon_soc() : new String("") %>'/>
		  		<liferay-util:param name="id_seccional" value='<%=ordenPago != null && ordenPago.getSeccional() != null ? String.valueOf(ordenPago.getSeccional().getId()) : new String("") %>'/>
		  		<liferay-util:param name="buscar_destino" value='<%= ordenPago!=null&&ordenPago.getAcreedor()!=null&&(null==ordenPago.getAcreedor().getRazon_soc()||"null".equals(ordenPago.getAcreedor().getRazon_soc().trim()))?"false":"true"%>'/>
			</liferay-util:include>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
	   <td>Email informado:</td>
	   <td><input name="<portlet:namespace />email_cbu"  id="<portlet:namespace />email_cbu" type="text" value="" size = "50" readonly="readonly" />
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	
	<% if (ordenPago  != null
							&& ordenPago.getReintegrosList()!=null && !ordenPago.getReintegrosList().isEmpty()) {
			%>
	<tr>
		<td><label><liferay-ui:message key="numero-lista" />:</label></td>
		<td>		
		<%=String.valueOf(ordenPago.getReintegrosList()) %>	
		</td>
		<td colspan="5" align="left">
			<input type="submit" value="<liferay-ui:message key="reintegros-asoc"/>" onClick="<portlet:namespace />verReintAsociados();return false;"/>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<%
		}
	%>
			
	<tr>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="5">
			<textarea rows="5" cols="80" <%if (!esEdicion) {%> disabled="disabled"  <%}%> id="<portlet:namespace />obs" name="<portlet:namespace />obs" ><%=ordenPago != null && ordenPago.getObservaciones() != null ? ordenPago.getObservaciones()  : ""%></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6">
			<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/destinatario_obs_interna.jsp">
				<liferay-util:param name="esEditable" value="<%=String.valueOf(mostrarBusquedaComp)%>" />
				<liferay-util:param name="destino" value="<%=ordenPago.getDestino()%>" />
				<liferay-util:param name="obs_interna" value="<%=ordenPago.getObsInterna()%>" />
			</liferay-util:include>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>	
	<tr>
		<td colspan="6" align="center"><input type="button" value="<liferay-ui:message key="modificar-datos-empresa" />" onClick="javascript:verInfoEmpresa();"/> </td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6">
			<liferay-util:include page="/html/portlet/utils/comprobantes/busqueda_comprobantes.jsp">
				<liferay-util:param name="esEditable" value="<%=String.valueOf(mostrarBusquedaComp)%>" />
			</liferay-util:include>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
		<tr>
	<td colspan="6">
	<fieldset>
		<legend>
				<label><liferay-ui:message key="anticipos" />:</label>
		</legend>
			<table width="100%">
				<tr>
					<td width="100%">
						<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/anticipos_agregar.jsp">
							<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
						</liferay-util:include>
					</td>
				</tr>
			</table>
	</fieldset>
	</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"><label><liferay-ui:message key="importe-a-pagar" />:</label>&nbsp;&nbsp;&nbsp;<input type="text" disabled="disabled" id="total_pagar" value="0"/></td>
		<td colspan="5">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
	<td colspan="6">	
		<fieldset>
			<legend>
					<label><liferay-ui:message key="formas-de-pago" />:</label>
			</legend>
				<table width="100%">
					<tr>
						<td width="100%">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/formas_pago_agregar.jsp">
								<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>							
							</liferay-util:include>
						</td>
					</tr>
				</table>
		</fieldset>

	</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6" align="left">
		<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveOP();return false;"/>&nbsp;&nbsp;
	
	<%
		if (ordenPago != null && ordenPago.getId() != null
				&& ordenPago.getId().intValue() != 0) {
	%>
			<input type="submit" value="<liferay-ui:message key="print" />" onClick="<portlet:namespace />imprimirOP();return false;"/>
			
			
	<%
		}
	%>
	<%
		if (ordenPago != null && ordenPago.isTieneRetencion()) {
	%>
			<input type="submit" value="<liferay-ui:message key="imprimir-comprobante-retencion" />" onClick="<portlet:namespace />imprimirRetencionGanancias();return false;"/>
	<%
		}
	%>
	<%
		if (ordenPago != null && null!=ordenPago.getLiquidacionesList() && ordenPago.getLiquidacionesList().size()>0) {
	%>
			<input type="submit" value="<liferay-ui:message key="imprimir-nota-debito" />" onClick="<portlet:namespace />imprimirND();return false;"/>
	<%
		}
	%>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	</table>
	
	<input type="hidden" name="<%=WebKeysLiquidaciones.FROM_REINTEGROS%>" value="<%=from%>" />
	
	<%
	if (request.getAttribute(WebKeysLiquidaciones.FROM_LIQUIDACION) != null
			&& request.getAttribute(
					WebKeysLiquidaciones.FROM_LIQUIDACION).equals(
					WebKeysLiquidaciones.FROM_LIQUIDACION)) {
	%>
		<input type="hidden" name="<%=WebKeysLiquidaciones.FROM_LIQUIDACION%>" value="<%=WebKeysLiquidaciones.FROM_LIQUIDACION%>" />
	<%
		}
	%>
	
</form>

	<div align="center" id="<portlet:namespace />busquedaChequeDiv">						
			</div>

<script type="text/javascript">
       function <portlet:namespace />saveOP() {
 	      var destino=jQuery('#<portlet:namespace />destino').val();				
	      if((destino.trim().length==0 || destino=='Ingrese Destino...' || destino=='undefined') && !confirm("No se ha cargado un destino para la OP, desea continuar?")){
		       return false;
	      }
	      var importe=jQuery('#<portlet:namespace />importe').val();
	      document.<portlet:namespace />fm.<portlet:namespace /><%=Constants.CMD%>.value = "<%=ordenPago == null || ordenPago.getId() == null
		      || ordenPago.getId().equals(0) ? Constants.ADD
		      : Constants.UPDATE%>";
		      
		  url = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'/>&struts_action=/<%=portlet_name%>/editar_orden_pago_ospim_entry";
	      submitForm(document.<portlet:namespace />fm, url);				
	      return true;
       }
		
		function <portlet:namespace />imprimirOP(){
			<%if(ordenPago.isFarmacia()){%>
				window.location.href ="/pdfservlet/?accion=ordenPagoOspimFarmacia&id_ini=<%=ordenPago != null && ordenPago.getId() != null ? ordenPago
							.getId().toString()
							: ""%>&id_fin=<%=ordenPago != null && ordenPago.getId() != null ? ordenPago
							.getId().toString()
							: ""%>";
			<%}else{%>
				window.location.href ="/pdfservlet/?accion=ordenPagoOspim&id_ini=<%=ordenPago != null && ordenPago.getId() != null ? ordenPago
							.getId().toString()
							: ""%>";
			<%}%>		
		}

		function <portlet:namespace />imprimirCheque(){
			window.location.href ="/odtservlet/?accion=cheque&numero_op=<%=ordenPago != null && ordenPago.getId() != null  ? ordenPago.getId() : ""%>";
		}
		
		function <portlet:namespace />imprimirRetencionGanancias(){			
				window.location.href ="/pdfservlet/?accion=comproRetenGanancias&id_ini=<%=ordenPago != null && ordenPago.getId() != null ? ordenPago
							.getId().toString()
							: ""%>&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>";							
		}

		function sugerirEnComprobante(){
			var cuit=jQuery('#<portlet:namespace />cuilcuit').val();
			jQuery('#<portlet:namespace />cuit_compr').val(cuit);
		}

		function <portlet:namespace />verReintAsociados(){
			var from ='<%=from%>';
			var url ;
			var inputs= '<%=ordenPago != null &&  ordenPago.getReintegrosList() != null && !ordenPago.getReintegrosList().isEmpty() ? ordenPago.getReintegrosList().toString() : new String("")%>';
			
			url = '/xlsservlet/?reporte=OP_REINTEGRO_FARMACIA_PRESTA&idLista=inputs' 
			url = url.replace("inputs", encodeURI(inputs));
			
			window.location.href =url;			
		}

		function sugerirNumero(){			
			buscarAnticipos();
			recalcularTotales()			
		}

		function cambiaCuit(){			
		}
		
		function buscarAnticipos(){		
			
		
			var cuitEntidad = jQuery("#<portlet:namespace />cuit_entidad").val();
			var sucuEntidad = jQuery("#<portlet:namespace />sucursal_entidad").val();
			var idSeccional = jQuery("#<portlet:namespace />id_seccional").val();			
			jQuery('#<portlet:namespace />agregandoAnticipo').show();
//			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/agregar_op_pago';

            var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_op_pago';
            
			url += '&cuit_entidad=' + cuitEntidad;
			url += '&sucu_entidad=' + sucuEntidad;
			url += '&id_seccional=' + idSeccional;
			url += '&esEdicion=<%=esEdicion%>';
			url += '&buscar_anticipos=buscar_anticipos';
			url += '&rnd=' + Math.floor(Math.random()*100);			
			jQuery('#<portlet:namespace />anticipos').load(url, function() {														
														jQuery('#<portlet:namespace />agregandoAnticipo').hide();														
														 jQuery('#<portlet:namespace />nro_anticipo').val("");														
														 recalcularTotales();
										   }
			 );	

			 
			
		}

		function sugerirRazonSocialChequeYDestino(){			
			<% if (request.getAttribute("cheque_a_favor_de") != null && !request.getAttribute("cheque_a_favor_de").equals("")){ %>
			jQuery("#<portlet:namespace />a_favor_de").val("<%=request.getAttribute("cheque_a_favor_de") %>");
			<%} else { %>			
			var cuitEntidad = jQuery("#<portlet:namespace />cuit_entidad").val();
			var sucuEntidad = jQuery("#<portlet:namespace />sucursal_entidad").val();
			var idSeccional = jQuery("#<portlet:namespace />id_seccional").val();			
//			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_ultima_razon_social_cheque_op';
	        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_ultima_razon_social_cheque_op';
	        	url += '&cuit_entidad=' + cuitEntidad;
				url += '&sucu_entidad=' + sucuEntidad;
				url += '&id_seccional=' + idSeccional;
				url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery.ajax({   
				url: url,
				success: function(data){
					data=data.replace(/\n/g,"\\n");					
					var obj = jQuery.parseJSON(data);					
					jQuery("#<portlet:namespace />a_favor_de").val(obj.razon);
					if(trim(jQuery("#<portlet:namespace />destino").val())=='' || trim(jQuery("#<portlet:namespace />destino").val())=="Ingrese Destino..." ){
						jQuery("#<portlet:namespace />destino").val(obj.destino);					
					}
					jQuery("#<portlet:namespace />cbu_sugerido").val(obj.cbu);
					jQuery("#<portlet:namespace />entidad").val(obj.nombre);	
										
					if(obj.cbu!='' && obj.cbu!='null'){						
						jQuery("#<portlet:namespace />tipo_pago").val("<%=PagoBancario.class.getName() + PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA %>");
						changeTipo();
					}
					jQuery("#<portlet:namespace />email_cbu").val(obj.email);
					
				}
			});
			<%}%>
		}

		<% if (request.getAttribute("nuevoAnticipo") != null && !(((String)request.getAttribute("nuevoAnticipo")).trim().equals(""))){%>
			var  pesosOriginal = "<%=((BigDecimal)request.getAttribute("valorAnticipoOriginal")).toString()%>";
			var pesosNuevo = "<%=((BigDecimal)request.getAttribute("valorAnticipoNuevo")).toString()%>";
			alert("Esta cancelando parcialmente el Anticipo por $" + pesosOriginal +" , se generará un nuevo Anticipo por el saldo pendiente ($"+ pesosNuevo+").");
		<%}%>



		<% if (cuit != null && !cuit.trim().equals("") && sucu != null && !sucu.trim().equals("") && (razon == null || razon.trim().equals(""))) { %>
		  <portlet:namespace />buscarEntidad();
		<%}%>
		
		
		function recalcularTotales(){
			var totalConceptos = jQuery("#total_conceptos").val();
			var totalPagos = jQuery("#total_formas_pago").val();
			var totalAnticipos = jQuery("#total_anticipos").val();
			jQuery("#<portlet:namespace />importe_pago").val(
					Math.round(
						(Math.round(parseFloat(totalConceptos)*100)/100 - 
						Math.round(parseFloat(totalAnticipos)*100)/100 - 
						Math.round(parseFloat(totalPagos)*100)/100)
					     *100)/100);
			jQuery("#total_pagar").val(
					Math.round(
								(Math.round(parseFloat(totalConceptos)*100)/100 - 
							     Math.round(parseFloat(totalAnticipos)*100)/100)
							     *100)/100);
		}

		function utilizarObservaciones(){
			var obs = jQuery("#obs_comprobantes").val();
			jQuery("#<portlet:namespace />obs").val(obs);
		}
		function actualizarValorAnticipos(anticipo){	
			var importe=anticipo.value;
			var anticipo=anticipo.id;				
			var cuitEntidad = jQuery("#<portlet:namespace />cuit_entidad").val();
			var sucuEntidad = jQuery("#<portlet:namespace />sucursal_entidad").val();
			var idSeccional = jQuery("#<portlet:namespace />id_seccional").val();
			
			jQuery('#<portlet:namespace />agregandoAnticipo').show();	

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_op_pago';
			url += '&cuit_entidad=' + cuitEntidad;
			url += '&sucu_entidad=' + sucuEntidad;
			url += '&id_seccional=' + idSeccional;
			url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'
			url += '&buscar_anticipos=modificar_anticipo';
			url += '&anticipo='+anticipo;
			url += '&importeAnticipo='+Math.abs(importe);
			url += '&rnd=' + Math.floor(Math.random()*100);
			
			jQuery('#<portlet:namespace />anticipos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoAnticipo').hide();	
														 jQuery('#<portlet:namespace />nro_anticipo').val("");														 
														 recalcularTotales();									
										   }
			 );				
		}
		var popup;
		function verInfoEmpresa(cuit_empleador, sucu_empleador) {
		    popup = Liferay.Popup({title:"<liferay-ui:message key="ver-info-empresa" />",modal:true,width:1200});
		    var cuitEntidad = jQuery("#<portlet:namespace />cuit_entidad").val();
			var sucuEntidad = jQuery("#<portlet:namespace />sucursal_entidad").val();
			var idSeccional = jQuery("#<portlet:namespace />id_seccional").val();
			if(idSeccional==0 || (cuitEntidad!='30629138567' && cuitEntidad!='30531143856' && cuitEntidad!='30604119568')){
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_empleadores_entry&cuit='+cuitEntidad+'&sucursal='+sucuEntidad;
			}else{				
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_seccionales_entry&cuit='+cuitEntidad+'&id_seccional='+idSeccional;
			}
			jQuery(popup).load(url);
		}		
		
		<%if (null!=ordenPago && null!=ordenPago.getLiquidacionesListAsString()){%>
			function <portlet:namespace />imprimirND(){
				var url = "/pdfservlet/?accion=notaDebitoLiquidacionxOP&id_liquidaciones=";
				url =url + encodeURI('<%=ordenPago.getLiquidacionesListAsString()%>');
				window.location.href =url;
			}
		<%}%>
		
		function submitFormNotSavePOP(solapa){	
			if (<portlet:namespace />validarCampos()) {
				document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "CAMBIO_SOLAPA";
				document.getElementById("cambioSolapa").value="cambioSolapa";
				if(solapa=="datos"){				
					document.getElementById("tabs1").value="datos-fiscales";
				}else{
					document.getElementById("tabs1").value="datos";
				}
				
				var form = jQuery(document.<portlet:namespace />emple);
				var url = '<portlet:actionURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/editar_empleadores_entry';
							
				
				form.ajaxForm(
					{
						url: url,
				    	target: popup,//".ui-dialog-content",//poopup
				        type: "POST",
				        beforeSubmit: function() {			        
				        },
				        success: function() {
				        }
				    }
				);	
							
				form.submit();    
			}
		}
		<% if (request.getAttribute("cheque_a_favor_de") != null && !request.getAttribute("cheque_a_favor_de").equals("")){ %>
			jQuery("#<portlet:namespace />a_favor_de").val("<%=null!=ordenPago&&null!=ordenPago.getAFavorDe()?ordenPago.getAFavorDe():""%>");
		<%}%>
		<% if(ordenPago!=null&&ordenPago.getAcreedor()!=null&&(null==ordenPago.getAcreedor().getRazon_soc()||"null".equals(ordenPago.getAcreedor().getRazon_soc().trim()))){%>
			<%-- alert('Por favor, complete los datos del prestador. <%=ordenPago.getRazonSocial()%>') --%>
			verInfoEmpresa('<%=ordenPago.getAcreedor().getCuit()%>', '<%=ordenPago.getAcreedor().getSucursal()%>'); 
		<%}else if(ordenPago!=null && ordenPago.getAcreedor()!=null && ordenPago.getAcreedor().getCuit()!=null){%>
			sugerirRazonSocialChequeYDestino();
		<%}%>
		
		<% if (ordenPago  != null
				&& ordenPago.getReintegrosList()!=null && !ordenPago.getReintegrosList().isEmpty() && esEdicion) {%>
				buscarAnticipos();
		<%}%>
		
		
	

		recalcularTotales();
				
</script>

