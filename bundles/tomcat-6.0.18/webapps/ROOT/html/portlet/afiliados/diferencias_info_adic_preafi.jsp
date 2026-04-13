<%@ include file="/html/portlet/afiliados/init.jsp"%>

<% 
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);

Afiliado preAfiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.PREAFILIADO_EN_SESSION);

if(preAfiliado!=null && afiliado != null){ %>
<table class="lfr-table">
 	  <tr>
		<th><label><liferay-ui:message key="dif-pre-carga-afi" />:</label></th>
	  </tr>
	  <tr>
	  	  <td>
	  	       <table class="lfr-table" style="font: fantasy; font-style: italic; color: red;">
	  	       <tr>
	  	           <%if(!preAfiliado.getCuitSituLaboral(0).equalsIgnoreCase(afiliado.getCuitSituLaboral(0))){ %>
	   			   <td>CUIT:&nbsp;<%=preAfiliado.getCuitSituLaboral(0) + " - " + preAfiliado.getSucursalSituLaboral(0) %>&nbsp;</td>
	  		 	   <% } %>
	  		 	    <%if(!preAfiliado.getSucursalSituLaboral(0).equalsIgnoreCase(afiliado.getSucursalSituLaboral(0))){ %>
	   			   <td>Sucursal:&nbsp;<%=preAfiliado.getSucursalSituLaboral(0)%>&nbsp;</td>
	  		 	   <% } %>
	  		   <tr>
	  		   <tr>
	  	           <%if(preAfiliado.getAfiPlan().getPlan().getId() != afiliado.getAfiPlan().getPlan().getId() ){ %>
	   			   <td>Plan:&nbsp;<%=preAfiliado.getAfiPlan().getPlan().getDescripcion()%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if(!preAfiliado.getAfiPlan().getVigenDesde().equals(afiliado.getAfiPlan().getVigenDesde())){ %>
	  		 	   <td>Fecha Desde:&nbsp;<%=sdf.format(preAfiliado.getAfiPlan().getVigenDesde() )%>&nbsp;</td>
	  		 	   <% } %>
	  		 	   <%if( //1
	  		 			  ((preAfiliado.getAfiPlan().getVigenHasta() != null && afiliado.getAfiPlan().getVigenHasta() != null) &&
	  		 			  (!preAfiliado.getAfiPlan().getVigenHasta().equals(afiliado.getAfiPlan().getVigenHasta()))) 
	  		 			  ||
	  		 			 //2 
	  		 			 (preAfiliado.getAfiPlan().getVigenHasta() != null && afiliado.getAfiPlan().getVigenHasta() == null)
	  		 			   /* || */
	  		 			 //3 
		  		 		 /*  (preAfiliado.getAfiPlan().getVigenHasta() == null && afiliado.getAfiPlan().getVigenHasta() != null) */
	  		 		   ){ %>
	  		 	   <td>Fecha Hasta:&nbsp;<%=sdf.format(preAfiliado.getAfiPlan().getVigenHasta() )%>&nbsp;</td>
	  		 	   <% } %>
	  		   <tr>
	  		   <tr>
	  		   	   <%if(!preAfiliado.getId_tercerizadora().equalsIgnoreCase(afiliado.getId_tercerizadora())){ %>
	  		 	   <td>Tercerizadora:&nbsp;<%=preAfiliado.getDesc_tercerizadora()%>&nbsp;</td>
	  		 	   <% } %>
	  		   <tr>		 	
	  	       </table>		
	  	  </td>
	  </tr>	  	
</table>
<%} %>	   