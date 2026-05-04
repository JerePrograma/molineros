<%
String id_lote=null;
try{
	id_lote=String.valueOf(request.getAttribute("id_lote"));
}catch(Exception e){
	e.printStackTrace();
}
%>
<script type="text/javascript">
	window.location.href ="/pdfservlet/?accion=credencialCES&id_lote=<%=id_lote%>" ;
</script>

