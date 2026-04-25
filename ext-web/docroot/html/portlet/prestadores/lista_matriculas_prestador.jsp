<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
PortletURL portletURL = renderResponse.createRenderURL();

List<MatriculaPrestador> matriculasDelPrestador = null;

matriculasDelPrestador =  (ArrayList<MatriculaPrestador>) request.getSession().getAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION);


List<String> headerNames = new ArrayList<String>();

headerNames.add("Tipo Matr�cula");
headerNames.add("N� Matr�cula");
headerNames.add("Provincia");
headerNames.add("Present� Matr�cula");
headerNames.add("Fecha Vto.");
headerNames.add("Eliminar");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-matriculas-were-found"));
					
if (matriculasDelPrestador != null && matriculasDelPrestador.size()>0){
	int total = matriculasDelPrestador.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < matriculasDelPrestador.size(); i++) {	    
 		
 		MatriculaPrestador mat = (MatriculaPrestador) matriculasDelPrestador.get(i);

	 	ResultRow row = new ResultRow(mat,new Integer(1+i), i);
	 	if (mat.getTipo().startsWith("P") ){
	 		row.addText("Provincial");
 		}else if (mat.getTipo().startsWith("N")){
	 		row.addText("Nacional");
 		}else if(mat.getTipo().startsWith("R")){
	 		row.addText("R.N.P");
 		}
  		row.addText(mat.getNumeroToString());
  		if(mat.getProvincia() != null && mat.getProvincia().getDescripcion() != null){
  			row.addText(mat.getProvincia().getDescripcion());
  		}else{
  			row.addText("");
  		}
			
  		if (mat.isPresentaCopia()){
	 		row.addText("SI");
 		}else{
	 		row.addText("NO");
 		}
  		if(mat.getFechaVto() != null){
  			row.addText(DateUtils.getDateString(mat.getFechaVto(), DateUtils.SHORT));
  		}else{
  			row.addText("");
  		}
  		StringBuilder sb=new StringBuilder(); 
  		if(mat.getEstado() == null || !mat.getEstado().equals(MatriculaPrestador.ESTADOS.BAJA)){
		 	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"borrar especialidades\" src=\"");
	 		sb.append(themeDisplay.getPathThemeImages());
	 		sb.append("/common/delete.png\" onClick=\"javascript:borrarMatricula('");
	 		sb.append(String.valueOf(mat.getIdMatricula()));
	 		sb.append("');\" />");
  		}else{
  			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img height='16'  width='16' src='/html/themes/classic/images/common/close.png'/>");
  		}
 		row.addText(sb.toString()); 
		resultRows.add(row);
		 
 	}
} 

%>
<liferay-ui:error exception="<%=MatriculaNacionalPrestadorException.class %>" message="matricula-nacional" />
<liferay-ui:error exception="<%=MatriculaProvincialPrestadorException.class %>" message="matricula-provincial" />


<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script>
function borrarMatricula(idMatricula){
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/prestadores/borrar_matricula_prestador';
	url = url+'&idMatricula='+idMatricula;
		jQuery("#<portlet:namespace />lista_matriculas").load(url);   
	}
</script>
