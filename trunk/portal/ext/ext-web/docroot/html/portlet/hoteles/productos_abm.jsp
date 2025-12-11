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
	
	List<ProductoCategoria> categorias =  HotelesServiceUtil.getProductosCategorias(ptoVtaAfip);
	

	session.setAttribute(WebKeysHoteles.CATEGORIAS_HOTEL,categorias);
	
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data">
    <input name="<portlet:namespace />id_hotel" type="hidden"	value="<%=ptoVtaAfip%>" /> 
	<fieldset class="block-labels">
		<legend>ABM Productos</legend>
		
		<table class="lfr-table">
		  <tr>
		   <td>Categoría:</td>
		     <td><select name="<portlet:namespace />categoria_filtro"
			              id="<portlet:namespace />categoria_filtro" 
			              onchange="">
			        <option value="">Seleccione Categoría</option>
			                
			        <%for(ProductoCategoria c:categorias) {%>
						     <option
							    value="<%=c.getCodigo() %>"	>
							      <%=c.getDescripcion() %>
						     </option>
					<%}%>
		         </select>
		     </td>
		  </tr>
		  <tr>
		    <td>&nbsp;</td>
		  </tr>
		</table>
		<input type="button" value="Buscar" onClick="<portlet:namespace />buscarProductos();"/>&nbsp;
		
		<input type="button" value="Nuevo" onClick="<portlet:namespace />nuevoProducto();"/>&nbsp;
			  
		<div id="<portlet:namespace />div_productos">
			<jsp:include page='/html/portlet/hoteles/productos_result.jsp' />  	
		</div>
		
	</fieldset>
</form>		

<script type="text/javascript">
		
	var popupMD;
	
	<portlet:namespace />initDateFields();
	
	function <portlet:namespace />initDateFields(){
	}
	
	function <portlet:namespace />nuevoProducto() {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_productos_abm" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function <portlet:namespace />buscarProductos() {	
		var categoria=jQuery('#<portlet:namespace />categoria_filtro').val();
				
		var busquedaNom = {"categoria":categoria,"cmd":"filtrar","id_hotel":"<%=ptoVtaAfip%>"};
	 	
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_productos_abm" /></portlet:renderURL>';
		jQuery('#<portlet:namespace />div_productos').load(url,busquedaNom, function(){	});	
	}
	
</script>

