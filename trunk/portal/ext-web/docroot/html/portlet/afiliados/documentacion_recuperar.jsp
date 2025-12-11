<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);

Calendar fechaVigen = CalendarFactoryUtil.getCalendar();
fechaVigen.setTime(afiliado.getVigen_fecha());
Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
String view=(String)request.getParameter("view");
String cuil_titular=request.getParameter("cuil_titular");
String inte = request.getParameter("inte");
List<Documento> documentosList = (ArrayList<Documento>) portletSession.getAttribute(WebKeysAfiliados.DOCUMENTOS_PARA_RECUPERAR_EN_SESSION,PortletSession.APPLICATION_SCOPE);

if (documentosList == null) {
	documentosList = TraeListasServiceUtil.getDocumentosActualizanAfiliado();
	portletSession.setAttribute(WebKeysAfiliados.DOCUMENTOS_PARA_RECUPERAR_EN_SESSION,documentosList,PortletSession.APPLICATION_SCOPE);	
}
%>
<portlet:defineObjects/>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="documentacion-adjunta" /></legend>
				<%if(null==view || !view.equals("true")){ %>
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
					<tr>				
						<td>
							<liferay-ui:message key="documento" />
						</td>		
						<td colspan="5">
							<select name="<portlet:namespace/>documento" id="<portlet:namespace/>documento" 
							      onchange="<portlet:namespace />habilitaComponentes()">
									<option value=""></option>									
									<%
										for (Documento doc : documentosList) {
									%>
										<option value="<%= doc.getId_documento()%>"><%=doc.getDescripcion()%></option>
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
					<tr>
						<td><label><liferay-ui:message key="ingre-fecha" /></label></td>
						<td colspan="2"> 
							<liferay-ui:input-date
								dayParam="fechaIngresoDocumentoDia"
								dayValue="<%= fechaVigen.get(Calendar.DATE)%>"
								monthParam="fechaIngresoDocumentoMes"
								monthValue="<%= fechaVigen.get(Calendar.MONTH) %>"								
								yearParam="fechaIngresoDocumentoAnio"
								yearValue="<%= fechaVigen.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaVigen.get(Calendar.YEAR) %>"
								yearRangeEnd="<%= fechaVigen.get(Calendar.YEAR) + 50 %>"
								firstDayOfWeek="<%= fechaVigen.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>"
							/>				
						</td>
						<td><label><liferay-ui:message key="egreso-fecha" /></label></td>						
						<td colspan="2">
							<liferay-ui:input-date
								monthParam="fechaEgresoDocumentoMes"								
								monthNullable="true"								
								dayParam="fechaEgresoDocumentoDia"								
								dayNullable="true"
								yearParam="fechaEgresoDocumentoAnio"								
								yearNullable="true"
								yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 31 %>"
								firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>"
							/>
						</td>						
					</tr>
					<tr>
						<td><label>N° Correspondencia:</label></td>
						<td><input id="<portlet:namespace />numero_correspondencia_rec_doc"
							name="<portlet:namespace />numero_correspondencia_rec_doc" size="10" maxlength="10"
							type="text" onkeydown="allowOnlyDigits(event);"
							value="" /></td>
						<td colspan="3">
							&nbsp;
						</td>	
						<td colspan="1">
						<div align="center" id="<portlet:namespace />botonAdjuntarDocumentacion">
							<input type="button" value="<liferay-ui:message key="adjuntar-y-recuperar" />" onClick="<portlet:namespace />grabarDocumentacion();" />							
						</div>
						</td>
					</tr>	
				</table>
				<div align="center" id="<portlet:namespace />buscandoDocumentacion">
					<table style="align:center;">
						<tr>
							<td align="center">
								<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>		
				</div>
				<div align="center" id="<portlet:namespace />verificarInfo">
					<table style="align:center;">
						<tr>
							<td>&nbsp;</td>
						</tr>
					</table>		
				</div>
				<%} %>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />verificarInfo').show();
	jQuery('#<portlet:namespace />buscandoDocumentacion').hide();
	jQuery('#<portlet:namespace />botonAdjuntarDocumentacion').show();
	
	function <portlet:namespace />grabarDocumentacion(){
		if(!<portlet:namespace />validarDatosDocumentacion()){
			return false;
		}else{
			jQuery('#<portlet:namespace />buscandoDocumentacion').show();
			jQuery('#<portlet:namespace />botonAdjuntarDocumentacion').hide();			
			
			var id_doc=jQuery('#<portlet:namespace />documento').val();
			var diaIngreso=	jQuery('#<portlet:namespace />fechaIngresoDocumentoDia').val();
			var mesIngreso= parseInt(jQuery('#<portlet:namespace />fechaIngresoDocumentoMes').val())+1;
			var anioIngreso=jQuery('#<portlet:namespace />fechaIngresoDocumentoAnio').val();		
			var fechaIngreso=diaIngreso+'/'+mesIngreso+'/'+anioIngreso;			
			var diaEgreso=jQuery('#<portlet:namespace />fechaEgresoDocumentoDia').val();
			var mesEgreso=parseInt(jQuery('#<portlet:namespace />fechaEgresoDocumentoMes').val())+1;			
			var anioEgreso=jQuery('#<portlet:namespace />fechaEgresoDocumentoAnio').val();
			var fechaEgreso=diaEgreso+'/'+mesEgreso+'/'+anioEgreso;
			var nro_correspondencia=jQuery('#<portlet:namespace />numero_correspondencia_rec_doc').val();
			var certificado=jQuery('#<portlet:namespace/>nroCertificado').val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/grabar_documentacion_recuperar&id_documentacion='+id_doc+
				'&fechaIngreso='+fechaIngreso+'&fechaEgreso='+fechaEgreso+'&cuil_titular='+<%=cuil_titular%>+'&inte='+<%=inte%>+'&numero_correspondencia='+nro_correspondencia
				+'&certificado='+certificado;
			jQuery('#<portlet:namespace />verificarInfo').load(url, function() {
				jQuery('#<portlet:namespace />buscandoDocumentacion').hide(); 
				<portlet:namespace />habilitaComponentes();
			   });
		}
	}
		
	function <portlet:namespace />validarDatosDocumentacion(){
		var id_doc=jQuery('#<portlet:namespace />documento').val();
		var fechaIngresoDia=jQuery('#<portlet:namespace />fechaIngresoDocumentoDia').val();		 
		var fechaIngresoMes=jQuery('#<portlet:namespace />fechaIngresoDocumentoMes').val();
		var fechaIngresoAnio=jQuery('#<portlet:namespace />fechaIngresoDocumentoAnio').val();

		var fechaEgresoDiaTrim=jQuery('#<portlet:namespace />fechaEgresoDocumentoDia').val().replace(/^\s+/g,'');
		var fechaEgresoMesTrim=jQuery('#<portlet:namespace />fechaEgresoDocumentoMes').val().replace(/^\s+/g,'');
		var fechaEgresoAnioTrim=jQuery('#<portlet:namespace />fechaEgresoDocumentoAnio').val().replace(/^\s+/g,'');
						
		var nro_correspondencia=jQuery('#<portlet:namespace />numero_correspondencia_rec_doc').val();
		
		var sinError=true;
		var mensaje="Debe completar los campos ";
		var codCUD=jQuery('#<portlet:namespace />nroCertificado').val();
		
		if(id_doc.length==0 && fechaEgresoDiaTrim.length > 1 && fechaEgresoMesTrim.length > 1 && fechaEgresoAnioTrim.length > 1){
			mensaje=mensaje+" *<liferay-ui:message key="error_documentacion-adjunta" />";
			sinError=false;
		}
		
		/* if(fechaIngresoDia.length==0){
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
		} */
		
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
		 /* || trim(nro_correspondencia) == "0" */
		if ( trim(nro_correspondencia).length == 0){
			mensaje = mensaje + " Número de correspondencia";
			jQuery("#<portlet:namespace />numero_correspondencia").focus();
			sinError = false;
		}
		
		var doc =id_doc.split('|')[0]; 
		if(doc==5 && (codCUD==null || codCUD=="" || "null"==codCUD ) ){
		   mensaje="El código de CUD es obligatorio";
		   sinError=false;	
		}
		
		if(!sinError){
			alert(mensaje);
		}
		return sinError;
	}
	
	function <portlet:namespace />habilitaComponentes(){
		var docu=jQuery('#<portlet:namespace />documento').val();
		var d = docu.split("|");
		if(d[0]=="5"){
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