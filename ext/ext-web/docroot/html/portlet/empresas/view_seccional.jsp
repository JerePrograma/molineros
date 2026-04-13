<%@ include file="/html/portlet/empresas/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
Seccional empresa = (Seccional)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);


boolean esEdicion = true;

%>
<c:choose>
	<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>			
		<liferay-ui:success key="request_processed" message="grabar-exitoso" />
	</c:when>
</c:choose>
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
<form action="" method="post" name="<portlet:namespace />emple">
<div id="<portlet:namespace />empresa_div" name="<portlet:namespace />empresa_div" >
<fieldset class="block-labels"><legend><liferay-ui:message
	key="datos-empresa" /></legend>
<table class="lfr-table">	
	<tr>
		<td><label><liferay-ui:message key="cuit" />:</label></td>
		<td><input id="<portlet:namespace />cuit"
			name="<portlet:namespace />cuit" size="13" maxlength="11" type="text"
			value="<%= empresa != null ? empresa.getCuit() : "" %>"
			<% if (empresa != null) { %> <%="readonly='readonly'" %> <%}%> /></td>		
		<td><label><liferay-ui:message key="razon-social" />:</label></td>
		<td><input id="<portlet:namespace />desc"
			name="<portlet:namespace />desc" size="50" type="text"
			value="<%= empresa != null ? empresa.getDescripcion() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
		<td>
			<table class="lfr-table">
				<tr>
					<td><label><liferay-ui:message key="seccional" />:</label></td>
					<td colspan="5"><liferay-util:include
						page="/html/portlet/empresas/busqueda_seccional.jsp">
						<liferay-util:param name="id_seccional"
							value="<%= empresa!=null ? String.valueOf(empresa.getId_seccional()) : new String() %>" />
						<liferay-util:param name="seccional" value="" />
						<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
					</liferay-util:include></td>
				</tr>
			</table>
		</td>		
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>	
	<tr>
		<td><label><liferay-ui:message key="destino-correspondencia" />:</label></td>
		<td>
			<textarea rows="5" cols="40" id="<portlet:namespace />destino" name="<portlet:namespace />destino"><%=empresa != null && null!= empresa.getDestinoCorrespondencia() ? empresa.getDestinoCorrespondencia() : ""%></textarea> 
		</td>
		<td><label><liferay-ui:message key="CBU" />:</label></td>
		<td><input id="<portlet:namespace />cbu"
			name="<portlet:namespace />cbu" size="22" maxlength="22"
			type="text"
			value="<%= empresa != null && null!= empresa.getCBU() ? empresa.getCBU() : "" %>"/></td>
		<td><label><liferay-ui:message key="cheque-a-nombre" />:</label></td>
		<td><input id="<portlet:namespace />cheque"
			name="<portlet:namespace />cheque" size="30" type="text"
			value="<%= empresa != null && null!= empresa.getPortaCheque() ? empresa.getPortaCheque() : "" %>"/>
		</td>			
	</tr>	
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	</table>
	<fieldset class="block-labels"><legend><liferay-ui:message	key="domicilio-empresa-ospim" /></legend>
	<table style="width:100%;">
		<tr>
			<td colspan="6">
				<liferay-util:include page="/html/portlet/empresas/agregar_domicilio.jsp">
					<liferay-util:param name="esEdicion" value="true"/>
				</liferay-util:include>
			</td>		
		</tr>
	</table>
	</fieldset>
	<table style="width:100%;">
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6">
			<liferay-util:include page="/html/portlet/empresas/agregar_contacto.jsp">
				<liferay-util:param name="esEdicion" value="true"/>
			</liferay-util:include>
		</td>		
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="5"><textarea rows="5" cols="50"
			id="<portlet:namespace />observaciones"
			name="<portlet:namespace />observaciones" <% if (!esEdicion) { %>
			<%="readonly='readonly'" %> <%}%>><%= empresa != null && empresa.getObservaciones() != null? empresa.getObservaciones() : "" %></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
</table>
</fieldset>
<% if (esEdicion) { %>
<br />
<div align="center">
<input type="submit" value="<liferay-ui:message key="save" />"
	<% if (!portlet_name.equals("estudio_isidro")&&!portlet_name.equals("liquidaciones")) { %>
		onClick="<portlet:namespace />saveEmpleador();return false;" 
	<%} else { %>
		onClick="<portlet:namespace />saveEmpleadorPopUp();return false;"
	<%} %> />
</div>	
<input name="<portlet:namespace /><%=Constants.CMD%>" type="hidden" value="" />
</form>	
<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa" />
<input type="hidden" value="" name="tabs1" id="tabs1" />
<input type="hidden" value="" name="view" id="view" />
<input type="hidden" value="" name="flag" id="flag" />
<input type="hidden"
	value="<%= Constants.UPDATE %>"
	name="accionOriginal" id="accionOriginal" />
<%} %>
			<div align="center" id="<portlet:namespace />saveEmpleadorDiv">						
			</div>
</div>			
<script type="text/javascript">
	function <portlet:namespace />saveEmpleador() {		
		var cbu=jQuery('#<portlet:namespace />cbu').val();
		if(cbu.trim().length>0 && !validarCBU(cbu, "<liferay-ui:message key='valida-cbu'/>")){
			jQuery('#<portlet:namespace />cbu').focus();
			return false;
		}	
		document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.UPDATE %>";
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString()%>"/>&struts_action=/<%=portlet_name%>/editar_seccionales_entry';
		<% if(portlet_name.equals("estudio_isidro")) {%>
			url=url+'&flagEstudio=true';
		<%}%>			
		document.<portlet:namespace />emple.method = 'post';				
		submitForm(document.<portlet:namespace />emple, url);		 
	}
	function <portlet:namespace />saveEmpleadorPopUp(){
		var cbu=jQuery('#<portlet:namespace />cbu').val();
			
		var form = jQuery(document.<portlet:namespace />emple);
		var url = '<portlet:actionURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/editar_seccionales_entry';
			document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.UPDATE %>";
			
		if(cbu.trim().length>0 && !validarCBU(cbu, "<liferay-ui:message key='valida-cbu'/>")){
				jQuery('#<portlet:namespace />cbu').focus();
				return false;
		}	
			form.ajaxForm(
				{
					url: url,
			    	target: popup,//".ui-dialog-content",//poopup
			        type: "POST",
			        beforeSubmit: function() {			        
			        },
			        success: function() {
			        	<% if (portlet_name.equals("liquidaciones")) { %>			        	
		        		sugerirRazonSocialChequeYDestino();
		        		<%}%>
			        }
			    }
			);	
						
			form.submit();    
		
	}
	

	function filtrarLocalidad() {		
		var idProvincia = jQuery('#<portlet:namespace/>provincia').val();		
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/id_provincia_localidad&idProvincia='+idProvincia;
		
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace/>localidad").length = 0;						
				var obj = jQuery.parseJSON(data);
				addElementToSelect("<portlet:namespace/>localidad", "Seleccione una localidad", 0);
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>localidad", text, value);
				}                                                                                                                                                                                                                                                            
			}
		});		
	}

	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}

	function filtrarCodPostal() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/id_localidad_codpostal&idLocalidad='+idLocalidad;		
		
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
				
				var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
				
				jQuery("#divCodPostal").load(url);		
				jQuery("#divCodPostal").show();
			} else {        
	    		jQuery("#divCodPostal").hide("slow");
	   		}
		}
	}
	
	function validarCBU(input, message){				
		if(input.trim().length==22){
			a=input.substring(0,1);
			b=input.substring(1,2);
			c=input.substring(2,3);
			d=input.substring(3,4);
			
			q=input.substring(4,5);
			r=input.substring(5,6);
			s=input.substring(6,7);
			
			valida1=input.substring(7,8);
			//alert(a+' '+b+' '+c+' '+d+' '+q+' '+r+' '+s);
			
			suma1=a*7+b*1+c*3+d*9+q*7+r*1+s*3;
			cadenaVal=suma1.toString().substring(suma1.toString().length-1,suma1.toString().length);
			diferencia1= 10-parseInt(cadenaVal);
			
			if(diferencia1==10){
                diferencia1=0;
        	}
			
			if(valida1!=diferencia1){
				alert('ERROR AL VALIDAR CBU, VERIFIQUE NUMEROS');
				return false;				
			}
			
			a=input.substring(8,9);
			b=input.substring(9,10);
			c=input.substring(10,11);
			d=input.substring(11,12);
			e=input.substring(12,13);
			f=input.substring(13,14);
			g=input.substring(14,15);
			h=input.substring(15,16);
			i=input.substring(16,17);
			j=input.substring(17,18);
			k=input.substring(18,19);
			l=input.substring(19,20);
			m=input.substring(20,21);
			
			//alert(a+' '+b+' '+c+' '+d+' '+e+' '+f+' '+g+' '+h+' '+i+' '+j+' '+k+' '+l+' '+m);
			valida2=input.substring(21,22);
			
			suma2=a*3+b*9+c*7+d*1+e*3+f*9+g*7+h*1+i*3+j*9+k*7+l*1+m*3;
			
			cadenaVal2=suma2.toString().substring(suma2.toString().length-1,suma2.toString().length);
			diferencia2= 10-parseInt(cadenaVal2);
			
			if(diferencia2==10){
                diferencia2=0;
        	}
			
			if(valida2!=diferencia2){
				alert('Ha ingresado un CBU inválido, por favor, verifique dígitos ingresados');
				return false;				
			}
			
			
			if(isPositiveInteger(input)){
				return true
			}		
		}
		alert(message);
		return false;		
	}
	
	
	function <portlet:namespace />cerrarCodPostal() {	
		jQuery("#divCodPostal").hide("slow");
	}
</script>