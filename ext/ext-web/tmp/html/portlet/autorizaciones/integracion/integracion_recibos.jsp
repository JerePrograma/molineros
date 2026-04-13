<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

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

portlet_name="autorizaciones";

String ejecutar = ParamUtil.getString(request, "ejecutar");



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
 	  <legend>Gestión de Recibos</legend>
 	  <table class="lfr-table">		 
		 <tr valign="top">
		   <td ><label>Lote:</label></td>
		   <td><input id="<portlet:namespace />nroLote" name="<portlet:namespace />nroLote" size="20" maxlength="20" type="text" value=''/></td>
		 
<!--  		   		<td><input id="<portlet:namespace />avisoLote" name="<portlet:namespace />avisoLote" type="button" value="Avisos"  onclick="avisoTransferenciaOP()"/></td>  -->

		   		<td><input id="<portlet:namespace />reciboLote" name="<portlet:namespace />reciboLote" type="button" value="Recibos"  onclick="ingresoRecibosOP()"/></td>
 	     </tr>
 	     <tr>
 	       <td colspan="2">
 	           <legend>Subir Recibos desde Excel</legend>
		       <table class="lfr-table">
		          <tr>
		            <td  align="left">
				       <input type="file" name="archivo"/>
			        </td>
			        
			        <td>
			         <a href="javascript:void(0)" onclick="help(event, 'helpArchivoRecibos')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
			        </td>
			        
			        <td align="left">
				     <input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivoSectorLiquidaciones()"/>
			        </td>			
		          </tr>	
		        </table>
 	      
 	       </td>
 	     </tr>
 	  </table>   
   </fieldset>
</td>
</tr>
</table>

<input id="<portlet:namespace />fmSSSH" name="<portlet:namespace />fmSSSH" type="hidden" value="" />
</form>

<div id="helpArchivoRecibos" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  El Archivo debe ser de extensión XLS(Excel) y contener 2 columnas. La primera con el nro. de OP y la segunda el nro. de recibo a asociar</div>
</div>



<script type="text/javascript">

<% if (ejecutar != null && ejecutar.equalsIgnoreCase("true")) {%>
	exportarCuentasInterbanking(); 

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


function ingresoRecibosOP(){
	var idLote=jQuery("#<portlet:namespace />nroLote").val();
	if(idLote!=null && ""!=idLote){
	   popup = Liferay.Popup({title:"Ingreso de Recibos de  Ordenes de Pago",modal:true,width:420});
	   var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
   	   url = url+'&<%= Constants.CMD %>'+'='+'ingreso_recibo_op'+'&nrolote='+idLote;
   	   jQuery(popup).load(url);
    } else {
       alert("Debe Ingresar un Nro. de Lote");	
    }  
}

function <portlet:namespace />uploadArchivoSectorLiquidaciones() {
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/integracion_upload_recibos';
	document.<portlet:namespace />fmSSS.method = 'post';
	submitForm(document.<portlet:namespace />fmSSS, url);
}

</script>