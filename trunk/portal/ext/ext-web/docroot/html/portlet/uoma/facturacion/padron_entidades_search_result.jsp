<%@ include file="/html/portlet/uoma/init.jsp" %>
<%-- <%@ page import="ar.com.ospim.padronentidades.action.PadronEntidadesUnificadoAction" %> --%>
<%-- <%@ page import="ar.com.ospim.global.beans.EntidadPadronUnificado" %>  --%>
<%@ page import="ar.com.ospim.padronentidades.action.PadronEntidadesUnificadoAfipAction" %>
<%@ page import="ar.com.ospim.global.beans.Empresa" %>

<script type="text/javascript">


</script>
<%


	PortletURL url = renderResponse.createRenderURL();		 				
	url.setWindowState(LiferayWindowState.EXCLUSIVE);		 				
	url.setParameter("struts_action","/uoma/buscar_padron_entidad_afip");
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
	List<Empresa> empresas=null;	
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
		empresas= (List<Empresa>)portletSession.getAttribute(PadronEntidadesUnificadoAfipAction.PADRON_EMPRESAS_AFIP,PortletSession.APPLICATION_SCOPE);
	}else{
		empresas= (List<Empresa>)request.getAttribute(PadronEntidadesUnificadoAfipAction.PADRON_EMPRESAS_AFIP);
	}
	//recupero coincidencias		
	
	//Seteo el total de la lista.
	int total = (Integer)request.getAttribute("total");
	 	searchContainer.setTotal(total);
	//Si existe una sola coincidencia la plancho en los campos del parent
	if(total==1){		
		Empresa empUnica=(Empresa) empresas.get(0);
		int idSeccional=empUnica.getIdSeccional();
		String cuitFinal = empUnica.getCuit() == null && cuit != null ? cuit : empUnica.getCuit();
		%>
			<script type="text/javascript">
				pasarParametrosAParentBusquedaPadrones<%=suf%>("<%=cuitFinal%>", 
						"<%=empUnica.getRazon_soc()!=null?empUnica.getRazon_soc().trim():""%>", 
						"<%=empUnica.getSucursal()!=null?empUnica.getSucursal():""%>", 
								"<%=idSeccional>0 && idSeccional!=9999?String.valueOf(idSeccional):empUnica.getSucursal().trim()%>",  
								"<%=empUnica.getImpGanancias()%>",
								"<%=empUnica.getImpIva()%>",
								"<%=empUnica.getMonotributo()%>",
								"<%=empUnica.getRegimen().getCodigoRegimen()%>",
								"<%=empUnica.getCBU()%>");
				
				<portlet:namespace />controlaTipoCliente();
				
				var auxIva = jQuery("#<portlet:namespace />imp_iva").val();
				if(auxIva == "null"){
					auxIva = "N/D";
				}
				var auxGan = jQuery("#<portlet:namespace />imp_ganancias").val()
				if(auxGan == "null"){
					auxGan = "N/D";
				}
				var auxMono = jQuery("#<portlet:namespace />monotributo").val();
				if(auxMono == "null"){
					auxMono = "N/D";
				}
				var auxReg = jQuery("#<portlet:namespace />codigo_regimen").val();
				if(auxReg == 0){
					auxReg = "N/D";
				}
				document.getElementById('lblempresaIVA').innerHTML = ' Condición IVA: ' + auxIva;
				document.getElementById('lblempresaGAN').innerHTML = ' Imp.Ganancias: ' + auxGan;
				document.getElementById('lblempresaMONO').innerHTML = ' Monotributo: ' + auxMono;
				document.getElementById('lblempresaREG').innerHTML = ' Cód. Régimen: ' + auxReg;
				
				
			</script>				
		<%
	//More de una coincidencia	
	}else {
			
	 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
		List resultRows = searchContainer.getResultRows();
	 	for (int i = 0; i < empresas.size(); i++) {
	 		Empresa empresa =  empresas.get(i);
			ResultRow row = new ResultRow(empresa.getCuit(),empresa.getDescripcion(), i);
			String cuitFinal = empresa.getCuit() == null && cuit != null ? cuit : empresa.getCuit();
			// Name and short description
			StringBuilder sb = new StringBuilder();
			sb.append("<a href='javascript:pasarParametrosAParentBusquedaPadrones" + suf + "(\"");
			sb.append(cuitFinal);
			sb.append("\",\"");
			sb.append(empresa.getRazon_soc());
			sb.append("\",\"");
			sb.append(empresa.getSucursal());
			sb.append("\",\"");
			sb.append(empresa.getIdSeccional());
			sb.append("\",\"");
			sb.append(empresa.getImpGanancias());
			sb.append("\",\"");
			sb.append(empresa.getImpIva());
			sb.append("\",\"");
			sb.append(empresa.getMonotributo());
			sb.append("\",\"");
			sb.append(empresa.getRegimen().getCodigoRegimen());
			sb.append("\",\"");
			sb.append(empresa.getCBU());
			sb.append("\")'>");			
			sb.append(cuitFinal);
			sb.append("</a>");
			row.addText(sb.toString());
			StringBuilder sb2 = new StringBuilder();
			sb2.append("<a href='javascript:pasarParametrosAParentBusquedaPadrones" + suf + "(\"");
			sb2.append(cuitFinal);
			sb2.append("\",\"");
			sb2.append(empresa.getRazon_soc());
			sb2.append("\",\"");
			sb2.append(empresa.getSucursal());
			sb2.append("\",\"");
			sb2.append(empresa.getIdSeccional());
			sb.append("\",\"");
			sb.append(empresa.getImpGanancias());
			sb.append("\",\"");
			sb.append(empresa.getImpIva());
			sb.append("\",\"");
			sb.append(empresa.getMonotributo());
			sb.append("\",\"");
			sb.append(empresa.getRegimen().getCodigoRegimen());
			sb.append("\",\"");
			sb.append(empresa.getCBU());
			sb2.append("\")'>");
			sb2.append(empresa.getDescripcion());
			sb2.append("</a>");
			row.addText(sb2.toString());
			StringBuilder sb3 = new StringBuilder();
			sb3.append("<a href='javascript:pasarParametrosAParentBusquedaPadrones" + suf + "(\"");
			sb3.append(cuitFinal);
			sb3.append("\",\"");
			sb3.append(empresa.getRazon_soc());
			sb3.append("\",\"");
			sb3.append(empresa.getSucursal());
			sb3.append("\",\"");
			sb3.append(empresa.getIdSeccional());
			sb.append("\",\"");
			sb.append(empresa.getImpGanancias());
			sb.append("\",\"");
			sb.append(empresa.getImpIva());
			sb.append("\",\"");
			sb.append(empresa.getMonotributo());
			sb.append("\",\"");
			sb.append(empresa.getRegimen().getCodigoRegimen());
			sb.append("\",\"");
			sb.append(empresa.getCBU());
			sb3.append("\")'>");
			sb3.append(empresa.getSucursal());
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
