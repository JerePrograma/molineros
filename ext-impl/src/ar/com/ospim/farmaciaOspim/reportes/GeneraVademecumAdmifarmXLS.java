package ar.com.ospim.farmaciaOspim.reportes;

import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Registro;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Comparacion;
import ar.com.ospim.farmaciaOspim.helper.VademecumAdmifarmHelper;

public class GeneraVademecumAdmifarmXLS {
    // Misma biblioteca HSSF de GeneraVademecumXLS. La primera hoja permite reimportar el contenido.
    public static HSSFWorkbook generar(String tipo, ImportacionVademecumAdmifarm importacion) {
        String[] columnas = ImportacionVademecumAdmifarm.getColumnas(tipo);
        Comparacion cambios = VademecumAdmifarmHelper.comparar(importacion.getAnteriores(), importacion.getRegistros());
        HSSFWorkbook libro = new HSSFWorkbook();
        HSSFFont fuente = libro.createFont();
        fuente.setBold(true);
        HSSFCellStyle encabezado = libro.createCellStyle();
        encabezado.setFont(fuente);
        agregarHoja(libro, "Vademecum", columnas, importacion.getRegistros(), encabezado);
        agregarHoja(libro, "Altas", columnas, cambios.getAltas(), encabezado);
        agregarHoja(libro, "Bajas", columnas, cambios.getBajas(), encabezado);
        agregarHoja(libro, "Modificaciones antes", columnas, cambios.getModificadosAntes(), encabezado);
        agregarHoja(libro, "Modificaciones ahora", columnas, cambios.getModificadosDespues(), encabezado);
        HSSFSheet resumen = libro.createSheet("Resumen");
        String[][] datos = {
            {"Tipo", tipo},
            {"Fecha importacion", importacion.getFecha().toString()},
            {"Fecha anterior", importacion.getFechaAnterior() == null ? "Sin historico anterior" : importacion.getFechaAnterior().toString()},
            {"Cantidad de registros", String.valueOf(importacion.getRegistros().size())},
            {"Altas", String.valueOf(cambios.getAltas().size())},
            {"Bajas", String.valueOf(cambios.getBajas().size())},
            {"Modificaciones", String.valueOf(cambios.getModificadosDespues().size())},
            {"Sin cambios", String.valueOf(cambios.getSinCambios())}
        };
        for (int i = 0; i < datos.length; i++) {
            HSSFRow fila = resumen.createRow(i);
            fila.createCell(0).setCellValue(datos[i][0]);
            fila.createCell(1).setCellValue(datos[i][1]);
        }
        resumen.setColumnWidth(0, 28 * 256);
        resumen.setColumnWidth(1, 40 * 256);
        return libro;
    }

    private static void agregarHoja(HSSFWorkbook libro, String nombre, String[] columnas,
            List<Registro> registros, HSSFCellStyle estilo) {
        if (registros.size() > 65535) {
            throw new IllegalArgumentException("El historico excede el limite de filas del formato .xls.");
        }
        HSSFSheet hoja = libro.createSheet(nombre);
        HSSFRow encabezado = hoja.createRow(0);
        for (int c = 0; c < columnas.length; c++) {
            encabezado.createCell(c).setCellValue(columnas[c]);
            encabezado.getCell(c).setCellStyle(estilo);
            hoja.setColumnWidth(c, (c == 0 ? 18 : 32) * 256);
        }
        int numero = 1;
        for (Registro registro : registros) {
            HSSFRow fila = hoja.createRow(numero++);
            // Texto para conservar exactamente registros numericos de mas de 15 digitos.
            fila.createCell(0).setCellValue(registro.getRegistro().toPlainString());
            for (int c = 0; c < registro.getValores().length; c++) {
                String valor = registro.getValores()[c];
                fila.createCell(c + 1).setCellValue(valor == null ? "" : valor);
            }
        }
        hoja.createFreezePane(0, 1);
    }
}
