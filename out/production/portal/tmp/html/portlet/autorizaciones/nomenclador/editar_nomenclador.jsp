<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.PrestacionConcepto"%>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	Nomenclador nomenclador=(Nomenclador)request.getSession().getAttribute(WebKeysAutorizaciones.NOMENCLADOR_EN_EDICION);
	PrestacionConcepto prestacionConcepto=(PrestacionConcepto)request.getSession().getAttribute(WebKeysAutorizaciones.PRESTACIONCONCEPTO_EN_EDICION);
	String accion = (String)request.getSession().getAttribute("accion");
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
    List<TipoNomenclador> tipoNomencladorList=TraeListasServiceUtil.getTiposNomenclador();
	List<Especialidad> especialidadesList=TraeListasServiceUtil.getEspecialidadesNomenclador();
	
	int id_nomenclador=nomenclador!=null?(int)nomenclador.getId_prestacion():0;
	if(nomenclador==null){
		nomenclador= new Nomenclador();
	} 
%>

<!-- 
<form action="" method="post" name="<portlet:namespace />fmN">
 -->
	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<liferay-ui:success key="insertCabOk"  message="<%=(String)request.getAttribute(\"msgCabOk\")  %>"  />
<liferay-ui:success key="updateCabOk"  message="<%=(String)request.getAttribute(\"msgCabOk\")  %>"  />
<liferay-ui:success key="deleteItemOk" message="<%=(String)request.getAttribute(\"msgItemOk\") %>"  />
<liferay-ui:error key="avisoNomencladorDuplicado" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />

<fieldset class="block-labels"><legend>Nomenclador</legend>

<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
    <tr>
	   <td>
	   <label><liferay-ui:message key="tipo-nomenclador" />:</label>
	   </td>
	   <td>
			<select name="<portlet:namespace/>tipoNomenclador" id="<portlet:namespace/>tipoNomenclador" <%if(nomenclador.getId_prestacion()!=0){ %> disabled="disabled" <% } %> onchange="cambioTipo();" >
					<option value="0">Seleccione un nomenclador</option>
					<%	for (TipoNomenclador tnom : tipoNomencladorList) { 
					     
					    if(tnom.getId_tipo_nomenclador() == 1 || tnom.getId_tipo_nomenclador() == 6 ||
					    		tnom.getId_tipo_nomenclador() == 3 || tnom.getId_tipo_nomenclador() == 9 || tnom.getId_tipo_nomenclador() == 10
					    		|| tnom.getId_tipo_nomenclador() == 11 || tnom.getId_tipo_nomenclador() == 12 || "view".equalsIgnoreCase(accion) ){ %>
								<option value="<%= tnom.getId_tipo_nomenclador()%>"
								<%if(tnom.getId_tipo_nomenclador()== nomenclador.getId_tipo_nomenclador()){%> selected="selected" <% } %>>
								<%=tnom.getDescripcion()%></option>
					<%	} } %>
			</select>
		</td>
			    
		<td><label><liferay-ui:message key="codigo"/>:</label></td>
		<td><input id="<portlet:namespace />codigoNomenclador" name="<portlet:namespace />codigoNomenclador" size="10" maxlength="10" type="text" value='<%=nomenclador.getCodigo()==null?"":nomenclador.getCodigo()%>'
		     <% if (nomenclador.getId_prestacion()!=0) { %> <%="readonly='readonly'" %> <%}%>/></td>
		<td><label id="descripcionLabel" ><liferay-ui:message key="descripcion"/>:</label></td>
		<td><input id="<portlet:namespace />descripcionNomenclador" name="<portlet:namespace />descripcionNomenclador" size="80" maxlength="200" type="text" value='<%=nomenclador.getDescripcion()==null?"":nomenclador.getDescripcion()%>'/></td>
		
	</tr>
	
	<c:if test="<%=nomenclador==null || nomenclador.getId_prestacion()==0 %>">
	    <tr><td>&nbsp;</td></tr>
	    <tr>
	      <td colspan="4">
	       <span id="busqueda_medicamentos" hidden="true">
				<liferay-util:include page="/html/portlet/utils/medicamentos/busqueda_medicamentos.jsp">
					<liferay-util:param name="search_url" value="/autorizaciones/buscar_medicamentos" />
					<liferay-util:param name="troquel" value='' />
					<liferay-util:param name="nombre_medicamento" value='' />
					<liferay-util:param name="id_medicamento" value='' />
					<liferay-util:param name="esEditable" value='true' />
					<liferay-util:param name="mostrar_con_presentacion" value='true' />
				</liferay-util:include>
		   </span>
	      </td>	
	    </tr>
	</c:if>
	<tr>			
	    <td><label><liferay-ui:message key="especialidad" />:</label></td>
	    <td>
	    <select name="<portlet:namespace/>especialidad" id="<portlet:namespace/>especialidad">
			<option value="0">Seleccione una especialidad</option>
				<%for (Especialidad tnom : especialidadesList) { 
				 if("P".equalsIgnoreCase(tnom.getTipoEspecialidad() ) || "view".equalsIgnoreCase(accion) ){%>
						<option value="<%= tnom.getId_especialidad()%>"
						<%if(tnom.getId_especialidad()== nomenclador.getId_especialidad() ){%> selected="selected" <% } %>>
							<%=tnom.getDescripcion()%></option>
				<%}	} %>
		</select>
	    </td>
	    <td><label><liferay-ui:message key="resolucion"/>: </label></td>
	  
	    <td><input id="<portlet:namespace />resolucionNomenclador" name="<portlet:namespace />resolucionNomenclador" size="20" maxlength="15" type="text"
	   		value='<%=nomenclador.getResolucion()==null?"":nomenclador.getResolucion()%>'/></td>
	   
	    <td><label><liferay-ui:message key="codigo-hospital"/>:</label></td>
	  	<td><input id="<portlet:namespace />codigoHospital" name="<portlet:namespace />codigoHospital" size="10" maxlength="10" type="text" 
	       value='<%=nomenclador.getCodigoHospital()==null?"":nomenclador.getCodigoHospital()%>'/></td>
	</tr>
	</table>
	  <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
	    <tr>
	      <td><input type="checkbox"  name="<portlet:namespace />recuperaSUR" id="<portlet:namespace />recuperaSUR" 
	  	    <%if(nomenclador.getRecuperaSUR() ){%> checked="checked" <% } %>> Recupera SUR</td>							 
	      <td><input type="checkbox"  name="<portlet:namespace />requiereAutorizacion" id="<portlet:namespace />requiereAutorizacion" 
	        <%if(nomenclador.getRequiereAutorizacion() ){%> checked="checked" <% } %>> Requiere Autorizaci&oacute;n</td>
	      <td><input type="checkbox"  name="<portlet:namespace />supra" id="<portlet:namespace />supra" 
	  	    <%if(nomenclador.isSupra() ){%> checked="checked" <% } %>> SUPRA</td>	
	      <td><input type="checkbox"  name="<portlet:namespace />cirugia" id="<portlet:namespace />cirugia" 
	  	    <%if(nomenclador.isCirugia() ){%> checked="checked" <% } %>> Cirug&iacute;a</td>
	      <td><input type="checkbox"  name="<portlet:namespace />planbasico" id="<portlet:namespace />planbasico" 
	  	    <%if(nomenclador.isPlanBasico() ){%> checked="checked" <% } %>>Plan B&aacutesico</td>
	  	  <td><input type="checkbox"  name="<portlet:namespace />enviarWSTercerizadora" id="<portlet:namespace />enviarWSTercerizadora" 
	  	    <%if(nomenclador.isEnviarWSTercerizadora() ){%> checked="checked" <% } %>>Enviar a Tercerizadora</td> 	
	      <td colspan="1">&nbsp;</td>
	    </tr>
	  </table>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">	
	<tr>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="5" >  
		       <textarea rows="2" cols="100" maxlength="20000" 
		                 id="<portlet:namespace />observacionesNomenclador" 
		                 name="<portlet:namespace />observacionesNomenclador"
		                 style="resize: none;"><%=nomenclador.getObservaciones()!=null?nomenclador.getObservaciones():"" %>
		       </textarea>
		</td>    
	</tr>
</table>
  
  <fieldset class="block-labels"><legend>Modalidad de Atenci&oacute;n</legend>
    <table>
      <tr>
		<td colspan="9">
			<liferay-util:include page="/html/portlet/autorizaciones/nomenclador/nomencladorPlan_agregar.jsp">
				<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
			</liferay-util:include>
		</td>
	  </tr>
	 </table> 
  </fieldset>
  
  <fieldset class="block-labels"><legend>Valores por Plan</legend>
    <table>
      <tr>
		<td colspan="9">
			<liferay-util:include page="/html/portlet/autorizaciones/nomenclador/nomencladorPlan_topesReintegros.jsp">
				<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
			</liferay-util:include>
		</td>
	  </tr>
	 </table> 
  </fieldset>
  
  <fieldset class="block-labels"><legend>Detalle</legend>
    <fieldset class="block-labels"><legend>Honorarios</legend>
      <table class="lfr-table">
       <tr>
         <td width="115px"><label><liferay-ui:message key="cantidad-galeno" />  </label>  </td>
         <td>
		   <input type="text" id="<portlet:namespace />cantidad_galeno" name="<portlet:namespace />cantidad_galeno" onkeydown="allowOnlyDigitsAndDecimalsConSuprimir(event)" onchange="muestraImporteGalHno();agregarCeros(this);recalcularTotales();" 
		     value='<%=nomenclador.getCantidadGaleno()==null?"":nomenclador.getCantidadGaleno()%>' size="10"/>
	     </td>
	     <td width="115px"><label><liferay-ui:message key="cantidad-galeno-ayudante" />  </label>  </td>
         <td>
		   <input type="text" id="<portlet:namespace />cantidad_galeno_ayudante" name="<portlet:namespace />cantidad_galeno_ayudante" onkeydown="allowOnlyDigitsAndDecimalsConSuprimir(event)" onchange="muestraImporteGalAyu();agregarCeros(this);recalcularTotales();" 
		   value='<%=nomenclador.getCantidadGalenoAyudante()==null?"":nomenclador.getCantidadGalenoAyudante()%>' size="10" />
	     </td>
	     
	     <td width="115px"><label><liferay-ui:message key="cantidad-ayudantes" />  </label>  </td>
         <td>
		   <input type="text" id="<portlet:namespace />cantidad_ayudantes" name="<portlet:namespace />cantidad_ayudantes" onkeydown="allowOnlyDigitsConSuprimir(event)" onchange="muestraImporteGalAyu();recalcularTotales();"  
		   value='<%=nomenclador.getCantidadAyudantes()==null?"":nomenclador.getCantidadAyudantes()%>' size="10"/>
	     </td>
	     
	     <td width="115px" ><label><liferay-ui:message key="cantidad-galeno-anestesista" />  </label>  </td>
         <td>
		   <input type="text" id="<portlet:namespace />cantidad_galeno_anestesista" name="<portlet:namespace />cantidad_galeno_anestesista" onkeydown="allowOnlyDigitsAndDecimalsConSuprimir(event)" onchange="muestraImporteGalAne();agregarCeros(this);recalcularTotales();" 
		   value='<%=nomenclador.getCantidadGalenoAnestesista()==null?"":nomenclador.getCantidadGalenoAnestesista()%>' size="10"/>
	     </td>
	      <td><label><liferay-ui:message key="valor-galeno" />  </label>  </td>
          <td width="115px">
            <input type="text" id="<portlet:namespace />valor_galeno" name="<portlet:namespace />valor_galeno" onkeydown="allowOnlyDigitsAndDecimalsConSuprimir(event)"  onchange="muestraImportes();agregarCeros(this);recalcularTotales();" 
            value='<%=nomenclador.getValorGaleno()==null?"":nomenclador.getValorGaleno()%>' size="10"/>
          </td>
       </tr>
       
       
       <tr>
         <td></td> 
         
         <td><label id="val-gal-honor"> $ pesos</label></td>
          <td></td> 
         <td><label id="val-gal-ayud"> $ pesos</label></td>
         <td></td> 
         <td></td> 
         <td></td> 
         <td><label id="val-gal-anest"> $ pesos</label></td>
       </tr>
       
       <tr>
		 <td>&nbsp;</td>
		 <td>&nbsp;</td>
	   </tr>
      </table>
    </fieldset>  
    
    <fieldset class="block-labels"><legend>Gastos</legend>
      <table class="lfr-table">
       <tr>
        <td width="115px"><label><liferay-ui:message key="cantidad-galeno-gastos" />  </label>  </td>
         <td>
		   <input type="text" id="<portlet:namespace />cantidad_galeno_gastos" name="<portlet:namespace />cantidad_galeno_gastos" 
		   onkeydown="allowOnlyDigitsAndDecimalsConSuprimir(event);limitDecimals(2,document.getElementById('<portlet:namespace />cantidad_galeno_gastos'),event);" 
		   onchange="muestraImporteGalGto();agregarCeros(this);recalcularTotales();" 
		     value='<%=nomenclador.getCantidadGalenoGastos() ==null?"":nomenclador.getCantidadGalenoGastos()%>' size="10"/>
	     </td>
        
         <td width="115px"><label><liferay-ui:message key="valor-galeno-gastos" />  </label>  </td>
         <td>
		   <input type="text" id="<portlet:namespace />valor_galeno_gastos" name="<portlet:namespace />valor_galeno_gastos" onkeydown="allowOnlyDigitsAndDecimalsConSuprimir(event)" onchange="muestraImporteGalGto();agregarCeros(this);recalcularTotales();" 
		     value='<%=nomenclador.getValorGalenoGastos() ==null?"":nomenclador.getValorGalenoGastos()%>' size="10"/>
	     </td>
	     
         <td><label><liferay-ui:message key="importe-total" />  </label></td>
	     <td>
		     <input type="text" id="<portlet:namespace />importe_nomenclador" name="<portlet:namespace />importe_nomenclador" onkeydown="allowOnlyDigitsAndDecimalsConSuprimir(event)"
				onchange="agregarCeros(this);" 
				value='<%=nomenclador.getImporte()==null?"":nomenclador.getImporte()%>' size="10"/>
	     </td>
	     <td>&nbsp;</td>
	    </tr> 
	    <tr>
         <td></td> 
         <td><label id="val-gal-gastos"> $ pesos</label></td>
        </tr> 
      </table>   
    </fieldset>
    
    <table>
      <tr>
         <td>&nbsp;</td>
      </tr>   
      <tr>
       <td>  
          <input id="<portlet:namespace />siguiente"
				value="<liferay-ui:message key="next"/>"
				title="<liferay-ui:message key="next" />"
				onClick="javascript:submitFormNotSave();"
				type="button" />
          
		</td>		
	 </tr>			
	</table>			
  </fieldset>
  
</fieldset>
<input type="hidden" name="<portlet:namespace />id_nomenclador" id="<portlet:namespace />id_nomenclador" value="<%=id_nomenclador%>" />
<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa" />
<input type="hidden" value="" name="tabs1" id="tabs1" />
<input type="hidden" value="" name="view" id="view" />
<!--  
</form>
-->

<script type="text/javascript">
muestraImportes();
cambioTipo();

function muestraImporteGalHno(){
	var cantidadGaleno=jQuery('#<portlet:namespace />cantidad_galeno').val();
	var valorGaleno=jQuery('#<portlet:namespace />valor_galeno').val();
	var ret ='$ pesos';
	try{
	   if(cantidadGaleno * valorGaleno!=0){	
          ret='$ '+ Math.round(parseFloat(cantidadGaleno * valorGaleno)*100)/100;
	   }   
	}catch (err){}
	document.getElementById("val-gal-honor").innerHTML=ret;
}

function muestraImporteGalAyu(){
	var cantidadGaleno=jQuery('#<portlet:namespace />cantidad_galeno_ayudante').val();
	var valorGaleno=jQuery('#<portlet:namespace />valor_galeno').val();
	var cantidadAyudante=jQuery('#<portlet:namespace />cantidad_ayudantes').val();
	var ret ='$ pesos';

	try{
	   if(cantidadGaleno * valorGaleno!=0){	
          ret='$ '+ (Math.round(parseFloat(cantidadGaleno * valorGaleno * cantidadAyudante)*100)/100) ;
	   }   
	}catch (err){}
	document.getElementById("val-gal-ayud").innerHTML=ret;
}

function muestraImporteGalAne(){
	var cantidadGaleno=jQuery('#<portlet:namespace />cantidad_galeno_anestesista').val();
	var valorGaleno=jQuery('#<portlet:namespace />valor_galeno').val();
	var ret ='$ pesos';
	try{
	   if(cantidadGaleno * valorGaleno!=0){	
          ret='$ '+ Math.round(parseFloat(cantidadGaleno * valorGaleno)*100)/100;
	   }   
	}catch (err){}
	document.getElementById("val-gal-anest").innerHTML=ret;
}

function muestraImporteGalGto(){
	var cantidadGaleno=jQuery('#<portlet:namespace />cantidad_galeno_gastos').val();
	var valorGaleno=jQuery('#<portlet:namespace />valor_galeno_gastos').val();
	var ret ='$ pesos';
	try{
	   if(cantidadGaleno * valorGaleno!=0){	
          ret='$ '+ Math.round(parseFloat(cantidadGaleno * valorGaleno)*100)/100;
	   }   
	}catch (err){}
	document.getElementById("val-gal-gastos").innerHTML=ret;
}

function muestraImportes(){
	muestraImporteGalHno();
	muestraImporteGalAyu();
	muestraImporteGalAne();
	muestraImporteGalGto()
}

function recalcularTotales(){
	var cantidadGaleno=jQuery('#<portlet:namespace />cantidad_galeno').val();
	var cantidadGalenoAyudante=jQuery('#<portlet:namespace />cantidad_galeno_ayudante').val();
	var cantidadGalenoAnestesista=jQuery('#<portlet:namespace />cantidad_galeno_anestesista').val();
	var cantidadGalenoGastos=jQuery('#<portlet:namespace />cantidad_galeno_gastos').val();
	var valorGalenoGastos=jQuery('#<portlet:namespace />valor_galeno_gastos').val();
	var valorGaleno=jQuery('#<portlet:namespace />valor_galeno').val();
	var cantidadAyudante=jQuery('#<portlet:namespace />cantidad_ayudantes').val();
	var ret;
	
	ret= Math.round(
		 (Math.round(parseFloat(cantidadGaleno * valorGaleno )*100)/100 +
	     Math.round(parseFloat(cantidadGalenoAyudante * valorGaleno * cantidadAyudante)*100 )/100  +
	     Math.round(parseFloat(cantidadGalenoAnestesista * valorGaleno )*100)/100 +
	 	 Math.round(parseFloat(cantidadGalenoGastos * valorGalenoGastos)*100 )/100)
	 	 *100)/100;
	jQuery('#<portlet:namespace />importe_nomenclador').val(ret);
}

function submitFormNotSave(){
	    document.getElementById("<portlet:namespace/>tipoNomenclador").disabled=false;
		document.getElementById("cambioSolapa").value="cambioSolapa";
		document.getElementById("tabs1").value="datos-contables";
		document.getElementById("view").value="true";
		
		<%-- var url = '<portlet:actionURL windowState="<%=LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/autorizaciones/editar_nomenclador';
		var params = "&<%= Constants.CMD %>=" + "CAMBIO_SOLAPA";
		url = url + params; --%>
		var cmd_ = 'CAMBIO_SOLAPA';
		var xportletUrl = '/autorizaciones/editar_nomenclador';
		
		var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="cmd" value="__cmd"/>'+
	    '</liferay-portlet:actionURL>';

	    url = url.replace("__xportletUrl",xportletUrl); 
	    url = url.replace("__cmd",cmd_); 
	    
		submitForm(document.<portlet:namespace />nomen, url);
}


function cambioTipo() {
	if (jQuery("#<portlet:namespace/>tipoNomenclador").val() != 9){
		jQuery('#busqueda_medicamentos').hide();
		jQuery('#descripcionLabel').show();
		jQuery('#<portlet:namespace />descripcionNomenclador').show();
		jQuery('#<portlet:namespace />nombre_medicamento').val('');
		jQuery('#<portlet:namespace />troquel').val('');
	}
	if (jQuery("#<portlet:namespace/>tipoNomenclador").val() == 9){
		jQuery('#busqueda_medicamentos').show();
		jQuery('#descripcionLabel').hide();
		if(<%=nomenclador.getId_prestacion()%>==0){
		  jQuery('#<portlet:namespace />descripcionNomenclador').hide();
		}
		if(jQuery('#<portlet:namespace />descripcionNomenclador').val()!=null){
		   jQuery('#<portlet:namespace />nombre_medicamento').val(jQuery('#<portlet:namespace />descripcionNomenclador').val());	
		   jQuery('#<portlet:namespace />troquel').val(<%=nomenclador.getTroquelMedicamento()%>);
		}
	}
}


</script>