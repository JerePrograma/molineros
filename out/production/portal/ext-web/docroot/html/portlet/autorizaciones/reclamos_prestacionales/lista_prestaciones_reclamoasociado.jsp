<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String cmd = (String) request.getAttribute(Constants.CMD);

NumberFormat format2D = new DecimalFormat("#0.00");

boolean inHabilitar = false; 

if (cmd != null && ( cmd.equalsIgnoreCase(Constants.VIEW) ||  cmd.equalsIgnoreCase(Constants.EDIT ) )  ){
	inHabilitar= true;
}


List<PrestacionesReclamo> prestacionesDelReclamo = null;

prestacionesDelReclamo =  (ArrayList<PrestacionesReclamo>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_ASOCIADAS_RECLAMOS_EN_SESION);


List<String> headerNames = new ArrayList<String>();

headerNames.add("Codigo");
headerNames.add("Prestacion Asociada");
headerNames.add("Frecuencia");
headerNames.add("Importe");
headerNames.add("Cargo OSPIM");
headerNames.add("Cargo PS");
headerNames.add("Observacion");


SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-prestaciones-were-found"));


%><script type="text/javascript"> jQuery('#<portlet:namespace />cantprestacioneslista').val('0');</script><%
if (prestacionesDelReclamo != null && prestacionesDelReclamo.size()>0){
	int total = prestacionesDelReclamo.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	%><script type="text/javascript"> jQuery('#<portlet:namespace />cantprestacioneslista').val('<%=prestacionesDelReclamo.size()%>');</script><%
	
 	for (int i = 0; i < prestacionesDelReclamo.size(); i++) {	    
 		
 		PrestacionesReclamo presreclamo  = (PrestacionesReclamo) prestacionesDelReclamo.get(i);
	
	 	ResultRow row = new ResultRow(presreclamo,new Integer(1+i), i);
	 	
	 	if( Integer.valueOf(presreclamo.getIdregistroString())  <0) {
	 		row.addText("");
	 	}else{
	 		row.addText(presreclamo.getIdregistroString());	
	 	}		 		 	 	 		 		
	 	
	 	row.addText(presreclamo.getDescripcion());
	  	
	 	row.addText(presreclamo.getFrecuencia());	
	 	
	 	row.addText(format2D.format(presreclamo.getImporte()));
	 	row.addText(presreclamo.getCargo_ospimString()  );
	 	row.addText(presreclamo.getCargo_psString() ); 		 			
	 	row.addText(presreclamo.getObservaciones()  );
  		
  		StringBuilder sb=new StringBuilder(); 

 		row.addText(sb.toString()); 
		resultRows.add(row);		 
 	}
} 

%>
<liferay-ui:error exception="<%=PrestacionesReclamosException.class %>" message="error-en-prestacion-reclamo" />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	


<script>
function borrarPrestacion(idPrestacion){
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/borrar_reclamosprestaciones';
	url = url+'&idPrestacion='+idPrestacion;	
		
	jQuery("#<portlet:namespace />lista_prestaciones_reclamos").load(url);		
	}
</script>
