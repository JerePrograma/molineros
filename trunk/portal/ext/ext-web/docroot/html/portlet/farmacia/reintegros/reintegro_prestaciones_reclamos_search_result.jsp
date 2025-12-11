<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.PrestacionesReclamo" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

<portlet:defineObjects/>

<%
PortletURL portletURL = renderResponse.createRenderURL();

String cmd = (String) request.getAttribute(Constants.CMD);

boolean inHabilitar = false; 
NumberFormat format2D = new DecimalFormat("#0.00");

if (cmd != null && ( cmd.equalsIgnoreCase(Constants.VIEW) ||  cmd.equalsIgnoreCase(Constants.EDIT ) )  ){
	inHabilitar= true;
}

List<PrestacionesReclamo> prestacionesDelReclamo = null;

prestacionesDelReclamo= (ArrayList<PrestacionesReclamo>)renderRequest.getAttribute(WebKeysLiquidaciones.REINTEGRO_PRESTACIONES_RECLAMOS);

boolean entrar =true;

if(null==prestacionesDelReclamo){					
  entrar=false;
}

List<String> headerNames = new ArrayList<String>();
headerNames.add("Id Reclamo");
headerNames.add("Estado");
headerNames.add("Codigo");
headerNames.add("Prestacion");
headerNames.add("Frecuencia");
headerNames.add("Comprobante");
headerNames.add("CUIT");
headerNames.add("Total Comprobante");
headerNames.add("Cantidad");
headerNames.add("Importe");
headerNames.add("Total");
headerNames.add("Cargo OSPIM");
headerNames.add("Cargo PS");
headerNames.add("Cargo Monotributo");
headerNames.add("Observacion");
	
if (!inHabilitar){
	String vereditarborrar = "Pasar Datos";
	headerNames.add(vereditarborrar);
}else{
	headerNames.add("");
}

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-prestaciones-were-found"));


if (prestacionesDelReclamo != null && prestacionesDelReclamo.size()>0){
	int total = prestacionesDelReclamo.size();
	String opcionesCombo="" ; 
	String enabledestado="";
	String captionEstadoAutorizadoRechazado="" ; 
	opcionesCombo="<option value='0'>CARGADO</option><option value='1'>AUTORIZADO</option><option value='2'>RECHAZADO</option>"; 
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	%><script type="text/javascript"> jQuery('#<portlet:namespace />cantprestacioneslista').val('<%=prestacionesDelReclamo.size()%>');</script><%
	
 	for (int i = 0; i < prestacionesDelReclamo.size(); i++) {	    
 		
 		PrestacionesReclamo presreclamo  = (PrestacionesReclamo) prestacionesDelReclamo.get(i);
	
	 	ResultRow row = new ResultRow(presreclamo,new Integer(1+i), i);
	 	// checkbosx de autorizados 
		StringBuilder sb1= new StringBuilder();									
		
		row.addText(String.valueOf(  presreclamo.getIdreclamoprestacional()) );
		
		enabledestado="";
 		if (presreclamo.getEstadoRechazoAprobado()>1 || inHabilitar ){ enabledestado = "disabled='disabled'"; }
 		
		sb1.append("<select " + enabledestado);
		sb1.append("name='comboestadosreclamo" + String.valueOf(presreclamo.getIdRegistro()) +"'" );
		sb1.append("id='comboestadosreclamo" + String.valueOf(presreclamo.getIdRegistro()) +"'" );
		sb1.append("onChange='CambioEstado(" + String.valueOf(presreclamo.getIdRegistro()) +");'>");
		
		if (presreclamo.getEstadoRechazoAprobado()==1){	sb1.append("<option value='1'>AUTORIZADO</option>");captionEstadoAutorizadoRechazado="A U T O R I Z A D A"; 		}
		if (presreclamo.getEstadoRechazoAprobado()==2){	sb1.append("<option value='2'>RECHAZADO</option>"); captionEstadoAutorizadoRechazado="R E C H A Z A D A";		}
		if (presreclamo.getEstadoRechazoAprobado()<1){ 	sb1.append(opcionesCombo); }
		
		sb1.append("</select>");
		
		row.addText(sb1.toString());
	 	
	 	if( Integer.valueOf(presreclamo.getIdregistroString())  <0) {
	 		row.addText("");
	 	}else{
	 		row.addText(String.valueOf(presreclamo.getCodigoPrestacion())); //  .getIdregistroString());	
	 	}		 		 	 	 		 
	 	
	 	row.addText(presreclamo.getDescripcion());
	 	
	 	row.addText(presreclamo.getFrecuencia());
	 	
	 	row.addText(presreclamo.getComprobanteTipo()+ " " + presreclamo.getComprobanteSucursal()+ "-" + presreclamo.getComprobanteNro());
	 	row.addText(presreclamo.getComprobanteCUIT()+"/"+presreclamo.getComprobanteSucursal());
	 	
	 	row.addText(format2D.format(presreclamo.getComprobanteTotal()));
		row.addText(presreclamo.getCantidadString() );
		row.addText(format2D.format(presreclamo.getImporte()));
		row.addText(format2D.format(presreclamo.getImporte()  * presreclamo.getCantidad()   ));		
	 	row.addText(presreclamo.getCargo_ospimString()  );
	 	row.addText(presreclamo.getCargo_psString() );
	 	row.addText(presreclamo.getCargo_imesaString());
	 	
	 	if  ((presreclamo.getObservaciones() != null) &&  (presreclamo.getObservaciones().length()>15)){
		 	StringBuilder sbo=new StringBuilder();
		 		    sbo.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"edita prestacion\" src=\"");
		 			sbo.append(themeDisplay.getPathThemeImages());
		 			sbo.append("/common/conversation.png\"  title='" + presreclamo.getObservaciones() +"'");
		 			sbo.append(" onClick=\"javascript:VtnaObs('");
			 		sbo.append(String.valueOf(presreclamo.getObservaciones()  ));
				 	sbo.append("','Observacion de la Prestacion');\" />");
		 			
				row.addText(sbo.toString());
	 	}else{
	 	   row.addText( Validator.isNotNull( presreclamo.getObservaciones() ) ? presreclamo.getObservaciones()  : "" );
	 	}
	 	
  		StringBuilder sb=new StringBuilder(); 
  	  			
  		            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"autorizacion prestacion\" src=\"");
		 			sb.append(themeDisplay.getPathThemeImages());
		 			sb.append("/common/submit.png\"   onClick=\"javascript:pasadatosaformulario('");
			 		sb.append(String.valueOf(presreclamo.getCodigoPrestacion()));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getCantidad()   ));			 		
			 		sb.append("','");
			 		sb.append(format2D.format(presreclamo.getImporte() * presreclamo.getCantidad()   ));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getImporteString()));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getIdreclamoprestacional()  ));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getIdprestacionReclamo()    ));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getDescripcion()   ));
			 		sb.append("','");
			 		sb.append(format2D.format(presreclamo.getCargo_ospim()));
			 		sb.append("','");
			 		sb.append(format2D.format(presreclamo.getCargo_ps()));
			 		sb.append("','");
			 		sb.append(format2D.format(presreclamo.getCargo_imesa()));
			 		sb.append("','");
			 		sb.append(presreclamo.getCbu()!=null?String.valueOf(presreclamo.getCbu()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getCuilCuenta()!=null?String.valueOf(presreclamo.getCuilCuenta()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getEmailCuenta()!=null?String.valueOf(presreclamo.getEmailCuenta()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getApellidoCuenta()!=null?String.valueOf(presreclamo.getApellidoCuenta()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getNombreCuenta()!=null?String.valueOf(presreclamo.getNombreCuenta()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteCUIT()!=null?String.valueOf(presreclamo.getComprobanteCUIT()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteCUITSucursal()!=null?String.valueOf(presreclamo.getComprobanteCUITSucursal()):""); 		
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteTipo()!=null?String.valueOf(presreclamo.getComprobanteTipo()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteSucursal()!=null?String.valueOf(presreclamo.getComprobanteSucursal()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteLetra()!=null?String.valueOf(presreclamo.getComprobanteLetra()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteNro()!=null?String.valueOf(presreclamo.getComprobanteNro()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteImporte()!=null?String.valueOf(presreclamo.getComprobanteImporte()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteFecha()!=null?String.valueOf(presreclamo.getComprobanteFechaDia()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteFecha()!=null?String.valueOf(presreclamo.getComprobanteFechaMes()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteFecha()!=null?String.valueOf(presreclamo.getComprobanteFechaAnno()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getFechaPrestacion()!=null?String.valueOf(presreclamo.getFechaPrestacionDia()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getFechaPrestacion()!=null?String.valueOf(presreclamo.getFechaPrestacionMes()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getFechaPrestacion()!=null?String.valueOf(presreclamo.getFechaPrestacionAnno()):"");
			 		
				 	sb.append("');\" />");	
		
 		row.addText(sb.toString()); 
		resultRows.add(row);		 
 	}
} 

%>
<script>
function pasadatosaformulario(codigo,cantidad,total,importe,idreclamo, idprestacionreclamo,prestacion,
		cargo_ospim,cargo_ps,cargo_imesa, cbu, cuilCuenta, emailCuenta,apellidoCuenta, nombreCuenta, cuitEntidad,
		sucursalEntidad, comproaDebitarTipo , comprobanteSuc, comprobanteLetra, comprobanteNro, importeCompro,
		comprobanteFechaDia, comprobanteFechaMes, comprobanteFechaAnno, fechaPrestacionDia, fechaPrestacionMes, fechaPrestacionAnno){
	    jQuery('#<portlet:namespace />troquel').val(codigo);
	   
		<portlet:namespace />oculta_prestaciones_reclamos();
		jQuery("#<portlet:namespace />div_boton_cancelar_reclamos_prestaciones").show();
		
      // guardo el original para validar que no supere este valor  
 		jQuery('#<portlet:namespace />importeoriginalreclamo').val(total.replace(",","."));
      
		jQuery('#<portlet:namespace />id_reclamo_prestacional').val(idreclamo);
		jQuery('#<portlet:namespace />id_prestacion_reclamo_prestacional').val(idprestacionreclamo);

		// desahilita  controles de importes de la pretacion 
		<portlet:namespace />desactivaControlesPrestacionDesdeReclamo(true);
		
		jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").hide();
		
		// desahilita el control de busqueda de afiliados 
		<portlet:namespace />habilitaControlBusquedaAfiliado(true); 
		
		jQuery('#<portlet:namespace />importereclamo').val(importe.replace(",","."));
		
		jQuery('#<portlet:namespace />monto_cober_prestadora_aux').val(cargo_ps);
		jQuery('#<portlet:namespace />cargo_imesa').val(cargo_imesa);
		

		jQuery('#<portlet:namespace />cbu').val(cbu);
		jQuery('#<portlet:namespace />cuil_cuenta').val(cuilCuenta);

		jQuery('#<portlet:namespace />email_cuenta').val(emailCuenta);
		jQuery('#<portlet:namespace />apellido_cuenta').val(apellidoCuenta);
		jQuery('#<portlet:namespace />nombre_cuenta').val(nombreCuenta);
		
		
		jQuery('#<portlet:namespace />cuit_entidad').val(cuitEntidad);
		jQuery('#<portlet:namespace />sucursal_entidad').val(sucursalEntidad);

		<portlet:namespace />buscarEntidad();
		
		
		jQuery('#<portlet:namespace />comprobante_tipo').val(comproaDebitarTipo);
		jQuery('#<portlet:namespace />comprobante_suc').val(comprobanteSuc);
		jQuery('#<portlet:namespace />comprobante_letra').val(comprobanteLetra);
		jQuery('#<portlet:namespace />comprobante_nro').val(comprobanteNro);
		jQuery('#<portlet:namespace />importeCompro').val(importeCompro * cantidad);	
		
		
		jQuery('#<portlet:namespace />comproFechaDia').val(comprobanteFechaDia);
		jQuery('#<portlet:namespace />comproFechaMes').val(comprobanteFechaMes);
		jQuery('#<portlet:namespace />comproFechaAnio').val(comprobanteFechaAnno);
		
		
		jQuery('#<portlet:namespace />fechaPrestacionDia').val(fechaPrestacionDia);
		jQuery('#<portlet:namespace />fechaPrestacionMes').val(fechaPrestacionMes);
		jQuery('#<portlet:namespace />fechaPrestacionAnio').val(fechaPrestacionAnno);
		
		// busca prestacion asignada
		
	 //<portlet:namespace />buscarMedicamentoFarmacia(); 
		
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_medicamento_json&nroTroquel='+codigo;
	 
	 jQuery.ajax({   
		url: url,
		async:false,
		success: function(data) {
			var obj = jQuery.parseJSON(data);
//			jQuery('#<portlet:namespace />id_medicamento').val(id_medicamento);
			jQuery('#<portlet:namespace />nombre_med').val(obj.nombreMedicacion);
			jQuery('#<portlet:namespace />presentacion').val(obj.presentacion);
			jQuery('#<portlet:namespace />laboratorio').val(obj.laboratorio);
			jQuery('#<portlet:namespace />codBarras').val(obj.codBarras);
			jQuery('#<portlet:namespace />id_medicamento').val(obj.idMedicamento);
		}});
		
		
		//jQuery('#<portlet:namespace />porcentaje').val()='100';
		// carga valores orioginales desde el reclamo seleccionado 
		
		//jQuery('#<portlet:namespace />cantidad').val(cantidad);
		
		//jQuery('#<portlet:namespace />monto_cober_ospim').val(importe.replace(",","."));
		//jQuery('#<portlet:namespace />monto_cober_ospim').val(importe.replace(",","."));		
		//jQuery('#<portlet:namespace />precio').val(importe.replace(",","."));
		//jQuery('#<portlet:namespace />total_cob').val(total.replace(",","."));
		/*
		jQuery('#<portlet:namespace />total_cob').val("2");
		jQuery("#<portlet:namespace />total_med").val("3");
		jQuery('#<portlet:namespace />porcentaje').val("100");
		jQuery('#<portlet:namespace />precio_ospim').val("4");
		*/
		//jQuery('#<portlet:namespace />monto_cober_ospim').val(importe.replace(",","."));
		
	    
		jQuery('#<portlet:namespace />cantidad').val(cantidad);
		
		jQuery('#<portlet:namespace />porcentaje').val('100');
		jQuery("#<portlet:namespace />porcentaje").attr("readonly", true);
		jQuery('#<portlet:namespace />porc_anterior').val('100'); 
		
		var cospim = cargo_ospim.replace(",",".");
		var cprestadora=cargo_ps.replace(",",".");
		var cimesa=cargo_imesa.replace(",",".");
		
		cospim=cospim;///cantidad;
		cprestadora=cprestadora;//cantidad;

		//alert('cprestadora   ' + cprestadora);
		
//		jQuery('#<portlet:namespace />monto_cober_ospim').val(importe.replace(",","."));		
		jQuery('#<portlet:namespace />monto_cober_ospim').val(cospim);
		jQuery('#<portlet:namespace />monto_cober_prestadora').val(cprestadora);
		jQuery('#<portlet:namespace />monto_cober_imesa').val(cimesa);
		
		jQuery('#<portlet:namespace />monto_cober_amtima').val(0);
		jQuery('#<portlet:namespace />precio').val(jQuery('#<portlet:namespace />importereclamo').val());
		var x = parseFloat(cantidad) * parseFloat(jQuery('#<portlet:namespace />precio').val());
		
//		var y = (parseFloat(jQuery('#<portlet:namespace />monto_cober_amtima').val())+parseFloat(jQuery('#<portlet:namespace />monto_cober_ospim').val())) * parseFloat(cantidad);
		
		
		var y = (parseFloat(jQuery('#<portlet:namespace />monto_cober_amtima').val())+
				 parseFloat(jQuery('#<portlet:namespace />monto_cober_ospim').val()) +
				 parseFloat(jQuery('#<portlet:namespace />monto_cober_imesa').val()) +
				 parseFloat(jQuery('#<portlet:namespace />monto_cober_prestadora').val()));// * parseFloat(cantidad) ;
		
		jQuery("#<portlet:namespace />total_med").val(Math.round(x * 100)/100);
		jQuery("#<portlet:namespace />total_cob").val(Math.round(y * 100)/100);
		
		jQuery('#<portlet:namespace />monto_cober_ospim').attr('readonly', true);
		jQuery('#<portlet:namespace />monto_cober_prestadora').attr('readonly', true);
		jQuery('#<portlet:namespace />monto_cober_amtima').attr('readonly', true);
		jQuery('#<portlet:namespace />monto_cober_imesa').attr('readonly', true);
		jQuery("#<portlet:namespace />cantidad").attr('readonly', true);
}

</script>



<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	
	
	
	
