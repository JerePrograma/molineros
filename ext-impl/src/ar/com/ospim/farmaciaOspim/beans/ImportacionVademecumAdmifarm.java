package ar.com.ospim.farmaciaOspim.beans;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ImportacionVademecumAdmifarm {
    private Timestamp fecha;
    private Timestamp fechaAnterior;
    private List<Registro> registros = new ArrayList<Registro>();
    private List<Registro> anteriores = new ArrayList<Registro>();

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp valor) { fecha = valor; }
    public Timestamp getFechaAnterior() { return fechaAnterior; }
    public void setFechaAnterior(Timestamp valor) { fechaAnterior = valor; }
    public List<Registro> getRegistros() { return registros; }
    public void setRegistros(List<Registro> valor) { registros = valor; }
    public List<Registro> getAnteriores() { return anteriores; }
    public void setAnteriores(List<Registro> valor) { anteriores = valor; }

    public static String[] getColumnas(String tipo) {
        if ("ampliado".equals(tipo)) {
            return new String[] { "registro", "nombre", "presentacion", "accion",
                "monodroga", "laboratorio", "tipo_venta" };
        }
        if ("pmo".equals(tipo)) {
            return new String[] { "registro", "nombre", "monodroga", "presentacion",
                "accion", "laboratorio" };
        }
        throw new IllegalArgumentException("El tipo de Vademecum no es valido.");
    }

    public static class Registro {
        private BigDecimal registro;
        private String[] valores;

        public Registro(BigDecimal registro, String[] valores) {
            this.registro = registro;
            this.valores = valores;
        }
        public BigDecimal getRegistro() { return registro; }
        public String[] getValores() { return valores; }
    }

    public static class Comparacion {
        private List<Registro> altas = new ArrayList<Registro>();
        private List<Registro> bajas = new ArrayList<Registro>();
        private List<Registro> modificadosAntes = new ArrayList<Registro>();
        private List<Registro> modificadosDespues = new ArrayList<Registro>();
        private int sinCambios;

        public List<Registro> getAltas() { return altas; }
        public List<Registro> getBajas() { return bajas; }
        public List<Registro> getModificadosAntes() { return modificadosAntes; }
        public List<Registro> getModificadosDespues() { return modificadosDespues; }
        public int getSinCambios() { return sinCambios; }
        public void agregarSinCambios() { sinCambios++; }
    }
}
