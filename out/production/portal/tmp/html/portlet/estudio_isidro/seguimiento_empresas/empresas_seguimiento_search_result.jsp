<%@page import="ar.com.ospim.util.StringUtils"%>
<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ page import="ar.com.ospim.estudioisidro.service.LlamadoServiceUtil" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

			<%
			

	 				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_ABM_ESTUDIO_ISIDRO);
					boolean nuevoSeguimiento = PermissionUtil.userContainsRole(user,WebKeysGlobal.SEGUIMIENTO_EMPRESA);
	 									
					//Si debe mostrarse el btn de agregar afiliado
					List<Empresa> empresas= (ArrayList<Empresa>) portletSession.getAttribute(WebKeysEstudioIsidro.EMPRESAS_BUSCADAS,PortletSession.APPLICATION_SCOPE);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 					 		
			 		headerNames.add("cuit");
			 		headerNames.add("razon-social");
			 		headerNames.add("contacto");
			 		headerNames.add("telefono");
			 		headerNames.add("email");
			 		headerNames.add("estado");
			 		headerNames.add("molinera");
			 		headerNames.add("fecha-calculo-deuda");				 		
			 		headerNames.add("Lote");				 		
			 		int total = 0;	
			 		
			 		
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,1000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-llamados-were-found"));
				
					if(null!=empresas && empresas.size()==1){				 								 	
				 	
					 %>	
					 		<script type="text/javascript">					 			
						 		buscarSeguimiento("<%=empresas.get(0).getCuit()%>","<%=empresas.get(0).getRazon_soc().trim()%>");
					 		</script>
					 	
				 		<%	
					 }else if(null!=empresas && empresas.size()>1){
						 searchContainer.setTotal(empresas.size());
						 	List resultRows = searchContainer.getResultRows();
						 	for (int i = 0; i < empresas.size(); i++) {
						 		Empresa empresa = (Empresa) empresas.get(i);
					 					ResultRow row = new ResultRow(empresa, empresa.getCuit(), i);
						 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
						 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 
						 				StringBuilder sb = new StringBuilder();
										sb.append("<a href='javascript:buscarSeguimiento(\"");
										sb.append(String.valueOf(empresa.getCuit()));										
										sb.append("\",\"");
										sb.append(empresa.getRazon_soc().trim());
										sb.append("\")'>");			
										sb.append(empresa.getCuit());
										sb.append("</a>");
										row.addText(sb.toString());
										
										StringBuilder sb1 = new StringBuilder();
										sb1.append("<a href='javascript:buscarSeguimiento(\"");
										sb1.append(String.valueOf(empresa.getCuit()));										
										sb1.append("\",\"");
										sb1.append(empresa.getRazon_soc().trim());
										sb1.append("\")'>");				
										sb1.append(empresa.getRazon_soc().trim());
										sb1.append("</a>");
										row.addText(sb1.toString());
										
										StringBuilder sb4 = new StringBuilder();
										sb4.append("<a href='javascript:buscarSeguimiento(\"");
										sb4.append(String.valueOf(empresa.getCuit()));										
										sb4.append("\",\"");
										sb4.append(empresa.getRazon_soc().trim());
										sb4.append("\")'>");
										if(	null!=empresa && null!= empresa.getContactosEConcat("P")){
											sb4.append(empresa.getContactosEConcat("P").trim());
										}else{
										    sb4.append("");
										}
										sb4.append("</a>");				
										row.addText(sb4.toString());
										
						 				StringBuilder sb2 = new StringBuilder();
										sb2.append("<a href='javascript:buscarSeguimiento(\"");
										sb2.append(String.valueOf(empresa.getCuit()));										
										sb2.append("\",\"");
										sb2.append(empresa.getRazon_soc().trim());
										sb2.append("\")'>");		
										if(	null!=empresa && null!= empresa.getContactosEConcat("T")){
											sb2.append(empresa.getContactosEConcat("T").trim());
										}else{
											sb2.append("");
										}
										sb2.append("</a>");			
										row.addText(sb2.toString());
										
						 				StringBuilder sb3 = new StringBuilder();
										sb3.append("<a href='javascript:buscarSeguimiento(\"");
										sb3.append(String.valueOf(empresa.getCuit()));										
										sb3.append("\",\"");
										sb3.append(empresa.getRazon_soc().trim());
										sb3.append("\")'>");
										if(	null!=empresa && null!= empresa.getContactosEConcat("E")){
										   sb3.append(empresa.getContactosEConcat("E").trim());
										}else{
										   sb3.append("");	
										}
										sb3.append("</a>");				
										row.addText(sb3.toString());
										
										StringBuilder sb6 = new StringBuilder();
										sb6.append("<a href='javascript:buscarSeguimiento(\"");									
										sb6.append(String.valueOf(empresa.getCuit()));										
										sb6.append("\",\"");
										sb6.append(empresa.getRazon_soc().trim());
										sb6.append("\")'>");	
										if(empresa!=null && null!= empresa.getEstado() 
											&& null!=empresa.getEstado().getDescripcion() ) { 		
											sb6.append(empresa.getEstado().getDescripcion());
										}else{
											sb6.append("");
										}
										sb6.append("</a>");				
										row.addText(sb6.toString());
										
										StringBuilder sb8 = new StringBuilder();
										sb8.append("<a href='javascript:buscarSeguimiento(\"");									
										sb8.append(String.valueOf(empresa.getCuit()));										
										sb8.append("\",\"");
										sb8.append(empresa.getRazon_soc().trim());
										sb8.append("\")'>");	
										if(empresa!=null && empresa.isMolinera()) { 		
											sb8.append("SI");
										}else{
											sb8.append("NO");
										}
										sb8.append("</a>");				
										row.addText(sb8.toString());
										
										StringBuilder sb7 = new StringBuilder();
										sb7.append("<a href='javascript:buscarSeguimiento(\"");									
										sb7.append(String.valueOf(empresa.getCuit()));										
										sb7.append("\",\"");
										sb7.append(empresa.getRazon_soc().trim());
										sb7.append("\")'>");
										if(	null!=empresa && null!= empresa.getFechaUltimoCalculoDeudaAsString()){
										   sb7.append(empresa.getFechaUltimoCalculoDeudaAsString());
										}else{
										   sb7.append("");	
										}
										sb7.append("</a>");				
										row.addText(sb7.toString());
										
										Llamado l = LlamadoServiceUtil.getProponeNroLote(empresa.getCuit(),null);
										StringBuilder sb9 = new StringBuilder();
										sb9.append("<a href='javascript:buscarSeguimiento(\"");									
										sb9.append(String.valueOf(empresa.getCuit()));										
										sb9.append("\",\"");
										sb8.append(empresa.getRazon_soc().trim());
										sb9.append("\")'>");
										if(	null!=empresa && null!= l.getLote() && l.getLote()!=0){
										   sb9.append(String.valueOf(l.getLote()));
										}else{
										   sb9.append("");	
										}
										sb9.append("</a>");				
										row.addText(sb9.toString());
										
										resultRows.add(row);
						 	}					 	
					 			
					 }
				 	
			%>
<%if(null!=empresas && empresas.size()>1){%>
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
<%}%>	 
	
</form>