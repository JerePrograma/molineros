<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
PortletURL portletURL = renderResponse.createRenderURL();

List<ContactoElectronicoPrestador> contactoesLugarAtPrestador = null;

contactoesLugarAtPrestador =  (ArrayList<ContactoElectronicoPrestador>) request.getSession().getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);

List<String> headerNames = new ArrayList<String>();

headerNames.add("Tipo");
headerNames.add("Contacto");
headerNames.add("Observaciones");
headerNames.add("Eliminar");

/* headerNames.add("Observaciones"); */

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-cont-elect-lugar-atencion-were-found"));
					
if (contactoesLugarAtPrestador != null && contactoesLugarAtPrestador.size()>0){
	int total = contactoesLugarAtPrestador.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
 	for (int i = 0; i < contactoesLugarAtPrestador.size(); i++) {	    
 		
 		ContactoElectronicoPrestador ce = (ContactoElectronicoPrestador) contactoesLugarAtPrestador.get(i);

	 	ResultRow row = new ResultRow(ce,new Integer(1+i), i);
	 	if (ce.getTipo().equals(ContactoElectronico.Tipo.EMAIL)){
	 		row.addText("Correo Electr�nico");
 		}else if (ce.getTipo().equals(ContactoElectronico.Tipo.SITIOWEB)){
	 		row.addText("Sitio Web");
 		}else if (ce.getTipo().equals(ContactoElectronico.Tipo.FAX)){
	 		row.addText("Factura");
 		}
  		row.addText(ce.getContacto());
  		row.addText(ce.getObservaciones());
  		
  		StringBuilder sb=new StringBuilder(); 
  		if(ce.getPropio().equals("P") || ce.getPropio().equals("D")){
	  		if(ce.getEstado() == null || !ce.getEstado().equals(ContactoElectronico.ESTADOS.BAJA)){
			 	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"borrar contacto\" src=\"");
		 		sb.append(themeDisplay.getPathThemeImages());
		 		sb.append("/common/delete.png\" onClick=\"javascript:borrarContactoLugarAt('");
		 		sb.append(String.valueOf(ce.getId()));
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
<!-- Poner excepcion para Contacto duplicado ?? -->

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	


<script>
function borrarContactoLugarAt(idCE){
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/prestadores/borrar_contactoe_lugar_at_prestador';
	url = url+'&idContactoe='+idCE;
		jQuery("#<portlet:namespace />lista_contactoes").load(url);   
	}
</script>
