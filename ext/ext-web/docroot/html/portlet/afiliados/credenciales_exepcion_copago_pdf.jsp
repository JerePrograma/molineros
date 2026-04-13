<%
String cuil=null;
String inte=null;
try{
	cuil=String.valueOf(request.getAttribute("cuil_aux"));
	inte=String.valueOf(request.getAttribute("inte_aux"));
}catch(Exception e){
	e.printStackTrace();
}
%>
<script type="text/javascript">
	window.location.href ="/pdfservlet/?accion=credencialExentoCoPago&cuil=<%=cuil%>&inte=<%=inte%>" ;
</script>
