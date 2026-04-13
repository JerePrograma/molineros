<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
¿Desea Generarlo Como ODT ó RTF?  
</br>
<input type="button" value="ODT" onClick="<portlet:namespace />odtRtf('<%=ParamUtil.getString(request, "cuil")%>','<%=ParamUtil.getString(request, "inte")%>','0');" />
<input type="button" value="RTF" onClick="<portlet:namespace />odtRtf('<%=ParamUtil.getString(request, "cuil")%>','<%=ParamUtil.getString(request, "inte")%>','1');" />