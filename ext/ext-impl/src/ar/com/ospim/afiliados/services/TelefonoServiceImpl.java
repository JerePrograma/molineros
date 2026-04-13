package ar.com.ospim.afiliados.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class TelefonoServiceImpl {

    private static Log _log = LogFactoryUtil.getLog(TelefonoServiceImpl.class);

    //obtener telefonos
    public List<Telefono> getTelefonos(String cuilTitular, int inte) throws SystemException {
        List<Telefono> lista = new ArrayList<Telefono>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = ConnectionHelper.getConnection();
            ps = con.prepareStatement("SELECT * FROM public.consultar_telefonos(?, ?)");
            ps.setString(1, cuilTitular);
            ps.setInt(2, inte);

            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(Telefono.getMapping(rs));
            }
        } catch (Exception e) {
            _log.error("Error buscando teléfonos", e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps);
            ConnectionHelper.cerrar(con);
        }
        return lista;
    }

    //insertar telefono
    public void insertaTelefono(Connection con, String cuilTitular, int inte, Telefono tel, String user) throws SystemException {
        CallableStatement stmt = null;
        try {
            stmt = con.prepareCall("{ ? = call inserta_afi_telefono(?,?,?,?,?,?,?,?,?,?) }");
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            stmt.setString(2, cuilTitular);
            stmt.setInt(3, inte);
            stmt.setString(4, tel.getTipo());
            stmt.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
            stmt.setString(6, tel.getCodigoPais());
            stmt.setString(7, tel.getCodigoArea());
            stmt.setString(8, tel.getNumero());
            stmt.setString(9, tel.getExtension());
            stmt.setString(10, tel.getObservaciones());
            stmt.setString(11, user);

            stmt.executeUpdate();

            int idGenerado = stmt.getInt(1);
            tel.setId(idGenerado);
        } catch (Exception e) {
            _log.error("Error insertando teléfono", e);
            throw new SystemException(e);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    //actualizar teléfono
    public void actualizaTelefono(Connection con, String cuilTitular, int inte, Telefono tel, String user) throws SystemException {
        CallableStatement stmt = null;
        try {
        	//si el numero está vacio,  dar de baja
            if (tel.getNumero() == null || tel.getNumero().trim().isEmpty()) {
                _log.info("Número vacío: se da de baja el teléfono id=" + tel.getId());
                bajaTelefono(tel.getId(), user);
                return;
            }
            
            //actualiza
            stmt = con.prepareCall("{ call actualiza_afi_telefono(?,?,?,?,?,?,?,?,?,?) }");
            stmt.setInt(1, tel.getId());
            stmt.setString(2, cuilTitular);
            stmt.setInt(3, inte);
            stmt.setString(4, tel.getTipo());
            stmt.setString(5, tel.getCodigoPais());
            stmt.setString(6, tel.getCodigoArea());
            stmt.setString(7, tel.getNumero());
            stmt.setString(8, tel.getExtension());
            stmt.setString(9, tel.getObservaciones());
            stmt.setString(10, user);
            stmt.executeUpdate();
        } catch (Exception e) {
            _log.error("Error actualizando teléfono", e);
            throw new SystemException(e);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    //baja de telefono
    public void bajaTelefono(int idTelefono, String user) throws SystemException {
        Connection con = null;
        CallableStatement stmt = null;
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall("{ call baja_afi_telefono(?,?) }");

            stmt.setInt(1, idTelefono);
            stmt.setString(2, user);

            stmt.executeUpdate();
        } catch (Exception e) {
            _log.error("Error dando de baja teléfono", e);
            throw new SystemException(e);
        } finally {
            ConnectionHelper.cerrar(stmt);
            ConnectionHelper.cerrar(con);
        }
    }

}