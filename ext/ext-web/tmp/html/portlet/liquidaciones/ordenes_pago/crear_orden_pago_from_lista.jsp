<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@page import="java.util.HashMap"%>
<%
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaFin = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
		
		
 		boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_OP);
 		boolean rolVER = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_VER_OP);
 		
 		Calendar fecha = CalendarFactoryUtil.getCalendar();
 		fecha.setTime(new Date());
 		
 		if (rolABM) {
 			rolVER = true;
 		}
		
 		List<CuentaBancaria> ctas = (List<CuentaBancaria>) request.getSession().getAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);
 		if (ctas == null) {
 		    ctas = TraeListasServiceUtil.getCtasBcrias();
 		    request.getSession().setAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION, ctas);
 		}
 		
 		
 		List<Seccional> seccionales =TraeListasServiceUtil.getSeccionales();
 		HashMap<Integer,String>mapSeccionales= new HashMap<Integer,String>();
 		for(Seccional s:seccionales){
 			mapSeccionales.put(s.getId(),s.getDescripcion());
 		}
 		
 		List<ReintegroList> pendientes = new ArrayList<ReintegroList>();
 		pendientes =  OrdenPagoServiceUtil.getReintegrosLists(null, null,null);
 		List<ReintegroList> pendientesFarmacia =  OrdenPagoServiceUtil.getReintegrosFarmaciaLists(null, null,null);
 		if(pendientesFarmacia!=null) pendientes.addAll(pendientesFarmacia);
 		for(ReintegroList p:pendientes){
 			p.getSeccional().setDescripcion(mapSeccionales.get(p.getSeccional().getId()));
 		}
 		
 		

 		Collections.sort(pendientes,
				new Comparator<ReintegroList>() {
					public int compare(ReintegroList o1,
							ReintegroList o2) {
						return o1.getSeccional().getDescripcion().compareTo(
								o2.getSeccional().getDescripcion());
					}
				});
 		
%>
<% if(rolABM) { %>
<style>
.scrollable-list-container {
  height: 200px; /* Set a fixed height for the container */
  overflow-y: auto; /* Enable vertical scrolling when content overflows */
  border: 1px solid #ccc; /* Optional: Add a border for visual clarity */
  padding: 10px; /* Optional: Add padding */
}
</style>
		<fieldset class="block-labels">
		<legend><liferay-ui:message key="crear-op-from-lista" /></legend>
		     
				<table class="lfr-table">
					<tr>
						<td>
							<select name="<portlet:namespace/>tipo_reintegro" id="<portlet:namespace/>tipo_reintegro">
							        <option selected="selected" value="TODOS">AMBAS</option>
									<option >PRESTACIONAL</option>
									<option>FARMACIA</option>
									
							</select>
						</td>
						<td><label><liferay-ui:message key="seccional" />:</label></td>
						<td>
							<div  id="<portlet:namespace/>div_seccional">
								<liferay-util:include page="/html/portlet/liquidaciones/busqueda_seccional.jsp">
									<liferay-util:param name="esEdicion" value="true" />
								</liferay-util:include>
							</div>
						</td>
						<td><label><liferay-ui:message key="entre-fechas" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaDesdeDia1"
							dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
							monthParam="fechaDesdeMes1"
							monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
							yearParam="fechaDesdeAnio1"
							yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 50 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
						<td>-</td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaHastaDia2"
							dayValue="<%= fechaFin.get(Calendar.DATE) %>" 
							monthParam="fechaHastaMes2"
							monthValue="<%= fechaFin.get(Calendar.MONTH) %>"				
							yearParam="fechaHastaAnio2"
							yearValue="<%= fechaFin.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -50 %>"	
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaFin.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>		
						<td><input type="button" value="<liferay-ui:message key="buscar-lista-pago" />" onClick="<portlet:namespace />altaOPFromLista();" /></td>
					</tr>
				</table>
				<br>
				<table class="lfr-table">
		          <tr><td widht="80%"> 
				    <div align="center" id="<portlet:namespace />busquedaListasReintegrosDiv"></div>
				  </td>
				  <td widht="20%">
				    <div class="scrollable-list-container">
                      <ul>
                      <% for(ReintegroList p : pendientes){ %>
                           <li><%= p.getSeccional().getDescripcion() + " (" +p.getSeccional().getId() + ") --  " + 
                                   p.getTipo() + " Nro " + p.getNroLista()%></li>
                      <%} %>
                      </ul>
                    </div> 
				   </td>
				  </tr>
				</table>  
				<br>     
		</fieldset>	
		<fieldset class="block-labels">
			<legend>
				Crear OPs desde Archivo de Liquidaciones
			</legend>
		
				<table class="lfr-table">
				<tr>
					<td>
						<input type="file" name="archivo" id="archivo"/>
					</td>
					<td colspan="6" align="center">
						<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadFileLiq();return false;"/>
					</td>
					
					
					<td> &nbsp;&nbsp;Forma de pago</td>
					
					<td>
						<span id="<portlet:namespace />spanctabcria2">
						<select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria">
							<% 	for (CuentaBancaria cta : ctas) { %> 
							<%	if( cta.getEntidad().equals("O")){%>
									<option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 10) {%> selected="selected" <%} %>><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
							<%}} %>
						</select>
						</span>
					</td>
					
					
				</tr>
				<tr>
				<td colspan="12" align="left">
				    El archivo debe ser de formato Excel(xls) en cuya primer columna debe figurar el nro de liquidación. El mismo no debe tener encabezado.
				</td>
				</tr>
				</table>
		</fieldset>
<!-- 			
		</form>
		<form action="" method="post" name="<portlet:namespace />fmUpload" id="<portlet:namespace />fmUpload" enctype="multipart/form-data">
 -->	
 
 <!-- 
			<fieldset class="block-labels">
			<legend>
				<liferay-ui:message key="crear-op-desde-archivo-farmacia" />
			</legend>
		
				<table class="lfr-table">
				<tr>
					<td colspan="6" align="center">
						<input type="file" name="zipFile"/>
					</td>
				</tr>
				<tr>
					<td colspan="6" align="center">
						<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadOrdenPago();return false;"/>
					</td>
				</tr>
				</table>
			</fieldset>
 -->		
<!--  			
		</form>
-->		
		<%} %>

<script type="text/javascript">

</script>