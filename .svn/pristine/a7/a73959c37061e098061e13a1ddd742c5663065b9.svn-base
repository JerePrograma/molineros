<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>

<%
int total_reg = 0;
int offset_reg = 0;
int total_pag = 0;
double resto = 0;
int i=0;

try{
	
	offset_reg=(Integer)session.getAttribute(WebKeysFarmaciaOspim.FILTRO_BUSQUEDA_MEDICACION_OFFSET_REG  );     // "offset_reg"
	total_reg =(Integer)session.getAttribute(WebKeysFarmaciaOspim.TOTAL_DE_REGISTROS_BUSQUEDA );     // "total_registros"
	total_pag =  total_reg  / 50;
	resto = total_reg % 50 ;
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
	<tr>
		<td>	<span><%=total_reg%>&nbsp;&nbsp; Resultados. 	</span>	</td>
		<td> 	</td>
		<td><span>&nbsp;<%=offset_reg+1%>&nbsp;/&nbsp;<%=total_pag%> páginas&nbsp; 	</span> </td>
		<td><select name="<portlet:namespace/>pagina_sel" id="<portlet:namespace/>pagina_sel"  
				onchange="javascript:<portlet:namespace />busquedaMedicamentosOpsim(1);" >				
				<%for(i=1; i <= total_pag; i++ ) {%>
					<option value="<%=i-1%>" <% if (offset_reg==i-1){ %> selected="selected" <%}%> ><%=i%></option>
				<% } %>
			</select>			
		</td>
		
	</tr>
</table>

