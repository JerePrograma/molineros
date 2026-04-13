<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
		
	<script type="text/javascript">		
		if ('<%=renderRequest.getAttribute("mensajeCertificado")%>' != '') {
			alert('<%=renderRequest.getAttribute("mensajeCertificado")%>');
		}
	</script>