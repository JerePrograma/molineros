<%@page import="ar.com.ospim.util.StringUtils"%>
<%@ include file="/html/portlet/uoma/init.jsp"%>
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
	total_pag = total_reg / 50;
	resto = total_reg % 50 ;
	if(resto > 0){
		total_pag++;
	}
	
}catch(Exception e){
	total_reg = 0;
	offset_reg = -1;
}

%>
<script type="text/javascript">
</script>
<fieldset>
<table class="lfr-table">
	<input type="hidden" name="<portlet:namespace/>total_reg"  id="<portlet:namespace/>total_registros" value="<%=total_reg%>" >
	<input type="hidden" name="<portlet:namespace/>offset_reg" id="<portlet:namespace/>offset_reg" value="<%=offset_reg%>" >
	
	<tr>
		<td><%=offset_reg+1%>/<%=total_pag%> páginas&nbsp;</td>
		<td><select name="<portlet:namespace/>pagina_sel" id="<portlet:namespace/>pagina_sel" 
				onchange="javascript:<portlet:namespace />buscarComprobantes();" >
				<%for(i=1; i <= total_pag; i++ ) {%>
				<option value="<%=i%>" <% if (offset_reg+1==i){ %> selected="selected" <%}%> ><%=i%></option>
				<% } %>
			</select>
		</td>
	</tr>
</table>
</fieldset>
