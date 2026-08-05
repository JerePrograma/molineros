<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include
	file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>
<%@ page
	import="ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil"%>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto" %>
<%
boolean reclamoPersistido =
        reclamoprestacional != null
        && reclamoprestacional.getId_reclamo() > 0;
String reclamoPortletNamespace = renderResponse.getNamespace();
String cmdParametroCompras = ParamUtil.getString(
        request,
        Constants.CMD,
        ""
);
String origenReclamoCompras = ParamUtil.getString(
        request,
        "origen",
        ""
);
String nonceReclamoCompras = ParamUtil.getString(
        request,
        WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE,
        ""
);
Object contextoReclamoComprasObj = request.getSession().getAttribute(
        WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
);
ReclamoPrestacionalCompraContexto contextoReclamoCompras =
        contextoReclamoComprasObj instanceof ReclamoPrestacionalCompraContexto
        ? (ReclamoPrestacionalCompraContexto) contextoReclamoComprasObj
        : null;

boolean handoffReclamoComprasValido =
        Constants.ADD.equalsIgnoreCase(cmdParametroCompras)
        && "compras".equalsIgnoreCase(origenReclamoCompras)
        && contextoReclamoCompras != null
        && contextoReclamoCompras.coincideNonce(nonceReclamoCompras)
        && contextoReclamoCompras.perteneceAUsuario(
                user != null ? user.getScreenName() : ""
        )
        && contextoReclamoCompras.estaVigente(
                System.currentTimeMillis()
        );

if (handoffReclamoComprasValido) {
    request.setAttribute(Constants.CMD, Constants.ADD);
} else if (!reclamoPersistido
        && Constants.ADD.equalsIgnoreCase(
                cmdParametroCompras
        )
        && (contextoReclamoCompras != null
            || !StringUtils.checkEmpty(
                    nonceReclamoCompras
            )
            || "compras".equalsIgnoreCase(
                    origenReclamoCompras
            ))) {

    request.getSession().removeAttribute(
            WebKeysCompras
                    .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
    );

    request.getSession().removeAttribute(
            WebKeysAutorizaciones
                    .RECLAMO_PRESTACION_EN_EDICION
    );

    request.getSession().removeAttribute(
            WebKeysAutorizaciones
                    .LISTADO_PRESTACIONES_RECLAMOS_EN_SESION
    );

    request.getSession().removeAttribute(
            WebKeysAutorizaciones
                    .PRESTACION_EN_PROCESO_DE_EDICION
    );

    request.getSession().removeAttribute(
            WebKeysAutorizaciones
                    .LISTADO_REVISIONES_RECLAMOS_EN_SESION
    );

    request.getSession().removeAttribute(
            WebKeysAutorizaciones
                    .LISTADO_CONTACTOS_RECLAMOS_EN_SESION
    );

    request.setAttribute(
            Constants.CMD,
            Constants.VIEW
    );
}

Calendar prestacionFecha = CalendarFactoryUtil.getCalendar();
String prestacionFechaString = prestacionFecha.get(Calendar.DATE)+"/"+(prestacionFecha.get(Calendar.MONTH) + 1)+"/"+prestacionFecha.get(Calendar.YEAR);

String cmd = (String) request.getAttribute(Constants.CMD);	
String caso_vinculado = String.valueOf(request.getAttribute("caso_vinculado")!=null?request.getAttribute("caso_vinculado"):0);
String cuit_titular_vinculado="";
int inte_vinculado=0;
boolean reclamo_vinculado =false;
boolean esEdicion = false;
int cantprestacioneslista=0;
int cantRevisiones=0;
boolean debitoTercerizadora = false;

ReclamoPrestacional  reclamoprestacional  = (ReclamoPrestacional)request.getSession().getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);

String _nuevoEstadoObservado = "";
Object nuevoEstadoObservadoObj =
        request.getSession().getAttribute(
                WebKeysAutorizaciones
                        .RECLAMO_NUEVO_ESTADO_OBS
        );

if (nuevoEstadoObservadoObj != null
        && reclamoprestacional != null) {

    String nuevoEstadoObservado =
            String.valueOf(
                    nuevoEstadoObservadoObj
            );

    reclamoprestacional.setEstado(
            Integer.parseInt(
                    nuevoEstadoObservado
            )
    );
}

request.getSession().removeAttribute(
        WebKeysAutorizaciones
                .RECLAMO_NUEVO_ESTADO_OBS
);
		
Calendar fechadia  =Calendar.getInstance(); 		
Calendar fechaospim  = Calendar.getInstance();	
Calendar fechacierre  = Calendar.getInstance();
Calendar fechaseccional  = Calendar.getInstance();
Calendar fecharevision = Calendar.getInstance();
String tabValue = ParamUtil.getString(request, "tab", null); // "datos"
boolean nofechaseccional=true;

boolean showReadOnlyReclamPrestac=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_CONSULTA_RECLAMOS_PRESTACIONALES);

fechadia.setTime(new Date());
//obtengo lista de session
List<CieDiez> cieDiez=(ArrayList<CieDiez>) request.getSession().getAttribute(WebKeysGlobal.DOCUMENTOS_CIE);


String divcheckbox="";
String nroreclamo=Constants.ADD.equalsIgnoreCase(cmd)
        ? "Nuevo Reclamo Prestacional"
        : "Caso Nro 00000";
String opAsignadaalReclamo ="";
boolean opAsignadaalReclamoExiste =false;
ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO resolucionAutorizado=ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO.SINVALOR;

divcheckbox="divheadercheckboxs";

if (cmd != null && (cmd.equalsIgnoreCase(Constants.EDIT) || cmd.equalsIgnoreCase(Constants.ADD)) ){
    esEdicion = true;	
}

boolean PuedeObservar = false;
int _estadoPendiente = 1;
int _estadoPrecarga = 0;

if(reclamoprestacional != null  ){
	if ((reclamoprestacional.getEstado() == _estadoPendiente) || 
	    (reclamoprestacional.getEstado() == _estadoPrecarga)) 
	{
		PuedeObservar = true;
	}
}

if(reclamoprestacional != null  ){
	Date fechaaux = null;
	divcheckbox="divcheckboxsEdicion";	
	fechaaux = Validator.isNotNull(reclamoprestacional)? reclamoprestacional.getAlta_fecha() : null;
	if (fechaaux != null) {
		fechaospim.setTime(reclamoprestacional.getAlta_fecha());	
	}
	// obtiene el estado de la autorizacion de las prestaciones 
	resolucionAutorizado= reclamoprestacional.getEstadoResolucionAutorizada();	
	
	fechaaux = Validator.isNotNull(reclamoprestacional)? reclamoprestacional.getFecha_cierre()   : null;
	if (fechaaux != null) {
		fechacierre.setTime(reclamoprestacional.getFecha_cierre());
		
	}
	
	fechaaux = Validator.isNotNull(reclamoprestacional)? reclamoprestacional.getSeccional_fecha()  : null;
	if (fechaaux != null) {
		fechaseccional.setTime(reclamoprestacional.getSeccional_fecha());
		nofechaseccional=false;
	}
	
	//cantprestacioneslista=reclamoprestacional.getPrestaciones() != null ? reclamoprestacional.getPrestaciones().size() :0;
	
	if (cantprestacioneslista == 0){	
		//List<PrestacionesReclamo> prestaciones = (List<PrestacionesReclamo>) session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);				
		if (reclamoprestacional != null &&  reclamoprestacional.getPrestaciones()!= null  &&  !reclamoprestacional.getPrestaciones().isEmpty() ){
			//cantprestacioneslista = prestaciones.size();
			for (int i = 0; i < reclamoprestacional.getPrestaciones().size(); i++) {	
				PrestacionesReclamo presreclamo  = (PrestacionesReclamo) reclamoprestacional.getPrestaciones().get(i);
				if (presreclamo.getEstado() == null || !presreclamo.getEstado().equals(PrestacionesReclamo.ESTADOS.BAJA)){
						cantprestacioneslista = cantprestacioneslista + 1;
						if (presreclamo.getCargo_ps() > 0){
							debitoTercerizadora = true; 

						}
				}
			}
		}
	}

	if (reclamoprestacional != null &&  reclamoprestacional.getRevisiones()!= null  &&  !reclamoprestacional.getRevisiones().isEmpty() ){
		for (int i = 0; i < reclamoprestacional.getRevisiones().size(); i++) {
			RevisionesReclamo revision  = (RevisionesReclamo) reclamoprestacional.getRevisiones().get(i);
			if (revision.getBaja_fecha() == null){
				cantRevisiones = cantRevisiones + 1;

			}
		}	
	}
	
	if (reclamoprestacional.getId_reclamo() > 0) {
		nroreclamo ="Reclamo Nro : " + "000"+  String.valueOf(reclamoprestacional.getId_reclamo());
	}
	if (reclamoprestacional.getId_lista_reintegro()==0 && reclamoprestacional.getIdOP()==0 
	&& reclamoprestacional.getChequeOP()==null  &&  reclamoprestacional.getFechaOP()==null ){
		     opAsignadaalReclamo="Sin Orden de Pago";
	}else{
		//String.valueOf(reclamoprestacional.getId_lista_reintegro())
		     String valorLista;
		  //   String valorCheque ;
		     String valorFechaCheque ;
		     String ctaDescrpcion;
		     String formaPago = "";
		     String ctaNro;    
		     String ctaSucursal;
		     if (reclamoprestacional.getChequeOP()!=null){
		    	 formaPago =  reclamoprestacional.getChequeOP()==null ? " " :" / CH: "  +  reclamoprestacional.getChequeOP() ;
		     }else{
		    	 formaPago =  reclamoprestacional.getCtaNro()==0 ? " " :" / CTA: "  +  reclamoprestacional.getCtaNro()  ;
		     }
		     
		     valorLista= reclamoprestacional.getId_lista_reintegro()==-1 ? " " :String.valueOf(reclamoprestacional.getId_lista_reintegro()) ;
		     valorFechaCheque=  reclamoprestacional.getFechaOP()==null ? " " :" / "  + reclamoprestacional.getfechaOPAsString() ;
		     opAsignadaalReclamo ="OP: " + valorLista    + " / " + reclamoprestacional.getIdOP() +  formaPago  +  valorFechaCheque ;
		     opAsignadaalReclamoExiste=true;
	}
	
	caso_vinculado =  String.valueOf(reclamoprestacional.getCaso_vinculado()); 
	if (Integer.valueOf(caso_vinculado)>0)
	{
		cuit_titular_vinculado = reclamoprestacional.getCuit_titular();
		inte_vinculado = reclamoprestacional.getInte();
		reclamo_vinculado=true;
	}	
	
}else{
	
if (Integer.parseInt(caso_vinculado)>0 ){// carga datos del afiliado del cas 
		ReclamoPrestacional reclamoprestacional1 = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(Integer.parseInt(caso_vinculado));
		cuit_titular_vinculado = reclamoprestacional1.getCuit_titular();
		inte_vinculado = reclamoprestacional1.getInte();
		reclamo_vinculado=true;
	}
}

Integer idPreautorizacion = 0;

if (reclamoprestacional != null && reclamoprestacional.getId_reclamo() > 0) {
    try {
    	idPreautorizacion = PreAutorizacionServiceUtil.buscarPreautorizacionPorIdReclamo(reclamoprestacional.getId_reclamo(),null);
    } catch (Exception e) {
    }
}
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");


boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_REABRIR_RECLAMO_PRESTACIONAL  );				


%>
<style>
div.divheadercheckboxs {
	/*
  position: absolute;
  top: 335px;
  right:30;
  left:1020px;
 */
	background-color: #f2f2f2;
	width: 170px;
	height: 150px;
	border: 1px solid black;
}

div.divcheckboxsEdicion {
	/*
  position: absolute;
  top: 335px;
  right:30;
  left:1050px;
*/
	background-color: #f2f2f2;
	width: 170px;
	height: 150px;
	border: 1px solid black;
}

div.divheaderNroReclamo {
	/*
  position: absolute;
  top: 270px;
  right:30;
  left:1000px;
*/
	background-color: #cccccc;
	width: 250px;
	height: 20px;
	border: 1px solid black;
	font-size: 145%
}

div.divheaderNroOP {
	/*
  position: absolute;
  top: 300px;
  right:30;
  left:1000px;
*/
	width: 250px;
	height: 18px;
	border: 1px solid black;
	font-size: 100%
}

div.divNroRecord_Vinculado {
	/*
  position: absolute;
  top: 238px;
  right:30;
  left:625px;
*/
	/*background-color: #f2f2f2;*/
	width: 200px;
	height: 20px;
	/*  border:1px solid black;*/
	font-size: 120%
}

   .alnright { text-align: right; }

span-fixed-size {
  display: inline-block;
  width: 20px;
}

</style>

<liferay-ui:error key="errorAfiliadoSinCobertMed"
	message="<%=(String)request.getAttribute(\"msgErrorAfiSinCobMed\") %>" />

<liferay-ui:error key="errorPrestacionComprobante"
	message="<%=(String)request.getAttribute(\"msgErrorPrestacionComprobante\") %>" />
	
<form name="<%= reclamoPortletNamespace %>reclamo_fm"
	id="<%= reclamoPortletNamespace %>reclamo_fm">
	
	
<div id="<%= reclamoPortletNamespace %>global"
		align="left"
		style="width:75%;">	
	
	<input
	    type="hidden"
	    id="<%= reclamoPortletNamespace %>plan_reclamo_bloqueado"
	    name="<%= reclamoPortletNamespace %>plan_reclamo_bloqueado"
	    value="0"
	/>
	
	<input
	    type="hidden"
	    id="<%= reclamoPortletNamespace %>nombre_plan_reclamo_bloqueado"
	    name="<%= reclamoPortletNamespace %>nombre_plan_reclamo_bloqueado"
	    value=""
	/>

	<input type="hidden" id="<%= reclamoPortletNamespace %>fprest"
		name="<%= reclamoPortletNamespace %>fprest" value="<%=prestacionFechaString%>" />
	<input type="hidden" id="<%= reclamoPortletNamespace %>posforcie10"
		name="<%= reclamoPortletNamespace %>posforcie10" value="0" /> <input
		type="hidden" id="<%= reclamoPortletNamespace %>codigoCie10"
		name="<%= reclamoPortletNamespace %>codigoCie10"
		value="<%=Validator.isNotNull(reclamoprestacional) && Validator.isNotNull(reclamoprestacional.getCodigoCie10())   ? reclamoprestacional.getCodigoCie10()   : ""  %>" />
	<input type="hidden" name="<%= reclamoPortletNamespace %><%= Constants.CMD %>"
		value="<%=cmd%>" />
	<input type="hidden"
		name="<%= reclamoPortletNamespace %><%= WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE %>"
		value="<%= handoffReclamoComprasValido ? contextoReclamoCompras.getNonce() : "" %>" />
	<input type="hidden"
		name="<%= reclamoPortletNamespace %>cantprestacioneslista"
		id="<%= reclamoPortletNamespace %>cantprestacioneslista"
		value="<%=cantprestacioneslista%>" /> <input type="hidden"
		name="<%= reclamoPortletNamespace %>cuiltitular"
		id="<%= reclamoPortletNamespace %>cuiltitular" /> <input type="hidden"
		name="<%= reclamoPortletNamespace %>intetitular"
		id="<%= reclamoPortletNamespace %>intetitular" /> <input type="hidden"
		name="<%= reclamoPortletNamespace %>cantrevisionesactivas"
		id="<%= reclamoPortletNamespace %>cantrevisionesactivas" value="" /> <input
		type="hidden" id="<%= reclamoPortletNamespace %>id_reclamosel"
		name="<%= reclamoPortletNamespace %>id_reclamosel" size="8"
		value="<%=Validator.isNotNull(reclamoprestacional)  ? reclamoprestacional.getId_reclamo()  : "0"  %>" />
	<input type="hidden" id="<%= reclamoPortletNamespace %>tipoaccionprestacion"
		name="<%= reclamoPortletNamespace %>tipoaccionprestacion" size="8" value='0' />
	<input type="hidden" id="<%= reclamoPortletNamespace %>evaluacionreclamo"
		name="<%= reclamoPortletNamespace %>evaluacionreclamo" size="8"
		value="<%=resolucionAutorizado%>" /> <input type="hidden"
		id="<%= reclamoPortletNamespace %>auditoriaadministrativa"
		name="<%= reclamoPortletNamespace %>auditoriaadministrativa" size="8"
		value="<%=resolucionAutorizado%>" /> <input type="hidden"
		id="<%= reclamoPortletNamespace %>montoPsPrestaciones"
		name="<%= reclamoPortletNamespace %>montoPsPrestaciones" size="8" value='0' />
	<input type="hidden"
		id="<%= reclamoPortletNamespace %>tipoNomencladorSeguimiento_filtro"
		name="<%= reclamoPortletNamespace %>tipoNomencladorSeguimiento_filtro" size="8"
		value='0' />


	<div id="helpComprobantes" class="containerPlus draggable {buttons:'c', skin:'default', width:'700',title:'Ayuda',closed:'true'}" style="top: 500px; left: 200px">
			Ejemplo de factura: CUIT 30999999999 FCP B 00001 - 00000028 F. Emisión 2/07/2020 <br>
			Comprobante: FCP,  <br>
			Letra: B,  <br>
			Suc.: 00001 ( o sólo el número 1 y el sistema autocompletará los 0 a izquierda al guardar)  <br>
			Nro: 00000028 ( o sólo el número 28 y el sistema autocompletará los 0 a izquierda al guardar)  <br>
			CUIT: 30999999999
	</div>

	<fieldset class="cabeceraCaso"> 
		<legend>
			<liferay-ui:message key="Cabecera Caso" />
		</legend>

		<!-- DS -->
		
		
		<table >
		<tr>
				<td width="10%" ><label><liferay-ui:message key="Fecha Ospim" />
									:&nbsp;</label></td>
							<%if(reclamoprestacional == null) {%>
							<td width="33%"  ><liferay-ui:input-date dayParam="fechaospimDia"
									dayValue="<%= fechadia.get(Calendar.DATE)%>"
									dayNullable="<%=true %>" monthParam="fechaospimMes"
									monthValue="<%= fechadia.get(Calendar.MONTH )%>"
									monthNullable="<%= true %>" yearParam="fechaospimAnio"
									yearValue="<%= fechadia.get(Calendar.YEAR)%>"
									yearRangeStart="<%= fechaospim.get(Calendar.YEAR)-5  %>"
									yearRangeEnd="<%= fechaospim.get(Calendar.YEAR)  %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaospim.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion  %>" /></td>
							<%}else{ %>
							<td width="33%" ><liferay-ui:input-date dayParam="fechaospimDia"
									dayValue="<%= fechaospim.get(Calendar.DATE)%>"
									dayNullable="<%=true %>" monthParam="fechaospimMes"
									monthValue="<%= fechaospim.get(Calendar.MONTH )%>"
									monthNullable="<%= true %>" yearParam="fechaospimAnio"
									yearValue="<%= fechaospim.get(Calendar.YEAR)%>"
									yearRangeStart="<%= fechaospim.get(Calendar.YEAR) - 5 %>"
									yearRangeEnd="<%= fechaospim.get(Calendar.YEAR) + 1 %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaospim.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion %>" /></td>
							<%} %>
							<td colspan="1"><label>&nbsp;&nbsp;&nbsp;</label></td>
							
							<td>
							<td   width="10%"><label><liferay-ui:message key="Fecha Seccional" />:&nbsp;</label></td>
							<%if(reclamoprestacional == null || nofechaseccional) {%>
							<td width="33%  style="text-align: left;"><liferay-ui:input-date
									dayParam="fechaseccionalDia" dayValue=""
									dayNullable="<%=true %>" monthParam="fechaseccionalMes"
									monthValue="-1" monthNullable="<%= true %>"
									yearParam="fechaseccionalAnio" yearValue=""
									yearRangeStart="<%= fechaseccional.get(Calendar.YEAR)-5  %>"
									yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR)  %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion %>" /></td>
							<%}else{ %>
							<td c width="33%"><liferay-ui:input-date
									dayParam="fechaseccionalDia"
									dayValue="<%= fechaseccional.get(Calendar.DATE)%>"
									dayNullable="<%=true %>" monthParam="fechaseccionalMes"
									monthValue="<%= fechaseccional.get(Calendar.MONTH)%>"
									monthNullable="<%= true %>" yearParam="fechaseccionalAnio"
									yearValue="<%= fechaseccional.get(Calendar.YEAR )%>"
									yearRangeStart="<%= fechaseccional.get(Calendar.YEAR) - 5 %>"
									yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR) + 1 %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion %>" /></td>
							<%} %> 
						
							
							<td>&nbsp;</td>
										
							<td width="3%"><liferay-ui:message key="Amparo"/>:</td>
							<td>&nbsp;</td>
											
							 <td width="3%"><input type="checkbox"
									id="<%= reclamoPortletNamespace %>chk_amparo"
									name="<%= reclamoPortletNamespace %>chk_amparo"
									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isAmparo()  ? "checked" : "Unchecked" %>
									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br>
							 </td> 
							 <td>&nbsp;</td>
							<td width="2%"><label><liferay-ui:message key="Lote" />: </label></td>
							 <td>&nbsp;</td>
							<td width="15%"><input id="<%= reclamoPortletNamespace %>nroLote"
								name="<%= reclamoPortletNamespace %>nroLote" size="8" maxlength="9"
								type="text"
								value="<%=reclamoprestacional==null || reclamoprestacional.getNroLote()==null
	    						|| reclamoprestacional.getNroLote()==0?"":reclamoprestacional.getNroLote() %>"
								readonly="readonly" /></td>
							</td>
				
							<td width="37%" >&nbsp;&nbsp;&nbsp;</td>
							
						<td>
							<div class="divheaderNroReclamo">
								<label><b><liferay-ui:message key="<%= nroreclamo %>" /></b></label>
							</div>
							<div  class="divheaderNroOP">
								<label><b><%= opAsignadaalReclamo %></b></label> 							
							</div>
						</td>
				
				<tr>
				<tr>
					<td></td>
				</tr>
					
		</table>
		<br>
		<table>
				<tr>
							<td colspan="2"><label><liferay-ui:message key="Tipo Pedido" />:&nbsp;&nbsp;</label></td>
							<td><table>
									<select <% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>tipopedido"
										id="<%= reclamoPortletNamespace %>tipopedido"
										onchange="cambioTipoPedido();manejarTipoPedidoCierre();manejartipogestion();"
										onblur="manejarTipoPedido();">
										<option value="SELECCIONAR">SELECCIONAR</option>
										<option value=EXCEPCION
											<%=Validator.isNotNull(reclamoprestacional) && Validator.isNotNull(reclamoprestacional.getTipoPedido())  && reclamoprestacional.getTipoPedido().equals("EXCEPCION") ? "selected" : ""  %>>EXCEPCIÓN</option>
										<option value="REINTEGRO"
											<%=Validator.isNotNull(reclamoprestacional)  && Validator.isNotNull(reclamoprestacional.getTipoPedido())  &&  reclamoprestacional.getTipoPedido().equals("REINTEGRO") ? "selected" : ""  %>>REINTEGRO</option>
										<option value="EXTRACAPITA"
											<%=Validator.isNotNull(reclamoprestacional) && Validator.isNotNull(reclamoprestacional.getTipoPedido())  && reclamoprestacional.getTipoPedido().equals("EXTRACAPITA") ? "selected" : ""  %>>EXTRACÁPITA</option>
									</select>
								</table></td>
							
							<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
							
							<td colspan="2"><label><liferay-ui:message key="Sector" />:&nbsp;&nbsp;</label></td>
							<td><table> 
									<select <% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>sector"
										id="<%= reclamoPortletNamespace %>sector"
										onchange="manejarTipoSector();">
										
										<option value="" <%= (reclamoprestacional == null || Validator.isNull(reclamoprestacional.getSector())) ? "selected" : "" %>>-- SELECCIONAR --</option>

							            <option value="DISCAPACIDAD"
							                <%= "DISCAPACIDAD".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                DISCAPACIDAD
							            </option>
							
							            <option value="PRESTACIONES MEDICAS"
							                <%= "PRESTACIONES MEDICAS".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                PRESTACIONES MÉDICAS
							            </option>
							
							            <option value="FARMACIA"
							                <%= "FARMACIA".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                FARMACIA
							            </option>
							
							            <option value="LEGALES"
							                <%= "LEGALES".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                LEGALES
							            </option>
							
							            <%-- 
							            <option value="LIQUIDACIONES"
							                <%= "LIQUIDACIONES".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                LIQUIDACIONES
							            </option>
							            --%>
							
							            <option value="ODONTOLOGIA"
							                <%= "ODONTOLOGIA".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                ODONTOLOGIA
							            </option>
            
									</select>
								</table></td>

								<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
								<td><label id="integracion_label"  style="display:none"><liferay-ui:message key="integracion"  />:&nbsp;&nbsp;</label></td>
								<td><select name="<%= reclamoPortletNamespace %>integracion" id="<%= reclamoPortletNamespace %>integracion"
									<% if (!esEdicion) { %> disabled='disabled' <%}%> style15"display:none"  >												
										<option value="0">Seleccione Integración</option>
											<% for (ReclamosPrestacionalesIntegracion integracion : listaIntegracion) { %>
												<option
													<%=reclamoprestacional != null  && reclamoprestacional.getCodigoIntegracion() == integracion.getId() ? "selected" : ""  %>
													value="<%= integracion.getId() %>"><%=integracion.getDescripcion()%>
												</option>
												<% } %>
												
								</select>
								</td>
								<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
								
								<td>								
								<div id="integracion_div"  style="display:none">
									<img id="integracion_desc"   height='16'  width='16'  src='/html/themes/classic/images/common/help.png' title=''   />
									</div>
								</td>
							<td>
							<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td colspan="1"><label><liferay-ui:message key="Estado" />: &nbsp;</label></td>
							<td><table>
									<tr>
										<td><select <% if (!esEdicion) { %> disabled='disabled'
											<%}%> name="<%= reclamoPortletNamespace %>estado"
											onchange="controlarEstadoCerrado();"
											id="<%= reclamoPortletNamespace %>estado">

												<option value="-1">SELECCIONE</option>
												<% for (EstadosReclamosPrestacionales estados : listaestados) { %>
													<%if(cmd!=null && cmd.equalsIgnoreCase(Constants.ADD) &&  estados.getId()==0){ %>
	
													<% } else{%>
													
														<option
															<%= reclamoprestacional != null  && reclamoprestacional.getEstado() == estados.getId() ? "selected" : ""  %>
															value="<%= estados.getId() %>"><%=estados.getDescripcion()%>
														</option>												
													<% } %>
												<% } %>

										</select></td>
										
									</tr>
																	
								</table></td>
						</tr>												
		</table>

		<br>
		<%-- Observacion como Textarea %>
		<%-- 
		<table class="lfr-table"
			style="border-collapse: separate; border-spacing: 0px;">
			<tr>
				<td colspan="8"><liferay-ui:message key="observacion" />:</td>
				<td><textarea rows="2" cols="150"
						disabled='disabled'
						id="<%= reclamoPortletNamespace %>estadoObservacion" maxlength="250"
						name="<%= reclamoPortletNamespace %>estadoObservacion"><%=reclamoprestacional != null && reclamoprestacional.getEstadoObservacion() != null
					? reclamoprestacional.getEstadoObservacion() : ""%>
														</textarea></td>
			</tr>
		</table>
		--%>
		
		<table >
			<%-- Observacion como Fieldset--%>
			<%
			if (reclamoprestacional != null && reclamoprestacional.getEstadoObservacion() != null && reclamoprestacional.getEstadoObservacion().length() >0 ) {
			%>																	
			<tr>
				<td colspan="12">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="observacion" />
						</legend>
						<table>
							<tr>
							<%-- class="span-fixed-size" --%>
								<td><span 
									id="<%= reclamoPortletNamespace %>estadoObservacion"
									style="color: red;"> <%=reclamoprestacional != null && reclamoprestacional.getEstadoObservacion() != null
				? reclamoprestacional.getEstadoObservacion() : ""%>
								</span></td>
							</tr>
						</table>
	
					</fieldset>
				</td>
			</tr>		
			<%
			}
			%>
		
			<tr>
				<td>
					<!-- DS -->
					<table 
						style="border-collapse: separate; border-spacing: 3px;">
						<tr>
							<!--  													
							    <td>
							    <div class="divheaderNroReclamo">		     
							    <label><b><liferay-ui:message key="<%= nroreclamo %>" /></b></label>
							    </div>
							    <div class="divheaderNroOP">		     
							    <label><b><liferay-ui:message key="<%= opAsignadaalReclamo %>" /></b></label>
							    </div>
							    <% if(idPreautorizacion!=null && idPreautorizacion!=0){ %>
							      <br>
							      <div>		     
							       <span style="font-size: 9pt; color: green; "><label><b>Preautorizacion: <%= idPreautorizacion %></b></label></span>
							      </div>
							    <%}%>
							    
							    </td>
							    
							    <td></td>
							-->
							</tr>					
							<tr>			       	
						</tr>
					</table>
				</td>
				
				
				<!-- DS -->
				<td>
<!-- 					<div class="divheaderNroReclamo"> -->
<%-- 						<label><b><liferay-ui:message key="<%= nroreclamo %>" /></b></label> --%>
<!-- 					</div> -->
<!-- 					<div  class="divheaderNroOP"> -->
<%-- 						<label><b><%= opAsignadaalReclamo %></b></label> 							 --%>
<!-- 					</div> -->

						<!-- <div class='<%=divcheckbox%>'>-->
						<!--  table class="lfr-table">-->
						<!--     <tr><td>&nbsp;</td></tr>
							<tr>
								<td>&nbsp;</td>
								<%-- <td><input type="checkbox"
									id="<%= reclamoPortletNamespace %>chk_amparo"
									name="<%= reclamoPortletNamespace %>chk_amparo"
									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isAmparo()  ? "checked" : "Unchecked" %>
									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br>
								</td> --%>
								<!-- <td>&nbsp;</td>
								<td><liferay-ui:message key="Amparo" /><br></td> -->
						<!-- 	</tr>-->
<!-- 							<tr><td>&nbsp;</td></tr> -->
<!-- 							<tr> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><input type="checkbox" -->
<%-- 									id="<%= reclamoPortletNamespace %>chk_superintendencia" --%>
<%-- 									name="<%= reclamoPortletNamespace %>chk_superintendencia" --%>
<%-- 									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isSuperintendencia()   ? "checked" : "Unchecked" %> --%>
<%-- 									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br> --%>
<!-- 								</td> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><liferay-ui:message key="Superintendencia" /><br></td> -->

<!-- 							</tr> -->
<!--							<tr><td>&nbsp;</td></tr>-->
<!-- 							<tr> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><input type="checkbox" -->
<%-- 									id="<%= reclamoPortletNamespace %>chk_recuperable" --%>
<%-- 									name="<%= reclamoPortletNamespace %>chk_recuperable" --%>
<%-- 									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isRecuperable()   ? "checked" : "Unchecked" %> --%>
<%-- 									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br> --%>
<!-- 								</td> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><liferay-ui:message key="Recuperable" /><br></td> -->
<!-- 							</tr> -->
							<!-- tr>
								<td>&nbsp;</td>
								<td><input type="checkbox"
									id="<%= reclamoPortletNamespace %>chk_entramite"
									name="<%= reclamoPortletNamespace %>chk_entramite"
									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isEntramite()   ? "checked" : "Unchecked" %>
									<% if (!esEdicion) { %> disabled='disabled' <%}%> /></td>
								<td>&nbsp;</td>
								<td><liferay-ui:message key="Beneficiario en trámite" /></td>
							</tr-->
					

						<!-- </table> -->
					<!-- </div> -->
					<%if (reclamo_vinculado) {%>
					<div class="divNroRecord_Vinculado">
						<liferay-ui:message key="Asociado al Reclamo Nro :" /><%=caso_vinculado%>
					</div> <%}%> <% if(reclamoprestacional != null &&
				    reclamoprestacional.getId_reclamo() > 0 &&
				    idPreautorizacion != null &&
				    idPreautorizacion > 0){ %> <br>
					<div>
						<span style="font-size: 9pt; color: green;"><label><b>Preautorizaci&oacute;n:
									<%= idPreautorizacion %></b></label></span>
					</div> <%}%>

				</td>

				<!-- </td> -->
			</tr>
		</table>
		<!-- DS -->
	</fieldset> <!-- Cabecera del caso -->

	<table class="cabeceraCaso">
	<tr>
							<td>
								<fieldset class="block-labels">
									<legend>
										<liferay-ui:message key="datos-afiliado" />
									</legend>
									<liferay-util:include
										page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>

										<liferay-util:param name="edit_mode" value="<%=String.valueOf(esEdicion) %>" />
										<liferay-util:param name="discapacidad" value="<%= null %>" />
										<liferay-util:param name="pag_reintegro" value="<%= String.valueOf(true) %>" />
										<liferay-util:param name="from_reclamo" value="true" />

										<% if ( reclamo_vinculado   ) { %>
										<liferay-util:param name="cuil"
											value="<%= String.valueOf(cuit_titular_vinculado) %>" />
										<liferay-util:param name="inte"
											value="<%= String.valueOf(inte_vinculado) %>" />
										<liferay-util:param name="origen" value="" />

										<%}else{ %>
										<liferay-util:param name="cuil"
											value="<%=reclamoprestacional!=null?reclamoprestacional.getCuit_titular():null%>" />
										<liferay-util:param name="inte"
											value="<%=reclamoprestacional!=null?String.valueOf(reclamoprestacional.getInte()):null%>" />
										<liferay-util:param name="origen" value="" />
										<%} %>

									</liferay-util:include>
								</fieldset>
							</td>
							<td>
				  <fieldset class="block-labels seccionVerificarDomicilio" id="<%= reclamoPortletNamespace %>seccionVerificarDomicilio">
				      <table>
				       <tr>
				          <td>&nbsp;</td>
			           </tr>   
				       <tr>		
			             <td><label><liferay-ui:message key="contacto-verif-domi" />:</label></td>
			            </tr>
			            <tr>
				          <td>&nbsp;</td>
			            </tr>
			            <tr> 
			             <td>
				            <div id="<%= reclamoPortletNamespace %>divBotonActualizar">
				               <%if(esEdicion){ %>
					            <input type="button" value="Actualizar" 
					    	     onclick="javascript:mostrarDomicilioAfiliado();"
					    	    />
					    	    <%} %>
				            </div>
				          </td>  
				        </tr>
				        
				        <tr>
				          <td>&nbsp;</td>
			            </tr>
				            
				        <tr>   
				         <td> 
				            <div id="<%= reclamoPortletNamespace %>divResultadoActualizarOK">
					           <p><b><liferay-ui:message key="crm-actualiza-domicilio"/></b></p>
				            </div>
			             </td>
			            </tr> 
			            <tr>
				          <td>&nbsp;</td>
			            </tr>  
			         </table>
			       </fieldset>
				</td>

						</tr>
	</table>
	
	<table class="lfr-table"
		style="border-collapse: separate; border-spacing: 0px;">
		<tr>
			<td>
				<fieldset class="cabeceraCaso">
					<legend>
						<liferay-ui:message key="datos-cie-diez" />
					</legend>
					<liferay-util:include
						page='/html/portlet/autorizaciones/busqueda_ciediez.jsp'>
						<liferay-util:param name="edit_mode" value="<%=String.valueOf(esEdicion) %>" />
						<liferay-util:param name="codigo"
							value="<%=reclamoprestacional!=null?reclamoprestacional.getCodigoCie10():null%>" />
					</liferay-util:include>
				</fieldset>
			</td>
		</tr>
	</table>
	<br>
	<table class="lfr-table">
		<tr>
			<td colspan="8"><liferay-ui:message key="Diagnostico" />:</td>
			<td><textarea rows="2" cols="167" <% if (!esEdicion) { %>
					disabled='disabled' <%}%> id="<%= reclamoPortletNamespace %>diagnostico"
					maxlength="250" name="<%= reclamoPortletNamespace %>diagnostico"><%=reclamoprestacional!=null && reclamoprestacional.getDiagnosticoAfiliado() !=null ?reclamoprestacional.getDiagnosticoAfiliado() :""%></textarea>
			</td>
		</tr>
	</table>


	<fieldset class="lfr-table"> 
	
		<legend>
			<liferay-ui:message key="Datos de la Prestación" />
		</legend>

		<div id="<%= reclamoPortletNamespace %>busqueda_farmacia" align="left" width="80%">
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 3px;">
				<tr>
					<td colspan="15"><label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;F. Prestación: </label></td>
					<td colspan="15"><liferay-ui:input-date
							dayParam="fechaPrestacionDiaFarmacia" dayValue=""
					dayNullable="<%=true%>"
					monthParam="fechaPrestacionMesFarmacia" monthValue="-1"
					monthNullable="<%=true%>"
							yearParam="fechaPrestacionAnioFarmacia" yearValue=""
							yearNullable="<%=true%>"
							yearRangeStart="<%=fechaseccional.get(Calendar.YEAR) - 5%>"
							yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR)<fechadia.get(Calendar.YEAR)?fechadia.get(Calendar.YEAR):fechadia.get(Calendar.YEAR) %>"
							firstDayOfWeek="" disabled="<%= !esEdicion %>" /></td>
					<td colspan="4"><liferay-util:include
							page="/html/portlet/utils/medicamentos/busqueda_medicamentos.jsp">
							<liferay-util:param name="search_url"
								value="/autorizaciones/buscar_medicamentos" />
							<liferay-util:param name="troquel" value='' />
							<liferay-util:param name="nombre_medicamento" value='' />
							<liferay-util:param name="id_medicamento" value='' />
							<liferay-util:param name="esEditable" value='true' />
							<liferay-util:param name="mostrar_con_presentacion" value='true' />
						</liferay-util:include></td>


				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>

			</table>
		</div>

		<div id="<%= reclamoPortletNamespace %>busqueda_prestaciones" align="left" width="80%">
			<table width="75%">
				<tr>
					<td ><label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;F. Prestación: </label></td>
					<td ><liferay-ui:input-date dayParam="fechaPrestacionDia" dayValue="" dayNullable="<%=true%>"
					monthParam="fechaPrestacionMes" monthValue="-1" monthNullable="<%=true%>"
					yearParam="fechaPrestacionAnio" yearValue="" yearNullable="<%=true%>"
							yearRangeStart="<%=fechaseccional.get(Calendar.YEAR) - 5%>"
							yearRangeEnd="<%=fechaseccional.get(Calendar.YEAR)<fechadia.get(Calendar.YEAR)?fechadia.get(Calendar.YEAR):fechadia.get(Calendar.YEAR)%>"
							firstDayOfWeek="" disabled="<%= !esEdicion %>" /></td>


					<td><label><liferay-ui:message key="codigo-presentado" />:</label></td>
					<td><input id="<%= reclamoPortletNamespace %>codigoSeguimiento_filtro"
						name="<%= reclamoPortletNamespace %>codigoSeguimiento_filtro" size="10"
						maxlength="20" type="text" value='' /></td>
					<td><input
						id="<%= reclamoPortletNamespace %>descripcionSeguimiento_filtro"
						name="<%= reclamoPortletNamespace %>descripcionSeguimiento_filtro"
						size="60" maxlength="200" type="text" value='' /></td>
					<td><div id="<%= reclamoPortletNamespace %>divBtnBusca">
							<a href="javascript: void(0);"
								onclick="javascript:<%= reclamoPortletNamespace %>buscarNomencladorAutocompletar();"
								tabindex="-1">Buscar</a> <a href="javascript: void(0);"
								onclick="javascript:<%= reclamoPortletNamespace %>limpiarNomencladorAutocompletar();"
								tabindex="-1">Limpiar</a>
						</div></td>
				</tr>		
				<tr>
					<td>&nbsp;</td>
				</tr>

				
			</table>
		</div>


		<div id="<%= reclamoPortletNamespace %>datos_edicion_prestacion" align="left" width="95%">
          <table width="95%;"><tr><td>
			<span><b>Prestaci&oacute;n en Proceso de Edici&oacute;n.</b></span>
			<%
			if (handoffReclamoComprasValido
					&& request.getAttribute("tipoEdicion") == null
					&& request.getSession().getAttribute(
							WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION
					) != null) {
				request.getSession().removeAttribute(
						WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION
				);
			}
			%>
			<liferay-util:include
				page="/html/portlet/autorizaciones/reclamos_prestacionales/datos_edicion_prestacion.jsp">
			</liferay-util:include>
		  </td></tr></table>	
		</div>

		<div id="<%= reclamoPortletNamespace %>datos_prestacion_ingreso">
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 2px; width: 85%;">
				<tr>
					<td colspan="15">
						<fieldset class="block-labels">
							<legend>
								<liferay-ui:message key="Datos del Comprobante" />
							</legend>
							<table>
								<tr>
									<td colspan="10">
										<table class="lfr-table"
											style="border-collapse: separate; border-spacing: 3px;" >
											<tr>
												<td><label><liferay-ui:message key="Frecuencia" />:</label></td>
												<td><select <% if (!esEdicion) { %> disabled='disabled'
													<%}%> name="<%= reclamoPortletNamespace %>frecuencia"
													id="<%= reclamoPortletNamespace %>frecuencia">
														<option value="SELECCIONE">SELECCIONE</option>
														<option value="UNICA">UNICA</option>
														<option value="SEMANAL">SEMANAL</option>
														<option value="TRIMESTRAL">TRIMESTRAL</option>
														<option value="MENSUAL">MENSUAL</option>
														<option value="SEMESTRAL">SEMESTRAL</option>
														<option value="ANUAL">ANUAL</option>
												</select></td>

												<td><label><liferay-ui:message
															key="comprobante" />:</label></td>
												<td><select name="<%= reclamoPortletNamespace %>comprobante_tipo"
													id="<%= reclamoPortletNamespace %>comprobante_tipo"
													<% if (!esEdicion) { %> disabled="disabled" <%} %>>
														<option value="FCP">FCP</option>
														<option value="RCB">RCB</option>
														<option value="OTR">OTRO</option>
														<!--  <option value="AUT">AUTORIZACION</option> -->
												</select></td>

												<td><label><liferay-ui:message key="letra" />:</label></td>
												<td colspan="3"><select
													name="<%= reclamoPortletNamespace %>comprobante_letra"
													id="<%= reclamoPortletNamespace %>comprobante_letra">
												</select></td>
												<td>Suc:</td>
												<td><input id="<%= reclamoPortletNamespace %>comprobante_suc"
													name="<%= reclamoPortletNamespace %>comprobante_suc" size="5"
													maxlength="5"
													onkeydown="allowOnlyDigits(event);"
													type="text" value="" <% if (!esEdicion) { %>
													readonly="readonly" <%} %> /></td>


												<td>Nro:</td>
												<td><input id="<%= reclamoPortletNamespace %>comprobante_nro"
													name="<%= reclamoPortletNamespace %>comprobante_nro" size="9"
													maxlength="8" type="text" onkeydown="allowOnlyDigits(event);"
													value="" <% if (!esEdicion) { %>
													readonly="readonly" <%} %> /></td>
												<td><label>F. Emisi&oacute;n: </label></td>
												<td colspan="3"><liferay-ui:input-date
														dayParam="fechaComprobanteDia" dayValue=""
														dayNullable="<%=true %>" monthParam="fechaComprobanteMes"
														monthValue="-1" monthNullable="<%= true %>"
														yearParam="fechaComprobanteAnio" yearValue=""
														yearRangeStart="<%= fechaseccional.get(Calendar.YEAR)-5  %>"
														yearRangeEnd="<%=fechaseccional.get(Calendar.YEAR)<fechadia.get(Calendar.YEAR)?fechadia.get(Calendar.YEAR):fechadia.get(Calendar.YEAR)%>"
														yearNullable="<%= true %>"
														firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
														disabled="<%= !esEdicion %>" /></td>

												<td>
										         	<a href="javascript:void(0)" onclick="help(event, 'helpComprobantes')"><img style="height: 25px; width: 25px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
										        </td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td colspan="15"><liferay-util:include
											page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
											<liferay-util:param name="esEditable"
												value='<%= String.valueOf(esEdicion) %>' />
											<liferay-util:param name="cuit" value='' />
											<liferay-util:param name="sucu" value='' />
											<liferay-util:param name="razon" value='' />
											<liferay-util:param name="id_seccional" value='' />
											<liferay-util:param name="esEmpresaPrestador" value='true' />
											<liferay-util:param name="suf_entidad" value='_' />
										</liferay-util:include></td>
								</tr>

								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td><label><liferay-ui:message key="Cantidad" />:</label>
									</td>
									<td><input id="<%= reclamoPortletNamespace %>cantidadFC"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>cantidadFC" size="8" maxlength="9"
										type="text" value="1" onblur="calculatotalFC()" /></td>

									<td><label><liferay-ui:message key="Importe" />:</label></td>
									<td><input id="<%= reclamoPortletNamespace %>importeUnitarioFC"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>importeUnitarioFC" size="8"
										maxlength="20" value='' type="text"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										onblur="calculatotalFC()" /></td>


									<td><label>Total Comprobante:</label></td>
									<td><input id="<%= reclamoPortletNamespace %>importeFC"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>importeFC" size="8" maxlength="20"
										value='' type="text"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										readonly="readonly" /></td>
								</tr>
							</table>
						</fieldset>

					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>

				<tr>
					<td colspan="15">
						<fieldset class="block-labels">
							<legend>
								<liferay-ui:message key="Autorizado por Área Médica:" />
							</legend>
							<table>
								<tr>
									<td><label><liferay-ui:message key="Cantidad" />:</label>
									</td>
									<td><input id="<%= reclamoPortletNamespace %>cantidad"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>cantidad" size="8" maxlength="9"
										type="text" value="1" onblur="calculatotal()" /></td>

									<td><label><liferay-ui:message key="Importe" />:</label>
									</td>
									<td><input id="<%= reclamoPortletNamespace %>importe"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>importe" size="8" maxlength="20"
										value='' type="text"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										onblur="calculatotal()" /></td>

									<td><label><liferay-ui:message key="Total" />:</label></td>
									<td><input id="<%= reclamoPortletNamespace %>total"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>total" size="8" maxlength="20"
										value='' type="text" readonly="readonly" /></td>

									<td><label><liferay-ui:message key="Cargo OSPIM" />:</label>
									</td>
									<td><input id="<%= reclamoPortletNamespace %>cargoospim"
										name="<%= reclamoPortletNamespace %>cargoospim" size="8" maxlength="20"
										value='' <% if (!esEdicion) { %> disabled='disabled' <%}%>
										type="text" value=""
										onkeydown="allowOnlyDigitsAndDecimals(event)" /></td>
									<td><label><liferay-ui:message key="Cargo Prestadora" />:</label>
									</td>
									<td><input id="<%= reclamoPortletNamespace %>cargops"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>cargops" size="8" maxlength="20"
										value='' onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="" /></td>
									<td><label>Cargo Monotributo:</label>
									<td><input id="<%= reclamoPortletNamespace %>cargoimesa"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>cargoimesa" size="8" maxlength="20"
										value='' onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="" /></td>

									<td><label>Reconocido SSS:</label>
									</td>
									<td><input id="<%= reclamoPortletNamespace %>reconocidoSSS"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>reconocidoSSS" size="8" maxlength="20"
										value='' onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="" /></td>

									<td>Recuperable:</label>

									<select name="<%= reclamoPortletNamespace %>recuperable_sur" id="<%= reclamoPortletNamespace %>recuperable_sur"
									 <% if (!esEdicion) { %> disabled="disabled" <%} %> onchange="cambiorecuperable();">
														<option value="0">Seleccione</option>
														<option value="1">SURGE</option>
														<option value="3">Integración</option>
														<option value="2">NO Recuperable</option>
									</select>


									</tr>
							</table>
						</fieldset>
					</td>
				</tr>
          </table>
          <table class="lfr-table">
				<tr>
					<td colspan="8"><liferay-ui:message key="observacion" />:</td>
					<td><textarea rows="3" cols="100" <% if (!esEdicion) { %>
							disabled='disabled' <%}%>
							id="<%= reclamoPortletNamespace %>observacion_prestacion" maxlength="250"
							name="<%= reclamoPortletNamespace %>observacion_prestacion"></textarea> <br>
						<b><liferay-ui:message
								key="La observación de 200 caracteres como máximo." /></b></td>
					<td colspan="12">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td><input type="button"
						value="<liferay-ui:message key="add-prestacion-reclamo" />"
						onClick="<%= reclamoPortletNamespace %>agregarPrestacion();"
						id="<%= reclamoPortletNamespace %>buttonaddprestacion"
						name="<%= reclamoPortletNamespace %>buttonaddprestacion"
						title="<liferay-ui:message key="add-prestacion-reclamo" />" /></td>
					<%if (reclamo_vinculado) {%>
					<td><input type="button"
						value="<liferay-ui:message key="Ver Prestaciones del Caso Asociado." />"
						onClick="<%= reclamoPortletNamespace %>verprestacionesasociadas();"
						id="<%= reclamoPortletNamespace %>botonprestacionesasociadas"
						name="<%= reclamoPortletNamespace %>botonprestacionesasociadas"
						title="<liferay-ui:message key="Ver Prestaciones del Caso Asociado." />" />
					</td>
					<%}%>
				</tr>
			</table>

		</div>

		<table>
			<tr>
				<td><span id="<%= reclamoPortletNamespace %>CantidadDePrestacionesDelReclamo" style="color: red;"></span></td>
			</tr>
		</table>

       <table style="align:left; width:75%;">
		<tr>
		<td colspan="10">
	   <div id="<%= reclamoPortletNamespace %>lista_prestaciones_reclamos"
			style="
        max-width: 1100px;
        max-height: 180px;
        overflow-x: auto;
        overflow-y: auto;
        border: 1px solid #ccc;
        border-radius: 6px;
        background: #fff;">
			<liferay-util:include
							page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_prestaciones_reclamos.jsp">
			</liferay-util:include>

		</div>
		</td>
		</tr>
		</table>

	</fieldset> <!-- Fin Datos de la Prestacion -->


	<div id="<%= reclamoPortletNamespace %>lista_prestaciones_asociadas"
		align="center"
		style="height: 120px; overflow: scroll; overflow-x: hidden;">
		<span style="background-color: #d4d9d5; font-size: 135%"><b><%= caso_vinculado%>.</b>

		</span>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_prestaciones_reclamoasociado.jsp"></liferay-util:include>
	</div>

	<%-- 	<input type="hidden" name="<%= reclamoPortletNamespace %>estadosel"
		id="<%= reclamoPortletNamespace %>estadosel"
		value="<%=Validator.isNotNull(reclamoprestacional)  ? reclamoprestacional.getEstado() : "0"  %>" /> --%>
	<input type="hidden" name="<%= reclamoPortletNamespace %>codigoprestacion"
		id="<%= reclamoPortletNamespace %>codigoprestacion"
		value="<%=Validator.isNotNull(reclamoprestacional)  ? reclamoprestacional.getEstado() : "0"  %>" />
	<input type="hidden" name="<%= reclamoPortletNamespace %>tipogestion"
		id="<%= reclamoPortletNamespace %>tipogestion" value="" /> <input
		type="hidden" name="<%= reclamoPortletNamespace %>idreclamoprestacion"
		id="<%= reclamoPortletNamespace %>idreclamoprestacion" value="" /> <input
		type="hidden" name="<%= reclamoPortletNamespace %>consultareclamo"
		id="<%= reclamoPortletNamespace %>consultareclamo"
		value="<%=esEdicion  ? true  : false  %>" /> <input type="hidden"
		id="<%= reclamoPortletNamespace %>nom_seleccionado"
		name="<%= reclamoPortletNamespace %>nom_seleccionado" value="" /> <input
		type="hidden" name="<%= reclamoPortletNamespace %>caso_vinculado"
		id="<%= reclamoPortletNamespace %>caso_vinculado" value="" />


	<fieldset class="block-labels">


		<table class="cabeceraCaso"
			style="border-collapse: separate; border-spacing: 3px;">
			<tr>
				<td colspan="4">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Contactos-Afiliado-Reclamo" />
						</legend>
						<table>
							<tr>
								<td><b><label id="CantidadDeContactosAsociados">
											Ningún Contacto Asociado. </label></b></td>
								<td><input type="button"
									value="Ver Contactos Asociados al Caso."
									onClick="<%= reclamoPortletNamespace %>vercontactosdelreclamo();"
									id="<%= reclamoPortletNamespace %>botoncontactosreclamo"
									title="<liferay-ui:message key="Ver Contactos Asociados al Caso." />">
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td style="display: none;">

					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Recuperable" />
						</legend>
						<table>
							<tr>
								<td style="display: none;"><input type="button"
									value="Buscar Solicitud SUR"
									title="<liferay-ui:message key="Buscar Solicitud SUR" />">
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
				<td style="display: none;">

					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Orden de Pago" />
						</legend>
						<table>
							<tr>
								<td style="display: none;"><input type="button"
									value="Buscar OP Reclamo"
									title="<liferay-ui:message key="Buscar OP Reclamo" />">
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr>
		</table>

		<div id="<%= reclamoPortletNamespace %>lista_contactos_reclamo" align="center"
			style="height: 160px; overflow: scroll; overflow-x: hidden;">

			<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_contactos_reclamo.jsp">
			</liferay-util:include>
		</div>

		<fieldset class="block-labels">
			<legend>
				<liferay-ui:message key="rev" />
			</legend>
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 3px;">
				<tr>
					<td><label><liferay-ui:message key="fecha-revision" />
							:</label></td>
					<td><liferay-ui:input-date dayParam="fecharevisionDia"
							dayValue="<%= fechadia.get(Calendar.DATE)%>"
							dayNullable="<%=true %>" monthParam="fecharevisionMes"
							monthValue="<%= fechadia.get(Calendar.MONTH )%>"
							monthNullable="<%= true %>" yearParam="fecharevisionAnio"
							yearValue="<%= fechadia.get(Calendar.YEAR)%>"
							yearRangeStart="<%= fecharevision.get(Calendar.YEAR) - 5 %>"
							yearRangeEnd="<%= fecharevision.get(Calendar.YEAR)  %>"
							yearNullable="<%= true %>"
							firstDayOfWeek="<%= fecharevision.getFirstDayOfWeek() - 1 %>"
							disabled="<%= !esEdicion %>" /></td>

					<td><label><liferay-ui:message key="responresolucion" />:</label></td>
					<td><select <% if (!esEdicion) { %> disabled='disabled' <%}%>
						name="<%= reclamoPortletNamespace %>respresolucion"
						id="<%= reclamoPortletNamespace %>respresolucion">
							<option value="SELECCIONE">SELECCIONE</option>
							<option value="AUDITORIA ADMINISTRATIVA">AUDITORIA ADMINISTRATIVA</option>
							<option value="AUDITORIA DE PRESTACIONES MEDICAS">AUDITORIA DE PRESTACIONES MEDICAS</option>
							<option value="AUDITORIA FARMACEUTICA">AUDITORIA FARMACEUTICA</option>
							<option value="AUDITORIA MEDICA">AUDITORIA MEDICA</option>
							<option value="AUTORIZADO O.S.">AUTORIZADO O.S.</option>
							<option value="AUDITORIA ODONTOLOGICA">AUDITORIA ODONTOLOGICA</option>
							<option value="COMISION DIRECTIVA">COMISION DIRECTIVA</option>
							<option value="DIRIGENTES">DIRIGENTES</option>
							<option value="EQUIPO INTERDISCIPLINARIO">EQUIPO INTERDISCIPLINARIO</option>
							<option value="GERENCIADORA">GERENCIADORA</option>
							<option value="LEGALES">LEGALES</option>
					</select></td>
				</tr>
				<tr>
					<td><label><liferay-ui:message key="Presentes" />:</label></td>
					<td><select <% if (!esEdicion) { %> disabled='disabled' <%}%>
						name="<%= reclamoPortletNamespace %>presentes"
						id="<%= reclamoPortletNamespace %>presentes">
							<option value="SELECCIONE">SELECCIONE</option>
							<option value="AUDITORIA MEDICA">AUDITORIA MEDICA</option>
							<option value="COMISION DIRECTIVA">COMISION DIRECTIVA</option>
							<option value="EQUIPO INTERDISCIPLINARIO">EQUIPO INTERDISCIPLINARIO</option>
							<option value="GERENCIADORA">GERENCIADORA</option>
					</select></td>
					<td><label><liferay-ui:message key="resolucion" /> :</label></td>
					<td><select <% if (!esEdicion) { %> disabled='disabled' <%}%>
						name="<%= reclamoPortletNamespace %>resolucion"
						id="<%= reclamoPortletNamespace %>resolucion"
						onchange="cambioresolucion();">
							<option value="Seleccione">SELECCIONE</option>
							<option value="AUTORIZADO">AUTORIZADO</option>
							<option value="RECHAZADO">RECHAZADO</option>
					</select></td>
				</tr>

				<tr>
					<td><liferay-ui:message key="observacion" />:</td>
					<td><textarea rows="3" cols="100"
							id="<%= reclamoPortletNamespace %>observacion_revision" maxlength="200"
							name="<%= reclamoPortletNamespace %>observacion_revision"></textarea> <br>
						<b><liferay-ui:message
								key="La observación de 200 caracteres como máximo." /></b></td>
					<td>
						<div id="<%= reclamoPortletNamespace %>botonrevision">
							<input type="button"
								value="<liferay-ui:message key="agregar-revision"  />"
								onClick="<%= reclamoPortletNamespace %>agregarRevision();"
								title="<liferay-ui:message key="agregar-revision" />" />
						</div>
					</td>
				</tr>
			</table>
			<div id="<%= reclamoPortletNamespace %>lista_revisiones" align="center"
				style="height: 120px; overflow: scroll; overflow-x: hidden;">
				<table>
					<tr>
						<td colspan="10"><liferay-util:include
								page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_revisiones_reclamo.jsp">
							</liferay-util:include></td>
					</tr>
				</table>
				<label align='center'
					id="<%= reclamoPortletNamespace %>mensajerevisionefectuada"></label>
			</div>
		</fieldset>


		<table align="center" class="lfr-table"
			style="border-collapse: separate; border-spacing: 3px;">
			<tr>
				<td colspan="5">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Justificación Médica" />
						</legend>
						<textarea
                            id="<%= reclamoPortletNamespace %>justificacionmedcica_reclamo"
                            name="<%= reclamoPortletNamespace %>justificacionmedcica_reclamo"
                            rows="4"
                            cols="80"
                            onkeyup="convertToUppercase(this)"
                            onchange="validarevision();"
                            <% if (!esEdicion
                                    || (reclamoprestacional != null
                                        && reclamoprestacional
                                                .getTipo_gestion_cierre_reclamo() > 0)) { %>
                                disabled="disabled"
                            <% } %>>
                        <%= reclamoprestacional != null
                                && reclamoprestacional.getJustificaconMedica() != null
                                ? reclamoprestacional.getJustificaconMedica()
                                : "" %></textarea>
					</fieldset>
				</td>
				<td colspan="5">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Dictamen Comisión" />
						</legend>
						<textarea rows="4" cols="70" onkeyup="convertToUppercase(this)"
							<% if (!esEdicion ||( reclamoprestacional!=null && reclamoprestacional.getTipo_gestion_cierre_reclamo()>0 ) )  { %>
							disabled='disabled' <%}%>
							id="<%= reclamoPortletNamespace %>dictamencomision_reclamo"
							name="<%= reclamoPortletNamespace %>dictamencomision_reclamo">
				<%=reclamoprestacional!=null && reclamoprestacional.getDictamenComision()!=null ?reclamoprestacional.getDictamenComision() :""%></textarea>
					</fieldset>
				</td>
			</tr>
		</table>

		<table align="center" class="lfr-table"
			style="border-collapse: separate; border-spacing: 3px; columns: 3;">
			<tr>
				<td>
					<div id="<%= reclamoPortletNamespace %>Cierre_Reclamo_Div">
						<fieldset class="block-labels">

							<legend>
								<liferay-ui:message key="Cierre Reclamo" />
							</legend>
							<table class="lfr-table"
								style="border-collapse: separate; border-spacing: 3px;">
								<tr>
									<%if(reclamoprestacional == null) {%>
									<td colspan="2"><label><liferay-ui:message
												key="Fecha Cierre" />:</label> <liferay-ui:input-date
											dayParam="fechacierreDia"
											dayValue="<%= fechacierre.get(Calendar.DATE)%>"
											dayNullable="<%=true %>" monthParam="fechacierreMes"
											monthValue="<%= fechacierre.get(Calendar.MONTH )%>"
											monthNullable="<%= true %>" yearParam="fechacierreAnio"
											yearValue="<%= fechacierre.get(Calendar.YEAR)%>"
											yearRangeStart="<%= fechacierre.get(Calendar.YEAR) - 5 %>"
											yearRangeEnd="<%= fechacierre.get(Calendar.YEAR)  %>"
											yearNullable="<%= true %>"
											firstDayOfWeek="<%= fechacierre.getFirstDayOfWeek() - 1 %>"
											disabled="<%= !esEdicion %>" /></td>
									<%}else{ %>
									<td colspan="2"><label><liferay-ui:message
												key="Fecha Cierre" />:</label> <liferay-ui:input-date
											dayParam="fechacierreDia"
											dayValue="<%= fechacierre.get(Calendar.DATE)%>"
											dayNullable="<%=true %>" monthParam="fechacierreMes"
											monthValue="<%= fechacierre.get(Calendar.MONTH )%>"
											monthNullable="<%= true %>" yearParam="fechacierreAnio"
											yearValue="<%= fechacierre.get(Calendar.YEAR)%>"
											yearRangeStart="<%= fechacierre.get(Calendar.YEAR) - 5  %>"
											yearRangeEnd="<%= fechacierre.get(Calendar.YEAR)  %>"
											yearNullable="<%= true %>"
											firstDayOfWeek="<%= fechacierre.getFirstDayOfWeek() - 1 %>"
											disabled="<%= !esEdicion %>" /></td>
									<%} %>
									<td colspan="2"><label><liferay-ui:message
												key="Tipo Gestion" />:</label> <select   <% if (!esEdicion) { %>
										disabled='disabled' <%}%>
										name="<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo"
										id="<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo"
										onchange="manejartipogestion();">
											<option selected value="0">SELECCIONE LA GESTION</option>
											<% for (TiposDeGestionReclamosPrestacionales tipogestion  : listatipogestionreclamos) { %>
											<option
												value="<%=tipogestion.getId()%>"><%=tipogestion.getDescripcion()%>
											</option>
											<% } %>
									</select></td>
									<tr id="<%= reclamoPortletNamespace %>observacion_medica_tr" style="display:none;">

											<td><label><liferay-ui:message key="observaciones-area-medica"/>:</label></td>
											<td><select name="<%= reclamoPortletNamespace %>observacion_medica" id="<%= reclamoPortletNamespace %>observacion_medica"
												    <% if (!esEdicion) { %> disabled="disabled" <% } %>>
												    <option value="0">Seleccione observación</option>
												</select></td>
									</tr>


								<tr>
								</tr>
								<tr>
									<td colspan="1"><liferay-ui:message key="observacion" />:
									</td>
									<td colspan="3"><textarea rows="3" cols="50"
											id="<%= reclamoPortletNamespace %>reclamo_observacion_cierre"
											maxlength="200"
											name="<%= reclamoPortletNamespace %>reclamo_observacion_cierre"><%=Validator.isNotNull(reclamoprestacional) &&   Validator.isNotNull(reclamoprestacional.getReclamo_observacion_cierre()) ? reclamoprestacional.getReclamo_observacion_cierre():"" %></textarea>
									</td>
								</tr>




							</table>


							<table align="center" width="600px">

								<tr>
									<td width="600px">&nbsp;</td>
								</tr>

								<tr>
									<td width="170px"><liferay-ui:message
											key="Incluido Convenio con Gerenciadora" />: <input
										type="checkbox"
										id="<%= reclamoPortletNamespace %>incluido_convenio_gerenciadora"
										name="<%= reclamoPortletNamespace %>incluido_convenio_gerenciadora"
										<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isReclamo_convenio_gerenciadora()  ? "checked" : "Unchecked"  %>
										<% if (!esEdicion) { %> disabled='disabled' <%}%>></td>

									<td width="320px"><liferay-ui:message key="2 % UOMA" />:
										<input type="checkbox" id="<%= reclamoPortletNamespace %>dosporciento"
										name="<%= reclamoPortletNamespace %>dosporciento"
										<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isDosPorciento()   ? "checked" : "Unchecked"  %>
										<% if (!esEdicion) { %> disabled='disabled' <%}%>></td>

									<td width="210px"><label>Débito Prestadora:</label> <input
										type="checkbox" id="<%= reclamoPortletNamespace %>debitoprestadora"
										name="<%= reclamoPortletNamespace %>debitoprestadora"
										<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isDebitoPrestadora()    ? "checked" : "Unchecked"  %>
										<% if (!esEdicion) { %> disabled='disabled' <%}%>></td>
								</tr>
							</table>

						</fieldset>
					</div>
				</td>
				<td>

					<fieldset class="block-labels">

						<legend>
							<liferay-ui:message key="Datos de la OP" />
						</legend>

						<table class="lfr-table"
							style="border-collapse: separate; border-spacing: 3px;">
							<% if (opAsignadaalReclamoExiste){ %>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de lista:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getId_lista_reintegro()>0  ? reclamoprestacional.getId_lista_reintegro() : ""  %></td>
							</tr>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de OP:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getIdOP()>0 ? reclamoprestacional.getIdOP() : ""  %></td>
							</tr>
							<%if (reclamoprestacional.getChequeOP()!=null){ %>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de cheque:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getChequeOP()!= null ? reclamoprestacional.getChequeOP(): ""  %></td>
							</tr>
							<%}else{ %>
								<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de Cuenta:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getCtaNro()!= 0 ? reclamoprestacional.getCtaNro(): ""  %></td>
							</tr>
							<%} %>

							<tr style="font-size: 110%">
								<td colspan="1"><b>Fecha OP:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getfechaOPAsString()   %></td>
							</tr>
							<%}else{%>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Sin Orden de Pago.</b></td>
							</tr>
							<%}%>

						</table>
				</td>
				</fieldset>
			</tr>
		</table>

		<br />
		<%if(cmd!=null && cmd.equalsIgnoreCase(Constants.ADD)){ %>
		<div id="<%= reclamoPortletNamespace %>botonsavereclamo" align="center"
			style="height: 80px; overflow-x: hidden;">
			<table>
				<tr>
					<td><input type="button"
						value="<liferay-ui:message key="Grabar" />"
						onClick="<%= reclamoPortletNamespace %>saveReclamo();"
						title="<liferay-ui:message key="Graba los Datos Ingresados." />" />
					</td>
				</tr>
			</table>
		</div>
		<%} %>
		<%if(cmd!=null && cmd.equalsIgnoreCase(Constants.EDIT)) {%>
		<div id="<%= reclamoPortletNamespace %>botoneditareclamo" align="center"
			style="height: 80px; overflow-x: hidden;">
			<table>
				<tr>
					<td><input type="button"
						value="<liferay-ui:message key="Actualizar" />"
						onClick="<%= reclamoPortletNamespace %>editaReclamo(false);"
						title="<liferay-ui:message key="Actualiza los Datos Ingresados." />" />
					</td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<%-- Si es Pendiente y NO ES ReadOnly, no muestra las opcion para volver a Recarga --%>
					<% if ((PuedeObservar) && (!showReadOnlyReclamPrestac)) {%>
						<td><input type="button"
							value="<liferay-ui:message key="Observar" />"
							onClick="<%= reclamoPortletNamespace %>volverEstadoObservado();"
							title="<liferay-ui:message key="Cambia a Estado Observado." />" />
						</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<% }%>
					<td><input type="button"
						value="<liferay-ui:message key="Imprimir Datos" />"
						onClick="<%= reclamoPortletNamespace %>imprimirReclamo();"
						title="<liferay-ui:message key="Imprimir Registro del Caso." />" />
					</td>
				</tr>
			</table>
		</div>
		<%} %>
		<% if( reclamoprestacional!=null
		&& showABMButtons == true
		&& reclamoprestacional.getEstado()== 3  && reclamoprestacional.getIdOP() == 0
		&& !reclamoprestacional.isMarcaReabrirReclamo()) { // cerrado sin OP %>

		<div id="<%= reclamoPortletNamespace %>boton_rollback" align="center"
			style="height: 80px; overflow-x: hidden;">
			<table>
				<tr>
					<td><input type="button"
						value="<liferay-ui:message key="rollback-reclamo" />"
						onClick="<%= reclamoPortletNamespace %>reabrirReclamo(false);"
						title="<liferay-ui:message key="Reabre Registro del Caso." />" />
					</td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				</tr>
			</table>
		</div>
		<%}%>

		<%if(reclamoprestacional != null){ %>
		<div align="center">
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 5px;">
				<tr>
					<td colspan="4"></hr></td>
				</tr>
				<tr>
					<td colspan="4">
						<div align="center" id="<%= reclamoPortletNamespace %>rp_auditoria">
							<table style="font-size: 8">
								<tr>
									<td><label><liferay-ui:message key="Alta Usuario" />:</label></td>
									<td><%=reclamoprestacional.getAlta_usr()!=null? reclamoprestacional.getAlta_usr():""%></td>
									<td><label><liferay-ui:message
												key="crm-contacto-alta-fec" />:</label></td>
									<td><%=sdf2.format(reclamoprestacional.getAlta_fecha()) %></td>
									<td><label><liferay-ui:message key="Modi Usuario" />:</label></td>
									<td><%=reclamoprestacional.getModi_usr()!=null?reclamoprestacional.getModi_usr():"" %></td>
									<td><label><liferay-ui:message
												key="crm-contacto-modi-fec" />:</label></td>
									<td><%=reclamoprestacional.getModi_usr()!=null? sdf2.format(reclamoprestacional.getModi_fecha()) :"" %></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<% } %>
		<input id="<%= reclamoPortletNamespace %>tipoNomenclador"
			name="<%= reclamoPortletNamespace %>tipoNomenclador" type="hidden" value="" />

		<div id='validarExistenciaCuit' style="float: right;"></div>
</div>
</form>

<script type="text/javascript">

var popupMD;
var guardandoReclamo = false;
var popupDomicilio;

jQuery('#<%= reclamoPortletNamespace %>divResultadoActualizarOK').hide();

jQuery('#<%= reclamoPortletNamespace %>cantprestacioneslista').val('<%=cantprestacioneslista%>');
jQuery("#<%= reclamoPortletNamespace %>busqueda_prestaciones").hide();
jQuery("#<%= reclamoPortletNamespace %>busqueda_farmacia").hide();
jQuery("#<%= reclamoPortletNamespace %>datos_edicion_prestacion").hide();
jQuery("#<%= reclamoPortletNamespace %>Cierre_Reclamo_Div").hide();
/* jQuery("#<%= reclamoPortletNamespace %>botoneditareclamo").hide(); */
jQuery("#<%= reclamoPortletNamespace %>lista_prestaciones_asociadas").hide();
jQuery("#<%= reclamoPortletNamespace %>lista_contactos_reclamo").hide();
jQuery("#<%= reclamoPortletNamespace %>justificacion_medica_reclamo").hide();
jQuery("#<%= reclamoPortletNamespace %>caso_vinculado").val(<%=caso_vinculado%>);
jQuery('#<%= reclamoPortletNamespace %>reconocidoSSS').attr('readonly', true);
var nom_seleccionado_edit =
    jQuery(
        "#<%= reclamoPortletNamespace %>nom_seleccionado_edit"
    ).val();

var tipoNomenclador_edit =
    jQuery(
        "#<%= reclamoPortletNamespace %>tipoNomenclador_edit"
    ).val();

var addprestacion=false;
var load =false;
var sectorIni='';
var estadoIni='';


var observacionesRechazado = [];

<%
for (ReclamosPrestacionalesRevisionEstado revisionEstado : listaRevisionEstado) {
%>
    observacionesRechazado.push({
        id: "<%=revisionEstado.getId()%>",
        descripcion: "<%=UnicodeFormatter.toString(revisionEstado.getDescripcion())%>"
    });
<%
}
%>


var observacionesAutorizado = [];

<%
for (
    ReclamosPrestacionalesRevisionEstado revisionEstado :
    listaRevisionEstadoAutorizado
) {
%>
    observacionesAutorizado.push({
        id: "<%=revisionEstado.getId()%>",
        descripcion: "<%=UnicodeFormatter.toString(revisionEstado.getDescripcion())%>"
    });
<%
}
%>


function cargarObservacionesMedicas(lista,observacionSeleccionada) {

	    var combo = jQuery("#<%= reclamoPortletNamespace %>observacion_medica");

	    combo.empty();

	    combo.append(new Option("Seleccione observación","0"));

	    for (var i = 0; i < lista.length; i++) {

	        combo.append(new Option(lista[i].descripcion,String(lista[i].id)));
	    }

	    combo.val("0");

	    if (combo.length > 0) {
	        combo[0].selectedIndex = 0;
	    }

	    // Solo restaura una observación previamente guardada
	    if (observacionSeleccionada != null && String(observacionSeleccionada) != "" && String(observacionSeleccionada) != "0") {

	        var valorGuardado = String(observacionSeleccionada);

	        if (combo.find("option[value='" + valorGuardado + "']").length > 0) {
	            combo.val(valorGuardado);
	        }
	    }
	}

function normalizarNombrePlan(nombrePlan) {

    if (nombrePlan == null) {
        return "";
    }

    return String(nombrePlan)
        .toUpperCase()
        .replace(/^\s+|\s+$/g, "")
        .replace(/\s+/g, " ");
}


function esPlanBloqueadoParaReclamo(nombrePlan, tipoPedido) {

	    if (tipoPedido != "REINTEGRO") {
	        return false;
	    }

	    var planNormalizado =
	        normalizarNombrePlan(nombrePlan);

	    return planNormalizado == "COBERTURA" ||
	           planNormalizado == "COBERTURA TOTAL O" ||
	           planNormalizado == "COBERTURA TOTAL M";
	}


function <%= reclamoPortletNamespace %>validarPlanParaReclamo(nombrePlan,mostrarMensaje) {

	    var tipoPedido = jQuery("#<%= reclamoPortletNamespace %>tipopedido").val();

	    var bloqueado = esPlanBloqueadoParaReclamo(nombrePlan,tipoPedido);

	    jQuery("#<%= reclamoPortletNamespace %>plan_reclamo_bloqueado").val(bloqueado ? "1" : "0");

	    jQuery("#<%= reclamoPortletNamespace %>nombre_plan_reclamo_bloqueado").val(bloqueado ? nombrePlan : "");

	    if (bloqueado) {

	        if (mostrarMensaje) {
	            alert('Afiliado con plan "' + nombrePlan +'" no puede cargar un reclamo de tipo REINTEGRO.');
	        }

	        return false;
	    }

	    return true;
	}

var ultimaValidacionPlanDetectada = null;

function verificarPlanAfiliadoDelReclamo() {

    var campoPlan = jQuery("#<%= reclamoPortletNamespace %>plan");

    if (campoPlan.length == 0) {
        return;
    }

    var nombrePlan = campoPlan.val();

    if (nombrePlan == null) {
        nombrePlan = "";
    }

    nombrePlan = String(nombrePlan);

    var tipoPedido = jQuery("#<%= reclamoPortletNamespace %>tipopedido").val();

    if (tipoPedido == null) {
        tipoPedido = "";
    }

    var claveValidacion =
        nombrePlan + "|" + tipoPedido;

    if (claveValidacion == ultimaValidacionPlanDetectada) {
        return;
    }

    ultimaValidacionPlanDetectada = claveValidacion;

    <%= reclamoPortletNamespace %>validarPlanParaReclamo(nombrePlan, true);
}

jQuery(document).ready(function() {
	load = true;
	sectorIni = jQuery("#<%= reclamoPortletNamespace %>sector").val();
	estadoIni = jQuery("#<%= reclamoPortletNamespace %>estado").val();

	//jQuery('#<%= reclamoPortletNamespace %>observacion_medica_div').hide();
	if ('EXCEPCION' ==  jQuery("#<%= reclamoPortletNamespace %>tipopedido").val()){
		traerDescripcion();
	}


	var observacionMedicaInicial = null;

	<%
	if (
	    reclamoprestacional != null &&
	    reclamoprestacional.getEstado() == 3
	) {
	%>

	    jQuery(
	        "#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo"
	    ).val(
	        "<%=reclamoprestacional.getTipo_gestion_cierre_reclamo()%>"
	    );

	    <%
	    if (reclamoprestacional.getIdObservacionMedica() > 0) {
	    %>

	        observacionMedicaInicial =
	            "<%=reclamoprestacional.getIdObservacionMedica()%>";

	    <%
	    }
	    %>

	<%
	}
	%>

	tipoGestionCierreReclamo(observacionMedicaInicial);

	filtrarLetraComprobante();
	integracionReclamo();

	//Revisa el afiliado que ya vino cargado, por ejemplo desde la aplicación.
	verificarPlanAfiliadoDelReclamo();

	window.setInterval(verificarPlanAfiliadoDelReclamo,500);

});



jQuery("#<%= reclamoPortletNamespace %>sector").change(function(){

	try {
   		var valor=jQuery('#<%= reclamoPortletNamespace %>cantprestacioneslista').val();


		if (valor >= 1 && load == true){

	        var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";

			var confirmar = false;
			confirmar=confirm ('Se eliminaran los ítems por no pertenecer al tipo correspondiente '+'\nDesea hacerlo?');
			if(confirmar){
				 var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/borrar_reclamosprestaciones_todos';
    			 url = url + params;
    			jQuery("#<%= reclamoPortletNamespace %>lista_prestaciones_reclamos").load(url);
			}else{
				jQuery("#<%= reclamoPortletNamespace %>sector option[value="+sectorIni+"]").attr("selected",true);
			}

		}

	}
	catch (err) {
		alert('error manejarTipoSector ');
	}

});

jQuery("#<%= reclamoPortletNamespace %>integracion").change(function(){

	try {
		traerDescripcion();
	}
	catch (err) {
		alert('error integracion ');
	}
});

jQuery("#<%= reclamoPortletNamespace %>estado").change(function(){

	try {
   		var estado =jQuery('#<%= reclamoPortletNamespace %>estado').val();

   		var chk_amparo =jQuery("#<%= reclamoPortletNamespace %>chk_amparo").is(':checked');

   		if (estado == 4 && chk_amparo == false ){
   			alert('Debe seleccionar la marca de Amparo ')	;

			jQuery("#<%= reclamoPortletNamespace %>estado option[value=1]").attr("selected",true);

   		}
	}
	catch (err) {
		alert('error estado ');
	}

});

jQuery("#<%= reclamoPortletNamespace %>tipopedido").change(function() {

	    try {

	        filtrarLetraComprobante();
	        integracionReclamo();

	        tipoGestionCierreReclamo();

	        verificarPlanAfiliadoDelReclamo();

	    } catch (err) {
	        alert("Error al cambiar el tipo de pedido");
	    }
	});


jQuery("#<%= reclamoPortletNamespace %>chk_amparo").change(function(){

	try {
   		var estado =jQuery('#<%= reclamoPortletNamespace %>estado').val();

   		var chk_amparo =jQuery("#<%= reclamoPortletNamespace %>chk_amparo").is(':checked');

   		if (estado == 4 && chk_amparo == false){
   			alert ('No puede sacar la marca de aparo si el estado es Incompleto ');
   			jQuery("#<%= reclamoPortletNamespace %>chk_amparo").attr('checked', true);
   		}

	}catch (err) {
		alert('error chk_amparo ');
	}

});

jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").change(function(){
	tipoGestionCierreReclamo();

});

jQuery("#<%= reclamoPortletNamespace %>observacion_medica").change(function(){
	try {
   		jQuery("#<%= reclamoPortletNamespace %>reclamo_observacion_cierre").text('');
	}
	catch (err) {
		alert('error observacion_medica text');
	}
});

function tipoGestionCierreReclamo(observacionSeleccionada) {

    try {

        var tipoPedido = jQuery("#<%= reclamoPortletNamespace %>tipopedido").val();
        var idGestion = String(jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").val() || "0");
        var filaObservacion = jQuery("#<%= reclamoPortletNamespace %>observacion_medica_tr");
        var comboObservacion = jQuery("#<%= reclamoPortletNamespace %>observacion_medica");
        var esRechazado = idGestion == "5";
        var esReintegro =  tipoPedido == "REINTEGRO" && idGestion == "4";
        var esExcepcionFacturacionDirecta =tipoPedido == "EXCEPCION" &&idGestion == "3";

        if (esRechazado) {

            cargarObservacionesMedicas(observacionesRechazado, observacionSeleccionada);

            filaObservacion.show();

            comboObservacion.attr("required","required");

        } else if (esReintegro || esExcepcionFacturacionDirecta) {

            cargarObservacionesMedicas(observacionesAutorizado,observacionSeleccionada);
            filaObservacion.show();
            comboObservacion.attr("required","required");

        } else {

            cargarObservacionesMedicas([], "0");
            filaObservacion.hide();
            comboObservacion.removeAttr("required");
        }

    } catch (err) {

        alert(
            "Error al manejar las observaciones del área médica: " +
            err.message
        );
    }
}

function integracionReclamo(){
	try {
		 if ('EXCEPCION' ==  jQuery("#<%= reclamoPortletNamespace %>tipopedido").val()){
			 jQuery('#integracion_label').show();
			 jQuery('#<%= reclamoPortletNamespace %>integracion').show();
			 jQuery('#integracion_desc').show();
			 jQuery('#integracion_div').show();
		 }else {
			 jQuery('#integracion_label').hide();
			 jQuery('#<%= reclamoPortletNamespace %>integracion').hide();
			 jQuery('#integracion_desc').show();
			 jQuery('#integracion_div').hide();
		 }
	}
	catch (err) {
		alert('error integracion ');
	}
}


/* var data=jQuery('#<%= reclamoPortletNamespace %>estado').val();
document.getElementById("<%= reclamoPortletNamespace %>estadosel").value = data; */

jQuery("#<%= reclamoPortletNamespace %>idreclamoprestacion").val("0");
<% if(reclamoprestacional != null) {%>
jQuery("#<%= reclamoPortletNamespace %>idreclamoprestacion").val(<%=reclamoprestacional.getId_reclamo() %>);
/* jQuery("#<%= reclamoPortletNamespace %>botoneditareclamo").show(); */
      <% if(reclamoprestacional.getEstado()==3 ) {%>
            jQuery("#<%= reclamoPortletNamespace %>Cierre_Reclamo_Div").show();
            jQuery("#<%= reclamoPortletNamespace %>botonrevision").hide();



      <%}%>
manejarTipoPedidoCierre();
manejarTipoSector();

<%if( resolucionAutorizado!=ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO.SINVALOR && resolucionAutorizado!=ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO.SINEVALUACION) {%>
	// oculta boton de agregar porque existe una evaluacion de rECHAZO o APROBACION no de baja
	jQuery("#<%= reclamoPortletNamespace %>botonrevision").hide();
	jQuery("#<%= reclamoPortletNamespace %>mensajerevisionefectuada").html("Revision Efectuada, el Sistema soporta solo una revision activa (No de baja).");
<%}%>

<%}%>


<% if(!esEdicion) {%>
    /* jQuery("#<%= reclamoPortletNamespace %>botoneditareclamo").hide();   */
    /* document.getElementById("<%= reclamoPortletNamespace %>sector").disabled = "disabled"; */

    document.getElementById("<%= reclamoPortletNamespace %>reclamo_observacion_cierre").disabled = "disabled";

    jQuery("#<%= reclamoPortletNamespace %>botonrevision").hide();
    jQuery("#<%= reclamoPortletNamespace %>buttonaddprestacion").hide();

    //document.getElementById("<%= reclamoPortletNamespace %>buscadorcie10buscador").disabled = "disabled";



<%}%>

<% if (Constants.ADD.equalsIgnoreCase(cmd) && cantRevisiones == 0) { %>
    jQuery("#<%= reclamoPortletNamespace %>botonrevision").show();
<% } %>



function filtrarLetraComprobante() {
	var tipoPedido = jQuery("#<%= reclamoPortletNamespace %>tipopedido").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/filtrarLetraComprobante&tipo_pedido='+tipoPedido;
	jQuery("#<%= reclamoPortletNamespace %>comprobante_letra").attr('disabled', 'disabled');

	jQuery.ajax({
		url: url,
		async:false,
		success: function(data){
			document.getElementById("<%= reclamoPortletNamespace %>comprobante_letra").length = 0;
			jQuery("#<%= reclamoPortletNamespace %>comprobante_letra").removeAttr('disabled');
			var obj = jQuery.parseJSON(data);
			jQuery('#<%= reclamoPortletNamespace %>comprobante_letra').html(data).fadeIn();

		}
	});
}



<%-- <% if(esEdicion) {%>
AcomodarControlesEdicion();
<%}%> --%>


aplicaEstiloBordeRojoDatosObligatorio();

<%-- function  AcomodarControlesEdicion() {
	// HEADER DATOS INHABILITADOS

	                         document.getElementById("<%= reclamoPortletNamespace %>sector").disabled = "disabled";
	                         <%if (Validator.isNotNull(reclamoprestacional) &&   Validator.isNotNull(reclamoprestacional.getTipoPedido()) ) {   %>
	                         if ( document.getElementById("<%= reclamoPortletNamespace %>tipopedido").selectedIndex!=0) {
	                        	 document.getElementById("<%= reclamoPortletNamespace %>tipopedido").disabled = "disabled";
	                         }
	                         <%}%>
						 	 document.getElementById("<%= reclamoPortletNamespace %>fechaospimDia").disabled = true;
							 document.getElementById("<%= reclamoPortletNamespace %>fechaospimMes").disabled = true;
							 document.getElementById("<%= reclamoPortletNamespace %>fechaospimAnio").disabled = true;
	// DATOS DE REVISION
	                         jQuery("#<%= reclamoPortletNamespace %>botoneditareclamo").show();
	                         document.getElementById("<%= reclamoPortletNamespace %>estado").disabled = "";
                        	 document.getElementById("<%= reclamoPortletNamespace %>fecharevisionDia").disabled = "";
							 document.getElementById("<%= reclamoPortletNamespace %>fecharevisionMes").disabled = "";
							 document.getElementById("<%= reclamoPortletNamespace %>fecharevisionAnio").disabled = "";
							 document.getElementById("<%= reclamoPortletNamespace %>observacion_revision").disabled = "";
							 document.getElementById("<%= reclamoPortletNamespace %>chk_amparo").disabled = "";
							 document.getElementById("<%= reclamoPortletNamespace %>chk_superintendencia").disabled = "";
							 document.getElementById("<%= reclamoPortletNamespace %>chk_recuperable").disabled = "";
							 document.getElementById("<%= reclamoPortletNamespace %>chk_entramite").disabled = "";
							 document.getElementById("<%= reclamoPortletNamespace %>resolucion").disabled = "";
							 document.getElementById("<%= reclamoPortletNamespace %>respresolucion").disabled = "";
							 document.getElementById("<%= reclamoPortletNamespace %>presentes").disabled = "";
		// DATOS DE CIERRE NO ES NECESARIO
		 					/*  document.getElementById("<%= reclamoPortletNamespace %>fechacierreDia").disabled = false;
							 document.getElementById("<%= reclamoPortletNamespace %>fechacierreMes").disabled = false;
							 document.getElementById("<%= reclamoPortletNamespace %>fechacierreAnio").disabled = false;


							 document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").disabled = "";

							 document.getElementById("<%= reclamoPortletNamespace %>reclamo_observacion_cierre").disabled = false;
							 document.getElementById("<%= reclamoPortletNamespace %>reclamo_ps_factura_ospim").disabled = "";
							 document.getElementById("<%= reclamoPortletNamespace %>reclamo_a_negociar").disabled = ""; */

	} --%>



function <%= reclamoPortletNamespace %>buscarNomencladorAutocompletar(){
	var nombre_nomenclador=jQuery("#<%= reclamoPortletNamespace %>descripcionSeguimiento_filtro").val();
	var codigo_nomenclador=jQuery("#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro").val();
    var tipoNomenclador=jQuery("#<%= reclamoPortletNamespace %>tipoNomencladorSeguimiento_filtro").val();

    // Marca ReinLiq no se utiliza en esta busqueda
    var marcaReinliq=null;
	if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />');
    }else {
    	if(popupMD==null)
    		popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});


    	if(tipoNomenclador==8){
    		marcaReinliq=6;
    	}

    	var esPrestMed = 0;
    	sector = jQuery("#<%= reclamoPortletNamespace %>sector").val();
    	if (sector == "PRESTACIONES MEDICAS")
    		esPrestMed = 1;

	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
	    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&tiponomenclador='+tipoNomenclador +'&codigonomenclador='+encodeURI(codigo_nomenclador)+'&soloActivos=true';
	    url += '&marcareinliq='+marcaReinliq+'&esPrestMed='+esPrestMed;

	    jQuery(popupMD).load(url);
    }
}


function <%= reclamoPortletNamespace %>buscarNomencladorAutocompletar_edit(){
	var nombre_nomenclador=jQuery("#<%= reclamoPortletNamespace %>descripcionSeguimiento_filtro_edit").val();
	var codigo_nomenclador=jQuery("#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro_edit").val();
    var tipoNomenclador=jQuery("#<%= reclamoPortletNamespace %>tipoNomencladorSeguimiento_filtro_edit").val();
    tipoNomenclador = '0';
	if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />');
    }else {
    	if(popupMD==null)
    		popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});

	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
	    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&tiponomenclador='+tipoNomenclador +'&codigonomenclador='+encodeURI(codigo_nomenclador)+'&soloActivos=true';
	    jQuery(popupMD).load(url);
    }
}


function <%= reclamoPortletNamespace %>limpiarNomencladorAutocompletar(){
	jQuery("#<%= reclamoPortletNamespace %>descripcionSeguimiento_filtro").val('');
	jQuery("#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro").val('');
	jQuery("#<%= reclamoPortletNamespace %>descripcionSeguimiento_filtro_edit").val('');
	jQuery("#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro_edit").val('');
}

<%-- function <%= reclamoPortletNamespace %>siguienteSolapa() {

		var accionEnCurso = document.<%= reclamoPortletNamespace %>prestador_fm.<%= reclamoPortletNamespace %><%= Constants.CMD %>.value;
		document.<%= reclamoPortletNamespace %>prestador_fm.<%= reclamoPortletNamespace %><%= Constants.CMD %>.value='<%=Constants.MOVE %>';

		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" /></portlet:actionURL>';
		url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest' + "&esDatosTab=true";

		document.<%= reclamoPortletNamespace %>prestador_fm.method = 'post';
		submitForm(document.<%= reclamoPortletNamespace %>prestador_fm, url);

} --%>

function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
	jQuery('#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro').val(codigo);
	jQuery("#<%= reclamoPortletNamespace %>descripcionSeguimiento_filtro").val(descripcion);
	jQuery("#<%= reclamoPortletNamespace %>nom_seleccionado").val("1"); // selecciona el tipo de nomenclador
	jQuery('#<%= reclamoPortletNamespace %>tipoNomenclador').val(tipoNomenclador);


	jQuery('#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro_edit').val(codigo);
	jQuery("#<%= reclamoPortletNamespace %>descripcionSeguimiento_filtro_edit").val(descripcion);
	jQuery("#<%= reclamoPortletNamespace %>nom_seleccionado_edit").val("1"); // selecciona el tipo de nomenclador
	jQuery('#<%= reclamoPortletNamespace %>tipoNomenclador_edit').val(tipoNomenclador);

	Liferay.Popup.close(popupMD);

}

function pasarParametrosAParentNm(tipoNomenclador,codigo,descripcion) {
	seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
    <%= reclamoPortletNamespace %>cerrarNm();
}


function <%= reclamoPortletNamespace %>cerrarDivNm(){
	jQuery("#divSeguimientoSur").hide("slow");
}

function <%= reclamoPortletNamespace %>cerrarNm(){
	<%= reclamoPortletNamespace %>cerrarDivNm();
	if(popupMD){
		Liferay.Popup.close(popupMD);
	}
}


function DatosRevisionOk() {
    var diaRevision =
        jQuery(
            "#<%= reclamoPortletNamespace %>fecharevisionDia"
        ).val();

    var mesRevision =
        jQuery(
            "#<%= reclamoPortletNamespace %>fecharevisionMes"
        ).val();

    var anioRevision =
        jQuery(
            "#<%= reclamoPortletNamespace %>fecharevisionAnio"
        ).val();

    var diaRevisionInvalido =
        isNaN(parseInt(diaRevision, 10));

    var mesRevisionInvalido =
        isNaN(parseInt(mesRevision, 10));

    var anioRevisionInvalido =
        isNaN(parseInt(anioRevision, 10));

    if (diaRevisionInvalido
            || mesRevisionInvalido
            || anioRevisionInvalido) {

        alert(
            "Debe ingresar una fecha de Revisión válida."
        );

        return false;
    }

    var resolucion =
        document.getElementById(
            "<%= reclamoPortletNamespace %>resolucion"
        );

    if (resolucion == null
            || resolucion.selectedIndex == 0) {

        alert(
            "Debe seleccionar el tipo de resolución."
        );

        return false;
    }

    var diaOspim =
        parseInt(
            jQuery(
                "#<%= reclamoPortletNamespace %>fechaospimDia"
            ).val(),
            10
        );

    var mesOspim =
        parseInt(
            jQuery(
                "#<%= reclamoPortletNamespace %>fechaospimMes"
            ).val(),
            10
        );

    var anioOspim =
        parseInt(
            jQuery(
                "#<%= reclamoPortletNamespace %>fechaospimAnio"
            ).val(),
            10
        );

    var diaRevisionNumero =
        parseInt(
            diaRevision,
            10
        );

    var mesRevisionNumero =
        parseInt(
            mesRevision,
            10
        );

    var anioRevisionNumero =
        parseInt(
            anioRevision,
            10
        );

    var fechaOspim =
        new Date(
            anioOspim,
            mesOspim,
            diaOspim
        );

    var fechaRevision =
        new Date(
            anioRevisionNumero,
            mesRevisionNumero,
            diaRevisionNumero
        );

    var hoy = new Date();

    hoy.setHours(
        23,
        59,
        59,
        999
    );

    if (fechaRevision.getTime() < fechaOspim.getTime()) {
        alert(
            "La fecha de revision no puede ser inferior "
                    + "a la fecha de Ingreso del Reclamo "
                    + "(Fecha Ospim)."
        );

        return false;
    }

    if (fechaRevision.getTime() > hoy.getTime()) {
        alert(
            "La fecha de revision no puede ser superior "
                    + "a la fecha de hoy."
        );

        return false;
    }

    return true;
}

function ValidarDatosObligatorios(Edicion){

	var planBloqueado = jQuery("#<%= reclamoPortletNamespace %>plan_reclamo_bloqueado").val();

		if (planBloqueado == "1") {

		    var nombrePlan = jQuery("#<%= reclamoPortletNamespace %>nombre_plan_reclamo_bloqueado").val();

		    alert('Afiliado con plan "' +nombrePlan +'" no puede cargar un reclamo.');

		    return false;
		}

	var valor = 0;
	valor=jQuery('#<%= reclamoPortletNamespace %>cantprestacioneslista').val();


	var dia  = isNaN(parseInt(jQuery("#<%= reclamoPortletNamespace %>fechaospimDia").val()));
	var mes  = isNaN(parseInt(jQuery("#<%= reclamoPortletNamespace %>fechaospimMes").val()));
	var anio   = isNaN(parseInt(jQuery("#<%= reclamoPortletNamespace %>fechaospimAnio").val()));

	var dia1  = isNaN(parseInt(jQuery("#<%= reclamoPortletNamespace %>fechaseccionalDia").val()));
	var mes1  = isNaN(parseInt(jQuery("#<%= reclamoPortletNamespace %>fechaseccionalMes").val()));
	var anio1   = isNaN(parseInt(jQuery("#<%= reclamoPortletNamespace %>fechaseccionalAnio").val()));


	var dia2  = isNaN(parseInt(jQuery("#<%= reclamoPortletNamespace %>fechacierreDia").val()));
	var mes2  = isNaN(parseInt(jQuery("#<%= reclamoPortletNamespace %>fechacierreMes").val()));
	var anio2   = isNaN(parseInt(jQuery("#<%= reclamoPortletNamespace %>fechacierreAnio").val()));


	var msgs = ["Error en la fecha Ospim.", "Debe seleccionar el sector que inicia  el reclamo.", "Debe seleccionar el estado del reclamo.","Debe seleccionar al Afiliado asociado al reclamo.","Complete la Fecha Seccional o dejela en blanco","Debe seleccionar el tipo de Pedido"];
	var condiciones =[5];
	var controles  =[5];

	var tipoSelectsector  =document.getElementById("<%= reclamoPortletNamespace %>sector");
	var tipoSelectestado  =document.getElementById("<%= reclamoPortletNamespace %>estado");
	var tipoSelecttipopedido =document.getElementById("<%= reclamoPortletNamespace %>tipopedido");
	/* document.getElementById("<%= reclamoPortletNamespace %>tipopedido").selectedIndex==0 */
	var cuil=jQuery('#<%= reclamoPortletNamespace %>cuil').val();
	var inte=jQuery('#<%= reclamoPortletNamespace %>inte').val();



	var  resp=true;

	controles[0]=document.getElementById("<%= reclamoPortletNamespace %>fechaospimDia");
	controles[1]=tipoSelectsector;
	controles[2]=tipoSelectestado;
	controles[3]=document.getElementById("<%= reclamoPortletNamespace %>cuil");
	controles[4]=document.getElementById("<%= reclamoPortletNamespace %>fechaseccionalDia");
	controles[5]=tipoSelecttipopedido;

	condiciones[0]=dia || mes || anio;

	condiciones[1]=(tipoSelectsector.selectedIndex==0);
	condiciones[2]=(tipoSelectestado.selectedIndex==0);
	condiciones[3]=(cuil=="" || inte=="" );
	condiciones[4]=(dia1 || mes1 || anio1) && (!dia1 || !mes1 || !anio1) ;
	condiciones[5]=(tipoSelecttipopedido.selectedIndex==0);

	if (condiciones[0]){
		resp=false;
		alert (msgs[0] );
		controles[0].focus();
	}
	if (condiciones[1] && resp){
		resp=false;
		alert (msgs[1] );
		controles[1].focus();
	}
	if (condiciones[2] && resp){
		resp=false;
		alert (msgs[2] );
		controles[2].focus();
	}
	if (condiciones[3] && resp){
		resp=false;
		alert (msgs[3] );
		controles[3].focus();
	}
	if (condiciones[4] && resp){
		resp=false;
		alert (msgs[4] );
		controles[4].focus();
	}

	if (condiciones[5] && resp){
		resp=false;
		alert (msgs[5] );
		controles[5].focus();
	}

	// valida datos del cierre del reclamo
	var idgestion = jQuery('#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo').val();

	var justificacion=jQuery('#<%= reclamoPortletNamespace %>justificacionmedcica_reclamo').val();

	var tipoPedidoCierre = jQuery("#<%= reclamoPortletNamespace %>tipopedido").val();

		var observacionMedica = jQuery("#<%= reclamoPortletNamespace %>observacion_medica").val();

		var requiereObservacionMedica =
		    tipoPedidoCierre == "REINTEGRO" &&
		    (
		        idgestion == "4" ||
		        idgestion == "5"
		    );

		if (
		    requiereObservacionMedica &&
		    (
		        observacionMedica == null ||
		        observacionMedica == "" ||
		        observacionMedica == "0"
		    )
		) {

		    alert(
		        "Debe seleccionar una observación del área médica."
		    );

		    jQuery(
		        "#<%= reclamoPortletNamespace %>observacion_medica"
		    ).focus();

		    return false;
		}

	if (idgestion == 0  && jQuery('#<%= reclamoPortletNamespace %>estado option:selected').text().trim() == 'CERRADO' ){
		alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
		document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").focus();
		return false;
	}

	/* if (idgestion==5){ */
	if (idgestion==5){
	/* 	var isDisabled = jQuery('#<%= reclamoPortletNamespace %>dosporciento').is(':disabled');
	    if (!isDisabled) { */
			if(! confirm("Al seleccionar la opción RECHAZADO el sistema rechazará todas las prestaciones del caso, no podrá asociarlas a reintegros. Está seguro ?")){
				return false;
			/* } */
	    }
	}
		var respResolucion = document.getElementById("<%= reclamoPortletNamespace %>respresolucion");

		if ( jQuery('#<%= reclamoPortletNamespace %>auditoriaadministrativa').val()!="Ok" ){ // auditoria administrativa

			if (justificacion.length ==0  && resp ){ // no hay revisiones activas
				alert('Tiene que ingresar la justificación médica del Caso para efectuar el Cierre del Caso.');
				jQuery('#<%= reclamoPortletNamespace %>justificacionmedcica_reclamo').focus();
				resp=false;
			}
		}
		// validar si
		if (idgestion<1  && resp && jQuery('#<%= reclamoPortletNamespace %>estado option:selected').text() == 'CERRADO' ){
			alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
			document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").focus();
			resp=false;
		}

			if ((dia2 || mes2 || anio2)  && resp )  {
				alert('Debe ingresar la fecha de Cierre del Reclamo');
				document.getElementById("<%= reclamoPortletNamespace %>fechacierreDia").focus();
				resp=false;
			}

		if (jQuery(
                "#<%= reclamoPortletNamespace %>estado"
            ).val() == "3") {

            if (parseInt(
                    jQuery(
                        "#<%= reclamoPortletNamespace %>cantrevisionesactivas"
                    ).val(),
                    10
                ) < 1
                && resp) {

                alert(
                    "Debe registrar por lo menos una revisión "
                            + "activa para cerrar el reclamo."
                );

                resp = false;
            }
        }


// SI ES CIERRE DEL CASO NO SE CONTROLA SI SE DIERON DE BAJA TODAS LAS PRESTACIONES

	valor=jQuery('#<%= reclamoPortletNamespace %>cantprestacioneslista').val();


    if (Edicion && addprestacion) {
    	if (valor <1   && resp){
    		alert('Debe tener ingresada por lo menos una prestación');
    		resp=false;
    	}
    }else{
    		if (valor <1  && resp ){

    		}
    }

    var integracion = jQuery("#<%= reclamoPortletNamespace %>integracion").val();
	 if ('EXCEPCION' ==  jQuery("#<%= reclamoPortletNamespace %>tipopedido").val()){
		if (integracion == '0'){
			alert('Debe seleccionar un tipo de integración ');
			resp=false;
		}

	 }

	 if (Edicion && resp ) {
		 if (idgestion!=0 &&idgestion!=5 ) {
	    	if (valor <1   ){
	    		alert('Debe tener ingresada por lo menos una prestación para poder cerrar el reclamo.');
	    		resp=false;
	    	}
		 }
	 }

	 var codError='';
	 var baja =  jQuery('#<%= reclamoPortletNamespace %>baja_fecha').val();
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/validar_reclamo_afiliado_prestaciones';
	 url +='&baja='+baja;

	 jQuery.ajax({
		   url: url,
		   async: false,
		   success: function(data) {
			  var obj = jQuery.parseJSON(data);
			  codError = obj.codError;
	   		}
	 });

	 if(codError == '6'){
	       alert('La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado');
		   resp=false;
	 }


	return resp;
}


function <%= reclamoPortletNamespace %>saveReclamo() {
    if (guardandoReclamo) {
        return false;
    }

    if (!ValidarDatosObligatorios(false)) {
        return false;
    }

    var idgestion =
        jQuery(
            '#<%= reclamoPortletNamespace %>'
                    + 'tipo_gestion_cierre_reclamo'
        ).val();

    jQuery(
        '#<%= reclamoPortletNamespace %>tipogestion'
    ).val(idgestion);

    document
        .<%= reclamoPortletNamespace %>reclamo_fm
        .<%= reclamoPortletNamespace %><%= Constants.CMD %>
        .value = '<%= Constants.SAVE %>';

    guardandoReclamo = true;

    var url =
        '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" /></portlet:actionURL>';

    url += "&esDatosTab=true";

    document
        .<%= reclamoPortletNamespace %>reclamo_fm
        .method = "post";

    submitForm(
        document
            .<%= reclamoPortletNamespace %>reclamo_fm,
        url
    );

    return false;
}

/* Cambia estado a Observado */
function <%= reclamoPortletNamespace %>volverEstadoObservado() {

	var confirmar = false;
	/* Recupera el Id del Reclamo */
	var idgestion=jQuery('#<%= reclamoPortletNamespace %>id_reclamosel').val();

	confirmar=confirm ('Estas observando la precarga, la misma será devuelta ' +
			'a la seccional. ' + '\nEstas seguro?');

	if(confirmar) {
		popup = Liferay.Popup({title:"<liferay-ui:message key="observacion-interna" />",modal:true,width:700});
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/observar';
		url = url + "&idReclamo=" + idgestion;
		jQuery(popup).load(url);
	}
}

function <%= reclamoPortletNamespace %>editaReclamo(fromAutoriza) {

	if (fromAutoriza) {
		abreAutorizacion();
	}

	if ( ValidarDatosObligatorios(true))  {

	  /* var data=jQuery('#<%= reclamoPortletNamespace %>estado').val();
	  if ( document.getElementById("<%= reclamoPortletNamespace %>estadosel").value == data){
		 document.getElementById("<%= reclamoPortletNamespace %>estado").value="0";
	  } */

	 /*  if ( document.getElementById("<%= reclamoPortletNamespace %>tipopedido").disabled = "disabled"){
		document.getElementById("<%= reclamoPortletNamespace %>tipopedido").disabled = "";
	  } */

	  /*esta chanchada es porque el action toma el id de cierre de tipogestion que es un hidden y no de tipo_gestion_cierre_reclamo*/
		var idgestion=jQuery('#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo').val()
		jQuery('#<%= reclamoPortletNamespace %>tipogestion').val(idgestion);
	    //jQuery('#<%= reclamoPortletNamespace %>id_reclamosel').val(0);

	  var accionEnCurso = document.<%= reclamoPortletNamespace %>reclamo_fm.<%= reclamoPortletNamespace %><%= Constants.CMD %>.value;
	  document.<%= reclamoPortletNamespace %>reclamo_fm.<%= reclamoPortletNamespace %><%= Constants.CMD %>.value='<%=Constants.UPDATE %>';

	  /* habilitarControlesCierre(); */




	  var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" /></portlet:actionURL>';
	  url = url + "&esDatosTab=true";
	  document.<%= reclamoPortletNamespace %>reclamo_fm.method = 'post';


	  submitForm(document.<%= reclamoPortletNamespace %>reclamo_fm, url);

	  /* onOffControlesRequest(true); */
	}
}


function <%= reclamoPortletNamespace %>reabrirReclamo(fromAutoriza) {

	if (fromAutoriza) {
		abreAutorizacion();
	}


/* 	  var data=jQuery('#<%= reclamoPortletNamespace %>estado').val();
	  if ( document.getElementById("<%= reclamoPortletNamespace %>estadosel").value == data){
		 document.getElementById("<%= reclamoPortletNamespace %>estado").value="0";
	  } */

	/*   if ( document.getElementById("<%= reclamoPortletNamespace %>tipopedido").disabled = "disabled"){
		document.getElementById("<%= reclamoPortletNamespace %>tipopedido").disabled = "";
	  } */

	  var accionEnCurso = document.<%= reclamoPortletNamespace %>reclamo_fm.<%= reclamoPortletNamespace %><%= Constants.CMD %>.value;
	  document.<%= reclamoPortletNamespace %>reclamo_fm.<%= reclamoPortletNamespace %><%= Constants.CMD %>.value='<%=Constants.RESTORE %>';

	  /* habilitarControlesCierre(); */

	  var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" /></portlet:actionURL>';
	  url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest' + "&esDatosTab=false";

	  document.<%= reclamoPortletNamespace %>reclamo_fm.method = 'post';

	  submitForm(document.<%= reclamoPortletNamespace %>reclamo_fm, url);

/* 	  onOffControlesRequest(true); */

}




function manejartipogestion(){

	/* var tipoGestionArray = jQuery('#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo').val().split("|");	 */
	var idgestion = jQuery('#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo').val();
	/* var idgestion =tipoGestionArray [0];	 */
	var sector=jQuery('#<%= reclamoPortletNamespace %>sector').val();
	var nroLote=jQuery('#<%= reclamoPortletNamespace %>nroLote').val();
	jQuery('#<%= reclamoPortletNamespace %>tipogestion').val(idgestion);
	if("1"==idgestion && sector=="PRESTACIONES MEDICAS" && (nroLote==null || nroLote=="" || nroLote=="0")){

		 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/propone_lote_reclamo_prestacional';
			jQuery.ajax({
				url: url,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					jQuery('#<%= reclamoPortletNamespace %>nroLote').val(obj.lote);
				}
			});
	}
	if("1"!=idgestion || sector!="PRESTACIONES MEDICAS"){
		jQuery('#<%= reclamoPortletNamespace %>nroLote').val("");
	}



}


function manejarListaPresentes(){
	var tipoSelect  =document.getElementById("<%= reclamoPortletNamespace %>presenteslista");
	jQuery("#<%= reclamoPortletNamespace %>presentes").val(tipoSelect.value); // asigna el valor de la lista al control oculto
}


function cambioresolucion(){

	try{
		var tipoSelect  =document.getElementById("<%= reclamoPortletNamespace %>resolucion");
		var justificacion=jQuery('#<%= reclamoPortletNamespace %>justificacionmedcica_reclamo').val();
		if  (tipoSelect.selectedIndex>0 && justificacion.length ==0  && document.getElementById("<%= reclamoPortletNamespace %>respresolucion").selectedIndex!=1){
				jQuery('#<%= reclamoPortletNamespace %>justificacionmedcica_reclamo').focus();
				tipoSelect.selectedIndex=0;
				alert('Tiene que ingresar la Justificacion Medica del Caso para ingresar la revision.');
			}

	}catch (err) {}

}


function manejarTipoPedido(){
	var tipoPedido =document.getElementById("<%= reclamoPortletNamespace %>tipopedido");
	if ( tipoPedido.selectedIndex==0 ){
		alert("El tipo de pedido es obligatorio");
		document.getElementById("<%= reclamoPortletNamespace %>tipopedido").focus();
	}
	//if(tipoPedido.value!="EXTRACAPITA"){
	//	jQuery("#<%= reclamoPortletNamespace %>comprobante_letra").append(new Option("A", "A"));
	//}

}

function cambioTipoPedido(){
	var tipoSector =document.getElementById("<%= reclamoPortletNamespace %>sector");
	if(tipoSector.selectedIndex!=0){
		manejarTipoSector();
	}
}


function manejarTipoPedidoCierre(){
	var tipoPedido  = document.getElementById("<%= reclamoPortletNamespace %>tipopedido");
	jQuery('#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo').html('');  //vacio lista opciones del select
/* 	jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").append(new Option("SELECCIONE LA GESTION", "0"));
	document.getElementById("<%= reclamoPortletNamespace %>tipopedido").selectedIndex==0 */
	jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").append(new Option("SELECCIONE UNA OPCION", "0"));
	jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo option[value='0']").attr("selected", true);
	if(tipoPedido.value=="EXCEPCION"){
		jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").append(new Option("FACTURACION DIRECTA", "3"));
		jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").append(new Option("PAGADO POR MECANISMO INTEGRACION", "6"));
		/* jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo option[value='3']").attr("selected", true); //FACT. DIRECTA */
	}
	if(tipoPedido.value=="REINTEGRO"){
		jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").append(new Option("REINTEGRO", "4"));
		/* jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo option[value='4']").attr("selected", true); //REINTEGRO */
	}
	if(tipoPedido.value=="EXTRACAPITA"){
		jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").append(new Option("EXTRACAPITA", "1"));
		/* jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo option[value='1']").attr("selected", true); //EXTRACAPITA */
	}
	jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").append(new Option("RECHAZADO", "5"));
}

function manejarTipoSector(){
	var tipoSector  =document.getElementById("<%= reclamoPortletNamespace %>sector");
	var tipopedido  = document.getElementById("<%= reclamoPortletNamespace %>tipopedido");
	try {
		jQuery("#<%= reclamoPortletNamespace %>busqueda_prestaciones").show();
		jQuery("#<%= reclamoPortletNamespace %>busqueda_farmacia").hide();
		jQuery("#<%= reclamoPortletNamespace %>nom_seleccionado").val("1"); // se selecciono maestra de prestaciones medicas
		jQuery('#<%= reclamoPortletNamespace %>troquel').val("");
		jQuery('#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro').val("");
		jQuery("#<%= reclamoPortletNamespace %>tipoNomencladorSeguimiento_filtro").val("");

		// 1. Discapacidad
		// 2. Prest Medicas
		// 3. Farmacia
		// 4. Legales
		// 5. Liquidaciones
		// 6. Odonto

		// En Tipo Reintegro y Sector Farmacia, muestra Medicamento y Troquel
		// En el resto muestra "Codigo Presentado (nomenclador)
		if (tipoSector.selectedIndex==3) {

   			if (tipopedido.selectedIndex!=1){
				if(tipoSector.selectedIndex == 3 && tipopedido.selectedIndex == 2){
	   				jQuery("#<%= reclamoPortletNamespace %>busqueda_farmacia").show();
	  				 jQuery("#<%= reclamoPortletNamespace %>busqueda_prestaciones").hide();
				}


  		         jQuery("#<%= reclamoPortletNamespace %>nom_seleccionado").val("2"); // se selecciono maestra de farmacia
   	   		}else{
   	   		     jQuery("#<%= reclamoPortletNamespace %>tipoNomencladorSeguimiento_filtro").val(9);  // farmacia
   	   		}
        }
   		if (tipoSector.selectedIndex==1){
   			jQuery("#<%= reclamoPortletNamespace %>tipoNomencladorSeguimiento_filtro").val(8); // discapacidad
   		} else if (tipoSector.selectedIndex==6){
   			/* ODONTOLOGIA Tipo Nomenclador 1 */
   			jQuery("#<%= reclamoPortletNamespace %>tipoNomencladorSeguimiento_filtro").val(1); // discapacidad
   		}
	}
	catch (err) {
		alert('error manejarTipoSector() ');
	}
}






function <%= reclamoPortletNamespace %>agregarRevision() {

	var  revisionConCierre =false;

	if ( DatosRevisionOk())  {

		var resolucion = jQuery('#<%= reclamoPortletNamespace %>resolucion').val();

		var presentes = jQuery('#<%= reclamoPortletNamespace %>presentes').val();
		var respresolucion = jQuery('#<%= reclamoPortletNamespace %>respresolucion').val();
		var revisionFechaVtoDia = jQuery('#<%= reclamoPortletNamespace %>fecharevisionDia').val();
		var revisionFechaVtoMes = jQuery('#<%= reclamoPortletNamespace %>fecharevisionMes').val();
		var revisionFechaVtoAnio = jQuery('#<%= reclamoPortletNamespace %>fecharevisionAnio').val();

		var observacionMedica = jQuery('#<%= reclamoPortletNamespace %>observacion_medica').val();



		var reclamoobservacion  = jQuery('#<%= reclamoPortletNamespace %>observacion_revision').val();
		var chk_amparo=jQuery("#<%= reclamoPortletNamespace %>chk_amparo").is(':checked');
		var chk_superintendencia=jQuery("#<%= reclamoPortletNamespace %>chk_superintendencia").is(':checked');
		var chk_recuperable = jQuery("#<%= reclamoPortletNamespace %>chk_recuperable").is(':checked');
		var chk_entramite = jQuery("#portlet:namespace />chk_entramite").is(':checked');

	    if (document.getElementById("<%= reclamoPortletNamespace %>resolucion").selectedIndex==0 ) {
	    	resolucion="";
	    }
	    if (document.getElementById("<%= reclamoPortletNamespace %>presentes").selectedIndex==0 ) {
	    	presentes="";
	    }
	    if (document.getElementById("<%= reclamoPortletNamespace %>respresolucion").selectedIndex==0 ) {
	    	respresolucion="";
	    }
	    jQuery('#<%= reclamoPortletNamespace %>auditoriaadministrativa').val('');
	    if (document.getElementById("<%= reclamoPortletNamespace %>respresolucion").selectedIndex==1 ) {
	    	jQuery('#<%= reclamoPortletNamespace %>auditoriaadministrativa').val('Ok');
	    }



		var params = {"resolucion":resolucion,
							   "presentes":presentes,
							   "respresolucion":respresolucion,
							   "revisionFechaVtoDia":revisionFechaVtoDia,
							   "revisionFechaVtoMes":revisionFechaVtoMes,
							   "revisionFechaVtoAnio":revisionFechaVtoAnio,
							   "reclamoobservacion":reclamoobservacion,
							   "observacionMedica":observacionMedica
							   };


		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/lista_revisiones_reclamo" /></portlet:renderURL>';


		if (resolucion.toUpperCase()!="AUTORIZADO"){
			if(confirm("Confirma el Cierre del Caso con el Rechazo en la revision ?")){
	 			    /* var estadoSelectsector  =document.getElementById("<%= reclamoPortletNamespace %>estado"); */
				    //estadoSelectsector.selectedIndex = 2; // setea el estado en cerrado
				    /* estadoSelectsector.selectedIndex = ubicacionOpcionEstadoCerradoCombo();	 */
				    /* jQuery("#<%= reclamoPortletNamespace %>estado option[value='3']").attr("selected", true); //CERARADO */
				    jQuery(
                        "#<%= reclamoPortletNamespace %>estado"
                    ).val("3");
				    controlarEstadoCerrado(); // hace visible los controles del estado cerrado

				    document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").disabled = false;

					var tipoSelectsector  =document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo");

					seteaControlesFacturacionDirecta(true);
					/* tipoSelectsector.selectedIndex= ubicacionOpcionRechazadoenCombo(); */
				    /* jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo option[value='5']").attr("selected", true); //RECHAZADO */
				    /*jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo option[value='RECHAZADO']").attr("selected",true);*/


				    jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").val("5");
				    tipoGestionCierreReclamo();

					/* var tipoGestionArray=jQuery('#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo').val().split("|"); */
					var idgestion=jQuery('#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo').val()

					/* var idgestion =tipoGestionArray [0]; */
					jQuery('#<%= reclamoPortletNamespace %>tipogestion').val(idgestion);
					jQuery('#<%= reclamoPortletNamespace %>reclamo_observacion_cierre').val('RECHAZO DE LA PRESTACION EN LA REVISION.');
					revisionConCierre=true;
					jQuery('#<%= reclamoPortletNamespace %>cantrevisionesactivas').val(1); // para que no valide esto
					desactivaCheckCierre();

	 		}else{
					return false;
			}
		}

		// oculta boton de agreagr revision porque solo se admite un aprobacion o un rechazo no hay parciales dentro del reclamo
		jQuery("#<%= reclamoPortletNamespace %>botonrevision").hide();
		jQuery("#<%= reclamoPortletNamespace %>mensajerevisionefectuada").html("Revisión Efectuada, el Sistema soporta solo una revisión activa (No de baja).");

	 	jQuery(
            '#<%= reclamoPortletNamespace %>lista_revisiones'
        ).load(
            url,
            params,
            function() {
                jQuery(
                    '#<%= reclamoPortletNamespace %>buscando'
                ).hide();

                jQuery(
                    '#<%= reclamoPortletNamespace %>resolucion'
                ).val('');

                jQuery(
                    '#<%= reclamoPortletNamespace %>presentes'
                ).val('');

                jQuery(
                    '#<%= reclamoPortletNamespace %>respresolucion'
                ).val('');

                jQuery(
                    '#<%= reclamoPortletNamespace %>observacion_revision'
                ).val('');

                if (revisionConCierre) {
                    <% if (reclamoPersistido) { %>
                    <%= reclamoPortletNamespace %>editaReclamo(
                            false
                    );
                    <% } else { %>
                    <%= reclamoPortletNamespace %>saveReclamo();
                    <% } %>
                }
            }
        );
	 	
		 jQuery('#<%= reclamoPortletNamespace %>resolucion').val('');
		 jQuery('#<%= reclamoPortletNamespace %>presentes').val('');
		 jQuery('#<%= reclamoPortletNamespace %>respresolucion').val('');	  	  	  
		 document.getElementById("<%= reclamoPortletNamespace %>fecharevisionDia").selectedIndex = 0;	 
		 document.getElementById("<%= reclamoPortletNamespace %>fecharevisionMes").selectedIndex = 0;	 
		 document.getElementById("<%= reclamoPortletNamespace %>fecharevisionAnio").selectedIndex = 0;
		 document.getElementById("<%= reclamoPortletNamespace %>fecharevisionAnio").selectedIndex = 0;
		 jQuery('#<%= reclamoPortletNamespace %>observacion_revision').val('');
	}
}       		

/* function ubicacionOpcionRechazadoenCombo(){
	var idselect;
	var pos=0;
	var posicion=0;
		jQuery('#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo option').each(function(){
        	tipoGestionArray = jQuery(this).val().split("|");
        	idselect =tipoGestionArray [0];         
        	if (idselect == 5){
        	 	posicion=pos;
	        }
        	pos=pos+1;
        });
	return posicion;
} */

/* function ubicacionOpcionEstadoCerradoCombo(){
	var idselect;
	var tipoGestionArray;
	var pos=0;
	var posicion=0;
		jQuery('#<%= reclamoPortletNamespace %>estado option').each(function(){
        	tipoGestionArray = jQuery(this).val().split("|");
        	idselect =tipoGestionArray [0];         
        	if (idselect == 3){
        	 	posicion=pos;
	        }
        	pos=pos+1;
        });
	return posicion;
} */

function <%= reclamoPortletNamespace %>verprestacionesasociadas() {
	
	if (document.getElementById("<%= reclamoPortletNamespace %>botonprestacionesasociadas").value=='Ver Prestaciones del Caso Asociado.'){
		jQuery("#<%= reclamoPortletNamespace %>lista_prestaciones_asociadas").show();
		document.getElementById("<%= reclamoPortletNamespace %>botonprestacionesasociadas").value='Ocultar Prestaciones del Caso Asociado.';
	}else{
		jQuery("#<%= reclamoPortletNamespace %>lista_prestaciones_asociadas").hide();
		document.getElementById("<%= reclamoPortletNamespace %>botonprestacionesasociadas").value='Ver Prestaciones del Caso Asociado.';
	}
}

function <%= reclamoPortletNamespace %>ocultacontactosdelreclamo() {
	jQuery("#<%= reclamoPortletNamespace %>lista_contactos_reclamo").hide();
	jQuery("#<%= reclamoPortletNamespace %>botoncontactosreclamo").show();
	jQuery("<%= reclamoPortletNamespace %>botoncontactosreclamo").value='Ver Contactos Asociados al Caso.';

}


function <%= reclamoPortletNamespace %>vercontactosdelreclamo() {
		
	var cuil=jQuery('#<%= reclamoPortletNamespace %>cuil').val();
	var inte=jQuery('#<%= reclamoPortletNamespace %>inte').val();
	var idreclamoprestacion=jQuery('#<%= reclamoPortletNamespace %>idreclamoprestacion').val();
	var modoconsulta=jQuery('#<%= reclamoPortletNamespace %>consultareclamo').val();
	
	    if ((cuil=="" || inte=="" )){		
			alert ('Debe seleccionar al Afiliado para ver sus contactos.');
			document.getElementById("<%= reclamoPortletNamespace %>cuil").focus();
			return false;
		}	    
			
	    if (document.getElementById("<%= reclamoPortletNamespace %>botoncontactosreclamo").value=='Ver Contactos Asociados al Caso.'){
		jQuery("#<%= reclamoPortletNamespace %>lista_contactos_reclamo").show();
		jQuery("#<%= reclamoPortletNamespace %>botoncontactosreclamo").hide();
		jQuery("#<%= reclamoPortletNamespace %>justificacion_medica_reclamo").hide();
		
		var cuil=jQuery('#<%= reclamoPortletNamespace %>cuil').val();
		var inte=jQuery('#<%= reclamoPortletNamespace %>inte').val();
		var idreclamoprestacion=jQuery('#<%= reclamoPortletNamespace %>idreclamoprestacion').val();		
		
		if ( jQuery("#<%= reclamoPortletNamespace %>idreclamoprestacion").val()<1 
				&&  ((cuil==jQuery("#<%= reclamoPortletNamespace %>cuiltitular").val()  
						&& inte==jQuery("#<%= reclamoPortletNamespace %>intetitular").val() ))  ){			
			return false; // es el mismo afiliado 
		}		
		
		jQuery("#<%= reclamoPortletNamespace %>cuiltitular").val(cuil);
		jQuery("#<%= reclamoPortletNamespace %>intetitular").val(inte);
		
		var params = {"cuil_contacto":cuil,"inte_contacto":inte,"idreclamoprestacion":idreclamoprestacion,"modoconsulta":modoconsulta};

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/lista_contactos_reclamo" /></portlet:renderURL>';
		
		jQuery('#<%= reclamoPortletNamespace %>lista_contactos_reclamo').load(url,params, function(){
										jQuery('#<%= reclamoPortletNamespace %>buscando').hide();          															
															  });			 	 
		}					
	}
	

function <%= reclamoPortletNamespace %>editarPrestacionSeleccionada(tipoAccion) {
	//tipoAccion=1 edicion 
	//tipoAccion=2 Autorizacion prestacion 
	//tipoAccion=3 Rechazo de  prestacion	
		
	var frecuencia= jQuery('#<%= reclamoPortletNamespace %>frecuenciaEdicion').val();
	var cantidad =  jQuery('#<%= reclamoPortletNamespace %>cantidadEdicion').val();
	var importe = jQuery('#<%= reclamoPortletNamespace %>importeEdicion').val();
	var cargoospim= jQuery('#<%= reclamoPortletNamespace %>cargoospimEdicion').val();
	var cargops= jQuery('#<%= reclamoPortletNamespace %>cargopsEdicion').val();
	var cargoimesa= jQuery('#<%= reclamoPortletNamespace %>cargoimesaEdicion').val();
	var reconocidoSSS= jQuery('#<%= reclamoPortletNamespace %>reconocidoSSSEdicion').val();
	var observaciones= jQuery('#<%= reclamoPortletNamespace %>observacion_prestacionEdicion').val();
    var prestacion= "Graba Edicion";
    var idprestacion =  jQuery("#<%= reclamoPortletNamespace %>codigoprestacion").val();
    var idRegistro=jQuery('#<%= reclamoPortletNamespace %>idRegistro').val();

    var estadoAprobacion = tipoAccion;
    var recuperableSur  =  jQuery('#<%= reclamoPortletNamespace %>recuperable_surEdicion').val();  
    
    var cpbteTipo=jQuery('#<%= reclamoPortletNamespace %>comprobante_tipo_edicion').val();

    var cpbteNro=jQuery('#<%= reclamoPortletNamespace %>comprobante_nro_edicion').val();
    var cpbteDia=jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteDiaEdicion').val();
    var cpbteMes=jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteMesEdicion').val();
    var cpbteAnio=jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteAnioEdicion').val();
    var cpbteCantidad=jQuery('#<%= reclamoPortletNamespace %>cantidadFC_edicion').val();
    var cpbteImporte= jQuery('#<%= reclamoPortletNamespace %>importeUnitarioFC_edicion').val();
    var importeFC = jQuery('#<%= reclamoPortletNamespace %>importeFC_edicion').val();
    var cpbteCuit=jQuery('#<%= reclamoPortletNamespace %>cuit_entidad_edicion').val();
    var cpbteSucursal=jQuery('#<%= reclamoPortletNamespace %>comprobante_suc_edicion').val();
    var cpbteCuitSucursal=jQuery('#<%= reclamoPortletNamespace %>sucursal_entidad_edicion').val();
    var cpbteLetra=jQuery('#<%= reclamoPortletNamespace %>comprobante_letra_edicion').val();


    var flagAmparo = false; 
    var estado=jQuery('#<%= reclamoPortletNamespace %>estado').val();
	var chk_amparo=jQuery("#<%= reclamoPortletNamespace %>chk_amparo").is(':checked');

	if (estado == 4 && chk_amparo == true ){
		//Si esta en estado inconsistente y es amparo permitimos grabar sin datos de comprobante
		flagAmparo = true;
	}


	// Solo validar montos si completó algo del área médica
	var tieneDatosAreaMedica = (
	    (importe != null && importe != '' && importe != 0) ||
	    (cargoospim != null && cargoospim != '' && cargoospim != 0) ||
	    (cargops != null && cargops != '' && cargops != 0) ||
	    (cargoimesa != null && cargoimesa != '' && cargoimesa != 0) ||
	    (reconocidoSSS != null && reconocidoSSS != '' && reconocidoSSS != 0)
	);

	if (tieneDatosAreaMedica) {
	    if (recuperableSur == 0) {
	        alert('Debe seleccionar el campo Recuperable');
	        return false;
	    }

	    //validación de montos
	    if (!validaMontosEdicion()) {       
	        return false;
	    }
	}
    
	/*
    if (!validaMontosEdicion()){       
   		return false;
	}*/

/*    
        importe=importe.replace(',','.');
        cargoospim=cargoospim.replace(',','.');
        cargops=cargops.replace(',','.');
*/

    if (frecuencia=="SELECCIONE"){
    	frecuencia="";    
	}
	
	var sector=jQuery('#<%= reclamoPortletNamespace %>sector').val();
	
	var fechaPrestacionDia='';
	var fechaPrestacionMes='';
	var fechaPrestacionAnio='';
	
    
    fechaPrestacionDia=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDiaEdicion').val(); 
    fechaPrestacionMes=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionMesEdicion').val();
    fechaPrestacionAnio=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionAnioEdicion').val();
    
    id_medicamento_edit=jQuery('#<%= reclamoPortletNamespace %>troquel_edit').val();
	var nombre_medicamento_edit = jQuery('#<%= reclamoPortletNamespace %>nombre_medicamento_edit').val();

    if (flagAmparo == false  && (frecuencia ==null ||  frecuencia=='')){
    		alert('Debe seleccionar la frecuencia correspondiente.');
    		return false ;
    }
		
	if (flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT')  && cpbteLetra==''){
		  alert('Debe seleccionar la letra del comprobante');
		  return false;
	}	
	
	if(flagAmparo == false && (importeFC==null || importeFC==0)){
	  	alert('Debe ingresar el importe de la Factura.');
		return false ;
	}
	
   
    if(flagAmparo == false && (cpbteCuit==null || cpbteCuit=='')){
    	alert('Debe ingresar el CUIT del Comprobante');
		return false ;
    }
    

    if(flagAmparo == false && (cpbteCuitSucursal==null || cpbteCuitSucursal=='')){
    	alert('Debe ingresar la sucursal del CUIT del Comprobante');
		return false ;
    }
    
    
    if(flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT') && (cpbteSucursal==null || cpbteSucursal=='')){
    	alert('Debe ingresar la Sucursal del Comprobante');
		return false ;
    }
    
    if(flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT') && (cpbteNro==null || cpbteNro=='')){
    	alert('Debe ingresar el Nro del Comprobante');
		return false ;
    }
    
    if (flagAmparo == false){
	    if(cpbteDia==null || cpbteDia==0 || cpbteDia=='' ||
	       cpbteMes==null || cpbteMes==-1 || cpbteMes=='' ||
	       cpbteAnio==null || cpbteAnio==0 || cpbteAnio==''){
	       alert('Debe ingresar la fecha del Comprobante');
	       return false;	
	    }
    }
	
    if(flagAmparo == false && (cpbteCantidad==null || cpbteCantidad==0 || cpbteCantidad=='')){
   		alert('Debe ingresar la cantidad del Comprobante');
        return false;	
    }
   
    if(flagAmparo == false && (cpbteImporte==null || cpbteImporte==0 || cpbteImporte=='')){
  	 	alert('Debe ingresar importe unitario del Comprobante');
        return false;	
   }
   
   if(flagAmparo == false && (importeFC==null || importeFC==0 || importeFC=='')){
     	alert('Debe ingresar importe total del Comprobante');
        return false;	
   }
    
    var codigoSeguimiento_filtro_edit = jQuery('#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro_edit').val();
	var descripcionSeguimiento_filtro_edit = jQuery("#<%= reclamoPortletNamespace %>descripcionSeguimiento_filtro_edit").val();
	var nom_seleccionado_edit = jQuery("#<%= reclamoPortletNamespace %>nom_seleccionado").val(); 
	var tipoNomenclador_edit = jQuery('#<%= reclamoPortletNamespace %>tipoNomenclador').val();
		

	if (nom_seleccionado_edit ==1){		 
		if (codigoSeguimiento_filtro_edit<1  ) {
		  alert('Debe seleccionar la prestación');
		  return false;
		} 	
	    if(descripcionSeguimiento_filtro_edit==null || descripcionSeguimiento_filtro_edit==''){
			  alert('Debe seleccionar la prestación');		  
			  return false;
	    }
		
	}else{		
		if ( id_medicamento_edit <1) {
			alert('Debe seleccionar el medicamento');
			return false;
		}
		if ( nombre_medicamento_edit==null || nombre_medicamento_edit=='') {
			alert('Debe seleccionar el medicamento');
			return false;
		}
		

	}    
	
    	
    if(fechaPrestacionDia==null || fechaPrestacionDia==0 || fechaPrestacionDia=='' ||
    	       fechaPrestacionMes==null || fechaPrestacionMes==-1 || fechaPrestacionMes=='' ||
    	       fechaPrestacionAnio==null || fechaPrestacionAnio==0 || fechaPrestacionAnio==''){
    	       alert('Debe ingresar la fecha de la Prestación');
    	return false;	
    }
    
    
    if (!ValidaDatosReclamoEditar()){       
   		return false;
	}
    
    
    var cuil=jQuery('#<%= reclamoPortletNamespace %>cuil').val();
	var inte=jQuery('#<%= reclamoPortletNamespace %>inte').val();	
	
	var idTecerizadora = jQuery('#<%= reclamoPortletNamespace %>id_tercerizadora').val();
	
	var params = {"frecuencia":frecuencia,
						   "importe":importe,	
						   "cargoospim":cargoospim,
						   "cargops":cargops,
						   "cargoimesa":cargoimesa,
						   "prestacion":prestacion,
						   "idprestacion":idprestacion,
						   "idRegistro":idRegistro,
						   "grabaedicion":true,
						   "estadoAprobacion": estadoAprobacion,
						   "recuperableSur": recuperableSur,
						   "cantidad": cantidad,
						   "observaciones":observaciones,
						   "cpbte_tipo":cpbteTipo,
						   "cpbte_nro":cpbteNro,
						   "cpbte_dia":cpbteDia,
						   "cpbte_mes":cpbteMes,
						   "cpbte_anio":cpbteAnio,
						   "cpbte_cantidad":cpbteCantidad,
						   "cpbte_importe":cpbteImporte,
						   "cpbte_cuit":cpbteCuit,
						   "cpbte_sucursal":cpbteSucursal,
						   "importeFC":importeFC,
						   "cpbte_cuit_sucursal":cpbteCuitSucursal,
						   "cpbte_letra":cpbteLetra,
						   "fecha_prestacion_dia":fechaPrestacionDia,
						   "fecha_prestacion_mes":fechaPrestacionMes,
						   "fecha_prestacion_anio":fechaPrestacionAnio,
						   "id_medicamento_edit":id_medicamento_edit,
						   "nombre_medicamento_edit":nombre_medicamento_edit,
						   "codigoSeguimiento_filtro_edit":codigoSeguimiento_filtro_edit,
						   "descripcionSeguimiento_filtro_edit":descripcionSeguimiento_filtro_edit,
						   "nom_seleccionado_edit":nom_seleccionado_edit,
						   "tipoNomenclador_edit":tipoNomenclador_edit,
						   "reconocidoSSS":reconocidoSSS,
						   "cuil":cuil,
						   "inte":inte,
						   "id_tercerizadora": idTecerizadora
						   };	
	
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones" /></portlet:renderURL>';
 	
	if(cpbteTipo != 'OTR' && cpbteTipo != 'AUT'){
	  if (!validarExisteComprobante(params)){   
	   	return false;
	  }
	}
	    
 	
	jQuery('#<%= reclamoPortletNamespace %>lista_prestaciones_reclamos').load(url,params, function(){
									jQuery('#<%= reclamoPortletNamespace %>buscando').hide();            															
													  });			
	jQuery('#<%= reclamoPortletNamespace %>cantidadEdicion').val('1');
	jQuery('#<%= reclamoPortletNamespace %>importeEdicion').val('');
	jQuery('#<%= reclamoPortletNamespace %>totalEdicion').val('');
 	jQuery('#<%= reclamoPortletNamespace %>cargoospimEdicion').val('');
 	jQuery('#<%= reclamoPortletNamespace %>cargopsEdicion').val('');
 	jQuery('#<%= reclamoPortletNamespace %>cargoimesaEdicion').val('');
 	jQuery('#<%= reclamoPortletNamespace %>reconocidoSSSEdicion').val('');
 	jQuery('#<%= reclamoPortletNamespace %>observacion_prestacionEdicion').val('');
 	document.getElementById("<%= reclamoPortletNamespace %>frecuenciaEdicion").selectedIndex = 0;
	jQuery('#<%= reclamoPortletNamespace %>troquel').val(""); // farmacia 
	jQuery('#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro').val("");// prestaciones medicas 
	//jQuery('#<%= reclamoPortletNamespace %>recuperable_sur').attr('checked', false);	
	document.getElementById("<%= reclamoPortletNamespace %>recuperable_sur").selectedIndex = 0; 	
	
	jQuery('#<%= reclamoPortletNamespace %>comprobante_tipo_edicion').val('FCP');
	jQuery('#<%= reclamoPortletNamespace %>comprobante_letra_edicion').val('');
	jQuery('#<%= reclamoPortletNamespace %>comprobante_nro_edicion').val('');
	jQuery('#<%= reclamoPortletNamespace %>comprobante_suc_edicion').val('');
	jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteDiaEdicion').val('');
	jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteMesEdicion').val('');
	jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteAnioEdicion').val('');
	jQuery('#<%= reclamoPortletNamespace %>cantidadFC_edicion').val('');
	jQuery('#<%= reclamoPortletNamespace %>importeUnitarioFC_edicion').val('');
	jQuery('#<%= reclamoPortletNamespace %>importeFC_edicion').val('');
	jQuery('#<%= reclamoPortletNamespace %>cuit_entidad_edicion').val('');
    jQuery('#<%= reclamoPortletNamespace %>sucursal_entidad_edicion').val('');
    jQuery('#<%= reclamoPortletNamespace %>entidad_edicion').val('');
    
	jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDiaFarmacia').val(''); 
    jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionMesFarmacia').val('');
    jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionAnioFarmacia').val('');
    
	jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDia').val(''); 
    jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionMes').val('');
    jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionAnio').val('');
	
    
    jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDiaEdicion').val('');
    jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionMesEdicion').val('');
    jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionAnioEdicion').val('');

    jQuery("#<%= reclamoPortletNamespace %>nombre_medicamento_edit").val('');
    jQuery("#<%= reclamoPortletNamespace %>divBtnBuscaMedicamento_edit").show();
    
    
	<%= reclamoPortletNamespace %>limpiarNomencladorAutocompletar();
	   
    addprestacion=false;
    <%= reclamoPortletNamespace %>cancelaEdicionPrestacion();

}


function <%= reclamoPortletNamespace %>cancelaEdicionPrestacion() {
	
	// oculta div de datos de edicion
	jQuery("#<%= reclamoPortletNamespace %>datos_edicion_prestacion").hide();
	// habilita el buscador segun el sector
	manejarTipoSector();	
	jQuery("#<%= reclamoPortletNamespace %>datos_prestacion_ingreso").show();
	
	<%= reclamoPortletNamespace %>limpiarNomencladorAutocompletar();
	onOffcombosestadosprestaciones(true);	
	// mover el combo a la posicion de cargado porque no se confirmo el rechazo o la autorizacion
	
	var datos = document.getElementById("<%= reclamoPortletNamespace %>tipoaccionprestacion").value;	
	var datasplit =datos.split('-');
	var idPrestacion = datasplit[1];	
	document.getElementById('comboestadosreclamo'+ idPrestacion ).selectedIndex = "0";	
	document.getElementById("<%= reclamoPortletNamespace %>tipoaccionprestacion").value="";
	
}

function <%= reclamoPortletNamespace %>agregarPrestacion() {	
	
	var frecuencia= jQuery('#<%= reclamoPortletNamespace %>frecuencia').val();		
	var importe = jQuery('#<%= reclamoPortletNamespace %>importe').val();
	var cantidad  = jQuery('#<%= reclamoPortletNamespace %>cantidad').val();
	var cargoospim= jQuery('#<%= reclamoPortletNamespace %>cargoospim').val();
	var cargops= jQuery('#<%= reclamoPortletNamespace %>cargops').val();
	var cargoimesa= jQuery('#<%= reclamoPortletNamespace %>cargoimesa').val();
	var reconocidoSSS= jQuery('#<%= reclamoPortletNamespace %>reconocidoSSS').val();
	var observaciones= jQuery('#<%= reclamoPortletNamespace %>observacion_prestacion').val();		
    var troquel= jQuery('#<%= reclamoPortletNamespace %>troquel').val();
    var prestacion= jQuery('#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro').val();    
    var tiponomenclador =jQuery('#<%= reclamoPortletNamespace %>nom_seleccionado').val();
    var tiponomencladorprestacion =jQuery('#<%= reclamoPortletNamespace %>tiponomenclador').val();
    var nombre_medicamento=jQuery("#<%= reclamoPortletNamespace %>nombre_medicamento").val();
    var nombre_prestacion = jQuery('#<%= reclamoPortletNamespace %>descripcionSeguimiento_filtro').val();
    var tiponomnecladorprestacion =  jQuery("#<%= reclamoPortletNamespace %>tipoNomenclador").val(); 
    
    
    
    var recuperableSur  =  jQuery('#<%= reclamoPortletNamespace %>recuperable_sur').val();
    
    
    var cpbteTipo=jQuery('#<%= reclamoPortletNamespace %>comprobante_tipo').val();
    var cpbteNro=jQuery('#<%= reclamoPortletNamespace %>comprobante_nro').val();
    var cpbteDia=jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteDia').val();
    var cpbteMes=jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteMes').val();
    var cpbteAnio=jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteAnio').val();
    var cpbteCantidad=jQuery('#<%= reclamoPortletNamespace %>cantidadFC').val();
    var cpbteImporte= jQuery('#<%= reclamoPortletNamespace %>importeUnitarioFC').val();
    var importeFC = jQuery('#<%= reclamoPortletNamespace %>importeFC').val();
    var cpbteCuit=jQuery('#<%= reclamoPortletNamespace %>cuit_entidad').val();
    var cpbteCuitSucursal=jQuery('#<%= reclamoPortletNamespace %>sucursal_entidad').val();
    var cpbteSucursal=jQuery('#<%= reclamoPortletNamespace %>comprobante_suc').val();
    var cpbteLetra=jQuery('#<%= reclamoPortletNamespace %>comprobante_letra').val();
    
    
    var flagAmparo = false; 
    var estado=jQuery('#<%= reclamoPortletNamespace %>estado').val();
	var chk_amparo=jQuery("#<%= reclamoPortletNamespace %>chk_amparo").is(':checked');

	// Solo validar montos si completó algo del área médica
	var tieneDatosAreaMedica = (
	    (importe != null && importe != '' && importe != 0) ||
	    (cargoospim != null && cargoospim != '' && cargoospim != 0) ||
	    (cargops != null && cargops != '' && cargops != 0) ||
	    (cargoimesa != null && cargoimesa != '' && cargoimesa != 0) ||
	    (reconocidoSSS != null && reconocidoSSS != '' && reconocidoSSS != 0)
	);

	if (tieneDatosAreaMedica) {
	    if (recuperableSur == 0) {
	        alert('Debe seleccionar el campo Recuperable');
	        return false;
	    }

	    //validación de montos
	    if (!ValidaMontos()) {       
	        return false;
	    }
	}
	
	
	if (estado == 4 && chk_amparo == true ){
		//Si esta en estado inconsistente y es amparo permitimos grabar sin datos de comprobante
		flagAmparo = true;
	}
    
 	if (jQuery("#<%= reclamoPortletNamespace %>nom_seleccionado").val()==''){
		  alert('Debe seleccionar el sector');
		  return false;
	}	
	if (jQuery("#<%= reclamoPortletNamespace %>nom_seleccionado").val()==1){		 
		if (jQuery('#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro').val()<1  ) {
		 	alert('Debe seleccionar la prestación');
		 	return false;
		} 	
	    if(nombre_prestacion==null || nombre_prestacion==''){
			  alert('Debe seleccionar la prestación');
			  return false;
		}
			
	}else{		
		if (jQuery('#<%= reclamoPortletNamespace %>troquel').val()<1) {
			alert('Debe seleccionar el medicamento');
			return false;
		}	
		if ( nombre_medicamento==null || nombre_medicamento=='') {
			alert('Debe seleccionar el medicamento');
			return false;
		}
	}    
	
	
	var sector=jQuery('#<%= reclamoPortletNamespace %>sector').val();

    var fechaPrestacionDia='';
    var fechaPrestacionMes='';
    var fechaPrestacionAnio='';
	 fechaPrestacionDia=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDia').val(); 

    if (fechaPrestacionDia==null || fechaPrestacionDia==0 || fechaPrestacionDia=='' ){
    	 fechaPrestacionDia=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDiaFarmacia').val(); 
         fechaPrestacionMes=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionMesFarmacia').val();
         fechaPrestacionAnio=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionAnioFarmacia').val();
    }else{
        fechaPrestacionDia=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDia').val(); 
        fechaPrestacionMes=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionMes').val();
        fechaPrestacionAnio=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionAnio').val();
    }
	
	

	if (frecuencia=="SELECCIONE"){
    	frecuencia="";    
	}
    
    var frecuenciacontrol =document.getElementById("<%= reclamoPortletNamespace %>frecuencia");
    if (flagAmparo == false && frecuenciacontrol.selectedIndex==0){
    		alert('Debe seleccionar la frecuencia correspondiente.');
    		return false ;
    }
    
	if (flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT')  && cpbteLetra==''){
		  alert('Debe seleccionar la letra del comprobante');
		  return false;
	}	
    
    if(flagAmparo == false && (importeFC==null || importeFC==0)){
    	alert('Debe ingresar el importe de la Factura.');
		return false ;
    }
    
    if(flagAmparo == false && (cpbteCuit==null || cpbteCuit=='')){
    	alert('Debe ingresar el CUIT del Comprobante');
		return false ;
    }
    
    
    if(flagAmparo == false && (cpbteCuitSucursal==null || cpbteCuitSucursal=='')){
    	alert('Debe ingresar la sucursal del CUIT del Comprobante');
		return false ;
    }
    
    
    if(flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT')  && (cpbteSucursal==null || cpbteSucursal=='')){
    	alert('Debe ingresar la Sucursal del Comprobante');
		return false ;
    }
    
    if(flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT')  && (cpbteNro==null || cpbteNro=='')){
    	alert('Debe ingresar el Nro del Comprobante');
		return false ;
    }
    
    if (flagAmparo == false){
	    if(cpbteDia==null || cpbteDia==0 || cpbteDia=='' ||
	       cpbteMes==null || cpbteMes==-1 || cpbteMes=='' ||
	       cpbteAnio==null || cpbteAnio==0 || cpbteAnio==''){
	       alert('Debe ingresar la fecha del Comprobante');
	       return false;	
	    }
    }
    if(fechaPrestacionDia==null || fechaPrestacionDia==0 || fechaPrestacionDia=='' ||
    	fechaPrestacionMes==null || fechaPrestacionMes==-1 || fechaPrestacionMes=='' ||
    	fechaPrestacionAnio==null || fechaPrestacionAnio==0 || fechaPrestacionAnio==''){
    	alert('Debe ingresar la fecha de la Prestación');
    	return false;	
    }
    	    

    if(flagAmparo == false && (cpbteCantidad==null || cpbteCantidad==0 || cpbteCantidad=='')){
    	 alert('Debe ingresar la cantidad del Comprobante');
         return false;	
    }
    
    if(flagAmparo == false && (cpbteImporte==null || cpbteImporte==0 || cpbteImporte=='')){
   	 alert('Debe ingresar importe unitario del Comprobante');
        return false;	
    }
    
    if(flagAmparo == false && (importeFC==null || importeFC==0 || importeFC=='')){
      	 alert('Debe ingresar importe total del Comprobante');
           return false;	
    }
    
   
    var tipoPedidoControl =document.getElementById("<%= reclamoPortletNamespace %>tipopedido");
    if (tipoPedidoControl.selectedIndex==0){
		alert('Debe seleccionar el Tipo de Pedido.');
		return false ;
	}
    
    
    if (!ValidaDatosReclamo()){       
   		return false;
	}
    
    var cuil=jQuery('#<%= reclamoPortletNamespace %>cuil').val();
	var inte=jQuery('#<%= reclamoPortletNamespace %>inte').val();	
	
	var params = {"frecuencia":frecuencia,
			   "importe":importe,	
			   "cargoospim":cargoospim,
			   "cargops":cargops,
			   "cargoimesa":cargoimesa,
			   "troquel":troquel,
			   "prestacion":prestacion,
			   "tiponomenclador":tiponomenclador,
			   "nombre_medicamento":nombre_medicamento,
			   "nombre_prestacion":nombre_prestacion,
			   "tiponomnecladorprestacion":tiponomnecladorprestacion,
			   "recuperableSur":recuperableSur,
			   "cantidad":cantidad,
			   "observaciones":observaciones,
			   "cpbte_tipo":cpbteTipo,
			   "cpbte_nro":cpbteNro,
			   "cpbte_dia":cpbteDia,
			   "cpbte_mes":cpbteMes,
			   "cpbte_anio":cpbteAnio,
			   "cpbte_cantidad":cpbteCantidad,
			   "cpbte_importe":cpbteImporte,
			   "cpbte_cuit":cpbteCuit,
			   "cpbte_sucursal":cpbteSucursal,
			   "importeFC":importeFC,
			   "cpbte_cuit_sucursal":cpbteCuitSucursal,
			   "cpbte_letra":cpbteLetra,
			   "fecha_prestacion_dia":fechaPrestacionDia,
			   "fecha_prestacion_mes":fechaPrestacionMes,
			   "fecha_prestacion_anio":fechaPrestacionAnio,
			   "reconocidoSSS":reconocidoSSS,
			   "cuil":cuil,
			   "inte":inte
			   };	

	if(cpbteTipo != 'OTR' && cpbteTipo != 'AUT'){
	 if (!validarExisteComprobante(params)){   
	   	return false;
	 }
	}
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/lista_prestaciones_reclamos" /></portlet:renderURL>';

	jQuery('#<%= reclamoPortletNamespace %>lista_prestaciones_reclamos').load(url,params, function(){
									jQuery('#<%= reclamoPortletNamespace %>buscando').hide();            															
													  });			
	/* document.getElementById("<%= reclamoPortletNamespace %>sector").disabled = "disabled"; */	  
 	jQuery('#<%= reclamoPortletNamespace %>importe').val('');
 	jQuery('#<%= reclamoPortletNamespace %>total').val('');
 	jQuery('#<%= reclamoPortletNamespace %>cantidad').val('1');
 	jQuery('#<%= reclamoPortletNamespace %>cargoospim').val('');
 	jQuery('#<%= reclamoPortletNamespace %>cargops').val('');
 	jQuery('#<%= reclamoPortletNamespace %>cargoimesa').val('');
 	jQuery('#<%= reclamoPortletNamespace %>reconocidoSSS').val('');
 	jQuery('#<%= reclamoPortletNamespace %>observacion_prestacion').val('');
	document.getElementById("<%= reclamoPortletNamespace %>frecuencia").selectedIndex = 0;
	jQuery('#<%= reclamoPortletNamespace %>troquel').val(""); // farmacia 
	jQuery('#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro').val("");// prestaciones medicas
	//jQuery('#<%= reclamoPortletNamespace %>recuperable_sur').attr('checked', false);
	document.getElementById("<%= reclamoPortletNamespace %>recuperable_sur").selectedIndex = 0;
	jQuery("#<%= reclamoPortletNamespace %>divBtnBuscaEntidad").show();

	
	jQuery('#<%= reclamoPortletNamespace %>comprobante_tipo').val('FCP');
	jQuery('#<%= reclamoPortletNamespace %>comprobante_nro').val('');
	jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteDia').val('');
	jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteMes').val('');
	jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteAnio').val('');
	jQuery('#<%= reclamoPortletNamespace %>cantidadFC').val('');
	jQuery('#<%= reclamoPortletNamespace %>importeUnitarioFC').val('');
	jQuery('#<%= reclamoPortletNamespace %>importeFC').val('');
	jQuery('#<%= reclamoPortletNamespace %>cuit_entidad').val('');
    jQuery('#<%= reclamoPortletNamespace %>sucursal_entidad').val('');
    jQuery('#<%= reclamoPortletNamespace %>entidad_').val('');
    jQuery('#<%= reclamoPortletNamespace %>comprobante_suc').val('');
    jQuery("#<%= reclamoPortletNamespace %>nombre_medicamento").val('');
    jQuery("#<%= reclamoPortletNamespace %>divBtnBuscaMedicamento").show();
    

	jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDiaFarmacia').val(''); 
    jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionMesFarmacia').val('');
    jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionAnioFarmacia').val('');
    
	jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDia').val(''); 
    jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionMes').val('');
    jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionAnio').val('');
    
	<%= reclamoPortletNamespace %>limpiarNomencladorAutocompletar();
	
    addprestacion=true;
    /* document.getElementById("<%= reclamoPortletNamespace %>tipopedido").disabled = true;  */    
    if (jQuery('#<%= reclamoPortletNamespace %>estado').val()==3){   // cerrado
    	jQuery('#<%= reclamoPortletNamespace %>montoPsPrestaciones').val(cargops); 
		/* validaFacturacionDirectayReintegro();  */    	
    }	                            
}   

function controlarEstadoCerrado() {

	var varCantRevisiones =
        parseInt(
            jQuery(
                "#<%= reclamoPortletNamespace %>cantrevisionesactivas"
            ).val(),
            10
        ) || 0;
	
	var  varDebitoTercerizadora = <%=debitoTercerizadora%>;
	
	
	
	
	
	// VERIFICAR SI EXISTE POR LO MENOS UN REGISTRO DE REVISION ACTIVO 	
	if (jQuery('#<%= reclamoPortletNamespace %>estado').val()==3){
		if (varCantRevisiones > 0 ){
			jQuery("#<%= reclamoPortletNamespace %>Cierre_Reclamo_Div").show();	
			if(varDebitoTercerizadora == true){
				jQuery("#<%= reclamoPortletNamespace %>debitoprestadora")[0].checked = true;

			}																												
		}else{
			alert("Debe agregar una Revisión");
			jQuery("#<%= reclamoPortletNamespace %>estado option[value="+estadoIni+"]").attr("selected",true);

		}
		/* validaFacturacionDirectayReintegro(); */		
	} else {
		jQuery("#<%= reclamoPortletNamespace %>Cierre_Reclamo_Div").hide();
		jQuery('#<%= reclamoPortletNamespace %>nroLote').val("");
	}	
}

/* function onOffControlesRequest(valor) {
	document.getElementById("<%= reclamoPortletNamespace %>fechaseccionalDia").disabled = valor;
	document.getElementById("<%= reclamoPortletNamespace %>fechaseccionalMes").disabled = valor;
	document.getElementById("<%= reclamoPortletNamespace %>fechaseccionalAnio").disabled = valor;
} */


function <%= reclamoPortletNamespace %>imprimirReclamo(){
		     
	window.location.href ="/pdfservlet/?accion=reclamoprestacional&idreclamo=<%=reclamoprestacional!=null?reclamoprestacional.getId_reclamo():0%>";
	
}


function ValidaDatosReclamo(){
	
	
	var respuesta=true;
	var codError='';	
	var cpbte_dia =  jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteDia').val();
	var cpbte_mes =  jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteMes').val();
	var cpbte_anio = jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteAnio').val();

	var sector=jQuery('#<%= reclamoPortletNamespace %>sector').val();
	
    var cpbteCuit=jQuery('#<%= reclamoPortletNamespace %>cuit_entidad').val();
    var tipopedido=jQuery('#<%= reclamoPortletNamespace %>tipopedido').val();


	var fecha_prestacion_dia='';
	var fecha_prestacion_mes='';
	var fecha_prestacion_anio='';
		
	    
	if (sector == 'FARMACIA'){
		 fecha_prestacion_dia=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDiaFarmacia').val(); 
		 fecha_prestacion_mes=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionMesFarmacia').val();
		 fecha_prestacion_anio=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionAnioFarmacia').val();
	}else{
		 fecha_prestacion_dia=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDia').val(); 
		 fecha_prestacion_mes=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionMes').val();
		 fecha_prestacion_anio=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionAnio').val();
	}
	
    var troquel= jQuery('#<%= reclamoPortletNamespace %>troquel').val();
    var prestacion= jQuery('#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro').val();    
    var tipoNomenclador =jQuery('#<%= reclamoPortletNamespace %>nom_seleccionado').val();
    var tipoNomencladorPrestacion =jQuery('#<%= reclamoPortletNamespace %>tiponomenclador').val();
 
	
     var baja =  jQuery('#<%= reclamoPortletNamespace %>baja_fecha').val();
    
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/validar_reclamo';
		
	 url +='&cpbte_dia='+cpbte_dia;
	 url +='&cpbte_mes='+cpbte_mes;
	 url +='&cpbte_anio='+cpbte_anio;
	 url +='&fecha_prestacion_dia='+fecha_prestacion_dia;
	 url +='&fecha_prestacion_mes='+fecha_prestacion_mes;
	 url +='&fecha_prestacion_anio='+fecha_prestacion_anio;
	 url +='&cpbteCuit='+cpbteCuit;
	 url +='&tipopedido='+tipopedido;
	 url +='&troquel='+troquel;
	 url +='&prestacion='+prestacion;
	 url +='&tiponomenclador='+tipoNomenclador;
	 url +='&tiponomencladorprestacion='+tipoNomencladorPrestacion;
	 url +='&baja='+baja;
	 
	 jQuery.ajax({   
		   url: url,
		   async: false,
		   success: function(data) {
			  var obj = jQuery.parseJSON(data);
			  codError = obj.codError;
	   		}
	   }); 
		   
	   if(codError == '1'){
	       alert('La fecha de la prestación no puede ser posterior');
		   respuesta=false;	   
		}
		   
		if(codError == '2'){
		   	alert('La fecha del comprobante no puede ser posterior');
			respuesta=false;
		 }
		if(codError == '3'){
		   	alert('Prestador CUIT ' +  cpbteCuit  + ' no se encuentra cargado para poder liquidar');
			respuesta=false;
		 }
		if(codError == '4'){
		   	alert('No existe Prestación en el nomenclador');
			respuesta=false;
		 }
		if(codError == '5'){
		   	alert('No existe medicamento en el nomenclador');
			respuesta=false;
		 }
	
		if(codError == '6'){
		   	alert('La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado');
			respuesta=false;
		 }
		
		if (codError == '7') {
		    alert('La fecha de prestación no puede ser posterior a la fecha de emisión');
		    respuesta = false;
		}
		
	return  respuesta;    
		
}




function ValidaDatosReclamoEditar(){
		
	var respuesta=true;
	var codError='';	
	var cpbte_dia =  jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteDiaEdicion').val();
	var cpbte_mes =  jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteMesEdicion').val();
	var cpbte_anio = jQuery('#<%= reclamoPortletNamespace %>fechaComprobanteAnioEdicion').val();

	    

	fecha_prestacion_dia=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionDiaEdicion').val(); 
	fecha_prestacion_mes=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionMesEdicion').val();
	fecha_prestacion_anio=jQuery('#<%= reclamoPortletNamespace %>fechaPrestacionAnioEdicion').val();
	
	var sector=jQuery('#<%= reclamoPortletNamespace %>sector').val();
	
    var tipopedido=jQuery('#<%= reclamoPortletNamespace %>tipopedido').val();

    var cpbteCuit=jQuery('#<%= reclamoPortletNamespace %>cuit_entidad_edicion').val();
	
    var troquel= jQuery('#<%= reclamoPortletNamespace %>troquel_edit').val();
    var prestacion= jQuery('#<%= reclamoPortletNamespace %>codigoSeguimiento_filtro_edit').val();    
    var tipoNomenclador =jQuery('#<%= reclamoPortletNamespace %>nom_seleccionado').val();
    var tipoNomencladorPrestacion =jQuery('#<%= reclamoPortletNamespace %>tiponomenclador').val();
    var baja =  jQuery('#<%= reclamoPortletNamespace %>baja_fecha').val();

	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/validar_reclamo';
		
	 url +='&cpbte_dia='+cpbte_dia;
	 url +='&cpbte_mes='+cpbte_mes;
	 url +='&cpbte_anio='+cpbte_anio;
	 url +='&fecha_prestacion_dia='+fecha_prestacion_dia;
	 url +='&fecha_prestacion_mes='+fecha_prestacion_mes;
	 url +='&fecha_prestacion_anio='+fecha_prestacion_anio;
	 url +='&cpbteCuit='+cpbteCuit;
	 url +='&tipopedido='+tipopedido;	 
	 url +='&troquel='+troquel;
	 url +='&prestacion='+prestacion;
	 url +='&tiponomenclador='+tipoNomenclador;
	 url +='&tiponomencladorprestacion='+tipoNomencladorPrestacion; 
	 url +='&baja='+baja;
	 
	 jQuery.ajax({   
		   url: url,
		   async: false,
		   success: function(data) {
			  var obj = jQuery.parseJSON(data);
			  codError = obj.codError;
	   		}
	   }); 
		   
	   if(codError == '1'){
	       alert('La fecha de la prestación no puede ser posterior');
		   respuesta=false;	   
		}
		   
		if(codError == '2'){
		   	alert('La fecha del comprobante no puede ser posterior');
			respuesta=false;
		}
		if(codError == '3'){
		   	alert('Prestador CUIT ' +  cpbteCuit  + ' no se encuentra cargado para poder liquidar');
			respuesta=false;
		}
		if(codError == '4'){
		   	alert('No existe Prestación en el nomenclador');
			respuesta=false;
		}
		if(codError == '5'){
		   	alert('No existe medicamento en el nomenclador');
			respuesta=false;
		}  
	
		if(codError == '6'){
		   	alert('La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado');
			respuesta=false;
		 }
		
		if (codError == '7') {
		    alert('La fecha de prestación no puede ser posterior a la fecha de emisión');
		    respuesta = false;
		}
		
	return  respuesta;    
		
}

function validarExisteComprobante( params ) {
    var resp=true;
	var respuesta=true;
    var rtaExisteCompro=false;
    var mensajeErrorOut='';
	
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/validar_existe_comprobante';
	
	    url +='&frecuencia='+params.frecuencia;
	    url +='&troquel='+params.troquel;
	    url +='&prestacion='+params.prestacion;
	    url +='&cpbte_tipo='+params.cpbte_tipo;
	    url +='&cpbte_nro='+params.cpbte_nro;
	    url +='&cpbte_dia='+params.cpbte_dia;
	    url +='&cpbte_mes='+params.cpbte_mes;
	    url +='&cpbte_anio='+params.cpbte_anio;
	    url +='&cpbte_cuit='+params.cpbte_cuit;  
	    url +='&cpbte_sucursal='+params.cpbte_sucursal;
		url +='&cpbte_cuit_sucursal='+params.cpbte_cuit_sucursal;
	    url +='&cpbte_letra='+params.cpbte_letra;
	    url +='&fecha_prestacion_dia='+params.fecha_prestacion_dia;
	    url +='&fecha_prestacion_mes='+params.fecha_prestacion_mes;
	    url +='&fecha_prestacion_anio='+params.fecha_prestacion_anio;
	    url +='&tiponomnecladorprestacion='+params.tiponomnecladorprestacion;
	    url +='&tiponomenclador='+params.tiponomenclador;
	    url +='&idRegistro='+params.idRegistro;
	    url +='&id_medicamento_edit='+params.id_medicamento_edit;
	    url +='&nombre_medicamento_edit='+params.nombre_medicamento_edit;
	    url +='&codigoSeguimiento_filtro_edit='+params.codigoSeguimiento_filtro_edit;
	    url +='&descripcionSeguimiento_filtro_edit='+params.descripcionSeguimiento_filtro_edit;
	    url +='&nom_seleccionado_edit='+params.nom_seleccionado_edit;
	    url +='&tipoNomenclador_edit='+params.tipoNomenclador_edit;
	    url +='&cuil='+params.cuil;
	    url +='&inte='+params.inte;
		   
    
	   jQuery.ajax({   
		   url: url,
		   async: false,
		   success: function(data) {
			  var obj = jQuery.parseJSON(data);
				resp = obj.existe;
				mensajeErrorOut = obj.mensajeError;
				rtaExisteCompro=(resp  === 'true');
	   		}
	   }); 
	   if(rtaExisteCompro){
		  alert('Ya existe una prestación en esa fecha para el mismo comprobante');
		  respuesta=false;
		   
	   }
	   
	   if(mensajeErrorOut != ''){
		   alert(mensajeErrorOut);
		   respuesta=false;
	   }
	   
       return  respuesta;    
	 
}


function evaluarOnSectorListaEnCero() { 

	jQuery('#<%= reclamoPortletNamespace %>cantprestacioneslista').val('0');
	document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").selectedIndex=0;
	seteaControlesFacturacionDirecta(false);
	
	
<%-- <%if (!esEdicion){%>
	document.getElementById("<%= reclamoPortletNamespace %>sector").disabled = "";
<%}%> --%>

}

function validarSiNumero(numero){	
	
	if (!/^([0-9])*$/.test(numero)  ){  //  Backspace, Delete keys
		return false 
	}else{
		return true 
	}	
}

function validaMonto(e, cantidad ){
	 
	tecla = (document.all) ? e.keyCode : e.which;//obtenemos el codigo ascii de la tecla	
	patron= new RegExp("^[0-9]+(\.)?[\d{1,2}]$","gi");
	    
		te = String.fromCharCode(tecla);//convertimos el codigo ascii a string
		if (tecla==8 || tecla==46 || tecla==0) return true;
		return validarSiNumero(te);	
		}
    



function verCrmContacto(idContSerial) {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.VIEW%>";
		params = params + '&idContactoSerial='+idContSerial;
		
		popupCRM = new Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true, width: 880, position:['center',30]});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_contacto_entry';
		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/editar_contacto_entry';
		</c:if>
		url = url + params;
		jQuery(popupCRM).load(url);	
	}
	


function validaMontosEdicion(){	
	
	/* var strimporte =   jQuery('#<%= reclamoPortletNamespace %>totalEdicion').val();

    var strcargoospim = jQuery('#<%= reclamoPortletNamespace %>cargoospimEdicion').val();
    var strcargops =   jQuery('#<%= reclamoPortletNamespace %>cargopsEdicion').val(); */

    //var importedouble = parseFloat(jQuery('#<%= reclamoPortletNamespace %>totalEdicion').val());
    var importedouble = parseFloat(jQuery('#<%= reclamoPortletNamespace %>totalEdicion').val().replace(",","."));
    
    var cargoospimdouble = parseFloat(jQuery('#<%= reclamoPortletNamespace %>cargoospimEdicion').val());
    var cargopsdouble = parseFloat(jQuery('#<%= reclamoPortletNamespace %>cargopsEdicion').val());
    var cargoimesadouble = parseFloat(jQuery('#<%= reclamoPortletNamespace %>cargoimesaEdicion').val());
    var reconocidoSSS = parseFloat(jQuery('#<%= reclamoPortletNamespace %>reconocidoSSSEdicion').val());
    var estado =jQuery("#<%= reclamoPortletNamespace %>estado").val();
    var recuperable  =  jQuery('#<%= reclamoPortletNamespace %>recuperable_surEdicion').val();

    var importeFC = parseFloat(jQuery('#<%= reclamoPortletNamespace %>importeFC').val());
    var importeFCEdicion = parseFloat(jQuery('#<%= reclamoPortletNamespace %>importeFC_edicion').val());
    if(isNaN(importeFC)) {
//	jQuery('#<%= reclamoPortletNamespace %>importeFC').val();
	   importeFC=0;
    }
    if(isNaN(importeFCEdicion)) {
 //	jQuery('#<%= reclamoPortletNamespace %>importeFC_edicion').val();
	   importeFCEdicion=0;
    }


/*
importedouble= parseFloat(strimporte.replace(',','.'));
cargoospimdouble= parseFloat(strcargoospim.replace(',','.'));
cargopsdouble= parseFloat(strcargops.replace(',','.'));
*/
    if(isNaN(importedouble)) {		jQuery('#<%= reclamoPortletNamespace %>totalEdicion').val()  ; importedouble=0; 	}
    if(isNaN(cargoospimdouble)) {	jQuery('#<%= reclamoPortletNamespace %>cargoospimEdicion').val()  ; cargoospimdouble=0; 	}
    if(isNaN(cargopsdouble)) {		jQuery('#<%= reclamoPortletNamespace %>cargopsEdicion').val()  ; cargopsdouble=0; 	}
    if(isNaN(cargoimesadouble)) {		jQuery('#<%= reclamoPortletNamespace %>cargoimesaEdicion').val()  ; cargoimesadouble=0; 	}
    if(isNaN(reconocidoSSS)) {		jQuery('#<%= reclamoPortletNamespace %>reconocidoSSSEdicion').val()  ; reconocidoSSS=0; 	}
    
    var reconocidoSSST=0;
	if(recuperable==1){
    	reconocidoSSST=0;
    }else{
    	reconocidoSSST=reconocidoSSS;
    }
    
    total= Math.round((cargoospimdouble + cargopsdouble +cargoimesadouble + reconocidoSSST) * 100) / 100 ;
    
    var importeAreaMedica = Math.round((importedouble) * 100) / 100;
	var importeFactura = Math.round((importeFCEdicion) * 100) / 100;
	
	if( importeAreaMedica - importeFactura   >  .01){
		alert('El importe autorizado por el Area Médica no puede superar el Importe de la Factura. Area Medica: ' + importeAreaMedica +" - Comprobante: " +importeFactura);
    	return false; 
	}

//  valida la suma de los importes no debe superar el importe ingresado 

    if(total==0 && (importeFC>0 ||importeFCEdicion>0) && estado==3){
	   alert('Debe ingresar los importes en el Área Médica');
	   return false; 
    }

    /* if ( total > importedouble && estado==3){ */
    	
    if ( total > importedouble){	
	    alert('La suma de los importes ( OSPIM, tercerizadora ) no puede superar el monto en el importe ingresado.');
	    return false; 
    }

    /* if ( (total >importedouble || total<importedouble) && estado==3){ */
    if (total > importedouble || total < importedouble){	
	    alert('La suma de los importes ( OSPIM y tercerizadora) no puede diferir del monto en el total ingresado.');
	    return false; 
    }

/*
if ( (total < importedouble) && (myXOR(cargopsdouble,cargoospimdouble)) ){ 
	alert('La suma de los importes ( OSPIM y PS ) no puede ser menor al monto ingresado en importe.');
	return false; 
}
*/

    if ( document.getElementById("<%= reclamoPortletNamespace %>tipopedido").selectedIndex==1) { // tipo de pedido excepcion 
	  if (total!=importedouble && estado==3){
		alert('El importe total de la prestación debe coincidir con la suma de cargo Ospim y cargo tercerizadora');
		return false;
	  }
    }
    
   
    if(recuperable==2){
    	if(reconocidoSSS>0){
    	   	   alert('El importe reconocido debe estar vacío');
    	   	   jQuery('#<%= reclamoPortletNamespace %>reconocidoSSSEdicion').val('');
        	   return false;
    	}
    }else{
    	if(reconocidoSSS==0){
    		alert('El importe reconocido debe ser mayor a cero');
        	return false; 
    	}else if ( reconocidoSSS > importedouble){	
    	    alert('El importe Reconocido no puede superar el monto en el importe ingresado.');
    	    return false; 
        }
    }
	
/*	
jQuery('#<%= reclamoPortletNamespace %>importeEdicion').val(importedouble);
jQuery('#<%= reclamoPortletNamespace %>cargoospimEdicion').val(cargoospimdouble);
jQuery('#<%= reclamoPortletNamespace %>cargopsEdicion').val(cargopsdouble);
*/	
	
	return true;
}
	
function ValidaMontos()
{
	var importeFC = parseFloat(jQuery('#<%= reclamoPortletNamespace %>importeFC').val());
	var importedouble = parseFloat(jQuery('#<%= reclamoPortletNamespace %>total').val());
	var cargoospimdouble = parseFloat(jQuery('#<%= reclamoPortletNamespace %>cargoospim').val());
	var cargopsdouble = parseFloat(jQuery('#<%= reclamoPortletNamespace %>cargops').val());
	var cargoimesadouble = parseFloat(jQuery('#<%= reclamoPortletNamespace %>cargoimesa').val());
	var reconocidoSSS = parseFloat(jQuery('#<%= reclamoPortletNamespace %>reconocidoSSS').val());
	var estado =jQuery("#<%= reclamoPortletNamespace %>estado").val();
	var recuperable  =  jQuery('#<%= reclamoPortletNamespace %>recuperable_sur').val();
	
	
	if(isNaN(importedouble)) {		jQuery('#<%= reclamoPortletNamespace %>total').val()  ; importedouble=0; 	}
	if(isNaN(cargoospimdouble)) {	jQuery('#<%= reclamoPortletNamespace %>cargoospim').val()  ; cargoospimdouble=0; 	}
	if(isNaN(cargopsdouble)) {		jQuery('#<%= reclamoPortletNamespace %>cargops').val()  ; cargopsdouble=0; 	}
	if(isNaN(cargoimesadouble)) {		jQuery('#<%= reclamoPortletNamespace %>cargoimesa').val()  ; cargoimesadouble=0; 	}
	if(isNaN(reconocidoSSS)) {		jQuery('#<%= reclamoPortletNamespace %>reconocidoSSS').val()  ; reconocidoSSS=0; 	}
	if(isNaN(importeFC)) {		jQuery('#<%= reclamoPortletNamespace %>importeFC').val()  ; importeFC=0; 	}
	
	var reconocidoSSST=0;
	if(recuperable==1){
    	reconocidoSSST=0;
    }else{
    	reconocidoSSST=reconocidoSSS;
    }
	
	
//	totalCargos= cargoospimdouble + cargopsdouble;
	totalCargos= Math.round((cargoospimdouble + cargopsdouble+cargoimesadouble +reconocidoSSST) * 100) / 100 ;
	
	
	var importeAreaMedica = Math.round((importedouble) * 100) / 100;
	var importeFactura = Math.round((importeFC) * 100) / 100;
	
	if( importeAreaMedica - importeFactura   >  .01){
		alert('El importe autorizado por el Area Médica no puede superar el Importe de la Factura. Area Medica: ' + importeAreaMedica +" - Comprobante: " +importeFactura);
    	return false; 
	}
	
	
	
	if ( totalCargos >importeFC && estado=='3' ){
    	alert('La suma de los importes ( OSPIM y Tercerizadora) no puede superar el Importe de la Factura.');
    	return false; 
    }
	
	
	if ( (totalCargos >importedouble || totalCargos<importedouble) && estado=='3'){
    	alert('La suma de los importes ( OSPIM y Tercerizadora ) no puede diferir del monto en el total ingresado.');
    	return false; 
    }
	
	if ( document.getElementById("<%= reclamoPortletNamespace %>tipopedido").selectedIndex==1) { // tipo de pedido excepcion 
		if (totalCargos!=importedouble && estado=='3'){
			alert('El importe total de la prestación debe coincidir con la suma de Cargo Ospim más Cargo Tercerizadora.');
			return false;
		}
	}
 
   if ( document.getElementById("<%= reclamoPortletNamespace %>tipopedido").selectedIndex==2) { // tipo de pedido reintegro
	    if (importedouble <totalCargos && estado=='3'){
			alert('El importe total de prestación debe coincidir con la suma de a Cargo Ospim más Cargo Tercerizadora');
			return false;
		}   
		if (totalCargos==0 && estado=='3'){
			alert(' la suma de a Cargo Ospim más a Cargo Tercerizadora debe ser mayor que cero.');
			return false;
		}
   }
   
  if(recuperable==2){
	   if(reconocidoSSS>0){
	   	   alert('El importe reconocido debe estar vacio');
	   	   jQuery('#<%= reclamoPortletNamespace %>reconocidoSSS').val('');
    	   return false;
	  } 
   }else{
	   if(reconocidoSSS==0){
   		   alert('El importe reconocido debe ser mayor a cero');
       	   return false;
   		
   	  }else if ( reconocidoSSS > importedouble){	
   	      alert('El importe Reconocido no puede superar el monto en el importe ingresado.');
   	      return false; 
   	  }
   }
   
   return true;
}


function validarevision()
{
	if (jQuery('#<%= reclamoPortletNamespace %>cantrevisionesactivas').val()<1){ // no hay revisiones activas 
		alert('Debe tener registrada por lo menos una revision activa.');			
		resp=false;
	}
}

function convertToUppercase(el) {
	  if(!el || !el.value) return;
	  el.value = el.value.toUpperCase();
	}
	
function myXOR(a,b) {
	var resp;
	respa= (a>0 && b>0);
	return ( respa );
	}

/*
function crit_busqueda() {
	  var input=document.getElementById('<%= reclamoPortletNamespace %>buscadorcie10buscador').value.toUpperCase();
	  var output=document.getElementById('<%= reclamoPortletNamespace %>cie_diez').options;
	  var dato;       
      pos=jQuery('#<%= reclamoPortletNamespace %>posforcie10').val();
      for(var i=pos;i<document.getElementById("<%= reclamoPortletNamespace %>cie_diez").options.length ;i++) {
		  dato = output[i].text;		  
		  if(dato.indexOf(input)>-1){
		        output[i].selected=true;		        
		        jQuery('#<%= reclamoPortletNamespace %>codigoCie10').val(output[i].value);
		        jQuery('#<%= reclamoPortletNamespace %>posforcie10').val(++i);
		        return false;
		      }		 
      } 
      
      if (output[0].selected){
    	  alert('No se encontro el dato buscado.')  
      }     else{
    	  alert('Se termino de recorrer al lista.');
    	  
    	  
      }  
      jQuery('#<%= reclamoPortletNamespace %>posforcie10').val(0);
	}
*/
function enterTecla(e){
	tecla = (document.all) ? e.keyCode : e.which;//obtenemos el codigo ascii de la tecla	
	if (tecla==13) {
		crit_busqueda();
	}else{
		jQuery('#<%= reclamoPortletNamespace %>posforcie10').val(0);
	} 

}

function aplicaEstiloBordeRojoDatosObligatorio() { 
	// borde rojo en datos obligatorios
	color="#ff9999"
	jQuery("#<%= reclamoPortletNamespace %>fechaospimMes").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>fechaospimAnio").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>fechaospimDia").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>estado").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>sector").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>tipopedido").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>fecharevisionMes").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>fecharevisionAnio").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>fecharevisionDia").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>resolucion").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>justificacionmedica").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>frecuencia").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>importe").css("borderColor",color);
	jQuery("#<%= reclamoPortletNamespace %>mensajerevisionefectuada").css("borderColor",color);

}

function calculatotal(){

	importe=jQuery("#<%= reclamoPortletNamespace %>importe").val();
	cantidad=jQuery("#<%= reclamoPortletNamespace %>cantidad").val()
	total= importe * cantidad  ;
	jQuery("#<%= reclamoPortletNamespace %>total").val(Math.round(total.toFixed(2) * 100)/100);
	//jQuery("#<%= reclamoPortletNamespace %>total").val(total.toFixed(2));

}

function seleccionaCamposCieDiez(codigo,descripcion ){
	jQuery('#<%= reclamoPortletNamespace %>codigoCie').val(codigo);
	jQuery('#<%= reclamoPortletNamespace %>detalleCie').val(descripcion);
	jQuery('#<%= reclamoPortletNamespace %>codigoCie10').val(codigo);
}	

<%if (reclamoprestacional != null  &&   reclamoprestacional.getCodigoCie10()!=null &&  ! reclamoprestacional.getCodigoCie10().equals("")  ) {%>
<%= reclamoPortletNamespace %>buscarCieCodigo(); 
<%}%>

function limpiaCamposBusquedaCieDiez(){
	jQuery('#<%= reclamoPortletNamespace %>codigoCie10').val("");
}

/* function validaFacturacionDirectayReintegro(){
	document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").selectedIndex=0;	
	jQuery('#<%= reclamoPortletNamespace %>tipogestion').val(0);
	seteaControlesFacturacionDirecta(false);
	if (jQuery('#<%= reclamoPortletNamespace %>montoPsPrestaciones').val()>0 && jQuery('#<%= reclamoPortletNamespace %>montoPsPrestaciones').val()!="" ){// forzar facturacion directa o reintegro
		
		if (document.getElementById("<%= reclamoPortletNamespace %>tipopedido").selectedIndex==1){ // excepcion 
			document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").selectedIndex=2;
			jQuery('#<%= reclamoPortletNamespace %>tipogestion').val(3); // facturacion directa 
			seteaControlesFacturacionDirecta(true);
		}	
 		if (document.getElementById("<%= reclamoPortletNamespace %>tipopedido").selectedIndex==2){ // reintegro
			validaReintegro();			
	}
}
} */


/* function validaReintegro(){
		document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").selectedIndex=3;		
	    document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").disabled = true;
		jQuery('#<%= reclamoPortletNamespace %>tipogestion').val(4); // reintegro 
} */

function seteaControlesFacturacionDirecta(estadoTrueFalse){
	document.getElementById("<%= reclamoPortletNamespace %>incluido_convenio_gerenciadora").checked = estadoTrueFalse;
	/* document.getElementById("<%= reclamoPortletNamespace %>incluido_convenio_gerenciadora").disabled = estadoTrueFalse; */
	document.getElementById("<%= reclamoPortletNamespace %>debitoprestadora").checked =estadoTrueFalse;
	/*  document.getElementById("<%= reclamoPortletNamespace %>debitoprestadora").disabled = estadoTrueFalse; */	
	/*  document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").disabled = estadoTrueFalse;*/
}
function desactivaCheckCierre(){
	seteaControlesFacturacionDirecta(false);
	document.getElementById("<%= reclamoPortletNamespace %>dosporciento").checked =false;
	document.getElementById("<%= reclamoPortletNamespace %>dosporciento").disabled = true;
}

/* function habilitarControlesCierre() {
	document.getElementById("<%= reclamoPortletNamespace %>sector").disabled =false;  
	document.getElementById("<%= reclamoPortletNamespace %>tipopedido").disabled =false; 
	document.getElementById("<%= reclamoPortletNamespace %>debitoprestadora").disabled =false; 
	document.getElementById("<%= reclamoPortletNamespace %>incluido_convenio_gerenciadora").disabled =false; 
	document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").disabled =false;
} */


function abreAutorizacion(){
	
	 window.open('<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="tabs1" value="autorizaciones-prestacionales"/><portlet:param name="redirect" value="#"/></portlet:renderURL>',
	         'Autorizaciones', 'height=800, menubar=no, resizable=yes,scrollbars=yes, status=no, toolbar=no, width=1200');  
}

function calculatotalFC(){

	importe=jQuery("#<%= reclamoPortletNamespace %>importeUnitarioFC").val();
	cantidad=jQuery("#<%= reclamoPortletNamespace %>cantidadFC").val();
	total= importe * cantidad  ;
	jQuery("#<%= reclamoPortletNamespace %>importeFC").val(Math.round(total.toFixed(2) * 100)/100);
/*	
	jQuery("#<%= reclamoPortletNamespace %>cantidad").val(cantidad);
	jQuery("#<%= reclamoPortletNamespace %>importe").val(importe);
	calculatotal();
	jQuery('#<%= reclamoPortletNamespace %>cargoospim').val(Math.round(total.toFixed(2) * 100)/100);
*/	
}


function traerDescripcion() {
	var idIntegracion = jQuery('#<%= reclamoPortletNamespace %>integracion').val();
	var descripcionLarga;
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/getIntegracionDetalle&id_integracion='+idIntegracion;
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);						
			descripcionLarga = obj.DescripcionLarga;	
			jQuery("#integracion_desc").attr({alt: descripcionLarga,title: descripcionLarga});

		}
	});	
}

function cambiorecuperable(){
	
	try{
		var recuperable=jQuery('#<%= reclamoPortletNamespace %>recuperable_sur').val();
		if(recuperable==3 || recuperable==1){
			jQuery('#<%= reclamoPortletNamespace %>reconocidoSSS').attr('readonly', false);
		}else{
			jQuery('#<%= reclamoPortletNamespace %>reconocidoSSS').val(0);
			jQuery('#<%= reclamoPortletNamespace %>reconocidoSSS').attr('readonly', true);
		}
		
			

	}catch (err) {}	
	
}

function <%= reclamoPortletNamespace %>validarEmail() {
	var email = jQuery('#<%= reclamoPortletNamespace %>email').val();
/* 	var emailReg = /^([\da-z_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
 */	
 
/*  Se solicito quitar el 24/05/2016
	if(trim(email).length == 0){
		alert("El campo Email es Obligatorio");
		jQuery("#<%= reclamoPortletNamespace %>email").focus();
		return false;
	} */
	if(trim(email).length == 0){
		return true;
	}
	var expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	
	if ( !expr.test(email) ){
	    alert("Error: La dirección de correo " + email + " es incorrecta.");
	    jQuery("#<%= reclamoPortletNamespace %>email").focus();
		return false;
	}
	    
	/* if(trim(email).length > 0){	
		if( !emailReg.test( email ) ) {
			jQuery("#<%= reclamoPortletNamespace %>email").focus();
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

	var d_id_domicilio=jQuery("#<%= reclamoPortletNamespace %>id_domicilio").val();
    var d_id_provincia = jQuery("#<%= reclamoPortletNamespace %>provincia").val();
	var d_id_localidad = jQuery("#<%= reclamoPortletNamespace %>localidad").val();
	var d_calle = jQuery("#<%= reclamoPortletNamespace %>calle").val();
	var d_numero = jQuery("#<%= reclamoPortletNamespace %>numero").val();
	var d_piso = jQuery("#<%= reclamoPortletNamespace %>piso").val();
	var d_dpto = jQuery("#<%= reclamoPortletNamespace %>dpto").val();
	var d_cod_pos = jQuery("#<%= reclamoPortletNamespace %>cod_postal").val();
	var d_barrio = jQuery("#<%= reclamoPortletNamespace %>barrio").val();
	var d_cod_area_tel = jQuery("#<%= reclamoPortletNamespace %>cod_area_telefono").val();
	var d_telefono = jQuery("#<%= reclamoPortletNamespace %>telefono").val();
	//var d_cod_area_laboral = jQuery("#<%= reclamoPortletNamespace %>cod_area_tel_laboral").val();
	//var d_laboral = jQuery("#<%= reclamoPortletNamespace %>tel_laboral").val();
	var d_cod_area_celu = jQuery("#<%= reclamoPortletNamespace %>cod_area_celular").val();
	var d_celular = jQuery("#<%= reclamoPortletNamespace %>celular").val();
	
	var d_email = jQuery("#<%= reclamoPortletNamespace %>email").val();
	var d_email_original = jQuery("#<%= reclamoPortletNamespace %>email_original").val();
	
//	var cuiltitular= jQuery('#<%= reclamoPortletNamespace %>cuil_titular').val();
	var cuiltitular= jQuery('#<%= reclamoPortletNamespace %>cuil').val();
	var integrante = jQuery("#<%= reclamoPortletNamespace %>inte").val();
	
	var idPar = jQuery("#<%= reclamoPortletNamespace %>idPar").val();
	if (idPar != "<%= WebKeysAfiliados.PARENTESCO_DEFAULT %>" &&
	    idPar != "<%= WebKeysAfiliados.CONYUGE_DEFAULT %>" &&
	    idPar != "<%= WebKeysAfiliados.CONCUBINO_DEFAULT %>") {
	  integrante = 0;
	}
	
	/*validamos los campos obligatorios*/
	if (trim(d_calle).length == 0){
		alert("Ingrese la calle del domicilio");
		jQuery('#<%= reclamoPortletNamespace %>calle').focus();
		return false;
	}
	
	if (
		 (trim(d_cod_area_tel) == '' && trim(d_telefono) != '') ||
		 (trim(d_cod_area_tel) != '' && trim(d_telefono) == '')
		){
		alert("El teléfono debe necesariamente tener el código de area y el número");
		jQuery('#<%= reclamoPortletNamespace %>telefono').focus();
		return false;
	}
	
	if(trim(d_cod_area_tel).startsWith('0')){
		alert("El código de area del teléfono no debe iniciar con cero");
		jQuery("#<%= reclamoPortletNamespace %>cod_area_telefono").focus();
		return false;
	}
	if(trim(d_telefono).startsWith('0')){
		alert("El número del teléfono no debe iniciar con cero");
		jQuery("#<%= reclamoPortletNamespace %>telefono").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_tel).length>0 || trim(d_telefono).length>0){
		if(trim(d_cod_area_tel).length+trim(d_telefono).length!=10){
			alert("La longitud del código de área + teléfono debe de ser de 10 caracteres");
			jQuery("#<%= reclamoPortletNamespace %>cod_area_telefono").focus();
			return false;
		}
	}
	/*
	if ((trim(d_cod_area_laboral) == '' && trim(d_laboral) != '') ||
		(trim(d_cod_area_laboral) != '' && trim(d_laboral) == '')
		){
		alert("El teléfono laboral debe necesariamente tener el código de area y el número");
		jQuery('#<%= reclamoPortletNamespace %>tel_laboral').focus();
		return false;
	}
	
	if(trim(d_cod_area_laboral).startsWith('0')){
		alert("El código de area laboral no debe iniciar con cero");
		jQuery("#<%= reclamoPortletNamespace %>cod_area_tel_laboral").focus();
		return false;
	}
	if(trim(d_laboral).startsWith('0')){
		alert("El número del teléfono laboral no debe iniciar con cero");
		jQuery("#<%= reclamoPortletNamespace %>tel_laboral").focus();
		return false;
	}
	
	if(trim(d_cod_area_laboral).length>0 || trim(d_laboral).length>0){
		if(trim(d_cod_area_laboral).length+trim(d_laboral).length!=10){
			alert("La longitud del código de área + teléfono laboral debe de ser de 10 caracteres");
			jQuery("#<%= reclamoPortletNamespace %>cod_area_tel_laboral").focus();
			return false;
		}
	}
	*/
	
	
	if(trim(d_cod_area_celu).startsWith('0')){
		alert("El código de area del celular no debe iniciar con cero");
		jQuery("#<%= reclamoPortletNamespace %>cod_area_celular").focus();
		return false;
	}
	if(trim(d_celular).startsWith('0')){
		alert("El número del celular no debe iniciar con cero");
		jQuery("#<%= reclamoPortletNamespace %>celular").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_celu).length>0 || trim(d_celular).length>0){
		if(trim(d_cod_area_celu).length+trim(d_celular).length!=10){
			alert("La longitud del código de área + celular debe de ser de 10 caracteres");
			jQuery("#<%= reclamoPortletNamespace %>cod_area_celular").focus();
			return false;
		}
	}
	
	
	
	if(!<%= reclamoPortletNamespace %>validarEmail()){
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
				jQuery("#<%= reclamoPortletNamespace %>divResultadoActualizarOK").show();
				jQuery("#<%= reclamoPortletNamespace %>divBotonActualizar").hide();
				Liferay.Popup.close(popupDomicilio); 
			}	 
		});
} 

function mostrarDomicilioAfiliado(){
	var cuil_titu= jQuery("#<%= reclamoPortletNamespace %>cuil").val();
	var inte= jQuery("#<%= reclamoPortletNamespace %>inte").val();
	var email;
	var actualizaDomicilio;
	
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliado_datos&cuil_titular=';
	   url += cuil_titu;
	   url += '&inte=' + inte;
		
 jQuery.ajax({   
 url: url,
 async:false,
 success: function(data){
	   var obj = jQuery.parseJSON(data);
	   email=obj.email;
	}});
	popupDomicilio= Liferay.Popup({title:"<liferay-ui:message key="detalle-domicilio" />",modal:true,width:950,height:330,fixedcenter:true});
	var url1 = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/actualiza_domicilio&cuil_titular='+cuil_titu+'&inte='+inte+'&cmd=view' +'&email='+encodeURI(email);
	jQuery(popupDomicilio).load(url1);
	
}

function <%= reclamoPortletNamespace %>actualizarAfiliadoPorFecha(diaId, mesId, anioId) {
	var diaPrest = jQuery("#<%= reclamoPortletNamespace %>" + diaId).val();
	var mesPrest = jQuery("#<%= reclamoPortletNamespace %>" + mesId).val();
	var anioPrest = jQuery("#<%= reclamoPortletNamespace %>" + anioId).val();

	if (diaPrest == "" || mesPrest == "" || anioPrest == "" || mesPrest == "-1") {
		return;
	}

	var mesReal = parseInt(mesPrest, 10) + 1;
	var fechaPrestacion = diaPrest + "/" + mesReal + "/" + anioPrest;

	jQuery("#<%= reclamoPortletNamespace %>fprest").val(fechaPrestacion);

	var cuil = jQuery("#<%= reclamoPortletNamespace %>cuil").val();
	var inte = jQuery("#<%= reclamoPortletNamespace %>inte").val();

	if (cuil != "" && inte != "") {
		<%= reclamoPortletNamespace %>buscarAfiliados_(fechaPrestacion);
	}
}

function <%= reclamoPortletNamespace %>actualizarFechaPrestacionAfiliado() {
	<%= reclamoPortletNamespace %>actualizarAfiliadoPorFecha(
		"fechaPrestacionDia",
		"fechaPrestacionMes",
		"fechaPrestacionAnio"
	);
}

function <%= reclamoPortletNamespace %>actualizarFechaPrestacionFarmaciaAfiliado() {
	<%= reclamoPortletNamespace %>actualizarAfiliadoPorFecha(
		"fechaPrestacionDiaFarmacia",
		"fechaPrestacionMesFarmacia",
		"fechaPrestacionAnioFarmacia"
	);
}

function <%= reclamoPortletNamespace %>actualizarAfiliadoPorFechaPrestacionEdicion() {
	<%= reclamoPortletNamespace %>actualizarAfiliadoPorFecha(
		"fechaPrestacionDiaEdicion",
		"fechaPrestacionMesEdicion",
		"fechaPrestacionAnioEdicion"
	);
}

jQuery("#<%= reclamoPortletNamespace %>fechaPrestacionDia").change(function(){
	<%= reclamoPortletNamespace %>actualizarFechaPrestacionAfiliado();
});

jQuery("#<%= reclamoPortletNamespace %>fechaPrestacionMes").change(function(){
	<%= reclamoPortletNamespace %>actualizarFechaPrestacionAfiliado();
});

jQuery("#<%= reclamoPortletNamespace %>fechaPrestacionAnio").change(function(){
	<%= reclamoPortletNamespace %>actualizarFechaPrestacionAfiliado();
});

jQuery("#<%= reclamoPortletNamespace %>fechaPrestacionDiaFarmacia").change(function(){
	<%= reclamoPortletNamespace %>actualizarFechaPrestacionFarmaciaAfiliado();
});

jQuery("#<%= reclamoPortletNamespace %>fechaPrestacionMesFarmacia").change(function(){
	<%= reclamoPortletNamespace %>actualizarFechaPrestacionFarmaciaAfiliado();
});

jQuery("#<%= reclamoPortletNamespace %>fechaPrestacionAnioFarmacia").change(function(){
	<%= reclamoPortletNamespace %>actualizarFechaPrestacionFarmaciaAfiliado();
});

jQuery(document).on("change", "#<%= reclamoPortletNamespace %>fechaPrestacionDiaEdicion", function(){
	<%= reclamoPortletNamespace %>actualizarAfiliadoPorFechaPrestacionEdicion();
});

jQuery(document).on("change", "#<%= reclamoPortletNamespace %>fechaPrestacionMesEdicion", function(){
	<%= reclamoPortletNamespace %>actualizarAfiliadoPorFechaPrestacionEdicion();
});

jQuery(document).on("change", "#<%= reclamoPortletNamespace %>fechaPrestacionAnioEdicion", function(){
	<%= reclamoPortletNamespace %>actualizarAfiliadoPorFechaPrestacionEdicion();
});


</script>

<style>

.cabeceraCaso {
	width: 1100px;
}

.seccionVerificarDomicilio {
  vertical-align: top;
  text-align: center;
  padding: 10px 5px;  
  border: none;
  box-shadow: none;
  background: transparent;
}

</style>
