<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.empresas.beans.Contacto" %>
<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();
boolean rolABMSeccionales = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_SECCIONALES);
String usuario_modi = user.getScreenName();

Seccional seccional = (Seccional)request.getSession().getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
List<Contacto>  pcuentas= new ArrayList<Contacto>();

if(seccional.getPlantel() !=null && seccional.getPlantel().size()>0 ){
	pcuentas=seccional.getPlantel();
}



List<String> headerNames = new ArrayList<String>();
//headerNames.add("ID. Contacto");
headerNames.add("Cargo");
headerNames.add("Nombre");
headerNames.add("Tipo Tel.");
headerNames.add("Cód.Area");
headerNames.add("Teléfono");
if(rolABMSeccionales){
  headerNames.add("Editar");
  headerNames.add("Eliminar");
}else{
  headerNames.add("");
  headerNames.add("");
}

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "contacto-no-encontrado"));

					
if (pcuentas != null && !pcuentas.isEmpty()){
	int total = pcuentas.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < pcuentas.size(); i++) {	    
		
		Contacto contacto = (Contacto) pcuentas.get(i);
		
	 	ResultRow row = new ResultRow(contacto,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		
		row.addText(null!=contacto.getCargoDescripcion()?contacto.getCargoDescripcion():"");
		row.addText(null!=contacto.getNombreApe()?contacto.getNombreApe():"");
		row.addText(null!=contacto.getTelefono() && "F".equalsIgnoreCase(contacto.getTelefono().getTipo()) ?"Fijo" :
			null!=contacto.getTelefono() && "M".equalsIgnoreCase(contacto.getTelefono().getTipo())?"Móvil":"");
		row.addText(null!=contacto.getTelefono() 
					&& contacto.getTelefono().getCodigoArea()!=null 
					&& !"null".equalsIgnoreCase(contacto.getTelefono().getCodigoArea()) 
					&& Integer.parseInt(contacto.getTelefono().getCodigoArea())!=0? contacto.getTelefono().getCodigoArea() :"");
		row.addText(null!=contacto.getTelefono() && Integer.parseInt(contacto.getTelefono().getNumero())!=0 ? contacto.getTelefono().getNumero() :"");
		
		StringBuilder sb0= new StringBuilder();
		StringBuilder sb= new StringBuilder();
		
		if(rolABMSeccionales ){
		  if( (null==contacto.getBajaFecha() || 
				 				(contacto.getEstado()!=null && !contacto.getEstado().equals(Contacto.ESTADOS.BAJA) )) ){
			
		   sb0.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		   sb0.append(themeDisplay.getPathThemeImages());
		   sb0.append("/common/edit.png\" onClick=\"javascript:editarContactoPersonal('");
		   sb0.append(contacto.getIdContacto());
		   sb0.append("','");
		   sb0.append(contacto.getCargo());
		   sb0.append("','");
		   sb0.append(contacto.getNombreApe());
		   sb0.append("','");
		   sb0.append(contacto.getTelefono().getTipo());
		   sb0.append("','");
		   sb0.append(contacto.getTelefono().getNumero());
		   sb0.append("','");
		   sb0.append(contacto.getTelefono().getCodigoArea());
		   sb0.append("');\" />");
		  
		   sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		   sb.append(themeDisplay.getPathThemeImages());
		   sb.append("/common/delete.png\" onClick=\"javascript:borraContactoPersonal('");
		   sb.append(contacto.getIdContacto());
		   sb.append("');\" />");
		   
		 }else{
			sb0.append("");
  			sb.append("&nbsp;<img height='16'  width='16' src='/html/themes/classic/images/common/close.png'/>");
 		 }
		}else{
			sb.append("");
			sb0.append("");
		}
		
		row.addText(sb0.toString());
		row.addText(sb.toString());
		resultRows.add(row);
	}

}
%>
	
 		
<script type="text/javascript">

</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>

