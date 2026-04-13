<%@ include file="/html/portlet/estudio_isidro/init.jsp"%>
<%@page import="ar.com.ospim.estudioisidro.service.DemandaJudicialServiceUtil"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	DemandaJudicial demanda=(DemandaJudicial)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	NumberFormat format2D = new DecimalFormat("#0.00");
	boolean esEdicion = true;
	if(viewStr==null){
		viewStr=ParamUtil.getString(request, "view");
	}
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "estudio_isidro";
	}
	
	int id_demanda=demanda!=null && demanda.getId()!= null ?(int)demanda.getId():0;
	if(demanda==null){
		demanda= new DemandaJudicial();
	} 
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	if(demanda.getFecha()==null){
		fecha.setTime(new Date());
	}else{
	  fecha.setTime(demanda.getFecha());
	} 
	
	String[] tiposStr = TraeListasServiceUtil.getSystemConfig("GESTION_JUDICIAL_TIPOS_DEMANDA").split(";");
	List<ClaseBase> tipos =new ArrayList<ClaseBase>();
	for(int i=0;i<=tiposStr.length-1;i++){
		ClaseBase c =new ClaseBase();
		String codigo = tiposStr[i].split("=")[0];
		String descripcion = tiposStr[i].split("=")[1];
		c.setId(codigo);
		c.setDescripcion(descripcion);
		tipos.add(c);
	}
	
	List<Banco>bancos =TraeListasServiceUtil.getBancos();
	
	String organizacionId = user.getOrganizations().size()>0?String.valueOf(user.getOrganizations().get(0).getOrganizationId()):"";
	String tabValue = ParamUtil.getString(request, "tab", null); // "datos"
		
%>

<form action="" method="post" name="<portlet:namespace />fmS">

	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
		value="" />
    <input type="hidden" name="<portlet:namespace />tab_seleccionada"  value="<%=tabValue%>" />
  
	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	<liferay-ui:error key="errorAfiliadoNull"
		message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
	
		
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
		

	<fieldset class="block-labels"> 
		<legend>Demanda</legend>
		
		<table class="lfr-table">
		   <tr>
		     <td><label><liferay-ui:message key="entidad" />:</label></td>
			  <td>
				<select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad">	
				   <option value="U" <%if (demanda != null && demanda.getEntidad() !=null && 
					              "U".equals(demanda.getEntidad())   ) { %>
							selected="selected" <%}%>     >U.O.M.A.</option>	
				   <option value="O" <%if (demanda != null && demanda.getEntidad() !=null && 
					              "O".equals(demanda.getEntidad())   ) { %>
							selected="selected" <%}%>   >O.S.P.I.M.</option>
				   <option value="A" <%if (demanda != null && demanda.getEntidad() !=null && 
					              "A".equals(demanda.getEntidad())   ) { %>
							selected="selected" <%}%>>A.M.T.I.M.A.</option>													
				</select>						
			  </td>	
			   <td>
			       <label><liferay-ui:message key="fecha" />:</label>
			   </td>
			   <td>  
			         <liferay-ui:input-date
						 dayParam="fechaDemandaDia"
						 dayValue="<%=demanda.getFecha()!=null?fecha.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						 dayNullable="<%= false %>" monthParam="fechaDemandaMes"
						 monthValue="<%=demanda.getFecha()!=null?fecha.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						 monthNullable="<%= false %>" yearParam="fechaDemandaAnio"
						 yearValue="<%=demanda.getFecha()!=null?fecha.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						 yearNullable="<%= false %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 10 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/>
			    </td>
			    
				<td>Tipo Demanda:</td>
			    <td>
			     <select name="<portlet:namespace />tipoDemanda"  id="<portlet:namespace />tipoDemanda" >
						<%for(ClaseBase c:tipos) {%>
						<option
							value="<%= c.getId() %>"
							<%if (demanda != null && demanda.getTipo() !=null && 
					              (c.getId()).equals(demanda.getTipo())) { %>
							selected="selected" <%} %>>
							<%=c.getDescripcion() %>
						</option>
						<% } %>
				  </select>
			    </td>
			    
			    <td><liferay-ui:message key="id" />:</td> 
			    <td><input id="<portlet:namespace />nroDemanda"
					name="<portlet:namespace />nroDemanda" size="15"
					maxlength="20" type="text" readonly="readonly" tabindex="-1"
					value="<%=demanda.getId() ==null?"":demanda.getId() %>" />
				</td>
				
			
			</tr>
			
			<tr>
				<td>&nbsp;</td>
			</tr>
						                
		</table>
		
		
		<table class="lfr-table">
		   <tr>
		   
		   <td>Expediente:</td> 
			    <td><input id="<portlet:namespace />nroExpediente"
					name="<portlet:namespace />nroExpediente" size="30"
					maxlength="50" type="text"  tabindex="-1"
					value="<%=demanda.getExpediente() ==null?"":demanda.getExpediente() %>" />
				</td>
				
				
				<td>Carátula:</td> 
			    <td><input id="<portlet:namespace />caratula"
					name="<portlet:namespace />caratula" size="120"
					maxlength="500" type="text"  tabindex="-1"
					value="<%=demanda.getCaratula() ==null?"":demanda.getCaratula() %>" />
				</td>
		   
		   
		       
			  
		 </tr>
		 <tr>
				<td>&nbsp;</td>
		 </tr>
		 
		 <table class="lfr-table">
		 <tr>
		   
		   <td>Juzgado:</td> 
		   <td><input id="<portlet:namespace />juzgado"
					name="<portlet:namespace />juzgado" size="180"
					maxlength="500" type="text"  tabindex="-1"
					value="<%=demanda.getJuzgado() ==null?"":demanda.getJuzgado() %>" />
				</td>
		</tr>	
		 <tr>
		    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		  </tr>	
		</table> 	 
		
		 <table>
		  <tr>
			<td>
				<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
									  		<liferay-util:param name="esEditable" value='true'/>						  		
									  		<liferay-util:param name="cuit" value=''/>
									  		<liferay-util:param name="sucu" value='000'/>
									  		<liferay-util:param name="suf_entidad" value='_dem'/>
									  		<liferay-util:param name="suf" value='_dem'/>
				</liferay-util:include>
			</td>
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>
			<label>Monto Original:</label>
			</td>
			<td>
				<input type="text" id="<portlet:namespace />importe" name="<portlet:namespace />importe" 
						     size="20" value="<%=demanda.getMontoOriginal()!=null?format2D.format(demanda.getMontoOriginal()):""%>" 
						     maxlength="60" onkeydown="allowOnlyDigits(event)"/>
			</td>
				  
		  </tr>
		  <tr>
		    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		  </tr>
		</table>
		
		<table class="lfr-table">
		<legend><liferay-ui:message key="obs-internas" />:</legend>
		   	&nbsp;&nbsp;&nbsp;&nbsp;<textarea rows="4" cols="160" maxlength="20000" 
		              id="<portlet:namespace />observaciones" 
		              name="<portlet:namespace />observaciones"
		              style="resize:vertical;"><%=demanda.getObservaciones()!=null?demanda.getObservaciones():"" %></textarea>
		
		</table>
		
		<fieldset class="block-labels"> 
		  <legend>Actas</legend>
		  <table class="lfr-table">
		   <tr>
		   
		    <td>
			<label>Nro Acta:</label>
			</td>
		    <td>
				<input type="text" id="<portlet:namespace />acta" name="<portlet:namespace />acta"  size="20" value=""  maxlength="60" />
			</td>
			<td>
			  <input id="<portlet:namespace />agregarActa" value="Agregar Acta" onClick="javascript: agregarActa();"
		          type="button"	 />
			</td>	
		  </tr>
		  
		   <tr> 
		   <td colspan="20">
		    <div align="center" id="<portlet:namespace />actasDiv">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_actas_result.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEdicion)%>" />
				</liferay-util:include>
			</div>	
		  </td>
		  </tr> 
		</table>
		
		</fieldset>
		
		
		<fieldset class="block-labels"> 
		  <legend>Convenios</legend>
		  
		  
		  <table class="lfr-table">
		   <tr>
		    <td>
			<label>Nro Convenio:</label>
			</td>
		    <td>
				<input type="text" id="<portlet:namespace />convenio" name="<portlet:namespace />convenio"  size="20" value=""  maxlength="60" />
			</td>
			<td>
			  <input id="<portlet:namespace />agregarConvenio" value="Agregar Convenio" onClick="javascript:agregarConvenio();"
		          type="button"	 />
			</td>	  
		  </tr>
		    <tr> 
		   <td colspan="20">
		    <div align="center" id="<portlet:namespace />conveniosDiv">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_convenios_result.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEdicion)%>" />
				</liferay-util:include>
			</div>	
		  </td>
		  </tr> 
		  </table>
		
		</fieldset>  
		
		
		<fieldset class="block-labels"> 
		  <legend>Cheques</legend>
		  
		  <table class="lfr-table">
		   <tr>
		    <td>
			<label>Nro Cheque:</label>
			</td>
		    <td>
				<input type="text" id="<portlet:namespace />chequeNro" name="<portlet:namespace />chequeNro"  size="20" value=""  maxlength="60" />
			</td>
			<td>
			<label>Cuit Cheque:</label>
			</td>
			
			<td>
				<input type="text" id="<portlet:namespace />chequeCuit" name="<portlet:namespace />chequeCuit"  size="20" value=""  maxlength="60" />
			</td>
			<td>
			    <select id="<portlet:namespace />id_banco" name="<portlet:namespace />id_banco" >
						
							<% for (Banco b : bancos) { %>
							<option value="<%=b.getId_banco() %>"><%=b.getDescripcion_banco() %></option>
							<%} %>
				</select>
			</td>			
			<td>
			  <input id="<portlet:namespace />agregarCheque" value="Agregar Cheque" onClick="javascript:agregarCheque();"
		          type="button"	 />
			</td>	  
		  </tr>
		    <tr> 
		   <td colspan="20">
		    <div align="center" id="<portlet:namespace />chequesDiv">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_cheques_result.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEdicion)%>" />
				</liferay-util:include>
			</div>	
		  </td>
		  </tr> 
		</table>
		
		</fieldset>
				 
	</fieldset>
	<br>
	


		    
	<br>
	<input type="hidden" name="<portlet:namespace />id_demanda"
		id="<portlet:namespace />id_demanda" value="<%=id_demanda%>" />
		
	<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" /> 
	

  <table>
	 <tr>
	 <td>
	 <%if (esEdicion){ %> 
      <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" 
		 />
	 <%}%>
	</td>
  	</tr>
  </table> 
   <input type="hidden" value="" name="view" id="view" />
   
</form>

<script type="text/javascript">

<% if(demanda.getId()!=null && demanda.getId()>0){ %>
   jQuery('#<portlet:namespace />cuit_entidad_dem').val('<%=demanda.getCuit()%>');
   jQuery('#<portlet:namespace />sucursal_entidad_dem').val('<%=demanda.getSucursal()%>');
   jQuery('#<portlet:namespace />entidad_dem').val('<%=demanda.getRazonSocial()%>');
   
   jQuery('#<portlet:namespace />cuit_entidad_dem').attr('readonly',true);
   jQuery('#<portlet:namespace />sucursal_entidad_dem').attr('readonly',true);;
   jQuery('#<portlet:namespace />entidad_dem').attr('readonly',true);
   
<%}%>

jQuery('#<portlet:namespace />cuit_entidad_dem').blur(function(){
    var cuit=jQuery('#<portlet:namespace />cuit_entidad_dem').val();
	jQuery("#<portlet:namespace />chequeCuit").val(cuit);
	
});

function agregarActa(){	
	var acta=jQuery('#<portlet:namespace />acta').val();
	var cuit=jQuery('#<portlet:namespace />cuit_entidad_dem').val();
	var entidad=jQuery('#<portlet:namespace />entidad').val();

	if (cuit==""){
		alert("Debe ingresar un C.U.I.T.");
		return false;
	}

	if (acta==""){
		alert("Debe ingresar un número de Acta");
		return false;
	}

	//jQuery('#<portlet:namespace />buscando').show();

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
	url += '&cmd=buscarActa&acta='+acta+"&cuit="+cuit+"&entidad="+entidad;
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />actasDiv').load(url, function() {
		    jQuery('#<portlet:namespace />acta').val("");
			//jQuery('#<portlet:namespace />buscando').hide();
		}
	);
}

function agregarConvenio(){	
	var convenio=jQuery('#<portlet:namespace />convenio').val();
	var cuit=jQuery('#<portlet:namespace />cuit_entidad_dem').val();
	var entidad=jQuery('#<portlet:namespace />entidad').val();

	if (cuit==""){
		alert("Debe ingresar un C.U.I.T.");
		return false;
	}

	if (convenio==""){
		alert("Debe ingresar un número de Convenio");
		return false;
	}
	

	//jQuery('#<portlet:namespace />buscando').show();

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
	url += '&cmd=buscarConvenio&convenio='+convenio+"&cuit="+cuit+"&entidad="+entidad;
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />conveniosDiv').load(url, function() {
		    jQuery('#<portlet:namespace />convenio').val("");
			//jQuery('#<portlet:namespace />buscando').hide();
		}
	);
}


function agregarCheque(){	
	var cheque=jQuery('#<portlet:namespace />chequeNro').val();
	var cuit=jQuery('#<portlet:namespace />chequeCuit').val();
	var entidad=jQuery('#<portlet:namespace />entidad').val();
	var banco=jQuery('#<portlet:namespace />id_banco').val();

	if (cuit==""){
		alert("Debe ingresar un C.U.I.T.");
		return false;
	}

	if (cheque==""){
		alert("Debe ingresar un número de Cheque");
		return false;
	}
	
	if (banco==""){
		alert("Debe seleccionar un Banco");
		return false;
	}

	//jQuery('#<portlet:namespace />buscando').show();

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
	url += '&cmd=buscarCheque&cheque='+cheque+"&cuit="+cuit+"&entidad="+entidad+"&banco="+banco;
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />chequesDiv').load(url, function() {
		    jQuery('#<portlet:namespace />chequeNro').val("");
			//jQuery('#<portlet:namespace />buscando').hide();
		}
	);
}


function <portlet:namespace />salvarEdicion(){
	window.onbeforeunload = null;
	var p ='<%=portlet_name%>';
	if (<portlet:namespace />validarCampos()) {
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/__portlet/demandas_editar" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
		'</liferay-portlet:renderURL>';
        url = url.replace("__portlet",p);
		submitForm(document.<portlet:namespace />fmS, url);
	}
	return false;		
}

function <portlet:namespace />validarCampos(){
	var result = true;
	var cuit=jQuery('#<portlet:namespace />cuit_entidad_dem').val();
	var importe=jQuery('#<portlet:namespace />importe').val();
	if(cuit==''){
	   alert("Debe Ingresar un CUIT");
	   result=false;
	}else if(importe=="" || importe=="0"){
	   alert("Debe Ingresar el Monto de la Demanda");
	   result=false;
	}
	
	return result;
}	
</script>

