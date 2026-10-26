import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

public class ComprasRequerimientoPdfTest {
    public static void main(String[] args) throws Exception {
        if (args.length == 3 && "--compile".equals(args[0])) {
            JasperCompileManager.compileReportToFile(args[1], args[2]);
            System.out.println("JASPER_COMPILADO " + args[2]);
            return;
        }
        String[] casos = {"sistemas-adjudicada", "sistemas-sin-empresa",
                "sistemas-sin-datos-fiscales", "sistemas-empresa-larga",
                "medica", "rrhh", "sistemas-multipagina"};
        String empresaLarga = "EMPRESA LARGA DE PRUEBA ";
        while (empresaLarga.length() < 195) empresaLarga += "denominacion extensa ";
        empresaLarga = empresaLarga.substring(0, 194) + " FINAL";
        for (int caso = 0; caso < casos.length; caso++) {
            String empresa = caso == 1 || caso == 4 || caso == 5 ? null
                    : caso == 3 ? empresaLarga : "EMPRESA ADJUDICADA DE PRUEBA";
            String cuit = caso == 2 || empresa == null ? null : "30123456789";
            String sucursal = caso == 2 || empresa == null ? null : "000007";
            String sector = caso == 4 ? "PRESTACIONES MEDICAS" : caso == 5 ? "RRHH" : "SISTEMAS";
            Map<String, String> parametros = new HashMap<String, String>();
            parametros.put("ID_REQUERIMIENTO", "123");
            List<Map<String, ?>> datos = filas(sector, empresa, cuit, sucursal, caso == 6 ? 60 : 3);
            JasperPrint print = JasperFillManager.fillReport(args[0], parametros,
                    new JRMapCollectionDataSource(datos));
            byte[] pdf = JasperExportManager.exportReportToPdf(print);
            FileOutputStream out = new FileOutputStream(args[1] + "/" + casos[caso] + ".pdf");
            try { out.write(pdf); } finally { out.close(); }
            String texto = texto(print);
            if (pdf.length < 1000 || pdf[0] != '%' || pdf[1] != 'P') fallar(casos[caso], "PDF invalido");
            if (caso == 6 ? print.getPages().size() <= 1 : print.getPages().size() != 1) {
                fallar(casos[caso], "Cantidad de paginas inesperada: " + print.getPages().size());
            }
            if (texto.indexOf("null") >= 0) fallar(casos[caso], "Imprime null literal");
            if (empresa != null) {
                contiene(casos[caso], texto, empresa);
                contiene(casos[caso], texto, "Empresa adjudicada:");
                if (cuit != null) contiene(casos[caso], texto, cuit);
                if (sucursal != null) contiene(casos[caso], texto, sucursal);
                if (caso == 2 && (texto.indexOf("CUIT:") >= 0 || texto.indexOf("Sucursal:") >= 0)) {
                    fallar(casos[caso], "Rotulos fiscales sin datos");
                }
            } else if (texto.indexOf("Empresa adjudicada:") >= 0 || texto.indexOf("EMPRESA ADJUDICADA") >= 0) {
                fallar(casos[caso], "Imprime empresa sin adjudicacion");
            }
            contiene(casos[caso], texto, "Detalle 1");
            contiene(casos[caso], texto, caso == 6 ? "Detalle 60" : "Detalle 3");
            if (caso == 4) contiene(casos[caso], texto, "PRESTADOR DE PRUEBA");
            if (args.length > 2 && (caso == 1 || caso == 4 || caso == 5)) {
                JasperPrint original = JasperFillManager.fillReport(args[2], parametros,
                        new JRMapCollectionDataSource(datos));
                compararLayout(casos[caso], original, print);
            }
            System.out.println("PDF_OK " + casos[caso] + " paginas=" + print.getPages().size() + " bytes=" + pdf.length);
        }
    }
    private static List<Map<String, ?>> filas(String sector, String empresa, String cuit, String sucursal, int cantidad) {
        List<Map<String, ?>> filas = new ArrayList<Map<String, ?>>();
        for (int i = 1; i <= cantidad; i++) {
            Map<String, Object> fila = new HashMap<String, Object>();
            fila.put("id_requerimiento", Integer.valueOf(123));
            fila.put("alta_fecha", new Timestamp(1792983600000L));
            fila.put("alta_usr", "USUARIO DE PRUEBA");
            fila.put("id_estado", Integer.valueOf(1));
            fila.put("estado_descripcion", "PENDIENTE");
            fila.put("id_sector", Integer.valueOf(1));
            fila.put("sector_descripcion", sector);
            fila.put("requiere_afiliado", Boolean.valueOf("PRESTACIONES MEDICAS".equals(sector)));
            fila.put("afiliado_id_ospim", Integer.valueOf(1234));
            fila.put("afiliado_int", Integer.valueOf(0));
            fila.put("afiliado_nombre_apellido", "AFILIADO DE PRUEBA");
            fila.put("afiliado_documento", "DOCUMENTO DE PRUEBA");
            fila.put("afiliado_direccion", "DIRECCION DE PRUEBA");
            fila.put("afiliado_localidad", "LOCALIDAD DE PRUEBA");
            fila.put("afiliado_provincia", "PROVINCIA DE PRUEBA");
            fila.put("afiliado_celular", "CELULAR DE PRUEBA");
            fila.put("afiliado_telefono", "TELEFONO DE PRUEBA");
            fila.put("afiliado_email", "prueba@example.invalid");
            fila.put("afiliado_seccional", "SECCIONAL DE PRUEBA");
            fila.put("cargo_ospim", Integer.valueOf(100));
            fila.put("cargo_tercerizadora", Integer.valueOf(0));
            fila.put("id_tercerizadora", null);
            fila.put("recupero", Boolean.FALSE);
            fila.put("surge", Boolean.FALSE);
            fila.put("observaciones", "Observacion general de prueba.");
            fila.put("detalle_id", Integer.valueOf(i));
            fila.put("detalle_orden", Integer.valueOf(i));
            fila.put("tipo_item", "MANUAL");
            fila.put("codigo_item", "COD" + i);
            fila.put("descripcion_item", "Detalle " + i);
            fila.put("id_prestacion", null);
            fila.put("id_tipo_nomenclador", null);
            fila.put("codigo_nomenclador", null);
            fila.put("descripcion_nomenclador", null);
            fila.put("id_medicamento", null);
            fila.put("troquel", null);
            fila.put("nombre_medicamento", null);
            fila.put("cantidad", Integer.valueOf(1));
            fila.put("precio_unitario_estimado", new BigDecimal("123.45"));
            fila.put("precio_total_estimado", new BigDecimal("123.45"));
            fila.put("prestador_razon_social", "PRESTACIONES MEDICAS".equals(sector) ? "PRESTADOR DE PRUEBA" : null);
            fila.put("prestador_cuit", "PRESTACIONES MEDICAS".equals(sector) ? "30987654321" : null);
            fila.put("detalle_observaciones", "Observacion del detalle.");
            if ("SISTEMAS".equals(sector)) {
                fila.put("empresa_adjudicada_descripcion", empresa);
                fila.put("empresa_adjudicada_cuit", cuit);
                fila.put("empresa_adjudicada_sucursal", sucursal);
            }
            filas.add(fila);
        }
        return filas;
    }
    private static void compararLayout(String caso, JasperPrint original, JasperPrint actual) throws Exception {
        List<String> antes = posiciones(original);
        List<String> despues = posiciones(actual);
        int cantidad = Math.max(antes.size(), despues.size());
        for (int i = 0; i < cantidad; i++) {
            String a = i < antes.size() ? antes.get(i) : "<sin elemento>";
            String d = i < despues.size() ? despues.get(i) : "<sin elemento>";
            if (!a.equals(d)) fallar(caso, "Layout distinto elemento " + i + " original=[" + a + "] actual=[" + d + "]");
        }
        System.out.println("LAYOUT_LEGACY_OK " + caso + " textos=" + antes.size());
    }
    private static List<String> posiciones(JasperPrint print) {
        List<String> resultado = new ArrayList<String>();
        List paginas = print.getPages();
        for (int i = 0; i < paginas.size(); i++) {
            posiciones(((JRPrintPage) paginas.get(i)).getElements(), resultado, i + 1, 0, 0);
        }
        return resultado;
    }
    private static void posiciones(List elementos, List<String> resultado, int pagina, int x, int y) {
        for (int i = 0; i < elementos.size(); i++) {
            JRPrintElement elemento = (JRPrintElement) elementos.get(i);
            if (elemento instanceof JRPrintText) {
                resultado.add("pagina=" + pagina + " x=" + (x + elemento.getX()) + " y=" + (y + elemento.getY())
                        + " texto=" + ((JRPrintText) elemento).getFullText());
            }
            if (elemento instanceof JRPrintFrame) {
                posiciones(((JRPrintFrame) elemento).getElements(), resultado, pagina,
                        x + elemento.getX(), y + elemento.getY());
            }
        }
    }
    private static String texto(JasperPrint print) {
        StringBuffer resultado = new StringBuffer();
        List paginas = print.getPages();
        for (int i = 0; i < paginas.size(); i++) agregar(((JRPrintPage) paginas.get(i)).getElements(), resultado);
        return resultado.toString();
    }
    private static void agregar(List elementos, StringBuffer resultado) {
        for (int i = 0; i < elementos.size(); i++) {
            JRPrintElement elemento = (JRPrintElement) elementos.get(i);
            if (elemento instanceof JRPrintText) resultado.append(((JRPrintText) elemento).getFullText()).append('\n');
            if (elemento instanceof JRPrintFrame) agregar(((JRPrintFrame) elemento).getElements(), resultado);
        }
    }
    private static void contiene(String caso, String texto, String esperado) throws Exception {
        if (texto.indexOf(esperado) < 0) fallar(caso, "Falta texto: " + esperado);
    }
    private static void fallar(String caso, String detalle) throws Exception {
        throw new Exception(caso + ": " + detalle);
    }
}