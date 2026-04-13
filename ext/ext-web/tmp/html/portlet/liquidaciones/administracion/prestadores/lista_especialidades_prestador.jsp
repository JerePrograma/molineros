<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();

List<ProfesionPrestador> profEspecSubEspDelPrestador = (ArrayList<ProfesionPrestador>) 
											request.getSession().getAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION);

List<String> headerNames = new ArrayList<String>();
headerNames.add("Profesión");
headerNames.add("Especialidad");
headerNames.add("Sub Especialidad");
headerNames.add("Cat. Prof. Ospim");
headerNames.add("Título Profesional");
headerNames.add("Título Especialidad");
headerNames.add("Eliminar");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-especialidades-were-found"));


if (profEspecSubEspDelPrestador != null && !profEspecSubEspDelPrestador.isEmpty()){
	int total = profEspecSubEspDelPrestador.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < profEspecSubEspDelPrestador.size(); i++) {
 		ProfesionPrestador profesionDelPrestador = (ProfesionPrestador) profEspecSubEspDelPrestador.get(i);
	 	EspecialidadPrestador especialidadDelPrestador = profesionDelPrestador.getEspecialidades().get(0);
	 	ResultRow row = new ResultRow(profesionDelPrestador,new Integer(1+i), i);
	  		
		row.addText(profesionDelPrestador.getDescripcion());
		row.addText(especialidadDelPrestador.getDescripcion());
	 	if(especialidadDelPrestador.getSubEspecialidades() != null &&
	 			especialidadDelPrestador.getSubEspecialidades().size() > 0 ){
		
	 		row.addText(especialidadDelPrestador.getSubEspecialidades().get(0).getDescripcion());
	  	}else{
	  		row.addText("");
	  	}
	 	
  		row.addText(profesionDelPrestador.getCategoriaProfOspim());
  		
  		if(profesionDelPrestador.isTituloProfesional()){
  			row.addText("SI");
  		}else{
  			row.addText("NO");
  		}
  		if(especialidadDelPrestador.isTituloEspecialidad()){
  			row.addText("SI");
  		}else{
  			row.addText("NO");
  		}
	 	StringBuilder sb=new StringBuilder(); 
  		if(profesionDelPrestador.getEstado() == null || !profesionDelPrestador.getEstado().equals(ProfesionPrestador.ESTADOS.BAJA)){
		 	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"borrar profesion\" src=\"");
	 		sb.append(themeDisplay.getPathThemeImages());
	 		sb.append("/common/delete.png\" onClick=\"javascript:borrarProfesion('");
	 		sb.append(String.valueOf(profesionDelPrestador.getIdPrestProf()));
	 		sb.append("');\" />");
  		}else{
  			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img height='16'  width='16' src='/html/themes/classic/images/common/close.png'/>");
  		}
 		
 		row.addText(sb.toString()); 
 		
		resultRows.add(row);
	 	
 	}
}
%>

<liferay-ui:error exception="<%=ProfesionEspecialidadSubEspecPrestadorException.class %>" message="prof-esp-sub-esp-duplicada" />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script>
function borrarProfesion(idPrestProf){
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/borrar_prestador_profesion';
	url = url+'&idPrestProf='+idPrestProf;
		jQuery("#<portlet:namespace />lista_especialidades").load(url);   
	}
</script>
