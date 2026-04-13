<%@ include file="/html/portlet/empresas/init.jsp"%>
<%
/* EntidadPadronUnificado empresa = (EntidadPadronUnificado)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);

if(null==empresa){
	LlamadosEstudio llest=(LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
	empresa=llest.getEmpresa();
} */
Calendar current = CalendarFactoryUtil.getCalendar();

//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));

//String dinámico que se le debe pasar a esta pagina para que sepa si se trata de contactos de la Empresa o Personalizados
boolean esEmprCont= Boolean.parseBoolean(ParamUtil.getString(request, "esEmpresaCont"));

%>

	
<div id="<portlet:namespace />ocultarContactosPersonalizados">
<fieldset class="block-labels">	
<legend><liferay-ui:message	key="address-book-pers" /></legend>

<table class="lfr-table" style="border-collapse:separate; border-spacing: 2px; width: 100%;" > 
	<tr>
		<td  style="width: 155px;">
			<liferay-ui:message key="profesion"/>:
		</td>
		<td>	
			<select name="<portlet:namespace />profesionCP" id="<portlet:namespace />profesionCP">
				<option value="" selected="selected">--Seleccione Profesión--</option>
				<option value="ABOGADO">Abogado</option>				
				<option value="CONTADOR">Contador</option>
				<option value="DIRECTOR">Director</option>
				<option value="EMPLEADOR">Empleado</option>
				<option value="OTROS">Otros</option>
			</select>
		</td>
		<td>	
			<liferay-ui:message key="cargo"/>:
		</td>
		<td>
			<input id="<portlet:namespace />cargoCP" name="<portlet:namespace />cargoCP" maxlength="50" type="text" style="width: 100px;"/>
		</td>
		<td>	
			<liferay-ui:message key="nombre"/>:
		</td>
		<td>	
			<input id="<portlet:namespace />apeynomP" name="<portlet:namespace />apeynomP" type="text" style="width: 300px;"/>
		</td>
		<td>
		&nbsp;
		</td>
	</tr>
	<tr>	
		<td style="width: 155px;"> <input type="hidden" name="<portlet:namespace/>idContactoCP" id="<portlet:namespace/>idContactoCP" value="" >		
			<liferay-ui:message key="tipo-contacto"/>:
		</td>
		<td>	
			<select name="<portlet:namespace />tipo_contactoP" id="<portlet:namespace />tipo_contactoP" onChange="ocultarComponenteP(this)">
				<option value="" selected="selected">--Seleccione Tipo--</option>
				<option value="T">Teléfono</option>				
				<option value="S">Sitio Web</option>
				<option value="E">Email</option>
				<option value="P">Personal</option>
			</select>
		</td>
		<td colspan="2" width="100%">		
			<table id="id_of_table_p">
				<tr>
					<td>						
						<liferay-ui:message key="number"/>:		
						(<input
						id="<portlet:namespace />telefono1_areaP"
						name="<portlet:namespace />telefono1_areaP" maxlength="5"
						type="text" style="width: 30px;"
						value=""/>)- <input id="<portlet:namespace />telefono1_numeroP" name="<portlet:namespace />telefono1_numeroP"  maxlength="10"
						type="text" style="width: 65px;"	value=""/>
						&nbsp;Ext.&nbsp; <input id="<portlet:namespace />telefono1_extP"
						name="<portlet:namespace />telefono1_extP"  maxlength="5"
						type="text" style="width: 30px;"
						value=""/>			
					</td>				
					<td>						
						<liferay-ui:message key="contacto"/>:
						<input id="<portlet:namespace />contactoCP" name="<portlet:namespace />contactoCP" maxlength="50" type="text" style="width: 300px;"/>						
					</td>						
				</tr>
			</table>
		</td>
		<td>
			<liferay-ui:message key="observaciones"/>:
		</td>
		<td>	
			<input id="<portlet:namespace />observacionesCP" name="<portlet:namespace />observacionesCP" size="20" maxlength="50" type="text"/>
		</td>
		<td> 
			<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarContactoP();" />		
		</td>	
		
	</tr>		
	<tr>
		<td colspan="7">
			<div align="center" id="<portlet:namespace />agregandoContactoPers">			
				<img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />					
			</div>
		</td>
	</tr>

	<tr>
		<td colspan="7" align="center">
			<div align="center" id="<portlet:namespace />contactosPers">
				<liferay-util:include page="/html/portlet/empresas/contactos_search_result_personas.jsp">	
				</liferay-util:include>
			</div>
		</td>
	</tr>
</table>
<%-- <div align="center" id="<portlet:namespace />hiddendiv">
</div> --%>
</fieldset>
</div>
<script type="text/javascript">

<%if(portlet_name.equals("estudio_isidro")){%>	
	jQuery('#<portlet:namespace />ocultarContactosPersonalizados').css('display','none')		
<%}%>
function <portlet:namespace />showHideDivContactosPers(){		
	if (jQuery("#<portlet:namespace />ocultarContactosPers").css('display') === 'none') {
		jQuery('#<portlet:namespace />ocultarContactosPers').css('display','block')
		jQuery('#<portlet:namespace />arrow_contactos_pers').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />ocultarContactosPers').css('display','none')
		jQuery('#<portlet:namespace />arrow_contactos_pers').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}
	
	jQuery('#<portlet:namespace />agregandoContactoPers').hide();
	jQuery('#<portlet:namespace />agregandoLineas').hide();
	
	function <portlet:namespace />agregarContactoP(){
			jQuery('#<portlet:namespace />agregandoContactoPers').show();	
			//var tel_pais=jQuery('#<portlet:namespace />telefono1_pais').val();			
			var tel_area=jQuery('#<portlet:namespace />telefono1_areaP').val();
			var tel_numero=jQuery('#<portlet:namespace />telefono1_numeroP').val();
			var tel_ext=jQuery('#<portlet:namespace />telefono1_extP').val();				
			var contactoC=jQuery('#<portlet:namespace />contactoCP').val();
			var observacionesC=jQuery('#<portlet:namespace />observacionesCP').val();				
			var tipo=jQuery('#<portlet:namespace />tipo_contactoP').val();
			var cargoC=jQuery('#<portlet:namespace />cargoCP').val();
			var nomyape=jQuery('#<portlet:namespace />apeynomP').val();			
			var profesionC=jQuery('#<portlet:namespace />profesionCP').val();
			var idCont = jQuery('#<portlet:namespace/>idContactoCP').val();
			
			
			
			if(nomyape!=null && nomyape!=''){
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_contacto';			
				url=url+'&tipo=' +tipo
							+'&idContactoC='+ idCont
							+'&tel_area=' + tel_area 
							+'&tel_numero=' + tel_numero
							+'&tel_ext='+ tel_ext
							+'&contactoC='+encodeURI(contactoC)
							+'&observacionesC='+encodeURI(observacionesC)
							+'&cargoC='+encodeURI(cargoC)
							+'&nomyape='+encodeURI(nomyape)
							+'&profesionC='+encodeURI(profesionC)
							+'&accion=ADD';
							
									
				jQuery('#<portlet:namespace />contactosPers').load(url, function() {
															jQuery('#<portlet:namespace />agregandoContactoPers').hide();		
															//jQuery('#<portlet:namespace />telefono1_pais').val("");			
															jQuery('#<portlet:namespace />telefono1_areaP').val("");
															jQuery('#<portlet:namespace />telefono1_numeroP').val("");
															jQuery('#<portlet:namespace />telefono1_extP').val("");				
															jQuery('#<portlet:namespace />contactoCP').val("");
															jQuery('#<portlet:namespace />observacionesCP').val("");
															/* jQuery('#<portlet:namespace />cargoC').val("");
															jQuery('#<portlet:namespace />profesionC').val("");
															jQuery('#<portlet:namespace />apeynom').val(""); */
															jQuery('#<portlet:namespace />tipoP').val("");
															jQuery('#<portlet:namespace />idContactoCP').val("");
															<%if(portlet_name.equals("estudio_isidro")){%>	
																jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','block');		
															<%}%>	
											   }
				 );
			
			}else{
				alert('Debe ingresar un nombre y apellido');
				jQuery('#<portlet:namespace />agregandoContactoPers').hide();
			}	
	}
	
	function editarContactoP(tipo, cargo, profesionC, nombre, contactoC, observaciones, area, numero, ext, idContactoC){
	    
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_contacto';		
		url=url+'&tipo=' +tipo
		+'&contactoC='+encodeURI(contactoC)
		+'&idContactoC='+idContactoC			
		+'&accion=EDIT';			
		jQuery('#<portlet:namespace />contactosPers').load(url, function() {
																					jQuery('#<portlet:namespace />agregandoContactoPers').hide();																					
																					ocultarComponenteP(tipo);
																					jQuery('#<portlet:namespace />tipo_contactoP').val(tipo);
																					jQuery("#<portlet:namespace/>tipo_contactoP option[value="+ tipo.substring(0, 1)  +"]").attr("selected",true);
																					jQuery('#<portlet:namespace />telefono1_areaP').val(area);
																					jQuery('#<portlet:namespace />telefono1_numeroP').val(numero);
																					jQuery('#<portlet:namespace />telefono1_extP').val(ext);				
																					jQuery('#<portlet:namespace />contactoCP').val(contactoC);
																					jQuery('#<portlet:namespace />observacionesCP').val(observaciones);
																					jQuery('#<portlet:namespace />cargoCP').val(cargo);
																					jQuery('#<portlet:namespace />apeynomP').val(nombre);
																					jQuery('#<portlet:namespace />profesionCP').val(profesionC);
																					jQuery('#<portlet:namespace/>idContactoCP').val(idContactoC);
																	   }
															   );
			
	}
	

	function borraContactoP(tipo, contactoC, idContactoC,nomyape){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_contacto';		
			url=url+'&tipo=' +tipo
			+'&contactoC='+encodeURI(contactoC)
			+'&idContactoC='+idContactoC	
			+'&nomyape='+encodeURI(nomyape)
			+'&accion=DELETE';			
			jQuery('#<portlet:namespace />contactosPers').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoContactoPers').hide();  
																						<%if(portlet_name.equals("estudio_isidro")){%>	
																							jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','block');		
																						<%}%>	
																			   }
															   );
		}	
	}
	
	function ocultarComponenteP(sel) {
		
		if(sel.value=="T" || sel=="TELEFONO"){			
			show_hide_columnP(0,true);
			show_hide_columnP(1,false);		
		}else if (sel.value=="E" || sel=="EMAIL" || sel.value=="S" || sel=="SITIOWEB" || sel.value=="P"|| sel=="PARTICULAR"){
			show_hide_columnP(0,false);
			show_hide_columnP(1,true);					
		}else{
			show_hide_columnP(0,false);
			show_hide_columnP(1,false);		
		}
 	   		    	   
 	}
	
	function show_hide_columnP(col_no, do_show) {
	    var stl;
	    if (do_show) stl = 'block'
	    else         stl = 'none';

	    var tbl  = document.getElementById('id_of_table_p');
	    var rows = tbl.getElementsByTagName('tr');

	    for (var row=0; row<rows.length;row++) {
	      var cels = rows[row].getElementsByTagName('td')	      
	      cels[col_no].style.display=stl;	      
	    }
	}
	
	show_hide_columnP(0,false);
	show_hide_columnP(1,false);
</script>