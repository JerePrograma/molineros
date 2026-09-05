<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/equipo_interdisciplinario/init.jsp"%>

<%

Calendar prestacionFecha = CalendarFactoryUtil.getCalendar();
String prestacionFechaString = prestacionFecha.get(Calendar.DATE)+"/"+(prestacionFecha.get(Calendar.MONTH) + 1)+"/"+prestacionFecha.get(Calendar.YEAR);
String cmd = (String) request.getAttribute(Constants.CMD);


int cantPrestacionesLista=0;
String nroIdEquiInter = "";
boolean esEdicion = false;
boolean inHabilitar = false;

boolean esFirmaAuditor = false;
boolean esFirmaAsistenteSocial = false;
boolean esFirmaTerapiaFisica = false;
boolean esFirmaPsicologo=false;
String pathFirmaAuditor = null;
String pathFirmaAsistenteSocial = null;
String pathFirmaTerapiaFisica = null;
String usrFirmaAuditor = null;
String usrFirmaAsistenteSocial =  null;
String usrFirmaTerapiaFisica =  null;
String pathFirmaPsicologo = null;
String usrFirmaPsicologo =  null;

String path = "/html/images/";

Calendar fechaDia  =Calendar.getInstance(); 		
fechaDia.setTime(new Date());

Calendar fechaEquipo   = Calendar.getInstance();
Date fechaReg = null;
Afiliado afiliado= null ;

EquipoInterdisciplinario  equipoInterdisciplinario  = (EquipoInterdisciplinario)request.getSession().getAttribute(WebKeysAutorizaciones.EQUIPO_DISCIPLINARIO_EN_EDICION);
if (equipoInterdisciplinario != null &&  equipoInterdisciplinario.getAfiliado() != null ) {
	 afiliado =  (Afiliado) equipoInterdisciplinario.getAfiliado();	 
			try{
				if ( afiliado.getDomicilioDefault().getProvinciaId()>1 ) { 
					localidades = localidadesPorProvincia.get(afiliado.getDomicilioDefault().getProvinciaId());	
				}else {
					localidades = localidadesPorProvincia.get(WebKeysAfiliados.ID_DEFAULT_PROVINCIA);	
				}
			}catch(Exception e){}
			List<FirmaAutorizante> firmas = equipoInterdisciplinario.getFirmaAutorizante();
			
//DS Agregado el 11/06/2026 por licencia de Marianela			
esFirmaAsistenteSocial = true;

			if(firmas !=  null && !firmas.isEmpty()){
				for (FirmaAutorizante  firma : firmas) {
					if (firma.getTipoDictamen() == 2){ //asistente-social
						esFirmaAsistenteSocial = true;
						pathFirmaAsistenteSocial = firma.getPath();
						usrFirmaAsistenteSocial = firma.getAltaUsr();
					}else if(firma.getTipoDictamen() == 1) { // auditor
						esFirmaAuditor = true;
						pathFirmaAuditor = firma.getPath();
						usrFirmaAuditor =  firma.getAltaUsr();
					}else if(firma.getTipoDictamen() == 4) { // auditor
						esFirmaAuditor = true;
						pathFirmaAuditor = firma.getPath();
						usrFirmaAuditor =  firma.getAltaUsr();	
					}else if(firma.getTipoDictamen() == 3){ // fisica terapia
						esFirmaTerapiaFisica = true;
						pathFirmaTerapiaFisica =  firma.getPath();
						usrFirmaTerapiaFisica =  firma.getAltaUsr();
					}else if(firma.getTipoDictamen() == 0){ // psicologo
						esFirmaPsicologo = true;
						pathFirmaPsicologo =  firma.getPath();
						usrFirmaPsicologo =  firma.getAltaUsr();
					}else if (firma.getTipoDictamen() == 5) { //asistente-social
						esFirmaAsistenteSocial = true;
						pathFirmaAsistenteSocial = firma.getPath();
						usrFirmaAsistenteSocial = firma.getAltaUsr();
					}
					
				}
			}
		
}

//obtengo lista de session
List<CieDiez> cieDiez=(ArrayList<CieDiez>) portletSession.getAttribute(WebKeysGlobal.DOCUMENTOS_CIE,PortletSession.APPLICATION_SCOPE);
if (cieDiez == null) {
	cieDiez=TraeListasServiceUtil.getListadoCieDiez();
	portletSession.setAttribute(WebKeysGlobal.DOCUMENTOS_CIE,cieDiez,PortletSession.APPLICATION_SCOPE);	
}

if (cmd != null && cmd.equalsIgnoreCase(Constants.VIEW)){
	inHabilitar= true;
}
if (cmd != null && cmd.equalsIgnoreCase(Constants.EDIT)){
    esEdicion = true;	
}

if(equipoInterdisciplinario != null  ){
	nroIdEquiInter ="Nro Registro : " + "000"+  String.valueOf(equipoInterdisciplinario.getId_registroEquipoInter() );
	fechaReg = Validator.isNotNull(equipoInterdisciplinario)? equipoInterdisciplinario.getFechaRegistro()    : null;
	if (fechaReg != null) {
		fechaEquipo.setTime(equipoInterdisciplinario.getFechaRegistro());
	}
	cantPrestacionesLista=equipoInterdisciplinario.getPrestaciones().size();
	if (equipoInterdisciplinario.getEstadoRegEquipoInter()=="ESTADO" ) { 
		inHabilitar= true;	
	}
}

Integer BtnFirmar = (Integer) request.getAttribute("Btn_Firmar");

Integer tipoDictamenConcurrente =
(Integer) request.getAttribute("tipoDictamenConcurrente");

String valorDictamenConcurrente =
(String) request.getAttribute("valorDictamenConcurrente");

String valorAntecedentes =
equipoInterdisciplinario != null
    ? equipoInterdisciplinario.getDictamen(
        EquipoInterdisciplinario.DICTAMENES.ANTECEDENTES)
    : "";

String valorMedicoAuditor =
equipoInterdisciplinario != null
    ? equipoInterdisciplinario.getDictamen(
        EquipoInterdisciplinario.DICTAMENES.MEDICOAUDITOR)
    : "";

String valorAsistenteSocial =
equipoInterdisciplinario != null
    ? equipoInterdisciplinario.getDictamen(
        EquipoInterdisciplinario.DICTAMENES.ASISTENTESOCIAL)
    : "";

String valorKinesiologia =
equipoInterdisciplinario != null
    ? equipoInterdisciplinario.getDictamen(
        EquipoInterdisciplinario.DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA)
    : "";

String valorLegales =
equipoInterdisciplinario != null
    ? equipoInterdisciplinario.getDictamen(
        EquipoInterdisciplinario.DICTAMENES.LEGALES)
    : "";

String valorEquipoInter =
equipoInterdisciplinario != null
    ? equipoInterdisciplinario.getDictamen(
        EquipoInterdisciplinario.DICTAMENES.EQUIPOINTERDISCIPLINARIO)
    : "";


if (tipoDictamenConcurrente != null &&
valorDictamenConcurrente != null) {

int tipo = tipoDictamenConcurrente.intValue();

if (tipo ==
    EquipoInterdisciplinario.DICTAMENES.ANTECEDENTES.ordinal()) {

    valorAntecedentes = valorDictamenConcurrente;

} else if (tipo ==
    EquipoInterdisciplinario.DICTAMENES.MEDICOAUDITOR.ordinal()) {

    valorMedicoAuditor = valorDictamenConcurrente;

} else if (tipo ==
    EquipoInterdisciplinario.DICTAMENES.ASISTENTESOCIAL.ordinal()) {

    valorAsistenteSocial = valorDictamenConcurrente;

} else if (tipo ==
    EquipoInterdisciplinario.DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA.ordinal()) {

    valorKinesiologia = valorDictamenConcurrente;

} else if (tipo ==
    EquipoInterdisciplinario.DICTAMENES.LEGALES.ordinal()) {

    valorLegales = valorDictamenConcurrente;

} else if (tipo ==
    EquipoInterdisciplinario.DICTAMENES.EQUIPOINTERDISCIPLINARIO.ordinal()) {

    valorEquipoInter = valorDictamenConcurrente;
}
}



%>
<style>

div.divheaderNroReclamo {
  position: absolute;
  top: 270px;
  right:30;
  left:1000px;
  background-color: #cccccc;
  width:200px;
  height:20px;
  border:1px solid black;
  font-size:145%
}



</style>

<form action="EditarPrestadoresEntryAction" name="<portlet:namespace />equipo_fm" id="<portlet:namespace />equipo_fm" >

    <input  type="hidden" id="<portlet:namespace />fprest" name="<portlet:namespace />fprest" value="<%=prestacionFechaString%>" />
 	<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
 	<input	type="hidden" name="<portlet:namespace />cantprestacioneslista"  id="<portlet:namespace />cantprestacioneslista" value="<%=cantPrestacionesLista%>"/>
  	<input	type="hidden" name="<portlet:namespace />cuiltitular"  id="<portlet:namespace />cuiltitular" />
 	<input	type="hidden" name="<portlet:namespace />intetitular"  id="<portlet:namespace />intetitular" />
 	<input  type="hidden" id="<portlet:namespace />posforcie10" name="<portlet:namespace />posforcie10" value="0" />
 	<input  type="hidden" id="<portlet:namespace />posforlocalidad" name="<portlet:namespace />posforlocalidad" value="0" />
 	<input  type="hidden" id="<portlet:namespace />id_registro_eq"	name="<portlet:namespace />id_registro_eq" size="8"  value="<%=Validator.isNotNull(equipoInterdisciplinario)  ? equipoInterdisciplinario.getId_registroEquipoInter()   : "0"  %>" type="text"  />
    <input  type="hidden" id="<portlet:namespace />codigoCie10"   name="<portlet:namespace />codigoCie10" value="<%=Validator.isNotNull(equipoInterdisciplinario) && Validator.isNotNull(equipoInterdisciplinario.getCodigoCie10())   ? equipoInterdisciplinario.getCodigoCie10()    : ""  %>" />
    <input  type="hidden"  id="<portlet:namespace />tipoDomicilio"   name="<portlet:namespace />tipoDomicilio" value="<%=Validator.isNotNull(equipoInterdisciplinario) && Validator.isNotNull(equipoInterdisciplinario.getTipoDomicilio())   ? equipoInterdisciplinario.getTipoDomicilio()    : ""  %>" />
    <input	type="hidden" name="<portlet:namespace />datosdefaultafiliado"  id="<portlet:namespace />datosdefaultafiliado" value='0' />
	
	<input	type="hidden" name="<portlet:namespace />valortipoprestacion"  id="<portlet:namespace />valortipoprestacion" value='' />


	<input	type="hidden" name="<portlet:namespace />firmaTipoDictamen"  id="<portlet:namespace />firmaTipoDictamen"  value='' />
	
	
	<input type="hidden"
    name="<portlet:namespace />origDictamenAntecedentes"
    value="<%=equipoInterdisciplinario != null && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.ANTECEDENTES) != null
        ? HtmlUtil.escape(equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.ANTECEDENTES))
        : ""%>" />

	<input type="hidden"
	    name="<portlet:namespace />origDictamenMedicoAuditor"
	    value="<%=equipoInterdisciplinario != null && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.MEDICOAUDITOR) != null
	        ? HtmlUtil.escape(equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.MEDICOAUDITOR))
	        : ""%>" />
	
	<input type="hidden"
	    name="<portlet:namespace />origDictamenAsistenteSocial"
	    value="<%=equipoInterdisciplinario != null && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.ASISTENTESOCIAL) != null
	        ? HtmlUtil.escape(equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.ASISTENTESOCIAL))
	        : ""%>" />
	
	<input type="hidden"
	    name="<portlet:namespace />origDictamenKinesiologia"
	    value="<%=equipoInterdisciplinario != null && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA) != null
	        ? HtmlUtil.escape(equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA))
	        : ""%>" />
	
	<input type="hidden"
	    name="<portlet:namespace />origDictamenLegales"
	    value="<%=equipoInterdisciplinario != null && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.LEGALES) != null
	        ? HtmlUtil.escape(equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.LEGALES))
	        : ""%>" />
	
	<input type="hidden"
	    name="<portlet:namespace />origDictamenEquipoInter"
	    value="<%=equipoInterdisciplinario != null && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.EQUIPOINTERDISCIPLINARIO) != null
	        ? HtmlUtil.escape(equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.EQUIPOINTERDISCIPLINARIO))
	        : ""%>" />
        	
<fieldset class="block-labels">
	<legend>		
		<liferay-ui:message key="Cabecera Registro de Equipo Interdisciplinario" /> 
	</legend>
	
	<div class="divheaderNroReclamo">		     
		  <label align='center' ><b> <%=nroIdEquiInter%> </b>  </label>   
    </div>
    	    
	<table align="center" class="lfr-table" style="border-collapse: separate; border-spacing: 3px;" >
		
		<tr>		
			<td ><label id="<portlet:namespace/>estadolabel" ><liferay-ui:message key="Estado Registro" /> :</label></td>
			<td ><table>
					<select <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%> name="<portlet:namespace/>estado"
						id="<portlet:namespace />estado" > 
						<!--
						<option value="SELECCIONE"
						>SELECCIONE</option>-->
						<option value="CARGADO"
						<%=Validator.isNotNull(equipoInterdisciplinario) && Validator.isNotNull(equipoInterdisciplinario.getEstadoRegEquipoInter())  && equipoInterdisciplinario.getEstadoRegEquipoInter().equals("CARGADO") ? "selected" : "selected"  %>
						>CARGADO</option>
						<%if (esFirmaAsistenteSocial && esFirmaAuditor && esFirmaTerapiaFisica){%>	
							<option value="CERRADO"
							<%=Validator.isNotNull(equipoInterdisciplinario) && Validator.isNotNull(equipoInterdisciplinario.getEstadoRegEquipoInter())  && equipoInterdisciplinario.getEstadoRegEquipoInter().equals("CERRADO") ? "selected" : ""  %>
							>CERRADO</option>
						<%}%>
					</select>
				</table></td>
			
			<td ><label id="<portlet:namespace />motivolabel" name="<portlet:namespace/>motivolabel" ><liferay-ui:message key="Motivo" /> :</label></td>
			<td ><table>
					<select  <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%> name="<portlet:namespace/>motivo"
						id="<portlet:namespace />motivo" > 
						<!--
						<option value="SELECCIONE"
						>SELECCIONE</option>-->
						<option value="AUTORIZADO"
						<%=Validator.isNotNull(equipoInterdisciplinario) && Validator.isNotNull(equipoInterdisciplinario.getMotivoCierreEquipoInter() )  && equipoInterdisciplinario.getMotivoCierreEquipoInter().equals("AUTORIZADO") ? "selected" : "selected"  %>
						>AUTORIZADO</option>
						<option value="EXCEPCION"
						<%=Validator.isNotNull(equipoInterdisciplinario) && Validator.isNotNull(equipoInterdisciplinario.getMotivoCierreEquipoInter())  && equipoInterdisciplinario.getMotivoCierreEquipoInter().equals("EXCEPCION") ? "selected" : ""  %>
						>EXCEPCION</option>
						<option value="RECHAZADO"
						<%=Validator.isNotNull(equipoInterdisciplinario) && Validator.isNotNull(equipoInterdisciplinario.getMotivoCierreEquipoInter())  && equipoInterdisciplinario.getMotivoCierreEquipoInter().equals("RECHAZADO") ? "selected" : ""  %>
						>RECHAZADO</option>
					</select>
				</table></td>
			
			
			<td ><label><liferay-ui:message key="Fecha" /> :</label></td>											
			<td >
			<%if(equipoInterdisciplinario == null) {%>
			     
					<liferay-ui:input-date dayParam="fechaequipoDia"
					dayValue="<%= fechaDia.get(Calendar.DATE)%>"   
					dayNullable="<%=false %>"
					monthParam="fechaequipoMes"
					monthValue="<%= fechaDia.get(Calendar.MONTH )%>"								
					monthNullable="<%= false %>"
					yearParam="fechaequipoAnio"
					yearValue="<%= fechaDia.get(Calendar.YEAR)%>"
					yearRangeStart="<%= fechaDia.get(Calendar.YEAR)  %>"
					yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  %>"
					yearNullable="<%= false %>"
					firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek() - 1 %>"
					disabled="<%= inHabilitar  %>" />
				 	
			<%}else{ %>
			     
					<liferay-ui:input-date dayParam="fechaequipoDia"
					dayValue="<%= fechaEquipo.get(Calendar.DATE)%>"   
					dayNullable="<%=false %>"
					monthParam="fechaequipoMes"
					monthValue="<%= fechaEquipo.get(Calendar.MONTH )%>"
					monthNullable="<%= false %>"
					yearParam="fechaequipoAnio"
					yearValue= "<%= fechaEquipo.get(Calendar.YEAR)%>"
					yearRangeStart="<%= fechaEquipo.get(Calendar.YEAR)  %>"
					yearRangeEnd="<%= fechaEquipo.get(Calendar.YEAR)  %>"
					yearNullable="<%= false %>"
					firstDayOfWeek="<%= fechaEquipo.getFirstDayOfWeek() - 1 %>"
					disabled="<%= inHabilitar %>" />
			     
			<%} %>
				</td>					   					
		</tr>
		</table>
		
	<table align="center" class="lfr-table" style="border-collapse: separate; border-spacing: 3px;" >
		<tr>
		<td colspan="12">
		<fieldset class="block-labels"><legend><liferay-ui:message
			key="datos-afiliado" /></legend> 
	 	   <liferay-util:include page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>
	 	                       
	 	                       <% if (!esEdicion && (cmd != null && !cmd.equalsIgnoreCase(Constants.VIEW))) { %>
                                 <liferay-util:param value="true" name="edit_mode" />
							   <%}else{ %>
                                 <liferay-util:param value="false" name="edit_mode" />
							   <%} %>						       
						       <liferay-util:param value="<%=String.valueOf(true)%>" name="discapacidad" />
						       
						       <liferay-util:param value="<%= String.valueOf(true) %>" name="pag_reintegro" />
						        
						        <liferay-util:param name="cuil" value="<%=equipoInterdisciplinario!=null?equipoInterdisciplinario.getCuit_titular() :null%>" />
						        <liferay-util:param name="inte" value="<%=equipoInterdisciplinario!=null?String.valueOf(equipoInterdisciplinario.getInte() ):null%>" />
						        <liferay-util:param value="" name="origen" />
								  				
		</liferay-util:include></fieldset>		
		</td>		
	</tr>								
	</table>	
	</fieldset>
	
	<!--  	************************* DOMICLIO AFILIADO  *************************	-->
	<label align='center' id="<portlet:namespace/>mensajeborde"></label>
	
	<fieldset class="block-labels"><legend><liferay-ui:message
	key="home-address-afi-obligatorio" /></legend>
<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="provincia" />:</label></td>
		<td colspan="2"><select  id="<portlet:namespace/>provincia" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   
		name="<portlet:namespace/>provincia" onchange="javascript:filtrarLocalidad();" >
			<%
										for (Provincia provincia : provincias) {
									%>
			<option
				
				
					<%= equipoInterdisciplinario != null && equipoInterdisciplinario.getAfiliado().getDomicilioDefault().getProvinciaId() == provincia.getId() ? "selected" : ""  %>
				<%= equipoInterdisciplinario == null && provincia.getId() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
				value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%>
				
				</option>
			<%
									}
									%>
		</select>
		
		
		</td>
		<td>
		<!--  <label><liferay-ui:message key="Buscador Localidad"/>:</label>-->
		</td>
		<td>
	    <!--  <input id="<portlet:namespace />buscadorlocalidad" name="<portlet:namespace />buscadorlocalidad"  onkeypress="return enterTecla(event,2)"  size="10" maxlength="20" type="text" value='' />-->
		</td>
		<td>
		
		 <div class="selector-localidad">
			         <%if(equipoInterdisciplinario!=null ) {%>
			           <select id="<portlet:namespace/>localidad"
				        name="<portlet:namespace/>localidad"  onchange="javascript:<portlet:namespace />filtrarCodPostal();"
				        style="width: 250px;">
					    <option selected value="0"   <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   >Seleccione una localidad</option>
					    <%	for (Localidad localidad : localidades) {	%>
					    <option <%= equipoInterdisciplinario != null && equipoInterdisciplinario.getAfiliado().getDomicilioDefault() !=null   && equipoInterdisciplinario.getAfiliado().getDomicilioDefault().getLocalidadId() == localidad.getId() ? "selected" : ""  %>
				<%= equipoInterdisciplinario == null && localidad.getId() == WebKeysAfiliados.ID_DEFAULT_LOCALIDAD ? "selected" : ""  %>
				value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
					      <%	}	%>
			           </select>
			         <%} else{%>
			  	       <select id="<portlet:namespace/>localidad"
				         name="<portlet:namespace/>localidad"  onchange="javascript:<portlet:namespace />filtrarCodPostal();"
				         style="width: 250px;">
					     <option selected value="0">Seleccione una localidad</option>
				       </select>	
			         <%} %>		
			       </div>
			       
		
		
		</td>
		<td><label><liferay-ui:message key="cod-postal" />:</label></td>
		<td colspan="2"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />cod_postal"
			name="<portlet:namespace />cod_postal" size="5" maxlength="4"
			type="text"
			value="<%= equipoInterdisciplinario != null && equipoInterdisciplinario.getAfiliado().getDomicilioDefault() !=null  ? equipoInterdisciplinario.getAfiliado().getDomicilioDefault().getPostal_codi()   : "" %>" />
		</td>
	</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"><label><liferay-ui:message key="barrio" />:</label></td>
		<td colspan="2"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />barrio"
			name="<portlet:namespace />barrio" size="12" maxlength="50"
			type="text"
			value="<%= equipoInterdisciplinario != null && equipoInterdisciplinario.getAfiliado().getDomicilioDefault().getBarrio() !=null  ? equipoInterdisciplinario.getAfiliado().getDomicilioDefault().getBarrio() : "" %>" />			 
		</td>
		
		<td><label><liferay-ui:message key="calle" />:</label></td>
		<td colspan="5"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />calle"
			name="<portlet:namespace />calle" size="50" maxlength="100"
			type="text"
			value="<%= equipoInterdisciplinario != null &&  equipoInterdisciplinario.getAfiliado().getDomicilioDefault() !=null  ? equipoInterdisciplinario.getAfiliado().getDomicilioDefault().getCalle()  : "" %>" />			
		</td>

		<td colspan="1"><label><liferay-ui:message key="numero" />:</label></td>
		<td colspan="1"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />numero"
			name="<portlet:namespace />numero" size="5" maxlength="4" type="text"
			value="<%= equipoInterdisciplinario != null &&    equipoInterdisciplinario.getAfiliado().getDomicilioDefault() !=null     ? equipoInterdisciplinario.getAfiliado().getDomicilioDefault().getNumero()  : "" %>" />
			
		</td>
		<td colspan="1"><label><liferay-ui:message key="piso" />:</label></td>
		<td colspan="1"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />piso"
			name="<portlet:namespace />piso" size="5" maxlength="5" type="text"
			value="<%= equipoInterdisciplinario != null &&  equipoInterdisciplinario.getAfiliado().getDomicilioDefault()!=null   ? equipoInterdisciplinario.getAfiliado().getDomicilioDefault().getPiso() : "" %>" />
			
		</td>
		
		<td colspan="1"><label><liferay-ui:message
			key="departamento" />:</label></td>
		<td colspan="1"><input <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>  id="<portlet:namespace />dpto"
			name="<portlet:namespace />dpto" size="5" maxlength="4" type="text"
			value="<%= afiliado != null && equipoInterdisciplinario.getAfiliado().getDomicilioDefault() !=null   ? equipoInterdisciplinario.getAfiliado().getDomicilioDefault().getDepto() : "" %>" />
		</td>
		<td colspan="3"></td>
	</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>
	</table>
	<table class="lfr-table">
	<tr>		
		<td ><label><liferay-ui:message key="tipo-telefono" />:</label></td>
		<td colspan="2">
		
		<select <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%> name="<portlet:namespace/>tipo_telefono"
						id="<portlet:namespace/>tipo_telefono" > 
						<option value="S"
						>SELECCIONE</option>
						<option value="P"
						<%=Validator.isNotNull(equipoInterdisciplinario) && Validator.isNotNull(equipoInterdisciplinario.getTelefonoContacto().getTipo()  )  && equipoInterdisciplinario.getTelefonoContacto().getTipo().equals("P") ? "selected" : ""  %>
						>PERSONAL</option>
						<option value="C"
						<%=Validator.isNotNull(equipoInterdisciplinario) && Validator.isNotNull(equipoInterdisciplinario.getTelefonoContacto().getTipo()  )  && equipoInterdisciplinario.getTelefonoContacto().getTipo().equals("C") ? "selected" : ""  %>
						>CELULAR</option>
					</select>
					
		</td>
		
		<td colspan="2"><label><liferay-ui:message key="codigo-telefono" />:</label></td>
		<td colspan="2"><input id="<portlet:namespace />cod_area_telefono"
			name="<portlet:namespace />cod_area_telefono" size="5" maxlength="5"
			type="text" 
			value=<%=Validator.isNotNull(equipoInterdisciplinario) && Validator.isNotNull(equipoInterdisciplinario.getTelefonoContacto().getCodigoArea())  ? equipoInterdisciplinario.getTelefonoContacto().getCodigoArea() : ""  %>
			<% if (inHabilitar) { %><%="disabled='disabled'" %><%}%> ></td>
			<td colspan="1"><label><liferay-ui:message key="telefono" />:</label></td>
		<td colspan="2"><input id="<portlet:namespace />telefono"
			name="<portlet:namespace />telefono" size="15" maxlength="15"
			type="text"  value=<%=equipoInterdisciplinario!=null && equipoInterdisciplinario.getTelefonoContacto().getNumero()   !=null ? equipoInterdisciplinario.getTelefonoContacto().getNumero() :""%>
			<% if (inHabilitar) { %><%="disabled='disabled'" %><%}%> ></td>
			
		<td colspan="5">
		<label><liferay-ui:message key="e-mail" />:</label>
		<input id="<portlet:namespace />email_afiliado"
			name="<portlet:namespace />email_afiliado" size="25" maxlength="25" onblur="validaMail()"  <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%> type="text" 
			value=<%=Validator.isNotNull(equipoInterdisciplinario) && Validator.isNotNull(equipoInterdisciplinario.getAfiliado().getEmail())  ? equipoInterdisciplinario.getAfiliado().getEmail() :""%>>
		</td>	
		<td>
		  
		</td>
	</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="11">

<input type="button" value="<liferay-ui:message key="get-data-default-afiliado-equipointer" />"
					      onClick="cargaDatosxDefaultDomicilioDiagnosticoEmail();"  id="<portlet:namespace />buttonaddafiliadodata" name="<portlet:namespace />buttonaddafiliadodata"  title="<liferay-ui:message key="get-data-default-afiliado-equipointer" />"/>
		
		</td>
	</tr>


					      
</table>
</fieldset>

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
	
	<!--  	************************* FIN DOMICLIO AFILIADO  *************************	-->
	
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
                               <liferay-util:param name="codigo" value="<%=equipoInterdisciplinario!=null?equipoInterdisciplinario.getCodigoCie10()  :null%>" />							   			  				
		    </liferay-util:include>
		    </fieldset>		
		</td>
		</tr>
		</table>
		<table align='center' class="lfr-table" style="border-collapse: separate; border-spacing: 0px;" >
		<tr>
		   <td >
	      <label><liferay-ui:message key="Diagnostico"/>:</label>
	      </td >
	      <td >
	      <textarea rows="2" cols="150"   <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%> 
					id="<portlet:namespace />diagnostico" maxlength="250"
					name="<portlet:namespace />diagnostico"  ><%=equipoInterdisciplinario!=null && equipoInterdisciplinario.getDiagnosticoAfiliado()  !=null ?equipoInterdisciplinario.getDiagnosticoAfiliado() :""%></textarea>
	      </td>
		 </tr>						
	     </table>
		
		
		
		<table  align="center" class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		 <tr>															
			<td colspan="5">				
				<fieldset class="block-labels">
				<legend>
				<liferay-ui:message key="Observaciones" />
				</legend>
				<textarea  rows="4" cols="80" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>  onkeyup="convertToUppercase(this)"   id="<portlet:namespace />observacion"  
				name="<portlet:namespace />observacion" ><%=equipoInterdisciplinario!=null && equipoInterdisciplinario.getObservaciones()  !=null ?equipoInterdisciplinario.getObservaciones() :""%></textarea>
				</fieldset>
			</td>
			<td colspan="5">				
				<fieldset class="block-labels">
				<legend>
				<liferay-ui:message key="Participantes" />
				</legend>
				<textarea rows="4" cols="70" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>  onkeyup="convertToUppercase(this)"  id="<portlet:namespace />participantes"   
				name="<portlet:namespace />participantes" ><%=equipoInterdisciplinario!=null && equipoInterdisciplinario.getParticipantes()   !=null ?equipoInterdisciplinario.getParticipantes() :""%></textarea>				
				</fieldset>
			</td>
			
		</tr>
</table>
		
		
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="datos-de-la-prestacion" />
	</legend>			
		
<div id="<portlet:namespace />busqueda_prestaciones">
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">	
	<tr>
		    <td><label><liferay-ui:message key="codigo-presentado"/>:</label></td>
				<td><input id="<portlet:namespace />codigoSeguimiento_filtro" name="<portlet:namespace />codigoSeguimiento_filtro" size="10" maxlength="20" type="text" value=''/></td>
				<td><input id="<portlet:namespace />descripcionSeguimiento_filtro" name="<portlet:namespace />descripcionSeguimiento_filtro" size="80" maxlength="200" type="text" value=''					
				/></td>
				<td><div id="<portlet:namespace />divBtnBusca">
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar();" tabindex="-1">Buscar</a>
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();" tabindex="-1">Limpiar</a>
				</div> </td>
			<td><label id="<portlet:namespace />opciones23101label" > <liferay-ui:message key="Tipo:"/>:</label></td>	
			<td>
			
			<select <% if (inHabilitar) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>opciones23101"
				id="<portlet:namespace/>opciones23101">
					<option selected value="0">SELECCIONE</option>
					<% for (OpcionesPrestacion opcionNomenclador   : listaOpciones) { %>
					<option					
						value="<%=opcionNomenclador.getId()+"|"+opcionNomenclador.getDescripcion()%>"><%=opcionNomenclador.getDescripcion()%>
						</option>
					<% } %>
			</select>
			
			</td>
	</tr>
	</table>				
</div>		
		
<div id="<portlet:namespace />datos_prestacion_ingreso">

<table  class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
	
		<tr>		
			<td><label><liferay-ui:message key="Cantidad" />:</label> </td>
			<td><input id="<portlet:namespace />cantidad"  <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%> 
				name="<portlet:namespace />cantidad" size="8" maxlength="9" value ='' type="text" value="" 
				onkeypress="return validaMonto(event,this)" onblur="calculatotal()" onkeydown="allowOnlyDigitsAndDecimals(event);calculatotal()" /> </td>
			<td><label><liferay-ui:message key="Importe" />:</label> </td>
			<td><input id="<portlet:namespace />importe"
				name="<portlet:namespace />importe" size="8" maxlength="9" value ='' <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>  
				type="text" value=""    onblur="calculatotal()"   onkeypress="return validaMonto(event,this)" onkeydown="allowOnlyDigitsAndDecimals(event);calculatotal()" /> </td>
			<td><label><liferay-ui:message key="total" />:</label> </td>
			<td><input id="<portlet:namespace />total"    
				name="<portlet:namespace />total" size="8" maxlength="9" value ='' onkeypress="return inValidaIngreso(event,this)" onkeydown="allowOnlyDigitsAndDecimals(event)"     
				type="text" value=""  /> </td>
			<td>  </td>
			 <td>
    <input type="button" value="<liferay-ui:message key="add-prestacion-reclamo" />"
					      onmouseover="calculatotal()" onClick="<portlet:namespace />agregarPrestacion();"  id="<portlet:namespace />buttonaddprestacion" name="<portlet:namespace />buttonaddprestacion"  title="<liferay-ui:message key="add-prestacion-reclamo" />"/>
    </td>
		</tr>
		</table>
		<table>
		<tr>
					      					
	</tr>
</table>

</div>

<div  id="<portlet:namespace />lista_prestaciones_equipo" align="center"  style="height:120px; overflow: scroll; overflow-x: hidden;">
<table>						
		<tr>															
			<td colspan="10">
 				 <liferay-util:include page="/html/portlet/autorizaciones/equipo_interdisciplinario/lista_prestaciones_equipo.jsp">
					</liferay-util:include> 
			</td>	
		</tr>
	</table>	
</div>

</fieldset>            
			

<input	 type="hidden" name="<portlet:namespace />estadosel"  id="<portlet:namespace />estadosel" value="<%=Validator.isNotNull(equipoInterdisciplinario)  ? equipoInterdisciplinario.getEstadoRegEquipoInter()  : "0"  %>" />
<input	 type="hidden" name="<portlet:namespace />consultaequipointer"  id="<portlet:namespace />consultaequipointer" value="<%=inHabilitar  ? true  : false  %>"/>
<input  id="<portlet:namespace />nom_seleccionado" name="<portlet:namespace />nom_seleccionado" type="hidden" value="1"/>
			<!-- </td>			
			</tr>										
</table>	 -->
<fieldset class="block-labels">
          <legend>
				<liferay-ui:message key="equipo-inter-dictamenes" />
		  </legend>
<table  align="center" class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		 <tr>															
			<td colspan="3">				
				<fieldset class="block-labels">
				<legend>
				<liferay-ui:message key="Psicóloga" />
				</legend>
				<textarea  rows="4" cols="55" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>  onkeyup="convertToUppercase(this)"   id="<portlet:namespace />observacion"  				
				name="<portlet:namespace />dictamenAntecedentes"><%=HtmlUtil.escape(valorAntecedentes)%></textarea>
				</fieldset>
				<div></div>
			</td>
			<td colspan="3">				
				<fieldset class="block-labels">
				<legend>
				<liferay-ui:message key="medico-auditor" />
				</legend>
				<textarea rows="4" cols="55" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>  onkeyup="convertToUppercase(this)"  id="<portlet:namespace />participantes"   				
				 name="<portlet:namespace />dictamenMedicoAuditor"><%=HtmlUtil.escape(valorMedicoAuditor)%></textarea>
				</fieldset>      
			</td>
			<td colspan="3">				
				<fieldset class="block-labels">
				<legend>
				<liferay-ui:message key="Trabajadora Social" />
				</legend>
				<textarea rows="4" cols="55" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>  onkeyup="convertToUppercase(this)"  id="<portlet:namespace />participantes"   
    			name="<portlet:namespace />dictamenAsistenteSocial"><%=HtmlUtil.escape(valorAsistenteSocial)%></textarea>				</fieldset>
			</td>
				
		</tr>
		<tr>
			<td colspan="3">
				<% if (BtnFirmar == 0 && esFirmaPsicologo == false){  %>
					<div   id="<portlet:namespace />id_firma_psicologo"  align="center"> <input type="button"  value="Firma Psicólogo"
						      onClick="<portlet:namespace />agregarFirmaPrestacion(0);"  id="<portlet:namespace />buttonaddfirma" name="<portlet:namespace />buttonaddfirma"  title="Firma psicólogo"/>
					</div>
				<%}%>	
			</td>
				
			<td  colspan="3">
				<% if ((BtnFirmar == 1 || BtnFirmar==4 )&& esFirmaAuditor == false ){  %>
 					<div id="<portlet:namespace />id_firma_auditor" align="center"> 

                    <input type="button"  value="<liferay-ui:message key="firma-dictamen-auditor" />"
						      onClick="<portlet:namespace />agregarFirmaPrestacion(<%=BtnFirmar%>);"  id="<portlet:namespace />buttonaddfirma" name="<portlet:namespace />buttonaddfirma"  title="<liferay-ui:message key="add-firma-auditor" />"/>
<!--  					
					<input type="button"  value="<liferay-ui:message key="firma-dictamen-auditor" />"
						      onClick="<portlet:namespace />agregarFirmaPrestacion(1);"  id="<portlet:namespace />buttonaddfirma" name="<portlet:namespace />buttonaddfirma"  title="<liferay-ui:message key="add-firma-auditor" />"/>
-->						      
					</div>	
				<%}%>
			</td>
			<td  colspan="3">
				<% if ((BtnFirmar == 2 ||  BtnFirmar==5) && esFirmaAsistenteSocial == false){  %>
					<div   id="<portlet:namespace />id_firma_asistente_social"  align="center">
					<input type="button"  value="<liferay-ui:message key="firma-dictamen-asistente-social" />"
						      onClick="<portlet:namespace />agregarFirmaPrestacion(<%=BtnFirmar%>);"  id="<portlet:namespace />buttonaddfirma" name="<portlet:namespace />buttonaddfirma"  title="<liferay-ui:message key="add-firma-asistente-social" />"/>
					 
<!--  					
					<input type="button"  value="<liferay-ui:message key="firma-dictamen-asistente-social" />"
						      onClick="<portlet:namespace />agregarFirmaPrestacion(2);"  id="<portlet:namespace />buttonaddfirma" name="<portlet:namespace />buttonaddfirma"  title="<liferay-ui:message key="add-firma-asistente-social" />"/>
						      
-->					
					
					</div>
				<%}%>	
			</td>	
			
		</tr>
			
		 <tr>															
			<td colspan="3">				
				<fieldset class="block-labels">
				<legend>
				<liferay-ui:message key="licenciado-kinesiologia-terapia-fisica" />
				</legend>
				<textarea  rows="4" cols="55" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>  onkeyup="convertToUppercase(this)"   id="<portlet:namespace />observacion"  
    			name="<portlet:namespace />dictamenKinesiologia"><%=HtmlUtil.escape(valorKinesiologia)%></textarea>				</fieldset>
			</td>
			<td colspan="3">				
				<fieldset class="block-labels">
				<legend>
				<liferay-ui:message key="Legales" />
				</legend>
				<textarea rows="4" cols="55" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>  onkeyup="convertToUppercase(this)"  id="<portlet:namespace />participantes"   
    			name="<portlet:namespace />dictamenLegales"><%=HtmlUtil.escape(valorLegales)%></textarea>				</fieldset>
			</td>
			<td colspan="3">				
				<fieldset class="block-labels">
				<legend>
				<liferay-ui:message key="Equipo Interdisciplinario" />
				</legend>
				<textarea rows="4" cols="55" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>  onkeyup="convertToUppercase(this)"  id="<portlet:namespace />participantes"   
    			name="<portlet:namespace />dictamenEquipoInter"><%=HtmlUtil.escape(valorEquipoInter)%></textarea>				</fieldset>
			</td>			
		</tr>
		
		<tr>
			<td colspan="3">
				<% if (BtnFirmar == 3 && esFirmaTerapiaFisica == false){  %>
					<div   id="<portlet:namespace />id_firma_terapia_fisica" align="center"> <input type="button"  value="<liferay-ui:message key="firma-dictamen-licenciado-kinesiología-terapia-fisica" />"
						      onClick="<portlet:namespace />agregarFirmaPrestacion(3);"  id="<portlet:namespace />buttonaddfirma" name="<portlet:namespace />buttonaddfirma"  title="<liferay-ui:message key="add-firma-terapia-fisica" />"/>
					</div>	
				<%}%>
			</td>
				
			<td  colspan="3">
			</td>
			<td  colspan="3">
			</td>		
		</tr>
		
</table>
</fieldset>
<br/>
<div  align="center" >
<table>
<tr >	
	<% if(   esFirmaAuditor == true)  {%>
		<td align="center"  title="<%=usrFirmaAuditor%>">
			<img  height='100'  width='100'src="<%=path%><%=pathFirmaAuditor%>"/>		
		</td>
	<%}%>
	<% if(   esFirmaAsistenteSocial == true)  {%>
		<td  align="center" title="<%=usrFirmaAsistenteSocial%>"> 
			<img  height='100'  width='100'  src="<%=path%><%=pathFirmaAsistenteSocial%>"/>		
		</td>
	<%}%>
	<% if(   esFirmaTerapiaFisica == true)  {%>
		<td align="center" title="<%=usrFirmaTerapiaFisica%>">
			<img  height='100'  width='100'  src="<%=path%><%=pathFirmaTerapiaFisica%>"/>		
		</td>
	<%}%>
	<% if(   esFirmaPsicologo == true)  {%>
		<td align="center" title="<%=usrFirmaPsicologo%>">
			<img  height='100'  width='100'  src="<%=path%><%=pathFirmaPsicologo%>"/>		
		</td>
	<%}%>
</tr>
</table>
</div>

<br/>
<div  id="<portlet:namespace />botonsaveEquipoInter" align="center"  style="height:80px;  overflow-x: hidden;">
<table>
	<tr>	
<td>
<input type="button" value="<liferay-ui:message key="Grabar" />"
	onClick="<portlet:namespace />saveEquipoInter();"  title="<liferay-ui:message key="Graba los Datos Ingresados." />"/>
</td>
</tr>
</table>
</div>

<% if (equipoInterdisciplinario!= null ) { %>
<div  id="<portlet:namespace />botoneditareclamo" align="center"  style="height:80px;  overflow-x: hidden;">
<table>
<tr>
<td>
<input type="button"  id="<portlet:namespace />Actualizar"   value="<liferay-ui:message key="Grabar" />"
	onClick="<portlet:namespace />editaEquipoInterdisciplinario ();" title="<liferay-ui:message key="Actualiza los Datos Ingresados." />"/>
</td>
<td> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </td>

  
<td>	
 <%if (equipoInterdisciplinario != null && equipoInterdisciplinario.getEstadoRegEquipoInter() != null  
	&& equipoInterdisciplinario.getEstadoRegEquipoInter().equals("CERRADO"))  {%>
<input type="button" id="<portlet:namespace />printButton"  value="<liferay-ui:message key="Imprimir Datos"     />"
	onClick="<portlet:namespace />imprimirRegistro();" title="<liferay-ui:message key="Imprimir Registro del Caso." />" />
</td>	
<%}%>
</tr>
</table>
</div>

	 <%if (equipoInterdisciplinario != null && equipoInterdisciplinario.getEstadoRegEquipoInter() != null  
	&& equipoInterdisciplinario.getEstadoRegEquipoInter().equals("CERRADO"))  {%>
	<div  id="<portlet:namespace />botonprintconsulta" align="center"  style="height:80px;  overflow-x: hidden;">
	<input type="button" id="<portlet:namespace />printButtonConsulta"  value="<liferay-ui:message key="Imprimir Datos"     />"
		onClick="<portlet:namespace />imprimirRegistro();" title="<liferay-ui:message key="Imprimir Registro del Caso." />" />
	</div>
	<%}%>

<%}%>

<div id='validarExistenciaCuit' style="float: right;"></div>

</form>

<script type="text/javascript">

var popupMD;



// asigna el valor por default  
jQuery("#<portlet:namespace />valortipoprestacion").val('');
jQuery('#<portlet:namespace />buscando').hide();
// oculta motivo cierre 
jQuery('#<portlet:namespace />motivolabel').hide();
jQuery('#<portlet:namespace />motivo').hide();
// combo de prestacion 23101
jQuery('#<portlet:namespace />opciones23101').hide();
jQuery('#<portlet:namespace />opciones23101label').hide();

jQuery("#<portlet:namespace/>provincia" ).change(function() {	cargaDatosDomicilioDiagnosticoEmail();validaDiscapacidad(); } );
//jQuery("#<portlet:namespace/>cie_diez" ).change(function() {	cargaDatosDomicilioDiagnosticoEmail();validaDiscapacidad(); } );
jQuery("#<portlet:namespace />buscarAfiliado").bind('mouseenter', function() { cargaDatosDomicilioDiagnosticoEmail(); });
jQuery("#<portlet:namespace />limpiarCampos").bind('click', function() { HabilitarDatosLimpiaDatosDefault();  });
jQuery("#<portlet:namespace />id_seccional").bind('change', function() { validardiscapacidad();  });
jQuery("#<portlet:namespace/>estado").click(function() {	validaEstado(); } );
jQuery("#<portlet:namespace/>opciones23101").click(function() {	jQuery("#<portlet:namespace />valortipoprestacion").val(jQuery('#<portlet:namespace />opciones23101').val()); } );


function filtrarLocalidad() {
	
	var idProvincia = jQuery('#<portlet:namespace/>provincia').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/id_provincia_localidad&idProvincia='+idProvincia;
	

	jQuery("#<portlet:namespace/>localidad").attr('disabled', 'disabled');
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			document.getElementById("<portlet:namespace/>localidad").length = 0;			
			jQuery("#<portlet:namespace/>localidad").removeAttr('disabled');
			var obj = jQuery.parseJSON(data);
			jQuery('.selector-localidad select').html(data).fadeIn();
	
		}
	});
}


jQuery("#<portlet:namespace />botoneditareclamo").hide();
jQuery("#<portlet:namespace />printButton").hide();

<% if(equipoInterdisciplinario != null) {%>
	jQuery("#<portlet:namespace />botonsaveEquipoInter").hide();
	jQuery("#<portlet:namespace />printButton").show();
	if (jQuery("#<portlet:namespace/>estado").val()=='CERRADO'){
		jQuery('#<portlet:namespace />motivolabel').show();
		jQuery('#<portlet:namespace />motivo').show();
		jQuery("#<portlet:namespace/>estado").attr('disabled', 'disabled');
		jQuery("#<portlet:namespace />motivo").attr('disabled', 'disabled');
	}
<%}%>

<% if(esEdicion) {%>
    jQuery("#<portlet:namespace />botoneditareclamo").show();
    jQuery("#<portlet:namespace />botonprintconsulta").hide();
<%}%>

<% if(inHabilitar) {%>
	jQuery("#<portlet:namespace/>localidad").attr('disabled', 'disabled');
	jQuery("#<portlet:namespace/>provincia").attr('disabled', 'disabled');
	jQuery("#<portlet:namespace/>buscadorlocalidad").attr('disabled', 'disabled');
	//jQuery("#<portlet:namespace/>buscadorcie10buscador").attr('disabled', 'disabled');
	jQuery("#<portlet:namespace />buttonaddprestacion").hide();
	jQuery("#<portlet:namespace />botonprintconsulta").show();
<%}%>

jQuery("#<portlet:namespace />buttonaddafiliadodata").hide();

aplicaEstiloBordeRojoDatosObligatorio();

// *******************************************************************************************************************************
// *******************************************************************************************************************************
function <portlet:namespace />saveEquipoInter() {

	
if (validaDatos()) {		
	var baja=jQuery('#<portlet:namespace />baja_fecha').val();		      	     	
   	var t = Date.now();
   	var hoy = new Date(t);
   	var fechaBajaAfiliado   = new Date(baja);		      	     			      	     	
   	diff  = new Date(hoy  - fechaBajaAfiliado);
   	    days  = diff/1000/60/60/24;   	    
   		if(days>0){		      	     			
   			if(!confirm("<liferay-ui:message key='aviso-afiliado-disca-debaja-ahora'/>")){
   				return false;
   			}
   		}
		
	document.getElementById("<portlet:namespace/>estado").disabled = ""; // para el alta 
	//<portlet:namespace />asignacodigocie10(); // asigna el cie10 del diagnostico a la variable
			
	var accionEnCurso = document.<portlet:namespace />equipo_fm.<portlet:namespace /><%= Constants.CMD %>.value;
	document.<portlet:namespace />equipo_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.SAVE  %>';
			
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_registro_equipointerdisciplanrio_entry" /></portlet:actionURL>';
	url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest' + "&esDatosTab=true";
	
	document.<portlet:namespace />equipo_fm.method = 'post';
	submitForm(document.<portlet:namespace />equipo_fm, url);		
	
	
}

}

function enterTecla(e,tipo){
	tecla = (document.all) ? e.keyCode : e.which;//obtenemos el codigo ascii de la tecla	
	if (tecla==13) {
		if(tipo==1)   // CIE 10 
			{
			     crit_busqueda();	
			}
		else { // LOCALIDADES 
			     crit_busqueda_localidad();
       		}
		
	}else{
		jQuery('#<portlet:namespace />posforcie10').val(0);
	} 

}

function crit_busqueda_localidad() {
	  var input=document.getElementById('<portlet:namespace />buscadorlocalidad').value.toUpperCase();
	  var output=document.getElementById('<portlet:namespace/>localidad').options;
	  var dato;       
  pos=jQuery('#<portlet:namespace />posforlocalidad').val();
  for(var i=pos;i<document.getElementById("<portlet:namespace/>localidad").options.length ;i++) {
		  dato = output[i].text;		  
		  if(dato.indexOf(input)>-1){
		        output[i].selected=true;
		        jQuery('#<portlet:namespace />posforlocalidad').val(++i);
		        return false;
		      }		 
  } 
  
  if (output[0].selected){
	  alert('No se encontro localidad con el texto ingresado.')  
  }     else{
	  alert('Se termino de recorrer la  lista de localidades.');
	  
  }  
  jQuery('#<portlet:namespace />posforlocalidad').val(0);
	}

function convertToUppercase(el) {
	  if(!el || !el.value) return;
	  el.value = el.value.toUpperCase();
	}


function <portlet:namespace />buscarNomencladorAutocompletar(){
	var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val();
	var codigo_nomenclador=jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val();
	
	<% if(!inHabilitar) {%>
	
	if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){		
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    }else {
    	if(popupMD==null)
    		popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
    	
    	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';    	
	    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&tiponomenclador='+/*encodeURI(8)*/'' +'&marcareinliq='+/*encodeURI(6)*/ ''  +'&codigonomenclador='+encodeURI(codigo_nomenclador)+'&soloActivos=true';
		jQuery(popupMD).load(url);
			
    }	
	<%}else{%>
    	alert('Modo Consulta');
	<%}%>
}
/*
function <portlet:namespace />asignacodigocie10(){
	var seleccionada=jQuery("#<portlet:namespace/>cie_diez").val();
	jQuery('#<portlet:namespace />codigoCie10').val(seleccionada);	
}
*/

function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
	
	jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val(codigo);
	jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val(descripcion);
	jQuery("#<portlet:namespace />nom_seleccionado").val("1"); // selecciona el tipo de nomenclador	 
	jQuery('#<portlet:namespace />tipoNomenclador').val(tipoNomenclador);	
	cargaValorPrestacionSeleccionada();	
}

function pasarParametrosAParentNm(tipoNomenclador,codigo,descripcion) {	
	seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
	
	if (codigo=='23101' || descripcion=='PRESTACIONES DE APOYO POR HORA'){		
	       jQuery('#<portlet:namespace />opciones23101').show();
		   jQuery('#<portlet:namespace />opciones23101label').show();
		   jQuery("#<portlet:namespace />valortipoprestacion").val('SELECCIONE');
		   jQuery('#<portlet:namespace />opciones23101').val('SELECCIONE');
		  
	}else{
	       jQuery('#<portlet:namespace />opciones23101').hide();
	       jQuery('#<portlet:namespace />opciones23101label').hide();
	       jQuery("#<portlet:namespace />valortipoprestacion").val('');
	       
	}	
    <portlet:namespace />cerrarNm();
    
    	
    
}

function <portlet:namespace />cerrarDivNm(){
	jQuery("#divSeguimientoSur").hide("slow");
}

function <portlet:namespace />cerrarNm(){
	<portlet:namespace />cerrarDivNm();
	if(popupMD){
		Liferay.Popup.close(popupMD);
	}
}

function calculatotal(){
	importe=jQuery("#<portlet:namespace />importe").val();
	cantidad=jQuery("#<portlet:namespace />cantidad").val();
	total= importe * cantidad  ;
	jQuery("#<portlet:namespace />total").val(total.toFixed(2));
	
}

function validaDatos(){
	var respuesta;
	respuesta=true;
	
	
	//DS- Comentado a pedido del sector 01/07/2022
	//if (jQuery("#<portlet:namespace />incapacidad_af").val() != '1') {
	//	alert ("El Afiliado no es discapacitado comuniquese con Afiliaciones.");
	//	respuesta= false ;
	//}
	
//  valida fecha de certificado de discapacidad 
	if ( !validaFechaDiscapacidad()){
		respuesta= false ;
	}  
	
	var tipoTelefono =document.getElementById("<portlet:namespace/>tipo_telefono");
	if (tipoTelefono.selectedIndex==0 ){			
		if ((jQuery("#<portlet:namespace />telefono").val()!="" || jQuery("#<portlet:namespace />cod_area_telefono").val()!="" )  && respuesta){
			alert('Ingrese el tipo de telefono');
			document.getElementById("<portlet:namespace/>tipo_telefono").focus();
			respuesta= false ;
		}
	}else{
		if ((jQuery("#<portlet:namespace />telefono").val()=="" || jQuery("#<portlet:namespace />cod_area_telefono").val()=="" )  && respuesta){
			alert('Ni el codigo y numero de telefono pueden ser vacios.');
			jQuery("#<portlet:namespace />telefono").focus();
			respuesta= false ;
		}
	}
		  
	var diaExist  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaequipoDia").val()));
	var mesExist  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaequipoMes").val()));
	var anioExist   = isNaN(parseInt(jQuery("#<portlet:namespace />fechaequipoAnio").val()));
        
	if   ( respuesta  &&  (diaExist || mesExist || anioExist) )   {
	   alert("Debe ingresar la fecha.");
	   respuesta= false ;
	}
	
	var cuil=jQuery('#<portlet:namespace />cuil').val();
	var inte=jQuery('#<portlet:namespace />inte').val();
	
    if ((cuil=="" || inte=="" ) && respuesta)
    {		
		alert ('Debe seleccionar al Afiliado.');
		jquery("<portlet:namespace />cuil").focus();
		respuesta= false;
	}
    
    //var seleccionada=jQuery("#<portlet:namespace/>cie_diez").val();
	//jQuery('#<portlet:namespace />codigoCie10').val(seleccionada);
	
    if ( jQuery('#<portlet:namespace />codigoCie10').val()==""  ||  jQuery('#<portlet:namespace />codigoCie').val()==""  ||  jQuery('#<portlet:namespace />detalleCie').val()==""   )  {    	
    	alert ('El diagnostico del Afiliado es un dato obligatorio.');
		jquery("<portlet:namespace />codigoCie10").focus();
		respuesta= false;
    }    
    
    if (respuesta  &&  !checkMail()){
		alert('El mail ingresado es invalido');
		jQuery("#<portlet:namespace />email_afiliado").focus();
		respuesta= false ;		
	               } 
	
    
	var valor;
	valor=jQuery('#<portlet:namespace />cantprestacioneslista').val();

	if (valor <1 && respuesta  ){
    			alert('Debe tener ingresada por lo menos una prestacion para grabar el registro.');
    			respuesta=false;   																 	
    	  }
	
	return respuesta;
}

function myXOR(a,b) {
	  return ( a || b ) && !( a && b );
	}

function validaMonto(e, cantidad ){
	 
	tecla = (document.all) ? e.keyCode : e.which;//obtenemos el codigo ascii de la tecla	
	patron= new RegExp("^[0-9]+(\.)?[\d{1,2}]$","gi");
		te = String.fromCharCode(tecla);//convertimos el codigo ascii a string
		if (tecla==0||tecla==8|| tecla==46) return true;
		return validarSiNumero(te);	
		}

function inValidaIngreso(e, cantidad ){
	    tecla = (document.all) ? e.keyCode : e.which;//obtenemos el codigo ascii de la tecla
	    if (tecla==0) return true;
		return false; 
		}


function <portlet:namespace />editaEquipoInterdisciplinario() {
	
	if (validaDatos()) 
		{	
			var data=jQuery('#<portlet:namespace/>estado').val();
			if ( document.getElementById("<portlet:namespace />estadosel").value == data){	// el mismo estado va en cero 	
				// document.getElementById("<portlet:namespace />estado").value="0";		
			}		
			
			<%if(esFirmaAsistenteSocial && esFirmaAuditor && esFirmaTerapiaFisica){%>
				alert('Se encuentran las firmas de los dictámenes, puede cerrar el caso.');
			<%}%>
			
			var accionEnCurso = document.<portlet:namespace />equipo_fm.<portlet:namespace /><%= Constants.CMD %>.value;
			document.<portlet:namespace />equipo_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.UPDATE %>';			 
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_borrar_equipointerdisciplinario_entry" /></portlet:actionURL>';
			url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest' + "&esDatosTab=false";			
			document.<portlet:namespace />equipo_fm.method = 'post';			
			submitForm(document.<portlet:namespace />equipo_fm, url);			
			
		}							  	
}


function <portlet:namespace />filtrarCodPostal() {
	var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/id_localidad_codpostal&idLocalidad='+idLocalidad;
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace />cod_postal").length = 0;						
			var obj = jQuery.parseJSON(data);						
			jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
		}
	});	
}

function <portlet:namespace />limpiarNomencladorAutocompletar(){
	<% if(!inHabilitar) {%>
	jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val('');
	jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val('');
	<%}else{%>
	    alert('Modo Consulta');
	<%}%>
}


function <portlet:namespace />agregarPrestacion() {	
		
	var importe = jQuery('#<portlet:namespace />importe').val();
	var cantidad = jQuery('#<portlet:namespace />cantidad').val();	
	var total1= jQuery('#<portlet:namespace />total').val();
	var tipoNomenclador =jQuery('#<portlet:namespace />nom_seleccionado').val();
	var nombrePrestacion = jQuery('#<portlet:namespace />descripcionSeguimiento_filtro').val();
	var tiponomnecladorprestacion =  jQuery("#<portlet:namespace />tipoNomenclador").val(); 
	var prestacion= jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val();
	var codigo_nomenclador=jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val();	
	var valortipo=jQuery('#<portlet:namespace />valortipoprestacion').val();	
	var idvalortipo = jQuery('#<portlet:namespace />valortipoprestacion').val().split("|");
	
	
	var tipoPrestacionArray = jQuery('#<portlet:namespace />valortipoprestacion').val().split("|");		
	
    var idTipoPrestacion =tipoPrestacionArray [0];
    var detalleTipoPrestacion=tipoPrestacionArray [1];
    if (idTipoPrestacion==0){
    	detalleTipoPrestacion="";
    }
	if ( jQuery("#<portlet:namespace />valortipoprestacion").val()!=''  ){
		if (jQuery('#<portlet:namespace />opciones23101').val()=='0' ){
			alert('Debe seleccionar el tipo');
			jQuery('#<portlet:namespace />opciones23101').focus();
			return false;
		}
	}
	if (jQuery("#<portlet:namespace />nom_seleccionado").val()==1){		 
		if (jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val()<1 ) {
		  alert('Debe seleccionar la prestacion');
		  return false;
		} 				
	}
	
	if (total1<1)
		{
			alert('El total de la prestacion no puede ser cero, revise importe o cantidad.');
			jQuery("#<portlet:namespace />importe").focus();
			return false ;
		}
//	valortipo , idtiponomenclador
// "valortipo":valortipo,	
	var params = {"total":total1,
			   "importe":importe,
			   "cantidad":cantidad,			   
			   "prestacion":prestacion,
			   "tiponomenclador":tipoNomenclador,			   			
			   "idTipoPrestacion":idTipoPrestacion,
			   "detalleTipoPrestacion":detalleTipoPrestacion,
			   "nombre_prestacion":nombrePrestacion};

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/lista_prestaciones_equipointerdisciplinarios" /></portlet:renderURL>';
			
			jQuery('#<portlet:namespace />lista_prestaciones_equipo').load(url,params, function(){
				jQuery('#<portlet:namespace />buscando').hide();  
								  });			
			jQuery('#<portlet:namespace />importe').val('');
			jQuery('#<portlet:namespace />cantidad').val('');
			jQuery('#<portlet:namespace />total').val('');
			jQuery('#<portlet:namespace />opciones23101').hide();
			jQuery('#<portlet:namespace />opciones23101label').hide();			
			<portlet:namespace />limpiarNomencladorAutocompletar();
			jQuery("#<portlet:namespace />valortipoprestacion").val('SELECCIONE');
			
	}   



function <portlet:namespace />agregarFirmaPrestacion(id_dictamen) {	
	
 	if (id_dictamen=="1" || id_dictamen=="4"){
 		jQuery('#<portlet:namespace />firmaTipoDictamen').val(id_dictamen);
 		jQuery('#<portlet:namespace />id_firma_auditor').hide();	
 		<%esFirmaAuditor = true;%>
 	}else if (id_dictamen=="2" || id_dictamen=="5"){
 		jQuery('#<portlet:namespace />firmaTipoDictamen').val(id_dictamen);
 		jQuery('#<portlet:namespace />id_firma_asistente_social').hide();	
 		<%esFirmaAsistenteSocial = true;%>
 	}else if (id_dictamen=="3"){
		jQuery('#<portlet:namespace />firmaTipoDictamen').val(id_dictamen);
 		jQuery('#<portlet:namespace />id_firma_terapia_fisica').hide();
 		<%esFirmaTerapiaFisica = true;%>
	}else if (id_dictamen=="0"){
		jQuery('#<portlet:namespace />firmaTipoDictamen').val(id_dictamen);
 		jQuery('#<portlet:namespace />id_firma_psicologo').hide();
 		<%esFirmaPsicologo = true;%>
	}			
}   
   

function validarSiNumero(numero){	
	
	if (!/^([0-9])*$/.test(numero)  ){  //  Backspace, Delete keys
		return false 
	}else{
		return true 
	}	
}

function <portlet:namespace />imprimirRegistro(){
	<%if (esEdicion || inHabilitar) {%>	     
		window.location.href ="/pdfservlet/?accion=equipointerdisciplinario&idequipo=<%= equipoInterdisciplinario.getId_registroEquipoInter()  %>";
	<%}%>
}


function cargaValorPrestacionSeleccionada()
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
}

function cargaDatosxDefaultDomicilioDiagnosticoEmail()
{
	var cuil=jQuery("#<portlet:namespace />cuil").val();
	var inte=jQuery("#<portlet:namespace />inte").val();
	
	if (cuil=="" || inte ==""){
		return false;
	} 
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_datos_default_afiliado_equipo_interdisciplinario&cuil='+cuil+'&inte='+encodeURI(inte);
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				var afiCalle  = obj.calle ;
				var afiCallenumero  = obj.numero ;
				var afipiso= obj.piso;
				var afidpto = obj.dpto ;
				var afibarrio = obj.barrio ;
				var aficodareatelefono = obj.codareatelefono ;
				var afitelefono = obj.telefono ;
				var afiemail = obj.email ;
				var aficodcie10 = obj.codcie10 ;
				var provincia = obj.provincia;
				var localidad = obj.localidad;
				var localidadtexto = obj.localidadtexto;
				var tipodomicilio= obj.tipodomicilio ;
				var codpostal = obj.codpostal ; 
				var diagnostico = obj.diagnostico;
				var tipotelefono = obj.tipotelefono;
		      	// asignacion de datos 	
		      			jQuery("#<portlet:namespace/>provincia" ).val(provincia);			      	
		      			jQuery("#<portlet:namespace />calle" ).val(afiCalle);
		      	     	jQuery("#<portlet:namespace />numero" ).val(afiCallenumero);
		      	     	jQuery("#<portlet:namespace />piso" ).val(afipiso);
		      	     	jQuery("#<portlet:namespace />dpto" ).val(afidpto);
		      	     	jQuery("#<portlet:namespace />barrio" ).val(afibarrio);
		      	     	jQuery("#<portlet:namespace />cod_area_telefono" ).val( aficodareatelefono );
		      	     	jQuery("#<portlet:namespace />telefono" ).val(afitelefono );			      	     	
		      	     	jQuery("#<portlet:namespace />email_afiliado" ).val(  afiemail);
		      	     	// cargar localidades de la provincia 
		      	     	filtrarLocalidad();		      	     	
		      	     	jQuery("#<portlet:namespace />diagnostico" ).val(diagnostico );
		      	     	jQuery('#<portlet:namespace />posforcie10').val(0);
		      	     	jQuery("#<portlet:namespace />codigoCie" ).val(aficodcie10 );
		      	     	// actualiza el diagnostico
		      	     	if (aficodcie10!=""){
		      	        <portlet:namespace />buscarCieCodigo(); // carga los datos del cie 10
		      			
		      	     	}
		      	     			      	     	
		      	  	    jQuery("#<portlet:namespace/>localidad" ).val(localidad);
		      	  	    jQuery("#<portlet:namespace />cod_postal" ).val(codpostal);
		      	  	    jQuery("#<portlet:namespace />datosdefaultafiliado" ).val(1); // marca para que no vuelva a ingresar
		      	  	    jQuery("#<portlet:namespace />tipoDomicilio" ).val(tipodomicilio); // marca el origen del domicilio

		      	  	    if (tipotelefono=='P'){
		      	  	        jQuery("#<portlet:namespace/>tipo_telefono" ).val("PERSONAL");
		      	  	        
		      	  	    }else{
		      	  	        jQuery("#<portlet:namespace/>tipo_telefono" ).val("CELULAR");
		      	  	    }
		      	  	jQuery("#<portlet:namespace />buttonaddafiliadodata").show();
		      	  	
			}				                                                                                                                                                                                                                                                            
			
		});
	
	
	}
function validardiscapacidad() { 
	alert('discapacidad');
}
function cargaDatosDomicilioDiagnosticoEmail()
{	
	jQuery('#<portlet:namespace />buscando').show();	
	cargado = jQuery("#<portlet:namespace />datosdefaultafiliado").val();
	<%if(equipoInterdisciplinario != null  ){%>
    cargado=1;  // solo se cargan datos por default de afiliados en el ALta de un nuevo registro 
	<%}%>
	if ( cargado==0) {		
		cargaDatosxDefaultDomicilioDiagnosticoEmail();		
				}
	jQuery('#<portlet:namespace />buscando').hide();
}

function validaMail() {
	if (!checkMail()){
		alert('EL mail ingresado es incorrecto');		
	               } 
	}

function checkMail() {
	  var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	  email = jQuery("#<portlet:namespace />email_afiliado").val();
	  if (email==''){
		  return true; 
	  }else{
		  return regex.test(email);  
	  }	  
	}
	
function aplicaEstiloBordeRojoDatosObligatorio() { 
	// borde rojo en datos obligatorios
	color="#ff9999"
	jQuery("#<portlet:namespace />calle").css("borderColor",color);
	jQuery("#<portlet:namespace />cod_area_telefono").css("borderColor",color);
	jQuery("#<portlet:namespace />telefono").css("borderColor",color);
	jQuery("#<portlet:namespace />numero").css("borderColor",color);
	jQuery("#<portlet:namespace/>provincia").css("borderColor",color);
	jQuery("#<portlet:namespace/>localidad").css("borderColor",color);
	jQuery("#<portlet:namespace/>tipo_telefono").css("borderColor",color);
	jQuery("#<portlet:namespace/>importe").css("borderColor",color);
	jQuery("#<portlet:namespace/>cantidad").css("borderColor",color);
	jQuery("#<portlet:namespace/>total").css("borderColor",color);	
	jQuery("#<portlet:namespace/>cie_diez").css("borderColor",color);
	jQuery("#<portlet:namespace/>mensajeborde").css("color","red");
	jQuery("#<portlet:namespace />codigoSeguimiento_filtro").css("borderColor",color);
	jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").css("borderColor",color);
	
	
<% if (equipoInterdisciplinario == null ){ %>
	jQuery("#<portlet:namespace/>mensajeborde").html("Borde rojo indica datos obligatorios si selecciona el afiliado y la provincia &oacute el diagn&oacute;stico el sistema autocompletara los datos.");
	<%}else{%>
	jQuery("#<portlet:namespace/>mensajeborde").html("Borde rojo indica datos obligatorios.");
	<%}%>
	// borde rojo en datos obligatorios
}

function HabilitarDatosLimpiaDatosDefault()
{
	jQuery("#<portlet:namespace />datosdefaultafiliado").val(0);
	jQuery("#<portlet:namespace />calle" ).val("");
   	jQuery("#<portlet:namespace />numero" ).val("");
   	jQuery("#<portlet:namespace />piso" ).val("");
   	jQuery("#<portlet:namespace />dpto" ).val("");
   	jQuery("#<portlet:namespace />barrio" ).val("");
   	

	jQuery("#<portlet:namespace/>provincia" ).val("DESCONOCIDA");			      	
	jQuery("#<portlet:namespace/>localidad" ).val("");
	jQuery("#<portlet:namespace/>cie_diez" ).val("");
   	jQuery("#<portlet:namespace />cod_area_telefono" ).val( "" );
   	jQuery("#<portlet:namespace />telefono" ).val("" );			      	     	
   	jQuery("#<portlet:namespace />email_afiliado" ).val(  "");
   	
   	jQuery("#<portlet:namespace />diagnostico" ).val("");
    jQuery("#<portlet:namespace/>tipo_telefono" ).val("SELECCIONE");
    //jQuery("#<portlet:namespace />buscadorcie10buscador" ).val("");
    
	jQuery("#<portlet:namespace />cod_postal" ).val("");
	
	
	}
	
function validaFechaDiscapacidad()
{
	
	var cuil=jQuery('#<portlet:namespace />cuil').val();
	var inte=jQuery('#<portlet:namespace />inte').val();
	
 	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliado_fecha_vto_documentacion&cuil_titular='+cuil+'&inte='+inte;		
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				var fechaVto = obj.fechaVto;
				if(fechaVto !=null){	
					var hoy = new Date();
					var vVto = fechaVto.split("-");
					var vto = new Date(vVto[2],vVto[1],vVto[0]);					
					if(vto<hoy ){
						alert("El certificado de Discapacidad Esta Vencido, comuniquese con Afiliaciones.")
						return false;
					}
				}
			}
		});
		return true;
		
}
function validaDiscapacidad()
{	
	if( jQuery("#<portlet:namespace />incapacidad_af").val()!=1){
		alert('El afiliado no es discapacitado, comuniquese con Afiliaciones.');		
	  }
}

function validaEstado() {	
	valor=jQuery("#<portlet:namespace/>estado").val();
		if (valor=='CERRADO'){
				jQuery('#<portlet:namespace />motivolabel').show();
				jQuery('#<portlet:namespace />motivo').show();
			
		}else{
			jQuery('#<portlet:namespace />motivolabel').hide();
			jQuery('#<portlet:namespace />motivo').hide();		
		}		
}

function seleccionaCamposCieDiez(codigo,descripcion ){
	jQuery('#<portlet:namespace />codigoCie').val(codigo);
	jQuery('#<portlet:namespace />detalleCie').val(descripcion);
	jQuery('#<portlet:namespace />codigoCie10').val(codigo);
}	

<%if (equipoInterdisciplinario != null) {%>
<portlet:namespace />buscarCieCodigo(); // carga los datos del cie 10
<%}%>

function limpiaCamposBusquedaCieDiez(){
	jQuery('#<portlet:namespace />codigoCie10').val("");
}
</script>

