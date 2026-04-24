package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.afiliados.beans.AfiCuentasBancarias;
import ar.com.ospim.util.ConnectionHelper;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

import org.apache.log4j.Logger;

public class AfiCuentasBancariasServiceImpl {

    private static final Logger _log = Logger.getLogger(AfiCuentasBancariasServiceImpl.class);

    // inserta nueva cuenta
    public void insertaCuenta(Connection con, AfiCuentasBancarias cuenta) throws SystemException {
        CallableStatement stmt = null;
        try {
            stmt = con.prepareCall("{ call inserta_afi_cuenta(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");

            stmt.setString(1, cuenta.getCuilTitular());
            stmt.setInt(2, cuenta.getInte());
            stmt.setString(3, cuenta.getApellido());
            stmt.setString(4, cuenta.getNombre());
            stmt.setString(5, cuenta.getEmail());
            stmt.setBoolean(6, cuenta.isTitular());
            stmt.setString(7, cuenta.getCbu());

            // 8) cuil_cbu (null si es titular)
            if (cuenta.getCuilCbu() == null || cuenta.getCuilCbu().trim().isEmpty()) {
                stmt.setNull(8, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(8, cuenta.getCuilCbu());
            }

            // 9) file_cbu
            if (cuenta.getFileCbu() == null || cuenta.getFileCbu().trim().isEmpty()) {
                stmt.setNull(9, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(9, cuenta.getFileCbu());
            }

            // 10) file_nota_autorizada (null si titular)
            if (cuenta.getFileNotaAutorizada() == null || cuenta.getFileNotaAutorizada().trim().isEmpty()) {
                stmt.setNull(10, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(10, cuenta.getFileNotaAutorizada());
            }

            // 11) alta_usr
            stmt.setString(11, cuenta.getAltaUsr());

            stmt.executeUpdate();

        } catch (Exception e) {
            _log.error("Error insertando cuenta bancaria", e);
            throw new SystemException("Error insertando cuenta bancaria", e);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }


    //da de baja una cuenta
    public void actualizaCuenta(Connection con, int idCuenta, String user) throws SystemException {
        CallableStatement stmt = null;
        try {
            stmt = con.prepareCall("{ call actualiza_afi_cuenta(?, ?, ?) }");
            stmt.setInt(1, idCuenta);
            stmt.setString(2, user);
            stmt.setString(3, user);
            stmt.executeUpdate();
        } catch (Exception e) {
            _log.error("Error al dar de baja la cuenta bancaria ID=" + idCuenta, e);
            throw new SystemException("Error al dar de baja la cuenta bancaria", e);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    //elimina archivo
    public void eliminaArchivo(Connection con, int id, String tipo) throws SystemException {
        CallableStatement stmt = null;
        try {
            stmt = con.prepareCall("{ call elimina_archivo_cuenta(?, ?) }");
            stmt.setInt(1, id);
            stmt.setString(2, tipo);
            stmt.executeUpdate();
        } catch (Exception e) {
            _log.error("Error eliminando archivo de cuenta bancaria ID=" + id, e);
            throw new SystemException("Error eliminando archivo", e);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    //busca por ID
    public AfiCuentasBancarias getCuentaPorId(Connection con, int id) throws SystemException {
        AfiCuentasBancarias cuenta = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM busca_afi_cuenta_por_id(" + id + ")");
            if (rs.next()) {
                cuenta = AfiCuentasBancarias.getMapping(rs, "");
            }
        } catch (Exception e) {
            _log.error("Error buscando cuenta por ID", e);
            throw new SystemException("Error buscando cuenta por ID", e);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
        return cuenta;
    }

    //busca todas las cuentas de un afiliado
    public List<AfiCuentasBancarias> getCuentas(Connection con, String cuilTitular, int inte) throws SystemException {
        List<AfiCuentasBancarias> lista = new ArrayList<AfiCuentasBancarias>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(
                "SELECT * FROM busca_afi_cuentas_por_afiliado('" + cuilTitular + "', " + inte + ")"
            );
            while (rs.next()) {
                lista.add(AfiCuentasBancarias.getMapping(rs, ""));
            }
        } catch (Exception e) {
            _log.error("Error buscando cuentas bancarias", e);
            throw new SystemException("Error buscando cuentas bancarias", e);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
        return lista;
    }
    
    public int insertaOActualizaCuentaBancaria(
            String cuilTitular,
            int inte,
            String apellido,
            String nombre,
            String email,
            boolean titular,
            String cbu,
            String cuilCbu,
            String fileCbu,
            String fileNotaAutorizada,
            User user
    ) throws SystemException {
        Connection con = null;
        CallableStatement stmt = null;
        int resultado = 0;

        try {
            con = ConnectionHelper.getConnection();
            String sql = "{? = call public.inserta_o_actualiza_afi_cuenta_bancaria(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            stmt = con.prepareCall(sql);
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, cuilTitular);
            stmt.setInt(3, inte);
            stmt.setString(4, apellido);
            stmt.setString(5, nombre);
            stmt.setString(6, email);
            stmt.setBoolean(7, titular);
            stmt.setString(8, cbu);
            stmt.setString(9, cuilCbu);
            stmt.setString(12, fileCbu);
            stmt.setString(13, fileNotaAutorizada);
            stmt.setString(14, user.getScreenName());

            stmt.execute();
            resultado = stmt.getInt(1);

        } catch (Exception e) {
            _log.error("Error en insertaOActualizaCuentaBancaria", e);
            throw new SystemException(e);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }

        return resultado;
    }

}
