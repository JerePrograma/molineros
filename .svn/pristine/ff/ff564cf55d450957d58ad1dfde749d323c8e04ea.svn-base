<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String cmd = (String) request.getAttribute(Constants.CMD);

boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_SITUACIONES_MEDICAS );

boolean inHabilitar = false;
boolean esEdicion=false;


SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");


int cantDeBaja=0;

if (cmd != null &&  cmd.equalsIgnoreCase(Constants.VIEW)   ){
	inHabilitar= true;
}

if (cmd != null &&  cmd.equalsIgnoreCase(Constants.EDIT)   ){
	esEdicion= true;
}

List<PatologiasSituacionMedica> patologiasSituacionMedica  = null;

patologiasSituacionMedica =  (ArrayList<PatologiasSituacionMedica>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PATOLOGIAS_SITUACION_MEDICA_EN_SESION);

List<String> headerNames = new ArrayList<String>();

headerNames.add("id");
headerNames.add("Tipo Sit Medica");
headerNames.add("Vig Desde");
headerNames.add("Vig Hasta");
headerNames.add("Cie X");
headerNames.add("Diagnostico CIE X");
headerNames.add("Diagnostico");
	
if (!inHabilitar){
	String vereditarborrar = "Elimina";		 		
	if(showABMButtons ||  true  ) {
		vereditarborrar+="|Edita|Ver";
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
if (patologiasSituacionMedica != null && patologiasSituacionMedica.size()>0){
	int total = patologiasSituacionMedica.size();
 
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	%><script type="text/javascript"> jQuery('#<portlet:namespace />cantprestacioneslista').val('<%=patologiasSituacionMedica.size()%>');</script><%
	
 	for (int i = 0; i < patologiasSituacionMedica.size(); i++) {	    
 		
 		PatologiasSituacionMedica sitMedicaPatologia   = (PatologiasSituacionMedica) patologiasSituacionMedica.get(i);	

 		ResultRow row = new ResultRow(sitMedicaPatologia,new Integer(1+i), i);	 	 
	 	
	 	row.addText(String.valueOf(sitMedicaPatologia.getIdSituacionMedica()   )); 
	 	row.addText(sitMedicaPatologia.getTipo_situ_medica()  );
	 	row.addText(sitMedicaPatologia.getFechaDesde()==null ? "" : sdf.format(sitMedicaPatologia.getFechaDesde()));
	 	row.addText(sitMedicaPatologia.getFechaHasta() ==null? "" : sdf.format(sitMedicaPatologia.getFechaHasta()) );
	 	row.addText(sitMedicaPatologia.getCodigoCieDiez()==null? "" : sitMedicaPatologia.getCodigoCieDiez() );
	 	row.addText(sitMedicaPatologia.getDiagnosticoCieX()==null? "" : sitMedicaPatologia.getDiagnosticoCieX()  );
	 	row.addText(sitMedicaPatologia.getDiagnostico()==null? "" : sitMedicaPatologia.getDiagnostico() );
  		StringBuilder sb=new StringBuilder(); 
  		if (esEdicion || !inHabilitar){
	  		if(sitMedicaPatologia.getEstado() == null || sitMedicaPatologia.getFechaBaja()==null )  {
	  			    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"borrar prestacion\" src=\"");
		 			sb.append(themeDisplay.getPathThemeImages());
		 			sb.append("/common/delete.png\" onClick=\"javascript:borrarSituacionMedica('");
			 		sb.append(String.valueOf(sitMedicaPatologia.getIdSituacionMedica()  ));
				 	sb.append("');\" />");
				 // editar
					if( Integer.valueOf(sitMedicaPatologia.getIdSituacionMedica())  >0) {
						sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"edita prestacion\" src=\"");
			 			sb.append(themeDisplay.getPathThemeImages());
			 			sb.append("/common/edit.png\" onClick=\"javascript:editarSituacionMedica('");
				 		sb.append(String.valueOf(sitMedicaPatologia.getIdSituacionMedica()  ));
					 	sb.append("',0);\" />");
					}
				 // consultar
				 	if( Integer.valueOf(sitMedicaPatologia.getIdSituacionMedica())  >0) {
						sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"edita prestacion\" src=\"");
			 			sb.append(themeDisplay.getPathThemeImages());
			 			sb.append("/common/view.png\" onClick=\"javascript:consultaSituacionMedica('");
				 		sb.append(String.valueOf(sitMedicaPatologia.getIdSituacionMedica()  ));
					 	sb.append("');\" />");
					}
	  		}else{  			
	  			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img height='16'  width='16' src='/html/themes/classic/images/common/close.png'/>");
	  			cantDeBaja=cantDeBaja+1;
	  		}
	  		
	  	
  		}else{
  		     if ( ! (sitMedicaPatologia.getEstado() == null || !sitMedicaPatologia.getEstado().equals(PatologiasSituacionMedica.ESTADOS.BAJA)) ){
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


<liferay-ui:error exception="<%=PatologiasSituacionMedica.class %>" message="error-en-prestacion-equipo" />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

		
<script>
// 
jQuery('#<portlet:namespace />valortipoprestacion').val('');

function editarSituacionMedica(idSitMedica , tipoEdicion ){	 
		
		if (idSitMedica==jQuery("#<portlet:namespace />registroDeBaja").val() ){
			alert('Ya esta editando este registro');
		}else{
            editaRegistrodeGrilla(idSitMedica);
		}	
}

function consultaSituacionMedica(idSitMedica ){	 
	    // Constants.PREVIEW para vtna popup
	    var regEditado;
	    regEditado=jQuery("#<portlet:namespace />id_registro_situmedica").val();
	    
		 popup = Liferay.Popup({title:"<liferay-ui:message key="Consulta Situación Médica" />",modal:true,width:1200,position:[50,10],xy: ['center', 100],
				 onClose: function() {
			  		var urlReload = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"/>&struts_action=/autorizaciones/editar_borrar_situacionmedica_entry';
					 window.location.href = urlReload;
			 	}}); 
		<%-- var params = "&<%=Constants.CMD %>=" + "<%= Constants.PREVIEW  %>";   
	 	params+="&idRegistroPopUp=" + idSitMedica;	
	 	params+="&idRegistroEditado=" + regEditado ;
	 		
	     var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/editar_borrar_situacionmedica_entry';
	     url += '&rnd=' + Math.floor(Math.random()*100);
	     url = url + params; --%>
	     
	     
	     var xportletUrl = '/autorizaciones/editar_borrar_situacionmedica_entry';
	 		
			var url1= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="cmd" value="__constans"/>'+
			'<liferay-portlet:param name="idRegistroPopUp" value="__idRegistroPopUp"/>'+
			'<liferay-portlet:param name="idRegistroEditado" value="__idRegistroEditado"/>'+
			'<liferay-portlet:param name="rnd" value="__rnd"/>'+
			'</liferay-portlet:renderURL>';
		    url1 = url1.replace("__xportletUrl",xportletUrl); 
	  	    url1 = url1.replace("__constans", "<%= Constants.PREVIEW %>");
	  	    url1 = url1.replace("__idRegistroPopUp", idSitMedica);
	  	    url1 = url1.replace("__idRegistroEditado", regEditado);
	  	  url1 = url1.replace("__rnd", Math.floor(Math.random()*100));
		    
	     
		jQuery(popup).load(url1);	
		
		
		
		
}


function borrarSituacionMedica(idSituMedica ){
var r = confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-patologia'/>");
   
	if (r == true) {
<%-- 
		 var params = "&<%=Constants.CMD %>=" + "<%= Constants.DEACTIVATE %>";  
 	 	params+="&id_registro_sitmed=" + idSituMedica;
 	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/editar_borrar_situacionmedica_entry'; 
 	    url = url + params;
 	    
 --%>	    
	    var xportletUrl = '/autorizaciones/editar_borrar_situacionmedica_entry';
 		
		var url1= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="cmd" value="__constans"/>'+
		'<liferay-portlet:param name="id_registro_sitmed" value="__id_registro_sitmed"/>'+
		'</liferay-portlet:renderURL>';
	    url1 = url1.replace("__xportletUrl",xportletUrl); 
  	    url1 = url1.replace("__constans", "<%= Constants.DEACTIVATE %>");
  	    url1 = url1.replace("__id_registro_sitmed", idSituMedica);
	    
	    jQuery("#<portlet:namespace />lista_prestaciones_equipo").load(url1);
	    
	    if (idSituMedica==jQuery("#<portlet:namespace />registroDeBaja").val() ){
			jQuery("#<portlet:namespace />divBotonEdicion").hide();
			jQuery("#<portlet:namespace/>mensajeDeBaja").html("Registro dado de Baja.");
		}
	}	

	
}


</script>
