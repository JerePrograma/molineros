<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<% 
	
			
			
				//Si debe mostrarse el btn de agregar afiliado								
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);				
				Map<String,Afiliado> afiliadosMap= (Map<String,Afiliado>)portletSession.getAttribute(WebKeysAfiliados.CREDENCIALES_A_IMPRIMIR,PortletSession.APPLICATION_SCOPE);
				List<Afiliado> afiliadosList=null;
				if(null!=afiliadosMap){
					afiliadosList=new ArrayList<Afiliado>(afiliadosMap.values());
				}else{
					afiliadosList=new ArrayList<Afiliado>();
				}
				PortletURL portletURL = renderResponse.createRenderURL();				
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("cuil");
		 		headerNames.add("inte");
		 		headerNames.add("apellido");
		 		headerNames.add("nombre");
		 		headerNames.add("parentesco");
				headerNames.add("tipo-documento");
				headerNames.add("nro-documento");
				headerNames.add("seccional");
				headerNames.add("id-ospim");
				headerNames.add("id-uoma");
				headerNames.add("id-amtima");
				headerNames.add("vigen-fecha");
				headerNames.add("baja-fecha");				 
				headerNames.add("borrar");
							
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-afiliados-were-found"));
			
				if(null!=afiliadosList){
	 								 	
	 				//Seteo el total de la lista.
				 	int total = afiliadosList.size();
				 	searchContainer.setTotal(total);
				 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < afiliadosList.size(); i++) {
				 		Afiliado afiliado = (Afiliado) afiliadosList.get(i);
	 					ResultRow row = new ResultRow(afiliado,afiliado.getCuil_titular(), i);		 				
		 				row.addText(afiliado.getCuil_titularMasked());
		 				row.addText(afiliado.getInteAsString());
		 				row.addText(afiliado.getApellido());
		 				row.addText(afiliado.getNombre());
		 				row.addText(afiliado.getParentesco());						
						row.addText(afiliado.getDocumento_tipo());						
						row.addText(afiliado.getDocu_numero());						
						row.addText(afiliado.getSeccional().getDescripcion()!=null?afiliado.getSeccional().getDescripcion():"Sin Especificar");
						
						String ospim = String.valueOf(afiliado.getId_ospim());
						Date ospimFechaBaja = afiliado.getId_ospim_baja_fecha();
						if (ospimFechaBaja != null && ospimFechaBaja.before(new Date())){
							ospim += " <img height='8'  width='8' src='/html/themes/classic/images/common/close.png' alt='Baja el: "+ospimFechaBaja+"'/>";
						}
						row.addText(ospim);
						String uoma = String.valueOf(afiliado.getId_uoma());
						Date uomaFechaBaja = afiliado.getId_uoma_baja_fecha();
						if (uomaFechaBaja != null && uomaFechaBaja.before(new Date())){
							uoma += " <img height='8'  width='8' src='/html/themes/classic/images/common/close.png' alt='Baja el: "+uomaFechaBaja+"'/>";
						}
						row.addText(uoma);
						String amtima = String.valueOf(afiliado.getId_amtima());
						Date amtimaFechaBaja = afiliado.getId_amtima_baja_fecha();
						if (amtimaFechaBaja != null && amtimaFechaBaja.before(new Date())){
							amtima += " <img height='8'  width='8' src='/html/themes/classic/images/common/close.png' alt='Baja el: "+amtimaFechaBaja+"'/>";
						}
						row.addText(amtima);
					
						row.addText(afiliado.getVigen_fechaAsString());
						row.addText(afiliado.getBaja_fechaAsString());
						StringBuffer sb=new StringBuffer();
						// DELETE
						sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 				sb.append(themeDisplay.getPathThemeImages());
		 				sb.append("/common/delete.png\" onClick=\"javascript:borraCredencial('");
		 				sb.append(afiliado.getCuil_titular());
		 				sb.append("|");
		 				sb.append(afiliado.getInte());		 					 					
		 				sb.append("');\" />");
						row.addText(sb.toString());
			 			resultRows.add(row);
				 	}
	 			}
	 	
 		%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
