<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
    Paritaria paritaria = (Paritaria)request.getAttribute(WebKeysUOMA.ALTA_PARITARIAS);
	String paramMes=null;
	String paramAnio=null;				
	Calendar periodoDesde = Calendar.getInstance(); 		
	if(paritaria!=null){
		periodoDesde.setTime(paritaria.getFechaAltaParitaria());
		periodoDesde.add(Calendar.MONTH, +1);    
	}
	
    String modoConsulta = (String) request.getAttribute("modoConsulta");
      
%>
<form name="formAltaParitaria" id="formAltaParitaria" method="post">
<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
<% if (modoConsulta != null && modoConsulta.equalsIgnoreCase("true")) {%>
<fieldset class="block-labels"><legend><liferay-ui:message 	key="ver-paritaria" /></legend>
<%}else{%>
<fieldset class="block-labels"><legend><liferay-ui:message 	key="alta-paritaria" /></legend>
<%}%> 
<table class="lfr-table">
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr><td colspan="6"><b>RECORDAR SOLICITAR REINICIAR TOMCAT DE PORTAL EMPLEADORES A SISTEMAS CUANDO SE GUARDAN CAMBIOS.</b> </td></tr>
		<tr><td colspan="6">&nbsp;</td></tr>
		<tr>
			<td>
			 	<td><liferay-ui:message key="fecha_inicio_paritaria" />:&nbsp;</td>
				<td><liferay-ui:input-date
						dayParam="fechaDesdeDia"
						dayNullable="<%= true %>" 
						dayValue="01" 
						monthParam="fechaDesdeMes"
						monthValue="<%= periodoDesde.get(Calendar.MONTH) %>"
						yearParam="fechaDesdeAnio"
						yearValue="<%= periodoDesde.get(Calendar.YEAR) %>"
						yearRangeStart="<%= periodoDesde.get(Calendar.YEAR)-5 %>"
						yearRangeEnd="<%= periodoDesde.get(Calendar.YEAR) +1  %>"
						firstDayOfWeek="<%= periodoDesde.getFirstDayOfWeek()%>"
						/> 			
				</td>
			</td>
			<td><label><liferay-ui:message key="camara" />:</label></td>
			<td><select name="<portlet:namespace/>nombre_camara" id="<portlet:namespace/>nombre_camara">
						<option value="0">Seleccione Camara</option>
						<%for(int i = 0; i < WebKeysUOMA.CAMARA.length; i++ ) {%>
			               <option value="<%=WebKeysUOMA.CAMARA[i][0] %>" 
					     <% if(paritaria!=null && paritaria.getCamara().equals(WebKeysUOMA.CAMARA[i][0])) {%> selected="selected" <%} %> > <%=WebKeysUOMA.CAMARA[i][0] %> </option> 
				       <% }%>
			</select>
			</td>			
		</tr>
		<tr>
			<td colspan="6">&nbsp;</td>
		</tr>

</table>
</fieldset>
<div align="center" id="<portlet:namespace />escalas_sueldos_div">
<fieldset class="block-labe	ls"><legend><liferay-ui:message	key="escalas_sueldos"  /></legend>
<table class="lfr-table">
		<tr >
		     <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		     <th colspan="5"><label><liferay-ui:message key="escalas_sueldos_basicos"/></label></th>
		     <td colspan="27">&nbsp;</td>
		     <th colspan="5"><label><liferay-ui:message key="escalas_sueldos_basicos_jornales"/></label></th>
		</tr>
		</tr>
		<tr> 	
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td><label><liferay-ui:message key="cat_a" />:</label></td>
			<td><label><liferay-ui:message key="cat_b" />:</label></td>
			<td><label><liferay-ui:message key="cat_c" />:</label></td>
			<td><label><liferay-ui:message key="cat_d" />:</label></td>
			<td><label><liferay-ui:message key="cat_e" />:</label></td>	
			<td>&nbsp;</td>
			<td colspan="25">&nbsp;</td>
			<td>&nbsp;</td>
			<td><label><liferay-ui:message key="cat_a" />:</label></td>
			<td><label><liferay-ui:message key="cat_b" />:</label></td>
			<td><label><liferay-ui:message key="cat_c" />:</label></td>
			<td><label><liferay-ui:message key="cat_d" />:</label></td>
			<td><label"><liferay-ui:message key="cat_e" />:</label></td>	
		</tr>
		<tr>
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td><input  size="12" id="<portlet:namespace />importe_cat_a" name="<portlet:namespace />importe_cat_a" type="text" value="<%if (paritaria!=null ) {%> <%=paritaria.getCatA()%> <%}%>"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_cat_a'),event);"/></td>
			<td><input  size="12" id="<portlet:namespace />importe_cat_b" name="<portlet:namespace />importe_cat_b" type="text" value="<%if (paritaria!=null ) {%> <%=paritaria.getCatB()%> <%}%>"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_cat_b'),event);"/></td>
			<td><input  size="12" id="<portlet:namespace />importe_cat_c" name="<portlet:namespace />importe_cat_c" type="text"   value="<%if (paritaria!=null ) {%> <%=paritaria.getCatC() %> <%}%>"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_cat_c'),event);"/></td>
			<td><input  size="12" id="<portlet:namespace />importe_cat_d" name="<portlet:namespace />importe_cat_d" type="text" value="<%if (paritaria!=null )  {%> <%=paritaria.getCatD()%> <%} %>" 
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_cat_d'),event);"/></td>
			<td><input  size="12" id="<portlet:namespace />importe_cat_e" name="<portlet:namespace />importe_cat_e" type="text"   value="<%if (paritaria!=null ) {%> <%=paritaria.getCatE()%> <%}%>"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_cat_e'),event);"/></td>
			<td>&nbsp;</td>
			<td colspan="25">&nbsp;</td>
			<td>&nbsp;</td>
			<td><input  size="12" id="<portlet:namespace />importe_cat_jornal_a" name="<portlet:namespace />importe_cat_jornal_a" type="text" value="<%if (paritaria!=null ) {%> <%= paritaria.getCatJornalesA()%> <%} %>"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_cat_jornal_a'),event);"/></td>
			<td><input  size="12" id="<portlet:namespace />importe_cat_jornal_b" name="<portlet:namespace />importe_cat_jornal_b" type="text" value="<%if (paritaria!=null )  {%><%= paritaria.getCatJornalesB()%> <%} %>"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_cat_jornal_b'),event);"/></td>
			<td><input  size="12" id="<portlet:namespace />importe_cat_jornal_c" name="<portlet:namespace />importe_cat_jornal_c" type="text" value="<%if (paritaria!=null ) {%> <%= paritaria.getCatJornalesC()%> <%} %>"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_cat_jornal_c'),event);"/></td>
			<td><input  size="12" id="<portlet:namespace />importe_cat_jornal_d" name="<portlet:namespace />importe_cat_jornal_d" type="text" value="<%if (paritaria!=null )  {%><%= paritaria.getCatJornalesD()%><%} %>"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_cat_jornal_d'),event);"/></td>
			<td><input  size="12" id="<portlet:namespace />importe_cat_jornal_e" name="<portlet:namespace />importe_cat_jornal_e" type="text" value="<%if (paritaria!=null )  {%><%=paritaria.getCatJornalesE()%> <%} %>"
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_cat_jornal_e'),event);"/></td>
		</tr>
</table>
</fieldset>
</div>
<div align="center" id="<portlet:namespace />sueldos_generados_div">
<fieldset class="block-labe	ls"><legend><liferay-ui:message	key="calculo_sueldo_basicos" /></legend>
<table class="lfr-table">
	<tr>
		<td>
		<div align="center" id="<portlet:namespace />busquedas_sueldos_basicosDiv">
			<liferay-util:include page="/html/portlet/uoma/paritarias/busquedas_sueldos_basicos.jsp"/>
			</div>
		</td>
		<td>
		<div align="center" id="<portlet:namespace />busquedas_sueldos_basicos_jornalesDiv">
			<liferay-util:include page="/html/portlet/uoma/paritarias/busquedas_sueldos_basicos_jornales.jsp"/>
			</div>
		</td>
	</tr>
			
</table>
</fieldset>
</div>
		<br>
		<div align="left" id="<portlet:namespace />button_div">
		<%if( !Constants.SAVE.equalsIgnoreCase((String)request.getAttribute(Constants.SAVE)) ){ %>
			<input type="button"  value="<liferay-ui:message key="simular_paritaria" />" onClick="<portlet:namespace />generarParitaria();" />
		<%} %>		
		<%if(paritaria!=null && !Constants.SAVE.equalsIgnoreCase((String)request.getAttribute(Constants.SAVE)) ){ %>
			<input type="button"  value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />agregarParitaria();" />
		<%} %>
		<input type="button"  value="<liferay-ui:message key="clear-all" />" onClick="<portlet:namespace />limpiarCampos();" />
		</div>

</form>	
<script type="text/javascript">
	jQuery('#<portlet:namespace />fechaDesdeDia').hide();	
		
	<% if (modoConsulta != null && modoConsulta.equalsIgnoreCase("true")) {%>
	     jQuery('#<portlet:namespace />escalas_sueldos_div').hide();	
	     jQuery('#<portlet:namespace />button_div').hide();	
	 
    <%}%> 
    	
	function <portlet:namespace />agregarParitaria() {
		if (<portlet:namespace />validarCampos()) {
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/alta_ver_paritaria" /></portlet:actionURL>';
			url=url+'&simular=false';
			document.formAltaParitaria.method = 'post';
			submitForm(document.formAltaParitaria, url);
		}
	}  
	
	function <portlet:namespace />generarParitaria() {
		if (<portlet:namespace />validarCampos()) {
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/alta_ver_paritaria" /></portlet:actionURL>';
			url=url+'&simular=true';
			document.formAltaParitaria.method = 'post';
			submitForm(document.formAltaParitaria, url);
		}	
	}     

	function <portlet:namespace />validarCampos() {
	    	var nombre_camara =  document.getElementById("<portlet:namespace />nombre_camara").value;
			var mes =jQuery('#<portlet:namespace />fechaDesdeMes').val();
			var anno =jQuery('#<portlet:namespace />fechaDesdeAnio').val();
			var importe_cat_a =jQuery('#<portlet:namespace />importe_cat_a').val();
			var importe_cat_b =jQuery('#<portlet:namespace />importe_cat_b').val();
			var importe_cat_c =jQuery('#<portlet:namespace />importe_cat_c').val();
			var importe_cat_d =jQuery('#<portlet:namespace />importe_cat_d').val();
			var importe_cat_e =jQuery('#<portlet:namespace />importe_cat_e').val();
			var importe_cat_jornal_a =jQuery('#<portlet:namespace />importe_cat_jornal_a').val();
			var importe_cat_jornal_b =jQuery('#<portlet:namespace />importe_cat_jornal_b').val();
			var importe_cat_jornal_c =jQuery('#<portlet:namespace />importe_cat_jornal_c').val();
			var importe_cat_jornal_d =jQuery('#<portlet:namespace />importe_cat_jornal_d').val();
			var importe_cat_jornal_e =jQuery('#<portlet:namespace />importe_cat_jornal_e').val();
			
			if(nombre_camara == 0){
			   alert("Debe seleccionar una camara");
			   return false;
			}
			if(mes.length == 0 || anno.length == 0){
				  alert("Debe seleccionar un periodo");
			 return false;
			}
			if(importe_cat_a.length == 0){
				  alert("Debe cargar un importe cat a");
			 return false;
			}
			if(importe_cat_b.length == 0){
				  alert("Debe cargar un importe cat b");
			 return false;
			}
			if(importe_cat_c.length == 0){
				  alert("Debe cargar un importe cat c");
			 return false;
			}
			if(importe_cat_d.length == 0){
				  alert("Debe cargar un importe cat d");
			 return false;
			}
			if(importe_cat_e.length == 0){
				  alert("Debe cargar un importe cat e");
			 return false;
			}
			if(importe_cat_jornal_a.length == 0){
				  alert("Debe cargar un importe cat jornal a");
			 return false;
			}
			if(importe_cat_jornal_b.length == 0){
				  alert("Debe cargar un importe cat jornal b");
			 return false;
			}
			if(importe_cat_jornal_c.length == 0){
				  alert("Debe cargar un importe cat jornal c");
			 return false;
			}
			if(importe_cat_jornal_d.length == 0){
				  alert("Debe cargar un importe cat jornal d");
			 return false;
			}
			if(importe_cat_jornal_e.length == 0){
				  alert("Debe cargar un importe cat jornal e");
			 return false;
			}if(validarParitariaExistente()){
				alert("La paritaria cargada ya existe o es menor a la última cargada");
				return false
			}	    	
		return true;
	}
	


	function validarParitariaExistente() {
	   
		var cuit="";
	    var params="";
	    var camara = jQuery("#<portlet:namespace/>nombre_camara").val();
	    var fechaDesdeMes = jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	    var fechaDesdeAnio = jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	    var respuesta=true;
	    var rta=false;
	    params += "&nombre_camara="+camara;
	    params += "&fechaDesdeMes="+fechaDesdeMes;
	    params += "&fechaDesdeAnio="+fechaDesdeAnio;
	    
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/validar_paritaria_existente';
		   url = url + params;
		   jQuery.ajax({   
			   url: url,
			   async: false,
			   success: function(data) {
					var obj = jQuery.parseJSON(data);
					var resp = obj.existe;
					rta=(resp  === 'true');
		   		}
		   }); 
		   if(rta){
			  
			  return false;
		   }
		   
	       return  respuesta;    
		 
	}
	
	
	function <portlet:namespace />limpiarCampos(){
		document.getElementById("<portlet:namespace />nombre_camara").value ='';
		jQuery('#<portlet:namespace />fechaDesdeMes').val('');
		jQuery('#<portlet:namespace />fechaDesdeAnio').val('');
		jQuery('#<portlet:namespace />importe_cat_a').val('');
		jQuery('#<portlet:namespace />importe_cat_b').val('');
		jQuery('#<portlet:namespace />importe_cat_c').val('');
		jQuery('#<portlet:namespace />importe_cat_d').val('');
		jQuery('#<portlet:namespace />importe_cat_e').val('');
		jQuery('#<portlet:namespace />importe_cat_jornal_a').val('');
		jQuery('#<portlet:namespace />importe_cat_jornal_b').val('');
		jQuery('#<portlet:namespace />importe_cat_jornal_c').val('');
		jQuery('#<portlet:namespace />importe_cat_jornal_d').val('');
		jQuery('#<portlet:namespace />importe_cat_jornal_e').val('');
		
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/alta_ver_paritaria"/></portlet:actionURL>';
		url = url + '&<%=Constants.CMD %>='+'<%=Constants.RESET%>';
		document.formAltaParitaria.method = 'post';
		submitForm(document.formAltaParitaria, url);
			

	}

	
</script>