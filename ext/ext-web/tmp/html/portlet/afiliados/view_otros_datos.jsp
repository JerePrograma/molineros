<%@page import="ar.com.ospim.util.DateUtils"%>
<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
String cadenaCuil = TraeListasServiceUtil.getSystemConfig("CUILES_NO_VISIBLES_REMUNERACION");

Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
String id_plan=request.getParameter("id_plan");
int id_plan_int=0;
String cuil_titular=request.getParameter("cuil_titular");
int inte=ParamUtil.getInteger(request,"inte");
String fechaEgreso = request.getParameter("fechaEgreso");
/* AfiAporteList afiAporteList= (AfiAporteList)portletSession.getAttribute(WebKeysAfiliados.BUSQUEDA_APORTES,PortletSession.APPLICATION_SCOPE);
 */
/* List<Plan> planList=TraeListasServiceUtil.getPlanes(); */
/* if(null==afiAporteList){
	afiAporteList=AporteServiceUtil.buscaAportesPorPlan(id_plan,cuil_titular,inte);
	portletSession.setAttribute(WebKeysAfiliados.BUSQUEDA_APORTES, afiAporteList,PortletSession.APPLICATION_SCOPE);
}

int id_plan_omint = 0;
String descripcion_omint = null;
if(null!=afiAporteList.getPlan()){
	id_plan_int=afiAporteList.getPlan().getId();
	id_plan_omint = afiAporteList.getPlan().getId_plan_omint();
	descripcion_omint = afiAporteList.getPlan().getDescripcionOmint();
} */

String view=request.getParameter("view");
request.setAttribute("view",view);

%>
<portlet:defineObjects/>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="situacion-laboral" /></legend>
				<div align="center" id="<portlet:namespace />situLaborales">		
					<jsp:include page='situlaboral_search_result.jsp'>
					      <jsp:param name="view" value="true" />
					</jsp:include> 
				</div>

				<%if(seccionalFijada==0 ||(seccionalFijada>0 && 
						(null== afiliado.getBaja_fecha() 
						|| DateUtils.compararFechasTruncarEnDia(afiliado.getBaja_fecha(), new Date()) >=1))){%>
				<table class="lfr-table">
					<tr>						
						<td colspan="7">
						     <%if (cadenaCuil.indexOf (cuil_titular) == -1){ %>
							    <input type="button" value="<liferay-ui:message key="ver-aportes" />" onClick="<portlet:namespace />verAportes(false);" />
						     <%}%>	
						</td>												
					</tr>					
						
				</table>
				<%}%>
		</fieldset>
		
		<div id="divVerAfiPlanAportes" align="left" >
			<liferay-util:include page='/html/portlet/afiliados/afiliado_plan_view.jsp' />
		</div>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="tercerizadora-servicio" /></legend>
				<%if(showABMButtons){%>
				<div>
					<a href="javascript:mostrarTercerizadoraHisto();" id="<portlet:namespace />mostrarTercHistoLink" ><liferay-ui:message key="ver-historico" /></a>
					<a href="javascript:ocultarTercerizadoraHisto();" id="<portlet:namespace />ocultarTercHistoLink" ><liferay-ui:message key="ocultar-historico" /></a>
						<div align="left" id="<portlet:namespace />histo_tercerizadoras">
						   <liferay-util:include page='/html/portlet/afiliados/historico_tercerizadoras_search_result.jsp' />
						</div>
				</div>
				<%} %>	
				<div align="center" id="<portlet:namespace />tercerizadoras">		
					<jsp:include page='tercerizadoras_search_result.jsp' />
				</div>				
		</fieldset>
		
<script type="text/javascript">
	jQuery('#<portlet:namespace />histo_planes').hide();
	jQuery('#<portlet:namespace />histo_tercerizadoras').hide();
	jQuery('#<portlet:namespace />ocultarPlanHistoLink').hide();
	jQuery('#<portlet:namespace />ocultarTercHistoLink').hide();
	
	var popupAfillAportes;

	function <portlet:namespace />verHistorico(){
		if (popupAfillAportes!=null){
		Liferay.Popup.close(popupAfillAportes);
		}
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/historico_aporte&cuil_titular=<%=cuil_titular%>&inte<%=inte%>';
		popupAfillAportes = Liferay.Popup({title:"Historico de Planes",modal:true,width:800});
		jQuery(popupAfillAportes).load(url);
	}

	function <portlet:namespace />exportarExcel(){
		var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();		
		if(periodoDesdeMesAnio==null){
			periodoDesdeMesAnio='1_2011';
		}
		var solo_deriva=document.getElementById("<portlet:namespace />solo_derivacion").checked		
		var cuota_amtima=document.getElementById('<portlet:namespace />cuota_amtima').checked;		
		var cuota_usufructo=document.getElementById("<portlet:namespace />cuota_usufructo").checked;		
		var art_46=document.getElementById("<portlet:namespace />art_46").checked;		
		var cuota_social_uoma=document.getElementById("<portlet:namespace />cuota_social_uoma").checked;		
		var aporte_solidario_uoma=document.getElementById("<portlet:namespace />aporte_solidario_uoma").checked;		
		var aporte_afip_ospim=document.getElementById("<portlet:namespace />aporte_afip_ospim").checked;
				
		/*var boleta_blanca_ospim=document.getElementById("<portlet:namespace />boleta_blanca_ospim").checked;
		var boleta_blanca_uoma=document.getElementById("<portlet:namespace />boleta_blanca_uoma").checked;
		var boleta_blanca_amtima=document.getElementById("<portlet:namespace />boleta_blanca_amtima").checked;*/
		
		window.location.href ='/xlsservlet/?reporte=REPORTE_APORTES'
			+'&cuil='+<%=cuil_titular%>
			+'&periodoDesdeMesAnio='+periodoDesdeMesAnio	
			+'&solo_derivacion='+solo_deriva
			+'&cuota_amtima='+cuota_amtima
			+'&cuota_usufructo='+cuota_usufructo
			+'&art_46='+art_46
			+'&cuota_social_uoma='+cuota_social_uoma
			+'&aporte_solidario_uoma='+aporte_solidario_uoma
			+'&aporte_afip_ospim='+aporte_afip_ospim;
			/*+'&boleta_blanca_ospim='+boleta_blanca_ospim
			+'&boleta_blanca_uoma='+boleta_blanca_uoma
			+'&boleta_blanca_amtima='+boleta_blanca_amtima;*/		
	}
	
	function <portlet:namespace />verAportes(cerrarAnterior){
		var periodoDesdeMesAnio = jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();

		if(periodoDesdeMesAnio==null){
			periodoDesdeMesAnio='012011';
		}
						
		if(cerrarAnterior=='true') {
			Liferay.Popup.close(popupAfillAportes);
		}
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/ver_aportes&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&cuil='+<%=cuil_titular%>;		
		popupAfillAportes = Liferay.Popup({title:"<liferay-ui:message key="aportes" />",modal:true,width:1300});
		jQuery(popupAfillAportes).load(url);
	}	
	
	function <portlet:namespace />setearMeses(cerrarAnterior){
		var periodoDesdeMesAnio = jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();
		var cuota_amtima=document.getElementById("<portlet:namespace />cuota_amtima").checked;
		var cuota_usufructo=document.getElementById("<portlet:namespace />cuota_usufructo").checked;
		var art_46=document.getElementById("<portlet:namespace />art_46").checked;
		var cuota_social_uoma=document.getElementById("<portlet:namespace />cuota_social_uoma").checked;
		var aporte_solidario_uoma=document.getElementById("<portlet:namespace />aporte_solidario_uoma").checked;
		var aporte_afip_ospim=document.getElementById("<portlet:namespace />aporte_afip_ospim").checked;
		var boleta_blanca_ospim=document.getElementById("<portlet:namespace />boleta_blanca_ospim").checked;
		var boleta_blanca_uoma=document.getElementById("<portlet:namespace />boleta_blanca_uoma").checked;
		var boleta_blanca_amtima=document.getElementById("<portlet:namespace />boleta_blanca_amtima").checked;
		
		if(periodoDesdeMesAnio==null){
			periodoDesdeMesAnio='012011';
		}
				
		if(cerrarAnterior=='true') {
			Liferay.Popup.close(popupAfillAportes);
		}
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/ver_aportes&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&cuota_amtima='+cuota_amtima+'&cuota_usufructo='+cuota_usufructo+'&art_46='+art_46+'&cuota_social_uoma='+cuota_social_uoma+'&aporte_solidario_uoma='+aporte_solidario_uoma+'&aporte_afip_ospim='+aporte_afip_ospim+'&boleta_blanca_ospim='+boleta_blanca_ospim+'&boleta_blanca_uoma='+boleta_blanca_uoma+'&boleta_blanca_amtima='+boleta_blanca_amtima+'&cuil='+<%=cuil_titular%>;		
		popupAfillAportes = Liferay.Popup({title:"<liferay-ui:message key="aportes" />",modal:true,width:1300});
		jQuery(popupAfillAportes).load(url);
	}

	function <portlet:namespace />filtrarBoletas(){
		var periodoDesdeMesAnio = jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();
		
		 if(periodoDesdeMesAnio==null){
			periodoDesdeMesAnio='012011';
		} 
		 
		var cuota_amtima=document.getElementById("<portlet:namespace />cuota_amtima").checked;
		var cuota_usufructo=document.getElementById("<portlet:namespace />cuota_usufructo").checked;
		var art_46=document.getElementById("<portlet:namespace />art_46").checked;
		var cuota_social_uoma=document.getElementById("<portlet:namespace />cuota_social_uoma").checked;
		var aporte_solidario_uoma=document.getElementById("<portlet:namespace />aporte_solidario_uoma").checked;
		var aporte_afip_ospim=document.getElementById("<portlet:namespace />aporte_afip_ospim").checked;
		var boleta_blanca_ospim=document.getElementById("<portlet:namespace />boleta_blanca_ospim").checked;
		var boleta_blanca_uoma=document.getElementById("<portlet:namespace />boleta_blanca_uoma").checked;
		var boleta_blanca_amtima=document.getElementById("<portlet:namespace />boleta_blanca_amtima").checked;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/filtrar_boletas&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&cuota_amtima='+cuota_amtima+'&cuota_usufructo='+cuota_usufructo+'&art_46='+art_46+'&cuota_social_uoma='+cuota_social_uoma+'&aporte_solidario_uoma='+aporte_solidario_uoma+'&aporte_afip_ospim='+aporte_afip_ospim+'&boleta_blanca_ospim='+boleta_blanca_ospim+'&boleta_blanca_uoma='+boleta_blanca_uoma+'&boleta_blanca_amtima='+boleta_blanca_amtima+'&cuil='+<%=cuil_titular%>;
 		jQuery("#<portlet:namespace />aportes_externos").load(url);   
	}
	
	function mostrarPlanHisto(){
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/historico_aporte&cuil_titular=<%=cuil_titular%>';
		jQuery('#<portlet:namespace />histo_planes').load(url, function() {
			jQuery('#<portlet:namespace />histo_planes').show();
			jQuery('#<portlet:namespace />ocultarPlanHistoLink').show();
			jQuery('#<portlet:namespace />mostrarPlanHistoLink').hide();	            															
		});
	}
	
	function ocultarPlanHisto(){
		jQuery('#<portlet:namespace />histo_planes').hide();
		jQuery('#<portlet:namespace />mostrarPlanHistoLink').show();
		jQuery('#<portlet:namespace />ocultarPlanHistoLink').hide();
	}
	
	function mostrarTercerizadoraHisto(){
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/historico_tercerizadora&cuil_titular=<%=cuil_titular%>';
		jQuery('#<portlet:namespace />histo_tercerizadoras').load(url, function() {
			jQuery('#<portlet:namespace />histo_tercerizadoras').show();
			jQuery('#<portlet:namespace />ocultarTercHistoLink').show();
			jQuery('#<portlet:namespace />mostrarTercHistoLink').hide();	            															
		});
	}
	
	function ocultarTercerizadoraHisto(){
		jQuery('#<portlet:namespace />histo_tercerizadoras').hide();
		jQuery('#<portlet:namespace />mostrarTercHistoLink').show();
		jQuery('#<portlet:namespace />ocultarTercHistoLink').hide();
	}
	
</script>