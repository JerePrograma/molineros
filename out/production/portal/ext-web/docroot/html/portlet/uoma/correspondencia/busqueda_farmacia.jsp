<%@ include file="/html/portlet/uoma/init.jsp" %>

<%
String id_farmacia=ParamUtil.getString(request,"id_farmacia");
String farmacia=ParamUtil.getString(request,"farmacia");
String id_farmacia_serial=ParamUtil.getString(request,"id_farmacia_serial");
%>

<table>
	<tr>
		<td><input type="hidden" id="<portlet:namespace />id_farmacia_serial" name="<portlet:namespace />id_farmacia_serial" value="<%= id_farmacia_serial %>" />
		</td>
	</tr>	
	<tr>
		<td>
			<input id="<portlet:namespace />id_farmacia" name="<portlet:namespace />id_farmacia" maxlenght="4" size="4" type="text" value="<%=id_farmacia%>" 
				onBlur="javascript:<portlet:namespace />pierdeFocoFarmacia();" onKeyUp="javascript:<portlet:namespace />buscarFarmaciaOnDiv(event)"/>
		</td>
		<td>&nbsp;&nbsp;</td>				
		<td>
			<input id="<portlet:namespace />farmacia" name="<portlet:namespace />farmacia" size="30" type="text" 
				   value="<%=farmacia%>" onKeyUp="javascript:<portlet:namespace />buscarFarmaciaOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFocoFarmacia();"/>
		</td>
		<td>&nbsp;
		<div id="<portlet:namespace />btnBuscarFarmacia" style="float:left;">
			<a href="javascript: void(0);" 
				onclick="javascript:<portlet:namespace />buscarFarmacia();" tabindex="-1">Buscar</a>
		</div>
		</td>
	</tr>	
</table>	
<input id="<portlet:namespace />farmacia_seleccionada" name="<portlet:namespace />farmacia_seleccionada" type="hidden" value=""/>
<div id='divFarmacia' style="float:right;">
</div>
	
<script type="text/javascript">
mylib.article.init();

var popup;
function <portlet:namespace />buscarFarmacia() {
	var id_farmacia=jQuery("#<portlet:namespace />id_farmacia").val();
    var farmacia=jQuery("#<portlet:namespace />farmacia").val();
	if (!<portlet:namespace />validaFormFarmacia(id_farmacia,farmacia)){
		return false;
	}
    popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-farmacias" />",modal:true,width:420});        
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/buscar_farmacia&id_farmacia='+id_farmacia+
    		  '&farmacia='+encodeURI(farmacia);

    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COR_1_"))%>'> 
    	url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/buscar_farmacia&id_farmacia='+id_farmacia+
		  '&farmacia='+encodeURI(farmacia);
	</c:if>		
	
	  <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_LIQ_1_"))%>'> 
  		url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_farmacia&id_farmacia='+id_farmacia+
		  '&farmacia='+encodeURI(farmacia);
	</c:if>	
	     	
	jQuery(popup).load(url);    
}
function <portlet:namespace />buscarFarmaciaOnDiv(e){
	//Se modificó el campo, debemos cambiar el selecc	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />farmacia_seleccionada").val() == "1"  && (keyPressed!=9 && keyPressed!=16)){
		jQuery("#<portlet:namespace />farmacia").val("");
		jQuery("#<portlet:namespace />id_farmacia").val("");
		jQuery("#<portlet:namespace />farmacia_seleccionada").val("");
		jQuery("#<portlet:namespace />btnBuscarFarmacia").show();
		return false;
	}
	var id_farmacia=jQuery("#<portlet:namespace />id_farmacia").val();
    var farmacia=jQuery("#<portlet:namespace />farmacia").val();
    if((farmacia.length>=3 || id_farmacia.length>2) && (keyPressed!=9 && keyPressed!=16)){
        if(id_farmacia.length >2){
        	jQuery("#<portlet:namespace />farmacia").val("");
        }else{
    		jQuery("#<portlet:namespace />id_farmacia").val("");
        }
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/buscar_farmacia&id_farmacia='+id_farmacia+
		  '&farmacia='+encodeURI(farmacia);

	    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COR_1_"))%>'> 
		    url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/buscar_farmacia&id_farmacia='+id_farmacia+
			'&farmacia='+encodeURI(farmacia);
		</c:if>			
		
		 <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_LIQ_1_"))%>'> 
		  	url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_farmacia&id_farmacia='+id_farmacia+
				  '&farmacia='+encodeURI(farmacia);
			</c:if>	
		  
		jQuery("#divFarmacia").load(url);		
		jQuery("#divFarmacia").show();
    }else{        
    	jQuery("#divFarmacia").hide("slow");
    }     
}
function <portlet:namespace />cerrarDivFarmacia(){
	jQuery("#divFarmacia").hide("slow");		
}
function <portlet:namespace />cerrarFarmacia(){	
	<portlet:namespace />cerrarDivFarmacia();
	if(popup){		
		Liferay.Popup.close(popup);
	}
}
function <portlet:namespace />pierdeFocoFarmacia(){
	var seleccionada=jQuery("#<portlet:namespace />farmacia_seleccionada").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivFarmacia();
		return false;
	}else{				
		return false; 
	}
}
function <portlet:namespace />validaFormFarmacia(id_farmacia, farmacia){
	 if(trim(id_farmacia).length==0 && trim(farmacia).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function <portlet:namespace />resetValidFarmacia() {
	
	if (jQuery("#<portlet:namespace />id_farmacia").val() != "") {
		jQuery("#<portlet:namespace />farmacia_seleccionada").val("1")
	}
}

</script>