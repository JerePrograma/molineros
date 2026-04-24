<%@ include file="/html/portlet/comprobantes/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>


<%

	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "comprobantes";
	}else if(renderResponse.getNamespace().equals("_COM_1_")){
		portlet_name = "comprobantes";
	}
 		
%>
	<fieldset class="block-labels">
		<legend>Lista de Errores</legend>
		<table width="70%" class="lfr-table" >
		    <tr>
					<th><label>Código</label></th>
					<th><label>Descripción</label></th>
		   </tr>
		   <tr>
		      <td>AUT</td><td>Sin Autorización Prestacional Vigente para la prestación</td>
		   </tr>
		   <tr>
		      <td>AFI</td><td>El afiliado no está dado de alta</td>
		   </tr>
		   <tr>
		      <td>ABJ</td><td>El afiliado está dado de baja</td>
		   </tr>
		   <tr>
		      <td>ARM</td><td>Falta completar datos del Area Médica</td>
		   </tr>
		   <tr>
		      <td>CAE</td><td>Falta cargar el CAE</td>
		   </tr>
		   <tr>
		      <td>CCD</td><td>Prestador CON Convenio Directo</td>
		   </tr>
		   <tr>
		      <td>CUD</td><td>Falta Código Unico de Discapacidad</td>
		   </tr>
		   <tr>
		      <td>DEP</td><td>Falta cargar la dependencia</td>
		   </tr>
		   <tr>
		      <td>DUP</td><td>Ya existe el comprobante en el sistema. Las siguientes indicaciones indican en donde poder ubicar el comprobante. Cab - lote de integración. Lot - lote SuperIntendencia.
		      OP - orden de pago. liq -liquidacion   </td>
		   </tr>
		   <tr>
		      <td>III</td><td>La prestación no tiene asociada su código equivalente de la Superintendencia</td>
		   </tr>
		   <tr>
		      <td>NOM</td><td>Falta código de prestación </td>
		   </tr>
		   <tr>
		      <td>PRO</td><td>Falta cargar la provincia</td>
		   </tr>
		   <tr>
		      <td>PST</td><td>El prestador no está dado de alta</td>
		   </tr>
		   <tr>
		      <td>RPO</td><td>Reclamo Prestacional sin cerrar.</td>
		   </tr>
		    <tr>
		      <td>SCD</td><td>Prestador SIN Convenio Directo</td>
		   </tr>
		   <tr>
		      <td>SIC</td><td>Falta cargar el importe del comprobante</td>
		   </tr>
		   <tr>
		      <td>SIS</td><td>Falta cargar el importe solicitado</td>
		   </tr>
		   <tr>
		      <td>S>I</td><td>El importe solicitado es mayor al importe del comprobante</td>
		   </tr>
		    <tr>
		      <td>SRP</td><td>Sin Reclamo Prestacional cargado.</td>
		   </tr>
		</table>			
	</fieldset>
		
	
			
<script type="text/javascript">
</script>
