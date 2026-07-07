
(function(window, jQuery) {
var cfg = window.ReclamoPrestacionalViewConfig || {};
var ns = cfg.namespace || "";


window.popupMD = window.popupMD;

window.popupDomicilio = window.popupDomicilio;

jQuery('#' + ns + 'divResultadoActualizarOK').hide();

jQuery('#' + ns + 'cantprestacioneslista').val('' + cfg.values.cantPrestacionesLista + '');
jQuery("#" + ns + "busqueda_prestaciones").hide();
jQuery("#" + ns + "busqueda_farmacia").hide();
jQuery("#" + ns + "datos_edicion_prestacion").hide();
jQuery("#" + ns + "Cierre_Reclamo_Div").hide();
/* jQuery("#" + ns + "botoneditareclamo").hide(); */
jQuery("#" + ns + "lista_prestaciones_asociadas").hide();
jQuery("#" + ns + "lista_contactos_reclamo").hide();
jQuery("#" + ns + "justificacion_medica_reclamo").hide();
jQuery("#" + ns + "caso_vinculado").val(cfg.values.casoVinculado);
jQuery('#' + ns + 'reconocidoSSS').attr('readonly', true);


window.addprestacion=false;
window.load =false;
window.sectorIni='';
window.estadoIni='';




jQuery(document).ready(function() {
	window.load = true;
	window.sectorIni = jQuery("#" + ns + "sector").val();
	window.estadoIni = jQuery("#" + ns + "estado").val();

	//jQuery('#' + ns + 'observacion_medica_div').hide();
	if ('EXCEPCION' ==  jQuery("#" + ns + "tipopedido").val()){
		traerDescripcion();
	}			 
	
	
	
	
	if (cfg.values.reclamoCerrado) {            
	
		    jQuery("#" + ns + "tipo_gestion_cierre_reclamo option[value="+cfg.values.tipoGestionCierreReclamo +"]").attr("selected",true);
	
		    jQuery("#" + ns + "observacion_medica option[value="+cfg.values.idObservacionMedica +"]").attr("selected",true);
		    

	}
    tipoGestionCierreReclamo();
    filtrarLetraComprobante();
	integracionReclamo();


});



jQuery("#" + ns + "sector").change(function(){
	
	try {	
   		var valor=jQuery('#' + ns + 'cantprestacioneslista').val();

   		
		if (valor >= 1 && window.load == true){
			
	        var params = "&" + cfg.values.actionParam + "=" + "" + cfg.values.reclamoPrestacionalSeccional + "";
			
			var confirmar = false;
			confirmar=confirm ('Se eliminaran los ítems por no pertenecer al tipo correspondiente '+'\nDesea hacerlo?');
			if(confirmar){
				 var url = cfg.urls.borrarReclamosPrestacionesTodos;
    			 url = url + params;
    			jQuery("#" + ns + "lista_prestaciones_reclamos").load(url);	
			}else{
				jQuery("#" + ns + "sector option[value="+window.sectorIni+"]").attr("selected",true);
			}	
			
		}
   		
	}
	catch (err) {
		alert('error manejarTipoSector ');
	}

});






jQuery("#" + ns + "integracion").change(function(){
	
	try {	

		traerDescripcion();
   		
	}
	catch (err) {
		alert('error integracion ');
	}

});

 
jQuery("#" + ns + "estado").change(function(){
	
	try {	
   		var estado =jQuery('#' + ns + 'estado').val();

   		var chk_amparo =jQuery("#" + ns + "chk_amparo").is(':checked');
   		
   		if (estado == 4 && chk_amparo == false ){
   			alert('Debe seleccionar la marca de Amparo ')	;
		
			jQuery("#" + ns + "estado option[value=1]").attr("selected",true);

   		}
	}
	catch (err) {
		alert('error estado ');
	}

});



jQuery("#" + ns + "tipopedido").change(function(){
	
	try {	
		 filtrarLetraComprobante();
		 
		 integracionReclamo();
	}
	catch (err) {
		alert('error tipopedido ');
	}

});


jQuery("#" + ns + "chk_amparo").change(function(){
	
	try {	
   		var estado =jQuery('#' + ns + 'estado').val();

   		var chk_amparo =jQuery("#" + ns + "chk_amparo").is(':checked');
   		
   		if (estado == 4 && chk_amparo == false){
   			alert ('No puede sacar la marca de aparo si el estado es Incompleto ');
   			jQuery("#" + ns + "chk_amparo").attr('checked', true);
   		}
			
	}catch (err) {
		alert('error chk_amparo ');
	}

});



jQuery("#" + ns + "tipo_gestion_cierre_reclamo").change(function(){
	tipoGestionCierreReclamo();
	
});

jQuery("#" + ns + "observacion_medica").change(function(){
	
	try {	   		
   		jQuery("#" + ns + "reclamo_observacion_cierre").text('');
   		
	}
	catch (err) {
		alert('error observacion_medica text');
	}
});


function tipoGestionCierreReclamo(){
	try {	
   		var tipo_presentes = jQuery('#' + ns + 'presentes').val();
   		var tipo_resolucion = jQuery('#' + ns + 'tipo_gestion_cierre_reclamo').val();
		
		if ( tipo_resolucion == 5){   
			jQuery('#' + ns + 'observacion_medica_tr').show();
   		}else{
   			jQuery('#' + ns + 'observacion_medica_tr').hide();
   		}
	}
	catch (err) {
		alert('error observacion_medica ');
	}
}


function integracionReclamo(){
	try {	
		 if ('EXCEPCION' ==  jQuery("#" + ns + "tipopedido").val()){
			 jQuery('#integracion_label').show();
			 jQuery('#' + ns + 'integracion').show();
			 jQuery('#integracion_desc').show();
			 jQuery('#integracion_div').show();
		 }else {
			 jQuery('#integracion_label').hide();
			 jQuery('#' + ns + 'integracion').hide();
			 jQuery('#integracion_desc').show();
			 jQuery('#integracion_div').hide();


		 }	
	}
	catch (err) {
		alert('error integracion ');
	}
}


/* var data=jQuery('#' + ns + 'estado').val();
document.getElementById("" + ns + "estadosel").value = data; */

jQuery("#" + ns + "idreclamoprestacion").val("0");
if (cfg.values.hasReclamo) {
jQuery("#" + ns + "idreclamoprestacion").val(cfg.values.idReclamo);
/* jQuery("#" + ns + "botoneditareclamo").show(); */
jQuery("#" + ns + "botonsavereclamo").hide();
      if (cfg.values.reclamoCerrado) {            
            jQuery("#" + ns + "Cierre_Reclamo_Div").show();
            jQuery("#" + ns + "botonrevision").hide();
          

            
      }      
manejarTipoPedidoCierre();      
manejarTipoSector();

if (cfg.values.resolucionEvaluada) { 
	// oculta boton de agregar porque existe una evaluacion de rECHAZO o APROBACION no de baja
	jQuery("#" + ns + "botonrevision").hide();
	jQuery("#" + ns + "mensajerevisionefectuada").html("Revision Efectuada, el Sistema soporta solo una revision activa (No de baja).");
} 

}


if (!cfg.values.esEdicion) {
    /* jQuery("#" + ns + "botoneditareclamo").hide();   */  
    /* document.getElementById("" + ns + "sector").disabled = "disabled"; */
    
    document.getElementById("" + ns + "reclamo_observacion_cierre").disabled = "disabled";
    
    jQuery("#" + ns + "botonrevision").hide();
    jQuery("#" + ns + "buttonaddprestacion").hide();    
    
    //document.getElementById("" + ns + "buscadorcie10buscador").disabled = "disabled";
       
    
    
}



function filtrarLetraComprobante() {
	var tipoPedido = jQuery("#" + ns + "tipopedido").val();
	var url = cfg.urls.filtrarLetraComprobante+tipoPedido;
	jQuery("#" + ns + "comprobante_letra").attr('disabled', 'disabled');
	
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			document.getElementById("" + ns + "comprobante_letra").length = 0;
			jQuery("#" + ns + "comprobante_letra").removeAttr('disabled');
			var obj = jQuery.parseJSON(data);
			jQuery('#' + ns + 'comprobante_letra').html(data).fadeIn();

		}
	});
}



//  if (cfg.values.esEdicion) {
// AcomodarControlesEdicion();
// } 


aplicaEstiloBordeRojoDatosObligatorio();

//  function  AcomodarControlesEdicion() {
// 	// HEADER DATOS INHABILITADOS   
// 	                         
// 	                         document.getElementById("" + ns + "sector").disabled = "disabled";
// 	                         if (cfg.values.hasTipoPedido) {
// 	                         if ( document.getElementById("" + ns + "tipopedido").selectedIndex!=0) {
// 	                        	 document.getElementById("" + ns + "tipopedido").disabled = "disabled"; 
// 	                         }	                            
// 	                         }
// 						 	 document.getElementById("" + ns + "fechaospimDia").disabled = true;
// 							 document.getElementById("" + ns + "fechaospimMes").disabled = true;
// 							 document.getElementById("" + ns + "fechaospimAnio").disabled = true;
// 	// DATOS DE REVISION
// 	                         jQuery("#" + ns + "botoneditareclamo").show();
// 	                         document.getElementById("" + ns + "estado").disabled = "";
//                         	 document.getElementById("" + ns + "fecharevisionDia").disabled = "";
// 							 document.getElementById("" + ns + "fecharevisionMes").disabled = "";
// 							 document.getElementById("" + ns + "fecharevisionAnio").disabled = "";
// 							 document.getElementById("" + ns + "observacion_revision").disabled = "";
// 							 document.getElementById("" + ns + "chk_amparo").disabled = "";
// 							 document.getElementById("" + ns + "chk_superintendencia").disabled = "";
// 							 document.getElementById("" + ns + "chk_recuperable").disabled = "";						 	 						 
// 							 document.getElementById("" + ns + "chk_entramite").disabled = "";
// 							 document.getElementById("" + ns + "resolucion").disabled = "";
// 							 document.getElementById("" + ns + "respresolucion").disabled = "";
// 							 document.getElementById("" + ns + "presentes").disabled = "";
// 		// DATOS DE CIERRE NO ES NECESARIO
// 		 					/*  document.getElementById("" + ns + "fechacierreDia").disabled = false;
// 							 document.getElementById("" + ns + "fechacierreMes").disabled = false;
// 							 document.getElementById("" + ns + "fechacierreAnio").disabled = false;
// 							
// 							
// 							 document.getElementById("" + ns + "tipo_gestion_cierre_reclamo").disabled = "";							
// 							 
// 							 document.getElementById("" + ns + "reclamo_observacion_cierre").disabled = false;
// 							 document.getElementById("" + ns + "reclamo_ps_factura_ospim").disabled = "";
// 							 document.getElementById("" + ns + "reclamo_a_negociar").disabled = ""; */
// 							 
// 	} 



function buscarNomencladorAutocompletar(){
	var nombre_nomenclador=jQuery("#" + ns + "descripcionSeguimiento_filtro").val();
	var codigo_nomenclador=jQuery("#" + ns + "codigoSeguimiento_filtro").val();
    var tipoNomenclador=jQuery("#" + ns + "tipoNomencladorSeguimiento_filtro").val();
    
    // Marca ReinLiq no se utiliza en esta busqueda
    var marcaReinliq=null;
	if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
        alert('' + cfg.messages.ingreseParametrosBusqueda + ''); 
    }else {
    	if(window.popupMD==null)
    		window.popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { window.popupMD = null;}});
    	
    	
    	if(tipoNomenclador==8){
    		marcaReinliq=6;
    	}    

    	var esPrestMed = 0;
    	sector = jQuery("#" + ns + "sector").val();
    	if (sector == "PRESTACIONES MEDICAS")
    		esPrestMed = 1;
    		    	
	    var url = cfg.urls.buscarNomenclador;
	    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&tiponomenclador='+tipoNomenclador +'&codigonomenclador='+encodeURI(codigo_nomenclador)+'&soloActivos=true';
	    url += '&marcareinliq='+marcaReinliq+'&esPrestMed='+esPrestMed;
	    	   
	    jQuery(window.popupMD).load(url);
    }
}


function buscarNomencladorAutocompletar_edit(){
	var nombre_nomenclador=jQuery("#" + ns + "descripcionSeguimiento_filtro_edit").val();
	var codigo_nomenclador=jQuery("#" + ns + "codigoSeguimiento_filtro_edit").val();
    var tipoNomenclador=jQuery("#" + ns + "tipoNomencladorSeguimiento_filtro_edit").val();
    tipoNomenclador = '0';   
	if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
        alert('' + cfg.messages.ingreseParametrosBusqueda + ''); 
    }else {
    	if(window.popupMD==null)
    		window.popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { window.popupMD = null;}});
    	
	    var url = cfg.urls.buscarNomenclador;
	    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&tiponomenclador='+tipoNomenclador +'&codigonomenclador='+encodeURI(codigo_nomenclador)+'&soloActivos=true';
	    jQuery(window.popupMD).load(url);
    }
}


function limpiarNomencladorAutocompletar(){	
	jQuery("#" + ns + "descripcionSeguimiento_filtro").val('');
	jQuery("#" + ns + "codigoSeguimiento_filtro").val('');
	jQuery("#" + ns + "descripcionSeguimiento_filtro_edit").val('');
	jQuery("#" + ns + "codigoSeguimiento_filtro_edit").val('');
}

//  function siguienteSolapa() {		
// 	 			
// 		var accionEnCurso = document[ns + "prestador_fm"][ns + cfg.values.cmdParam].value;
// 		document[ns + "prestador_fm"][ns + cfg.values.cmdParam].value='' + cfg.values.moveCommand + '';
// 		
// 		var url = cfg.urls.editarReclamoEntry;
// 		url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest' + "&esDatosTab=true";
// 		
// 		document[ns + "prestador_fm"].method = 'post';
// 		submitForm(document[ns + "prestador_fm"], url);
// 	  
// } 	 
 
function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
	jQuery('#' + ns + 'codigoSeguimiento_filtro').val(codigo);
	jQuery("#" + ns + "descripcionSeguimiento_filtro").val(descripcion);
	jQuery("#" + ns + "nom_seleccionado").val("1"); // selecciona el tipo de nomenclador	 
	jQuery('#' + ns + 'tipoNomenclador').val(tipoNomenclador);
	
	
	jQuery('#' + ns + 'codigoSeguimiento_filtro_edit').val(codigo);
	jQuery("#" + ns + "descripcionSeguimiento_filtro_edit").val(descripcion);
	jQuery("#" + ns + "nom_seleccionado_edit").val("1"); // selecciona el tipo de nomenclador	 
	jQuery('#' + ns + 'tipoNomenclador_edit').val(tipoNomenclador);
	
	Liferay.Popup.close(window.popupMD);

}

function pasarParametrosAParentNm(tipoNomenclador,codigo,descripcion) {	
	seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
    cerrarNm();
}


function cerrarDivNm(){
	jQuery("#divSeguimientoSur").hide("slow");
}

function cerrarNm(){
	cerrarDivNm();
	if(window.popupMD){
		Liferay.Popup.close(window.popupMD);
	}
}


function DatosRevisionOk(){
	 
	var dianro  = jQuery("#" + ns + "fecharevisionDia").val();
	var mesnro  = jQuery("#" + ns + "fecharevisionMes").val()  ;
	var anionro   = jQuery("#" + ns + "fecharevisionAnio").val();
	 
	  
	if (dia || mes || anio){
	   alert("Debe ingresar la fecha de Revisi\u00F3n");
		return false ;
	}
	if (dia || mes || anio ||  jQuery('#' + ns + 'resolucion').val()=='' ){
		   alert("Debe ingresar la resoluci\u00F3n");
			return false ;
		}
		
	var resolucion   =document.getElementById("" + ns + "resolucion");
	if (resolucion.selectedIndex==0){
		alert('Debe seleccionar el tipo de resolucion de la lista.');
		return false ;
	}		
	
	var diaExist  = isNaN(parseInt(jQuery("#" + ns + "fecharevisionDia").val()));
	var mesExist  = isNaN(parseInt(jQuery("#" + ns + "fecharevisionMes").val()));
	var anioExist   = isNaN(parseInt(jQuery("#" + ns + "fecharevisionAnio").val()));
	
	var dia  = jQuery('#' + ns + 'fechaospimDia').val();
	var mes  = jQuery("#" + ns + "fechaospimMes").val() ;
	var anio   = jQuery("#" + ns + "fechaospimAnio").val();
	
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
	valor=jQuery('#' + ns + 'cantprestacioneslista').val();
	
	
	var dia  = isNaN(parseInt(jQuery("#" + ns + "fechaospimDia").val()));
	var mes  = isNaN(parseInt(jQuery("#" + ns + "fechaospimMes").val()));
	var anio   = isNaN(parseInt(jQuery("#" + ns + "fechaospimAnio").val()));
	
	var dia1  = isNaN(parseInt(jQuery("#" + ns + "fechaseccionalDia").val()));
	var mes1  = isNaN(parseInt(jQuery("#" + ns + "fechaseccionalMes").val()));
	var anio1   = isNaN(parseInt(jQuery("#" + ns + "fechaseccionalAnio").val()));	
	
	
	var dia2  = isNaN(parseInt(jQuery("#" + ns + "fechacierreDia").val()));
	var mes2  = isNaN(parseInt(jQuery("#" + ns + "fechacierreMes").val()));
	var anio2   = isNaN(parseInt(jQuery("#" + ns + "fechacierreAnio").val()));
	
	
	var msgs = ["Error en la fecha Ospim.", "Debe seleccionar el sector que inicia  el reclamo.", "Debe seleccionar el estado del reclamo.","Debe seleccionar al Afiliado asociado al reclamo.","Complete la Fecha Seccional o dejela en blanco","Debe seleccionar el tipo de Pedido"]; 
	var condiciones =[5];
	var controles  =[5];
		
	var tipoSelectsector  =document.getElementById("" + ns + "sector");
	var tipoSelectestado  =document.getElementById("" + ns + "estado");
	var tipoSelecttipopedido =document.getElementById("" + ns + "tipopedido");
	/* document.getElementById("" + ns + "tipopedido").selectedIndex==0 */
	var cuil=jQuery('#' + ns + 'cuil').val();
	var inte=jQuery('#' + ns + 'inte').val();	
	
	
	
	var  resp=true;
	
	controles[0]=document.getElementById("" + ns + "fechaospimDia"); 	
	controles[1]=tipoSelectsector;
	controles[2]=tipoSelectestado; 	
	controles[3]=document.getElementById("" + ns + "cuil");	
	controles[4]=document.getElementById("" + ns + "fechaseccionalDia");
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
	var idgestion = jQuery('#' + ns + 'tipo_gestion_cierre_reclamo').val();
	
	var justificacion=jQuery('#' + ns + 'justificacionmedcica_reclamo').val();

	
	if (idgestion == 0  && jQuery('#' + ns + 'estado option:selected').text().trim() == 'CERRADO' ){
		alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
		document.getElementById("" + ns + "tipo_gestion_cierre_reclamo").focus();
		return false;
	}
	
	/* if (idgestion==5){ */
	if (idgestion==5){
	/* 	var isDisabled = jQuery('#' + ns + 'dosporciento').is(':disabled');			
	    if (!isDisabled) { */
			if(! confirm("Al seleccionar la opción RECHAZADO el sistema rechazará todas las prestaciones del caso, no podrá asociarlas a reintegros. Está seguro ?")){
				return false;	
			/* } */
	    }	
	}
		var respResolucion = document.getElementById("" + ns + "respresolucion");
		
		if ( jQuery('#' + ns + 'auditoriaadministrativa').val()!="Ok" ){ // auditoria administrativa 

			if (justificacion.length ==0  && resp ){ // no hay revisiones activas 
				alert('Tiene que ingresar la justificación médica del Caso para efectuar el Cierre del Caso.');
				jQuery('#' + ns + 'justificacionmedcica_reclamo').focus();
				resp=false;
			}		
		}
		// validar si 
		if (idgestion<1  && resp && jQuery('#' + ns + 'estado option:selected').text() == 'CERRADO' ){
			alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
			document.getElementById("" + ns + "tipo_gestion_cierre_reclamo").focus();
			resp=false;
		}
		
			if ((dia2 || mes2 || anio2)  && resp )  {
				alert('Debe ingresar la fecha de Cierre del Reclamo');
				document.getElementById("" + ns + "fechacierreDia").focus();
				resp=false;
			}
		
		if(tipoSelecttipopedido == 3){ //si estado = cerrado
			if (jQuery('#' + ns + 'cantrevisionesactivas').val()<1  && resp ){ // no hay revisiones activas 
				alert('Recuerde, debe tener registrada por lo menos una revisión activa para el cierre del caso!!!!.');			
				resp=false;
			}
		}
		
		
// SI ES CIERRE DEL CASO NO SE CONTROLA SI SE DIERON DE BAJA TODAS LAS PRESTACIONES

	valor=jQuery('#' + ns + 'cantprestacioneslista').val();
	

    if (Edicion && window.addprestacion) {
    	if (valor <1   && resp){
    		alert('Debe tener ingresada por lo menos una prestación');
    		resp=false;
    	}
    }else{
    		if (valor <1  && resp ){
    			
    		}	
    }
    
    var integracion = jQuery("#" + ns + "integracion").val();
	 if ('EXCEPCION' ==  jQuery("#" + ns + "tipopedido").val()){
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
	 var baja =  jQuery('#' + ns + 'baja_fecha').val();
	 var url = cfg.urls.validarReclamoAfiliadoPrestaciones;
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


function saveReclamo() {
	
/* 	// para el alta
	habilitarControlesCierre();	 */
	
	if ( ValidarDatosObligatorios(false))  {
				
	/*esta chanchada es porque el action toma el id de cierre de tipogestion que es un hidden y no de tipo_gestion_cierre_reclamo*/
	var idgestion=jQuery('#' + ns + 'tipo_gestion_cierre_reclamo').val();
	jQuery('#' + ns + 'tipogestion').val(idgestion);
		
	var accionEnCurso = document[ns + "reclamo_fm"][ns + cfg.values.cmdParam].value;
	document[ns + "reclamo_fm"][ns + cfg.values.cmdParam].value='' + cfg.values.saveCommand + '';
	
	/* 	var chk_amparo=jQuery("#" + ns + "chk_amparo").is(':checked');
		var chk_superintendencia=jQuery("#" + ns + "chk_superintendencia").is(':checked');
		var chk_recuperable = jQuery("#" + ns + "chk_recuperable").is(':checked');	
		var chk_entramite = jQuery("#" + ns + "chk_entramite").is(':checked');	 */	
	
		var url = cfg.urls.editarReclamoEntry;
		url = url + "&esDatosTab=true";
		document[ns + "reclamo_fm"].method = 'post';
		submitForm(document[ns + "reclamo_fm"], url);		
	
	}							  	
}

/* Cambia estado a Observado */
function volverEstadoObservado() {

	var confirmar = false;
	/* Recupera el Id del Reclamo */
	var idgestion=jQuery('#' + ns + 'id_reclamosel').val();
	
	confirmar=confirm ('Estas observando la precarga, la misma será devuelta ' + 
			'a la seccional. ' + '\nEstas seguro?');
	
	if(confirmar) {  	
		popup = Liferay.Popup({title:"" + cfg.messages.observacionInterna + "",modal:true,width:700});
		var url = cfg.urls.observar;
		url = url + "&idReclamo=" + idgestion;
		jQuery(popup).load(url);		
	}  
}

function editaReclamo(fromAutoriza) {
	
	if (fromAutoriza) {
		abreAutorizacion();
	}
	
	if ( ValidarDatosObligatorios(true))  {
		
	  /* var data=jQuery('#' + ns + 'estado').val();
	  if ( document.getElementById("" + ns + "estadosel").value == data){	 	
		 document.getElementById("" + ns + "estado").value="0";		
	  } */

	 /*  if ( document.getElementById("" + ns + "tipopedido").disabled = "disabled"){
		document.getElementById("" + ns + "tipopedido").disabled = "";	
	  } */
	
	  /*esta chanchada es porque el action toma el id de cierre de tipogestion que es un hidden y no de tipo_gestion_cierre_reclamo*/
		var idgestion=jQuery('#' + ns + 'tipo_gestion_cierre_reclamo').val()
		jQuery('#' + ns + 'tipogestion').val(idgestion);
	    //jQuery('#' + ns + 'id_reclamosel').val(0);
	  
	  var accionEnCurso = document[ns + "reclamo_fm"][ns + cfg.values.cmdParam].value;
	  document[ns + "reclamo_fm"][ns + cfg.values.cmdParam].value='' + cfg.values.updateCommand + '';

	  /* habilitarControlesCierre(); */
	  
	  

	
	  var url = cfg.urls.editarReclamoEntry;
	  url = url + "&esDatosTab=true";
	  document[ns + "reclamo_fm"].method = 'post';

	  
	  submitForm(document[ns + "reclamo_fm"], url);
		
	  /* onOffControlesRequest(true); */
	}							  	
}


function reabrirReclamo(fromAutoriza) {
	
	if (fromAutoriza) {
		abreAutorizacion();
	}
	
		
/* 	  var data=jQuery('#' + ns + 'estado').val();
	  if ( document.getElementById("" + ns + "estadosel").value == data){	 	
		 document.getElementById("" + ns + "estado").value="0";		
	  } */
	
	/*   if ( document.getElementById("" + ns + "tipopedido").disabled = "disabled"){
		document.getElementById("" + ns + "tipopedido").disabled = "";	
	  } */
	
	  var accionEnCurso = document[ns + "reclamo_fm"][ns + cfg.values.cmdParam].value;
	  document[ns + "reclamo_fm"][ns + cfg.values.cmdParam].value='' + cfg.values.restoreCommand + '';

	  /* habilitarControlesCierre(); */
	
	  var url = cfg.urls.editarReclamoEntry;
	  url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest' + "&esDatosTab=false";
	
	  document[ns + "reclamo_fm"].method = 'post';
	
	  submitForm(document[ns + "reclamo_fm"], url);
		
/* 	  onOffControlesRequest(true); */
							  	
}




function manejartipogestion(){

	/* var tipoGestionArray = jQuery('#' + ns + 'tipo_gestion_cierre_reclamo').val().split("|");	 */
	var idgestion = jQuery('#' + ns + 'tipo_gestion_cierre_reclamo').val();	
	/* var idgestion =tipoGestionArray [0];	 */
	var sector=jQuery('#' + ns + 'sector').val();
	var nroLote=jQuery('#' + ns + 'nroLote').val();
	jQuery('#' + ns + 'tipogestion').val(idgestion);	
	if("1"==idgestion && sector=="PRESTACIONES MEDICAS" && (nroLote==null || nroLote=="" || nroLote=="0")){
		
		 var url = cfg.urls.proponeLoteReclamoPrestacional;		 
			jQuery.ajax({   
				url: url,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					jQuery('#' + ns + 'nroLote').val(obj.lote);
				}
			}); 
	}
	if("1"!=idgestion || sector!="PRESTACIONES MEDICAS"){
		jQuery('#' + ns + 'nroLote').val("");
	}
	
	
	
}


function manejarListaPresentes(){
	var tipoSelect  =document.getElementById("" + ns + "presenteslista");
	jQuery("#" + ns + "presentes").val(tipoSelect.value); // asigna el valor de la lista al control oculto 
}


function cambioresolucion(){
	
	try{
		var tipoSelect  =document.getElementById("" + ns + "resolucion");
		var justificacion=jQuery('#' + ns + 'justificacionmedcica_reclamo').val();
		if  (tipoSelect.selectedIndex>0 && justificacion.length ==0  && document.getElementById("" + ns + "respresolucion").selectedIndex!=1){
				jQuery('#' + ns + 'justificacionmedcica_reclamo').focus();
				tipoSelect.selectedIndex=0;
				alert('Tiene que ingresar la Justificacion Medica del Caso para ingresar la revision.');			
			}	

	}catch (err) {}	
	
}


function manejarTipoPedido(){
	var tipoPedido =document.getElementById("" + ns + "tipopedido");
	if ( tipoPedido.selectedIndex==0 ){
		alert("El tipo de pedido es obligatorio");	
		document.getElementById("" + ns + "tipopedido").focus();
	}
	//if(tipoPedido.value!="EXTRACAPITA"){
	//	jQuery("#" + ns + "comprobante_letra").append(new Option("A", "A"));
	//}
	
}

function cambioTipoPedido(){
	var tipoSector =document.getElementById("" + ns + "sector");
	if(tipoSector.selectedIndex!=0){
		manejarTipoSector();
	}
}


function manejarTipoPedidoCierre(){
	var tipoPedido  = document.getElementById("" + ns + "tipopedido");
	jQuery('#' + ns + 'tipo_gestion_cierre_reclamo').html('');  //vacio lista opciones del select
/* 	jQuery("#" + ns + "tipo_gestion_cierre_reclamo").append(new Option("SELECCIONE LA GESTION", "0"));
	document.getElementById("" + ns + "tipopedido").selectedIndex==0 */
	jQuery("#" + ns + "tipo_gestion_cierre_reclamo").append(new Option("SELECCIONE UNA OPCION", "0"));
	jQuery("#" + ns + "tipo_gestion_cierre_reclamo option[value='0']").attr("selected", true);
	if(tipoPedido.value=="EXCEPCION"){
		jQuery("#" + ns + "tipo_gestion_cierre_reclamo").append(new Option("FACTURACION DIRECTA", "3"));
		jQuery("#" + ns + "tipo_gestion_cierre_reclamo").append(new Option("PAGADO POR MECANISMO INTEGRACION", "6"));
		/* jQuery("#" + ns + "tipo_gestion_cierre_reclamo option[value='3']").attr("selected", true); //FACT. DIRECTA */
	}
	if(tipoPedido.value=="REINTEGRO"){
		jQuery("#" + ns + "tipo_gestion_cierre_reclamo").append(new Option("REINTEGRO", "4"));
		/* jQuery("#" + ns + "tipo_gestion_cierre_reclamo option[value='4']").attr("selected", true); //REINTEGRO */
	}
	if(tipoPedido.value=="EXTRACAPITA"){
		jQuery("#" + ns + "tipo_gestion_cierre_reclamo").append(new Option("EXTRACAPITA", "1"));
		/* jQuery("#" + ns + "tipo_gestion_cierre_reclamo option[value='1']").attr("selected", true); //EXTRACAPITA */
	}
	jQuery("#" + ns + "tipo_gestion_cierre_reclamo").append(new Option("RECHAZADO", "5"));
}

function manejarTipoSector(){
	var tipoSector  =document.getElementById("" + ns + "sector");
	var tipopedido  = document.getElementById("" + ns + "tipopedido");
	try {
		jQuery("#" + ns + "busqueda_prestaciones").show();
		jQuery("#" + ns + "busqueda_farmacia").hide();
		jQuery("#" + ns + "nom_seleccionado").val("1"); // se selecciono maestra de prestaciones medicas 
		jQuery('#' + ns + 'troquel').val("");  
		jQuery('#' + ns + 'codigoSeguimiento_filtro').val("");
		jQuery("#" + ns + "tipoNomencladorSeguimiento_filtro").val("");
		
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
	   				jQuery("#" + ns + "busqueda_farmacia").show();       
	  				 jQuery("#" + ns + "busqueda_prestaciones").hide();    		
				}
   				
   					       
  		         jQuery("#" + ns + "nom_seleccionado").val("2"); // se selecciono maestra de farmacia
   	   		}else{
   	   		     jQuery("#" + ns + "tipoNomencladorSeguimiento_filtro").val(9);  // farmacia
   	   		}	 
        }
   		if (tipoSector.selectedIndex==1){     	       
   			jQuery("#" + ns + "tipoNomencladorSeguimiento_filtro").val(8); // discapacidad 
   		} else if (tipoSector.selectedIndex==6){
   			/* ODONTOLOGIA Tipo Nomenclador 1 */
   			jQuery("#" + ns + "tipoNomencladorSeguimiento_filtro").val(1); // discapacidad	
   		}
	}
	catch (err) {
		alert('error manejarTipoSector() ');
	}
}






function agregarRevision() {		
     
	var  revisionConCierre =false;
	
	if ( DatosRevisionOk())  {
		
		var resolucion = jQuery('#' + ns + 'resolucion').val();
		
		var presentes = jQuery('#' + ns + 'presentes').val();
		var respresolucion = jQuery('#' + ns + 'respresolucion').val();		
		var revisionFechaVtoDia = jQuery('#' + ns + 'fecharevisionDia').val(); 
		var revisionFechaVtoMes = jQuery('#' + ns + 'fecharevisionMes').val(); 
		var revisionFechaVtoAnio = jQuery('#' + ns + 'fecharevisionAnio').val();
		
		var observacionMedica = jQuery('#' + ns + 'observacion_medica').val();

		
		
		var reclamoobservacion  = jQuery('#' + ns + 'observacion_revision').val();
		var chk_amparo=jQuery("#" + ns + "chk_amparo").is(':checked');
		var chk_superintendencia=jQuery("#" + ns + "chk_superintendencia").is(':checked');
		var chk_recuperable = jQuery("#" + ns + "chk_recuperable").is(':checked');
		var chk_entramite = jQuery("#portlet:namespace />chk_entramite").is(':checked');
	
	    if (document.getElementById("" + ns + "resolucion").selectedIndex==0 ) {
	    	resolucion="";     
	    }
	    if (document.getElementById("" + ns + "presentes").selectedIndex==0 ) { 
	    	presentes="";     
	    }
	    if (document.getElementById("" + ns + "respresolucion").selectedIndex==0 ) {
	    	respresolucion="";     
	    }
	    jQuery('#' + ns + 'auditoriaadministrativa').val('');
	    if (document.getElementById("" + ns + "respresolucion").selectedIndex==1 ) {
	    	jQuery('#' + ns + 'auditoriaadministrativa').val('Ok');
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
			
		
		var url = cfg.urls.listaRevisionesReclamo;
		
		
		if (resolucion.toUpperCase()!="AUTORIZADO"){
			if(confirm("Confirma el Cierre del Caso con el Rechazo en la revision ?")){
	 			    /* var estadoSelectsector  =document.getElementById("" + ns + "estado"); */
				    //estadoSelectsector.selectedIndex = 2; // setea el estado en cerrado
				    /* estadoSelectsector.selectedIndex = ubicacionOpcionEstadoCerradoCombo();	 */		    
				    /* jQuery("#" + ns + "estado option[value='3']").attr("selected", true); //CERARADO */
				    jQuery("#" + ns + "estado option[value='CERRADO']").attr("selected",true);
				    controlarEstadoCerrado(); // hace visible los controles del estado cerrado
				    
				    document.getElementById("" + ns + "tipo_gestion_cierre_reclamo").disabled = false;
					
					var tipoSelectsector  =document.getElementById("" + ns + "tipo_gestion_cierre_reclamo");
					
					seteaControlesFacturacionDirecta(true);
					/* tipoSelectsector.selectedIndex= ubicacionOpcionRechazadoenCombo(); */
				    /* jQuery("#" + ns + "tipo_gestion_cierre_reclamo option[value='5']").attr("selected", true); //RECHAZADO */
				    jQuery("#" + ns + "tipo_gestion_cierre_reclamo option[value='RECHAZADO']").attr("selected",true);

					/* var tipoGestionArray=jQuery('#' + ns + 'tipo_gestion_cierre_reclamo').val().split("|"); */
					var idgestion=jQuery('#' + ns + 'tipo_gestion_cierre_reclamo').val()
					
					/* var idgestion =tipoGestionArray [0]; */
					jQuery('#' + ns + 'tipogestion').val(idgestion);				
					jQuery('#' + ns + 'reclamo_observacion_cierre').val('RECHAZO DE LA PRESTACION EN LA REVISION.');
					revisionConCierre=true;
					jQuery('#' + ns + 'cantrevisionesactivas').val(1); // para que no valide esto
					desactivaCheckCierre();							
					
	 		}else{
					return false;	
			}	
		}
			
		// oculta boton de agreagr revision porque solo se admite un aprobacion o un rechazo no hay parciales dentro del reclamo
		jQuery("#" + ns + "botonrevision").hide();
		jQuery("#" + ns + "mensajerevisionefectuada").html("Revisión Efectuada, el Sistema soporta solo una revisión activa (No de baja).");
	
	 	jQuery('#' + ns + 'lista_revisiones').load(url,params, function(){
															jQuery('#' + ns + 'buscando').hide();            															
														  });
	 	
		 jQuery('#' + ns + 'resolucion').val('');
		 jQuery('#' + ns + 'presentes').val('');
		 jQuery('#' + ns + 'respresolucion').val('');	  	  	  
		 document.getElementById("" + ns + "fecharevisionDia").selectedIndex = 0;	 
		 document.getElementById("" + ns + "fecharevisionMes").selectedIndex = 0;	 
		 document.getElementById("" + ns + "fecharevisionAnio").selectedIndex = 0;
		 document.getElementById("" + ns + "fecharevisionAnio").selectedIndex = 0;
		 jQuery('#' + ns + 'observacion_revision').val('');
		 if (cfg.values.hasReclamo) {
		 	if (revisionConCierre==true){			 
		 		editaReclamo(false); 
		 	}
		 } else {
		 	if (revisionConCierre==true){
			 saveReclamo();
		 	}
		 }
	}
}       		

/* function ubicacionOpcionRechazadoenCombo(){
	var idselect;
	var pos=0;
	var posicion=0;
		jQuery('#' + ns + 'tipo_gestion_cierre_reclamo option').each(function(){
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
		jQuery('#' + ns + 'estado option').each(function(){
        	tipoGestionArray = jQuery(this).val().split("|");
        	idselect =tipoGestionArray [0];         
        	if (idselect == 3){
        	 	posicion=pos;
	        }
        	pos=pos+1;
        });
	return posicion;
} */

function verprestacionesasociadas() {
	
	if (document.getElementById("" + ns + "botonprestacionesasociadas").value=='Ver Prestaciones del Caso Asociado.'){
		jQuery("#" + ns + "lista_prestaciones_asociadas").show();
		document.getElementById("" + ns + "botonprestacionesasociadas").value='Ocultar Prestaciones del Caso Asociado.';
	}else{
		jQuery("#" + ns + "lista_prestaciones_asociadas").hide();
		document.getElementById("" + ns + "botonprestacionesasociadas").value='Ver Prestaciones del Caso Asociado.';
	}
}

function ocultacontactosdelreclamo() {
	jQuery("#" + ns + "lista_contactos_reclamo").hide();
	jQuery("#" + ns + "botoncontactosreclamo").show();
	jQuery("" + ns + "botoncontactosreclamo").value='Ver Contactos Asociados al Caso.';

}


function vercontactosdelreclamo() {
		
	var cuil=jQuery('#' + ns + 'cuil').val();
	var inte=jQuery('#' + ns + 'inte').val();
	var idreclamoprestacion=jQuery('#' + ns + 'idreclamoprestacion').val();
	var modoconsulta=jQuery('#' + ns + 'consultareclamo').val();
	
	    if ((cuil=="" || inte=="" )){		
			alert ('Debe seleccionar al Afiliado para ver sus contactos.');
			document.getElementById("" + ns + "cuil").focus();
			return false;
		}	    
			
	    if (document.getElementById("" + ns + "botoncontactosreclamo").value=='Ver Contactos Asociados al Caso.'){
		jQuery("#" + ns + "lista_contactos_reclamo").show();
		jQuery("#" + ns + "botoncontactosreclamo").hide();
		jQuery("#" + ns + "justificacion_medica_reclamo").hide();
		
		var cuil=jQuery('#' + ns + 'cuil').val();
		var inte=jQuery('#' + ns + 'inte').val();
		var idreclamoprestacion=jQuery('#' + ns + 'idreclamoprestacion').val();		
		
		if ( jQuery("#" + ns + "idreclamoprestacion").val()<1 
				&&  ((cuil==jQuery("#" + ns + "cuiltitular").val()  
						&& inte==jQuery("#" + ns + "intetitular").val() ))  ){			
			return false; // es el mismo afiliado 
		}		
		
		jQuery("#" + ns + "cuiltitular").val(cuil);
		jQuery("#" + ns + "intetitular").val(inte);
		
		var params = {"cuil_contacto":cuil,"inte_contacto":inte,"idreclamoprestacion":idreclamoprestacion,"modoconsulta":modoconsulta};

		var url = cfg.urls.listaContactosReclamo;
		
		jQuery('#' + ns + 'lista_contactos_reclamo').load(url,params, function(){
										jQuery('#' + ns + 'buscando').hide();          															
															  });			 	 
		}					
	}
	

function editarPrestacionSeleccionada(tipoAccion) {
	//tipoAccion=1 edicion 
	//tipoAccion=2 Autorizacion prestacion 
	//tipoAccion=3 Rechazo de  prestacion	
		
	var frecuencia= jQuery('#' + ns + 'frecuenciaEdicion').val();
	var cantidad =  jQuery('#' + ns + 'cantidadEdicion').val();
	var importe = jQuery('#' + ns + 'importeEdicion').val();
	var cargoospim= jQuery('#' + ns + 'cargoospimEdicion').val();
	var cargops= jQuery('#' + ns + 'cargopsEdicion').val();
	var cargoimesa= jQuery('#' + ns + 'cargoimesaEdicion').val();
	var reconocidoSSS= jQuery('#' + ns + 'reconocidoSSSEdicion').val();
	var observaciones= jQuery('#' + ns + 'observacion_prestacionEdicion').val();
    var prestacion= "Graba Edicion";
    var idprestacion =  jQuery("#" + ns + "codigoprestacion").val();
    var idRegistro=jQuery('#' + ns + 'idRegistro').val();

    var estadoAprobacion = tipoAccion;
    var recuperableSur  =  jQuery('#' + ns + 'recuperable_surEdicion').val();  
    
    var cpbteTipo=jQuery('#' + ns + 'comprobante_tipo_edicion').val();

    var cpbteNro=jQuery('#' + ns + 'comprobante_nro_edicion').val();
    var cpbteDia=jQuery('#' + ns + 'fechaComprobanteDiaEdicion').val();
    var cpbteMes=jQuery('#' + ns + 'fechaComprobanteMesEdicion').val();
    var cpbteAnio=jQuery('#' + ns + 'fechaComprobanteAnioEdicion').val();
    var cpbteCantidad=jQuery('#' + ns + 'cantidadFC_edicion').val();
    var cpbteImporte= jQuery('#' + ns + 'importeUnitarioFC_edicion').val();
    var importeFC = jQuery('#' + ns + 'importeFC_edicion').val();
    var cpbteCuit=jQuery('#' + ns + 'cuit_entidad_edicion').val();
    var cpbteSucursal=jQuery('#' + ns + 'comprobante_suc_edicion').val();
    var cpbteCuitSucursal=jQuery('#' + ns + 'sucursal_entidad_edicion').val();
    var cpbteLetra=jQuery('#' + ns + 'comprobante_letra_edicion').val();


    var flagAmparo = false; 
    var estado=jQuery('#' + ns + 'estado').val();
	var chk_amparo=jQuery("#" + ns + "chk_amparo").is(':checked');

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
	
	var sector=jQuery('#' + ns + 'sector').val();
	
	var fechaPrestacionDia='';
	var fechaPrestacionMes='';
	var fechaPrestacionAnio='';
	
    
    fechaPrestacionDia=jQuery('#' + ns + 'fechaPrestacionDiaEdicion').val(); 
    fechaPrestacionMes=jQuery('#' + ns + 'fechaPrestacionMesEdicion').val();
    fechaPrestacionAnio=jQuery('#' + ns + 'fechaPrestacionAnioEdicion').val();
    
    id_medicamento_edit=jQuery('#' + ns + 'troquel_edit').val();
	var nombre_medicamento_edit = jQuery('#' + ns + 'nombre_medicamento_edit').val();

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
    
    var codigoSeguimiento_filtro_edit = jQuery('#' + ns + 'codigoSeguimiento_filtro_edit').val();
	var descripcionSeguimiento_filtro_edit = jQuery("#" + ns + "descripcionSeguimiento_filtro_edit").val();
	var nom_seleccionado_edit = jQuery("#" + ns + "nom_seleccionado").val(); 
	var tipoNomenclador_edit = jQuery('#' + ns + 'tipoNomenclador').val();
		

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
    
    
    var cuil=jQuery('#' + ns + 'cuil').val();
	var inte=jQuery('#' + ns + 'inte').val();	
	
	var idTecerizadora = jQuery('#' + ns + 'id_tercerizadora').val();
	
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
	
 	var url = cfg.urls.editarReclamosPrestaciones;
 	
	if(cpbteTipo != 'OTR' && cpbteTipo != 'AUT'){
	  if (!validarExisteComprobante(params)){   
	   	return false;
	  }
	}
	    
 	
	jQuery('#' + ns + 'lista_prestaciones_reclamos').load(url,params, function(){
									jQuery('#' + ns + 'buscando').hide();            															
													  });			
	jQuery('#' + ns + 'cantidadEdicion').val('1');
	jQuery('#' + ns + 'importeEdicion').val('');
	jQuery('#' + ns + 'totalEdicion').val('');
 	jQuery('#' + ns + 'cargoospimEdicion').val('');
 	jQuery('#' + ns + 'cargopsEdicion').val('');
 	jQuery('#' + ns + 'cargoimesaEdicion').val('');
 	jQuery('#' + ns + 'reconocidoSSSEdicion').val('');
 	jQuery('#' + ns + 'observacion_prestacionEdicion').val('');
 	document.getElementById("" + ns + "frecuenciaEdicion").selectedIndex = 0;
	jQuery('#' + ns + 'troquel').val(""); // farmacia 
	jQuery('#' + ns + 'codigoSeguimiento_filtro').val("");// prestaciones medicas 
	//jQuery('#' + ns + 'recuperable_sur').attr('checked', false);	
	document.getElementById("" + ns + "recuperable_sur").selectedIndex = 0; 	
	
	jQuery('#' + ns + 'comprobante_tipo_edicion').val('FCP');
	jQuery('#' + ns + 'comprobante_letra_edicion').val('');
	jQuery('#' + ns + 'comprobante_nro_edicion').val('');
	jQuery('#' + ns + 'comprobante_suc_edicion').val('');
	jQuery('#' + ns + 'fechaComprobanteDiaEdicion').val('');
	jQuery('#' + ns + 'fechaComprobanteMesEdicion').val('');
	jQuery('#' + ns + 'fechaComprobanteAnioEdicion').val('');
	jQuery('#' + ns + 'cantidadFC_edicion').val('');
	jQuery('#' + ns + 'importeUnitarioFC_edicion').val('');
	jQuery('#' + ns + 'importeFC_edicion').val('');
	jQuery('#' + ns + 'cuit_entidad_edicion').val('');
    jQuery('#' + ns + 'sucursal_entidad_edicion').val('');
    jQuery('#' + ns + 'entidad_edicion').val('');
    
	jQuery('#' + ns + 'fechaPrestacionDiaFarmacia').val(''); 
    jQuery('#' + ns + 'fechaPrestacionMesFarmacia').val('');
    jQuery('#' + ns + 'fechaPrestacionAnioFarmacia').val('');
    
	jQuery('#' + ns + 'fechaPrestacionDia').val(''); 
    jQuery('#' + ns + 'fechaPrestacionMes').val('');
    jQuery('#' + ns + 'fechaPrestacionAnio').val('');
	
    
    jQuery('#' + ns + 'fechaPrestacionDiaEdicion').val('');
    jQuery('#' + ns + 'fechaPrestacionMesEdicion').val('');
    jQuery('#' + ns + 'fechaPrestacionAnioEdicion').val('');

    jQuery("#" + ns + "nombre_medicamento_edit").val('');
    jQuery("#" + ns + "divBtnBuscaMedicamento_edit").show();
    
    
	limpiarNomencladorAutocompletar();
	   
    window.addprestacion=false;
    cancelaEdicionPrestacion();

}


function cancelaEdicionPrestacion() {
	
	// oculta div de datos de edicion
	jQuery("#" + ns + "datos_edicion_prestacion").hide();
	// habilita el buscador segun el sector
	manejarTipoSector();	
	jQuery("#" + ns + "datos_prestacion_ingreso").show();
	
	limpiarNomencladorAutocompletar();
	onOffcombosestadosprestaciones(true);	
	// mover el combo a la posicion de cargado porque no se confirmo el rechazo o la autorizacion
	
	var datos = document.getElementById("" + ns + "tipoaccionprestacion").value;	
	var datasplit =datos.split('-');
	var idPrestacion = datasplit[1];	
	document.getElementById('comboestadosreclamo'+ idPrestacion ).selectedIndex = "0";	
	document.getElementById("" + ns + "tipoaccionprestacion").value="";
	
}

function agregarPrestacion() {	
	
	var frecuencia= jQuery('#' + ns + 'frecuencia').val();		
	var importe = jQuery('#' + ns + 'importe').val();
	var cantidad  = jQuery('#' + ns + 'cantidad').val();
	var cargoospim= jQuery('#' + ns + 'cargoospim').val();
	var cargops= jQuery('#' + ns + 'cargops').val();
	var cargoimesa= jQuery('#' + ns + 'cargoimesa').val();
	var reconocidoSSS= jQuery('#' + ns + 'reconocidoSSS').val();
	var observaciones= jQuery('#' + ns + 'observacion_prestacion').val();		
    var troquel= jQuery('#' + ns + 'troquel').val();
    var prestacion= jQuery('#' + ns + 'codigoSeguimiento_filtro').val();    
    var tiponomenclador =jQuery('#' + ns + 'nom_seleccionado').val();
    var tiponomencladorprestacion =jQuery('#' + ns + 'tiponomenclador').val();
    var nombre_medicamento=jQuery("#" + ns + "nombre_medicamento").val();
    var nombre_prestacion = jQuery('#' + ns + 'descripcionSeguimiento_filtro').val();
    var tiponomnecladorprestacion =  jQuery("#" + ns + "tipoNomenclador").val(); 
    
    
    
    var recuperableSur  =  jQuery('#' + ns + 'recuperable_sur').val();
    
    
    var cpbteTipo=jQuery('#' + ns + 'comprobante_tipo').val();
    var cpbteNro=jQuery('#' + ns + 'comprobante_nro').val();
    var cpbteDia=jQuery('#' + ns + 'fechaComprobanteDia').val();
    var cpbteMes=jQuery('#' + ns + 'fechaComprobanteMes').val();
    var cpbteAnio=jQuery('#' + ns + 'fechaComprobanteAnio').val();
    var cpbteCantidad=jQuery('#' + ns + 'cantidadFC').val();
    var cpbteImporte= jQuery('#' + ns + 'importeUnitarioFC').val();
    var importeFC = jQuery('#' + ns + 'importeFC').val();
    var cpbteCuit=jQuery('#' + ns + 'cuit_entidad').val();
    var cpbteCuitSucursal=jQuery('#' + ns + 'sucursal_entidad').val();
    var cpbteSucursal=jQuery('#' + ns + 'comprobante_suc').val();
    var cpbteLetra=jQuery('#' + ns + 'comprobante_letra').val();
    
    
    var flagAmparo = false; 
    var estado=jQuery('#' + ns + 'estado').val();
	var chk_amparo=jQuery("#" + ns + "chk_amparo").is(':checked');

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
    
 	if (jQuery("#" + ns + "nom_seleccionado").val()==''){
		  alert('Debe seleccionar el sector');
		  return false;
	}	
	if (jQuery("#" + ns + "nom_seleccionado").val()==1){		 
		if (jQuery('#' + ns + 'codigoSeguimiento_filtro').val()<1  ) {
		 	alert('Debe seleccionar la prestación');
		 	return false;
		} 	
	    if(nombre_prestacion==null || nombre_prestacion==''){
			  alert('Debe seleccionar la prestación');
			  return false;
		}
			
	}else{		
		if (jQuery('#' + ns + 'troquel').val()<1) {
			alert('Debe seleccionar el medicamento');
			return false;
		}	
		if ( nombre_medicamento==null || nombre_medicamento=='') {
			alert('Debe seleccionar el medicamento');
			return false;
		}
	}    
	
	
	var sector=jQuery('#' + ns + 'sector').val();

    var fechaPrestacionDia='';
    var fechaPrestacionMes='';
    var fechaPrestacionAnio='';
	 fechaPrestacionDia=jQuery('#' + ns + 'fechaPrestacionDia').val(); 

    if (fechaPrestacionDia==null || fechaPrestacionDia==0 || fechaPrestacionDia=='' ){
    	 fechaPrestacionDia=jQuery('#' + ns + 'fechaPrestacionDiaFarmacia').val(); 
         fechaPrestacionMes=jQuery('#' + ns + 'fechaPrestacionMesFarmacia').val();
         fechaPrestacionAnio=jQuery('#' + ns + 'fechaPrestacionAnioFarmacia').val();
    }else{
        fechaPrestacionDia=jQuery('#' + ns + 'fechaPrestacionDia').val(); 
        fechaPrestacionMes=jQuery('#' + ns + 'fechaPrestacionMes').val();
        fechaPrestacionAnio=jQuery('#' + ns + 'fechaPrestacionAnio').val();
    }
	
	

	if (frecuencia=="SELECCIONE"){
    	frecuencia="";    
	}
    
    var frecuenciacontrol =document.getElementById("" + ns + "frecuencia");
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
    
   
    var tipoPedidoControl =document.getElementById("" + ns + "tipopedido");
    if (tipoPedidoControl.selectedIndex==0){
		alert('Debe seleccionar el Tipo de Pedido.');
		return false ;
	}
    
    
    if (!ValidaDatosReclamo()){       
   		return false;
	}
    
    var cuil=jQuery('#' + ns + 'cuil').val();
	var inte=jQuery('#' + ns + 'inte').val();	
	
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
	
	var url = cfg.urls.listaPrestacionesReclamos;

	jQuery('#' + ns + 'lista_prestaciones_reclamos').load(url,params, function(){
									jQuery('#' + ns + 'buscando').hide();            															
													  });			
	/* document.getElementById("" + ns + "sector").disabled = "disabled"; */	  
 	jQuery('#' + ns + 'importe').val('');
 	jQuery('#' + ns + 'total').val('');
 	jQuery('#' + ns + 'cantidad').val('1');
 	jQuery('#' + ns + 'cargoospim').val('');
 	jQuery('#' + ns + 'cargops').val('');
 	jQuery('#' + ns + 'cargoimesa').val('');
 	jQuery('#' + ns + 'reconocidoSSS').val('');
 	jQuery('#' + ns + 'observacion_prestacion').val('');
	document.getElementById("" + ns + "frecuencia").selectedIndex = 0;
	jQuery('#' + ns + 'troquel').val(""); // farmacia 
	jQuery('#' + ns + 'codigoSeguimiento_filtro').val("");// prestaciones medicas
	//jQuery('#' + ns + 'recuperable_sur').attr('checked', false);
	document.getElementById("" + ns + "recuperable_sur").selectedIndex = 0;
	jQuery("#" + ns + "divBtnBuscaEntidad").show();

	
	jQuery('#' + ns + 'comprobante_tipo').val('FCP');
	jQuery('#' + ns + 'comprobante_nro').val('');
	jQuery('#' + ns + 'fechaComprobanteDia').val('');
	jQuery('#' + ns + 'fechaComprobanteMes').val('');
	jQuery('#' + ns + 'fechaComprobanteAnio').val('');
	jQuery('#' + ns + 'cantidadFC').val('');
	jQuery('#' + ns + 'importeUnitarioFC').val('');
	jQuery('#' + ns + 'importeFC').val('');
	jQuery('#' + ns + 'cuit_entidad').val('');
    jQuery('#' + ns + 'sucursal_entidad').val('');
    jQuery('#' + ns + 'entidad_').val('');
    jQuery('#' + ns + 'comprobante_suc').val('');
    jQuery("#" + ns + "nombre_medicamento").val('');
    jQuery("#" + ns + "divBtnBuscaMedicamento").show();
    

	jQuery('#' + ns + 'fechaPrestacionDiaFarmacia').val(''); 
    jQuery('#' + ns + 'fechaPrestacionMesFarmacia').val('');
    jQuery('#' + ns + 'fechaPrestacionAnioFarmacia').val('');
    
	jQuery('#' + ns + 'fechaPrestacionDia').val(''); 
    jQuery('#' + ns + 'fechaPrestacionMes').val('');
    jQuery('#' + ns + 'fechaPrestacionAnio').val('');
    
	limpiarNomencladorAutocompletar();
	
    window.addprestacion=true;
    /* document.getElementById("" + ns + "tipopedido").disabled = true;  */    
    if (jQuery('#' + ns + 'estado').val()==3){   // cerrado
    	jQuery('#' + ns + 'montoPsPrestaciones').val(cargops); 
		/* validaFacturacionDirectayReintegro();  */    	
    }	                            
}   

function controlarEstadoCerrado() {

	var  varCantRevisiones = cfg.values.cantRevisiones;
	
	var  varDebitoTercerizadora = cfg.values.debitoTercerizadora;
	
	
	
	
	
	// VERIFICAR SI EXISTE POR LO MENOS UN REGISTRO DE REVISION ACTIVO 	
	if (jQuery('#' + ns + 'estado').val()==3){
		if (varCantRevisiones > 0 ){
			jQuery("#" + ns + "Cierre_Reclamo_Div").show();	
			if(varDebitoTercerizadora == true){
				jQuery("#" + ns + "debitoprestadora")[0].checked = true;

			}																												
		}else{
			alert("Debe agregar una Revisión");
			jQuery("#" + ns + "estado option[value="+window.estadoIni+"]").attr("selected",true);

		}
		/* validaFacturacionDirectayReintegro(); */		
	} else {
		jQuery("#" + ns + "Cierre_Reclamo_Div").hide();
		jQuery('#' + ns + 'nroLote').val("");
	}	
}

/* function onOffControlesRequest(valor) {
	document.getElementById("" + ns + "fechaseccionalDia").disabled = valor;
	document.getElementById("" + ns + "fechaseccionalMes").disabled = valor;
	document.getElementById("" + ns + "fechaseccionalAnio").disabled = valor;
} */


function imprimirReclamo(){
		     
	window.location.href ="/pdfservlet/?accion=reclamoprestacional&idreclamo=" + cfg.values.idReclamo;
	
}


function ValidaDatosReclamo(){
	
	
	var respuesta=true;
	var codError='';	
	var cpbte_dia =  jQuery('#' + ns + 'fechaComprobanteDia').val();
	var cpbte_mes =  jQuery('#' + ns + 'fechaComprobanteMes').val();
	var cpbte_anio = jQuery('#' + ns + 'fechaComprobanteAnio').val();

	var sector=jQuery('#' + ns + 'sector').val();
	
    var cpbteCuit=jQuery('#' + ns + 'cuit_entidad').val();
    var tipopedido=jQuery('#' + ns + 'tipopedido').val();


	var fecha_prestacion_dia='';
	var fecha_prestacion_mes='';
	var fecha_prestacion_anio='';
		
	    
	if (sector == 'FARMACIA'){
		 fecha_prestacion_dia=jQuery('#' + ns + 'fechaPrestacionDiaFarmacia').val(); 
		 fecha_prestacion_mes=jQuery('#' + ns + 'fechaPrestacionMesFarmacia').val();
		 fecha_prestacion_anio=jQuery('#' + ns + 'fechaPrestacionAnioFarmacia').val();
	}else{
		 fecha_prestacion_dia=jQuery('#' + ns + 'fechaPrestacionDia').val(); 
		 fecha_prestacion_mes=jQuery('#' + ns + 'fechaPrestacionMes').val();
		 fecha_prestacion_anio=jQuery('#' + ns + 'fechaPrestacionAnio').val();
	}
	
    var troquel= jQuery('#' + ns + 'troquel').val();
    var prestacion= jQuery('#' + ns + 'codigoSeguimiento_filtro').val();    
    var tipoNomenclador =jQuery('#' + ns + 'nom_seleccionado').val();
    var tipoNomencladorPrestacion =jQuery('#' + ns + 'tiponomenclador').val();
 
	
     var baja =  jQuery('#' + ns + 'baja_fecha').val();
    
	 var url = cfg.urls.validarReclamo;
		
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
	var cpbte_dia =  jQuery('#' + ns + 'fechaComprobanteDiaEdicion').val();
	var cpbte_mes =  jQuery('#' + ns + 'fechaComprobanteMesEdicion').val();
	var cpbte_anio = jQuery('#' + ns + 'fechaComprobanteAnioEdicion').val();

	    

	fecha_prestacion_dia=jQuery('#' + ns + 'fechaPrestacionDiaEdicion').val(); 
	fecha_prestacion_mes=jQuery('#' + ns + 'fechaPrestacionMesEdicion').val();
	fecha_prestacion_anio=jQuery('#' + ns + 'fechaPrestacionAnioEdicion').val();
	
	var sector=jQuery('#' + ns + 'sector').val();
	
    var tipopedido=jQuery('#' + ns + 'tipopedido').val();

    var cpbteCuit=jQuery('#' + ns + 'cuit_entidad_edicion').val();
	
    var troquel= jQuery('#' + ns + 'troquel_edit').val();
    var prestacion= jQuery('#' + ns + 'codigoSeguimiento_filtro_edit').val();    
    var tipoNomenclador =jQuery('#' + ns + 'nom_seleccionado').val();
    var tipoNomencladorPrestacion =jQuery('#' + ns + 'tiponomenclador').val();
    var baja =  jQuery('#' + ns + 'baja_fecha').val();

	 var url = cfg.urls.validarReclamo;
		
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
	
    var url = cfg.urls.validarExisteComprobante;
	
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

	jQuery('#' + ns + 'cantprestacioneslista').val('0');
	document.getElementById("" + ns + "tipo_gestion_cierre_reclamo").selectedIndex=0;
	seteaControlesFacturacionDirecta(false);
	
	
//  if (!cfg.values.esEdicion) {
// 	document.getElementById("" + ns + "sector").disabled = "";
// } 

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
		var params = "&" + cfg.values.cmdParam + "=" + cfg.values.viewCommand;
		params = params + '&idContactoSerial='+idContSerial;
		
		popupCRM = new Liferay.Popup({title:cfg.messages.detalleContacto,modal:true, width: 880, position:['center',30]});
		var url = cfg.values.caiNamespace ? cfg.urls.editarContactoCai : cfg.urls.editarContactoAfiliados;
		url = url + params;
		jQuery(popupCRM).load(url);	
	}
	


function validaMontosEdicion(){	
	
	/* var strimporte =   jQuery('#' + ns + 'totalEdicion').val();

    var strcargoospim = jQuery('#' + ns + 'cargoospimEdicion').val();
    var strcargops =   jQuery('#' + ns + 'cargopsEdicion').val(); */

    //var importedouble = parseFloat(jQuery('#' + ns + 'totalEdicion').val());
    var importedouble = parseFloat(jQuery('#' + ns + 'totalEdicion').val().replace(",","."));
    
    var cargoospimdouble = parseFloat(jQuery('#' + ns + 'cargoospimEdicion').val());
    var cargopsdouble = parseFloat(jQuery('#' + ns + 'cargopsEdicion').val());
    var cargoimesadouble = parseFloat(jQuery('#' + ns + 'cargoimesaEdicion').val());
    var reconocidoSSS = parseFloat(jQuery('#' + ns + 'reconocidoSSSEdicion').val());
    var estado =jQuery("#" + ns + "estado").val();
    

    var importeFC = parseFloat(jQuery('#' + ns + 'importeFC').val());
    var importeFCEdicion = parseFloat(jQuery('#' + ns + 'importeFC_edicion').val());
    if(isNaN(importeFC)) {
//	jQuery('#' + ns + 'importeFC').val();
	   importeFC=0;
    }
    if(isNaN(importeFCEdicion)) {
 //	jQuery('#' + ns + 'importeFC_edicion').val();
	   importeFCEdicion=0;
    }


/*
importedouble= parseFloat(strimporte.replace(',','.'));
cargoospimdouble= parseFloat(strcargoospim.replace(',','.'));
cargopsdouble= parseFloat(strcargops.replace(',','.'));
*/
    if(isNaN(importedouble)) {		jQuery('#' + ns + 'totalEdicion').val()  ; importedouble=0; 	}
    if(isNaN(cargoospimdouble)) {	jQuery('#' + ns + 'cargoospimEdicion').val()  ; cargoospimdouble=0; 	}
    if(isNaN(cargopsdouble)) {		jQuery('#' + ns + 'cargopsEdicion').val()  ; cargopsdouble=0; 	}
    if(isNaN(cargoimesadouble)) {		jQuery('#' + ns + 'cargoimesaEdicion').val()  ; cargoimesadouble=0; 	}
    if(isNaN(reconocidoSSS)) {		jQuery('#' + ns + 'reconocidoSSSEdicion').val()  ; reconocidoSSS=0; 	}

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

    if ( document.getElementById("" + ns + "tipopedido").selectedIndex==1) { // tipo de pedido excepcion 
	  if (total!=importedouble && estado==3){
		alert('El importe total de la prestación debe coincidir con la suma de cargo Ospim y cargo tercerizadora');
		return false;
	  }
    }
    
    var recuperable  =  jQuery('#' + ns + 'recuperable_surEdicion').val();
    if(recuperable==2){
    	if(reconocidoSSS>0){
    	   	   alert('El importe reconocido debe estar vacío');
    	   	   jQuery('#' + ns + 'reconocidoSSSEdicion').val('');
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
jQuery('#' + ns + 'importeEdicion').val(importedouble);
jQuery('#' + ns + 'cargoospimEdicion').val(cargoospimdouble);
jQuery('#' + ns + 'cargopsEdicion').val(cargopsdouble);
*/	
	
	return true;
}
	
function ValidaMontos()
{
	var importeFC = parseFloat(jQuery('#' + ns + 'importeFC').val());
	var importedouble = parseFloat(jQuery('#' + ns + 'total').val());
	var cargoospimdouble = parseFloat(jQuery('#' + ns + 'cargoospim').val());
	var cargopsdouble = parseFloat(jQuery('#' + ns + 'cargops').val());
	var cargoimesadouble = parseFloat(jQuery('#' + ns + 'cargoimesa').val());
	var reconocidoSSS = parseFloat(jQuery('#' + ns + 'reconocidoSSS').val());
	var estado =jQuery("#" + ns + "estado").val();
	
	if(isNaN(importedouble)) {		jQuery('#' + ns + 'total').val()  ; importedouble=0; 	}
	if(isNaN(cargoospimdouble)) {	jQuery('#' + ns + 'cargoospim').val()  ; cargoospimdouble=0; 	}
	if(isNaN(cargopsdouble)) {		jQuery('#' + ns + 'cargops').val()  ; cargopsdouble=0; 	}
	if(isNaN(cargoimesadouble)) {		jQuery('#' + ns + 'cargoimesa').val()  ; cargoimesadouble=0; 	}
	if(isNaN(reconocidoSSS)) {		jQuery('#' + ns + 'reconocidoSSS').val()  ; reconocidoSSS=0; 	}
	if(isNaN(importeFC)) {		jQuery('#' + ns + 'importeFC').val()  ; importeFC=0; 	}
	
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
	
	if ( document.getElementById("" + ns + "tipopedido").selectedIndex==1) { // tipo de pedido excepcion 
		if (totalCargos!=importedouble && estado=='3'){
			alert('El importe total de la prestación debe coincidir con la suma de Cargo Ospim más Cargo Tercerizadora.');
			return false;
		}
	}
 
   if ( document.getElementById("" + ns + "tipopedido").selectedIndex==2) { // tipo de pedido reintegro
	    if (importedouble <totalCargos && estado=='3'){
			alert('El importe total de prestación debe coincidir con la suma de a Cargo Ospim más Cargo Tercerizadora');
			return false;
		}   
		if (totalCargos==0 && estado=='3'){
			alert(' la suma de a Cargo Ospim más a Cargo Tercerizadora debe ser mayor que cero.');
			return false;
		}
   }
   
   var recuperable  =  jQuery('#' + ns + 'recuperable_sur').val();
   if(recuperable==2){
	   if(reconocidoSSS>0){
	   	   alert('El importe reconocido debe estar vacio');
	   	   jQuery('#' + ns + 'reconocidoSSS').val('');
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
	if (jQuery('#' + ns + 'cantrevisionesactivas').val()<1){ // no hay revisiones activas 
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
	  var input=document.getElementById('' + ns + 'buscadorcie10buscador').value.toUpperCase();
	  var output=document.getElementById('' + ns + 'cie_diez').options;
	  var dato;       
      pos=jQuery('#' + ns + 'posforcie10').val();
      for(var i=pos;i<document.getElementById("" + ns + "cie_diez").options.length ;i++) {
		  dato = output[i].text;		  
		  if(dato.indexOf(input)>-1){
		        output[i].selected=true;		        
		        jQuery('#' + ns + 'codigoCie10').val(output[i].value);
		        jQuery('#' + ns + 'posforcie10').val(++i);
		        return false;
		      }		 
      } 
      
      if (output[0].selected){
    	  alert('No se encontro el dato buscado.')  
      }     else{
    	  alert('Se termino de recorrer al lista.');
    	  
    	  
      }  
      jQuery('#' + ns + 'posforcie10').val(0);
	}
*/
function enterTecla(e){
	tecla = (document.all) ? e.keyCode : e.which;//obtenemos el codigo ascii de la tecla	
	if (tecla==13) {
		crit_busqueda();
	}else{
		jQuery('#' + ns + 'posforcie10').val(0);
	} 

}

function aplicaEstiloBordeRojoDatosObligatorio() { 
	// borde rojo en datos obligatorios
	color="#ff9999"
	jQuery("#" + ns + "fechaospimMes").css("borderColor",color);
	jQuery("#" + ns + "fechaospimAnio").css("borderColor",color);
	jQuery("#" + ns + "fechaospimDia").css("borderColor",color);
	jQuery("#" + ns + "estado").css("borderColor",color);
	jQuery("#" + ns + "sector").css("borderColor",color);
	jQuery("#" + ns + "tipopedido").css("borderColor",color);
	jQuery("#" + ns + "fecharevisionMes").css("borderColor",color);
	jQuery("#" + ns + "fecharevisionAnio").css("borderColor",color);
	jQuery("#" + ns + "fecharevisionDia").css("borderColor",color);
	jQuery("#" + ns + "resolucion").css("borderColor",color);
	jQuery("#" + ns + "justificacionmedica").css("borderColor",color);
	jQuery("#" + ns + "frecuencia").css("borderColor",color);
	jQuery("#" + ns + "importe").css("borderColor",color);
	jQuery("#" + ns + "mensajerevisionefectuada").css("borderColor",color);

}

function calculatotal(){

	importe=jQuery("#" + ns + "importe").val();
	cantidad=jQuery("#" + ns + "cantidad").val()
	total= importe * cantidad  ;
	jQuery("#" + ns + "total").val(Math.round(total.toFixed(2) * 100)/100);
	//jQuery("#" + ns + "total").val(total.toFixed(2));

}

function seleccionaCamposCieDiez(codigo,descripcion ){
	jQuery('#' + ns + 'codigoCie').val(codigo);
	jQuery('#' + ns + 'detalleCie').val(descripcion);
	jQuery('#' + ns + 'codigoCie10').val(codigo);
}	

if (cfg.values.codigoCie10Presente) {
window[ns + "buscarCieCodigo"](); 
}

function limpiaCamposBusquedaCieDiez(){
	jQuery('#' + ns + 'codigoCie10').val("");
}

/* function validaFacturacionDirectayReintegro(){
	document.getElementById("" + ns + "tipo_gestion_cierre_reclamo").selectedIndex=0;	
	jQuery('#' + ns + 'tipogestion').val(0);
	seteaControlesFacturacionDirecta(false);
	if (jQuery('#' + ns + 'montoPsPrestaciones').val()>0 && jQuery('#' + ns + 'montoPsPrestaciones').val()!="" ){// forzar facturacion directa o reintegro
		
		if (document.getElementById("" + ns + "tipopedido").selectedIndex==1){ // excepcion 
			document.getElementById("" + ns + "tipo_gestion_cierre_reclamo").selectedIndex=2;
			jQuery('#' + ns + 'tipogestion').val(3); // facturacion directa 
			seteaControlesFacturacionDirecta(true);
		}	
 		if (document.getElementById("" + ns + "tipopedido").selectedIndex==2){ // reintegro
			validaReintegro();			
	}
}
} */


/* function validaReintegro(){
		document.getElementById("" + ns + "tipo_gestion_cierre_reclamo").selectedIndex=3;		
	    document.getElementById("" + ns + "tipo_gestion_cierre_reclamo").disabled = true;
		jQuery('#' + ns + 'tipogestion').val(4); // reintegro 
} */

function seteaControlesFacturacionDirecta(estadoTrueFalse){
	document.getElementById("" + ns + "incluido_convenio_gerenciadora").checked = estadoTrueFalse;
	/* document.getElementById("" + ns + "incluido_convenio_gerenciadora").disabled = estadoTrueFalse; */
	document.getElementById("" + ns + "debitoprestadora").checked =estadoTrueFalse;
	/*  document.getElementById("" + ns + "debitoprestadora").disabled = estadoTrueFalse; */	
	/*  document.getElementById("" + ns + "tipo_gestion_cierre_reclamo").disabled = estadoTrueFalse;*/
}
function desactivaCheckCierre(){
	seteaControlesFacturacionDirecta(false);
	document.getElementById("" + ns + "dosporciento").checked =false;
	document.getElementById("" + ns + "dosporciento").disabled = true;
}

/* function habilitarControlesCierre() {
	document.getElementById("" + ns + "sector").disabled =false;  
	document.getElementById("" + ns + "tipopedido").disabled =false; 
	document.getElementById("" + ns + "debitoprestadora").disabled =false; 
	document.getElementById("" + ns + "incluido_convenio_gerenciadora").disabled =false; 
	document.getElementById("" + ns + "tipo_gestion_cierre_reclamo").disabled =false;
} */


function abreAutorizacion(){
	
	 window.open(cfg.urls.autorizacionesPrestacionales,
	         'Autorizaciones', 'height=800, menubar=no, resizable=yes,scrollbars=yes, status=no, toolbar=no, width=1200');  
}

function calculatotalFC(){

	importe=jQuery("#" + ns + "importeUnitarioFC").val();
	cantidad=jQuery("#" + ns + "cantidadFC").val();
	total= importe * cantidad  ;
	jQuery("#" + ns + "importeFC").val(Math.round(total.toFixed(2) * 100)/100);
/*	
	jQuery("#" + ns + "cantidad").val(cantidad);
	jQuery("#" + ns + "importe").val(importe);
	calculatotal();
	jQuery('#' + ns + 'cargoospim').val(Math.round(total.toFixed(2) * 100)/100);
*/	
}


function traerDescripcion() {
	var idIntegracion = jQuery('#' + ns + 'integracion').val();
	var descripcionLarga;
	var url = cfg.urls.getIntegracionDetalle+idIntegracion;
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
		var recuperable=jQuery('#' + ns + 'recuperable_sur').val();
		if(recuperable==3 || recuperable==1){
			jQuery('#' + ns + 'reconocidoSSS').attr('readonly', false);
		}else{
			jQuery('#' + ns + 'reconocidoSSS').val(0);
			jQuery('#' + ns + 'reconocidoSSS').attr('readonly', true);
		}
		
			

	}catch (err) {}	
	
}

function validarEmail() {
	var email = jQuery('#' + ns + 'email').val();
/* 	var emailReg = /^([\da-z_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
 */	
 
/*  Se solicito quitar el 24/05/2016
	if(trim(email).length == 0){
		alert("El campo Email es Obligatorio");
		jQuery("#" + ns + "email").focus();
		return false;
	} */
	if(trim(email).length == 0){
		return true;
	}
	var expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	
	if ( !expr.test(email) ){
	    alert("Error: La dirección de correo " + email + " es incorrecta.");
	    jQuery("#" + ns + "email").focus();
		return false;
	}
	    
	/* if(trim(email).length > 0){	
		if( !emailReg.test( email ) ) {
			jQuery("#" + ns + "email").focus();
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

	var d_id_domicilio=jQuery("#" + ns + "id_domicilio").val();
    var d_id_provincia = jQuery("#" + ns + "provincia").val();
	var d_id_localidad = jQuery("#" + ns + "localidad").val();
	var d_calle = jQuery("#" + ns + "calle").val();
	var d_numero = jQuery("#" + ns + "numero").val();
	var d_piso = jQuery("#" + ns + "piso").val();
	var d_dpto = jQuery("#" + ns + "dpto").val();
	var d_cod_pos = jQuery("#" + ns + "cod_postal").val();
	var d_barrio = jQuery("#" + ns + "barrio").val();
	var d_cod_area_tel = jQuery("#" + ns + "cod_area_telefono").val();
	var d_telefono = jQuery("#" + ns + "telefono").val();
	//var d_cod_area_laboral = jQuery("#" + ns + "cod_area_tel_laboral").val();
	//var d_laboral = jQuery("#" + ns + "tel_laboral").val();
	var d_cod_area_celu = jQuery("#" + ns + "cod_area_celular").val();
	var d_celular = jQuery("#" + ns + "celular").val();
	
	var d_email = jQuery("#" + ns + "email").val();
	var d_email_original = jQuery("#" + ns + "email_original").val();
	
//	var cuiltitular= jQuery('#' + ns + 'cuil_titular').val();
	var cuiltitular= jQuery('#' + ns + 'cuil').val();
	var integrante = jQuery("#" + ns + "inte").val();
	
	var idPar = jQuery("#" + ns + "idPar").val();
	if (idPar != cfg.values.parentescoDefault &&
	    idPar != cfg.values.conyugeDefault &&
	    idPar != cfg.values.concubinoDefault) {
	  integrante = 0;
	}
	
	/*validamos los campos obligatorios*/
	if (trim(d_calle).length == 0){
		alert("Ingrese la calle del domicilio");
		jQuery('#' + ns + 'calle').focus();
		return false;
	}
	
	if (
		 (trim(d_cod_area_tel) == '' && trim(d_telefono) != '') ||
		 (trim(d_cod_area_tel) != '' && trim(d_telefono) == '')
		){
		alert("El teléfono debe necesariamente tener el código de area y el número");
		jQuery('#' + ns + 'telefono').focus();
		return false;
	}
	
	if(trim(d_cod_area_tel).startsWith('0')){
		alert("El código de area del teléfono no debe iniciar con cero");
		jQuery("#" + ns + "cod_area_telefono").focus();
		return false;
	}
	if(trim(d_telefono).startsWith('0')){
		alert("El número del teléfono no debe iniciar con cero");
		jQuery("#" + ns + "telefono").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_tel).length>0 || trim(d_telefono).length>0){
		if(trim(d_cod_area_tel).length+trim(d_telefono).length!=10){
			alert("La longitud del código de área + teléfono debe de ser de 10 caracteres");
			jQuery("#" + ns + "cod_area_telefono").focus();
			return false;
		}
	}
	/*
	if ((trim(d_cod_area_laboral) == '' && trim(d_laboral) != '') ||
		(trim(d_cod_area_laboral) != '' && trim(d_laboral) == '')
		){
		alert("El teléfono laboral debe necesariamente tener el código de area y el número");
		jQuery('#' + ns + 'tel_laboral').focus();
		return false;
	}
	
	if(trim(d_cod_area_laboral).startsWith('0')){
		alert("El código de area laboral no debe iniciar con cero");
		jQuery("#" + ns + "cod_area_tel_laboral").focus();
		return false;
	}
	if(trim(d_laboral).startsWith('0')){
		alert("El número del teléfono laboral no debe iniciar con cero");
		jQuery("#" + ns + "tel_laboral").focus();
		return false;
	}
	
	if(trim(d_cod_area_laboral).length>0 || trim(d_laboral).length>0){
		if(trim(d_cod_area_laboral).length+trim(d_laboral).length!=10){
			alert("La longitud del código de área + teléfono laboral debe de ser de 10 caracteres");
			jQuery("#" + ns + "cod_area_tel_laboral").focus();
			return false;
		}
	}
	*/
	
	
	if(trim(d_cod_area_celu).startsWith('0')){
		alert("El código de area del celular no debe iniciar con cero");
		jQuery("#" + ns + "cod_area_celular").focus();
		return false;
	}
	if(trim(d_celular).startsWith('0')){
		alert("El número del celular no debe iniciar con cero");
		jQuery("#" + ns + "celular").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_celu).length>0 || trim(d_celular).length>0){
		if(trim(d_cod_area_celu).length+trim(d_celular).length!=10){
			alert("La longitud del código de área + celular debe de ser de 10 caracteres");
			jQuery("#" + ns + "cod_area_celular").focus();
			return false;
		}
	}
	
	
	
	if(!validarEmail()){
		return false;
	}
	
	var url = cfg.urls.actualizaDomicilioPorParentesco + idPar;
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
			if(window.popupDomicilio!=null){
				jQuery("#" + ns + "divResultadoActualizarOK").show();
				jQuery("#" + ns + "divBotonActualizar").hide();
				Liferay.Popup.close(window.popupDomicilio); 
			}	 
		});
} 

function mostrarDomicilioAfiliado(){
	var cuil_titu= jQuery("#" + ns + "cuil").val();
	var inte= jQuery("#" + ns + "inte").val();
	var email;
	var actualizaDomicilio;
	
	
	var url = cfg.urls.buscarAfiliadoDatos;
	   url += cuil_titu;
	   url += '&inte=' + inte;
		
 jQuery.ajax({   
 url: url,
 async:false,
 success: function(data){
	   var obj = jQuery.parseJSON(data);
	   email=obj.email;
	}});
	window.popupDomicilio= Liferay.Popup({title:cfg.messages.detalleDomicilio,modal:true,width:950,height:330,fixedcenter:true});
	var url1 = cfg.urls.actualizaDomicilioVista+cuil_titu+'&inte='+inte+'&cmd=view' +'&email='+encodeURI(email);
	jQuery(window.popupDomicilio).load(url1);
	
}

window[ns + "buscarNomencladorAutocompletar"] = buscarNomencladorAutocompletar;
window[ns + "buscarNomencladorAutocompletar_edit"] = buscarNomencladorAutocompletar_edit;
window[ns + "limpiarNomencladorAutocompletar"] = limpiarNomencladorAutocompletar;
window[ns + "cerrarDivNm"] = cerrarDivNm;
window[ns + "cerrarNm"] = cerrarNm;
window[ns + "saveReclamo"] = saveReclamo;
window[ns + "volverEstadoObservado"] = volverEstadoObservado;
window[ns + "editaReclamo"] = editaReclamo;
window[ns + "reabrirReclamo"] = reabrirReclamo;
window[ns + "agregarRevision"] = agregarRevision;
window[ns + "verprestacionesasociadas"] = verprestacionesasociadas;
window[ns + "ocultacontactosdelreclamo"] = ocultacontactosdelreclamo;
window[ns + "vercontactosdelreclamo"] = vercontactosdelreclamo;
window[ns + "editarPrestacionSeleccionada"] = editarPrestacionSeleccionada;
window[ns + "cancelaEdicionPrestacion"] = cancelaEdicionPrestacion;
window[ns + "agregarPrestacion"] = agregarPrestacion;
window[ns + "imprimirReclamo"] = imprimirReclamo;
window[ns + "validarEmail"] = validarEmail;
window.tipoGestionCierreReclamo = tipoGestionCierreReclamo;
window.integracionReclamo = integracionReclamo;
window.filtrarLetraComprobante = filtrarLetraComprobante;
window.seleccionaCamposNm = seleccionaCamposNm;
window.pasarParametrosAParentNm = pasarParametrosAParentNm;
window.DatosRevisionOk = DatosRevisionOk;
window.ValidarDatosObligatorios = ValidarDatosObligatorios;
window.manejartipogestion = manejartipogestion;
window.manejarListaPresentes = manejarListaPresentes;
window.cambioresolucion = cambioresolucion;
window.manejarTipoPedido = manejarTipoPedido;
window.cambioTipoPedido = cambioTipoPedido;
window.manejarTipoPedidoCierre = manejarTipoPedidoCierre;
window.manejarTipoSector = manejarTipoSector;
window.controlarEstadoCerrado = controlarEstadoCerrado;
window.ValidaDatosReclamo = ValidaDatosReclamo;
window.ValidaDatosReclamoEditar = ValidaDatosReclamoEditar;
window.validarExisteComprobante = validarExisteComprobante;
window.evaluarOnSectorListaEnCero = evaluarOnSectorListaEnCero;
window.validarSiNumero = validarSiNumero;
window.validaMonto = validaMonto;
window.verCrmContacto = verCrmContacto;
window.validaMontosEdicion = validaMontosEdicion;
window.ValidaMontos = ValidaMontos;
window.validarevision = validarevision;
window.convertToUppercase = convertToUppercase;
window.myXOR = myXOR;
window.enterTecla = enterTecla;
window.aplicaEstiloBordeRojoDatosObligatorio = aplicaEstiloBordeRojoDatosObligatorio;
window.calculatotal = calculatotal;
window.seleccionaCamposCieDiez = seleccionaCamposCieDiez;
window.limpiaCamposBusquedaCieDiez = limpiaCamposBusquedaCieDiez;
window.seteaControlesFacturacionDirecta = seteaControlesFacturacionDirecta;
window.desactivaCheckCierre = desactivaCheckCierre;
window.abreAutorizacion = abreAutorizacion;
window.calculatotalFC = calculatotalFC;
window.traerDescripcion = traerDescripcion;
window.cambiorecuperable = cambiorecuperable;
window.confirmaActualizacionDomicilioAfiliado = confirmaActualizacionDomicilioAfiliado;
window.mostrarDomicilioAfiliado = mostrarDomicilioAfiliado;

})(window, jQuery);
