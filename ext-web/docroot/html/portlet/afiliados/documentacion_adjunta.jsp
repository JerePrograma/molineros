<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%
Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
fechaHoy.setTime(new Date());
String view=(String)request.getParameter("view");
String cuil_titular=request.getParameter("cuil_titular");
String inte = request.getParameter("inte");

List<Documento> documentosList = (ArrayList<Documento>) portletSession.getAttribute(WebKeysAfiliados.DOCUMENTOS_EN_SESSION,PortletSession.APPLICATION_SCOPE);

if (documentosList == null) {
	documentosList = TraeListasServiceUtil.getDocumentos();
	portletSession.setAttribute(WebKeysAfiliados.DOCUMENTOS_EN_SESSION,documentosList,PortletSession.APPLICATION_SCOPE);	
}
%>
<portlet:defineObjects/>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="documentacion-adjunta" /></legend>
				<%if(null==view || !view.equals("true")){ %>
				<table class="lfr-table">
					<tr>				
						<td>
							<liferay-ui:message key="documento" />
						</td>		
						<td colspan="5">
							<select name="<portlet:namespace/>doc" id="<portlet:namespace/>doc"
							    onchange="<portlet:namespace />habilitaComponentes()">									
									<%
										for (Documento doc : documentosList) {
									%>
										<option value="<%= doc.getId_documento()%>|<%= doc.getId_motivo_baja()%>"><%=doc.getDescripcion()%></option>
									<%
									}
									%>
							</select>		
						</td>
						
						<td>
						   <label id="<portlet:namespace />lbCertificado" name="<portlet:namespace />lbCertificado">Cod. CUD</label>
						</td>
						
						<td><input id="<portlet:namespace />nroCertificado"
					          name="<portlet:namespace />nroCertificado" size="50"
					          maxlength="50" type="text" value="" />
					    </td>      
					</tr>
					<tr><td>&nbsp;&nbsp;</td></tr>
				</table>
				<table class="lfr-table">											
					<tr>
						<td><label><liferay-ui:message key="ingre-fecha" /></label></td>
						<td> 
							<liferay-ui:input-date
								dayParam="fechaIngresoDocumentoDia"
								dayValue="<%= fechaHoy.get(Calendar.DATE)%>"
								monthParam="fechaIngresoDocumentoMes"
								monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"								
								yearParam="fechaIngresoDocumentoAnio"
								yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 25 %>"
								yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR)+10%>"
								firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>"
							/>				
						</td>
						<td><label><liferay-ui:message key="egreso-fecha" /></label></td>						
						<td>							
							<liferay-ui:input-date
								monthParam="fechaEgresoDocumentoMes"								
								monthNullable="true"								
								dayParam="fechaEgresoDocumentoDia"								
								dayNullable="true"
								yearParam="fechaEgresoDocumentoAnio"								
								yearNullable="true"
								yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 25%>"
								firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>"
							/>
						</td>
						<td colspan="1">
							<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />grabarDocumentacion();" />
							<input type="hidden" id="<portlet:namespace />editarDoc" name="<portlet:namespace />editarDoc" value=""/>
							<input type="hidden" id="<portlet:namespace />id" name="<portlet:namespace />id" value=""/>
							<input type="hidden" id="<portlet:namespace />intePopUp" name="<portlet:namespace />intePopUp" value=""/>							
						</td>
						<td colspan="1">
							<input type="button" value="<liferay-ui:message key="limpiar-campos" />" onClick="<portlet:namespace />limpiarCamposDocumentacion();" />
						</td>
						<td>
						<div id="subsidio_fallecimiento">							
							<input type="button" value="<liferay-ui:message key="recibo-subsidio-fallecimiento" />" onClick="<portlet:namespace />imprimirReciboSubsidioFalle();" />							
						</div>
						</td>						
					</tr>
				</table>				
				<div align="center" id="<portlet:namespace />buscandoDocumentacion">
					<table style="align:center;">
						<tr>
							<td><liferay-ui:message key='buscando'/></td>
							<td align="center">					
								<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>		
				</div>	
				<%} %>
				<div align="center" id="<portlet:namespace />documentos">		
					<jsp:include page='documentacion_search_result.jsp'>
						<jsp:param name="view" value="<%=view%>" />					   
					</jsp:include>
				</div>
		</fieldset>
			
<script type="text/javascript">	
	jQuery('#<portlet:namespace />buscandoDocumentacion').hide();
	
	function <portlet:namespace />imprimirReciboSubsidioFalle(){
		window.location.href ='/odtservlet/?accion=RECIBO_FALLECIMIENTO';	
	}
			
	function <portlet:namespace />grabarDocumentacion(){
		if(!<portlet:namespace />validarDatosDocumentacion()){			
			return false;
		}else{
			jQuery('#<portlet:namespace />buscandoDocumentacion').show();	
			var id_doc=jQuery('#<portlet:namespace/>doc').val();
			var id=jQuery('#<portlet:namespace />id').val();
			var diaIngreso=	jQuery('#<portlet:namespace />fechaIngresoDocumentoDia').val();
			var mesIngreso= parseInt(jQuery('#<portlet:namespace />fechaIngresoDocumentoMes').val())+1;
			var anioIngreso=jQuery('#<portlet:namespace />fechaIngresoDocumentoAnio').val();		
			var fechaIngreso=diaIngreso+'/'+mesIngreso+'/'+anioIngreso;			
			var diaEgreso=jQuery('#<portlet:namespace />fechaEgresoDocumentoDia').val();
			var mesEgreso=parseInt(jQuery('#<portlet:namespace />fechaEgresoDocumentoMes').val())+1;			
			var anioEgreso=jQuery('#<portlet:namespace />fechaEgresoDocumentoAnio').val();
			var fechaEgreso=diaEgreso+'/'+mesEgreso+'/'+anioEgreso;
			var editar=jQuery('#<portlet:namespace />editarDoc').val();
			var cuil_titular_ = '<%=cuil_titular%>';
			var inte_ = '<%=inte%>';
			var certificado=jQuery('#<portlet:namespace/>nroCertificado').val();
			if(editar == true){
				inte_ = jQuery('#<portlet:namespace />intePopUp').val();	
			}
			
			
<%-- 			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/grabar_documentacion&id_documentacion='+id_doc+
			'&fechaIngreso='+fechaIngreso+'&fechaEgreso='+fechaEgreso+'&cuil_titular='+<%=cuil_titular%>+'&inte='+<%=inte%>+'&editarDoc='+editar+'&id='+id;						
			jQuery('#<portlet:namespace />documentos').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoDocumentacion').hide();            															
																			   }
															   ); --%>
															   
		  var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="/afiliados/grabar_documentacion" />'+
			'<liferay-portlet:param name="id_documentacion" value="__id_documentacion"/>'+
			'<liferay-portlet:param name="fechaIngreso" value="__fechaIngreso"/>'+
			'<liferay-portlet:param name="fechaEgreso" value="__fechaEgreso"/>'+
			'<liferay-portlet:param name="editarDoc" value="__editar"/>'+
			'<liferay-portlet:param name="cuil_titular" value="__cuil_titular"/>'+
			'<liferay-portlet:param name="inte" value="__inte"/>'+
			'<liferay-portlet:param name="id_documentacion" value="__id_documentacion"/>'+
			'<liferay-portlet:param name="afi_id" value="__afi_id"/>'+
			'<liferay-portlet:param name="certificado" value="__certificado"/>'+
		    '</liferay-portlet:renderURL>';
		    url = url.replace("__id_documentacion",encodeURI(id_doc));
		    url = url.replace("__fechaIngreso",encodeURI(fechaIngreso));
		    url = url.replace("__fechaEgreso",encodeURI(fechaEgreso));
		    url = url.replace("__cuil_titular", cuil_titular_);
		    url = url.replace("__inte", inte_);
		    url = url.replace("__afi_id", id);	
		    url = url.replace("__editar", editar); 
		    url = url.replace("__certificado",encodeURI(certificado));
		    jQuery('#<portlet:namespace />documentos').load(url, function() {
				jQuery('#<portlet:namespace />buscandoDocumentacion').hide();            															
	   		});											   
							
		    
		
			
			<portlet:namespace />limpiarCamposDocumentacion();
			
		
			
		}				
	}	
	
	function borraDocumentacion(id_doc, ingre_fecha, egre_fecha, id){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/grabar_documentacion&id_documentacion='+id_doc+
			'&fechaIngreso='+ingre_fecha+'&cuil_titular='+<%=cuil_titular%>+'&inte='+<%=inte%>+'&borrarDoc=true'+'&afi_id='+id;
			jQuery('#<portlet:namespace />documentos').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoDocumentacion').hide();            															
																			   }
															   );
		}	
	}
	
	function editaDocumentacion(id_doc, ingre_fecha, baja_fecha, id_motivo_baja, inte, id,codigo_cud){
		//Marco que es edit
		jQuery('#<portlet:namespace />editarDoc').val(true);
		
		if(id_motivo_baja == 0){
			if(id_doc == 4){
				id_motivo_baja = 4;
			}
			if(id_doc == 5){
				id_motivo_baja = 15;
			}	
			if(id_doc == 19){
				id_motivo_baja = 15;
			}
			if(id_doc == 15){
				id_motivo_baja = 15;
			}
			if(id_doc == 16){
				id_motivo_baja = 110;
			}
		}
		
		//lleno los campos
		var valor = id_doc+'|'+id_motivo_baja;

		jQuery('#<portlet:namespace/>doc').val(id_doc+'|'+id_motivo_baja);
		/* jQuery('#<portlet:namespace/>doc option[value="'+id_doc+'|'+id_motivo_baja+'"]'); */
		jQuery("#<portlet:namespace/>doc option[value="+ valor +"]").attr("selected",true);
		var diaIngreso=ingre_fecha.substring(0,2);
		var mesIngreso=ingre_fecha.substring(3,5);		
		if(mesIngreso.substring(0,1)==0){			
			mesIngreso=mesIngreso.substring(1,2);
		}		
		var anioIngreso=ingre_fecha.substring(6,10);
		jQuery('#<portlet:namespace />fechaIngresoDocumentoDia').val(parseInt(diaIngreso));
		jQuery('#<portlet:namespace />fechaIngresoDocumentoMes').val(parseInt(mesIngreso)-1);		
		jQuery('#<portlet:namespace />fechaIngresoDocumentoAnio').val(parseInt(anioIngreso));
		
		jQuery('#<portlet:namespace />id').val(id);
		
		jQuery('#<portlet:namespace />intePopUp').val(inte);
		
		//deshabilito los campos
		/*jQuery('#<portlet:namespace/>doc').attr("disabled",true);	
		jQuery('#<portlet:namespace />fechaIngresoDocumentoDia').attr("disabled",true);
		jQuery('#<portlet:namespace />fechaIngresoDocumentoMes').attr("disabled",true);
		jQuery('#<portlet:namespace />fechaIngresoDocumentoAnio').attr("disabled",true);*/

		//lleno los campos
		var diaEgreso=baja_fecha.substring(0,2);		
		var mesEgreso=baja_fecha.substring(3,5);		
		if(mesEgreso.substring(0,1)==0){			
			mesEgreso=mesEgreso.substring(1,2);
		}		
		var anioEgreso=baja_fecha.substring(6,10);
		jQuery('#<portlet:namespace />fechaEgresoDocumentoDia').val(parseInt(diaEgreso));
		jQuery('#<portlet:namespace />fechaEgresoDocumentoMes').val(parseInt(mesEgreso)-1);		
		jQuery('#<portlet:namespace />fechaEgresoDocumentoAnio').val(parseInt(anioEgreso));
		jQuery('#<portlet:namespace />nroCertificado').val(codigo_cud);
		
		<portlet:namespace />habilitaComponentes();
		
	}
	
	function <portlet:namespace />validarDatosDocumentacion(){
		var id_doc=jQuery('#<portlet:namespace/>doc').val();
		var fechaIngresoDia=jQuery('#<portlet:namespace />fechaIngresoDocumentoDia').val();		 
		var fechaIngresoMes=jQuery('#<portlet:namespace />fechaIngresoDocumentoMes').val();
		var fechaIngresoAnio=jQuery('#<portlet:namespace />fechaIngresoDocumentoAnio').val();

		var fechaEgresoDiaTrim=jQuery('#<portlet:namespace />fechaEgresoDocumentoDia').val().replace(/^\s+/g,'');
		var fechaEgresoMesTrim=jQuery('#<portlet:namespace />fechaEgresoDocumentoMes').val().replace(/^\s+/g,'');
		var fechaEgresoAnioTrim=jQuery('#<portlet:namespace />fechaEgresoDocumentoAnio').val().replace(/^\s+/g,'');
		var codCUD=jQuery('#<portlet:namespace />nroCertificado').val();
		
		var mensaje="Debe completar los campos ";
		
		var sinError=true;
		if(fechaIngresoDia.length==0){
			mensaje=mensaje+" *<liferay-ui:message key="dia" />";
			sinError=false;
		}
		if(fechaIngresoMes.length==0){
			mensaje=mensaje+" *<liferay-ui:message key="mes" />";
			sinError=false;
		}
		if(fechaIngresoAnio.length==0){
			mensaje=mensaje+" *<liferay-ui:message key="anio" />";
			sinError=false;
		}
		
		if(fechaEgresoDiaTrim!="" && fechaEgresoMesTrim !="" && fechaEgresoAnioTrim!=""){
			if(parseInt(fechaIngresoAnio)==parseInt(fechaEgresoAnioTrim)){
				if(parseInt(fechaIngresoMes)>parseInt(fechaEgresoMesTrim)){					
					mensaje="<liferay-ui:message key='fechaIngreso-mayor-fechaVto'/>";
					sinError=false;
				}else if(parseInt(fechaIngresoMes)==parseInt(fechaEgresoMesTrim) && parseInt(fechaIngresoDia)>parseInt(fechaEgresoDiaTrim)){					
					mensaje="<liferay-ui:message key='fechaIngreso-mayor-fechaEgreso'/>";
					sinError=false;
				}
			}else if(parseInt(fechaIngresoAnio)>parseInt(fechaEgresoAnioTrim)){				
				mensaje="<liferay-ui:message key='fechaIngreso-mayor-fechaEgreso'/>";
				sinError=false;				
			}			
		}
		
        var doc =id_doc.split('|')[0]; 
		if((doc==5 || doc==19)  && (codCUD==null || codCUD=="" || "null"==codCUD ) ){
		   mensaje="El código de CUD es obligatorio";
		   sinError=false;	
		}
		
		if(!sinError){		
			alert(mensaje);
		}		
		return sinError;
	}	
	
	function <portlet:namespace />limpiarCamposDocumentacion(){
		//enable
		jQuery('#<portlet:namespace/>doc').attr("disabled",false);		
		jQuery('#<portlet:namespace />fechaIngresoDocumentoDia').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaIngresoDocumentoMes').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaIngresoDocumentoAnio').attr("disabled",false);

		//no more update
		jQuery('#<portlet:namespace />editarDoc').val(false);

		//limpio los datos
		jQuery('#<portlet:namespace/>doc').val("")
		jQuery('#<portlet:namespace />fechaIngresoDocumentoDia').val("");
		jQuery('#<portlet:namespace />fechaIngresoDocumentoMes').val("");		
		jQuery('#<portlet:namespace />fechaIngresoDocumentoAnio').val("");
		jQuery('#<portlet:namespace />fechaEgresoDocumentoDia').val("");
		jQuery('#<portlet:namespace />fechaEgresoDocumentoMes').val("");		
		jQuery('#<portlet:namespace />fechaEgresoDocumentoAnio').val("");
		jQuery('#<portlet:namespace />nroCertificado').val("");
		jQuery('#<portlet:namespace />nroCertificado').hide();
		jQuery('#<portlet:namespace />lbCertificado').hide();
	}
	
	function <portlet:namespace />habilitaComponentes(){
		var docu=jQuery('#<portlet:namespace />doc').val();
		var d = docu.split("|");
		if(d[0]=="5" || d[0]=="19"){
			jQuery('#<portlet:namespace />nroCertificado').show();
			jQuery('#<portlet:namespace />lbCertificado').show();
		}else{
			jQuery('#<portlet:namespace />nroCertificado').val("");
			jQuery('#<portlet:namespace />nroCertificado').hide();
			jQuery('#<portlet:namespace />lbCertificado').hide();
		}
	}
	
	<portlet:namespace />habilitaComponentes();
</script>