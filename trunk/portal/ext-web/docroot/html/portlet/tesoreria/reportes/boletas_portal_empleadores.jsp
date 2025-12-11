<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%

		String portlet_name = ParamUtil.getString(request, "portlet_name");
		
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "tesoreria";
		}
		if(renderResponse.getNamespace().equals("_AFI_1_")){
			portlet_name = "afiliados";
		}
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
		}
		 
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		} 

		List<ConvenioNacion> convenioNac = (List<ConvenioNacion>) portletSession.getAttribute(WebKeysTesoreria.CONVENIO_EN_SESSION, PortletSession.APPLICATION_SCOPE);
		Set<CuentasNacion> cuentasNac = (Set<CuentasNacion>) portletSession.getAttribute(WebKeysTesoreria.CUENTAS_EN_SESSION, PortletSession.APPLICATION_SCOPE);
	
		boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ENTIDAD_OSPIM);
		boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ENTIDAD_AMTIMA);
		boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ENTIDAD_UOMA); 
		
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
		fechaDesde.add(Calendar.MONTH, -1);
		Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
		fechaHasta.setTime(new Date()); 
		Calendar current = CalendarFactoryUtil.getCalendar();
		
		String seccionalString=null;
 		String seccionalDefecto=user.getExpandoBridge().getAttribute("id_seccional").toString(); 		
 		int seccionalFijada=null!=seccionalDefecto&& !seccionalDefecto.trim().equals("")&& !seccionalDefecto.trim().equals("0")?Integer.parseInt(seccionalDefecto):0;
 		if(seccionalFijada!=0){
 			seccionalString=user.getExpandoBridge().getAttribute("seccional").toString();
 		}
		
%>
<form  method="get" name="fm" onSubmit="submitForm(this); return false;" style="display:block" id="formReporte">			
	<fieldset class="block-labels">
				<legend><liferay-ui:message key="reporte-boletas-portal-empleadores" />
				<% if(seccionalFijada!=0){%>
					 Seccional <%=seccionalString%>
				<%}%>
				</legend>
				<table class="lfr-table">
				<% if(seccionalFijada==0){%>
					<tr>						
						<td><label><liferay-ui:message key="fecha-recaudacion-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaRecDesdeDia"
							dayValue="1" 
							monthParam="fechaRecDesdeMes"
							monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>"				
							yearParam="fechaRecDesdeAnio"
							yearValue="2012"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR) %>"
							firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-recaudacion-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaRecHastaDia"
							dayValue="<%= fechaHasta.get(Calendar.DATE) %>" 
							monthParam="fechaRecHastaMes"
							monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>"				
							yearParam="fechaRecHastaAnio"
							yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -10 %>"	
							yearRangeEnd="<%= current.get(Calendar.YEAR) %>"
							firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>								
					</tr>
					<tr>						
						<td><label><liferay-ui:message key="fecha-rendicion-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaRenDesdeDia"
							dayValue="1" 
							monthParam="fechaRenDesdeMes"
							monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>"				
							yearParam="fechaRenDesdeAnio"
							yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR) %>"
							firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-rendicion-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaRenHastaDia"
							dayValue="<%= fechaHasta.get(Calendar.DATE) %>" 
							monthParam="fechaRenHastaMes"
							monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>"				
							yearParam="fechaRenHastaAnio"
							yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -10 %>"	
							yearRangeEnd="<%= current.get(Calendar.YEAR) %>"
							firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>								
					</tr>
				<%} %>
					<tr>						
						<td><label><liferay-ui:message key="periodo-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="periodoDesdeDia"
							dayValue="1" 
							monthParam="periodoDesdeMes"
							monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>"				
							yearParam="periodoDesdeAnio"
							yearNullable="<%= true %>"
							yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR) %>"
							firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="periodo-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="periodoHastaDia"
							dayValue="<%= fechaHasta.get(Calendar.DATE) %>"  
							monthParam="periodoHastaMes"
							monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>"
							yearParam="periodoHastaAnio"
							yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -10 %>"	
							yearRangeEnd="<%= current.get(Calendar.YEAR) %>"
							firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>
								
					</tr>
					<tr>
					
					<% if(seccionalFijada==0){%>
					<td><label><liferay-ui:message key="cuenta-suc" />:</label></td>
						<td>
							<select  name="<portlet:namespace/>cuentaSuc" id="combo_0" onChange="change(this)" multiple="multiple" size="5" type="text" value=''>
 							<% for (CuentasNacion cuentasNacion : cuentasNac) {	%>
 								<%if(showOspim && cuentasNacion.getOspim() || showUoma && cuentasNacion.getUoma() || showAmtima && cuentasNacion.getAmtima()) {%>
 								<option	value="<%= cuentasNacion.getId() %>"selected><%=cuentasNacion.getCuenta_suc()%></option>
								<% } %>	
							<% } %>
							</select>
						</td>
						<td><label><liferay-ui:message key="tipo-boleta" />:</label></td>
							<td>
								<select  name="<portlet:namespace/>tipoBoleta" id="combo_1" onChange="change(this)" multiple="multiple" size="5" type="text" value=''>
	 							<% for (ConvenioNacion convenioNacion : convenioNac) {	%>
	 								<%if(showOspim && convenioNacion.getOspim() || showUoma && convenioNacion.getUoma() || showAmtima && convenioNacion.getAmtima()) { %>
	 									<option	value="<%= convenioNacion.getTipo_boleta() %>"selected><%=convenioNacion.getDescripcion()%></option>
									<% } %>	
								<% } %>	
								</select>
							</td>
					<%}else {%>
						<td colspan="4"><input type="hidden" value="1,2,3,4,5,6,7,7,7,8" id="combo_1"/></td>
					<% }%>
						<% if(seccionalFijada==0){%>	
							<td><label><liferay-ui:message key="nro-acta-convenio"/>:</label></td>
							<td><input id="<portlet:namespace />actaConvenio" name="<portlet:namespace />actaConvenio" size="10" maxlength="20" type="text" value=''/></td>
						<%}else{%>
							<td colspan="2">&nbsp;</td>
						<%}%>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>								
					</tr>
					<% if(seccionalFijada==0){%>	
					<tr>
						<td><label><liferay-ui:message key="nro-cheque"/>:</label></td>
						<td><input id="<portlet:namespace />nroCheque" name="<portlet:namespace />nroCheque" size="20" maxlength="20" type="text" value=''/></td>
						<td><label><liferay-ui:message key="imp-desde"/>:</label></td>
						<td><input id="<portlet:namespace />impDesde" name="<portlet:namespace />impDesde" size="15" maxlength="20" type="text" value=''/></td>
						<td><label><liferay-ui:message key="imp-hasta"/>:</label></td>
						<td><input id="<portlet:namespace />impHasta" name="<portlet:namespace />impHasta" size="15" maxlength="20" type="text" value=''/></td>
						<td><label><liferay-ui:message key="estado-cheque" />:</label></td>
							<td><select id="<portlet:namespace/>estadoCheque" name="<portlet:namespace/>estadoCheque">
								<option value="L" selected>Depositado</option>
								<option value="P">Presentado</option>
								<option value="R">Rechazado</option>
								<option value=''>Todos</option>
							</select>
							</td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>								
					</tr>
				    <%}%>
					<tr>
						<td>
							<div id="entidadesTitle" style="visibility: visible;">
								<label><liferay-ui:message key="empresa"/>:</label>
							</div>
						</td>
						<td colspan="5">
						<div id="entidades" style="visibility: visible;">
							<table>
								<tr>
									<td>
										<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
								  			<liferay-util:param name="esEditable" value='true'/>
								  			<liferay-util:param name="portlet_name" value='tesoreria'/>		
								  			<liferay-util:param name="suf_entidad" value='boletas'/>							  								  			
										</liferay-util:include>
									</td>
									<% if(seccionalFijada==0){%>	
										<td>
											<legend><liferay-ui:message key="solo-ddjj" />:</legend>
										</td>
										<td><input type="checkbox" id="<portlet:namespace />solo_ddjj" name="<portlet:namespace />solo_ddjj" onClick="javascript:clickSoloDDJJ();"/></td>
									<%}else{%>
										<td colspan="2"><input type="hidden" id="<portlet:namespace />solo_ddjj" name="<portlet:namespace />solo_ddjj" value="true"/></td>
									<%}%>
								</tr>
							</table>
						</div>	
						</td>						
					</tr>					
					<tr>
						<td colspan="8">&nbsp;</td>								
					</tr>
					<tr>
					<% if(seccionalFijada==0){%>
							<td><label><liferay-ui:message key="seccional" />:</label></td>
							<td colspan="2" rowspan="3" style="vertical-align:top" >
								<liferay-util:include page="/html/portlet/afiliados/busqueda_seccional.jsp"/>
							</td>
							<td colspan="5">&nbsp;</td>
						<%}else{%>
							<td colspan="8">&nbsp;</td>
						<%} %>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>								
					</tr>
					<tr>
					<td colspan="8" align="right"><input type="button" id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" /></td>
					</tr>
					</table>
		</fieldset>	
 </form>	

<form  method="get" name="fm2" onSubmit="submitForm(this);" style="display: none" id="formArchivo">
	<div id="<portlet:namespace />sistema_viejo_uoma">		
		<jsp:include page='sistema_viejo_uoma.jsp' />
	</div>
</form>
 <% if(seccionalFijada==0){%>
 	<fieldset class="block-labels">
 		<table class="lfr-table" style="float:right;">
	 		<tr>
				<td>
					<div id="consolidadoDiv" name="consolidadoDiv" style="visibility: hidden;">	
					 	<liferay-ui:message key="agrupado"/>:&nbsp;<input type="checkbox"  id="<portlet:namespace />consolidado" name="<portlet:namespace />consolidado" />					    	
				   	</div>				
				</td>
				<td>
					<div id="ddjj" name="ddjj">	
					 	<liferay-ui:message key="ddjj-todas-empresas"/>:&nbsp;<input type="checkbox"  id="<portlet:namespace />ddjj_todas_empresas" name="<portlet:namespace />ddjj_todas_empresas" onchange="ocultarComponente('entidades');ocultarComponente('entidadesTitle');"/>					    	
				   	</div>				
				</td>
				<td>
					<div id="cruce" name="cruce">	
					 	<liferay-ui:message key="cruzar-ddjj-os"/>:&nbsp;<input type="checkbox"  id="<portlet:namespace />cruce_ddjj_os" name="<portlet:namespace />cruce_ddjj_os"/>					    	
				   	</div>				
				</td>									    		    	
	 			
			</tr>
		</table>
	</fieldset>
<%}%>

<script type="text/javascript">
	
	<% int cont_ctas=0;
	for (CuentasNacion cuentasNacion : cuentasNac) {	
			if(showOspim && cuentasNacion.getOspim() || showUoma && cuentasNacion.getUoma() || showAmtima && cuentasNacion.getAmtima()) { %>
				data_<%=cont_ctas%> = new Option("<%=cuentasNacion.getCuenta_suc()%>", "<%=cuentasNacion.getId()%>");   			
				<%int cont_aux=0;
				for (int cont_btas=0;cont_btas<convenioNac.size();cont_btas++) {   				
			   			ConvenioNacion conve=convenioNac.get(cont_btas);
			   			if(cuentasNacion.getId()==conve.getId()){
			   				if(showOspim && conve.getOspim() || showUoma && conve.getUoma() || showAmtima && conve.getAmtima() ) { %>
			   					data_<%=cont_ctas%>_<%=cont_aux%>=new Option("<%=conve.getDescripcion()%>","<%=conve.getTipo_boleta()%>");
			   					<%cont_aux++;
			   				}
			   			}
				}
				cont_ctas++;	
			}
		} %>	
	
	 displaywhenempty=""
	 valuewhenempty=""
	
	 displaywhennotempty="-Seleccione-"
	 valuewhennotempty=1
	
	function change(currentbox) {
		
		numb = currentbox.id.split("_");
		currentbox = numb[1];
	 i=parseInt(currentbox)+1
	
	// I empty all combo boxes following the current one 
	
	 while ((eval("typeof(document.getElementById(\"combo_"+i+"\"))!='undefined'")) &&
	        (document.getElementById("combo_"+i)!=null)) {
	      son = document.getElementById("combo_"+i);
		     // I empty all options except the first one (it isn't allowed) 
		     for (m=son.options.length-1;m>0;m--) son.options[m]=null;
		     // I reset the first option 
		     son.options[0]=new Option(displaywhenempty,valuewhenempty)
		     i=i+1
	 }
	
	// now I create the string with the "base" name ("stringa"), ie. "data_1_0" 
	// to which I'll add _0,_1,_2,_3 etc to obtain the name of the combo box to fill 
	
	 stringa='data'
	 i=0
	 while ((eval("typeof(document.getElementById(\"combo_"+i+"\"))!='undefined'")) &&
	        (document.getElementById("combo_"+i)!=null)) {
	        eval("stringa=stringa+'_'+document.getElementById(\"combo_"+i+"\").selectedIndex");
	        if (i==currentbox){
	     	   break
	     	};
	        i=i+1;
	 }
	
	// filling the "son" combo (if exists)
	
	 following=parseInt(currentbox)+1
	
	 if ((eval("typeof(document.getElementById(\"combo_"+following+"\"))!='undefined'")) &&
	    (document.getElementById("combo_"+following)!=null)) {
	    son = document.getElementById("combo_"+following);       
	    stringa=stringa+"_"
	    i=0
	    while ((eval("typeof("+stringa+i+")!='undefined'")) || (i==0)) {
	    // if there are no options, I empty the first option of the "son" combo 
		   // otherwise I put "-select-" in it 
	
		   	  if ((i==0) && eval("typeof("+stringa+"0)=='undefined'"))
		   	      if (eval("typeof("+stringa+"1)=='undefined'"))
		   	         eval("son.options[0]=new Option(displaywhenempty,valuewhenempty)")
		   	      else
		             eval("son.options[0]=new Option(displaywhennotempty,valuewhennotempty)")
		      else
	           eval("son.options["+i+"]=new Option("+stringa+i+".text,"+stringa+i+".value)")
		      i=i+1
		   }
	    //son.focus()
	    i=1
	    combostatus=''
	    cstatus=stringa.split("_")
	    while (cstatus[i]!=null) {
	       combostatus=combostatus+cstatus[i]
	       i=i+1
	       }
	    return combostatus;
	 }
	}

	jQuery('#<portlet:namespace />buscando').hide();
	jQuery('#<portlet:namespace />buscar').click(function(){
		
		var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();		
		var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();		
		var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();		
		var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();		
		var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();		
		var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();
		
		var fechaRecDesdeDia=jQuery('#<portlet:namespace />fechaRecDesdeDia').val();		
		var fechaRecDesdeMes=jQuery('#<portlet:namespace />fechaRecDesdeMes').val();
		var fechaRecDesdeAnio=jQuery('#<portlet:namespace />fechaRecDesdeAnio').val();	
		var fechaRecHastaDia=jQuery('#<portlet:namespace />fechaRecHastaDia').val();		
		var fechaRecHastaMes=jQuery('#<portlet:namespace />fechaRecHastaMes').val();
		var fechaRecHastaAnio=jQuery('#<portlet:namespace />fechaRecHastaAnio').val();
		
		var fechaRenDesdeDia=jQuery('#<portlet:namespace />fechaRenDesdeDia').val();
		var fechaRenDesdeMes=jQuery('#<portlet:namespace />fechaRenDesdeMes').val();
		var fechaRenDesdeAnio=jQuery('#<portlet:namespace />fechaRenDesdeAnio').val();
		var fechaRenHastaDia=jQuery('#<portlet:namespace />fechaRenHastaDia').val();
		var fechaRenHastaMes=jQuery('#<portlet:namespace />fechaRenHastaMes').val();
		var fechaRenHastaAnio=jQuery('#<portlet:namespace />fechaRenHastaAnio').val();
		
		var cuentaSuc = jQuery('#combo_0').val();		
		var tipoBoleta = jQuery('#combo_1').val();		
		
		var actaConvenio = jQuery('#<portlet:namespace/>actaConvenio').val();
		var nroCheque = jQuery('#<portlet:namespace />nroCheque').val();
		var impDesde = jQuery('#<portlet:namespace />impDesde').val();
		var impHasta = jQuery('#<portlet:namespace />impHasta').val();
		var estadoCheque = jQuery('#<portlet:namespace />estadoCheque').val();
		var cuit_entidad=jQuery('#<portlet:namespace />cuit_entidad').val();	
		var ddjj_todas_empresas=jQuery('#<portlet:namespace />ddjj_todas_empresas').is(':checked');
		<% if(seccionalFijada==0){%>
			var solo_ddjj=jQuery('#<portlet:namespace />solo_ddjj').is(':checked');
		<%}else{%>
			var solo_ddjj=true;
		<%}%>
		var consolidado=jQuery('#<portlet:namespace />consolidado').is(':checked');
		var cruce_ddjj_os=jQuery('#<portlet:namespace />cruce_ddjj_os').is(':checked');
		
		<% if(seccionalFijada==0){%>
			var seccional=jQuery('#<portlet:namespace />id_seccional').val();		
			var seccional_nombre=jQuery('#<portlet:namespace />seccional').val();
		<%}else{%>
			var seccional="<%=seccionalFijada%>";		
			var seccional_nombre="";
		<%}%>
		
		if(cruce_ddjj_os){
			alert('Con la opción Cruzar con ddjj de OS seleccionada sólo se traerá un período, todos los tipos de boleta y se traerán agrupados');
		}

		jQuery('#<portlet:namespace />buscando').show();
		    window.location.href ='/xlsservlet/?reporte=REPORTE_BOLETA_PORTAL_EMPLEADORES'+'&periodoDesdeDia='+periodoDesdeDia+
			'&periodoDesdeMes='+periodoDesdeMes+'&periodoDesdeAnio='+periodoDesdeAnio+'&periodoHastaDia='+periodoHastaDia+
			'&periodoHastaMes='+periodoHastaMes+'&periodoHastaAnio='+periodoHastaAnio+'&fechaRecDesdeDia='+fechaRecDesdeDia+
			'&fechaRecDesdeMes='+fechaRecDesdeMes+'&fechaRecDesdeAnio='+fechaRecDesdeAnio +'&fechaRecHastaDia='+fechaRecHastaDia+
			'&fechaRecHastaMes='+fechaRecHastaMes+'&fechaRecHastaAnio='+fechaRecHastaAnio+'&cuentaSuc='+cuentaSuc+
			'&fechaRenDesdeMes='+fechaRenDesdeMes+'&fechaRenDesdeAnio='+fechaRenDesdeAnio +'&fechaRenHastaDia='+fechaRenHastaDia+
			'&fechaRenHastaMes='+fechaRenHastaMes+'&fechaRenHastaAnio='+fechaRenHastaAnio+'&fechaRenDesdeDia='+fechaRenDesdeDia+
			'&tipoBoleta='+tipoBoleta+'&actaConvenio='+actaConvenio +'&actaConvenio='+actaConvenio+'&nroCheque='+nroCheque+
			'&impDesde='+impDesde+'&impHasta='+impHasta+'&estadoCheque='+estadoCheque+'&cuit_entidad='+cuit_entidad+
			'&ddjj_todas_empresas='+ddjj_todas_empresas+'&solo_ddjj='+solo_ddjj+'&consolidado='+consolidado+'&cruce_ddjj_os='+cruce_ddjj_os
			+'&id_seccional='+seccional; 
		}); 
	
	// Cambia al form de sistema_viejo_uoma.jsp 
    function Mostrar(fr,fa,dj,cons) {
       var formR = document.getElementById(fr);
       var formA = document.getElementById(fa);
       var DJ = document.getElementById(dj);
       var consol = document.getElementById(cons);

       if (formR.style.display == "block") {
			formR.style.display = "none"; 
			formA.style.display = "block";
			DJ.style.display = "none";
			consol.style.display = "none";
		}
       else {
    	  	formR.style.display = "block";
    	  	formA.style.display = "none";
    	  	DJ.style.display = "block";
    	  	consol.style.display = "block"
    	}
    }
    
	// oculta el div que tiene busqueda_padron_entidades.jsp 
	// y limpio el numero de CUIT 
    function ocultarComponente(id) {
    	   if(document.getElementById("consolidadoDiv").style.visibility=="visible"){
    	   		document.getElementById("consolidadoDiv").style.visibility="hidden";
    	   }else{
    	   		document.getElementById("consolidadoDiv").style.visibility="visible";
    	   }
    	   var ele = document.getElementById(id);
    	   if (ele.style.visibility == "visible") {
    		   ele.style.visibility = "hidden";
    		jQuery('#<portlet:namespace />cuit_entidad').val("");
   			var cuit_entidad=jQuery('#<portlet:namespace />cuit_entidad').val("");	
    	   }
    	   else { ele.style.visibility = "visible"; }
		   jQuery("consolidadoDiv").show();    	   
    }
    
</script>