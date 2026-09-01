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
function <%= reclamoPortletNamespace %>agregarRevision() {

    var  revisionConCierre =false;

    if ( DatosRevisionOk())  {

        var resolucion = jQuery('#<%= reclamoPortletNamespace %>resolucion').val();

        var presentes = jQuery('#<%= reclamoPortletNamespace %>presentes').val();
        var respresolucion = jQuery('#<%= reclamoPortletNamespace %>respresolucion').val();
        var revisionFechaVtoDia = jQuery('#<%= reclamoPortletNamespace %>fecharevisionDia').val();
        var revisionFechaVtoMes = jQuery('#<%= reclamoPortletNamespace %>fecharevisionMes').val();
        var revisionFechaVtoAnio = jQuery('#<%= reclamoPortletNamespace %>fecharevisionAnio').val();

        var reclamoobservacion  = jQuery('#<%= reclamoPortletNamespace %>observacion_revision').val();

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



        var params = {
            "usr_presente":
                    presentes,

            "usr_resolucion":
                    resolucion,

            "usr_responsable_resolucion":
                    respresolucion,

            "fechaRevisionDay":
                    revisionFechaVtoDia,

            "fechaRevisionMonth":
                    revisionFechaVtoMes,

            "fechaRevisionYear":
                    revisionFechaVtoAnio,

            "observacion":
                    reclamoobservacion,

            "<%= WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE %>":
                    "<%= handoffReclamoComprasValido
                            ? contextoReclamoCompras.getNonce()
                            : "" %>"
        };


        var url =
            '<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/lista_revisiones_reclamo" /></portlet:actionURL>';

        if (resolucion.toUpperCase()!="AUTORIZADO"){
            if(confirm("Confirma el Cierre del Caso con el Rechazo en la revision ?")){

                    cierrePorRevisionRechazada = true;

                    jQuery(
                        "#<%= reclamoPortletNamespace %>cantrevisionesactivas"
                    ).val(1);

                    jQuery(
                        "#<%= reclamoPortletNamespace %>estado"
                    ).val("3");

                    document.getElementById("<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").disabled = false;

                    seteaControlesFacturacionDirecta(true);

                    jQuery("#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo").val("5");

                    var idgestion =
                        jQuery(
                            "#<%= reclamoPortletNamespace %>tipo_gestion_cierre_reclamo"
                        ).val();

                    jQuery('#<%= reclamoPortletNamespace %>tipogestion').val(idgestion);
                    jQuery('#<%= reclamoPortletNamespace %>reclamo_observacion_cierre').val('RECHAZO DE LA PRESTACION EN LA REVISION.');

                    tipoGestionCierreReclamo();
                    controlarEstadoCerrado();

                    revisionConCierre = true;
                    desactivaCheckCierre();

             }else{
                    return false;
            }
        }

         jQuery(
             '#<%= reclamoPortletNamespace %>lista_revisiones'
         ).load(
             url,
             params,
             function(responseText, status) {

                 jQuery(
                     '#<%= reclamoPortletNamespace %>buscando'
                 ).hide();

                 if (status == "error") {
                     jQuery(
                         "#<%= reclamoPortletNamespace %>botonrevision"
                     ).show();

                     jQuery(
                         "#<%= reclamoPortletNamespace %>"
                                 + "mensajerevisionefectuada"
                     ).html(
                         ""
                     );

                     return;
                 }

                 jQuery(
                     "#<%= reclamoPortletNamespace %>botonrevision"
                 ).hide();

                 jQuery(
                     "#<%= reclamoPortletNamespace %>"
                             + "mensajerevisionefectuada"
                 ).html(
                     "Revisión Efectuada, el Sistema soporta "
                             + "solo una revisión activa (No de baja)."
                 );

                 jQuery(
                     '#<%= reclamoPortletNamespace %>resolucion'
                 ).val(
                     ''
                 );

                 jQuery(
                     '#<%= reclamoPortletNamespace %>presentes'
                 ).val(
                     ''
                 );

                 jQuery(
                     '#<%= reclamoPortletNamespace %>respresolucion'
                 ).val(
                     ''
                 );

                 document.getElementById(
                     "<%= reclamoPortletNamespace %>fecharevisionDia"
                 ).selectedIndex = 0;

                 document.getElementById(
                     "<%= reclamoPortletNamespace %>fecharevisionMes"
                 ).selectedIndex = 0;

                 document.getElementById(
                     "<%= reclamoPortletNamespace %>fecharevisionAnio"
                 ).selectedIndex = 0;

                 jQuery(
                     '#<%= reclamoPortletNamespace %>observacion_revision'
                 ).val(
                     ''
                 );

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
    var nom_seleccionado_edit =
    jQuery(
        "#<%= reclamoPortletNamespace %>nom_seleccionado_edit"
    ).val();
    var tipoNomenclador_edit =
    jQuery(
        "#<%= reclamoPortletNamespace %>tipoNomenclador_edit"
    ).val();


    if (nom_seleccionado_edit ==1){
        if (codigoSeguimiento_filtro_edit<1
                && codigoSeguimiento_filtro_edit!='0') {
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

    // Oculta el sector de edición.
    jQuery(
        "#<%= reclamoPortletNamespace %>"
        + "datos_edicion_prestacion"
    ).hide();

    // Habilita el buscador correspondiente al sector.
    manejarTipoSector();

    jQuery(
        "#<%= reclamoPortletNamespace %>"
        + "datos_prestacion_ingreso"
    ).show();

    <%= reclamoPortletNamespace %>limpiarNomencladorAutocompletar();

    onOffcombosestadosprestaciones(
        true
    );

    /*
     * El combo de estado puede no existir en un reclamo nuevo
     * originado desde Compras.
     */
    var tipoAccionPrestacion =
            document.getElementById(
                "<%= reclamoPortletNamespace %>"
                + "tipoaccionprestacion"
            );

    if (tipoAccionPrestacion != null) {
        var datos =
                tipoAccionPrestacion.value || "";

        var datasplit =
                datos.split("-");

        var idPrestacion =
                datasplit.length > 1
                        ? datasplit[1]
                        : "";

        if (idPrestacion != "") {
            var comboEstado =
                    document.getElementById(
                        "comboestadosreclamo"
                        + idPrestacion
                    );

            if (comboEstado != null) {
                comboEstado.selectedIndex = 0;
            }
        }

        tipoAccionPrestacion.value = "";
    }
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

    var varCantRevisiones = <%=cantRevisiones%>;

    var cantRevisionesPantalla = parseInt(
        jQuery(
            "#<%= reclamoPortletNamespace %>cantrevisionesactivas"
        ).val(),
        10
    );

    if (
        !isNaN(cantRevisionesPantalla) &&
        cantRevisionesPantalla > varCantRevisiones
    ) {
        varCantRevisiones = cantRevisionesPantalla;
    }

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
</script>
