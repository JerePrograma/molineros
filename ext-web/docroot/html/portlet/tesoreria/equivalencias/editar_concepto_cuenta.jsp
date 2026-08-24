<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
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
<input type="hidden" name="id" value="${concepto.id}"/>
<c:if test="${concepto.id != 0}">
	<input type="hidden" name="ejercicio_hasta" value="${ejercicio_hasta}"/>
	<input type="hidden" name="ejercicio_desde_original" value="${ejercicio_desde_original}"/>
</c:if>
<table style="width: 50%">
	<tr>
		<td><b>Ejercicio:</b></td>
			<c:if test="${concepto.id != 0}">
				<td>
				<select name="ejercicio_desde">
					<% while (DateUtils.compararFechasTruncarEnDia(desde.getTime(), hasta.getTime()) <= 0){%>
						<option value="01/<%=desde.get(Calendar.MONTH)+1%>/<%=desde.get(Calendar.YEAR)%>">01/<%=desde.get(Calendar.MONTH)+1%>/<%=desde.get(Calendar.YEAR)%></option>
					<%	desde.add(Calendar.MONTH, 1);
						}
					%>
				</select><b>&nbsp;-&nbsp;${ejercicio_hasta}</b><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
					
					
			</c:if>
			<c:if test="${concepto.id == 0}">
				<td><select name="ejercicio" id="ejercicio" onchange="actualizarCuentas()">
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
		<td>Concepto:</td>
		<td><input type="text" name="concepto" value="${concepto.descripcion}" size="50"/><a href="javascript:void(0)" onclick="help(event, 'helpConcepto')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
	</tr>
	<% if (rolABMEquivalencias) { %>
	<tr>
		<td>Cuenta:</td>
		<td>
			<select name="cuenta_por_numero" id="cuenta_por_numero">
				<c:forEach items="${PLAN_CUENTAS}" var="cta">
					<option value="${cta.id}"/><c:out value="${cta.numero}"/>-<c:out value="${cta.cuenta}"/></option>
				</c:forEach>
			</select>
			<!-- <select name="cuenta_por_nombre" id="cuenta_por_nombre" onchange="cambioNombre()">
				<c:forEach items="${cuentas_por_nombre}" var="cta2">
					<option value="${cta2.id}"/><c:out value="${cta2.cuenta}"/></option>
				</c:forEach>
			</select>--><a href="javascript:void(0)" onclick="help(event, 'helpCuenta')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
	<tr>
		<td>Cuenta Pasivo:</td>
		<td>
			<select name="cuenta_por_numero_pasivo" id="cuenta_por_numero_pasivo">
				<c:forEach items="${PLAN_CUENTAS}" var="cta3">
					<option value="${cta3.id}"/><c:out value="${cta3.numero}"/>-<c:out value="${cta3.cuenta}"/></option>
				</c:forEach>
			</select>
		<!--  	<select name="cuenta_por_nombre_pasivo" id="cuenta_por_nombre_pasivo" onchange="cambioNombre_pasivo()">
				<c:forEach items="${cuentas_por_nombre}" var="cta4">
					<option value="${cta4.id}"/><c:out value="${cta4.cuenta}"/></option>
				</c:forEach>
			</select>--><a href="javascript:void(0)" onclick="help(event, 'helpCuentaPasivo')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
	<%} %>
	<% if(portlet_name.equals("tesoreria")){%>
		<tr>
			<td colspan="2">Liquidaciones:&nbsp;<input type="checkbox" name="liquidaciones" id="liquidaciones" value="true"/><a href="javascript:void(0)" onclick="help(event, 'helpLiquidaciones')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			</td>
		</tr>
	<%}else if(portlet_name.equals("uoma")){%>
		<tr>
			<td><liferay-ui:message key="seccional" />:</td>
			<td>			
			<input type="hidden" name="liquidaciones" id="liquidaciones" value="false"/>
			<input type="hidden" name="id_seccional_original" id="id_seccional_original" value="${concepto.idSeccional}"/>
			<input type="hidden" name="id_secuencial" id="id_secuencial" value="${concepto.idSecuencial}"/>
			<liferay-util:include page='/html/portlet/uoma/busqueda_seccional.jsp'>
				<liferay-util:param name="id_seccional" value='${concepto.idSeccional}'/>
				<liferay-util:param name="seccional" value='${concepto.seccional}'/>							
			</liferay-util:include>
			</td>
		</tr>	
    <%}else{%>
    	<tr>
			<td colspan="2"><input type="hidden" name="liquidaciones" id="liquidaciones" value="false"/>
			</td>
		</tr>		
	<%}%>
	<tr>
		<td colspan="2">Egreso:&nbsp;<input type="checkbox" name="egreso" id="egreso" value="true"/><a href="javascript:void(0)" onclick="help(event, 'helpEgresos')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
						Va al Subdiario Egreso:<input type="checkbox" name="sub_egreso"  id="sub_egreso" value="true"/><a href="javascript:void(0)" onclick="help(event, 'helpSubEgresos')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		</td>
	</tr>
	<tr>
		<td colspan="2">Ingreso:<input type="checkbox" name="ingreso"  id="ingreso" value="true"/><a href="javascript:void(0)" onclick="help(event, 'helpIngresos')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
						Va al Subdiario Ingreso:<input type="checkbox" name="sub_ingreso"  id="sub_ingreso" value="true"/><a href="javascript:void(0)" onclick="help(event, 'helpSubIngresos')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		</td>
	</tr>
	<tr>
		<td colspan="2"><span id="botonGuardar"><input type="button" value="Guardar" onclick="guargar()"/><a href="javascript:void(0)" onclick="help(event, 'helpGuardar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></span></td>
	</tr>
</table>
</form>
<span id="guardando">
	<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
</span>

<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="volver">
	<%if(portlet_name.equals("farmacia")){%>
		<portlet:param name="struts_action" value="/farmacia/equivalencias_conceptos_cuentas" />
	<%}else if(portlet_name.equals("uoma")){%>		
		<portlet:param name="struts_action" value="/uoma/equivalencias_conceptos_cuentas" />
	<%}else{%>
		<portlet:param name="struts_action" value="/tesoreria/equivalencias_conceptos_cuentas" />
	<%}%>
</portlet:renderURL>
<p><a href="<%= volver %>">Volver</a><a href="javascript:void(0)" onclick="help(event, 'helpVolver')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></p>

<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Es el período de vigencia del concepto y su equivalencia contable. Generalmente, coincide con el ejercicio contable. Se modificará, por ejemplo, en el caso que se desee que la equivalencia cobre vigencia desde un mes en particular del ejercicio, ya que los anteriores son períodos con el análisis concluido y los ajustes efectuados.
</div>
<div id="helpConcepto" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto: Descripción del concepto.
</div>
<div id="helpCuenta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Cuenta: Es la cuenta que le corresponde contablemente a todo movimiento que utilice el concepto tanto en la generación de asientos automáticos como en los reportes con resúmenes por cuenta contable. Por ejemplo: subdiarios de ingresos y egresos.
</div>
<div id="helpCuentaPasivo" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Cuenta Pasivo: Es la cuenta contable que se utilizará como equivalencia para el concepto, en el caso que se trate de una deuda que no es pagada en el mismo mes que fuera contabilizada. Es decir, efectuado el pago con posterioridad del mes de la fecha de recepción del comprobante de dicha deuda. Se utiliza tanto en la generación de asientos automáticos como en los reportes con resúmenes por cuenta contable. Por ejemplo: subdiarios de ingresos y egresos.
</div>
<div id="helpLiquidaciones" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Liquidaciones: Se selecciona en los casos de conceptos que pueden ser utilizados en el módulo de liquidaciones cuando no se detalla por código de prestación del nomenclador.
</div>
<div id="helpEgresos" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Egresos: Se selecciona en los casos de conceptos que pueden ser seleccionados en la carga de comprobantes del módulo de pagos. Por lo general los conceptos de egresos que no son seleccionados, son los que se corresponden a procesos especiales con un desarrollo específico; por ejemplo, los canjes de cheques propios o rechazados de terceros o los créditos de autogestión. Estos casos, si bien son conceptos de egresos, no pueden asignarse a un comprobante presentado por un proveedor. Los casos donde no se selecciona ni "Egreso", ni "Ingreso", ni "Liquidaciones", se trata de conceptos que sólo son utilizados para asociarlos a prestaciones del nomenclador, en la tabla correspondiente.
</div>
<div id="helpIngresos" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ingresos: Se selecciona en los casos de conceptos que pueden ser seleccionados en la carga de recibos del módulo de ingresos. Los casos donde no se selecciona ni "Egreso", ni "Ingreso", ni "Liquidaciones", se trata de conceptos que sólo son utilizados para asociarlos a prestaciones del nomenclador, en la tabla correspondiente.
</div>
<div id="helpSubEgresos" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Va al Subdiario Egreso: Se selecciona en los casos de conceptos de egresos que deben ser considerados para listarse en el subdiario de egresos. Generalmente, todos los conceptos marcados como Egreso.
</div>
<div id="helpSubIngresos" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Va al Subdiario Ingreso: Esta marca es analizada si y solo si, se trata de un ingreso originado en un movimiento bancario. El resto de los ingresos, sin importar lo que se indique, son incluídos en el reporte de subdiario de ingresos.
</div>
<div id="helpGuardar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Guardar: Al seleccionar este botón, se efectúan todos los controles sobre los datos ingresados y se graba en la tabla correspondiente; confirmando así lo ingresado. No será guardado ningún cambio si se abandona la pantalla sin seleccionar este botón.
</div>
<div id="helpVolver" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Volver: Seleccionando este link, se vuelve a la pantalla anterior. Se perderá toda actualización efectuada en el caso que los cambios no se guarden previamente.
</div>


<script type="text/javascript">
<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
<%}%>
function guargar(){
		
	jQuery("#botonGuardar").toggle();
		jQuery("#guardando").toggle();
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_concepto';
		document.<portlet:namespace />editar_concepto_plan.method = 'post';
		submitForm(document.<portlet:namespace />editar_concepto_plan, url);
}
function cambioNumero(){
	jQuery("#cuenta_por_nombre").val(jQuery("#cuenta_por_numero").val());	
}
function cambioNombre(){
	jQuery("#cuenta_por_numero").val(jQuery("#cuenta_por_nombre").val());
}
function cambioNumero_pasivo(){
	jQuery("#cuenta_por_nombre_pasivo").val(jQuery("#cuenta_por_numero_pasivo").val());	
}
function cambioNombre_pasivo(){
	jQuery("#cuenta_por_numero_pasivo").val(jQuery("#cuenta_por_nombre_pasivo").val());
}

	jQuery(document).ready(function() {
		jQuery("#guardando").hide();
		jQuery("#cuenta_por_nombre").val("${concepto.planCuentas.id}");	
		jQuery("#cuenta_por_numero").val("${concepto.planCuentas.id}");
		jQuery("#cuenta_por_nombre_pasivo").val("${concepto.planCuentasPasivo.id}");	
		jQuery("#cuenta_por_numero_pasivo").val("${concepto.planCuentasPasivo.id}");
		jQuery('#liquidaciones').attr('checked', ${concepto.liquidaciones});
		jQuery('#egreso').attr('checked', ${concepto.egreso});
		jQuery('#ingreso').attr('checked', ${concepto.ingreso});
		jQuery('#sub_egreso').attr('checked', ${concepto.subEgreso});
		jQuery('#sub_ingreso').attr('checked', ${concepto.subIngreso});
	});

	function actualizarCuentas(){
		var ejercicio=jQuery("#ejercicio").val();	
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_plan_cuentas_para_fecha'
		    + '&ejercicio=' +ejercicio;
		url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM%>';
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				jQuery('#cuenta_por_numero').find('option').remove();
				jQuery('#cuenta_por_numero_pasivo').find('option').remove();
				for(var i =0;i< obj.cuentas.length; i++){
					jQuery('#cuenta_por_numero').append('<option value="'+obj.cuentas[i].id+'">'+obj.cuentas[i].numero + '-' + obj.cuentas[i].cuenta +'</option>');
					jQuery('#cuenta_por_numero_pasivo').append('<option value="'+obj.cuentas[i].id+'">'+obj.cuentas[i].numero + '-' + obj.cuentas[i].cuenta +'</option>');
				}                                                                                                                                                                                                                                                            
			}
		});		
	}	
</script>

