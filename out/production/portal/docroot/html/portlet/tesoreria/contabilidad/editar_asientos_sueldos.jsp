<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%@ page import="ar.com.uoma.beans.CentroCosto" %>
<%@ page import="ar.com.ospim.tesoreria.beans.contabilidad.Asiento" %>

<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
	
<%	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
    String  entidad="";
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
		entidad="O";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
		entidad="A";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
		entidad = "U";
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
	List<CentroCosto> pSectores=TraeListasServiceUtil.getSectoresLiquidacionSueldos(entidad);
	int i = 1;
	
	boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD);
	boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
	rolABM = true;
	String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
	
	
	String esEditableStr = ParamUtil.getString(request, "esEditable");
	if (esEditableStr == null || esEditableStr.equals("false")){
			esEditableStr ="false";
	}
	boolean esEditable = Boolean.parseBoolean(esEditableStr);
	
	Integer idNeteo=(Integer)request.getSession().getAttribute(WebKeysTesoreria.ASIENTO_SUELDOS_CUENTA_NETEO);
	Integer idSector=(Integer)request.getSession().getAttribute(WebKeysTesoreria.ASIENTO_SUELDOS_SECTOR_LIQUIDADO);
	
	Asiento asiento=(Asiento)request.getSession().getAttribute(WebKeysTesoreria.ASIENTO_SUELDO_EN_SESSION);
	if(asiento==null){
	   asiento=new Asiento();
	   asiento.setDescripcion("");
	}
		
%>

<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="asiento-menor-fecha-contable" />
<form action="" method="post" name="<portlet:namespace />editar_asiento" enctype="multipart/form-data">

<input type="hidden" name="id" value="0"/>

<table style="width: 100%">
	<tr><td colspan="3"><b>Asiento</b></td></tr>
	<tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<td><b>Ejercicio:</b>&nbsp;</td>
		<td colspan="3">
				
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
			
		</td>
	</tr>	
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td>
			Fecha:&nbsp;&nbsp;&nbsp;
		</td>
		<td><input type="text" name="fecha" id="fecha" value="<%=asiento.getFechaString() %>" size="9"/><a href="javascript:void(0)" onclick="help(event, 'helpFecha')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		
		<td>Nro:&nbsp;<input type="text" name="nro" value="<%=asiento.getNro() %>" size="5" readonly="readonly"/><a href="javascript:void(0)" onclick="help(event, 'helpNumero')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		<td>Descripción:&nbsp;<input type="text" name="descripcion" id="descripcion" value="<%=asiento.getDescripcion()!=null?asiento.getDescripcion():""%>" size="80"/>*Asiento manual<a href="javascript:void(0)" onclick="help(event, 'helpDescripcion')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
</table>


<fieldset class="block-labels" >
  <legend>Parámetros de Importación</legend>
   <table class="lfr-table">
		<tr>
		    
			<td><label>Area Liquidación:</label></td>
			<td>
					<select id="<portlet:namespace />sector" name="<portlet:namespace />sector">
						<%for(CentroCosto sector:pSectores) {%>
						<option
							value="<%=sector.getId() %>"
							<%if(idSector!=null && idSector==sector.getId()){ %>
							   selected="selected" 
							<%} %>
							><%=sector.getDescripcion() %>
						</option>
					    <%}%>					
				    </select>
			</td>
			
			
			<td><label>Cuenta de Neteo:</label></td>
			<td>
					<select id="<portlet:namespace />neteo" name="<portlet:namespace />neteo">
						<%for(PlanCuentas cuenta:pCuentas) {%>
						<option
							value="<%=cuenta.getId() %>"
							<%if(idNeteo!=null && idNeteo==cuenta.getId()){ %>
							   selected="selected" 
							<%} %>
							><%=cuenta.getNumero() +" - " +cuenta.getCuenta() %>
						</option>
					    <%}%>					
				    </select>
			</td>
			
		</tr>
		<tr>
		    <td colspan="4">&nbsp;</td>
	    </tr>
		<tr>
		    <td><label style="color:blue">Seleccione Archivo proveniente del Sistema Bejerman(xls versión 97-2003) </label></td>
			<td  align="center">
				<input type="file" name="archivo"/>
			</td>
			<td align="center">
				<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivo()"/>
			</td>
			<td><input type="button" id="<portlet:namespace />limpiar_renglones" name="<portlet:namespace />limpiar_renglones" 
			    value="Limpiar Importación" onclick="javascript:<portlet:namespace />limpiarImportacion();"/>
		    </td>
		</tr>
		
		<tr>
		  <td>
		      <table>
		       <thead>
		          Tipo de Error:
		       </thead>
		       <tr>
		         <td colspan="6"  style="color:red">
		             SE  -  Sin equivalencia contable
		         </td>
		       </tr>
		      </table>
		  
		  </td> 
		</tr>
   </table>
</fieldset>   
<span id="guardando">
	<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
</span>

<% if (rolABM && !soloVer) {%> 
<br>
<table class="lfr-table"><Asiento>
  <tr>
    <td>
     <input type="button" value="Guardar" onclick="guardar()"/><a href="javascript:void(0)" onclick="help(event, 'helpGuardar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
    </td>
    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
    <td>
     <input type="button" value="Visualizar Asiento" onclick="visualizarAsiento()"/>&nbsp;
    </td>  
  </tr>      
</table>
<br>	
<br>
<% } %>

<fieldset class="block-labels">
			<legend>
					<label>Registros Importados:</label>
			</legend>
		
			<div align="center" id="<portlet:namespace />renglonesDiv">
				<liferay-util:include page="/html/portlet/tesoreria/contabilidad/editar_asientos_sueldos_search_result.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEditable)%>" />
				</liferay-util:include>
			</div>	
</fieldset>
			
<br/>

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
	
	function validar(){
		var totalDebe = 0;
		var totalHaber = 0;
		var error = false;
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
			alert("Los totales de Debe y Haber no coinciden");
			return;
		}
		
		
		
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/importar_asiento_sueldos';
		 url += '&cmd=save';
		document.<portlet:namespace />editar_asiento.method = 'post';
		submitForm(document.<portlet:namespace />editar_asiento, url);
		
	}
	jQuery(document).ready(function(){
		jQuery("#guardando").hide();
		
		jQuery("#fecha").datepicker(jQuery.datepicker.regional['es']);
		validar();
	});
	
	
	function cambioEjercicio(){
		jQuery("#fecha").val("");
		actualizarCuentas(-1);
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
					
				} else {
					jQuery('#cuenta_'+numeroDeSelect).find('option').remove();
					for(var i =0;i< obj.cuentas.length; i++){
						jQuery('#cuenta_'+numeroDeSelect).append('<option value="'+obj.cuentas[i].id+'">'+obj.cuentas[i].numero + '-' + obj.cuentas[i].cuenta +'</option>');
					}
				}
			}
		});		
	}
	
///----- Nuevo  ---------	
	
	function <portlet:namespace />uploadArchivo() {	
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/importar_asiento_sueldos';
		
		jQuery("#guardando").show();
		document.<portlet:namespace />editar_asiento.method = 'post';
		submitForm(document.<portlet:namespace />editar_asiento, url);
	}
	
	
	function <portlet:namespace />limpiarImportacion(){
		jQuery("#guardando").show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/importar_asiento_sueldos'
		    + '&cmd=clean'
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />renglonesDiv').load(url, function() {jQuery("#guardando").hide();});
	}
	
	
	var popupE;
	
	function editarEquivalencias(id,idcuenta,codigo,descripcion,sector,entidad,debehaber){
		jQuery("#guardando").show();
		if(popupE==null)
		    popupE = Liferay.Popup({title:"Equivalencias Códigos",modal:true,width:700,onClose: function() {
		    	
		    	popupE = null;
		    	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/importar_asiento_sueldos_equivalencias'
				    + '&cmd=review'
				url += '&rnd=' + Math.floor(Math.random()*100);
				jQuery('#<portlet:namespace />renglonesDiv').load(url, function() {jQuery("#guardando").hide();});
				
	    	
		    }});
	
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/importar_asiento_sueldos';
		url += '&cmd=equivalencias'  ;
		url += "&id="+id;
		url += "&cuentaid="+idcuenta;
        url += "&codigo="+codigo;
        url += "&descripcion="+encodeURI(descripcion);
        url += "&sector="+sector;
        url += "&entidad="+entidad;
        url += "&debehaber="+debehaber;
        jQuery(popupE).load(url);		
        	
	}
	
	
	function eliminarEquivalencias(id){
		jQuery("#guardando").show();
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/importar_asiento_sueldos_equivalencias';
		url += '&cmd=delete_equivalencias'  ;
		url += "&id="+id;
        jQuery("#<portlet:namespace />renglonesDiv").load(url,function() {jQuery("#guardando").hide();});		
        	
	}
	
	
	function visualizarAsiento(){
		jQuery("#guardando").show();
		var cuentaneteo = jQuery("#<portlet:namespace />neteo").val();
		if(popupE==null)
		    popupE = Liferay.Popup({title:"Visualización Asiento",modal:true,width:700,position:[150,10],xy: ['center', 100],onClose: function() {
		    	popupE = null;
		    	jQuery("#guardando").hide();
		    }});
	
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/importar_asiento_sueldos_equivalencias';
		url += '&cmd=asiento_view'  ;
		url += '&neteo='+cuentaneteo;
		jQuery(popupE).load(url);		
        	
	}
	
</script>

