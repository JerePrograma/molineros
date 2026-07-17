<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>

<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="ar.com.ospim.global.beans.Parentesco" %>
<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.tesoreria.beans.PrecioPlanSuperador" %>
<%@ page import="java.util.Comparator" %>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
 	PrecioPlanSuperador precio=(PrecioPlanSuperador)request.getSession().getAttribute(WebKeysTesoreria.PRECIO_EN_SESSION);
	List<Plan> planes = (List<Plan>)request.getSession().getAttribute(WebKeysTesoreria.PRECIO_EN_SESSION_PLANES); 
	
	Collections.sort(planes, new Comparator<Object>() {
		public int compare(Object o1, Object o2) {
			return ((Comparable<String>) ((Plan) (o1)).getDescripcion())
					.compareTo(((Plan) (o2)).getDescripcion());
		}
	});
	
	Collections.sort(precio.getPlanes(), new Comparator<Object>() {
		public int compare(Object o1, Object o2) {
			return ((Comparable<String>) ((Plan) (o1)).getDescripcion())
					.compareTo(((Plan) (o2)).getDescripcion());
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
		                <select name="<portlet:namespace />planes_asignados"
					       id="<portlet:namespace />planes_asignados"  width=300 style="width: 350px; height: 100px"  size="12" multiple>
						<%for(Plan p:precio.getPlanes()) {%>
						    <option	value="<%=p.getId() %>"><%=p.getDescripcion() %></option>
						<% } %>
	                    </select>
                        </td>
                        <td>
                        
                             <input id="<portlet:namespace />agregar"   value="<<" title="Seleccionar"
		                      onClick="javascript: <portlet:namespace />agregarPlan();" type="button" />
		                     <br><br>
		                     <input id="<portlet:namespace />sacar"   value=">>" title="Deseleccionar"
		                      onClick="javascript: <portlet:namespace />sacarPlan();" type="button" /> 
                        
                        </td>
                        <td>	  
                    	<select name="<portlet:namespace />planes_disponible"
					       id="<portlet:namespace />planes_disponible"  width=300 style="width: 350px; height: 100px"  size="12" multiple>
						<%for(Plan p:planes) {%>
						    <option	value="<%=p.getId() %>"><%=p.getDescripcion() %></option>
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

