<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>

<portlet:defineObjects />

<form action="ListaLugarAtencionAction" name="<portlet:namespace />prestador_lugarat_fm"
		 id="<portlet:namespace />prestador_lugarat_fm" method="post" >
 	<input  type="hidden" name="<portlet:namespace />" value="" />
 	<input type="hidden" name="<portlet:namespace />tab_seleccionada"  value="" />
 	<input type="hidden" name="<portlet:namespace />id_prestador_prestador"  value="0" />
	

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="domicilio-afip" />
	</legend>
	<p><b></b></p>
</fieldset>	
	
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="lugar-atencion-prestador" />
	</legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">		
		<tr>
			<td colspan="4">
				<div id="<portlet:namespace />lista_lugares_atencion">
					<jsp:include page='/html/portlet/prestadores/lista_lugares_atencion_prestador.jsp' />
				</div>
			</td>
		</tr>
		<tr><td colspan="4">* Seleccione un lugar de atención para ver el detalle</td></tr>	
	</table>			
</fieldset>	
	
<br/>

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="home-address" />
	</legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		
	    <input name="<portlet:namespace />id_domicilio" id="<portlet:namespace />id_domicilio" 
	    	type="hidden" value="" />
	    
	    <tr>
	    	<td style="width: 85px"><label><liferay-ui:message key="tipo-factura-prest" />:</label></td>
	    	<td colspan="1" style="width: 210px"><select id="<portlet:namespace/>lugarat_factura"
				name="<portlet:namespace/>lugarat_factura"  
				onchange="javascript:mostrarIndirecto();">					
					
			</select></td>
		    <td ><label><liferay-ui:message key="nombre-lugarat" />:</label></td>
	    	<td colspan="1"><input id="<portlet:namespace />lugarat_nombre"
				name="<portlet:namespace />lugarat_nombre"  size="50" maxlength="250"
				type="text"				value="" 			 readonly="readonly" />
			
			<td colspan="4">&nbsp;</td>
			<td>&nbsp;</td>	
	    </tr>
	    <tr>
	    	<td colspan="8">
				<div id="<portlet:namespace />divBuscarPrestador" name="<portlet:namespace />divBuscarPrestador">										
					<liferay-util:include page="/html/portlet/utils/prestadores/busqueda_prestador_lugar_at.jsp">
							<liferay-util:param name="search_url" value="/liquidaciones/buscar_prestador" />
							<liferay-util:param name="cuit_prestador" value='' />
							<liferay-util:param name="nombre_prestador" value='' />
							<liferay-util:param name="id_prestador" value='' />
							<liferay-util:param name="esEditable" value='' />
					</liferay-util:include>
				</div>
			</td>	
	    </tr>
	    
	   		
	   	
		
		
	</table>	
</fieldset>
<br/>
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="lugar-atencion-contactos-telefonos" />
	</legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
	
		<tr>
			<td colspan="5">
				<div id="<portlet:namespace />lista_telefonos">
					<liferay-util:include page="/html/portlet/prestadores/lista_telefonos_prestador_lugar_atencion.jsp">
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>			
</fieldset>

<br/>

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="lugar-atencion-contactos-electronicos" />
	</legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
	
		<tr>
			<td colspan="5">
				<div id="<portlet:namespace />lista_contactoes">
						<liferay-util:include page="/html/portlet/prestadores/lista_contactoes_prestador_lugar_atencion.jsp">
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>
</fieldset>	
<br/>	
			

<br />
<div align="left" style="vertical-align: bottom;" >
<input type="button" value="<liferay-ui:message key="back" />"
	onClick="<portlet:namespace />anteriorSolapa();" />
&nbsp;&nbsp;

<input type="submit" value="<liferay-ui:message key="save-prestador" />"
	onClick="<portlet:namespace />savePrestador();return false;" />
</div>


<div id='validarExistenciaCuit' style="float: right;"></div>

</form>

<% if(lugarAtencion != null && lugarAtencion.getVigenciaDesdeHabilitacion() == null){ %>
<script>
	jQuery('#<portlet:namespace/>laVigenteDesdeFechaDia').val('');
	jQuery('#<portlet:namespace/>laVigenteDesdeFechaMes').val('');
	jQuery('#<portlet:namespace/>laVigenteDesdeFechaAnio').val('');
	jQuery('#<portlet:namespace/>laVigenteHastaFechaDia').val(0);
	jQuery('#<portlet:namespace/>laVigenteHastaFechaMes').val(-1);
	jQuery('#<portlet:namespace/>laVigenteHastaFechaAnio').val(0);
	</script>
<%} %>	 

<script type="text/javascript">

jQuery("#<portlet:namespace />lugarat_nombre").focus();

<%if(lugarAtencion!=null && lugarAtencion.isPresentaCopiaHabilitacion()) {%>
jQuery("#<portlet:namespace/>lugarat_pres_copia_habilitacion").attr('checked', 'checked');  
<%} %>


	function validarExistencia(e) {
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode				
		var cuit = jQuery("#<portlet:namespace />cuit").val();
		
		if (cuit.length > 0) {									
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/buscar_cuit_existente&cuit='+cuit;
			jQuery("#validarExistenciaCuit").load(url);		
			jQuery("#validarExistenciaCuit").show();				
		}
	}

	function <portlet:namespace />savePrestador() {
		
		if(<%=lugarAtencion != null %> ){
			if (!confirm("Esta modificando un Lugar de Atención, se perderán su cambios recientes. ¿Desea continuar de todos modos?")){
				return false;
			}
		} 

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/liquidaciones/editar_prestadores_entry';
		<% if(prestador==null || (prestador != null && prestador.getId_prestador() <= 0 ) ) {%>
			url = url + '&<%=Constants.CMD %>='+'<%=Constants.ADD%>';
		<% }else{ %>
			url = url + '&<%=Constants.CMD %>='+'<%=Constants.UPDATE%>';
		<% } %>
		
		document.<portlet:namespace />prestador_lugarat_fm.action = url;
 		submitForm(document.<portlet:namespace />prestador_lugarat_fm, url);
	}     

	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}

	function filtrarLocalidad() {
		var idProvincia = jQuery('#<portlet:namespace/>provincia').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/id_provincia_localidad&idProvincia='+idProvincia;
		jQuery.ajax({   
			url: url,
			async: false,
			success: function(data){
				document.getElementById("<portlet:namespace/>localidad").length = 0;						
				var obj = jQuery.parseJSON(data);
				jQuery('.selector-localidad select').html(data).fadeIn();
				/*
				addElementToSelect("<portlet:namespace/>localidad", "Seleccione una localidad", 0);
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>localidad", text, value);					
				}
				*/
			}
		});
	}
	
	function filtrarCodPostal() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/id_localidad_codpostal&idLocalidad='+idLocalidad;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />cod_postal").length = 0;						
				var obj = jQuery.parseJSON(data);						
				jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
			}
		});	
	}
	
	function <portlet:namespace />buscarCodPostalOnDiv(e) {
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
		if (jQuery("#<portlet:namespace/>localidad").val() == "265" && jQuery("#<portlet:namespace />calle").val() != "" && jQuery("#<portlet:namespace />numero").val() > 0) {
			var calle = jQuery("#<portlet:namespace />calle").val();
			var numero = jQuery("#<portlet:namespace />numero").val();
			if (calle.length > 0 && numero > 0) {				
				var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
				jQuery("#divCodPostal").load(url);		
				jQuery("#divCodPostal").show();
			} else {        
	    		jQuery("#divCodPostal").hide("slow");
	   		}
		}
	}
	
	function <portlet:namespace />cerrarCodPostal() {	
		jQuery("#divCodPostal").hide("slow");
	}
	
	function <portlet:namespace />agregarLugarAtTelefono(){
		
		var tipoTel =jQuery('#<portlet:namespace/>tipo_telefono').val();
		var codPais =jQuery('#<portlet:namespace/>telefono_pais').val(); 
		var codArea =jQuery('#<portlet:namespace/>telefono_area').val();
		var numero  =jQuery('#<portlet:namespace/>telefono_numero').val();	
		var exten   =jQuery('#<portlet:namespace/>telefono_ext').val();
		var obs   =jQuery('#<portlet:namespace/>telefono_obs').val();
		var idTel =jQuery('#<portlet:namespace/>telefono_id').val();
	    var propio = "P";
	    if(jQuery('#<portlet:namespace/>lugarat_factura').val() == 'DIRECTO' ){
	    	propio = 'D'
	    }
		if(<portlet:namespace />validaLugarAtTelefono()){
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/lista_telefonos_lugar_at_prestador';
			url = url+'&idTelefono='+idTel+
			'&tipoTel='+tipoTel+
			'&codPais='+codPais+
			'&codArea='+codArea+
			'&numero='+numero+
			'&exten='+exten+
			'&obs='+encodeURI(obs)+
			'&propio='+propio;
			
			jQuery("#<portlet:namespace />lista_telefonos").load(url); 
			
			/* Limpiamos campos */
			jQuery('#<portlet:namespace/>telefono_pais').val('54'); 
			jQuery('#<portlet:namespace/>telefono_area').val('011');
			jQuery('#<portlet:namespace/>telefono_numero').val('');	
			jQuery('#<portlet:namespace/>telefono_ext').val('');
			jQuery('#<portlet:namespace/>telefono_obs').val('');
		}
		
	}
	
	function <portlet:namespace />validaLugarAtTelefono(){
	
		if (trim(jQuery('#<portlet:namespace />telefono_pais').val()) == '' ||
			trim(jQuery('#<portlet:namespace />telefono_area').val()) == '' ||
			trim(jQuery('#<portlet:namespace />telefono_numero').val()) == ''){
				alert("El teléfono debe necesariamente tener el código de país, de area y el número");
				jQuery('#<portlet:namespace />telefono_numero').focus();
				return false;
		}
		return true;
	}	
	
	function <portlet:namespace />agregarLugarAtContactoE(){
		
		var tipoContE =jQuery('#<portlet:namespace/>tipo_contacto').val();
		var descripcion =jQuery('#<portlet:namespace/>contactoe_descripcion').val(); 
		var obs   =jQuery('#<portlet:namespace/>contactoe_obs').val();
		var idContE =jQuery('#<portlet:namespace/>contactoe_id').val();
		var propio = "P";
	    if(jQuery('#<portlet:namespace/>lugarat_factura').val() == 'DIRECTO' ){
	    	propio = 'D'
	    }
		
		if(<portlet:namespace />validaLugarAtContacto()){
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/lista_contactos_lugar_at_prestador';
			url = url+'&idContactoE='+idContE+
			'&tipoContacto='+tipoContE+
			'&descripcion='+descripcion+
			'&obs='+encodeURI(obs)+
			'&propio='+propio;
			
			jQuery("#<portlet:namespace />lista_contactoes").load(url); 
			
			/* Limpiamos campos */
			jQuery('#<portlet:namespace/>contactoe_descripcion').val(''); 
			jQuery('#<portlet:namespace/>contactoe_obs').val('');
		}
		
	}
	
	function <portlet:namespace />validaLugarAtContacto(){
	
		var tipoContE =jQuery('#<portlet:namespace/>tipo_contacto').val();
		
		if(tipoContE == '<%=ContactoElectronico.Tipo.EMAIL%>'){
			return <portlet:namespace />validarEmail();
		}	
		if(tipoContE == '<%=ContactoElectronico.Tipo.SITIOWEB%>'){
			return <portlet:namespace />validarSitioWeb();
		}		
		return false;
	}
	
	function <portlet:namespace />saveLugarAtencionCompleto(){
		
	 	if(<portlet:namespace />validaLugarAtDomicilio()){
	 
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/lista_lugares_atencion_prestador" /></portlet:actionURL>';
			url = url + '&<%=Constants.CMD %>='+'<%=Constants.ADD%>';
			document.<portlet:namespace />prestador_lugarat_fm.method = 'post';
			submitForm(document.<portlet:namespace />prestador_lugarat_fm, url);
		} 
		
	}
	
	function <portlet:namespace />validaLugarAtDomicilio(){
		
		if(jQuery('#<portlet:namespace/>lugarat_nombre').val() == ""|| jQuery('#<portlet:namespace/>lugarat_nombre').val()==0){
			alert("<liferay-ui:message key='nombre-lugarat-obligatorio' />");
			jQuery("#<portlet:namespace />lugarat_nombre").focus();
			return false;
		}
		
		if(jQuery('#<portlet:namespace/>provincia').val() == ""|| jQuery('#<portlet:namespace/>provincia').val()==0){
			alert("<liferay-ui:message key='provincia-obligatoria' />");
			jQuery("#<portlet:namespace />provincia").focus();
			return false;
		}
		
		if(jQuery('#<portlet:namespace/>localidad').val() == "" || jQuery('#<portlet:namespace/>localidad').val()==0){
			alert("<liferay-ui:message key='localidad-obligatoria' />");
			jQuery("#<portlet:namespace />localidad").focus();
			return false;
		}
	
		if (trim(jQuery('#<portlet:namespace/>calle').val()).length == 0) {
			alert("<liferay-ui:message key='calle-obligatorio' />");
			jQuery('#<portlet:namespace />calle').focus();
			return false;
		}
		
		if (!isPositiveInteger(trim(jQuery('#<portlet:namespace/>cod_postal').val()))){
			alert("<liferay-ui:message key='codigo-postal-invalido' />");
			jQuery('#<portlet:namespace />cod_postal').focus();
			return false;
		}	
	
		return true;
	}	
	
	function <portlet:namespace />validarEmail() {
		var email = jQuery('#<portlet:namespace/>contactoe_descripcion').val();
	
		if(trim(email).length == 0){
			alert("El campo descripción del Email es Obligatorio");
			jQuery("#<portlet:namespace />contactoe_descripcion").focus();
			return false;
		}
		var expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		
		if (!expr.test(email) ){
		    alert("Error: La dirección de correo " + email + " es incorrecta.");
		    jQuery("#<portlet:namespace />contactoe_descripcion").focus();
			return false;
		}
		return true;
	}
	
	function <portlet:namespace />validarSitioWeb() {
		var sitioWeb = jQuery('#<portlet:namespace/>contactoe_descripcion').val();
	
		if(trim(sitioWeb).length == 0){
			alert("El campo descripción del Sitio WEb es Obligatorio");
			jQuery("#<portlet:namespace />contactoe_descripcion").focus();
			return false;
		}
	
		var regexp = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/
		
		/* return regexp.test(sitioWeb); */
		if (!regexp.test(sitioWeb) ){
		    alert("Error: La url " + sitioWeb + " es incorrecta.");
		    jQuery("#<portlet:namespace />contactoe_descripcion").focus();
			return false;
		}
		return true;
	}
	
	function <portlet:namespace />limpiarLugarAtDomicilio(){
		
		jQuery('#<portlet:namespace/>lugarat_factura').val('DIRECTO');
		jQuery('#<portlet:namespace/>lugarat_nombre').val('');
		jQuery('#<portlet:namespace/>lugarat_nro_habilitacion').val('');
		jQuery('#<portlet:namespace/>lugarat_aut_habilitacion').val('');
		jQuery('#<portlet:namespace/>id_prestador').val('');
	/* 	var presCopia = document.getElementById("<portlet:namespace />lugarat_pres_copia_habilitacion");
		var presCopiaHabil = presCopia.checked ? 'true' : 'false'; */
		jQuery('#<portlet:namespace/>provincia').val(0);
		jQuery('#<portlet:namespace/>localidad').val(0);
		jQuery('#<portlet:namespace/>calle').val('');
		jQuery('#<portlet:namespace/>numero').val('');
		jQuery('#<portlet:namespace/>piso').val('');
		jQuery('#<portlet:namespace/>dpto').val('');
		jQuery('#<portlet:namespace/>cod_postal').val('');
		jQuery('#<portlet:namespace/>barrio').val('');
	    jQuery('#<portlet:namespace/>cat_prof').val('');
	    jQuery('#<portlet:namespace/>reg_histo_clinica').val('');
	    jQuery("#<portlet:namespace/>lugarat_pres_copia_habilitacion").attr('checked', ''); //checked
	    
	    /* recargar las listas de telefonos y contactoes desp de agregar un lugar de atencion completo */
	    
	    mostrarIndirecto();
	}    
	
	function mostrarIndirecto() {
		jQuery('#<portlet:namespace />divBuscarPrestador').hide();
		var tipo=jQuery('#<portlet:namespace/>lugarat_factura').val();
		
		if (tipo == 'INDIRECTO') {
			jQuery('#<portlet:namespace />divBuscarPrestador').show();			
		}else{
			jQuery('#<portlet:namespace />divBuscarPrestador').hide();
		}
	}
	mostrarIndirecto();
		
	function <portlet:namespace />updateLugarAtencionCompleto(){
	
	 	if(<portlet:namespace />validaLugarAtDomicilio()){
			
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/lista_lugares_atencion_prestador" /></portlet:actionURL>';
			url = url + '&<%=Constants.CMD %>='+'<%=Constants.UPDATE%>';
			document.<portlet:namespace />prestador_lugarat_fm.method = 'post';
			submitForm(document.<portlet:namespace />prestador_lugarat_fm, url);
		}
	}
	
	function <portlet:namespace />limpiarCamposLugarAt(){
			
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/lista_lugares_atencion_prestador" /></portlet:actionURL>';
		url = url + '&<%=Constants.CMD %>='+'<%=Constants.RESET%>';
		document.<portlet:namespace />prestador_lugarat_fm.method = 'post';
		submitForm(document.<portlet:namespace />prestador_lugarat_fm, url);	
	}
	
	function <portlet:namespace />anteriorSolapa() {		
		 /* if (<portlet:namespace />validarCampos()) { */ 
			<%-- var accionEnCurso = jQuery('#<portlet:namespace /><%= Constants.CMD %>').val(); --%>
			var accionEnCurso = document.<portlet:namespace />prestador_lugarat_fm.<portlet:namespace /><%= Constants.CMD %>.value;
			document.<portlet:namespace />prestador_lugarat_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.MOVE %>';
			
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_prestadores_entry" /></portlet:actionURL>';
			url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest';
			
			document.<portlet:namespace />prestador_lugarat_fm.method = 'post';
			submitForm(document.<portlet:namespace />prestador_lugarat_fm, url);
	 	/* } */  
	}
	
	
	if(jQuery('#<portlet:namespace/>provincia').val()!=null){
		var idLocal = jQuery('#<portlet:namespace/>localidad').val();
		filtrarLocalidad();
		jQuery('#<portlet:namespace/>localidad').val(idLocal);
	};
	
</script>