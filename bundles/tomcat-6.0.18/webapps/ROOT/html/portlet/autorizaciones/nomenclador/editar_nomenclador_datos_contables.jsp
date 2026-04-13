<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.PrestacionConcepto"%>
<%
	String viewStr = (String)request.getAttribute("view");
	Nomenclador nomenclador=(Nomenclador)request.getSession().getAttribute(WebKeysAutorizaciones.NOMENCLADOR_EN_EDICION);
	PrestacionConcepto prestacionConcepto=(PrestacionConcepto)request.getSession().getAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_EN_EDICION);
	
	PrestacionConcepto prestacionConceptoOriginal=(PrestacionConcepto)request.getSession().getAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_ORIGINAL);
	String accion = (String)request.getSession().getAttribute("accion");
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	int id_nomenclador=nomenclador!=null?(int)nomenclador.getId_prestacion():0;
%>

<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<fieldset class="block-labels"><legend>Información Contable</legend>
    
      <table class="lfr-table">
        <tr>
         <td>
		  <liferay-ui:message key="marca-reintegro-liquidacion" />
	     </td>
	     <td>
			<select name="<portlet:namespace/>marcaReintegroLiq" id="<portlet:namespace/>marcaReintegroLiq">
					<option value="0">Seleccione un Codigo</option>
					<option value="3" <%if(3== nomenclador.getMarcaReintegroLiquidacion() ){%> selected="selected" <% } %>>
							Prestacionales y Farmacias</option>
					<option value="4" <%if(4== nomenclador.getMarcaReintegroLiquidacion() ){%> selected="selected" <% } %>>
							Protesis</option>
					<option value="5" <%if(5== nomenclador.getMarcaReintegroLiquidacion() ){%> selected="selected" <% } %>>
							Ortopedia/Ortodoncia</option>
					<option value="6" <%if(6== nomenclador.getMarcaReintegroLiquidacion() ){%> selected="selected" <% } %>>
							Discapacidad</option>						
			</select>
		 </td>
       
         <td width="115px"><label><liferay-ui:message key="coeficiente-honorarios" />  </label>  </td>
         <td>
		   <input type="text" id="<portlet:namespace />coeficiente_honorarios" name="<portlet:namespace />coeficiente_honorarios" 
		   onkeydown="allowOnlyDigitsAndDecimals(event);limitDecimals(6,document.getElementById('<portlet:namespace />coeficiente_honorarios'),event);" 
		   onchange="agregarCeros(this);" 
		     value='<%=nomenclador.getCoeficienteHonorarios() ==null?"0":nomenclador.getCoeficienteHonorarios()%>' size="10"/>
	     </td>
        
         <td width="115px"><label><liferay-ui:message key="coeficiente-gastos" />  </label>  </td>
         <td>
		   <input type="text" id="<portlet:namespace />coeficiente_gastos" name="<portlet:namespace />coeficiente_gastos" 
		    onkeydown="allowOnlyDigitsAndDecimals(event);limitDecimals(6,document.getElementById('<portlet:namespace />coeficiente_gastos'),event);" 
		    onchange="agregarCeros(this);" 
		     value='<%=nomenclador.getCoeficienteGastos() ==null?"0":nomenclador.getCoeficienteGastos() %>' size="10"/>
	     </td>
         <td>&nbsp;</td>
        </tr>
        
        <tr><td>&nbsp;</td></tr>
        
        <tr>
           <td>Ejercicio:</td>
           <td>
              <select name="ejercicio" id="ejercicio" onchange="actualizarConceptos()">
					<%	Calendar cal = Calendar.getInstance();
						int hastaAnio = cal.get(Calendar.YEAR);
						
						if (cal.get(Calendar.MONTH) < Calendar.AUGUST){
							hastaAnio--;
						} 
						//hastaAnio--;
						
						Calendar calp = Calendar.getInstance();
						int anioCargado = calp.get(Calendar.YEAR);
						if(prestacionConcepto.getValidoHastaHonorariosAmbulatorio()!=null){
						   calp.setTime(prestacionConcepto.getValidoHastaHonorariosAmbulatorio());
						   anioCargado=calp.get(Calendar.YEAR);
						}
						for (int i = hastaAnio; i<=hastaAnio; i++){  %>
					<option value="<%=i%>-<%=i+1%>" <%if(i+1 == anioCargado ) { %>
						selected="selected" <%} %>>
						Agosto&nbsp;<%=i %>&nbsp;-&nbsp;Julio&nbsp;<%= i+1 %></option>
					<%} %>
			  </select>
			</td>
        </tr>
        
        <tr>
		   <td>Honorarios Ambulatorio Anterior:</td>
		   <td><%=prestacionConceptoOriginal.getHonorariosAmbulatorio().getDescripcion() %></td>
	    </tr>
	    <tr>
		   <td>Nuevo:&nbsp;</td>
		   <td>
			<select name="honorarios_ambulatorio" id="honorarios_ambulatorio">
				<option value="-1"/></option>
				<c:forEach items="${conceptos}" var="con">
					<option value="${con.id}"/><c:out value="${con.descripcion}"/></option>
				</c:forEach>
			</select>
		   </td>
	    </tr>
	    <tr>
		   <td>Honorarios Internación Anterior:</td>
		   <td><%=prestacionConceptoOriginal.getHonorariosInternacion().getDescripcion()%></td>
	    </tr>
	    <tr>
		   <td>Nuevo:</td>
		   <td>
			<select name="honorarios_internacion" id="honorarios_internacion">
				<option value="-1"/></option>
				<c:forEach items="${conceptos}" var="con2">
					<option value="${con2.id}"/><c:out value="${con2.descripcion}"/></option>
				</c:forEach>
			</select>
		   </td>
	    </tr>
	    <tr>
		 <td>Gastos Ambulatorio Anterior:</td>
		 <td><%=prestacionConceptoOriginal.getGastosAmbulatorio().getDescripcion()%></td>
	    </tr>
	    <tr>
		 <td>Nuevo:</td>
		 <td>
			<select name="gastos_ambulatorio" id="gastos_ambulatorio">
				<option value="-1"/></option>
				<c:forEach items="${conceptos}" var="con3">
					<option value="${con3.id}"/><c:out value="${con3.descripcion}"/></option>
				</c:forEach>
			</select>
		 </td>
	    </tr>
	    <tr>
		  <td>Gastos Internación Anterior:</td>
		  <td><%=prestacionConceptoOriginal.getGastosInternacion().getDescripcion()%></td>
	    </tr>
	    <tr>
		  <td>Nuevo:</td>
		  <td>
			<select name="gastos_internacion" id="gastos_internacion">
				<option value="-1"/></option>
				<c:forEach items="${conceptos}" var="con4">
					<option value="${con4.id}"/><c:out value="${con4.descripcion}"/></option>
				</c:forEach>
			</select>
		 </td>
	    </tr>
        
        
      </table>   
</fieldset>

<table>
      <tr>
         <td>&nbsp;</td>
      </tr>   
      <tr>
       <td>  
          <input id="<portlet:namespace />anterior" value="<liferay-ui:message key="previous"/>"
           title="<liferay-ui:message key="previous" />"  onClick="javascript:submitFormNotSave();"  type="button" />
           
           
           <% if("edit".equalsIgnoreCase(accion)) { %>
              <input id="<portlet:namespace />guardar"
				value="<liferay-ui:message key="guardar"/>"
				title="<liferay-ui:message key="guardar" />"
				onClick="javascript: <portlet:namespace />salvarEdicion();"
				type="button" /> 								
		  
            <%} %>
           
       </td>
      </tr>
</table>           
 
<input type="hidden" name="<portlet:namespace />id_nomenclador" id="<portlet:namespace />id_nomenclador" value="<%=id_nomenclador%>" />

<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa" />
<input type="hidden" value="" name="tabs1" id="tabs1" />
<input type="hidden" value="" name="view" id="view" />

<script type="text/javascript">

actualizarConceptos();
function submitFormNotSave(){
	document.getElementById("cambioSolapa").value="cambioSolapa";
	document.getElementById("tabs1").value="datos";
	document.getElementById("view").value="true";
	<%-- var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/autorizaciones/editar_nomenclador';
		
	var params = "&<%= Constants.CMD %>=" + "CAMBIO_SOLAPA";
	url = url + params; --%>
	var cmd_ = 'CAMBIO_SOLAPA';
	var xportletUrl = '/autorizaciones/editar_nomenclador';
	
	var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
	'<liferay-portlet:param name="cmd" value="__cmd"/>'+
    '</liferay-portlet:actionURL>';

    url = url.replace("__xportletUrl",xportletUrl); 
    url = url.replace("__cmd",cmd_); 
    
	submitForm(document.<portlet:namespace />nomen, url);
		
}


function <portlet:namespace />validarCampos(){
	var result = true;
	var marcaReintegro=jQuery('#<portlet:namespace/>marcaReintegroLiq').val();
	var coefHonor=jQuery('#<portlet:namespace />coeficiente_honorarios').val();
	var coefGastos=jQuery('#<portlet:namespace />coeficiente_gastos').val();
	
	
	if (<%=nomenclador.getId_tipo_nomenclador() %>==null || <%=nomenclador.getId_tipo_nomenclador() %>==0){
		result=false;
		alert("Debe Seleccionar un Tipo de Nomenclador.");
	}else{
		if ( <%="".equals(nomenclador.getCodigo())%> ){
			result=false;
			alert("Debe ingresar un Código de Nomenclador.");
		}else{	
			if (<%="".equals(nomenclador.getDescripcion())%>){
				result=false;
				alert("Debe ingresar una Descripción del Nomenclador.");
			}else{
				if (<%="".equals(nomenclador.getResolucion())%> ){
					result=false;
					alert("Debe ingresar la Resolución del Nomenclador.");
				}else{
					if(isNaN(marcaReintegro) || marcaReintegro == "" || marcaReintegro == 0){
						result=false;
						alert("Debe ingresar Marca Reintegro/Liquidación.(Datos Contables)"); 	
					}else{
						if (jQuery("#honorarios_ambulatorio").val()	== -1 ||
								jQuery("#honorarios_internacion").val()	== -1 ||
								jQuery("#gastos_ambulatorio").val()	== -1 ||
								jQuery("#gastos_internacion").val()	== -1 ) {
							    result=false;
								alert("Debe seleccionar los conceptos");
						}
					}
				}
			}    
		}   
	}
	
	if((coefHonor == "" && coefGastos == "") || 
	   (coefHonor == 0.0 && coefGastos == 0.0 ||
	   (coefHonor == "" && coefGastos == 0.0 ) ||
	   (coefHonor == 0.0 && coefGastos == "" ))	){
		alert("Complete el coeficiente de Honorarios o Gastos");
		result=false;
	}else if((parseFloat(coefHonor,6) + parseFloat(coefGastos,6)) != parseFloat(1.0) ){
		alert("Coeficiente de Honorarios sumado a Gastos debe dar 1");
		result=false;
	}
	
	return result;
}

function <portlet:namespace />salvarEdicion(){
	if (<portlet:namespace />validarCampos()) {
<%--         var params = "&<%= Constants.CMD %>=" + "<%= Constants.UPDATE %>";
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/autorizaciones/editar_nomenclador';
		url = url + params; --%>
		var cmd_ = '<%= Constants.UPDATE %>';
		var xportletUrl = '/autorizaciones/editar_nomenclador';
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="cmd" value="__cmd"/>'+
	    '</liferay-portlet:renderURL>';

	    url = url.replace("__xportletUrl",xportletUrl); 
	    url = url.replace("__cmd",cmd_); 
	    
		submitForm(document.<portlet:namespace />nomen, url);
	} 
	return false;		
}


function actualizarConceptos(){
	
	var ejercicio=jQuery("#ejercicio").val();	
<%-- 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/traer_conceptos_para_ejercicio'
	    + '&ejercicio=' +ejercicio;
	url += '&rnd=' + Math.floor(Math.random()*100); --%>
	var rnd = '<%= Math.floor(Math.random()*100) %>';
	var xportletUrl = '/autorizaciones/traer_conceptos_para_ejercicio';
	
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
	'<liferay-portlet:param name="ejercicio" value="__ejercicio"/>'+
	'<liferay-portlet:param name="rnd" value="__rnd"/>'+
    '</liferay-portlet:renderURL>';

    url = url.replace("__xportletUrl",xportletUrl); 
    url = url.replace("__ejercicio",encodeURI(ejercicio));
    url = url.replace("__rnd", rnd); 
	
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			jQuery('#honorarios_ambulatorio').find('option').remove();
			jQuery('#honorarios_internacion').find('option').remove();
			jQuery('#gastos_ambulatorio').find('option').remove();
			jQuery('#gastos_internacion').find('option').remove();
			
			jQuery('#honorarios_ambulatorio').append('<option value="-1"></option>');
			jQuery('#honorarios_internacion').append('<option value="-1"></option>');
			jQuery('#gastos_ambulatorio').append('<option value="-1"></option>');
			jQuery('#gastos_internacion').append('<option value="-1"></option>');
			
			for(var i =0;i< obj.conceptos.length; i++){
				jQuery('#honorarios_ambulatorio').append('<option value="'+obj.conceptos[i].id+'"'+ (obj.conceptos[i].id==<%=prestacionConcepto.getHonorariosAmbulatorio().getId()%>? "selected='selected'":'') +'">'+obj.conceptos[i].descripcion+'</option>');
				jQuery('#honorarios_internacion').append('<option value="'+obj.conceptos[i].id+'"'+ (obj.conceptos[i].id==<%=prestacionConcepto.getHonorariosInternacion().getId()%>? "selected='selected'":'') +'">'+obj.conceptos[i].descripcion+'</option>');
				jQuery('#gastos_ambulatorio').append('<option value="'+obj.conceptos[i].id+'"'+ (obj.conceptos[i].id==<%=prestacionConcepto.getGastosAmbulatorio().getId()%>? "selected='selected'":'') +'">'+obj.conceptos[i].descripcion+'</option>');
				jQuery('#gastos_internacion').append('<option value="'+obj.conceptos[i].id+'"'+ (obj.conceptos[i].id==<%=prestacionConcepto.getGastosInternacion().getId()%>? "selected='selected'":'') +'">'+obj.conceptos[i].descripcion+'</option>');
			}
			
		}
	});
	
}

jQuery(document).ready(function() {
});


</script>