<%
String cuil=null;
int inte=0;
try{
	cuil=String.valueOf(request.getAttribute("cuil"));
	inte=Integer.parseInt((String)request.getAttribute("inte"));
}catch(Exception e){
	e.printStackTrace();
}
%>
<script type="text/javascript">	
	window.location.href ="/odtservlet/?accion=certificadoAfiliacion&cuil="+<%=cuil%>+"&inte="+<%=inte%>+"&tipo='0'";
</script>

