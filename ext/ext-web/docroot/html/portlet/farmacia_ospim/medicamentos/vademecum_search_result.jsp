<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
	 
     	<liferay-util:include page="/html/portlet/farmacia_ospim/medicamentos/paginador_vademecum.jsp">
	    </liferay-util:include>
<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysFarmaciaOspim.ROL_MEDICAMENTOS_FARMACIA_OSPIM  );
				List<Vademecum> vademecumList = new ArrayList<Vademecum >();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
				SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
				BusquedaVademecumFiltro   filtroVade   = (BusquedaVademecumFiltro)request.getSession().getAttribute(WebKeysFarmaciaOspim.FILTRO_BUSQUEDA_VADEMECUM);
				
				vademecumList= (ArrayList<Vademecum>)renderRequest.getAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_VADEMECUM );
				if (vademecumList == null || vademecumList.size() == 0) {
					vademecumList = (ArrayList<Vademecum>) portletSession.getAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_VADEMECUM, PortletSession.PORTLET_SCOPE);
				}
				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 				 
		 		headerNames.add("Registro");		 		
		 		headerNames.add("Troquel");
		 		headerNames.add("Nombre");
		 		headerNames.add("Droga");
		 		headerNames.add("Laboratorio");
		 		headerNames.add("presentacion");
		 		headerNames.add("accion");
		 		headerNames.add("Origen");
		 		headerNames.add("Periodo");
		 		if (filtroVade!=null && ! filtroVade.isBuscaEnHistoricoDeVademecum() ){
		 			String vereditarborrar = "Ver";		 		
					if(showABMButtons) {
						vereditarborrar+="|Editar|Borrar";
					}
					headerNames.add(vereditarborrar);	
		 		}
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-vademecum-were-found"));
		        
				if(null!=vademecumList){		 			
					 	int total = vademecumList.size();
					 	searchContainer.setTotal(total);
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < vademecumList.size(); i++) {
					 		Vademecum vademecum     = (Vademecum) vademecumList.get(i);
		 					ResultRow row = new ResultRow(vademecum,vademecum.getRegistro()  , i);
			 				PortletURL rowURL = renderResponse.createRenderURL();
			 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
			 				rowURL.setParameter("struts_action","/farmaciaospim/editar_borrar_vademecum_entry");			 				
					 		rowURL.setParameter("id_registro_vade", String.valueOf(vademecum.getRegistro()   ));
					 		rowURL.setParameter("buscaEnHistorico", String.valueOf(filtroVade.isBuscaEnHistoricoDeVademecum()  ) );
					 		rowURL.setParameter("periodoDia", String.valueOf(vademecum.getPeriodoAltasBajas().getDate() ));
					 		rowURL.setParameter("periodoMes", String.valueOf(vademecum.getPeriodoAltasBajas().getMonth()));
					 		rowURL.setParameter("periodoAnio", String.valueOf(sdfAnio.format(vademecum.getPeriodoAltasBajas())  )  );
					 		rowURL.setParameter("cmd","view");
					 		row.addText(vademecum  != null ? String.valueOf(vademecum.getRegistro())    : "" ,  rowURL);
			 				row.addText(String.valueOf(vademecum.getTroquel())     != null ? String.valueOf(vademecum.getTroquel())  : "" ,  rowURL);
			 				row.addText(vademecum.getNombre()  != null ? vademecum.getNombre().toUpperCase()  : "" ,  rowURL);			 				 				
			 				row.addText(vademecum.getDroga()  != null ? vademecum.getDroga().toUpperCase() : "" ,  rowURL);
			 				row.addText(vademecum.getLaboratorio() != null ? vademecum.getLaboratorio().toUpperCase()  : "" ,  rowURL);
			 				row.addText(vademecum.getPresentacion()  != null ? vademecum.getPresentacion().toUpperCase()  : "" ,  rowURL);
			 				row.addText( vademecum.getAccion() != null ? String.valueOf(vademecum.getAccion())  : "" ,  rowURL);
			 				if (filtroVade!=null && filtroVade.isBuscaEnHistoricoDeVademecum() ){
			 					row.addText(vademecum.getOrigenDeLosDatos()==null?"":vademecum.getOrigenDeLosDatos() ,  rowURL);
			 				}else{
			 					if (vademecum.isNuevaAltaDeLaSss()  )  { 
				 					if (vademecum.getOrigenDeLosDatos()==null){
				 						row.addText("Nueva Alta " ,  rowURL);	
				 					}else{
				 						row.addText("Nueva Alta " + (vademecum.getOrigenDeLosDatos().equals("SSS")?"/SSS":""),  rowURL);
				 					}
				 					
				 				}else{
				 					row.addText(vademecum.getOrigenDeLosDatos()==null?"":vademecum.getOrigenDeLosDatos() ,  rowURL);	
				 				}	
			 				}
			 				
			 				row.addText(vademecum.getPeriodoAltasBajas() != null ? sdf.format(vademecum.getPeriodoAltasBajas())  : "" ,  rowURL);
							// Action
							if (filtroVade!=null && ! filtroVade.isBuscaEnHistoricoDeVademecum() ){
								row.addJSP( "left", SearchEntry.DEFAULT_VALIGN,"/html/portlet/farmacia_ospim/medicamentos/editar_borrar_vademecum.jsp");	
							}
					
				 			resultRows.add(row);
					 	}					 	
		 			}
 		%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
		
	<script type="text/javascript">
			
	function imprimir(id){		
		window.location.href ="/pdfservlet/?accion=equipointerdisciplinario&idequipo=" + id ;
	}
	
	
	
	
	</script>
