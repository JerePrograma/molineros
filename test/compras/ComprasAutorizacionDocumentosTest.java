import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.servlets.PdfServlet;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Arrays;

/** PdfReader y sello reales, documentos sinteticos; sin JDBC, DL ni transporte. */
public class ComprasAutorizacionDocumentosTest {
    private static int checks;
    public static void main(String[] args) throws Exception {
        probarImagenes();
        byte[] original = pdf(false);
        byte[] conservado = (byte[]) original.clone();
        DocumentoLibraryComprasHelper.validarPdfParaCorreoAutorizacion(original, "original.pdf");
        verificar(Arrays.equals(original, conservado), "Validar conserva los bytes del original.");
        rechazar(null, "original.pdf");
        rechazar(new byte[0], "original.pdf");
        rechazar("%PDF-".getBytes("ISO-8859-1"), "presupuesto_truncado.pdf");
        rechazar(Arrays.copyOf(original, original.length / 2), "presupuesto_cortado.pdf");
        rechazar("%PDF-1.4\ncontenido ajeno\n%%EOF".getBytes("ISO-8859-1"), "presupuesto_invalido.pdf");
        rechazar(pdf(true), "presupuesto_protegido.pdf");
        PdfServlet generador = new PdfServlet();
        byte[] actual = generador.identificarCopiaActualAutorizacion(original, 1376, "PEDIDO DE PRESUPUESTO");
        DocumentoLibraryComprasHelper.validarPdfParaCorreoAutorizacion(actual, "pedido_copia_actual.pdf");
        if (args.length > 0) {
            File carpeta = new File(args[0]);
            if (!carpeta.isDirectory() && !carpeta.mkdirs()) throw new IllegalStateException("No se pudo crear evidencia PDF.");
            guardar(carpeta, "autorizacion_original_sintetico.pdf", original);
            guardar(carpeta, "autorizacion_copia_actual_sintetica.pdf", actual);
        }
        verificar(Arrays.equals(original, conservado), "El sello no reemplaza ni altera el PDF original.");
        PdfReader anterior = new PdfReader(original);
        PdfReader nuevo = new PdfReader(actual);
        try {
            verificar(nuevo.getNumberOfPages() == 2 && nuevo.getNumberOfPages() == anterior.getNumberOfPages(), "Copia mantiene las dos paginas.");
            for (int pagina = 1; pagina <= nuevo.getNumberOfPages(); pagina++) {
                Rectangle a = anterior.getPageSize(pagina);
                Rectangle b = nuevo.getPageSize(pagina);
                verificar(Math.abs(a.getWidth() - b.getWidth()) < 0.01F && b.getHeight() > a.getHeight(), "Banda actual amplia altura sin reducir ancho de pagina " + pagina);
                String contenido = new String(nuevo.getPageContent(pagina), "ISO-8859-1");
                verificar(contenido.indexOf("COPIA ACTUAL") >= 0 && contenido.indexOf("1376") >= 0, "Cada pagina identifica copia actual y requerimiento.");
                verificar(contenido.indexOf("CONTENIDO ORIGINAL CONTROLADO " + pagina) >= 0, "Conserva el contenido del documento en pagina " + pagina);
                verificar(contenido.toLowerCase(java.util.Locale.ENGLISH).indexOf("historico") >= 0, "No acredita envio historico.");
            }
        } finally { anterior.close(); nuevo.close(); }
        boolean rechazado = false;
        try { generador.identificarCopiaActualAutorizacion(original, 0, "PEDIDO"); } catch (Exception esperado) { rechazado = true; }
        verificar(rechazado, "Sello rechaza requerimiento sin identidad.");
        rechazado = false;
        try { generador.identificarCopiaActualAutorizacion(original, 1376, ""); } catch (Exception esperado) { rechazado = true; }
        verificar(rechazado, "Sello rechaza tipo documental vacio.");
        byte[] rotado = rotar(original);
        rechazado = false;
        try { generador.identificarCopiaActualAutorizacion(rotado, 1376, "PEDIDO"); } catch (Exception esperado) { rechazado = true; }
        verificar(rechazado, "Rotacion no soportada se rechaza expresamente, sin producir copia dudosa.");
        System.out.println("COMPRAS_AUTORIZACION_DOCUMENTOS_REALES_OK checks=" + checks + " JDBC=0 SMTP=0 documentos=sinteticos");
    }
    private static void probarImagenes() throws Exception {
        String[] formatos = {"png", "jpg"};
        String[] tipos = {"image/png", "image/jpeg"};
        for (int i = 0; i < formatos.length; i++) {
            BufferedImage imagen = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
            imagen.setRGB(0, 0, 0x123456);
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            verificar(ImageIO.write(imagen, formatos[i], salida), "Codec de prueba disponible.");
            byte[] bytes = salida.toByteArray();
            byte[] copia = (byte[]) bytes.clone();
            DocumentoLibraryComprasHelper.validarImagenParaCorreoAutorizacion(bytes, "orden." + formatos[i], tipos[i]);
            verificar(Arrays.equals(bytes, copia), "Decodificar imagen conserva el original " + formatos[i]);
            byte[] soloFirma = Arrays.copyOf(bytes, i == 0 ? 8 : 3);
            rechazarImagen(soloFirma, "orden." + formatos[i], tipos[i]);
            rechazarImagen(Arrays.copyOf(bytes, bytes.length - 2), "orden." + formatos[i], tipos[i]);
        }
    }
    private static void rechazarImagen(byte[] bytes, String nombre, String tipo) throws Exception {
        boolean rechazado = false;
        try { DocumentoLibraryComprasHelper.validarImagenParaCorreoAutorizacion(bytes, nombre, tipo); } catch (Exception esperado) { rechazado = true; }
        verificar(rechazado, "Imagen incompleta se rechaza por contenido real: " + nombre);
    }
    private static void guardar(File carpeta, String nombre, byte[] bytes) throws Exception {
        FileOutputStream salida = new FileOutputStream(new File(carpeta, nombre));
        try { salida.write(bytes); } finally { salida.close(); }
    }
    private static byte[] pdf(boolean cifrado) throws Exception {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document();
        PdfWriter writer = PdfWriter.getInstance(documento, salida);
        if (cifrado) writer.setEncryption("lectura".getBytes("ISO-8859-1"), "propietario".getBytes("ISO-8859-1"), PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);
        documento.open();
        documento.add(new Paragraph("CONTENIDO ORIGINAL CONTROLADO 1"));
        documento.newPage();
        documento.add(new Paragraph("CONTENIDO ORIGINAL CONTROLADO 2"));
        documento.close();
        return salida.toByteArray();
    }
    private static byte[] rotar(byte[] bytes) throws Exception {
        PdfReader lector = new PdfReader(bytes);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        PdfStamper stamp = null;
        try {
            lector.getPageN(1).put(PdfName.ROTATE, new PdfNumber(90));
            stamp = new PdfStamper(lector, salida);
            stamp.close(); stamp = null;
            return salida.toByteArray();
        } finally { if (stamp != null) stamp.close(); lector.close(); }
    }
    private static void rechazar(byte[] bytes, String nombre) throws Exception {
        boolean rechazado = false;
        try { DocumentoLibraryComprasHelper.validarPdfParaCorreoAutorizacion(bytes, nombre); } catch (Exception esperado) { rechazado = true; }
        verificar(rechazado, "PDF original invalido se rechaza: " + nombre);
    }
    private static void verificar(boolean valor, String mensaje) {
        if (!valor) throw new IllegalStateException(mensaje);
        checks++;
    }
}