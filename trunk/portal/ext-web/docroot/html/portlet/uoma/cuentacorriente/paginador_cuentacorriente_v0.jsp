<%@ include file="/html/portlet/uoma/init.jsp" %>
<%
int total_reg = 0;
int offset_reg = 0;
int total_pag = 0;
double resto = 0;
int i=0;

try{
	
	offset_reg=(Integer)session.getAttribute(WebKeysUOMA.CTACTE_EMPRESAS_OFFSET_REG);     // "offset_reg"
	total_reg =(Integer)session.getAttribute(WebKeysUOMA.CTACTE_EMPRESAS_TOTAL_REGISTROS);     // "total_registros"
	total_pag =  total_reg  / 20;
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
	<input type="hidden"  name="<portlet:namespace/>total_reg"  id="<portlet:namespace/>total_registros" value="<%=total_reg%>" >
	<input type="hidden"  name="<portlet:namespace/>offset_reg" id="<portlet:namespace/>offset_reg" value="<%=offset_reg%>" >	
	
	<tr>		
		<td>	Total Filas encontradas:<%=total_reg%> 		</td>
		<td> 	</td>
		<td><%=offset_reg==0?1:offset_reg%>/<%=total_pag%> páginas&nbsp;</td>
		<td><select name="<portlet:namespace/>pagina_sel" id="<portlet:namespace/>pagina_sel"
				onchange="javascript:<portlet:namespace />buscar_vista_0();" >				
				<%for(i=1; i <= total_pag; i++ ) {%>
					<option value="<%=i%>" <% if (offset_reg==i){ %> selected="selected" <%}%> ><%=i%></option>
				<% } %>
			</select>			
		</td>
		
	</tr>
</table>

