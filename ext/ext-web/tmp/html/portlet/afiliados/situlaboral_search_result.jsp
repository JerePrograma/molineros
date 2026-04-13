<%@ include file="/html/portlet/afiliados/init.jsp"%>
<portlet:defineObjects />
<liferay-ui:error exception="<%= SituacionLaboralException.class %>"
	message="situ-laboral-no-valida" />
<%
	//Si debe mostrarse el btn de agregar afiliado
	boolean showABMButtons = PermissionUtil.userContainsRole(user,
			WebKeysAfiliados.ROL_ABM_AFILIADO);
	String cuil = request.getParameter("cuil_titular");
	String view = request.getParameter("view");
	int inte = 0;
	if (null != request.getParameter("inte")
			&& !request.getParameter("inte").trim().equals("")) {
		inte = Integer.parseInt(request.getParameter("inte"));
	}

	List<SituacionLaboral> laboralList = (List<SituacionLaboral>)request.getSession().getAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL);
	
	/* if (null == laboralList) {
		laboralList = SituLaboralServiceUtil.buscaSituLaboral(cuil,
				inte);
	} */
	request.getSession().setAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL, laboralList);
	

	PortletURL portletURLSitu = renderResponse.createRenderURL();
	List<String> headerNamesSitu = new ArrayList<String>();
	headerNamesSitu.add("cuil");
	headerNamesSitu.add("inte");
	headerNamesSitu.add("cuit-cuil");
	headerNamesSitu.add("empresa");
	headerNamesSitu.add("sucursal");
	headerNamesSitu.add("categoria");
	headerNamesSitu.add("sit-revista");
	/* headerNamesSitu.add("escala-salarial"); */
	headerNamesSitu.add("Cat.UOMA");
/* 	headerNamesSitu.add("ingre-fecha");
	headerNamesSitu.add("egreso-fecha"); */
	headerNamesSitu.add("Ingreso");
	headerNamesSitu.add("Egreso");
	/* headerNamesSitu.add("motivo-baja"); */
	headerNamesSitu.add("Mot.Baja");
	if (showABMButtons && (null == view || view.trim().equals(""))) {
		headerNamesSitu.add("editar-borrar");
	}
	SearchContainer searchContainerSitu = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, portletURLSitu,
			headerNamesSitu, LanguageUtil.get(pageContext,
					"no-situacion-laboral-were-found"));

	if (null != laboralList) {
		int total = laboralList.size();
		searchContainerSitu.setTotal(total);
		//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
		List resultRowsSitu = searchContainerSitu.getResultRows();
		ResultRow rowSitu = null;
		for (int i = 0; i < laboralList.size(); i++) {
			SituacionLaboral situ = (SituacionLaboral) laboralList.get(i);
			if (situ.getFecha_baja_logica() == null) {
				String status = situ.getEstado();
				if (status == null || status.equals(Constants.ADD)
						|| status.equals(Constants.UPDATE)) {
					if (situ.getAfiliado().getInte() == inte) {
						rowSitu = new ResultRow(situ, situ.getEmpresa().getRazon_soc()!=null?situ.getEmpresa().getRazon_soc().replaceAll("'"," "):"", i, true);
					} else {
						rowSitu = new ResultRow(situ, situ.getEmpresa().getRazon_soc(), i);
					}
					rowSitu.addText(situ.getAfiliado().getCuil_titular());
					rowSitu.addText(String.valueOf(situ.getAfiliado().getInte()));					
					if (situ.getId_categoria() == 5
							|| situ.getId_categoria() == 4
							|| situ.getId_categoria() == 6
							|| situ.getId_categoria() == 7
							|| situ.getId_categoria() == 12) {
						rowSitu.addText(situ.getAfiliado().getCuil());
						rowSitu.addText(situ.getAfiliado().getApellido()
										+ ", "
										+ situ.getAfiliado().getNombre());
					} else if (situ.getId_categoria() == 10
							|| situ.getId_categoria() == 8) {
						rowSitu.addText(situ.getAfiliado().getCuil());
						rowSitu.addText(situ.getAfiliado().getApellido()
								+ ", "
								+ situ.getAfiliado().getNombre());
					} else {
						rowSitu.addText(situ.getEmpresa().getCuit());
						rowSitu.addText(situ.getEmpresa().getRazon_soc());
					}
					rowSitu.addText(situ.getEmpresa().getSucursal());
					rowSitu.addText(situ.getCategoria());
					rowSitu.addText(situ.getRevista());
					rowSitu.addText(situ.getEscala_salarial());
					rowSitu.addText(situ.getFecha_ingreAsString());
					rowSitu.addText(situ.getFecha_bajaAsString());
					rowSitu.addText(situ.getMotivoBaja()!=null&&situ.getMotivoBaja().getDescripcion() != null ? situ.getMotivoBaja().getDescripcion() : "");
					
					StringBuilder sb = new StringBuilder();
					if (null == view || !view.trim().equals("true")) {
						sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
						sb.append(themeDisplay.getPathThemeImages());
						sb.append("/portlet/edit_guest.png\" onClick=\"javascript:editaSituacionLaboral('");
						if (situ.getId_categoria() == 5
								|| situ.getId_categoria() == 4
								|| situ.getId_categoria() == 6
								|| situ.getId_categoria() == 7
								|| situ.getId_categoria() == 12) {
							sb.append(situ.getAfiliado().getCuil());
							sb.append("','");
							sb.append(situ.getEmpresa().getSucursal());
							sb.append("','");
							sb.append(situ.getAfiliado().getApellido()
									+ ". "
									+ situ.getAfiliado().getNombre());
							sb.append("','");
						} else if (situ.getId_categoria() == 10
								|| situ.getId_categoria() == 8) {
							if(inte == 0){ // situ laboiral del titular
								sb.append(situ.getAfiliado().getCuil_titular());
							}else if(inte > 0){ // situ laboral del inte 1 (unifica aporte)
								sb.append(situ.getAfiliado().getCuil());
							}
							sb.append("','");
							sb.append(situ.getEmpresa().getSucursal());
							sb.append("','");
							sb.append(situ.getAfiliado().getApellido()
									+ ". "
									+ situ.getAfiliado().getNombre());
							sb.append("','");	
						} else {
							sb.append(situ.getEmpresa().getCuit());
							sb.append("','");
							sb.append(situ.getEmpresa().getSucursal());
							sb.append("','");
							sb.append(situ.getEmpresa().getRazon_soc()!=null?situ.getEmpresa().getRazon_soc().replaceAll("'"," "):"");
							sb.append("','");
						}
						sb.append(situ.getFecha_ingreAsString());
						sb.append("','");
						sb.append(situ.getFecha_bajaAsString());
						sb.append("','");
						sb.append(situ.getId_categoria());
						sb.append("','");
						sb.append(situ.getId_revista());
						sb.append("','");
						sb.append(situ.getEscala_salarial());
						sb.append("','");
						sb.append(situ.getId());
						sb.append("','");
						sb.append(situ.getMotivoBaja()!=null?situ.getMotivoBaja().getId_motivo_baja():"");
						sb.append("','");
						sb.append(situ.getAfiliado().getInte());
						sb.append("');\" />");
						sb.append(" / ");
						sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
						sb.append(themeDisplay.getPathThemeImages());
						sb.append("/common/delete.png\" onClick=\"javascript:borraSituacionLaboral('");
						if (situ.getId_categoria() == 10
								|| situ.getId_categoria() == 8) {
							sb.append(situ.getAfiliado().getCuil_titular());
						} else {
							sb.append(situ.getEmpresa().getCuit());							
						}
						sb.append("','");
						sb.append(situ.getEmpresa().getSucursal());
						sb.append("','");
						sb.append(situ.getEmpresa().getRazon_soc()!=null?situ.getEmpresa().getRazon_soc().replaceAll("'"," "):"");
						sb.append("','");
						sb.append(situ.getFecha_ingreAsString());
						sb.append("');\" />");
						rowSitu.addText(sb.toString());
					}
				} else {
					continue;
				}
				resultRowsSitu.add(rowSitu);
			}
		}
	}
%>
<liferay-ui:error exception="<%= Exception.class %>"
	message="error-al-grabar" />

<liferay-ui:search-iterator searchContainer="<%=searchContainerSitu%>" />


