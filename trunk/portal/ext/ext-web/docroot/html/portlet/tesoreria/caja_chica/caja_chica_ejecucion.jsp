<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="ar.com.ospim.global.beans.Seccional" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.global.beans.Comprobante"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="ar.com.uoma.beans.CentroCosto" %>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	
	NumberFormat formatter = new DecimalFormat("#0.00");  
	
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	
	Integer entidad = WebKeysGlobal.OSPIM;
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		entidad = WebKeysGlobal.UOMA;
		portlet_name = "uoma";
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	CajaChica cajaChica=(CajaChica)request.getSession().getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
	ComprobanteCajaChica comprobante=(ComprobanteCajaChica)request.getSession().getAttribute(WebKeysCajaChica.CAJA_CHICA_COMPROBANTE_EN_EDICION);
	if(comprobante==null){
		comprobante= new ComprobanteCajaChica();
	}
	
	
	boolean rolAdministrador = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_ADMINISTRADOR_CAJA_CHICA);
	
	List<Concepto> conceptosAux = TraeListasServiceUtil.getConceptosConSeccional(DateUtils.getDesdeEjercicioActual().getTime(), entidad);
	List<Concepto>conceptos = new ArrayList<Concepto>();
	for(Concepto c:conceptosAux){
		if("tesoreria".equalsIgnoreCase(portlet_name)   || cajaChica.getSeccional().getId()==0 || 
			c.getIdSeccional()==cajaChica.getSeccional().getId() || (rolAdministrador && "uoma".equalsIgnoreCase(portlet_name) &&
					c.getIdSeccional() == 0)
		){
			conceptos.add(c);
		}
		
	}
	int id_caja_chica=cajaChica!=null && cajaChica.getId() !=null ?(int)cajaChica.getId():0;
	
	int id_comprobante_caja_chica=comprobante!=null && comprobante.getId() !=null ?(int)comprobante.getId():0;
	
	if(cajaChica==null){
		cajaChica= new CajaChica();
	} 
	
	Calendar fechaComprobante = CalendarFactoryUtil.getCalendar();
	if(comprobante==null || comprobante.getFechaEmision()==null){
	  fechaComprobante.setTime(new Date());
	}else{
	  fechaComprobante.setTime(comprobante.getFechaEmision());
	}
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	
	String comprobanteTipo = (String)request.getSession().getAttribute("comprobanteTipo");
	String comprobanteLetra= (String)request.getSession().getAttribute("comprobanteLetra");
	String comprobantePtoVenta= (String)request.getSession().getAttribute("comprobantePtoVenta");
	String comprobanteNro= (String)request.getSession().getAttribute("comprobanteNro");
	String comprobanteCuit= (String)request.getSession().getAttribute("comprobanteCuit");
	String comprobanteRazonSocial= (String)request.getSession().getAttribute("comprobanteRazonSocial");
	String comprobanteSucursal= (String)request.getSession().getAttribute("comprobanteSucursal");
	
	
	comprobanteTipo =  comprobanteTipo==null?"":comprobanteTipo;
	comprobanteLetra=  comprobanteLetra==null?"":comprobanteLetra;
	comprobantePtoVenta= comprobantePtoVenta==null?"1":comprobantePtoVenta;
	comprobanteNro= comprobanteNro==null?"":comprobanteNro;
	comprobanteCuit= comprobanteCuit==null?"":comprobanteCuit;
	comprobanteRazonSocial= comprobanteRazonSocial==null?"":comprobanteRazonSocial;
	comprobanteSucursal= comprobanteSucursal==null?"":comprobanteSucursal;
	
	List<Seccional>seccionales = TraeListasServiceUtil.getSeccionales();
	
	String conceptoStr = TraeListasServiceUtil.getSystemConfig("caja_chica_concepto_anticipo_"+entidad);
	Integer conceptoAnticipoId=0;
	if(conceptoStr.length()>0){
	   String[] vConceptos=conceptoStr.split(";");
	   conceptoAnticipoId=Integer.parseInt(vConceptos[0]);
	}		   
	
	List<CentroCosto> centros = new ArrayList<CentroCosto>();
	try{
	   centros=TraeListasServiceUtil.getCentrosDeCostosVigentes(entidad);
	}catch(Exception e){}		
	%>

<form action="" method="post" name="<portlet:namespace />fmCJCHEJ">

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	<liferay-ui:error key="errorValida" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
	
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
					<liferay-ui:message key="Estado" />
				</td>
				<td>
				   <input id="<portlet:namespace />estadoCajaChica"
					name="<portlet:namespace />estadoCajaChica" size="40"
					maxlength="40" type="text" 
					value='<%=cajaChica.getEstado().getDescripcion()==null  ?"":cajaChica.getEstado().getDescripcion() %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
				</td>	
					
				<td>
				   <input id="<portlet:namespace />estadoFechaCajaChica"
					name="<portlet:namespace />estadoFechaCajaChica" size="20"
					maxlength="20" type="text"
					value='<%=cajaChica.getEstado().getFecha() ==null?"":sdf.format(cajaChica.getEstado().getFecha()) %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
				</td>
				<td>
					<liferay-ui:message key="Saldo" />
				</td>
				<td>
				
				   <input id="<portlet:namespace />saldoCajaChica"  style="background-color: #72A4D2;"
					name="<portlet:namespace />saldoCajaChica" size="20" 
					maxlength="20" type="text"
					value='<%=formatter.format(cajaChica.getSaldo())%>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
					
				</td>		
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>	
		
		<table class="lfr-table">	
			<tr>
				<td><input id="<portlet:namespace />ultimosMovimientos"
		          value="<liferay-ui:message key="ultimos-movimientos"/>"
		          title="<liferay-ui:message key="ultimos-movimientos" />"
		          onClick="javascript: <portlet:namespace />ultimosMovimientosCajaChica();"
		          type="button" 
		         />
		         
		         <%if("uoma".equalsIgnoreCase(portlet_name)){%>
		            <a href="javascript:void(0)" onclick="help(event, 'helpUltimosMovimientos')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		         <%}%>
		         
		         <%if("tesoreria".equalsIgnoreCase(portlet_name)){%>
	                <input id="<portlet:namespace />solicitudReposicion"
		            value="<liferay-ui:message key="solicitud-reposicion"/>"
		            title="<liferay-ui:message key="solicitud-reposicion" />"
		            onClick="javascript: <portlet:namespace />solicitarReposicionCajaChica();"
		            type="button" 
		            />	 
	                <input id="<portlet:namespace />ingresoReposicion"
		            value="<liferay-ui:message key="ingreso-reposicion"/>"
		            title="<liferay-ui:message key="ingreso-reposicion" />"
		            onClick="javascript: <portlet:namespace />ingresaReposicionCajachica();"
		            type="button" 
		            />
		         <%}%>
		         
		         
		       </td>
		       <td colspan="2" align="right" width="50%">
		          <label id="<portlet:namespace />avisoReposicion" style="font: fantasy; font-style: italic; font-size: 13pt; color:red" hidden="true"><liferay-ui:message key="pedir-reposicion" /></label>
		       </td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>
		
	</fieldset>
	<br>
	<fieldset class="block-labels">
		<legend>Egreso</legend>

		<table class="lfr-table">
		    <tr>
		      <table class="lfr-table">
		        <tr>
		           <td><label><liferay-ui:message key="fecha" />:</label></td>
		           <td>  
					    <liferay-ui:input-date
					         dayParam="fechaComprobanteCajaChicaDia"
					         dayValue="<%=fechaComprobante.get(Calendar.DAY_OF_MONTH )%>"
					         dayNullable="<%= true %>" monthParam="fechaComprobanteCajaChicaMes"
					         monthValue="<%=fechaComprobante.get(Calendar.MONTH )%>"
					         monthNullable="<%= true %>" yearParam="fechaComprobanteCajaChicaAnio"
					         yearValue="<%=fechaComprobante.get(Calendar.YEAR )%>"
					         yearNullable="<%= true %>"
					         yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
					         yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
					         firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
					         disabled="<%= false %>"/>
					         
					         <%if("uoma".equalsIgnoreCase(portlet_name)){%>
		                          <a href="javascript:void(0)" onclick="help(event, 'helpFechaComprobante')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		                     <%}%>
				  </td>
				  
				  <td>
				    <%if("uoma".equalsIgnoreCase(portlet_name) || "tesoreria".equalsIgnoreCase(portlet_name)){%>
				      <div id="<portlet:namespace/>seccionalGasto" <%if(cajaChica!=null && !cajaChica.getPideSeccionalGasto() ){%> hidden="hidden" <%}%>>
					     <table>
					      <tr>   
					       <td>
						     <liferay-ui:message key="seccional"/>
					       </td>
					       <td>
						    <select name="<portlet:namespace/>seccionalCajaChica" id="<portlet:namespace/>seccionalCajaChica"
						        onchange="javascript:<portlet:namespace />actualizaConceptosSeccional();">
							   <option value="0">Seleccione una seccional</option>
							   <%	for (Seccional tnom : seccionales) { %>
									<option value="<%= tnom.getId()  %>"
									
									<%if (comprobante != null && comprobante.getSeccional()  !=null && 
									      comprobante.getSeccional().getId() != 0  &&
									      tnom.getId()==comprobante.getSeccional().getId()
					                     ) { %>
							             selected="selected" <%} %>
									 
									><%=tnom.getDescripcion() %></option>
							   <%	} %>
						    </select>
					       </td>	
					      </tr>
					      </table>
				       </div>
				      <%}%>
				  </td>
				  
			      <td><label><liferay-ui:message key="concepto" />:</label></td>
			      <td>
					<select name="<portlet:namespace/>conceptoComprobante" id="<portlet:namespace/>conceptoComprobante" >
						<option value="0">Seleccione un concepto</option>
							<%	for (Concepto tnom : conceptos) { %>
									<option value="<%= tnom.getId() %>" 
									<%if (comprobante != null && comprobante.getConceptos()  !=null && 
									      comprobante.getConceptos().size()>0 &&
									      tnom.getId()==comprobante.getConceptos().get(0).getConceptoComprobante().getId()
					                     ) { %>
							             selected="selected" <%} %>
									><%=tnom.getDescripcion() %></option>
							<%	} %>
					</select>
					<%if("uoma".equalsIgnoreCase(portlet_name)){%>
		                  <a href="javascript:void(0)" onclick="help(event, 'helpConceptoComprobante')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		            <%}%>
				  </td>	
				  <%if("uoma".equalsIgnoreCase(portlet_name)){%>
				       <td colspan="4" >
					      <liferay-ui:message key="centro-costo"/>:&nbsp;
				       </td>
				       <td>
					      <select id="<portlet:namespace />id_centroCosto" name="<portlet:namespace />id_centroCosto">
					         <option value="0">Seleccione Centro Costo</option>
						       <%for (CentroCosto centro : centros) {  %>
								   <option value="<%=centro.getId()%>"
								   
								   <%if (comprobante != null && comprobante.getCentroCosto()  !=null && 
									      comprobante.getCentroCosto().getId() != 0  &&
									      centro.getId()==comprobante.getCentroCosto().getId()
					                     ) { %>
							             selected="selected" <%}%>
								   
								   ><%=centro.getDescripcion()%></option>
							   <%}%>
					      </select>
				       </td>
				  <%}%>
			    </tr>
			  </table>	  
		    </tr>
		    <tr>
				<td>&nbsp;</td>
			</tr>
		    <tr>
		      <table class="lfr-table">
		       <tr>
			      <td valign="top" >
			        <%if("uoma".equalsIgnoreCase(portlet_name)){%>
		                  <a href="javascript:void(0)" onclick="help(event, 'helpAcreedorComprobante')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		            <%}%><label><liferay-ui:message key="acreedor" />:</label>
		          </td>
			      <td colspan="5" valign="top">
				   <liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
			  		    <liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
			  		    <liferay-util:param name="cuit" value='<%= comprobante != null &&  comprobante.getAcreedorEmpresa() != null ? comprobante.getAcreedorEmpresa().getCuit() : new String("")%>'/>
			  		    <liferay-util:param name="sucu" value='<%=comprobante != null && comprobante.getAcreedorEmpresa() != null ? comprobante.getAcreedorEmpresa().getSucursal() : new String("") %>'/>
			  		    <liferay-util:param name="razon" value='<%=comprobante != null && comprobante.getAcreedorEmpresa() != null ? comprobante.getAcreedorEmpresa().getRazon_soc() : new String("") %>'/>
  			  		     
			  		    <liferay-util:param name="buscar_destino" value='<%= comprobante!=null&& comprobante.getAcreedorEmpresa()!=null&&(null==comprobante.getAcreedorEmpresa().getRazon_soc()||"null".equals(comprobante.getAcreedorEmpresa().getRazon_soc().trim()))?"false":"true"%>'/>
				   </liferay-util:include>
				   
			      </td>
			   </tr>
			  </table>     
	        </tr>
	        <tr>
				<td>&nbsp;</td>
			</tr>
	        <tr>
	           <table width="90%">
				<tr>
					<td><label><liferay-ui:message key="tipo" />:</label></td>
					<td>
					    <select id="<portlet:namespace />tipo_comprobante" name="<portlet:namespace />tipo_comprobante" onchange="<portlet:namespace />sugerirLetraComprobante();<portlet:namespace />sugerirNroComprobante()">
					       <option value="TCK" <%=Validator.isNotNull(comprobante.getTipoComprobante()) && comprobante.getTipoComprobante().equals("TCK") ? "selected" : ""  %>>TCK</option>
						   <option value="FCP" <%=Validator.isNotNull(comprobante.getTipoComprobante()) && comprobante.getTipoComprobante().equals("FCP") ? "selected" : ""  %>>FCP</option>
						   <option value="NCR" <%=Validator.isNotNull(comprobante.getTipoComprobante()) && comprobante.getTipoComprobante().equals("NCR") ? "selected" : ""  %>>NCR</option>
						   <option value="NDB" <%=Validator.isNotNull(comprobante.getTipoComprobante()) && comprobante.getTipoComprobante().equals("NDB") ? "selected" : ""  %>>NDB</option>
						   <option value="RCB" <%=Validator.isNotNull(comprobante.getTipoComprobante()) && comprobante.getTipoComprobante().equals("RCB") ? "selected" : ""  %>>RCB</option>
						   <option value="ANT" <%=Validator.isNotNull(comprobante.getTipoComprobante()) && comprobante.getTipoComprobante().equals("ANT") ? "selected" : ""  %>>ANT</option>
						   <option value="REI" <%=Validator.isNotNull(comprobante.getTipoComprobante()) && comprobante.getTipoComprobante().equals("REI") ? "selected" : ""  %>
						                       <%=portlet_name.equalsIgnoreCase("uoma")?"hidden":"" %>>REI</option>
						   <option value="VAR" <%=Validator.isNotNull(comprobante.getTipoComprobante()) && comprobante.getTipoComprobante().equals("VAR") ? "selected" : ""  %> >VAR</option>
						</select>
						
						<%if("uoma".equalsIgnoreCase(portlet_name)){%>
		                  <a href="javascript:void(0)" onclick="help(event, 'helpTipoComprobante')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		                <%}%>
						
					</td>
					<td><label><liferay-ui:message key="letra" />:</label></td>
					<td>
					<select id="<portlet:namespace />letra" name="<portlet:namespace />letra" onchange="<portlet:namespace />sugerirNroComprobante()">
					   <option value="" <%=Validator.isNotNull(comprobante.getLetraComprobante()) && comprobante.getLetraComprobante().equals("") ? "selected" : ""  %>
					                     <%=!portlet_name.equalsIgnoreCase("uoma")?"hidden":"" %>></option>
					    <option value="X" <%=Validator.isNotNull(comprobante.getLetraComprobante()) && comprobante.getLetraComprobante().equals("X") ? "selected" : ""  %>>X</option>
						<option value="A" <%=Validator.isNotNull(comprobante.getLetraComprobante()) && comprobante.getLetraComprobante().equals("A") ? "selected" : ""  %>>A</option>
						<option value="B" <%=Validator.isNotNull(comprobante.getLetraComprobante()) && comprobante.getLetraComprobante().equals("B") ? "selected" : ""  %>>B</option>
						<option value="C" <%=Validator.isNotNull(comprobante.getLetraComprobante()) && comprobante.getLetraComprobante().equals("C") ? "selected" : ""  %>>C</option>
						<option value="M" <%=Validator.isNotNull(comprobante.getLetraComprobante()) && comprobante.getLetraComprobante().equals("M") ? "selected" : ""  %>>M</option>
						</select>
					</td>
					<td>
						<label><liferay-ui:message key="pto-venta" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />pto_venta" name="<portlet:namespace />pto_venta" onkeydown="allowOnlyDigits(event)" value='<%= comprobante != null ? comprobante.getPtoVenta() : new String("")%>' 
						onchange="<portlet:namespace />sugerirNroComprobante()" maxlength="5" />
						<%if("uoma".equalsIgnoreCase(portlet_name)){%>
		                  <a href="javascript:void(0)" onclick="help(event, 'helpPtoVtaComprobante')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		                <%}%>
					</td>
					<td>
						<label><liferay-ui:message key="numero" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />nro_comprobante_cc" name="<portlet:namespace />nro_comprobante_cc" maxlength="25" value='<%= comprobante != null && comprobante.getNroComprobante()!=null? comprobante.getNroComprobante() : new String("")%>' maxlength="8"
						    onkeydown="allowOnlyDigits(event)"/>
						<%if("uoma".equalsIgnoreCase(portlet_name)){%>
		                  <a href="javascript:void(0)" onclick="help(event, 'helpNroComprobante')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		                <%}%>
					</td>
					
					<td>
						<label><liferay-ui:message key="importe" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />importe_comprobante" name="<portlet:namespace />importe_comprobante" maxlength="25"
						value='<%= comprobante != null && comprobante.getImporteComprobante()!=null? comprobante.getImporteComprobante() : new String("")%>'/>
						<%if("uoma".equalsIgnoreCase(portlet_name)){%>
		                  <a href="javascript:void(0)" onclick="help(event, 'helpImporteComprobante')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		                <%}%>
					</td>
				  	
				</tr>
				<tr>
				 <td>&nbsp;</td>
			    </tr>
				<tr>
				  <td colspan="1" valign="top">
				  <%if("uoma".equalsIgnoreCase(portlet_name)){%>
		                  <a href="javascript:void(0)" onclick="help(event, 'helpObservacionComprobante')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		          <%}%>
				  <liferay-ui:message key="observaciones"/>:</label></td>
	              <td colspan="10"><textarea rows="5" cols="110" maxlength="250"  
		               id="<portlet:namespace />observacionesComprobante" 
					   name="<portlet:namespace />observacionesComprobante"
					   style="resize: none;"><%= comprobante != null && comprobante.getObservaciones()!=null? comprobante.getObservaciones() : new String("")%></textarea>
		          </td>	
				</tr>
				
				<tr>
				   <td>&nbsp;</td>
				</tr>
				
				<tr>
				  <td><input id="<portlet:namespace />grabaComprobantes"
		              value="<liferay-ui:message key="guardar"/>"
		              title="<liferay-ui:message key="guardar" />"
		              onClick="javascript: <portlet:namespace />salvarComprobante();"
		              type="button"/>
		              <%if("uoma".equalsIgnoreCase(portlet_name)){%>
		                  <a href="javascript:void(0)" onclick="help(event, 'helpGuardarComprobante')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		                <%}%>
		          </td>
		          
		          <td><input id="<portlet:namespace />limpiarComprobantes"
		              value="<liferay-ui:message key="Limpiar"/>"
		              title="<liferay-ui:message key="Limpiar" />"
		              onClick="javascript: <portlet:namespace />limpiarComprobante();"
		              type="button"/>
		          </td>
		          
				</tr>
			  </table>	
	        </tr> 
	    </table>
		
	</fieldset>
	
	<input type="hidden" name="<portlet:namespace />id_caja_chica"
		id="<portlet:namespace />id_caja_chica" value="<%=id_caja_chica%>" />
    <input type="hidden" name="<portlet:namespace />id_comprobante_caja_chica"
		id="<portlet:namespace />id_comprobante_caja_chica" value="<%=id_comprobante_caja_chica%>" />		
	<input type="hidden" value="" name="view" id="view" /> 

    
   
</form>

<div id="helpUltimosMovimientos" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  A través de la opción 'Últimos Movimientos' se accede a una nueva pantalla donde se exhiben todos los comprobantes ingresados y pendientes de rendir. Se podrán modificar o eliminar.</div>
</div>

<div id="helpFechaComprobante" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  Aquí se deberá ingresar la fecha de emisión que figure en el comprobante.
</div>

<div id="helpConceptoComprobante" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  El concepto del gasto debe ser seleccionado del listado que aquí se propone. En el caso que se trate de un comprobante que contenga más de un concepto de gasto, se deberán realizar varios ingresos por separado, del mismo comprobante, e indicando en cada uno el concepto e importe que le corresponda. El total de los distintos ingresos, debe coincidir con el total del comprobante. En el caso de no poder determinar un concepto del gasto entre los propuestos, deberán contactar a la Tesorería de la Sede Central, para que les indiquen cómo proceder.
</div>

<div id="helpAcreedorComprobante" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  El CUIT que se ingresa aquí, debe ser el que figure en el comprobante. 
</div>

<div id="helpTipoComprobante" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  El 'Tipo' se corresponde con el tipo de comprobate que se trate. Se deberá seleccionar uno de los propuestos. La 'Letra' se refiere a la identificación 'B' o 'C' que figure en el comprobante que se está ingresando.
</div>
<div id="helpPtoVtaComprobante" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
   El 'Punto de venta' es el que figure en el comprobante. En el caso de facturas o recibos, son los primeros 4 dígitos del número, que generalmente se encuentran separados con un guión del resto. En caso de comprobantes que no tengan identificación del punto de venta, se deberá dejar en cero.
</div>
<div id="helpNroComprobante" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
   Se deberá ingresar el número de comprobante. En el caso de facturas o recibos, son los siguientes 8 dígitos, que continúan a la identificación del punto de venta.
</div>
<div id="helpImporteComprobante" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
    Aquí se indicará el importe del comprobante en el caso de corresponder un único concepto de gasto o el importe parcial, que se corresponda con el concepto indicado, en el caso de varios conceptos para un mismo comprobante. Se recuerda que en el caso que se trate de un comprobante que contenga más de un concepto de gasto, se deberán realizar varios ingresos por separado, del mismo comprobante, e indicando en cada uno el concepto e importe que le corresponda. El total de los distintos ingresos, debe coincidir con el total del comprobante.
</div>
<div id="helpObservacionComprobante" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
    Se trata de un texto opcional, que describa el gasto efectuado. Es una observación, que permite indicar un detalle adicional, al concepto previamente ingresado
</div>

<div id="helpGuardarComprobante" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
 Una vez concluida la carga, se deberá ejecutar el botón 'Grabar' para que lo ingresado se registre en la base de datos del portal. Si se abandona la pantalla sin ejecutar esta orden, todo lo ingresado se perderá.
</div>
<script type="text/javascript">

var popupCJ;
var auxiliar;

<portlet:namespace />initDateFields();

function <portlet:namespace />initDateFields(){
  if(<%=cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()>0 && comprobante.getId()==null  %> ){
	 try{ 
	  jQuery('#<portlet:namespace />tipo_comprobante').val('<%= comprobanteTipo%>'); 
	  jQuery('#<portlet:namespace />letra').val('<%= comprobanteLetra%>');
	  jQuery('#<portlet:namespace />pto_venta').val('<%= comprobantePtoVenta%>');
	  jQuery('#<portlet:namespace />nro_comprobante_cc').val('<%= comprobanteNro%>');
	  
	  jQuery('#<portlet:namespace />cuit_entidad').val('<%= comprobanteCuit%>'); 
	  jQuery('#<portlet:namespace />sucursal_entidad').val('<%= comprobanteSucursal%>');
	  jQuery('#<portlet:namespace />entidad').val('<%= comprobanteRazonSocial%>');
	  
	 }catch(err){}
	 
  }
}



function <portlet:namespace />validarCampos(){
	var result = true;
	var msg="";
	var sugiererepo=false;
	if(jQuery("#<portlet:namespace/>fechaComprobanteCajaChicaDia").val()=="" || jQuery("#<portlet:namespace/>fechaComprobanteCajaChicaMes").val()=="" ||
			jQuery("#<portlet:namespace/>fechaComprobanteCajaChicaAnio").val()==""){
		result=false;
		alert("Debe ingresar una Fecha");
	}else{
		if (jQuery("#<portlet:namespace/>cuit_entidad").val()=="" || jQuery("#<portlet:namespace/>sucursal_entidad").val()==""){
			result=false;
			alert("Debe ingresar seleccionar un Acreedor");
		}else{
			if (jQuery('#<portlet:namespace />conceptoComprobante').val()==0 ){
				result=false;
				alert("Debe Seleccionar un Concepto");
			} else{
				
			  if(jQuery('#<portlet:namespace />nro_comprobante_cc').val()==""){
				  result=false;
				  alert("Debe ingresar el nro de Comprobante"); 
			  }else{
				if (jQuery("#<portlet:namespace/>importe_comprobante").val()=="" || jQuery("#<portlet:namespace/>importe_comprobante").val()==0){
				   result=false;
				   alert("Debe ingresar el Importe");
				}else{
					if(!<portlet:namespace />verificaSaldoCajaChica()){
						result=false;
					}
				}   
			  }	
		   }
		}		
	}	
	return result;
}


function <portlet:namespace />salvarComprobante(){
	if (<portlet:namespace />validarCampos()) {
		var params = "&<%= Constants.CMD %>=" + "savecomprobante";
		// url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_caja_chica';
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
		url = url + params;
		submitForm(document.<portlet:namespace />fmCJCHEJ, url);	
	}
	return false;		
}

function <portlet:namespace />verificaSaldoCajaChica(){
	var result=true;
	var importe = jQuery("#<portlet:namespace/>importe_comprobante").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/valida_saldo_caja_chica';
	url += "&idCaja=<%=cajaChica.getId()%>";
	url += "&importe="+importe;
	url += "&entidad=<%=entidad%>";
    
	if(<%= "tesoreria".equalsIgnoreCase(portlet_name) %>){
		jQuery.ajax({   
				url: url,
				async: false,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					var mensaje= obj.mensaje;
					var resultado =obj.resultado;
					var sugiere=obj.sugierereposicion;
					
		            msg=mensaje;
		            result=(resultado === 'true');
		            if(mensaje!=null && mensaje !=""){
		            	alert(mensaje);
		            } 
		            
		            if((sugiere === 'true')){
		            	jQuery('#<portlet:namespace/>avisoReposicion').show();
		            }
		            
				}				                                                                                                                                                                                                                                                            
				
		});
	}	
	return result;
}

function  <portlet:namespace />ultimosMovimientosCajaChica(){
   var editarNom = {'<%= Constants.CMD %>':'ultimosmovimientos',"id_caja_chica":'<%=id_caja_chica%>'};
   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/caja_chica_ultimos_movimientos';
   popUpCJ = Liferay.Popup({title:"<liferay-ui:message key="Ultimos Movimientos:" />",modal:true,width:1200,position:[50,10],xy: ['center', 100]});
   jQuery(popUpCJ).load(url,editarNom, function(){});	
}	

function  <portlet:namespace />solicitarReposicionCajaChica(){
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/editar_caja_chica&id_caja_chica=<%=id_caja_chica%>';		
	url += "&entidad=<%=entidad%>";
    url += '&<%= Constants.CMD %>=solicitareposicion';
	jQuery.ajax({   
		url: url,
		async: false,
		success: function(data){
			alert("Se ha solicitado la reposicion de la Caja");
		}				                                                                                                                                                                                                                                                            
		
	});
	
}


function  <portlet:namespace />ingresaReposicionCajachica(){
	   var editarNom = {'<%= Constants.CMD %>':'ingresareposicion',"id_caja_chica":'<%=id_caja_chica%>'};
	   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/tesoreria/editar_caja_chica" /></portlet:renderURL>';
	   popUpCJ = Liferay.Popup({title:"<liferay-ui:message key="Ingresa Reposición:" />",modal:true,width:1200});
	   jQuery(popUpCJ).load(url,editarNom, function(){});	
}	

<portlet:namespace />verificaSaldoCajaChica();


function <portlet:namespace />limpiarComprobante(){
	
	jQuery("#<portlet:namespace/>fechaComprobanteCajaChicaDia").val('<%=fechaComprobante.get(Calendar.DAY_OF_MONTH )%>'); 
	jQuery("#<portlet:namespace/>fechaComprobanteCajaChicaMes").val('<%=fechaComprobante.get(Calendar.MONTH)%>');
	jQuery("#<portlet:namespace/>fechaComprobanteCajaChicaAnio").val('<%=fechaComprobante.get(Calendar.YEAR)%>');
	jQuery("#<portlet:namespace/>cuit_entidad").val("");
	jQuery("#<portlet:namespace/>sucursal_entidad").val("");
	jQuery("#<portlet:namespace/>entidad").val("");
	jQuery('#<portlet:namespace />conceptoComprobante').val("");
	jQuery('#<portlet:namespace />nro_comprobante_cc').val("");
	jQuery("#<portlet:namespace/>importe_comprobante").val("");
	jQuery("#<portlet:namespace />id_comprobante_caja_chica").val("");
	jQuery("#<portlet:namespace />observacionesComprobante").val("");
	jQuery("#<portlet:namespace />pto_venta").val("");
	jQuery("#<portlet:namespace />letra").val("");
	jQuery("#<portlet:namespace />tipo_comprobante").val("");
	
	var params = "&<%= Constants.CMD %>=" + "limpiarcomprobante";
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
	url = url + params;
	submitForm(document.<portlet:namespace />fmCJCHEJ, url);	
	
	

}

function sugerirNumero(){
  var portlet ="<%=portlet_name%>";
  if(portlet=="uoma"){
     <portlet:namespace />sugerirNroComprobante()
  }
    
}

function <portlet:namespace />sugerirNroComprobante(){
	var result=0;
	var tipo = jQuery("#<portlet:namespace />tipo_comprobante").val();
	var cuit = jQuery("#<portlet:namespace/>cuit_entidad").val();
	var sucursal=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var letra=jQuery("#<portlet:namespace />letra").val();
	var ptoVta =jQuery("#<portlet:namespace />pto_venta").val();
	if( cuit!=null && cuit!="" && sucursal!=null && sucursal!="" && ptoVta !=null && ptoVta !=""){
	  var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sugiere_nro_comprobante_caja_chica';
	      url += '&tipo='+tipo;
	      url += "&cuit="+cuit;
	      url += "&sucursal="+sucursal;
	      url+= "&letra="+letra;
	      url += "&entidad=<%=entidad%>";
	      url+= "&ptovta="+ptoVta;
	      
	      jQuery.ajax({   
		     url: url,
		     async: false,
		     success: function(data){
			 var obj = jQuery.parseJSON(data);
			 var resultado =obj.resultado;
			 jQuery('#<portlet:namespace />nro_comprobante_cc').val(resultado);
            
		  }				                                                                                                                                                                                                                                                            
		
	  });
   }
}

function <portlet:namespace />sugerirLetraComprobante(){
	var tipo = jQuery("#<portlet:namespace />tipo_comprobante").val();
	var portlet ="<%=portlet_name%>";
    var letra="<%=comprobanteLetra%>";
    var ptoVta="<%=comprobantePtoVenta%>";
    var comprobanteId="<%=comprobante.getId()%>";
	if(portlet =="uoma" && comprobanteId=="null"){
		document.getElementById("<portlet:namespace />letra").options.length=0;
		if(tipo=='FCP'){
			letra=(letra==null?"A":letra);
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("A", "A");
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("B", "B");
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("C", "C");
		}
		if(tipo=='TCK'){
			letra=(letra==null?"X":letra);
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("X", "X");
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("C", "C");
		}
		if(tipo=='NCR'){
			letra=(letra==null?"A":letra);
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("A", "A");
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("B", "B");
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("C", "C");
		}
		if(tipo=='NDB'){
			letra=(letra==null?"":letra);
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("B", "B");
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("C", "C");
		}
		if(tipo=='RCB'){
			letra=(letra==null?"X":letra);
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("X", "X");
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("B", "B");
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("C", "C");
		}
		if(tipo=='ANT'){
			letra=(letra==null?"":letra);	
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("", "");
			if(<%=conceptoAnticipoId != 0 %>){
				jQuery('#<portlet:namespace />conceptoComprobante').val("<%=conceptoAnticipoId%>");
			}
		}
		if(tipo=='VAR'){
			letra="";
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("", "");
		}
		
		
		if(letra!=""){
		   jQuery("#<portlet:namespace />letra option:contains("+ letra +")").attr('selected', true);
		}else{
			jQuery("#<portlet:namespace />letra option[value='']").attr('selected', true)
		}   

		jQuery("#<portlet:namespace />pto_venta").val((ptoVta==null || ptoVta=="null"?"1":ptoVta));
	}
}


function <portlet:namespace />actualizaConceptosSeccional(){
	var seccional=jQuery("#<portlet:namespace/>seccionalCajaChica").val();	
	var visible = !jQuery('#<portlet:namespace/>seccionalGasto').is(':hidden');
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_conceptos_por_seccional'
	    + '&seccional=' +seccional;
	url += "&entidad=<%=entidad%>";
	if(visible){
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				var str='';
				jQuery('#<portlet:namespace/>conceptoComprobante').find('option').remove();
				str='<option value="0">Seleccione un concepto</option>';
				jQuery('#<portlet:namespace/>conceptoComprobante').append(str);
				
				for(var i =0;i< obj.conceptos.length; i++){
					str='<option value="'+obj.conceptos[i].id+'"';
					if(<%=comprobante !=null && comprobante.getSeccional()!=null &&
							comprobante.getConceptos().size()>0 ?comprobante.getConceptos().get(0).getConceptoComprobante().getId():0%>==obj.conceptos[i].id){
					   str += ' selected ';	
					}
					str+='>'+obj.conceptos[i].descripcion +		
					'</option>'
					jQuery('#<portlet:namespace/>conceptoComprobante').append(str);
				}
				if(<%=comprobante !=null && comprobante.getSeccional()==null && cajaChica.getConceptoUnicoOP() !=null%>){
					jQuery("#<portlet:namespace />conceptoComprobante option:contains("+ '<%=cajaChica.getConceptoUnicoOP().getDescripcion() %>' +")").attr('selected', true);
				}				
				
			}
		});
	}	
}

<portlet:namespace />actualizaConceptosSeccional();
<portlet:namespace />sugerirLetraComprobante();
</script>

