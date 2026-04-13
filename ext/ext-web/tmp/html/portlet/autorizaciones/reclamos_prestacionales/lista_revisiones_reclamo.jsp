<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
PortletURL portletURL = renderResponse.createRenderURL();

boolean inHabilitar = false;
boolean auditoriaAdministrativa=false;
String cmd = (String) request.getAttribute(Constants.CMD);
int cantrevisionesok =0;

if (cmd != null && cmd.equalsIgnoreCase(Constants.VIEW)){
	inHabilitar= true;
}

	
List<RevisionesReclamo> revisionesreclamo = null;

revisionesreclamo =  (ArrayList<RevisionesReclamo>) request.getSession().getAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION);


List<String> headerNames = new ArrayList<String>();

headerNames.add("Fecha Revisión");	
headerNames.add("Presentes");
headerNames.add("Resolución");
headerNames.add("Resp Resolución");
headerNames.add("Observaciones");
headerNames.add("Eliminar");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-revisiones-were-found"));
					
if (revisionesreclamo != null && revisionesreclamo.size()>0){
	int total = revisionesreclamo.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < revisionesreclamo.size(); i++) {	    
 		
 		RevisionesReclamo revreclamo  = (RevisionesReclamo) revisionesreclamo.get(i);
	 	ResultRow row = new ResultRow( revreclamo,new Integer(1+i), i);	 	
	 	row.addText( revreclamo.getFecha_revisionTostring() );	 	 
	 	row.addText( Validator.isNotNull( revreclamo.getUsr_presente() ) ? revreclamo.getUsr_presente()  : "" );	 	
	 	row.addText( Validator.isNotNull( revreclamo.getUsr_resolucion() ) ? revreclamo.getUsr_resolucion()  : "" );	 	
	 	row.addText( Validator.isNotNull( revreclamo.getUsr_responsable_resolucion() ) ? revreclamo.getUsr_responsable_resolucion()  : "" );
	 	
	 	if  (   (revreclamo.getObservacion() != null) &&   (revreclamo.getObservacion().length()>15) ){	 		
		 	StringBuilder sbo=new StringBuilder();
		 		    sbo.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"edita prestacion\" src=\"");
		 			sbo.append(themeDisplay.getPathThemeImages());
		 			sbo.append("/common/conversation.png\"  title='" + revreclamo.getObservacion() +"'");
		 			sbo.append(" onClick=\"javascript:VtnaObs('");
			 		sbo.append(String.valueOf(revreclamo.getObservacion()  ));
				 	sbo.append("','Observacion de la Revision');\" />");
				    row.addText(sbo.toString());
	 	}else{
	 		row.addText( Validator.isNotNull( revreclamo.getObservacion() ) ? revreclamo.getObservacion()  : "" );
	 	}
	 	
	 	if ( revreclamo.getUsr_responsable_resolucion()!=null){
	 		if (revreclamo.getUsr_responsable_resolucion().equals("AUDITORIA ADMINISTRATIVA") ){
		 		auditoriaAdministrativa=true;
		 	}	
	 	}
	 	
  		StringBuilder sb=new StringBuilder(); 
  		if(revreclamo.getEstado() == null || !revreclamo.getEstado().equals(RevisionesReclamo.ESTADOS.BAJA)){
		 	if (!inHabilitar){
  			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"borrar especialidades\" src=\"");
	 		sb.append(themeDisplay.getPathThemeImages());
	 		sb.append("/common/delete.png\" onClick=\"javascript:borrarRevision('");
	 		sb.append(String.valueOf(revreclamo.getId()));
	 		sb.append("');\" />");
	 		cantrevisionesok++;
	 		
		 	               }else{		 	            	  
		 	                  sb.append(" ");                 }

  		}else{
  			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img height='16'  width='16' src='/html/themes/classic/images/common/close.png'/>");
  		}
 		row.addText(sb.toString()); 
		resultRows.add(row);		 
 	}
} 


%>
<liferay-ui:error exception="<%=RevisionesReclamosException.class %>" message="error-en-revision-reclamo" />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script>
<%if (cantrevisionesok>0) {%>
jQuery("#<portlet:namespace />cantrevisionesactivas").val('<%=cantrevisionesok%>');
<%}%>
<%if (auditoriaAdministrativa) {%>
jQuery("#<portlet:namespace />auditoriaadministrativa").val('Ok');
<%}%>

function borrarRevision (idRevision){
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/borrar_reclamosrevisiones';
	url = url+'&idRevision='+idRevision;	
		jQuery("#<portlet:namespace />lista_revisiones").load(url);		
	// visualizar el boton de agregar revision  
		jQuery("#<portlet:namespace />botonrevision").show();
		jQuery("#<portlet:namespace/>mensajerevisionefectuada").html("");		
	}
</script>