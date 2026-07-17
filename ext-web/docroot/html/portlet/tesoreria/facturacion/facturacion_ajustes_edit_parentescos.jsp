<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>

<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="ar.com.ospim.global.beans.Parentesco" %>
<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.tesoreria.beans.AjustePlanSuperador" %>
<%@ page import="java.util.Comparator" %>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
 	AjustePlanSuperador precio=(AjustePlanSuperador)request.getSession().getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION);
	List<Parentesco> parentescos = (List<Parentesco>)request.getSession().getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PARENTESCOS); 
	
	Collections.sort(parentescos, new Comparator<Object>() {
		public int compare(Object o1, Object o2) {
			return ((Comparable<String>) ((Parentesco) (o1)).getDescripcion())
					.compareTo(((Parentesco) (o2)).getDescripcion());
		}
	});
	
	Collections.sort(precio.getParentescos(), new Comparator<Object>() {
		public int compare(Object o1, Object o2) {
			return ((Comparable<String>) ((Parentesco) (o1)).getDescripcion())
					.compareTo(((Parentesco) (o2)).getDescripcion());
		}
	});
	
%>

	
	             <table>
		                <thead >
		                  <td style="background-color: #2c5e77;color:white">
		                   <label>Asignados</label>
		                  </td>
		                  
		                  <td> </td>
		                  <td style="background-color: #2c5e77;color:white">
		                  <label>Disponibles</label>
		                  </td>  
		                </thead>
		                <tr> 
		                <td> 
		                <select name="<portlet:namespace />parentesco_asignados"
					       id="<portlet:namespace />parentesco_asignados"  width=300 style="width: 350px; height: 50px"  size="12" multiple>
						<%for(Parentesco p:precio.getParentescos()) {%>
						    <option	value="<%=p.getCodigo() %>"><%=p.getDescripcion() %></option>
						<% } %>
	                    </select>
                        </td>
                        <td>
                        
                             <input id="<portlet:namespace />agregar"   value="<<" title="Seleccionar"
		                      onClick="javascript: <portlet:namespace />agregarParentesco();" type="button" />
		                     <br><br>
		                     <input id="<portlet:namespace />sacar"   value=">>" title="Deseleccionar"
		                      onClick="javascript: <portlet:namespace />sacarParentesco();" type="button" /> 
                        
                        </td>
                        <td>	  
                    	<select name="<portlet:namespace />parentesco_disponible"
					       id="<portlet:namespace />parentesco_disponible"  width=300 style="width: 350px; height: 50px"  size="12" multiple>
						<%for(Parentesco p:parentescos) {%>
						    <option	value="<%=p.getCodigo() %>"><%=p.getDescripcion() %></option>
						<% } %>
	                    </select>
	                    </td>
	                    </tr>
	                    
	                    <tfoot style="color:blue;font-weight: bold;">
                             <tr>
                             <td>Puede seleccionar uno o más valores(teclas Control o Shift) </td>
                             
                             </tr>
                        </tfoot>
		             </table>
	
<script type="text/javascript">

</script>

