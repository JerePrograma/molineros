<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>

<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="ar.com.ospim.global.beans.Parentesco" %>
<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.tesoreria.beans.AjustePlanSuperador" %>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
 	AjustePlanSuperador precio=(AjustePlanSuperador)request.getSession().getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION);
	
	List<Parentesco> parentescos = (List<Parentesco>)request.getSession().getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PARENTESCOS);
	/*
	if(parentescos ==null || parentescos.isEmpty()){
		parentescos=TraeListasServiceUtil.getParentescosFacturables();
		request.getSession().setAttribute(WebKeysTesoreria.PRECIO_EN_SESSION_PARENTESCOS,parentescos);
	}
	*/
	List<Plan> planes=(List<Plan>)request.getSession().getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PLANES);
	/*
	if(planes ==null || planes.isEmpty()){
		planes=TraeListasServiceUtil.getPlanesFacturables();
		request.getSession().setAttribute(WebKeysTesoreria.PRECIO_EN_SESSION_PLANES,planes);
	}
	*/
	List<Provincia> provinciasPrecio =  (List<Provincia>)request.getSession().getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PROVINCIAS);
	/*
	if(provinciasPrecio ==null || provinciasPrecio.isEmpty()){
		provinciasPrecio=TraeListasServiceUtil.getProvinciasFacturables();
		request.getSession().setAttribute(WebKeysTesoreria.PRECIO_EN_SESSION_PROVINCIAS,provinciasPrecio);
	}
	*/
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	
	
	int id_precio=precio!=null && precio.getId() !=null ?(int)precio.getId():0;
	/*
	if(precio==null || id_precio==0){
		precio= new PrecioPlanSuperador();
		request.getSession().setAttribute(WebKeysTesoreria.PRECIO_EN_SESSION,precio);
	}else if(id_precio>0){
	
	}
	*/
	Integer entidad = WebKeysGlobal.OSPIM;
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		entidad = WebKeysGlobal.UOMA;
		portlet_name = "uoma";
	}
	
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		entidad = WebKeysGlobal.UOMA;
		portlet_name = "farmacia";
	}
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
			
    Calendar fechaVigenciaDde = CalendarFactoryUtil.getCalendar();
    if(precio==null || precio.getFechaDesde() ==null){
	    fechaVigenciaDde.setTime(new Date());
    }else{
    	fechaVigenciaDde.setTime(precio.getFechaDesde());
    }  
	
    Calendar fechaVigenciaHta = CalendarFactoryUtil.getCalendar();
    if(precio==null || precio.getFechaHasta() ==null){
	    fechaVigenciaHta.setTime(new Date());
    }else{
    	fechaVigenciaHta.setTime(precio.getFechaHasta());
    } 
	
%>

<form action="" method="post" name="<portlet:namespace />fmCCTO">

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	
	<fieldset class="block-labels">
	
	<legend>Ajuste</legend>
	
	
		
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
			<tr>
			  <td><label>Id:</label></td>
				<td><input id="<portlet:namespace />listaId"
					name="<portlet:namespace />listaId" size="20"
					maxlength="20" type="text" readonly="readonly"
					value='<%=precio.getId()==null?"": precio.getId()%>' /></td>
			   <td>
			
			
			
			   <td><label>Descripción:</label></td>
				<td><input id="<portlet:namespace />descripcion"
					name="<portlet:namespace />descripcion" size="145"
					maxlength="145" type="text"
					value='<%=precio.getDescripcion()==null?"":precio.getDescripcion() %>' />
				</td>
			</tr>	
		</table>	
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">	
			<tr>
	            <td><label>Desde:</label></td>
				<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaDesdeDia"
							dayValue="<%=precio !=null && precio.getFechaDesde() !=null?fechaVigenciaDde.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
							dayNullable="<%= false %>"
							monthParam="fechaDesdeMes"
							monthValue="<%=precio !=null && precio.getFechaDesde()!=null?fechaVigenciaDde.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
							monthNullable="<%= false %>"			
							yearParam="fechaDesdeAnio"
							yearValue="<%=precio !=null && precio.getFechaDesde()!=null?fechaVigenciaDde.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
							yearNullable="<%= false %>"
							yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
			  </td>
			
			  <td><label>Hasta:</label></td>
				<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaHastaDia"
							dayValue="<%=precio !=null && precio.getFechaHasta() !=null?fechaVigenciaHta.get(Calendar.DAY_OF_MONTH ):-1%>"
							dayNullable="<%= true %>"
							monthParam="fechaHastaMes"
							monthValue="<%=precio !=null && precio.getFechaHasta()!=null?fechaVigenciaHta.get(Calendar.MONTH ):-1%>"
							monthNullable="<%= true %>"			
							yearParam="fechaHastaAnio"
							yearValue="<%=precio !=null && precio.getFechaHasta()!=null?fechaVigenciaHta.get(Calendar.YEAR ):-1 %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
							yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR)+50%>"
							firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
			  </td>
			  
             
             
             <td><label>Edad Desde:</label></td>
				<td><input id="<portlet:namespace />edadDde" name="<portlet:namespace />edadDde" size="15"
					maxlength="3" type="text" value='<%=precio.getEdadDesde()==null?"": precio.getEdadDesde()%>' /></td>
			   
			 <td><label>Hasta:</label></td>
				<td><input id="<portlet:namespace />edadHta" name="<portlet:namespace />edadHta" size="15"
					maxlength="3" type="text" value='<%=precio.getEdadHasta()==null || precio.getEdadHasta().equals(0) ?"":precio.getEdadHasta()%>' /></td>
					  
            
			 
			 </tr>   
			 <tr>
			    <td><label>Porcentaje:</label></td>
				<td><input id="<portlet:namespace />porcentaje" name="<portlet:namespace />porcentaje" size="15"
					maxlength="20" type="text" value='<%=precio.getPorcentaje()==null?"": precio.getPorcentaje()%>' /></td>
			   
			    <td><label>Importe:</label></td>
				<td><input id="<portlet:namespace />importe" name="<portlet:namespace />importe" size="15"
					maxlength="20" type="text" value='<%=precio.getImporte()==null || precio.getImporte().equals(0) ?"":precio.getImporte()%>' /></td>
			</tr>					
			 <tr><td colspan="5"><label style="color:blue;font-weight: bold;">Valores positivos para DEBITO - Valores negativos para BONIFICACIONES</label></td></tr>
			 
			<tr>
			  <td><label>Solo Uso Personalizado:</label></td>
			  <td><input type="checkbox" id="<portlet:namespace />usoPersonalizado" 
			     name="<portlet:namespace />usoPersonalizado" <%=precio.getSoloUsoPersonalizado() !=null && precio.getSoloUsoPersonalizado() ?"checked=\"checked\"":"" %>/></td>
			  <td colspan="5"><label style="color:blue;font-weight: bold;">El Ajuste con esta marca aparecerá como seleccionable en el proceso de cotización </label></td>   
			</tr>
		</table>
		
		
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
	    <tr><td width="80%">
	
		
		
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">	
		  <tr>
		    <td>
		       <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">	
		       <tr>
		        <td>
		      	<fieldset class="block-labels">
		        <legend>Parentesco</legend>
		         <div id="<portlet:namespace />divParentesco">
		         
		              <liferay-util:include
						page='/html/portlet/tesoreria/facturacion/facturacion_ajustes_edit_parentescos.jsp'>
					  </liferay-util:include>
		          
		         </div>
		        </fieldset>
		        </td>
		        
		        
		       </tr>
		       <tr>
		         <td>
		      	 <fieldset class="block-labels">
		          <legend>Planes</legend>
		            <div id="<portlet:namespace />divPlanes">
		         
		              <liferay-util:include
						page='/html/portlet/tesoreria/facturacion/facturacion_ajustes_edit_planes.jsp'>
					  </liferay-util:include>
		          
		            </div>
		         </fieldset>
		         </td>
		       </tr>
		       
		       
		       
		      </table>  
		    </td>
		 </tr>
		</table>
	</td>
	<td width="20%" align="left" valign="top">
	   <fieldset class="block-labels">
		<legend>Cuiles</legend>
	    <table>
	    <tr>
	    
		<td><label>Cuil Titular:</label>
		     <input id="<portlet:namespace />cuil" name="<portlet:namespace />cuil" size="20" maxlength="20" type="text" />
		</td>
		</td>
		
		
		<td>
		  <input id="<portlet:namespace />agregar_valor" 	value="Agregar"     title="Agregar Valor"
		       onClick="javascript: <portlet:namespace />agregarValor();" type="button"/>
		</td>
		</tr>	
		<tr>
		 <td>
		 <div id="<portlet:namespace />divValores">
              <liferay-util:include	page='/html/portlet/tesoreria/facturacion/facturacion_ajustes_edit_cuiles.jsp'> </liferay-util:include>
		          
		 </div>
		 </td>
		</tr> 
	   </table>
	   </fieldset>
	   
	   <table>
	           <tr>
		         <td>
		      	 <fieldset class="block-labels">
		          <legend>Provincias</legend>
		            <div id="<portlet:namespace />divLocalidades">
		         
		              <liferay-util:include
						page='/html/portlet/tesoreria/facturacion/facturacion_ajustes_edit_localidades.jsp'>
					  </liferay-util:include>
		          
		            </div>
		         </fieldset>
		         </td>
		       </tr>
		</table>
	</td>
	</tr>
	</table>	
	</fieldset>
	<br>
	<input type="hidden" name="<portlet:namespace />id_precio"
		id="<portlet:namespace />id_precio" value="<%=id_precio%>" />
		
    		
		
	<input type="hidden" value="" name="view" id="view" /> 

    <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" 
	 />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 
	 <input id="<portlet:namespace />atras"
		value="Atrás"
		title="Atrás"
		onClick="javascript: <portlet:namespace />atrasEdicion();"
		type="button" 
	 />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 
	 
	  <input id="<portlet:namespace />limpiar"
		value="Limpiar"
		title="Limpiar"
		onClick="javascript: <portlet:namespace />limpiarEdicion();"
		type="button" 
	 />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</form>

<script type="text/javascript">

var popupNM;
<portlet:namespace />initDateFields();

function <portlet:namespace />initDateFields(){}

function <portlet:namespace />agregarParentesco(){
		var params = jQuery("#<portlet:namespace />parentesco_disponible").val();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_ajustes'
			+	'&<%= Constants.CMD%>=' + 'agregarParentesco'
			+ '&parentescosid=' + encodeURI(params); 	
			
			jQuery('#<portlet:namespace />divParentesco').load(url, function() {});
		
	return false;	
		
}

function <portlet:namespace />sacarParentesco(){
	var params = jQuery("#<portlet:namespace />parentesco_asignados").val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_ajustes'
		+	'&<%= Constants.CMD%>=' + 'sacarParentesco'
		+ '&parentescosid=' + encodeURI(params); 	
		
		jQuery('#<portlet:namespace />divParentesco').load(url, function() {});
	
return false;	
	
}

function <portlet:namespace />agregarPlan(){
	var params = jQuery("#<portlet:namespace />planes_disponible").val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_ajustes'
		+	'&<%= Constants.CMD%>=' + 'agregarPlan'
		+ '&planesid=' + encodeURI(params); 	
		
		jQuery('#<portlet:namespace />divPlanes').load(url, function() {});
	
return false;	
	
}

function <portlet:namespace />sacarPlan(){
var params = jQuery("#<portlet:namespace />planes_asignados").val();

var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_ajustes'
	+	'&<%= Constants.CMD%>=' + 'sacarPlan'
	+ '&planesid=' + encodeURI(params); 	
	
	jQuery('#<portlet:namespace />divPlanes').load(url, function() {});

return false;	

}

function <portlet:namespace />agregarLocalidad(){
	var params = jQuery("#<portlet:namespace />provincias_disponible").val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_ajustes'
		+	'&<%= Constants.CMD%>=' + 'agregarLocalidad'
		+ '&provinciasid=' + encodeURI(params); 	
		
		jQuery('#<portlet:namespace />divLocalidades').load(url, function() {});
	
return false;	
	
}

function <portlet:namespace />sacarLocalidad(){
var params = jQuery("#<portlet:namespace />provincias_asignados").val();

var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_ajustes'
	+	'&<%= Constants.CMD%>=' + 'sacarLocalidad'
	+ '&provinciasid=' + encodeURI(params); 	
	
	jQuery('#<portlet:namespace />divLocalidades').load(url, function() {});

return false;	

}

function <portlet:namespace />agregarValor(){
	var params = jQuery("#<portlet:namespace />cuil").val();
	
	if(params==null || params==''){
		alert("El CUIL no puede estar vacío");
		return false;
	}
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_ajustes'
		+	'&<%= Constants.CMD%>=' + 'agregarValor'
		+ '&cuil=' + encodeURI(params); 	
		
		jQuery('#<portlet:namespace />divValores').load(url, function() {jQuery("#<portlet:namespace />cuil").val("");});
	
return false;	
	
}


function <portlet:namespace />validarCampos(){
	var result = true;
	var descripcion =jQuery('#<portlet:namespace />descripcion').val();
	if(descripcion==null || ""==descripcion){
		alert("Debe ingresar una Descripción ");
		return false;
	}

	var importe =jQuery('#<portlet:namespace />importe').val();
	var porcentaje =jQuery('#<portlet:namespace />porcentaje').val();
	if(importe==0  && porcentaje==0){
		alert("Debe ingresar un Importe o un porcentaje");
		return false;
	}
	
	
	var fechaDdeDia=jQuery("#<portlet:namespace />fechaDesdeDia").val();
	var fechaDdeMes=jQuery("#<portlet:namespace />fechaDesdeMes").val();
	var fechaDdeAnio=jQuery("#<portlet:namespace />fechaDesdeAnio").val();
	var fechaHtaDia=jQuery("#<portlet:namespace />fechaHastaDia").val();
	var fechaHtaMes=jQuery("#<portlet:namespace />fechaHastaMes").val();
	var fechaHtaAnio=jQuery("#<portlet:namespace />fechaHastaAnio").val();
	var edadDde=jQuery("#<portlet:namespace />edadDde").val();
	var edadHta=jQuery("#<portlet:namespace />edadHta").val();
		
	return true;
}

function <portlet:namespace />salvarEdicion(){
	if (<portlet:namespace />validarCampos()) {
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/tesoreria/facturacion_ajustes" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
		'</liferay-portlet:renderURL>';
		submitForm(document.<portlet:namespace />fmCCTO, url);
	}
	return false;		
}


function <portlet:namespace />atrasEdicion(){
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/tesoreria/facturacion_ajustes" />'+
		'<liferay-portlet:param name="cmd" value="atras"/>'+
		'</liferay-portlet:renderURL>';
		
		submitForm(document.<portlet:namespace />fmCCTO, url);
	return false;		
}


function <portlet:namespace />limpiarEdicion() {
	var params = "&<%= Constants.CMD %>=NEW" ;
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/tesoreria/facturacion_ajustes" /></portlet:renderURL>';
	url = url + params;
	document.<portlet:namespace />fmCCTO.method = 'post';
	submitForm(document.<portlet:namespace />fmCCTO, url);		
}
</script>

