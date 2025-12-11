<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<%@ page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@ page import="com.liferay.portal.kernel.util.OrderByComparator"%>
<%@ page import="ar.com.enterpriseadmin.search.UserSearch" %>
<%@ page import="ar.com.enterpriseadmin.search.UserSearchTerms" %>
<%@ page import="ar.com.enterpriseadmin.search.UserDisplayTerms" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/autorizaciones/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	//verificar los calendars
	Calendar current = CalendarFactoryUtil.getCalendar();
	String usuario_modi = user.getScreenName();
	
	
	Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
	/* fechaInicio.add(Calendar.MONTH, -1); */
	Calendar fechaMigracion = CalendarFactoryUtil.getCalendar();
	fechaMigracion.set(Calendar.MONTH, Calendar.JANUARY);
	fechaMigracion.set(Calendar.DATE, 1);
	fechaMigracion.set(Calendar.YEAR, 1999);
	
	boolean popup=ParamUtil.getBoolean(request, "popup", false);
	boolean rolExpedienteSUR = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ALTA_EXPEDIENTES_SUR);
	
/*
	List<User> users = UserLocalServiceUtil.search(
			themeDisplay.getCompanyId(), null, Boolean.TRUE, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator)null);
*/

	List<String> users = TraeListasServiceUtil.getUsuariosAltaSeguimientoSur();
    List<ModalidadAtencion> estadosList=TraeListasServiceUtil.getEstadosSeguimientoSur() ;
    List<ModalidadAtencion> estadosPorIdList=TraeListasServiceUtil.getEstadosSeguimientoSurPorEstados("3,10,14") ;
    
    
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
		<legend><liferay-ui:message key="seguimiento-sur" /></legend>
		<table class="lfr-table">	
			<tr>
			    <td><liferay-ui:message key="clase-expediente" /></td>
				<td><select name="<portlet:namespace />claseExpedienteFiltro"
					id="<portlet:namespace />claseExpedienteFiltro" onchange="javascript:<portlet:namespace />actualizaBimestresFiltro();">
					    <option value="">Seleccione Tipo</option>
						<%for(int i = 0; i < WebKeysAutorizaciones.CLASES_EXPEDIENTES.length; i++ ) {%>
						<option	value="<%=WebKeysAutorizaciones.CLASES_EXPEDIENTES[i][0] %>">
							<%=WebKeysAutorizaciones.CLASES_EXPEDIENTES[i][1] %>
						</option>
						<% } %>
				</select></td>
				<td>
					<liferay-ui:message key="Nro" />
				</td>
				<td>
   				    <input id="<portlet:namespace />nroClaseExpedienteFiltro" name="<portlet:namespace />nroClaseExpedienteFiltro" size="4" maxlength="5" type="text" value='' onkeydown="allowOnlyDigitsAndDecimals(event)"/>	
				</td>
			   <td>
					<liferay-ui:message key="anio" />
				</td>
			   <td>
			      <select name="<portlet:namespace/>ejercicio_filtro"  id="<portlet:namespace/>ejercicio_filtro" onchange="javascript:<portlet:namespace />actualizaBimestresFiltro();">
			        <option value="0">Seleccione ejercicio</option>
					<%	Calendar cal = Calendar.getInstance();
						int hastaAnio = cal.get(Calendar.YEAR);
						for (int i = 2010; i<=hastaAnio; i++){  %>
					      <option value="<%=i%>">
						   <%=i %></option>
						<%}%>
			       </select>
			    </td>
			    
			     <td>
					<liferay-ui:message key="Periodicidad" />
				</td>
			   <td>
			      <select name="<portlet:namespace/>bimestre_filtro"  id="<portlet:namespace/>bimestre_filtro">
			      <option value="0">Seleccione período</option>
					<%	String bimestre[]={"","Primer","Segundo","Tercer","Cuarto","Quinto","Sexto"};
						for (int i = 1; i<=6; i++){  %>
					      <option value="<%=i%>">
						   <%=bimestre[i]+ " Bimestre" %></option>
						<%}%>
			       </select>
			    </td>
			    
			    <td>
					<liferay-ui:message key="cobertura-expediente" />
				</td>
				<td><select name="<portlet:namespace />tipo_expediente_filtro"
			        id="<portlet:namespace />tipo_expediente_filtro" 
			        onchange="javascript:<portlet:namespace />habilitaTercerizadora();" >
			        <option value="0">Seleccione Cobertura</option>
			        <%for(int i = 0; i < WebKeysAutorizaciones.TIPOS_EXPEDIENTES.length; i++ ) {%>
				    <option value="<%=WebKeysAutorizaciones.TIPOS_EXPEDIENTES[i][0] %>" 
					 > <%=WebKeysAutorizaciones.TIPOS_EXPEDIENTES[i][1] %> </option>
				    <% } %>
		            </select>
		         </td>
		         <td>
		         <div id="<portlet:namespace />divTipoExpedienteTercerizadora">
		         <table>
		         <tr>
		         <td>&nbsp;&nbsp;<label><liferay-ui:message key="cobertura-expediente-tercerizadora" />:</label></td>
				 <td>
					    <select name="<portlet:namespace />tercerizadora_filtro"
						id="<portlet:namespace />tercerizadora_filtro" onchange="">
							<%for(int i = 0; i < WebKeysAutorizaciones.TIPOS_EXPEDIENTES_TERCERIZADORA.length; i++ ) {%>
							<option value="<%=WebKeysAutorizaciones.TIPOS_EXPEDIENTES_TERCERIZADORA[i][0] %>">
								<%=WebKeysAutorizaciones.TIPOS_EXPEDIENTES_TERCERIZADORA[i][1] %>
							</option>
							<% } %>
					    </select>
					   </td>
				</tr>						
				 </table>
				 </div>
				 </td>
		         
		         <td>
					<liferay-ui:message key="autoriza-omint" />
				</td>
				<td>
					<select name="<portlet:namespace/>autorizaOmint_filtro" id="<portlet:namespace/>autorizaOmint_filtro">
						<option value="0">Seleccione autorización</option>
						<%for(int i = 0; i < WebKeysAutorizaciones.AUTORIZA_OMINT.length; i++ ) {%>
			               <option value="<%=WebKeysAutorizaciones.AUTORIZA_OMINT[i][0] %>" 
					      > <%=WebKeysAutorizaciones.AUTORIZA_OMINT[i][1] %> </option>
				        <%}%>
					</select>
				</td>
		         
			 </tr>
			 <tr><td>&nbsp;</td></tr>	
		</table>	 
		
		<table class="lfr-table">		 
			 <tr>
			 <td><label><liferay-ui:message key="nroSolicitudSur"/>:</label></td>
				<td><input id="<portlet:namespace />nroSolicitudSUR_filtro" name="<portlet:namespace />nroSolicitudSUR_filtro" size="20" maxlength="20" type="text" value=''/></td>
			    <td><label><liferay-ui:message key="codigo-presentado"/>:</label></td>
				<td><input id="<portlet:namespace />codigoSeguimiento_filtro" name="<portlet:namespace />codigoSeguimiento_filtro" size="10" maxlength="20" type="text" value=''/></td>
				<td><input id="<portlet:namespace />descripcionSeguimiento_filtro" name="<portlet:namespace />descripcionSeguimiento_filtro" size="80" maxlength="200" type="text" value=''
				onKeyUp="javascript:<portlet:namespace />buscarSeguimientoSurOnDiv(event)"	
				/></td>
				<td><div id="<portlet:namespace />divBtnBuscaSeguimientoSur">
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar();" tabindex="-1">Buscar</a>
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();" tabindex="-1">Limpiar</a>
				</div> </td>
				<td><label><liferay-ui:message key="nroExpediente"/>:</label></td>
		        <td><input id="<portlet:namespace />nroExpedienteSUR_filtro" 
		                name="<portlet:namespace />nroExpedienteSUR_filtro" size="20" maxlength="20" type="text" value=''/></td>
			 </tr>
		</table>
		
		<table class="lfr-table">
			<tr>
			  <td>
				<table class="lfr-table">
				   <tr>
				      <td>
				         <div id="<portlet:namespace/>divAfiliadosSeguimientoSurFiltro" >
		                    <fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>
					           <liferay-util:include page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>
						       <liferay-util:param value="<%= String.valueOf(true) %>" name="edit_mode" />
						       <liferay-util:param value="<%= null %>" name="discapacidad" />
						       <liferay-util:param value="<%= String.valueOf(true) %>" name="pag_reintegro" />
						       <liferay-util:param name="cuil" value='' />
						       <liferay-util:param name="inte" value='' />
						       <liferay-util:param value="_filtro" name="origen" />
						       </liferay-util:include>
			                </fieldset>
			             </div> 
				      </td> 
				   </tr>
				</table>
			  </td>
			  
			  <td>
			   <fieldset class="block-labels">
			    <table class="lfr-table">
			      <tr>
			          <td><label>Ingr.Area Desde:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaDesdeDiaFiltro"
							dayValue="<%= fechaMigracion.get(Calendar.DATE) %>" 
							monthParam="fechaDesdeMesFiltro"
							monthValue="<%= fechaMigracion.get(Calendar.MONTH) %>"				
							yearParam="fechaDesdeAnioFiltro"
							yearValue="<%= fechaMigracion.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 20 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
				  </tr>
				  <tr><td colspan="9">&nbsp;</td></tr>
				  <tr>		
						<td><label>Ingr.Area Hasta:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaHastaDiaFiltro"
							dayValue="<%= current.get(Calendar.DATE) %>" 
							monthParam="fechaHastaMesFiltro"
							monthValue="<%= current.get(Calendar.MONTH) %>"				
							yearParam="fechaHastaAnioFiltro"
							yearValue="<%= current.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -10 %>"	
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
				   </tr>
				   <tr><td colspan="9">&nbsp;</td></tr>
				   <tr>
				   <td colspan="2">Incluye Bajas <input type="checkbox"  name="<portlet:namespace />bajasfiltro" 
							 id="<portlet:namespace />bajasfiltro"></td>
				   </tr>	
			    </table>
			   </fieldset>
			  </td>	
			</tr>
			<tr><td colspan="9">&nbsp;</td></tr>
			<tr>
			   <table class="lfr-table">
			   <tr>
			   <td>Estado Cierre: <select name="<portlet:namespace />estadoSeguimientoSur_filtro"
			                   id="<portlet:namespace />estadoSeguimientoSur_filtro" 
			                   onchange="">
			                <option value="">Seleccione estado</option>
			                <%for(int i = 0; i < WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES.length; i++ ) {%>
				            <option value="<%=WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES[i][0] %>" 
					          > <%=WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES[i][1] %> </option>
				            <% } %>
		             </select>
		        </td>
		        
		        <td><label for="<portlet:namespace />usuario"><liferay-ui:message key="usuario" />:</label>
		           <select name="<portlet:namespace/>usuario_altaSur_filtro" id="<portlet:namespace/>usuario_altaSur_filtro" >
		              <option value="">Seleccione un Usuario de Alta</option>	       					
							<%for(String u:users) {%>
							   <option value="<%=u%>"> <%=u%></option>
							<%}%>
				   </select>
				</td>
				
				<td><table>
				    <label><liferay-ui:message key="Convenio Tercerizadora" />:</label>
					<select 
						name="<portlet:namespace />conveniotercerizadora"
						id="<portlet:namespace />conveniotercerizadora" >
						<option value="" ></option>
						<option value="OMINT 2017">OMINT 2017</option>	
					</select>					
				</table></td>
				<td>
					<label><liferay-ui:message key="codigohiv" />:<label>
				</td>
				<td>
   				    <input id="<portlet:namespace />codigo_hiv_filtro" name="<portlet:namespace />codigo_hiv_filtro" size="12" maxlength="25" type="text" value=''/>	
				</td>				
				</tr>
				</table>
			 </tr>
			 <tr><td colspan="1">&nbsp;&nbsp;&nbsp;</td></tr>
			 <tr>
			   <td>
			   <table class="lfr-table">
			   <tr>
			     <td>Est. Seguimiento</td>
			     <td>
		            <select name="<portlet:namespace />estadoSSSeguimientoSur_filtro"
			                   id="<portlet:namespace />estadoSSSeguimientoSur_filtro" 
			                   onchange="" multiple="multiple"  size="5">
			            <option value="0">Seleccione Estado</option>
				        <%	for (ModalidadAtencion terce : estadosList) { %>
						   <option value="<%= terce.getId()%>"><%=terce.getDescripcion()%></option>
				        <%	} %>       
		            </select>
 	           </td>	
               <td>
			   <fieldset class="block-labels">
			    <table class="lfr-table">
			      <tr>
			          <td><label>F. Est. Desde:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaEstadoDesdeDiaFiltro"
							dayValue="" 
							dayNullable="<%= true %>"
							monthParam="fechaEstadoDesdeMesFiltro"
							monthValue="-1"	
							monthNullable="<%= true %>"					
							yearParam="fechaEstadoDesdeAnioFiltro"
							yearValue=""
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 20 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
				  </tr>
				  <tr><td colspan="9">&nbsp;</td></tr>
				  <tr>		
						<td><label>F. Est. Hasta:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaEstadoHastaDiaFiltro"
							dayValue="" 
							dayNullable="<%= true %>"
							monthParam="fechaEstadoHastaMesFiltro"
							monthValue="-1"			
							monthNullable="<%= true %>"		
							yearParam="fechaEstadoHastaAnioFiltro"
							yearValue=""
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -10 %>"	
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
				   </tr>
				   <!-- tr><td colspan="9">&nbsp;</td></tr-->
			    </table>
			   </fieldset>
               </td>		         
		 		 <td>Est. Seguimiento<br>Histórico</td>
			     <td>
		            <select name="<portlet:namespace />estadoSS_His_SeguimientoSur_filtro"
			                   id="<portlet:namespace />estadoSS_His_SeguimientoSur_filtro" 
			                   onchange="" multiple="multiple"  size="5">
			            <option value="0">Seleccione Estado</option>
				        <%	for (ModalidadAtencion terce : estadosPorIdList) { %>
						   <option value="<%= terce.getId()%>"><%=terce.getDescripcion()%></option>
				        <%	} %>       
		            </select>
		         <td>   <fieldset class="block-labels">    
		          <table class="lfr-table">
			      <tr>
			          <td><label>F. Est. Desde:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaEstSurDesdeDiaFiltro"
							dayValue="" 
							dayNullable="<%= true %>"
							monthParam="fechaEstSurDesdeMesFiltro"
							monthValue="-1"	
							monthNullable="<%= true %>"					
							yearParam="fechaEstSurDesdeAnioFiltro"
							yearValue=""
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 20 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
				  </tr>
				  <tr><td colspan="9">&nbsp;</td></tr>
				  <tr>		
						<td><label>F. Est. Hasta:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaEstSurHastaDiaFiltro"
							dayValue="" 
							dayNullable="<%= true %>"
							monthParam="fechaEstSurHastaMesFiltro"
							monthValue="-1"			
							monthNullable="<%= true %>"		
							yearParam="fechaEstSurHastaAnioFiltro"
							yearValue=""
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -10 %>"	
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
				   </tr>
				   <!-- tr><td colspan="9">&nbsp;</td></tr->
			    </table>
			   </fieldset>
			    </tr>
		    <!-- tr><td colspan="9">&nbsp;</td></tr-->
		  </table>
		    </fieldset>
		    </td>
		       
		</tr>
			 <tr><td colspan="9">&nbsp;</td></tr>
			 
			
			 
		</table>
		
		
		
	   
		   <table class="lfr-table">
			    <tr>
			    <td>
					<liferay-ui:message key="Nro Correspondencia" />
				</td>
				<td>
   				    <input id="<portlet:namespace />nroCorrespondenciaFiltro" name="<portlet:namespace />nroCorrespondenciaFiltro" size="4" maxlength="5" type="text" value='' onkeydown="allowOnlyDigitsAndDecimals(event)"/>	
				</td>
			    
				<td>		         
		         <label>Desde:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaCorresDesdeDiaFiltro"
							dayValue=""
							dayNullable="<%= true %>"  
							monthParam="fechaCorresDesdeMesFiltro"
							monthNullable="<%= true %>"
							monthValue="-1"											
							yearParam="fechaCorresDesdeAnioFiltro"
							yearNullable="<%= true %>"
							yearValue=""
							yearRangeStart="<%= current.get(Calendar.YEAR) - 20 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
				</td>
					
				<td><label>Hasta:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaCorresHastaDiaFiltro"
							dayNullable="<%= true %>"
							dayValue="" 
							monthParam="fechaCorresHastaMesFiltro"
							monthNullable="<%= true %>"
							monthValue="-1"				
							yearParam="fechaCorresHastaAnioFiltro"
							yearNullable="<%= true %>"
							yearValue=""
							yearRangeStart="<%= current.get(Calendar.YEAR) - 20 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
				</td>
				
				<td>
					DDJJ:
				</td>
			   <td>
			      <select name="<portlet:namespace/>ddjj_filtro"  id="<portlet:namespace/>ddjj_filtro">
			        <option value="0">Seleccione DDJJ</option>
					<%	Calendar cal1 = Calendar.getInstance();
						int hastaAnio1 = cal.get(Calendar.YEAR);
						for (int i = hastaAnio1; i>=hastaAnio1; i--){  %>
					      <option value="<%=i%>">
						   <%=i %></option>
						<%}%>
			       </select>
			    </td>
				
				</tr>
			</table>
		 
		<table>
		         <tr align="left">
				    <td>&nbsp;</td>
				 </tr>
				 <tr align="left">
				    <td>&nbsp;</td>
					<td align="left">						
						<input id="<portlet:namespace />buscar"
						value="<liferay-ui:message key="buscar"/>"
						title="<liferay-ui:message key="buscar" />"
						onClick="javascript: <portlet:namespace />buscarSeguimientoSur();"
						type="button" />
						
						<c:if test="<%= rolExpedienteSUR %>">
						<input type="button" value="Nuevo" onClick="<portlet:namespace />nuevoSeguimientoSur();"/>&nbsp;
						<input type="button" value="Procesar Archivos" onClick="<portlet:namespace />pagosSeguimientoSur();"/>&nbsp;
						</c:if>
						
					</td>
					<td align="left">						
					  <input id="<portlet:namespace />reporte"
						value="<liferay-ui:message key="reporte"/>"
						title="<liferay-ui:message key="reporte" />"
						onClick="javascript: <portlet:namespace />reporteSeguimiento();"
						type="button" />
					</td>
					<!-- <td>&nbsp;</td> -->
				</tr>
		</table>
		
		<div id='divSeguimientoSur' style="float:left;">
		</div>
	</fieldset>
	<fieldset class="block-labels">
		<div align="center" id="<portlet:namespace />buscando">
			<table style="align:center;">
				<tr>
					<td><liferay-ui:message key='buscando'/></td>
					<td align="center">					
					<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
					</td>
				</tr>
			</table>		
		</div>	
		<div id="<portlet:namespace />listado_seguimientoSur">
			<jsp:include page='/html/portlet/autorizaciones/seguimiento_sur/seguimiento_sur_result.jsp' />  	
		</div>
		
	</fieldset>
	<%-- <table class="lfr-table">
		<tr>
		  <td>&nbsp;</td>
		  <td>&nbsp;</td>
		</tr>
		<tr>
			<td>						
			  <input id="<portlet:namespace />reporte"
				value="<liferay-ui:message key="reporte"/>"
				title="<liferay-ui:message key="reporte" />"
				onClick="javascript: <portlet:namespace />reporteSeguimiento();"
				type="button" />
			</td>
		</tr>
	</table> --%>
	
	<input id="<portlet:namespace />nom_seleccionado" name="<portlet:namespace />nom_seleccionado" type="hidden" value=""/>
	
</form>		

<script type="text/javascript">
	
	jQuery('#<portlet:namespace />buscando').hide();
	
	jQuery("#<portlet:namespace />divTipoExpedienteTercerizadora").hide();
	
	var popupMD;
	
	<portlet:namespace />initDateFields();
	
	function <portlet:namespace />initDateFields(){
		<portlet:namespace />actualizaBimestres();
	}
	
	
	function <portlet:namespace />buscarSeguimientoSurOnDiv(e){
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
				
		if(jQuery("#<portlet:namespace />nom_seleccionado").val() == "1" && (keyPressed==8 || keyPressed==46)){
			jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val("");
			jQuery("#<portlet:namespace />nom_seleccionado").val("");
			return false;
		}
		
	    var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val();
	    if (nombre_nomenclador==null){
	    	nombre_nomenclador = '';
	    }    
	    if(jQuery("#<portlet:namespace />nom_seleccionado").val() != "1" && nombre_nomenclador.length>=6 ){
	    	if(popupMD==null)
	    	    popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
	    	
	    	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
		    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador);
			jQuery(popupMD).load(url);
			
	    }
	}
	
	function <portlet:namespace />nuevoSeguimientoSur() {
<%-- 		var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_seguimientosur" /></portlet:renderURL>';
		url = url + params; --%>
		var porletUrl='/autorizaciones/editar_seguimientosur';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__portletUrl" />'+
		'<liferay-portlet:param name="cmd" value="write"/>'+
	    '</liferay-portlet:renderURL>';
	    url = url.replace("__portletUrl",porletUrl);
	    
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function <portlet:namespace />buscarSeguimientoSur(){
		var clase=jQuery('#<portlet:namespace />claseExpedienteFiltro').val();
		var claseNro=jQuery('#<portlet:namespace />nroClaseExpedienteFiltro').val();
		var anio=jQuery('#<portlet:namespace />ejercicio_filtro').val();
		var bimestre=jQuery('#<portlet:namespace />bimestre_filtro').val();
		var tipoExpediente=jQuery('#<portlet:namespace />tipo_expediente_filtro').val();
		var autorizaOmint=jQuery('#<portlet:namespace />autorizaOmint_filtro').val();
		var nroSolicitudSur=jQuery('#<portlet:namespace />nroSolicitudSUR_filtro').val();
		
		var nroCorrespondencia=jQuery('#<portlet:namespace />nroCorrespondenciaFiltro').val();
		
		var convenioTercerizadora=jQuery('#<portlet:namespace />conveniotercerizadora').val();
		
		var codigoPresentado=jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val();
		var descripcionPresentado=jQuery('#<portlet:namespace />descripcionSeguimiento_filtro').val();
		var nroExpedienteSur=jQuery('#<portlet:namespace />nroExpedienteSUR_filtro').val();
		var cuil=jQuery('#<portlet:namespace />cuil_filtro').val();
		var inte=jQuery('#<portlet:namespace />inte_filtro').val();
		var bajas=jQuery("#<portlet:namespace/>bajasfiltro").is(':checked');
		
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaFiltro");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMesFiltro");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioFiltro");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDiaFiltro");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMesFiltro");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioFiltro");

		var fechaCorresDesdeDia  = document.getElementById("<portlet:namespace />fechaCorresDesdeDiaFiltro");
		var fechaCorresDesdeMes= document.getElementById("<portlet:namespace />fechaCorresDesdeMesFiltro");
		var fechaCorresDesdeAnio = document.getElementById("<portlet:namespace />fechaCorresDesdeAnioFiltro");

		var fechaCorresHastaDia = document.getElementById("<portlet:namespace />fechaCorresHastaDiaFiltro");
		var fechaCorresHastaMes = document.getElementById("<portlet:namespace />fechaCorresHastaMesFiltro");
		var fechaCorresHastaAnio = document.getElementById("<portlet:namespace />fechaCorresHastaAnioFiltro");
		var usuarioAlta = jQuery("#<portlet:namespace />usuario_altaSur_filtro").val();
		var estadoExpediente=jQuery('#<portlet:namespace />estadoSeguimientoSur_filtro').val();

		var estadoSSSExpediente=jQuery('#<portlet:namespace />estadoSSSeguimientoSur_filtro').val();
		
		var estadoSSSHisExpediente=jQuery('#<portlet:namespace />estadoSS_His_SeguimientoSur_filtro').val();

		
        var tipoTercerizadoraFiltro = jQuery('#<portlet:namespace />tercerizadora_filtro').val();
         
        var opcion =jQuery("#<portlet:namespace />tipo_expediente_filtro").val();
		if(opcion!=2){
			tipoTercerizadoraFiltro=0
		}
        
		var selectedValues = [];    
    	jQuery("#<portlet:namespace />estadoSSSeguimientoSur_filtro :selected").each(function(){
        selectedValues.push(jQuery(this).val()); 
    	});
		estadoSSSExpediente=selectedValues;
		if (selectedValues==null){
			estadoSSSExpediente="";
		}
		
		var selectedValuesHis = [];    
		jQuery("#<portlet:namespace />estadoSS_His_SeguimientoSur_filtro :selected").each(function(){
			selectedValuesHis.push(jQuery(this).val()); 
	    	});
			estadoSSSHisExpediente=selectedValuesHis;
			if (selectedValuesHis==null){
				estadoSSSHisExpediente="";
			}
		
		var fechaEstadoDesdeDia  = document.getElementById("<portlet:namespace />fechaEstadoDesdeDiaFiltro");
		var fechaEstadoDesdeMes= document.getElementById("<portlet:namespace />fechaEstadoDesdeMesFiltro");
		var fechaEstadoDesdeAnio = document.getElementById("<portlet:namespace />fechaEstadoDesdeAnioFiltro");

		var fechaEstadoHastaDia = document.getElementById("<portlet:namespace />fechaEstadoHastaDiaFiltro");
		var fechaEstadoHastaMes = document.getElementById("<portlet:namespace />fechaEstadoHastaMesFiltro");
		var fechaEstadoHastaAnio = document.getElementById("<portlet:namespace />fechaEstadoHastaAnioFiltro");


		var fechaEstSurDesdeDia  = document.getElementById("<portlet:namespace />fechaEstSurDesdeDiaFiltro");
		var fechaEstSurDesdeMes= document.getElementById("<portlet:namespace />fechaEstSurDesdeMesFiltro");
		var fechaEstSurDesdeAnio = document.getElementById("<portlet:namespace />fechaEstSurDesdeAnioFiltro");

		var fechaEstSurHastaDia = document.getElementById("<portlet:namespace />fechaEstSurHastaDiaFiltro");
		var fechaEstSurHastaMes = document.getElementById("<portlet:namespace />fechaEstSurHastaMesFiltro");
		var fechaEstSurHastaAnio = document.getElementById("<portlet:namespace />fechaEstSurHastaAnioFiltro");

		var ddjj=jQuery('#<portlet:namespace />ddjj_filtro').val();
		
		var codigohiv = jQuery('#<portlet:namespace />codigo_hiv_filtro').val();
		
		jQuery('#<portlet:namespace />buscando').show();
		
		
	 	var busquedaNom = {"anio":anio,"bimestre":bimestre,"tipoexpediente":tipoExpediente,
	 			"autorizaomint":autorizaOmint,"nrosolicitudsur":nroSolicitudSur,"codigopresentado":codigoPresentado,
	 			"descripcionpresentado":descripcionPresentado,"nroexpedientesur":nroExpedienteSur,
	 			"cuil":cuil,"inte":inte,"fechadesdedia":fechaDesdeDia.value,"fechadesdemes":fechaDesdeMes.value,
	 			"fechadesdeanio":fechaDesdeAnio.value,
	 			"fechahastadia":fechaHastaDia.value,"fechahastames":fechaHastaMes.value,"fechahastaanio":fechaHastaAnio.value,
	 			"incluyebajas":bajas,"estado":estadoExpediente,"clase":clase,"usuarioalta":usuarioAlta,"estadosss":estadoSSSExpediente,"clasenro":claseNro,"fechaCorresdesdedia":fechaCorresDesdeDia.value,"fechaCorresdesdemes":fechaCorresDesdeMes.value,
	 			"fechaCorresdesdeanio":fechaCorresDesdeAnio.value,"fechaCorreshastadia":fechaCorresHastaDia.value,"fechaCorreshastames":fechaCorresHastaMes.value,"fechaCorreshastaanio":fechaCorresHastaAnio.value,
	 			"tipoTercerizadora":tipoTercerizadoraFiltro, "nroCorrespondencia":nroCorrespondencia,"convenioTercerizadora":convenioTercerizadora,
	 			"fechaestadodesdedia":fechaEstadoDesdeDia.value,"fechaestadodesdemes":fechaEstadoDesdeMes.value,
	 			"fechaestadodesdeanio":fechaEstadoDesdeAnio.value,
	 			"fechaestadohastadia":fechaEstadoHastaDia.value,"fechaestadohastames":fechaEstadoHastaMes.value,"fechaestadohastaanio":fechaEstadoHastaAnio.value
	 			,"estadosss_his":estadoSSSHisExpediente,
	 			"fechaestsurdesdedia":fechaEstSurDesdeDia.value,"fechaestsurdesdemes":fechaEstSurDesdeMes.value,"fechaestsurdesdeanio":fechaEstSurDesdeAnio.value,
	 			"fechaestsurhastadia":fechaEstSurHastaDia.value,"fechaestsurhastames":fechaEstSurHastaMes.value,"fechaestsurhastaanio":fechaEstSurHastaAnio.value,
	 			"ddjj":ddjj,"codigoHIV":codigohiv
	 			};
	 	
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/buscarSeguimientoSur" /></portlet:renderURL>';
	 	
		jQuery('#<portlet:namespace />listado_seguimientoSur').load(url,busquedaNom, function(){
															jQuery('#<portlet:namespace />buscando').hide();      
		});	
		
	}
	
	function <portlet:namespace />buscarNomencladorAutocompletar(){
		var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val();
		var codigo_nomenclador=jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val();

		if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
	        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
	    }else {
	    	if(popupMD==null)
	    		popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
	    	
		    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
		    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&codigonomenclador='+encodeURI(codigo_nomenclador);
			jQuery(popupMD).load(url);
	    }
    }

	function pasarParametrosAParentNm(tipoNomenclador,codigo,descripcion) {	
		seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
	    <portlet:namespace />cerrarNm();
	}
	
	function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
		jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val(codigo);
		jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val(descripcion);
		jQuery("#<portlet:namespace />nom_seleccionado").val("1");
	}
	
	function <portlet:namespace />cerrarDivNm(){
		jQuery("#divSeguimientoSur").hide("slow");
	}

	function <portlet:namespace />cerrarNm(){
		<portlet:namespace />cerrarDivNm();
		if(popupMD){
			Liferay.Popup.close(popupMD);
		}
	}
	
	function <portlet:namespace />limpiarNomencladorAutocompletar(){
		jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val('');
		jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val('');
    }
	
	function <portlet:namespace />reporteSeguimiento(){
		window.location.href ='/xlsservlet/?reporte=REPORTE_SEGUIMIENTOSUR';
	}
	
	function <portlet:namespace />actualizaBimestresFiltro(){
		var ejercicio=jQuery("#<portlet:namespace />ejercicio_filtro").val();	
		var clase=jQuery("#<portlet:namespace />claseExpedienteFiltro").val();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_bimestres_para_anio'
		    + '&ejercicio=' +ejercicio;
		url += '&clase=' + clase;
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				var str='';
				jQuery('#<portlet:namespace />bimestre_filtro').find('option').remove();
				str="<option value='0'>Seleccione período</option>";
				jQuery('#<portlet:namespace />bimestre_filtro').append(str);
				for(var i =0;i< obj.bimestres.length; i++){
					str='<option value="'+obj.bimestres[i].id+'"';
					str+='>'+obj.bimestres[i].descripcion +'</option>'
					jQuery('#<portlet:namespace />bimestre_filtro').append(str);
				}                                                                                                                                                                                                                                                            
			}
		});		
	}
	
	function <portlet:namespace />pagosSeguimientoSur() {
		var params = "&<%= Constants.CMD %>=" + "opcion_pagos";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_seguimientosur" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}

	function <portlet:namespace />habilitaTercerizadora(){
		var opcion =jQuery("#<portlet:namespace />tipo_expediente_filtro").val();
		if(opcion==2){
			jQuery("#<portlet:namespace />divTipoExpedienteTercerizadora").show();
		}else{
			jQuery("#<portlet:namespace />divTipoExpedienteTercerizadora").hide();
		}
	}

</script>

