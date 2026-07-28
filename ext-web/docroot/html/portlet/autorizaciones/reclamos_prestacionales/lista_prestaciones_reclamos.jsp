<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String cmd = (String) request.getAttribute(Constants.CMD);

boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_RECLAM_PREST );

boolean inHabilitar = false;
boolean esEdicion=false;
int cantPrestaciones=0;
int cantDeBaja=0;
double montoPS=0;
NumberFormat format2D = new DecimalFormat("#0.00");
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

if (cmd != null &&  cmd.equalsIgnoreCase(Constants.VIEW)   ){
	inHabilitar= true;
}

if (cmd != null &&  cmd.equalsIgnoreCase(Constants.EDIT)   ){
	esEdicion= true;
}

List<PrestacionesReclamo> prestacionesDelReclamo = null;

prestacionesDelReclamo =  (ArrayList<PrestacionesReclamo>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);

List<String> headerNames = new ArrayList<String>();

headerNames.add("Estado");
headerNames.add("Código");
headerNames.add("Prestación");
headerNames.add("Frecuencia");
headerNames.add("Recuperable");
headerNames.add("Fecha Prestación");
headerNames.add("Cpte");
headerNames.add("Cuit");
headerNames.add("Cpte Total");
headerNames.add("Cant");
headerNames.add("Importe");
headerNames.add("Total Autorizado");
headerNames.add("Cargo OSPIM");
headerNames.add("Cargo Prestadora");
headerNames.add("Monotributo");
headerNames.add("Reconocido SSS");
headerNames.add("OP");
headerNames.add("Observación");
	
if (!inHabilitar){
	String vereditarborrar = "Elimina";		 		
	if(showABMButtons) {
		vereditarborrar+="|Edita|Autoriza";
	}
	headerNames.add(vereditarborrar);
}else{
	headerNames.add("");
}

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-prestaciones-were-found"));

%><script type="text/javascript"> 
jQuery('#<portlet:namespace />cantprestacioneslista').val('0');

</script><%
if (prestacionesDelReclamo != null && prestacionesDelReclamo.size()>0){
	int total = prestacionesDelReclamo.size();
	String opcionesCombo="" ; 
	String enabledestado="";
	String captionEstadoAutorizadoRechazado="" ; 
	opcionesCombo="<option value='0'>CARGADO</option><option value='1'>AUTORIZADO</option><option value='2'>RECHAZADO</option>"; 
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	%><script type="text/javascript"> jQuery('#<portlet:namespace />cantprestacioneslista').val('<%=prestacionesDelReclamo.size()%>');</script><%
	cantPrestaciones= prestacionesDelReclamo.size() ; 
 	for (int i = 0; i < prestacionesDelReclamo.size(); i++) {	    
 		
 		PrestacionesReclamo presreclamo  = (PrestacionesReclamo) prestacionesDelReclamo.get(i);
	
	 	ResultRow row = new ResultRow(presreclamo,new Integer(1+i), i);
	 	// checkbosx de autorizados 
		StringBuilder sb1= new StringBuilder();									
		
		enabledestado="";
 		/* if (presreclamo.getEstadoRechazoAprobado()>1 || inHabilitar ){ enabledestado = "disabled='disabled'"; } */
		if ( inHabilitar ){ enabledestado = "disabled='disabled'"; }
		sb1.append("<select " + enabledestado);
		sb1.append("name='comboestadosreclamo" + String.valueOf(presreclamo.getIdRegistro()) +"'" );
		sb1.append("id='comboestadosreclamo" + String.valueOf(presreclamo.getIdRegistro()) +"'" );
		sb1.append("onChange='CambioEstado(" + String.valueOf(presreclamo.getIdRegistro()) +");'>");
		
		if (presreclamo.getEstadoRechazoAprobado()==1){	sb1.append("<option value='1'>AUTORIZADO</option>");captionEstadoAutorizadoRechazado="A U T O R I Z A D A"; 		}
		if (presreclamo.getEstadoRechazoAprobado()==2){	sb1.append("<option value='2'>RECHAZADO</option>"); captionEstadoAutorizadoRechazado="R E C H A Z A D A";		}
		if (presreclamo.getEstadoRechazoAprobado()<1){ 	sb1.append(opcionesCombo); }
		
		sb1.append("</select>");
		
		row.addText(sb1.toString());
	 	
	 	//row.addText(String.valueOf(presreclamo.getCodigoPrestacion())); //  .getIdregistroString());	
	 			 		 	 	 		 		
	 	//row.addText(presreclamo.getDescripcion());
	 	
	 	row.addText(presreclamo.getCodigoPrestacion() != null ? String.valueOf(presreclamo.getCodigoPrestacion()) : "");
	 	row.addText(presreclamo.getDescripcion() != null ? presreclamo.getDescripcion() : "");
	 	row.addText(presreclamo.getFrecuencia());
	 	//row.addText( presreclamo.isRecuperableSur() == null ? "" : presreclamo.isRecuperableSur() ? "Si" : "No"   );
	 	
	 	row.addText( presreclamo.getRecuperable() == null  || presreclamo.getRecuperable()==0? "" : 
	 		presreclamo.getRecuperable()==1 ? "SURGE" : presreclamo.getRecuperable()==3?"Integración":""   );
	 	row.addText(presreclamo.getFechaPrestacion()!=null?sdf.format(presreclamo.getFechaPrestacion()):"");	 
	 	row.addText((presreclamo.getComprobanteTipo()!=null?presreclamo.getComprobanteTipo():"") +  " " + 
	 				(presreclamo.getComprobanteLetra()!=null?presreclamo.getComprobanteLetra():"") +  " " + 
	 	            (presreclamo.getComprobanteSucursal()!=null?presreclamo.getComprobanteSucursal():"") + "-" +
	 	            (presreclamo.getComprobanteNro()!=null?presreclamo.getComprobanteNro():"")+"  "+
	 	            (presreclamo.getComprobanteFecha()!=null?sdf.format(presreclamo.getComprobanteFecha()):"")
	 	            );
	 	row.addText((presreclamo.getComprobanteCUIT()!=null?presreclamo.getComprobanteCUIT():""));
	 	row.addText(format2D.format(presreclamo.getComprobanteTotal()));	 
	 	row.addText( presreclamo.getCantidadString());
	 	//row.addText(format2D.format(presreclamo.getComprobanteTotal()));	 	
	 	//row.addText(format2D.format(presreclamo.getCantidad() * presreclamo.getComprobanteTotal()));
	 	
	 	row.addText(format2D.format(presreclamo.getImporte()));	 	
	 	row.addText(format2D.format(presreclamo.getCantidad() * presreclamo.getImporte()));
	 	row.addText(presreclamo.getCargo_ospimString()  );
	 	row.addText(presreclamo.getCargo_psString() );
	 	row.addText(presreclamo.getCargo_imesaString() );
	 	row.addText(presreclamo.getReconocidoSSSString() );
	 	montoPS= montoPS+  presreclamo.getCargo_ps()   ;
	 	row.addText(presreclamo.getOp() );
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
  		if (esEdicion || !inHabilitar){
	  		if(presreclamo.getEstado() == null || !presreclamo.getEstado().equals(PrestacionesReclamo.ESTADOS.BAJA))  {
	  			if (presreclamo.getEstadoRechazoAprobado()<2){
	  			    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"borrar prestacion\" src=\"");
		 			sb.append(themeDisplay.getPathThemeImages());
		 			sb.append("/common/delete.png\" onClick=\"javascript:borrarPrestacionconvalida('");
			 		sb.append(String.valueOf(presreclamo.getIdRegistro()  ));
				 	sb.append("');\" />");		 			
				// editar
				if( Integer.valueOf(presreclamo.getIdregistroString())  >0) {
					sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"edita prestacion\" src=\"");
		 			sb.append(themeDisplay.getPathThemeImages());
		 			sb.append("/common/edit.png\" onClick=\"javascript:editarPrestacion('");
			 		sb.append(String.valueOf(presreclamo.getIdRegistro()));
			 		sb.append("','");
			 		sb.append(String.valueOf(presreclamo.getCodigoPrestacion()));
				 	sb.append("',0  );\" />");
				}   
	  			 }else{
	  				sb.append(captionEstadoAutorizadoRechazado);
	  			 }
	  			if (presreclamo.getEstadoRechazoAprobado()!=3){
		 		// autorizar  
  		            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"autorizacion prestacion\" src=\"");
		 			sb.append(themeDisplay.getPathThemeImages());
		 			sb.append("/common/submit.png\"   onClick=\"javascript:autorizarPrestacion('");
			 		sb.append(String.valueOf(presreclamo.getIdRegistro()  ));
				 	sb.append("');\" />");	
	  			}
		 			
	  		}else{  			
	  			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img height='16'  width='16' src='/html/themes/classic/images/common/close.png'/>");
	  			cantDeBaja=cantDeBaja+1;
	  		}	  		
  		}else{
  		     if ( ! (presreclamo.getEstado() == null || !presreclamo.getEstado().equals(PrestacionesReclamo.ESTADOS.BAJA)) ){
  			     row.addText("Eliminado"  );
  		     }
  		     else{
  		    	
  		     }
  		    	 
  		} 
		
 		row.addText(sb.toString()); 
		resultRows.add(row);		 
 	}
} 
%>



<liferay-ui:error exception="<%=PrestacionesReclamosException.class %>" message="error-en-prestacion-reclamo" />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

		
<script>

jQuery("#<portlet:namespace />CantidadDePrestacionesDelReclamo").html('Cantidad Total de Prestaciones : <%=cantPrestaciones%>' );
jQuery("#<portlet:namespace />montoPsPrestaciones").val(<%=montoPS%>);

function editarPrestacion(idRegistro,codigoPrestacion, tipoEdicion ){
//tipoEdicion=1 es edicion 
//tipoEdicion=2 es Autorizacion 
//tipoEdicion=3 es Rechazo prestacion


		
	if (jQuery("#<portlet:namespace />datos_edicion_prestacion").is(":hidden")){	
		jQuery("#<portlet:namespace />busqueda_prestaciones").hide();
		jQuery("#<portlet:namespace />busqueda_farmacia").hide();
		jQuery("#<portlet:namespace />datos_edicion_prestacion").show();	
		jQuery("#<portlet:namespace />datos_prestacion_ingreso").hide();
		
		onOffcombosestadosprestaciones(false);
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/editar_reclamosprestaciones';		
		url = url+'&idRegistro='+idRegistro +'&tipoEdicion='+tipoEdicion +'&codigoPrestacion='+  codigoPrestacion +'&estadoAprobacion='+tipoEdicion ;	
		
		jQuery("#<portlet:namespace />datos_edicion_prestacion").load(url, function(){
			setTimeout(function(){
				<portlet:namespace />actualizarAfiliadoPorFechaPrestacionEdicion();
			}, 300);
		});
	}else{
        try{
    		valor=document.getElementById("<portlet:namespace />tipoaccionprestacion").value;

        }catch(err){}
		
		switch (valor )
			{					
				case '1': // editando  
					alert('Esta en proceso de edicion de la prestacion.');
				    break;
   				case '2': // autorizando 
   					alert('Esta en proceso de autorizacion de la prestacion.');
   					break;
   				case '3': // rechazando 
   					alert('Esta en proceso de rechazo de la prestacion.');
   					break;   					
   				default:
   					alert('Esta en proceso de edicion de la prestacion.');
   					
			}		
	}	    
}

function autorizarPrestacion(idPrestacion){
	if (!jQuery("#<portlet:namespace />datos_edicion_prestacion").is(":hidden")){
		alert('Esta editando la prestacion cancele o confirme la edición para continuar.')
		return false;  
		}
		
	var resp = confirm("El paso a autorizaciones grabará los datos, Confirma la acción ?");
	if (resp == true) {
		<portlet:namespace />editaReclamo(true);			
	} 
}


function borrarPrestacionconvalida(idRegistro){
	
	if (!jQuery("#<portlet:namespace />datos_edicion_prestacion").is(":hidden")){
		alert('No se puede eliminar la prestacion porque esta en proceso de edicion, cancele la edición y luego reintente.')
		return false;
	}else{
		
		
		var r = confirm("Seguro de Eliminar la prestación no podrá restaurarla. ?");
		if (r == true) {
		 
		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/borrar_reclamosprestaciones';
		url = url+'&idRegistro='+idRegistro;	
		
		jQuery("#<portlet:namespace />lista_prestaciones_reclamos").load(url);	
		var cant;
		cant =	<%=Validator.isNotNull(prestacionesDelReclamo)  ? prestacionesDelReclamo.size() - cantDeBaja : 0  %>;
		
		jQuery('#<portlet:namespace />cantprestacioneslista').val(cant);	
		
		if (cant==1)
			{
			alert('Recuerde que debe ingresar por lo menos una prestación al caso para que se grabe.');	
			document.getElementById("<portlet:namespace />tipopedido").disabled = false;//habilita la seleccion del tipo de pedido
			 evaluarOnSectorListaEnCero();
			}	
		}
	}	
}

function CambioEstado (idRegistro) {
	
	var tipoAccion =jQuery('#comboestadosreclamo'+ idRegistro).val();
	
	if (tipoAccion==0){ // estado cargado no hace nada  
	   return false;	
	}
	
	document.getElementById("<portlet:namespace />tipoaccionprestacion").value = tipoAccion + '-'  + idRegistro  ;
	
	editarPrestacion(idRegistro,0,tipoAccion);
	
}
function onOffcombosestadosprestaciones(activo){
		
		var nombrecombo ;
		var frm = document.getElementById("<portlet:namespace />reclamo_fm");
		for (i=0;i<frm.elements.length;i++)
		{
			nombrecombo = frm.elements[i].name;
			
			if (  nombrecombo.indexOf('estadosrecla')>0  )
			{
				document.getElementById(nombrecombo).disabled = "";
				if (!activo){
					document.getElementById(nombrecombo).disabled = "disabled";	
				}
			}
		}
		
}



</script>
