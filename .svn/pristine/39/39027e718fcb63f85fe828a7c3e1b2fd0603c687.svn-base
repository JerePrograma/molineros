<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="com.liferay.portal.kernel.dao.search.*" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ include file="/html/portlet/afiliados/init.jsp" %>

<portlet:defineObjects />

<%
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
	    portlet_name = "afiliados";
	}
	
	List<Afiliado> afiliadosList= (ArrayList<Afiliado>)renderRequest.getAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO);
	if (afiliadosList == null || afiliadosList.size() == 0) {
		afiliadosList = (ArrayList<Afiliado>) portletSession
		.getAttribute(WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION,
			PortletSession.APPLICATION_SCOPE);
	}

    if (afiliadosList == null || afiliadosList.isEmpty()) {
%>
    <div class="portlet-msg-info" style="margin-top:10px;">
        No se encontraron afiliados.
    </div>
<%
    } else {
        PortletURL portletURL = renderResponse.createRenderURL();

        List<String> headerNames = new ArrayList<String>();
        headerNames.add("CUIL");
        headerNames.add("Inte");
        headerNames.add("Apellido");
        headerNames.add("Nombre");
        headerNames.add("Nro. Doc");
        headerNames.add("Seccional");
        headerNames.add("Fecha Vigencia");
        headerNames.add("Fecha Baja");
        headerNames.add("Acciones");

        SearchContainer searchContainer = new SearchContainer(
            renderRequest, null, null,
            SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
            portletURL, headerNames,
            LanguageUtil.get(pageContext, "no-afiliados-were-found")
        );

        searchContainer.setTotal(afiliadosList.size());
        List<ResultRow> resultRows = searchContainer.getResultRows();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < afiliadosList.size(); i++) {
            Afiliado afiliado = afiliadosList.get(i);
            ResultRow row = new ResultRow(afiliado, afiliado.getCuil_titular(), i);

            row.addText(Validator.isNotNull(afiliado.getCuil_titularMasked()) ? afiliado.getCuil_titularMasked() : "");
            row.addText(String.valueOf(afiliado.getInte()));
            row.addText(Validator.isNotNull(afiliado.getApellido()) ? afiliado.getApellido() : "");
            row.addText(Validator.isNotNull(afiliado.getNombre()) ? afiliado.getNombre() : "");
            row.addText(Validator.isNotNull(afiliado.getDocu_numero()) ? afiliado.getDocu_numero() : "");
            row.addText(afiliado.getSeccional() != null && afiliado.getSeccional().getDescripcion() != null ? afiliado.getSeccional().getDescripcion() : "");
            row.addText(Validator.isNotNull(afiliado.getVigen_fechaAsString()) ? afiliado.getVigen_fechaAsString() : "");
            row.addText(Validator.isNotNull(afiliado.getBaja_fechaAsString()) ? afiliado.getBaja_fechaAsString() : "");
            
            PortletURL editarURL = renderResponse.createRenderURL();
            editarURL.setWindowState(LiferayWindowState.MAXIMIZED);
            editarURL.setParameter("struts_action", "/afiliados/buscar_afiliados_cuenta_bancaria");
            editarURL.setParameter("modo", "editar");
            editarURL.setParameter("cuil", afiliado.getCuil_titular());
            editarURL.setParameter("inte", afiliado.getInteAsString());
            
            PortletURL verURL = renderResponse.createRenderURL();
            verURL.setWindowState(LiferayWindowState.MAXIMIZED);
            verURL.setParameter("struts_action", "/afiliados/buscar_afiliados_cuenta_bancaria");
            verURL.setParameter("modo", "ver");
            verURL.setParameter("cuil", afiliado.getCuil_titular());
            verURL.setParameter("inte", afiliado.getInteAsString());
            
            //histórico
			PortletURL historicoURL = renderResponse.createRenderURL();
			historicoURL.setWindowState(LiferayWindowState.MAXIMIZED);
			historicoURL.setParameter("struts_action", "/afiliados/buscar_afiliados_cuenta_bancaria");
			historicoURL.setParameter("modo", "historico");
			historicoURL.setParameter("cuil", afiliado.getCuil_titular());
			historicoURL.setParameter("inte", afiliado.getInteAsString());
            
            String editIcon = themeDisplay.getPathThemeImages() + "/common/edit.png";
            String viewIcon = themeDisplay.getPathThemeImages() + "/common/view.png";
            String historyIcon = themeDisplay.getPathThemeImages() + "./../../../../html/images/icon_localinfo_over.gif";
            
            String botones =
	            "<div style='display:inline-flex; gap:4px; align-items:center;'>"
		          + "<a title='Editar CBU' href='" + editarURL.toString() + "'>"
		          + "<img src='" + editIcon + "' alt='Editar' style='vertical-align:middle;border:none;'/></a>"
		          + "<a title='Ver CBU' href='" + verURL.toString() + "'>"
		          + "<img src='" + viewIcon + "' alt='Ver' style='vertical-align:middle;border:none;'/></a>"
		          + "<a title='Histórico de CBU' href='" + historicoURL.toString() + "'>"
		          + "<img src='" + historyIcon + "' alt='Histórico' style='vertical-align:middle;border:none;'/></a>"
		          + "</div>";
            
            row.addText(botones);

            resultRows.add(row);
        }
%>
    <liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%
    }
%>
