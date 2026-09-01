package ar.com.ospim.prestadores.services;

import java.io.File;
import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ImportarCartillaSOPServiceImpl {

    private static final Log log = LogFactoryUtil.getLog(ImportarCartillaSOPServiceImpl.class);

    private static final String[] COLUMNAS_ESPERADAS = {
        "nombre",
        "direccion",
        "telefono",
        "localidad",
        "provincia"
    };

    public int importarCartillaSOP(File archivo)
        throws Exception {

        if (archivo == null || !archivo.exists() ||archivo.length() == 0) {
            throw new IllegalArgumentException(
                "El archivo está vacío o no existe."
            );
        }

        List<RegistroCartillaSOP> registros = leerYValidarExcel(archivo);

        if (registros.isEmpty()) {
            throw new IllegalArgumentException(
                "El archivo no contiene registros."
            );
        }

        Connection con = null;

        try {
            con = ConnectionHelper.getConnection();
            con.setAutoCommit(false);

            resolverProvinciasYLocalidades(con, registros);

            int idImportacion = insertarImportacion(con, registros.size());

            limpiarCartilla(con);

            insertarRegistros(con, idImportacion, registros);

            con.commit();

            return registros.size();

        } catch (Exception e) {

            if (con != null) {
                try {
                    con.rollback();

                } catch (SQLException rollbackException) {
                    log.error("Error haciendo rollback de Cartilla SOP", rollbackException);
                }
            }

            log.error("Error importando Cartilla SOP",e);

            throw e;

        } finally {

            if (con != null) {
                try {
                    con.setAutoCommit(true);

                } catch (SQLException e) {
                    log.warn("No se pudo restaurar el autocommit", e);
                }
            }

            ConnectionHelper.cerrar(con);
        }
    }

    public List<Object[]> getImportacionesCartillaSOP()
        throws Exception {

        List<Object[]> importaciones =
            new ArrayList<Object[]>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();

            String sql =
                "SELECT id_importacion, fecha_importacion, cantidad_registros " +
                "FROM public.cartilla_odontologia_sop_cabecera " +
                "ORDER BY fecha_importacion DESC, id_importacion DESC";

            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[3];

                fila[0] = Integer.valueOf(rs.getInt("id_importacion"));

                fila[1] = rs.getTimestamp("fecha_importacion");

                fila[2] = Integer.valueOf(rs.getInt("cantidad_registros"));

                importaciones.add(fila);
            }

            return importaciones;

        } catch (SQLException e) {
            log.error("Error consultando importaciones de Cartilla SOP", e);

            throw e;

        } finally {
            cerrarResultSet(rs);
            ConnectionHelper.cerrar(stmt);
            ConnectionHelper.cerrar(con);
        }
    }

    private List<RegistroCartillaSOP> leerYValidarExcel(File archivo) throws Exception {

        List<RegistroCartillaSOP> registros = new ArrayList<RegistroCartillaSOP>();

        FileInputStream inputStream = null;
        XSSFWorkbook workbook = null;

        try {
            inputStream = new FileInputStream(archivo);

            workbook = new XSSFWorkbook(inputStream);

            DataFormatter formatter = new DataFormatter(new Locale("es", "AR"));

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Sheet sheet = buscarPrimeraSolapaConDatos(workbook, formatter, evaluator);

            if (sheet == null) {
                throw new IllegalArgumentException(
                    "El archivo no contiene ninguna solapa con datos."
                );
            }

            validarEncabezado(sheet, formatter, evaluator, sheet.getSheetName());

            leerRegistrosSolapa(sheet, formatter, evaluator, registros);

            return registros;

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            log.error("No se pudo leer el archivo Excel",e);

            throw new IllegalArgumentException("El archivo no es un Excel XLSX válido.", e);

        } finally {

            if (workbook != null) {
                try {
                    workbook.close();

                } catch (Exception e) {
                    log.warn("No se pudo cerrar el Excel",e);
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();

                } catch (Exception e) {
                    log.warn("No se pudo cerrar el archivo",e);
                }
            }
        }
    }

    private Sheet buscarPrimeraSolapaConDatos(XSSFWorkbook workbook, DataFormatter formatter, FormulaEvaluator evaluator) {

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);

            Row primeraFila = sheet.getRow(0);

            if (primeraFila == null) {
                continue;
            }

            for (int columna = 0; columna < COLUMNAS_ESPERADAS.length; columna++) {
                String valor = obtenerValorCelda(primeraFila.getCell(columna), formatter, evaluator);

                if (!estaVacio(valor)) {
                    return sheet;
                }
            }
        }

        return null;
    }

    private void validarEncabezado(
            Sheet sheet,
            DataFormatter formatter,
            FormulaEvaluator evaluator,
            String nombreSolapa) {

        Row encabezado = sheet.getRow(0);

        if (encabezado == null) {
            throw new IllegalArgumentException(
                "El archivo no tiene encabezado."
            );
        }

        for (int columna = 0; columna < COLUMNAS_ESPERADAS.length; columna++) {
            String valor =
                obtenerValorCelda(
                    encabezado.getCell(columna),
                    formatter,
                    evaluator
                );

            valor = normalizarTexto(valor);

            if (!COLUMNAS_ESPERADAS[columna].equals(valor)) {

                throw new IllegalArgumentException(
                    "Formato incorrecto en la solapa " +
                    nombreSolapa +
                    ". La columna " +
                    (columna + 1) +
                    " debe llamarse " +
                    COLUMNAS_ESPERADAS[columna] +
                    "."
                );
            }
        }

        String sextaColumna =
            obtenerValorCelda(
                encabezado.getCell(5),
                formatter,
                evaluator
            );

        if (!estaVacio(sextaColumna)) {
            throw new IllegalArgumentException(
                "El archivo solamente debe contener las columnas " +
                "nombre, direccion, telefono, localidad y provincia."
            );
        }
    }

    private void leerRegistrosSolapa(
            Sheet sheet,
            DataFormatter formatter,
            FormulaEvaluator evaluator,
            List<RegistroCartillaSOP> registros) {

        for (int numeroFila = 1; numeroFila <= sheet.getLastRowNum(); numeroFila++) {
            Row row = sheet.getRow(numeroFila);

            if (row == null) {
                continue;
            }

            String nombre =
                obtenerValorCelda(
                    row.getCell(0),
                    formatter,
                    evaluator
                );

            String direccion =
                obtenerValorCelda(
                    row.getCell(1),
                    formatter,
                    evaluator
                );

            String telefono =
                obtenerValorCelda(
                    row.getCell(2),
                    formatter,
                    evaluator
                );

            String localidad =
                obtenerValorCelda(
                    row.getCell(3),
                    formatter,
                    evaluator
                );

            String provincia =
                obtenerValorCelda(
                    row.getCell(4),
                    formatter,
                    evaluator
                );

            if (
                estaVacio(nombre) &&
                estaVacio(direccion) &&
                estaVacio(telefono) &&
                estaVacio(localidad) &&
                estaVacio(provincia)
            ) {
                continue;
            }

            RegistroCartillaSOP registro =
                new RegistroCartillaSOP();

            registro.nombre =
                valorNoNull(nombre);

            registro.direccion =
                limpiarNullable(direccion);

            registro.telefono =
                limpiarNullable(telefono);

            registro.localidad =
                valorNoNull(localidad);

            registro.provincia =
                valorNoNull(provincia);

            registro.numeroFila =
                numeroFila + 1;

            registros.add(registro);
        }
    }

    private String obtenerValorCelda(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {

        if (cell == null) {
            return "";
        }

        String valor =
            formatter.formatCellValue(
                cell,
                evaluator
            );

        return valor == null
            ? ""
            : valor.trim();
    }

    private void resolverProvinciasYLocalidades(Connection con, List<RegistroCartillaSOP> registros) throws SQLException {

        for (RegistroCartillaSOP registro : registros) {

            registro.idProvincia =
                buscarIdProvincia(
                    con,
                    registro.provincia
                );

            if (registro.idProvincia == null) {
                throw new IllegalArgumentException(
                    "No se encontró la provincia '" +
                    registro.provincia +
                    "' en la fila " +
                    registro.numeroFila +
                    "."
                );
            }

            registro.idLocalidad =
                buscarIdLocalidad(
                    con,
                    registro.localidad,
                    registro.idProvincia
                );

            if (registro.idLocalidad == null) {
                log.warn(
                    "No se encontró la localidad '" +
                    registro.localidad +
                    "' para la provincia '" +
                    registro.provincia +
                    "' en la fila " +
                    registro.numeroFila +
                    ". Se insertará con id_localidad NULL."
                );
            }
        }
    }

    private Integer buscarIdProvincia(Connection con, String provincia) throws SQLException {

        CallableStatement stmt = null;

        try {
            String sql = "{? = call public.buscar_id_provincia_cartilla_sop(?)}";

            stmt = con.prepareCall(sql);

            stmt.registerOutParameter(
                1,
                Types.INTEGER
            );

            stmt.setString(
                2,
                provincia
            );

            stmt.execute();

            int idProvincia =
                stmt.getInt(1);

            return stmt.wasNull()
                ? null
                : Integer.valueOf(idProvincia);

        } catch (SQLException e) {
            log.error(
                "Error buscando la provincia '" +
                provincia +
                "'",
                e
            );

            throw e;

        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    private Integer buscarIdLocalidad(
            Connection con,
            String localidad,
            Integer idProvincia)
        throws SQLException {

        CallableStatement stmt = null;

        try {
            String sql = "{? = call public.buscar_id_localidad_cartilla_sop(?, ?)}";

            stmt = con.prepareCall(sql);

            stmt.registerOutParameter(
                1,
                Types.INTEGER
            );

            stmt.setString(
                2,
                localidad
            );

            stmt.setInt(
                3,
                idProvincia.intValue()
            );

            stmt.execute();

            int idLocalidad =
                stmt.getInt(1);

            return stmt.wasNull()
                ? null
                : Integer.valueOf(idLocalidad);

        } catch (SQLException e) {
            log.error(
                "Error buscando la localidad '" +
                localidad +
                "'",
                e
            );

            throw e;

        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    private int insertarImportacion(
            Connection con,
            int cantidadRegistros)
        throws SQLException {

        CallableStatement stmt = null;

        try {
            String sql = "{? = call public.insertar_cartilla_sop_importacion(?)}";

            stmt = con.prepareCall(sql);

            stmt.registerOutParameter(
                1,
                Types.INTEGER
            );

            stmt.setInt(
                2,
                cantidadRegistros
            );

            stmt.execute();

            return stmt.getInt(1);

        } catch (SQLException e) {
            log.error(
                "Error creando la importación de Cartilla SOP",
                e
            );

            throw e;

        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    private void limpiarCartilla(
            Connection con)
        throws SQLException {

        CallableStatement stmt = null;

        try {
            String sql = "{call public.limpiar_cartilla_odontologia_sop()}";

            stmt = con.prepareCall(sql);
            stmt.execute();

        } catch (SQLException e) {
            log.error(
                "Error limpiando la Cartilla SOP",
                e
            );

            throw e;

        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    private void insertarRegistros(
            Connection con,
            int idImportacion,
            List<RegistroCartillaSOP> registros)
        throws SQLException {

        CallableStatement stmt = null;

        try {
            String sql =
                "{call public.insertar_cartilla_odontologia_sop(" +
                "?, ?, ?, ?, ?, ?, ?, ?)}";

            stmt = con.prepareCall(sql);

            for (RegistroCartillaSOP registro : registros) {

                stmt.clearParameters();

                stmt.setInt(
                    1,
                    idImportacion
                );

                stmt.setString(
                    2,
                    registro.nombre
                );

                setStringNullable(
                    stmt,
                    3,
                    registro.direccion
                );

                setStringNullable(
                    stmt,
                    4,
                    registro.telefono
                );

                stmt.setString(
                    5,
                    registro.localidad
                );

                stmt.setString(
                    6,
                    registro.provincia
                );

                if (registro.idLocalidad == null) {
                    stmt.setNull(
                        7,
                        Types.INTEGER
                    );
                } else {
                    stmt.setInt(
                        7,
                        registro.idLocalidad.intValue()
                    );
                }

                stmt.setInt(
                    8,
                    registro.idProvincia.intValue()
                );

                stmt.execute();
            }

        } catch (SQLException e) {
            log.error(
                "Error insertando registros de Cartilla SOP",
                e
            );

            throw e;

        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    private void setStringNullable(
            CallableStatement stmt,
            int posicion,
            String valor)
        throws SQLException {

        if (estaVacio(valor)) {
            stmt.setNull(
                posicion,
                Types.VARCHAR
            );
        } else {
            stmt.setString(
                posicion,
                valor.trim()
            );
        }
    }

    private String valorNoNull(
            String valor) {

        return valor == null
            ? ""
            : valor.trim();
    }

    private String limpiarNullable(
            String valor) {

        return estaVacio(valor)
            ? null
            : valor.trim();
    }

    private boolean estaVacio(
            String valor) {

        return valor == null ||
            valor.trim().length() == 0;
    }

    private String normalizarTexto(
            String valor) {

        if (valor == null) {
            return "";
        }

        String texto =
            Normalizer.normalize(
                valor.trim(),
                Normalizer.Form.NFD
            );

        texto =
            texto.replaceAll(
                "\\p{InCombiningDiacriticalMarks}+",
                ""
            );

        texto =
            texto.replaceAll(
                "\\s+",
                " "
            );

        return texto.toLowerCase();
    }

    private void cerrarResultSet(
            ResultSet rs) {

        if (rs == null) {
            return;
        }

        try {
            rs.close();

        } catch (SQLException e) {
            log.warn(
                "No se pudo cerrar el ResultSet",
                e
            );
        }
    }

    private static class RegistroCartillaSOP {

        private String nombre;
        private String direccion;
        private String telefono;
        private String localidad;
        private String provincia;

        private Integer idLocalidad;
        private Integer idProvincia;
        private int numeroFila;
    }
}
