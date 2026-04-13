<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<liferay-ui:error exception="<%= Exception.class %>" message="acta-sin-pagos" />
<form action="" method="get" name="<portlet:namespace />fm2" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
	
		<legend>
			<liferay-ui:message key="liquidar-actas-convenios" />
		</legend>
	
		<div id="<portlet:namespace />acta_convenios_liquidados">
			<jsp:include page='/html/portlet/tesoreria/liquida_acta_convenios/acta_convenios_liquidados.jsp' />  
		</div>	
		<div align="center">
			<input type="button" value="<liferay-ui:message key='liquidar-pagos-pendientes'/>" onClick="javascript:liquidarPagosPendientes()" />			
		</div>			
				
	</fieldset>

<script type="text/javascript">
		function exportarExcel(fechaliq){		
			window.location.href ='/xlsservlet/?reporte=REPORTE_LIQ_ACTA_CONVENIO&fechaLiq='+fechaliq;						
		}
		
		function liquidarPagosPendientes(){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/liquidar_acta_convenio';
	        jQuery('#<portlet:namespace />acta_convenios_liquidados').load(url, function() {
	        																jQuery('#<portlet:namespace />buscando').hide();            															
	        															  }
	        );
		}
		
</script>
