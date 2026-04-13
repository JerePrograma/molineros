<%@include file="/html/portlet/hoteles/hoteles.css"%>
<%@ include file="/html/portlet/hoteles/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
		
		String ptoVtaAfip="00030";

		try{
			ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 	
		}catch(Exception e){
			//ptoVtaAfip="0000";
			ptoVtaAfip="00030";
		}
		
		if(ptoVtaAfip.equals("9999")){  // Meter una opción para quien es adminsitrador de hoteles y seleccione el que desea... 
			ptoVtaAfip="00030";
		}
 		
		boolean showConfiteria = PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_CONFITERIA);
		boolean showHabitaciones = PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_HABITACIONES);
		
%>	
<fieldset class="block-labels">
   <input type="button" class="mostrarOcultar" id="btnOcultar" value="Ocultar" onclick="javascript:ocultarColumna();"/>
   <input type="button" class="mostrarOcultar" id="btnMostrar" value="Mostrar" onclick="javascript:mostrarColumna();"/>
</fieldset>

<table>
 <tr>
  <td id="tdCol1">
     <fieldset class="block-labels">
		<legend>Gestión de Consumos </legend>
		<table>
		  <tr>
		  <%if(showConfiteria) {%>
		   <td> 
		       <input type="button" class="menu" value="CONFITERIA" onclick="javascript:mostrarUnidades('MESAS','<%=ptoVtaAfip%>');"/>
		  </td>
		  <%}%>
		  <td>&nbsp;</td>
		  <%if(showHabitaciones) {%>
		  <td>
		    <input type="button" class="menu" value="HABITACIONES"  onclick="javascript:mostrarUnidades('HABITACIONESGRUPOS','<%=ptoVtaAfip%>');"/>
		  </td>  
		   <%}%>
		  </tr>  
		  
		 
		</table>
		<table>
		 <tr id="agrupacion_habitaciones">
		   <td colspan="2">
		      <div id="div_agrupacion_habitaciones"></div>
		   </td>
		  </tr>
		</table>
		<table>
		<tr>
		  <td width="100%">
		    <div id="div_seleccion"></div>
		    <br>
		    <div id="div_libre" class="estadoLibre">
		       <p><strong>LIBRE</strong>
		    </div>		    
		    <div id="div_ocupado" class="estadoOcupado">
		       <p><strong>OCUPADA</strong>
		    </div>
		    <div id="div_con_cuenta" class="estadoConCuenta">
		       <p><strong>CON PEDIDO DE CUENTA</strong>
		    </div>
		    
		    <div id="div_sin_consumo" class="estadoLibre">
		       <p><strong>SIN CONSUMO</strong>
		    </div>	
		    
		    <div id="div_con_consumo" class="estadoOcupado">
		       <p><strong>CON CONSUMO</strong>
		    </div>
		    
		    <div id="div_deshabilitado" class="estadoDeshabilitado">
		       <p><strong>DESHABILITADO</strong>
		    </div>
		    
		  </td>
		</tr>
		</table>
		
	</fieldset>
  </td>
  <td valign="top">
    <div id="div_identificacionPersonal" >
    	<fieldset class="block-labels">
		  <legend>Identificación </legend>
		<table>
		 <tr id="login">
		     <td colspan="15">
		       <input type="password" class="login" id="id_login" size="50" width="50px" onKeyUp="javascript:testeaIngreso(this,'<%=ptoVtaAfip%>')"/>
		     </td>
		     <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		     <td>
		       <input type="button" class="login" value="Ingresar"  onclick="javascript:verificarLogin('<%=ptoVtaAfip%>');"/>
		     </td>
		  </tr>
		  
		  <tr id="logout">
		     <td colspan="15">
		       <input type="text" value="" class="login" id="id_logout" size="50" width="50px"/>
		     </td>
		      <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		     <td>
		       <input type="button" class="login" value="Cerrar Sesión"  onclick="javascript:logout('<%=ptoVtaAfip%>');"/>
		     </td>
		  </tr>
		  
		</table>
		</fieldset>
    </div>
    
    <div id="div_consumos">
     <fieldset class="block-labels">
		<legend>Consumos </legend>
		<liferay-util:include page="/html/portlet/hoteles/consumos_hotel.jsp">
							
		</liferay-util:include>
		
	 </fieldset>	
    </div>
  </td>
 </tr> 	
</table>	
		
<input type="hidden" id="id_empleado"/>	
<input type="hidden" id="tipo_unidad"/>		
		
<script type="text/javascript">
jQuery("#div_ocupado").hide();
jQuery("#div_libre").hide();
jQuery("#div_con_cuenta").hide();
jQuery("#div_identificacionPersonal").hide();
jQuery("#div_consumos").hide();
jQuery("#div_sin_consumo").hide();
jQuery("#div_con_consumo").hide();
jQuery("#div_deshabilitado").hide();
jQuery("#logout").hide();

jQuery("#id_empleado").val("");
jQuery("#tipo_unidad").val("");


jQuery("#btnMostrar").hide();


var popupHOT;
var popupTIC;


function mostrarUnidades(tipo,ptoVta){
	jQuery("#div_identificacionPersonal").hide();
	jQuery("#div_consumos").hide();
	
	jQuery("#tipo_unidad").val(tipo);
	
	jQuery("#id_login").val("");
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/configuracion_unidades&tipo='+tipo;
	    url +='&ptovta='+ptoVta;
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
		
			var obj = jQuery.parseJSON(data);
	        if("MESAS"==tipo){	
	           jQuery("#div_seleccion").show();
			   jQuery("#div_seleccion").html(obj.cadena);
			   jQuery("#div_agrupacion_habitaciones").hide();
 			   jQuery("#div_ocupado").show();
 			   jQuery("#div_libre").show();
 			   jQuery("#div_con_cuenta").show();
 			   jQuery("#div_deshabilitado").show();
 			   jQuery("#div_sin_consumo").hide();
 			   jQuery("#div_con_consumo").hide();
 			    
	        }   

	        if("HABITACIONESGRUPOS"==tipo){
	        	jQuery("#div_agrupacion_habitaciones").show();
	        	jQuery("#div_agrupacion_habitaciones").html(obj.cadena);
	        	jQuery("#div_seleccion").hide();
	        	jQuery("#div_ocupado").hide();
	        	jQuery("#div_libre").hide();
	 			jQuery("#div_con_cuenta").hide();
	 			jQuery("#div_deshabilitado").hide();
	 			jQuery("#div_sin_consumo").show();
	 			jQuery("#div_con_consumo").show();
	 			
//	 			jQuery("#div_identificacionPersonal").hide();
	        }
	        
      
	        if("true"==obj.necesitaLogin){
			      jQuery("#div_identificacionPersonal").show();
			      jQuery("#login").show();
			      jQuery("#logout").hide();
			}else{
				  jQuery("#div_identificacionPersonal").hide();
			}  
	        
	        jQuery("#div_consumos_categorias").html(obj.categorias);
	        jQuery("#div_consumos_productos").html('');
	        jQuery("#id_empleado").val(obj.empleado_id);
		}
	});
}

function mostrarUnidadesGrupo(obj){
	var str=obj.id;
	var res = str.split("_");
	var tipo=res[1];
	var ptoVta=res[2];
	var idGrupo=res[3];
	var idEmpleado=jQuery("#id_empleado").val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/configuracion_unidades&tipo='+tipo;
    url +='&ptovta='+ptoVta;
    url +='&idgrupo='+idGrupo;
    url +='&idempleado='+idEmpleado;
    jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
		
			var obj = jQuery.parseJSON(data);
	        if("HABITACIONES"==tipo){
	        	jQuery("#div_agrupacion_habitaciones").show();
	        	jQuery("#div_seleccion").show();
	        	jQuery("#div_seleccion").html(obj.cadena);
	        	jQuery("#div_ocupado").hide();
	        	jQuery("#div_libre").hide();
	 			jQuery("#div_con_cuenta").hide();
	        }
	        
		}
	});
}

function mostrarMesa(obj){
	var str=obj.id;
	var res = str.split("_");
	var tipo=res[1];
	var ptoVta=res[2];
	var idMesa=res[3];
	
	
	
	jQuery("#div_consumos").show();
	jQuery("#div_comandos").show();
	jQuery("#btn_cerrar_habitacion").hide();
	
	jQuery("#<portlet:namespace />unidad_id").val(idMesa);
	jQuery("#<portlet:namespace />unidad_tipo").val(tipo);
	jQuery("#<portlet:namespace />unidad_hotel").val(ptoVta);
	
	
	jQuery("#<portlet:namespace />lb_id").html("MESA: " + idMesa);
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/productos_gestion';
    url +='&ptovta='+ptoVta;
    url +='&tipo='+tipo;
    url +='&producto=';
    url +='&unidad='+idMesa;
    url +='&cantidad=0';
    url +='&cmd=';
    
    jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			var obj = jQuery.parseJSON(data);
	       	jQuery("#div_consumos_detalle").html(obj.consumos);
	       	jQuery("#div_reimpresion_asignacion_habitacion").html(obj.ultimaasignacion);
	       	jQuery("#lb_Totales").html(obj.total);
	       	if(obj.estado=="PCU" ){
	       	 jQuery("#btn_factura").show();
	       	 jQuery("#btn_precomanda").show(); 
	       	 jQuery("#btn_habitacion").show();
	       	 jQuery("#btn_pedir_cuenta").hide();
	       	 jQuery("#btn_cierre").show();
	       	}else if(obj.estado=="OCU") {
	       	 jQuery("#btn_factura").hide();
	       	 jQuery("#btn_precomanda").hide();
	       	 jQuery("#btn_habitacion").hide();
	       	 jQuery("#btn_pedir_cuenta").show();
	       	 jQuery("#btn_cierre").hide();
	       	}else{
	       	 jQuery("#btn_precomanda").hide();
	       	 jQuery("#btn_factura").hide();
		     jQuery("#btn_habitacion").hide();
		     jQuery("#btn_pedir_cuenta").hide();
		     jQuery("#btn_cierre").hide();
	        }
		}
	});
}


function mostrarHabitacion(obj){
	var str=obj.id;
	var res = str.split("_");
	var tipo=res[1];
	var ptoVta=res[2];
	var idHabitacion=res[3];
	
	jQuery("#div_consumos").show();
	jQuery("#<portlet:namespace />unidad_id").val(idHabitacion);
	jQuery("#<portlet:namespace />unidad_tipo").val(tipo);
	jQuery("#<portlet:namespace />unidad_hotel").val(ptoVta);
	jQuery("#div_comandos").show();
	
	jQuery("#btn_precomanda").hide();
	jQuery("#btn_factura").hide();
	jQuery("#btn_habitacion").hide();
    jQuery("#btn_pedir_cuenta").hide();
    jQuery("#btn_cierre").hide();
    jQuery("#btn_cerrar_habitacion").show();
	
	jQuery("#<portlet:namespace />lb_id").html("HABITACION: " + idHabitacion);

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/productos_gestion';
    url +='&ptovta='+ptoVta;
    url +='&tipo='+tipo;
    url +='&producto=';
    url +='&unidad='+idHabitacion;
    url +='&cantidad=0';
    url +='&cmd=';
    
    jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
		
			var obj = jQuery.parseJSON(data);
	       	jQuery("#div_consumos_detalle").html(obj.consumos);
	    	jQuery("#div_reimpresion_asignacion_habitacion").html(obj.ultimaasignacion);
	       	jQuery("#lb_Totales").html(obj.total);
		}
	});
	
}


function agregar_producto(obj){
	var str=obj.id;
	var res = str.split("_");
	var ptoVta=res[1];
	var codigo=res[2];
	var cantidad = jQuery('#<portlet:namespace />cantidad').val();
	var unidadTipo=jQuery('#<portlet:namespace />unidad_tipo').val();
	var unidadId=jQuery('#<portlet:namespace />unidad_id').val();
	var personalId = jQuery('#id_empleado').val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/productos_gestion';
    url +='&ptovta='+ptoVta;
    url +='&tipo='+unidadTipo;
    url +='&producto='+codigo;
    url +='&unidad='+unidadId;
    url +='&cantidad='+cantidad;
    url +='&idpersonal='+personalId 
    url +='&cmd=alta';
    
    jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
		
			var obj = jQuery.parseJSON(data);
	       	jQuery("#div_consumos_detalle").html(obj.consumos);
	       	jQuery("#lb_Totales").html(obj.total);
	       	jQuery('#<portlet:namespace />cantidad').val('1');
	       	var strobj ="unidad_"+unidadTipo+"_"+ptoVta+"_"+unidadId;   

	       	layout(strobj,unidadTipo,obj.estado);

/*	       	
	       	if(unidadTipo=='MESAS'){
	       	  if(obj.estado=="LIB" ){
	       		jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades');
	       	    jQuery("#btn_factura").hide();
	       	    jQuery("#btn_precomanda").hide();
		        jQuery("#btn_habitacion").hide();
		        jQuery("#btn_pedir_cuenta").hide();
		        jQuery("#btn_cierre").hide();
	       	  }else if (obj.estado=="OCU"){
	       		jQuery('#'+strobj).removeClass('unidades').addClass('unidades_ocupadas'); 
	       		jQuery("#btn_factura").hide();
	       		jQuery("#btn_precomanda").hide();
		       	jQuery("#btn_habitacion").hide();
		       	jQuery("#btn_pedir_cuenta").show();
		       	jQuery("#btn_cierre").hide();
	       	  }else if(obj.estado=="PCU" ){
	       		jQuery("#btn_precomanda").show(); 
	       	     jQuery("#btn_factura").show();
		       	 jQuery("#btn_habitacion").show();
		       	 jQuery("#btn_pedir_cuenta").hide();
		       	 jQuery("#btn_cierre").show();
	          }
	       	}else{
	       		if(obj.estado=="LIB" ){
		       		jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades');
		       	}else if (obj.estado=="OCU"){
		       		jQuery('#'+strobj).removeClass('unidades').addClass('unidades_ocupadas'); 
		       	}		
	       	}
*/
		}
	});
}

function eliminar_producto(obj){
	var str=obj.id;
	var res = str.split("_");
	var ptoVta=res[1];
	var codigo=res[2];
	var cantidad = jQuery('#<portlet:namespace />cantidad').val();
	var unidadTipo=jQuery('#<portlet:namespace />unidad_tipo').val();
	var unidadId=jQuery('#<portlet:namespace />unidad_id').val();

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/productos_gestion';
    url +='&ptovta='+ptoVta;
    url +='&tipo='+unidadTipo;
    url +='&producto='+codigo;
    url +='&unidad='+unidadId;
    url +='&cantidad=0';
    url +='&cmd=baja';
    
    jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
		
			var obj = jQuery.parseJSON(data);
	       	jQuery("#div_consumos_detalle").html(obj.consumos);
	       	jQuery("#lb_Totales").html(obj.total);
	       	jQuery('#<portlet:namespace />cantidad').val('1');
	       	var strobj ="unidad_"+unidadTipo+"_"+ptoVta+"_"+unidadId; 
	       	
	       	layout(strobj,unidadTipo,obj.estado);

/*
	       	if(unidadTipo=='MESAS'){
	       	  if(obj.estado=="LIB" ){
	       		jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades');  
	       		jQuery('#'+strobj).removeClass('unidades_con_cuenta').addClass('unidades');
	       		jQuery("#btn_precomanda").hide();
	       		jQuery("#btn_factura").hide();
		        jQuery("#btn_habitacion").hide();
		        jQuery("#btn_pedir_cuenta").hide();
		        jQuery("#btn_cierre").hide();
	       	  }else if (obj.estado=="OCU"){
	       		jQuery('#'+strobj).removeClass('unidades').addClass('unidades_ocupadas');
	       		jQuery("#btn_precomanda").hide();
	       		jQuery("#btn_factura").hide();
		       	jQuery("#btn_habitacion").hide();
		       	jQuery("#btn_pedir_cuenta").show();
		       	jQuery("#btn_cierre").hide();
	       	  }else if(obj.estado=="PCU" ){
	       		     jQuery("#btn_precomanda").show();
		       	     jQuery("#btn_factura").show();
			       	 jQuery("#btn_habitacion").show();
			       	 jQuery("#btn_pedir_cuenta").hide();
			       	 jQuery("#btn_cierre").show();
	       	  }     	 
	       	}else{
	       		if(obj.estado=="LIB" ){
		       		jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades');
		       	}else if (obj.estado=="OCU"){
		       		jQuery('#'+strobj).removeClass('unidades').addClass('unidades_ocupadas'); 
		       	}
	       	}
*/
		}
	});
}


function cambiar_estado(estado){
	
	var ptoVta='<%=ptoVtaAfip%>';
	var cantidad = jQuery('#<portlet:namespace />cantidad').val();
	var unidadTipo=jQuery('#<portlet:namespace />unidad_tipo').val();
	var unidadId=jQuery('#<portlet:namespace />unidad_id').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/productos_gestion';
    url +='&ptovta='+ptoVta;
    url +='&tipo='+unidadTipo;
    url +='&producto=';
    url +='&unidad='+unidadId;
    url +='&cantidad=0';
    url +='&estadoId='+estado;
    url +='&cmd=cambiaestado';
    jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			var obj = jQuery.parseJSON(data);
//	       	jQuery("#div_consumos_detalle").html(obj.consumos);
//	       	jQuery("#lb_Totales").html(obj.total);
//	       	jQuery('#<portlet:namespace />cantidad').val('1');
	       	var strobj ="unidad_"+unidadTipo+"_"+ptoVta+"_"+unidadId; 
	       	if(unidadTipo=='MESAS'){
    		
	       	  if(obj.estado=="LIB" ){
	       		jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades'); 
	       	  }else if (obj.estado=="OCU"){
	       		jQuery('#'+strobj).removeClass('unidades').addClass('unidades_ocupadas'); 
	       	  }else if (obj.estado=="PCU"){
		       	 jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades_con_cuenta');
		       	 jQuery("#btn_precomanda").show();
		       	 jQuery("#btn_factura").show();
		       	 jQuery("#btn_habitacion").show();
		       	 jQuery("#btn_pedir_cuenta").hide();
		       	 jQuery("#btn_cierre").show();
	       	  } 
	       	}  
		}
	});
	
}

function testeaIngreso(e,ptoVta){
	var txt = e.value;
	if(txt.length>=7){
		verificarLogin(ptoVta)
	}
}


function verificarLogin(ptoVta){
	jQuery("#div_consumos").hide();
	
	
	var idPersonal=jQuery("#id_login").val();
	var tipo=jQuery("#tipo_unidad").val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/configuracion_unidades_personal&tipo='+tipo;
	    url +='&ptovta='+ptoVta+'&login='+idPersonal;
	    
    
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			
			var obj = jQuery.parseJSON(data);
			
			jQuery("#id_logout").val(obj.empleado_str);
			jQuery("#id_empleado").val(obj.empleado_id);
			
			if("true" ==obj.resultado){
			   jQuery("#logout").show();
			   jQuery("#login").hide();
			   jQuery("#div_seleccion").html(obj.unidades);
			}else{
				alert("Ha introducido una identificación inválida");
			}
	        
		}
	});
	
}

function logout(ptoVta){
	jQuery("#div_consumos").hide();
	jQuery("#id_login").val("");
	var tipo=jQuery("#tipo_unidad").val();
   
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/configuracion_unidades_personal&tipo='+tipo;
	    url +='&ptovta='+ptoVta+'&login=0';    
   
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			
			var obj = jQuery.parseJSON(data);
			
			jQuery("#id_empleado").val("");
			jQuery("#logout").hide();
			jQuery("#login").show();
			jQuery("#div_seleccion").html(obj.unidades);
		}
	});
	
	
	
}
	
function asignar_consumo_habitacion(){
	var unidadId=jQuery('#<portlet:namespace />unidad_id').val();
	var ptoVta='<%=ptoVtaAfip%>';
	var unidadTipo=jQuery('#<portlet:namespace />unidad_tipo').val();
	var empleadoId=jQuery("#id_empleado").val();
	
	popupHOT = Liferay.Popup({title:"ASIGNACIÓN CONSUMOS A HABITACIÓN",modal:true,width:700,position:[150,10],xy: ['center', 100],
		 onClose: function() {
			 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/productos_gestion';
			    url +='&ptovta='+ptoVta;
			    url +='&tipo='+unidadTipo;
			    url +='&producto=';
			    url +='&unidad='+unidadId;
			    url +='&cantidad=0';
			    url +='&cmd=';
			    jQuery.ajax({   
					url: url,
					async:false,
					success: function(data){
					
						var obj = jQuery.parseJSON(data);
				       	jQuery("#div_consumos_detalle").html(obj.consumos);
				       	jQuery("#lb_Totales").html(obj.total);
				        var strobj ="unidad_"+unidadTipo+"_"+ptoVta+"_"+unidadId; 
				        
				        layout(strobj,unidadTipo,obj.estado);
/*				       	
				       	if(obj.estado=="PCU" ){
				       	 jQuery("#btn_precomanda").show();	
				       	 jQuery("#btn_factura").show();
				       	 jQuery("#btn_habitacion").show();
				       	 jQuery("#btn_pedir_cuenta").hide();
				       	 jQuery("#btn_cierre").show();
				       	}else if(obj.estado=="OCU") {
				       	 jQuery("#btn_factura").hide();
				       	 jQuery("#btn_habitacion").hide();
				       	 jQuery("#btn_precomanda").hide();
				       	 if(tipo=="MESAS"){
				       	   jQuery("#btn_pedir_cuenta").show();
				       	 }else{
				       	   jQuery("#btn_pedir_cuenta").hide(); 
				       	 }  
				       	 jQuery("#btn_cierre").hide();
				       	}else{
				       	 jQuery("#btn_precomanda").hide();	
				       	 jQuery("#btn_factura").hide();
					     jQuery("#btn_habitacion").hide();
					     jQuery("#btn_pedir_cuenta").hide();
					     jQuery("#btn_cierre").hide();
					     
					   
						 jQuery('#'+strobj).removeClass('unidades_con_cuenta').addClass('unidades');
						 jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades');
				        }
*/				       	
				        
					}
				});

			 			 
	 	}});
	
     var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/hoteles/comandos_confiteria';
         url +='&ptovta='+ptoVta;
		 url +='&tipo='+unidadTipo;
		 url +='&unidad='+unidadId;
		 url +='&idpersonal='+empleadoId;
		 url +='&cmd=asignarconsumohabitacion';
         url += '&rnd=' + Math.floor(Math.random()*100);
     jQuery(popupHOT).load(url);
	
}



function facturar_consumos(){
	var unidadId=jQuery('#<portlet:namespace />unidad_id').val();
	var ptoVta='<%=ptoVtaAfip%>';
	var unidadTipo=jQuery('#<portlet:namespace />unidad_tipo').val();
	var empleadoId=jQuery("#id_empleado").val();
	
	popupHOT = Liferay.Popup({title:"FACTURAR CONSUMOS",modal:true,width:1100,position:[100,10],xy: ['center', 100],
		 onClose: function() {
			 
			 
			 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/productos_gestion';
			    url +='&ptovta='+ptoVta;
			    url +='&tipo='+unidadTipo;
			    url +='&producto=';
			    url +='&unidad='+unidadId;
			    url +='&cantidad=0';
			    url +='&cmd=verificarfacturaconsumos';
			    jQuery.ajax({   
					url: url,
					async:false,
					success: function(data){
					
						var obj = jQuery.parseJSON(data);
						var strobj ="unidad_"+unidadTipo+"_"+ptoVta+"_"+unidadId; 
				       	jQuery("#div_consumos_detalle").html(obj.consumos);
				       	jQuery("#lb_Totales").html(obj.total);
				       	
				     	layout(strobj,unidadTipo,obj.estado);
				       	
				       	
/*				       	
				       	if(obj.estado=="PCU" ){
				       	 jQuery("#btn_precomanda").show();	
				       	 jQuery("#btn_factura").show();
				       	 jQuery("#btn_habitacion").show();
				       	 jQuery("#btn_pedir_cuenta").hide();
				       	 jQuery("#btn_cierre").show();
				       	}else if(obj.estado=="OCU") {
				       	 jQuery("#btn_factura").hide();
				       	 jQuery("#btn_habitacion").hide();
				       	 jQuery("#btn_precomanda").hide();
				       	 if(tipo=="MESAS"){
				       	   jQuery("#btn_pedir_cuenta").show();
				       	 }else{
				       	   jQuery("#btn_pedir_cuenta").hide(); 
				       	 }  
				       	 jQuery("#btn_cierre").hide();
				       	}else{
				       	 jQuery("#btn_factura").hide();
					     jQuery("#btn_habitacion").hide();
					     jQuery("#btn_pedir_cuenta").hide();
					     jQuery("#btn_cierre").hide();
					     jQuery("#btn_precomanda").hide();
					     jQuery('#'+strobj).removeClass('unidades_con_cuenta').addClass('unidades');
						 jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades');
				        }
*/				        
					}
				});

	 	}});
	
     var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/hoteles/comandos_confiteria';
         url +='&ptovta='+ptoVta;
		 url +='&tipo='+unidadTipo;
		 url +='&unidad='+unidadId;
		 url +='&idpersonal='+empleadoId;
		 url +='&cmd=facturarconsumos';
         url += '&rnd=' + Math.floor(Math.random()*100);
        
     jQuery(popupHOT).load(url);
	
}


function liberar_consumos(){
	
	var unidadId=jQuery('#<portlet:namespace />unidad_id').val();
	var ptoVta='<%=ptoVtaAfip%>';
	var unidadTipo=jQuery('#<portlet:namespace />unidad_tipo').val();
	

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/productos_gestion';
    url +='&ptovta='+ptoVta;
    url +='&tipo='+unidadTipo;
    url +='&producto=';
    url +='&unidad='+unidadId;
    url +='&cantidad=0';
    url +='&cmd=liberar_consumos_mesas';
    jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
		
			var obj = jQuery.parseJSON(data);
	       	jQuery("#div_consumos_detalle").html(obj.consumos);
	       	jQuery("#lb_Totales").html(obj.total);
	       	jQuery('#<portlet:namespace />cantidad').val('1');
	       	var strobj ="unidad_"+unidadTipo+"_"+ptoVta+"_"+unidadId; 
	       	
	     	layout(strobj,unidadTipo,obj.estado);
/*	     	
	       	if(unidadTipo=='MESAS'){
	       	  if(obj.estado=="LIB" ){
	       		jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades');  
	       		jQuery('#'+strobj).removeClass('unidades_con_cuenta').addClass('unidades');
	       		jQuery("#btn_precomanda").hide();
	       		jQuery("#btn_factura").hide();
		        jQuery("#btn_habitacion").hide();
		        jQuery("#btn_pedir_cuenta").hide();
		        jQuery("#btn_cierre").hide();
	       	  }else if (obj.estado=="OCU"){
	       		jQuery('#'+strobj).removeClass('unidades').addClass('unidades_ocupadas');
	       		jQuery("#btn_precomanda").hide();
	       		jQuery("#btn_factura").hide();
		       	jQuery("#btn_habitacion").hide();
		       	jQuery("#btn_pedir_cuenta").show();
		       	jQuery("#btn_cierre").hide();
	       	  }else if(obj.estado=="PCU" ){
	       		     jQuery("#btn_precomanda").show();
		       	     jQuery("#btn_factura").show();
			       	 jQuery("#btn_habitacion").show();
			       	 jQuery("#btn_pedir_cuenta").hide();
			       	 jQuery("#btn_cierre").show();
	       	  }     	 
	       	}else{
	       		if(obj.estado=="LIB" ){
		       		jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades');
		       	}else if (obj.estado=="OCU"){
		       		jQuery('#'+strobj).removeClass('unidades').addClass('unidades_ocupadas'); 
		       	}
	       	} 
*/	     	
		}
	});
}

function ocultarColumna(){
	jQuery("#tdCol1").hide();
	jQuery("#btnMostrar").show();
	jQuery("#btnOcultar").hide();
}


function mostrarColumna(){
	jQuery("#tdCol1").show();
	jQuery("#btnOcultar").show();
	jQuery("#btnMostrar").hide();
}


function precomanda_consumos(){
	
	var unidadId=jQuery('#<portlet:namespace />unidad_id').val();
	var ptoVta='<%=ptoVtaAfip%>';
	var unidadTipo=jQuery('#<portlet:namespace />unidad_tipo').val();
	

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/productos_gestion';
    url +='&ptovta='+ptoVta;
    url +='&tipo='+unidadTipo;
    url +='&producto=';
    url +='&unidad='+unidadId;
    url +='&cantidad=0';
    url +='&cmd=precomanda_consumos_mesas';
    url +='&estadoId=PCU';
    jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
		
			var obj = jQuery.parseJSON(data);
	       	jQuery("#div_consumos_detalle").html(obj.consumos);
	       	jQuery("#lb_Totales").html(obj.total);
	       	jQuery('#<portlet:namespace />cantidad').val('1');
	       	var strobj ="unidad_"+unidadTipo+"_"+ptoVta+"_"+unidadId; 
	       	
	       	layout(strobj,unidadTipo,obj.estado);
	       	
	       	
		}
	});
}

function layout(strobj,unidadTipo,estado){
	if(unidadTipo=='MESAS'){
     	  if(estado=="LIB" ){
     		jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades');  
     		jQuery('#'+strobj).removeClass('unidades_con_cuenta').addClass('unidades');
     		jQuery("#btn_precomanda").hide();
     		jQuery("#btn_factura").hide();
	        jQuery("#btn_habitacion").hide();
	        jQuery("#btn_pedir_cuenta").hide();
	        jQuery("#btn_cierre").hide();
     	  }else if (estado=="OCU"){
     		jQuery('#'+strobj).removeClass('unidades').addClass('unidades_ocupadas');
     		jQuery("#btn_precomanda").hide();
     		jQuery("#btn_factura").hide();
	       	jQuery("#btn_habitacion").hide();
	       	jQuery("#btn_pedir_cuenta").show();
	       	jQuery("#btn_cierre").hide();
     	  }else if(estado=="PCU" ){
     		     jQuery("#btn_precomanda").show();
	       	     jQuery("#btn_factura").show();
		       	 jQuery("#btn_habitacion").show();
		       	 jQuery("#btn_pedir_cuenta").hide();
		       	 jQuery("#btn_cierre").show();
     	  }     	 
     	}else{
     		if(estado=="LIB" ){
	       		jQuery('#'+strobj).removeClass('unidades_ocupadas').addClass('unidades');
	       	}else if (estado=="OCU"){
	       		jQuery('#'+strobj).removeClass('unidades').addClass('unidades_ocupadas'); 
	       	}
     	}  
}


function reimprimirUltimaAsignacion(){
	var unidadId=jQuery('#<portlet:namespace />unidad_id').val();
	var ptoVta='<%=ptoVtaAfip%>';
	var unidadTipo=jQuery('#<portlet:namespace />unidad_tipo').val();
	

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/productos_gestion';
    url +='&ptovta='+ptoVta;
    url +='&tipo='+unidadTipo;
    url +='&producto=';
    url +='&unidad='+unidadId;
    url +='&cantidad=0';
    url +='&cmd=ticket_consumo_habitaciones';
    url +='&estadoId=';
    jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
		
			var obj = jQuery.parseJSON(data);
	       	jQuery("#div_consumos_detalle").html(obj.consumos);
	       	jQuery("#lb_Totales").html(obj.total);
	       	jQuery('#<portlet:namespace />cantidad').val('1');
	       	var strobj ="unidad_"+unidadTipo+"_"+ptoVta+"_"+unidadId; 
	       	
	       	layout(strobj,unidadTipo,obj.estado);
	       	
	       	
		}
	});
}


</script>