<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%
int total_reg = 0;
int offset_reg = 0;
int total_pag = 0;
double resto = 0;
int i=0;

try{
 	total_reg= ParamUtil.getInteger(request,"total_conceptos");
	offset_reg=(Integer)request.getAttribute("offset_reg");
		
	/* fin */
	total_pag = total_reg / 20;
	resto = total_reg % 20 ;
	if(resto > 0){
		total_pag++;
	}
	
}catch(Exception e){
	e.printStackTrace();
	total_reg = 0;
	offset_reg = -1;
}

%>
<script type="text/javascript">
</script>
<table class="lfr-table">
	<input type="hidden" name="<portlet:namespace/>total_reg"  id="<portlet:namespace/>total_registros" value="<%=total_reg%>" >
	<input type="hidden" name="<portlet:namespace/>offset_reg" id="<portlet:namespace/>offset_reg" value="<%=offset_reg%>" >	
	
	<tr>		
		<td><%=offset_reg+1%>/<%=total_pag%> páginas&nbsp;</td>
		<td><select name="<portlet:namespace/>pagina_sel" id="<portlet:namespace/>pagina_sel"
				onchange="javascript:buscarConceptos();" >				
				<%for(i=1; i <= total_pag; i++ ) {%>
					<option value="<%=i-1%>" <% if (offset_reg==i-1){ %> selected="selected" <%}%> ><%=i%></option>
				<% } %>
			</select>
		</td>
	</tr>
</table>

