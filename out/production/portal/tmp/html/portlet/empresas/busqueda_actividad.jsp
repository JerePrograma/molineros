<%@ include file="/html/portlet/empresas/init.jsp" %>

<%

String esEdicionStr=ParamUtil.getString(request,"esEdicion");
boolean esEdicion = true;
if (esEdicionStr != null && !esEdicionStr.trim().equals("")) {
	if (esEdicionStr.equals("true")){
		esEdicion = true;
	} else {
		esEdicion = false;
	}
}
String cod_actividad=ParamUtil.getString(request,"cod_actividad");
String actividad=ParamUtil.getString(request,"actividad");
String actividad_sec=ParamUtil.getString(request,"actividad_sec");
String cod_actividad_sec=ParamUtil.getString(request,"cod_actividad_sec");
String vista=ParamUtil.getString(request,"vista","H");   // H = Horizontal, V = Vertical
String tamanioDescrip=ParamUtil.getString(request,"tamanioDescrip","35");
String prefijo="empre_";

%>
<div style="display: table; vertical-align: top;">
	<%if(vista !=null && vista.equalsIgnoreCase("H")){ %>
		<div id="<portlet:namespace />divActividadesEmpresa" style="display: table-row;">
			<div id="F1_C1" style="display: table-cell;">
				<label><liferay-ui:message key="actividad-principal" />:&nbsp;&nbsp;</label>			
			</div>
			<div id="F1_C2" style="display: table-cell;">
				<input  id="<portlet:namespace />cod_actividad<%=prefijo%>" name="<portlet:namespace />cod_actividad<%=prefijo%>"  size="4" type="text" value="<%=cod_actividad%>" 
				<% if (esEdicion) { %> onBlur="javascript:<portlet:namespace />pierdeFoco('');" onKeyUp="javascript:<portlet:namespace />buscarActividadOnDiv(event,'')" <%} %>
				<% if (!esEdicion) { %> readonly='readonly'<%} %>/>			
			</div>
			<div id="F1_C3" style="display: table-cell;">
				<input id="<portlet:namespace />actividad<%=prefijo%>" name="<portlet:namespace />actividad<%=prefijo%>" size="<%=tamanioDescrip%>" type="text"  value="<%=actividad%>" 
				   <% if (esEdicion) { %> onKeyUp="javascript:<portlet:namespace />buscarActividadOnDiv(event,'')" onBlur="javascript:<portlet:namespace />pierdeFoco('');" <%} %>
				   <% if (!esEdicion) { %> readonly='readonly'<%} %>/>
				<input id="<portlet:namespace />act_seleccionada<%=prefijo%>" name="<portlet:namespace />act_seleccionada<%=prefijo%>" type="hidden" value=""/>
			</div>
			<div id="<portlet:namespace />btnBuscarActividad" style="display: table-cell;">
				<% if (esEdicion) { %>
					<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarActividad('');" tabindex="-1">
						Buscar
					</a>
				<%} %>
			</div>
			<div id="divActividad" style="display: table-cell;">
					<!-- <div id='divActividad' style="float:right;"></div> -->
			</div>
			<div id="separador" style="display: table-cell;">
				&nbsp;
			</div>
			<div id="F1_C6" style="display: table-cell;">
				<label><liferay-ui:message key="actividad-secundaria" />:</label>			
			</div>
			<div id="F1_C7" style="display: table-cell;">
				<input  id="<portlet:namespace />cod_actividad_sec<%=prefijo%>" name="<portlet:namespace />cod_actividad_sec<%=prefijo%>" size="4" type="text" value="<%=cod_actividad_sec%>" 
				<% if (esEdicion) { %> onBlur="javascript:<portlet:namespace />pierdeFoco('_sec');" onKeyUp="javascript:<portlet:namespace />buscarActividadOnDiv(event, '_sec')" <%} %>
				<% if (!esEdicion) { %> readonly='readonly'<%} %>/>			
			</div>
			<div id="F1_C8" style="display: table-cell;">
				<input id="<portlet:namespace />actividad_sec<%=prefijo%>" name="<portlet:namespace />actividad_sec<%=prefijo%>" size="<%=tamanioDescrip %>" type="text" value="<%=actividad_sec%>" 
	   			<% if (esEdicion) { %> onKeyUp="javascript:<portlet:namespace />buscarActividadOnDiv(event,'_sec')" onBlur="javascript:<portlet:namespace />pierdeFoco('_sec');" <%} %>
	   			<% if (!esEdicion) { %> readonly='readonly'<%} %>/>
	   			<input id="<portlet:namespace />act_seleccionada_sec<%=prefijo%>" name="<portlet:namespace />act_seleccionada_sec<%=prefijo%>" type="hidden" value=""/>
			</div>
			<div id="<portlet:namespace />btnBuscarActividad_sec" style="display: table-cell;">
				<% if (esEdicion) { %>
				<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarActividad('_sec');" tabindex="-1">
					Buscar
				</a>
				<%} %>
			</div>
			<div id="divActividad_sec" style="display: table-cell;"></div>
		</div>
	<%} %>
</div>

	

<script type="text/javascript">
<% if (null!=cod_actividad && !cod_actividad.trim().equals("") && !cod_actividad.trim().equals("0")){%>	
	jQuery("#<portlet:namespace />cod_actividad<%=prefijo%>").val("<%=cod_actividad%>");	
	<portlet:namespace />buscarActividad('');
<%}%>
<% if (null!=cod_actividad_sec && !cod_actividad_sec.trim().equals("") && !cod_actividad_sec.trim().equals("0")){%>	
jQuery("#<portlet:namespace />cod_actividad_sec<%=prefijo%>").val("<%=cod_actividad_sec%>");	
<portlet:namespace />buscarActividad('_sec');
<%}%>
var popupAct;
var popupActSec;
function <portlet:namespace />buscarActividad(tipo) {	
	var cod_actividad=jQuery("#<portlet:namespace />cod_actividad"+tipo+"<%=prefijo%>").val();	
    var actividad=jQuery("#<portlet:namespace />actividad"+tipo+"<%=prefijo%>").val();    
	if (!<portlet:namespace />validaFormAct(cod_actividad,actividad)){
		return false;
	}	 
	if(tipo=='_sec'){		
		popupActSec = Liferay.Popup({title:"<liferay-ui:message key="busqueda-actividad" />",modal:true,width:420});
	}else{
    	popupAct = Liferay.Popup({title:"<liferay-ui:message key="busqueda-actividad" />",modal:true,width:420});
	}
    
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_actividad&cod_actividad='+cod_actividad+
    		  '&actividad='+encodeURI(actividad)+'&prefijo=<%=prefijo%>'+'&tipo='+encodeURI(tipo);    
    if(tipo=='_sec'){
    	jQuery(popupActSec).load(url);
    }else{
		jQuery(popupAct).load(url);
    }
}
function <portlet:namespace />buscarActividadOnDiv(e, tipo){
	//Se modificó el campo, debemos cambiar el selecc	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />act_seleccionada"+tipo+"<%=prefijo%>").val() == "1" && (keyPressed==8 || keyPressed==46)){		
		jQuery("#<portlet:namespace />actividad"+tipo+"<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />cod_actividad"+tipo+"<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />act_seleccionada"+tipo+"<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />btnbuscarActividad"+tipo).show();
		return false;
	}
	
	var cod_actividad=jQuery("#<portlet:namespace />cod_actividad"+tipo+"<%=prefijo%>").val();	
    var actividad=jQuery("#<portlet:namespace />actividad"+tipo+"<%=prefijo%>").val();
    if((actividad.length>=3 || cod_actividad.length>2) && (keyPressed!=9 && keyPressed!=16)){    	
        if(cod_actividad.length >2){
        	jQuery("#<portlet:namespace />actividad"+tipo+"<%=prefijo%>").val("");
        }else{        	
    		jQuery("#<portlet:namespace />cod_actividad"+tipo+"<%=prefijo%>").val("");
        }
        
        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_actividad&cod_actividad='+cod_actividad+
		  	'&actividad='+encodeURI(actividad)+'&prefijo=<%=prefijo%>'+'&tipo='+tipo;
        
		jQuery("#divActividad"+tipo).load(url);		
		jQuery("#divActividad"+tipo).show();
    }else{
        jQuery("#divActividad"+tipo).hide("slow");
    }
}
function <portlet:namespace />cerrarDiv<%=prefijo%>(tipo){
	jQuery("#divActividad"+tipo).hide("slow");		
}

function <portlet:namespace />cerrar<%=prefijo%>(tipo){
	<portlet:namespace />cerrarDiv(tipo);
	if(popupAct && tipo=="_sec"){		
		Liferay.Popup.close(popupActSec);
	}else{
		Liferay.Popup.close(popupAct);
	}	
}


function <portlet:namespace />cerrarDivAct<%=prefijo%>(tipo){
	jQuery("#divActividad"+tipo).hide("slow");
}
function <portlet:namespace />cerrarAct<%=prefijo%>(tipo){	
	<portlet:namespace />cerrarDivAct<%=prefijo%>(tipo);
	if(popupAct && tipo=="_sec"){		
		Liferay.Popup.close(popupActSec);
	}else{
		Liferay.Popup.close(popupAct);
	}		
}

function <portlet:namespace />pierdeFoco(tipo){
	var seleccionada=jQuery("#<portlet:namespace />act_seleccionada"+tipo+"<%=prefijo%>").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDiv<%=prefijo%>(tipo);
		return false;
	}else{				
		return false; 
	}	
}

function <portlet:namespace />validaFormAct(cod, act){	
	 if(trim(cod).length==0 && trim(act).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function <portlet:namespace />resetValid(tipo) {
	if (jQuery("#<portlet:namespace />cod_actividad"+tipo+"<%=prefijo%>").val() != "") {
		jQuery("#<portlet:namespace />act_seleccionada"+tipo+"<%=prefijo%>").val("1")
	}
}
<portlet:namespace />resetValid('');
<portlet:namespace />resetValid('_sec');

</script>