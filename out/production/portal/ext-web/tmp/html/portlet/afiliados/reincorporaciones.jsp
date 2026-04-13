<%@page import="ar.com.ospim.afiliados.services.PlanServiceUtil"%>
<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
				//TODO DAR OPCIÓN DE AÑADIR DOCUMENTACIÓN ADJUNTA				
				String view=request.getParameter("view");
	
				String cuil_titular=request.getParameter("cuil_titular");
				String inte=request.getParameter("inte");
			
				List<Afiliado> afiliadosList= BusquedaAfiliadoServiceUtil.getBusquedaGrupoFliar(cuil_titular);
				
				AfiPlan afPlan = PlanServiceUtil.getInstance().buscarUltimoPlanAportes(cuil_titular);   
						
				PortletURL portletURL = renderResponse.createRenderURL();
				
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
				
		 		List<String> headerNames = new ArrayList<String>();
		 		
		 		headerNames.add("apellido");
				headerNames.add("nombre");
				headerNames.add("paren");
				headerNames.add("process-fecha");
				headerNames.add("vigen-fecha");
				headerNames.add("baja-fecha");
				headerNames.add("recuperar");							   			
				
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-afiliados-were-found"));
			
				Calendar fechaHoy= CalendarFactoryUtil.getCalendar();
				fechaHoy.setTime(new Date());
				String fecha_ingre_titular = null;
				
				if (null!=afiliadosList) {				 	
	 				//Seteo el total de la lista.
				 	int total = afiliadosList.size();
				 	searchContainer.setTotal(total);
				 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < afiliadosList.size(); i++) {
				 		Afiliado afiliado = (Afiliado) afiliadosList.get(i);
	 					ResultRow row = new ResultRow(afiliado,afiliado.getCuil_titular(), i);
		 				PortletURL rowURL = renderResponse.createRenderURL();		 				
		 				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		 				row.addText(afiliado.getApellido());
		 				row.addText(afiliado.getNombre());
						row.addText(afiliado.getParentesco());
						row.addText(afiliado.getIngre_fechaAsString());
						row.addText(afiliado.getVigen_fechaAsString());
						row.addText(afiliado.getBaja_fechaAsString());						
						if (afiliado.esTitular()) {
							fechaHoy.setTime(afiliado.getVigen_fecha());
							fecha_ingre_titular = afiliado.getVigen_fechaAsString();
						}						
						StringBuffer sb = new StringBuffer();
						if (afiliado.esBaja()) {
							sb.append("<input type=\"checkbox\""); 
							sb.append("name=\"");
							sb.append(afiliado.getInte());
							sb.append("\" id=\"");
							sb.append(afiliado.getCuil_titular()+"|"+afiliado.getInte());
							sb.append("\" value=\"");
							sb.append(afiliado.getCuil_titular()+"|"+afiliado.getInte());
							/* if (afiliado.esTitular()) {
								sb.append("\" onclick=\"javascript:seleccionDinamicaAfiliados('");
								sb.append(afiliado.getInte());
								sb.append("');");
							} */
							/* if (!afiliado.esTitular()) {
								sb.append("\" onclick=\"javascript:validaSeleccionTitular('");
								sb.append(afiliado.getInte());
								sb.append("');");
							} */
							sb.append("\" onclick=\"javascript:verificaSiEstaVigente('");
							sb.append(afiliado.getCuil_titular());
							sb.append("','");
							sb.append(afiliado.getInte());
							sb.append("');");
							
							/* sb.append("\" checked=\"false"); */
							/* sb.append("\" checked=\"true"); */
							sb.append("\"/>");
							
						}else {
							sb.append(" - ");
						}
						row.addText(sb.toString());						
			 			resultRows.add(row);
				 	}
	 			}
 		%>
	<div>
		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	</div>
	<table style="center:50%; border-collapse: separate; border-spacing: 5px;" >
		<tr>
			<td colspan="6">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2">
				<liferay-ui:input-date dayParam="fechaDia"
				dayValue="<%= fechaHoy.get(Calendar.DATE)%>" monthParam="fechaMes"
				monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"
				yearParam="fechaAnio" yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 120 %>"
				yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 120 %>"
				firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
				disabled="<%= false %>"
				 />
			</td>
			<td>&nbsp;&nbsp;</td>
			<td>¿Desea recuperar &uacute;ltimo plan:&nbsp;</td>
			<td><%=afPlan.getPlan().getDescripcion()%>&nbsp; ? &nbsp;</td>
			<td>
				<input type="radio" name="<portlet:namespace />recupera_planes" value="true" checked="checked">SI &nbsp;
				<input type="radio" name="<portlet:namespace />recupera_planes" value="false">NO
			</td>
		</tr>
		<tr>
			<td><label>N° Correspondencia:</label></td>
			<td><input id="<portlet:namespace />numero_correspondencia"
			name="<portlet:namespace />numero_correspondencia" size="10" maxlength="10"
			type="text" onkeydown="allowOnlyDigits(event);"
			value="" /></td>
			<td colspan="3">&nbsp;&nbsp;</td>
			<td>
				<div align="center" id="<portlet:namespace />verificarInfo">
					<input type="button" value="<liferay-ui:message key="recuperar" />" onClick="<portlet:namespace />seleccionarAfiliados();" />
				</div>				
			</td>
		</tr>
	</table>
	
	<div align="center" id="<portlet:namespace />buscandoDocumentacion">
	<table style="align:center;">
			<tr>
				<td align="center">					
					<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
				</td>
			</tr>
		</table>		
	</div>
	
<div id="<portlet:namespace/>resultRecuperaDiv">
</div>
<script>

	jQuery('#<portlet:namespace />buscandoDocumentacion').hide();
		
	function validaSeleccionTitular(name) {
		var inputs=jQuery('input:checkbox');
		var iTitular = obtieneIndiceTitular();
		var i=obtieneIndiceName(name);
		if (!inputs[iTitular].checked){			
				inputs[i].checked = false;
		}		
	}

	function obtieneIndiceTitular(){
		var inputs=jQuery('input:checkbox');
		var i=0;
		for(i=0;i<inputs.length;i++){			
			if(0 == inputs[i].name){				
				return i;				
			}
		}
		return i;
	}

	function obtieneIndiceName(name){
		var inputs=jQuery('input:checkbox');
		var i=0;
		for(i=0;i<inputs.length;i++){			
			if(name == inputs[i].name){				
				return i;				
			}
		}
		return i;
	}
	
	function seleccionDinamicaAfiliados(name) {
		var inputs=jQuery('input:checkbox');
		var iTitular = obtieneIndiceTitular();
		var i=0;
		if(!inputs[iTitular].checked){
			for(i=0;i<inputs.length;i++){
				if (i != iTitular){
					inputs[i].checked = false;
				}
			}
		}
		else if(inputs[iTitular].checked){
			for(i=0;i<inputs.length;i++){
				if (i != iTitular){
					inputs[i].checked = true;
				}
			}
		}	
	}
	
	function <portlet:namespace />seleccionarAfiliados(){
		var inputs=jQuery('input:checkbox');
		
		var iTitular = obtieneIndiceTitular();
		if (!inputs[iTitular].checked){			
			alert("Debe seleccionar por lo menos un integrante");
			return false;
		}
		
		var aux=serializaInputs(inputs);
		<portlet:namespace />ejecutarReincorporaciones(aux);						
	}
	
	function serializaInputs(inputText){
		var i=0;
		var text='';
		for(i=0;i<inputText.length;i++){
			if(inputText[i].checked){
				text=text+'-'+inputText[i].id;
			}
		}
		return text;
	}
	
	function <portlet:namespace />ejecutarReincorporaciones(inputs) {
		jQuery('#<portlet:namespace />buscandoDocumentacion').show();
		var desea_recuperar_planes = null;			
		var diaIngreso = jQuery('#<portlet:namespace />fechaDia').val();
		var mesIngreso = parseInt(jQuery('#<portlet:namespace />fechaMes').val())+1;
		var anioIngreso = jQuery('#<portlet:namespace />fechaAnio').val();				
		if (parseInt(diaIngreso) < 10) {
			diaIngreso = '0'+String(diaIngreso);
		}
		if (parseInt(mesIngreso) < 10) {
			mesIngreso = '0'+String(mesIngreso);
		}
		/* var vigen_fecha = String(diaIngreso) + "/" + String(mesIngreso) + "/"+ String(anioIngreso);	 */
		var vigen_fecha = String(pad(diaIngreso,2)) + String(pad(mesIngreso,2)) + String(anioIngreso);	
		var fecha_ingre_titular = '<%=fecha_ingre_titular == null? "" : fecha_ingre_titular%>';		
		var fechaIni = vigen_fecha.split('/');
		var fechaFin = fecha_ingre_titular.split('/');
/* 		var fechainicial = fechaIni[2] + fechaIni[1] + fechaIni[0];
		var fechafinal = fechaFin[2] + fechaFin[1] + fechaFin[0]; */
		var fechainicial=new Date(fechaIni[2],(fechaIni[1]-1),fechaIni[0]); 
		var fechafinal=new Date(fechaFin[2],(fechaFin[1]-1),fechaFin[0]);
		
		var nro_correspondencia=jQuery('#<portlet:namespace />numero_correspondencia').val();
		
		/* evitar que quieran reincorporar, por anterior a la ultima fecha vigencia del titular. */
		if (fechafinal > fechainicial) { 
			alert('La fecha de reincorporacion no debe ser anterior a la última fecha de vigencia');
			jQuery('#<portlet:namespace />buscandoDocumentacion').hide();
			return false;
		}
		/* || trim(nro_correspondencia) == "0" */
		if ( trim(nro_correspondencia).length == 0 ){
			alert("Debe ingresar un numero de correspondencia");
			jQuery("#<portlet:namespace />numero_correspondencia").focus();
			return false;
		}
		
		
/* 		desea_recuperar_planes = jQuery('#<portlet:namespace />recupera_planes').val(); */
 		desea_recuperar_planes = jQuery("input[name='<portlet:namespace />recupera_planes']:checked").val();
 		
/* 		if (parseInt(fechainicial) != parseInt(fechafinal)) {
			desea_recuperar_planes = confirm("<liferay-ui:message key='desea-recuperar-planes'/>");
		} else {
			desea_recuperar_planes = true;
		}  */
		<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/ejecuta_reincorporaciones'+inputs+'&vigen_fecha='+vigen_fecha+'&desea_recuperar_planes='+desea_recuperar_planes;
		url += "&cuil_titular=<%=request.getParameter("cuil_titular")%>";
		url += "&inte=<%=request.getParameter("inte")%>";
		url += "&numero_correspondencia="+nro_correspondencia; --%>
		/* window.location = url;  */

		var cuil_titular_p = '<%=request.getParameter("cuil_titular")%>';
		var inte_p = '<%=request.getParameter("inte")%>';
		var xportletUrl = '/afiliados/ejecuta_reincorporaciones';
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="reincorporar" value="__inputs"/>'+
		'<liferay-portlet:param name="cuil_titular" value="__cuil_titu"/>'+
		'<liferay-portlet:param name="integ" value="__inte"/>'+
		'<liferay-portlet:param name="desea_recuperar_planes" value="__desea_recuperar_planes"/>'+
		'<liferay-portlet:param name="vigen_fecha" value="__vigen_fecha"/>'+
		'<liferay-portlet:param name="numero_correspondencia" value="__numero_correspondencia"/>'+
	    '</liferay-portlet:renderURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
  	    url = url.replace("__inputs", encodeURI(inputs));
	    url = url.replace("__cuil_titu",cuil_titular_p);
	    url = url.replace("__inte", inte_p);
	    url = url.replace("__desea_recuperar_planes",desea_recuperar_planes); 
	    url = url.replace("__vigen_fecha",vigen_fecha);
	    url = url.replace("__numero_correspondencia",nro_correspondencia); 
	    
	    /* window.location = url; */
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);

	}
	
	function verificaSiEstaVigente(cuil,inte){
	 
	 var chkbox = cuil+"|"+inte	
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_vigente&cuil='+cuil+'&inte='+encodeURI(inte);		 
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				/* if(obj.validado=="1"){
					alert("<liferay-ui:message key='cuil-invalido'/>");
				}else */ if(obj.validado=="1"){
					alert("<liferay-ui:message key='cuil-titular-existente'/>");
					document.getElementById(chkbox).checked=false; 
				} 					
			}
		}); 
	}
	function pad (n, length) {
	    var  n = n.toString();
	    while(n.length < length)
	         n = "0" + n;
	    return n;
	}
	
</script>