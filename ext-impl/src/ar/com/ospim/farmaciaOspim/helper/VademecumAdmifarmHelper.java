package ar.com.ospim.farmaciaOspim.helper;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

import com.liferay.portal.model.User;

public class VademecumAdmifarmHelper {

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
            StringBuffer erroresRegistro = new StringBuffer();
            int cantidadErrores = 0;
            // La primera fila corresponde al encabezado; los datos empiezan en la segunda.
            int primeraFila = 1;

            for (int f = primeraFila; f <= hoja.getLastRowNum(); f++) {
                Row fila = hoja.getRow(f);
                if (fila == null) {
                    continue;
                }

                boolean vacia = true;
                for (int c = 0; c < fila.getLastCellNum(); c++) {
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

                Cell celdaRegistro = fila.getCell(posiciones[0]);
                BigDecimal registro;
                try {
                    if (celdaRegistro != null
                            && celdaRegistro.getCellType() == CellType.NUMERIC) {
                        registro = BigDecimal.valueOf(celdaRegistro.getNumericCellValue());
                        if (registro.precision() - registro.scale() > 15) {
                            throw new NumberFormatException("Precision de Excel");
                        }
                    } else {
                        registro = new BigDecimal(
                                valorCelda(celdaRegistro, formato, f + 1));
                    }
                } catch (NumberFormatException e) {
                    cantidadErrores++;
                    if (cantidadErrores <= 10) {
                        if (erroresRegistro.length() > 0) {
                            erroresRegistro.append(", ");
                        }
                        erroresRegistro.append(f + 1);
                    }
                    continue;
                }

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

            if (cantidadErrores > 0) {
                throw new IllegalArgumentException("Hay " + cantidadErrores
                        + " filas sin registro numerico valido. Filas: "
                        + erroresRegistro.toString() + ". No se importo ninguna fila.");
            }
            if (registros.isEmpty()) {
                throw new IllegalArgumentException(
                        "El archivo no contiene registros. No se modifico el Vademecum.");
            }
            indexar(registros);
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
            if (fila == null || fila.getRegistro() == null) {
                throw new IllegalArgumentException("Hay una fila sin registro en el Vademecum.");
            }
            if (fila.getValores() == null) {
                throw new IllegalArgumentException("Hay una fila sin columnas en el Vademecum.");
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
        return resultado;
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
