<%@include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/hoteles/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "hoteles";
	}
	
	String ptoVtaAfip="00030";

	try{
		ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
	}catch(Exception e){
		//ptoVtaAfip="0000";
		ptoVtaAfip="00030";
	}
	
	//List<ProductoCategoria> categorias =  HotelesServiceUtil.getProductosCategorias(ptoVtaAfip);
	//session.setAttribute(WebKeysHoteles.CATEGORIAS_HOTEL,categorias);
	
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data">
    <input name="<portlet:namespace />id_hotel" type="hidden"	value="<%=ptoVtaAfip%>" /> 
	<fieldset class="block-labels">
		<legend>ABM Personal</legend>
		
		<table class="lfr-table">
		  <tr>
		   <td>Categoría:</td>
		      <td><select name="<portlet:namespace />categoria_filtro"  id="<portlet:namespace />categoria_filtro" >
		                <option value="">Seleccione Categoría</option>
						<%for(int i = 0; i < WebKeysHoteles.CATEGORIAS_EMPLEADOS.length; i++ ) {%>
						<option
							value="<%= WebKeysHoteles.CATEGORIAS_EMPLEADOS[i][0] %>">
							<%=WebKeysHoteles.CATEGORIAS_EMPLEADOS[i][1] %>
						</option>
						<% } %>
				 </select>
			  </td>	 
		     
		  </tr>
		  <tr>
		    <td>&nbsp;</td>
		  </tr>
		</table>
		<input type="button" value="Buscar" onClick="<portlet:namespace />buscarPersonal();"/>&nbsp;
		
		<input type="button" value="Nuevo" onClick="<portlet:namespace />nuevoPersonal();"/>&nbsp;
			  
		<div id="<portlet:namespace />div_personal">
			<jsp:include page='/html/portlet/hoteles/personal_result.jsp' />  	
		</div>
		
	</fieldset>
</form>		

<script type="text/javascript">
		
	var popupMD;
	
	<portlet:namespace />initDateFields();
	
	function <portlet:namespace />initDateFields(){
	}
	
	function <portlet:namespace />nuevoPersonal() {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_personal_abm" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function <portlet:namespace />buscarPersonal() {	
		var categoria=jQuery('#<portlet:namespace />categoria_filtro').val();
				
		var busquedaNom = {"categoria":categoria,"cmd":"filtrar","id_hotel":"<%=ptoVtaAfip%>"};
	 	
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_personal_abm" /></portlet:renderURL>';
		jQuery('#<portlet:namespace />div_personal').load(url,busquedaNom, function(){	});	
	}
	
</script>

