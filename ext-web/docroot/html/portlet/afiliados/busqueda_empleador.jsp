<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%
/* List<CategoriaLaboral> categorias=TraeListasServiceUtil.getCategoriasLaborales();
List<SituacionRevista> situacionesRevista=TraeListasServiceUtil.getSituacionRevista(); */
List<CategoriaLaboral> categorias=(ArrayList<CategoriaLaboral>) portletSession.getAttribute(WebKeysAfiliados.CATEGORIAS_EMPRESA_EN_SESSION, PortletSession.APPLICATION_SCOPE);
List<SituacionRevista> situacionesRevista=(ArrayList<SituacionRevista>) portletSession.getAttribute(WebKeysAfiliados.SITUACIONES_REVISTA_EMPRESA_EN_SESSION, PortletSession.APPLICATION_SCOPE);

Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);	
String cuit=null!=request.getParameter("cuit")?(String)request.getParameter("cuit"):"";
String razon_soc=null!=request.getParameter("razon_soc")?(String)request.getParameter("razon_soc"):"";
String sucursal=null!=request.getParameter("sucu")?(String)request.getParameter("sucu"):"";
String id_categoria=null!=request.getParameter("categoria")?(String)request.getParameter("categoria"):"";
String id_revista=null!=request.getParameter("situ_revista")?(String)request.getParameter("situ_revista"):"";

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
<table style="border-collapse: separate; border-spacing: 3px;">

	<tr>
		<td><liferay-ui:message key="categoria" /></td>
		<td colspan="5"><select name="<portlet:namespace/>categoria"
			id="<portlet:namespace/>categoria"
			onChange="javascript:<portlet:namespace/>cambiaCategoria();">
			<%
										for (CategoriaLaboral categoria : categorias) {
									%>
			<option <%= categoria.getId_categoria()==11&&id_categoria=="" ? "selected" : 
				id_categoria!=""&&categoria.getId_categoria()==Integer.valueOf(id_categoria) ? "selected" : "" %>
				value="<%= categoria.getId_categoria()%>"><%=categoria.getDescripcion()%></option>
			<%
									}
									%>
		</select></td>
		<td><liferay-ui:message key="situacion-revista" /></td>
		<td><select name="<portlet:namespace/>situRevista"
			id="<portlet:namespace/>situRevista">
			<%
										for (SituacionRevista situRevista: situacionesRevista) {
									%>
			<option <%= situRevista.getId_situ_revista()==6&&id_revista=="" ? "selected" : 
					id_revista!=""&&situRevista.getId_situ_revista()==Integer.valueOf(id_revista) ? "selected" : "" %>
				value="<%= situRevista.getId_situ_revista()%>"><%=situRevista.getDescripcion()%></option>
			<%
									}
									%>
		</select></td>
	</tr>
				
	<tr>
		<td width="48px;"><liferay-ui:message key="cuit" /></td>
		<td><input id="<portlet:namespace />cuit_empleador"
			name="<portlet:namespace />cuit_empleador" maxlength="11"
			type="text" value="<%=cuit%>"
			onBlur="javascript:<portlet:namespace />pierdeFoco();"					
			onKeyUp="javascript:<portlet:namespace />buscarEmpleadorOnDiv(event)"
			style="width: 75px;"/>
		<input type="hidden" id="<portlet:namespace />sucur"
			name="<portlet:namespace />sucur" value="<%=sucursal%>" /> <a href="javascript: void(0);"
			onclick="javascript:<portlet:namespace />cargarEmpleador();"
			tabindex="-1">Nueva Empresa</a></td>

		<td><liferay-ui:message key="razon-social" /></td>
		<td><input id="<portlet:namespace />empleador"
			name="<portlet:namespace />empleador" type="text" value="<%=razon_soc%>"					
			onBlur="javascript:<portlet:namespace />pierdeFoco();" 
			style="width: 325px;"/>
		</td>
		<td align="left">
		<div id="<portlet:namespace />divBtnBuscaEmpleador"><a
			href="javascript: void(0);"
			onclick="javascript:<portlet:namespace />buscarEmpleador();"
			tabindex="-1">Buscar</a></div>
		</td>
		<td colspan="1">
			<div id='divEmpleador'></div>
		</td>
		<%if(portlet_name.equalsIgnoreCase("AFILIADOS")){ %>
			<td><label><liferay-ui:message key="escala-salarial" /></label></td>
			<td>
				<select name="<portlet:namespace/>escala_salarial" id="<portlet:namespace/>escala_salarial">
					<option value=""></option>
					<option value="A">A</option>
					<option value="B">B</option>
					<option value="C">C</option>
					<option value="D">D</option>
					<option value="E">E</option>
				</select>
			</td>
		<%} %>
	</tr>	
</table>
<!-- <div id='divEmpleador' style="float: right;"></div> -->
<input id="<portlet:namespace />empl_seleccionada"
	name="<portlet:namespace />empl_seleccionada" type="hidden" value="" />

<script type="text/javascript">
var popup;
function <portlet:namespace />buscarEmpleador() {
	
	var cuit_empleador=jQuery("#<portlet:namespace />cuit_empleador").val();
    var empleador=jQuery("#<portlet:namespace />empleador").val();  
    var sucursal = jQuery("#<portlet:namespace />sucur").val();
    var bandera = false;
    if(cuit_empleador.length==0 && empleador.length==0){
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    } else {
	    popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-empresas" />",modal:true,width:420});
	    var cuit_empleador=jQuery("#<portlet:namespace />cuit_empleador").val();
	    var empleador=jQuery("#<portlet:namespace />empleador").val();
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_padron_entidad&cuit_entidad='+cuit_empleador+
		'&sucursal='+sucursal+'&entidad='+encodeURI(empleador);
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
	var cuit_empleador=jQuery("#<portlet:namespace />cuit_empleador").val();	
    var empleador=jQuery("#<portlet:namespace />empleador").val();
    var sucursal = jQuery("#<portlet:namespace />sucur").val();
    var categoria=jQuery("#<portlet:namespace/>categoria").val();
            
    //jQuery("#<portlet:namespace />ent_seleccionada").val() != "1" &&
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

function <portlet:namespace />cerrarDiv(){
	jQuery("#divEmpleador").hide("slow");		
}
function <portlet:namespace />cerrar(){	
	<portlet:namespace />cerrarDiv();
	if(popup){		
		Liferay.Popup.close(popup);
	}	
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
		jQuery("#<portlet:namespace />cuit_empleador").val("<%=afiliado.getCuil()%>");
		jQuery("#<portlet:namespace />empleador").val("<%=afiliado.getApellido()%>, <%=afiliado.getNombre()%>");
		jQuery("#<portlet:namespace />situRevista option[value=3]").attr("selected",true);
	} else if (categoria==8 || categoria==10) {
		jQuery("#<portlet:namespace />cuit_empleador").val("<%=afiliado.getCuil()%>");
		jQuery("#<portlet:namespace />empleador").val("<%=afiliado.getApellido()%>, <%=afiliado.getNombre()%>");
		jQuery("#<portlet:namespace />situRevista option[value=3]").attr("selected",true);
	}  else if (categoria==11 || categoria==13 ){
		jQuery("#<portlet:namespace />situRevista option[value=6]").attr("selected",true);
	}  else if (categoria==2 || categoria==3 ){
		jQuery("#<portlet:namespace />situRevista option[value=1]").attr("selected",true);
	}  else{
		jQuery("#<portlet:namespace />situRevista option[value=3]").attr("selected",true);
	}
	
			
}
</script>