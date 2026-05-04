<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
 
<%

Calendar current = CalendarFactoryUtil.getCalendar();
String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "autorizaciones";
}
String esEditableStr = ParamUtil.getString(request, "esEdicion");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEdicion = Boolean.parseBoolean(esEditableStr);
List<ModalidadAtencion> modalidadAtencionList=TraeListasServiceUtil.getModalidadAtencion() ;
List<NomencladorPlan> topes = (List<NomencladorPlan>) request.getSession().getAttribute(WebKeysAutorizaciones.TOPES_REINTEGROS);
List<Plan> planList=TraeListasServiceUtil.getPlanesMolineros();
%>

<input id="<portlet:namespace />topeNomencladorId" name="<portlet:namespace />topeNomencladorId" type="hidden" value="0"/>

	<table class="lfr-table" width="100%">
		<%if (esEdicion){ %>
		   <tr>
                   <td width="70px"><label>Plan:</label></td>
                   <td>
	               <select name="<portlet:namespace/>planNomenclador_tope" id="<portlet:namespace/>planNomenclador_tope">
				     <option value="0">Seleccione plan</option>
				     <%	for (Plan terce : planList) { %>
						<option value="<%= terce.getId() %>"><%=terce.getDescripcion()%></option>
				     <%	} %>
			         </select>
		           </td>
                   
	               
	               <td><label>Importe:</label></td>
	               <td>
	                 <input id="<portlet:namespace />topeNomenclador" name="<portlet:namespace />topeNomenclador" size="20" maxlength="50" 
	                   type="text" value=''  onkeydown="allowOnlyDigitsAndDecimalsConSuprimir(event)"/>
		           </td>
		    </tr>
		    <tr><td>&nbsp;</td></tr>
		    <tr>  
              <td  colspan="250"> 		    
	             <table width="100%">
	               <tr> 	       
		            <td colspan="2"><label>Vigencia Desde:</label></td>
					<td colspan="4">
							<liferay-ui:input-date
							dayParam="fechaDesdeDiaTope"
							dayValue="-1" 
							dayNullable="<%= true %>"
							monthParam="fechaDesdeMesTope"
							monthValue="-1"	
							monthNullable="<%= true %>"			
							yearParam="fechaDesdeAnioTope"
							yearValue="-1"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 3 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)+10%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
					</td>
		           
		            <td><label>Hasta:</label></td>
					<td colsapn="4">
							<liferay-ui:input-date
							dayParam="fechaHastaDiaTope"
							dayValue="-1" 
							dayNullable="<%= true %>"
							monthParam="fechaHastaMesTope"
							monthValue="-1"	
							monthNullable="<%= true %>"			
							yearParam="fechaHastaAnioTope"
							yearValue="-1"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 3 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)+10%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
					</td>
		           
	               <td>
		               <input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarTope();" />
	               </td>
	               
	              </tr>
	            </table>
	           </td>    
	               
	       </tr>
           
		<%} %>
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />agregandoTope">
				<table style="align: center;" width="100%">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
	</table>
	<table  class="lfr-table" width="100%">	
		<tr>
			<td colspan="12">
				<div align="center" id="<portlet:namespace />topesReintegrosList">
					<liferay-util:include page="/html/portlet/autorizaciones/nomenclador/nomencladorPlan_topesReintegros_search_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>
<script type="text/javascript">
	function <portlet:namespace />agregarTope(){
		document.getElementById("<portlet:namespace />planNomenclador_tope").disabled=false;	
		var plan=jQuery('#<portlet:namespace />planNomenclador_tope').val();
		var fechaDesdeDia=jQuery("#<portlet:namespace />fechaDesdeDiaTope").val();
		var fechaDesdeMes=jQuery("#<portlet:namespace />fechaDesdeMesTope").val();
		var fechaDesdeAnio=jQuery("#<portlet:namespace />fechaDesdeAnioTope").val();
		
		var fechaHastaDia=jQuery("#<portlet:namespace />fechaHastaDiaTope").val();
		var fechaHastaMes=jQuery("#<portlet:namespace />fechaHastaMesTope").val();
		var fechaHastaAnio=jQuery("#<portlet:namespace />fechaHastaAnioTope").val();
		
		var importe=jQuery("#<portlet:namespace />topeNomenclador").val();
		var idRenglon=jQuery("#<portlet:namespace />topeNomencladorId").val();
		
		if(plan==0){
			alert("Debe seleccionar un plan");
			return;
		}
		
		if(fechaDesdeDia=="" || fechaDesdeMes=="" || fechaDesdeAnio==""){
			alert("Debe seleccionar una fecha de vigencia desde");
			return;
		}
		
		if(importe==0){
			alert("Debe ingresar el tope");
			return;
		}
		
		jQuery('#<portlet:namespace />agregandoTope').show();
		if(plan!=0 && parseFloat(importe)!=0){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_nomenclador'
			+ '&cmd=topeAdd' 
			+ '&plan=' + encodeURI(plan)
			+ '&importe=' +encodeURI(importe)
			+ '&diadde=' +encodeURI(fechaDesdeDia)
			+ '&mesdde='+encodeURI(fechaDesdeMes)
			+ '&aniodde=' +encodeURI(fechaDesdeAnio)
			+ '&diahta=' +encodeURI(fechaHastaDia)
			+ '&meshta='+encodeURI(fechaHastaMes)
			+ '&aniohta=' +encodeURI(fechaHastaAnio)
			+ '&idren=' +encodeURI(idRenglon)
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />topesReintegrosList').load(url, function() {
														jQuery('#<portlet:namespace />agregandoTope').hide();
														jQuery('#<portlet:namespace />planNomenclador_tope').val('');
														jQuery("#<portlet:namespace />fechaDesdeDiaTope").val('');
														jQuery("#<portlet:namespace />fechaDesdeMesTope").val('');
														jQuery("#<portlet:namespace />fechaDesdeAnioTope").val('');
														jQuery("#<portlet:namespace />fechaHastaDiaTope").val('');
														jQuery("#<portlet:namespace />fechaHastaMesTope").val('');
														jQuery("#<portlet:namespace />fechaHastaAnioTope").val('');
														jQuery("#<portlet:namespace />topeNomenclador").val('');
														jQuery("#<portlet:namespace />topeNomencladorId").val(0);
										   }
			 );
		}else{
			jQuery('#<portlet:namespace />agregandoTope').hide();
		}	
	}

	function borraTope(idMod){
		var id = idMod;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_nomenclador'
		    +  '&cmd=topeDelete' 
			+  '&idren=' +id
			+  '&esEdicion=' + "<%=esEdicion%>" ;
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />topesReintegrosList').load(url, function() {
									jQuery('#<portlet:namespace />agregandoTope').hide();
			}
	   );
	}
	
	
	function editaTope(idMod,idPlan,tope,ddeDia,ddeMes,ddeAnio,htaDia,htaMes,htaAnio){
		jQuery('#<portlet:namespace />planNomenclador_tope').val(idPlan);
		jQuery("#<portlet:namespace />fechaDesdeDiaTope").val(ddeDia);
		jQuery("#<portlet:namespace />fechaDesdeMesTope").val(ddeMes);
		jQuery("#<portlet:namespace />fechaDesdeAnioTope").val(ddeAnio);
		if(htaDia==0){
		  jQuery("#<portlet:namespace />fechaHastaDiaTope").val('');
		  jQuery("#<portlet:namespace />fechaHastaMesTope").val('');
		  jQuery("#<portlet:namespace />fechaHastaAnioTope").val('');
		}else{
		  jQuery("#<portlet:namespace />fechaHastaDiaTope").val(htaDia);
		  jQuery("#<portlet:namespace />fechaHastaMesTope").val(htaMes);
		  jQuery("#<portlet:namespace />fechaHastaAnioTope").val(htaAnio);	
		}  
		jQuery("#<portlet:namespace />topeNomenclador").val(tope);
		jQuery("#<portlet:namespace />topeNomencladorId").val(idMod);
		document.getElementById("<portlet:namespace />planNomenclador_tope").disabled=true;
		
	}
	
	jQuery('#<portlet:namespace />agregandoTope').hide();
</script>