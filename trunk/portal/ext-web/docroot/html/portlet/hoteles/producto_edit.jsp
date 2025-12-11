<%@ include file="/html/portlet/hoteles/init.jsp"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
 	ProductoConfiteria producto=(ProductoConfiteria)request.getSession().getAttribute(WebKeysHoteles.PRODUCTO_EN_EDICION);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	boolean esEdicion = true;
	if(viewStr==null){
		viewStr=ParamUtil.getString(request, "view");
	}
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "hoteles";
	}
	
	String id_producto=producto!=null?producto.getCodigo():"";
	if(producto==null){
		producto= new ProductoConfiteria();
	}
	
	List<ProductoCategoria> categorias = (List<ProductoCategoria>)request.getSession().getAttribute(WebKeysHoteles.CATEGORIAS_HOTEL);
%>

<form action="" method="post" name="<portlet:namespace />fmS">

	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
		value="" />
  
	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	<liferay-ui:error key="errorAfiliadoNull"
		message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
		
    <liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
		

	<fieldset class="block-labels"> 
		<legend>Producto</legend>
		
		<table class="lfr-table">
		   <tr>
			    <td>Código:</td> 
			    <td><input id="<portlet:namespace />codigo"
					name="<portlet:namespace />codigo" size="6" onkeyup="mayus(this);"
					maxlength="6" type="text" tabindex="1" <%=!esEdicion ?"readonly=\"readonly\"":"" %>
					value='<%=producto.getCodigo()==null?"":producto.getCodigo()%>' />
				</td>
				<td>Descripción:</td> 
			    <td><input id="<portlet:namespace />descripcion"
					name="<portlet:namespace />descripcion" size="50"
					maxlength="100" type="text" tabindex="2" 
					value="<%=producto.getDescripcion()==null?"":producto.getDescripcion() %>" />
				</td>
				
				
				<td>Descripción Corta:</td> 
			    <td><input id="<portlet:namespace />descripcion_corta"
					name="<portlet:namespace />descripcion_corta" size="20"
					maxlength="30" type="text" tabindex="3" 
					value="<%=producto.getDescripcionCorta()==null?"":producto.getDescripcionCorta() %>" />
				</td>
			</tr>
			<tr><td>&nbsp;</td></tr>
		</table>
		<table class="lfr-table">
			<tr>
				<td>Categoría:</td>
		        <td>
		           <select name="<portlet:namespace />categoria"
			              id="<portlet:namespace />categoria" 
			              onchange="" tabindex="4">
			        <option value="">Seleccione Categoría</option>
			                
			        <%for(ProductoCategoria c:categorias) {%>
						     <option  value="<%=c.getCodigo() %>"
						         <%if( producto.getCategoria()!=null && producto.getCategoria().getCodigo()!=null  
						        		 && c.getCodigo().equalsIgnoreCase(producto.getCategoria().getCodigo())){%>
						             selected="selected"
						         <%}%>
						     >
							      <%=c.getDescripcion() %> 
						     </option>
					<%}%>
		           </select>
		        </td>
				<td>
				<td>Precio:</td>
				</td>
				<td><input id="<portlet:namespace />precio"
					name="<portlet:namespace />precio" size="20"
					maxlength="30" type="text" tabindex="5" onkeyup="soloNumeros(this);" onkeydown="allowOnlyDigits(event);"
					value='<%=producto.getPrecio()!=null?producto.getPrecio():""%>' tabindex="5"/>
				</td>
				<td>Habilitado para Habitaciones:</td> 
			    <td>
				    <input type="checkbox" id="<portlet:namespace />paraHabitaciones" name="<portlet:namespace />paraHabitaciones" 
				    <%=producto.isHabilitadoHabitaciones()  ?"checked=\"checked\"":"" %> tabindex='6'/>
				</td>
			</tr>
	    </table>
		
	</fieldset>
	
	<br>
	<input type="hidden" name="<portlet:namespace />id_producto"
		id="<portlet:namespace />id_producto" value="<%=id_producto%>" />
		
	<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" /> 

    <table>
	 <tr>
	  <td>


      <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" tabindex='7'
	  />
		 
	</td>
	</tr>
	</table>      
   <input type="hidden" value="" name="view" id="view" />
   
</form>

<script type="text/javascript">

function <portlet:namespace />validarCampos(){
	var result = true;
	var codigo=jQuery("#<portlet:namespace />codigo").val();
	var descripcion=jQuery("#<portlet:namespace />descripcion").val();
	var descripcion_corta=jQuery("#<portlet:namespace />descripcion_corta").val();
	var categoria=jQuery("#<portlet:namespace />categoria").val();
	
	if(codigo==null || codigo==""){
		alert("Debe ingresar un Código");
		return false;
	}
	
	if(descripcion==null || descripcion==""){
		alert("Debe ingresar una Descripción");
		return false;
	}
	
	if(descripcion_corta==null || descripcion_corta==""){
		alert("Debe ingresar una Descripción Corta");
		return false;
	}

	if(categoria==null || categoria==""){
		alert("Debe ingresar una Categoría");
		return false;
	}
	
	return true;
}

function <portlet:namespace />salvarEdicion(){
	if (<portlet:namespace />validarCampos()) {
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_productos_abm" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
		'</liferay-portlet:renderURL>';
		
		submitForm(document.<portlet:namespace />fmS, url);
	}
	return false;		
}


function mayus(e) {
    e.value = e.value.toUpperCase();
}


</script>

