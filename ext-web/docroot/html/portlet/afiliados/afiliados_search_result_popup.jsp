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

	StringBuilder estilosColoresAntecedentes = new StringBuilder();

	java.util.Set<String> clasesColoresAntecedentes = new java.util.HashSet<String>();
	
	if(null!=afiliadosList){

		//Seteo el total de la lista.
		int total = afiliadosList.size();
		if (total == 1){
			Afiliado afiliado = (Afiliado) afiliadosList.get(0);
			String fechaRecepcion =  null;
			int antecedentesSeleccion = (afiliado != null && afiliado.getTieneAntecedentesJudiciales() == 1) ? 1 : 0;
			String colorAntecedenteSeleccion = afiliado != null && afiliado.getColorAntecedenteJudicial() != null ? afiliado.getColorAntecedenteJudicial() : "";
			String codigoAntecedenteSeleccion = afiliado != null && afiliado.getCodigoAntecedenteJudicial() != null ? afiliado.getCodigoAntecedenteJudicial() : "";
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
			,'<%= antecedentesSeleccion %>','<%= colorAntecedenteSeleccion %>','<%= codigoAntecedenteSeleccion %>');
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
			,'<%= antecedentesSeleccion %>','<%= colorAntecedenteSeleccion %>','<%= codigoAntecedenteSeleccion %>');
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

				String colorAntecedente = afiliado != null ? afiliado.getColorAntecedenteJudicial() : null;

					if (colorAntecedente != null && !colorAntecedente.trim().equals("")) {
					    colorAntecedente = colorAntecedente.trim();

					    if (colorAntecedente.matches("^#[0-9a-fA-F]{6}$")) {

					        String codigoColor = colorAntecedente.substring(1);
					        String claseColor = "afiliado-color-" + codigoColor;
					        row.setClassName(claseColor);

					        if (!clasesColoresAntecedentes.contains(claseColor)) {
					            clasesColoresAntecedentes.add(claseColor);

					            int rojo = Integer.parseInt( codigoColor.substring(0, 2),16);
					            int verde = Integer.parseInt(codigoColor.substring(2, 4),16);
					            int azul = Integer.parseInt(codigoColor.substring(4, 6),16);
					            int luminosidad =(rojo * 299 + verde * 587 + azul * 114) / 1000;

					            String colorTexto = luminosidad < 150 ? "#ffffff" : "#333333";

					            estilosColoresAntecedentes
					                .append("tr.")
					                .append(claseColor)
					                .append(" td {")
					                .append("background:")
					                .append(colorAntecedente)
					                .append(" !important;")
					                .append("color:")
					                .append(colorTexto)
					                .append(" !important;")
					                .append("}");

					            estilosColoresAntecedentes
					                .append("tr.")
					                .append(claseColor)
					                .append(" td a {")
					                .append("color:")
					                .append(colorTexto)
					                .append(" !important;")
					                .append("font-weight:bold;")
					                .append("}");
					        }
					    }
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
						sb.append("','");
						sb.append(afiliado.getColorAntecedenteJudicial() != null ? afiliado.getColorAntecedenteJudicial() : "");				
						
						sb.append("','");
						sb.append(afiliado.getCodigoAntecedenteJudicial() != null ? afiliado.getCodigoAntecedenteJudicial() : "");
						sb.append("');\" />");
						
						row.addText(sb.toString());
					}
				}
				resultRows.add(row);
			}
		}
	}

%>


<%
if(estilosColoresAntecedentes.length() > 0){
%>

<style type="text/css">
    <%= estilosColoresAntecedentes.toString() %>
</style>

<%
}
%>

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