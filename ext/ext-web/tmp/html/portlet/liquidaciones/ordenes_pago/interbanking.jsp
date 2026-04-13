<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.ospim.autorizaciones.constantes.interbaking.ConstantesInterbanking" %>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.AfipCantidadRegistrosIncorrectaException.class %>" message="afip-cantidad-registros-incorrecta" />
<liferay-ui:error exception="<%= java.text.ParseException.class %>" message="afip-parse-exception" />
<liferay-ui:error exception="<%= org.postgresql.util.PSQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.sql.SQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.io.IOException.class %>" message="afip-io-exception" />
<liferay-ui:error exception="<%= ar.com.ospim.tesoreria.afip.ErrorProcesandoArchivosAfipException.class%>" message="error-procesando-archivos-afip" />
<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.RendicionBancoNacionRegistroDuplicado.class%>" message="rendicion-duplicada" />
<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.ErrorGeneralProcesandoArchivos.class%>" message="error-procesando-archivos" />
<%

String portlet_name=null;
String entidad="";

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "farmacia";
	entidad="AMTIMA";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
	entidad="UOMA";
}

boolean flag = ParamUtil.getBoolean(request, "flagOcultar");
String ejecutar = ParamUtil.getString(request, "ejecutar");
Integer ctabcria=ParamUtil.getInteger(request, "ctabcria");
boolean cEmail=ParamUtil.getBoolean(request, "cemail");

List<CuentaBancaria> ctas = (List<CuentaBancaria>) request.getSession().getAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);
if (ctas == null) {
    ctas = TraeListasServiceUtil.getCtasBcrias();
    request.getSession().setAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION, ctas);
}

String listOp = ParamUtil.getString(request, ConstantesInterbanking.PARAMETRO);


Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
fechaDesde.add(Calendar.MONTH, -1);
Calendar current = CalendarFactoryUtil.getCalendar();



List<String> errores = (List<String>)request.getAttribute("errores");
if (errores != null && !errores.isEmpty()){
	%>
	<table  style="color:#0000ff" >
	<%
	for (String error : errores){
		%>
		<tr><td>
		<%=error%>
		</td></tr>
		<%
	}
	%>
	</table>
	<%
}
%>
</form>

<form action="" method="get" name="<portlet:namespace />fmSSS" enctype="multipart/form-data">

<table class="lfr-table">
<tr>
<td>
   
  <fieldset class="block-labels">
      <legend>Archivo de transferencias a interbaking </legend>
 	  <table class="lfr-table">		 
		 <tr>
 	       <td colspan="2">
 	       	  <br>
 	       	  <legend>Importar Archivo con las Ordenes de Pago a procesar  </legend>
 	       	  <table class="lfr-table">
		 		 	
			 	<tr>
			      <td  align="left">
				       <input type="file" name="archivo"/>
				      
				       <input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivoInterbanking()"/>
	              </td>	
	              
	              <td>
				     <label>Con EMAIL de aviso:</label>
				  </td>
	              <td>
	                 &nbsp;&nbsp;<input type="checkbox" id="<portlet:namespace />cE" name="<portlet:namespace />cE"/>
	              </td>							
				</tr>
				<tr>
				  <td>
				    <span style="color:blue">
				    La primer columna del archivo debe ser el nro. de Orden de Pago.
				    </span>
				  </td>
				</tr>	
			  </table>
            </td>
 	     </tr>
 	     <tr><td>&nbsp;</td></tr>
 	     
 	     <tr>
		   <td>
			   Cuenta Bancaria:
		   </td>
		   <td>
			  <select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria">
					<% 	for (CuentaBancaria cta : ctas) { 
									if (portlet_name.equals("farmacia") && cta.getEntidad().equals("A")){%>
									<option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 13) {%> selected="selected" <%} %>> <%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>									
					<%	}else if(portlet_name.equals("uoma") && cta.getEntidad().equals("U")){%>	
									<option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 12) {%> selected="selected" <%} %>><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
					<%}} %>
			  </select>
			</td>
		</tr>			
 	  </table>   
   </fieldset>
</td>
</tr>
</table>

<input id="<portlet:namespace />fmSSSH" name="<portlet:namespace />fmSSSH" type="hidden" value="" />
<input id="<portlet:namespace />entidad" name="<portlet:namespace />entidad" type="hidden" value="<%=entidad%>" />
</form>




<script type="text/javascript">
<%if (ejecutar != null && ejecutar.equalsIgnoreCase("true")){%> 
   exportarCuentasInterbankingOPS(<%=ctabcria%>,'<%=entidad%>',<%=cEmail%>);
<%}%>

var popup;
function avisoTransferenciaOP(){
	var idLote=jQuery("#<portlet:namespace />nroLote").val();
	if(idLote!=null && ""!=idLote){
	   popup = Liferay.Popup({title:"Aviso Transferencia de  Ordenes de Pago",modal:true,width:420});
	   var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
   	   url = url+'&<%= Constants.CMD %>'+'='+'aviso_op_transferencia'+'&nrolote='+idLote;
   	   jQuery(popup).load(url);
    } else {
       alert("Debe Ingresar un Nro. de Lote");	
    }  
}

function avisoTransferenciaOpInterbanking(){
	var idLote=jQuery("#<portlet:namespace />nroLote").val();
	if(idLote!=null && ""!=idLote){
	   popup = Liferay.Popup({title:"Generar Transferencia de  Ordenes de Pago por interbanking",modal:true,width:420});
	   var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
   	   url = url+'&<%= Constants.CMD %>'+'='+'aviso_op_transferencia'+'&nrolote='+idLote+'&flagOcultar='+true;
   	   jQuery(popup).load(url);
    } else {
       alert("Debe Ingresar un Nro. de Lote");	
    }  
}


function <portlet:namespace />uploadArchivoInterbanking() {
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_archivos_interbanking';
	document.<portlet:namespace />fmSSS.method = 'post';
	submitForm(document.<portlet:namespace />fmSSS, url);

}


function exportarCuentasInterbankingOPS(ctaBcria,entidad,cEmail) {
	var op =  '<%=listOp%>';
	if(!cEmail){
	   window.location.href ='/txtservlet/?reporte=INTERBANKING_OPS'
		+'&in='+op +'&ctabcria='+ctaBcria+'&entidad='+entidad;
	}else{
		 window.location.href ='/txtservlet/?reporte=INTERBANKING_OPS_EMAIL'
				+'&in='+op +'&ctabcria='+ctaBcria+'&entidad='+entidad;
	}   
}

</script>