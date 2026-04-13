<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiAportes"%>

<portlet:defineObjects/>
	
	<%
	PortletURL portletURL = renderResponse.createRenderURL();	
	List<String> headerNames = new ArrayList<String>();
	
	headerNames.add("Tipo Aporte");
	headerNames.add("Nro. Socio");
	headerNames.add("Fecha Inicio");
	headerNames.add("Fecha Fin");
	headerNames.add("Fecha Modifica");
	headerNames.add("Usuario Modifica");
		
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
			LanguageUtil.get(pageContext, "no-aporte-were-found"));
	
	List<AfiAportes> aportes = (List<AfiAportes>) request.getAttribute("IdsSocio"); 
		
	if(null!=aportes){
		 	
	//Seteo el total de la lista.
	 	int total =  0 ; // aportes.size(); //
	 //	searchContainer.setTotal(total);
	 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	 	List resultRows = searchContainer.getResultRows();
	 	for (int i = 0; i < aportes.size(); i++) {
	 		AfiAportes afiAp = (AfiAportes) aportes.get(i);
	 		ResultRow row = new ResultRow(aportes, String.valueOf(afiAp.getTipoIdSocio()), i);
			PortletURL rowURL = renderResponse.createRenderURL();	
			
	 		if((afiAp.getTipoIdSocio()!=null && 
	 			(afiAp.getTipoIdSocio().equalsIgnoreCase("O") ||
	 			afiAp.getTipoIdSocio().equalsIgnoreCase("U") ||
	 			afiAp.getTipoIdSocio().equalsIgnoreCase("A") ) ) &&
	 			afiAp.getIdSocio() > 0){
	 			total++;
				row.addText(afiAp.getAporte().getDescripcion() ,rowURL);
				row.addText(String.valueOf(afiAp.getIdSocio()),rowURL);
				row.addText(afiAp.getFechaIngre()!=null?sdf.format(afiAp.getFechaIngre()):"",rowURL);
				row.addText(afiAp.getFechaEgre()!=null?sdf.format(afiAp.getFechaEgre()):"",rowURL);
				row.addText(afiAp.getModi_fecha()!=null?sdf.format(afiAp.getModi_fecha()):"",rowURL);
				row.addText(afiAp.getModi_usr()!=null?afiAp.getModi_usr():"",rowURL);
		 		
				resultRows.add(row);
	 		}else{
	 			total++;
				row.addText(afiAp.getAporte().getDescripcion() ,rowURL);
				row.addText(String.valueOf(afiAp.getIdSocio()),rowURL);
				row.addText(afiAp.getFechaIngre()!=null?sdf.format(afiAp.getFechaIngre()):"",rowURL);
				row.addText(afiAp.getFechaEgre()!=null?sdf.format(afiAp.getFechaEgre()):"",rowURL);
				row.addText(afiAp.getModi_fecha()!=null?sdf.format(afiAp.getModi_fecha()):"",rowURL);
				row.addText(afiAp.getModi_usr()!=null?afiAp.getModi_usr():"",rowURL);
				resultRows.add(row); 
	 		} 
			
			
	 	}
	 	searchContainer.setTotal(total);
	}	
	%>
	
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		

	