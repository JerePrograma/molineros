<%@ include file="/html/portlet/crm/init.jsp"%>
<%
    String portlet_name = ParamUtil.getString(request, "portlet_name");
    if(renderResponse.getNamespace().equals("_JUD_1_")){
	   portlet_name = "judicial";
    }else{
	   portlet_name = "afiliados";
    }

	String accion = (String) request.getAttribute(Constants.CMD);
	boolean esView = false;
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
	DocumentoLegalCRM reclamo = null;
	
	if(esView){
		reclamo = (DocumentoLegalCRM) request.getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_VIEW);
	}else{
		reclamo = (DocumentoLegalCRM) request.getSession().getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_EDICION);
	}
	
	Afiliado crmafi = (Afiliado) request.getAttribute(WebKeysCrm.CRM_AFILIADO);
	Domicilio afiDomicilio = (Domicilio) request.getAttribute(WebKeysCrm.CRM_AFILIADO_DOMICILIO);

	Calendar vigenteFecha = CalendarFactoryUtil.getCalendar();
	Calendar bajaFecha = CalendarFactoryUtil.getCalendar();
	
	if(crmafi != null){
		vigenteFecha.setTime(crmafi.getVigen_fecha());
		if(crmafi.getBaja_fecha() != null){
			bajaFecha.setTime(crmafi.getBaja_fecha() );
		}
	/* }else{
		vigenteFecha.setTime(vigenteFecha.getTime()); */
	}else{
		crmafi = reclamo!=null?reclamo.getAfiliado():null;
		if(crmafi != null)
		vigenteFecha.setTime(crmafi.getVigen_fecha());
	}
	
	boolean tieneAntecedentesJudiciales = false;

    String colorAntecedenteJudicial = null;
    String codigoAntecedenteJudicial = "";
    String colorAntecedenteFondo = "";
    String colorAntecedenteBorde = "";
    String estiloAntecedenteJudicial = "";
    String estiloBadgeAntecedenteJudicial = "";
    String colorTextoBadgeAntecedenteJudicial = "#ffffff";

    if (crmafi != null) {

        tieneAntecedentesJudiciales = crmafi.getTieneAntecedentesJudiciales() == 1;
        colorAntecedenteJudicial = crmafi.getColorAntecedenteJudicial();
        codigoAntecedenteJudicial =
                crmafi.getCodigoAntecedenteJudicial() != null
                    ? crmafi.getCodigoAntecedenteJudicial().trim()
                    : "";
    }

    boolean tieneColorAntecedenteJudicial =
        colorAntecedenteJudicial != null
        && colorAntecedenteJudicial.trim().matches(
            "^#[0-9a-fA-F]{6}$"
        );

    if (tieneColorAntecedenteJudicial) {

        colorAntecedenteJudicial = colorAntecedenteJudicial.trim();

        int r = Integer.parseInt(colorAntecedenteJudicial.substring(1, 3),16);
        int g = Integer.parseInt(colorAntecedenteJudicial.substring(3, 5),16);
        int b = Integer.parseInt(colorAntecedenteJudicial.substring(5, 7),16);
        int luminosidad = (r * 299 + g * 587 + b * 114) / 1000;

        if (luminosidad > 160) {
            colorTextoBadgeAntecedenteJudicial = "#333333";
        } else {
            colorTextoBadgeAntecedenteJudicial = "#ffffff";
        }

        colorAntecedenteFondo ="rgba(" + r + "," + g + "," + b + ",0.16)";
        colorAntecedenteBorde ="rgba(" + r + "," + g + "," + b + ",0.45)";

        estiloAntecedenteJudicial =
            "background-color:" + colorAntecedenteFondo + ";" +
            "border:1px solid " + colorAntecedenteBorde + ";" +
            "border-left:6px solid " + colorAntecedenteJudicial + ";";

        estiloBadgeAntecedenteJudicial =
            "background:" + colorAntecedenteJudicial + ";" +
            "border:1px solid " + colorAntecedenteBorde + ";" +
            "color:" + colorTextoBadgeAntecedenteJudicial + ";";
    }

    boolean mostrarAntecedenteJudicial = tieneAntecedentesJudiciales || tieneColorAntecedenteJudicial;
%>

<style type="text/css">

    #<portlet:namespace />panelDatosAfiliadoLegal {
    position: relative;
}

	#<portlet:namespace />panelDatosAfiliadoLegal.afiliado-con-antecedentes-panel {
	    border-radius: 4px;
	    padding: 6px;
	    padding-top: 34px;
	}

	#<portlet:namespace />panelDatosAfiliadoLegal.afiliado-con-antecedentes-panel td,
	#<portlet:namespace />panelDatosAfiliadoLegal.afiliado-con-antecedentes-panel span,
	#<portlet:namespace />panelDatosAfiliadoLegal.afiliado-con-antecedentes-panel b,
	#<portlet:namespace />panelDatosAfiliadoLegal.afiliado-con-antecedentes-panel label {
	    color: #333333 !important;
	}

	#<portlet:namespace />panelDatosAfiliadoLegal.afiliado-con-antecedentes-panel label {
	    font-weight: bold;
	}

    #<portlet:namespace />panelDatosAfiliadoLegal.afiliado-con-antecedentes-panel input,
    #<portlet:namespace />panelDatosAfiliadoLegal.afiliado-con-antecedentes-panel select {
        background: #ffffff !important;
        color: #222222 !important;
        border: 1px solid #c9c9c9 !important;
    }

    #<portlet:namespace />panelDatosAfiliadoLegal.afiliado-con-antecedentes-panel input[readonly],
    #<portlet:namespace />panelDatosAfiliadoLegal.afiliado-con-antecedentes-panel select[disabled] {
        background: #f3f3f3 !important;
        color: #222222 !important;
        border: 1px solid #d0d0d0 !important;
    }

    #<portlet:namespace />antecedentesJudicialesLegal {
        position: absolute;
        top: 6px;
        right: 12px;
        z-index: 2;
        white-space: nowrap;
    }

    #<portlet:namespace />antecedentesJudicialesLegal span {
	    display: inline-block;
	    padding: 2px 8px;
	    border-radius: 4px;
	    font-weight: bold;
	    line-height: 1.2;
	}

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

<%if(crmafi!=null) {%>

<table class="cabeceraCaso" style="width:100%;">
	
    <tr>
        <td style="vertical-align:top; width:88%;">
            <fieldset class="block-labels">
     
	<legend><liferay-ui:message key='afiliado' /></legend>
	
	<div id="<portlet:namespace />panelDatosAfiliadoLegal" class="<%= mostrarAntecedenteJudicial? "afiliado-con-antecedentes-panel" : "" %>"
	    <% if(mostrarAntecedenteJudicial && !estiloAntecedenteJudicial.equals("")){ %>
	        style="<%=estiloAntecedenteJudicial%>"
	    <% } %>>

    <% if(mostrarAntecedenteJudicial){ %>

	    <div id="<portlet:namespace />antecedentesJudicialesLegal">
	
	        <span
	            <% if(!estiloBadgeAntecedenteJudicial.equals("")){ %>
	                style="<%=estiloBadgeAntecedenteJudicial%>"
	            <% } %>>
	            Antecedentes Judiciales<%
	                if (!codigoAntecedenteJudicial.equals("")) {
	            %> - <%=codigoAntecedenteJudicial%><%
	                }
	            %>
	        </span>
	
	        <a href="javascript:void(0)"
	           onclick="help(event, '<portlet:namespace />helpAntecedentesJudiciales')"
	           style="display:inline-block; margin-left:4px; vertical-align:middle;">
	
	            <img
	                style="height:16px; width:16px; vertical-align:middle;"
	                src="/html/images/help.png"
	                title="Referencia de antecedentes judiciales"
	                alt="Ayuda"
	            />
	
	        </a>
	
	    </div>
	
	<% } %>

<%
    String entidadAfiliado = "";
    int nroAfiliado = 0;

    if (crmafi.getId_ospim_baja_fecha() == null && crmafi.getId_ospim() > 0) {
        entidadAfiliado = "O.S.P.I.M.";
        nroAfiliado = crmafi.getId_ospim();

    } else if (crmafi.getId_amtima_baja_fecha() == null && crmafi.getId_amtima() > 0) {
        entidadAfiliado = "A.M.T.I.M.A.";
        nroAfiliado = crmafi.getId_amtima();

    } else if (crmafi.getId_uoma_baja_fecha() == null && crmafi.getId_uoma() > 0) {
        entidadAfiliado = "U.O.M.A.";
        nroAfiliado = crmafi.getId_uoma();
    }

    String tercerizadoraAfiliado = crmafi.getDesc_tercerizadora() != null ? crmafi.getDesc_tercerizadora() : "";
%>	
	
	<table class="lfr-table"
       style="border-collapse: separate; border-spacing: 5px; width: 100%;">
    <tr>
        <td>
            <label>Entidad:</label>
        </td>

        <td>
            <input type="text" value="<%=entidadAfiliado%>" readonly="readonly" style="width: 75px;" />
        </td>

        <td>
            <label>Nro. Afi.:</label>
        </td>

        <td>
            <input type="text" value="<%=nroAfiliado > 0 ? String.valueOf(nroAfiliado) : ""%>"
                readonly="readonly"
                style="width: 55px;" />
        </td>

        <td>
            <label>
                <liferay-ui:message key="cuil-titular" />
            </label>
        </td>

        <td>
            <input
                type="text"
                id="<portlet:namespace />cuil_titular"
                name="<portlet:namespace />cuil_titular"
                size="13"
                maxlength="11"
                value="<%=crmafi.getCuil_titular()%>"
                readonly="readonly" />
        </td>


        <td>
            <label>
                <liferay-ui:message key="integrante" />:
            </label>
        </td>

        <td>
            <input
                id="<portlet:namespace />integ"
                name="<portlet:namespace />integ"
                size="2"
                maxlength="2"
                type="text"
                value="<%=crmafi.getInteAsString()%>"
                readonly="readonly" />
        </td>


        <td>
            <label>T.Doc:</label>
        </td>

        <td>
            <select
                id="<portlet:namespace/>documento_tipo"
                name="<portlet:namespace/>documento_tipo"
                disabled="disabled">

                <%
                    for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {
                %>

                    <option
                        <%=crmafi.getDocumento_tipo().equals(tipoDoc)
                            ? "selected"
                            : ""%>
                        value="<%=tipoDoc%>">

                        <%=tipoDoc%>

                    </option>

                <% } %>

            </select>
        </td>


        <td>
            <label>
                <liferay-ui:message key="nro-documento" />:
            </label>
        </td>

        <td>
            <input
                type="text"
                id="<portlet:namespace />nroDoc"
                name="<portlet:namespace />nroDoc"
                size="9"
                maxlength="8"
                value="<%=crmafi.getDocu_numero()%>"
                readonly="readonly" />
        </td>

    </tr>

    <tr>
        <td>
            <label>
                <liferay-ui:message key="seccional" />:
            </label>
        </td>

        <td colspan="3">

            <liferay-util:include
                page="/html/portlet/afiliados/busqueda_seccional.jsp">

                <liferay-util:param
                    name="id_seccional"
                    value="<%=String.valueOf(
                        crmafi.getSeccional().getId()
                    )%>" />

                <liferay-util:param
                    name="seccional"
                    value="<%=crmafi.getSeccional().getDescripcion()%>" />

                <liferay-util:param
                    name="esEdicion"
                    value="false" />

            </liferay-util:include>

        </td>

        <td>
            <label>
                <liferay-ui:message key="plan" />:
            </label>
        </td>

        <td colspan="2">

            <input
                type="text"
                id="<portlet:namespace />plan_vig_desc"
                name="<portlet:namespace />plan_vig_desc"
                value="<%=crmafi.getUltimo_plan().getDescripcion()%>"
                readonly="readonly"
                style="width: 180px;" />

        </td>

        <td>
            <label>Tercerizadora:</label>
        </td>

        <td colspan="4">

            <input
                type="text"
                value="<%=tercerizadoraAfiliado%>"
                readonly="readonly"
                style="width: 190px;" />

        </td>

    </tr>

    <tr>
        <td>
            <label>
                <liferay-ui:message key="apellido" />:
            </label>
        </td>

        <td colspan="2">

            <input
                id="<portlet:namespace />apellido"
                name="<portlet:namespace />apellido"
                size="20"
                maxlength="100"
                type="text"
                value="<%=crmafi.getApellido()%>"
                readonly="readonly" />
        </td>

        <td>
            <label>
                <liferay-ui:message key="nombre" />:
            </label>
        </td>

        <td colspan="2">

            <input
                id="<portlet:namespace />nombre"
                name="<portlet:namespace />nombre"
                maxlength="100"
                type="text"
                value="<%=crmafi.getNombre()%>"
                readonly="readonly" />
        </td>

        <td>
            <label>
                <liferay-ui:message key="fecha-baja" />:
            </label>
        </td>

        <%if(crmafi.getBaja_fecha()!=null) {%>

            <td colspan="3">

                <liferay-ui:input-date
                    dayParam="bajaFechaDia"
                    dayValue="<%=bajaFecha.get(Calendar.DATE)%>"
                    monthParam="bajaFechaMes"
                    monthValue="<%=bajaFecha.get(Calendar.MONTH)%>"
                    yearParam="bajaFechaAnio"
                    yearValue="<%=bajaFecha.get(Calendar.YEAR)%>"
                    yearRangeStart="<%=bajaFecha.get(Calendar.YEAR)%>"
                    yearRangeEnd="<%=bajaFecha.get(Calendar.YEAR)%>"
                    firstDayOfWeek="<%=bajaFecha.getFirstDayOfWeek()%>"
                    disabled="<%=true%>" />

            </td>

        <%}else{%>

            <td colspan="3">

                <input
                    type="text"
                    value=""
                    readonly="readonly"
                    style="width: 120px;" />

            </td>

        <%}%>

    </tr>
    
    <tr>
        <td>
            <label>
                <liferay-ui:message key="vigente-desde" />:
            </label>
        </td>

        <td colspan="4">

            <liferay-ui:input-date
                dayParam="vigenteFechaDia"
                dayValue="<%=vigenteFecha.get(Calendar.DATE)%>"
                monthParam="vigenteFechaMes"
                monthValue="<%=vigenteFecha.get(Calendar.MONTH)%>"
                yearParam="vigenteFechaAnio"
                yearValue="<%=vigenteFecha.get(Calendar.YEAR)%>"
                yearRangeStart="<%=vigenteFecha.get(Calendar.YEAR)%>"
                yearRangeEnd="<%=vigenteFecha.get(Calendar.YEAR)%>"
                firstDayOfWeek="<%=vigenteFecha.getFirstDayOfWeek()%>"
                disabled="<%=true%>" />

        </td>

        <td colspan="4" align="right">

            <input
                type="button"
                value="<liferay-ui:message key="ver-aportes" />"
                onClick="<portlet:namespace />verAportes(false);" />

        </td>

    </tr>

</table>
	</div>
</fieldset>	
</td>


 <%if(afiDomicilio != null){ %>

        <td style="vertical-align:top; width:12%; padding-left:8px;">

            <fieldset
                class="block-labels seccionVerificarDomicilio"
                id="<portlet:namespace />seccionVerificarDomicilio">

                <table style="width:100%;">

                    <tr>
                        <td>&nbsp;</td>
                    </tr>

                    <tr>
                        <td style="text-align:center;">
                            <label>
                                Verificar datos<br/>
                                contacto:
                            </label>
                        </td>
                    </tr>

                    <tr>
                        <td>&nbsp;</td>
                    </tr>

                    <tr>
                        <td style="text-align:center;">

                            <div id="<portlet:namespace />divBotonActualizar">

                                <input
                                    type="button"
                                    value="Actualizar"
                                    onclick="javascript:mostrarDomicilioAfiliado(
                                        '<%=crmafi.getCuil_titular()%>',
                                        '<%=crmafi.getInte()%>'
                                    );" />

                            </div>

                        </td>
                    </tr>

                    <tr>
                        <td>&nbsp;</td>
                    </tr>

                    <tr>
                        <td style="text-align:center;">

                            <div
                                id="<portlet:namespace />divResultadoActualizarOK">

                                <p>
                                    <b>
                                        <liferay-ui:message key="crm-actualiza-domicilio" />
                                    </b>
                                </p>

                            </div>

                        </td>
                    </tr>

                </table>

            </fieldset>

        </td>

        <%} %>

    </tr>
</table>

<%} %>

<script type="text/javascript">
<% String cuil_titular = crmafi!=null&&!crmafi.getCuil_titular().isEmpty()?crmafi.getCuil_titular():new String("999999999"); %>
/* Extraido de la pagina otros_datos.jsp */
var popupAfill;
function <portlet:namespace />verAportes(cerrarAnterior){  
	var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();

	if(periodoDesdeMesAnio==null){			
		periodoDesdeMesAnio='012011';
	}
			
	if(cerrarAnterior=='true'){
		Liferay.Popup.close(popupAfill);
	}

    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/ver_aportes';
    url += '&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&cuil='+<%=cuil_titular%>;
    
	popupAfill = Liferay.Popup({title:"<liferay-ui:message key="aportes" />",modal:true,width:1300});
	jQuery(popupAfill).load(url);
	
}
</script>
