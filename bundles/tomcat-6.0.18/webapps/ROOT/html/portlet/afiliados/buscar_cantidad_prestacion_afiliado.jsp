<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%
String cuil=ParamUtil.getString(request, "cuil_titular");
int inte=ParamUtil.getInteger(request, "inte");
int id_prestacion=ParamUtil.getInteger(request, "id_prestacion");
int id_cantidad=ParamUtil.getInteger(request, "tope_prestacion");
int cantidad=ParamUtil.getInteger(request, "cantidad");
BigDecimal cant_prestaciones_afiliado = ReintegroServiceUtil.getCantidadPrestacionesAnio(cuil, inte, id_prestacion);
String ids_reintegros_anio = ReintegroServiceUtil.getIdReintegrosAnio(cuil, inte, id_prestacion);
if (cant_prestaciones_afiliado == null) {
	cant_prestaciones_afiliado = BigDecimal.ZERO;
}
%>

<script type="text/javascript">

function <portlet:namespace />muestraAlertaSiExcedeTope() {
	if (<%=cant_prestaciones_afiliado.doubleValue() %> + <%=cantidad %>> <%=id_cantidad %>){
		alert ('Se ha excedido el tope de la prestación seleccionada para el afiliado seleccionado en este año, con reintegro(s), número: ' + '<%=ids_reintegros_anio%>');
	}
	<portlet:namespace />saveReintegro();
}

<portlet:namespace />muestraAlertaSiExcedeTope();
</script>