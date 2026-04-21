<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.uoma.beans.Incidente" %>
<portlet:defineObjects/>

<%
	//Si debe mostrarse el btn de agregar afiliado
	String prefijo=ParamUtil.getString(request, "origen","");
	String view=ParamUtil.getString(request,"view");
	String checkbox=ParamUtil.getString(request,"checkbox");
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
	List<Afiliado> afiliadosList= (ArrayList<Afiliado>)renderRequest.getAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO);
	if(null!=checkbox && !checkbox.trim().equals("")){ //Viene de credenciales...
		portletSession.setAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO_CRED,afiliadosList,PortletSession.APPLICATION_SCOPE);
		renderRequest.getPortletSession().removeAttribute(WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION, PortletSession.APPLICATION_SCOPE);
	}
	PortletURL portletURL = renderResponse.createRenderURL();
	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("cuil");
	headerNames.add("inte");
	headerNames.add("apellido");
	headerNames.add("nombre");
	headerNames.add("documento");
	headerNames.add("parentesco");
	headerNames.add("seccional");
	headerNames.add("fecha-nacimiento");
	headerNames.add("baja-fecha");
	headerNames.add("choose");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
			LanguageUtil.get(pageContext, "no-afiliados-were-found"));

	if(null!=afiliadosList){

		//Seteo el total de la lista.
		int total = afiliadosList.size();
		if (total == 1){
			Afiliado afiliado = (Afiliado) afiliadosList.get(0);
			String fechaRecepcion =  null;
			int antecedentesSeleccion = (afiliado != null && afiliado.getTieneAntecedentesJudiciales() == 1) ? 1 : 0;
%>
<script type="text/javascript">

	<%if (afiliado != null && afiliado.getPrevencion() != null ){%>
	<%
        Incidente  incidente =  null;
        Date fecha =  null;
        fechaRecepcion =  null;
        if (afiliado.getIncidentes() != null){
            incidente = afiliado.getIncidentes().iterator().next();
            fecha = incidente.getFechaRecepcion();
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
            fechaRecepcion = sdf.format(fecha);

        }
    %>
	seleccionaAfiliado<%=prefijo%>('<%=afiliado.getCuil_titular()%>','<%=afiliado.getInte()%>','<%=afiliado.getDocumento_tipo()%>'
			,'<%= afiliado.getDocu_numero() %>',"<%= afiliado.getNombre().replaceAll("'","\\'")%>","<%= afiliado.getApellido().replaceAll("'","\\'")%>"
			,'<%= afiliado.getSeccional().getId() %>','<%= afiliado.getSeccional().getDescripcion() %>'
			,'<%= afiliado.getId_ospim() %>','<%= afiliado.getId_uoma() %>','<%= afiliado.getId_amtima()%>', '<%= afiliado.getBaja_fechaAsString()%>', '<%=afiliado.getNombrePlan()%>'
			,'<%= afiliado.getUltimo_plan() != null ? afiliado.getUltimo_plan().getId() : 0 %>'
			,'<%= afiliado.getAlta_fechaAsString()%>','<%= afiliado.getDiscapacitado()%>','<%=afiliado.getId_tercerizadora() != null ? afiliado.getId_tercerizadora() : "" %>','<%=afiliado.getDesc_tercerizadora() != null ? afiliado.getDesc_tercerizadora() : "" %>','<%=afiliado.getConReclamoPrestacional() ? "1" : "0" %>'
			,'<%= afiliado != null  && afiliado.getPrevencion() != null ? afiliado.getPrevencion().getNroSocio() : 0 %>'
			,'<%= afiliado != null  && afiliado.getPrevencion() != null ? afiliado.getPrevencion().getNroCredencial() : 0 %>'
			,'<%= afiliado.getIncidentes() != null ? fechaRecepcion : 0 %>'
			,'<%= antecedentesSeleccion %>');
	<%}else{%>
	seleccionaAfiliado<%=prefijo%>('<%=afiliado.getCuil_titular()%>','<%=afiliado.getInte()%>','<%=afiliado.getDocumento_tipo()%>'
			,'<%= afiliado.getDocu_numero() %>',"<%= afiliado.getNombre().replaceAll("'","\\'")%>","<%= afiliado.getApellido().replaceAll("'","\\'")%>"
			,'<%= afiliado.getSeccional().getId() %>','<%= afiliado.getSeccional().getDescripcion() %>'
			,'<%= afiliado.getId_ospim() %>','<%= afiliado.getId_uoma() %>','<%= afiliado.getId_amtima()%>', '<%= afiliado.getBaja_fechaAsString()%>', '<%=afiliado.getNombrePlan()%>'
			,'<%= afiliado.getUltimo_plan() != null ? afiliado.getUltimo_plan().getId() : 0 %>'
			,'<%= afiliado.getAlta_fechaAsString()%>','<%= afiliado.getDiscapacitado()%>','<%=afiliado.getId_tercerizadora() != null ? afiliado.getId_tercerizadora() : "" %>','<%=afiliado.getDesc_tercerizadora() != null ? afiliado.getDesc_tercerizadora() : "" %>','<%=afiliado.getConReclamoPrestacional() ? "1" : "0" %>'
			,'<%= afiliado != null  && afiliado.getPrevencion() != null ? afiliado.getPrevencion().getNroSocio() : 0 %>'
			,'<%= afiliado != null  && afiliado.getPrevencion() != null ? afiliado.getPrevencion().getNroCredencial() : 0 %>'
			,'<%= afiliado.getIncidentes() != null ? fechaRecepcion : 0 %>'
			,'<%= antecedentesSeleccion %>');
	<%}%>
</script>
<%
		} else {

			searchContainer.setTotal(total);
			//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
			List resultRows = searchContainer.getResultRows();
			for (int i = 0; i < afiliadosList.size(); i++) {
				Afiliado afiliado = (Afiliado) afiliadosList.get(i);
				int antecedentesSeleccion = (afiliado != null && afiliado.getTieneAntecedentesJudiciales() == 1) ? 1 : 0;
				ResultRow row = new ResultRow(afiliado,afiliado.getCuil_titular(), i);
				boolean tieneAntecedentes = (afiliado != null && afiliado.getTieneAntecedentesJudiciales() == 1);
				if (tieneAntecedentes) {
					row.setClassName("afiliado-antecedentes");
				}
				row.addText(afiliado.getCuil_titularMasked());
				row.addText(afiliado.getInteAsString());
				row.addText(afiliado.getApellido());
				row.addText(afiliado.getNombre());
				row.addText(afiliado.getDocumento_tipo() + " " + afiliado.getDocu_numero());
				row.addText(afiliado.getParentesco());
				row.addText(afiliado.getSeccional().getDescripcion()!=null?afiliado.getSeccional().getDescripcion():"Sin Especificar");
				row.addText(afiliado.getNaci_fechaAsString());
				row.addText(afiliado.getBaja_fechaAsString());
				StringBuilder sb= new StringBuilder();
				if(null!=checkbox && !checkbox.trim().equals("")){
					sb.append("<input type=\"checkbox\"");
					sb.append("name=\"");
					sb.append(afiliado.getCuil_titular()+"|"+afiliado.getInte());
					sb.append("\" id=\"");
					sb.append(afiliado.getCuil_titular()+"|"+afiliado.getInte());
					sb.append("\" value=\"");
					sb.append(afiliado.getCuil_titular()+"|"+afiliado.getInte());
					sb.append("\"/>");
					row.addText(sb.toString());
				}else{
					if(null==view || !view.trim().equals("true")){
						sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
						sb.append(themeDisplay.getPathThemeImages());
						sb.append("/portlet/edit_guest.png\" onClick=\"javascript:seleccionaAfiliado");
						sb.append(prefijo+"('");
						sb.append(afiliado.getCuil_titular());
						sb.append("','");
						sb.append(afiliado.getInte());
						sb.append("','");
						sb.append(afiliado.getDocumento_tipo());
						sb.append("','");
						sb.append(afiliado.getDocu_numero());
						sb.append("','");
						sb.append(afiliado.getNombre().replaceAll("'","\\\\'"));
						sb.append("','");
						sb.append(afiliado.getApellido().replaceAll("'","\\\\'"));
						sb.append("','");
						sb.append(afiliado.getSeccional().getId());
						sb.append("','");
						sb.append(afiliado.getSeccional().getDescripcion());
						sb.append("','");
						sb.append(afiliado.getId_ospim());
						sb.append("','");
						sb.append(afiliado.getId_uoma());
						sb.append("','");
						sb.append(afiliado.getId_amtima());
						sb.append("','");
						sb.append(afiliado.getBaja_fechaAsString());
						sb.append("','");
						sb.append(afiliado.getNombrePlan());
						sb.append("','");
						sb.append(afiliado.getUltimo_plan() != null ? afiliado.getUltimo_plan().getId() : 0);
						sb.append("','");
						sb.append(afiliado.getAlta_fechaAsString());
						sb.append("','");
						sb.append(afiliado.getDiscapacitado());
						sb.append("','");
						sb.append(afiliado.getId_tercerizadora() != null ? afiliado.getId_tercerizadora() : "");
						sb.append("','");
						sb.append(afiliado.getDesc_tercerizadora() != null ? afiliado.getDesc_tercerizadora() : "");
						sb.append("','");
						if (afiliado.getConReclamoPrestacional()){
							sb.append("1");
						}else{
							sb.append("0");
						}
						if (afiliado != null && afiliado.getPrevencion() != null ){
							sb.append("','");
							sb.append(afiliado.getPrevencion() != null ? afiliado.getPrevencion().getNroSocio() : 0);
							sb.append("','");
							sb.append(afiliado.getPrevencion() != null ? afiliado.getPrevencion().getNroCredencial() : 0);
							sb.append("','");
							Incidente  incidente =  null;
							Date fecha =  null;
							String fechaRecepcion =  null;
							if (afiliado.getIncidentes() != null){
								incidente = afiliado.getIncidentes().iterator().next();
								fecha = incidente.getFechaRecepcion();
								SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
								fechaRecepcion = sdf.format(fecha);
								sb.append(fechaRecepcion);
							}else{
								sb.append("0");
							}
						}else{
							sb.append("','");
							sb.append("0");
							sb.append("','");
							sb.append("0");
							sb.append("','");
							sb.append("0");
						}
						sb.append("','");
						sb.append(antecedentesSeleccion);
						sb.append("');\" />");
						row.addText(sb.toString());
					}
				}
				resultRows.add(row);
			}
		}
	}

%>

<style type="text/css">
	tr.afiliado-antecedentes td {
		background: #ff4d4d !important;
		color: #ffffff !important;
	}

	tr.afiliado-antecedentes td a,
	tr.afiliado-antecedentes td a:visited,
	tr.afiliado-antecedentes td a:hover,
	tr.afiliado-antecedentes td a:active {
		color: #ffffff !important;
		font-weight: bold;
	}
</style>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%if(null!=checkbox && !checkbox.trim().equals("")){ %>
<div align="right">
	<input id="<portlet:namespace />seleccionarAfiliado" value="<liferay-ui:message key="choose"/>" title="<liferay-ui:message key="seleccionar" />" type="button" onClick="javascript:<portlet:namespace />seleccionarAfiliados();"/>
</div>
<%} %>

<script type="text/javascript">
	function <portlet:namespace />seleccionarAfiliados(){
		var inputs=jQuery('input:checkbox');
		var aux=serializaInputs(inputs);
		<portlet:namespace />pedirCredencial(encodeURI(aux));
	}

	function serializaInputs(inputText){
		var i=0;
		var text='';
		for(i=0;i<inputText.length;i++){
			if(inputText[i].checked){
				text=text+'-'+inputText[i].id;
			}
		}
		return "&credenciales="+text;
	}
</script>