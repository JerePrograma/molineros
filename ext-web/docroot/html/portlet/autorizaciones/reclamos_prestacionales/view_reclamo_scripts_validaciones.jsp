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
        <c:if test='<%="_CAI_1_".equals(reclamoPortletNamespace)%>'>
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

jQuery(document).bind(
    "change",
    function(event) {
        var target =
                event.target ||
                event.srcElement;

        if (!target) {
            return;
        }

        if (
            target.id ==
                    "<%= reclamoPortletNamespace %>"
                    + "fechaPrestacionDiaEdicion" ||
            target.id ==
                    "<%= reclamoPortletNamespace %>"
                    + "fechaPrestacionMesEdicion" ||
            target.id ==
                    "<%= reclamoPortletNamespace %>"
                    + "fechaPrestacionAnioEdicion"
        ) {
            <%= reclamoPortletNamespace %>
                actualizarAfiliadoPorFechaPrestacionEdicion();
        }
    }
);
aplicaEstiloBordeRojoDatosObligatorio();
</script>
