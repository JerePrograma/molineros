<%@page import="ar.com.ospim.seccional.beans.WebKeysSeccionales"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/afiliados/init.jsp"%>
<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	Seccional seccional=(Seccional)request.getSession().getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "afiliados";
	}
	
	int id_seccional=seccional!=null ?seccional.getId():0;
	if(seccional==null){
		seccional= new Seccional();
	} 
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	Calendar fechaVigencia = CalendarFactoryUtil.getCalendar();
	if(seccional.getVigen_fecha()==null){
	  fechaVigencia.setTime(new Date());
	}else{
	  fechaVigencia.setTime(seccional.getVigen_fecha());
	} 
	
	boolean rolUOMATarjeta = PermissionUtil.userContainsRole(user,WebKeysSeccionales.ROL_UOMA_TARJETA_RECARGABLE );
	boolean rolABMSeccionales = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_SECCIONALES);
%>
<!-- 
<form action="" method="post" name="<portlet:namespace />fmS">
 -->
	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
		value="" />

  

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
    <liferay-ui:error key="avisoSeccionalDuplicado"
		message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />		
		

	<fieldset class="block-labels"> 
		<legend>Seccional</legend>
		
		<table class="lfr-table">
		   <tr>
			   <td>
			       <label><liferay-ui:message key="numero-texto" />:</label>
			   </td>
			   <td>  
			      <input id="<portlet:namespace />idSeccional"
					name="<portlet:namespace />idSeccional" size="20"
					maxlength="20" type="text" value='<%=seccional.getId()%>'
					<% if (seccional != null && "ED".equalsIgnoreCase(seccional.getModo())) { %> readonly='readonly'<%}%> />
			    </td>
			    
			    <td>
			       <label><liferay-ui:message key="descripcion" />:</label>
			   </td>
			   <td>  
			      <input id="<portlet:namespace />descripcionSeccional"
					name="<portlet:namespace />descripcionSeccional" size="80"
					maxlength="80" type="text" value='<%=seccional.getDescripcion()!=null?seccional.getDescripcion():"" %>'/>
			    </td>
			    
			    <td><liferay-ui:message key="tipo" /></td>
				<td><select name="<portlet:namespace />tipoSeccional" 
					 id="<portlet:namespace />tipoSeccional" onchange="javascript:<portlet:namespace />proximoNroSeccional();" 
					 <% if (seccional != null && "ED".equalsIgnoreCase(seccional.getModo())) { %> disabled='disabled'<%}%>>
						<%for(int i = 0; i < WebKeysAfiliados.TIPOS_SECCIONALES.length; i++ ) {%>
						<option
							value="<%=WebKeysAfiliados.TIPOS_SECCIONALES[i][0] %>"
							<%if (seccional != null && seccional.getTipo()  !=null && 
					              (WebKeysAfiliados.TIPOS_SECCIONALES[i][0]).equals(seccional.getTipo())) { %>
							selected="selected" <%} %>>
							<%=WebKeysAfiliados.TIPOS_SECCIONALES[i][1] %>
						</option>
						<% } %>
				    </select>
				</td>
			    
			    <td><input type="checkbox"  name="<portlet:namespace />imaginariaSeccional" 
							               id="<portlet:namespace />imaginariaSeccional" <%if(seccional.getImaginaria()!=null && seccional.getImaginaria()==1 ){%> checked="checked" <% } %>
							               ><label id="<portlet:namespace />imaginariaSeccionalLb">Imaginaria</label></td>
			    
			</tr>
			
			<tr>
				<td>&nbsp;</td>
			</tr>
			
			<tr>
			  <td colspan="12">
			  <fieldset class="block-labels"> 
		        <legend>Domicilio</legend>
			   <table class="lfr-table">
			    <tr>
			      <td><label><liferay-ui:message key="provincia" />:</label></td>
			      <td colspan="1">
			        <select id="<portlet:namespace/>provincia"
					        name="<portlet:namespace/>provincia"  
					        onchange="javascript:<portlet:namespace />proximoNroSeccional();<portlet:namespace />filtrarLocalidad();" 
					        style="width: 150px;"
				      <%-- <% if (seccional != null && "ED".equalsIgnoreCase(seccional.getModo())) { %> disabled='disabled'<%}%>> --%>
				      >
					  <%	for (Provincia provincia : provincias) { %>
					  <option
						  <%=seccional != null && seccional.getDomicilio()!=null &&  seccional.getDomicilio().getProvinciaId() == provincia.getId() ? "selected" : ""%>
						  value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
					  <%	} %>
			         </select>
			      </td>
			      <td><label><liferay-ui:message key="localidad" />:</label></td>
			      <td colspan="1">
			       <div class="selector-localidad">
			         <%if(seccional != null) {%>
			           <select id="<portlet:namespace/>localidad"
				        name="<portlet:namespace/>localidad"  onchange="javascript:<portlet:namespace />filtrarCodPostal();"
				        style="width: 250px;">
					    <option selected value="0">Seleccione una localidad</option>
					    <%	for (Localidad localidad : localidades) {	%>
					    <option
						  <%=seccional != null && seccional.getDomicilio()!=null && seccional.getDomicilio().getLocalidadId() == localidad.getId() ? "selected" : ""%>
						     value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
					      <%	}	%>
			           </select>
			         <%} else{%>
			  	       <select id="<portlet:namespace/>localidad"
				         name="<portlet:namespace/>localidad"  onchange="javascript:<portlet:namespace />filtrarCodPostal();"
				         style="width: 250px;">
					     <option selected value="0">Seleccione una localidad</option>
				       </select>	
			         <%} %>		
			       </div>
			      </td> 
			
			
			      <td><label><liferay-ui:message key="calle" />:</label></td>
			      <td colspan="1" style="vertical-align: top"><jsp:include
					   page='/html/portlet/afiliados/busqueda_calle_seccional.jsp' /></td>
			      <td colspan="1"><label><liferay-ui:message key="numero" />:</label></td>
			      <td colspan="1"><input id="<portlet:namespace />numero"
				      name="<portlet:namespace />numero" size="5" maxlength="5"
				      type="text"
				      value="<%= seccional != null && seccional.getDomicilio()!=null  && seccional.getDomicilio().getNumero()!=null 
				            ? seccional.getDomicilio().getNumero(): "" %>"
				   	  onblur="javascript:<portlet:namespace />buscarCodPostalOnDiv(event);" />
			      </td>
		        </tr>
		        <tr>
			      <td colspan="8">&nbsp;</td>
		        </tr>
		        <tr>
			       <div id='divCodPostal' style="float: right;"></div>
			         <td colspan="1"><label><liferay-ui:message key="piso" />:</label></td>
			         <td colspan="1"><input id="<portlet:namespace />piso"
				         name="<portlet:namespace />piso" size="5" maxlength="2" type="text"
				         value="<%= seccional != null && seccional.getDomicilio()!=null && seccional.getDomicilio().getPiso() !=null ? seccional.getDomicilio().getPiso() : "" %>"
				          />
				     </td>
			        <td colspan="1"><label><liferay-ui:message key="departamento" />:</label></td>
			        <td colspan="1"><input id="<portlet:namespace />dpto"
				        name="<portlet:namespace />dpto" size="5" maxlength="4" type="text"
				        value="<%= seccional != null && seccional.getDomicilio()!=null && seccional.getDomicilio().getDepto()!=null ? seccional.getDomicilio().getDepto() : "" %>"
				        /></td>
				
			        <td><label><liferay-ui:message key="cod-postal" />:</label></td>
			        <td colspan="1"><input id="<portlet:namespace />cod_postal"
				        name="<portlet:namespace />cod_postal" size="5" maxlength="4"
				        type="text" value="<%= seccional != null && seccional.getDomicilio()!=null && seccional.getDomicilio().getPostal_codi()!=null ? seccional.getDomicilio().getPostal_codi() : "" %>"></td>
			        <td colspan="1"><label><liferay-ui:message key="barrio" />:</label></td>
			        <td colspan="1"><input id="<portlet:namespace />barrio"
				        name="<portlet:namespace />barrio" size="12" maxlength="50"
				        type="text" value="<%= seccional != null && seccional.getDomicilio()!=null && seccional.getDomicilio().getBarrio()!=null ? seccional.getDomicilio().getBarrio() : "" %>"/></td>
				</tr>    
				<tr>
				  <td>&nbsp;</td>
			    </tr>    
			</table>
			</fieldset>
			</td>	
		 </tr>

         <tr>
			<td>&nbsp;</td>
		 </tr>
			
		<tr>
		 <table class="lfr-table">
		   <tr>  
		    <td>
		        <label>Cheque a la Orden:</label>
		    </td>
		    <td>  
			      <input id="<portlet:namespace />chequeOrdenSeccional"
					name="<portlet:namespace />chequeOrdenSeccional" size="80"
					maxlength="80" type="text" value='<%= seccional != null && seccional.getCheque_a_la_orden()!=null ? seccional.getCheque_a_la_orden() : "" %>'/>
		    </td>
		    <td>
		        <label>Email (envia transferencia):</label>
		    </td>
		    <td>  
			      <input id="<portlet:namespace />contactoSeccional"
					name="<portlet:namespace />contactoSeccional" size="80"
					maxlength="250" type="text" value='<%=seccional != null && seccional.getContacto() !=null?   seccional.getContacto():"" %>'/>
		    </td>
		   </tr> 
		   
		   <tr>  
		    <td>
		        <label>Destino Correo:</label>
		    </td>
		    <td>  
			      <input id="<portlet:namespace />destinoCorreoSeccional"
					name="<portlet:namespace />destinoCorreoSeccional" size="80"
					maxlength="80" type="text" value='<%=seccional != null && seccional.getDestinoCorrespondencia() !=null?seccional.getDestinoCorrespondencia():"" %>'/>
		    </td>
		    <td>
		        <label>CBU:</label>
		    </td>
		    <td>  
			      <input id="<portlet:namespace />cbuSeccional"
					name="<portlet:namespace />cbuSeccional" size="80"
					maxlength="80" type="text" value='<%=seccional != null && seccional.getCBU() !=null?seccional.getCBU():""%>'/>
		    </td>
		    </tr>
		    <tr>
			   <td>&nbsp;</td>
		    </tr> 
		    <tr>
		     <td>
		        <label>Tarjeta Recargable:</label>
		     </td>
		     <td>  
			      <input id="<portlet:namespace />tarjetaSeccional"
					name="<portlet:namespace />tarjetaSeccional" size="80"
					maxlength="80" type="text" value='<%=seccional != null && seccional.getNroTarjetaRecargable()  !=null?seccional.getNroTarjetaRecargable():""%>' 
					<% if(!rolUOMATarjeta){ %>readonly="readonly"<%} %>/>
		     </td>
		    </tr>
		    <tr>
			   <td>&nbsp;</td>
		    </tr>
		   
		   <tr>  
		    <td>
		        <label>Observaciones:</label>
		    </td>
		    <td colspan="3">  
			      <input id="<portlet:namespace />observacionSeccional"
					name="<portlet:namespace />observacionSeccional" size="170"
					maxlength="250" type="text" value='<%=seccional != null && seccional.getObservaciones() !=null?seccional.getObservaciones():"" %>'/>
		    </td>
		   </tr> 
		   
		 </table>
		</tr>
			
		<tr>
			<td>&nbsp;</td>
		</tr>	
			
		<tr>
		 <table class="lfr-table">
		   <tr>  

			   <td><label>Vigencia:</label></td>
			   <td colspan="1">
				    <liferay-ui:input-date
				       dayParam="fechaVigenciaSeccionalDia"																					
				       dayValue="<%=seccional.getVigen_fecha()!=null?fechaVigencia.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH ) %>"
				       dayNullable="<%= true %>"
				       monthParam="fechaVigenciaSeccionalMes"
				       monthValue="<%=seccional.getVigen_fecha()!=null?fechaVigencia.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
				       monthNullable="<%= true %>"
				       yearParam="fechaVigenciaSeccionalAnio"
				       yearValue="<%=seccional.getVigen_fecha()!=null?fechaVigencia.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR )%>"
					   yearNullable="<%= true %>"
					   yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
					   yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
					   firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
					   disabled="<%= false %>" />
			   </td>
			    <td><input type="checkbox"  name="<portlet:namespace />ospimSeccional" 
							               id="<portlet:namespace />ospimSeccional" <%if(seccional.isOspim() ){%> checked="checked" <% } %>
							               >OSPIM
				</td>
				<td><input type="checkbox"  name="<portlet:namespace />uomaSeccional" 
							               id="<portlet:namespace />uomaSeccional" <%if(seccional.isUoma() ){%> checked="checked" <% } %>
							               >UOMA
				</td>
				<td><input type="checkbox"  name="<portlet:namespace />amtimaSeccional" 
							               id="<portlet:namespace />amtimaSeccional" <%if(seccional.isAmtima() ){%> checked="checked" <% } %>
							               >AMTIMA
				</td>
			<td>Horario de atención
			
			<input id="<portlet:namespace />horarioAtencionSeccional"
					name="<portlet:namespace />horarioAtencionSeccional" size="70"
					maxlength="200" type="text" value='<%=seccional != null && seccional.getHorarioAtencion()  !=null?seccional.getHorarioAtencion() :"" %>'/>
					
			</td>
			</tr>
			
			<tr>
			<td>&nbsp;</td>
		    </tr>
						
			<tr>
			<td>
		        <label>Descripción UOMA:</label>
		    </td>
		    <td colspan="3">  
			      <input id="<portlet:namespace />descripcionUOMASeccional"
					name="<portlet:namespace />descripcionUOMASeccional" size="70"
					maxlength="70" type="text" value='<%=seccional != null && seccional.getDescripcion_uoma() !=null?seccional.getDescripcion_uoma():"" %>'/>
		    </td>
		    <td>
		        <label>Descripción AMTIMA:</label>
		    </td>
		    <td colspan="3">  
			      <input id="<portlet:namespace />descripcionAMTIMASeccional"
					name="<portlet:namespace />descripcionAMTIMASeccional" size="70"
					maxlength="70" type="text" value='<%=seccional != null && seccional.getDescripcion_amtima() !=null?seccional.getDescripcion_amtima():"" %>'/>
		    </td>
			</tr>
		 </table>
		</tr>
			
		<tr>
			<td>&nbsp;</td>
		</tr>
						                
		</table>
		
		
		<table class="lfr-table" width="100%">
		   <tr>
				<td>
				  <div id="<portlet:namespace />divDelegacionesAsociadas">
					<fieldset class="block-labels">
						<legend>
							Asociar Delegación
						</legend>
						<liferay-util:include
							page='/html/portlet/afiliados/editar_seccional_asigna_delegaciones.jsp'>
						</liferay-util:include>
					</fieldset>
				  </div>	
				</td>
			</tr>
			<tr>
	          <td>&nbsp;</td>
	        </tr>
		</table>
		
		<table class="lfr-table" width="100%">
	       <tr>
		    <td>
			  <liferay-util:include page="/html/portlet/afiliados/editar_seccional_agregar_contacto.jsp">
				  <liferay-util:param name="esEdicion" value="true"/>
			  </liferay-util:include>
		    </td>		
	       </tr>
	       <tr>
            <td>&nbsp;</td>
           </tr>
	    </table>
	    
       
        
       <table>   
        <tr>
         <td>  
          <input id="<portlet:namespace />siguiente"
				value="<liferay-ui:message key="next"/>"
				title="<liferay-ui:message key="next" />"
				onClick="javascript:submitFormNotSave();"
				type="button" />
          
		  </td>		
	    </tr>			
	   </table>	
	
	</fieldset>
	<br>
	
	
   <input type="hidden" value="" name="cambioSolapa" id="cambioSolapa" />
   <input type="hidden" value="" name="tabs1" id="tabs1" />
   <input type="hidden" name="<portlet:namespace />id_seccional"
		id="<portlet:namespace />id_seccional" value="<%=id_seccional%>" />
		
	<input type="hidden" value="" name="view" id="view" /> 
	<c:if test="<%= rolABMSeccionales %>">
    <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" 
		 />
	</c:if>	 
<!--     
</form>
-->

<script type="text/javascript">
var popupNM;
var popupPAT;

function <portlet:namespace />validarCampos(){
	var result = true;
	var idSeccional=jQuery("#<portlet:namespace/>idSeccional").val();
	if(idSeccional==null || idSeccional=="" || idSeccional==0){
		alert("Debe ingresar Nro. de Seccional");
		result=false;
	}
	
	
	return result;
}

function <portlet:namespace />salvarEdicion(){
	document.getElementById("<portlet:namespace/>provincia").disabled=false;
	document.getElementById("<portlet:namespace/>tipoSeccional").disabled=false;
	if (<portlet:namespace />validarCampos()) {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.UPDATE %>";
		params+= "&accion=" + "<%= Constants.UPDATE %>";
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/editar_seccional';
		url = url + params;
		submitForm(document.<portlet:namespace />fmSecc, url);	
	}
	return false;		
}


function <portlet:namespace />filtrarLocalidad() {
	var idProvincia = jQuery('#<portlet:namespace/>provincia').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_provincia_localidad&idProvincia='+idProvincia;
	jQuery("#<portlet:namespace/>localidad").attr('disabled', 'disabled');
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			document.getElementById("<portlet:namespace/>localidad").length = 0;
			jQuery("#<portlet:namespace/>localidad").removeAttr('disabled');
			var obj = jQuery.parseJSON(data);
			jQuery('.selector-localidad select').html(data).fadeIn();

		}
	});
}


function <portlet:namespace />filtrarCodPostal() {
	var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_localidad_codpostal&idLocalidad='+idLocalidad;
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace />cod_postal").length = 0;						
			var obj = jQuery.parseJSON(data);						
			jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
		}
	});	
}

function submitFormNotSave(){
	document.getElementById("<portlet:namespace/>provincia").disabled=false;
	document.getElementById("<portlet:namespace/>tipoSeccional").disabled=false;
	document.getElementById("cambioSolapa").value="cambioSolapa";
	document.getElementById("tabs1").value="datos-contactos";
	document.getElementById("view").value="true";
	
	var url = '<portlet:actionURL windowState="<%=LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/editar_seccional';
	var params = "&<%= Constants.CMD %>=" + "CAMBIO_SOLAPA";
	params+= "&accion=" + "CAMBIO_SOLAPA";
	url = url + params;
	submitForm(document.<portlet:namespace />fmSecc, url);
}

         
function <portlet:namespace />proximoNroSeccional() {

	<%if (seccional==null || (seccional!=null && seccional.getIdSeccional() == 0)) {%>   


	var idProvincia = jQuery('#<portlet:namespace/>provincia').val();
	var idTipo = jQuery('#<portlet:namespace />tipoSeccional').val();
	/* var idSeccional =jQuery("#<portlet:namespace />idSeccional").val(); */
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/proximo_numero_seccional';
	       url +='&idProvincia='+idProvincia+'&idTipo='+idTipo;
	       
	   jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			var idSeccional=obj.seccional;
			jQuery("#<portlet:namespace/>idSeccional").val(idSeccional);
			jQuery("#<portlet:namespace/>id_seccional").val(idSeccional);
		}
	   });



	   <%}%>
	   
}


</script>

