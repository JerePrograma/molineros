<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.global.beans.ProcesoSQL" %>
<portlet:defineObjects/>

<liferay-ui:error exception="<%= Exception.class %>" message="numero-recibo-existente" />
<%		
	ProcesoSQL corriendo=new ProcesoSQL();
	/* corriendo=LiquidaDesreguladosServiceUtil.isRunningProcess();
	  if(corriendo==null || corriendo.getProcid()==0){
		 corriendo=(ProcesoSQL)renderRequest.getPortletSession().getAttribute("procesoSQL");	
	  }
	*/
	corriendo=null;
	
%>
<%if (null!=corriendo&& corriendo.getProcid()>0){ %>
	<p>Proceso corriendo. Iniciado el: <%= corriendo.getFechaComienzoAsString()%> <a href="javascript:cancelarProceso('<%=corriendo.getProcid()%>');">Cancelar proceso</a></p>
<%}else{%>
    <br>
    <fieldset class="block-labels">
							<legend>
								<liferay-ui:message key="datos-afiliado" />
							</legend>
       <liferay-util:include
			page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>
			<liferay-util:param value="<%= String.valueOf(true) %>"	  name="edit_mode" />
			<liferay-util:param value="<%= null %>"  name="discapacidad" />
			<liferay-util:param value="<%= String.valueOf(false) %>"  name="pag_reintegro" />
			<liferay-util:param name="cuil" value='' />
			<liferay-util:param name="inte" value='' />
			<liferay-util:param value="" name="origen" />
       </liferay-util:include>
   
    <br>   
	<input type="button" id="boton_liquida_desreg" name="boton_liquida_desreg" value="<liferay-ui:message key='liquidar-aportes-pendientes'/>" onClick="javascript:liquidarAportesPendientes()" />
	<br>
	<label id="<portlet:namespace />mensaje" style="color: red"></label>
 </fieldset>
<%}%>	 

<script type="text/javascript">

<portlet:namespace />verificaEstadoReporte(); 	

function <portlet:namespace />verificaEstadoReporte() {
	
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/reporte_verifica';
				url+= '&reporte=reporte.liquidar_desregulados';
				jQuery.ajax({   
							url: url,
							async: false,
							success: function(data){
								var obj = jQuery.parseJSON(data);
								
								if("r"==obj.status){
									document.getElementById("boton_liquida_desreg").style.visibility = "hidden";
									document.getElementById("<portlet:namespace />mensaje").innerHTML = "Lanzado el " + obj.descripcion;
									
								}else{
									document.getElementById("boton_liquida_desreg").style.visibility = "visible";
									document.getElementById("<portlet:namespace />mensaje").innerHTML="";
								}
							}
				});	
			}


</script>