<%@ include file="/html/portlet/login-ext/init.jsp" %>

<c:choose>
	<c:when test="<%= themeDisplay.isSignedIn() %>">

		<%
		String signedInAs = user.getFullName();

		if (themeDisplay.isShowMyAccountIcon()) {
			signedInAs = "<a href=\"" + HtmlUtil.escape(themeDisplay.getURLMyAccount().toString()) + "\">" + signedInAs + "</a>";
		}
		%>

		<%= LanguageUtil.format(pageContext, "you-are-signed-in-as-x", signedInAs) %>
	</c:when>
	<c:otherwise>
		<%
		String redirect = ParamUtil.getString(renderRequest, "redirect");

		String login = LoginUtil.getLogin(request, "login", company);
		String password = StringPool.BLANK;
		String coordenadas = StringPool.BLANK;
		
		//HttpSession session = req.getSession();
		Long coordenada1_x=(Long) portletSession.getAttribute("c1x");
		Long coordenada1_y=(Long) portletSession.getAttribute("c1y");
		Long coordenada2_x=(Long) portletSession.getAttribute("c2x");
		Long coordenada2_y=(Long) portletSession.getAttribute("c2y");
		 
		Boolean pedirCoord=((Boolean)portletSession.getAttribute("PEDIR_COORD"))!=null?((Boolean)portletSession.getAttribute("PEDIR_COORD")):Boolean.TRUE;
		boolean pedirCoordUser=false;
		
		if (!SessionErrors.isEmpty(renderRequest)) {		
			if(SessionErrors.contains(renderRequest,"ar.com.ospim.login.exception.UsuarioConCoordenadasException")){								
				pedirCoord=true;
			}
		}
		
		boolean rememberMe = ParamUtil.getBoolean(request, "rememberMe");

		if (Validator.isNull(authType)) {
			authType = company.getAuthType();
		}
		%>

		<form action="<portlet:actionURL secure="<%= PropsValues.COMPANY_SECURITY_AUTH_REQUIRES_HTTPS || request.isSecure() %>"><portlet:param name="saveLastPath" value="0" /><portlet:param name="struts_action" value="/login-ext/login" /></portlet:actionURL>" class="uni-form" method="post" name="<portlet:namespace />fm">
		<input name="<portlet:namespace />redirect" type="hidden" value="<%= HtmlUtil.escape(redirect) %>" />
		<input id="<portlet:namespace />rememberMe" name="<portlet:namespace />rememberMe" type="hidden" value="<%= rememberMe %>" />

		<c:if test='<%= SessionMessages.contains(request, "user_added") %>'>

			<%
			String userEmailAddress = (String)SessionMessages.get(request, "user_added");
			String userPassword = (String)SessionMessages.get(request, "user_added_password");
			%>

			<span class="portlet-msg-success">
				<c:choose>
					<c:when test="<%= company.isStrangersVerify() || Validator.isNull(userPassword) %>">
						<%= LanguageUtil.format(pageContext, "thank-you-for-creating-an-account-your-password-has-been-sent-to-x", userEmailAddress) %>
					</c:when>
					<c:otherwise>
						<%= LanguageUtil.format(pageContext, "thank-you-for-creating-an-account-your-password-is-x", new Object[] {userPassword, userEmailAddress}) %>
					</c:otherwise>
				</c:choose>
			</span>
		</c:if>

		<liferay-ui:error exception="<%= AuthException.class %>" message="authentication-failed" />
		<liferay-ui:error exception="<%= CookieNotSupportedException.class %>" message="authentication-failed-please-enable-browser-cookies" />
		<liferay-ui:error exception="<%= NoSuchUserException.class %>" message="please-enter-a-valid-login" />
		<liferay-ui:error exception="<%= PasswordExpiredException.class %>" message="your-password-has-expired" />
		<liferay-ui:error exception="<%= UserEmailAddressException.class %>" message="please-enter-a-valid-login" />
		<liferay-ui:error exception="<%= UserLockoutException.class %>" message="this-account-has-been-locked" />
		<liferay-ui:error exception="<%= UserPasswordException.class %>" message="please-enter-a-valid-password" />
		<liferay-ui:error exception="<%= UserScreenNameException.class %>" message="please-enter-a-valid-screen-name" />
		<liferay-ui:error exception="<%= UsuarioConCoordenadasException.class %>" message="debe-ingresar-coordenadas" />

		<fieldset class="block-labels">
			<div class="ctrl-holder">

				<%
				String loginLabel = null;

				if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
					loginLabel = "email-address";
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
					loginLabel = "screen-name";
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
					loginLabel = "id";
				}
				%>

				<label for="<portlet:namespace />login"><liferay-ui:message key="<%= loginLabel %>" /></label>

				<input name="<portlet:namespace />login" type="text" value="<%= HtmlUtil.escape(login) %>" />
			</div>

			<div class="ctrl-holder">
				<label for="<portlet:namespace />password"><liferay-ui:message key="password" /></label>

				<input id="<portlet:namespace />password" name="<portlet:namespace />password" type="password" value="<%= password %>" />

				<span id="<portlet:namespace />passwordCapsLockSpan" style="display: none;"><liferay-ui:message key="caps-lock-is-on" /></span>
			</div>
			<%if(pedirCoord.equals(Boolean.TRUE)){%>
				<div class="ctrl-holder">
					<label for="<portlet:namespace />coordenadas"><liferay-ui:message key="coordenadas" /></label>
					<span id="letra1"></span><span id="nro1"></span>&nbsp;<input id="<portlet:namespace />coor1" size="3" maxsize="3" name="<portlet:namespace />coor1" type="password" value="<%= password %>" />&nbsp;
					<span id="letra2"></span><span id="nro2"></span>&nbsp;<input id="<portlet:namespace />coor2" size="3" maxsize="3" name="<portlet:namespace />coor2" type="password" value="<%= password %>" />
				</div>
			<%}%>

			<c:if test="<%= company.isAutoLogin() && !PropsValues.SESSION_DISABLED %>">
				<div class="ctrl-holder inline-label">
					<label for="<portlet:namespace />rememberMeCheckbox"><liferay-ui:message key="remember-me" /></label>

					<input <%= rememberMe ? "checked" : "" %> id="<portlet:namespace />rememberMeCheckbox" type="checkbox" />
				</div>
			</c:if>

			<div class="button-holder">
				<input type="submit" value="<liferay-ui:message key="sign-in" />" />
			</div>
			
			<div >
				<img style="height: 25%; width: 25%;" align="right" src="/html/images/comodo_secure_seal_113x59_transp.png" />
				
			</div>
			
		</fieldset>
			<input type="hidden" name="PEDIR_COORD" id="PEDIR_COORD" value="<%=pedirCoord%>"/>

		</form>

		

		<script type="text/javascript">
			
			document.getElementById("letra1").innerHTML = pasarALetra(<%=coordenada1_x%>);
			document.getElementById("nro1").innerHTML = <%=coordenada1_y%>;
			document.getElementById("letra2").innerHTML = pasarALetra(<%=coordenada2_x%>);
			document.getElementById("nro2").innerHTML = <%=coordenada2_y%>;			
			jQuery(
				function() {
					jQuery('#<portlet:namespace />password').keypress(
						function(event) {
							Liferay.Util.showCapsLock(event, '<portlet:namespace />passwordCapsLockSpan');
						}
					);

					jQuery('#<portlet:namespace />rememberMeCheckbox').click(
						function() {
							var checked = 'off';

							if (this.checked) {
								checked = 'on';
							}

							jQuery('#<portlet:namespace />rememberMe').val(checked);
						}
					);
				}
			);
			
			function pasarALetra(nro) {				
				return String.fromCharCode(nro+65);
			}

			<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
				Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />login);
			</c:if>
		</script>
	</c:otherwise>
</c:choose>