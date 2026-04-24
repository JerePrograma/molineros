<%@ include file="/html/portlet/cgt_ddhh/init.jsp"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<% 

boolean showABMButtons =  PermissionUtil.userContainsRole(user,WebKeysCGT.ROL_ABM_ORGANISMO);

NormaDdHh normaDDHH  = (NormaDdHh)portletSession.getAttribute(WebKeysCGT.NORMADDHH_EN_EDICION);

/*boolean esEdicion = true;
if(normaDDHH != null){
	esEdicion=false;
}*/

Calendar current = CalendarFactoryUtil.getCalendar();

if(normaDDHH !=null && normaDDHH.getFecha() != null){
	current.setTime(normaDDHH.getFecha());
	
}

String cmd=normaDDHH!=null&&normaDDHH.getId()!=0?"update":"";

ArrayList<TemasNormasDDHH> temasNormasDH = (ArrayList<TemasNormasDDHH>) portletSession.getAttribute(WebKeysCGT.TEMAS_NORMADDHH);
ArrayList<TiposNormasDDHH> tiposNormasDH = (ArrayList<TiposNormasDDHH>) portletSession.getAttribute(WebKeysCGT.TIPOS_NORMADDHH);

%>


<form name="<portlet:namespace />ndh" id="<portlet:namespace />ndh" >
<fieldset class="block-labels"><legend><liferay-ui:message key="datos-normaddhh" /></legend>

<table class="lfr-table" width="100%">	
	<tr>
		<td><label><liferay-ui:message key="sistema" />:</label></td>
		<td>
			<select name="<portlet:namespace/>sistemaselect" id="<portlet:namespace/>sistemaselect" <%if(!showABMButtons){%>disabled<%}%> onchange="javascript:filtrarTiposNormas(); mostrarInternacional();" >
				<%	for (String sist : WebKeysCGT.SISTEMA) {	%>
						<option value="<%= sist %>" <%= null!=normaDDHH && normaDDHH.getSistema().equals(sist) ? "selected" : "" %> >
							<%=sist%>
						</option>
				<%	}	%>					
			</select>
		</td>
		<td><label><liferay-ui:message key="tipo" />:</label></td>
		<td>
			<select name="<portlet:namespace/>tipo_norma" id="<portlet:namespace/>tipo_norma" <%if(!showABMButtons){%>disabled<%}%> onload="javascript:filtrarTiposNormas();">
				
				<% if(normaDDHH!=null){ for (TiposNormasDDHH t : tiposNormasDH) { %>
					<option
					<%= normaDDHH.getTipo().getId() == t.getId() ? "selected" : ""   %>
					value="<%= t.getId() %>"><%=t.getDescripcion()%></option>
				<% } }%>
			</select>
		</td>
		<td><label><liferay-ui:message key="numero" />:</label></td>
		<td>
			<input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="20" maxlength="15"  type="text" 
			value="<%=(null!=normaDDHH && normaDDHH.getNumero()!=null)?normaDDHH.getNumero():""%>" <%if(!showABMButtons){%>readonly<%}%>/>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>	
	<tr>
		<td>
			<label><liferay-ui:message key="fuentedepen" />:</label>
		</td>
		<td>
			<input id="<portlet:namespace />fuenteDependencia" name="<portlet:namespace />fuenteDependencia" size="20" maxlength="25"  type="text" 
				value="<%=(null!=normaDDHH && normaDDHH.getFuenteDependencia()!=null)?normaDDHH.getFuenteDependencia():""%>" <%if(!showABMButtons){%>readonly<%}%>/>
		</td>
		<td>
			<label><liferay-ui:message key="author" />:</label>
		</td>
		<td>
			<input id="<portlet:namespace />autor" name="<portlet:namespace />autor" size="20" maxlength="25"  type="text" 
				value="<%=(null!=normaDDHH && normaDDHH.getAutor()!=null)?normaDDHH.getAutor():""%>" <%if(!showABMButtons){%>readonly<%}%>/>
		</td>	
		<td>
			<label><liferay-ui:message key="fecha" />:</label>
		</td>
		<td>
			<span id="<portlet:namespace />fecha">
			<liferay-ui:input-date
			dayParam="fechaDia"
			dayValue="<%= current.get(Calendar.DATE) %>" 
			monthParam="fechaMes"
			monthValue="<%= current.get(Calendar.MONTH) %>"				
			yearParam="fechaAnio"
			yearValue="<%= current.get(Calendar.YEAR) %>"
			yearRangeStart="<%= current.get(Calendar.YEAR) -5 %>"	
			yearRangeEnd="<%= current.get(Calendar.YEAR) + 1%>"
			firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
			disabled="<%= !showABMButtons  %>" />
			</span>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td>
			<label><liferay-ui:message key="lugar" />:</label>
		</td>
		<td><textarea id="<portlet:namespace />lugar" name="<portlet:namespace />lugar" cols="45" rows="3" <%if(!showABMButtons){%>readonly<%}%>><%=(null!=normaDDHH&&normaDDHH.getLugar()!=null)?normaDDHH.getLugar():""%></textarea>
		</td>
		<td>
			<label><liferay-ui:message key="resumen" />:</label>
		</td>
		<td><textarea id="<portlet:namespace />resumen" name="<portlet:namespace />resumen" cols="45" rows="3" <%if(!showABMButtons){%>readonly<%}%>><%=(null!=normaDDHH&&normaDDHH.getResumen()!=null)?normaDDHH.getResumen():""%></textarea>
		</td>
		<td>
			<label><liferay-ui:message key="tema" />:</label>
		</td>
		<td>
			<select name="<portlet:namespace/>tema_norma" id="<portlet:namespace/>tema_norma" <%if(!showABMButtons){%>disabled<%}%>>
				<%	for (TemasNormasDDHH temas : temasNormasDH ) {	%>
						<option value="<%= temas.getId() %>" <%= null!=normaDDHH && null!=normaDDHH.getTema() && normaDDHH.getTema().getId() == temas.getId()  ? "selected" : "" %> >
							<%=temas.getDescripcion()%>
						</option>
				<%	}	%>				
			</select>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td>
			<label><liferay-ui:message key="contenido" />:</label>
		</td>
		<td colspan="5">
			<textarea id="<portlet:namespace />contenido" name="<portlet:namespace />contenido" cols="45" rows="3" <%if(!showABMButtons){%>readonly<%}%>><%=(null!=normaDDHH&&normaDDHH.getContenido()!=null)?normaDDHH.getContenido():""%></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>	
	<tr>
		<td>
			<label><liferay-ui:message key="web" />:</label>
		</td>
		<td colspan="5">
			<input id="<portlet:namespace />link" name="<portlet:namespace />link" size="50" maxlength="100"  type="text" 
				value="<%=(null!=normaDDHH && normaDDHH.getLink()!=null)?normaDDHH.getLink():""%>" <%if(!showABMButtons){%>readonly<%}%>/>
		</td>
	</tr>		
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
</table>
	<div name="<portlet:namespace />divDetalleInternac" id="<portlet:namespace />divDetalleInternac" <% if(normaDDHH!=null && normaDDHH.getSistema().equalsIgnoreCase("INTERNACIONAL") ){%> style="display: inline;" <% } else { %> style="display: none;" <% } %> > 
		<table>
		<tr>
			<td>
				<label><liferay-ui:message key="sigla" />:</label>
			</td>
			<td colspan="5">&nbsp;
				<input id="<portlet:namespace />sigla" name="<portlet:namespace />sigla" size="50" maxlength="100"  type="text" 
					value="<%=(null!=normaDDHH && normaDDHH.getSigla() !=null)?normaDDHH.getSigla():""%>" <%if(!showABMButtons){%>readonly<%}%>/>
			</td>
		</tr>
		<tr>
			<td colspan="6">&nbsp;</td>
		</tr>
		<tr>
			<td>
				<label><liferay-ui:message key="incLegNac" />:</label>
			</td>
			<td colspan="5">&nbsp;
				<input id="<portlet:namespace />incLegNac" name="<portlet:namespace />incLegNac" size="50" maxlength="100"  type="text" 
					value="<%=(null!=normaDDHH && normaDDHH.getIncLegisNac() !=null)?normaDDHH.getIncLegisNac():""%>" <%if(!showABMButtons){%>readonly<%}%>/>
			</td>
		</tr>
		</table>
	</div>

<%if(showABMButtons){%>
	<div align="center">
		<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveNormaDH();return false;"/>
	</div>
<%}%>
<input type="hidden" name="<portlet:namespace />cmd" id="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd!=null?cmd:""%>"/>
<input type="hidden" name="<portlet:namespace />id_normaddhh" id="<portlet:namespace />id_normaddhh" value="<%=normaDDHH!=null?normaDDHH.getId():""%>"/>
</fieldset>
</form>

<script type="text/javascript">	

	function filtrarTiposNormas() {		
		var idSistema = jQuery('#<portlet:namespace />sistemaselect').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/id_sistema_tipos_normas&idSistema='+idSistema;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace/>tipo_norma").length = 0;						
				var obj = jQuery.parseJSON(data);
				addElementToSelect("<portlet:namespace/>tipo_norma", "Seleccione un Tipo de Norma", 0);
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>tipo_norma", text, value);
				}                                                                                                                                                                                                                                                            
			}
		});		
	}

	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}

				
	var popup;
	function <portlet:namespace />saveNormaDH() {
		var cmd=document.<portlet:namespace />ndh.<portlet:namespace />cmd.value;		 
		if (<portlet:namespace />validarCampos()) {
			if(cmd==""){
				document.<portlet:namespace />ndh.<portlet:namespace /><%= Constants.CMD %>.value='<%= Constants.ADD %>';
			}			
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_norma_ddhh_entry" /></portlet:actionURL>';			
			document.<portlet:namespace />ndh.method = 'post';			
			submitForm(document.<portlet:namespace />ndh, url);			
		}
	}     
	
	function <portlet:namespace />validarCampos() {

		try{

			if (document.getElementById("<portlet:namespace />tipo_norma").value == 0) {
				alert("Debe seleccionar el tipo de la norma");
				document.getElementById("<portlet:namespace />tipo_norma").focus();
				return false;
			} 
			
			
			if (trim(document.getElementById("<portlet:namespace />numero").value) == "") {
				alert("Debe ingresar el numero de la norma");
				document.getElementById("<portlet:namespace />numero").focus();
				return false;
			} 

		} catch (err) {
			return false;
		}
		
		return true;
	} 
		
	function mostrarInternacional(){
		var s = jQuery("#<portlet:namespace />sistemaselect").val();
		
		if(s.length > 9){
			jQuery('#<portlet:namespace />divDetalleInternac').show();
		}else{
			document.getElementById("<portlet:namespace />sigla").value="";
			document.getElementById("<portlet:namespace />incLegNac").value="";
			jQuery('#<portlet:namespace />divDetalleInternac').hide();
		
		}	
		//document.getElementById("<portlet:namespace />divDetalleInternac").style.display = 'none';
		//document.getElementById("<portlet:namespace />divDetalleInternac").style.display = 'inline-block';
	}
	<%if(normaDDHH==null){ %>
	
		filtrarTiposNormas();

	<%} %>
</script>


