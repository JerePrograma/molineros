package ar.com.ospim.procesaArchivos.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.util.ConnectionHelper;

public class ProcesaArchivosAdmifarmServiceImpl {

    private static Log logger = LogFactoryUtil.getLog(ProcesaArchivosAdmifarmServiceImpl.class);

    //inserta cabecera
    public int insertarCabeceraAdmifarm(Connection con, String usuario, Date fechaArchivo) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "INSERT INTO farmacia.farmacia_monotributo_cabecera (" +
                    "alta_usr, alta_fecha, periodo" +
                    ") VALUES (?, NOW(), ?) " +
                    "RETURNING id_cabecera";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, usuario);
            stmt.setDate(2, fechaArchivo);

            rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }
    
    //insertar detalle
    public void insertarDetalleAdmifarm(Connection con, String nombreTabla, String[] cols, Date fechaArchivo, int idCabecera) throws SQLException {
        PreparedStatement stmt = null;
        try {
        	String sql =
        		    "INSERT INTO farmacia." + nombreTabla + " (" +
        		    " hasta, cod_plan, desc_plan, dni_benef, nombre_benef, " +
        		    " fecha, dispensa, tipo_matricula, matricula, profesional, " +
        		    " registro, troquel, nombre_comercial, pot, accion, " +
        		    " principio, nro_lote, orden, receta, nro_item, " +
        		    " env, precio_unitario, pvp, porcentaje, entidad, " +
        		    " porc_bonif, imp_bonif, imp_neto, cod_farmacia, farmacia, " +
        		    " localidad, provincia, region, laboratorio, autorizacion, " +
        		    " id_cabecera, id_localidad_sss" +
        		    ") VALUES (" +
        		    " ?, ?, ?, ?, ?, " +
        		    " ?, ?, ?, ?, ?, " +
        		    " ?, ?, ?, ?, ?, " +
        		    " ?, ?, ?, ?, ?, " +
        		    " ?, ?, ?, ?, ?, " +
        		    " ?, ?, ?, ?, ?, " +
        		    " ?, ?, ?, ?, ?, ?, ? " +
        		    ")";

            stmt = con.prepareStatement(sql);

            int i = 1;

            stmt.setDate(i++, fechaArchivo);      // hasta
            stmt.setString(i++, get(cols, 1));    // cod_plan
            stmt.setString(i++, get(cols, 2));    // desc_plan
            stmt.setString(i++, get(cols, 3));    // dni_benef
            stmt.setString(i++, get(cols, 4));    // nombre_benef
            stmt.setString(i++, get(cols, 5));    // fecha prescripción
            stmt.setString(i++, get(cols, 6));    // dispensa/venta
            stmt.setString(i++, get(cols, 7));    // tipo_matricula
            stmt.setString(i++, get(cols, 8));    // matricula
            stmt.setString(i++, get(cols, 9));    // profesional
            stmt.setString(i++, get(cols, 10));   // registro
            stmt.setString(i++, get(cols, 11));   // troquel
            stmt.setString(i++, get(cols, 12));   // nombre_comercial
            stmt.setString(i++, get(cols, 13));   // pot
            stmt.setString(i++, get(cols, 14));   // accion
            stmt.setString(i++, get(cols, 15));   // principio
            stmt.setString(i++, get(cols, 16));   // nro_lote
            stmt.setString(i++, get(cols, 17));   // orden
            stmt.setString(i++, get(cols, 18));   // receta
            stmt.setString(i++, get(cols, 19));   // nro_item
            stmt.setDouble(i++, dbl(cols, 20));   // env (cantidad)
            stmt.setDouble(i++, dbl(cols, 21));   // precio_unitario (PU100)
            stmt.setDouble(i++, dbl(cols, 22));   // pvp (Total)
            stmt.setDouble(i++, dbl(cols, 23));   // porcentaje (PorcDesc)
            stmt.setDouble(i++, dbl(cols, 24));   // entidad (ACargoOS)
            stmt.setDouble(i++, dbl(cols, 25));   // porc_bonif
            stmt.setDouble(i++, dbl(cols, 26));   // imp_bonif
            stmt.setDouble(i++, dbl(cols, 27));   // imp_neto
            stmt.setString(i++, get(cols, 28));   // cod_farmacia
            stmt.setString(i++, get(cols, 29));   // farmacia
            stmt.setString(i++, get(cols, 30));   // localidad
            stmt.setString(i++, get(cols, 31));   // provincia
            stmt.setString(i++, get(cols, 32));   // region
            stmt.setString(i++, get(cols, 33));   // laboratorio
            stmt.setString(i++, get(cols, 34));   // autorizacion
            stmt.setInt(i++, idCabecera);         // id_cabecera    
            stmt.setNull(i++, java.sql.Types.INTEGER); //id_localidad_sss
            stmt.executeUpdate();
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    //actualizar cabecera
    public void actualizarCabeceraAdmifarm(Connection con, int idCabecera, int totalRecords, double totalPvp, double totalEntidad, double totalOspim, double totalUoma, double totalAmtima) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "UPDATE farmacia.farmacia_monotributo_cabecera SET " +
                    "totalrecords = ?, totalpvp = ?, totalentidad = ?, totalospim = ?, " +
                    "totaluoma = ?, totalamtima = ? " +
                    "WHERE id_cabecera = ?";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, totalRecords);
            stmt.setDouble(2, totalPvp);
            stmt.setDouble(3, totalEntidad);
            stmt.setDouble(4, totalOspim);
            stmt.setDouble(5, totalUoma);
            stmt.setDouble(6, totalAmtima);
            stmt.setInt(7, idCabecera);
            stmt.executeUpdate();
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    //obtiene texto desde una columna del archivo
    private String get(String[] cols, int index) {
        if (cols == null || index >= cols.length) return null;
        return cols[index] != null ? cols[index].trim() : null;
    }

    //lee un numero desde la columna, devolviendo 0 si hay error
    private double dbl(String[] cols, int index) {
        try {
            if (cols == null || index >= cols.length) return 0d;
            String v = cols[index].trim().replace(",", ".");
            if (v.isEmpty()) return 0d;
            return Double.parseDouble(v);
        } catch (Exception e) {
            return 0d;
        }
    }
    
    public void correProcesosAdmifarmArchivos(Connection con, String nombreTabla, String fechaPeriodoArchivo) 
    		throws SQLException { 
    	CallableStatement stmt = null; 
    	try { 
    		String sql = "{call proceso_archivo_admifarm_updates(?,?)}"; 
    		stmt = con.prepareCall(sql); 
    		stmt.setString(1, nombreTabla); 
    		stmt.setString(2, fechaPeriodoArchivo); 
    		stmt.executeUpdate(); 
    	} catch (SQLException e) { 
    		logger.error("ERROR EN EL PROCESO DE LA TABLA DE ADMIFARM", e); 
    		throw e; 
    	} finally { 
    		ConnectionHelper.cerrar(stmt); 
    	} 
    }
}