<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>



<fieldset class="block-labels"><legend><liferay-ui:message
	key="reporte-afiliados-anses" /></legend>
	
	<b>Generando el reporte de jubilados, espere por favor...</b>
	
</fieldset>

		
<script type="text/javascript">

	function reporteAfiliadosAnses(){		
	
		jQuery('#<portlet:namespace />buscando').show();		

		window.location.href ='/xlsservlet/?reporte=REPORTE_AFILIADOS_ANSES';					
	
		jQuery('#<portlet:namespace />buscando').hide();
	}
	
	reporteAfiliadosAnses();
	
</script>