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
    List<Provincia> provinciasPrecio = (List<Provincia>)request.getSession().getAttribute(WebKeysTesoreria.PRECIO_EN_SESSION_PROVINCIAS);
    
   
    Collections.sort(provinciasPrecio, new Comparator<Object>() {
    		public int compare(Object o1, Object o2) {
    			return ((Comparable<String>) ((Provincia) (o1)).getDescripcion())
    					.compareTo(((Provincia) (o2)).getDescripcion());
    		}
    	});
	
	
	 
	Collections.sort(precio.getProvincias(), new Comparator<Object>() {
		public int compare(Object o1, Object o2) {
			return ((Comparable<String>) ((Provincia) (o1)).getDescripcion())
					.compareTo(((Provincia) (o2)).getDescripcion());
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
		                
		                
		                   <select name="<portlet:namespace />provincias_asignados"
					            id="<portlet:namespace />provincias_asignados"  width=200 style="width: 200px; height: 100px"  size="12" multiple>
						      <%for(Provincia p:precio.getProvincias()) {%>
						         <option	value="<%=p.getId() %>"><%=p.getDescripcion() %></option>
						      <% } %>
	                       </select>
                        </td>
                        <td>
                        
                             <input id="<portlet:namespace />agregar"   value="<<" title="Seleccionar"
		                      onClick="javascript: <portlet:namespace />agregarLocalidad();" type="button" />
		                     <br><br>
		                     <input id="<portlet:namespace />sacar"   value=">>" title="Deseleccionar"
		                      onClick="javascript: <portlet:namespace />sacarLocalidad();" type="button" /> 
                        
                        </td>
                        <td> 
                    	    <select name="<portlet:namespace />provincias_disponible"
					            id="<portlet:namespace />provincias_disponible"  width=200 style="width: 200px; height: 100px"  size="12" multiple>
					           <%for(Provincia p:provinciasPrecio) {%>
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

