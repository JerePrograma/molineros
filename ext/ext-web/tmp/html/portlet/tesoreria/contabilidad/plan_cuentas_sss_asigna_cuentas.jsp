<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@ page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@ page import="com.liferay.portal.kernel.util.OrderByComparator"%>
 
<%
int entidad=WebKeysGlobal.OSPIM;
String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
	entidad=WebKeysGlobal.UOMA;
}else if(renderResponse.getNamespace().equals("_FAR_1_")){
	entidad=WebKeysGlobal.AMTIMA;
}

String esEditableStr = ParamUtil.getString(request, "esEdicion");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEdicion = Boolean.parseBoolean(esEditableStr);

List<PlanCuentas> ctas = TraeListasServiceUtil.getPlanCuentas(new Date(), entidad) ;

%>


<table width="100%">
  <tr>
	<td width="100%" valign="top">
	<table class="lfr-table" width="100%">
	   <tr>
	    <td valign="top" width="30%" >
	     <table>
	       <tr>
	         <td valign="top"><label><liferay-ui:message key="cuenta" />:</label></td>
	         <td valign="top">
	              <select name="<portlet:namespace/>cuentas_contables" id="<portlet:namespace/>cuentas_contables">
		              <option value="">Seleccione una Cuenta</option>	       					
							<%for(PlanCuentas u:ctas) {%>
							   <option value="<%=u.getNumero() %>"><%=u.getNumero() + " -- " + u.getCuenta().trim()  %> </option>
							<%}%>
								
			      </select>
	   
		     </td>
		     <td>     
		            <input type="button" value="Asignar Cuenta" 
		            onClick="<portlet:namespace />agregarCuentaContable();" />
		     </td>     
		     
	       </tr>
	       <tr>
		     <td colspan="1">&nbsp;</td>
	       </tr>
	      </table>
	     </td>
	     </tr>
	     <tr>
	     <td valign="top" colspan="15" width="70%">
				<div align="center" id="<portlet:namespace />cuentasContablesDiv">
					<liferay-util:include page="/html/portlet/tesoreria/contabilidad/plan_cuentas_sss_asigna_cuentas_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
        </tr>
	</table>
	</td>
  </tr>
</table>

<input type="hidden" name="<portlet:namespace />id_cuenta_asignado" id="<portlet:namespace />id_cuenta_asignado" value="" />

<script type="text/javascript">
   	function <portlet:namespace />agregarCuentaContable(){
		var idDetalle=jQuery('#<portlet:namespace />id_cuenta_asignado').val();
			
		var cuentaId=jQuery('#<portlet:namespace/>cuentas_contables').val();
		var cuentaDescripcion=jQuery('#<portlet:namespace/>cuentas_contables').find('option:selected').text();
		var cuentaDesc=cuentaDescripcion.split("--");
		if(cuentaDesc.length>0){
		   cuentaDescripcion=cuentaDesc[1];
		}   
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/plan_cuentas_sss_agregar_cuenta'
			+	'&<%= Constants.CMD%>=' 
				
		if(idDetalle==null || idDetalle =="" || idDetalle==0){
			url += 'asociarcuenta'
		}
					
		url += '&cuentaid=' + encodeURI(cuentaId)
				+ '&cuentadescripcion=' + encodeURI(cuentaDescripcion)
				+ '&iddetalle=' + encodeURI(idDetalle)
				+ '&esEdicion=' +"<%=esEdicion%>"; 	
				
		if(cuentaId!=null && cuentaId!=""){			
				jQuery('#<portlet:namespace />cuentasContablesDiv').load(url, function() {} );
		}else {
			alert("Debe seleccionar una cuenta");
		}		
				
	}
	

	function borraCuentaAsociada(idMod){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/plan_cuentas_sss_agregar_cuenta'
			+	'&<%= Constants.CMD%>=' + 'desasociarcuenta'
			+ '&cuentaid=' + encodeURI(idMod)
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
			jQuery('#<portlet:namespace />cuentasContablesDiv').load(url, function() {}	 );
	}
	
	
	
	
	
</script>