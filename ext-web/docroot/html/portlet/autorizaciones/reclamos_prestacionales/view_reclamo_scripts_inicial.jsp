<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto" %>
<%
String reclamoPortletNamespace =
        (String) request.getAttribute(
                "rp.view.scripts.namespace"
        );

ReclamoPrestacional reclamoprestacional =
        (ReclamoPrestacional) request.getAttribute(
                "rp.view.scripts.reclamo"
        );

String cmd =
        (String) request.getAttribute(
                "rp.view.scripts.cmd"
        );

boolean reclamoPersistido =
        Boolean.TRUE.equals(
                request.getAttribute(
                        "rp.view.scripts.persistido"
                )
        );

boolean esEdicion =
        Boolean.TRUE.equals(
                request.getAttribute(
                        "rp.view.scripts.edicion"
                )
        );

Object cantPrestacionesObj =
        request.getAttribute(
                "rp.view.scripts.cantPrestaciones"
        );

int cantprestacioneslista =
        cantPrestacionesObj instanceof Integer
                ? ((Integer) cantPrestacionesObj).intValue()
                : 0;

Object cantRevisionesObj =
        request.getAttribute(
                "rp.view.scripts.cantRevisiones"
        );

int cantRevisiones =
        cantRevisionesObj instanceof Integer
                ? ((Integer) cantRevisionesObj).intValue()
                : 0;

String caso_vinculado =
        String.valueOf(
                request.getAttribute(
                        "rp.view.scripts.casoVinculado"
                ) != null
                        ? request.getAttribute(
                                "rp.view.scripts.casoVinculado"
                        )
                        : "0"
        );

Object resolucionAutorizadoObj =
        request.getAttribute(
                "rp.view.scripts.resolucionAutorizado"
        );

ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO
        resolucionAutorizado =
                resolucionAutorizadoObj
                        instanceof
                        ReclamoPrestacional
                                .ESTADOSEVALUACIONRECLAMO
                        ? (ReclamoPrestacional
                                .ESTADOSEVALUACIONRECLAMO)
                                resolucionAutorizadoObj
                        : ReclamoPrestacional
                                .ESTADOSEVALUACIONRECLAMO
                                .SINVALOR;

boolean debitoTercerizadora =
        Boolean.TRUE.equals(
                request.getAttribute(
                        "rp.view.scripts.debitoTercerizadora"
                )
        );

boolean handoffReclamoComprasValido =
        Boolean.TRUE.equals(
                request.getAttribute(
                        "rp.view.scripts.handoffCompras"
                )
        );

Object contextoReclamoComprasObj =
        request.getAttribute(
                "rp.view.scripts.contextoCompras"
        );

ReclamoPrestacionalCompraContexto
        contextoReclamoCompras =
                contextoReclamoComprasObj
                        instanceof
                        ReclamoPrestacionalCompraContexto
                        ? (ReclamoPrestacionalCompraContexto)
                                contextoReclamoComprasObj
                        : null;
%>
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
</script>
