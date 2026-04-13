<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>

<%
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	List<ConsultaIGSTotal> consultasIGS = (List<ConsultaIGSTotal>) request.getSession().getAttribute(WebKeysAfiliados.BUSQUEDA_CONSULTAS_IGS);

	PortletURL portletURLConsIGS = renderResponse.createRenderURL();
	List<String> headerNamesConsIGS = new ArrayList<String>();
	  
	headerNamesConsIGS.add("param-cuil");
	headerNamesConsIGS.add("param-inte"); 
	headerNamesConsIGS.add("param-tipo-doc");
	headerNamesConsIGS.add("param-nro-doc");
	headerNamesConsIGS.add("param-nro-cred");
	headerNamesConsIGS.add("estado");
	headerNamesConsIGS.add("cuil-titular");
	headerNamesConsIGS.add("inte");
	headerNamesConsIGS.add("apellido");
	headerNamesConsIGS.add("nombre");
	headerNamesConsIGS.add("plan");
	headerNamesConsIGS.add("nro-credencial-prevencion");
	headerNamesConsIGS.add("Ip");
	headerNamesConsIGS.add("fecha-alta");


	SearchContainer searchContainerConsultasIGS = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, 
			portletURLConsIGS,
			headerNamesConsIGS, 
			LanguageUtil.get(pageContext,"no-consultas-igs-were-found"));
	int total = 0;

	if (null != consultasIGS) {
		total = consultasIGS.size();
		searchContainerConsultasIGS.setTotal(total);
		List resultRows = searchContainerConsultasIGS.getResultRows();

		for (int i = 0; i < consultasIGS.size(); i++) {
			ConsultaIGSTotal consultaIGS = (ConsultaIGSTotal) consultasIGS.get(i);
			
			ResultRow row = null;
			row = new ResultRow(consultaIGS, String.valueOf(i), i);

			row.addText(consultaIGS.getCuilParametro()!=null&&consultaIGS.getCuilParametro()!="null"?consultaIGS.getCuilParametro():"");
			row.addText(String.valueOf(consultaIGS.getInte()));
			row.addText(consultaIGS.getDocuTipoParam()!=null&&consultaIGS.getDocuTipoParam()!="null"?consultaIGS.getDocuTipoParam():"");
			row.addText(consultaIGS.getDocuNumeroParam()!=null&&consultaIGS.getDocuNumeroParam()!="null"?consultaIGS.getDocuNumeroParam():"");
			row.addText(consultaIGS.getNroCredencialParam()!=null&&String.valueOf(consultaIGS.getNroCredencialParam())!="null"?String.valueOf(consultaIGS.getNroCredencialParam()):"");
			row.addText(consultaIGS.getEstado()!=null?consultaIGS.getEstado():"");
			row.addText(consultaIGS.getCuilTitular()!=null?consultaIGS.getCuilTitular():"");
			row.addText(consultaIGS.getInte()!=null?String.valueOf(consultaIGS.getInte()):"");
			row.addText(consultaIGS.getApellido()!=null?consultaIGS.getApellido():"");
			row.addText(consultaIGS.getNombre()!=null?consultaIGS.getNombre():"");				
			row.addText(consultaIGS.getPlan()!=null?consultaIGS.getPlan():"");
			row.addText(consultaIGS.getNroCredencial()!=null?String.valueOf(consultaIGS.getNroCredencial()):"");
			row.addText(consultaIGS.getIp());
			row.addText(sdf.format(consultaIGS.getAltaFecha()));
			
			resultRows.add(row);
		}
	}
	
%>

<div class="search-results">
	<c:choose>
		<c:when test="<%= total != 1 %>">
			<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>
		</c:when>
		<c:otherwise>
			<%= LanguageUtil.format(pageContext, "showing-x-result", total) %>
		</c:otherwise>
	</c:choose>
	<liferay-util:include page="/html/portlet/autorizaciones/reportes/paginador_consultas_igs.jsp"></liferay-util:include>

</div>
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainerConsultasIGS %>" />
<br/>

<div class="search-results">
	<c:choose>
		<c:when test="<%= total != 1 %>">
			<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>
		</c:when>
		<c:otherwise>
			<%= LanguageUtil.format(pageContext, "showing-x-result", total) %>
		</c:otherwise>
	</c:choose>
</div>
