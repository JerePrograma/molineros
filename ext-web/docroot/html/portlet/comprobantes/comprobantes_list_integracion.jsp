<%@ include file="/html/portlet/comprobantes/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<liferay-ui:error exception="<%= ar.com.ospim.liquidaciones.ListasReintegrosNoEncontradasException.class %>" message="lista-reintegros-no-encontrada" />
<liferay-ui:error exception="<%=ar.com.ospim.global.services.ComprobantesYaPagadosException.class %>" message="exception-comprobantes-ya-pagados-baja" />

<%

	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "comprobantes";
	}else if(renderResponse.getNamespace().equals("_COM_1_")){
		portlet_name = "comprobantes";
	}
	List<ClaseBase>sectores = (List<ClaseBase>)ComprobanteServiceUtil.getSectoresByUser(user.getScreenName());
	String usuario=user.getScreenName();
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	fecha.setTime(new Date());


 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
 		
 		String esEditableStr = ParamUtil.getString(request, "esEditable");
 		if (esEditableStr == null || esEditableStr.equals("false")){
 			esEditableStr ="false";
 		}
 		boolean esEditable = Boolean.parseBoolean(esEditableStr);
 		
%>
		<fieldset class="block-labels">
		<legend><liferay-ui:message key="busqueda-comprobantes" /></legend>
		<table width="70%" class="lfr-table">
				<tr>
					<td><label><liferay-ui:message key="tipo" />:</label></td>
					<td>
					<select id="<portlet:namespace />tipo_comprobante" name="<portlet:namespace />tipo_comprobante">
						<option value="">Todos</option>						
						<option value="FCP">FCP</option>
						<option value="NCR">NCR</option>
						<option value="NDB">NDB</option>
						<option value="RCB">RCB</option>
						</select>
					</td>
					<td><label><liferay-ui:message key="letra" />:</label></td>
					<td>
					<select id="<portlet:namespace />letra" name="<portlet:namespace />letra">
						<option value="">Todos</option>						
						<option value="A">A</option>
						<option value="B">B</option>
						<option value="C">C</option>
						<option value="M">M</option>
						</select>
					</td>
					<td>
						<label><liferay-ui:message key="pto-venta" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />pto_venta" name="<portlet:namespace />pto_venta" onkeydown="allowOnlyDigits(event)" />
					</td>
					<td>
						<label><liferay-ui:message key="numero" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />nro_comprobante" name="<portlet:namespace />nro_comprobante" value="" maxlength="25"/>
					</td>
		</tr>
		</table>			
		<table class="lfr-table">
		   <tr><td colspan="8">&nbsp;</td></tr>
		   <tr>			
					
			 <td><label>Estado:</label></td>
			 <td>
				<select id="<portlet:namespace />estado" name="<portlet:namespace />estado">
						<option value="Verificado">Verificado</option>
					</select>
			</td>
					
			<td><label>Area Liquidación:</label></td>
			<td>
					<select id="<portlet:namespace />sector" name="<portlet:namespace />sector">
						<option value="Integración">Integración</option>	
				    </select>
			</td>
					
					
		 </tr>
	</table>
	<table class="lfr-table">			
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td><label><liferay-ui:message key="emisor" />:</label></td>
					<td colspan="7"><liferay-ui:message key="cuit" />&nbsp;<input type="text" id="<portlet:namespace />cuit_compr_emisor" name="<portlet:namespace />cuit_compr_emisor"
						onkeydown="allowOnlyDigits(event)" size="13" maxlength="11" value =""/>
					</td>
					<td>
						<label><liferay-ui:message key="razon-social" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />razon_social" name="<portlet:namespace />razon_social" 
						     size="50" value="" maxlength="60"/>
					</td>
					
				</tr>
	</table>
	<table class="lfr-table">			
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td>
						<label><liferay-ui:message key="fecha-emision" /> Desde:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date 
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaEmisionComprobanteDiaDde"
						monthParam="fechaEmisionComprobanteMesDde"
						yearParam="fechaEmisionComprobanteAnioDde"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 20 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 20 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
					
					
					<td>
						<label><liferay-ui:message key="fecha-emision" /> Hasta:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date 
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaEmisionComprobanteDiaHta"
						monthParam="fechaEmisionComprobanteMesHta"
						yearParam="fechaEmisionComprobanteAnioHta"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 20 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 20 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>	
					<td>
						<label><liferay-ui:message key="fecha-alta" /> Desde:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaRecepcionComprobanteDiaDde"
						monthParam="fechaRecepcionComprobanteMesDde"
						yearParam="fechaRecepcionComprobanteAnioDde" 
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
					
					<td>
						<label><liferay-ui:message key="fecha-alta" /> Hasta:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaRecepcionComprobanteDiaHta"
						monthParam="fechaRecepcionComprobanteMesHta"
						yearParam="fechaRecepcionComprobanteAnioHta" 
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>	
					<td>
						<label><liferay-ui:message key="fecha-vencimiento" /> Desde:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaVencimientoComprobanteDiaDde"
						monthParam="fechaVencimientoComprobanteMesDde"
						yearParam="fechaVencimientoComprobanteAnioDde" 
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
					
					<td>
						<label><liferay-ui:message key="fecha-vencimiento" /> Hasta:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaVencimientoComprobanteDiaHta"
						monthParam="fechaVencimientoComprobanteMesHta"
						yearParam="fechaVencimientoComprobanteAnioHta" 
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
				<table class="lfr-table">
				<tr>
					<td><label><liferay-ui:message key="periodo-prestacion" /> Desde:</label></td>
					<td colspan="2"><liferay-ui:input-date dayParam="periodoDia"
						dayNullable="<%= true %>" 
						dayValue=""
						monthAndYearParam="periodoMesAnio"
						monthAndYearNullable="<%= true %>"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false%>" /></td>
					<td colspan="4">&nbsp;</td>
					
					<td><label><liferay-ui:message key="periodo-prestacion" /> Hasta:</label></td>
					<td colspan="2"><liferay-ui:input-date dayParam="periodoDiaHasta"
						dayNullable="<%= true %>" 
						dayValue=""
						monthAndYearParam="periodoMesAnioHasta"
						monthAndYearNullable="<%= true %>"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false%>" /></td>
					<td colspan="4">&nbsp;</td>
					
					<td><label>Carpeta Integración:</label></td>
					<td colspan="2"><liferay-ui:input-date dayParam="periodoCarpetaDia"
						dayNullable="<%= true %>" 
						dayValue=""
						monthAndYearParam="periodoCarpetaMesAnio"
						monthAndYearNullable="<%= true %>"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 1 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR)%>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false%>" /></td>
						
					<td>
					  <input id="<portlet:namespace />carpeta_filtro" value="Proponer Período" title="Proponer Período" type="button"
						onclick="javascript:<portlet:namespace />proponerPeriodo('periodoCarpetaMesAnio');"/>	
					</td>	
					<td colspan="4">&nbsp;</td>
					<td style="background-color:#AEB6BF">
						<label>Pendientes de Asignar a Carpeta:</label>
			            <input type="checkbox"
						  id="<portlet:namespace />pendientes"
						  name="<portlet:namespace />pendientes" value="false">
					</td>
				</tr>	
				<tr><td colspan="8">&nbsp;</td></tr>
				</table>
    </table>
    <table>	
               <tr><td>
                  <table><tr>
                    <td>
                     <a href="javascript:<portlet:namespace />showHideDivAfiliado();">
		               <legend>
			           <liferay-ui:message	key="afiliado" /> 
			            <img name="arrow_afiliado" id="<portlet:namespace />arrow_afiliado" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
		               </legend>
		             </a>
		            </td>
		            <td>
                     <a href="javascript:<portlet:namespace />showHideDivPrestacion();">
		              <legend>
			           <liferay-ui:message	key="prestacion" /> 
			            <img name="arrow_prestacion" id="<portlet:namespace />arrow_prestacion" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
		              </legend>
		             </a>
		            </td>
		          </tr>
		          </table> 
		        </td></tr>
		        
		        
				<tr>
			     <td>
				  <table class="lfr-table" style="border-collapse: separate; border-spacing:5px;" >
				   <tr>
				      <td>
				         <div id="<portlet:namespace/>divAfiliadosFiltro" >
		                    <fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>
					           <liferay-util:include page='/html/portlet/comprobantes/busqueda_afiliado.jsp'>
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
			   </tr>
			</table>
			
			<tables>
			  <tr>
			     <td colspan="3">
		            <div id="<portlet:namespace />div_prestacion">
		               <fieldset class="block-labels">
		                 <legend>
			                <liferay-ui:message	key="prestacion" /> </legend>
		               
		                 <liferay-util:include
			              page="/html/portlet/comprobantes/busqueda_prestacion.jsp">
			              <liferay-util:param name="search_url"  value="/comprobantes/buscar_prestacion" />
			                    <liferay-util:param name="id_prestacion" value='' />
			              <liferay-util:param name="codigo" value='' />
			              <liferay-util:param name="prestacion" value='' />
			              <liferay-util:param name="discapacidad" value='' />
			              <liferay-util:param name="esEditable"   value='true' />
			              <liferay-util:param name="suf" value='_trat'/>
		               </liferay-util:include></fieldset>
		            </div>
		          </td>
			  </tr>
			  <tr><td colspan="8">&nbsp;</td></tr>
			</tables>
			
			<table>	
				<tr>
				   <td>
				    <div id="<portlet:namespace />divTroquelFiltro">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="buscar-medicamento" />
						</legend>
						   <liferay-util:include page="/html/portlet/utils/medicamentos/busqueda_medicamentos.jsp">
					       <liferay-util:param name="search_url" value="/comprobantes/buscar_medicamento" />
					       <liferay-util:param name="troquel" value='' />
					       <liferay-util:param name="nombre_medicamento" value='' />
					       <liferay-util:param name="id_medicamento" value='' />
					       <liferay-util:param name="esEditable" value='true' />
					       <liferay-util:param name="mostrar_con_presentacion" value='true' />
					       <liferay-util:param name="popup" value='true' />
				        </liferay-util:include>
				    </fieldset>
				  </div>
				  </td>
				</tr>
		</table>
		<table>		
				<tr>
					<td>
						<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"
						onclick="javascript:<portlet:namespace />buscarCptes();"/>							
					</td>
					<td>
					&nbsp;
					</td>	
					<td>
						<input id="<portlet:namespace />limpiar" value="Limpiar Filtro" title="<liferay-ui:message key="limpiar" />" type="button"
						onclick="javascript:<portlet:namespace />limpiarFiltro();"/>							
					</td>
					<td>
					&nbsp;
					</td>
					
					<td colspan="6">&nbsp;</td>
					<td colspan="6">&nbsp;</td>
					<td colspan="6">&nbsp;</td>
					
					<td>
						<input id="<portlet:namespace />marcar" value="Marcar Todos" title="Marcar todos" type="button"
						onclick="javascript:<portlet:namespace />marcarCptes(true);"/>							
					</td>
					<td>
					&nbsp;
					</td>	
					<td>
						<input id="<portlet:namespace />desmarcar" value="Desmarcar Todos" title="Desmarcar" type="button"
						onclick="javascript:<portlet:namespace />marcarCptes(false);"/>							
					</td>
					<td>
					&nbsp;
					</td>
					
					<td colspan="6">&nbsp;</td>	
				</tr>	
				<tr>	
					<td colspan="6">&nbsp;</td>	
				</tr>
		</table>	
		
		<fieldset class="block-labels">
			
		<table>		
				<tr>
				    <td><label>Carpeta a procesar:</label></td>
					<td colspan="2"><liferay-ui:input-date dayParam="carpetaDia"
						dayNullable="<%= true %>" 
						dayValue=""
						monthAndYearParam="carpetaMesAnio"
						monthAndYearNullable="<%= true %>"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 1 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false%>" /></td>
					
				    
				   <!--   <td colspan="6">&nbsp;</td> -->	
				    <td>&nbsp;&nbsp;</td>	
				    
					<td>
						<input id="<portlet:namespace />incluir" value="Incluir en Carpeta" title="Asociar a Carpeta" type="button"
						onclick="javascript:<portlet:namespace />incluirCptes(true);"/>							
					</td>
					<td>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</td>	
					<td>
						<input id="<portlet:namespace />excluir" value="Excluir de Carpeta" title="Desasociar de Carpeta" type="button"
						onclick="javascript:<portlet:namespace />incluirCptes(false);"/>							
					</td>
					<td>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</td>
					<td>
						<input id="<portlet:namespace />excluir" value="Verificar" title="Verificar Comprobantes" type="button"
						onclick="javascript:<portlet:namespace />verificarCptes();"/>							
					</td>
					<td>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</td>
					<td colspan="6">&nbsp;</td>	
					<td>
						<input id="<portlet:namespace />exportar" value="Exportar" title="Exportar Carpeta" type="button"
						onclick="javascript:<portlet:namespace />exportarCarpeta();"/>							
					</td>
					<td colspan="4">&nbsp;&nbsp;&nbsp;</td>
					<td><input type="checkbox" id="<portlet:namespace />forzarExportacion" name="<portlet:namespace />forzarExportacion"/></td>
					<td colspan="16"><label>Forzar Exportación</label></td>
					<td colspan="16">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td>
						<input id="<portlet:namespace />imagenes" value="Recuperar Imágenes" title="Recuperar Imágenes" type="button"
						onclick="javascript:<portlet:namespace />recuperarImgs();"/>							
					</td>
					<!--  <td colspan="16">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>-->
					<td colspan="16">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					
					<td>
						<input id="<portlet:namespace />imagenes" value="Recuperar Imagen Recibo" title="Recuperar Imagen Recibo" type="button"
						onclick="javascript:<portlet:namespace />recuperarImgsRecibo();"/>							
					</td>
					
					
					<td colspan="16">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td>
						<input id="<portlet:namespace />imagenesExp" value="Zip Imágenes" title="Zip Imágenes" type="button"
						onclick="javascript:<portlet:namespace />zipImgs();"/>							
					</td>
					<td colspan="16">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						
					<td colspan="16" align="right">
						<input id="<portlet:namespace />help_errores" value="Códigos Errores" title="Códigos de Error" type="button"
						onclick="javascript:<portlet:namespace />verErrores();"/>							
					</td>
				</tr>
		  		
		</table>
		</fieldset>
		
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
		
		<fieldset class="block-labels">
			<legend>
					<label><liferay-ui:message key="comprobantes" />:</label>
			</legend>
		
			<div align="center" id="<portlet:namespace />busquedaComprobDiv">
				<liferay-util:include page="/html/portlet/comprobantes/comprobantes_search_result_integracion.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEditable)%>" />
				</liferay-util:include>
			</div>	
		</fieldset>

			
<script type="text/javascript">
    jQuery("#<portlet:namespace />periodoDia").hide();
    jQuery("#<portlet:namespace />periodoDiaHasta").hide();
    jQuery("#<portlet:namespace />periodoCarpetaDia").hide();
    jQuery("#<portlet:namespace />carpetaDia").hide();
    jQuery('#<portlet:namespace />buscando').hide();
    
    jQuery('#<portlet:namespace/>divAfiliadosFiltro').hide();
    jQuery('#<portlet:namespace/>div_prestacion').hide();
    jQuery('#<portlet:namespace/>divTroquelFiltro').hide();
    
    

function <portlet:namespace />buscarCptes(){		
				var cuit=jQuery('#<portlet:namespace />cuit_compr_emisor').val();
				var razon=jQuery('#<portlet:namespace />razon_social').val();
				var pto_venta=jQuery('#<portlet:namespace />pto_venta').val();
				var tipo_comprobante=jQuery('#<portlet:namespace />tipo_comprobante').val();
				var nro_comprobante=jQuery('#<portlet:namespace />nro_comprobante').val();			
				var letra=document.getElementById("<portlet:namespace />letra").value;
				var fechaEmisionComprobanteDiaDde=jQuery('#<portlet:namespace />fechaEmisionComprobanteDiaDde').val();
				var fechaEmisionComprobanteMesDde=jQuery('#<portlet:namespace />fechaEmisionComprobanteMesDde').val();
				var fechaEmisionComprobanteAnioDde=jQuery('#<portlet:namespace />fechaEmisionComprobanteAnioDde').val();
				
				var fechaEmisionComprobanteDiaHta=jQuery('#<portlet:namespace />fechaEmisionComprobanteDiaHta').val();
				var fechaEmisionComprobanteMesHta=jQuery('#<portlet:namespace />fechaEmisionComprobanteMesHta').val();
				var fechaEmisionComprobanteAnioHta=jQuery('#<portlet:namespace />fechaEmisionComprobanteAnioHta').val();
				var pendientes=jQuery("#<portlet:namespace/>pendientes").is(':checked');

				if (fechaEmisionComprobanteDiaDde != "" || fechaEmisionComprobanteMesDde != "" || fechaEmisionComprobanteAnioDde != ""){
					if (fechaEmisionComprobanteDiaDde == "" || fechaEmisionComprobanteMesDde == "" || fechaEmisionComprobanteAnioDde == ""){
						alert("Por favor seleccione todos los campos de la fecha de Emision Desde.");
						return false;
					}
				}
				
				if (fechaEmisionComprobanteDiaHta != "" || fechaEmisionComprobanteMesHta != "" || fechaEmisionComprobanteAnioHta != ""){
					if (fechaEmisionComprobanteDiaHta == "" || fechaEmisionComprobanteMesHta == "" || fechaEmisionComprobanteAnioHta == ""){
						alert("Por favor seleccione todos los campos de la fecha de Emision Hasta.");
						return false;
					}
				}
				
				var fechaRecepcionComprobanteDiaDde=jQuery('#<portlet:namespace />fechaRecepcionComprobanteDiaDde').val();
				var fechaRecepcionComprobanteMesDde=jQuery('#<portlet:namespace />fechaRecepcionComprobanteMesDde').val();
				var fechaRecepcionComprobanteAnioDde=jQuery('#<portlet:namespace />fechaRecepcionComprobanteAnioDde').val();

				if (fechaRecepcionComprobanteDiaDde != "" || fechaRecepcionComprobanteMesDde != "" || fechaRecepcionComprobanteAnioDde != ""){
					if (fechaRecepcionComprobanteDiaDde == "" || fechaRecepcionComprobanteMesDde == "" || fechaRecepcionComprobanteAnioDde == ""){
						alert("Por favor seleccione todos los campos de la fecha de Alta Desde.");
						return false;
					}
				}
				
				
				var fechaRecepcionComprobanteDiaHta=jQuery('#<portlet:namespace />fechaRecepcionComprobanteDiaHta').val();
				var fechaRecepcionComprobanteMesHta=jQuery('#<portlet:namespace />fechaRecepcionComprobanteMesHta').val();
				var fechaRecepcionComprobanteAnioHta=jQuery('#<portlet:namespace />fechaRecepcionComprobanteAnioHta').val();

				if (fechaRecepcionComprobanteDiaHta != "" || fechaRecepcionComprobanteMesHta != "" || fechaRecepcionComprobanteAnioHta != ""){
					if (fechaRecepcionComprobanteDiaHta == "" || fechaRecepcionComprobanteMesHta == "" || fechaRecepcionComprobanteAnioHta == ""){
						alert("Por favor seleccione todos los campos de la fecha de Alta Hasta.");
						return false;
					}
				}

				var fechaVencimientoComprobanteDiaDde=jQuery('#<portlet:namespace />fechaVencimientoComprobanteDiaDde').val();
				var fechaVencimientoComprobanteMesDde=jQuery('#<portlet:namespace />fechaVencimientoComprobanteMesDde').val();
				var fechaVencimientoComprobanteAnioDde=jQuery('#<portlet:namespace />fechaVencimientoComprobanteAnioDde').val();

				if (fechaVencimientoComprobanteDiaDde != "" || fechaVencimientoComprobanteMesDde != "" || fechaVencimientoComprobanteAnioDde != ""){
					if (fechaVencimientoComprobanteDiaDde == "" || fechaVencimientoComprobanteMesDde == "" || fechaVencimientoComprobanteAnioDde == ""){
						alert("Por favor seleccione todos los campos de la fecha de Vencimiento Desde.");
						return false;
					}
				}
				
				
				var fechaVencimientoComprobanteDiaHta=jQuery('#<portlet:namespace />fechaVencimientoComprobanteDiaHta').val();
				var fechaVencimientoComprobanteMesHta=jQuery('#<portlet:namespace />fechaVencimientoComprobanteMesHta').val();
				var fechaVencimientoComprobanteAnioHta=jQuery('#<portlet:namespace />fechaVencimientoComprobanteAnioHta').val();

				if (fechaVencimientoComprobanteDiaHta != "" || fechaVencimientoComprobanteMesHta != "" || fechaVencimientoComprobanteAnioHta != ""){
					if (fechaVencimientoComprobanteDiaHta == "" || fechaVencimientoComprobanteMesHta == "" || fechaVencimientoComprobanteAnioHta == ""){
						alert("Por favor seleccione todos los campos de la fecha de Vencimiento Hasta.");
						return false;
					}
				}

           
           
				var peri = jQuery("#<portlet:namespace />periodoMesAnio").val();
				var periHta = jQuery("#<portlet:namespace />periodoMesAnioHasta").val();
				var carpeta = jQuery("#<portlet:namespace />periodoCarpetaMesAnio").val();
				var usr ="<%=usuario%>";
				var estado=jQuery('#<portlet:namespace />estado').val();
				var sector=jQuery('#<portlet:namespace />sector').val();
				var prestacion=jQuery('#<portlet:namespace />codigo_trat').val();
				var prestaciondesc=jQuery('#<portlet:namespace />prestacion_trat').val();
				var medicamento=jQuery('#<portlet:namespace />troquel').val();
				var medicamentodesc=jQuery('#<portlet:namespace />nombre_medicamento').val();
				var dni=jQuery('#<portlet:namespace />nroDoc_filtro').val();
				
				var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();			
				jQuery("#pagina").val(pagina_sel);
				
				jQuery('#<portlet:namespace />buscando').show();
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/comprobantes_portal_proveedores_integracion';
				url += '&cmd=buscar&pto_venta='+pto_venta+'&tipo_comprobante='+tipo_comprobante+'&nro_comprobante='+nro_comprobante+
				    '&fechaEmisionComprobanteDiaDde='+fechaEmisionComprobanteDiaDde+'&fechaEmisionComprobanteMesDde='+fechaEmisionComprobanteMesDde+
				    '&fechaEmisionComprobanteAnioDde='+fechaEmisionComprobanteAnioDde;
				url+='&fechaEmisionComprobanteDiaHta='+fechaEmisionComprobanteDiaHta+'&fechaEmisionComprobanteMesHta='+fechaEmisionComprobanteMesHta+
				    '&fechaEmisionComprobanteAnioHta='+fechaEmisionComprobanteAnioHta+   
					'&fechaRecepcionComprobanteDiaDde='+fechaRecepcionComprobanteDiaDde+
					'&fechaRecepcionComprobanteMesDde='+fechaRecepcionComprobanteMesDde+
					'&fechaRecepcionComprobanteAnioDde='+fechaRecepcionComprobanteAnioDde+
					'&fechaRecepcionComprobanteDiaHta='+fechaRecepcionComprobanteDiaHta+
					'&fechaRecepcionComprobanteMesHta='+fechaRecepcionComprobanteMesHta+
					'&fechaRecepcionComprobanteAnioHta='+fechaRecepcionComprobanteAnioHta+
					'&fechaVencimientoComprobanteDiaDde='+fechaVencimientoComprobanteDiaDde+
					'&fechaVencimientoComprobanteMesDde='+fechaVencimientoComprobanteMesDde+
					'&fechaVencimientoComprobanteAnioDde='+fechaVencimientoComprobanteAnioDde+
					'&fechaVencimientoComprobanteDiaHta='+fechaVencimientoComprobanteDiaHta+
					'&fechaVencimientoComprobanteMesHta='+fechaVencimientoComprobanteMesHta+
					'&fechaVencimientoComprobanteAnioHta='+fechaVencimientoComprobanteAnioHta+
					'&cuit_compr_emisor=' + cuit + '&letra=' + escape(letra);
				url += '&razon_soc='+razon;
				url += '&estado='+estado;
				url += '&sector='+encodeURI(sector);
				url += '&portlet_name=<%=portlet_name%>';
				url += '&periodoMesAnio=' + peri;
				url += '&periodoMesAnioHasta=' + periHta;
				url += '&carpetaMesAnio=' + carpeta;
				url += '&user='+usr;
				url += '&prestacion='+prestacion;
				url += '&medicamento='+medicamento;
				url += '&prestaciondesc='+encodeURI(prestaciondesc);
				url += '&medicamentodesc='+encodeURI(medicamentodesc);
				url += '&dni='+dni;
				url += '&pagina='+pagina_sel;
				url += '&pendientes='+pendientes;
				
				url += '&rnd=' + Math.floor(Math.random()*100);
				
				jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
		        		jQuery('#<portlet:namespace />buscando').hide();
					}
		        );
};
			
			
function <portlet:namespace />showHideDivAfiliado(){
    if (jQuery("#<portlet:namespace />divAfiliadosFiltro").css('display') === 'none') {
		jQuery('#<portlet:namespace />divAfiliadosFiltro').css('display','block');
		jQuery('#<portlet:namespace />arrow_afiliado').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />divAfiliadosFiltro').css('display','none');
		jQuery('#<portlet:namespace />arrow_afiliado').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}			
	
function <portlet:namespace />showHideDivPrestacion(){
    if (jQuery("#<portlet:namespace />div_prestacion").css('display') === 'none') {
		jQuery('#<portlet:namespace />div_prestacion').css('display','block');
		jQuery('#<portlet:namespace />arrow_prestacion').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />div_prestacion').css('display','none');
		jQuery('#<portlet:namespace />arrow_prestacion').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}


function <portlet:namespace />limpiarFiltro(){		
	jQuery('#<portlet:namespace />cuit_compr_emisor').val('');
	jQuery('#<portlet:namespace />razon_social').val('');
	jQuery('#<portlet:namespace />pto_venta').val('');
	jQuery('#<portlet:namespace />tipo_comprobante').val('');
	jQuery('#<portlet:namespace />nro_comprobante').val('');			
	document.getElementById("<portlet:namespace />letra").value='';
	jQuery('#<portlet:namespace />fechaEmisionComprobanteDiaDde').val('');
	jQuery('#<portlet:namespace />fechaEmisionComprobanteMesDde').val('');
	jQuery('#<portlet:namespace />fechaEmisionComprobanteAnioDde').val('');
	
	jQuery('#<portlet:namespace />fechaEmisionComprobanteDiaHta').val('');
	jQuery('#<portlet:namespace />fechaEmisionComprobanteMesHta').val('');
	jQuery('#<portlet:namespace />fechaEmisionComprobanteAnioHta').val('');

	jQuery('#<portlet:namespace />fechaRecepcionComprobanteDiaDde').val('');
	jQuery('#<portlet:namespace />fechaRecepcionComprobanteMesDde').val('');
	jQuery('#<portlet:namespace />fechaRecepcionComprobanteAnioDde').val('');

	jQuery('#<portlet:namespace />fechaRecepcionComprobanteDiaHta').val('');
	jQuery('#<portlet:namespace />fechaRecepcionComprobanteMesHta').val('');
	jQuery('#<portlet:namespace />fechaRecepcionComprobanteAnioHta').val('');

	jQuery('#<portlet:namespace />fechaVencimientoComprobanteDiaDde').val('');
	jQuery('#<portlet:namespace />fechaVencimientoComprobanteMesDde').val('');
	jQuery('#<portlet:namespace />fechaVencimientoComprobanteAnioDde').val('');

	jQuery('#<portlet:namespace />fechaVencimientoComprobanteDiaHta').val('');
	jQuery('#<portlet:namespace />fechaVencimientoComprobanteMesHta').val('');
	jQuery('#<portlet:namespace />fechaVencimientoComprobanteAnioHta').val('');

	jQuery("#<portlet:namespace />periodoMesAnio").val('');
	jQuery("#<portlet:namespace />periodoMesAnioHasta").val('');
	jQuery("#<portlet:namespace />periodoCarpetaMesAnio").val('');
	jQuery('#<portlet:namespace />estado').val('');
	jQuery('#<portlet:namespace />sector').val('');
	jQuery('#<portlet:namespace />codigo_trat').val('');
	jQuery('#<portlet:namespace />prestacion_trat').val('');
	jQuery('#<portlet:namespace />troquel').val('');
	jQuery('#<portlet:namespace />nombre_medicamento').val('');
	jQuery('#<portlet:namespace />nroDoc_filtro').val('');
	
	jQuery('#<portlet:namespace />pendientes').removeAttr('checked');
	
	<portlet:namespace />limpiarCamposAfiliado_filtro();
	
}
	

function <portlet:namespace />marcarCptes(valor){	
  var checkboxes = document.getElementsByName('comprob');
  for(i=0;i<checkboxes.length;i++){
		if(checkboxes[i].type == "checkbox"){
			checkboxes[i].checked=valor;
		}
  }
}


function <portlet:namespace />incluirCptes(valor){
	
	var carpeta = jQuery("#<portlet:namespace />carpetaMesAnio").val();
	
	var trat = document.getElementsByName('comprob');
	var tratValue = "";
	var i = 0;
	for (i = 0; i<trat.length; i++){
		if (trat[i].checked) {					
			tratValue= tratValue+trat[i].value+";"; 
		}
	}

	if( (carpeta==null || ""==carpeta) && valor===true){
		alert("Debe seleccionar una carpeta");
		jQuery("#<portlet:namespace />carpetaMesAnio").focus();
		return false;	
	}
	
	if(tratValue == ""){
		alert("Debe seleccionar comprobantes para realizar la operación");
		return false;	
	}
	
	if(valor===false){
	  carpeta=""; 	
	}
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/comprobantes_portal_proveedores_integracion';
	url += '&cmd=asignarCarpeta&operacion='+valor+'&carpeta='+carpeta+'&ids='+tratValue;
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />buscando').show();
	
	if(confirm ('Esta seguro de incluir/excluir de la carpeta?')){	
	   jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {jQuery('#<portlet:namespace />buscando').hide();});
	}
}


function <portlet:namespace />verificarCptes(){
	
	
	var trat = document.getElementsByName('comprob');
	var tratValue = "";
	var i = 0;
	for (i = 0; i<trat.length; i++){
		if (trat[i].checked) {					
			tratValue= tratValue+trat[i].value+";"; 
		}
	}
	
	if(tratValue == ""){
		alert("Debe seleccionar comprobantes para realizar la operación");
		return false;	
	}
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/comprobantes_portal_proveedores_integracion';
	url += '&cmd=verificar&ids='+tratValue;
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />buscando').show();
	jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {jQuery('#<portlet:namespace />buscando').hide();});
	
}

function <portlet:namespace />exportarCarpeta(){

 	var carpeta = jQuery("#<portlet:namespace />carpetaMesAnio").val();
	var forzarExportacion=jQuery('#<portlet:namespace />forzarExportacion').attr('checked');
	if( (carpeta==null || ""==carpeta)){
		alert("Debe seleccionar una carpeta");
		jQuery("#<portlet:namespace />carpetaMesAnio").focus();
		return false;	
	}
	
	var url1 = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/comprobantes/comprobantes_portal_proveedores_integracion_verifica_carpeta';
	url1 += '&carpeta='+carpeta;
	jQuery('#<portlet:namespace />buscando').show();
    jQuery.ajax({   
       url: url1,
       async:false,
       success: function(data){
	   var obj = jQuery.parseJSON(data);
	   var existeCarpeta=(obj.existeCarpeta === 'true');
	   var existeCarpetaLiquidada=(obj.existeCarpetaLiquidada === 'true');
	   var prosigue=true;
	   if(existeCarpetaLiquidada){
		  prosigue=false;
		  alert("La Carpeta ya fue liquidada. Imposible exportar");
	   }else  if (existeCarpeta){
		   prosigue=confirm("Carpeta Existente. Desea sobreescribirla?");
	   }else{}
	   jQuery('#<portlet:namespace />buscando').hide();
	   if(prosigue){
		   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/comprobantes_portal_proveedores_integracion';
		   url += '&cmd=exportarCarpeta&carpeta='+carpeta;
		   url += '&forzar='+forzarExportacion;
		   url += '&rnd=' + Math.floor(Math.random()*100);
		   jQuery('#<portlet:namespace />buscando').show();
		   jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {jQuery('#<portlet:namespace />buscando').hide();});  
	   }
    }				                                                                                                                                                                                                                                                            
   });	
}

var popupE;
function <portlet:namespace />verErrores(){
  if(popupE==null)
    popupE = Liferay.Popup({title:"Descripción Códigos de Error",modal:true,width:700,onClose: function() { popupE = null;}});

  var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/comprobantes_portal_proveedores_integracion_detalle_errores';
  jQuery(popupE).load(url);
}


function <portlet:namespace />recuperarImgs(){
	var trat = document.getElementsByName('comprob');
	var tratValue = "";
	var i = 0;
	for (i = 0; i<trat.length; i++){
		if (trat[i].checked) {					
			tratValue= tratValue+trat[i].value+";"; 
		}
	}
	
	if(tratValue == ""){
		alert("Debe seleccionar comprobantes para realizar la operación");
		return false;	
	}
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/comprobantes_portal_proveedores_integracion';
	url += '&cmd=recuperarimgs&ids='+tratValue;
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />buscando').show();
	jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {jQuery('#<portlet:namespace />buscando').hide();});
	
}


function <portlet:namespace />recuperarImgsRecibo(){
	var trat = document.getElementsByName('comprob');
	var tratValue = "";
	var i = 0;
	for (i = 0; i<trat.length; i++){
		if (trat[i].checked) {					
			tratValue= tratValue+trat[i].value+";"; 
		}
	}
	
	if(tratValue == ""){
		alert("Debe seleccionar comprobantes para realizar la operación");
		return false;	
	}
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/comprobantes_portal_proveedores_integracion';
	url += '&cmd=recuperarimgsrecibo&ids='+tratValue;
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />buscando').show();
	jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {jQuery('#<portlet:namespace />buscando').hide();});
	
}

function <portlet:namespace />proponerPeriodo(elemento){
	  var ele =	jQuery("#<portlet:namespace />"+elemento).val();
	  
	  var e = new Date();
	  e.setMonth(e.getMonth() -1);
	  
	
	  //jQuery("#<portlet:namespace />periodoCarpetaMesAnio").val(e.getMonth()+"_"+ e.getFullYear());
	  
	  jQuery("#<portlet:namespace />"+elemento).val(e.getMonth()+"_"+ e.getFullYear());
}


function <portlet:namespace />zipImgs(){
	
	var carpeta = jQuery("#<portlet:namespace />carpetaMesAnio").val();
	window.location.href ='/txtservlet/?reporte=COMPROBANTES_INTEGRACION_EXPORTAR_IMAGENES'
		+'&carpeta='+carpeta ;	
	
}



var e = new Date();
e.setMonth(e.getMonth() -1);
jQuery("#<portlet:namespace />carpetaMesAnio").val(e.getMonth()+"_"+ e.getFullYear());
</script>
