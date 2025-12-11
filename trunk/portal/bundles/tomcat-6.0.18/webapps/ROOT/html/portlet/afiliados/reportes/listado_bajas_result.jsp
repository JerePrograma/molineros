<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@page import="ar.com.ospim.afiliados.beans.Baja"%>
<%@page import="ar.com.ospim.afiliados.reportes.action.ReporteBusquedaListadoBajasAction"%>
<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%><portlet:defineObjects/>
<%	PortletURL portletURL = renderResponse.createRenderURL();
	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("cuil");
	headerNames.add("DNI");
	headerNames.add("parentesco");
	headerNames.add("apellido");		 		
	headerNames.add("nombre");
	headerNames.add("alta_fecha");
	headerNames.add("baja_fecha");
	headerNames.add("plan");
	headerNames.add("tipo de baja");
	
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
	SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
	LanguageUtil.get(pageContext, "no-reintegros-were-found"));
	boolean hayBajas = false;

	//recupero coincidencias		
	List<Baja> reporte = (List<Baja>)request.getAttribute(WebKeysAfiliados.AFILIADO_BAJA);
	String cuil = null;
	
	//Seteo el total de la lista.
	int total = reporte.size();
	searchContainer.setTotal(total);
	List resultRowsBaja = searchContainer.getResultRows();
	if (reporte!=null) {
		for (int i = 0; i < total; i++) {
			Baja baja =  reporte.get(i);
			cuil = baja.getCuil() == null && cuil != null ? cuil : baja.getCuil();
			ResultRow row = new ResultRow(reporte.get(i), i, i);	
			row.addText(cuil);
			row.addText(baja.getDni());
			row.addText(baja.getParentesco());
			row.addText(baja.getApellido());
			row.addText(baja.getNombre());
			row.addText(baja.getAlta_fecha().toString());
			row.addText(baja.getBaja_fecha().toString());
			row.addText(baja.getUltimo_plan().getDescripcion());
			row.addText(baja.getTipo_de_baja());
			resultRowsBaja.add(row);
		}		
	}
%>
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />