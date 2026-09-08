<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.liquidaciones.ConceptoUtilizadoException"%>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<liferay-ui:error exception="<%= ConceptoUtilizadoException.class %>"
	message="concepto-utilizado" />

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
	boolean rolABMEquivalencias = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_EQUIVALENCIAS);

	int filtro_cuenta=0;
	int filtro_cuenta_pasivo=0;
	String filtro_concepto=(String)request.getAttribute("filtro_concepto");
	
	if(portlet_name.equals("uoma")){
		
		try{			
			filtro_cuenta=Integer.parseInt((String)request.getAttribute("filtro_cuenta"));
		}catch(Exception e){
			filtro_cuenta=0;
		}
		if(filtro_cuenta==0){
			filtro_cuenta--;
		}
		
		try{
			filtro_cuenta_pasivo=Integer.parseInt((String)request.getAttribute("filtro_cuenta_pasivo"));
		}catch(Exception e){
			filtro_cuenta_pasivo=0;
		}
		if(filtro_cuenta_pasivo==0){
			filtro_cuenta_pasivo--;
		}
	}	
	List<PlanCuentas> planCuentas=(List<PlanCuentas>)request.getAttribute(WebKeysTesoreria.PLAN_CUENTAS);
	
	String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
%>

<portlet:defineObjects />
<form action="" method="POST" id="busqueda_conceptos"
	name="busqueda_conceptos">
	<table style="width: 100%">
		<tr>
			<td colspan="2"><b>Buscar conceptos</b></td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2">Ejercicio:&nbsp;<select name="ejercicio" id="ejercicio" onchange="actualizarCuentas()">
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
						<% if(portlet_name.equals("farmacia")){%>
							Julio&nbsp;<%=i %>&nbsp;-&nbsp;Junio&nbsp;<%= i+1 %></option>
						<%}else{%>
							Agosto&nbsp;<%=i %>&nbsp;-&nbsp;Julio&nbsp;<%= i+1 %></option>
						<%}
					} %>
			</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
			Concepto&nbsp;
			<input type="text" id="concepto" name="concepto" value="<%=filtro_concepto!=null?filtro_concepto:""%>"	size="50" /> <a href="javascript:void(0)" onclick="help(event, 'helpConcepto')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp; 
			Cuenta:&nbsp; 
			<select name="cuenta" id="cuenta"	style="width: 150px">
				<option value="-1" /></option>
				<%for(PlanCuentas p:planCuentas){%>
					<option value="<%=p.getId()%>" <%if(p.getId()==filtro_cuenta){%>selected="true"<%}%> />
					<%=p.getNumero()%> - <%=p.getCuenta()%>
					</option>
				<%}%>				
			</select> <a href="javascript:void(0)" onclick="help(event, 'helpCuenta')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp; 
			Cuenta Pasivo:&nbsp; 
			<select name="cuenta_pasivo"	id="cuenta_pasivo" style="width: 150px">
					<option value="-1" /></option>
					<%for(PlanCuentas p:planCuentas){%>
					<option value="<%=p.getId()%>" <%if(p.getId()==filtro_cuenta_pasivo){%>selected="true"<%}%> />
					<%=p.getNumero()%> - <%=p.getCuenta()%>
						</option>
					<%}%>	
			</select><a href="javascript:void(0)" onclick="help(event, 'helpCuentaPasivo')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a> &nbsp; </td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2"><input type="button" onclick="buscarConceptos()" value="Buscar" /><a href="javascript:void(0)" onclick="help(event, 'helpBuscar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp; 
			<% if (rolABMEquivalencias) {%> 
				<input type="button" onclick="altaConcepto()" value="Alta Concepto" /><a href="javascript:void(0)" onclick="help(event, 'helpAlta')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			<% } %>
			</td>
		</tr>
	</table>
	<%if (portlet_name.equals("uoma")){ %>
		<input type="hidden" name="pagina" id="pagina" value="5"/>
	<%}%>
</form>
<hr />
<br />
<%	
		String ejDesde = (String)request.getAttribute("ejercicio_desde");
		String ejHasta = (String)request.getAttribute("ejercicio_hasta");
					
		List<Concepto> conceptos = (List<Concepto>) portletSession.getAttribute("conceptos",PortletSession.PORTLET_SCOPE);
		
		
	//Si debe mostrarse el btn de agregar afiliado								
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("Concepto <a href='javascript:void(0)' onclick='help(event, \"helpConceptoHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Cuenta <a href='javascript:void(0)' onclick='help(event, \"helpCuentaHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Cuenta Pasivo <a href='javascript:void(0)' onclick='help(event, \"helpCuentaPasivoHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Egr. <a href='javascript:void(0)' onclick='help(event, \"helpEgresosHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Ing. <a href='javascript:void(0)' onclick='help(event, \"helpIngresosHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		if(portlet_name.equals("tesoreria")){
 			headerNames.add("Liq. <a href='javascript:void(0)' onclick='help(event, \"helpLiquidacionesHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		}
 		headerNames.add("Desde <a href='javascript:void(0)' onclick='help(event, \"helpDesdeHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Hasta <a href='javascript:void(0)' onclick='help(event, \"helpHastaHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		 if (rolABMEquivalencias) {
 		headerNames.add("Editar <a href='javascript:void(0)' onclick='help(event, \"helpEditarHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Eliminar <a href='javascript:void(0)' onclick='help(event, \"helpEliminarHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		 }
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-conceptos-were-found"));
		int total=conceptos.size();
		if(portlet_name.equals("uoma")){	 		
	 		total = (Integer) portletSession.getAttribute("total_conceptos",PortletSession.PORTLET_SCOPE);
	 	}
		searchContainer.setTotal(total);
			List resultRows = searchContainer.getResultRows();

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaDesde = format.parse(ejDesde);
		Date fechaHasta = format.parse(ejHasta);
		int m = 0; 
		for (Concepto concepto: conceptos){
			m++;
			ResultRow row = new ResultRow(m, m, m);
			row.addText(concepto.getDescripcion());
			if (concepto.getPlanCuentas().getNumero() != null){
				row.addText(concepto.getPlanCuentas().getNumero() + " - " + concepto.getPlanCuentas().getCuenta());
			} else {
				row.addText("-");
			}
			if (concepto.getPlanCuentasPasivo().getNumero() != null){
				row.addText(concepto.getPlanCuentasPasivo().getNumero() + " - " + concepto.getPlanCuentasPasivo().getCuenta());
			} else {
				row.addText("-");
			}
			row.addText((concepto.isEgreso() ? "E" : "") + (concepto.isSubEgreso() ? "S" : ""));
			row.addText((concepto.isIngreso() ? "I" : "")+(concepto.isSubIngreso() ? "S" : ""));
			if(portlet_name.equals("tesoreria")){
				row.addText(concepto.isLiquidaciones() ? "L" : "");
			}
			
			String dd = ejDesde;
			String hta = ejHasta;
			if (DateUtils.compararFechasTruncarEnDia(concepto.getValidoDesde(), fechaDesde) >= 0){
				dd = concepto.getValidoDesdeString();
			}
			row.addText(dd);
			
			//if (DateUtils.compararFechasTruncarEnDia(concepto.getValidoHasta(), fechaHasta) <= 0){
				hta = concepto.getValidoHastaString();
			//}
			row.addText(hta);
			
			 if (rolABMEquivalencias) {
			 	
			 	row.addText("<a href='javascript:void(0)' onclick=\"editarConcepto(" + concepto.getId() +", '" + concepto.getValidoDesdeString()+ "', '"+dd+"','"+ hta+"','"+concepto.getIdSeccional()+"','"+concepto.getIdSecuencial()+"')\">Editar</a>");
				row.addText("<a href='javascript:void(0)' onclick=\"eliminarConcepto(" + concepto.getId() +", '" + concepto.getValidoDesdeString()+ "', '"+dd+"','"+ hta+"','"+ concepto.getIdSecuencial()+"')\">Eliminar</a>");
			 }
			resultRows.add(row);
		}
		
%>
<% if (conceptos != null && conceptos.size()>=1){ %>
<p>
	<b>Ejercicio &nbsp;<%=ejDesde %>&nbsp;-&nbsp;<%=ejHasta %></b>
</p>

<table style="width: 100%">
<tr>
<td align="center">
<%if (portlet_name.equals("uoma") ){ %>
<b>Total de resultados: <%=total%><b>
<liferay-util:include page='/html/portlet/tesoreria/equivalencias/paginador_conceptos_cuentas.jsp'>
	<liferay-util:param value="<%= String.valueOf(total) %>" name="total_conceptos" />
</liferay-util:include>

<%} %>
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
</td></tr>
</table>
<%} %>


<div id="helpConcepto" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto: Se completa s�lo cuando se quiere efectuar una b�squeda que filtre por la descripci�n de un concepto o parte del mismo. Tomar� s�lo las cuentas del ejercicio que se indique. Luego de ingresar el texto, se deber� seleccionar el bot�n "Buscar" para visualizar el resultado en el  cuadro inferior de esta pantalla.
</div>
<div id="helpCuenta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Cuenta: Se completa s�lo cuando se quiere efectuar una b�squeda que filtre por una cuenta del plan de cuentas en particular en la columna "Cuenta". Tomar� s�lo las cuentas del ejercicio que se indique. Luego de seleccionada la cuenta, se deber� seleccionar el bot�n "Buscar" para visualizar el resultado en el cuadro inferior de esta pantalla.
</div>
<div id="helpCuentaPasivo" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Cuenta Pasivo: Se completa s�lo cuando se quiere efectuar una b�squeda que filtre por una cuenta del plan de cuentas en particular en la columna "Cuenta Pasivo". Tomar� s�lo las cuentas del ejercicio que se indique. Luego de seleccionada la cuenta, se deber� seleccionar el bot�n "Buscar" para visualizar el resultado en el cuadro inferior de esta pantalla.
</div>
<div id="helpBuscar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Buscar: De acuerdo a los par�metros previos, seleccionando este bot�n, se ejecuta la b�squeda de registros coincidentes. El resultado se visualiza en el cuadro inferior de esta pantalla.
</div>
<div id="helpAlta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Alta Concepto: Seleccionando este bot�n, se abrir� la pantalla de alta de un nuevo concepto.
</div>
<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Es el ejercicio al cual pertenecen los registros del cuadro inferior de la pantalla. Es decir, de la �ltima b�squeda efectuada.
</div>
<div id="helpConceptoHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto: Descripci�n del concepto. Puede ser modificado seleccionando "Editar" en el registro que se trate.
</div>
<div id="helpCuentaHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Cuenta: Es la cuenta que le corresponde contablemente a todo movimiento que utilice el concepto tanto en la generaci�n de asientos autom�ticos como en los reportes con res�menes por cuenta contable. Por ejemplo: subdiarios de ingresos y egresos. Puede ser modificado seleccionando "Editar" en el registro que se trate.
</div>
<div id="helpCuentaPasivoHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Cuenta Pasivo: Es la cuenta contable que se utilizar� como equivalencia para el concepto, en el caso que se trate de una deuda que no es pagada en el mismo mes que fuera contabilizada. Es decir, efectuado el pago con posterioridad del mes de la fecha de recepci�n del comprobante de dicha deuda. Se utiliza tanto en la generaci�n de asientos autom�ticos como en los reportes con res�menes por cuenta contable. Por ejemplo: subdiarios de ingresos y egresos. Puede ser modificado seleccionando "Editar" en el registro que se trate.
</div>
<div id="helpEgresosHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Egresos: Se indica con la letra "E" los casos de conceptos que pueden ser seleccionados en la carga de comprobantes del m�dulo de pagos. Se indica con la letra "S" los casos de conceptos de egresos que deben ser considerados para listarse en el subdiario de egresos.
</div>
<div id="helpIngresosHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ingresos: Se indica con la letra "I" los casos de conceptos que pueden ser seleccionados en la carga de recibos del m�dulo de ingresos. Se indica con la letra "S" los casos de conceptos de ingresos que deben ser considerados para listarse en el subdiario de ingresos.
</div>
<div id="helpLiquidacionesHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Liquidaciones: Se indica con la letra "L" en los casos de conceptos que pueden ser utilizados en el m�dulo de liquidaciones cuando no se detalla por c�digo de prestaci�n del nomenclador.
</div>
<div id="helpDesdeHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Desde: Es la fecha de inicio de la vigencia de la equivalencia establecida para el concepto que se trate. Dentro de un mismo ejercicio se podr�n asignar distintas equivalencias para diferentes per�odos. De esta forma, se evita que los cambios efectuados sean retroactivos a meses ya cerrados del ejercicio.
</div>
<div id="helpHastaHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Hasta: Es la fecha de finalizaci�n de la vigencia de la equivalencia establecida para el concepto que se trate. Dentro de un mismo ejercicio se podr�n asignar distintas equivalencias para diferentes per�odos. De esta forma, se evita que los cambios efectuados sean retroactivos a meses ya cerrados del ejercicio que se trate.
</div>
<div id="helpEditarHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Editar: Se selecciona en el caso que se desee efectuar cambios sobre alg�n dato de un registro. Se abrir� una nueva pantalla de actualizaci�n.
</div>
<div id="helpEliminarHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Eliminar: Se selecciona en el caso que se desee un registro. No podr� borrarse un registro que fuera utilizado en alguna tabla o transacci�n del sistema.
</div>


<script type="text/javascript">
<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
<%}%>

function editarConcepto(id, ddOriginal, dd, hasta, id_seccional, id_secuencial){
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_concepto'
	+  '&id=' +id + '&ejercicio_desde=' + escape(dd)+ '&ejercicio_hasta='+ escape(hasta) + '&ejercicio_desde_original=' + escape(ddOriginal)+
	'&id_seccional='+id_seccional+'&id_secuencial='+id_secuencial;
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function eliminarConcepto(id, ddOriginal, dd, hasta, id_secuencial){
	<%if(portlet_name.equals("uoma")){%>
		var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();			
		jQuery("#pagina").val(pagina_sel);
	<%}%>
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/eliminar_concepto'
	+  '&id=' +id + '&ejercicio_desde=' + escape(dd)+ '&ejercicio_hasta='+ escape(hasta) + '&ejercicio_desde_original=' + escape(ddOriginal) + '&id_secuencial=' + id_secuencial;
	url += '&rnd=' + Math.floor(Math.random()*100);
	<%if(portlet_name.equals("uoma")){%>
		url +="&pagina="+pagina_sel;
	<%}%>
	window.location = url;
}
function altaConcepto(){
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_concepto';
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function buscarConceptos(){
	<%if(portlet_name.equals("uoma")){%>
		var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();	
		jQuery("#pagina").val(pagina_sel);
		if(jQuery("#concepto").val()!="" || jQuery("#cuenta").val()!="-1" || jQuery("#cuenta_pasivo").val()!="-1"){			   
			   if(jQuery("#concepto").val()!='<%=filtro_concepto%>' || jQuery("#cuenta").val()!='<%=filtro_cuenta%>' || jQuery("#cuenta_pasivo").val()!='<%=filtro_cuenta_pasivo%>' ){			   
			   		jQuery("#pagina").val(0);
			   }
		}
	<%}%>
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/equivalencias_conceptos_cuentas';	
	submitForm(document.busqueda_conceptos, url);
}

function actualizarCuentas(){
	var ejercicio=jQuery("#ejercicio").val();	
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_plan_cuentas_para_fecha'
	    + '&ejercicio=' +ejercicio;
	<%if(portlet_name.equals("farmacia")){%>
		url+='&amtima=true';
	<%}%>
	    
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			jQuery('#cuenta').find('option').remove();
			jQuery('#cuenta_pasivo').find('option').remove();
			jQuery('#cuenta').append('<option value="-1"></option>');
			jQuery('#cuenta_pasivo').append('<option value="-1"></option>');
			for(var i =0;i< obj.cuentas.length; i++){
				jQuery('#cuenta').append('<option value="'+obj.cuentas[i].id+'">'+obj.cuentas[i].numero + '-' + obj.cuentas[i].cuenta +'</option>');
				jQuery('#cuenta_pasivo').append('<option value="'+obj.cuentas[i].id+'">'+obj.cuentas[i].numero + '-' + obj.cuentas[i].cuenta +'</option>');
			}                                                                                                                                                                                                                                                            
		}
	});		
}
</script>
