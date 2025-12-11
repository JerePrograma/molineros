<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
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
<form action="" method="post" name="<portlet:namespace />editar_concepto_afip" >
<input type="hidden" name="id" value="${conceptoAfip.id}"/>
<c:if test="${conceptoAfip.id != 0}">
	<input type="hidden" name="ejercicio_hasta" value="${ejercicio_hasta}"/>
</c:if>
<table style="width: 70%">
	<tr>
		<td><b>Ejercicio:</b></td>
			<c:if test="${conceptoAfip.id != 0}">
				<td>
				<select name="ejercicio_desde">
					<% while (DateUtils.compararFechasTruncarEnDia(desde.getTime(), hasta.getTime()) <= 0){%>
						<option value="01/<%=desde.get(Calendar.MONTH)+1%>/<%=desde.get(Calendar.YEAR)%>">01/<%=desde.get(Calendar.MONTH)+1%>/<%=desde.get(Calendar.YEAR)%></option>
					<%	desde.add(Calendar.MONTH, 1);
						}
					%>
				</select><b>&nbsp;-&nbsp;${ejercicio_hasta}</b><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
			</c:if>
			<c:if test="${conceptoAfip.id == 0}">
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
	<tr>
		<td>Concepto Afip:</td>
		<c:if test="${conceptoAfip.id == 0}">
			<td><input type="text" name="descripcion" id="descripcion" value="${conceptoAfip.descripcion}" size="50"/><a href="javascript:void(0)" onclick="help(event, 'helpConceptoAFIP')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		</c:if>
		<c:if test="${conceptoAfip.id != 0}">
			<td>${conceptoAfip.descripcion}<input type="hidden" name="descripcion" id="descripcion" value="${conceptoAfip.descripcion}" size="50"/><a href="javascript:void(0)" onclick="help(event, 'helpConceptoAFIP')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		</c:if>
	</tr>
	<tr>
		<td>Codigo Concepto Afip:</td>
		<c:if test="${conceptoAfip.id == 0}">
			<td><input type="text" name="codigoConcepto" id="codigoConcepto" value="${conceptoAfip.codigoConcepto}" size="50"/><a href="javascript:void(0)" onclick="help(event, 'helpCodigo')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		</c:if>
		<c:if test="${conceptoAfip.id != 0}">
			<td>${conceptoAfip.codigoConcepto}<input type="hidden" name="codigoConcepto"  id="codigoConcepto" value="${conceptoAfip.codigoConcepto}" size="50"/><a href="javascript:void(0)" onclick="help(event, 'helpCodigo')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		</c:if>
	</tr>
	<tr>
		<td>Codigo ContraConcepto Afip:</td>
		<c:if test="${conceptoAfip.id == 0}">
			<td><input type="text" name="codigoContraConcepto" id="codigoContraConcepto" value="${conceptoAfip.codigoContraConcepto}" size="50"/><a href="javascript:void(0)" onclick="help(event, 'helpCodigoContraconcepto')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		</c:if>
		<c:if test="${conceptoAfip.id != 0}">
			<td>${conceptoAfip.codigoContraConcepto}<input type="hidden" name="codigoContraConcepto" id="codigoContraConcepto" value="${conceptoAfip.codigoContraConcepto}" size="50"/><a href="javascript:void(0)" onclick="help(event, 'helpCodigoContraconcepto')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		</c:if>
	</tr>
	<% if (rolABMEquivalencias) { %>
	<tr>
		<td>Concepto:</td>
		<td>
			<select name="concepto_id" id="concepto_id">
				<c:forEach items="${conceptos}" var="conc">
					<option value="${conc.id}"/><c:out value="${conc.descripcion}"/></option>
				</c:forEach>
			</select><a href="javascript:void(0)" onclick="help(event, 'helpConcepto')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
	<%} %>
	<tr>
		<td colspan="2">Liquidable:&nbsp;<input type="checkbox" name="liquidable" id="liquidable" value="true"/><a href="javascript:void(0)" onclick="help(event, 'helpLiquidable')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<select name="debitoCredito" id="debitoCredito">
				<option value="D">Débito</option>
				<option value="C">Crédito</option>
			</select><a href="javascript:void(0)" onclick="help(event, 'helpCredito')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
		</td>
	</tr>
		<tr>
		<td colspan="2">&nbsp;</td>
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
	<portlet:param name="struts_action" value="/tesoreria/equivalencias_conceptos_afip" />
</portlet:renderURL>
<p><a href="<%= volver %>">Volver</a><a href="javascript:void(0)" onclick="help(event, 'helpVolver')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></p>

<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Es el período de vigencia del concepto AFIP y su equivalencia. Generalmente, coincide con el ejercicio contable. Se modificará, por ejemplo, en el caso que se desee que cobre vigencia desde un mes en particular del ejercicio, ya que los anteriores son períodos con el análisis concluido y los ajustes efectuados.
</div>
<div id="helpConceptoAFIP" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto AFIP: Es la descripción que informa AFIP para el concepto que se trate.
</div>
<div id="helpCodigo" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Código Concepto AFIP: Es el concepto definido por AFIP para identificar un tipo de aporte o contribución que se detalla en el archivo quincenal de nominas.
</div>
<div id="helpCodigoContraconcepto" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Código Contraconcepto AFIP: Es sólo informativo e indica con qué concepto se remitirá un movimiento de reversión (débito) de una transacción previa, por ese código de AFIP.
</div>
<div id="helpConcepto" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto: es el concepto de ingreso de la tabla propia que se establece como equivalencia para el concepto de AFIP.
</div>
<div id="helpLiquidable" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Liquidable: Se indica si las acreditaciones de este concepto se deben o no liquidar a las tercerizadoras. Por ejemplo, en los casos de intereses por pago fuera de término o por multas, dichos importes no son liquidados a la tercerizadora.
</div>
<div id="helpCredito" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Crédito/Débito: Describe cómo se comporta el concepto de AFIP, si acredita o debita importes. Es informativo, ya que, en los registros del archivo de nóminas, cada uno de los registros que se informan, contienen el dato de débito o crédito según corresponda.
</div>
<div id="helpGuardar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Guardar: Al seleccionar este botón, se efectúan todos los controles sobre los datos ingresados y se graba en la tabla correspondiente; confirmando así lo ingresado. No será guardado ningún cambio si se abandona la pantalla sin seleccionar este botón.
</div>
<div id="helpVolver" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Volver: Seleccionando este link, se vuelve a la pantalla anterior. Se perderá toda actualización efectuada en el caso que los cambios no se guarden previamente.
</div>


<script type="text/javascript">
function guargar(){
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/tesoreria/editar_concepto_afip" /></portlet:actionURL>';
		document.<portlet:namespace />editar_concepto_afip.method = 'post';
		if (jQuery("#codigoConcepto").val() == "" || jQuery("#descripcion").val() == "" || jQuery("#codigoContraConcepto").val() == ""){
			alert("Debe completar todos los campos");
			return;
		}
		jQuery("#botonGuardar").toggle();
		jQuery("#guardando").toggle();
		submitForm(document.<portlet:namespace />editar_concepto_afip, url);
}

	jQuery(document).ready(function() {
		jQuery("#guardando").hide();
		jQuery("#concepto_id").val("${conceptoAfip.concepto.id}");
		jQuery("#debitoCredito").val("${conceptoAfip.debitoCredito}");
		jQuery('#liquidable').attr('checked', ${conceptoAfip.liquidable});
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
				jQuery('#concepto_id').find('option').remove();
				for(var i =0;i< obj.conceptos.length; i++){
					jQuery('#concepto_id').append('<option value="'+obj.conceptos[i].id+'">'+obj.conceptos[i].descripcion+'</option>');
				}                                                                                                                                                                                                                                                            
			}
		});		
	}	
</script>

