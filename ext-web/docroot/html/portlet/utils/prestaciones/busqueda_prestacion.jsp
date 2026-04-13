<%@ include file="/html/portlet/utils/prestaciones/init.jsp" %>

<%

//String dinámico que se le debe pasar a esta pagina para que sepa a que direccion redireccionar
//con el formato /<nombre_portlet>/buscar_prestador
//ej: /tesoreria/buscar_prestador
//esto se debe corresponder con el action definido en struts-config
//de la forma: <action path="/<nombre_portlet>/buscar_prestador" forward="portlet.utils.prestador.view" />
String searchURL = ParamUtil.getString(request, "search_url");
String esEditableStr = ParamUtil.getString(request, "esEditable");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEditable = Boolean.parseBoolean(esEditableStr);

String idPrestacion = ParamUtil.getString(request, "id_prestacion");
String codigo = ParamUtil.getString(request, "codigo");
String prestacion = ParamUtil.getString(request, "prestacion");
String discapacidad = ParamUtil.getString(request, "discapacidad");
String cuil = ParamUtil.getString(request, "cuil");

String suf = ParamUtil.getString(request, "suf", "");
if (suf == null) {
	suf = "";
}

String tipo_reintegro = (String)request.getAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION);

if (idPrestacion == null){
	idPrestacion = "";
}
if (prestacion == null){
	prestacion = "";
}
if (discapacidad == null){
	discapacidad = "";
}

if (cuil == null){
	cuil = "";
}
%>

 <table>
 <tr>
	<td>
		<liferay-ui:message key="codigo" />&nbsp;&nbsp;
		<input id="<portlet:namespace />discapacidad<%=suf%>" name="<portlet:namespace />discapacidad<%=suf%>" type="hidden" value="<%=discapacidad %>"/>
		<input id="<portlet:namespace />id_prestacion<%=suf%>" name="<portlet:namespace />id_prestacion<%=suf%>" type="hidden" value=""/>
		<input id="<portlet:namespace />codigo<%=suf%>" name="<portlet:namespace />codigo<%=suf%>" maxlenght="6" size="6" type="text" value="" 
		onBlur="javascript:<portlet:namespace />pierdeFocoPresc();" onKeyUp="javascript:<portlet:namespace />buscarPrestacionOnDiv(event)" <%= !esEditable ? " readonly='readonly'" : ""  %>/>
	</td>
	<td>
		&nbsp;
		<liferay-ui:message key="descripcion" />:&nbsp;&nbsp;
		<input id="<portlet:namespace />prestacion<%=suf%>" name="<portlet:namespace />prestacion<%=suf%>" size="60" type="text" 
	   value="" onKeyUp="javascript:<portlet:namespace />buscarPrestacionOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFocoPresc();" <%= !esEditable ? " readonly='readonly'" : ""  %>/>
	</td>
	<td>
		<div id="<portlet:namespace />btnBuscarPrestacion<%=suf%>" style="float:right;">
		 <% if (esEditable) {%>
		<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarPrestacion();" tabindex="-1">
			Buscar
		</a>
		<%} %>
		</div>
	</td>
</tr>	
</table>
<input id="<portlet:namespace />pres_seleccionada<%=suf%>" name="<portlet:namespace />pres_seleccionada<%=suf%>" type="hidden" value=""/>
<input id="<portlet:namespace />pres_transporte<%=suf%>" name="<portlet:namespace />pres_transporte<%=suf%>" type="hidden" value=""/>
<input id="<portlet:namespace />tope_cantidad<%=suf%>" name="<portlet:namespace />tope_cantidad<%=suf%>" type="hidden" value=""/>
<input id="<portlet:namespace />tope_importe<%=suf%>" name="<portlet:namespace />tope_importe<%=suf%>" type="hidden" value=""/>
<input id="<portlet:namespace />tope_individ_cantidad<%=suf%>" name="<portlet:namespace />tope_individ_cantidad<%=suf%>" type="hidden" value=""/>
<input id="<portlet:namespace />tope_individ_importe<%=suf%>" name="<portlet:namespace />tope_individ_importe<%=suf%>" type="hidden" value=""/>

<div id='divPrestacion' style="float:right;">
</div>

<script type="text/javascript">
var popup;
function <portlet:namespace />buscarPrestacion() {
	var id_prestacion=jQuery("#<portlet:namespace />id_prestacion<%=suf%>").val();
	var codigo=jQuery("#<portlet:namespace />codigo<%=suf%>").val();
    var prestacion=jQuery("#<portlet:namespace />prestacion<%=suf%>").val();
    var discapacidad=jQuery("#<portlet:namespace />discapacidad<%=suf%>").val();
	if (!<portlet:namespace />validaFormPresc(id_prestacion,codigo,prestacion)){
		return false;
	}
	var ext = '&protesis=0';
	<% if (tipo_reintegro != null && tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) 
	{
	%>
		ext='&protesis=1';
	<%
	} else if (tipo_reintegro != null && tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
    %>
		ext = '&protesis=2';
	<%
	} else if (tipo_reintegro == null) {
	%>
		if ( discapacidad != '' && discapacidad == 'true') {
			ext = '&protesis=3';
		}
	<%
	}
	%>
    popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-prestaciones" />",modal:true,width:420});
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>&id_prestacion='+id_prestacion+
    '&codigo='+codigo+'&prestacion='+encodeURIComponent(prestacion)+ext;
    
  //DS - Agregado para traer el plan del afiliado y buscar los importes por ese plan	
	var cuil="";
	var fechaPDia="";
	var fechaPMes="";
	var fechaPAnio="";
	var idPlanAfi="";
    try{
    	cuil=jQuery("#<portlet:namespace />cuil").val();
    }catch (err){}
    try{
    	fechaPDia=jQuery("#<portlet:namespace />prestacionFechaDia").val();
    }catch (err){}
    
    try{
    	fechaPMes=jQuery("#<portlet:namespace />prestacionFechaMes").val();
    }catch (err){}
    
    try{
    	fechaPAnio=jQuery("#<portlet:namespace />prestacionFechaAnio").val();
    }catch (err){}
    
    try{
    	idPlanAfi=jQuery("#<portlet:namespace />id_plan_afi").val();
    }catch (err){}
    
	url += '&cuil='+encodeURIComponent(cuil)+
	       '&dia='+ encodeURIComponent(fechaPDia)+
	       '&mes='+ encodeURIComponent(fechaPMes)+
	       '&anio='+ encodeURIComponent(fechaPAnio)+
	       '&id_plan_afi='+ encodeURIComponent(idPlanAfi);
    //DS - Fin Agregado para traer el plan del afiliado		

    jQuery(popup).load(url);
}
function <portlet:namespace />buscarPrestacionOnDiv(e){
	//Se modificó el campo, debemos cambiar el selecc
	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val() == "1" && (keyPressed==8 || keyPressed==46)){
		jQuery("#<portlet:namespace />prestacion<%=suf%>").val("");
		jQuery("#<portlet:namespace />id_prestacion<%=suf%>").val("");
		jQuery("#<portlet:namespace />codigo<%=suf%>").val("");
		jQuery("#<portlet:namespace />tope_cantidad<%=suf%>").val("");
		jQuery("#<portlet:namespace />tope_importe<%=suf%>").val("");
		jQuery("#<portlet:namespace />tope_individ_cantidad<%=suf%>").val("");
		jQuery("#<portlet:namespace />tope_individ_importe<%=suf%>").val("");
		jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val("");
		jQuery("#<portlet:namespace />pres_transporte<%=suf%>").val("");
		jQuery("#<portlet:namespace />btnBuscarPrestacion<%=suf%>").show();

		if (document.getElementById("<portlet:namespace />cantidad") != null){
			document.getElementById("<portlet:namespace />cantidad").value = "";
		}
		if (document.getElementById("<portlet:namespace />importe") != null){
			document.getElementById("<portlet:namespace />importe").value = "";
		}
		try {
			if (document.getElementById("<portlet:namespace />total") != null){
				document.getElementById("<portlet:namespace />total").value = "";
			}
		} catch (err){}
		return false;
	}	
	
	var id_prestacion=jQuery("#<portlet:namespace />id_prestacion<%=suf%>").val();
	var codigo = jQuery("#<portlet:namespace />codigo<%=suf%>").val();	
    var prestacion=jQuery("#<portlet:namespace />prestacion<%=suf%>").val();
    var discapacidad=jQuery("#<portlet:namespace />discapacidad<%=suf%>").val();
    
    
    if(jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val() != "1" && (prestacion.length>=8 || codigo.length>=4)){
        if(codigo.length >=4){
        	jQuery("#<portlet:namespace />prestacion").val("");        	
        }else if (prestacion.length>=8) {
    		jQuery("#<portlet:namespace />codigo").val("");    		
        }
        var ext = '&protesis=0';
    	<% if (tipo_reintegro != null && tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) 
    	{
    	%>
    		ext = '&protesis=1';
    	<%
    	} else if (tipo_reintegro != null && tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
        %>
    		ext = '&protesis=2';
    	<%
    	} else if (tipo_reintegro == null) {
		%>
		if ( discapacidad != '' && discapacidad == 'true') {
			ext = '&protesis=3';
		}
		<%
		}
		%>
    		            
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>&id_prestacion='+id_prestacion+
		'&codigo='+codigo+'&prestacion='+encodeURIComponent(prestacion)+ext;
		
//DS - Agregado para traer el plan del afiliado y buscar los importes por ese plan	
		var cuil="";
		var fechaPDia="";
		var fechaPMes="";
		var fechaPAnio="";
		var idPlanAfi="";
	    try{
	    	cuil=jQuery("#<portlet:namespace />cuil").val();
	    }catch (err){}
	    try{
	    	fechaPDia=jQuery("#<portlet:namespace />prestacionFechaDia").val();
	    }catch (err){}
	    
	    try{
	    	fechaPMes=jQuery("#<portlet:namespace />prestacionFechaMes").val();
	    }catch (err){}
	    
	    try{
	    	fechaPAnio=jQuery("#<portlet:namespace />prestacionFechaAnio").val();
	    }catch (err){}
	    
	    try{
	    	idPlanAfi=jQuery("#<portlet:namespace />id_plan_afi").val();
	    }catch (err){}
	    
		url += '&cuil='+encodeURIComponent(cuil)+
		       '&dia='+ encodeURIComponent(fechaPDia)+
		       '&mes='+ encodeURIComponent(fechaPMes)+
		       '&anio='+ encodeURIComponent(fechaPAnio)+
		       '&id_plan_afi='+ encodeURIComponent(idPlanAfi);
//DS - Fin Agregado para traer el plan del afiliado		
	
		jQuery("#divPrestacion").load(url);
		jQuery("#divPrestacion").show();
    }else{
    	jQuery("#divPrestacion").hide("slow");
    }    
}
function <portlet:namespace />cerrarDivPresc(){
	jQuery("#divPrestacion").hide("slow");		
}
function <portlet:namespace />cerrarPresc(){
	<portlet:namespace />cerrarDivPresc();
	if(popup){
		Liferay.Popup.close(popup);
	}
}
function <portlet:namespace />pierdeFocoPresc(){
	var seleccionada=jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivPresc();
		return false;
	}else{
		return false; 
	}
}
function <portlet:namespace />validaFormPresc(id_prestacion, codigo, prestacion){
	 if(trim(id_prestacion).length==0 && trim(prestacion).length==0 && trim(codigo).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function pasarParametrosAParentPresc(id, cod, param, topeCant, topeImp, topeIndCant, topeIndImp) {
	seleccionaCamposPrescBis(id, cod, param, topeCant, topeImp, topeIndCant, topeIndImp);
    <portlet:namespace />cerrarPresc();
 	try {
		<portlet:namespace />buscarContratoPrestador();
    } catch (err) {}
 }

<c:if test="<%= (tipo_reintegro != null && (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) || tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA))) || discapacidad.length() > 0 %>">
function pasarParametroImporte(importe) {	
	if (jQuery("#<portlet:namespace />importe").val().length == 0 || 
			(jQuery("#<portlet:namespace />importe").val().length > 0 && (jQuery("#<portlet:namespace />importe").val() == '0' || jQuery("#<portlet:namespace />importe").val() == '0.0'))) {		
		jQuery("#<portlet:namespace />importe").val(importe);
	}
	sumarTodo();
	
}
</c:if>

function seleccionaCamposPrescBis(id, cod, param, topeCant, topeImp, topeIndCant, topeIndImp) {
    jQuery("#<portlet:namespace />id_prestacion<%=suf%>").val(id);
    jQuery("#<portlet:namespace />codigo<%=suf%>").val(cod);    
    jQuery("#<portlet:namespace />prestacion<%=suf%>").val(param);
    jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val("1");
	jQuery("#<portlet:namespace />tope_cantidad<%=suf%>").val(topeCant);
	jQuery("#<portlet:namespace />tope_importe<%=suf%>").val(topeImp);
	jQuery("#<portlet:namespace />tope_individ_cantidad<%=suf%>").val(topeIndCant);
	jQuery("#<portlet:namespace />tope_individ_importe<%=suf%>").val(topeIndImp);
    jQuery("#<portlet:namespace />btnBuscarPrestacion<%=suf%>").hide();
    if (cod == '<%=WebKeysLiquidaciones.PRESTACION_TRANSPORTE%>') {
    	jQuery("#<portlet:namespace />pres_transporte<%=suf%>").val("1");
    	try {
    		<portlet:namespace />mostrarDivTransporte();
        } catch (err) {}    	
    } else {
    	jQuery("#<portlet:namespace />pres_transporte<%=suf%>").val("");
    	try {
    		<portlet:namespace />ocultarDivTransporte();
        } catch (err) {}
    }
      
 }

function seleccionaCamposPresc(id, cod, param) {
    jQuery("#<portlet:namespace />id_prestacion<%=suf%>").val(id);
    jQuery("#<portlet:namespace />codigo<%=suf%>").val(cod);    
    jQuery("#<portlet:namespace />prestacion<%=suf%>").val(param);
    jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val("1");
    jQuery("#<portlet:namespace />btnBuscarPrestacion<%=suf%>").hide();
    if (cod == '<%=WebKeysLiquidaciones.PRESTACION_TRANSPORTE%>') {
    	jQuery("#<portlet:namespace />pres_transporte<%=suf%>").val("1");
    	try {
    		<portlet:namespace />mostrarDivTransporte();
        } catch (err) {}
    } else {
    	jQuery("#<portlet:namespace />pres_transporte<%=suf%>").val("");
    	try {
    		<portlet:namespace />ocultarDivTransporte();
        } catch (err) {}    	
    }
    
}

var idPrestacionJs = "<%=idPrestacion%>";
var prestacionJs = "<%=prestacion%>";
var codigoJs = "<%=codigo%>";
if (idPrestacionJs != "" && codigoJs != "") {
	seleccionaCamposPresc(idPrestacionJs, codigoJs, prestacionJs);
	<portlet:namespace />buscarPrestacion();
	
}

</script>