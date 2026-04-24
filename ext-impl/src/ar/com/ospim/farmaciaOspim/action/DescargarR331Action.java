package ar.com.ospim.farmaciaOspim.action;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ar.com.ospim.util.ConnectionHelper;

public class DescargarR331Action {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static final int TIPO_PADRON = 1;
    private static final int TIPO_PRESTADORES = 2;
    private static final int TIPO_AFILIADOS_PROV = 3;
    private static final int TIPO_RECETAS_PRESTADOR = 4;
    private static final int TIPO_RECETAS_BENEF = 5;
    private static final int TIPO_PATOLOGIAS = 6;

    //PADRON AFILIADOS
    public static byte[] generarPadron(int anio, int trimestre) throws Exception {
        String sql =
            "select concat_ws('|'," +
            "  obra_social, cuit, cuil_titular, parentesco, cuil, documento_tipo, docu_numero, ape_nombre, sexo, estado_civil," +
            "  naci_fecha, nacionalidad, calle, numero, piso, depto, localidad, postal_codi, provincia, tipo_domi, telefono," +
            "  situ_revista, discapacitado, tipo_beneficiario, fecha_alta_os, fecha_corte, estado" +
            ") as linea " +
            "from conciliacion.r331_padron_afiliados_trimestre(?, ?)";

        return ejecutarQuery(sql, anio, trimestre, TIPO_PADRON);
    }
    
    //PRESTADORES
    public static byte[] generarPrestadores(int anio, int trimestre) throws Exception {
        String sql = "select * from conciliacion.r331_prestadores_trimestre(?, ?)";
        return ejecutarQuery(sql, anio, trimestre, TIPO_PRESTADORES);
    }

    //AFILIADOS POR PROVINCIA
    public static byte[] generarAfiliadosProvincia(int anio, int trimestre) throws Exception {
        String sql = "select * from conciliacion.r331_afiliados_provincia_trimestre(?, ?)";
        return ejecutarQuery(sql, anio, trimestre, TIPO_AFILIADOS_PROV);
    }
    
    //RECETAS POR PRESTADOR
    public static byte[] generarRecetasPorPrestador(int anio, int trimestre) throws Exception {
        String sql = "select * from conciliacion.r331_recetas_prestador_trimestre(?, ?)";
        return ejecutarQuery(sql, anio, trimestre, TIPO_RECETAS_PRESTADOR);
    }
    
    //RECETAS POR BENEFICIARIO
    public static byte[] generarRecetasPorBeneficiario(int anio, int trimestre) throws Exception {
        String sql = "select * from conciliacion.r331_recetas_beneficiario_trimestre(?, ?)";
        return ejecutarQuery(sql, anio, trimestre, TIPO_RECETAS_BENEF);
    }
    
    //PATOLOGIAS
    public static byte[] generarPatologias(int anio, int trimestre) throws Exception {
        String sql = "select * from conciliacion.r331_patologias_trimestre(?, ?)";
        return ejecutarQuery(sql, anio, trimestre, TIPO_PATOLOGIAS);
    }
    
    //arma las lineas
    private static byte[] ejecutarQuery(String sql, int anio, int trimestre, int tipo) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos, UTF8);

        try {
            con = ConnectionHelper.getConnection();

            ps = con.prepareStatement(sql);
            ps.setInt(1, anio);
            ps.setInt(2, trimestre);

            rs = ps.executeQuery();
            while (rs.next()) {

                String linea;

                if (tipo == TIPO_PADRON) {
                    //devuelve una linea
                    linea = rs.getString(1);

                } else if (tipo == TIPO_PRESTADORES) {
                    linea = formatearPrestadores(rs);

                } else if (tipo == TIPO_AFILIADOS_PROV) {
                    linea = formatearAfiliadosProvincia(rs);

                } else if (tipo == TIPO_RECETAS_PRESTADOR) {
                    linea = formatearRecetasPrestador(rs);

                } else if (tipo == TIPO_RECETAS_BENEF) {
                    linea = formatearRecetasBeneficiario(rs);

                } else if (tipo == TIPO_PATOLOGIAS) {
                    linea = formatearPatologias(rs);

                }else {
                    linea = null;
                }

                if (linea != null) {
                    writer.write(linea);
                }
                writer.write("\n");
            }

            writer.flush();
            return baos.toByteArray();

        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
            try { writer.close(); } catch (Exception ignored) {}
        }
    }

    private static String formatearPrestadores(ResultSet rs) throws Exception {      
        String cuit = nvl(rs.getString("cuit"), "");
        String nombre = rpad(nvl(rs.getString("nombre"), " "), 70);
        String tipo = nvl(rs.getString("tipo"), "09");
        String domi = rpad(nvl(rs.getString("domi"), " "), 70);
        String loc = lpad(nvl(rs.getString("localidad_sss"), "0"), 5, '0');     
        String repres = rpad(" ", 70);
        String cero = "0";
        String nroNrp = rpad(" ", 50);
        String amb = " ";
        String inter = " ";
        String altCompl = " ";
        String urg = " ";
        String saludMental = " ";
        String odonto = " ";
        String farmaciaFlag = " ";

        String estado = nvl(rs.getString("estado"), "A");

        return cuit + "|" + nombre + "|" + tipo + "|" + domi + "|" + loc + "|" +
               repres + "|" + cero + "|" + nroNrp + "|" +
               amb + "|" + inter + "|" + altCompl + "|" + urg + "|" +
               saludMental + "|" + odonto + "|" + farmaciaFlag + "|" + estado;
    }

    private static String formatearAfiliadosProvincia(ResultSet rs) throws Exception {
        String proceso = nvl(rs.getString("proceso"), "87");
        String cobertura = lpad(nvl(rs.getString("cobertura"), "0"), 3, '0');
        String provincia = lpad(nvl(rs.getString("provincia"), "0"), 2, '0');
        String cantidad = lpad(String.valueOf(rs.getLong("cantidad")), 11, '0');

        return proceso + "|" + cobertura + "|" + provincia + "|" + cantidad;
    }

    private static String formatearRecetasPrestador(ResultSet rs) throws Exception {
        String proceso = nvl(rs.getString("proceso"), "87");
        String cuit = nvl(rs.getString("cuit"), "");
        String cantidad = lpad(String.valueOf(rs.getLong("cantidad")), 11, '0');
        String monto = nvl(rs.getString("monto"), "0");
        monto = lpad(monto, 11, '0');

        String estado = nvl(rs.getString("estado"), "A");

        return proceso + "|" + cuit + "|" + cantidad + "|" + monto + "|" + estado;
    }

    private static String formatearRecetasBeneficiario(ResultSet rs) throws Exception {
        String receta = lpad(nvl(rs.getString("receta"), ""), 11, '0');
        String tipoDoc = nvl(rs.getString("tipo_doc"), "DU");
        String documento = lpad(nvl(rs.getString("documento"), "0"), 8, '0');
        String marca = nvl(rs.getString("marca"), "");
        String fecha = nvl(rs.getString("fecha_txt"), "");
        String generico = lpad(nvl(rs.getString("generico"), "0"), 3, '0');
        String cobertura = lpad(nvl(rs.getString("cobertura"), "0"), 3, '0');
        String cant = lpad(nvl(rs.getString("cantidad"), "0"), 2, '0');
        String monto = lpad(nvl(rs.getString("monto"), "0"), 11, '0');
        String estado = nvl(rs.getString("estado"), "A");

        return receta + "|" + tipoDoc + "|" + documento + "|" + marca + "|" + fecha + "|" +
               generico + "|" + cobertura + "|" + cant + "|" + monto + "|" + estado;
    }
    
    private static String formatearPatologias(ResultSet rs) throws Exception {
        String tipoDoc   = nvl(rs.getString("tipo_doc"), "DU");
        String documento = lpad(nvl(rs.getString("documento"), "0"), 8, '0');
        String patologia = lpad(nvl(rs.getString("patologia"), "0"), 3, '0');
        String cobertura = lpad(nvl(rs.getString("cobertura"), "0"), 3, '0');
        String estado    = nvl(rs.getString("estado"), "A");

        return tipoDoc + "|" + documento + "|" + patologia + "|" + cobertura + "|" + estado;
    }

    private static String nvl(String s, String def) {
        return (s == null) ? def : s;
    }

    private static String rpad(String s, int len) {
        if (s == null) s = "";
        if (s.length() >= len) return s.substring(0, len);
        StringBuffer sb = new StringBuffer(s);
        while (sb.length() < len) sb.append(' ');
        return sb.toString();
    }

    private static String lpad(String s, int len, char ch) {
        if (s == null) s = "";
        if (s.length() >= len) return s.substring(s.length() - len);
        StringBuffer sb = new StringBuffer();
        while (sb.length() + s.length() < len) sb.append(ch);
        sb.append(s);
        return sb.toString();
    }
}
