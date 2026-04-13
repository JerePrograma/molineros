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
headerNames.add("Estado");
headerNames.add("Codigo");
headerNames.add("Prestacion");
headerNames.add("Frecuencia");
headerNames.add("Comprobante");
headerNames.add("letra");
headerNames.add("sucu");
headerNames.add("nro");
headerNames.add("Cantidad");
headerNames.add("Importe");
headerNames.add("Total");
headerNames.add("Cargo OSPIM");
headerNames.add("Cargo PS");
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
	 		row.addText(String.valueOf(presreclamo.getCodigoPrestacion())); 	
	 	}		 		 	 	 		 		
	 	row.addText(presreclamo.getDescripcion());	  	
	 	row.addText(presreclamo.getFrecuencia());	
	 	if(!StringUtils.checkEmpty(presreclamo.getComprobanteTipo())){
	 		row.addText(presreclamo.getComprobanteTipo());	
		 	row.addText(presreclamo.getComprobanteLetra());	
		 	row.addText(presreclamo.getComprobanteCUITSucursal());	
		 	row.addText(presreclamo.getComprobanteNro());	
		 		
	 	}else{
			row.addText("");	
			row.addText("");
			row.addText("");
			row.addText("");
	 	}
	 	row.addText(presreclamo.getCantidadString() );
	 	row.addText(format2D.format(presreclamo.getImporte()) ); 	 	
	 	row.addText(format2D.format(presreclamo.getImporte() * presreclamo.getCantidad() )  ); 
	 	
	 	row.addText(presreclamo.getCargo_ospimString()  );
	 	row.addText(presreclamo.getCargo_psString() );
	 	
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
			 		sb.append(String.valueOf(presreclamo.getCuilTitular()));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getInte()));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getComprobanteTipo()));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getComprobanteLetra()));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getComprobanteCUITSucursal()));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getComprobanteNro()));
			 		
				 	sb.append("');\" />");	
		
 		row.addText(sb.toString()); 
		resultRows.add(row);		 
 	}
} 

// }

%>
<script>
function pasadatosaformulario(codigo,importe,cantidad,total, idreclamo, idprestacionreclamo,prestacion , cargo_ospim ,cargo_ps , cuilTitular , inte, comprobanteTipo , comprobanteLetra, comprobanteCUITSucursal, comprobanteNro){
	    
	
	// solo para el formulario de liquidaciones cuando la liquidacion esta cerrada 
	try {
		if ( jQuery('#<portlet:namespace />ajustarLiquidacion').is(":visible")){
			alert('La liquidación se encuentra Cerrada por lo que  no se puede añadir prestaciones de Reclamos del Afiliado.');
			return false;
		}
	} catch (err){}
	
	//Valido 
	var v_cargo_total = Number(cargo_ospim,2) + Number(cargo_ps,2);
	var v_total = Number(total.replace(",","."),2);

	try {
		if (v_total < v_cargo_total){
			alert('El cargo Ospim más cargo prestadora no puede superar al total de la liquidación.');
			return false;
		}
	} catch (err){}
	
	
	    jQuery('#<portlet:namespace />prestacion').val(prestacion);	
	    jQuery('#<portlet:namespace />codigo').val(codigo);
		jQuery('#<portlet:namespace />importe').val(importe.replace(",","."));
		// guardo el original para validar que no supere este valor  
		jQuery('#<portlet:namespace />importeoriginalreclamo').val(total.replace(",","."));
		
		jQuery('#<portlet:namespace />cantidad').val(cantidad);
		jQuery('#<portlet:namespace />total').val(total.replace(",","."));
		<portlet:namespace />oculta_prestaciones_reclamos();
		jQuery("#<portlet:namespace />div_boton_cancelar_reclamos_prestaciones").show();
				
		jQuery('#<portlet:namespace />id_reclamo_prestacional').val(idreclamo);
		jQuery('#<portlet:namespace />id_prestacion_reclamo_prestacional').val(idprestacionreclamo);
		
		jQuery('#<portlet:namespace />cargo_ospim').val(cargo_ospim);    // cargo ospim
		jQuery('#<portlet:namespace />cargo_prestadora').val(cargo_ps); // cargo a prestadora

		jQuery('#<portlet:namespace />cuil_titular_aux').val(cuilTitular);
		jQuery('#<portlet:namespace />inte_aux').val(inte);
			
		if (comprobanteTipo != '' && comprobanteLetra != ''  && comprobanteCUITSucursal != '' && comprobanteNro != ''){
			jQuery('#<portlet:namespace />comprobante_tipo').val(comprobanteTipo);

			jQuery('#<portlet:namespace />comprobante_letra').val(comprobanteLetra);

			jQuery('#<portlet:namespace />sucu').val(comprobanteCUITSucursal);

			jQuery('#<portlet:namespace />comprobante_nro').val(comprobanteNro);
	
		}
		

		
		
		// desahilita  controles de importes de la pretacion 
		<portlet:namespace />desactivaControlesPrestacionDesdeReclamo(true);
		
		jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").hide();
		
		
		
       		
	    if (document.getElementById("<portlet:namespace />nombre_plan").value == '') {
	    	var cuil_titular_aux = jQuery('#<portlet:namespace />cuil_titular_aux').val();
	    	var inte_aux = jQuery('#<portlet:namespace />inte_aux').val();
	    	
			jQuery('#<portlet:namespace />cuil').val(cuilTitular);
			jQuery('#<portlet:namespace />inte').val(inte);		
	    	
	    	<portlet:namespace />buscarAfiliados_(jQuery("#<portlet:namespace />fprest").val());	
	    }
		
		
		// desahilita el control de busqueda de afiliados 
		<portlet:namespace />habilitaControlBusquedaAfiliado(true); 
		
		
		//document.getElementById("<portlet:namespace />numero_afi").disabled = "disabled";
		//document.getElementById("<portlet:namespace />cuil").disabled = "disabled";
		//document.getElementById("<portlet:namespace />inte").disabled = "disabled";
		//document.getElementById("<portlet:namespace/>tipoDoc").disabled = "disabled";
		//document.getElementById("<portlet:namespace />nroDoc").disabled = "disabled";
	    //document.getElementById("<portlet:namespace />buscarAfiliado").disabled = "disabled";
		<portlet:namespace />buscarPrestacion(); // busca prestacion asignada
		
		
		
		
		
}

</script>



<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	
	
	
	