package ar.com.ospim.farmaciaOspim.helper;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.portlet.PortletSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ar.com.ospim.farmaciaOspim.WebKeysFarmaciaOspim;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Comparacion;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Registro;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class VademecumAdmifarmHelper {

    private static final Log log =
            LogFactoryUtil.getLog(VademecumAdmifarmHelper.class);

    private static final String TOKEN = "VADEMECUM_ADMIFARM_TOKEN_";
    public static final long MAX_ARCHIVO = 20L * 1024L * 1024L;

    public static boolean tienePermiso(User user) throws Exception {
        return user != null && !user.isDefaultUser()
                && PermissionUtil.userContainsRole(user,
                        WebKeysFarmaciaOspim.ROL_VADEMECUM_FARMACIA_OSPIM);
    }

    public static void validarPermiso(User user) throws Exception {
        if (!tienePermiso(user)) {
            throw new SecurityException("No posee permisos para administrar Vademecum.");
        }
    }

    public static synchronized String obtenerToken(PortletSession session,
            long usuario, String tipo) {
        ImportacionVademecumAdmifarm.getColumnas(tipo);
        String clave = TOKEN + tipo + "_" + usuario;
        String token = (String) session.getAttribute(clave);

        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute(clave, token);
        }
        return token;
    }

    public static synchronized void consumirToken(PortletSession session,
            long usuario, String tipo, String token) {
        ImportacionVademecumAdmifarm.getColumnas(tipo);
        String clave = TOKEN + tipo + "_" + usuario;
        String esperado = (String) session.getAttribute(clave);

        if (esperado == null || !esperado.equals(token)) {
            throw new IllegalArgumentException(
                    "El formulario vencio o ya fue enviado. Vuelva a cargar la pantalla.");
        }
        session.removeAttribute(clave);
    }

    public static List<Registro> leerArchivo(File archivo, String nombre,
            String tipo) throws Exception {
        String[] columnas = ImportacionVademecumAdmifarm.getColumnas(tipo);
        if (archivo == null || !archivo.isFile() || archivo.length() == 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un archivo Excel con registros.");
        }
        if (archivo.length() > MAX_ARCHIVO) {
            throw new IllegalArgumentException("El archivo supera el limite de 20 MB.");
        }
        if (nombre == null || !nombre.toLowerCase(Locale.ENGLISH).endsWith(".xlsx")) {
            throw new IllegalArgumentException(
                    "El archivo debe tener formato .xlsx.");
        }

        FileInputStream input = new FileInputStream(archivo);
        Workbook libro = null;
        try {
            try {
                libro = new XSSFWorkbook(input);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "No se pudo leer el archivo en el formato Excel indicado por su extension.", e);
            }

            if (libro.getNumberOfSheets() == 0) {
                throw new IllegalArgumentException("El archivo no contiene hojas.");
            }
            Sheet hoja = libro.getSheetAt(0);
            DataFormatter formato = new DataFormatter();
            // El orden del archivo es fijo. No se leen los titulos del encabezado.
            int[] posiciones = posicionesColumnas(tipo);

            List<Registro> registros = new ArrayList<Registro>();
            StringBuffer filasSinRegistro = new StringBuffer();
            int cantidadSinRegistro = 0;
            // La primera fila corresponde al encabezado; los datos empiezan en la segunda.
            int primeraFila = 1;

            for (int f = primeraFila; f <= hoja.getLastRowNum(); f++) {
                Row fila = hoja.getRow(f);
                if (fila == null) {
                    continue;
                }

                boolean vacia = true;
                for (int c = 0; c < fila.getLastCellNum(); c++) {
                    // La celda de registro no utiliza la validacion restrictiva
                    // del resto de las columnas, ni siquiera al detectar filas vacias.
                    if (c == posiciones[0]) {
                        if (tieneDatoRegistro(fila.getCell(c), formato)) {
                            vacia = false;
                        }
                        continue;
                    }
                    String valor = valorCelda(fila.getCell(c), formato, f + 1);
                    if (valor.length() > 0) {
                        vacia = false;
                        boolean incluida = false;
                        for (int i = 0; i < posiciones.length; i++) {
                            if (posiciones[i] == c) {
                                incluida = true;
                                break;
                            }
                        }
                        if (!incluida) {
                            throw new IllegalArgumentException("La fila " + (f + 1)
                                    + " tiene datos en una columna sin destino. No se omitieron datos.");
                        }
                    }
                }
                if (vacia) {
                    continue;
                }

                BigDecimal registro = leerRegistro(fila.getCell(posiciones[0]), formato);
                if (registro == null) {
                    cantidadSinRegistro++;
                    if (cantidadSinRegistro <= 10) {
                        if (filasSinRegistro.length() > 0) {
                            filasSinRegistro.append(", ");
                        }
                        filasSinRegistro.append(f + 1);
                    }
                }
                // La fila se conserva aunque no tenga numero. No se inventa un identificador.

                String[] valores = new String[columnas.length - 1];
                for (int c = 1; c < columnas.length; c++) {
                    valores[c - 1] = valorCelda(fila.getCell(posiciones[c]), formato, f + 1);
                }
                if (registros.size() == 65535) {
                    throw new IllegalArgumentException(
                            "El archivo supera el limite de 65535 registros.");
                }
                registros.add(new Registro(registro, valores));
            }

            if (registros.isEmpty()) {
                throw new IllegalArgumentException(
                        "El archivo no contiene registros. No se modifico el Vademecum.");
            }
            indexar(registros);
            if (cantidadSinRegistro > 0) {
                log.warn("Archivo leido con advertencias: " + cantidadSinRegistro
                        + " filas sin registro numerico valido. Filas: "
                        + filasSinRegistro.toString()
                        + (cantidadSinRegistro > 10 ? " (primeras 10)" : "")
                        + ". Se conservaron para importar con registro nulo.");
            }
            return registros;
        } finally {
            try {
                if (libro != null) {
                    libro.close();
                }
            } finally {
                input.close();
            }
        }
    }

    private static int[] posicionesColumnas(String tipo) {
        if ("ampliado".equals(tipo)) {
            // Orden interno: registro, nombre, presentacion, accion,
            // monodroga, laboratorio, tipo_venta.
            // El ultimo destino sigue siendo tipo_venta en el bean vigente.
            return new int[] { 0, 1, 3, 4, 2, 5, 6 };
        }
        if ("pmo".equals(tipo)) {
            // Orden interno: registro, nombre, monodroga, presentacion,
            // accion, laboratorio. El bean vigente no tiene destino para G.
            return new int[] { 0, 1, 4, 2, 3, 5 };
        }
        throw new IllegalArgumentException("El tipo de Vademecum no es valido.");
    }

    private static boolean tieneDatoRegistro(Cell celda, DataFormatter formato) {
        if (celda == null || celda.getCellType() == CellType.BLANK) {
            return false;
        }
        if (celda.getCellType() == CellType.STRING) {
            return formato.formatCellValue(celda).trim().length() > 0;
        }
        return true;
    }

    private static BigDecimal leerRegistro(Cell celda, DataFormatter formato) {
        if (celda == null) {
            return null;
        }
        try {
            if (celda.getCellType() == CellType.NUMERIC) {
                BigDecimal numero = BigDecimal.valueOf(celda.getNumericCellValue());
                if (numero.precision() - numero.scale() > 15) {
                    // No usar como identificador un numero cuya precision no es confiable.
                    return null;
                }
                return numero;
            }
            if (celda.getCellType() == CellType.STRING) {
                return new BigDecimal(formato.formatCellValue(celda).trim());
            }
        } catch (NumberFormatException e) {
            return null;
        }
        // Vacia, formula, error o booleano en registro: conservar la fila sin numero.
        // No se ejecutan formulas para obtener un identificador.
        return null;
    }

    private static String valorCelda(Cell celda, DataFormatter formato, int fila) {
        if (celda == null) {
            return "";
        }
        if (celda.getCellType() == CellType.FORMULA
                || celda.getCellType() == CellType.ERROR
                || celda.getCellType() == CellType.BOOLEAN) {
            throw new IllegalArgumentException("La fila " + fila
                    + " contiene una formula, error o valor logico. Importe valores.");
        }
        return formato.formatCellValue(celda).trim();
    }

    public static Map<BigDecimal, Registro> indexar(List<Registro> registros) {
        if (registros == null) {
            throw new IllegalArgumentException("No se informaron los registros del Vademecum.");
        }
        Map<BigDecimal, Registro> resultado = new LinkedHashMap<BigDecimal, Registro>();
        for (Registro fila : registros) {
            if (fila == null) {
                throw new IllegalArgumentException("Hay una fila nula en el Vademecum.");
            }
            if (fila.getValores() == null) {
                throw new IllegalArgumentException("Hay una fila sin columnas en el Vademecum.");
            }
            // Este indice solo identifica filas numeradas. comparar() procesa
            // las filas sin numero por separado, sin descartarlas de la importacion.
            if (fila.getRegistro() == null) {
                continue;
            }
            BigDecimal clave = fila.getRegistro().stripTrailingZeros();
            if (resultado.put(clave, fila) != null) {
                throw new IllegalArgumentException("El registro " + clave.toPlainString()
                        + " esta repetido. No se modifico el Vademecum.");
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
        compararSinRegistro(anteriores, actuales, resultado);
        return resultado;
    }

    private static void compararSinRegistro(List<Registro> anteriores,
            List<Registro> actuales, Comparacion resultado) {
        Map<List<String>, LinkedList<Registro>> pendientes =
                new LinkedHashMap<List<String>, LinkedList<Registro>>();

        for (Registro fila : anteriores) {
            if (fila.getRegistro() != null) {
                continue;
            }
            List<String> clave = claveSinRegistro(fila);
            LinkedList<Registro> coincidencias = pendientes.get(clave);
            if (coincidencias == null) {
                coincidencias = new LinkedList<Registro>();
                pendientes.put(clave, coincidencias);
            }
            coincidencias.add(fila);
        }

        for (Registro fila : actuales) {
            if (fila.getRegistro() != null) {
                continue;
            }
            LinkedList<Registro> coincidencias = pendientes.get(claveSinRegistro(fila));
            if (coincidencias != null && !coincidencias.isEmpty()) {
                // Se empareja una sola ocurrencia; no se colapsan filas repetidas.
                coincidencias.removeFirst();
                resultado.agregarSinCambios();
            } else {
                resultado.getAltas().add(fila);
            }
        }

        for (LinkedList<Registro> coincidencias : pendientes.values()) {
            resultado.getBajas().addAll(coincidencias);
        }
    }

    private static List<String> claveSinRegistro(Registro fila) {
        List<String> clave = new ArrayList<String>();
        for (String valor : fila.getValores()) {
            clave.add(valor == null ? "" : valor.trim());
        }
        return clave;
    }

    private static boolean mismosValores(Registro antes, Registro ahora) {
        if (antes.getValores().length != ahora.getValores().length) {
            return false;
        }
        for (int i = 0; i < antes.getValores().length; i++) {
            String a = antes.getValores()[i] == null ? "" : antes.getValores()[i].trim();
            String b = ahora.getValores()[i] == null ? "" : ahora.getValores()[i].trim();
            if (!a.equals(b)) {
                return false;
            }
        }
        return true;
    }
}
