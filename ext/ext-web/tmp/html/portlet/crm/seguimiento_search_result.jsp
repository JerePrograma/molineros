<%@ include file="/html/portlet/crm/init.jsp" %>
<%
	//obtengo lista del request	
	List<DerivacionSeguimiento> derivaciones= null;
	derivaciones = (List<DerivacionSeguimiento>) request.getAttribute(WebKeysCrm.CRM_DERIVACIONES_CONTACTO);
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	int total = 0;
	/* boolean esView = (Boolean)request.getAttribute("view"); */
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();
    String derivadoA = "", altaSecUsu = "";
	headerNames.add("Derivado a");
	headerNames.add("Fecha Derivacion");
	headerNames.add("Usuario Derivacion");
	headerNames.add("Observaciones Derivacion");
	
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"no-seguim-contactos-crm-were-found"));
		
	//recupero coincidencias		
	if (null != derivaciones && derivaciones.size() > 0) {
		total = derivaciones.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		for (int i = 0; i < derivaciones.size(); i++) {
			DerivacionSeguimiento ds = (DerivacionSeguimiento) derivaciones.get(i);
 			ResultRow row = new ResultRow(ds, String.valueOf(ds.getId()), i);		
 			PortletURL rowURL = renderResponse.createRenderURL();
 			/* rowURL.setWindowState(WindowState.MAXIMIZED);
 			rowURL.setParameter("struts_action","/afiliados/editar_contacto_entry");
 			rowURL.setParameter("id_serialcontacto", String.valueOf(c.getId()));
 			rowURL.setParameter("view", "true");  */
 			derivadoA = ds.getDerivacionEdificio() + "/" + ds.getDerivacionSector() + "/" + ds.getDerivacionUsr();
 			altaSecUsu = ds.getModiSector() + "/" + ds.getAltaUsr();
 			row.addText(derivadoA); 
			row.addText(sdf.format(ds.getAltaFecha()));
			row.addText(altaSecUsu);
			row.addText(ds.getObservaciones());
			
			resultRows.add(row);
		}
	}
%>
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />

