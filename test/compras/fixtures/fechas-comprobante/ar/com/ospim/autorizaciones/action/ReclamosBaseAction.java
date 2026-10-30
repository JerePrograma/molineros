package ar.com.ospim.autorizaciones.action;
import javax.portlet.PortletRequest;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
public class ReclamosBaseAction {
 public static boolean compras=true;
 public static ReclamoPrestacionalCompraContexto contexto;
 public static ReclamoPrestacionalCompraContexto validarContextoEditorCompras(PortletRequest request) { return compras ? contexto : null; }
 public static double getImportePrestacionFromRequest(PortletRequest request,String key) {
  String value=request.getParameter(key); return value==null||value.length()==0 ? 0 : Double.parseDouble(value.replace(',','.'));
 }
}
