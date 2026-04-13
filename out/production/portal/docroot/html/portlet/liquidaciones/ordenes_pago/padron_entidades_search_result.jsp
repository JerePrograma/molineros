<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ page import="ar.com.ospim.padronentidades.action.PadronEntidadesUnificadoAction" %>
<%@ page import="ar.com.ospim.global.beans.EntidadPadronUnificado" %>

<script type="text/javascript">


</script>
<%


	PortletURL url = renderResponse.createRenderURL();		 				
	url.setWindowState(LiferayWindowState.EXCLUSIVE);		 				
	url.setParameter("struts_action","/liquidaciones/buscar_padron_entidad");
	String cuitPrm = (String) request.getAttribute("cuit_entidad");
	if (cuitPrm != null) {
		url.setParameter("cuit_entidad", cuitPrm);
	}
	String ent = (String) request.getAttribute("entidad");
	if (ent != null){
		url.setParameter("entidad", ent);
	}
	String sucu = (String) request.getAttribute("sucursal");
	if (sucu != null){ 
		url.setParameter("sucursal", sucu);
	}
	String idPrestador =  (String)request.getAttribute("id_prestador");
	if (idPrestador != null){
		url.setParameter("id_prestador", idPrestador);
	}
	String soloIngresos = (String)request.getAttribute("soloIngresos");
	if (soloIngresos != null){
		url.setParameter("soloIngresos", soloIngresos);
	}
	String suf = (String)request.getAttribute("suf");	
	if (suf != null){
		url.setParameter("suf", suf);
	} else {
		suf = "";
	}	
	
	//obtengo lista de session
	PortletSession ps= renderRequest.getPortletSession();	
	List<EntidadPadronUnificado> entidades=null;	
	String cuit=(String)renderRequest.getAttribute("cuit_entidad");
	PortletURL portletURL = renderResponse.createRenderURL();				
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("cuit");
	headerNames.add("empresa");
	headerNames.add("sucursal");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, url
				, headerNames,
				LanguageUtil.get(pageContext, "no-empresas-were-found"));
	
	if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {
		entidades= (List<EntidadPadronUnificado>)portletSession.getAttribute(PadronEntidadesUnificadoAction.PADRON_ENTIDADES,PortletSession.APPLICATION_SCOPE);
	}else{
		entidades= (List<EntidadPadronUnificado>)request.getAttribute(PadronEntidadesUnificadoAction.PADRON_ENTIDADES);
	}
	//recupero coincidencias		
	
	//Seteo el total de la lista.
	int total = (Integer)request.getAttribute("total");
	 	searchContainer.setTotal(total);
	//Si existe una sola coincidencia la plancho en los campos del parent
	if(total==0 && cuit!=null && cuit.length()==11){
		if(!CuilUtils.validarNum(cuit)){
		   List resultRows = searchContainer.getResultRows();
		   ResultRow row = new ResultRow("","",0);
		   StringBuilder sb = new StringBuilder();
		   sb.append("CUIT/CUIL inválido");
		   row.addText(sb.toString());
		   resultRows.add(row);
		}
	}
	if(total==1){		
		EntidadPadronUnificado emplUnica=(EntidadPadronUnificado) entidades.get(0);
		int idSeccional=emplUnica.getIdSeccional();
		String cuitFinal = emplUnica.getCuit() == null && cuit != null ? cuit : emplUnica.getCuit();
		%>
			<script type="text/javascript">
				pasarParametrosAParentBusquedaPadrones<%=suf%>("<%=cuitFinal%>", "<%=emplUnica.getDescripcion()!=null?emplUnica.getDescripcion().trim():""%>", "<%=idSeccional>0 && idSeccional!=9999?String.valueOf(idSeccional):emplUnica.getSucursal().trim()%>",  "<%=emplUnica.getIdSeccional()%>");
			</script>				
		<%
	//More de una coincidencia	
	}else {
	 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
		List resultRows = searchContainer.getResultRows();
	 	for (int i = 0; i < entidades.size(); i++) {
	 		EntidadPadronUnificado entidad =  entidades.get(i);
			ResultRow row = new ResultRow(entidad.getCuit(),entidad.getDescripcion(), i);
			String cuitFinal = entidad.getCuit() == null && cuit != null ? cuit : entidad.getCuit();
			// Name and short description
			StringBuilder sb = new StringBuilder();
			sb.append("<a href='javascript:pasarParametrosAParentBusquedaPadrones" + suf + "(\"");
			sb.append(cuitFinal);
			sb.append("\",\"");
			sb.append(entidad.getDescripcion());
			sb.append("\",\"");
			sb.append(entidad.getSucursal());
			sb.append("\",\"");
			sb.append(entidad.getIdSeccional());
			sb.append("\")'>");			
			sb.append(cuitFinal);
			sb.append("</a>");
			row.addText(sb.toString());
			StringBuilder sb2 = new StringBuilder();
			sb2.append("<a href='javascript:pasarParametrosAParentBusquedaPadrones" + suf + "(\"");
			sb2.append(cuitFinal);
			sb2.append("\",\"");
			sb2.append(entidad.getDescripcion());
			sb2.append("\",\"");
			sb2.append(entidad.getSucursal());
			sb2.append("\",\"");
			sb2.append(entidad.getIdSeccional());
			sb2.append("\")'>");
			sb2.append(entidad.getDescripcion());
			sb2.append("</a>");
			row.addText(sb2.toString());
			StringBuilder sb3 = new StringBuilder();
			sb3.append("<a href='javascript:pasarParametrosAParentBusquedaPadrones" + suf + "(\"");
			sb3.append(cuitFinal);
			sb3.append("\",\"");
			sb3.append(entidad.getDescripcion());
			sb3.append("\",\"");
			sb3.append(entidad.getSucursal());
			sb3.append("\",\"");
			sb3.append(entidad.getIdSeccional());
			sb3.append("\")'>");
			sb3.append(entidad.getSucursal());
			sb3.append("</a>");
			row.addText(sb3.toString());
			resultRows.add(row);
	 	}
		%>
		<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
<%
	}
	
%>

<%if (total >1){ %>
<%@ include file="/html/portlet/utils/paginator/paginator.jsp" %>
<%} %>
<script type="text/javascript">
 function <portlet:namespace />paginar(cur){
	buscarEnPopUp<%=suf%>(cur)
 }
</script>
