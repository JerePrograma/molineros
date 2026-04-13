<%@page import="ar.com.ospim.util.StringUtils"%>
<%@ include file="/html/portlet/correspondencia/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%
Integer total_reg = 0;
Integer offset_reg = 0;
Integer total_pag = 0;
double resto = 0;
int i=0;
String viene_de = "";

try{
 	total_reg= (Integer)session.getAttribute("total_registros");
	offset_reg= (Integer)session.getAttribute("offset_reg"); 
	
	viene_de = ParamUtil.getString(request, "llamada");
	
	if(StringUtils.checkEmpty(viene_de)){
		viene_de = (String) session.getAttribute("llamada"); 
	}
	/*Este ajuste es para mantener la busqueda de inbox */
	if(total_reg == null && offset_reg == null){
		BusquedaBandejaCorreoFiltro f = (BusquedaBandejaCorreoFiltro) session.getAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA);
		total_reg = f.getRegistrosTotal();
		offset_reg = f.getPagina();
	}	
	/* fin */
	total_pag = total_reg / 50;
	resto = total_reg % 50 ;
	if(resto > 0){
		total_pag++;
	}
	
}catch(Exception e){
	total_reg = 0;
	offset_reg = -1;
}
/* session.removeAttribute("total_registros");
session.removeAttribute("offset_reg"); 
session.removeAttribute("llamada");  */

%>
<script type="text/javascript">
</script>
<table class="lfr-table">
	<input type="hidden" name="<portlet:namespace/>total_reg"  id="<portlet:namespace/>total_registros" value="<%=total_reg%>" >
	<input type="hidden" name="<portlet:namespace/>offset_reg" id="<portlet:namespace/>offset_reg" value="<%=offset_reg%>" >
	<input type="hidden" name="<portlet:namespace/>viene_de"   id="<portlet:namespace/>viene_de" value="<%=viene_de%>" >
	
	<tr>
		<!-- <td><label>Lugar Emisión/Recepción:</label></td> -->
		<td><%=offset_reg+1%>/<%=total_pag%> páginas&nbsp;</td>
		<td><select name="<portlet:namespace/>pagina_sel" id="<portlet:namespace/>pagina_sel" 
			<%if(viene_de.equalsIgnoreCase("entradas_salidas")){ %>
				onchange="javascript:<portlet:namespace />buscarCorrespondencia();" >
			<%}%>
			<%if(viene_de.equalsIgnoreCase("inbox")){ %>
				onchange="javascript:<portlet:namespace />buscarItemsCorrespondencia();" >
			<%}%>
			<%if(viene_de.equalsIgnoreCase("paquetes")){ %>
				onchange="javascript:<portlet:namespace />buscarPaquetes();" >
			<%}%>
			
				
				<%for(i=1; i <= total_pag; i++ ) {%>
				<option value="<%=i%>" <% if (offset_reg+1==i){ %> selected="selected" <%}%> ><%=i%></option>
				<% } %>
			</select>
		</td>
	</tr>
</table>

