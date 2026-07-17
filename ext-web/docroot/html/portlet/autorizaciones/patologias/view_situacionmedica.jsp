<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/patologias/init.jsp"%>

<%
Calendar prestacionFecha = CalendarFactoryUtil.getCalendar();
String prestacionFechaString = prestacionFecha.get(Calendar.DATE)+"/"+(prestacionFecha.get(Calendar.MONTH) + 1)+"/"+prestacionFecha.get(Calendar.YEAR);
String cmd = (String) request.getAttribute(Constants.CMD);

int cantPrestacionesLista=0;
String nroSitMedica = "";
boolean esEdicion = false;
boolean inHabilitar = false;

Calendar fechaDia  =Calendar.getInstance(); 		
fechaDia.setTime(new Date());

Date fechaReg = null;
Afiliado afiliado= null ;

Calendar fechaVigenDesde = CalendarFactoryUtil.getCalendar();
fechaVigenDesde.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
Calendar fechaVigenHasta = CalendarFactoryUtil.getCalendar();
fechaVigenHasta.setTime(DateUtils.getLastDateOfYear(new Date(), true));

SituacionMedica  situacionMedica  = (SituacionMedica)request.getSession().getAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION);
if (situacionMedica != null  ) {
	 afiliado =  (Afiliado) situacionMedica.getAfiliado();
}

if (cmd != null && cmd.equalsIgnoreCase(Constants.VIEW)){
	inHabilitar= true;
}
if (cmd != null && cmd.equalsIgnoreCase(Constants.EDIT)){
    esEdicion = true;	
}

if(situacionMedica != null  ){
	nroSitMedica ="Nro Registro : " + "000"+  String.valueOf(situacionMedica.getId_Situacion() );
}
boolean fechaDesdeOk=false;
boolean fechaHastaOk=false;
fechaReg = Validator.isNotNull(situacionMedica)? situacionMedica.getFechaVigen_Desde()    : null;
if (fechaReg != null) {
	fechaVigenDesde.setTime(situacionMedica.getFechaVigen_Desde());
	fechaDesdeOk=true ;
}
fechaReg = Validator.isNotNull(situacionMedica)? situacionMedica.getFechaVigen_Hasta()    : null;
if (fechaReg != null) {
	fechaVigenHasta.setTime(situacionMedica.getFechaVigen_Hasta());
	fechaHastaOk=true;
}

List<TipoDiscapacidad> tiposDisc=(ArrayList<TipoDiscapacidad>) portletSession.getAttribute(WebKeysGlobal.TIPOS_DISCAPACIDAD,PortletSession.APPLICATION_SCOPE);
	if (tiposDisc == null) {
		tiposDisc=TraeListasServiceUtil.getTiposDiscapacidad();
		portletSession.setAttribute(WebKeysGlobal.TIPOS_DISCAPACIDAD,tiposDisc,PortletSession.APPLICATION_SCOPE);	
	}
		
%>
<style>

div.divHeaderNro {
  position: absolute;
  top: 230px;
  right:30;
  left:1000px;
  background-color: #cccccc;
  width:200px;
  height:20px;
  border:1px solid black;
  font-size:145%
}
</style>


<form action="EditarPrestadoresEntryAction" name="<portlet:namespace />sitmedica_fm" id="<portlet:namespace />sitmedica_fm" >

    <input  type="hidden" id="<portlet:namespace />fprest" name="<portlet:namespace />fprest" value="<%=prestacionFechaString%>" />
 	<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" /> 	
  	<input	type="hidden"  name="<portlet:namespace />cuiltitular"  id="<portlet:namespace />cuiltitular" />
 	<input	type="hidden"  name="<portlet:namespace />intetitular"  id="<portlet:namespace />intetitular" />
 	<input  type="hidden" id="<portlet:namespace />id_registro_situmedica"	name="<portlet:namespace />id_registro_situmedica" size="8"  value="<%=Validator.isNotNull(situacionMedica)  ? situacionMedica.getId_Situacion()  : "0"  %>"  />
    <input  type="hidden" id="<portlet:namespace />tipo_discapacidad_seleccionados"	name="<portlet:namespace />tipo_discapacidad_seleccionados" size="8"  value=""  />
    <input  type="hidden" id="<portlet:namespace />esDiscapacitado" name="<portlet:namespace />esDiscapacitado" value="0" />    
    <input  type="hidden" id="<portlet:namespace />codigoCie10"   name="<portlet:namespace />codigoCie10" value="<%=Validator.isNotNull(situacionMedica) && Validator.isNotNull(situacionMedica.getCie10() )   ? situacionMedica.getCie10()    : ""  %>" />
    <input  type="hidden" id="<portlet:namespace />registroDeBaja"   name="<portlet:namespace />registroDeBaja" value="" />
		
<fieldset class="block-labels">
	<legend>		
		<liferay-ui:message key="Cabecera Registro de Situación Medica del Afiliado" /> 
	</legend>
	
	<div class="divHeaderNro">		     
		  <label align='center' ><b> <%=nroSitMedica%> </b>  </label>   
    </div>
	
	<table align="center" class="lfr-table" style="border-collapse: separate; border-spacing: 3px; width:100%;">
	<tr>
		<td style="vertical-align: top;">
			<fieldset id="afilbusqueda" class="block-labels">
				<legend>
					<liferay-ui:message key="datos-afiliado" />
				</legend>

				<liferay-util:include page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>

					<% if (!esEdicion && (cmd != null && !cmd.equalsIgnoreCase(Constants.VIEW))) { %>
						<liferay-util:param value="true" name="edit_mode" />
					<% } else { %>
						<liferay-util:param value="false" name="edit_mode" />
					<% } %>

					<liferay-util:param value="<%=String.valueOf(true)%>" name="discapacidad" />
					<liferay-util:param value="<%= String.valueOf(true) %>" name="pag_reintegro" />

					<liferay-util:param name="cuil"
						value="<%=situacionMedica!=null?situacionMedica.getAfiliado().getCuil_titular() :null%>" />

					<liferay-util:param name="inte"
						value="<%=situacionMedica!=null?String.valueOf(situacionMedica.getInte() ):null%>" />

					<liferay-util:param value="" name="origen" />

				</liferay-util:include>
			</fieldset>
		</td>

		<td style="vertical-align: top; width:170px; padding-top:8px;">
			<fieldset class="block-labels seccionVerificarDomicilio"
				id="<portlet:namespace />seccionVerificarDomicilio"
				style="width:150px; text-align:center;">

				<table style="width:100%;">
					<tr>
						<td>&nbsp;</td>
					</tr>

					<tr>
						<td>
							<label>
								<liferay-ui:message key="contacto-verif-domi" />:
							</label>
						</td>
					</tr>

					<tr>
						<td>&nbsp;</td>
					</tr>

					<tr>
						<td>
							<div id="<portlet:namespace />divBotonActualizar">
								<% if (!inHabilitar) { %>
									<input type="button"
										value="Actualizar"
										onclick="javascript:mostrarDomicilioAfiliado();" />
								<% } %>
							</div>
						</td>
					</tr>

					<tr>
						<td>&nbsp;</td>
					</tr>

					<tr>
						<td>
							<div id="<portlet:namespace />divResultadoActualizarOK">
								<p>
									<b>
										<liferay-ui:message key="crm-actualiza-domicilio"/>
									</b>
								</p>
							</div>
						</td>
					</tr>
				</table>
			</fieldset>
		</td>
	</tr>
</table>	
	
</fieldset>

<fieldset  class="block-labels"><legend><liferay-ui:message
			key="situacion-medica-afiliado" /></legend> 		
<table  align='center'>		
	<tr>
	<td >&nbsp;</td>
	</tr>
		<tr>
		<td ><label><liferay-ui:message key="vigen-fecha-desde" />:</label> </td>
	<% if (fechaDesdeOk) {%>
			<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue="<%=fechaVigenDesde.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="<%=fechaVigenDesde.get(Calendar.MONTH)%>"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue="<%=fechaVigenDesde.get(Calendar.YEAR)%>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR) -1%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR) +10 %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /> </td>
			<%}else{ %>
			<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR)  -1%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  + 10 %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /> </td>
			<%} %>
		<td >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </td>				
		<td ><label>&nbsp;&nbsp;&nbsp;&nbsp;<liferay-ui:message key="vigen-fecha-hasta" />:</label> </td>
			<% if (fechaHastaOk) {%>
			<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue="<%=fechaVigenHasta.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="<%=fechaVigenHasta.get(Calendar.MONTH)%>"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue="<%=fechaVigenHasta.get(Calendar.YEAR)%>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR)  -1%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /> </td>
			<%}else{ %>
			<td> <liferay-ui:input-date dayParam="fechaHastaDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaHastaMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaHastaAnio"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR)%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR) +1   %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /></td>
			<%} %>
		</tr>	
</table>

<table align ='center'  class="lfr-table">
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="tipo-situacion-medica" />:</label> </td>
		<td>
			<select name="<portlet:namespace/>situacionMedica"  <% if (esEdicion) { %><%="disabled='disabled'" %><%}%> onclick="javascript:cambiacaption();" onchange="javascript:cambiacaption();"  <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%> 				
				id="<portlet:namespace/>situacionMedica">
					<option  value="0">Seleccione</option>
					<% for (TiposDeSituacionesMedicas situaciones  : listatipodesituacionesmedicas) { %>
					<option
					    <%= situacionMedica != null  && situacionMedica.getIdTipoSituMedica()   == situaciones.getId() ? "selected" : ""  %>
						value="<%= situaciones.getId() %>"><%=situaciones.getDescripcion()%>
						</option>
					<% } %>								
			</select>
		<td colspan="1"><label align='center' id="<portlet:namespace/>captionsituacionmedicasel">Detalle</label></td>
<!--  		
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />detalleSituMedica"
			name="<portlet:namespace />detalleSituMedica" size="100" maxlength="100"
			type="text"
			value="<%= situacionMedica != null &&  situacionMedica.getDetalleSituMedica() !=null  ? situacionMedica.getDetalleSituMedica()  : "" %>" />			
		</td>
-->		
		
		<td colspan="5">
		  <textarea rows="4" cols="100" maxlength="20000"   <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
		              id="<portlet:namespace />detalleSituMedica" 
		              name="<portlet:namespace />detalleSituMedica"
		              style="resize:vertical;"><%= situacionMedica != null &&  situacionMedica.getDetalleSituMedica() !=null  ? situacionMedica.getDetalleSituMedica()  : "" %></textarea>
	    </td>
	</tr>
</table>


</fieldset>
	
	
<label align='center' id="<portlet:namespace/>mensajeDeBaja"   style="color:red ;font-size:135%" ></label>
	
<!--  	************************* PATOLOGIAS DEL AFILIADO   *************************	-->
<fieldset id="listapatologias" class="block-labels"><legend><liferay-ui:message key="listado-patologias-situ-medica" /></legend>
<div  id="<portlet:namespace />lista_prestaciones_equipo" align="center"  style="height:170px; overflow: scroll; overflow-x: hidden;">
<table>						
		<tr>															
			<td colspan="10">
 				 <liferay-util:include page="/html/portlet/autorizaciones/patologias/lista_patologias_situacionmedica.jsp">
					</liferay-util:include> 
			</td>	
		</tr>
	</table>	
</div>
</fieldset>
	
	
	<!--  	************************* BLOQUE DE DATOS PARA DISCAPACITADO  *************************	-->
<div  id="<portlet:namespace />datosdiscapacitado">
<fieldset class="block-labels"><legend><liferay-ui:message key="det-discap" /></legend> 		
<table class="lfr-table">
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"><label><liferay-ui:message key="observaciones-diagnostico"/>:</label></td>
		<td colspan="9"><textarea rows="2" cols="100" maxlength="250" id="<portlet:namespace />diagnostico" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
			name="<portlet:namespace />diagnostico"   ><%=situacionMedica!=null && situacionMedica.getDetalleDiscapacidad().getDiagnostico() !=null ? situacionMedica.getDetalleDiscapacidad().getDiagnostico()  :""%></textarea>
		</td>
		
		<td><label><liferay-ui:message key="tipo-discapacidad"/>:</label></td>
		<td><select name="<portlet:namespace/>tipo_discapacidad" id="<portlet:namespace/>tipo_discapacidad" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
			   multiple="multiple">
				<option value=""></option>
				<%for (TipoDiscapacidad td : tiposDisc) {%>				
					<option value="<%=td.getId()%>"> <%=td.getDescripcion()%></option>
				<%}%>
			</select>			 
		</td>
		
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="dependencia"/>:</label></td>
		<td><select name="<portlet:namespace/>dependencia" id="<portlet:namespace/>dependencia" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
			 size="1">
			<option value="false" <%=Validator.isNotNull(situacionMedica) && Validator.isNotNull(situacionMedica.getDetalleDiscapacidad().isDependencia() && !situacionMedica.getDetalleDiscapacidad().isDependencia() )   ? "selected" : ""  %> "">No</option>
			<option value="true" <%=Validator.isNotNull(situacionMedica) && Validator.isNotNull(situacionMedica.getDetalleDiscapacidad().isDependencia() && situacionMedica.getDetalleDiscapacidad().isDependencia() )   ? "selected" : ""  %> "">Si</option>			
			</select>			 
			&nbsp;&nbsp;&nbsp;
			</td>
		
		
		<td><label><liferay-ui:message key="telef-contacto" />:</label></td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />telefono_contacto"
			name="<portlet:namespace />telefono_contacto" size="50" maxlength="100"
			type="text"
			value="<%= situacionMedica != null &&  situacionMedica.getDetalleDiscapacidad().getTelefono_contacto() !=null  ? situacionMedica.getDetalleDiscapacidad().getTelefono_contacto()  : "" %>" />			
		</td>
		
	</tr>
	<tr>
		
	</tr>
</table>
</fieldset>
</div>		   

<div  id="<portlet:namespace />datosNodiscapacitado">
<br>
<table class="lfr-table">
		<tr>
<td colspan="1"><label><liferay-ui:message key="observaciones-diagnostico"/>:</label></td>
		<td colspan="9"><textarea rows="1" cols="150" id="<portlet:namespace />diagnosticonodiscapacitado" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
			name="<portlet:namespace />diagnosticonodiscapacitado"   ><%=situacionMedica!=null && situacionMedica.getdiagnostico() !=null ?situacionMedica.getdiagnostico() :""%></textarea>
		</td>
		</tr>
</table>
<br>		
</div>

<div  id="<portlet:namespace />diagnosticociex">
	   <table align='center' class="lfr-table" style="border-collapse: separate; border-spacing: 0px;" >
		<tr>
		<td>
		   <fieldset class="block-labels"><legend><liferay-ui:message	key="datos-cie-diez" /></legend> 
	 	   <liferay-util:include page='/html/portlet/autorizaciones/busqueda_ciediez.jsp'>
                               <% if (!inHabilitar) { %>
                                 <liferay-util:param value="true" name="edit_mode" />
                                 <liferay-util:param value="false" name="inhabilitar" />
							   <%}else{ %>
                                 <liferay-util:param value="false" name="edit_mode" />
                                 <liferay-util:param value="true" name="inhabilitar" />
							   <%} %>
                               <liferay-util:param name="codigo" value="<%=situacionMedica!=null?situacionMedica.getDetalleDiscapacidad().getCie_diez()    :null%>" />							   			  				
		    </liferay-util:include>
		    </fieldset>		
		</td>
		</tr>
        </table>
</div>  
      

<br/>
<% if (situacionMedica== null ) { %>
<div  id="<portlet:namespace />saveSituacionMedica" align="center"  style="height:80px;  overflow-x: hidden;">
<table>
	<tr>	
<td>
<input type="button" value="<liferay-ui:message key="Grabar" />"
	onClick="<portlet:namespace />saveSituacionMedica();"  title="<liferay-ui:message key="Graba los Datos Ingresados." />"/>	
</td>
</tr>
</table>
</div>
<%}%>
<% if (situacionMedica!= null ) { %>
<div  id="<portlet:namespace />diveditSituacionMedica" align="center"  style="height:80px;  overflow-x: hidden;">
<table>
	<tr>	
<td>
<% if (!inHabilitar ) { %>
<div id='<portlet:namespace />divBotonEdicion'>
<input type="button" value="<liferay-ui:message key="Grabar"  />"
	onClick="<portlet:namespace />editaSituacionMedica();"  title="<liferay-ui:message key="Edita los Datos Ingresados." />"/>
</div>		
<% }%>
</td>

<%
boolean tienePdfConfigurado = false;

if (situacionMedica != null) {
    int idTipoSituMedica = situacionMedica.getIdTipoSituMedica();
    tienePdfConfigurado = (idTipoSituMedica == 1 || idTipoSituMedica == 2 || idTipoSituMedica == 3 || idTipoSituMedica == 6 || idTipoSituMedica == 7 || idTipoSituMedica == 8 || idTipoSituMedica == 12);
}
%>

<td style="padding-left:10px;">
          <span title="<%= tienePdfConfigurado ? "Imprime el formulario PDF de la situación médica" : "No hay pdf configurado para la situación médica" %>">
    <input type="button"
           id="<portlet:namespace />btnImprimirFormulario"
           value="Imprimir Formulario"
           onClick="<portlet:namespace />imprimirSituacionMedicaPdf();"
           <%= !tienePdfConfigurado ? "disabled=\"disabled\"" : "" %> />
</span>
        </td>
        
</tr>
</table>
</div>
<%}%>

<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>

<input	 type="hidden" name="<portlet:namespace />consultaequipointer"  id="<portlet:namespace />consultaequipointer" value="<%=inHabilitar  ? true  : false  %>"/>
<input  id="<portlet:namespace />nom_seleccionado" name="<portlet:namespace />nom_seleccionado" type="hidden" value="1"/>


<div id='validarExistenciaCuit' style="float: right;"></div>

</form>

<script type="text/javascript">

var popupMD;

jQuery('#<portlet:namespace />buscando').hide();
/* jQuery("#<portlet:namespace />botoneditareclamo").hide(); */
jQuery("#<portlet:namespace />printButton").hide();

jQuery("#<portlet:namespace />buttonaddafiliadodata").hide();

jQuery("#<portlet:namespace />divResultadoActualizarOK").hide();

jQuery('#diagnosticociex').mouseout(function(){
	setearDivDiscapacitado();
});

jQuery('#listapatologias').mouseout(function(){
	setearDivDiscapacitado();
});

jQuery('#<portlet:namespace/>situacionMedica').keydown(function(){
	cambiacaption();
});


jQuery("#<portlet:namespace />datosdiscapacitado").hide();
jQuery("#<portlet:namespace />datosNodiscapacitado").hide();
// *******************************************************************************************************************************
// *******************************************************************************************************************************


function <portlet:namespace />cerrarDivNm(){
	jQuery("#divSeguimientoSur").hide("slow");
}

function <portlet:namespace />cerrarNm(){
	<portlet:namespace />cerrarDivNm();
	if(popupMD){
		Liferay.Popup.close(popupMD);
	}
}


function validaDatos(){
	var respuesta;
	respuesta=true;
		  
	var diaExist  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaDesdeDia").val()));
	var mesExist  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaDesdeMes").val()));
	var anioExist   = isNaN(parseInt(jQuery("#<portlet:namespace />fechaDesdeAnio").val()));
	
	var diaExist1  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaHastaDia").val()));
	var mesExist1  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaHastaMes").val()));
	var anioExist1   = isNaN(parseInt(jQuery("#<portlet:namespace />fechaHastaAnio").val()));
	
        
	if   ( respuesta  &&  (diaExist || mesExist || anioExist) )   {
	   alert("Debe ingresar la fecha Desde.");
	   respuesta= false ;
	}
	
	if   ( respuesta  &&  (diaExist1 || mesExist1 || anioExist1)  && !(diaExist1 && mesExist1 && anioExist1) )   {
		   alert("Error en la fecha Hasta ingresada.");
		   respuesta= false ;
		}
	
	var cuil=jQuery('#<portlet:namespace />cuil').val();
	var inte=jQuery('#<portlet:namespace />inte').val();
	
    if ((cuil=="" || inte=="" ) && respuesta)
    {		
		alert ('Debe seleccionar al Afiliado.');
		jQuery("#<portlet:namespace />cuil").focus();
		respuesta= false;
	}
	
    var tipoSituacionMedica = jQuery('#<portlet:namespace/>situacionMedica').val();

    if (
        tipoSituacionMedica != "1" &&
        (
            jQuery('#<portlet:namespace />codigoCie').val() == "" ||
            jQuery('#<portlet:namespace />detalleCie').val() == ""
        ) &&
        jQuery('#<portlet:namespace />diagnostico').val() == "" &&
        jQuery('#<portlet:namespace />diagnosticonodiscapacitado').val() == ""
    ) {
        alert('El diagnostico del Afiliado es un dato obligatorio.');
        jQuery('#<portlet:namespace />codigoCie').focus();
        respuesta = false;
    }   
    
    if ( jQuery('#<portlet:namespace/>situacionMedica').val()=="0" )  {    	
    	alert ('La situacion medíca es un dato obligatorio.');
    	jQuery("#<portlet:namespace />codigoCie10").focus();
		respuesta= false;
    }
    	
	return respuesta;
}

<%-- function validarSiNumero(numero){	
	
	if (!/^([0-9])*$/.test(numero)  ){  //  Backspace, Delete keys
		return false 
	}else{
		return true 
	}	
}

function <portlet:namespace />imprimirRegistro(){
	<%if (esEdicion || inHabilitar) {%>	     
		window.location.href ="/pdfservlet/?accion=equipointerdisciplinario&idequipo=<%= situacionMedica.getId_Situacion()   %>";
	<%}%>
} --%>


<%-- function cargaValorPrestacionSeleccionada()
{
	var codigoPrestacion=jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val();
		
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_valor_prestacion_seleccionada&codigoPrestacion='+codigoPrestacion;
	jQuery.ajax({   
		url: url,
		success: function(data) {
			var obj = jQuery.parseJSON(data);
			var importe  = obj.importe ;
			var importeHonorario   = obj.importeHonorarios ;
			var importeGastos   = obj.importeGastos ;
			jQuery("#<portlet:namespace />importe" ).val(importe);			
								}
				});
} --%>

function seleccionaCamposCieDiez(codigo,descripcion ){	
	jQuery('#<portlet:namespace />codigoCie').val(codigo);
	jQuery('#<portlet:namespace />detalleCie').val(descripcion);		
}

/*
function cambiacaption() {
	valor = jQuery('#<portlet:namespace/>situacionMedica option:selected').html();
	if (jQuery('#<portlet:namespace/>situacionMedica' ).val()==0){
		jQuery("#<portlet:namespace/>captionsituacionmedicasel").html('Detalle');
	}else {
		jQuery("#<portlet:namespace/>captionsituacionmedicasel").html('Detalle ' + valor);	
	}	
}*/

function <portlet:namespace />saveSituacionMedica() {
			
	if ( validaDatos())  {
		
		var selectedValues = [];    
    	jQuery("#<portlet:namespace />tipo_discapacidad :selected").each(function(){
        selectedValues.push(jQuery(this).val()); 
    	});
		jQuery("#<portlet:namespace />tipo_discapacidad_seleccionados").val(selectedValues);		
		var detalleSitMed=jQuery('#<portlet:namespace />detalleSituMedica').val();		
		var accionEnCurso = document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value;
		document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.SAVE  %>';				
		
<%-- 	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_registro_situacionmedica_entry" /></portlet:actionURL>';
		url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest' + "&esDatosTab=true"+'&detalleSitMedEncode='+ encodeURI(detalleSitMed);
		
 --%>	
			var xportletUrl = '/autorizaciones/editar_registro_situacionmedica_entry';
			
			var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+
			'<liferay-portlet:param name="detalleSitMedEncode" value="__detalleSitMedEncode"/>'+			
			'<liferay-portlet:param name="esDatosTab" value="true"/>'+
			'</liferay-portlet:actionURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__accionEnCurso", accionEnCurso);
	  	    url = url.replace("__detalleSitMedEncode", encodeURI(detalleSitMed));
	  	    
		
		document.<portlet:namespace />sitmedica_fm.method = 'post';		
		submitForm(document.<portlet:namespace />sitmedica_fm, url);	

	}
		
}
	
function setearDivDiscapacitado() { 
  cuilDife  =  jQuery("#<portlet:namespace />cuiltitular").val()!=jQuery('#<portlet:namespace />cuil').val();
  inteDife  =  jQuery("#<portlet:namespace />intetitular").val()!=jQuery('#<portlet:namespace />inte').val();
  nonulos =  jQuery('#<portlet:namespace />cuil').val()!="" && jQuery('#<portlet:namespace />inte').val()!="";  
  otroafiliado=(jQuery("#<portlet:namespace />cuil").val()!="" && jQuery("#<portlet:namespace />inte").val()!="" && (cuilDife || inteDife));

  if ((cuilDife || inteDife) && nonulos) {
	  jQuery("#<portlet:namespace />datosNodiscapacitado").hide();
	  jQuery("#<portlet:namespace />datosdiscapacitado").hide();
	  
  }  
  if (otroafiliado){
	  if (jQuery("#<portlet:namespace />incapacidad_af").val() == '1') {
		  jQuery("#<portlet:namespace />datosdiscapacitado").show();
		  jQuery("#<portlet:namespace />esDiscapacitado").val(1);
		}else{
			jQuery("#<portlet:namespace />datosNodiscapacitado").show();
			<portlet:namespace />esDiscapacitado
			jQuery("#<portlet:namespace />esDiscapacitado").val(0);
		}  
	  jQuery("#<portlet:namespace />cuiltitular").val(jQuery('#<portlet:namespace />cuil').val());
	  jQuery("#<portlet:namespace />intetitular").val(jQuery('#<portlet:namespace />inte').val()) ;
  }	
}

<%if (situacionMedica!= null) {   %>
    jQuery("#<portlet:namespace/>mensajeDeBaja").html("");
	jQuery("#<portlet:namespace />registroDeBaja").val(<%=situacionMedica.getId_Situacion()%>);
	<portlet:namespace />buscarCieCodigo(); // carga los datos del cie 10
<%}%>



<%if(situacionMedica!=null && StringUtils.checkNotEmpty(situacionMedica.getDetalleDiscapacidad().getTiposDiscapacidadDelAfiliado()) ){ %>
var values='<%=situacionMedica.getDetalleDiscapacidad().getTiposDiscapacidadDelAfiliado()%>';
jQuery.each(values.split(","), function(i,e){
	jQuery("#<portlet:namespace />tipo_discapacidad option[value='" + e + "']").attr("selected", true);
});
<%}%>


function <portlet:namespace />editaSituacionMedica() {
	<% if (situacionMedica!=null ) { %>
		    if (validaDatos()) 
				{
				var detalleSitMed=jQuery('#<portlet:namespace />detalleSituMedica').val();		
				var selectedValues = [];    
			    	jQuery("#<portlet:namespace />tipo_discapacidad :selected").each(function(){
			        selectedValues.push(jQuery(this).val()); 
			    	});
			    	jQuery("#<portlet:namespace />tipo_discapacidad_seleccionados").val(selectedValues);			    
					var accionEnCurso = document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value;
					document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.UPDATE %>';			 
					
					<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_registro_situacionmedica_entry" /></portlet:actionURL>';
					url = url + '&accionEnCurso=' + accionEnCurso +'&detalleSitMedEncode='+ encodeURI(detalleSitMed) + "&esDatosTab=false"+"&id_registro_sitmed=<%=situacionMedica.getId_Situacion()%>";
					 --%>
					var xportletUrl = '/autorizaciones/editar_registro_situacionmedica_entry';
					
					var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
					'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
					'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+
					'<liferay-portlet:param name="detalleSitMedEncode" value="__detalleSitMedEncode"/>'+			
					'<liferay-portlet:param name="esDatosTab" value="true"/>'+
					'<liferay-portlet:param name="id_registro_sitmed" value="__id_registro_sitmed"/>'+					
					'</liferay-portlet:actionURL>';
				
				    url = url.replace("__xportletUrl",xportletUrl); 
			  	    url = url.replace("__accionEnCurso", accionEnCurso);
			  	    url = url.replace("__detalleSitMedEncode", encodeURI(detalleSitMed));
			  	    url = url.replace("__id_registro_sitmed", <%=situacionMedica.getId_Situacion()%>);
			  	  
			  	    
					document.<portlet:namespace />sitmedica_fm.method = 'post';				
					submitForm(document.<portlet:namespace />sitmedica_fm, url);			
					alert('Datos Editados.');
				}				
	<%}%>
}

function editaRegistrodeGrilla(idSitMedica) {
	
	var accionEnCurso = document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value;
	document.<portlet:namespace />sitmedica_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.EDIT  %>';				
	
	<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_registro_situacionmedica_entry" /></portlet:actionURL>';
	url = url + '&accionEnCurso=' + accionEnCurso + "&id_registro_sitmed=" + idSitMedica;
	 --%>
	var xportletUrl = '/autorizaciones/editar_registro_situacionmedica_entry';
	
	var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
	'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+
	'<liferay-portlet:param name="id_registro_sitmed" value="__id_registro_sitmed"/>'+
	'</liferay-portlet:actionURL>';
    url = url.replace("__xportletUrl",xportletUrl); 
	url = url.replace("__accionEnCurso", accionEnCurso);
	url = url.replace("__id_registro_sitmed", idSitMedica);
	
	
	document.<portlet:namespace />sitmedica_fm.method = 'post';		
	submitForm(document.<portlet:namespace />sitmedica_fm, url);	
}

function <portlet:namespace />imprimirSituacionMedicaPdf() {
	<% if (situacionMedica != null && (
	        situacionMedica.getIdTipoSituMedica() == 1 ||
	        situacionMedica.getIdTipoSituMedica() == 2 ||
	        situacionMedica.getIdTipoSituMedica() == 3 ||
	        situacionMedica.getIdTipoSituMedica() == 6 ||
	        situacionMedica.getIdTipoSituMedica() == 7 ||
	        situacionMedica.getIdTipoSituMedica() == 8 ||
	        situacionMedica.getIdTipoSituMedica() == 12
	    )) { %>
        window.open(
            "/pdfservlet/?accion=situacionMedicaPdf&id_situacion=<%= situacionMedica.getId_Situacion() %>",
            "_blank"
        );
    <% } else { %>
        alert("Esta situación médica no tiene formulario PDF configurado.");
    <% } %>
}

var popupDomicilio;

function mostrarDomicilioAfiliado() {
	
	var cuil_titu = jQuery("#<portlet:namespace />cuil").val();
	var inte = jQuery("#<portlet:namespace />inte").val();
	var email = "";

	if (cuil_titu == "" || inte == "") {
		alert("Debe seleccionar al Afiliado.");
		return;
	}

	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliado_datos&cuil_titular=';
	url += cuil_titu;
	url += '&inte=' + inte;

	jQuery.ajax({
		url: url,
		async: false,
		success: function(data) {
			var obj = jQuery.parseJSON(data);
			email = obj.email;
		}
	});

	popupDomicilio = Liferay.Popup({
		title: "<liferay-ui:message key="detalle-domicilio" />",
		modal: true,
		width: 950,
		height: 330,
		fixedcenter: true
	});

	var url1 = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/actualiza_domicilio'
		+ '&cuil_titular=' + cuil_titu
		+ '&inte=' + inte
		+ '&cmd=view'
		+ '&email=' + encodeURIComponent(email);

	jQuery(popupDomicilio).load(url1);
}

function <portlet:namespace />validarEmail() {
	var email = jQuery('#<portlet:namespace/>email').val();
/* 	var emailReg = /^([\da-z_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
 */	
 
/*  Se solicito quitar el 24/05/2016
	if(trim(email).length == 0){
		alert("El campo Email es Obligatorio");
		jQuery("#<portlet:namespace />email").focus();
		return false;
	} */
	if(trim(email).length == 0){
		return true;
	}
	var expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	
	if ( !expr.test(email) ){
	    alert("Error: La dirección de correo " + email + " es incorrecta.");
	    jQuery("#<portlet:namespace />email").focus();
		return false;
	}
	    
	/* if(trim(email).length > 0){	
		if( !emailReg.test( email ) ) {
			jQuery("#<portlet:namespace />email").focus();
			return false;
		} else {
			return true;
		}
	}else{
		return false;
	} */
	return true;
}

function confirmaActualizacionDomicilioAfiliado(){

	var d_id_domicilio=jQuery("#<portlet:namespace/>id_domicilio").val();
    var d_id_provincia = jQuery("#<portlet:namespace/>provincia").val();
	var d_id_localidad = jQuery("#<portlet:namespace/>localidad").val();
	var d_calle = jQuery("#<portlet:namespace />calle").val();
	var d_numero = jQuery("#<portlet:namespace />numero").val();
	var d_piso = jQuery("#<portlet:namespace />piso").val();
	var d_dpto = jQuery("#<portlet:namespace />dpto").val();
	var d_cod_pos = jQuery("#<portlet:namespace />cod_postal").val();
	var d_barrio = jQuery("#<portlet:namespace />barrio").val();
	var d_cod_area_tel = jQuery("#<portlet:namespace />cod_area_telefono").val();
	var d_telefono = jQuery("#<portlet:namespace />telefono").val();
	//var d_cod_area_laboral = jQuery("#<portlet:namespace />cod_area_tel_laboral").val();
	//var d_laboral = jQuery("#<portlet:namespace />tel_laboral").val();
	var d_cod_area_celu = jQuery("#<portlet:namespace />cod_area_celular").val();
	var d_celular = jQuery("#<portlet:namespace />celular").val();
	
	var d_email = jQuery("#<portlet:namespace />email").val();
	var d_email_original = jQuery("#<portlet:namespace />email_original").val();
	
//	var cuiltitular= jQuery('#<portlet:namespace />cuil_titular').val();
	var cuiltitular= jQuery('#<portlet:namespace />cuil').val();
	var integrante = jQuery("#<portlet:namespace />inte").val();
	
	var idPar = jQuery("#<portlet:namespace />idPar").val();
	if (idPar != "<%= WebKeysAfiliados.PARENTESCO_DEFAULT %>" &&
	    idPar != "<%= WebKeysAfiliados.CONYUGE_DEFAULT %>" &&
	    idPar != "<%= WebKeysAfiliados.CONCUBINO_DEFAULT %>") {
	  integrante = 0;
	}
	
	/*validamos los campos obligatorios*/
	if (trim(d_calle).length == 0){
		alert("Ingrese la calle del domicilio");
		jQuery('#<portlet:namespace/>calle').focus();
		return false;
	}
	
	if (
		 (trim(d_cod_area_tel) == '' && trim(d_telefono) != '') ||
		 (trim(d_cod_area_tel) != '' && trim(d_telefono) == '')
		){
		alert("El teléfono debe necesariamente tener el código de area y el número");
		jQuery('#<portlet:namespace />telefono').focus();
		return false;
	}
	
	if(trim(d_cod_area_tel).startsWith('0')){
		alert("El código de area del teléfono no debe iniciar con cero");
		jQuery("#<portlet:namespace />cod_area_telefono").focus();
		return false;
	}
	if(trim(d_telefono).startsWith('0')){
		alert("El número del teléfono no debe iniciar con cero");
		jQuery("#<portlet:namespace />telefono").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_tel).length>0 || trim(d_telefono).length>0){
		if(trim(d_cod_area_tel).length+trim(d_telefono).length!=10){
			alert("La longitud del código de área + teléfono debe de ser de 10 caracteres");
			jQuery("#<portlet:namespace />cod_area_telefono").focus();
			return false;
		}
	}
	/*
	if ((trim(d_cod_area_laboral) == '' && trim(d_laboral) != '') ||
		(trim(d_cod_area_laboral) != '' && trim(d_laboral) == '')
		){
		alert("El teléfono laboral debe necesariamente tener el código de area y el número");
		jQuery('#<portlet:namespace />tel_laboral').focus();
		return false;
	}
	
	if(trim(d_cod_area_laboral).startsWith('0')){
		alert("El código de area laboral no debe iniciar con cero");
		jQuery("#<portlet:namespace />cod_area_tel_laboral").focus();
		return false;
	}
	if(trim(d_laboral).startsWith('0')){
		alert("El número del teléfono laboral no debe iniciar con cero");
		jQuery("#<portlet:namespace />tel_laboral").focus();
		return false;
	}
	
	if(trim(d_cod_area_laboral).length>0 || trim(d_laboral).length>0){
		if(trim(d_cod_area_laboral).length+trim(d_laboral).length!=10){
			alert("La longitud del código de área + teléfono laboral debe de ser de 10 caracteres");
			jQuery("#<portlet:namespace />cod_area_tel_laboral").focus();
			return false;
		}
	}
	*/
	
	
	if(trim(d_cod_area_celu).startsWith('0')){
		alert("El código de area del celular no debe iniciar con cero");
		jQuery("#<portlet:namespace />cod_area_celular").focus();
		return false;
	}
	if(trim(d_celular).startsWith('0')){
		alert("El número del celular no debe iniciar con cero");
		jQuery("#<portlet:namespace />celular").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_celu).length>0 || trim(d_celular).length>0){
		if(trim(d_cod_area_celu).length+trim(d_celular).length!=10){
			alert("La longitud del código de área + celular debe de ser de 10 caracteres");
			jQuery("#<portlet:namespace />cod_area_celular").focus();
			return false;
		}
	}
	
	
	
	if(!<portlet:namespace />validarEmail()){
		return false;
	}
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/actualiza_domicilio&id_parentesco=' + idPar;
	jQuery.post(url,{
					 cuil_titular:cuiltitular,
					 inte:integrante,	 
					 id_domicilio:d_id_domicilio,
					 id_provincia:d_id_provincia,
					 id_localidad:d_id_localidad,
					 calle:d_calle,
					 numero:d_numero,
					 piso:d_piso,
					 departamento:d_dpto,
					 codigo_postal:d_cod_pos,
					 barrio:d_barrio,
					 cod_area_telefono:d_cod_area_tel,
					 telefono:d_telefono,
					 //cod_area_laboral:d_cod_area_laboral,
					 //telefono_laboral:d_laboral,
					 cod_area_celular:d_cod_area_celu,
					 celular:d_celular,
					 email:d_email,
					 email_original:d_email_original,
					 cmd:'save'}, function() {																																											
			if(popupDomicilio!=null){
				jQuery("#<portlet:namespace />divResultadoActualizarOK").show();
				jQuery("#<portlet:namespace />divBotonActualizar").hide();
				Liferay.Popup.close(popupDomicilio); 
			}	 
		});
} 
</script>

<style>
input:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}
</style>
