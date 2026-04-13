<%@include file="/html/portlet/hoteles/hoteles.css"%>
<%@ include file="/html/portlet/hoteles/init.jsp" %>
 
 <div id="div_reimpresion_asignacion_habitacion">
 </div>

<div id="div_titulo" class="titulo">
    <h1>
      <label id="<portlet:namespace />lb_id"></label>
    </h1>
</div>


<input type="hidden" name="<portlet:namespace />unidad_tipo" id="<portlet:namespace />unidad_tipo"/>
<input type="hidden" name="<portlet:namespace />unidad_id" id="<portlet:namespace />unidad_id"/>
<input type="hidden" name="<portlet:namespace />unidad_hotel" id="<portlet:namespace />unidad_hotel" />

<table>
<tr>
 <td width="30%">
   <table>
     <tr>
      <td colspan="2">
      <div id="div_consumos_titulo" class="consumos_titulo">
<!--        <th>Código</th> -->
       <table><tr><th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th><th width="50%">Descripción</th><th>Cantidad</th><th>Precio</th><th>Total</th></tr></table>
      </div>  
       <div id="div_consumos_detalle" class="consumos">
       </div>
      </td> 
     </tr> 
     <tr> 
       <td> 
         <span style="color: blue;font-size: 20px"><b>Total</b> </span>
       </td>
       <td align="right">
         <label id="lb_Totales" style="font-size: 20px"></label>
       </td>  
     </tr>  
    
   </table> 
 </td>
 
 <td width="70%" valign="top" >
   <div id="div_comandos" class="subtitulo">
   <fieldset>
    <h4>
   <span class="label">COMANDOS</span>
   </h4>
     <table>
     <tr> <td>&nbsp;</td> </tr>
     <tr>
       <td>
        <input type="button" id="btn_cerrar_habitacion" class="comandos" value="CERRAR" onclick="javascript:asignar_consumo_habitacion();" />
       </td> 
     </tr>
     
     <tr> <td>&nbsp;</td> </tr>
     <tr>
       <td>
        <input type="button" id="btn_pedir_cuenta" class="comandos" value="PEDIR CUENTA" onclick="javascript:cambiar_estado('PCU');"/>
        <input type="button" id="btn_precomanda" class="comandos" value="PRECOMANDA" onclick="javascript:precomanda_consumos();" />
       </td> 
     </tr>
     
     <tr> <td>&nbsp;</td> </tr>
     <tr>
        <td>
        <input type="button" id="btn_factura" class="comandos" value="FACTURA" onclick="javascript:facturar_consumos();" />
        </td>
     </tr>
     <tr> <td>&nbsp;</td> </tr>
     <tr>
       <td>
        <input type="button" id="btn_habitacion" class="comandos" value="A HABITACION" onclick="javascript:asignar_consumo_habitacion();" />
       </td> 
     </tr>
     
     <tr> <td>&nbsp;</td> </tr>
     <tr>
      <td>
        <input type="button" id="btn_cierre" class="comandos" value="LIBERAR" onclick="javascript:liberar_consumos();" />
      </td>  
     </tr>
     
     </table>
   </fieldset>
   </div>
 </td>   
</tr>
</table>

<table>
  <tr>
    <td width="100%" valign="top" >
   <div id="div_productos_seccion" class="subtitulo">
   <fieldset>
   <legend>Selección de Productos </legend>
    <h4>
   <span class="label">CATEGORIAS</span>
   </h4>
    <div id="div_consumos_categorias"> </div>
    <div id="div_categoria" class="subtitulo">
     <h4>
      <span class="label">OPCIONES</span>
     </h4> 
    </div>
    
    <label style="font-weight: bold;font-size:14px;">Cantidad: </label>
    <input id="<portlet:namespace />cantidad"
					name="<portlet:namespace />cantidad" size="5" onkeypress="return validar_numero(event)"
					maxlength="20" type="text" style="font-weight: bold;font-size:14px; text-align: right;"
					value="1" />
					
    <div id="div_consumos_productos"> </div>
   </fieldset>
   </div>
 </td>
  </tr>
</table>



<script type="text/javascript">

function validar_numero(e){
	var key = window.Event ? e.which : e.keyCode 
			return ((key >= 48 && key <= 57) || (key==8)) 
}

function mostrar_productos(obj){
	var str=obj.id;
	var res = str.split("_");
	var tipo=res[1];
	var ptoVta=res[2];
	var categoria=res[3];
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/productos_por_categoria';
    url +='&ptovta='+ptoVta;
    url +='&categoria='+categoria;
    url +='&tipo='+tipo;
    jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
		
			var obj = jQuery.parseJSON(data);
	       	jQuery("#div_consumos_productos").html(obj.productos);
	        
		}
	});
    
    jQuery('#div_consumos_categorias :input').each(function(idx, el) {
	
    	if(obj.id==el.id){
	    	obj.style.background="#F7D358";
	    }else{
	    	el.style.background="#79bbff";
	    }
    });
	
}




</script>
