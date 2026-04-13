<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%
String cuit=ParamUtil.getString(request, "cuit");
String tipo=ParamUtil.getString(request, "tipo");

ArrayList<Prestador> prestadores = (ArrayList<Prestador>)PrestadorServiceUtil.getPrestadores(0, cuit, null, false);

/* Iterator<Prestador> iterator = prestadores.iterator();				
while (iterator.hasNext()) {
	Prestador prestador = iterator.next();
	if (prestador.getBaja_fecha() != null) {
		iterator.remove();
		break;
	}
} */

int prestadoresNum=prestadores.size();
int deBaja=0;
if(prestadores!=null){
   if(prestadores.get(0).getBaja_fecha()!=null){
	  deBaja=1; 
   }
}
%>

<script type="text/javascript">
function <portlet:namespace />muestraAlertaSiExcedeTopes() {
	//5 Corresponde al tipo Prestador HOSPITAL
	if (parseInt(<%=prestadoresNum%>) > 0 && parseInt(<%=tipo%>)!= 5){
		if(parseInt(<%=deBaja%>) == 0){
		  alert ('Ya existe el cuit como Prestador. Verifiqué con una nueva búsqueda');
		}else{
		  alert('CUIT dado de Baja. Comuníquese con Sistemas para reincorporarlo');	
		}  
		  
	}
}
<portlet:namespace />muestraAlertaSiExcedeTopes();
</script>