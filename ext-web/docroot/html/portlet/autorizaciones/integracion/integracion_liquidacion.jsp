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

boolean flag = ParamUtil.getBoolean(request, "flagOcultar");
String ejecutar = ParamUtil.getString(request, "ejecutar");
Integer ctabcria=ParamUtil.getInteger(request, "ctabcria");
boolean cEmail=ParamUtil.getBoolean(request, "cemail");



String listOp = ParamUtil.getString(request, ConstantesInterbanking.PARAMETRO);


Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
fechaDesde.add(Calendar.MONTH, -1);
Calendar current = CalendarFactoryUtil.getCalendar();

%>
</form>

<form action="" method="get" name="<portlet:namespace />fmSSS" enctype="multipart/form-data">

<table class="lfr-table">
<%if (flag == false) {%>
<tr>
<td align="left">
   <fieldset>
	<legend>Lotes Superintendencia</legend>
		<table class="lfr-table">
<!--  		
		  <tr>
            <td  align="left">
				       <input type="file" name="archivoFTP"/>
				       <a href="javascript:void(0)" onclick="help(event, 'helpArchivoFTP')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
				       <input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivoSectorLiquidaciones()"/>
	        </td>
	        <td align="left">
				    
			</td>			
          </tr>
-->          
		  <tr>
		   <td>
			  <div id="<portlet:namespace />lotes_sss">
						<jsp:include page='/html/portlet/autorizaciones/integracion/integracion_archivos_sss_result_by_cabecera.jsp' />  
			  </div>
			</td>	
		  </tr>
		</table>
	</fieldset>	
</td>
<%} %>
<td>
   
     <%if (flag == false) {%>
<!--   	  <legend>Avisos de Transferencias/Gestión de Recibos</legend> -->
 	 <%}else{%>
 	    <fieldset class="block-labels">
 	       <legend>Archivo de transferencias a interbaking </legend>
 	  <%}%>
 	  <table class="lfr-table">		 
		 <tr valign="top">
		   
		    <%if (flag == false) {%>
<!-- 		   		<td><input id="<portlet:namespace />avisoLote" name="<portlet:namespace />avisoLote" type="button" value="Avisos"  onclick="avisoTransferenciaOP()"/></td> -->
		   	 <%} else {%>
		   	   <td ><label>Lote:</label></td>
		       <td><input id="<portlet:namespace />nroLote" name="<portlet:namespace />nroLote" size="20" maxlength="20" type="text" value=''/></td>
<!--  		   	 	<td><input id="<portlet:namespace />avisoLote" name="<portlet:namespace />avisoLote" type="button" value="Avisos"  onclick="avisoTransferenciaOpInterbanking()"/></td> -->
		   	 <%}%>
		   <%if (flag == false) {%>
<!--  	   		<td><input id="<portlet:namespace />reciboLote" name="<portlet:namespace />reciboLote" type="button" value="Recibos"  onclick="ingresoRecibosOP()"/></td> -->
		   	<%}%>
 	     </tr>
 	     <tr>
 	       <td colspan="2">
 	       <%if (flag == false) {%>
<!--   	       
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
-->		        
 	       <%}else { %>
 	       		<br>
 	       	    <!--  <legend>Planilla de cálculo casar ll del final</legend> -->
 	       	    <legend>Exportar Archivo para Transferir</legend> 

				<table class="lfr-table">
		 		 </tr>
				<input id="<portlet:namespace />ExportarOP" value="Exportar OP para procesar" title="Exportar Excell Con Ordenes de pago" type="button" onClick="javascript:exportarCuentasFiltroInterbanking();"/>								
				</tr>
				<tr>	
				 <legend>Importar Archivo Ajustado</legend>	
			 	<tr>
			 <!-- td  align="left"-->
				       <input type="file" name="archivo"/>
				       <a href="javascript:void(0)" onclick="help(event, 'helpArchivoFTP')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
				       <input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivoInterbanking()"/>
	        <!-- /td-->	
	        
	             
	              <td>
				     <label>Con EMAIL de aviso:</label>
				  </td>
	              <td>
	                 &nbsp;&nbsp;<input type="checkbox" id="<portlet:namespace />cE" name="<portlet:namespace />cE"/>
	              </td>	
	        
	        							
				</tr>							
			</table>
<!--  		</fieldset> -->	
                </fieldset>
 	       
 	       <%} %>     
 	       </td>
 	     </tr>
 	  </table>   
  
</td>
</tr>
</table>

<input id="<portlet:namespace />fmSSSH" name="<portlet:namespace />fmSSSH" type="hidden" value="" />
</form>

<div id="helpArchivoRecibos" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  El Archivo debe ser de extensión XLS(Excel) y contener 2 columnas. La primera con el nro. de OP y la segunda el nro. de recibo a asociar</div>
</div>

<div id="helpArchivoFTP" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  La primer columna del archivo debe ser el nro. de Orden de Pago.
</div>


<script type="text/javascript">

<% if (ejecutar != null && ejecutar.equalsIgnoreCase("true")) {%>
	exportarCuentasInterbanking(<%=cEmail%>); 

<%}else if (ejecutar != null && ejecutar.equalsIgnoreCase("trueOP")){%> 
   exportarCuentasInterbankingOPS(<%=ctabcria%>,<%=cEmail%>);
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





function exportarCuentasFiltroInterbanking() {
	var idLote=jQuery("#<portlet:namespace />nroLote").val();
	if(idLote.length == 0){
		   alert("Debe ingresar un Lote");
		   return false;
	}
	var idLote=jQuery("#<portlet:namespace />nroLote").val();
	window.location.href ='/xlsservlet/?reporte=ORDENES_PAGO'
		+'&id_lote='+idLote;
}
function <portlet:namespace />uploadArchivoInterbanking() {
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/autorizaciones/integracion_upload_archivos_interbanking';
	document.<portlet:namespace />fmSSS.method = 'post';
	submitForm(document.<portlet:namespace />fmSSS, url);

  }

function exportarCuentasInterbanking(cEmail) {
	var op =  '<%=listOp%>';
	
	if(!cEmail){
	   window.location.href ='/txtservlet/?reporte=EXPORTAR_CUENTAS_INTERBANKING'
		+'&in='+op;
	}else{
		 window.location.href ='/txtservlet/?reporte=EXPORTAR_CUENTAS_INTERBANKING_EMAIL'
				+'&in='+op;
	} 

}

function exportarCuentasInterbankingOPS(ctaBcria,cEmail) {
	var op =  '<%=listOp%>';
	if(!cEmail){
	    window.location.href ='/txtservlet/?reporte=EXPORTAR_CUENTAS_INTERBANKING_OPS'
		+'&in='+op +'&ctabcria='+ctaBcria;
	}else{
		 window.location.href ='/txtservlet/?reporte=EXPORTAR_CUENTAS_INTERBANKING_OPS_EMAIL'
				+'&in='+op +'&ctabcria='+ctaBcria;
	}
	 

}

</script>