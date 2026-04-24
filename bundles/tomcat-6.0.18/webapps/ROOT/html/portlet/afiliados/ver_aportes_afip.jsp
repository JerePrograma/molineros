<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%



	HashMap<Integer, Boolean> boletas= (HashMap<Integer, Boolean>)portletSession.getAttribute(WebKeysAfiliados.TBOLETA_SESSION,
			PortletSession.APPLICATION_SCOPE);
	boolean cuota_amtima=false;
	boolean cuota_usufructo=false;
	boolean art_46=false;
	boolean cuota_social_uoma=false;
	boolean aporte_solidario_uoma=false;
	boolean aporte_afip_ospim=true;
	
	if ( boletas!=null && boletas.size()>0 ) {
		cuota_amtima=boletas.get(1);
		cuota_usufructo=boletas.get(2);
		art_46=boletas.get(3);
		cuota_social_uoma=boletas.get(4);
		aporte_solidario_uoma=boletas.get(5);
		aporte_afip_ospim=boletas.get(6);
		} 
	
	PortletURL portletURL = renderResponse.createRenderURL();				
	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");
	String cuil_titular=request.getParameter("cuil_titular");
	String paramDesdeMesAnio=request.getParameter("periodoDesdeMesAnio");
	String paramMes=null;
	String paramAnio=null;			
	String[] mesAnio=null;
		if(null!=paramDesdeMesAnio && paramDesdeMesAnio.indexOf("_")!=-1){
		mesAnio=paramDesdeMesAnio.split("_");
	    paramMes=mesAnio[0];
		paramAnio=mesAnio[1];					
		}
	Calendar periodoDesde = new GregorianCalendar(); 		
	periodoDesde.add(Calendar.YEAR,-1);
	int anioDesde=periodoDesde.get(Calendar.YEAR);
%>

	<table>
 		<tr>
 			<td><legend><liferay-ui:message key="periodo-desde" />:&nbsp;</legend></td>
			<td><liferay-ui:input-date
								dayParam="periodoDesdeDia"
								dayNullable="<%= true %>" 
								dayValue="01"
								monthAndYearParam="periodoDesdeMesAnio"
								monthValue="<%=paramMes!=null?Integer.parseInt(paramMes):0%>"
								monthAndYearNullable="<%= true %>"
								yearValue="<%=paramAnio!=null?Integer.parseInt(paramAnio):anioDesde%>"							
								yearRangeStart="<%= periodoDesde.get(Calendar.YEAR) - 40 %>"
								yearRangeEnd="<%= periodoDesde.get(Calendar.YEAR) %>"
								firstDayOfWeek="<%= periodoDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" /></td>
			<td><input type="button" value="<liferay-ui:message key="search" />" onClick="javascript: setearMeses();" /></td>
			<%if(seccionalFijada==0){%>
		    <td><input type="button" value="<liferay-ui:message key="reporte" />" onClick="javascript: exportarExcel();" /></td>
		    <td><legend><liferay-ui:message key="solo-derivacion" />:&nbsp;</legend></td>
		    <td><input type="checkbox" id="<portlet:namespace />solo_derivacion" name="<portlet:namespace />solo_derivacion" checked="checked"/></td>
		    <%}%>
		</tr>
	</table>
	
	<form onclick="filtrarBoletas(this);">
	  	<fieldset class="block-labels">
	  	<legend><liferay-ui:message key="tipo-boleta"/></legend>
			<table>
			  	<tr>
				    <td><legend><liferay-ui:message key="cuota-amtima" />:&nbsp;</legend></td>
				    <td><input type="checkbox" id="<portlet:namespace />cuota_amtima" name="<portlet:namespace />cuota_amtima" <%=cuota_amtima?"checked=\"checked\"":"" %>/></td>
				    <td><legend><liferay-ui:message key="cuota-usufructo"/>:&nbsp;</legend></td>
				    <td><input type="checkbox" id="<portlet:namespace />cuota_usufructo" name="<portlet:namespace />cuota_usufructo"<%=cuota_usufructo?"checked=\"checked\"":"" %> /></td>
				    <td><legend><liferay-ui:message key="art-46"/>:&nbsp;</legend></td>
				    <td><input type="checkbox" id="<portlet:namespace />art_46" name="<portlet:namespace />art_46" <%=art_46?"checked=\"checked\"":"" %>/></td>
				    <td><legend><liferay-ui:message key="cuota-social-uoma"/>:&nbsp;</legend></td>
				    <td><input type="checkbox" id="<portlet:namespace />cuota_social_uoma" name="<portlet:namespace />cuota_social_uoma" <%=cuota_social_uoma?"checked=\"checked\"":"" %>/></td>
				    <td><legend><liferay-ui:message key="aporte-solidario-uoma"/>:&nbsp;</legend></td>
				    <td><input type="checkbox" id="<portlet:namespace />aporte_solidario_uoma" name="<portlet:namespace />aporte_solidario_uoma" <%=aporte_solidario_uoma?"checked=\"checked\"":"" %>   /></td>
				    <td><legend><liferay-ui:message key="aporte-afip-ospim"/>:&nbsp;</legend></td>
				    <td><input type="checkbox" id="<portlet:namespace />aporte_afip_ospim" name="<portlet:namespace />aporte_afip_ospim" <%=aporte_afip_ospim?"checked=\"checked\"":"" %>/></td>
				    <td><!--legend><liferay-ui:message key="boleta-blanca-ospim"/>:&nbsp;</legend--></td>
				    <td><input type="hidden" id="<portlet:namespace />boleta_blanca_ospim" name="<portlet:namespace />boleta_blanca_ospim"/></td>
				    <td><!--legend><liferay-ui:message key="boleta-blanca-uoma"/>:&nbsp;</legend--></td>
				    <td><input type="hidden" id="<portlet:namespace />boleta_blanca_uoma" name="<portlet:namespace />boleta_blanca_uoma"/></td>
				    <td><!--legend><liferay-ui:message key="boleta-blanca-amtima"/>:&nbsp;</legend--></td>
				    <td><input type="hidden" id="<portlet:namespace />boleta_blanca_amtima" name="<portlet:namespace />boleta_blanca_amtima"/></td>
			    </tr>
			</table>
	 	</fieldset>
	</form>
	
	<div align="center" id="<portlet:namespace />aportes_externos">		
	<jsp:include page='aportes_externos_search_result.jsp' />
	</div>
	
	<script type="text/javascript">
	
		jQuery("#<portlet:namespace />periodoDesdeDia").hide();
		
		function setearMeses(){
			<portlet:namespace />setearMeses('true');
		}		 
		
		function exportarExcel(){
			<portlet:namespace />exportarExcel();			
		}	
		
		 function filtrarBoletas(){		 	
			<portlet:namespace />filtrarBoletas();
		} 
		 
	</script>
	