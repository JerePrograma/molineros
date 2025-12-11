<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFolder"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil"%>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();

NumberFormat formatter = new DecimalFormat("#0.00");     

String portlet_name=null;
portlet_name = "tesoreria";
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}


List<String> headerNames = new ArrayList<String>();
headerNames.add("Fecha");
headerNames.add("Comprobante");
headerNames.add("Concepto");
if("uoma".equalsIgnoreCase(portlet_name)){
	headerNames.add("Gravado");
	headerNames.add("T.IVA %");
	headerNames.add("IVA");
	headerNames.add("Perc.IVA");
	headerNames.add("Perc.IIBB");
	headerNames.add("Otros");
}

headerNames.add("Total");
headerNames.add("Saldo");
headerNames.add("Seccional");	
if(!"uoma".equalsIgnoreCase(portlet_name) ){
  headerNames.add("Rechazado");
}
headerNames.add("");
headerNames.add("");
headerNames.add("");


DLFolder f = DLFolderLocalServiceUtil.getFolder(
	        10136, 0L, "CajaChica");
long folderId = f.getFolderId();


	    

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "comprobante-no-encontrado"));

List<ComprobanteCajaChica> comprobantes= (List<ComprobanteCajaChica>) request.getSession().getAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION);					
if (comprobantes != null && !comprobantes.isEmpty()){
	int total = comprobantes.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);
	
	List resultRows = searchContainer.getResultRows();

	
	for (int i = 0; i < comprobantes.size(); i++) {	    
		ComprobanteCajaChica liq = (ComprobanteCajaChica) comprobantes.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		row.addText(liq.getFechaEmisionAsString());
		row.addText(liq.getTipoComprobante()+" "+ liq.getLetraComprobante()+" "+liq.getPtoVenta()+"-"+liq.getNroComprobante()+ "  "+
		liq.getAcreedorEmpresa().getCuit()+" "+liq.getAcreedorEmpresa().getDescripcion() );
		row.addText(liq.getConceptos().get(0).getConceptoComprobante().getDescripcion());
		
		if("uoma".equalsIgnoreCase(portlet_name)){
			row.addText(liq.getGravadoIVA()==null?"":formatter.format(liq.getGravadoIVA()));
			row.addText(liq.getTasaIva()==null?"":formatter.format(liq.getTasaIva()*100D ));
			row.addText(liq.getIva()==null?"":formatter.format(liq.getIva()));
			row.addText(liq.getPercepcionIVA()==null?"":formatter.format(liq.getPercepcionIVA() ));
			row.addText(liq.getPercepcionIIBB()==null?"":formatter.format(liq.getPercepcionIIBB()));
			row.addText(liq.getOtrosTributos()==null?"":formatter.format(liq.getOtrosTributos()));
		}
		
		row.addText(liq.getImporteComprobante().toString());
		row.addText(formatter.format(liq.getImporteComprobanteOriginal()));
		row.addText(liq.getSeccional()!=null && liq.getSeccional().getDescripcion()!=null?liq.getSeccional().getDescripcion():"");
		StringBuilder sb=new StringBuilder();
		
		
		if(!"uoma".equalsIgnoreCase(portlet_name) ){
		  sb.append("<input type=\"checkbox\"");
		  if(liq.getRechazado()){
		     sb.append("checked=\"checked\"");
		  }
		  sb.append("name=\"aprobado\"");		
		  sb.append("id=\"");
		  sb.append("check-"+liq.getId());
	      sb.append("\" value=\"");
		  sb.append(liq.getId());
		  sb.append("\"/>");		
		
		  row.addText(sb.toString());
		}
		
		sb=new StringBuilder();
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Editar Comprobante\" src=\"");
	    sb.append(themeDisplay.getPathThemeImages());
	    sb.append("/common/edit.png\" onClick=\"javascript:editarComprobanteCajaChica('");
	    sb.append(liq.getId() );
	    sb.append("');\"");
        sb.append(" title=\"Editar\"");
	    sb.append("/>");
	    
	    if("uoma".equalsIgnoreCase(portlet_name)){
	       sb.append("<a href=\"javascript:void(0)\" onclick=\"help(event, 'helpUltMovEdit')\"><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>&nbsp;");  
	    }
	    
	    row.addText(sb.toString());  
		
	    sb=new StringBuilder();
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Eliminar Comprobante\" src=\"");
	    sb.append(themeDisplay.getPathThemeImages());
	    sb.append("/common/delete.png\" onClick=\"javascript:eliminarComprobanteCajaChica('");
	    sb.append(liq.getId() );
	    sb.append("');\"");
        sb.append(" title=\"Eliminar\"");
	    sb.append("/>");
	    
	    if("uoma".equalsIgnoreCase(portlet_name)){
		       sb.append("<a href=\"javascript:void(0)\" onclick=\"help(event, 'helpUltMovDelete')\"><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>&nbsp;");  
		}
	    row.addText(sb.toString());  
	    
	    
	    if("uoma".equalsIgnoreCase(portlet_name)){
	    	String nameFile=liq.getImagenNombreFileEntry();
	    	StringBuilder  sb1=new StringBuilder();
	    	if(!"".equals(nameFile)){
	    	
		      sb1.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Ver Imágen Comprobante\" src=\"");
	          sb1.append(themeDisplay.getPathThemeImages());
	          sb1.append("/common/view.png\" onClick=\"javascript:verImagen('");
	          sb1.append(String.valueOf(folderId));
			  sb1.append("','");
			  sb1.append(nameFile);
	          sb1.append("');\""); 
              sb1.append(" title=\"Ver Imágen\"");
	          sb1.append("/>");
	    	}else{
	    		sb1.append("");
	    	}
	      
		    row.addText(sb1.toString());  
		}
	   
	    
		resultRows.add(row);
	}


}
%>
	
 		
<script type="text/javascript">
function editarComprobanteCajaChica(id_comprobante){
	var params = "&<%= Constants.CMD %>=" + "editarcomprobante";
	params +="&comprobanteid="+id_comprobante;
	params +="&usuario_modi="+ '<%=usuario_modi%>';
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';	
	url = url + params;
	submitForm(document.<portlet:namespace />fmCJCHEJ, url);	
	return false;		
}

function eliminarComprobanteCajaChica(id_comprobante){
	var params = "&<%= Constants.CMD %>=" + "eliminarcomprobante";
	params +="&comprobanteid="+id_comprobante;
	params +="&usuario_modi="+ '<%=usuario_modi%>';
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
	url = url + params;
	submitForm(document.<portlet:namespace />fmCJCHEJ, url);	
	return false;		
}


function verImagen(folderId,fileName){
	
	   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	   '<liferay-portlet:param name="struts_action" value="/uoma/documentacion_adjunta_recuperar"/>'+
	   '<liferay-portlet:param name="name" value="__Name"/>'+
	   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
	   '</liferay-portlet:actionURL>';      
	   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
	   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}


</script>
	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>

