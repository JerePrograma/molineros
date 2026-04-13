<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%
String cuil=ParamUtil.getString(request, "cuil_titular");
int inte=ParamUtil.getInteger(request, "inte");
int id_prestacion=ParamUtil.getInteger(request, "id_prestacion");
String fecha_prestacion=ParamUtil.getString(request, "fecha_prestacion");
int cantidad=ParamUtil.getInteger(request, "cantidad");
String importe=ParamUtil.getString(request, "importe");
String importe_anterior=ParamUtil.getString(request, "importe_anterior", "0.0");
String cantidad_anterior=ParamUtil.getString(request, "cantidad_anterior", "0.0");
String cuit=ParamUtil.getString(request, "cuit_entidad");
String sucu=ParamUtil.getString(request, "sucursal_entidad", "000");
String periodo = ParamUtil.getString(request, "periodo");
String codPrestaci = ParamUtil.getString(request, "codPrestaci");

MotivoAltaDiscapacidad motivoAltaDiscapacidad = TratamientoDiscapacidadServiceUtil.validarDiscapacidad(cuil, inte, id_prestacion, fecha_prestacion, cantidad, importe, cuit, sucu, periodo, codPrestaci, importe_anterior, cantidad_anterior);
String mensajeDiscapacidad = motivoAltaDiscapacidad.getMensajeAltaEstado() != null ? motivoAltaDiscapacidad.getMensajeAltaEstado() : ""; 
int estadoDiscapacidad = motivoAltaDiscapacidad.getEstadoAlta();
%>

<script type="text/javascript">

function <portlet:namespace />muestraAlertaSiExcedeTopeDiscapacidad() {
	if ('<%=mensajeDiscapacidad%>' != ''){
		if (confirm('<%=mensajeDiscapacidad%>' + ' \n¿Está seguro de que quiere continuar guardando?') == true) {
			jQuery('#<portlet:namespace />motivoAltaDiscapacidad').val('<%=estadoDiscapacidad%>');
			<portlet:namespace />validarTopes();
		} else {
			return false;
		}			
	} else {		
		<portlet:namespace />validarTopes();
	}
}

<portlet:namespace />muestraAlertaSiExcedeTopeDiscapacidad();
</script>
