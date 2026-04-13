package ar.com.ospim.autorizaciones.action;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.util.ConnectionHelper;

import java.net.URL;
import java.sql.Connection;
import java.net.MalformedURLException;

public class UrlDatosAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		 String protocolo="";
		 String host ="";
		 int  port=0 ;
		 StringBuffer sb = req.getRequestURL();		  
		 String uriData  =  sb.toString();
		 try {  
	            URL myUrl = new URL(uriData);
	            protocolo= myUrl.getProtocol();
	            host = myUrl.getHost();
	            port =myUrl.getPort();	            
	        } catch (MalformedURLException ex) {
	            ex.printStackTrace();
	        }
		String protocoloHostPort =protocolo + "://" +host +":"+(port!=-1?String.valueOf(port):"")   ;		
        String resultado = "{}";
	    resultado = "{ \"protocoloHostPort\" : \"" 			    + protocoloHostPort     + "\",\"host\" : \"" 	        + host+ "\" }";
		return resultado;		
	}

}