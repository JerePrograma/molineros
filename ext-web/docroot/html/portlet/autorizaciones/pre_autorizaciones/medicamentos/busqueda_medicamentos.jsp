<%@ include file="/html/portlet/autorizaciones/pre_autorizaciones/medicamentos/init.jsp" %>

<%//String dinámico que se le debe pasar a esta pagina para que sepa a que direccion redireccionar
			//con el formato /<farmacia_portlet>/buscar_medicamentos
			//ej: /farmacia/buscar_medicamento
			//esto se debe corresponder con el action definido en struts-config
			//de la forma: <action path="/<farmacia_portlet>/buscar_farmacia" forward="portlet.utils.medicamentos.view" />
			String searchURL = ParamUtil.getString(request, "search_url");
			String esEditableStr = ParamUtil.getString(request, "esEditable");
			if (esEditableStr == null || esEditableStr.equals("false")) {
				esEditableStr = "false";
			}
			boolean esEditable = Boolean.parseBoolean(esEditableStr);
			String id_medicamento = ParamUtil.getString(request,
					"id_medicamento", "");
			String troquel = ParamUtil.getString(request, "troquel", "");
			String medicamento = ParamUtil.getString(request,
					"nombre_medicamento", "");
			boolean popup=ParamUtil.getBoolean(request, "popup", false);
			boolean mostrarConPresentacion = ParamUtil.getBoolean(request,"mostrar_con_presentacion", false);%>
			

		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;" width="100%">
			<tr>
				<td>
					<input id="<portlet:namespace />id_medicamento" name="<portlet:namespace />id_medicamento" type="hidden" value="" 
					    <%if (!esEditable) {%>
							<%="readonly='readonly'"%> 
						<%} else {%> 
							onBlur="javascript:<portlet:namespace />pierdeFocoMd();" onKeyUp="javascript:<portlet:namespace />buscarMedicamentoOnDiv(event);" onkeydown="allowOnlyDigits(event);"
						<%}%>/>
					<liferay-ui:message key='medicamento' />:
				</td>
				<td>
					<input id="<portlet:namespace />nombre_medicamento" name="<portlet:namespace />nombre_medicamento" size="50" type="text" value=""  
						<%if (!esEditable) {%>
							<%="readonly='readonly'"%>
						<%} else {%>
					   		onKeyUp="javascript:<portlet:namespace />buscarMedicamentoOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFocoMd();"
					   	<%}%>/>&nbsp;
				</td>
				<td><liferay-ui:message key='troquel' />:</td>
				<td>
					<input  id="<portlet:namespace />troquel" name="<portlet:namespace />troquel" maxlength="7" size="7" type="text" value=""   
						<%if (!esEditable) {%> 
							<%="readonly='readonly'"%> 
						<%} else {%> 
							onBlur="javascript:<portlet:namespace />pierdeFocoMd();" onKeyUp="javascript:<portlet:namespace />buscarMedicamentoOnDiv(event);"
						<%}%>/>
				</td>				
				<td>
				<%
					if (esEditable) {
				%> 
					<div id="<portlet:namespace />divBtnBuscaMedicamento">
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarMedicamento();" tabindex="-1">Buscar</a>
					</div>
					<%
						}
					%>
				</td>
			</tr>	
			<tr>
		    	<td colspan="5">
				      <table>
				        <tr>
				           <td><label><liferay-ui:message key="cantidad" />:</label></td>
							
					       <td><input id="<portlet:namespace />cantidadMedicamento"
								name="<portlet:namespace />cantidadMedicamento" size="20"
								maxlength="20" type="text" onkeydown="allowOnlyDigits(event);"
								value='1' onchange="<portlet:namespace />actualizoTotal()"/>
					       </td>
				    
				           <td><label><liferay-ui:message key="importe" />:</label></td>
				           <td><input id="<portlet:namespace />importeMedicamento"
								name="<portlet:namespace />importeMedicamento" size="20"
								maxlength="20" type="text" onkeydown="allowOnlyDigitsAndDecimals(event);"
								value='' onchange="<portlet:namespace />actualizoTotal()"/>
					       </td>
				    
				          <td><label><liferay-ui:message key="total" />:</label></td>
				          <td><input id="<portlet:namespace />totalMedicamento"
								name="<portlet:namespace />totalMedicamento" size="20"
								maxlength="20" type="text"
								value='' readonly="readonly" />
					      </td>
				       
				          <td>
				              <%if(esEditable){ %>
							  <input type="button" value="<liferay-ui:message key="agregar-troquel" />" 
					                 onClick="<portlet:namespace />agregarPreautorizacionMedicamento();" />
					          <%} %>       
					     </td>	
					   </tr>
					 </table> 
			 	</td>  
		    </tr>
		    
			<%-- <tr>
				<td colspan="4">
					<div align="center" id="<portlet:namespace />agregandoTroquelesPreautorizacion">
					<table style="align: center;" width="100%">
						<tr>
							<td><liferay-ui:message key='buscando' /></td>
							<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>
					</div>
				</td>
			</tr> --%>
			<tr>
			  <td colspan="1">&nbsp;</td>
		    </tr>
			<tr>
				<td colspan="5">
					<div align="center" id="<portlet:namespace />troquelespreautorizaciones">
						<liferay-util:include page="/html/portlet/autorizaciones/pre_autorizaciones/medicamentos/preautorizacion_medicamentos_result.jsp">
							<liferay-util:param name="esEditable" value='<%=String.valueOf(esEditable) %>'/>
						</liferay-util:include>
					</div>
				</td>
			</tr>		
		</table>
		<div id='divMedicamento' style="float:left;">
		</div>
<input id="<portlet:namespace />med_seleccionado" name="<portlet:namespace />med_seleccionado" type="hidden" value=""/>

<input type="hidden" name="<portlet:namespace />id_preautorizacion_medicamento" id="<portlet:namespace />id_preautorizacion_medicamento" value="" />
<input type="hidden" name="<portlet:namespace />id_detalle_m" id="<portlet:namespace />id_detalle_m" value="" />
<input type="hidden" name="<portlet:namespace />id_detalle_aux_m" id="<portlet:namespace />id_detalle_aux_m" value="" />
	
<script type="text/javascript">
/* jQuery('#<portlet:namespace />agregandoTroquelesPreautorizacion').hide(); */
function <portlet:namespace />actualizoTotal(){
		
		var cantidad=jQuery('#<portlet:namespace />cantidadMedicamento').val();
		var importe=jQuery('#<portlet:namespace />importeMedicamento').val();
		if(cantidad!=null && importe !=null){
			jQuery('#<portlet:namespace />totalMedicamento').val(cantidad*importe);
		}
		
}
	
var popupMD;
function <portlet:namespace />buscarMedicamento() {	
	var id_medicamento=jQuery("#<portlet:namespace />id_medicamento").val();
	var troquel=jQuery("#<portlet:namespace />troquel").val();	
    var nombre_medicamento=jQuery("#<portlet:namespace />nombre_medicamento").val();    
		   
    if (troquel == null || troquel == ''){
        
    	troquel = '0';    	
    }    
    if (nombre_medicamento==null){
    	nombre_medicamento = '';
    }
    if(troquel=='0' && nombre_medicamento.length==0){
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    }else {
	    popupMD = Liferay.Popup({title:"Búsqueda Medicamentos",modal:true,width:700});	   	       
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=<%=searchURL%>&troquel='+troquel+
		  '&nombre_medicamento='+encodeURIComponent(nombre_medicamento)+'&popup=<%=popup%>';   	
		jQuery(popupMD).load(url);
    }
}
function <portlet:namespace />buscarMedicamentoOnDiv(e){
	//Se modificó el campo, debemos cambiar el selecc		
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
			
	if(jQuery("#<portlet:namespace />med_seleccionado").val() == "1" && (keyPressed==8 || keyPressed==46)){
		jQuery("#<portlet:namespace />id_medicamento").val("");
		jQuery("#<portlet:namespace />troquel").val("");
		jQuery("#<portlet:namespace />nombre_medicamento").val("");
		jQuery("#<portlet:namespace />med_seleccionado").val("");
		jQuery("#<portlet:namespace />divBtnBuscaMedicamento").show();
		return false;
	}
	var id_medicamento=jQuery("#<portlet:namespace />id_medicamento").val();
	var troquel=jQuery("#<portlet:namespace />troquel").val();	
    var nombre_medicamento=jQuery("#<portlet:namespace />nombre_medicamento").val();
    if (troquel == null || troquel == ''){
        
    	troquel = '0';    	
    }    
    if (nombre_medicamento==null){
    	nombre_medicamento = '';
    }    
    if(jQuery("#<portlet:namespace />med_seleccionado").val() != "1" && (nombre_medicamento.length>=6 || troquel.length>3)){
        if (troquel.length>3){
    		jQuery("#<portlet:namespace />id_medicamento").val("");
    		jQuery("#<portlet:namespace />nombre_medicamento").val("");
        } else if (nombre_medicamento.length>=6) {
    		jQuery("#<portlet:namespace />troquel").val("");
    		jQuery("#<portlet:namespace />id_medicamento").val("");
        }
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=<%=searchURL%>&troquel='+troquel+
		  '&nombre_medicamento='+encodeURI(nombre_medicamento);
		jQuery("#divMedicamento").load(url);
		jQuery("#divMedicamento").show();
    }else{
    	jQuery("#divMedicamento").hide("slow");
    }
}

function <portlet:namespace />cerrarDivMd(){
	jQuery("#divMedicamento").hide("slow");		
}

function <portlet:namespace />cerrarMd(){
	<portlet:namespace />cerrarDivMd();
	if(popupMD){
		Liferay.Popup.close(popupMD);
	}
}

function <portlet:namespace />pierdeFocoMd(){
	var seleccionada=jQuery("#<portlet:namespace />med_seleccionada").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivPd();
		return false;
	}else{
		return false;
	}
}

var idJS = ""
var troquelJs = "<%=troquel%>";
var medicamentoJs = "<%=medicamento%>";

if ("<%=String.valueOf(esEditable)%>" == "true" && troquelJs != ""){
	<portlet:namespace />buscarMedicamento();
}

function pasarParametrosAParentMd(troquel,medicamento,id, pres) {	
	seleccionaCamposMd(id, troquel, medicamento, pres);
    <portlet:namespace />cerrarMd();
}

function seleccionaCamposMd(id, cod, param, pres) {
	<%if (mostrarConPresentacion) {%>
		jQuery("#<portlet:namespace />nombre_medicamento").val(param + " " + pres);
	<% } else {%>
    	jQuery("#<portlet:namespace />nombre_medicamento").val(param);
	<%}%>
    jQuery("#<portlet:namespace />id_medicamento").val(id);
    jQuery("#<portlet:namespace />troquel").val(cod);
    jQuery("#<portlet:namespace />med_seleccionado").val("1");
    jQuery("#<portlet:namespace />divBtnBuscaMedicamento").hide();
}
</script>