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
headerNames.add("Estado");
headerNames.add("Codigo");
headerNames.add("Prestacion");
headerNames.add("Fecha");
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
headerNames.add("Tope Plan");

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
	 	row.addText(presreclamo.getFechaPrestacion()!=null?sdf.format(presreclamo.getFechaPrestacion()):"");
	 	row.addText(presreclamo.getFrecuencia());
	 	
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
	 	row.addText(presreclamo.getCantidadString() );
	 	row.addText(format2D.format(presreclamo.getImporte()) ); 	 	
	 	row.addText(format2D.format(presreclamo.getImporte() * presreclamo.getCantidad() )  ); 
	
	 	row.addText(presreclamo.getCargo_ospimString()  );
	 	row.addText(presreclamo.getCargo_psString() );	
	 	row.addText(presreclamo.getCargo_imesaString() );	
	 	row.addText(presreclamo.getImporteTopePlanString()  );
	 	
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
			 		//sb.append(String.valueOf(presreclamo.getCargo_ospim()   ));
			 		sb.append(String.valueOf(presreclamo.getCargo_ospimString()   ));
			 		sb.append("','");
			 		//sb.append(String.valueOf(presreclamo.getCargo_ps()));
			 		sb.append(String.valueOf(presreclamo.getCargo_psString()));
			 		sb.append("','");
                    //sb.append(String.valueOf(presreclamo.getCargo_imesa()));
                    sb.append(String.valueOf(presreclamo.getCargo_imesaString()));
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
			 		sb.append("','");
			 		sb.append(presreclamo.getFechaPrestacion()!=null?sdf.format(presreclamo.getFechaPrestacion()):"");
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getImporteTopePlan()));
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
		comprobanteLetra, cbu, cuilCuenta, emailCuenta,apellidoCuenta, nombreCuenta, prestacionFecha, importeTopePlan ){
	    
	// solo para el formulario de liquidaciones cuando la liquidacion esta cerrada 
	try {
		if ( jQuery('#<portlet:namespace />ajustarLiquidacion').is(":visible")){
			alert('La liquidación se encuentra Cerrada por lo que  no se puede añadir prestaciones de Reclamos del Afiliado.');
			return false;
		}
	} catch (err){}
	
	//Valido 
	var v_cargo_total = Number(cargo_ospim,2) + Number(cargo_ps,2)+Number(cargo_imesa,2);

	var v_total = Number(total.replace(",","."),2);

	v_cargo_total = Math.round(v_cargo_total * 100) / 100;

	try {
		if (v_total < v_cargo_total){
			alert('El cargo Ospim más cargo prestadora no puede superar al total de la liquidación.');
			return false;
		}
	} catch (err){}
			
	    jQuery('#<portlet:namespace />prestacion').val(prestacion);	
	    jQuery('#<portlet:namespace />codigo').val(codigo);
		
	    //jQuery('#<portlet:namespace />importe').val(importe.replace(",","."));
	  	
	    // Duvi - 2022-04-21
	    // En reintegro de Protesis, baja el importe Total a Importe
	    /*
	    No estoy pudiendo acceder a las const de WebKeys
	    REINTEGRO_PRE = "pre";
		REINTEGRO_ODO_PROTESIS = "pro";
		REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA = "ort";
	    */
	    var tipo_reintegro = jQuery('#<portlet:namespace />tipo_reintegro').val();
	    	    	    	
	    if (tipo_reintegro == "ort") {
	    	jQuery('#<portlet:namespace />importe').val(importeTopePlan.replace(",","."));
	    } else  {
		    // Protesis, baja el Importe del RP
		    jQuery('#<portlet:namespace />importe').val(importe.replace(",","."));
	    } 
	    
		// guardo el original para validar que no supere este valor  
		jQuery('#<portlet:namespace />importeoriginalreclamo').val(total.replace(",","."));
		
		jQuery('#<portlet:namespace />cantidad').val(cantidad);
		jQuery('#<portlet:namespace />total').val(total.replace(",","."));
		<portlet:namespace />oculta_prestaciones_reclamos();
		jQuery("#<portlet:namespace />div_boton_cancelar_reclamos_prestaciones").show();
				
		jQuery('#<portlet:namespace />id_reclamo_prestacional').val(idreclamo);
		jQuery('#<portlet:namespace />id_prestacion_reclamo_prestacional').val(idprestacionreclamo);
		
		jQuery('#<portlet:namespace />cargo_ospim').val(cargo_ospim.replace(",","."));
		jQuery('#<portlet:namespace />cargo_prestadora').val(cargo_ps.replace(",","."));
		jQuery('#<portlet:namespace />cargo_imesa').val(cargo_imesa.replace(",","."));
		jQuery('#<portlet:namespace />tope_importe_plan').val(importeTopePlan);
		
		jQuery('#<portlet:namespace />importeCompro').val(comprobanteTotal.replace(",","."));
		jQuery('#<portlet:namespace />cuit_entidad').val(comprobanteCuit);
		jQuery('#<portlet:namespace />sucursal_entidad').val(comprobanteCuitSucursal);
		jQuery('#<portlet:namespace />comprobante_tipo').val(comprobanteTipo);
		
/* 		jQuery("#<portlet:namespace />comprobante_tipo option[value='"+ comprobanteTipo +"']").attr("selected",true);
 */				
		jQuery('#<portlet:namespace />comprobante_suc').val(comprobanteSucursal);
		jQuery('#<portlet:namespace />comprobante_nro').val(comprobanteNro.padStart(8,"0"));
				
		jQuery('#<portlet:namespace />comprobante_letra').val(comprobanteLetra);
				
		jQuery('#<portlet:namespace />prestacionComproFechaDia').val(parseInt(comprobanteFecha.substring(0,2)) );
		jQuery('#<portlet:namespace />prestacionComproFechaMes').val(parseInt(comprobanteFecha.substring(3,5))-1 );
		jQuery('#<portlet:namespace />prestacionComproFechaAnio').val(parseInt(comprobanteFecha.substring(6,10)) );
		
		try {
		    jQuery('#<portlet:namespace />prestacionFechaDia').val(parseInt(prestacionFecha.substring(0,2)) );
		    jQuery('#<portlet:namespace />prestacionFechaMes').val(parseInt(prestacionFecha.substring(3,5))-1 );
		    jQuery('#<portlet:namespace />prestacionFechaAnio').val(parseInt(prestacionFecha.substring(6,10)) );
		} catch(err) {}
		
		jQuery('#<portlet:namespace />cbu').val(cbu);
		jQuery('#<portlet:namespace />cuil_cuenta').val(cuilCuenta);

		jQuery('#<portlet:namespace />email_cuenta').val(emailCuenta);
		jQuery('#<portlet:namespace />apellido_cuenta').val(apellidoCuenta);
		jQuery('#<portlet:namespace />nombre_cuenta').val(nombreCuenta);
		
		// Pasaje del Cuit del Prestador Externo a Busqueda de Prestador
		jQuery("#<portlet:namespace />prest_cuit").val(comprobanteCuit);

		// desahilita  controles de importes de la prestacion
		try {
			<portlet:namespace />desactivaControlesPrestacionDesdeReclamo(true);	
		} catch(err) {}
				
		jQuery("#<portlet:namespace />div_boton_reclamos_prestaciones").hide();
		
		// desahilita el control de busqueda de afiliados 
		<portlet:namespace />habilitaControlBusquedaAfiliado(true); 
		if(comprobanteCuit!=null && comprobanteCuit!=""){
			try{	
		       <portlet:namespace />buscarEntidad();
			}catch(err){}   
		   if(popup){
				Liferay.Popup.close(popup);
		   }
		     
		}	
		
		// Sección para pasaje de datos a Liquidaciones
		try{ 
			
			jQuery('#<portlet:namespace />cuit_prestador').val(comprobanteCuit);
			jQuery('#<portlet:namespace />sucu').val(comprobanteSucursal);

			
			jQuery('#<portlet:namespace />fechaEDia').val(parseInt(comprobanteFecha.substring(0,2)) );
			jQuery('#<portlet:namespace />fechaEMes').val(parseInt(comprobanteFecha.substring(3,5))-1 );
			jQuery('#<portlet:namespace />fechaEAnio').val(parseInt(comprobanteFecha.substring(6,10)) );
			/*
			jQuery('#<portlet:namespace />fechaRDia').val(parseInt(comprobanteFecha.substring(0,2)) );
			jQuery('#<portlet:namespace />fechaRMes').val(parseInt(comprobanteFecha.substring(3,5))-1 );
			jQuery('#<portlet:namespace />fechaRAnio').val(parseInt(comprobanteFecha.substring(6,10)) );
			jQuery('#<portlet:namespace />fechaVDia').val(parseInt(comprobanteFecha.substring(0,2)) );
			jQuery('#<portlet:namespace />fechaVMes').val(parseInt(comprobanteFecha.substring(3,5))-1 );
			jQuery('#<portlet:namespace />fechaVAnio').val(parseInt(comprobanteFecha.substring(6,10)) );
			*/
			
		}catch(err){}
		
		try{
		  <portlet:namespace />buscarPrestador();
		}catch(err){}
		/*
		if(popupdd){
			   Liferay.Popup.close(popupdd);;
		}
		if(popupAfell){
		     Liferay.Popup.close(popupAfill);
		}
		*/
		 try{
		   <portlet:namespace />buscarPrestacion();
         }catch(err){}
}

</script>



<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	
	
	
	