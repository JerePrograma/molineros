<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%
	String portlet_name = ParamUtil.getString(request, "portlet_name");

	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}
	boolean rolABMEquivalencias = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_EQUIVALENCIAS)||portlet_name.equals("farmacia")||portlet_name.equals("uoma");
	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	String ejDesde = (String) request.getAttribute("ejercicio_desde");
	String ejHasta = (String) request.getAttribute("ejercicio_hasta");
	Calendar  desde = null;
	Calendar  hasta = null;
	if (ejDesde !=null){
		desde = Calendar.getInstance();
		desde.setTime(format.parse(ejDesde));
		hasta = Calendar.getInstance();
		hasta.setTime(format.parse(ejHasta));
	}
	String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
%>
<form action="" method="post" name="<portlet:namespace />editar_concepto_plan" >
<input type="hidden" name="id" value="${tipoMovBcrio.id_tipo_mov}"/>
<c:if test="${tipoMovBcrio.id_tipo_mov != 0}">
	<input type="hidden" name="ejercicio_desde_original" value="${ejercicio_desde_original}"/>
	<input type="hidden" name="ejercicio_hasta" value="${ejercicio_hasta}"/>
</c:if>
<table style="width: 50%">
	<tr>
		<td><b>Ejercicio:</b></td>
			<c:if test="${tipoMovBcrio.id_tipo_mov != 0}">
				<td>
				<select name="ejercicio_desde">
					<% while (DateUtils.compararFechasTruncarEnDia(desde.getTime(), hasta.getTime()) <= 0){%>
						<option value="01/<%=desde.get(Calendar.MONTH)+1%>/<%=desde.get(Calendar.YEAR)%>">01/<%=desde.get(Calendar.MONTH)+1%>/<%=desde.get(Calendar.YEAR)%></option>
					<%	desde.add(Calendar.MONTH, 1);
						}
					%>
				</select><b>&nbsp;-&nbsp;${ejercicio_hasta}</b><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
			</c:if>
			<c:if test="${tipoMovBcrio.id_tipo_mov == 0}">
				<td><select name="ejercicio" id="ejercicio" onchange="actualizarConceptos()">
					<%	Calendar cal = Calendar.getInstance();
						int hastaAnio = cal.get(Calendar.YEAR);
						if(portlet_name.equals("farmacia")){
							if (cal.get(Calendar.MONTH) < Calendar.JULY){
								hastaAnio--;
							}
						}else{
							if (cal.get(Calendar.MONTH) < Calendar.AUGUST){
								hastaAnio--;
							}
						}
						for (int i = 2000; i<=hastaAnio; i++){  %>
					<option value="<%=i%>-<%=i+1%>" <%if (i == hastaAnio) { %>
						selected="selected" <%} %>>
						<%if(portlet_name.equals("farmacia")){%>
								Julio&nbsp;<%=i %>&nbsp;-&nbsp;Junio&nbsp;<%= i+1 %></option>
							<%}else{%>
								Agosto&nbsp;<%=i %>&nbsp;-&nbsp;Julio&nbsp;<%= i+1 %></option>
						<%}
					} %>
					</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				</td>
			</c:if>
	</tr>	
	<tr>
		<td>Descripcion:</td>
		<td><input type="text" name="concepto" value="${tipoMovBcrio.descripcion}" size="50"/><a href="javascript:void(0)" onclick="help(event, 'helpDescripcion')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
	</tr>
	<% if (rolABMEquivalencias) { %>
	<tr>
		<td>Concepto:</td>
		<td>
			<select name="id_concepto" id="id_concepto">
				<option value="-1"/></option>
				<c:forEach items="${conceptos}" var="conc">
					<option value="${conc.id}"/><c:out value="${conc.descripcion}"/></option>
				</c:forEach>
			</select><a href="javascript:void(0)" onclick="help(event, 'helpConcepto')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
	<%} %>
	<tr>
		<td colspan="2"><span id="botonGuardar"><input type="button" value="Guardar" onclick="guargar()"/><a href="javascript:void(0)" onclick="help(event, 'helpGuardar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></span></td>
	</tr>
</table>
</form>
<span id="guardando">
	<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
</span>
<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="volver">
	<%if(portlet_name.equals("farmacia")){%>
		<portlet:param name="struts_action" value="/farmacia/equivalencias_tipos_mov_bcrios" />
	<%}else if(portlet_name.equals("uoma")){{%>
		<portlet:param name="struts_action" value="/uoma/equivalencias_tipos_mov_bcrios" />
	<%}%>else{%>
		<portlet:param name="struts_action" value="/tesoreria/equivalencias_tipos_mov_bcrios" />
	<%}%>
</portlet:renderURL>
<p><a href="<%= volver %>">Volver</a><a href="javascript:void(0)" onclick="help(event, 'helpVolver')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></p>

<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Es el per�odo de vigencia del tipo de movimiento y su concepto. Generalmente, coincide con el ejercicio contable. Se modificar�, por ejemplo, en el caso que se desee que cobre vigencia desde un mes en particular del ejercicio, ya que los anteriores son per�odos con el an�lisis concluido y los ajustes efectuados.
</div>
<div id="helpDescripcion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Descripci�n: descripci�n del tipo de movimiento bancario.
</div>
<div id="helpConcepto" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto: concepto asignado al movimiento bancario. En el caso de no asignar un concepto, los movimientos bancarios de este tipo s�lo tendr�n efecto en el libro banco/caja y no se ver�n reflejados en los subdiarios ni en los asientos autom�ticos. Si se asigna un concepto, los movimientos de este tipo se incluir�n, o no, en alguno de los subdiarios, dependiendo de lo que se indique en la tabla de conceptos para el concepto asignado en los campos: "va en subdiario de ingresos" y "va en subdiario de egresos".
</div>
<div id="helpGuardar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Guardar: Al seleccionar este bot�n, se efect�an todos los controles sobre los datos ingresados y se graba en la tabla correspondiente; confirmando as� lo ingresado. No ser� guardado ning�n cambio si se abandona la pantalla sin seleccionar este bot�n.
</div>
<div id="helpVolver" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Volver: Seleccionando este link, se vuelve a la pantalla anterior. Se perder� toda actualizaci�n efectuada en el caso que los cambios no se guarden previamente.
</div>



<script type="text/javascript">
<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
<%}%>
function guargar(){
		jQuery("#botonGuardar").toggle();
		jQuery("#guardando").toggle();		
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_equivalencias_tipos_mov_bcrios';
		document.<portlet:namespace />editar_concepto_plan.method = 'post';
		submitForm(document.<portlet:namespace />editar_concepto_plan, url);
}

	jQuery(document).ready(function() {
		jQuery("#guardando").hide();
		jQuery("#id_concepto").val("${tipoMovBcrio.concepto.id}");	
	});
	
	function actualizarConceptos(){
		var ejercicio=jQuery("#ejercicio").val();	
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_conceptos_para_ejercicio'
		    + '&ejercicio=' +ejercicio;
		url+='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				jQuery('#id_concepto').find('option').remove();
				for(var i =0;i< obj.conceptos.length; i++){
					jQuery('#id_concepto').append('<option value="'+obj.conceptos[i].id+'">'+obj.conceptos[i].descripcion+'</option>');
				}                                                                                                                                                                                                                                                            
			}
		});		
	}	
	
</script>

