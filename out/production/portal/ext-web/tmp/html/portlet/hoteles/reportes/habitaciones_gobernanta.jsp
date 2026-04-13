<%@include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/hoteles/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "hoteles";
	}
	
	String ptoVtaAfip="00030";

	try{
		ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
	}catch(Exception e){
		//ptoVtaAfip="0000";
		ptoVtaAfip="00030";
	}
	
	Calendar current = CalendarFactoryUtil.getCalendar();
	
	//List<ProductoCategoria> categorias =  HotelesServiceUtil.getProductosCategorias(ptoVtaAfip);
	//session.setAttribute(WebKeysHoteles.CATEGORIAS_HOTEL,categorias);
	
%>

<form action="" method="get" name="<portlet:namespace />fm1" id="<portlet:namespace />fm1" enctype="multipart/form-data">
    <input name="<portlet:namespace />id_hotel"  id="<portlet:namespace />id_hotel" type="hidden"	value="<%=ptoVtaAfip%>" /> 
	<fieldset class="block-labels">
		<legend>Habitaciones para Gobernanta</legend>
		
		<table class="lfr-table">
		  <tr>
		   <td><label><liferay-ui:message key="anio"/>:</label></td>
		   <td><select name="<portlet:namespace />anio_filtro"  id="<portlet:namespace />anio_filtro" >
		                <option value="<%=current.get(Calendar.YEAR)%>"><%=current.get(Calendar.YEAR)%>	</option>
						<option value="<%=current.get(Calendar.YEAR)-1%>"><%=current.get(Calendar.YEAR)-1%>	</option>
				 </select>
		   </td>	 
		   
		   <td><label><liferay-ui:message key="fecha"/>:</label></td>
		   <td colspan="2">
				<liferay-ui:input-date
						dayParam="fechaDesdeDiaFiltro"
						dayValue="<%=current.get(Calendar.DATE) %>" 
						dayNullable="<%= false %>"
						monthParam="fechaDesdeMesFiltro"
						monthValue="<%= current.get(Calendar.MONTH) %>"	
						monthNullable="<%= false %>"			
						yearParam="fechaDesdeAnioFiltro"
						yearValue="<%= current.get(Calendar.YEAR) %>"
						yearNullable="<%= false %>"
						yearRangeStart="<%= current.get(Calendar.YEAR) - 1 %>"
						yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
						firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
						disabled="false" />
			</td>
			<td>
				<fieldset class="block-labels"><legend>N° de Pisos</legend>
				<table class="lfr-table" style="border-spacing: 2px; border-collapse: separate;">
					<tr>
						<td style="background-color:#AEB6BF" valign="middle" >
							Piso 1<input type="checkbox" id="<portlet:namespace />piso1Chk" name="<portlet:namespace />piso1Chk" />
						</td>
						<td style="background-color:#AEB6BF" valign="middle" >
							Piso 2<input type="checkbox" id="<portlet:namespace />piso2Chk" name="<portlet:namespace />piso1Chk" />
						</td>
						<td style="background-color:#AEB6BF" valign="middle" >
							Piso 3<input type="checkbox" id="<portlet:namespace />piso3Chk" name="<portlet:namespace />piso3Chk" />
						</td>
						<td style="background-color:#AEB6BF" valign="middle" >
							Piso 4<input type="checkbox" id="<portlet:namespace />piso4Chk" name="<portlet:namespace />piso4Chk" />
						</td>
						<td style="background-color:#AEB6BF" valign="middle" >
							Piso 5<input type="checkbox" id="<portlet:namespace />piso5Chk" name="<portlet:namespace />piso5Chk" />
						</td>
						<td style="background-color:#AEB6BF" valign="middle" >
							Piso 6<input type="checkbox" id="<portlet:namespace />piso6Chk" name="<portlet:namespace />piso6Chk" />
						</td>
						<td style="background-color:#AEB6BF" valign="middle" >
							Piso 7<input type="checkbox" id="<portlet:namespace />piso7Chk" name="<portlet:namespace />piso7Chk" />
						</td>
						<td style="background-color:#AEB6BF" valign="middle" >
							Piso 8<input type="checkbox" id="<portlet:namespace />piso8Chk" name="<portlet:namespace />piso8Chk" />
						</td>
						<td style="background-color:#AEB6BF" valign="middle" >
							Piso 9<input type="checkbox" id="<portlet:namespace />piso9Chk" name="<portlet:namespace />piso9Chk" />
						</td>
						<td style="background-color:#AEB6BF" valign="middle" >
							Piso 10<input type="checkbox" id="<portlet:namespace />piso10Chk" name="<portlet:namespace />piso10Chk" />
						</td>
						<td>
							<a href="#" onclick="<portlet:namespace />seleccionarTodos();" >Todos</a>
							<a href="#" onclick="<portlet:namespace />desSeleccionarTodos();" >Ninguno</a>
						</td>
					</tr>
				</table>
				</fieldset>
			</td>
		  </tr>
		  <tr>
		    <td colspan="4">&nbsp;</td>
		  </tr>
		  <tr><td colspan="4"><input type="button" value="Imprimir" onClick="<portlet:namespace />imprimir();"/></td></tr>
		</table>

	</fieldset>
</form>		

<script type="text/javascript">

	function <portlet:namespace />imprimir() {
		var anio=jQuery('#<portlet:namespace />anio_filtro').val();

		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDiaFiltro').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMesFiltro').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnioFiltro').val();
		var piso1=jQuery('#<portlet:namespace />piso1Chk').is(':checked');
		var piso2=jQuery('#<portlet:namespace />piso2Chk').is(':checked');
		var piso3=jQuery('#<portlet:namespace />piso3Chk').is(':checked');
		var piso4=jQuery('#<portlet:namespace />piso4Chk').is(':checked');
		var piso5=jQuery('#<portlet:namespace />piso5Chk').is(':checked');
		var piso6=jQuery('#<portlet:namespace />piso6Chk').is(':checked');
		var piso7=jQuery('#<portlet:namespace />piso7Chk').is(':checked');
		var piso8=jQuery('#<portlet:namespace />piso8Chk').is(':checked');
		var piso9=jQuery('#<portlet:namespace />piso9Chk').is(':checked');
		var piso10=jQuery('#<portlet:namespace />piso10Chk').is(':checked');

		
		
		window.location.href ='/pdfservlet/?accion=reportediariohabitacionesgobernanta'
			+'&anio='+anio
			+'&fechadesdedia='+fechaDesdeDia
			+'&fechadesdemes='+fechaDesdeMes
			+'&fechadesdeanio='+fechaDesdeAnio
			+'&piso1='+piso1
			+'&piso2='+piso2
			+'&piso3='+piso3
			+'&piso4='+piso4
			+'&piso5='+piso5
			+'&piso6='+piso6
			+'&piso7='+piso7
			+'&piso8='+piso8
			+'&piso9='+piso9
			+'&piso10='+piso10;

	}
	
	function <portlet:namespace />seleccionarTodos() {
				
		for (i=0;i<document.<portlet:namespace />fm1.elements.length;i++){
			
		      if(document.<portlet:namespace />fm1.elements[i].type == "checkbox"){
		         document.<portlet:namespace />fm1.elements[i].checked=1
		      } 
		}
	}
	function <portlet:namespace />desSeleccionarTodos() {
		
		for (i=0;i<document.<portlet:namespace />fm1.elements.length;i++){
			
		      if(document.<portlet:namespace />fm1.elements[i].type == "checkbox"){
		         document.<portlet:namespace />fm1.elements[i].checked=0
		      } 
		}
	}
	
</script>

