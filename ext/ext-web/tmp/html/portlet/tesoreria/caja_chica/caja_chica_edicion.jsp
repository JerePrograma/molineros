<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="ar.com.ospim.global.beans.Seccional" %>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	CajaChica cajaChica=(CajaChica)request.getSession().getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	
	int id_caja_chica=cajaChica!=null && cajaChica.getId() !=null ?(int)cajaChica.getId():0;
	if(cajaChica==null){
		cajaChica= new CajaChica();
	} 
	
	Integer entidad = WebKeysGlobal.OSPIM;
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		entidad = WebKeysGlobal.UOMA;
		portlet_name = "uoma";
	}
	List<Concepto> conceptos = TraeListasServiceUtil.getConceptos(DateUtils.getDesdeEjercicioActual().getTime(), entidad);
	List<Seccional>seccionales = TraeListasServiceUtil.getSeccionales();
	%>

<form action="" method="post" name="<portlet:namespace />fmCJCH">

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	
	<fieldset class="block-labels">
		<legend>Caja Chica</legend>

		<table class="lfr-table">
			<tr>
			
			   <td><label><liferay-ui:message key="caja-chica-nombre" />:</label></td>
				<td><input id="<portlet:namespace />descripcionCajaChica"
					name="<portlet:namespace />descripcionCajaChica" size="70"
					maxlength="70" type="text"
					value='<%=cajaChica.getDescripcion()==null?"":cajaChica.getDescripcion() %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/></td>
			   <td>
					<liferay-ui:message key="concepto" />
				</td>
				<td>
					<select name="<portlet:namespace/>conceptoCajaChica" id="<portlet:namespace/>conceptoCajaChica"  
					  <%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>>
						<option value="0">Seleccione un concepto</option>
						<%	for (Concepto tnom : conceptos) { %>
								<option value="<%= tnom.getId() %>"><%=tnom.getDescripcion() %></option>
						<%	} %>
					</select>
				</td>	
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>	
		<table class="lfr-table">	
			<tr>
			  <td><label><liferay-ui:message key="caja-chica-importe-original" />:</label></td>
				<td><input id="<portlet:namespace />importeOriginalCajaChica"
					name="<portlet:namespace />importeOriginalCajaChica" size="20"
					maxlength="20" type="text"
					value='<%=cajaChica.getImporteOriginal() %>' /></td>
			   <td>
			
			  <td>
					<liferay-ui:message key="seccional" />
				</td>
				<td>
					<select name="<portlet:namespace/>seccionalCajaChica" id="<portlet:namespace/>seccionalCajaChica"
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>>
						<option value="0">Seleccione una seccional</option>
						<%	for (Seccional tnom : seccionales) { %>
								<option value="<%= tnom.getId()  %>"><%=tnom.getDescripcion() %></option>
						<%	} %>
					</select>
				</td>	
			</tr>
			
			<tr>
				<td>&nbsp;</td>
			</tr>
			
		</table>	
		<table class="lfr-table">	
			<tr>
			   <td colspan="1" valign="top"><label><liferay-ui:message key="observaciones"/>:</label></td>
	           <td colspan="7"><textarea rows="4" cols="140" maxlength="250" 
		               id="<portlet:namespace />observacionesCajaChica" 
					   name="<portlet:namespace />observacionesCajaChica"
					   style="resize: none;"></textarea>
		       </td>	
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			
			<tr>
			    <td><label id="<portlet:namespace />emailsInformeLb"><liferay-ui:message key="emailsInforme"/>:</label></td>
			    <td colspan="7">
	              <div id="<portlet:namespace />divEmailInformeCajaChica">
	                <input id="<portlet:namespace />emailsInforme"
					name="<portlet:namespace />emailsInforme" size="120"
					maxlength="500" type="text"
					value='<%=cajaChica.getEmailsController()!=null?cajaChica.getEmailsController():"" %>' />
				  </div>	
		        </td>	
		       
			</tr>
			
			<tr>
				<td>&nbsp;</td>
			</tr>
			
		</table>
		
		<table  class="lfr-table">
		
		   <tr>
			  <td colspan="5">
			   <input type="checkbox"  name="<portlet:namespace />pideSeccionalCajaChica" 
		               id="<portlet:namespace />pideSeccionalCajaChica" <%if(cajaChica.getPideSeccionalGasto() ){%> checked="checked" <% } %>>
		               <label id="<portlet:namespace />pideSeccionalCajaChicaLb">Pide Seccional en Carga de Comprobantes</label>
			  </td>
			  
		  </tr>
		  <tr>
				<td>&nbsp;</td>
		  </tr>	  
		
		  <tr>	  
			  <td>
					<liferay-ui:message key="concepto-unifica-op" />
			  </td>
			  <td>
					<select name="<portlet:namespace/>conceptoCajaChicaUnicoOP" id="<portlet:namespace/>conceptoCajaChicaUnicoOP">
						<option value="0">Seleccione un concepto</option>
						<%	for (Concepto tnom : conceptos) { %>
								<option value="<%= tnom.getId() %>"
								<%=Validator.isNotNull(cajaChica.getConceptoUnicoOP()) 
								    && Validator.isNotNull(cajaChica.getConceptoUnicoOP().getId())
								     && cajaChica.getConceptoUnicoOP().getId()==tnom.getId() ? "selected" : ""  %>><%=tnom.getDescripcion() %>		
								</option>
						<%	} %>
					</select>
			 </td>	
		 </tr>
		 <tr>
				<td>&nbsp;</td>
		 </tr>
		
		</table>
		
		<table class="lfr-table" width="100%">
		   <tr>
				<td>
				  <div id="<portlet:namespace />divUsuariosCajaChica">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="assign-users" />
						</legend>
						<liferay-util:include
							page='/html/portlet/tesoreria/caja_chica/caja_chica_asigna_usuarios.jsp'>
							<liferay-util:param value="<%=String.valueOf(esEdicion)%>"
								name="esEdicion" />
						</liferay-util:include>
					</fieldset>
				  </div>	
				</td>
			</tr>
		</table>
		
	</fieldset>
	<br>
	<input type="hidden" name="<portlet:namespace />id_caja_chica"
		id="<portlet:namespace />id_caja_chica" value="<%=id_caja_chica%>" />
	<input type="hidden" value="" name="view" id="view" /> 

    <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" 
		 />
   
</form>

<script type="text/javascript">

var popupNM;
<portlet:namespace />initDateFields();

function <portlet:namespace />initDateFields(){
  if(<%=cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()>0%> ){
    jQuery("#<portlet:namespace />descripcionCajaChica").val('<%=cajaChica.getDescripcion() %>');
    jQuery("#<portlet:namespace />conceptoCajaChica").val('<%=cajaChica.getConcepto().getId() %>');
    jQuery("#<portlet:namespace />seccionalCajaChica").val('<%=cajaChica.getSeccional().getId() %>');
    jQuery("#<portlet:namespace />observacionesCajaChica").val('<%=cajaChica.getObservaciones() %>');
    jQuery("#<portlet:namespace />importeOriginalCajaChica").val('<%=cajaChica.getImporteOriginal() %>');
  }
  
  if(<%=entidad == WebKeysGlobal.UOMA%>){
	 jQuery("#<portlet:namespace />divEmailInformeCajaChica").show(); 
	 document.getElementById("<portlet:namespace/>valorUnitarioMedicamentoSurLb").style.visibility = "hidden";
  }else{
     jQuery("#<portlet:namespace />divEmailInformeCajaChica").hide();
     document.getElementById("<portlet:namespace/>emailsInformeLb").style.visibility = "hidden";
  }
}

function <portlet:namespace />salvarEdicion(){
	if (<portlet:namespace />validarCampos()) {
		document.getElementById("<portlet:namespace/>conceptoCajaChica").disabled=false;
		document.getElementById("<portlet:namespace/>descripcionCajaChica").disabled=false;
		document.getElementById("<portlet:namespace/>seccionalCajaChica").disabled=false;
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.UPDATE %>";
		//url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_caja_chica';
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
		url = url + params;
		submitForm(document.<portlet:namespace />fmCJCH, url);	
	}
	return false;		
}

function <portlet:namespace />validarCampos(){
	var result = true;
	var portlet = '<%=portlet_name%>';
	if (jQuery("#<portlet:namespace/>descripcionCajaChica").val()==""){
		result=false;
		alert("Debe ingresar la Descripción");
	}else{
		if (jQuery('#<portlet:namespace />conceptoCajaChica').val()==0 ){
			result=false;
			alert("Debe Seleccionar un Concepto");
		} else if ("tesoreria" == portlet  && (jQuery("#<portlet:namespace/>importeOriginalCajaChica").val()=="" || jQuery("#<portlet:namespace/>importeOriginalCajaChica").val()==0)){
			result=false;
			alert("Debe ingresar el Importe Original");
		}	
	}
	return result;
}


</script>

