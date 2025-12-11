<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.util.PropsValues" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="com.liferay.portal.kernel.util.HttpUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.PortletResponse" %>
<%@ page import="com.liferay.portal.kernel.util.JavaConstants" %>

<%
	String namespace = StringPool.BLANK;
	PortletResponse portletResponse = (PortletResponse)request.getAttribute(JavaConstants.JAVAX_PORTLET_RESPONSE);
	if (portletResponse != null) {
		namespace = portletResponse.getNamespace();
	}
	SearchContainer searchContainer_ = (SearchContainer)request.getAttribute("liferay-ui:search:searchContainer");
	String type = "regular";
	int cur = searchContainer_.getCur();
	String curParam = searchContainer_.getCurParam();
	int delta = searchContainer_.getDelta();
	String deltaParam = searchContainer_.getDeltaParam();
	int total_ = searchContainer_.getTotal();
	double pages =  Math.ceil(total_ *1D / delta); 
	
	int start = (cur - 1) * delta;
	int end = cur * delta;
	
	if (end > total_) {
		end = total_;
	}
	
	int resultRowsSize = delta;
	
	if (total_ < delta) {
		resultRowsSize = total_;
	}
	else {
		resultRowsSize = total_ - ((cur - 1) * delta);
	
		if (resultRowsSize > delta) {
			resultRowsSize = delta;
		}
	}
	NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
%>

<c:if test='<%= type.equals("regular") %>'>
	<script type="text/javascript">
		function <%= namespace %>updateCur(box) {
			<%= namespace %>paginar(box.value);
		}

		function <%= namespace %><%= deltaParam %>updateDelta(box) {
			var delta = jQuery("option:selected", box).val();
				alert(delta);
		}
	</script>
</c:if>


<c:if test="<%= total_ > delta %>">
	<div class="search-pages">
		<c:if test='<%= type.equals("regular") %>'>
			<div class="page-selector">
				<liferay-ui:message key="page" />

						<select onchange="<%= namespace %>updateCur(this);">

							<%
							double pagesIteratorBegin = 1;
							double pagesIteratorEnd = pages;

							for (double i = pagesIteratorBegin; i <= pagesIteratorEnd; i++) {
								int j = Double.valueOf(i).intValue();
							%>

								<option <%= (j == cur) ? "selected=\"selected\"" : "" %> value="<%= j %>"><%= j %></option>

							<%
							}
							%>

						</select>

				<liferay-ui:message key="of" />

				<%=Double.valueOf(pages).intValue() %>
			</div>
		</c:if>

		<div class="page-links">
			<c:if test='<%= type.equals("regular") %>'>
				<c:choose>
					<c:when test="<%= cur != 1 %>">
						<a class="first" href="javascript:void(0)" onclick="<%= namespace %>paginar('1')">
					</c:when>
					<c:otherwise>
						<span class="first">
					</c:otherwise>
				</c:choose>

				<liferay-ui:message key="first" />

				<c:choose>
					<c:when test="<%= cur != 1 %>">
						</a>
					</c:when>
					<c:otherwise>
						</span>
					</c:otherwise>
				</c:choose>
			</c:if>

			<c:choose>
				<c:when test="<%= cur != 1 %>">
					<a class="previous" href="javascript:void(0)" onclick="<%= namespace %>paginar('<%=cur - 1%>')">
				</c:when>
				<c:when test='<%= type.equals("regular") %>'>
					<span class="previous">
				</c:when>
			</c:choose>

			<c:if test='<%= (type.equals("regular") || cur != 1) %>'>
				<liferay-ui:message key="previous" />
			</c:if>

			<c:choose>
				<c:when test="<%= cur != 1 %>">
					</a>
				</c:when>
				<c:when test='<%= type.equals("regular") %>'>
					</span>
				</c:when>
			</c:choose>

			<c:choose>
				<c:when test="<%= cur != pages %>">
					<a class="next" href="javascript:void(0)" onclick="<%= namespace %>paginar('<%=cur + 1%>')">
				</c:when>
				<c:when test='<%= type.equals("regular") %>'>
					<span class="next">
				</c:when>
			</c:choose>

			<c:if test='<%= (type.equals("regular") || cur != pages) %>'>
				<liferay-ui:message key="next" />
			</c:if>

			<c:choose>
				<c:when test="<%= cur != pages %>">
					</a>
				</c:when>
				<c:when test='<%= type.equals("regular") %>'>
					</span>
				</c:when>
			</c:choose>

			<c:if test='<%= type.equals("regular") %>'>
				<c:choose>
					<c:when test="<%= cur != pages %>">
						<a class="last" href="javascript:void(0)" onclick="<%= namespace %>paginar('<%=Math.round(pages)%>')">
					</c:when>
					<c:otherwise>
						<span class="last">
					</c:otherwise>
				</c:choose>

				<liferay-ui:message key="last" />

				<c:choose>
					<c:when test="<%= cur != pages %>">
						</a>
					</c:when>
					<c:otherwise>
						</span>
					</c:otherwise>
				</c:choose>
			</c:if>
		</div>
	</div>
</c:if>

