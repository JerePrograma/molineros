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
	
	String ptoVtaAfip="00030";

	try{
		ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
	}catch(Exception e){
		//ptoVtaAfip="0000";
		ptoVtaAfip="00030";
	}
	 
	List<Personal> empleados = HotelesServiceUtil.getPersonal(ptoVtaAfip, "MOZO", null);

%>

<form action="" method="post" name="<portlet:namespace />fmS">

	<fieldset class="block-labels"> 
		<legend>Asignación de Mesas</legend>
		
		<label>Empleado:</label>
		<select name="<portlet:namespace />empleado"
			              id="<portlet:namespace />empleado" 
			              onchange="javascript:mostrarMesas()">
			        <option value="">Seleccione Empleado</option>
			                
			        <%for(Personal c:empleados) {%>
						     <option
							    value="<%=c.getId() %>"	>
							      <%=c.getApellido()+" "+c.getNombre() %>
						     </option>
					<%}%>
		</select>
		<div id="mesas_empleado">
		</div>
				
	</fieldset>
	
	<br>
	<table>
	 <tr>
	  <td>
      <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" 
	  />
	</td>
	</tr>
	</table>      
</form>

<script type="text/javascript">
function mostrarMesas(){
	var idPersonal=jQuery("#<portlet:namespace />empleado").val();

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/asignar_unidades&tipo=MESAS';
	    url +='&ptovta='+'<%=ptoVtaAfip%>';
	    url +='&idpersonal='+idPersonal;
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			jQuery("#mesas_empleado").html(obj.unidades);
		}
	});
}


function <portlet:namespace />salvarEdicion(){
   var selectedItems = new Array();
   var idPersonal=jQuery("#<portlet:namespace />empleado").val();

   jQuery("input[@name='unidades[]']:checked").each(function(){
	   selectedItems.push(jQuery(this).val());
   });

   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/hoteles_asignar_unidades&tipo=MESAS';
   url +='&cmd=update';
   url +='&seleccion='+selectedItems;
   url +='&ptovta='+'<%=ptoVtaAfip%>';
   url +='&idpersonal='+idPersonal;
   jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			
			if(obj.rta=="true"){
				alert("Actualización Exitosa");
			}else{
				alert("Actualización Fallida");
			}
		}
	});
   
   return false;		
}


function mayus(e) {
    e.value = e.value.toUpperCase();
}


</script>

