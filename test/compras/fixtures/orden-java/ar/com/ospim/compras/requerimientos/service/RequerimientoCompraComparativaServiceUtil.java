package ar.com.ospim.compras.requerimientos.service;
import java.sql.Connection;
import java.util.*;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
public class RequerimientoCompraComparativaServiceUtil {
    public static List<RequerimientoCompraComparativa> comparativas=new ArrayList<RequerimientoCompraComparativa>();
    public static List<RequerimientoCompraComparativa> listar(Connection con,int id,int prestador){return comparativas;}
    public static List<RequerimientoCompraComparativa> listar(int id,int prestador){throw new IllegalStateException("Lectura global prohibida.");}
}
