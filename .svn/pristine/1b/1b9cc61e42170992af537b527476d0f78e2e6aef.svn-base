<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%@ page import="ar.com.uoma.beans.CentroCosto" %>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
	
<%	
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
	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	String ejDesde = (String) request.getAttribute("ejercicio_desde");
	String ejHasta = (String) request.getAttribute("ejercicio_hasta");
	Calendar  desde = null;
	Calendar  hasta = null;
	if (ejDesde !=null){
		desde = Calendar.getInstance();
		desde.setTime(format.parse(ejDesde));
		hasta = Calendar.getInstance();
		hasta.setTime(format.parse(ejHasta));
	}
	
	
	List<PlanCuentas> pCuentas = (List<PlanCuentas>)request.getAttribute("planCuentas");
	int i = 1;
	
	boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD);
	boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
	rolABM = true;
	String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
	
	List<CentroCosto> pCentros = (List<CentroCosto>)request.getAttribute("centrosCosto");
		
%>

<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="asiento-menor-fecha-contable" />
<form action="" method="post" name="<portlet:namespace />editar_asiento" >
<input type="hidden" name="id" value="${asiento.id}"/>
<c:if test="${asiento.id != 0}">
	<input type="hidden" name="ejercicio_hasta" value="${asiento.ejercicioHastaString}"/>
	<input type="hidden" name="ejercicio_desde" value="${asiento.ejercicioDesdeString}"/>
</c:if>
<table style="width: 100%">
	<tr><td colspan="3"><b>Asiento</b></td></tr>
	<tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<td><b>Ejercicio:</b>&nbsp;</td>
		<td colspan="3">
			<c:if test="${empty asiento or asiento.id == 0}">				
				<select name="ejercicio" id="ejercicio" onchange="cambioEjercicio()">
					<%	Calendar cal = Calendar.getInstance();
						int hastaAnio = cal.get(Calendar.YEAR);
						if(portlet_name.equals("farmacia")){
							if (cal.get(Calendar.MONTH) < Calendar.JULY){
								hastaAnio--;
							}
						}else{
							if (cal.get(Calendar.MONTH) < Calendar.AUGUST){
								hastaAnio--;
							}
						}
						for (int j = 2000; j<=hastaAnio; j++){  %>
					<option value="<%=j%>-<%=j+1%>" <%if (j == hastaAnio) { %>
						selected="selected" <%} %>>
						<% if(portlet_name.equals("farmacia")){%>
							Julio&nbsp;<%=j %>&nbsp;-&nbsp;Junio&nbsp;<%= j+1 %></option>
						<%}else{%>						
							Agosto&nbsp;<%=j %>&nbsp;-&nbsp;Julio&nbsp;<%= j+1 %></option>
						<%}%>
					<%} %>
				</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			</c:if>
			<c:if test="${asiento.id != 0}">
				<b>${asiento.ejercicioDesdeString}&nbsp;-&nbsp;${asiento.ejercicioHastaString}</b><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				<input type="hidden" id="ejercicio" value="${asiento.anioEjercicioDesdeString}-${asiento.anioEjercicioHastaString}"/>
			</c:if>
		</td>
	</tr>	
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td>
			Fecha:&nbsp;&nbsp;&nbsp;
		</td>
		<td><input type="text" name="fecha" id="fecha" value="${asiento.fechaString}" size="9"/><a href="javascript:void(0)" onclick="help(event, 'helpFecha')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		
		<td>Nro:&nbsp;<input type="text" name="nro" value="${asiento.nro}" size="5" readonly="readonly"/><a href="javascript:void(0)" onclick="help(event, 'helpNumero')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		<td>Descripción:&nbsp;<input type="text" name="descripcion" id="descripcion" value="${asiento.descripcion}" size="80"/>*Asiento manual<a href="javascript:void(0)" onclick="help(event, 'helpDescripcion')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
</table>
<fieldset class="block-labels" style="width: 80%">
<legend>Detalle</legend>
<table style="width: 100%" id="detalle_asientos">
	<thead>
		<tr id="tr_0">
			<th colspan="2"> Pase<a href="javascript:void(0)" onclick="help(event, 'helpPase')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></th>
			<th>Cuenta</th>
			<th>Centro Costo</th>
			<th>Comprobante</th>
			<th>Debe</th>
			<th>Haber</th>
			<th>Observaciones</th>
		</tr>
	</thead>
	<tbody id="sortable">
		<c:if test="${asiento.id == 0}">
			<tr id="tr_1" ><td  id="td_1" style="width: 22px"><span><img src="/html/images/move.png" style="width: 20px; height: 20px"/></span><!-- no dejar espacio entre el tr y el td porque sino se rompe el js --></td>
				<td><input readonly="readonly" type="text" name="pase_1" value="1" size="7"/></td>
				<td>
					<select name="cuenta_1" id="cuenta_1" style="width: 180px">
					<% for(PlanCuentas pc : pCuentas){
						if (pc.isImputable()){%>
						   <% if(portlet_name.equals("uoma")){%>
						     <option value="<%=pc.getId()%>"><%=pc.getNumero() + " - " +pc.getCuenta()   %></option>
						   <%}else {%> 
						     <option value="<%=pc.getId()%>"><%=pc.getNumero() + " - " + pc.getCuenta()%></option>
						   <%}%>
					<%}} %>
					</select>
				</td>
				
				<td>
					<select name="ccosto_1" id="ccosto_1" style="width: 180px">
					     <option value="0">Seleccione un centro de costo</option>
					<%for(CentroCosto pc : pCentros){%>
						 <option value="<%=pc.getId()%>"><%=pc.getDescripcion() %></option>
						
					<%}%>
					</select>
				</td>
				
				<td><input type="text" name="comprobante_1" id="comprobante_1" value="" size="20"/></td>
				<td><input type="text" name="debe_1" value="" size="10" onchange="validar()" style="text-align: right;"/><span id="error_debe_1" style="color: red"></span></td>
				<td><input type="text" name="haber_1" value="" size="10" onchange="validar()" style="text-align: right;"/><span id="error_haber_1" style="color: red"></span></td>
				<td><input type="text" name="observaciones_1" value="" size="30"/></td>
				<td><a href="javascript:void(0)" onclick="borrar(1)">Borrar</a><a href="javascript:void(0)" onclick="help(event, 'helpBorrar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
				<%i++; %>
			</tr>
		</c:if>
		<c:if test="${asiento.id != 0}">
				 <c:forEach var="det" items="${asiento.detalle}">
				 	<tr id="tr_${det.pase}" ><td  id="td_${det.pase}" style="width: 22px"><span><img src="/html/images/move.png" style="width: 20px; height: 20px"/></span><!-- no dejar espacio entre el tr y el td porque sino se rompe el js --></td>
						<td><input type="text" readonly="readonly" name="pase_${det.pase}" value="${det.pase}" size="7"/></td>
						<td>
							<select id="cuenta_${det.pase}" name="cuenta_${det.pase}" style="width: 180px">
							<% for(PlanCuentas pc : pCuentas){
								if (pc.isImputable()){%>
								<option value="<%=pc.getId()%>"><%=pc.getNumero() + " - " + pc.getCuenta()%></option>
							<%}} %>
							</select>
						</td>
						
						<td>
							<select id="ccosto_${det.pase}" name="ccosto_${det.pase}" style="width: 180px">
							    <option value="0">Seleccione un centro de costo</option>
							<%for(CentroCosto pc : pCentros){%>
								<option value="<%=pc.getId()%>"><%=pc.getDescripcion()%></option>
							<%} %>
							</select>
						</td>
						
						<td><input type="text" name="comprobante_${det.pase}" id="comprobante_${det.pase}" value="${det.comprobante}" size="20"/></td>
						<td><input type="text" name="debe_${det.pase}" value="${det.debe}" size="10" onchange="validar()" style="text-align: right;"/><span id="error_debe_${det.pase}" style="color: red"></span></td>
						<td><input type="text" name="haber_${det.pase}" value="${det.haber}" size="10" onchange="validar()" style="text-align: right;"/><span id="error_haber_${det.pase}" style="color: red"></span></td>
						<td><input type="text" name="observaciones_${det.pase}" value="${det.observaciones}" size="30"/></td>
						<td><input type="hidden" name="detalle_id_${det.pase}" id="detalle_id_${det.pase}" value="${det.id}"/><a href="javascript:void(0)" onclick="borrarDeSession(${det.pase})">Borrar</a>
							<% if (i == 1) { %>
								<a href="javascript:void(0)" onclick="help(event, 'helpBorrar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
							<%}  %>
						</td>
						<%i++; %>
					</tr>
				 </c:forEach>
		</c:if>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">&nbsp;</td>
			<td style="text-align: right;"><b>Totales</b></td>
			<td style="text-align: right;"><b><span id="totalDebe">0</span></b>&nbsp;&nbsp;</td>
			<td style="text-align: right;"><b><span id="totalHaber">0</span></b>&nbsp;&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
   </tfoot>
</table>

<input type="button" value="Agregar Detalle" onclick="agregar()"/><a href="javascript:void(0)" onclick="help(event, 'helpAgregar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
</fieldset>
<br/>
<% if (rolABM && !soloVer) {%> 
<input type="button" value="Guardar" onclick="guardar()"/><a href="javascript:void(0)" onclick="help(event, 'helpGuardar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
	<c:if test="${asiento.id != 0}">
		<input type="button" onclick="altaAsiento()" value="Alta Nuevo Asiento" /><a href="javascript:void(0)" onclick="help(event, 'helpAlta')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
	</c:if>
<% } %>
<span id="guardando">
	<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
</span>
<input type="hidden" value="" name="cantidad" id="cantidad"/>
</form>

	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="volver">
		<%if(portlet_name.equals("farmacia")){%>
			<portlet:param name="struts_action" value="/farmacia/asientos" />
		<%}else if(portlet_name.equals("uoma")){%>
			<portlet:param name="struts_action" value="/uoma/asientos" />			
		<%}else{%>
			<portlet:param name="struts_action" value="/tesoreria/asientos" />
		<%}%>	
	</portlet:renderURL>
	<p><a href="<%= volver %>">Volver</a><a href="javascript:void(0)" onclick="help(event, 'helpVolver')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></p>


 <c:forEach var="det" items="${asiento.detalle}">
	<script type="text/javascript">
		jQuery("#cuenta_${det.pase}").val(${det.cuenta.id})
		jQuery("#ccosto_${det.pase}").val(${det.centroCosto.id})
	</script>
 </c:forEach>

<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">				 
Ejercicio: Es el ejercicio que le corresponde al asiento.
</div>
<div id="helpFecha" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Fecha: Es la fecha asignada al asiento.
</div>
<div id="helpNumero" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Número: Indica el número de asiento. Es asignado automáticamente por el sistema en el caso de un alta y no es modificable manualmente. Como este número debe ser correlativo por fecha, en el caso de agregarse un asiento que no respete esta correlatividad, se informará en pantalla que los asientos del ejercicio no están ordenados, dejando optar al usuario para renumerarlos.
</div>
<div id="helpDescripcion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Descripción: En el caso de los asientos automáticos es la descripción asignada por el proceso de generación de los distintos asientos. Se agrega la palabra "automático" al final del mismo. En el caso de asientos manuales, es el texto que el usuario ingrese.
</div>
<div id="helpPase" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Pase: Se detalla cada uno de los pases del asiento. El orden de los pases dentro del asiento puede modificarse arrastrando el mismo al lugar deseado desde el botón que inicia la línea. Se permite cargar un importe en la columna debe o en la columna haber. No en ambas. El comprobante y las observaciones son opcionales. Se permite modificar sólo para el caso de los asientos manuales.
</div>
<div id="helpBorrar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Borrar: Se utiliza para eliminar un pase del asiento. Está activo sólo para el caso de los asientos manuales.
</div>
<div id="helpAgregar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Agregar detalle: mediante este botón se agregan pases al asiento.
</div>
<div id="helpGuardar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Guardar: Al seleccionar este botón, se efectúan todos los controles sobre los datos ingresados y se graba en la tabla correspondiente; confirmando así lo ingresado. No será guardado ningún cambio si se abandona la pantalla sin seleccionar este botón. Se controlará que los totales del asiento de las columnas "debe" y "haber" coincidan.
</div>
<div id="helpAlta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Alta Nuevo Asiento: Este botón aparece en el caso de tratarse de un asiento previamente guardado. Al seleccionar este botón, se limpia la pantalla para la carga de un nuevo asiento sin la necesidad de volver al la pantalla anterior. Se debe tener en cuenta que cualquier modificación que se efectúe sobre el asiento y no sea guardada previamente a la ejecución de este botón, se perderá.
</div>
<div id="helpVolver" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Volver: Seleccionando este link, se vuelve a la pantalla anterior. Se perderá toda actualización efectuada en el caso que los cambios no se guarden previamente.
</div>
				 
				 
<script type="text/javascript">	
	<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
	jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
	<%}%>

	var i = <%=i%>;
	var iFinal = <%=i%>;
	
	function reordenar(){
		for (var nro = 0; nro < jQuery( "#sortable" ).children().size(); nro++){
			var row = jQuery("#sortable").children()[nro];
			var cellPase = row.childNodes[1];
			cellPase.childNodes[0].value = nro+1;
		}
		i = jQuery( "#sortable" ).children().size() +1;
	}
	
	function agregar() {   
 	   var index = iFinal;
 	  jQuery('<tr id="tr_'+index+'">' + 
        '<td style="width: 22px"><span><img src="/html/images/move.png" style="width: 20px; height: 20px"/></span></td>'+
		'<td><input type="text" readonly="readonly" name="pase_'+index+'" value="'+i+'" size="7"/></td>'+
		'<td>'+
		'	<select name="cuenta_'+index+'" id="cuenta_'+index+'" style="width: 180px">'+
			'</select>'+
		'</td>'+
		
		 '<td>'+
		     '<div class="select-centro-costo_' + index + '">' +
	        '   <select name="ccosto_'+index+'" id="ccosto_'+index+'" style="width: 180px">'+
	        '       <option value="0">Sin centro de costo</option>'+
	        '   </select>'+
	        '</div>'+
	     '</td>'+
		
		'<td><input type="text" name="comprobante_'+index+'" value="" size="20" id="comprobante_'+index+'"/></td>'+
		'<td><input type="text" name="debe_'+index+'" value="" size="10" onchange="validar()" style="text-align: right;"/><span id="error_debe_'+index+'" style="color: red"></span></td>'+
		'<td><input type="text" name="haber_'+index+'" value="" size="10" onchange="validar()" style="text-align: right;"/><span id="error_haber_'+index+'" style="color: red"></span></td>'+
		'<td><input type="text" name="observaciones_'+index+'" value="" size="30"/></td>'+
		'<td><a href="javascript:void(0)" onclick="borrar('+index+')">Borrar</a></td></tr>').appendTo('#sortable');
 		llenarCuentasNuevaFila(index);
 		i++;
        iFinal++;
    }
	
	function validar(){
		var totalDebe = 0;
		var totalHaber = 0;
		var error = false;
		for (var index = 0; index < jQuery( "#sortable" ).children().size(); index++){
			var row = jQuery("#sortable").children()[index];
			var id = row.id;
			var nro = id.substring(3);
			var comprobante = row.childNodes[4].childNodes[0].value;
			var debe = row.childNodes[5].childNodes[0].value;
			var haber= row.childNodes[6].childNodes[0].value;
			var errorDebe = false;
			var errorHaber = false;
			if (jQuery.trim(debe) == "" && jQuery.trim(haber) == ""){
				jQuery("#error_debe_" + (nro)).html("*");
				jQuery("#error_haber_" + (nro)).html("*");
				errorDebe = true;
				errorHaber = true;
				error = true;
			}
			if (jQuery.trim(debe) != "" && !IsNumeric(debe)){
				jQuery("#error_debe_" + (nro)).html("*");
				errorDebe = true;
				error = true;
			}
			if (jQuery.trim(haber) != "" && !IsNumeric(haber)){
				jQuery("#error_haber_" + (nro)).html("*");
				errorHaber = true;
				error = true;
			}
			if (jQuery.trim(haber) != "" && parseFloat(haber) != 0 && jQuery.trim(debe) != "" && parseFloat(debe) != 0 ){
				jQuery("#error_debe_" + (nro)).html("*");
				jQuery("#error_haber_" + (nro)).html("*");
				errorDebe = true;
				errorHaber = true;
				error = true;
			}
			
			if (!errorDebe) {
				jQuery("#error_debe_" + (nro)).html("");
			}
			if (!errorHaber){
				jQuery("#error_haber_" + (nro)).html("");
			}
			if (!errorDebe && !errorHaber){
				if (jQuery.trim(haber) != "" && parseFloat(haber) != 0){
					row.childNodes[5].childNodes[0].value = "0";
					debe = "0";
				} else {
					row.childNodes[6].childNodes[0].value = "0";
					haber = "0";
				}
			}
			
			if (IsNumeric(debe) || debe == "0"){
				totalDebe = Math.round((totalDebe + (Math.round(parseFloat(row.childNodes[5].childNodes[0].value) * 100) / 100)) *100) /100;
			}
			
			if (IsNumeric(haber) || haber == "0"){
				totalHaber = Math.round((totalHaber + (Math.round(parseFloat(row.childNodes[6].childNodes[0].value) * 100) / 100)) *100) /100;
			}
			
// 			if (jQuery.trim(comprobante) == "" || jQuery.trim(comprobante) == "Complete Comprobante"){
// 				row.childNodes[3].childNodes[0].value = "Complete Comprobante";
// 				jQuery("#comprobante_" + (nro)).css({'color': 'red'});
// 				error = true;
// 			} else {
// 				jQuery("#comprobante_" + (nro)).css({'color': 'black'});
// 			}
		}
		jQuery("#totalDebe").html(numberWithSeparator(totalDebe) + " ");
		jQuery("#totalHaber").html(numberWithSeparator(totalHaber) + " ");
		return !error;
	}
	
	function numberWithSeparator(x) {
		var parts = x.toString().split(",");
	    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ".");
	    return parts.join(",");
	}
	
	function validarFecha(){
		if (jQuery("#fecha").val().toString().length == 0){
			jQuery("#fecha").css({'color': 'red'});
			jQuery("#fecha").val("Debe completar la fecha");
 			return false;
		}
		if (jQuery("#fecha").val().toString().length != 0){
			if (jQuery("#fecha").val().split("/").length != 3){
				jQuery("#fecha").css({'color': 'red'});
				return false;
			}
	 		if (jQuery("#fecha").val().split("/")[0] > 31 ){
	 			jQuery("#fecha").css({'color': 'red'});
	 			return false;
	 		}
	 		if (jQuery("#fecha").val().split("/")[1] > 12 ){
	 			jQuery("#fecha").css({'color': 'red'});
	 			return false;
	 		}
	 		if (jQuery("#fecha").val().split("/")[2] < 1800 || jQuery("#fecha").val().split("/")[2] >2099 ){
	 			jQuery("#fecha").css({'color': 'red'});
	 			return false;
	 		}
		}
		<%if(portlet_name.equals("farmacia")){%>
			var ejercicioDesde=new Date(jQuery("#ejercicio").val().split("-")[0],6,1,0,0,0);
		<%}else{%>
			var ejercicioDesde=new Date(jQuery("#ejercicio").val().split("-")[0],7,1,0,0,0);
		<%}%>
		<%if(portlet_name.equals("farmacia")){%>
			var ejercicioHasta=new Date(jQuery("#ejercicio").val().split("-")[1],5,31,0,0,0);
		<%}else{%>
			var ejercicioHasta=new Date(jQuery("#ejercicio").val().split("-")[1],6,31,0,0,0);
		<%}%>
		
		var fecha=new Date(jQuery("#fecha").val().split("/")[2],
							parseFloat(jQuery("#fecha").val().split("/")[1])-1,
							jQuery("#fecha").val().split("/")[0],0,0,0);
		
		if (fecha < ejercicioDesde || fecha > ejercicioHasta){			
			jQuery("#fecha").css({'color': 'red'});
 			return false;
		}
		
	 	jQuery("#fecha").css({'color': 'black'});
		return true;
	}
	
	function guardar(){
		var error = false;
		var errorFecha = false;
		if (!validar()){
			error = true;
		}
		
		if (!validarFecha()){
			errorFecha = true;
		}
		
		if (jQuery.trim(jQuery("#descripcion").val()) == "" || jQuery.trim(jQuery("#descripcion").val()) == "Complete Descripcion"){
			jQuery("#descripcion").val("Complete Descripcion");
			jQuery("#descripcion").css({'color': 'red'});
			error = true;
		} else {
			jQuery("#descripcion").css({'color': 'black'});
		}
		
		if (error){
			alert("Verifique los items en rojo");
			return;
		}
		
		if (errorFecha){
			alert("La fecha debe estar dentro del ejercicio seleccionado");
			return;
		}
		
		if (jQuery.trim(jQuery("#totalDebe").html()) != jQuery.trim(jQuery("#totalHaber").html())){
			
			//if(!confirm("Los totales de Debe y Haber no coinciden")){
			//	return; 
			//}
			
			alert("Los totales de Debe y Haber no coinciden");
			return;
		}
		jQuery("#cantidad").val(iFinal);
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_asiento';
		document.<portlet:namespace />editar_asiento.method = 'post';
		submitForm(document.<portlet:namespace />editar_asiento, url);
		
	}

	function borrar(numero) {		
		jQuery('#tr_'+numero).remove();   
		reordenar();
		validar();
	}

	function borrarDeSession(numero){		
		 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/eliminar_detalle_asiento';
		 url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
			
		jQuery.getJSON(url, { detalle_id:  jQuery("#detalle_id_"+numero).val()}, function(rta) {
			if (rta.status == "ok") {
				borrar(numero);
			} else {
				alert("Error al tratar de eliminar registro");
			}
		});
	}
	
	jQuery(document).ready(function(){
		jQuery("#guardando").hide();
		
		jQuery("#fecha").datepicker(jQuery.datepicker.regional['es']);
		jQuery("#sortable").sortable({ handle: 'span', stop: function(event, ui) { reordenar() }});
		validar();
	});
	
	function altaAsiento(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_asiento';
		url += '&rnd=' + Math.floor(Math.random()*100);
		window.location = url;
	}
	
	function cambioEjercicio(){
		jQuery("#fecha").val("");
		actualizarCuentas(-1);
		traerCentrosCostos(-1);
	}
	

	function actualizarCuentas(numeroDeSelect){
		var ejercicio=jQuery("#ejercicio").val();	
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_plan_cuentas_para_fecha'
		    + '&ejercicio=' +ejercicio;
		url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				if (numeroDeSelect == -1) {
					for (var index = 0; index < jQuery( "#sortable" ).children().size(); index++){
						var row = jQuery("#sortable").children()[index];
						var id = row.id;
						var nro = id.substring(3);
						var seleccionada=jQuery('#cuenta_'+nro).val();
						var selected='';						
						jQuery('#cuenta_'+nro).find('option').remove();
						for(var i =0;i< obj.cuentas.length; i++){
							var selected='';						
							if(seleccionada==obj.cuentas[i].id){
								selected=' selected';
							}							
							jQuery('#cuenta_'+nro).append('<option value="'+obj.cuentas[i].id+'"'+selected+'>'+obj.cuentas[i].numero + '-' + obj.cuentas[i].cuenta +'</option>');
						}
					}
				} else {
					jQuery('#cuenta_'+numeroDeSelect).find('option').remove();
					for(var i =0;i< obj.cuentas.length; i++){
						jQuery('#cuenta_'+numeroDeSelect).append('<option value="'+obj.cuentas[i].id+'">'+obj.cuentas[i].numero + '-' + obj.cuentas[i].cuenta +'</option>');
					}
				}
			}
		});		
	}
	
	function llenarCuentasNuevaFila(index){
		if (jQuery( "#sortable" ).children().size() > 1){
			
			var row = jQuery("#sortable").children()[0];
			var id = row.id;
			var nro = id.substring(3);
			
			for(var i =0;i< jQuery('#cuenta_'+nro).children().length; i++){
				var opt = jQuery('#cuenta_'+nro).children()[i];
				jQuery('#cuenta_'+index).append('<option value="'+opt.value+'">'+opt.innerHTML +'</option>');
			}
			traerCentrosCostos(index);
		} else {
			actualizarCuentas(index);
			traerCentrosCostos(index);
		}
	}
	
	
    function traerCentrosCostos(nro) {
		var ejercicio=jQuery("#ejercicio").val();	
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/trae_centros_costos'
			    + '&ejercicio=' +ejercicio;
			    url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
			    url += '&rnd=' + Math.floor(Math.random()*100);
			
	    jQuery.ajax({   
		        url: url,
		        async:true,
		        success: function(data){	
		        	var idCentro=jQuery("#ccosto_"+nro).val();
		        	if (nro == -1) {
		        		for (var index = 1; index <= jQuery( "#sortable" ).children().size(); index++){
							jQuery('.select-centro-costo_' + index + ' select').html(data).fadeIn();
				    		jQuery("#ccosto_" + index + " option[value=" + idCentro + "]").attr("selected", true);
		        		}		
		        	}else{
		               jQuery('.select-centro-costo_' + nro + ' select').html(data).fadeIn();
		    		   jQuery("#ccosto_" + nro + " option[value=" + idCentro + "]").attr("selected", true);
		        	}   
		        }
		 });
		
	}
	
</script>

