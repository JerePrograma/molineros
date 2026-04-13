<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String cmd = (String) request.getAttribute(Constants.CMD);

boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_RECLAM_PREST );

boolean inHabilitar = false;
boolean esEdicion=false;

NumberFormat format2D = new DecimalFormat("#0.00");

int cantDeBaja=0;

if (cmd != null &&  cmd.equalsIgnoreCase(Constants.VIEW)   ){
	inHabilitar= true;
}

if (cmd != null &&  cmd.equalsIgnoreCase(Constants.EDIT)   ){
	esEdicion= true;
}

List<PrestacionesEquipoInterdisciplinario> prestacionesEquipoInterdisiplinario  = null;

prestacionesEquipoInterdisiplinario =  (ArrayList<PrestacionesEquipoInterdisciplinario>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION);

List<String> headerNames = new ArrayList<String>();

headerNames.add("Codigo");
headerNames.add("Prestacion");
headerNames.add("Tipo");
headerNames.add("Cantidad");
headerNames.add("Importe");
headerNames.add("Total");

	
if (!inHabilitar){
	String vereditarborrar = "Elimina";		 		
	if(showABMButtons &&  false ) {
		vereditarborrar+="|Edita|Autoriza";
	}
	headerNames.add(vereditarborrar);
}else{
	headerNames.add("");
}

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-prestaciones-were-found"));

%><script type="text/javascript"> 
jQuery('#<portlet:namespace />cantprestacioneslista').val('0');

</script><%
if (prestacionesEquipoInterdisiplinario != null && prestacionesEquipoInterdisiplinario.size()>0){
	int total = prestacionesEquipoInterdisiplinario.size();
	String opcionesCombo="" ; 
	String enabledestado="";
	String captionEstadoAutorizadoRechazado="" ; 
	opcionesCombo="<option value='0'>CARGADO</option><option value='2'>AUTORIZADO</option><option value='3'>RECHAZADO</option>"; 
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	%><script type="text/javascript"> jQuery('#<portlet:namespace />cantprestacioneslista').val('<%=prestacionesEquipoInterdisiplinario.size()%>');</script><%
	
 	for (int i = 0; i < prestacionesEquipoInterdisiplinario.size(); i++) {	    
 		
 		PrestacionesEquipoInterdisciplinario presequipo   = (PrestacionesEquipoInterdisciplinario) prestacionesEquipoInterdisiplinario.get(i);	
	 	ResultRow row = new ResultRow(presequipo,new Integer(1+i), i);	 	 
	 	
	 	row.addText(String.valueOf(presequipo.getCodigo_Prestacion() )); 
	 	row.addText(presequipo.getDescripcion());
	 	row.addText(presequipo.getTipoPrestacionDetalle()==null ? "" : presequipo.getTipoPrestacionDetalle());
	 	row.addText(presequipo.getCantidadString() );
	 	row.addText(format2D.format(presequipo.getImporte()));
	 	row.addText(format2D.format(presequipo.getImporte()  *  presequipo.getCantidad()   ));
	 	
  		StringBuilder sb=new StringBuilder(); 
  		if (esEdicion || !inHabilitar){
	  		if(presequipo.getEstado() == null || !presequipo.getEstado().equals(PrestacionesEquipoInterdisciplinario.ESTADOS.BAJA))  {
	  			
	  			    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"borrar prestacion\" src=\"");
		 			sb.append(themeDisplay.getPathThemeImages());
		 			sb.append("/common/delete.png\" onClick=\"javascript:borrarPrestacionconvalida('");
			 		sb.append(String.valueOf(presequipo.getIdregistro()  ));
				 	sb.append("');\" />");		 			
				/* 
  		            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"edita prestacion\" src=\"");
		 			sb.append(themeDisplay.getPathThemeImages());
		 			sb.append("/common/edit.png\" onClick=\"javascript:editarPrestacion('");
			 		sb.append(String.valueOf(presequipo.getIdregistro()  ));
				 	sb.append("',1);\" />");
	  			*/
		 			
	  		}else{  			
	  			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img height='16'  width='16' src='/html/themes/classic/images/common/close.png'/>");
	  			cantDeBaja=cantDeBaja+1;
	  		}	  		
  		}else{
  		     if ( ! (presequipo.getEstado() == null || !presequipo.getEstado().equals(PrestacionesEquipoInterdisciplinario.ESTADOS.BAJA)) ){
  			     row.addText("Eliminado"  );
  		                      }
  		     else{
  		    	
  		     }
  		    	 
  		     } 
		
 		row.addText(sb.toString()); 
		resultRows.add(row);		 
 	}
} 

%>


<liferay-ui:error exception="<%=PrestacionesEquipoInterdisciplinarioException.class %>" message="error-en-prestacion-equipo" />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

		
<script>
// 
jQuery('#<portlet:namespace />valortipoprestacion').val('');

function editarPrestacion(idPrestacion, tipoEdicion ){

}



function borrarPrestacionconvalida(idPrestacion){
		
		
		var r = confirm("Seguro de Eliminar la prestación no podra restaurarla. ?");
		if (r == true) {
		 
		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/borrar_equipoprestaciones';
		url = url+'&idPrestacion='+idPrestacion;	
		
		jQuery("#<portlet:namespace />lista_prestaciones_equipo").load(url);	
		var cant;
		cant =	<%=Validator.isNotNull(prestacionesEquipoInterdisiplinario)  ? prestacionesEquipoInterdisiplinario.size() - cantDeBaja : 0  %>;
			
		if (cant==1)
			{
			alert('Recuerde que debe ingresar por lo menos una prestación al caso para que se grabe.');		
			/* evaluarOnSectorListaEnCero(); */
			}	

	}	

	
}





</script>
