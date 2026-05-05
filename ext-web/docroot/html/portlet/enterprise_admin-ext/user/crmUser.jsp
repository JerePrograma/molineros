<%
/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
%>

<%@ include file="/html/portlet/enterprise_admin-ext/init.jsp" %>

<%
User selUser = (User)request.getAttribute("user.selUser");
DerivacionNotificacion derivacionNotificacion=CrmServiceUtil.getNotificacionDerivacion(selUser.getScreenName());
if(derivacionNotificacion==null){
	derivacionNotificacion=new DerivacionNotificacion();
}

List<User> users = UserLocalServiceUtil.search(
		themeDisplay.getCompanyId(), null, Boolean.TRUE, null,
		QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator)null);
%>

<h3><liferay-ui:message key="crmUser" /></h3>

<c:choose>
	<c:when test="<%= selUser != null %>">
		<fieldset class="block-labels">
		    <div class="ctrl-holder">
		      
	        </div>
	        <table class="user-table">
	           <tr>
	            <td>
		          <label for="<portlet:namespace />emailAddress"><liferay-ui:message key="email-address" /></label>
			         <input id="<portlet:namespace />derivacionEmail" name="<portlet:namespace />derivacionEmail" size="70" 
			         maxlength="100" type="text" value='<%=derivacionNotificacion.getDerivacionEmail()==null?"":derivacionNotificacion.getDerivacionEmail()%>'/>
			     </td>      
		       </tr>
		       <tr><td>&nbsp;</td></tr>
		       <tr>
	            <td> 
		        <label for="<portlet:namespace />responsable"><liferay-ui:message key="responsable" /></label>
		        </td>
		        </tr>
		        <tr>
		          <td>
		           <select name="<portlet:namespace/>usuario_responsable_" id="<portlet:namespace/>usuario_responsable_" onchange="<portlet:namespace />proponeEMailResponsable()">
		              <option value="">Seleccione un Responsable</option>	       					
							<%for(User u:users) {%>
							  
							   <option value="<%=u.getUserId()%>" <%if(derivacionNotificacion.getResponsableUsr()!=null 
							           && derivacionNotificacion.getResponsableUsr().equals(u.getScreenName()) ){%> selected="selected" <% } %>>
						            <%=u.getFullName()+"(" + u.getEmailAddress()+")"%> 
						       </option>
						            
							<%}%>
								
				   </select>
				  </td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
				  <td>
				    <label for="<portlet:namespace />emailAddress"><liferay-ui:message key="email-address-crm" /></label>
			         <input id="<portlet:namespace />responsableEmail" name="<portlet:namespace />responsableEmail" size="70" 
			          maxlength="100" type="text" value='<%=derivacionNotificacion.getResponsableEmail() ==null?"":derivacionNotificacion.getResponsableEmail() %>'/>
			      </td>    
			    </tr>      
			</table>	
		</fieldset>
	</c:when>
	<c:otherwise>
		<div class="portlet-msg-info">
			<liferay-ui:message key="this-section-will-be-editable-after-creating-the-user" />
		</div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
function <portlet:namespace />proponeEMailResponsable(){
 var resp =jQuery("#<portlet:namespace/>usuario_responsable_ option:selected").text();
 var email="";
 if(resp!=null){
	 var aux = resp.split("(");
	 if(aux.length>0){
		 var aux2 =aux[1].split(")");
		 jQuery("#<portlet:namespace />responsableEmail").val(aux2[0]);
	 }
 }
}


</script>