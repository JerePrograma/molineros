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
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@page import="com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil"%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFolder"%>
<%@page import="ar.com.ospim.tesoreria.service.CajaChicaServiceUtil"%>


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
	
	
	int id_caja_chica=cajaChica!=null && cajaChica.getId() !=null ?(int)cajaChica.getId():0;
	cajaChica = CajaChicaServiceUtil.get(id_caja_chica,entidad );
	
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
	
	List<Localidad> jurisdicciones=TraeListasServiceUtil.getPercepcionesIIBB(entidad);
	
	
	
	 DLFolder f = DLFolderLocalServiceUtil.getFolder(
		        10136, 0L, "CajaChica");
		    long folderId = f.getFolderId();
	
	
	String comprobanteImagen = comprobante.getImagenNombreFileEntry();	    
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
					
				</tr>
				
				<tr>
				  <td>&nbsp;</td>
			    </tr>
					
				<tr>
				  <table class="lfr-table" with="100%">
			         <tr>
			          <td>
					      <label>Gravado</label>
				      </td>
				      <td>
      					<input type="text" name="<portlet:namespace />importe_gravado" id="<portlet:namespace />importe_gravado" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="calculoIva();calculoConcepto();agregarCeros(this);"
      					 value='<%= comprobante != null && comprobante.getGravadoIVA() !=null? comprobante.getGravadoIVA() : new String("")%>'
      					/>
				      </td>
				      <td style="background-color:#AEB6BF" ><input type="radio" name="<portlet:namespace />tasa_iva" 
				          value="0" onchange="calculoIva();calculoConcepto()"
				          <%if(comprobante != null && (comprobante.getTasaIva() ==null || comprobante.getTasaIva()==0D)){%> 
				          checked="checked" 
				          <%}%>
				          >Exento &nbsp;</td>
				      <td>&nbsp;</td>
				      <td style="background-color:#AEB6BF" ><input type="radio" name="<portlet:namespace />tasa_iva" 
				           value="0.27" onchange="calculoIva();calculoConcepto()"
				           <%if(comprobante != null && comprobante.getTasaIva() !=null && comprobante.getTasaIva()==0.27D){%> 
				              checked="checked" 
				          <%}%>
				           >Gravado 27% &nbsp;</td>
				      <td>&nbsp;</td>
				      <td style="background-color:#AEB6BF" ><input type="radio" name="<portlet:namespace />tasa_iva" 
				           value="0.21" onchange="calculoIva();calculoConcepto()"
				           <%if(comprobante != null && comprobante.getTasaIva() !=null && comprobante.getTasaIva()==0.21D){%> 
				              checked="checked" 
				           <%}%>
				           >Gravado 21% &nbsp;</td>
				      <td>&nbsp;</td>
				      <td style="background-color:#AEB6BF" ><input type="radio" name="<portlet:namespace />tasa_iva" 
				          value="0.105" onchange="calculoIva();calculoConcepto()"
				          <%if(comprobante != null && comprobante.getTasaIva() !=null && comprobante.getTasaIva()==0.105D){%> 
				          checked="checked" 
				          <%}%> 
				           >Gravado 10.5% &nbsp;</td>
				      <td>&nbsp;</td>
				      <td>I.V.A:</td>
				      <td>
				        <input type="text" value="<%= comprobante != null && comprobante.getIva() !=null? comprobante.getIva() : new String("")%>" 
				          name="<portlet:namespace />importe_iva" id="<portlet:namespace />importe_iva" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="calculoConcepto();agregarCeros(this);"/>
				      </td>
			        </tr>  
			        <tr>
				      <td>&nbsp;</td>
			        </tr>
	              </table>
				  
				  <table class="lfr-table" with="100%">			    
			        <tr>
				      <td>Percep.IVA:</td>
				      <td>
				        <input type="text" 
				            value="<%= comprobante != null && comprobante.getPercepcionIVA() !=null? comprobante.getPercepcionIVA() : new String("")%>" 
				            name="<portlet:namespace />importe_percep_iva" id="<portlet:namespace />importe_percep_iva" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="calculoConcepto();agregarCeros(this);"/>
				      </td>
				      
				      <td>Percep.IIBB:</td>
				      <td>
				        <input type="text" 
				        value="<%= comprobante != null && comprobante.getPercepcionIIBB() !=null? comprobante.getPercepcionIIBB() : new String("")%>" 
				        name="<portlet:namespace />importe_percep_iibb" id="<portlet:namespace />importe_percep_iibb" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="calculoConcepto();agregarCeros(this);"/>
				      </td>
				      <td>Jurisdicción:</td>
				      
				      
				      <td>
			             <select name="<portlet:namespace/>jurisdiccion_iibb" id="<portlet:namespace/>jurisdiccion_iibb"  >
					               <option value="0">Seleccione</option>
					               <%for (Localidad tnom : jurisdicciones) {%>
								     <option value="<%= tnom.getId_provincia() %>"
								     <%if(comprobante != null && comprobante.getJurisdiccionIIBB() !=null && comprobante.getJurisdiccionIIBB()==tnom.getId_provincia()) {%>
								         selected="selected"
								     <%}%>
								     >
								     <%=tnom.getDescripcion() %></option>
					               <%}%>
			              </select>
		              </td>
				      <td>Otros Tributos:</td>
				      <td>
				        <input type="text" 
				            value="<%= comprobante != null && comprobante.getOtrosTributos() !=null? comprobante.getOtrosTributos() : new String("")%>" 
				            name="<portlet:namespace />importe_otros_tributos" id="<portlet:namespace />importe_otros_tributos" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="calculoConcepto();agregarCeros(this);"/>
				      </td>
				      
				      
				      <td>Total Comprobante:</td>
<!--  			          
			          <td>
				        <input type="text" value="" name="<portlet:namespace />importe_concepto" id="<portlet:namespace />importe_concepto" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
				               style="background:#AEB6BF" readonly="readonly" />
				               
				      </td> 
-->				      
				      
				      <td>
						<input type="text" id="<portlet:namespace />importe_comprobante" name="<portlet:namespace />importe_comprobante" maxlength="25"
						value='<%= comprobante != null && comprobante.getImporteComprobante()!=null? comprobante.getImporteComprobante() : new String("")%>'
						style="background:#AEB6BF" readonly="readonly"/>
						
						<%if("uoma".equalsIgnoreCase(portlet_name)){%>
		                  <a href="javascript:void(0)" onclick="help(event, 'helpImporteComprobante')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		                <%}%>
					  </td>
			      
			      </tr>
				    
				  <tr>
				    <td>&nbsp;</td>
			      </tr>  
			     </table>	
					
				  	
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


<form action="" method="post" name="<portlet:namespace />fmImgS" enctype="multipart/form-data">
 <div id="comprobanteIMG">
 
 <fieldset>
   <legend>Imágen</legend>
   <table  class="lfr-table" style="border-collapse: separate; border-spacing: 5px;" >
				      <tr>
				        <td>Imágen Comprobante:</td>  
				        <td>
				             <input type="text" name="<portlet:namespace />fc_nombre" 
										id="<portlet:namespace />fc_nombre" value="<%=comprobanteImagen%>" 
						 				disabled='disabled' >
						 				  
						</td>		
						<td>
							   <input type="file" name="fc_imagen" id="fc_imagen" />
						 </td>
						<td>&nbsp;</td>
	                    
		                <td>
		                  <input id="<portlet:namespace />uploadIMGFactura"
					      value="<liferay-ui:message key="upload-file"/>"
					      title="<liferay-ui:message key="upload-file" />"
					      onClick="javascript: <portlet:namespace />uploadImagen();"
					      type="button" />
					    </td>
 	                    
 	                    <td>
	                      <input id="<portlet:namespace />verIMGFactura"
					      value="Ver Imágen"
					      title="Ver Imágen"
					      onClick="javascript: <portlet:namespace />verImagen('<%=folderId%>','<%=comprobante.getImagenNombreFileEntry()%>');"
					      type="button" />
 	                    </td>
 	                    <td>
 	                      <input id="<portlet:namespace />delIMGFactura"
					      value="Eliminar Imágen"
					      title="Eliminar Imágen"
					      onClick="javascript: <portlet:namespace />deleteImagen('<%=folderId%>','<%=comprobante.getImagenNombreFileEntry()  %>');"
					      type="button" />

 	                    </td>
		              </tr>
		              <tr><td>&nbsp;</td></tr>
		         
	</table>
</fieldset>	
</div>	
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
  
  jQuery('#<portlet:namespace />fc_nombre').css("background","#F998AA");
  if(jQuery('#<portlet:namespace />fc_nombre').val()!=""){
	  jQuery('#<portlet:namespace />fc_nombre').css("background","#D3EFB4");
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
					
					if ( (jQuery("#<portlet:namespace/>jurisdiccion_iibb").val()=="0" && 
							(jQuery("#<portlet:namespace />importe_percep_iibb").val()!="" && jQuery("#<portlet:namespace />importe_percep_iibb").val()!="0"))
						  ||
						 (jQuery("#<portlet:namespace/>jurisdiccion_iibb").val()!="0" && 
								 (jQuery("#<portlet:namespace />importe_percep_iibb").val()=="" || jQuery("#<portlet:namespace />importe_percep_iibb").val()=="0"))						
					){ 
						result=false;
						alert("Debe ingresar el Importe y la Jurisdicción de IIBB");
					
				    }else{
					   if(!<portlet:namespace />verificaSaldoCajaChica()){
						   result=false;
					   }
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
   popUpCJ = Liferay.Popup({title:"<liferay-ui:message key="Ultimos Movimientos:" />",modal:true,width:1250,position:[20,10],xy: ['center', 100]});
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
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("M", "M");
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
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("M", "M");
		}
		if(tipo=='NDB'){
			letra=(letra==null?"":letra);
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("B", "B");
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("C", "C");
			document.getElementById("<portlet:namespace />letra").options[document.getElementById("<portlet:namespace />letra").options.length]=new Option("M", "M");
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


function calculoIva(){
	  var pIva = jQuery("input:radio[name='<portlet:namespace />tasa_iva']:checked").val();
	  var gravado = jQuery('#<portlet:namespace />importe_gravado').val();
	  jQuery('#<portlet:namespace />importe_iva').val(Math.round(gravado*pIva*100)/100);
	  
}
	
	
	
function calculoConcepto(){
	  var iIva = jQuery('#<portlet:namespace />importe_iva').val();
	  var iGravado = jQuery('#<portlet:namespace />importe_gravado').val();
	  var iPercIva =jQuery('#<portlet:namespace />importe_percep_iva').val();
	  var iPercIIBB =jQuery('#<portlet:namespace />importe_percep_iibb').val();
	  var iOtrosTributos =jQuery('#<portlet:namespace />importe_otros_tributos').val();
		  
		  
	  iGravado = (iGravado == null || iGravado == undefined || iGravado == "") ? 0 : iGravado;
	  iIva = (iIva == null || iIva == undefined || iIva == "") ? 0 : iIva;
	  iPercIva = (iPercIva == null || iPercIva == undefined || iPercIva == "") ? 0 : iPercIva;
	  iPercIIBB = (iPercIIBB == null || iPercIIBB == undefined || iPercIIBB == "") ? 0 : iPercIIBB;
	  iOtrosTributos = (iOtrosTributos == null || iOtrosTributos == undefined || iOtrosTributos == "") ? 0 : iOtrosTributos;
		  
      var iTotal=(parseFloat(iGravado)+parseFloat(iIva)+parseFloat(iPercIva)+parseFloat(iPercIIBB)+parseFloat(iOtrosTributos)).toFixed(2);

	  jQuery('#<portlet:namespace />importe_comprobante').val(Math.round(iTotal*100)/100);
		  
}


//Imagenes

function <portlet:namespace />uploadImagen(){
	 if (<portlet:namespace />validarCamposIMG()) {
			var tipo = jQuery("#<portlet:namespace />tipo_comprobante").val();
			var cuit = jQuery("#<portlet:namespace/>cuit_entidad").val();
			var sucursal=jQuery("#<portlet:namespace/>sucursal_entidad").val();
			var razonSoc=jQuery("#<portlet:namespace/>entidad").val();
			var letra=jQuery("#<portlet:namespace />letra").val();
			var ptoVta =jQuery("#<portlet:namespace />pto_venta").val();
			var nro = jQuery('#<portlet:namespace />nro_comprobante_cc').val();
			var params = "&<%= Constants.CMD %>=" + "addImagen";
			var idCaja=jQuery('#<portlet:namespace />id_caja_chica').val();
			var idComprobante=jQuery('#<portlet:namespace />id_comprobante_caja_chica').val();
			
			var tasaIva = jQuery("input:radio[name='<portlet:namespace />tasa_iva']:checked").val();
			var iIva = jQuery('#<portlet:namespace />importe_iva').val();
			var iGravado = jQuery('#<portlet:namespace />importe_gravado').val();
			var iPercIva =jQuery('#<portlet:namespace />importe_percep_iva').val();
			var iPercIIBB =jQuery('#<portlet:namespace />importe_percep_iibb').val();
			var iOtrosTributos =jQuery('#<portlet:namespace />importe_otros_tributos').val();
			var jurIIBB=jQuery('#<portlet:namespace />jurisdiccion_iibb').val();
			var ctoCosto=jQuery('#<portlet:namespace />id_centroCosto').val(); 
			var importe = jQuery("#<portlet:namespace/>importe_comprobante").val();
			
			var fechaDia =jQuery("#<portlet:namespace/>fechaComprobanteCajaChicaDia").val();
			var fechaMes = jQuery("#<portlet:namespace/>fechaComprobanteCajaChicaMes").val();
			var fechaAnio = jQuery("#<portlet:namespace/>fechaComprobanteCajaChicaAnio").val();
			var conceptoId = jQuery("#<portlet:namespace/>conceptoComprobante").val();
			var observaciones =jQuery("#<portlet:namespace />observacionesComprobante").val();
			var seccional=jQuery("#<portlet:namespace/>seccionalCajaChica").val();	

			params += "&cuit_entidad="+ cuit;
			params += "&sucursal_entidad=" +sucursal;
			params += "&letra="+letra;
			params += "&tipo_comprobante="+tipo;
			params += "&pto_venta="+ptoVta;
			params += "&nro_comprobante_cc="+nro;
			params += "&id_caja_chica="+idCaja;
			params += "&id_comprobante="+idComprobante;
			params += "&razon_social="+encodeURI(razonSoc);
			
			params += "&iva="+iIva;
			params += "&gravado="+iGravado;
			params += "&percepcion_iva="+iPercIva;
			params += "&percepcion_iibb="+iPercIIBB;
			params += "&otros_tributos="+iOtrosTributos;
			params += "&tasa_iva=" + tasaIva;
			params += "&jurisdiccion_iibb="+jurIIBB;
			params += "&importe_comprobante="+importe;
	
			params += "&fechaComprobanteCajaChicaDia="+fechaDia;
			params += "&fechaComprobanteCajaChicaMes="+fechaMes;
			params += "&fechaComprobanteCajaChicaAnio="+fechaAnio;
			params += "&conceptoComprobante="+conceptoId;
			
			params += "&id_centroCosto="+ctoCosto;
			params += "&observacionesComprobante="+encodeURI(observaciones);
			params += "&seccionalCajaChica="+seccional; 
			
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
			url = url + params;
			submitForm(document.<portlet:namespace />fmImgS, url);
	}
}


function <portlet:namespace />verImagen(folderId,fileName){
	
	   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	   '<liferay-portlet:param name="struts_action" value="/uoma/documentacion_adjunta_recuperar"/>'+
	   '<liferay-portlet:param name="name" value="__Name"/>'+
	   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
	   '</liferay-portlet:actionURL>';      
	   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
	   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}

function <portlet:namespace />deleteImagen(folderId,fileName) {
	var confirmar=false;
	confirmar = confirm ('Está seguro de eliminar este documento');
	if(confirmar){
		var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/__portlet/editar_caja_chica" />'+
		'<liferay-portlet:param name="cmd" value="deleteImagen"/>'+
		'<liferay-portlet:param name="name" value="__Name"/>'+
		'<liferay-portlet:param name="folderId" value="__FolderId"/>'+
		'</liferay-portlet:actionURL>';	
		url = url.replace("__Name",fileName).replace("__FolderId",folderId).replace("__portlet",'<%= portlet_name%>');
		submitForm(document.<portlet:namespace />fmImgS, url);
	}else{
		return false;
	}	
}


function <portlet:namespace />validarCamposIMG(){
	var result = true;
	if (jQuery("#<portlet:namespace/>cuit_entidad").val()=="" || jQuery("#<portlet:namespace/>sucursal_entidad").val()==""){
			result=false;
			alert("Debe ingresar seleccionar un Acreedor");
	}else{
		if (jQuery('#<portlet:namespace />tipo_comprobante').val()=="" ){
				result=false;
				alert("Debe Seleccionar un Tipo de Comprobante");
		} else{
				
		  if(jQuery('#<portlet:namespace />nro_comprobante_cc').val()==""){
				  result=false;
				  alert("Debe ingresar el nro de Comprobante"); 
		  }else{
			if (jQuery('#<portlet:namespace />letra').val()=="" && jQuery('#<portlet:namespace />tipo_comprobante').val()!="VAR"){
				   result=false;
				   alert("Debe ingresar la letra del Comprobante");
			} else {
				if (jQuery('#<portlet:namespace />pto_venta').val()=="" ){
						   result=false;
						   alert("Debe ingresar el punto de venta del Comprobante");
				} 
			}   
		  }	
	   }
	}		
	return result;
}


//Fin Imagenes

</script>

