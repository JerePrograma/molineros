<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
Random rn = new Random();
int aux=rn.nextInt(100000); 
%>
<STYLE TYPE="text/css">
TH {text-align:right}
SPAN {vertical-align:bottom}
</STYLE>

</HEAD>

<TABLE BORDER=0 align="center">
<TR>
	<TD>Descargando...</TD>
</TR>
<TR>
    <TD>    	
    	<div id="contenedor">
	    	<div ID="barraAfuera<%=aux%>" STYLE="border-style:groove;width:500px;height:15px;">
	    	<div ID="barraAdentro<%=aux%>" STYLE="background-color:#669966;height:100%;visibility:hidden;"></div>
	    	<input type="hidden" name="aleatorio" id="aleatorio" value="<%=aux%>"/>
    	</div>
    </TD>
</TR>
</TABLE>


<SCRIPT LANGUAGE="javascript">
var auto_refresh = setInterval(function(){
var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/buscar_progreso&reporte=reporte_aportes_contrib&rnd=<%=aux%>';
	jQuery.ajax({   
		url: url,
		success: function(data){
				var obj = jQuery.parseJSON(data);
				if(obj.progreso<%=aux%>>0){										
					incrementarBarra(obj.progreso<%=aux%>, obj.totalProgreso<%=aux%>)									
				}
			}
		});
		}, 6000);
function incrementarBarra(valor, total) {
		jQuery("#barraAdentro<%=aux%>").css("visibility","visible");
		var porc_progreso=Math.round((parseInt(valor)*100)/parseInt(total));		
		var progreso_barra=(parseInt(500)*parseInt(porc_progreso))/100;
    	jQuery("#barraAdentro<%=aux%>").width(parseInt(progreso_barra));    	
    	jQuery("#barraAdentro<%=aux%>").html("<p align='right'><b>"+porc_progreso+"%</b></p>");    	
    	    
}
</SCRIPT>