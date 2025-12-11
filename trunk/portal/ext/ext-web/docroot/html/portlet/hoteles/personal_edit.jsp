<%@ include file="/html/portlet/hoteles/init.jsp"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
 	Personal personal=(Personal)request.getSession().getAttribute(WebKeysHoteles.PERSONAL_EN_EDICION);
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
	
	String id_personal=personal!=null && personal.getId()!=null?personal.getId().toString():"";
	if(personal==null){
		personal= new Personal();
	}
	
	
	String ptoVtaAfip="00030";
	try{
		ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 	
	}catch(Exception e){
		//ptoVtaAfip="0000";
		ptoVtaAfip="00030";
	}
	
	if(ptoVtaAfip.equals("9999")){  // Meter una opción para quien es adminsitrador de hoteles y seleccione el que desea... 
		ptoVtaAfip="00030";
	}

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
		<legend>Personal</legend>
		
		<table class="lfr-table">
		   <tr>
			    <td>Id:</td> 
			    <td><input id="<portlet:namespace />codigo"
					name="<portlet:namespace />codigo" size="5" onkeyup="mayus(this);"
					maxlength="3" type="text" tabindex="1" <%=!esEdicion ?"readonly=\"readonly\"":"" %>
					value='<%=personal.getId()==null?"":personal.getId()%>' />
				</td>
				<td>Apellido:</td> 
			    <td><input id="<portlet:namespace />apellido"
					name="<portlet:namespace />apellido" size="50"
					maxlength="100" type="text" tabindex="2" 
					value="<%=personal.getApellido()==null?"":personal.getApellido() %>" />
				</td>
				
				
				<td>Nombre:</td> 
			    <td><input id="<portlet:namespace />nombre"
					name="<portlet:namespace />nombre" size="50"
					maxlength="100" type="text" tabindex="3" 
					value="<%=personal.getNombre()==null?"":personal.getNombre() %>" />
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
					<%for(int i = 0; i < WebKeysHoteles.CATEGORIAS_EMPLEADOS.length; i++ ) {%>
						<option
							value="<%= WebKeysHoteles.CATEGORIAS_EMPLEADOS[i][0] %>"
							<%if (personal != null && personal.getCategoria() !=null && 
					              (WebKeysHoteles.CATEGORIAS_EMPLEADOS[i][0]).equals(personal.getCategoria())) { %>
							selected="selected" <%} %>>
							<%=WebKeysHoteles.CATEGORIAS_EMPLEADOS[i][1] %>
						</option>
					<% } %>
		           </select>
		        </td>
				<td>
				<td>Password(Solo números):</td>
				</td>
				<td><input id="<portlet:namespace />password"
					name="<portlet:namespace />password" size="20"
					maxlength="4" type="text" tabindex="5" onkeyup="soloNumeros(this);" onkeydown="allowOnlyDigits(event);"
					value='<%=personal.getPassword()!=null?personal.getPassword():""%>' />
				</td>
				
			</tr>
	    </table>
		
	</fieldset>
	
	<br>
	<input type="hidden" name="<portlet:namespace />id_personal"
		id="<portlet:namespace />id_personal" value="<%=id_personal%>" />
		
	<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" /> 

    <table>
	 <tr>
	  <td>


      <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" tabindex="6"
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
	var apellido=jQuery("#<portlet:namespace />apellido").val();
	var nombre=jQuery("#<portlet:namespace />nombre").val();
	var categoria=jQuery("#<portlet:namespace />categoria").val();
	var password=jQuery("#<portlet:namespace />password").val();
	
	if(codigo==null || codigo==""){
		alert("Debe ingresar un Id.");
		return false;
	}
	
	if(apellido==null || apellido==""){
		alert("Debe ingresar el Apellido");
		return false;
	}
	
	if(nombre==null || nombre==""){
		alert("Debe ingresar el Nombre");
		return false;
	}

	if(categoria==null || categoria==""){
		alert("Debe ingresar una Categoría");
		return false;
	}
	
	if(password==null || password==""){
		alert("Debe ingresar el Password");
		return false;
	}
	
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/configuracion_unidades_personal&tipo='+'MESAS';
    url +='&ptovta=<%=ptoVtaAfip%>'+'&login='+codigo;
    
    jQuery.ajax({   
	url: url,
	async:false,
	success: function(data){
		
		var obj = jQuery.parseJSON(data);
		
		if("" !=obj.empleado_str){
			
			var bAct= confirm("Ya existe este código.Actualiza?");
			if(bAct==false){
		        result=false;
				return false;
			}
			
		}else{
			
		}
        
	}
    });
    
    if(result==false) return false;
    
	return true;
}

function <portlet:namespace />salvarEdicion(){
	if (<portlet:namespace />validarCampos()==true) {
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_personal_abm" />'+
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

