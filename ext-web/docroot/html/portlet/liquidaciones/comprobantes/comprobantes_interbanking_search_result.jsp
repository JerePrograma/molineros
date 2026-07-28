<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFileEntry"%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFolder"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil"%>
<portlet:defineObjects/>
			<%
			        NumberFormat f2D = new DecimalFormat("#0.00");
			        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					String portlet_name=null;
					boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
					if (portlet_name == null || portlet_name.trim().equals("")){
						portlet_name = "liquidaciones";
					}
					if(renderResponse.getNamespace().equals("_UOM_1_")){
						portlet_name = "uoma";
					}
					
					
					DLFolder f = DLFolderLocalServiceUtil.getFolder(
				            10136, 0L, "Interbanking");
				    long folderId = f.getFolderId();
					List<Object> results = (List<Object>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_COMPROBANTES_INTERBANKING);
					
					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
					
					List<String> headerNames = new ArrayList<String>();
					
					headerNames.add("#");
					headerNames.add("folder");
					headerNames.add("document");
					headerNames.add("Descripción");
					headerNames.add("Fecha de Proceso");
					headerNames.add("");
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
				
					
					
					int total = results.size();
					
					searchContainer.setTotal(total);
					
					List resultRows = searchContainer.getResultRows();
						
					int i = 0;
					for (Object f1 :results){
							   ResultRow row = new ResultRow(f1, i, i);
					           row.addText(searchContainer.getStart() + i + 1 + StringPool.PERIOD);
					           i++;
							    
							   DLFileEntry fileEntry = (DLFileEntry) f1;
							   row.setObject(fileEntry);
							   row.addText(fileEntry.getFolder().getName());
							   row.addText(fileEntry.getTitle());
							   row.addText(fileEntry.getDescription());
							   row.addText(sdf.format(fileEntry.getCreateDate()));
								
							   StringBuilder s = new StringBuilder();
							   s.append("<img alt=\"Ver Imagen\" src=\"");
							   s.append(themeDisplay.getPathThemeImages());
							   s.append("/common/view.png\" onClick=\"javascript:verImagenComprobante('");				 					
							   s.append(String.valueOf(folderId));
							   s.append("','");
							   s.append(fileEntry.getName());
							   s.append("');\" /> ");
							   row.addText(s.toString());
								
							   resultRows.add(row);
					}
%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />	
	