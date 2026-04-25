<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
PortletURL portletURL = renderResponse.createRenderURL();

List<TelefonoPrestador> telefonosLugarAtPrestador = null;

telefonosLugarAtPrestador =  (ArrayList<TelefonoPrestador>) request.getSession().getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);

List<String> headerNames = new ArrayList<String>();

headerNames.add("Tipo");
headerNames.add("C�digo Pa�s");
headerNames.add("C�digo Area");
headerNames.add("N�mero");
headerNames.add("Extensi�n");
headerNames.add("Observaciones");
headerNames.add("Eliminar");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-telefonos-lugar-atencion-were-found"));
					
if (telefonosLugarAtPrestador != null && telefonosLugarAtPrestador.size()>0){
	int total = telefonosLugarAtPrestador.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < telefonosLugarAtPrestador.size(); i++) {	    
 		
 		TelefonoPrestador tel = (TelefonoPrestador) telefonosLugarAtPrestador.get(i);
 			
	 	ResultRow row = new ResultRow(tel,new Integer(1+i), i);
	 	if (tel.getTipo().startsWith("P") ){
	 		row.addText("Particular");
 		}else if (tel.getTipo().startsWith("C")){
	 		row.addText("Celular");
 		}else if(tel.getTipo().startsWith("F")){
	 		row.addText("Fax");
 		}
  		row.addText(tel.getCodigoPais());
  		row.addText(tel.getCodigoArea());
  		row.addText(tel.getNumero());
  		row.addText(tel.getExtension());
  		row.addText(tel.getObservaciones());
  		
  		StringBuilder sb=new StringBuilder(); 
  		if(tel.getPropio().equals("P") || tel.getPropio().equals("D")){
	  		if(tel.getEstado() == null || !tel.getEstado().equals(Telefono.ESTADOS.BAJA)){
			 	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"borrar telefono\" src=\"");
		 		sb.append(themeDisplay.getPathThemeImages());
		 		sb.append("/common/delete.png\" onClick=\"javascript:borrarTelefonoLugarAt('");
		 		sb.append(String.valueOf(tel.getId()));
		 		sb.append("');\" />");
	  		}else{
	  			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img height='16'  width='16' src='/html/themes/classic/images/common/close.png'/>");
	  		}
	  		row.addText(sb.toString()); 
  		}else{
  			row.addText("N/D");
  		}
 
		resultRows.add(row);
		
 	}
} 

%>
<!-- Poner excepcion para Telefono duplicado ?? -->

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	


<script>
function borrarTelefonoLugarAt(idTel){
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/prestadores/borrar_telefono_lugar_at_prestador';
	url = url+'&idTelefono='+idTel;
		jQuery("#<portlet:namespace />lista_telefonos").load(url);   
	}
</script>
