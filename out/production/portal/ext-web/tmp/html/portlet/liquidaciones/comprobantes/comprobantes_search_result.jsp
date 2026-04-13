<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFileEntry" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>


<portlet:defineObjects/>
			<%
					String portlet_name=null;
					boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
					if (portlet_name == null || portlet_name.trim().equals("")){
						portlet_name = "liquidaciones";
					}
					if(renderResponse.getNamespace().equals("_UOM_1_")){
						portlet_name = "uoma";
					}	 		
					List<Comprobante> comprobantes = (ArrayList<Comprobante>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_COMPROBANTES);
					renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_COMPROBANTES, comprobantes);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
					String idFacturaImg="";
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("pto-venta");
			 		headerNames.add("comprobante-tipo");
			 		headerNames.add("letra");
			 		headerNames.add("sucursal");
			 		headerNames.add("numero");
					headerNames.add("cuit-emisor");
					headerNames.add("cuit-acreedor");
					headerNames.add("importe");
					headerNames.add("fecha-emision");
					headerNames.add("fecha-recibido");
					headerNames.add("editar-borrar");
					headerNames.add("");
					
					if(portlet_name=="liquidaciones"){
						headerNames.add("IMG");
					}
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
				
					if(null!=comprobantes){
					 	int total = comprobantes.size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < comprobantes.size(); i++) {
					 		Comprobante comp = comprobantes.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
			 				PortletURL rowURL = renderResponse.createRenderURL();		 				
			 				rowURL.setWindowState(WindowState.MAXIMIZED);		 				
			 				rowURL.setParameter("struts_action","/"+portlet_name+"/view_comprobante_entry");
			 				rowURL.setParameter("pto_venta", String.valueOf(comp.getPtoVenta()));
			 				rowURL.setParameter("tipo_comprobante", comp.getTipoComprobante());
			 				rowURL.setParameter("letra", comp.getLetraComprobante());
			 				rowURL.setParameter("sucursal", String.valueOf(comp.getSucuComprobante()));
			 				rowURL.setParameter("nro_comprobante", comp.getNroComprobante());
			 				rowURL.setParameter("cuit_compr_emisor", comp.getCuit());
			 				rowURL.setParameter("VIEW", "VIEW");
			 				row.addText(String.valueOf(comp.getPtoVenta()), rowURL);
			 				row.addText( comp.getTipoComprobante(), rowURL);
			 				row.addText(comp.getLetraComprobante(), rowURL);
			 				row.addText( String.valueOf(comp.getSucuComprobante()), rowURL);
			 				row.addText( comp.getNroComprobante(), rowURL);
			 				row.addText( comp.getCuit(), rowURL);
			 				row.addText( comp.getAcreedorEmpresa().getCuit(), rowURL);
			 				row.addText( comp.getImporteComprobante().toString(), rowURL);
			 				row.addText( comp.getFechaEmisionAsString(), rowURL);
			 				row.addText( comp.getFechaRecepcionAsString(), rowURL);
			 				if (comp.getAnulacion_fecha() != null){
			 					row.addText("Anulado el " + comp.getAnulacion_fechaAsString());
			 					row.addText("");
			 				} else if (comp.isPagado()){
			 					row.addText("Pagado");
			 					row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/comprobantes/editar_borrar_comprobante.jsp");
			 				} else if(!soloVer) {
								row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/comprobantes/editar_borrar_comprobante.jsp");
								row.addText("");
			 				} else if(soloVer){
			 					row.addText("");
			 				}
			 				
			 				idFacturaImg="";
			 				if(portlet_name=="liquidaciones"){
			 					idFacturaImg = comp.getCuit()+"-"+comp.getTipoComprobante()+"-"+comp.getLetraComprobante()+
			 							String.format("%05d",comp.getSucuComprobante())+ comp.getNroComprobante();
			 				   
			 					List<DLFileEntryImpl>imagenes = ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"CPBTE");	
			 				   comp.setImagenes(imagenes);
			 				   
//Imagenes
								StringBuilder sbImg= new StringBuilder();
		 						sbImg.append("");
		 						if(comp.getImagenes()!=null && comp.getImagenes().size()>0){
		 						    sbImg.append("<img alt=\"Ver Imagen\" src=\"");
		 							sbImg.append(themeDisplay.getPathThemeImages());
		 							sbImg.append("/common/view.png\" onClick=\"javascript:verImagenComprobante('");				 					
		 							sbImg.append(String.valueOf(comp.getImagenes().get(0).getFolderId()));
		 							sbImg.append("','");
		 							sbImg.append(comp.getImagenes().get(0).getName());
		 							sbImg.append("');\"");
		 							sbImg.append(" title=\"Imagenes\"");
			 						sbImg.append("/>");
		 						}  
		 			 		    row.addText(sbImg.toString());
	//Fin Imagenes
			 				   
			 				}   
					 		resultRows.add(row);
						}
					 }
			%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />	
	
<script type="text/javascript">	
		function verImagenComprobante(folderId,fileName){
			   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
			   '<liferay-portlet:param name="struts_action" value="/liquidaciones/documentacion_adjunta_recuperar"/>'+
			   '<liferay-portlet:param name="name" value="__Name"/>'+
			   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
			   '</liferay-portlet:actionURL>';      
			   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
			   var izq = screen.width-800;
			   var conf ='width=800,height=800,toolbar=no,resizable=yes,left=screen.width -1000,top=100'.replace('screen.width',izq);
			   //'width=800,height=800,toolbar=no,resizable=yes,left=screen.width -1000,top=100';
			    window.open(url,fileName,conf); 
		}
	</script>	
