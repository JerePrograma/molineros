<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.PrestacionesReclamo" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

<portlet:defineObjects/>

<%
PortletURL portletURL = renderResponse.createRenderURL();

String cmd = (String) request.getAttribute(Constants.CMD);
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

boolean inHabilitar = false; 

if (cmd != null && ( cmd.equalsIgnoreCase(Constants.VIEW) ||  cmd.equalsIgnoreCase(Constants.EDIT ) )  ){
	inHabilitar= true;
}

List<PrestacionesReclamo> prestacionesDelReclamo = null;

prestacionesDelReclamo= (ArrayList<PrestacionesReclamo>)renderRequest.getAttribute(WebKeysLiquidaciones.REINTEGRO_PRESTACIONES_RECLAMOS);
NumberFormat format2D = new DecimalFormat("#0.00");
boolean entrar =true;

if(null==prestacionesDelReclamo){					
  entrar=false;
}

List<String> headerNames = new ArrayList<String>();
headerNames.add("Id Reclamo");

headerNames.add("Codigo");
headerNames.add("Prestacion");
headerNames.add("Comprobante");
headerNames.add("CUIT");
headerNames.add("Total Comprobante");

/*
headerNames.add("Cantidad");
headerNames.add("Importe");
headerNames.add("Total");
*/
headerNames.add("Cargo OSPIM");
/*
headerNames.add("Cargo PS");
headerNames.add("Observacion");
*/
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
		
		// row.addText(sb1.toString());
	 	
	 	if( Integer.valueOf(presreclamo.getIdregistroString())  <0) {
	 		row.addText("");
	 	}else{
	 		row.addText(String.valueOf(presreclamo.getCodigoPrestacion())); 	
	 	}		 		 	 	 		 		
	 	row.addText(presreclamo.getDescripcion());
	 	
	 	//row.addText(presreclamo.getFrecuencia());
	 	
	 	String cpbte = (presreclamo.getComprobanteTipo()!=null?presreclamo.getComprobanteTipo()+ " ":"")  + 
	 			 (presreclamo.getComprobanteSucursal()!=null?presreclamo.getComprobanteSucursal()+"-":"") +
	 			 (presreclamo.getComprobanteLetra()!=null?presreclamo.getComprobanteLetra()+" ":"") + 
	 	         (presreclamo.getComprobanteNro()!=null?presreclamo.getComprobanteNro()+" ":"") +
	 	         (presreclamo.getComprobanteFecha()!=null?sdf.format(presreclamo.getComprobanteFecha()):"");
	 	
	 	row.addText(cpbte);
	 	row.addText(presreclamo.getComprobanteCUIT());
	 	//row.addText(format2D.format(presreclamo.getComprobanteCantidad()) ); 	
	 	//row.addText(format2D.format(presreclamo.getComprobanteImporte()) ); 	
	 	row.addText(format2D.format(presreclamo.getComprobanteTotal()) );
	 	/*
	 	row.addText(presreclamo.getCantidadString() );
	 	row.addText(format2D.format(presreclamo.getImporte()) ); 	 	
	 	row.addText(format2D.format(presreclamo.getImporte() * presreclamo.getCantidad() )  ); 
		*/
		
	 	row.addText(presreclamo.getCargo_ospimString()  );
	 	//row.addText(presreclamo.getCargo_psString() );
	 	
	 	if  ((presreclamo.getObservaciones() != null) &&  (presreclamo.getObservaciones().length()>15)){
		 	StringBuilder sbo=new StringBuilder();
		 		    sbo.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"edita prestacion\" src=\"");
		 			sbo.append(themeDisplay.getPathThemeImages());
		 			sbo.append("/common/conversation.png\"  title='" + presreclamo.getObservaciones() +"'");
		 			sbo.append(" onClick=\"javascript:VtnaObs('");
			 		sbo.append(String.valueOf(presreclamo.getObservaciones()  ));
				 	sbo.append("','Observacion de la Prestacion');\" />");
		 			
				// row.addText(sbo.toString());
	 	}else{
	 	   // row.addText( Validator.isNotNull( presreclamo.getObservaciones() ) ? presreclamo.getObservaciones()  : "" );
	 	}
	 	
  		StringBuilder sb=new StringBuilder(); 
  	  			
  		            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"autorizacion prestacion\" src=\"");
		 			sb.append(themeDisplay.getPathThemeImages());
		 			sb.append("/common/submit.png\"   onClick=\"javascript:pasadatosaformulario('");
			 		sb.append(String.valueOf(presreclamo.getCodigoPrestacion()));
			 		sb.append("','");
			 		sb.append(format2D.format(presreclamo.getImporte())); 
			 		sb.append("','");			 		
			 		sb.append(String.valueOf(presreclamo.getCantidadString()   ));
			 		sb.append("','");			 		
			 		sb.append(format2D.format(presreclamo.getImporte() * presreclamo.getCantidad() )); 
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getIdreclamoprestacional()  ));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getIdprestacionReclamo()    ));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getDescripcion()   ));
					sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getCargo_ospim()   ));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getCargo_ps()));
			 		
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getCargo_imesa()));
			 		
			 		
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteTipo()!=null?String.valueOf(presreclamo.getComprobanteTipo()):"" ); 
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteNro()!=null?String.valueOf(presreclamo.getComprobanteNro()):"" );
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteFecha()!=null?sdf.format(presreclamo.getComprobanteFecha()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteCantidad()!=null?format2D.format(presreclamo.getComprobanteCantidad()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteImporte()!=null?format2D.format(presreclamo.getComprobanteImporte()):""); 
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteTotal()!=null?format2D.format(presreclamo.getComprobanteTotal()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteCUIT()!=null?String.valueOf(presreclamo.getComprobanteCUIT()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteSucursal()!=null?String.valueOf(presreclamo.getComprobanteSucursal()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteCUITSucursal()!=null?String.valueOf(presreclamo.getComprobanteCUITSucursal()):"");
			 		sb.append("','");
			 		sb.append(presreclamo.getComprobanteLetra()!=null?String.valueOf(presreclamo.getComprobanteLetra()):"");
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
				 	sb.append("');\" />");	
		
 		row.addText(sb.toString()); 
		resultRows.add(row);		 
 	}
} 

// }

%>
<script>
function pasadatosaformulario(codigo,importe,cantidad,total, idreclamo, idprestacionreclamo,prestacion , cargo_ospim ,cargo_ps,cargo_imesa,
		comprobanteTipo,comprobanteNro,comprobanteFecha,comprobanteCantidad,comprobanteImporte,
		comprobanteTotal,comprobanteCuit,comprobanteSucursal,comprobanteCuitSucursal , 
		comprobanteLetra, cbu, cuilCuenta, emailCuenta,apellidoCuenta, nombreCuenta ){
	
	// solo para el formulario de liquidaciones cuando la liquidacion esta cerrada 
	try {
		if ( jQuery('#<portlet:namespace />ajustarLiquidacion').is(":visible")){
			alert('La liquidación se encuentra Cerrada por lo que  no se puede añadir prestaciones de Reclamos del Afiliado.');
			return false;
		}
	} catch (err){}
	
	//Valido 
	var v_cargo_total = Number(cargo_ospim,2) + Number(cargo_ps,2) +  Number(cargo_imesa,2);
	var v_total = Number(total.replace(",","."),2);

	v_cargo_total = Math.round(v_cargo_total * 100) / 100;	
	
	try {
		if (v_total < v_cargo_total){
			alert('El cargo Ospim más cargo prestadora no puede superar al total de la liquidación.');
			return false;
		}
	} catch (err){}
	
	try {
		jQuery('#<portlet:namespace />cuit_entidad').val(comprobanteCuit);
		jQuery('#<portlet:namespace />sucursal_entidad').val(comprobanteCuitSucursal);
		jQuery('#<portlet:namespace />comprobante_tipo').val(comprobanteTipo);
		jQuery('#<portlet:namespace />comprobante_letra').val(comprobanteLetra);
		
		jQuery('#<portlet:namespace />comprobante_sucu').val(comprobanteSucursal);
		jQuery('#<portlet:namespace />comprobante_nro').val(comprobanteNro.padStart(8,"0"));
		//Nuevos campos para detalle_cuota
		jQuery("#<portlet:namespace />cuota_id_reclamo").val(idreclamo);
		jQuery("#<portlet:namespace />cuota_id_reclamo_prestaciones").val(idprestacionreclamo);
		
		// Pasa al campo de importe de Cuota el monto de cargo Ospim
		jQuery("#<portlet:namespace />importe_cuota").val(cargo_ospim);
		<portlet:namespace />oculta_reclamo_prest_cuota();
		<portlet:namespace />calc_porcentaje();

	} catch (err){}
}

</script>



<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	
	
	
	
	