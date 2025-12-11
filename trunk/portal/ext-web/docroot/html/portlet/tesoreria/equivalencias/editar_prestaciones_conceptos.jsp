<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%
	boolean rolABMNomenclador = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_EQUIVALENCIAS_PRESTACIONES);
	boolean rolABMEquivalencias = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_EQUIVALENCIAS);

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
%>
<form action="" method="post" name="<portlet:namespace />editar_prestacion_concepto" >
<input type="hidden" name="idHonorariosAmbulatorio" value="${prestacionConcepto.idHonorariosAmbulatorio}"/>
<input type="hidden" name="idHonorariosInternacion" value="${prestacionConcepto.idHonorariosInternacion}"/>
<input type="hidden" name="idGastosAmbulatorio" value="${prestacionConcepto.idGastosAmbulatorio}"/>
<input type="hidden" name="idGastosInternacion" value="${prestacionConcepto.idGastosInternacion}"/>
<input type="hidden" name="id_prestacion" value="${prestacionConcepto.prestacion.id}"/>
<c:if test="${not empty prestacionConcepto.prestacion and prestacionConcepto.prestacion.id != 0}">
	<input type="hidden" name="ejercicio_desde_original" value="${ejercicio_desde_original}"/>
	<input type="hidden" name="ejercicio_hasta" value="${ejercicio_hasta}"/>
</c:if>
<table style="width: 50%">
	<tr>
		<td><b>Ejercicio:</b></td>
		<c:if test="${not empty prestacionConcepto.prestacion and prestacionConcepto.prestacion.id != 0}">
				<td>
				<select name="ejercicio_desde">
					<% while (DateUtils.compararFechasTruncarEnDia(desde.getTime(), hasta.getTime()) <= 0){%>
						<option value="01/<%=desde.get(Calendar.MONTH)+1%>/<%=desde.get(Calendar.YEAR)%>">01/<%=desde.get(Calendar.MONTH)+1%>/<%=desde.get(Calendar.YEAR)%></option>
					<%	desde.add(Calendar.MONTH, 1);
						}
					%>
				</select><b>&nbsp;-&nbsp;${ejercicio_hasta}</b><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
			</c:if>
			<c:if test="${empty prestacionConcepto.prestacion or prestacionConcepto.prestacion.id == 0}">
				<td><select name="ejercicio" id="ejercicio" onchange="actualizarConceptos()">
					<%	Calendar cal = Calendar.getInstance();
						int hastaAnio = cal.get(Calendar.YEAR);
						if (cal.get(Calendar.MONTH) < Calendar.AUGUST){
							hastaAnio--;
						}
						for (int i = 2000; i<=hastaAnio; i++){  %>
					<option value="<%=i%>-<%=i+1%>" <%if (i == hastaAnio) { %>
						selected="selected" <%} %>>
						Agosto&nbsp;<%=i %>&nbsp;-&nbsp;Julio&nbsp;<%= i+1 %></option>
					<%} %>
					</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				</td>
			</c:if>
	</tr>	
	</tr>
		<tr>
		<td>Nomenclador:</td>
		<td>
			<c:if test="${not empty prestacionConcepto.prestacion and prestacionConcepto.prestacion.id != 0}">
				<select disabled="disabled" id="tipo_nomenclador">
					<option value="1">NOM. NAC.ODONTOLOGICO</option>
					<option value="2">NOM. NAC.PREST.MEDICAS</option>
					<option value="3">NOM. PROPIO</option>
					<option value="4">NOM. NAC ANALISIS CLINICOS</option>
					<option value="5">NOM. NAC P.M.O.</option>
					<option value="6">NOM. NAC QUIRURGICO</option>
					<option value="7">DEBITOS VARIOS</option>
					<option value="8">NOM.DISCAPACIDAD</option>
					<option value="9">MEDICAMENTOS</option>
					<option value="10">PROTESIS E INSUMOS</option>
				</select><a href="javascript:void(0)" onclick="help(event, 'helpNomenclador')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				<input type="hidden" name="tipo_nomenclador" value="${prestacionConcepto.idTipoNomenclador}"/>
				<input type="hidden" name="nombre_medicamento" value="${prestacionConcepto.prestacion.descripcion}"/>
			</c:if>
			<c:if test="${empty prestacionConcepto.prestacion or prestacionConcepto.prestacion.id == 0}">
				<select id="tipo_nomenclador" name="tipo_nomenclador" onchange="cambioTipo()">
					<% if (rolABMEquivalencias){ %><option value="3">NOM. PROPIO</option><%} %>
					<option value="9">MEDICAMENTOS</option>
					<option value="10">PROTESIS E INSUMOS</option>
				</select><a href="javascript:void(0)" onclick="help(event, 'helpNomenclador')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			</c:if>
		</td>
	</tr>	
	<tr>
		<td>Codigo de Prestacion:</td>
		<td>
			<c:if test="${prestacionConcepto.idTipoNomenclador == 3}">
				<input type="text" name="codigo" id="codigo" value="${prestacionConcepto.prestacion.codigo}" maxlength="10" size="50"/>
			</c:if>
			<c:if test="${prestacionConcepto.idTipoNomenclador != 3}">
				${prestacionConcepto.prestacion.codigo}
				<input type="hidden" name="codigo" id="codigo" value="${prestacionConcepto.prestacion.codigo}" size="50"/>
			</c:if>
			<a href="javascript:void(0)" onclick="help(event, 'helpCodigo')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
	<tr>
		<td>Descripción de Prestacion:</td>
		<td>
			<span id="no_busqueda_medicamentos">
				<c:if test="${prestacionConcepto.idTipoNomenclador == 3}">
						<input type="text" name="descripcion" id="descripcion" value="${prestacionConcepto.prestacion.descripcion}" size="50"/>
					</c:if>
				<c:if test="${prestacionConcepto.idTipoNomenclador != 3}">
					${prestacionConcepto.prestacion.descripcion}
					<input type="hidden" name="descripcion" id="descripcion" value="${prestacionConcepto.prestacion.descripcion}" size="50"/>
				</c:if>
			</span>
			
			<span id="busqueda_medicamentos">
			<c:if test="${not empty prestacionConcepto.prestacion and prestacionConcepto.prestacion.id != 0}">
				${prestacionConcepto.prestacion.descripcion}
					<input type="hidden" name="descripcion" id="descripcion" value="${prestacionConcepto.prestacion.descripcion}" size="50"/>
			</c:if>
			<c:if test="${empty prestacionConcepto.prestacion or prestacionConcepto.prestacion.id == 0}">
				<liferay-util:include page="/html/portlet/utils/medicamentos/busqueda_medicamentos.jsp">
					<liferay-util:param name="search_url" value="/tesoreria/buscar_medicamentos" />
					<liferay-util:param name="troquel" value='' />
					<liferay-util:param name="nombre_medicamento" value='' />
					<liferay-util:param name="id_medicamento" value='' />
					<liferay-util:param name="esEditable" value='true' />
					<liferay-util:param name="mostrar_con_presentacion" value='true' />
				</liferay-util:include>
			</c:if>
			</span>
		</td>
	</tr>
	<tr>
		<td>Coeficiente Honorarios:</td>
		<td>
			<c:if test="${prestacionConcepto.idTipoNomenclador == 3}">
					<input type="text" name="coefHonoarios"  id="coefHonoarios"  value="${prestacionConcepto.coeficienteHonorarios}" size="50"/>
				</c:if>
			<c:if test="${prestacionConcepto.idTipoNomenclador != 3}">
				${prestacionConcepto.coeficienteHonorarios}
				<input type="hidden" name="coefHonoarios"  id="coefHonoarios"  value="${prestacionConcepto.coeficienteHonorarios}" size="50"/>
			</c:if>
			<a href="javascript:void(0)" onclick="help(event, 'helpCoeficienteHonorarios')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
	<tr>
		<td>Coeficiente Gastos:</td>
		<td>
			<c:if test="${prestacionConcepto.idTipoNomenclador == 3}">
					<input type="text" name="coefGastos" id="coefGastos" value="${prestacionConcepto.coeficienteGastos}" size="50"/>
				</c:if>
			<c:if test="${prestacionConcepto.idTipoNomenclador != 3}">
				${prestacionConcepto.coeficienteGastos}
				<input type="hidden" name="coefGastos" id="coefGastos" value="${prestacionConcepto.coeficienteGastos}" size="50"/>
			</c:if>
			<a href="javascript:void(0)" onclick="help(event, 'helpCoeficienteGastos')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
		<tr>
		<td>Precio:</td>
		<td>
			<c:if test="${prestacionConcepto.idTipoNomenclador == 3}">
					<input type="text" name="importe" id="importe" value="${prestacionConcepto.prestacion.importe}"/>
				</c:if>
			<c:if test="${prestacionConcepto.idTipoNomenclador != 3}">
				${prestacionConcepto.prestacion.importe}
				<input type="hidden" name="importe" id="importe" value="${prestacionConcepto.prestacion.importe}"/>
			</c:if>
		</td>
	</tr>
	<tr>
		<td>Tipo:</td>
		<td>
			<c:if test="${prestacionConcepto.idTipoNomenclador != 3}">
				<c:if test="${prestacionConcepto.prestacion.marca_rein_liq == 3}">
				Prestacional
				</c:if>
				<c:if test="${prestacionConcepto.prestacion.marca_rein_liq == 4}">
				Protesis
				</c:if>
				<c:if test="${prestacionConcepto.prestacion.marca_rein_liq == 5}">
				Ortopedia / Ortodoncia
				</c:if>
				<c:if test="${prestacionConcepto.prestacion.marca_rein_liq == 6}">
				Discapacidad
				</c:if>
				<input type="hidden" name="marca_rein_liq" id="marca_rein_liq" value="${prestacionConcepto.prestacion.marca_rein_liq}" size="50"/>
			</c:if>
			<c:if test="${prestacionConcepto.idTipoNomenclador == 3}">
				<select name="marca_rein_liq" id="marca_rein_liq">
					<option value="3">Prestacional</option>
					<option value="4">Protesis</option>
					<option value="5">Ortopedia / Ortodoncia</option>
					<option value="6">Discapacidad</option>
				</select>
			</c:if>
		</td>
	</tr>
	<tr>
		<td>Honorarios Ambulatorio Anterior:</td>
		<td>${prestacionConcepto.honorariosAmbulatorio.descripcion}</td>
	</tr>
	<tr>
		<td>Nuevo:&nbsp;</td>
		<td>
			<select name="honorarios_ambulatorio" id="honorarios_ambulatorio">
				<option value="-1"/></option>
				<c:forEach items="${conceptos}" var="con">
					<option value="${con.id}"/><c:out value="${con.descripcion}"/></option>
				</c:forEach>
			</select><a href="javascript:void(0)" onclick="help(event, 'helpHA')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
	<tr>
		<td>Honorarios Internación Anterior:</td>
		<td>${prestacionConcepto.honorariosInternacion.descripcion}</td>
	</tr>
	<tr>
		<td>Nuevo:</td>
		<td>
			<select name="honorarios_internacion" id="honorarios_internacion">
				<option value="-1"/></option>
				<c:forEach items="${conceptos}" var="con2">
					<option value="${con2.id}"/><c:out value="${con2.descripcion}"/></option>
				</c:forEach>
			</select><a href="javascript:void(0)" onclick="help(event, 'helpHI')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
	<tr>
		<td>Gastos Ambulatorio Anterior:</td>
		<td>${prestacionConcepto.gastosAmbulatorio.descripcion}</td>
	</tr>
	<tr>
		<td>Nuevo:</td>
		<td>
			<select name="gastos_ambulatorio" id="gastos_ambulatorio">
				<option value="-1"/></option>
				<c:forEach items="${conceptos}" var="con3">
					<option value="${con3.id}"/><c:out value="${con3.descripcion}"/></option>
				</c:forEach>
			</select><a href="javascript:void(0)" onclick="help(event, 'helpGA')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
	<tr>
		<td>Gastos Internación Anterior:</td>
		<td>${prestacionConcepto.gastosInternacion.descripcion}</td>
	</tr>
	<tr>
		<td>Nuevo:</td>
		<td>
			<select name="gastos_internacion" id="gastos_internacion">
				<option value="-1"/></option>
				<c:forEach items="${conceptos}" var="con4">
					<option value="${con4.id}"/><c:out value="${con4.descripcion}"/></option>
				</c:forEach>
			</select><a href="javascript:void(0)" onclick="help(event, 'helpGI')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
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
<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="volver">
	<portlet:param name="struts_action" value="/tesoreria/equivalencias_prestaciones_conceptos" />
</portlet:renderURL>
<p><a href="<%= volver %>">Volver</a><a href="javascript:void(0)" onclick="help(event, 'helpVolver')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></p>


<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Es el período de vigencia de la prestación y su equivalencia con los conceptos. Generalmente, coincide con el ejercicio contable. Se modificará, por ejemplo, en el caso que se desee que la equivalencia cobre vigencia desde un mes en particular del ejercicio, ya que los anteriores son períodos con el análisis concluido y los ajustes efectuados.
</div>
<div id="helpNomenclador" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Nomenclador: Es el tipo de nomenclador al que pertenece la prestación.
</div>
<div id="helpCodigo" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Código de Prestación: Código asignado a la prestación, según nomenclador nacional, nomenclador propio, etc.
</div>
<div id="helpCoeficienteHonorarios" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Coeficiente Honorarios: es el coeficiente que da la proporción a asignar como honorarios, sobre el importe total de la prestación. Al sumarse al coeficiente de gastos debe dar 1.
</div>
<div id="helpCoeficienteGastos" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Coeficiente Gastos: es el coeficiente que da la proporción a asignar como gasto, sobre el importe total de la prestación. Al sumarse al coeficiente de honorarios debe dar 1.
</div>
<div id="helpHA" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Honorarios Ambulatorio: es la equivalencia para la prestación con el concepto de egreso que le corresponda, pero sólo para el importe de los honorarios y en el caso de efectuarse la práctica en forma ambulatoria. En el caso de una prestación que sólo tiene gastos, se indicará el mismo concepto que el que se asigne a "gastos ambulatorio". También, en el caso que sólo sea una prestación que se use sólo en internación, se indicará el mismo concepto que se asigne a "honorarios internación".
</div>
<div id="helpHI" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Honorarios Internación: es la equivalencia para la prestación con el concepto de egreso que le corresponda, pero sólo para el importe de los honorarios y en el caso de efectuarse la práctica por una internación. En el caso de una prestación que sólo tiene gastos, se indicará el mismo concepto que el que se asigne a "gastos internación". También, en el caso que sólo sea una prestación que se use sólo en forma ambultoria, se indicará el mismo concepto que se asigne a "honorarios ambulatorio".
</div>
<div id="helpGA" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Gastos Ambulatorio: es la equivalencia para la prestación con el concepto de egreso que le corresponda, pero sólo para el importe de los gastos y en el caso de efectuarse la práctica en forma ambulatoria. En el caso de una prestación que sólo tiene honorarios, se indicará el mismo concepto que el que se asigne a "honorarios ambulatorio". También, en el caso que sólo sea una prestación que se use sólo en internación, se indicará el mismo concepto que se asigne a "gastos internación".
</div>
<div id="helpGI" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Gastos Internación: es la equivalencia para la prestación con el concepto de egreso que le corresponda, pero sólo para el importe de los gastos y en el caso de efectuarse la práctica por una internación. En el caso de una prestación que sólo tiene honorarios, se indicará el mismo concepto que el que se asigne a "honorarios internación". También, en el caso que sólo sea una prestación que se use sólo en forma ambulatoria, se indicará el mismo concepto que se asigne a "gastos ambulatorios".
</div>
<div id="helpGuardar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Guardar: Al seleccionar este botón, se efectúan todos los controles sobre los datos ingresados y se graba en la tabla correspondiente; confirmando así lo ingresado. No será guardado ningún cambio si se abandona la pantalla sin seleccionar este botón.
</div>
<div id="helpVolver" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Volver: Seleccionando este link, se vuelve a la pantalla anterior. Se perderá toda actualización efectuada en el caso que los cambios no se guarden previamente.
</div>



<script type="text/javascript">
function guargar(){
	<c:if test="${prestacionConcepto.idTipoNomenclador == 3}">
		if (jQuery("#codigo").val() == "" || isNaN(jQuery("#codigo").val())){
			alert("Debe completar un codigo numerico");
			return;
		}
		if (jQuery("#coefHonoarios").val() == "" || isNaN( jQuery("#coefHonoarios").val())){
			alert("Debe completar un coeficiente de honorarios");
			return;
		}
		if (jQuery("#coefGastos").val() == "" || isNaN( jQuery("#coefGastos").val())){
			alert("Debe completar un coeficiente de gastos");
			return;
		}
		
		if ((parseFloat(jQuery("#coefHonoarios").val()) + parseFloat(jQuery("#coefGastos").val())) > 1){
			alert("La suma de los coeficientes no puede superar 1");
			return;
		}
		
		if (jQuery("#descripcion").val() == ""  && jQuery("#tipo_nomenclador").val() == 3 ){
			alert("Debe completar una descripcion");
			return;
		}
	</c:if>	
	
	if (jQuery("#id_prestacion").val() == "" && jQuery("#tipo_nomenclador").val() == 9 && jQuery("#<portlet:namespace />med_seleccionado").val() != "1"){
		alert("Debe seleccionar un medicamento");
		return;
	}
	 
		if (jQuery("#honorarios_ambulatorio").val()	== -1 ||
			jQuery("#honorarios_internacion").val()	== -1 ||
			jQuery("#gastos_ambulatorio").val()	== -1 ||
			jQuery("#gastos_internacion").val()	== -1 ) {
			alert("Debe seleccionar los conceptos");
			return;
		}
		
		jQuery("#botonGuardar").toggle();
		jQuery("#guardando").toggle();
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/tesoreria/editar_equivalencias_prestaciones_conceptos" /></portlet:actionURL>';
		document.<portlet:namespace />editar_prestacion_concepto.method = 'post';
		submitForm(document.<portlet:namespace />editar_prestacion_concepto, url);
}

	jQuery(document).ready(function() {
		jQuery('#busqueda_medicamentos').hide();
		jQuery("#guardando").hide();
		jQuery("#honorarios_ambulatorio").val("${prestacionConcepto.honorariosAmbulatorio.id}");	
		jQuery("#honorarios_internacion").val("${prestacionConcepto.honorariosInternacion.id}");
		jQuery("#gastos_ambulatorio").val("${prestacionConcepto.gastosAmbulatorio.id}");	
		jQuery("#gastos_internacion").val("${prestacionConcepto.gastosInternacion.id}");
		jQuery("#marca_rein_liq").val("${prestacionConcepto.prestacion.marca_rein_liq}");
		jQuery("#tipo_nomenclador").val("${prestacionConcepto.idTipoNomenclador}");
		<%if (rolABMNomenclador && !rolABMEquivalencias){ %>
			cambioTipo();
		<%}%>
		
	});
	
	function actualizarConceptos(){
		var ejercicio=jQuery("#ejercicio").val();	
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/traer_conceptos_para_ejercicio'
		    + '&ejercicio=' +ejercicio;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				jQuery('#honorarios_ambulatorio').find('option').remove();
				jQuery('#honorarios_internacion').find('option').remove();
				jQuery('#gastos_ambulatorio').find('option').remove();
				jQuery('#gastos_internacion').find('option').remove();
				for(var i =0;i< obj.conceptos.length; i++){
					jQuery('#honorarios_ambulatorio').append('<option value="'+obj.conceptos[i].id+'">'+obj.conceptos[i].descripcion+'</option>');
					jQuery('#honorarios_internacion').append('<option value="'+obj.conceptos[i].id+'">'+obj.conceptos[i].descripcion+'</option>');
					jQuery('#gastos_ambulatorio').append('<option value="'+obj.conceptos[i].id+'">'+obj.conceptos[i].descripcion+'</option>');
					jQuery('#gastos_internacion').append('<option value="'+obj.conceptos[i].id+'">'+obj.conceptos[i].descripcion+'</option>');
				}                                                                                                                                                                                                                                                            
			}
		});		
	}
	
	function cambioTipo() {
		if (jQuery("#tipo_nomenclador").val() == 3){
			jQuery('#busqueda_medicamentos').hide();
			jQuery('#no_busqueda_medicamentos').show();
		}
		if (jQuery("#tipo_nomenclador").val() == 9){
			jQuery('#busqueda_medicamentos').show();
			jQuery('#no_busqueda_medicamentos').hide();
			jQuery('#marca_rein_liq').val("3");
			jQuery('#honorarios_ambulatorio').val("${medicamento_ambulatorio}");
			jQuery('#honorarios_internacion').val("${medicamento_internacion}");
			jQuery('#gastos_ambulatorio').val("${medicamento_ambulatorio}");
			jQuery('#gastos_internacion').val("${medicamento_internacion}");
			jQuery("#coefGastos").val("1");	
			jQuery("#coefHonoarios").val("0");
		}
	}
	
</script>
