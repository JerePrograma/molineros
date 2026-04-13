<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionDetalleDR" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%

	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	IntegracionDetalleDR registro=(IntegracionDetalleDR)request.getSession().getAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_REGISTRO_EN_EDICION);
	if (registro == null ) {
		registro = (IntegracionDetalleDR) portletSession.getAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_REGISTRO_EN_EDICION , PortletSession.PORTLET_SCOPE);
	}
	NumberFormat nf = new DecimalFormat("#0.00");
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	boolean esEdicion = true;
	if(viewStr==null){
		viewStr=ParamUtil.getString(request, "view");
	}
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	int id_registro=registro!=null && registro.getId()!= null ?(int)registro.getId():0;
	if(registro==null){
		registro= new IntegracionDetalleDR();
	} 
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	Calendar fechaTransfI = CalendarFactoryUtil.getCalendar();
	if(registro.getFechaTransferenciaI() !=null){
		fechaTransfI.setTime(registro.getFechaTransferenciaI());
	}
	Calendar fechaTransfII = CalendarFactoryUtil.getCalendar();
	if(registro.getFechaTransferenciaII() !=null){
		fechaTransfII.setTime(registro.getFechaTransferenciaII());
	}
	
	String descError="";
	if(registro.getError()!=null && !"".equalsIgnoreCase(registro.getError()) && !"OK".equalsIgnoreCase(registro.getError())){
		String[] vErrores = registro.getError().split("-");
		for(int i=1;i<vErrores.length;i++){
			descError += vErrores[i]+" - " +IntegracionServiceUtil.getDescripcionError(Integer.parseInt(vErrores[i]))+"<br>";		
		}
//		descError = IntegracionServiceUtil.getDescripcionError(Integer.parseInt(registro.getError()));
	}else{
		descError = IntegracionServiceUtil.getDescripcionError(registro.analizaError());
	}
%>

<form action="" method="post" name="<portlet:namespace />fmS">

	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
		value="" />
  
	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	<liferay-ui:error key="errorAfiliadoNull"
		message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
		
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />

	<fieldset class="block-labels"> 
		<legend>Detalle</legend>
		
		<fieldset class="block-labels"> 
		<legend>DR ENVIO</legend>
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		   <tr>
		   
		       <td><liferay-ui:message key="id" />:</td> 
			   <td><input id="<portlet:namespace />detalleId"
					name="<portlet:namespace />detalleId" size="10"
					maxlength="20" type="text"
					readonly="readonly"
					value="<%=registro.getId() ==null?"":registro.getId() %>" />
				</td>
		   
		       <td>Clave:</td> 
			   <td><input id="<portlet:namespace />clave"
					name="<portlet:namespace />clave" size="11"
					maxlength="20" type="text" readonly="readonly" 
					value="<%=registro.getClave() ==null?"":registro.getClave() %>" />
				</td>
				
			   <td>Tipo Archivo:</td> 
			   <td><input id="<portlet:namespace />tipoArchivo"
					name="<portlet:namespace />tipoArchivo" size="2"
					maxlength="2" type="text" readonly="readonly"
					value="<%=registro.getTipoArchivo() ==null?"":registro.getTipoArchivo() %>" />
				</td>
				
			   <td>Período Presentación:</td> 
			   <td><input id="<portlet:namespace />periodo"
					name="<portlet:namespace />periodo" size="6"
					maxlength="6" type="text" readonly="readonly"
					value="<%=registro.getPeriodoPresentacion() ==null?"":registro.getPeriodoPresentacion() %>" />
				</td>
				<td>Período Prestación:</td> 
			    <td><input id="<portlet:namespace />periodoPrestacion"
					name="<portlet:namespace />periodoPrestacion" size="6"
					maxlength="6" type="text" readonly="readonly"
					value="<%=registro.getPeriodoPrestacion() ==null?"":registro.getPeriodoPrestacion() %>" />
				</td>
				<td>Cuil:</td> 
			    <td><input id="<portlet:namespace />cuil"
					name="<portlet:namespace />cuil" size="11"
					maxlength="11" type="text" readonly="readonly"
					value="<%=registro.getCuil() ==null?"":registro.getCuil() %>" />
				</td>
				
          </tr>
          <tr>
             <td>Código Práctica:</td> 
			 <td><input id="<portlet:namespace />prestacion"
					name="<portlet:namespace />prestacion" size="3"
					maxlength="3" type="text" readonly="readonly"
					value="<%=registro.getPrestacionCodigo() ==null?"":registro.getPrestacionCodigo() %>" />
			 </td>
             <td>Subsidiado:</td> 
			    <td><input id="<portlet:namespace />subsidiado"
					name="<portlet:namespace />subsidiado" size="13"
					maxlength="13" type="text" readonly="readonly"
					value="<%=registro.getImporteLiquidado() ==null?"":nf.format(registro.getImporteLiquidado()) %>" />
			 </td>
			 <td>Solicitado:</td> 
			    <td><input id="<portlet:namespace />solicitado"
					name="<portlet:namespace />solicitado" size="13"
					maxlength="13" type="text" readonly="readonly"
					value="<%=registro.getImporteSolicitado() ==null?"":nf.format(registro.getImporteSolicitado()) %>" />
			 </td>
			 <td>Nro AFIP:</td> 
			    <td><input id="<portlet:namespace />nroAfip"
					name="<portlet:namespace />snroAfip" size="4"
					maxlength="4" type="text" readonly="readonly"
					value="<%=registro.getNroEnvioAfip() ==null?"":registro.getNroEnvioAfip() %>" />
			 </td>
          </tr>
          
          
          <tr>
             <td>Tipo Cpbte:</td> 
			 <td><input id="<portlet:namespace />cpbte_tipo"
					name="<portlet:namespace />cpbte_tipo" size="3"
					maxlength="3" type="text" readonly="readonly"
					value="<%=registro.getComprobanteTipo() ==null?"":registro.getComprobanteTipo() %>" />
			 </td>
             <td>Punto de Venta:</td> 
			    <td><input id="<portlet:namespace />cpbte_pto_vta"
					name="<portlet:namespace />cpbte_pto_vta" size="5"
					maxlength="5" type="text" readonly="readonly"
					value="<%=registro.getComprobantePtoVta() ==null?"":registro.getComprobantePtoVta() %>" />
			 </td>
			 <td>Nro Cpbte::</td> 
			    <td><input id="<portlet:namespace />cpbte_nro"
					name="<portlet:namespace />cpbte_nro" size="13"
					maxlength="13" type="text" readonly="readonly"
					value="<%=registro.getComprobanteNro() ==null?"":registro.getComprobanteNro().toString() %>" />
			 </td>
          </tr>
          
        </table>
        </fieldset>
<!--Datos a Completar -->  
        <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">        
          <tr>
             <td>Cuit del CBU:</td> 
			 <td><input id="<portlet:namespace />cuit"
					name="<portlet:namespace />cuit" size="11"
					maxlength="11" type="text" 
					value="<%=registro.getCbuCuit() ==null?"":registro.getCbuCuit() %>" />
			 </td>
             <td>CBU:</td> 
			 <td><input id="<portlet:namespace />cbu"
					name="<portlet:namespace />cbu" size="22"
					maxlength="22" type="text" 
					value="<%=registro.getCbu() ==null?"":registro.getCbu() %>" />
			 </td>
             <td>O.Pago I:</td> 
			 <td><input id="<portlet:namespace />ordenPagoI"
					name="<portlet:namespace />ordenPagoI" size="10"
					maxlength="10" type="text" 
					value="<%=registro.getOrdenPagoI() ==null?"":registro.getOrdenPagoI() %>" />
			 </td>
			 <td>O.Pago II:</td> 
			 <td><input id="<portlet:namespace />ordenPagoII"
					name="<portlet:namespace />ordenPagoII" size="10"
					maxlength="10" type="text" 
					value="<%=registro.getOrdenPagoII() ==null?"":registro.getOrdenPagoII() %>" />
			 </td> 
          </tr>
         
          <tr>
            <td>
			     Transferencia I 
			 </td>
			 <td>  
			       <liferay-ui:input-date
						 dayParam="fechaTransIDia"
						 dayValue='<%=registro.getFechaTransferenciaI() !=null?fechaTransfI.get(Calendar.DAY_OF_MONTH ):-1%>'
						 dayNullable="<%= true %>" monthParam="fechaTransIMes"
						 monthValue='<%=registro.getFechaTransferenciaI()!=null?fechaTransfI.get(Calendar.MONTH ):-1%>'
						 monthNullable="<%= true %>" yearParam="fechaTransIAnio"
						 yearValue='<%=registro.getFechaTransferenciaI()!=null?fechaTransfI.get(Calendar.YEAR ):-1%>'
						 yearNullable="<%= true %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/> 
			 </td>
			 
			<td>
			     Transferencia II 
			 </td>
			 <td>  
			       <liferay-ui:input-date
						 dayParam="fechaTransIIDia"
						 dayValue='<%=registro.getFechaTransferenciaII() !=null?fechaTransfII.get(Calendar.DAY_OF_MONTH ):-1%>'
						 dayNullable="<%= true %>" monthParam="fechaTransIIMes"
						 monthValue='<%=registro.getFechaTransferenciaII()!=null?fechaTransfII.get(Calendar.MONTH ):-1%>'
						 monthNullable="<%= true %>" yearParam="fechaTransIIAnio"
						 yearValue='<%=registro.getFechaTransferenciaII()!=null?fechaTransfII.get(Calendar.YEAR ):-1%>'
						 yearNullable="<%= true %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/> 
						 
			 </td>
             <td>Cheque:</td> 
			 <td><input id="<portlet:namespace />cheque"
					name="<portlet:namespace />cheque" size="10"
					maxlength="10" type="text" 
					value="<%=registro.getCheque() ==null?"":registro.getCheque() %>" />
			 </td>			 
          </tr>
          <tr>
          
            <td>Transferido:</td> 
			    <td><input id="<portlet:namespace />importeTransferido"
					name="<portlet:namespace />importeTransferido" size="13"
					maxlength="13" type="text" 
					value="<%=registro.getImporteTransferido() ==null?"":nf.format(registro.getImporteTransferido()).replace(",",".") %>" />
			 </td>
			 <td>Ret.Ganancias:</td> 
			    <td><input id="<portlet:namespace />retGanancias"
					name="<portlet:namespace />retGanancias" size="13"
					maxlength="13" type="text" 
					value="<%=registro.getRetencionGanancias() ==null?"":nf.format(registro.getRetencionGanancias()).replace(",",".")%>" />
			 </td>
			 <td>Ret.IIBB:</td> 
			    <td><input id="<portlet:namespace />retIIBB"
					name="<portlet:namespace />retIIBB" size="13"
					maxlength="13" type="text" 
					value="<%=registro.getRetencionIIBB() ==null?"":nf.format(registro.getRetencionIIBB()).replace(",",".")%>" />
			 </td>
			 <td>Otras Ret:</td> 
			    <td><input id="<portlet:namespace />otrasRet"
					name="<portlet:namespace />otrasRet" size="13"
					maxlength="13" type="text" 
					value="<%=registro.getOtrasRetenciones() ==null?"":nf.format(registro.getOtrasRetenciones()).replace(",",".")%>" />
			 </td>
          </tr>
          <tr>
            <td>Aplicado:</td> 
			    <td><input id="<portlet:namespace />importeAplicado"
					name="<portlet:namespace />importeAplicado" size="13"
					maxlength="13" type="text" 
					value="<%=registro.getImporteAplicado() ==null?"":nf.format(registro.getImporteAplicado()).replace(",",".")%>" />
			 </td>
			 <td>Fdos Prop:</td> 
			    <td><input id="<portlet:namespace />importeFondosPropios"
					name="<portlet:namespace />importeFondosPropios" size="13"
					maxlength="13" type="text" 
					value="<%=registro.getFondosPropiosDiscapacidad() ==null?"":nf.format(registro.getFondosPropiosDiscapacidad()).replace(",",".")%>" />
			 </td>
			 <td>Otras Ctas:</td> 
			    <td><input id="<portlet:namespace />importeOtraCuenta"
					name="<portlet:namespace />importeOtraCuenta" size="13"
					maxlength="13" type="text" 
					value="<%=registro.getFondosPropiosOtraCuenta() ==null?"":nf.format(registro.getFondosPropiosOtraCuenta()).replace(",",".")%>" />
			 </td>
			 <td>Recibo:</td> 
			    <td><input id="<portlet:namespace />recibo"
					name="<portlet:namespace />recibo" size="8"
					maxlength="8" type="text" 
					value="<%=registro.getNroRecibo() ==null?"":registro.getNroRecibo() %>" />
			 </td>
          </tr>
          <tr>
            <td>Trasladado:</td> 
			    <td><input id="<portlet:namespace />importeTrasladado"
					name="<portlet:namespace />importeTrasladado" size="13"
					maxlength="13" type="text" 
					value="<%=registro.getImporteTrasladado() ==null?"":nf.format(registro.getImporteTrasladado()).replace(",",".")%>" />
			 </td>
			 <td>Devuelto:</td> 
			    <td><input id="<portlet:namespace />importeDevuelto"
					name="<portlet:namespace />importeDevuelto" size="13"
					maxlength="13" type="text" 
					value="<%=registro.getImporteDevuelto() ==null?"":nf.format(registro.getImporteDevuelto()).replace(",",".")%>" />
			 </td>
			 <td>No Aplic:</td> 
			    <td><input id="<portlet:namespace />importeNoAplicado"
					name="<portlet:namespace />importeNoAplicado" size="13"
					maxlength="13" type="text" 
					value="<%=registro.getSaldoNoAplicado() ==null?"":nf.format(registro.getSaldoNoAplicado()).replace(",",".")%>" />
			 </td>
			 <td>Recupero:</td> 
			    <td><input id="<portlet:namespace />importeRecupero"
					name="<portlet:namespace />importeRecupero" size="13"
					maxlength="13" type="text" 
					value="<%=registro.getRecuperoFondosPropios() ==null?"":nf.format(registro.getRecuperoFondosPropios()).replace(",",".")%>" />
			 </td>
          </tr>
          <tr>
            <td>Observaciones:</td> 
            <td colspan="4">
               <textarea rows="5" cols="80"
			   id="<portlet:namespace />observaciones"
			   name="<portlet:namespace />observaciones"><%= registro.getObservaciones() != null ? registro.getObservaciones() : "" %>
			   </textarea>
			</td>
			<td colspan="3">
			  <%if(descError.length()>0 ){%>
			     <p style="background-color:#F1948A;"><%=descError%></p>
			  <%}%>
			</td>   
		</td>
          </tr>
       </table>
       
       <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" 
	   />
       
    </fieldset>      		   
</form>

<script type="text/javascript">

<%String success = (String) portletSession.getAttribute("Success");
if (success != null&& success.equals("Success")) {%>
	   if(popupINT){
		   Liferay.Popup.close(popupINT);
	   }
<%}%>

function <portlet:namespace />validarCampos(){
	return true;
}

function <portlet:namespace />salvarEdicion(){
	var idDetalle = jQuery("#<portlet:namespace />detalleId").val();
	var cuit = jQuery("#<portlet:namespace />cuit").val();
	var cbu = jQuery("#<portlet:namespace />cbu").val();
	var ordenI = jQuery("#<portlet:namespace />ordenPagoI").val();
	var ordenII = jQuery("#<portlet:namespace />ordenPagoII").val();
	
	var fechaTransIDia=jQuery('#<portlet:namespace />fechaTransIDia').val();
	var fechaTransIMes=jQuery('#<portlet:namespace />fechaTransIMes').val();
	var fechaTransIAnio=jQuery('#<portlet:namespace />fechaTransIAnio').val();
	var fechaTransIIDia=jQuery('#<portlet:namespace />fechaTransIIDia').val();
	var fechaTransIIMes=jQuery('#<portlet:namespace />fechaTransIIMes').val();
	var fechaTransIIAnio=jQuery('#<portlet:namespace />fechaTransIIAnio').val();
	var cheque=jQuery('#<portlet:namespace />cheque').val();
	var importeTransferido=jQuery('#<portlet:namespace />importeTransferido').val();
	var retGcias=jQuery('#<portlet:namespace />retGanancias').val();
	var retIIBB=jQuery('#<portlet:namespace />retIIBB').val();
	var otrasRet=jQuery('#<portlet:namespace />otrasRet').val();
	var importeAplicado=jQuery('#<portlet:namespace />importeAplicado').val();
	var importeFondosPropios=jQuery('#<portlet:namespace />importeFondosPropios').val();
	var importeOtraCuenta=jQuery('#<portlet:namespace />importeOtraCuenta').val();
	var recibo=jQuery('#<portlet:namespace />recibo').val();
	
	var importeTrasladado=jQuery('#<portlet:namespace />importeTrasladado').val();
	var importeDevuelto=jQuery('#<portlet:namespace />importeDevuelto').val();
	var importeNoAplicado=jQuery('#<portlet:namespace />importeNoAplicado').val();
	var importeRecupero=jQuery('#<portlet:namespace />importeRecupero').val();
	var observaciones=jQuery('#<portlet:namespace />observaciones').val();
	
	if (<portlet:namespace />validarCampos()) {
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/autorizaciones/integracion_editar" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
		'<liferay-portlet:param name="idDetalle" value="__idDetalle"/>'+
		'<liferay-portlet:param name="cuit" value="__cuit"/>'+
		'<liferay-portlet:param name="cbu" value="__cbu"/>'+
		'<liferay-portlet:param name="ordenI" value="__ordenI"/>'+
		'<liferay-portlet:param name="ordenII" value="__ordenII"/>'+
		'<liferay-portlet:param name="fechaTransIDia" value="__fechaTransIDia"/>'+
		'<liferay-portlet:param name="fechaTransIMes" value="__fechaTransIMes"/>'+
		'<liferay-portlet:param name="fechaTransIAnio" value="__fechaTransIAnio"/>'+
		'<liferay-portlet:param name="fechaTransIIDia" value="__fechaTransIIDia"/>'+
		'<liferay-portlet:param name="fechaTransIIMes" value="__fechaTransIIMes"/>'+
		'<liferay-portlet:param name="fechaTransIIAnio" value="__fechaTransIIAnio"/>'+
		'<liferay-portlet:param name="cheque" value="__cheque"/>'+
		'<liferay-portlet:param name="importeTransferido" value="__importeTransferido"/>'+
		'<liferay-portlet:param name="retGcias" value="__retGcias"/>'+
		'<liferay-portlet:param name="retIIBB" value="__retIIBB"/>'+
		'<liferay-portlet:param name="otrasRet" value="__otrasRet"/>'+
		'<liferay-portlet:param name="importeAplicado" value="__importeAplicado"/>'+
		'<liferay-portlet:param name="importeFondosPropios" value="__importeFondosPropios"/>'+
		'<liferay-portlet:param name="importeOtraCuenta" value="__importeOtraCuenta"/>'+
		'<liferay-portlet:param name="recibo" value="__recibo"/>'+
		'<liferay-portlet:param name="importeTrasladado" value="__importeTrasladado"/>'+
		'<liferay-portlet:param name="importeDevuelto" value="__importeDevuelto"/>'+
		'<liferay-portlet:param name="importeNoAplicado" value="__importeNoAplicado"/>'+
		'<liferay-portlet:param name="importeRecupero" value="__importeRecupero"/>'+
		'<liferay-portlet:param name="observaciones" value="__observaciones"/>'+
		'</liferay-portlet:renderURL>';
		
		 url = url.replace("__idDetalle",encodeURI(idDetalle));
		 url = url.replace("__cuit",encodeURI(cuit));
		 url = url.replace("__cbu",encodeURI(cbu));
		 url = url.replace("__ordenI",encodeURI(ordenI));
		 url = url.replace("__ordenII",encodeURI(ordenII));
		 url = url.replace("__fechaTransIDia",encodeURI(fechaTransIDia));
		 url = url.replace("__fechaTransIMes",encodeURI(fechaTransIMes));
		 url = url.replace("__fechaTransIAnio",encodeURI(fechaTransIAnio));
		 url = url.replace("__fechaTransIIDia",encodeURI(fechaTransIIDia));
		 url = url.replace("__fechaTransIIMes",encodeURI(fechaTransIIMes));
		 url = url.replace("__fechaTransIIAnio",encodeURI(fechaTransIIAnio));
		 url = url.replace("__cheque",encodeURI(cheque));
		 url = url.replace("__importeTransferido",encodeURI(importeTransferido));
		 url = url.replace("__retGcias",encodeURI(retGcias));
		 url = url.replace("__retIIBB",encodeURI(retIIBB));
		 url = url.replace("__otrasRet",encodeURI(otrasRet));
		 url = url.replace("__importeAplicado",encodeURI(importeAplicado));
		 url = url.replace("__importeFondosPropios",encodeURI(importeFondosPropios));
		 url = url.replace("__importeOtraCuenta",encodeURI(importeOtraCuenta));
		 url = url.replace("__recibo",encodeURI(recibo));
		 
		 url = url.replace("__importeTrasladado",encodeURI(importeTrasladado));
		 url = url.replace("__importeDevuelto",encodeURI(importeDevuelto));
		 url = url.replace("__importeNoAplicado",encodeURI(importeNoAplicado));
		 url = url.replace("__importeRecupero",encodeURI(importeRecupero));
		 url = url.replace("__observaciones",encodeURI(observaciones));
		 
		jQuery(popupINT).load(url);
	}
	return false;		
}


</script>

