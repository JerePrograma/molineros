<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%
//EntidadPadronUnificado empresa = (EntidadPadronUnificado)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);

Seccional seccional = (Seccional)portletSession.getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION ,PortletSession.APPLICATION_SCOPE);

Calendar current = CalendarFactoryUtil.getCalendar();

String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "afiliados";
}



//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
boolean rolABMSeccionales = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_SECCIONALES);
////String dinámico que se le debe pasar a esta pagina para que sepa si se trata de contactos de la Empresa o Personalizados
//boolean esEmprCont= Boolean.parseBoolean(ParamUtil.getString(request, "esEmpresaCont"));

%>

	
<div id="<portlet:namespace />ocultarContactos">
<fieldset class="block-labels">	
<legend><liferay-ui:message	key="address-book" /></legend>

<table class="lfr-table" style="border-collapse:separate; border-spacing: 2px; width: 100%;" > 
	
	<tr>	
		<input id="<portlet:namespace />profesionC" name="<portlet:namespace />profesionC" type="hidden" value="" />
		<input id="<portlet:namespace />cargoC" name="<portlet:namespace />cargoC" type="hidden" />
		<input id="<portlet:namespace />apeynom" name="<portlet:namespace />apeynom" type="hidden" />
		
		<td style="width: 155px;"> <input type="hidden" name="<portlet:namespace/>idContactoC" id="<portlet:namespace/>idContactoC" value="" >		
			<liferay-ui:message key="tipo-contacto"/>:
		</td>
		<td>	
			<select name="<portlet:namespace />tipo_contacto" id="<portlet:namespace />tipo_contacto" onChange="ocultarComponente(this)">
				<option value="" selected="selected">--Seleccione Tipo--</option>
				<option value="T">Teléfono</option>				
				<option value="S">Sitio Web</option>
				<option value="E">Email</option>
				<option value="P">Personal</option>
			</select>
		</td>
		<td colspan="2" width="100%">		
			<table id="id_of_table">
				<tr>
					<td>						
						<liferay-ui:message key="number"/>:		
						(<input
						id="<portlet:namespace />telefono1_area"
						name="<portlet:namespace />telefono1_area" maxlength="5"
						type="text" style="width: 30px;"
						value=""/>)- <input id="<portlet:namespace />telefono1_numero" name="<portlet:namespace />telefono1_numero"  maxlength="10"
						type="text" style="width: 65px;"	value=""/>
						&nbsp;Ext.&nbsp; <input id="<portlet:namespace />telefono1_ext"
						name="<portlet:namespace />telefono1_ext"  maxlength="5"
						type="text" style="width: 30px;"
						value=""/>			
					</td>				
					<td>						
						<liferay-ui:message key="contacto"/>:
						<input id="<portlet:namespace />contactoC" name="<portlet:namespace />contactoC" maxlength="50" type="text" style="width: 300px;"/>						
					</td>						
				</tr>
			</table>
		</td>
		<td>
			<liferay-ui:message key="observaciones"/>:
		</td>
		<td>	
			<input id="<portlet:namespace />observacionesC" name="<portlet:namespace />observacionesC" size="20" maxlength="50" type="text"/>
		</td>
		<td> 
		   <c:if test="<%= rolABMSeccionales %>">
			<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarContacto();" />
		   </c:if>			
		</td>	
		
	</tr>		
	<tr>
		<td colspan="7">
			<div align="center" id="<portlet:namespace />agregandoContacto">			
				<img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />					
			</div>
		</td>
	</tr>

	<tr>
		<td colspan="7" align="center">
			<div align="center" id="<portlet:namespace />contactos">
				<liferay-util:include page="/html/portlet/afiliados/editar_seccional_contactos_search_result.jsp">											
				</liferay-util:include>
			</div>
		</td>
	</tr>
</table>
</fieldset>
</div>
<script type="text/javascript">

<%if(portlet_name.equals("estudio_isidro")){%>	
	jQuery('#<portlet:namespace />ocultarContactos').css('display','none')		
<%}%>
function <portlet:namespace />showHideDivContactos(){		
	if (jQuery("#<portlet:namespace />ocultarContactos").css('display') === 'none') {
		jQuery('#<portlet:namespace />ocultarContactos').css('display','block')
		jQuery('#<portlet:namespace />arrow_contactos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />ocultarContactos').css('display','none')
		jQuery('#<portlet:namespace />arrow_contactos').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}
	
	jQuery('#<portlet:namespace />agregandoContacto').hide();
	jQuery('#<portlet:namespace />agregandoLineas').hide();
	function <portlet:namespace />agregarContacto(){
			jQuery('#<portlet:namespace />agregandoContacto').show();	
			//var tel_pais=jQuery('#<portlet:namespace />telefono1_pais').val();			
			var tel_area=jQuery('#<portlet:namespace />telefono1_area').val();
			var tel_numero=jQuery('#<portlet:namespace />telefono1_numero').val();
			var tel_ext=jQuery('#<portlet:namespace />telefono1_ext').val();				
			var contactoC=jQuery('#<portlet:namespace />contactoC').val();
			var observacionesC=jQuery('#<portlet:namespace />observacionesC').val();				
			var tipo=jQuery('#<portlet:namespace />tipo_contacto').val();
			var cargoC=jQuery('#<portlet:namespace />cargoC').val();
			var nomyape=jQuery('#<portlet:namespace />apeynom').val();			
			var profesionC=jQuery('#<portlet:namespace />profesionC').val();
			var idCont = jQuery('#<portlet:namespace/>idContactoC').val();
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_seccional_agregar_contacto';			
			url=url+'&tipo=' +tipo
						+'&idContactoC='+ idCont
						+'&tel_area=' + encodeURI(tel_area) 
						+'&tel_numero=' + encodeURI(tel_numero)
						+'&tel_ext='+ encodeURI(tel_ext)
						+'&contactoC='+encodeURI(contactoC)
						+'&observacionesC='+encodeURI(observacionesC)
						+'&cargoC='+encodeURI(cargoC)
						+'&nomyape='+encodeURI(nomyape)
						+'&profesionC='+encodeURI(profesionC)
						+'&accion=ADDCONTACTO';
						
			jQuery('#<portlet:namespace />contactos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoContacto').hide();		
														//jQuery('#<portlet:namespace />telefono1_pais').val("");			
														jQuery('#<portlet:namespace />telefono1_area').val("");
														jQuery('#<portlet:namespace />telefono1_numero').val("");
														jQuery('#<portlet:namespace />telefono1_ext').val("");				
														jQuery('#<portlet:namespace />contactoC').val("");
														jQuery('#<portlet:namespace />observacionesC').val("");
														/* jQuery('#<portlet:namespace />cargoC').val("");
														jQuery('#<portlet:namespace />profesionC').val("");
														jQuery('#<portlet:namespace />apeynom').val(""); */
														jQuery('#<portlet:namespace />tipo').val("");
														jQuery('#<portlet:namespace />idContactoC').val("");
										   }
			 );	
	}
	
	function editarContacto(tipo, cargo, profesionC, nombre, contactoC, observaciones, area, numero, ext, idContactoC){
	    
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_seccional_agregar_contacto';		
		url=url+'&tipo=' +tipo
		+'&contactoC='+encodeURI(contactoC)
		+'&idContactoC='+idContactoC
		+'&accion=EDITCONTACTO';			
		
		if(area=="null") area="";
		if(ext=="null") ext="";
		if(observaciones="null") observaciones="";
		
		jQuery('#<portlet:namespace />contactos').load(url, function() {
																					jQuery('#<portlet:namespace />agregandoContacto').hide();																					
																					ocultarComponente(tipo);
																					jQuery('#<portlet:namespace />tipo_contacto').val(tipo);
																					jQuery("#<portlet:namespace/>tipo_contacto option[value="+ tipo.substring(0, 1)  +"]").attr("selected",true);
																					jQuery('#<portlet:namespace />telefono1_area').val(area);
																					jQuery('#<portlet:namespace />telefono1_numero').val(numero);
																					jQuery('#<portlet:namespace />telefono1_ext').val(ext);				
																					jQuery('#<portlet:namespace />contactoC').val(contactoC);
																					jQuery('#<portlet:namespace />observacionesC').val(observaciones);
																					jQuery('#<portlet:namespace />cargoC').val(cargo);
																					jQuery('#<portlet:namespace />apeynom').val(nombre);
																					jQuery('#<portlet:namespace />profesionC').val(profesionC);
																					jQuery('#<portlet:namespace/>idContactoC').val(idContactoC);
																	   }
															   );
			
	}
	

	function borraContacto(tipo, contactoC, area, numero, ext, idContactoC){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_seccional_agregar_contacto';		
			url=url+'&tipo=' +tipo
			+'&contactoC='+encodeURI(contactoC)
			+'&idContactoC='+idContactoC
			+'&tel_area=' + area 
			+'&tel_numero=' + encodeURI(numero)
			+'&tel_ext='+ ext
			+'&accion=DELETECONTACTO';	
			
			jQuery('#<portlet:namespace />contactos').load(url, function() {
						jQuery('#<portlet:namespace />agregandoContacto').hide();  
																		   }
				  );
		}	
	}
	
	function ocultarComponente(sel) {
		
		if(sel.value=="T" || sel=="TELEFONO"){			
			show_hide_column(0,true);
			show_hide_column(1,false);		
		}else if (sel.value=="E" || sel=="EMAIL" || sel.value=="S" || sel=="SITIOWEB" || sel.value=="P"|| sel=="PARTICULAR"){
			show_hide_column(0,false);
			show_hide_column(1,true);					
		}else{
			show_hide_column(0,false);
			show_hide_column(1,false);		
		}
 	   		    	   
 	}
	
	function show_hide_column(col_no, do_show) {
	    var stl;
	    if (do_show) stl = 'block'
	    else         stl = 'none';

	    var tbl  = document.getElementById('id_of_table');
	    var rows = tbl.getElementsByTagName('tr');

	    for (var row=0; row<rows.length;row++) {
	      var cels = rows[row].getElementsByTagName('td')	      
	      cels[col_no].style.display=stl;	      
	    }
	}
	
	show_hide_column(0,false);
	show_hide_column(1,false);
</script>