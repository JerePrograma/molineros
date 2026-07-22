package ar.com.ospim.procesaArchivos;

import java.io.BufferedReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.procesaArchivos.exception.ArchivoAdmifarmGeneralOspimIncorrectoException;
import ar.com.ospim.procesaArchivos.exception.ArchivoAdmifarmIncorrectoException;
import ar.com.ospim.procesaArchivos.services.ProcesaArchivosAdmifarmOspimGeneralServiceImpl;
import ar.com.ospim.procesaArchivos.services.ProcesaArchivosAdmifarmServiceImpl;
import ar.com.ospim.util.ConnectionHelper;

public final class ProcesaArchivosAdmifarm {

    private static Log _log = LogFactoryUtil.getLog(ProcesaArchivosAdmifarm.class);

    public void procesarArchivoAdmifarm(User usuario, BufferedReader scanner, java.util.Date fechaArchivo)
            throws Exception {

        ProcesaArchivosAdmifarmServiceImpl servicio = new ProcesaArchivosAdmifarmServiceImpl();
        Connection con = null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaArchivo);

        int mes = cal.get(Calendar.MONTH) + 1;
        int anio = cal.get(Calendar.YEAR);

        String nombreTabla = "admifarm_monotributo_" + String.format("%02d", mes) + anio;

        int columnasEsperadas = 35;

        try {
        	
            //valida primera linea antes de crear la tabla
            String primeraLinea = scanner.readLine();
            if (primeraLinea == null || primeraLinea.trim().isEmpty()) {
                throw new ArchivoAdmifarmIncorrectoException(4);
            }
            
            String[] cols = primeraLinea.split("\\|", -1);
            
            //valida que no sea header (ej: "Periodo|CodPlan|DescPlan...")
            if (!esLineaDeDatos(cols)) {
            	primeraLinea = scanner.readLine();
                if (primeraLinea == null) throw new ArchivoAdmifarmIncorrectoException(4);
                cols = primeraLinea.split("\\|", -1);
            }
            
            if (cols.length < columnasEsperadas) {
                throw new ArchivoAdmifarmIncorrectoException(4);
            }
            
            con = ConnectionHelper.getConnection();

            con.setAutoCommit(true);
            crearTablaAdmifarmPeriodo(con, nombreTabla);   //se crea tabla solo si archivo es valido
            con.setAutoCommit(false);

            //inserta cabecera
            int idCabecera = servicio.insertarCabeceraAdmifarm(
                    con,
                    usuario.getScreenName(),
                    new Date(fechaArchivo.getTime())
            );

            int totalRecords = 0;
            double totalPvp = 0d;
            double totalEntidad = 0d;
            double totalOspim = 0d;
            double totalUoma = 0d;
            double totalAmtima = 0d;
            
            //inserta primera linea
            servicio.insertarDetalleAdmifarm(con, nombreTabla, cols,
                    new Date(fechaArchivo.getTime()), idCabecera);

            totalRecords++;
            totalPvp += parse(cols, 22);
            totalEntidad += parse(cols, 24);
            totalOspim += parse(cols, 24);
            
            String line;

            while ((line = scanner.readLine()) != null) {

                if (line.trim().isEmpty())
                    continue;
                
                cols = line.split("\\|", -1);

                if (cols.length < columnasEsperadas) {
                    throw new ArchivoAdmifarmIncorrectoException(4);
                }

                servicio.insertarDetalleAdmifarm(con, nombreTabla, cols,
                        new Date(fechaArchivo.getTime()), idCabecera);

                totalRecords++;
                totalPvp += parse(cols, 22);
                totalEntidad += parse(cols, 24);
                totalOspim += parse(cols, 24);
            }
            
            servicio.correProcesosAdmifarmArchivos(
                    con,
                    nombreTabla,
                    new Date(fechaArchivo.getTime()).toString()
            );

            servicio.actualizarCabeceraAdmifarm(
                    con,
                    idCabecera,
                    totalRecords,
                    totalPvp,
                    totalEntidad,
                    totalOspim,
                    totalUoma,
                    totalAmtima
            );

            con.commit();

        } catch (Exception e) {

            _log.error("Error procesando archivo Admifarm", e);

            if (con != null) {
                ConnectionHelper.rollback(con);

                // borrar tabla, solo si la tabla existe (si fue creada por error)
                borrarTablaAdmifarmPeriodo(con, nombreTabla);
            }

            throw e;

        } finally {
            ConnectionHelper.cerrar(null, con);
        }
    }

    public void procesarArchivoAdmifarmOspimGeneral(User usuario, BufferedReader scanner, java.util.Date fechaArchivo)
            throws Exception {

    	ProcesaArchivosAdmifarmOspimGeneralServiceImpl servicio = new ProcesaArchivosAdmifarmOspimGeneralServiceImpl();
        Connection con = null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaArchivo);

        int mes = cal.get(Calendar.MONTH) + 1;
        int anio = cal.get(Calendar.YEAR);

        String nombreTabla = "admifarm_ospim_general_" + String.format("%02d", mes) + anio;

        int columnasEsperadas = 35;

        try {
        	
            //valida primera linea antes de crear la tabla
            String primeraLinea = scanner.readLine();
            if (primeraLinea == null || primeraLinea.trim().isEmpty()) {
                throw new ArchivoAdmifarmGeneralOspimIncorrectoException(4);
            }
            
            String[] cols = primeraLinea.split("\\|", -1);
            
            //valida que no sea header (ej: "Periodo|CodPlan|DescPlan...")
            if (!esLineaDeDatos(cols)) {
            	primeraLinea = scanner.readLine();
                if (primeraLinea == null) throw new ArchivoAdmifarmGeneralOspimIncorrectoException(4);
                cols = primeraLinea.split("\\|", -1);
            }
            
            if (cols.length < columnasEsperadas) {
                throw new ArchivoAdmifarmGeneralOspimIncorrectoException(4);
            }
            
            con = ConnectionHelper.getConnection();

            con.setAutoCommit(true);
            crearTablaAdmifarmOspimGeneralPeriodo(con, nombreTabla);   //se crea tabla solo si archivo es valido
            con.setAutoCommit(false);

            //inserta cabecera
            int idCabecera = servicio.insertarCabeceraAdmifarmOspimGeneral(
                    con,
                    usuario.getScreenName(),
                    new Date(fechaArchivo.getTime())
            );

            int totalRecords = 0;
            double totalPvp = 0d;
            double totalEntidad = 0d;
            double totalOspim = 0d; //impNeto
            double totalUoma = 0d;
            double totalAmtima = 0d;
            
            //inserta primera linea
            servicio.insertarDetalleAdmifarmOspimGeneral(con, nombreTabla, cols,
                    new Date(fechaArchivo.getTime()), idCabecera);

            totalRecords++;
            totalPvp += parse(cols, 22);
            totalEntidad += parse(cols, 24);
            totalOspim += parse(cols, 27);
            
            String line;

            while ((line = scanner.readLine()) != null) {

                if (line.trim().isEmpty())
                    continue;
                
                cols = line.split("\\|", -1);

                if (cols.length < columnasEsperadas) {
                    throw new ArchivoAdmifarmIncorrectoException(4);
                }

                servicio.insertarDetalleAdmifarmOspimGeneral(con, nombreTabla, cols,
                        new Date(fechaArchivo.getTime()), idCabecera);

                totalRecords++;
                totalPvp += parse(cols, 22);
                totalEntidad += parse(cols, 24);
                totalOspim += parse(cols, 27);
            }
            
            servicio.correProcesosAdmifarmOspimGeneralArchivos(
                    con,
                    nombreTabla,
                    new Date(fechaArchivo.getTime()).toString()
            );

            servicio.actualizarCabeceraAdmifarmOspimGeneral(
                    con,
                    idCabecera,
                    totalRecords,
                    totalPvp,
                    totalEntidad,
                    totalOspim,
                    totalUoma,
                    totalAmtima
            );

            con.commit();

        } catch (Exception e) {

            _log.error("Error procesando archivo Admifarm Ospim General", e);

            if (con != null) {
                ConnectionHelper.rollback(con);

                // borrar tabla, solo si la tabla existe (si fue creada por error)
                borrarTablaAdmifarmOspimGeneralPeriodo(con, nombreTabla);
            }

            throw e;

        } finally {
            ConnectionHelper.cerrar(null, con);
        }
    }

    private double parse(String[] cols, int ix) {
        try {
            return Double.parseDouble(cols[ix].replace(",", "."));
        } catch (Exception e) {
            return 0;
        }
    }
    
    private void crearTablaAdmifarmPeriodo(Connection con, String nombreTabla) throws SQLException {
        CallableStatement stmt = null;
        try {
            String sql = "{call crea_tabla_admifarm_monotributo(?)}";
            stmt = con.prepareCall(sql);
            stmt.setString(1, nombreTabla);
            stmt.executeUpdate();
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }
    
    private void crearTablaAdmifarmOspimGeneralPeriodo(Connection con, String nombreTabla) throws SQLException {
        CallableStatement stmt = null;
        try {
            String sql = "{call crea_tabla_admifarm_ospim_general(?)}";
            stmt = con.prepareCall(sql);
            stmt.setString(1, nombreTabla);
            stmt.executeUpdate();
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    //metodo para borrar tabla que se crea con datos erroneos
    private void borrarTablaAdmifarmPeriodo(Connection con, String nombreTabla) {
    	CallableStatement stmt = null;
        try {
            stmt = con.prepareCall("DROP TABLE IF EXISTS farmacia." + nombreTabla);
            stmt.executeUpdate();
        } catch (Exception ex) {
            _log.error("Error en excel importado, se borra tabla", ex);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }
    
    private void borrarTablaAdmifarmOspimGeneralPeriodo(Connection con, String nombreTabla) {
    	CallableStatement stmt = null;
        try {
            stmt = con.prepareCall("DROP TABLE IF EXISTS farmacia." + nombreTabla);
            stmt.executeUpdate();
        } catch (Exception ex) {
            _log.error("Error en excel importado, se borra tabla", ex);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }
    
    private boolean esLineaDeDatos(String[] cols) {
        //header: "CodPlan", "DescPlan", "CodAfiliado", etc
        return !cols[0].equalsIgnoreCase("Periodo")
            && !cols[1].equalsIgnoreCase("CodPlan")
            && !cols[3].equalsIgnoreCase("CodAfiliado");
    }


}
