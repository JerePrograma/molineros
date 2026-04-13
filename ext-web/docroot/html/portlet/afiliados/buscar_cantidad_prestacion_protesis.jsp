<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%
String cuil=ParamUtil.getString(request, "cuil_titular");
BigDecimal importe = new BigDecimal(ParamUtil.getString(request, "importe", "0"));
BigDecimal total= importe.multiply(new BigDecimal(ParamUtil.getInteger(request, "cantidad", 0)));
String idPrestacionAnterior=ParamUtil.getString(request, "id_prestacion_anterior","");
Integer idPlan= ParamUtil.getInteger(request, "id_plan",-1);

/*
BigDecimal cant_prestaciones_grupo = ReintegroServiceUtil.getCantidadPrestacionesProtesisAnio(cuil);
if (cant_prestaciones_grupo == null) {
	cant_prestaciones_grupo = BigDecimal.ZERO;
}
int tope_prestacion=ParamUtil.getInteger(request, "tope_prestacion");
*/

Object[] topeExcedido = ReintegroServiceUtil.evaluaTopesReintegro(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS,cuil,total,idPrestacionAnterior,idPlan);

%>

<script type="text/javascript">

function <portlet:namespace />muestraAlertaSiExcedeTopes() {
	/* alert('muestraAlertas'); */
	var confirmar = true;
	var esExcepcion=false;

	if (<%=topeExcedido[0]%>){
		confirmar = confirm ('Está excediendo el tope anual del grupo familiar\nTope: '+'<%=topeExcedido[1]%>'+'\nGasto grupo: '+'<%=topeExcedido[2]%>'+'\nDesea guardarlo?');
		if(confirmar){
			esExcepcion=true;
		}
	}
	
	if (!confirmar) {
		return;
	}
	
	<portlet:namespace />saveReintegroProtesis(esExcepcion);
}
<portlet:namespace />muestraAlertaSiExcedeTopes();
</script>