<%@ include file="/html/portlet/novedades/init.jsp"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%

/* List<CategoriaLaboral> categorias=TraeListasServiceUtil.getCategoriasLaborales();
List<SituacionRevista> situacionesRevista=TraeListasServiceUtil.getSituacionRevista(); */
List<CategoriaLaboral> categorias=(ArrayList<CategoriaLaboral>) portletSession.getAttribute(WebKeysAfiliados.CATEGORIAS_EMPRESA_EN_SESSION, PortletSession.APPLICATION_SCOPE);
List<SituacionRevista> situacionesRevista=(ArrayList<SituacionRevista>) portletSession.getAttribute(WebKeysAfiliados.SITUACIONES_REVISTA_EMPRESA_EN_SESSION, PortletSession.APPLICATION_SCOPE);

PreAfiliado afiliado = (PreAfiliado)session.getAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);	
String cuit=null!=request.getParameter("cuit")?(String)request.getParameter("cuit"):"";
String razon_soc=null!=request.getParameter("razon_soc")?(String)request.getParameter("razon_soc"):"";
String sucursal=null!=request.getParameter("sucur")?(String)request.getParameter("sucur"):"";
String esEdicionStr = request.getParameter("esEdicion");
boolean esEdicion=false;
if(esEdicionStr == null || esEdicionStr.equalsIgnoreCase("true")){
	esEdicion = true;
}

Calendar ingreFecha = null;
ingreFecha = CalendarFactoryUtil.getCalendar();
if(afiliado != null){
	if(afiliado.getFecha_ingre()!=null){
		ingreFecha.setTime(afiliado.getFecha_ingre());
	}else{
		ingreFecha.setTime(new Date());
	}
	cuit = afiliado.getCuit();
}else{
	ingreFecha.setTime(new Date());

}
String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_AFI_1_")){
	portlet_name = "afiliados";
}
%>
<script type="text/javascript" >
/* jQuery('#<portlet:namespace />cuit_empleador').val('30531143856'); 
<portlet:namespace />buscarEmpleador();
<portlet:namespace />cerrarDiv();
<portlet:namespace />cerrar(); */
</script>

<fieldset class="block-labels">
<legend><liferay-ui:message	key="situacion-laboral" /></legend>
<table class="lfr-table">
	<tr>
		<td colspan="1"><liferay-ui:message key="categoria" /></td>
		<td colspan="4"><select name="<portlet:namespace/>categoria"
			id="<portlet:namespace/>categoria"
			onChange="javascript:<portlet:namespace/>cambiaCategoria();" <%if(!esEdicion){ %> disabled="disabled" <%} %> >
			<%
										for (CategoriaLaboral categoria : categorias) {
									%>
			<!-- 2ble IF -->				
			<option <%= afiliado!=null && afiliado.getId_categoria().intValue()==categoria.getId_categoria()? "selected" : "" %>
				    <%= afiliado==null && categoria.getId_categoria()==11 ? "selected" : "" %>
				value="<%= categoria.getId_categoria()%>"><%=categoria.getDescripcion()%></option>
			<%
									}
									%>
		</select></td>
	</tr>
	<tr><td colspan="5">&nbsp;</td></tr>
	<tr>	
		<td><liferay-ui:message key="situacion-revista" /></td>
		<td><select name="<portlet:namespace/>situRevista"
			id="<portlet:namespace/>situRevista" <%if(!esEdicion){ %> disabled="disabled" <%} %> >
			<%
										for (SituacionRevista situRevista: situacionesRevista) {
									%>
			<!-- 2ble IF -->
			<option <%= afiliado!=null && afiliado.getId_revista().intValue()==situRevista.getId_situ_revista() ? "selected" : "" %>
					<%= afiliado==null && situRevista.getId_situ_revista()==6 ? "selected" : "" %>
				value="<%= situRevista.getId_situ_revista()%>"><%=situRevista.getDescripcion()%></option>
			<%
									}
									%>
		</select></td>
		<td colspan="3">&nbsp;</td>
	</tr>
	<tr><td colspan="5">&nbsp;</td></tr>
	<tr>
		<td><liferay-ui:message key="cuit" /></td>
		<td colspan="1"><input id="<portlet:namespace />cuit_empleador"
			name="<portlet:namespace />cuit_empleador" maxlength="13" size="13"
			type="text" value="<%=cuit%>" 
			onBlur="javascript:<portlet:namespace />pierdeFoco();"/>
		<input type="hidden" id="<portlet:namespace />sucur"
			name="<portlet:namespace />sucur" value="<%=sucursal%>" /></td>
		<td><liferay-ui:message key="razon-social" /></td>
		<td><input id="<portlet:namespace />empleador"
			name="<portlet:namespace />empleador" size="50" type="text" value="<%=razon_soc%>"			
			onBlur="javascript:<portlet:namespace />pierdeFoco();" <%if(!esEdicion){ %> readonly="readonly" <%} %> />&nbsp;</td>
		<td colspan="1">
		<div id="<portlet:namespace />divBtnBuscaEmpleador"><a
			href="javascript: void(0);"
			onclick="javascript:<portlet:namespace />buscarEmpleador();"
			tabindex="-1">Buscar</a></div>
		</td>
	</tr>
	<tr><td colspan="5">&nbsp;</td></tr>
	<tr><td>
		<div id='divEmpleador' style="float: right;"></div>
			<input id="<portlet:namespace />empl_seleccionada"
			name="<portlet:namespace />empl_seleccionada" type="hidden" value="" />
		</td>
	</tr>	
	<tr>
		<td><label><liferay-ui:message key="ingre-fecha" /></label></td>
		<td><liferay-ui:input-date dayParam="fechaIngresoEmpresaDia"
			dayValue="<%=ingreFecha.get(Calendar.DATE)%>"
			monthParam="fechaIngresoEmpresaMes"
			monthValue="<%=ingreFecha.get(Calendar.MONTH)%>"
			yearParam="fechaIngresoEmpresaAnio"
			yearValue="<%=ingreFecha.get(Calendar.YEAR)%>"
			yearRangeStart="<%=ingreFecha.get(Calendar.YEAR) - 40%>"
			yearRangeEnd="<%=ingreFecha.get(Calendar.YEAR) + 1%>"
			firstDayOfWeek="<%=ingreFecha.getFirstDayOfWeek() - 1%>"
			disabled="<%=!esEdicion%>" /></td>
		<%-- <td><label><liferay-ui:message key="egreso-fecha" /></label></td>
		<td><liferay-ui:input-date monthParam="fechaEgresoEmpresaMes"
			monthNullable="true" dayParam="fechaEgresoEmpresaDia"
			dayNullable="true" yearParam="fechaEgresoEmpresaAnio"
			yearNullable="true"
			yearRangeStart="<%=ingreFecha.get(Calendar.YEAR) - 40%>"
			yearRangeEnd="<%=ingreFecha.get(Calendar.YEAR) + 20%>"
			firstDayOfWeek="<%=ingreFecha.getFirstDayOfWeek() - 1%>"
			disabled="<%=false%>" /></td> --%>
		<td><label><liferay-ui:message key="escala-salarial" /></label></td>
		<td>
			<select name="<portlet:namespace/>escala_salarial" id="<portlet:namespace/>escala_salarial"
			 	<%if(!esEdicion){ %> disabled="disabled" <%} %> >
				<option value=""></option>
				<option value="A" <%=afiliado!=null && afiliado.getEscala_salarial()!=null 
						&& afiliado.getEscala_salarial().equalsIgnoreCase("A")? "selected" : ""  %>>A</option>
				<option value="B" <%=afiliado!=null && afiliado.getEscala_salarial()!=null 
						&& afiliado.getEscala_salarial().equalsIgnoreCase("B")? "selected" : ""  %>>B</option>
				<option value="C" <%=afiliado!=null && afiliado.getEscala_salarial()!=null 
						&& afiliado.getEscala_salarial().equalsIgnoreCase("C")? "selected" : ""  %>>C</option>
				<option value="D" <%=afiliado!=null && afiliado.getEscala_salarial()!=null 
						&& afiliado.getEscala_salarial().equalsIgnoreCase("D")? "selected" : ""  %>>D</option>
				<option value="E" <%=afiliado!=null && afiliado.getEscala_salarial()!=null 
						&& afiliado.getEscala_salarial().equalsIgnoreCase("E")? "selected" : ""  %>>E</option>
			</select>
		</td>
		<td>&nbsp;</td>
	</tr>	
	</table>
</fieldset>
<br/>
<script type="text/javascript">
var popup;
function <portlet:namespace />buscarEmpleador() {
	
	var cuit_empleador=jQuery("#<portlet:namespace />cuit_empleador").val();
    var empleador=jQuery("#<portlet:namespace />empleador").val();    
    var bandera = false;
    if(cuit_empleador.length==0 && empleador.length==0){
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    } else {
	    popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-empresas" />",modal:true,width:420});
	    var cuit_empleador=jQuery("#<portlet:namespace />cuit_empleador").val();
	    var empleador=jQuery("#<portlet:namespace />empleador").val();    
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_padron_entidad&cuit_entidad='+cuit_empleador+
		'&sucursal=&entidad='+encodeURI(empleador);	       	
		jQuery(popup).load(url);
    }    
}
var cartel = null;
function <portlet:namespace />cargarEmpleador() {	
	var bandera = true;
	cartel = Liferay.Popup({title:"<liferay-ui:message key="alta-empleador" />",modal:true,width:840,heigh:800});
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_empleadores_entry&bandera='+bandera;   	
	jQuery(cartel).load(url);    
}

function <portlet:namespace />buscarEmpleadorOnDiv(e){	
	//Se modificó el campo, debemos cambiar el selecc	
	jQuery("#<portlet:namespace />empl_seleccionada").val("");
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	/* if(keyPressed==8 || keyPressed==46){		
		jQuery("#<portlet:namespace />empleador").val("");
		jQuery("#<portlet:namespace />cuit_empleador").val("");
		jQuery("#<portlet:namespace />divBtnBuscaEmpleador").show();
		return false;
	} */
	var cuit_empleador=jQuery("#<portlet:namespace />cuit_empleador").val();	
    var empleador=jQuery("#<portlet:namespace />empleador").val();
    var categoria=jQuery("#<portlet:namespace/>categoria").val();
    if(null!=cuit_empleador && cuit_empleador.length>10){
	if (categoria != 8 && categoria != 10) {
	    if(empleador.length>=3 || cuit_empleador.length>10){        
	        if(cuit_empleador.length >2){
	        	jQuery("#<portlet:namespace />empleador").val("");
	        }else{
	    		jQuery("#<portlet:namespace />cuit_empleador").val("");
	        }        
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_padron_entidad&cuit_entidad='+cuit_empleador+
			'&sucursal=&entidad='+encodeURI(empleador);
			jQuery("#divEmpleador").load(url);		
			jQuery("#divEmpleador").show();
	    }else{        
	    	jQuery("#divEmpleador").hide("slow");
	    }		
	}     
    }
}

function <portlet:namespace />cerrarDiv(){
	jQuery("#divEmpleador").hide("slow");		
}
function <portlet:namespace />cerrar(){	
	<portlet:namespace />cerrarDiv();
	if(popup){		
		Liferay.Popup.close(popup);
	}	
}

function pasarParametrosAParentBusquedaPadrones(cuit, razon, sucursal, id_seccional) {
	jQuery("#<portlet:namespace />cuit_empleador").val(cuit);
    jQuery("#<portlet:namespace />empleador").val(razon);
    jQuery("#<portlet:namespace />sucur").val(sucursal);	    
    //jQuery("#<portlet:namespace />empl_seleccionada").val("1");	    	   
    jQuery("#<portlet:namespace />empl_seleccionada").val("1");	    
    <portlet:namespace />cerrar();
    //jQuery("#<portlet:namespace />divBtnBuscaEntidad").hide();
	//jQuery("#<portlet:namespace />id_seccional").val(id_seccional);
    //<portlet:namespace />cerrarBusquedaPadrones();
 }


function <portlet:namespace />pierdeFoco(){
	var seleccionada=jQuery("#<portlet:namespace />empl_seleccionada").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDiv();
		return false;
	}else{				
		return false; 
	}	
}
function <portlet:namespace/>cambiaCategoria(){	
	var categoria=jQuery("#<portlet:namespace />categoria").val();
	if(categoria==4 || categoria==5 || categoria==6 || categoria==7 || categoria==12){
		jQuery("#<portlet:namespace />cuit_empleador").val("<%=afiliado != null?afiliado.getCuil():""%>");
		jQuery("#<portlet:namespace />empleador").val("<%=afiliado != null?afiliado.getApellido().concat(", ").concat(afiliado.getNombre()):""%>");
		jQuery("#<portlet:namespace />situRevista option[value=3]").attr("selected",true);
	} else if (categoria==8 || categoria==10) {
		jQuery("#<portlet:namespace />cuit_empleador").val("<%=afiliado != null?afiliado.getCuil_titular():""%>");
		jQuery("#<portlet:namespace />empleador").val("<%=afiliado != null?afiliado.getApellido().concat(", ").concat(afiliado.getNombre()):""%>");
		jQuery("#<portlet:namespace />situRevista option[value=3]").attr("selected",true);
	}  else if (categoria==11 || categoria==13 ){
		jQuery("#<portlet:namespace />situRevista option[value=6]").attr("selected",true);
	}  else if (categoria==2 || categoria==3 ){
		jQuery("#<portlet:namespace />situRevista option[value=1]").attr("selected",true);
	}  else{
		jQuery("#<portlet:namespace />situRevista option[value=3]").attr("selected",true);
	}
	<portlet:namespace/>proponerMonotributista(categoria);
}

function <portlet:namespace/>proponerMonotributista(cat){
	var cuilTit_ = jQuery("#<portlet:namespace />cuil_titular").val();
	var apeNom_ =  jQuery("#<portlet:namespace />apellido").val() + ", " + jQuery("#<portlet:namespace />nombre").val();
	var cuit_ = jQuery("#<portlet:namespace />cuit_empleador").val();
	<%if(afiliado == null){ %>
		if(cat==8 || cat==10){
			jQuery("#<portlet:namespace />cuit_empleador").val( cuilTit_ );
			jQuery("#<portlet:namespace />empleador").val(apeNom_);
		}else{
			if(cuilTit_ == cuit_){
				jQuery("#<portlet:namespace />cuit_empleador").val("");
				jQuery("#<portlet:namespace />empleador").val("");
			}
		}
	<%}%>
}


 </script>    