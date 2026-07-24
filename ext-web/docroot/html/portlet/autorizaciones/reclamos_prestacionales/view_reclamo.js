(function(window, jQuery) {
var reclamoPrestacionalViewConfig = window.ReclamoPrestacionalViewConfig || {};
var reclamoPrestacionalNamespace = reclamoPrestacionalViewConfig.namespace || "";

var popupMD;

var popupDomicilio;

jQuery('#' + reclamoPrestacionalNamespace + 'divResultadoActualizarOK').hide();

jQuery('#' + reclamoPrestacionalNamespace + 'cantprestacioneslista').val(reclamoPrestacionalViewConfig.values.cantPrestaciones);
jQuery("#" + reclamoPrestacionalNamespace + "busqueda_prestaciones").hide();
jQuery("#" + reclamoPrestacionalNamespace + "busqueda_farmacia").hide();

var editorPrestacion = jQuery(
		"#" + reclamoPrestacionalNamespace + "datos_edicion_prestacion"
);
var ingresoPrestacion = jQuery(
		"#" + reclamoPrestacionalNamespace + "datos_prestacion_ingreso"
);

if (reclamoPrestacionalViewConfig.values.esBorradorCompras ||
		editorPrestacion.children().length) {

	editorPrestacion.show().attr("aria-hidden", "false");
	ingresoPrestacion.hide().attr("aria-hidden", "true");
} else {
	editorPrestacion.hide().attr("aria-hidden", "true");
	ingresoPrestacion.show().attr("aria-hidden", "false");
}
jQuery("#" + reclamoPrestacionalNamespace + "Cierre_Reclamo_Div").hide();
/* jQuery("#namespacebotoneditareclamo").hide(); */
jQuery("#" + reclamoPrestacionalNamespace + "lista_prestaciones_asociadas").hide();
jQuery("#" + reclamoPrestacionalNamespace + "lista_contactos_reclamo").hide();
jQuery("#" + reclamoPrestacionalNamespace + "justificacion_medica_reclamo").hide();
jQuery("#" + reclamoPrestacionalNamespace + "caso_vinculado").val(reclamoPrestacionalViewConfig.values.casoVinculado);
jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSS').attr('readonly', true);


var addprestacion=false;
var load =false;
var sectorIni='';
var estadoIni='';




jQuery(document).ready(function() {
	load = true;
	sectorIni = jQuery("#" + reclamoPrestacionalNamespace + "sector").val();
	estadoIni = jQuery("#" + reclamoPrestacionalNamespace + "estado").val();

	//jQuery('#namespaceobservacion_medica_div').hide();
	if ('EXCEPCION' ==  jQuery("#" + reclamoPrestacionalNamespace + "tipopedido").val()){
		traerDescripcion();
	}			 
	
	
	
	
	if (reclamoPrestacionalViewConfig.values.reclamoCerrado) {            
	
		    jQuery("#" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo option[value="+reclamoPrestacionalViewConfig.values.tipoGestionCierre +"]").attr("selected",true);
	
		    jQuery("#" + reclamoPrestacionalNamespace + "observacion_medica option[value="+reclamoPrestacionalViewConfig.values.idObservacionMedica +"]").attr("selected",true);
		    

	}
    tipoGestionCierreReclamo();
    filtrarLetraComprobante();
	integracionReclamo();
	manejarTipoSector();


});



jQuery("#" + reclamoPrestacionalNamespace + "sector").change(function(){
	
	try {	
   		var valor=jQuery('#' + reclamoPrestacionalNamespace + 'cantprestacioneslista').val();

   		
		if (valor >= 1 && load == true){
			
	        var params = "&" + reclamoPrestacionalViewConfig.values.actionParam + "=" + reclamoPrestacionalViewConfig.values.reclamoPrestacionalSeccional;
			
			var confirmar = false;
			confirmar=confirm ('Se eliminaran los ítems por no pertenecer al tipo correspondiente '+'\nDesea hacerlo?');
			if(confirmar){
				 var url = reclamoPrestacionalViewConfig.urls.borrarPrestaciones;
    			 url = url + params;
    			jQuery("#" + reclamoPrestacionalNamespace + "lista_prestaciones_reclamos").load(url);	
			}else{
				jQuery("#" + reclamoPrestacionalNamespace + "sector option[value="+sectorIni+"]").attr("selected",true);
			}	
			
		}
   		
	}
	catch (err) {
		alert('error manejarTipoSector ');
	}

});






jQuery("#" + reclamoPrestacionalNamespace + "integracion").change(function(){
	
	try {	

		traerDescripcion();
   		
	}
	catch (err) {
		alert('error integracion ');
	}

});

 
jQuery("#" + reclamoPrestacionalNamespace + "estado").change(function(){
	
	try {	
   		var estado =jQuery('#' + reclamoPrestacionalNamespace + 'estado').val();

   		var chk_amparo =jQuery("#" + reclamoPrestacionalNamespace + "chk_amparo").is(':checked');
   		
   		if (estado == 4 && chk_amparo == false ){
   			alert('Debe seleccionar la marca de Amparo ')	;
		
			jQuery("#" + reclamoPrestacionalNamespace + "estado option[value=1]").attr("selected",true);

   		}
	}
	catch (err) {
		alert('error estado ');
	}

});



jQuery("#" + reclamoPrestacionalNamespace + "tipopedido").change(function(){
	
	try {	
		 filtrarLetraComprobante();
		 
		 integracionReclamo();
	}
	catch (err) {
		alert('error tipopedido ');
	}

});


jQuery("#" + reclamoPrestacionalNamespace + "chk_amparo").change(function(){
	
	try {	
   		var estado =jQuery('#' + reclamoPrestacionalNamespace + 'estado').val();

   		var chk_amparo =jQuery("#" + reclamoPrestacionalNamespace + "chk_amparo").is(':checked');
   		
   		if (estado == 4 && chk_amparo == false){
   			alert ('No puede sacar la marca de aparo si el estado es Incompleto ');
   			jQuery("#" + reclamoPrestacionalNamespace + "chk_amparo").attr('checked', true);
   		}
			
	}catch (err) {
		alert('error chk_amparo ');
	}

});



jQuery("#" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo").change(function(){
	tipoGestionCierreReclamo();
	
});

jQuery("#" + reclamoPrestacionalNamespace + "observacion_medica").change(function(){
	
	try {	   		
   		jQuery("#" + reclamoPrestacionalNamespace + "reclamo_observacion_cierre").text('');
   		
	}
	catch (err) {
		alert('error observacion_medica text');
	}
});


function tipoGestionCierreReclamo(){
	try {	
   		var tipo_presentes = jQuery('#' + reclamoPrestacionalNamespace + 'presentes').val();
   		var tipo_resolucion = jQuery('#' + reclamoPrestacionalNamespace + 'tipo_gestion_cierre_reclamo').val();
		
		if ( tipo_resolucion == 5){   
			jQuery('#' + reclamoPrestacionalNamespace + 'observacion_medica_tr').show();
   		}else{
   			jQuery('#' + reclamoPrestacionalNamespace + 'observacion_medica_tr').hide();
   		}
	}
	catch (err) {
		alert('error observacion_medica ');
	}
}


function integracionReclamo(){
	try {	
		 if ('EXCEPCION' ==  jQuery("#" + reclamoPrestacionalNamespace + "tipopedido").val()){
			 jQuery('#integracion_label').show();
			 jQuery('#' + reclamoPrestacionalNamespace + 'integracion').show();
			 jQuery('#integracion_desc').show();
			 jQuery('#integracion_div').show();
		 }else {
			 jQuery('#integracion_label').hide();
			 jQuery('#' + reclamoPrestacionalNamespace + 'integracion').hide();
			 jQuery('#integracion_desc').show();
			 jQuery('#integracion_div').hide();


		 }	
	}
	catch (err) {
		alert('error integracion ');
	}
}


/* var data=jQuery('#namespaceestado').val();
document.getElementById("namespaceestadosel").value = data; */

jQuery("#" + reclamoPrestacionalNamespace + "idreclamoprestacion").val("0");
if (reclamoPrestacionalViewConfig.values.hasReclamo) {
jQuery("#" + reclamoPrestacionalNamespace + "idreclamoprestacion").val(reclamoPrestacionalViewConfig.values.idReclamo);
/* jQuery("#namespacebotoneditareclamo").show(); */
jQuery("#" + reclamoPrestacionalNamespace + "botonsavereclamo").hide();
      if (reclamoPrestacionalViewConfig.values.reclamoCerrado) {            
            jQuery("#" + reclamoPrestacionalNamespace + "Cierre_Reclamo_Div").show();
            jQuery("#" + reclamoPrestacionalNamespace + "botonrevision").hide();
          

            
      }      
manejarTipoPedidoCierre();

if (reclamoPrestacionalViewConfig.values.tieneResolucion) { 
	// oculta boton de agregar porque existe una evaluacion de rECHAZO o APROBACION no de baja
	jQuery("#" + reclamoPrestacionalNamespace + "botonrevision").hide();
	jQuery("#" + reclamoPrestacionalNamespace + "mensajerevisionefectuada").html("Revision Efectuada, el Sistema soporta solo una revision activa (No de baja).");
} 

}


if (!reclamoPrestacionalViewConfig.values.esEdicion) {
    /* jQuery("#namespacebotoneditareclamo").hide();   */  
    /* document.getElementById("namespacesector").disabled = "disabled"; */
    
    document.getElementById("" + reclamoPrestacionalNamespace + "reclamo_observacion_cierre").disabled = "disabled";
    
    jQuery("#" + reclamoPrestacionalNamespace + "botonrevision").hide();
    jQuery("#" + reclamoPrestacionalNamespace + "buttonaddprestacion").hide();    
    
    //document.getElementById("namespacebuscadorcie10buscador").disabled = "disabled";
       
    
    
}



function filtrarLetraComprobante() {
	var tipoPedido = jQuery("#" + reclamoPrestacionalNamespace + "tipopedido").val();
	var url = reclamoPrestacionalViewConfig.urls.filtrarLetraComprobante+tipoPedido;
	jQuery("#" + reclamoPrestacionalNamespace + "comprobante_letra").attr('disabled', 'disabled');
	
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			document.getElementById("" + reclamoPrestacionalNamespace + "comprobante_letra").length = 0;
			jQuery("#" + reclamoPrestacionalNamespace + "comprobante_letra").removeAttr('disabled');
			var obj = jQuery.parseJSON(data);
			jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_letra').html(data).fadeIn();

		}
	});
}






aplicaEstiloBordeRojoDatosObligatorio();





function reclamoPrestacional_buscarNomencladorAutocompletar(){
	var nombre_nomenclador=jQuery("#" + reclamoPrestacionalNamespace + "descripcionSeguimiento_filtro").val();
	var codigo_nomenclador=jQuery("#" + reclamoPrestacionalNamespace + "codigoSeguimiento_filtro").val();
    var tipoNomenclador=jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro").val();
    
    // Marca ReinLiq no se utiliza en esta busqueda
    var marcaReinliq=null;
	if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
        alert(reclamoPrestacionalViewConfig.messages.ingreseParametrosBusqueda); 
    }else {
    	if(popupMD==null)
    		popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
    	
    	
    	if(tipoNomenclador==8){
    		marcaReinliq=6;
    	}    

    	var esPrestMed = 0;
    	sector = jQuery("#" + reclamoPrestacionalNamespace + "sector").val();
    	if (sector == "PRESTACIONES MEDICAS")
    		esPrestMed = 1;
    		    	
	    var url = reclamoPrestacionalViewConfig.urls.buscarNomenclador;
	    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&tiponomenclador='+tipoNomenclador +'&codigonomenclador='+encodeURI(codigo_nomenclador)+'&soloActivos=true';
	    url += '&marcareinliq='+marcaReinliq+'&esPrestMed='+esPrestMed;
	    	   
	    jQuery(popupMD).load(url);
    }
}


function reclamoPrestacional_buscarNomencladorAutocompletar_edit(){
	var nombre_nomenclador=jQuery("#" + reclamoPrestacionalNamespace + "descripcionSeguimiento_filtro_edit").val();
	var codigo_nomenclador=jQuery("#" + reclamoPrestacionalNamespace + "codigoSeguimiento_filtro_edit").val();
    var tipoNomenclador=jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro_edit").val();
    tipoNomenclador = '0';   
	if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
        alert(reclamoPrestacionalViewConfig.messages.ingreseParametrosBusqueda); 
    }else {
    	if(popupMD==null)
    		popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
    	
	    var url = reclamoPrestacionalViewConfig.urls.buscarNomenclador;
	    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&tiponomenclador='+tipoNomenclador +'&codigonomenclador='+encodeURI(codigo_nomenclador)+'&soloActivos=true';
	    jQuery(popupMD).load(url);
    }
}


function reclamoPrestacional_limpiarNomencladorAutocompletar(){	
	jQuery("#" + reclamoPrestacionalNamespace + "descripcionSeguimiento_filtro").val('');
	jQuery("#" + reclamoPrestacionalNamespace + "codigoSeguimiento_filtro").val('');
	jQuery("#" + reclamoPrestacionalNamespace + "descripcionSeguimiento_filtro_edit").val('');
	jQuery("#" + reclamoPrestacionalNamespace + "codigoSeguimiento_filtro_edit").val('');
}

	 
 
function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
	jQuery('#' + reclamoPrestacionalNamespace + 'codigoSeguimiento_filtro').val(codigo);
	jQuery("#" + reclamoPrestacionalNamespace + "descripcionSeguimiento_filtro").val(descripcion);
	jQuery("#" + reclamoPrestacionalNamespace + "nom_seleccionado").val("1"); // selecciona el tipo de nomenclador	 
	jQuery('#' + reclamoPrestacionalNamespace + 'tipoNomenclador').val(tipoNomenclador);
	
	
	jQuery('#' + reclamoPrestacionalNamespace + 'codigoSeguimiento_filtro_edit').val(codigo);
	jQuery("#" + reclamoPrestacionalNamespace + "descripcionSeguimiento_filtro_edit").val(descripcion);
	jQuery("#" + reclamoPrestacionalNamespace + "nom_seleccionado_edit").val("1"); // selecciona el tipo de nomenclador	 
	jQuery('#' + reclamoPrestacionalNamespace + 'tipoNomenclador_edit').val(tipoNomenclador);
	
	Liferay.Popup.close(popupMD);

}

function pasarParametrosAParentNm(tipoNomenclador,codigo,descripcion) {	
	seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
    reclamoPrestacional_cerrarNm();
}


function reclamoPrestacional_cerrarDivNm(){
	jQuery("#divSeguimientoSur").hide("slow");
}

function reclamoPrestacional_cerrarNm(){
	reclamoPrestacional_cerrarDivNm();
	if(popupMD){
		Liferay.Popup.close(popupMD);
	}
}


function DatosRevisionOk(){
	 
	var dianro  = jQuery("#" + reclamoPrestacionalNamespace + "fecharevisionDia").val();
	var mesnro  = jQuery("#" + reclamoPrestacionalNamespace + "fecharevisionMes").val()  ;
	var anionro   = jQuery("#" + reclamoPrestacionalNamespace + "fecharevisionAnio").val();
	 
	  
	if (dia || mes || anio){
	   alert("Debe ingresar la fecha de Revisión");
		return false ;
	}
	if (dia || mes || anio ||  jQuery('#' + reclamoPrestacionalNamespace + 'resolucion').val()=='' ){
		   alert("Debe ingresar la resolución");
			return false ;
		}
		
	var resolucion   =document.getElementById("" + reclamoPrestacionalNamespace + "resolucion");
	if (resolucion.selectedIndex==0){
		alert('Debe seleccionar el tipo de resolucion de la lista.');
		return false ;
	}		
	
	var diaExist  = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fecharevisionDia").val()));
	var mesExist  = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fecharevisionMes").val()));
	var anioExist   = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fecharevisionAnio").val()));
	
	var dia  = jQuery('#' + reclamoPrestacionalNamespace + 'fechaospimDia').val();
	var mes  = jQuery("#" + reclamoPrestacionalNamespace + "fechaospimMes").val() ;
	var anio   = jQuery("#" + reclamoPrestacionalNamespace + "fechaospimAnio").val();
	
	var fechaOspim  = new Date(anio, mes ,dia);
	var fechaRevision  = new Date(anionro,mesnro, dianro);
	var t = Date.now();
	var hoy = new Date(t);
	    
    diff  = new Date(fechaRevision - fechaOspim);
    days  = diff/1000/60/60/24;     
    
    if (diaExist || mesExist || anioExist) {
    	alert('Error en la fecha de revision ingresada.');
    	return false;
    }
	if(days<0){
		alert('La fecha de revision no puede ser inferior a la fecha de Ingreso del Reclamo (Fecha Ospim).');
		return false;
	}
	
	diff  = new Date(hoy  - fechaRevision);
    days  = diff/1000/60/60/24;
	if(days<0){
		alert('La fecha de revision no puede ser superior a la fecha de hoy.');
		return false;
	}
	
	
	return true ;		
}

function ValidarDatosObligatorios(Edicion){

	var valor = 0;
	valor=jQuery('#' + reclamoPrestacionalNamespace + 'cantprestacioneslista').val();
	
	
	var dia  = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fechaospimDia").val()));
	var mes  = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fechaospimMes").val()));
	var anio   = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fechaospimAnio").val()));
	
	var dia1  = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fechaseccionalDia").val()));
	var mes1  = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fechaseccionalMes").val()));
	var anio1   = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fechaseccionalAnio").val()));	
	
	
	var dia2  = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fechacierreDia").val()));
	var mes2  = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fechacierreMes").val()));
	var anio2   = isNaN(parseInt(jQuery("#" + reclamoPrestacionalNamespace + "fechacierreAnio").val()));
	
	
	var msgs = ["Error en la fecha Ospim.", "Debe seleccionar el sector que inicia  el reclamo.", "Debe seleccionar el estado del reclamo.","Debe seleccionar al Afiliado asociado al reclamo.","Complete la Fecha Seccional o dejela en blanco","Debe seleccionar el tipo de Pedido"]; 
	var condiciones =[5];
	var controles  =[5];
		
	var tipoSelectsector  =document.getElementById("" + reclamoPrestacionalNamespace + "sector");
	var tipoSelectestado  =document.getElementById("" + reclamoPrestacionalNamespace + "estado");
	var tipoSelecttipopedido =document.getElementById("" + reclamoPrestacionalNamespace + "tipopedido");
	/* document.getElementById("namespacetipopedido").selectedIndex==0 */
	var cuil=jQuery('#' + reclamoPrestacionalNamespace + 'cuil').val();
	var inte=jQuery('#' + reclamoPrestacionalNamespace + 'inte').val();	
	
	
	
	var  resp=true;
	
	controles[0]=document.getElementById("" + reclamoPrestacionalNamespace + "fechaospimDia"); 	
	controles[1]=tipoSelectsector;
	controles[2]=tipoSelectestado; 	
	controles[3]=document.getElementById("" + reclamoPrestacionalNamespace + "cuil");	
	controles[4]=document.getElementById("" + reclamoPrestacionalNamespace + "fechaseccionalDia");
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
	var idgestion = jQuery('#' + reclamoPrestacionalNamespace + 'tipo_gestion_cierre_reclamo').val();

	var justificacion=jQuery('#' + reclamoPrestacionalNamespace + 'justificacionmedcica_reclamo').val();


	if (idgestion == 0  && jQuery('#' + reclamoPrestacionalNamespace + 'estado option:selected').text().trim() == 'CERRADO' ){
		alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
		document.getElementById("" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo").focus();
		return false;
	}

	/* if (idgestion==5){ */
	if (idgestion==5){
	/* 	var isDisabled = jQuery('#namespacedosporciento').is(':disabled');
	    if (!isDisabled) { */
			if(! confirm("Al seleccionar la opción RECHAZADO el sistema rechazará todas las prestaciones del caso, no podrá asociarlas a reintegros. Está seguro ?")){
				return false;
			/* } */
	    }
	}
		var respResolucion = document.getElementById("" + reclamoPrestacionalNamespace + "respresolucion");

		if ( jQuery('#' + reclamoPrestacionalNamespace + 'auditoriaadministrativa').val()!="Ok" ){ // auditoria administrativa

			if (justificacion.length ==0  && resp ){ // no hay revisiones activas
				alert('Tiene que ingresar la justificación médica del Caso para efectuar el Cierre del Caso.');
				jQuery('#' + reclamoPrestacionalNamespace + 'justificacionmedcica_reclamo').focus();
				resp=false;
			}
		}
		// validar si
		if (idgestion<1  && resp && jQuery('#' + reclamoPrestacionalNamespace + 'estado option:selected').text() == 'CERRADO' ){
			alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
			document.getElementById("" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo").focus();
			resp=false;
		}

			if ((dia2 || mes2 || anio2)  && resp )  {
				alert('Debe ingresar la fecha de Cierre del Reclamo');
				document.getElementById("" + reclamoPrestacionalNamespace + "fechacierreDia").focus();
				resp=false;
			}

		if(tipoSelecttipopedido == 3){ //si estado = cerrado
			if (jQuery('#' + reclamoPrestacionalNamespace + 'cantrevisionesactivas').val()<1  && resp ){ // no hay revisiones activas
				alert('Recuerde, debe tener registrada por lo menos una revisión activa para el cierre del caso!!!!.');
				resp=false;
			}
		}


// SI ES CIERRE DEL CASO NO SE CONTROLA SI SE DIERON DE BAJA TODAS LAS PRESTACIONES

	valor=jQuery('#' + reclamoPrestacionalNamespace + 'cantprestacioneslista').val();


    if (Edicion && addprestacion) {
    	if (valor <1   && resp){
    		alert('Debe tener ingresada por lo menos una prestación');
    		resp=false;
    	}
    }else{
    		if (valor <1  && resp ){

    		}
    }

    var integracion = jQuery("#" + reclamoPrestacionalNamespace + "integracion").val();
	 if ('EXCEPCION' ==  jQuery("#" + reclamoPrestacionalNamespace + "tipopedido").val()){
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
	 var baja =  jQuery('#' + reclamoPrestacionalNamespace + 'baja_fecha').val();
	 var url = reclamoPrestacionalViewConfig.urls.validarReclamoAfiliadoPrestaciones;
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


function reclamoPrestacional_saveReclamo() {

/* 	// para el alta
	habilitarControlesCierre();	 */

	if ( ValidarDatosObligatorios(false))  {

	/*esta chanchada es porque el action toma el id de cierre de tipogestion que es un hidden y no de tipo_gestion_cierre_reclamo*/
	var idgestion=jQuery('#' + reclamoPrestacionalNamespace + 'tipo_gestion_cierre_reclamo').val();
	jQuery('#' + reclamoPrestacionalNamespace + 'tipogestion').val(idgestion);

	var accionEnCurso = document[reclamoPrestacionalNamespace + "reclamo_fm"][reclamoPrestacionalNamespace + reclamoPrestacionalViewConfig.values.cmdParam].value;
	document[reclamoPrestacionalNamespace + "reclamo_fm"][reclamoPrestacionalNamespace + reclamoPrestacionalViewConfig.values.cmdParam].value=reclamoPrestacionalViewConfig.values.saveCommand;

	/* 	var chk_amparo=jQuery("#namespacechk_amparo").is(':checked');
		var chk_superintendencia=jQuery("#namespacechk_superintendencia").is(':checked');
		var chk_recuperable = jQuery("#namespacechk_recuperable").is(':checked');
		var chk_entramite = jQuery("#namespacechk_entramite").is(':checked');	 */

		var url = reclamoPrestacionalViewConfig.urls.editarReclamo;
		url = url + "&esDatosTab=true";
		document[reclamoPrestacionalNamespace + "reclamo_fm"].method = 'post';
		submitForm(document[reclamoPrestacionalNamespace + "reclamo_fm"], url);

	}
}

/* Cambia estado a Observado */
function reclamoPrestacional_volverEstadoObservado() {

	var confirmar = false;
	/* Recupera el Id del Reclamo */
	var idgestion=jQuery('#' + reclamoPrestacionalNamespace + 'id_reclamosel').val();

	confirmar=confirm ('Estas observando la precarga, la misma será devuelta ' +
			'a la seccional. ' + '\nEstas seguro?');

	if(confirmar) {
		popup = Liferay.Popup({title:reclamoPrestacionalViewConfig.messages.observacionInterna,modal:true,width:700});
		var url = reclamoPrestacionalViewConfig.urls.observar;
		url = url + "&idReclamo=" + idgestion;
		jQuery(popup).load(url);
	}
}

function reclamoPrestacional_editaReclamo(fromAutoriza) {

	if (fromAutoriza) {
		abreAutorizacion();
	}

	if ( ValidarDatosObligatorios(true))  {

	  /* var data=jQuery('#namespaceestado').val();
	  if ( document.getElementById("namespaceestadosel").value == data){
		 document.getElementById("namespaceestado").value="0";
	  } */

	 /*  if ( document.getElementById("namespacetipopedido").disabled = "disabled"){
		document.getElementById("namespacetipopedido").disabled = "";
	  } */

	  /*esta chanchada es porque el action toma el id de cierre de tipogestion que es un hidden y no de tipo_gestion_cierre_reclamo*/
		var idgestion=jQuery('#' + reclamoPrestacionalNamespace + 'tipo_gestion_cierre_reclamo').val()
		jQuery('#' + reclamoPrestacionalNamespace + 'tipogestion').val(idgestion);
	    //jQuery('#namespaceid_reclamosel').val(0);

	  var accionEnCurso = document[reclamoPrestacionalNamespace + "reclamo_fm"][reclamoPrestacionalNamespace + reclamoPrestacionalViewConfig.values.cmdParam].value;
	  document[reclamoPrestacionalNamespace + "reclamo_fm"][reclamoPrestacionalNamespace + reclamoPrestacionalViewConfig.values.cmdParam].value=reclamoPrestacionalViewConfig.values.updateCommand;

	  /* habilitarControlesCierre(); */




	  var url = reclamoPrestacionalViewConfig.urls.editarReclamo;
	  url = url + "&esDatosTab=true";
	  document[reclamoPrestacionalNamespace + "reclamo_fm"].method = 'post';


	  submitForm(document[reclamoPrestacionalNamespace + "reclamo_fm"], url);

	  /* onOffControlesRequest(true); */
	}
}


function reclamoPrestacional_reabrirReclamo(fromAutoriza) {

	if (fromAutoriza) {
		abreAutorizacion();
	}


/* 	  var data=jQuery('#namespaceestado').val();
	  if ( document.getElementById("namespaceestadosel").value == data){
		 document.getElementById("namespaceestado").value="0";
	  } */

	/*   if ( document.getElementById("namespacetipopedido").disabled = "disabled"){
		document.getElementById("namespacetipopedido").disabled = "";
	  } */

	  var accionEnCurso = document[reclamoPrestacionalNamespace + "reclamo_fm"][reclamoPrestacionalNamespace + reclamoPrestacionalViewConfig.values.cmdParam].value;
	  document[reclamoPrestacionalNamespace + "reclamo_fm"][reclamoPrestacionalNamespace + reclamoPrestacionalViewConfig.values.cmdParam].value=reclamoPrestacionalViewConfig.values.restoreCommand;

	  /* habilitarControlesCierre(); */

	  var url = reclamoPrestacionalViewConfig.urls.editarReclamo;
	  url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest' + "&esDatosTab=false";

	  document[reclamoPrestacionalNamespace + "reclamo_fm"].method = 'post';

	  submitForm(document[reclamoPrestacionalNamespace + "reclamo_fm"], url);

/* 	  onOffControlesRequest(true); */

}




function manejartipogestion(){

	/* var tipoGestionArray = jQuery('#namespacetipo_gestion_cierre_reclamo').val().split("|");	 */
	var idgestion = jQuery('#' + reclamoPrestacionalNamespace + 'tipo_gestion_cierre_reclamo').val();
	/* var idgestion =tipoGestionArray [0];	 */
	var sector=jQuery('#' + reclamoPrestacionalNamespace + 'sector').val();
	var nroLote=jQuery('#' + reclamoPrestacionalNamespace + 'nroLote').val();
	jQuery('#' + reclamoPrestacionalNamespace + 'tipogestion').val(idgestion);
	if("1"==idgestion && sector=="PRESTACIONES MEDICAS" && (nroLote==null || nroLote=="" || nroLote=="0")){

		 var url = reclamoPrestacionalViewConfig.urls.proponeLote;
			jQuery.ajax({
				url: url,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					jQuery('#' + reclamoPrestacionalNamespace + 'nroLote').val(obj.lote);
				}
			});
	}
	if("1"!=idgestion || sector!="PRESTACIONES MEDICAS"){
		jQuery('#' + reclamoPrestacionalNamespace + 'nroLote').val("");
	}



}


function manejarListaPresentes(){
	var tipoSelect  =document.getElementById("" + reclamoPrestacionalNamespace + "presenteslista");
	jQuery("#" + reclamoPrestacionalNamespace + "presentes").val(tipoSelect.value); // asigna el valor de la lista al control oculto
}


function cambioresolucion(){

	try{
		var tipoSelect  =document.getElementById("" + reclamoPrestacionalNamespace + "resolucion");
		var justificacion=jQuery('#' + reclamoPrestacionalNamespace + 'justificacionmedcica_reclamo').val();
		if  (tipoSelect.selectedIndex>0 && justificacion.length ==0  && document.getElementById("" + reclamoPrestacionalNamespace + "respresolucion").selectedIndex!=1){
				jQuery('#' + reclamoPrestacionalNamespace + 'justificacionmedcica_reclamo').focus();
				tipoSelect.selectedIndex=0;
				alert('Tiene que ingresar la Justificacion Medica del Caso para ingresar la revision.');
			}

	}catch (err) {}

}


function manejarTipoPedido(){
	var tipoPedido =document.getElementById("" + reclamoPrestacionalNamespace + "tipopedido");
	if ( tipoPedido.selectedIndex==0 ){
		alert("El tipo de pedido es obligatorio");
		document.getElementById("" + reclamoPrestacionalNamespace + "tipopedido").focus();
	}
	//if(tipoPedido.value!="EXTRACAPITA"){
	//	jQuery("#namespacecomprobante_letra").append(new Option("A", "A"));
	//}

}

function cambioTipoPedido(){
	var tipoSector =document.getElementById("" + reclamoPrestacionalNamespace + "sector");
	if(tipoSector.selectedIndex!=0){
		manejarTipoSector();
	}
}


function manejarTipoPedidoCierre(){
	var tipoPedido  = document.getElementById("" + reclamoPrestacionalNamespace + "tipopedido");
	jQuery('#' + reclamoPrestacionalNamespace + 'tipo_gestion_cierre_reclamo').html('');  //vacio lista opciones del select
/* 	jQuery("#namespacetipo_gestion_cierre_reclamo").append(new Option("SELECCIONE LA GESTION", "0"));
	document.getElementById("namespacetipopedido").selectedIndex==0 */
	jQuery("#" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo").append(new Option("SELECCIONE UNA OPCION", "0"));
	jQuery("#" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo option[value='0']").attr("selected", true);
	if(tipoPedido.value=="EXCEPCION"){
		jQuery("#" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo").append(new Option("FACTURACION DIRECTA", "3"));
		jQuery("#" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo").append(new Option("PAGADO POR MECANISMO INTEGRACION", "6"));
		/* jQuery("#namespacetipo_gestion_cierre_reclamo option[value='3']").attr("selected", true); //FACT. DIRECTA */
	}
	if(tipoPedido.value=="REINTEGRO"){
		jQuery("#" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo").append(new Option("REINTEGRO", "4"));
		/* jQuery("#namespacetipo_gestion_cierre_reclamo option[value='4']").attr("selected", true); //REINTEGRO */
	}
	if(tipoPedido.value=="EXTRACAPITA"){
		jQuery("#" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo").append(new Option("EXTRACAPITA", "1"));
		/* jQuery("#namespacetipo_gestion_cierre_reclamo option[value='1']").attr("selected", true); //EXTRACAPITA */
	}
	jQuery("#" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo").append(new Option("RECHAZADO", "5"));
}

function reclamoPrestacional_usaBuscadorMedicamentos() {
	var sector = jQuery('#' + reclamoPrestacionalNamespace + 'sector').val();
	var tipoPedido = jQuery('#' + reclamoPrestacionalNamespace + 'tipopedido').val();

	return sector == 'FARMACIA' && tipoPedido != 'EXCEPCION';
}

function manejarTipoSector(){
	var sector = jQuery('#' + reclamoPrestacionalNamespace + 'sector').val();
	var tipoPedido = jQuery('#' + reclamoPrestacionalNamespace + 'tipopedido').val();

	try {
		jQuery("#" + reclamoPrestacionalNamespace + "busqueda_prestaciones").show();
		jQuery("#" + reclamoPrestacionalNamespace + "busqueda_farmacia").hide();
		jQuery("#" + reclamoPrestacionalNamespace + "nom_seleccionado").val("1");

		jQuery('#' + reclamoPrestacionalNamespace + 'troquel').val("");
		jQuery('#' + reclamoPrestacionalNamespace + 'codigoSeguimiento_filtro').val("");
		jQuery('#' + reclamoPrestacionalNamespace + 'descripcionSeguimiento_filtro').val("");
		jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro").val("");

		if (reclamoPrestacional_usaBuscadorMedicamentos()) {
			jQuery("#" + reclamoPrestacionalNamespace + "busqueda_farmacia").show();
			jQuery("#" + reclamoPrestacionalNamespace + "busqueda_prestaciones").hide();
			jQuery("#" + reclamoPrestacionalNamespace + "nom_seleccionado").val("2");
			return;
		}

		if (sector == 'FARMACIA' && tipoPedido == 'EXCEPCION') {
			/*
			 * Produccion usa Codigo Presentado para EXCEPCION + FARMACIA.
			 * El tipo 9 limita la busqueda al nomenclador de farmacia.
			 */
			jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro").val("9");
			return;
		}

		if (sector == 'DISCAPACIDAD') {
			jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro").val("8");
		} else if (sector == 'ODONTOLOGIA') {
			jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro").val("1");
		} else if (sector == 'PRESTACIONES MEDICAS' || sector == 'LEGALES') {
			jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro").val("0");
		}
	}
	catch (err) {
		alert('error manejarTipoSector() ');
	}
}





function reclamoPrestacional_agregarRevision() {

	var  revisionConCierre =false;

	if ( DatosRevisionOk())  {

		var resolucion = jQuery('#' + reclamoPrestacionalNamespace + 'resolucion').val();

		var presentes = jQuery('#' + reclamoPrestacionalNamespace + 'presentes').val();
		var respresolucion = jQuery('#' + reclamoPrestacionalNamespace + 'respresolucion').val();
		var revisionFechaVtoDia = jQuery('#' + reclamoPrestacionalNamespace + 'fecharevisionDia').val();
		var revisionFechaVtoMes = jQuery('#' + reclamoPrestacionalNamespace + 'fecharevisionMes').val();
		var revisionFechaVtoAnio = jQuery('#' + reclamoPrestacionalNamespace + 'fecharevisionAnio').val();

		var observacionMedica = jQuery('#' + reclamoPrestacionalNamespace + 'observacion_medica').val();



		var reclamoobservacion  = jQuery('#' + reclamoPrestacionalNamespace + 'observacion_revision').val();
		var chk_amparo=jQuery("#" + reclamoPrestacionalNamespace + "chk_amparo").is(':checked');
		var chk_superintendencia=jQuery("#" + reclamoPrestacionalNamespace + "chk_superintendencia").is(':checked');
		var chk_recuperable = jQuery("#" + reclamoPrestacionalNamespace + "chk_recuperable").is(':checked');
		var chk_entramite = jQuery("#portlet:namespace />chk_entramite").is(':checked');

	    if (document.getElementById("" + reclamoPrestacionalNamespace + "resolucion").selectedIndex==0 ) {
	    	resolucion="";
	    }
	    if (document.getElementById("" + reclamoPrestacionalNamespace + "presentes").selectedIndex==0 ) {
	    	presentes="";
	    }
	    if (document.getElementById("" + reclamoPrestacionalNamespace + "respresolucion").selectedIndex==0 ) {
	    	respresolucion="";
	    }
	    jQuery('#' + reclamoPrestacionalNamespace + 'auditoriaadministrativa').val('');
	    if (document.getElementById("" + reclamoPrestacionalNamespace + "respresolucion").selectedIndex==1 ) {
	    	jQuery('#' + reclamoPrestacionalNamespace + 'auditoriaadministrativa').val('Ok');
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


		var url = reclamoPrestacionalViewConfig.urls.listaRevisiones;


		if (resolucion.toUpperCase()!="AUTORIZADO"){
			if(confirm("Confirma el Cierre del Caso con el Rechazo en la revision ?")){
	 			    /* var estadoSelectsector  =document.getElementById("namespaceestado"); */
				    //estadoSelectsector.selectedIndex = 2; // setea el estado en cerrado
				    /* estadoSelectsector.selectedIndex = ubicacionOpcionEstadoCerradoCombo();	 */
				    /* jQuery("#namespaceestado option[value='3']").attr("selected", true); //CERARADO */
				    jQuery("#" + reclamoPrestacionalNamespace + "estado option[value='CERRADO']").attr("selected",true);
				    controlarEstadoCerrado(); // hace visible los controles del estado cerrado

				    document.getElementById("" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo").disabled = false;

					var tipoSelectsector  =document.getElementById("" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo");

					seteaControlesFacturacionDirecta(true);
					/* tipoSelectsector.selectedIndex= ubicacionOpcionRechazadoenCombo(); */
				    /* jQuery("#namespacetipo_gestion_cierre_reclamo option[value='5']").attr("selected", true); //RECHAZADO */
				    jQuery("#" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo option[value='RECHAZADO']").attr("selected",true);

					/* var tipoGestionArray=jQuery('#namespacetipo_gestion_cierre_reclamo').val().split("|"); */
					var idgestion=jQuery('#' + reclamoPrestacionalNamespace + 'tipo_gestion_cierre_reclamo').val()

					/* var idgestion =tipoGestionArray [0]; */
					jQuery('#' + reclamoPrestacionalNamespace + 'tipogestion').val(idgestion);
					jQuery('#' + reclamoPrestacionalNamespace + 'reclamo_observacion_cierre').val('RECHAZO DE LA PRESTACION EN LA REVISION.');
					revisionConCierre=true;
					jQuery('#' + reclamoPrestacionalNamespace + 'cantrevisionesactivas').val(1); // para que no valide esto
					desactivaCheckCierre();							
					
	 		}else{
					return false;	
			}	
		}
			
		// oculta boton de agreagr revision porque solo se admite un aprobacion o un rechazo no hay parciales dentro del reclamo
		jQuery("#" + reclamoPrestacionalNamespace + "botonrevision").hide();
		jQuery("#" + reclamoPrestacionalNamespace + "mensajerevisionefectuada").html("Revisión Efectuada, el Sistema soporta solo una revisión activa (No de baja).");
	
	 	jQuery('#' + reclamoPrestacionalNamespace + 'lista_revisiones').load(url,params, function(){
															jQuery('#' + reclamoPrestacionalNamespace + 'buscando').hide();            															
														  });
	 	
		 jQuery('#' + reclamoPrestacionalNamespace + 'resolucion').val('');
		 jQuery('#' + reclamoPrestacionalNamespace + 'presentes').val('');
		 jQuery('#' + reclamoPrestacionalNamespace + 'respresolucion').val('');	  	  	  
		 document.getElementById("" + reclamoPrestacionalNamespace + "fecharevisionDia").selectedIndex = 0;	 
		 document.getElementById("" + reclamoPrestacionalNamespace + "fecharevisionMes").selectedIndex = 0;	 
		 document.getElementById("" + reclamoPrestacionalNamespace + "fecharevisionAnio").selectedIndex = 0;
		 document.getElementById("" + reclamoPrestacionalNamespace + "fecharevisionAnio").selectedIndex = 0;
		 jQuery('#' + reclamoPrestacionalNamespace + 'observacion_revision').val('');
		 if (reclamoPrestacionalViewConfig.values.hasReclamo) {
		 	if (revisionConCierre==true){			 
		 		reclamoPrestacional_editaReclamo(false); 
		 	}
		 } else {
		 	if (revisionConCierre==true){
			 reclamoPrestacional_saveReclamo();
		 	}
		 }
	}
}       		

/* function ubicacionOpcionRechazadoenCombo(){
	var idselect;
	var pos=0;
	var posicion=0;
		jQuery('#namespacetipo_gestion_cierre_reclamo option').each(function(){
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
		jQuery('#namespaceestado option').each(function(){
        	tipoGestionArray = jQuery(this).val().split("|");
        	idselect =tipoGestionArray [0];         
        	if (idselect == 3){
        	 	posicion=pos;
	        }
        	pos=pos+1;
        });
	return posicion;
} */

function reclamoPrestacional_verprestacionesasociadas() {
	
	if (document.getElementById("" + reclamoPrestacionalNamespace + "botonprestacionesasociadas").value=='Ver Prestaciones del Caso Asociado.'){
		jQuery("#" + reclamoPrestacionalNamespace + "lista_prestaciones_asociadas").show();
		document.getElementById("" + reclamoPrestacionalNamespace + "botonprestacionesasociadas").value='Ocultar Prestaciones del Caso Asociado.';
	}else{
		jQuery("#" + reclamoPrestacionalNamespace + "lista_prestaciones_asociadas").hide();
		document.getElementById("" + reclamoPrestacionalNamespace + "botonprestacionesasociadas").value='Ver Prestaciones del Caso Asociado.';
	}
}

function reclamoPrestacional_ocultacontactosdelreclamo() {
	jQuery("#" + reclamoPrestacionalNamespace + "lista_contactos_reclamo").hide();
	jQuery("#" + reclamoPrestacionalNamespace + "botoncontactosreclamo").show();
	jQuery("" + reclamoPrestacionalNamespace + "botoncontactosreclamo").value='Ver Contactos Asociados al Caso.';

}


function reclamoPrestacional_vercontactosdelreclamo() {
		
	var cuil=jQuery('#' + reclamoPrestacionalNamespace + 'cuil').val();
	var inte=jQuery('#' + reclamoPrestacionalNamespace + 'inte').val();
	var idreclamoprestacion=jQuery('#' + reclamoPrestacionalNamespace + 'idreclamoprestacion').val();
	var modoconsulta=jQuery('#' + reclamoPrestacionalNamespace + 'consultareclamo').val();
	
	    if ((cuil=="" || inte=="" )){		
			alert ('Debe seleccionar al Afiliado para ver sus contactos.');
			document.getElementById("" + reclamoPrestacionalNamespace + "cuil").focus();
			return false;
		}	    
			
	    if (document.getElementById("" + reclamoPrestacionalNamespace + "botoncontactosreclamo").value=='Ver Contactos Asociados al Caso.'){
		jQuery("#" + reclamoPrestacionalNamespace + "lista_contactos_reclamo").show();
		jQuery("#" + reclamoPrestacionalNamespace + "botoncontactosreclamo").hide();
		jQuery("#" + reclamoPrestacionalNamespace + "justificacion_medica_reclamo").hide();
		
		var cuil=jQuery('#' + reclamoPrestacionalNamespace + 'cuil').val();
		var inte=jQuery('#' + reclamoPrestacionalNamespace + 'inte').val();
		var idreclamoprestacion=jQuery('#' + reclamoPrestacionalNamespace + 'idreclamoprestacion').val();		
		
		if ( jQuery("#" + reclamoPrestacionalNamespace + "idreclamoprestacion").val()<1 
				&&  ((cuil==jQuery("#" + reclamoPrestacionalNamespace + "cuiltitular").val()  
						&& inte==jQuery("#" + reclamoPrestacionalNamespace + "intetitular").val() ))  ){			
			return false; // es el mismo afiliado 
		}		
		
		jQuery("#" + reclamoPrestacionalNamespace + "cuiltitular").val(cuil);
		jQuery("#" + reclamoPrestacionalNamespace + "intetitular").val(inte);
		
		var params = {"cuil_contacto":cuil,"inte_contacto":inte,"idreclamoprestacion":idreclamoprestacion,"modoconsulta":modoconsulta};

		var url = reclamoPrestacionalViewConfig.urls.listaContactos;
		
		jQuery('#' + reclamoPrestacionalNamespace + 'lista_contactos_reclamo').load(url,params, function(){
										jQuery('#' + reclamoPrestacionalNamespace + 'buscando').hide();          															
															  });			 	 
		}					
	}
	

function reclamoPrestacional_editarPrestacionSeleccionada(tipoAccion) {
	//tipoAccion=1 edicion 
	//tipoAccion=2 Autorizacion prestacion 
	//tipoAccion=3 Rechazo de  prestacion	
		
	var frecuencia= jQuery('#' + reclamoPrestacionalNamespace + 'frecuenciaEdicion').val();
	var cantidad =  jQuery('#' + reclamoPrestacionalNamespace + 'cantidadEdicion').val();
	var importe = jQuery('#' + reclamoPrestacionalNamespace + 'importeEdicion').val();
	var cargoospim= jQuery('#' + reclamoPrestacionalNamespace + 'cargoospimEdicion').val();
	var cargops= jQuery('#' + reclamoPrestacionalNamespace + 'cargopsEdicion').val();
	var cargoimesa= jQuery('#' + reclamoPrestacionalNamespace + 'cargoimesaEdicion').val();
	var reconocidoSSS= jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSSEdicion').val();
	var observaciones= jQuery('#' + reclamoPrestacionalNamespace + 'observacion_prestacionEdicion').val();
    var prestacion= "Graba Edicion";
    var idprestacion =  jQuery("#" + reclamoPrestacionalNamespace + "codigoprestacion").val();
    var idRegistro=jQuery('#' + reclamoPrestacionalNamespace + 'idRegistro').val();

    var estadoAprobacion = tipoAccion;
    var recuperableSur  =  jQuery('#' + reclamoPrestacionalNamespace + 'recuperable_surEdicion').val();  
    
    var cpbteTipo=jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_tipo_edicion').val();

    var cpbteNro=jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_nro_edicion').val();
    var cpbteDia=jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteDiaEdicion').val();
    var cpbteMes=jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteMesEdicion').val();
    var cpbteAnio=jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteAnioEdicion').val();
    var cpbteCantidad=jQuery('#' + reclamoPrestacionalNamespace + 'cantidadFC_edicion').val();
    var cpbteImporte= jQuery('#' + reclamoPrestacionalNamespace + 'importeUnitarioFC_edicion').val();
    var importeFC = jQuery('#' + reclamoPrestacionalNamespace + 'importeFC_edicion').val();
    var cpbteCuit=jQuery('#' + reclamoPrestacionalNamespace + 'cuit_entidad_edicion').val();
    var cpbteSucursal=jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_suc_edicion').val();
    var cpbteCuitSucursal=jQuery('#' + reclamoPrestacionalNamespace + 'sucursal_entidad_edicion').val();
    var cpbteLetra=jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_letra_edicion').val();


    var flagAmparo = false; 
    var estado=jQuery('#' + reclamoPrestacionalNamespace + 'estado').val();
	var chk_amparo=jQuery("#" + reclamoPrestacionalNamespace + "chk_amparo").is(':checked');

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
	
	var sector=jQuery('#' + reclamoPrestacionalNamespace + 'sector').val();
	
	var fechaPrestacionDia='';
	var fechaPrestacionMes='';
	var fechaPrestacionAnio='';
	
    
    fechaPrestacionDia=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDiaEdicion').val(); 
    fechaPrestacionMes=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionMesEdicion').val();
    fechaPrestacionAnio=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionAnioEdicion').val();
    
    id_medicamento_edit=jQuery('#' + reclamoPrestacionalNamespace + 'troquel_edit').val();
	var nombre_medicamento_edit = jQuery('#' + reclamoPrestacionalNamespace + 'nombre_medicamento_edit').val();

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
    
    var codigoSeguimiento_filtro_edit = jQuery('#' + reclamoPrestacionalNamespace + 'codigoSeguimiento_filtro_edit').val();
	var descripcionSeguimiento_filtro_edit = jQuery("#" + reclamoPrestacionalNamespace + "descripcionSeguimiento_filtro_edit").val();
	var nom_seleccionado_edit = jQuery("#" + reclamoPrestacionalNamespace + "nom_seleccionado").val(); 
	var tipoNomenclador_edit = jQuery('#' + reclamoPrestacionalNamespace + 'tipoNomenclador').val();
		

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
    
    
    var cuil=jQuery('#' + reclamoPrestacionalNamespace + 'cuil').val();
	var inte=jQuery('#' + reclamoPrestacionalNamespace + 'inte').val();	
	
	var idTecerizadora = jQuery('#' + reclamoPrestacionalNamespace + 'id_tercerizadora').val();
	
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
	
 	var url = reclamoPrestacionalViewConfig.urls.editarPrestaciones;
 	
	if(cpbteTipo != 'OTR' && cpbteTipo != 'AUT'){
	  if (!validarExisteComprobante(params)){   
	   	return false;
	  }
	}
	    
 	
	jQuery('#' + reclamoPrestacionalNamespace + 'lista_prestaciones_reclamos').load(url,params, function(){
									jQuery('#' + reclamoPrestacionalNamespace + 'buscando').hide();            															
													  });			
	jQuery('#' + reclamoPrestacionalNamespace + 'cantidadEdicion').val('1');
	jQuery('#' + reclamoPrestacionalNamespace + 'importeEdicion').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'totalEdicion').val('');
 	jQuery('#' + reclamoPrestacionalNamespace + 'cargoospimEdicion').val('');
 	jQuery('#' + reclamoPrestacionalNamespace + 'cargopsEdicion').val('');
 	jQuery('#' + reclamoPrestacionalNamespace + 'cargoimesaEdicion').val('');
 	jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSSEdicion').val('');
 	jQuery('#' + reclamoPrestacionalNamespace + 'observacion_prestacionEdicion').val('');
 	document.getElementById("" + reclamoPrestacionalNamespace + "frecuenciaEdicion").selectedIndex = 0;
	jQuery('#' + reclamoPrestacionalNamespace + 'troquel').val(""); // farmacia 
	jQuery('#' + reclamoPrestacionalNamespace + 'codigoSeguimiento_filtro').val("");// prestaciones medicas 
	//jQuery('#namespacerecuperable_sur').attr('checked', false);	
	document.getElementById("" + reclamoPrestacionalNamespace + "recuperable_sur").selectedIndex = 0; 	
	
	jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_tipo_edicion').val('FCP');
	jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_letra_edicion').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_nro_edicion').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_suc_edicion').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteDiaEdicion').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteMesEdicion').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteAnioEdicion').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'cantidadFC_edicion').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'importeUnitarioFC_edicion').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'importeFC_edicion').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'cuit_entidad_edicion').val('');
    jQuery('#' + reclamoPrestacionalNamespace + 'sucursal_entidad_edicion').val('');
    jQuery('#' + reclamoPrestacionalNamespace + 'entidad_edicion').val('');
    
	jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDiaFarmacia').val(''); 
    jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionMesFarmacia').val('');
    jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionAnioFarmacia').val('');
    
	jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDia').val(''); 
    jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionMes').val('');
    jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionAnio').val('');
	
    
    jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDiaEdicion').val('');
    jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionMesEdicion').val('');
    jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionAnioEdicion').val('');

    jQuery("#" + reclamoPrestacionalNamespace + "nombre_medicamento_edit").val('');
    jQuery("#" + reclamoPrestacionalNamespace + "divBtnBuscaMedicamento_edit").show();
    
    
	reclamoPrestacional_limpiarNomencladorAutocompletar();
	   
    addprestacion=false;
    reclamoPrestacional_cancelaEdicionPrestacion();

}


function reclamoPrestacional_cancelaEdicionPrestacion() {
	var editor = jQuery(
			"#" + reclamoPrestacionalNamespace + "datos_edicion_prestacion"
	);
	var ingreso = jQuery(
			"#" + reclamoPrestacionalNamespace + "datos_prestacion_ingreso"
	);
	var tipoAccion = document.getElementById(
			reclamoPrestacionalNamespace + "tipoaccionprestacion"
	);
	var datos = tipoAccion ? String(tipoAccion.value || "") : "";
	var partes;
	var idPrestacion;
	var comboEstado;

	editor.hide().attr("aria-hidden", "true");

	manejarTipoSector();

	ingreso.show().attr("aria-hidden", "false");

	reclamoPrestacional_limpiarNomencladorAutocompletar();
	onOffcombosestadosprestaciones(true);

	if (datos) {
		partes = datos.split("-");
		idPrestacion = partes.length > 1 ? partes[1] : "";

		if (idPrestacion) {
			comboEstado = document.getElementById(
					"comboestadosreclamo" + idPrestacion
			);

			if (comboEstado) {
				comboEstado.selectedIndex = 0;
			}
		}
	}

	if (tipoAccion) {
		tipoAccion.value = "";
	}
}

function reclamoPrestacional_agregarPrestacion() {	
	
	var frecuencia= jQuery('#' + reclamoPrestacionalNamespace + 'frecuencia').val();		
	var importe = jQuery('#' + reclamoPrestacionalNamespace + 'importe').val();
	var cantidad  = jQuery('#' + reclamoPrestacionalNamespace + 'cantidad').val();
	var cargoospim= jQuery('#' + reclamoPrestacionalNamespace + 'cargoospim').val();
	var cargops= jQuery('#' + reclamoPrestacionalNamespace + 'cargops').val();
	var cargoimesa= jQuery('#' + reclamoPrestacionalNamespace + 'cargoimesa').val();
	var reconocidoSSS= jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSS').val();
	var observaciones= jQuery('#' + reclamoPrestacionalNamespace + 'observacion_prestacion').val();		
    var troquel= jQuery('#' + reclamoPrestacionalNamespace + 'troquel').val();
    var prestacion= jQuery('#' + reclamoPrestacionalNamespace + 'codigoSeguimiento_filtro').val();    
    var tiponomenclador =jQuery('#' + reclamoPrestacionalNamespace + 'nom_seleccionado').val();
    var tiponomencladorprestacion =jQuery('#' + reclamoPrestacionalNamespace + 'tiponomenclador').val();
    var nombre_medicamento=jQuery("#" + reclamoPrestacionalNamespace + "nombre_medicamento").val();
    var nombre_prestacion = jQuery('#' + reclamoPrestacionalNamespace + 'descripcionSeguimiento_filtro').val();
    var tiponomnecladorprestacion =  jQuery("#" + reclamoPrestacionalNamespace + "tipoNomenclador").val(); 
    
    
    
    var recuperableSur  =  jQuery('#' + reclamoPrestacionalNamespace + 'recuperable_sur').val();
    
    
    var cpbteTipo=jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_tipo').val();
    var cpbteNro=jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_nro').val();
    var cpbteDia=jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteDia').val();
    var cpbteMes=jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteMes').val();
    var cpbteAnio=jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteAnio').val();
    var cpbteCantidad=jQuery('#' + reclamoPrestacionalNamespace + 'cantidadFC').val();
    var cpbteImporte= jQuery('#' + reclamoPrestacionalNamespace + 'importeUnitarioFC').val();
    var importeFC = jQuery('#' + reclamoPrestacionalNamespace + 'importeFC').val();
    var cpbteCuit=jQuery('#' + reclamoPrestacionalNamespace + 'cuit_entidad').val();
    var cpbteCuitSucursal=jQuery('#' + reclamoPrestacionalNamespace + 'sucursal_entidad').val();
    var cpbteSucursal=jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_suc').val();
    var cpbteLetra=jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_letra').val();
    
    
    var flagAmparo = false; 
    var estado=jQuery('#' + reclamoPrestacionalNamespace + 'estado').val();
	var chk_amparo=jQuery("#" + reclamoPrestacionalNamespace + "chk_amparo").is(':checked');

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
    
 	if (jQuery("#" + reclamoPrestacionalNamespace + "nom_seleccionado").val()==''){
		  alert('Debe seleccionar el sector');
		  return false;
	}	
	if (jQuery("#" + reclamoPrestacionalNamespace + "nom_seleccionado").val()==1){		 
		if (jQuery('#' + reclamoPrestacionalNamespace + 'codigoSeguimiento_filtro').val()<1  ) {
		 	alert('Debe seleccionar la prestación');
		 	return false;
		} 	
	    if(nombre_prestacion==null || nombre_prestacion==''){
			  alert('Debe seleccionar la prestación');
			  return false;
		}
			
	}else{		
		if (jQuery('#' + reclamoPrestacionalNamespace + 'troquel').val()<1) {
			alert('Debe seleccionar el medicamento');
			return false;
		}	
		if ( nombre_medicamento==null || nombre_medicamento=='') {
			alert('Debe seleccionar el medicamento');
			return false;
		}
	}    
	
	
	var sector=jQuery('#' + reclamoPrestacionalNamespace + 'sector').val();

    var fechaPrestacionDia='';
    var fechaPrestacionMes='';
    var fechaPrestacionAnio='';
	 fechaPrestacionDia=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDia').val(); 

    if (fechaPrestacionDia==null || fechaPrestacionDia==0 || fechaPrestacionDia=='' ){
    	 fechaPrestacionDia=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDiaFarmacia').val(); 
         fechaPrestacionMes=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionMesFarmacia').val();
         fechaPrestacionAnio=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionAnioFarmacia').val();
    }else{
        fechaPrestacionDia=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDia').val(); 
        fechaPrestacionMes=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionMes').val();
        fechaPrestacionAnio=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionAnio').val();
    }
	
	

	if (frecuencia=="SELECCIONE"){
    	frecuencia="";    
	}
    
    var frecuenciacontrol =document.getElementById("" + reclamoPrestacionalNamespace + "frecuencia");
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
    
    if (flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT') ){
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
    
   
    var tipoPedidoControl =document.getElementById("" + reclamoPrestacionalNamespace + "tipopedido");
    if (tipoPedidoControl.selectedIndex==0){
		alert('Debe seleccionar el Tipo de Pedido.');
		return false ;
	}
    
    
    if (!ValidaDatosReclamo()){       
   		return false;
	}
    
    var cuil=jQuery('#' + reclamoPrestacionalNamespace + 'cuil').val();
	var inte=jQuery('#' + reclamoPrestacionalNamespace + 'inte').val();	
	
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
	
	var url = reclamoPrestacionalViewConfig.urls.listaPrestaciones;

	jQuery('#' + reclamoPrestacionalNamespace + 'lista_prestaciones_reclamos').load(url,params, function(){
									jQuery('#' + reclamoPrestacionalNamespace + 'buscando').hide();            															
													  });			
	/* document.getElementById("namespacesector").disabled = "disabled"; */	  
 	jQuery('#' + reclamoPrestacionalNamespace + 'importe').val('');
 	jQuery('#' + reclamoPrestacionalNamespace + 'total').val('');
 	jQuery('#' + reclamoPrestacionalNamespace + 'cantidad').val('1');
 	jQuery('#' + reclamoPrestacionalNamespace + 'cargoospim').val('');
 	jQuery('#' + reclamoPrestacionalNamespace + 'cargops').val('');
 	jQuery('#' + reclamoPrestacionalNamespace + 'cargoimesa').val('');
 	jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSS').val('');
 	jQuery('#' + reclamoPrestacionalNamespace + 'observacion_prestacion').val('');
	document.getElementById("" + reclamoPrestacionalNamespace + "frecuencia").selectedIndex = 0;
	jQuery('#' + reclamoPrestacionalNamespace + 'troquel').val(""); // farmacia 
	jQuery('#' + reclamoPrestacionalNamespace + 'codigoSeguimiento_filtro').val("");// prestaciones medicas
	//jQuery('#namespacerecuperable_sur').attr('checked', false);
	document.getElementById("" + reclamoPrestacionalNamespace + "recuperable_sur").selectedIndex = 0;
	jQuery("#" + reclamoPrestacionalNamespace + "divBtnBuscaEntidad").show();

	
	jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_tipo').val('FCP');
	jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_nro').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteDia').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteMes').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteAnio').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'cantidadFC').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'importeUnitarioFC').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'importeFC').val('');
	jQuery('#' + reclamoPrestacionalNamespace + 'cuit_entidad').val('');
    jQuery('#' + reclamoPrestacionalNamespace + 'sucursal_entidad').val('');
    jQuery('#' + reclamoPrestacionalNamespace + 'entidad_').val('');
    jQuery('#' + reclamoPrestacionalNamespace + 'comprobante_suc').val('');
    jQuery("#" + reclamoPrestacionalNamespace + "nombre_medicamento").val('');
    jQuery("#" + reclamoPrestacionalNamespace + "divBtnBuscaMedicamento").show();
    

	jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDiaFarmacia').val(''); 
    jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionMesFarmacia').val('');
    jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionAnioFarmacia').val('');
    
	jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDia').val(''); 
    jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionMes').val('');
    jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionAnio').val('');
    
	reclamoPrestacional_limpiarNomencladorAutocompletar();
	
    addprestacion=true;
    /* document.getElementById("namespacetipopedido").disabled = true;  */    
    if (jQuery('#' + reclamoPrestacionalNamespace + 'estado').val()==3){   // cerrado
    	jQuery('#' + reclamoPrestacionalNamespace + 'montoPsPrestaciones').val(cargops); 
		/* validaFacturacionDirectayReintegro();  */    	
    }	                            
}   

function controlarEstadoCerrado() {

	var  varCantRevisiones = reclamoPrestacionalViewConfig.values.cantRevisiones;
	
	var  varDebitoTercerizadora = reclamoPrestacionalViewConfig.values.debitoTercerizadora;
	
	
	
	
	
	// VERIFICAR SI EXISTE POR LO MENOS UN REGISTRO DE REVISION ACTIVO 	
	if (jQuery('#' + reclamoPrestacionalNamespace + 'estado').val()==3){
		if (varCantRevisiones > 0 ){
			jQuery("#" + reclamoPrestacionalNamespace + "Cierre_Reclamo_Div").show();	
			if(varDebitoTercerizadora == true){
				jQuery("#" + reclamoPrestacionalNamespace + "debitoprestadora")[0].checked = true;

			}																												
		}else{
			alert("Debe agregar una Revisión");
			jQuery("#" + reclamoPrestacionalNamespace + "estado option[value="+estadoIni+"]").attr("selected",true);

		}
		/* validaFacturacionDirectayReintegro(); */		
	} else {
		jQuery("#" + reclamoPrestacionalNamespace + "Cierre_Reclamo_Div").hide();
		jQuery('#' + reclamoPrestacionalNamespace + 'nroLote').val("");
	}	
}

/* function onOffControlesRequest(valor) {
	document.getElementById("namespacefechaseccionalDia").disabled = valor;
	document.getElementById("namespacefechaseccionalMes").disabled = valor;
	document.getElementById("namespacefechaseccionalAnio").disabled = valor;
} */


function reclamoPrestacional_imprimirReclamo(){
		     
	window.location.href ="/pdfservlet/?accion=reclamoprestacional&idreclamo=" + reclamoPrestacionalViewConfig.values.idReclamo;
	
}


function ValidaDatosReclamo(){
	
	
	var respuesta=true;
	var codError='';	
	var cpbte_dia =  jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteDia').val();
	var cpbte_mes =  jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteMes').val();
	var cpbte_anio = jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteAnio').val();

	var sector=jQuery('#' + reclamoPrestacionalNamespace + 'sector').val();
	
    var cpbteCuit=jQuery('#' + reclamoPrestacionalNamespace + 'cuit_entidad').val();
    var tipopedido=jQuery('#' + reclamoPrestacionalNamespace + 'tipopedido').val();


	var fecha_prestacion_dia='';
	var fecha_prestacion_mes='';
	var fecha_prestacion_anio='';
		
	    
	if (reclamoPrestacional_usaBuscadorMedicamentos()) {
		fecha_prestacion_dia=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDiaFarmacia').val();
		fecha_prestacion_mes=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionMesFarmacia').val();
		fecha_prestacion_anio=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionAnioFarmacia').val();
	} else {
		fecha_prestacion_dia=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDia').val();
		fecha_prestacion_mes=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionMes').val();
		fecha_prestacion_anio=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionAnio').val();
	}
	
    var troquel= jQuery('#' + reclamoPrestacionalNamespace + 'troquel').val();
    var prestacion= jQuery('#' + reclamoPrestacionalNamespace + 'codigoSeguimiento_filtro').val();    
    var tipoNomenclador =jQuery('#' + reclamoPrestacionalNamespace + 'nom_seleccionado').val();
    var tipoNomencladorPrestacion =jQuery('#' + reclamoPrestacionalNamespace + 'tiponomenclador').val();
 
	
     var baja =  jQuery('#' + reclamoPrestacionalNamespace + 'baja_fecha').val();
    
	 var url = reclamoPrestacionalViewConfig.urls.validarReclamo;
		
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
	return  respuesta;    
		
}




function ValidaDatosReclamoEditar(){
		
	var respuesta=true;
	var codError='';	
	var cpbte_dia =  jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteDiaEdicion').val();
	var cpbte_mes =  jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteMesEdicion').val();
	var cpbte_anio = jQuery('#' + reclamoPrestacionalNamespace + 'fechaComprobanteAnioEdicion').val();

	    

	fecha_prestacion_dia=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionDiaEdicion').val(); 
	fecha_prestacion_mes=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionMesEdicion').val();
	fecha_prestacion_anio=jQuery('#' + reclamoPrestacionalNamespace + 'fechaPrestacionAnioEdicion').val();
	
	var sector=jQuery('#' + reclamoPrestacionalNamespace + 'sector').val();
	
    var tipopedido=jQuery('#' + reclamoPrestacionalNamespace + 'tipopedido').val();

    var cpbteCuit=jQuery('#' + reclamoPrestacionalNamespace + 'cuit_entidad_edicion').val();
	
    var troquel= jQuery('#' + reclamoPrestacionalNamespace + 'troquel_edit').val();
    var prestacion= jQuery('#' + reclamoPrestacionalNamespace + 'codigoSeguimiento_filtro_edit').val();    
    var tipoNomenclador =jQuery('#' + reclamoPrestacionalNamespace + 'nom_seleccionado').val();
    var tipoNomencladorPrestacion =jQuery('#' + reclamoPrestacionalNamespace + 'tiponomenclador').val();
    var baja =  jQuery('#' + reclamoPrestacionalNamespace + 'baja_fecha').val();

	 var url = reclamoPrestacionalViewConfig.urls.validarReclamo;
		
	 url +='&cpbte_dia='+cpbte_dia;
	 url +='&cpbte_mes='+cpbte_mes;
	 url +='&cpbte_anio='+cpbte_anio;
	 url +='&fecha_prestacion_dia='+fecha_prestacion_dia;
	 url +='&fecha_prestacion_mes='+fecha_prestacion_mes;
	 url +='&fecha_presacion_anio='+fecha_prestacion_anio;
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
	return  respuesta;    
		
}

function validarExisteComprobante( params ) {
    var resp=true;
	var respuesta=true;
    var rtaExisteCompro=false;
    var mensajeErrorOut='';
	
    var url = reclamoPrestacionalViewConfig.urls.validarExisteComprobante;
	
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

	jQuery('#' + reclamoPrestacionalNamespace + 'cantprestacioneslista').val('0');
	document.getElementById("" + reclamoPrestacionalNamespace + "tipo_gestion_cierre_reclamo").selectedIndex=0;
	seteaControlesFacturacionDirecta(false);
	
	


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
		var params = "&" + reclamoPrestacionalViewConfig.values.cmdParam + "=" + reclamoPrestacionalViewConfig.values.viewCommand;
		params = params + '&idContactoSerial='+idContSerial;
		
		popupCRM = new Liferay.Popup({title:reclamoPrestacionalViewConfig.messages.detalleContacto,modal:true, width: 880, position:['center',30]});
		var url = reclamoPrestacionalViewConfig.urls.editarContactoAfiliados;
		if (reclamoPrestacionalViewConfig.values.caiNamespace) {
		url = reclamoPrestacionalViewConfig.urls.editarContactoCai;
		}
		url = url + params;
		jQuery(popupCRM).load(url);	
	}
	


function validaMontosEdicion(){	
	
	/* var strimporte =   jQuery('#namespacetotalEdicion').val();

    var strcargoospim = jQuery('#namespacecargoospimEdicion').val();
    var strcargops =   jQuery('#namespacecargopsEdicion').val(); */

    //var importedouble = parseFloat(jQuery('#namespacetotalEdicion').val());
    var importedouble = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'totalEdicion').val().replace(",","."));
    
    var cargoospimdouble = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'cargoospimEdicion').val());
    var cargopsdouble = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'cargopsEdicion').val());
    var cargoimesadouble = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'cargoimesaEdicion').val());
    var reconocidoSSS = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSSEdicion').val());
    var estado =jQuery("#" + reclamoPrestacionalNamespace + "estado").val();
    

    var importeFC = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'importeFC').val());
    var importeFCEdicion = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'importeFC_edicion').val());
    if(isNaN(importeFC)) {
//	jQuery('#namespaceimporteFC').val();
	   importeFC=0;
    }
    if(isNaN(importeFCEdicion)) {
 //	jQuery('#namespaceimporteFC_edicion').val();
	   importeFCEdicion=0;
    }


/*
importedouble= parseFloat(strimporte.replace(',','.'));
cargoospimdouble= parseFloat(strcargoospim.replace(',','.'));
cargopsdouble= parseFloat(strcargops.replace(',','.'));
*/
    if(isNaN(importedouble)) {		jQuery('#' + reclamoPrestacionalNamespace + 'totalEdicion').val()  ; importedouble=0; 	}
    if(isNaN(cargoospimdouble)) {	jQuery('#' + reclamoPrestacionalNamespace + 'cargoospimEdicion').val()  ; cargoospimdouble=0; 	}
    if(isNaN(cargopsdouble)) {		jQuery('#' + reclamoPrestacionalNamespace + 'cargopsEdicion').val()  ; cargopsdouble=0; 	}
    if(isNaN(cargoimesadouble)) {		jQuery('#' + reclamoPrestacionalNamespace + 'cargoimesaEdicion').val()  ; cargoimesadouble=0; 	}
    if(isNaN(reconocidoSSS)) {		jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSSEdicion').val()  ; reconocidoSSS=0; 	}

    total= Math.round((cargoospimdouble + cargopsdouble +cargoimesadouble + reconocidoSSS) * 100) / 100 ;
    
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

    if ( document.getElementById("" + reclamoPrestacionalNamespace + "tipopedido").selectedIndex==1) { // tipo de pedido excepcion 
	  if (total!=importedouble && estado==3){
		alert('El importe total de la prestación debe coincidir con la suma de cargo Ospim y cargo tercerizadora');
		return false;
	  }
    }
    
    var recuperable  =  jQuery('#' + reclamoPrestacionalNamespace + 'recuperable_surEdicion').val();
    if(recuperable==2){
    	if(reconocidoSSS>0){
    	   	   alert('El importe reconocido debe estar vacío');
    	   	   jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSSEdicion').val('');
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
jQuery('#namespaceimporteEdicion').val(importedouble);
jQuery('#namespacecargoospimEdicion').val(cargoospimdouble);
jQuery('#namespacecargopsEdicion').val(cargopsdouble);
*/	
	
	return true;
}
	
function ValidaMontos()
{
	var importeFC = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'importeFC').val());
	var importedouble = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'total').val());
	var cargoospimdouble = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'cargoospim').val());
	var cargopsdouble = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'cargops').val());
	var cargoimesadouble = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'cargoimesa').val());
	var reconocidoSSS = parseFloat(jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSS').val());
	var estado =jQuery("#" + reclamoPrestacionalNamespace + "estado").val();
	
	if(isNaN(importedouble)) {		jQuery('#' + reclamoPrestacionalNamespace + 'total').val()  ; importedouble=0; 	}
	if(isNaN(cargoospimdouble)) {	jQuery('#' + reclamoPrestacionalNamespace + 'cargoospim').val()  ; cargoospimdouble=0; 	}
	if(isNaN(cargopsdouble)) {		jQuery('#' + reclamoPrestacionalNamespace + 'cargops').val()  ; cargopsdouble=0; 	}
	if(isNaN(cargoimesadouble)) {		jQuery('#' + reclamoPrestacionalNamespace + 'cargoimesa').val()  ; cargoimesadouble=0; 	}
	if(isNaN(reconocidoSSS)) {		jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSS').val()  ; reconocidoSSS=0; 	}
	if(isNaN(importeFC)) {		jQuery('#' + reclamoPrestacionalNamespace + 'importeFC').val()  ; importeFC=0; 	}
	
//	totalCargos= cargoospimdouble + cargopsdouble;
	totalCargos= Math.round((cargoospimdouble + cargopsdouble+cargoimesadouble +reconocidoSSS) * 100) / 100 ;
	
	
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
	
	if ( document.getElementById("" + reclamoPrestacionalNamespace + "tipopedido").selectedIndex==1) { // tipo de pedido excepcion 
		if (totalCargos!=importedouble && estado=='3'){
			alert('El importe total de la prestación debe coincidir con la suma de Cargo Ospim más Cargo Tercerizadora.');
			return false;
		}
	}
 
   if ( document.getElementById("" + reclamoPrestacionalNamespace + "tipopedido").selectedIndex==2) { // tipo de pedido reintegro
	    if (importedouble <totalCargos && estado=='3'){
			alert('El importe total de prestación debe coincidir con la suma de a Cargo Ospim más Cargo Tercerizadora');
			return false;
		}   
		if (totalCargos==0 && estado=='3'){
			alert(' la suma de a Cargo Ospim más a Cargo Tercerizadora debe ser mayor que cero.');
			return false;
		}
   }
   
   var recuperable  =  jQuery('#' + reclamoPrestacionalNamespace + 'recuperable_sur').val();
   if(recuperable==2){
	   if(reconocidoSSS>0){
	   	   alert('El importe reconocido debe estar vacio');
	   	   jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSS').val('');
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
	if (jQuery('#' + reclamoPrestacionalNamespace + 'cantrevisionesactivas').val()<1){ // no hay revisiones activas 
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
	  var input=document.getElementById('namespacebuscadorcie10buscador').value.toUpperCase();
	  var output=document.getElementById('namespacecie_diez').options;
	  var dato;       
      pos=jQuery('#namespaceposforcie10').val();
      for(var i=pos;i<document.getElementById("namespacecie_diez").options.length ;i++) {
		  dato = output[i].text;		  
		  if(dato.indexOf(input)>-1){
		        output[i].selected=true;		        
		        jQuery('#namespacecodigoCie10').val(output[i].value);
		        jQuery('#namespaceposforcie10').val(++i);
		        return false;
		      }		 
      } 
      
      if (output[0].selected){
    	  alert('No se encontro el dato buscado.')  
      }     else{
    	  alert('Se termino de recorrer al lista.');
    	  
    	  
      }  
      jQuery('#namespaceposforcie10').val(0);
	}
*/
function enterTecla(e){
	tecla = (document.all) ? e.keyCode : e.which;//obtenemos el codigo ascii de la tecla	
	if (tecla==13) {
		crit_busqueda();
	}else{
		jQuery('#' + reclamoPrestacionalNamespace + 'posforcie10').val(0);
	} 

}

function aplicaEstiloBordeRojoDatosObligatorio() { 
	// borde rojo en datos obligatorios
	color="#ff9999"
	jQuery("#" + reclamoPrestacionalNamespace + "fechaospimMes").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "fechaospimAnio").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "fechaospimDia").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "estado").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "sector").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "tipopedido").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "fecharevisionMes").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "fecharevisionAnio").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "fecharevisionDia").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "resolucion").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "justificacionmedica").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "frecuencia").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "importe").css("borderColor",color);
	jQuery("#" + reclamoPrestacionalNamespace + "mensajerevisionefectuada").css("borderColor",color);

}

function calculatotal(){

	importe=jQuery("#" + reclamoPrestacionalNamespace + "importe").val();
	cantidad=jQuery("#" + reclamoPrestacionalNamespace + "cantidad").val()
	total= importe * cantidad  ;
	jQuery("#" + reclamoPrestacionalNamespace + "total").val(Math.round(total.toFixed(2) * 100)/100);
	//jQuery("#namespacetotal").val(total.toFixed(2));

}

function seleccionaCamposCieDiez(codigo,descripcion ){
	jQuery('#' + reclamoPrestacionalNamespace + 'codigoCie').val(codigo);
	jQuery('#' + reclamoPrestacionalNamespace + 'detalleCie').val(descripcion);
	jQuery('#' + reclamoPrestacionalNamespace + 'codigoCie10').val(codigo);
}	

if (reclamoPrestacionalViewConfig.values.codigoCie10Presente) {
window[reclamoPrestacionalNamespace + "buscarCieCodigo"](); 
}

function limpiaCamposBusquedaCieDiez(){
	jQuery('#' + reclamoPrestacionalNamespace + 'codigoCie10').val("");
}

/* function validaFacturacionDirectayReintegro(){
	document.getElementById("namespacetipo_gestion_cierre_reclamo").selectedIndex=0;	
	jQuery('#namespacetipogestion').val(0);
	seteaControlesFacturacionDirecta(false);
	if (jQuery('#namespacemontoPsPrestaciones').val()>0 && jQuery('#namespacemontoPsPrestaciones').val()!="" ){// forzar facturacion directa o reintegro
		
		if (document.getElementById("namespacetipopedido").selectedIndex==1){ // excepcion 
			document.getElementById("namespacetipo_gestion_cierre_reclamo").selectedIndex=2;
			jQuery('#namespacetipogestion').val(3); // facturacion directa 
			seteaControlesFacturacionDirecta(true);
		}	
 		if (document.getElementById("namespacetipopedido").selectedIndex==2){ // reintegro
			validaReintegro();			
	}
}
} */


/* function validaReintegro(){
		document.getElementById("namespacetipo_gestion_cierre_reclamo").selectedIndex=3;		
	    document.getElementById("namespacetipo_gestion_cierre_reclamo").disabled = true;
		jQuery('#namespacetipogestion').val(4); // reintegro 
} */

function seteaControlesFacturacionDirecta(estadoTrueFalse){
	document.getElementById("" + reclamoPrestacionalNamespace + "incluido_convenio_gerenciadora").checked = estadoTrueFalse;
	/* document.getElementById("namespaceincluido_convenio_gerenciadora").disabled = estadoTrueFalse; */
	document.getElementById("" + reclamoPrestacionalNamespace + "debitoprestadora").checked =estadoTrueFalse;
	/*  document.getElementById("namespacedebitoprestadora").disabled = estadoTrueFalse; */	
	/*  document.getElementById("namespacetipo_gestion_cierre_reclamo").disabled = estadoTrueFalse;*/
}
function desactivaCheckCierre(){
	seteaControlesFacturacionDirecta(false);
	document.getElementById("" + reclamoPrestacionalNamespace + "dosporciento").checked =false;
	document.getElementById("" + reclamoPrestacionalNamespace + "dosporciento").disabled = true;
}

/* function habilitarControlesCierre() {
	document.getElementById("namespacesector").disabled =false;  
	document.getElementById("namespacetipopedido").disabled =false; 
	document.getElementById("namespacedebitoprestadora").disabled =false; 
	document.getElementById("namespaceincluido_convenio_gerenciadora").disabled =false; 
	document.getElementById("namespacetipo_gestion_cierre_reclamo").disabled =false;
} */


function abreAutorizacion(){
	
	 window.open(reclamoPrestacionalViewConfig.urls.autorizaciones,
	         'Autorizaciones', 'height=800, menubar=no, resizable=yes,scrollbars=yes, status=no, toolbar=no, width=1200');  
}

function calculatotalFC(){

	importe=jQuery("#" + reclamoPrestacionalNamespace + "importeUnitarioFC").val();
	cantidad=jQuery("#" + reclamoPrestacionalNamespace + "cantidadFC").val();
	total= importe * cantidad  ;
	jQuery("#" + reclamoPrestacionalNamespace + "importeFC").val(Math.round(total.toFixed(2) * 100)/100);
/*	
	jQuery("#namespacecantidad").val(cantidad);
	jQuery("#namespaceimporte").val(importe);
	calculatotal();
	jQuery('#namespacecargoospim').val(Math.round(total.toFixed(2) * 100)/100);
*/	
}


function traerDescripcion() {
	var idIntegracion = jQuery('#' + reclamoPrestacionalNamespace + 'integracion').val();
	var descripcionLarga;
	var url = reclamoPrestacionalViewConfig.urls.integracionDetalle+idIntegracion;
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
		var recuperable=jQuery('#' + reclamoPrestacionalNamespace + 'recuperable_sur').val();
		if(recuperable==3 || recuperable==1){
			jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSS').attr('readonly', false);
		}else{
			jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSS').val(0);
			jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSS').attr('readonly', true);
		}
		
			

	}catch (err) {}	
	
}

function reclamoPrestacional_validarEmail() {
	var email = jQuery('#' + reclamoPrestacionalNamespace + 'email').val();
/* 	var emailReg = /^([\da-z_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
 */	
 
/*  Se solicito quitar el 24/05/2016
	if(trim(email).length == 0){
		alert("El campo Email es Obligatorio");
		jQuery("#namespaceemail").focus();
		return false;
	} */
	if(trim(email).length == 0){
		return true;
	}
	var expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	
	if ( !expr.test(email) ){
	    alert("Error: La dirección de correo " + email + " es incorrecta.");
	    jQuery("#" + reclamoPrestacionalNamespace + "email").focus();
		return false;
	}
	    
	/* if(trim(email).length > 0){	
		if( !emailReg.test( email ) ) {
			jQuery("#namespaceemail").focus();
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

	var d_id_domicilio=jQuery("#" + reclamoPrestacionalNamespace + "id_domicilio").val();
    var d_id_provincia = jQuery("#" + reclamoPrestacionalNamespace + "provincia").val();
	var d_id_localidad = jQuery("#" + reclamoPrestacionalNamespace + "localidad").val();
	var d_calle = jQuery("#" + reclamoPrestacionalNamespace + "calle").val();
	var d_numero = jQuery("#" + reclamoPrestacionalNamespace + "numero").val();
	var d_piso = jQuery("#" + reclamoPrestacionalNamespace + "piso").val();
	var d_dpto = jQuery("#" + reclamoPrestacionalNamespace + "dpto").val();
	var d_cod_pos = jQuery("#" + reclamoPrestacionalNamespace + "cod_postal").val();
	var d_barrio = jQuery("#" + reclamoPrestacionalNamespace + "barrio").val();
	var d_cod_area_tel = jQuery("#" + reclamoPrestacionalNamespace + "cod_area_telefono").val();
	var d_telefono = jQuery("#" + reclamoPrestacionalNamespace + "telefono").val();
	//var d_cod_area_laboral = jQuery("#namespacecod_area_tel_laboral").val();
	//var d_laboral = jQuery("#namespacetel_laboral").val();
	var d_cod_area_celu = jQuery("#" + reclamoPrestacionalNamespace + "cod_area_celular").val();
	var d_celular = jQuery("#" + reclamoPrestacionalNamespace + "celular").val();
	
	var d_email = jQuery("#" + reclamoPrestacionalNamespace + "email").val();
	var d_email_original = jQuery("#" + reclamoPrestacionalNamespace + "email_original").val();
	
//	var cuiltitular= jQuery('#namespacecuil_titular').val();
	var cuiltitular= jQuery('#' + reclamoPrestacionalNamespace + 'cuil').val();
	var integrante = jQuery("#" + reclamoPrestacionalNamespace + "inte").val();
	
	var idPar = jQuery("#" + reclamoPrestacionalNamespace + "idPar").val();
	if (idPar != reclamoPrestacionalViewConfig.values.parentescoDefault &&
	    idPar != reclamoPrestacionalViewConfig.values.conyugeDefault &&
	    idPar != reclamoPrestacionalViewConfig.values.concubinoDefault) {
	  integrante = 0;
	}
	
	/*validamos los campos obligatorios*/
	if (trim(d_calle).length == 0){
		alert("Ingrese la calle del domicilio");
		jQuery('#' + reclamoPrestacionalNamespace + 'calle').focus();
		return false;
	}
	
	if (
		 (trim(d_cod_area_tel) == '' && trim(d_telefono) != '') ||
		 (trim(d_cod_area_tel) != '' && trim(d_telefono) == '')
		){
		alert("El teléfono debe necesariamente tener el código de area y el número");
		jQuery('#' + reclamoPrestacionalNamespace + 'telefono').focus();
		return false;
	}
	
	if(trim(d_cod_area_tel).startsWith('0')){
		alert("El código de area del teléfono no debe iniciar con cero");
		jQuery("#" + reclamoPrestacionalNamespace + "cod_area_telefono").focus();
		return false;
	}
	if(trim(d_telefono).startsWith('0')){
		alert("El número del teléfono no debe iniciar con cero");
		jQuery("#" + reclamoPrestacionalNamespace + "telefono").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_tel).length>0 || trim(d_telefono).length>0){
		if(trim(d_cod_area_tel).length+trim(d_telefono).length!=10){
			alert("La longitud del código de área + teléfono debe de ser de 10 caracteres");
			jQuery("#" + reclamoPrestacionalNamespace + "cod_area_telefono").focus();
			return false;
		}
	}
	/*
	if ((trim(d_cod_area_laboral) == '' && trim(d_laboral) != '') ||
		(trim(d_cod_area_laboral) != '' && trim(d_laboral) == '')
		){
		alert("El teléfono laboral debe necesariamente tener el código de area y el número");
		jQuery('#namespacetel_laboral').focus();
		return false;
	}
	
	if(trim(d_cod_area_laboral).startsWith('0')){
		alert("El código de area laboral no debe iniciar con cero");
		jQuery("#namespacecod_area_tel_laboral").focus();
		return false;
	}
	if(trim(d_laboral).startsWith('0')){
		alert("El número del teléfono laboral no debe iniciar con cero");
		jQuery("#namespacetel_laboral").focus();
		return false;
	}
	
	if(trim(d_cod_area_laboral).length>0 || trim(d_laboral).length>0){
		if(trim(d_cod_area_laboral).length+trim(d_laboral).length!=10){
			alert("La longitud del código de área + teléfono laboral debe de ser de 10 caracteres");
			jQuery("#namespacecod_area_tel_laboral").focus();
			return false;
		}
	}
	*/
	
	
	if(trim(d_cod_area_celu).startsWith('0')){
		alert("El código de area del celular no debe iniciar con cero");
		jQuery("#" + reclamoPrestacionalNamespace + "cod_area_celular").focus();
		return false;
	}
	if(trim(d_celular).startsWith('0')){
		alert("El número del celular no debe iniciar con cero");
		jQuery("#" + reclamoPrestacionalNamespace + "celular").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_celu).length>0 || trim(d_celular).length>0){
		if(trim(d_cod_area_celu).length+trim(d_celular).length!=10){
			alert("La longitud del código de área + celular debe de ser de 10 caracteres");
			jQuery("#" + reclamoPrestacionalNamespace + "cod_area_celular").focus();
			return false;
		}
	}
	
	
	
	if(!reclamoPrestacional_validarEmail()){
		return false;
	}
	
	var url = reclamoPrestacionalViewConfig.urls.actualizarDomicilio + idPar;
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
				jQuery("#" + reclamoPrestacionalNamespace + "divResultadoActualizarOK").show();
				jQuery("#" + reclamoPrestacionalNamespace + "divBotonActualizar").hide();
				Liferay.Popup.close(popupDomicilio); 
			}	 
		});
} 

function mostrarDomicilioAfiliado(){
	var cuil_titu= jQuery("#" + reclamoPrestacionalNamespace + "cuil").val();
	var inte= jQuery("#" + reclamoPrestacionalNamespace + "inte").val();
	var email;
	var actualizaDomicilio;
	
	
	var url = reclamoPrestacionalViewConfig.urls.buscarAfiliadoDatos;
	   url += cuil_titu;
	   url += '&inte=' + inte;
		
 jQuery.ajax({   
 url: url,
 async:false,
 success: function(data){
	   var obj = jQuery.parseJSON(data);
	   email=obj.email;
	}});
	popupDomicilio= Liferay.Popup({title:reclamoPrestacionalViewConfig.messages.detalleDomicilio,modal:true,width:950,height:330,fixedcenter:true});
	var url1 = reclamoPrestacionalViewConfig.urls.verDomicilio+cuil_titu+'&inte='+inte+'&cmd=view' +'&email='+encodeURI(email);
	jQuery(popupDomicilio).load(url1);
	
}


window[reclamoPrestacionalNamespace + "buscarNomencladorAutocompletar"] = reclamoPrestacional_buscarNomencladorAutocompletar;
window[reclamoPrestacionalNamespace + "buscarNomencladorAutocompletar_edit"] = reclamoPrestacional_buscarNomencladorAutocompletar_edit;
window[reclamoPrestacionalNamespace + "limpiarNomencladorAutocompletar"] = reclamoPrestacional_limpiarNomencladorAutocompletar;
window[reclamoPrestacionalNamespace + "cerrarDivNm"] = reclamoPrestacional_cerrarDivNm;
window[reclamoPrestacionalNamespace + "cerrarNm"] = reclamoPrestacional_cerrarNm;
window[reclamoPrestacionalNamespace + "saveReclamo"] = reclamoPrestacional_saveReclamo;
window[reclamoPrestacionalNamespace + "volverEstadoObservado"] = reclamoPrestacional_volverEstadoObservado;
window[reclamoPrestacionalNamespace + "editaReclamo"] = reclamoPrestacional_editaReclamo;
window[reclamoPrestacionalNamespace + "reabrirReclamo"] = reclamoPrestacional_reabrirReclamo;
window[reclamoPrestacionalNamespace + "agregarRevision"] = reclamoPrestacional_agregarRevision;
window[reclamoPrestacionalNamespace + "verprestacionesasociadas"] = reclamoPrestacional_verprestacionesasociadas;
window[reclamoPrestacionalNamespace + "ocultacontactosdelreclamo"] = reclamoPrestacional_ocultacontactosdelreclamo;
window[reclamoPrestacionalNamespace + "vercontactosdelreclamo"] = reclamoPrestacional_vercontactosdelreclamo;
window[reclamoPrestacionalNamespace + "editarPrestacionSeleccionada"] = reclamoPrestacional_editarPrestacionSeleccionada;
window[reclamoPrestacionalNamespace + "cancelaEdicionPrestacion"] = reclamoPrestacional_cancelaEdicionPrestacion;
window[reclamoPrestacionalNamespace + "agregarPrestacion"] = reclamoPrestacional_agregarPrestacion;
window[reclamoPrestacionalNamespace + "imprimirReclamo"] = reclamoPrestacional_imprimirReclamo;
window[reclamoPrestacionalNamespace + "validarEmail"] = reclamoPrestacional_validarEmail;

window["tipoGestionCierreReclamo"] = tipoGestionCierreReclamo;
window["integracionReclamo"] = integracionReclamo;
window["filtrarLetraComprobante"] = filtrarLetraComprobante;
window["seleccionaCamposNm"] = seleccionaCamposNm;
window["pasarParametrosAParentNm"] = pasarParametrosAParentNm;
window["DatosRevisionOk"] = DatosRevisionOk;
window["ValidarDatosObligatorios"] = ValidarDatosObligatorios;
window["manejartipogestion"] = manejartipogestion;
window["manejarListaPresentes"] = manejarListaPresentes;
window["cambioresolucion"] = cambioresolucion;
window["manejarTipoPedido"] = manejarTipoPedido;
window["cambioTipoPedido"] = cambioTipoPedido;
window["manejarTipoPedidoCierre"] = manejarTipoPedidoCierre;
window["manejarTipoSector"] = manejarTipoSector;
window["controlarEstadoCerrado"] = controlarEstadoCerrado;
window["ValidaDatosReclamo"] = ValidaDatosReclamo;
window["ValidaDatosReclamoEditar"] = ValidaDatosReclamoEditar;
window["validarExisteComprobante"] = validarExisteComprobante;
window["evaluarOnSectorListaEnCero"] = evaluarOnSectorListaEnCero;
window["validarSiNumero"] = validarSiNumero;
window["validaMonto"] = validaMonto;
window["verCrmContacto"] = verCrmContacto;
window["validaMontosEdicion"] = validaMontosEdicion;
window["ValidaMontos"] = ValidaMontos;
window["validarevision"] = validarevision;
window["convertToUppercase"] = convertToUppercase;
window["myXOR"] = myXOR;
window["enterTecla"] = enterTecla;
window["aplicaEstiloBordeRojoDatosObligatorio"] = aplicaEstiloBordeRojoDatosObligatorio;
window["calculatotal"] = calculatotal;
window["seleccionaCamposCieDiez"] = seleccionaCamposCieDiez;
window["limpiaCamposBusquedaCieDiez"] = limpiaCamposBusquedaCieDiez;
window["seteaControlesFacturacionDirecta"] = seteaControlesFacturacionDirecta;
window["desactivaCheckCierre"] = desactivaCheckCierre;
window["abreAutorizacion"] = abreAutorizacion;
window["calculatotalFC"] = calculatotalFC;
window["traerDescripcion"] = traerDescripcion;
window["cambiorecuperable"] = cambiorecuperable;
window["confirmaActualizacionDomicilioAfiliado"] = confirmaActualizacionDomicilioAfiliado;
window["mostrarDomicilioAfiliado"] = mostrarDomicilioAfiliado;
})(window, jQuery);
