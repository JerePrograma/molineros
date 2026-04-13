<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentasSSS"%>
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
//	boolean rolABMEquivalencias = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_EQUIVALENCIAS)||portlet_name.equals("farmacia")||portlet_name.equals("uoma");
//	PlanCuentasSSS cuenta = (PlanCuentasSSS)request.getAttribute("cuentaSSS"); 
%>


<liferay-ui:error exception="<%=ar.com.ospim.tesoreria.CuentaDuplicadaException.class %>" message="duplicate-cuenta" />
<form action="" method="post" name="<portlet:namespace />editar_plan_sss" >
<input type="hidden" name="id" value="${cuentaSSS.id}"/>
<input type="hidden" name="<%=Constants.CMD%>" value="<%=Constants.EDIT%>"/>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;"  > 
	<tr>
		<td>Cuenta:</td>
		<td><input type="text" name="cuenta" id="cuenta" value="${cuentaSSS.cuenta}" size="50"/></td>
	</tr>
	<tr>
		<td>Numero:</td>
		<td><input type="text" name="numero" id="numero" value="${cuentaSSS.numero}" size="50"/></td>
	</tr>
	<tr>
		<td>Tipo:</td>
		<td><select name="tipo" id="tipo">
							<option value="EOAF">EOAF</option>
							<option value="ESFC">ESFC</option>
			</select>
		</td>
		<td>Signo:</td>
		<td><select name="signo" id="signo">
							<option value="1">Positivo</option>
							<option value="-1">Negativo</option>
			</select>
		</td>
		
		
	</tr>
	<tr>
	  <td>Acumula Sobre:</td>
	  <td><input type="text" name="acumula" value="${cuentaSSS.acumulaSobre}" size="50"/></td>
	</tr>
	
	<tr>
	    <table class="lfr-table" width="100%">
		   <tr>
				<td>
				  <div id="<portlet:namespace />divCuentasAsociadas">
					<fieldset class="block-labels">
						<legend>
							Asociar Cuentas
						</legend>
						<liferay-util:include
							page='/html/portlet/tesoreria/contabilidad/plan_cuentas_sss_asigna_cuentas.jsp'>
						</liferay-util:include>
					</fieldset>
				  </div>	
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
		<td colspan="2"><span id="botonGuardar"><input type="button" value="Guardar" onclick="guardar()"/>
		</span></td>
	</tr>
</table>
</form>
<span id="guardando">
	<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
</span>
<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="volver">
<portlet:param name="struts_action" value="/tesoreria/plan_cuentas_sss" />
</portlet:renderURL>
<p><a href="<%= volver %>">Volver</a></p>



<script type="text/javascript">
function guardar(){
	if(validarDatos()){
		jQuery("#botonGuardar").toggle();
		jQuery("#guardando").toggle();
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_plan_cuenta_sss'
		document.<portlet:namespace />editar_plan_sss.method = 'post';
		submitForm(document.<portlet:namespace />editar_plan_sss, url);
	}		
}

jQuery(document).ready(function() {
		jQuery("#guardando").hide();
		jQuery('#tipo').val("${cuentaSSS.tipo}");
		jQuery('#signo').val("${cuentaSSS.signo}");
});

function validarDatos(){
 var ret = true;
 if(jQuery('#cuenta').val()==null || jQuery('#cuenta').val()==''){
	 alert('Debe ingresar una descripcion de la cuenta');
	 ret=false;
 }else if(jQuery('#numero').val()==null || jQuery('#numero').val()==''){
	 alert('Debe ingresar un numero de cuenta');
	 ret=false;
 }
 return ret;
}
	
</script>

