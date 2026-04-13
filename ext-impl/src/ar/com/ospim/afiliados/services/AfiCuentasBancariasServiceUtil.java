package ar.com.ospim.afiliados.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ar.com.ospim.afiliados.beans.AfiCuentasBancarias;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;

public class AfiCuentasBancariasServiceUtil {

	public static List<AfiCuentasBancarias> getCuentas(String cuilTitular, int inte) throws SystemException {
	    List<AfiCuentasBancarias> lista = new ArrayList<AfiCuentasBancarias>();
	    Connection con = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	        con = ConnectionHelper.getConnection();
	        
	        ps = con.prepareStatement(
	        	    "SELECT * FROM afi_cuentas_bancarias WHERE cuil_titular = ? AND inte = ? ORDER BY alta_fecha DESC"
	        	);
	        ps.setString(1, cuilTitular);
	        ps.setInt(2, inte);

	        rs = ps.executeQuery();

	        while (rs.next()) {
	            lista.add(AfiCuentasBancarias.getMapping(rs, ""));
	        }
	    } catch (Exception e) {
	        throw new SystemException("Error buscando cuentas bancarias", e);
	    }
	    return lista;
	}
	
	public static AfiCuentasBancarias getCuentaPorId(int id) throws SystemException {
		AfiCuentasBancarias cuenta = null;
	    Connection con = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	        con = ConnectionHelper.getConnection();
	        ps = con.prepareStatement("SELECT * FROM afi_cuentas_bancarias WHERE id = ?");
	        ps.setInt(1, id);
	        rs = ps.executeQuery();

	        if (rs.next()) {
	            cuenta = AfiCuentasBancarias.getMapping(rs, "");
	        }
	    } catch (Exception e) {
	            throw new SystemException("Error buscando cuenta bancaria por ID", e);
	        }
	    return cuenta;
	}
	
    public static void insertCuenta(AfiCuentasBancarias cuenta) throws SystemException {
    	Connection con = null;
    	PreparedStatement ps = null;
    	try {
            con = ConnectionHelper.getConnection();
            ps = con.prepareStatement("INSERT INTO afi_cuentas_bancarias "
                    + "(cuil_titular, inte, apellido_afiliado, nombre_afiliado, email, titular, cbu, cuil_cbu, "
                    + "apellido_apoderado, nombre_apoderado, file_cbu, file_nota_autorizada, alta_usr) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            
            ps.setString(1, cuenta.getCuilTitular());
            ps.setInt(2, cuenta.getInte());
            ps.setString(3, cuenta.getApellidoAfiliado());
            ps.setString(4, cuenta.getNombreAfiliado());
            ps.setString(5, cuenta.getEmail());
            ps.setBoolean(6, cuenta.isTitular());
            ps.setString(7, cuenta.getCbu());
            ps.setString(8, cuenta.getCuilCbu());
            ps.setString(9, cuenta.getApellidoApoderado());
            ps.setString(10, cuenta.getNombreApoderado());
            ps.setString(11, cuenta.getFileCbu());
            ps.setString(12, cuenta.getFileNotaAutorizada());
            ps.setString(13, cuenta.getAltaUsr());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new SystemException("Error insertando cuenta bancaria", e);
        }
    }

    public static void updateCuenta(AfiCuentasBancarias cuenta) throws SystemException {
    	Connection con = null;
    	PreparedStatement ps = null;
    	try {
            con = ConnectionHelper.getConnection();
            ps = con.prepareStatement(
            	    "UPDATE afi_cuentas_bancarias SET email=?, titular=?, cbu=?, cuil_cbu=?, "
            	  + "apellido_apoderado=?, nombre_apoderado=?, "
            	  + "file_cbu=COALESCE(?, file_cbu), file_nota_autorizada=COALESCE(?, file_nota_autorizada), "
            	  + "modi_usr=?, modi_fecha=now() WHERE id=?");
            ps.setString(1, cuenta.getEmail());
            ps.setBoolean(2, cuenta.isTitular());
            ps.setString(3, cuenta.getCbu());
            ps.setString(4, cuenta.getCuilCbu());
            ps.setString(5, cuenta.getApellidoApoderado());
            ps.setString(6, cuenta.getNombreApoderado());
            ps.setString(7, cuenta.getFileCbu());
            ps.setString(8, cuenta.getFileNotaAutorizada());
            ps.setString(9, cuenta.getModiUsr());
            ps.setInt(10, cuenta.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new SystemException("Error actualizando cuenta bancaria", e);
        }
    }
    
    public static void eliminarArchivo(int id, String tipo) throws SystemException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = ConnectionHelper.getConnection();
            String campo = tipo.equals("fileNotaAutorizada") ? "file_nota_autorizada" : "file_cbu";
            ps = con.prepareStatement("UPDATE afi_cuentas_bancarias SET " + campo + " = NULL, modi_fecha=now() WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new SystemException("Error eliminando archivo de cuenta bancaria", e);
        }
    }

}
