package ar.com.ospim.farmaciaOspim.helper;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.portlet.PortletSession;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ar.com.ospim.farmaciaOspim.WebKeysFarmaciaOspim;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Registro;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Comparacion;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.portal.model.User;

public class VademecumAdmifarmHelper {
    private static final String TOKEN = "VADEMECUM_ADMIFARM_TOKEN_";
    public static final long MAX_ARCHIVO = 20L * 1024L * 1024L;

    public static boolean tienePermiso(User user) throws Exception {
        return user != null && !user.isDefaultUser()
            && PermissionUtil.userContainsRole(user, WebKeysFarmaciaOspim.ROL_VADEMECUM_FARMACIA_OSPIM);
    }

    public static void validarPermiso(User user) throws Exception {
        if (!tienePermiso(user)) {
            throw new SecurityException("No posee permisos para administrar Vademecum.");
        }
    }

    public static synchronized String obtenerToken(PortletSession session, long usuario, String tipo) {
        ImportacionVademecumAdmifarm.getColumnas(tipo);
        String clave = TOKEN + tipo + "_" + usuario;
        String token = (String) session.getAttribute(clave);
        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute(clave, token);
        }
        return token;
    }

    public static synchronized void consumirToken(PortletSession session, long usuario,
            String tipo, String token) {
        ImportacionVademecumAdmifarm.getColumnas(tipo);
        String clave = TOKEN + tipo + "_" + usuario;
        String esperado = (String) session.getAttribute(clave);
        if (esperado == null || !esperado.equals(token)) {
            throw new IllegalArgumentException("El formulario vencio o ya fue enviado. Vuelva a cargar la pantalla.");
        }
        session.removeAttribute(clave);
    }

    // Adaptado de ImportarCartillaSOPServiceImpl.leerYValidarExcel y de los modelos Admifarm.
    public static List<Registro> leerArchivo(File archivo, String nombre, String tipo) throws Exception {
        String[] columnas = ImportacionVademecumAdmifarm.getColumnas(tipo);
        if (archivo == null || !archivo.isFile() || archivo.length() == 0) {
            throw new IllegalArgumentException("Debe seleccionar un archivo Excel con registros.");
        }
        if (archivo.length() > MAX_ARCHIVO) {
            throw new IllegalArgumentException("El archivo supera el limite de 20 MB.");
        }
        if (nombre == null || (!nombre.toLowerCase(Locale.ENGLISH).endsWith(".xls")
                && !nombre.toLowerCase(Locale.ENGLISH).endsWith(".xlsx"))) {
            throw new IllegalArgumentException("El archivo debe tener formato .xls o .xlsx.");
        }
        FileInputStream input = new FileInputStream(archivo);
        Workbook libro = null;
        try {
            try {
                libro = nombre.toLowerCase(Locale.ENGLISH).endsWith(".xlsx")
                    ? new XSSFWorkbook(input) : new HSSFWorkbook(input);
            } catch (Exception e) {
                throw new IllegalArgumentException("No se pudo leer el archivo en el formato Excel indicado por su extension.", e);
            }
            if (libro.getNumberOfSheets() == 0) {
                throw new IllegalArgumentException("El archivo no contiene hojas.");
            }
            Sheet hoja = libro.getSheetAt(0);
            DataFormatter formato = new DataFormatter();
            Row encabezado = hoja.getRow(0);
            if (encabezado == null) {
                throw new IllegalArgumentException("La primera fila del archivo esta vacia.");
            }
            Cell primeraCelda = encabezado.getCell(0);
            String primerValor = valorCelda(primeraCelda, formato, 1);
            boolean sinEncabezado = "ampliado".equals(tipo)
                && (primerValor.length() == 0 || primeraCelda.getCellType() == CellType.NUMERIC
                    || primerValor.matches("[+-]?[0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?"));
            // Modelo Ampliado: registro, nombre, monodroga, presentacion, accion, laboratorio, tipo_venta.
            // Se remapea al orden de la tabla sin descartar la primera fila del archivo.
            int[] posiciones = sinEncabezado ? new int[] { 0, 1, 3, 4, 2, 5, 6 }
                : posicionesColumnas(encabezado, columnas, formato);
            List<Registro> registros = new ArrayList<Registro>();
            StringBuffer erroresRegistro = new StringBuffer();
            int cantidadErrores = 0;
            for (int f = sinEncabezado ? 0 : 1; f <= hoja.getLastRowNum(); f++) {
                Row fila = hoja.getRow(f);
                if (fila == null) { continue; }
                boolean vacia = true;
                for (int c = 0; c < fila.getLastCellNum(); c++) {
                    String valor = valorCelda(fila.getCell(c), formato, f + 1);
                    if (valor.length() > 0) {
                        vacia = false;
                        boolean incluida = false;
                        for (int posicion : posiciones) { if (posicion == c) { incluida = true; } }
                        if (!incluida) {
                            throw new IllegalArgumentException("La fila " + (f + 1) + " tiene datos en una columna sin destino. No se omitieron datos.");
                        }
                    }
                }
                if (vacia) { continue; }
                Cell celdaRegistro = fila.getCell(posiciones[0]);
                BigDecimal registro;
                try {
                    if (celdaRegistro != null && celdaRegistro.getCellType() == CellType.NUMERIC) {
                        registro = BigDecimal.valueOf(celdaRegistro.getNumericCellValue());
                        if (registro.precision() - registro.scale() > 15) {
                            throw new NumberFormatException("Precision de Excel");
                        }
                    } else {
                        registro = new BigDecimal(valorCelda(celdaRegistro, formato, f + 1));
                    }
                } catch (NumberFormatException e) {
                    cantidadErrores++;
                    if (cantidadErrores <= 10) {
                        if (erroresRegistro.length() > 0) { erroresRegistro.append(", "); }
                        erroresRegistro.append(f + 1);
                    }
                    continue;
                }
                String[] valores = new String[columnas.length - 1];
                for (int c = 1; c < columnas.length; c++) {
                    valores[c - 1] = valorCelda(fila.getCell(posiciones[c]), formato, f + 1);
                }
                registros.add(new Registro(registro, valores));
            }
            if (cantidadErrores > 0) {
                throw new IllegalArgumentException("Hay " + cantidadErrores + " filas sin registro numerico valido. Filas: "
                    + erroresRegistro.toString() + ". No se importo ninguna fila.");
            }
            if (registros.size() > 65535) {
                throw new IllegalArgumentException("El archivo supera el limite de 65535 registros.");
            }
            if (registros.isEmpty()) {
                throw new IllegalArgumentException("El archivo no contiene registros. No se modifico el Vademecum.");
            }
            indexar(registros);
            return registros;
        } finally {
            try { if (libro != null) { libro.close(); } }
            finally { input.close(); }
        }
    }

    private static int[] posicionesColumnas(Row encabezado, String[] columnas, DataFormatter formato) {
        if (encabezado.getLastCellNum() > 32) {
            throw new IllegalArgumentException("El archivo contiene demasiadas columnas.");
        }
        int[] posiciones = new int[columnas.length];
        for (int i = 0; i < posiciones.length; i++) { posiciones[i] = -1; }
        for (int c = 0; c < encabezado.getLastCellNum(); c++) {
            String nombre = Normalizer.normalize(valorCelda(encabezado.getCell(c), formato, 1), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ENGLISH).replaceAll("\\s+", "_");
            if ("codigo".equals(nombre)) { nombre = "registro"; }
            if ("producto".equals(nombre)) { nombre = "nombre"; }
            if ("accion_farmacologica".equals(nombre)) { nombre = "accion"; }
            boolean encontrada = false;
            for (int i = 0; i < columnas.length; i++) {
                if (columnas[i].equals(nombre)) {
                    if (posiciones[i] >= 0) { throw new IllegalArgumentException("El encabezado repite la columna " + nombre + "."); }
                    posiciones[i] = c;
                    encontrada = true;
                }
            }
            // El modelo PMO incluye esta columna sin datos; si tiene valores se rechaza en la lectura.
            if (!encontrada && nombre.length() > 0 && !"observaciones_ensalud".equals(nombre)) {
                throw new IllegalArgumentException("Encabezado no reconocido: " + nombre + ".");
            }
        }
        for (int i = 0; i < columnas.length; i++) {
            if (posiciones[i] < 0) { throw new IllegalArgumentException("Falta la columna " + columnas[i] + " en el encabezado."); }
        }
        return posiciones;
    }

    private static String valorCelda(Cell celda, DataFormatter formato, int fila) {
        if (celda == null) { return ""; }
        if (celda.getCellType() == CellType.FORMULA
                || celda.getCellType() == CellType.ERROR
                || celda.getCellType() == CellType.BOOLEAN) {
            throw new IllegalArgumentException("La fila " + fila + " contiene una formula, error o valor logico. Importe valores.");
        }
        return formato.formatCellValue(celda).trim();
    }

    public static Map<BigDecimal, Registro> indexar(List<Registro> registros) {
        Map<BigDecimal, Registro> resultado = new LinkedHashMap<BigDecimal, Registro>();
        for (Registro fila : registros) {
            if (fila.getRegistro() == null) {
                throw new IllegalArgumentException("Hay una fila sin registro en el Vademecum.");
            }
            BigDecimal clave = fila.getRegistro().stripTrailingZeros();
            if (resultado.put(clave, fila) != null) {
                throw new IllegalArgumentException("El registro " + clave.toPlainString() + " esta repetido. No se modifico el Vademecum.");
            }
        }
        return resultado;
    }

    public static Comparacion comparar(List<Registro> anteriores, List<Registro> actuales) {
        Map<BigDecimal, Registro> previo = indexar(anteriores);
        Map<BigDecimal, Registro> nuevo = indexar(actuales);
        Comparacion resultado = new Comparacion();
        for (Map.Entry<BigDecimal, Registro> entrada : nuevo.entrySet()) {
            Registro antes = previo.get(entrada.getKey());
            Registro ahora = entrada.getValue();
            if (antes == null) {
                resultado.getAltas().add(ahora);
            } else if (!mismosValores(antes, ahora)) {
                resultado.getModificadosAntes().add(antes);
                resultado.getModificadosDespues().add(ahora);
            } else {
                resultado.agregarSinCambios();
            }
        }
        for (Map.Entry<BigDecimal, Registro> entrada : previo.entrySet()) {
            if (!nuevo.containsKey(entrada.getKey())) {
                resultado.getBajas().add(entrada.getValue());
            }
        }
        return resultado;
    }

    private static boolean mismosValores(Registro antes, Registro ahora) {
        if (antes.getValores().length != ahora.getValores().length) { return false; }
        for (int i = 0; i < antes.getValores().length; i++) {
            String a = antes.getValores()[i] == null ? "" : antes.getValores()[i].trim();
            String b = ahora.getValores()[i] == null ? "" : ahora.getValores()[i].trim();
            if (!a.equals(b)) { return false; }
        }
        return true;
    }
}
