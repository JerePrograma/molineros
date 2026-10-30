package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.helper.EditarRequerimientoCompraHelper;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.SectorNomencladorCompra;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoComprasCreado;
import ar.com.ospim.compras.requerimientos.documentos.OrdenMedicaValidada;
import ar.com.ospim.util.ConnectionHelper;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Persistencia de edición de Requerimientos de Compra.
 *
 * Sólo abre/cierra conexiones, invoca funciones PostgreSQL, parametriza
 * valores y recupera los resultados técnicos de esas funciones.
 */
public class EditarRequerimientoCompraServiceImpl {

    public Transaccion abrirTransaccion() throws Exception {
        Connection con = ConnectionHelper.getConnectionForTransaction();

        if (con == null) {
            throw new SQLException(
                    "No se obtuvo una conexión transaccional para Compras."
            );
        }

        return new Transaccion(this, con);
    }

    public int guardarRequerimientoCompra(RequerimientoCompra r, String usuario) throws Exception {
        return new EditarRequerimientoCompraHelper().guardarRequerimientoCompra(r, usuario);
    }

    public int guardarRequerimientoCompra(Connection con, RequerimientoCompra r, String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt=null; ResultSet rs=null;
        try {
            if (r.getIdRequerimientoCompra() <= 0) {
                stmt=con.prepareStatement("INSERT INTO compras.requerimiento (estado,id_sector,afiliado_cuil_titular,"
                    + "afiliado_int,afiliado_id_ospim,afiliado_nombre,afiliado_apellido,afiliado_documento_tipo,afiliado_documento_nro,"
                    + "afiliado_direccion,afiliado_localidad,afiliado_provincia,afiliado_celular,afiliado_telefono,afiliado_email,"
                    + "cargo_ospim,cargo_tercerizadora,id_tercerizadora,recupero,surge,legales,observaciones,alta_usr,observacion_interna) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id_requerimiento");
                stmt.setInt(1,r.getEstado()); setNullableInteger(stmt,2,r.getIdSector());
                stmt.setString(3,r.getAfiliadoCuilTitular());setNullableInteger(stmt,4,r.getAfiliadoInt());
                setNullableInteger(stmt,5,r.getAfiliadoIdOspim());stmt.setString(6,r.getAfiliadoNombre());stmt.setString(7,r.getAfiliadoApellido());
                stmt.setString(8,r.getAfiliadoDocumentoTipo());stmt.setString(9,r.getAfiliadoDocumentoNro());stmt.setString(10,r.getAfiliadoDireccion());
                stmt.setString(11,r.getAfiliadoLocalidad());stmt.setString(12,r.getAfiliadoProvincia());stmt.setString(13,r.getAfiliadoCelular());
                stmt.setString(14,r.getAfiliadoTelefono());stmt.setString(15,r.getAfiliadoEmail());setNullableInteger(stmt,16,r.getCargoOspim());
                setNullableInteger(stmt,17,r.getCargoTercerizadora());stmt.setString(18,r.getIdTercerizadora());stmt.setBoolean(19,r.isRecupero());
                stmt.setBoolean(20,r.isSurge());stmt.setBoolean(21,r.isLegales());stmt.setString(22,r.getObservaciones());
                stmt.setString(23,usuario);stmt.setString(24,r.getObservacionInterna());
            } else {
                stmt=con.prepareStatement("UPDATE compras.requerimiento SET cargo_ospim=?,cargo_tercerizadora=?,id_tercerizadora=?,"
                    + "recupero=?,surge=?,observaciones=?,observacion_interna=?,modi_fecha=now(),modi_usr=? "
                    + "WHERE id_requerimiento=? AND estado=? AND baja_fecha IS NULL RETURNING id_requerimiento");
                setNullableInteger(stmt,1,r.getCargoOspim());setNullableInteger(stmt,2,r.getCargoTercerizadora());stmt.setString(3,r.getIdTercerizadora());
                stmt.setBoolean(4,r.isRecupero());stmt.setBoolean(5,r.isSurge());stmt.setString(6,r.getObservaciones());
                stmt.setString(7,r.getObservacionInterna());stmt.setString(8,usuario);stmt.setInt(9,r.getIdRequerimientoCompra());stmt.setInt(10,r.getEstado());
            }
            rs=stmt.executeQuery();return rs.next()?rs.getInt(1):0;
        } finally {cerrar(rs);ConnectionHelper.cerrar(stmt);}
    }

    public int registrarOrdenMedica(Connection con,int id,OrdenMedicaValidada orden,
            DocumentoComprasCreado documento,String usuario)throws Exception {
        return ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper.registrarOrdenMedicaEnTransaccion(
                new EditarRequerimientoCompraServiceUtil.Transaccion(new Transaccion(this,con)),id,orden,documento,usuario);
    }

    public int guardarObservacionInterna(int id,String observacion,String usuario)throws Exception {
        return new EditarRequerimientoCompraHelper().guardarObservacionInterna(id,observacion,usuario);
    }

    public int corregirItem(RequerimientoCompraDetalle detalle, String usuario) throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.EditarRequerimientoCompraHelper()
                .corregirItem(detalle, usuario);
    }

    private int corregirItem(Connection con, RequerimientoCompraDetalle detalle,
            String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("UPDATE compras.requerimiento_detalle SET "
                    + "tipo_item = ?, id_prestacion = ?, id_tipo_prestacion = ?, cantidad = ?, "
                    + "precio_total_estimado = ?, id_tipo_nomenclador = ?, codigo_nomenclador = ?, "
                    + "descripcion_nomenclador = ?, id_medicamento = NULL, troquel = NULL, "
                    + "nombre_medicamento = NULL, modi_fecha = now(), modi_usr = ? "
                    + "WHERE id_detalle = ? AND id_requerimiento = ? AND baja_fecha IS NULL "
                    + "RETURNING id_detalle");
            stmt.setString(1, detalle.getTipoItemNormalizado());
            stmt.setInt(2, detalle.getIdPrestacionInt());
            if (detalle.getIdTipoPrestacion() == null) stmt.setNull(3, Types.INTEGER);
            else stmt.setInt(3, detalle.getIdTipoPrestacion().intValue());
            stmt.setInt(4, detalle.getCantidad().intValue());
            stmt.setBigDecimal(5, detalle.getPrecioTotalEstimadoInformado());
            stmt.setInt(6, detalle.getIdTipoNomencladorInt());
            stmt.setString(7, detalle.getCodigoNomenclador());
            stmt.setString(8, detalle.getDescripcionNomenclador());
            stmt.setString(9, usuario);
            stmt.setInt(10, detalle.getIdInt());
            stmt.setInt(11, detalle.getIdRequerimientoCompra());
            rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public int guardarDetalle(RequerimientoCompraDetalle d, String usuario) throws Exception {
        return new EditarRequerimientoCompraHelper().guardarDetalle(d,usuario);
    }

    private int guardarDetalle(Connection con, RequerimientoCompraDetalle d, String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt=null;ResultSet rs=null;
        try {
            if(d.getIdInt()<=0) {
                stmt=con.prepareStatement("INSERT INTO compras.requerimiento_detalle (id_requerimiento,tipo_item,id_tipo_prestacion,"
                    + "id_prestacion,id_tipo_nomenclador,codigo_nomenclador,descripcion_nomenclador,id_medicamento,troquel,nombre_medicamento,"
                    + "cantidad,observaciones,alta_usr) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id_detalle");
            } else {
                stmt=con.prepareStatement("UPDATE compras.requerimiento_detalle SET id_requerimiento=?,tipo_item=?,id_tipo_prestacion=?,"
                    + "id_prestacion=?,id_tipo_nomenclador=?,codigo_nomenclador=?,descripcion_nomenclador=?,id_medicamento=?,troquel=?,"
                    + "nombre_medicamento=?,cantidad=?,observaciones=?,modi_usr=?,precio_unitario_estimado=NULL,precio_total_estimado=NULL,"
                    + "id_prestador=NULL,modi_fecha=now() WHERE id_detalle=? AND id_requerimiento=? AND baja_fecha IS NULL RETURNING id_detalle");
            }
            stmt.setInt(1,d.getIdRequerimientoCompra());stmt.setString(2,d.getTipoItem());setNullableInteger(stmt,3,d.getIdTipoPrestacion());
            setNullableInteger(stmt,4,d.getIdPrestacion());setNullableInteger(stmt,5,d.getIdTipoNomenclador());stmt.setString(6,d.getCodigoNomenclador());
            stmt.setString(7,d.getDescripcionNomenclador());setNullableInteger(stmt,8,d.getIdMedicamento());setNullableInteger(stmt,9,d.getTroquel());
            stmt.setString(10,d.getNombreMedicamento());setNullableInteger(stmt,11,d.getCantidad());stmt.setString(12,d.getObservaciones());stmt.setString(13,usuario);
            if(d.getIdInt()>0){stmt.setInt(14,d.getIdInt());stmt.setInt(15,d.getIdRequerimientoCompra());}
            rs=stmt.executeQuery();return rs.next()?rs.getInt(1):0;
        } finally {cerrar(rs);ConnectionHelper.cerrar(stmt);}
    }

    public void borrarDetalle(int detalle,String usuario)throws Exception {
        Connection con=null;PreparedStatement stmt=null;ResultSet rs=null;int id=0;
        try{
            con=ConnectionHelper.getConnection();
            stmt=con.prepareStatement("SELECT id_requerimiento FROM compras.requerimiento_detalle WHERE id_detalle=? AND baja_fecha IS NULL");
            stmt.setInt(1,detalle);rs=stmt.executeQuery();if(rs.next())id=rs.getInt(1);
        }finally{cerrar(rs);ConnectionHelper.cerrar(stmt,con);}
        new EditarRequerimientoCompraHelper().borrarDetalle(id,detalle,usuario);
    }

    public void borrarRequerimientoCompra(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        anularRequerimiento(
                idRequerimientoCompra,
                "",
                usuario
        );
    }

    public void anularRequerimiento(int id,String motivo,String usuario)throws Exception {
        new EditarRequerimientoCompraHelper().anularRequerimiento(id,motivo,usuario);
    }

    public void cambiarEstado(int id,int estado,String usuario)throws Exception {
        new EditarRequerimientoCompraHelper().cambiarEstado(id,estado,usuario);
    }

    public void cambiarEstado(Connection con,int id,int estado,String usuario)throws Exception {
        new EditarRequerimientoCompraHelper().cambiarEstadoEnTransaccion(
            new EditarRequerimientoCompraServiceUtil.Transaccion(new Transaccion(this,con)),id,estado,usuario);
    }

    public int guardarCotizacion(
            int idRequerimientoCompra,
            Integer[] idsDetalle,
            BigDecimal[] preciosUnitarios,
            Integer idPrestadorAdjudicado,
            String usuario) throws Exception {

        return guardarCotizacion(
                idRequerimientoCompra,
                construirArrayEnterosPostgreSql(idsDetalle),
                construirArrayNumericosPostgreSql(preciosUnitarios),
                "{}",
                idPrestadorAdjudicado,
                null,
                usuario
        );
    }

    public int guardarCotizacion(
            int idRequerimientoCompra,
            Integer[] idsDetalle,
            BigDecimal[] preciosUnitarios,
            Integer[] idsDetalleEliminados,
            Integer idPrestadorAdjudicado,
            boolean surge,
            String usuario) throws Exception {

        return guardarCotizacion(
                idRequerimientoCompra,
                construirArrayEnterosPostgreSql(idsDetalle),
                construirArrayNumericosPostgreSql(preciosUnitarios),
                construirArrayEnterosPostgreSql(idsDetalleEliminados),
                idPrestadorAdjudicado,
                Boolean.valueOf(surge),
                usuario
        );
    }

    public int guardarCotizacion(int id,String ids,String precios,String eliminados,
            Integer prestador,Boolean surge,String usuario)throws Exception {
        return new EditarRequerimientoCompraHelper().guardarCotizacionPersistida(id,ids,precios,eliminados,prestador,surge,usuario);
    }

    public int registrarPresupuesto(RequerimientoCompraPresupuesto presupuesto,String usuario)throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper()
                .registrarAsociacionConTransaccionPropia(presupuesto,usuario);
    }





    public boolean darDeBajaPresupuesto(int doc,int id,String usuario)throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper()
                .cambiarAsociacionConTransaccionPropia(id,doc,false,usuario,false);
    }

    public boolean reactivarPresupuesto(int doc,int id)throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper()
                .cambiarAsociacionConTransaccionPropia(id,doc,true,null,false);
    }

    public boolean darDeBajaCotizacionEmpresa(int doc,int id,String usuario)throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper()
                .cambiarAsociacionConTransaccionPropia(id,doc,false,usuario,true);
    }

    public boolean reactivarCotizacionEmpresa(int doc,int id)throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper()
                .cambiarAsociacionConTransaccionPropia(id,doc,true,null,true);
    }

    public int confirmarEnvioACotizar(int id,String usuario)throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.NotificarCotizacionPrestadorHelper().confirmarEnvioACotizarJava(id,usuario);
    }

    public int confirmarOrdenCompra(int id,String usuario)throws Exception {
        return new EditarRequerimientoCompraHelper().pasarAOrdenCompra(id,usuario);
    }

    public void guardarEmpresaAdjudicada(int idRequerimiento, String cuit, String sucursal, String usuario) throws Exception {
        new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper()
                .guardarEmpresaAdjudicada(idRequerimiento, cuit, sucursal, usuario);
    }

    private void guardarEmpresaAdjudicada(Connection con, int idRequerimiento,
            String cuit, String sucursal, String usuario) throws Exception {
        new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper().guardarEmpresaAdjudicadaEnTransaccion(
                new EditarRequerimientoCompraServiceUtil.Transaccion(new Transaccion(this, con)),
                idRequerimiento, cuit, sucursal, usuario);
    }

    public void guardarEmpresaAdjudicada(int idRequerimiento, int idPresupuesto, String usuario) throws Exception {
        new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper()
                .guardarEmpresaAdjudicada(idRequerimiento, idPresupuesto, usuario);
    }

    private void guardarEmpresaAdjudicada(Connection con, int idRequerimiento,
            int idPresupuesto, String usuario) throws Exception {
        new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper().guardarEmpresaAdjudicadaEnTransaccion(
                new EditarRequerimientoCompraServiceUtil.Transaccion(new Transaccion(this, con)),
                idRequerimiento, idPresupuesto, usuario);
    }

    public void completarEmpresaCotizacion(int id,int doc,String cuit,String sucursal,String usuario)throws Exception {
        new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper().completarEmpresaCotizacion(id,doc,cuit,sucursal,usuario);
    }

    public void editarEmpresaCotizacion(int id,int doc,String nombre,String cuit,String sucursal,String usuario)throws Exception {
        new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper().editarEmpresaCotizacion(id,doc,nombre,cuit,sucursal,usuario);
    }

    private boolean tieneCreacionReclamoPrestacional(
            Connection con, int idRequerimiento) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(
                    "SELECT 1 FROM compras.requerimiento_reclamo_prestacional "
                            + "WHERE id_requerimiento = ?");
            stmt.setInt(1, idRequerimiento);
            rs = stmt.executeQuery();
            return rs.next();
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    private void bloquearRequerimientoYDetalles(
            Connection con, int idRequerimiento) throws Exception {
        CallableStatement bloqueo = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            bloqueo = con.prepareCall(
                    "{call compras.bloquear_requerimiento_reclamo_prestacional(?)}");
            bloqueo.setInt(1, idRequerimiento);
            bloqueo.execute();
            stmt=con.prepareStatement("SELECT rp.id FROM autorizaciones.reclamos_prestacionales rp "
                    + "JOIN compras.requerimiento_reclamo_prestacional rr ON rr.id_reclamo_prestacional=rp.id "
                    + "WHERE rr.id_requerimiento=? ORDER BY rp.id FOR UPDATE OF rp");
            stmt.setInt(1,idRequerimiento);rs=stmt.executeQuery();while(rs.next())rs.getInt(1);
            cerrar(rs);rs=null;ConnectionHelper.cerrar(stmt);
            stmt=con.prepareStatement("SELECT id_requerimiento FROM compras.requerimiento_reclamo_prestacional WHERE id_requerimiento=? FOR UPDATE");
            stmt.setInt(1,idRequerimiento);rs=stmt.executeQuery();while(rs.next())rs.getInt(1);
            cerrar(rs);rs=null;ConnectionHelper.cerrar(stmt);
            stmt = con.prepareStatement(
                    "SELECT id_requerimiento FROM compras.requerimiento "
                            + "WHERE id_requerimiento = ? FOR UPDATE");
            stmt.setInt(1, idRequerimiento);
            rs = stmt.executeQuery();
            while (rs.next()) {
                rs.getInt(1);
            }
            cerrar(rs);
            rs = null;
            ConnectionHelper.cerrar(stmt);
            stmt = con.prepareStatement(
                    "SELECT id_detalle FROM compras.requerimiento_detalle "
                            + "WHERE id_requerimiento = ? AND baja_fecha IS NULL "
                            + "ORDER BY id_detalle FOR UPDATE");
            stmt.setInt(1, idRequerimiento);
            rs = stmt.executeQuery();
            while (rs.next()) {
                rs.getInt(1);
            }
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt);
            ConnectionHelper.cerrar(bloqueo);
        }
    }

    private int guardarConceptoAutomatico(
            Connection con, RequerimientoCompraDetalle detalle,
            String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(
                    "INSERT INTO compras.requerimiento_detalle "
                            + "(id_requerimiento, tipo_item, id_prestacion, "
                            + "id_tipo_nomenclador, codigo_nomenclador, "
                            + "descripcion_nomenclador, cantidad, id_tipo_prestacion, alta_usr) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_detalle");
            stmt.setInt(1, detalle.getIdRequerimientoCompra());
            stmt.setString(2, detalle.getTipoItem());
            setNullableInteger(stmt, 3, detalle.getIdPrestacion());
            setNullableInteger(stmt, 4, detalle.getIdTipoNomenclador());
            stmt.setString(5, detalle.getCodigoNomenclador());
            stmt.setString(6, detalle.getDescripcionNomenclador());
            setNullableInteger(stmt, 7, detalle.getCantidad());
            setNullableInteger(stmt, 8, detalle.getIdTipoPrestacion());
            stmt.setString(9, usuario);
            rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    private int persistirTotalDetalle(
            Connection con, RequerimientoCompraDetalle detalle,
            String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(
                    "UPDATE compras.requerimiento_detalle "
                            + "SET precio_total_estimado = ?, modi_fecha = now(), modi_usr = ? "
                            + "WHERE id_requerimiento = ? AND id_detalle = ? AND baja_fecha IS NULL");
            stmt.setBigDecimal(1, detalle.getPrecioTotalEstimadoInformado());
            stmt.setString(2, usuario);
            stmt.setInt(3, detalle.getIdRequerimientoCompra());
            stmt.setInt(4, detalle.getIdInt());
            return stmt.executeUpdate();
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    private int persistirImportesDetalle(
            Connection con, RequerimientoCompraDetalle detalle,
            String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(
                    "UPDATE compras.requerimiento_detalle "
                            + "SET precio_unitario_estimado = ?, precio_total_estimado = ?, "
                            + "id_prestador = ?, modi_fecha = now(), modi_usr = ? "
                            + "WHERE id_requerimiento = ? AND id_detalle = ? AND baja_fecha IS NULL");
            stmt.setBigDecimal(1, detalle.getPrecioUnitarioEstimado());
            stmt.setBigDecimal(2, detalle.getPrecioTotalEstimadoInformado());
            setNullableInteger(stmt, 3, detalle.getIdPrestador());
            stmt.setString(4, usuario);
            stmt.setInt(5, detalle.getIdRequerimientoCompra());
            stmt.setInt(6, detalle.getIdInt());
            return stmt.executeUpdate();
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    private String construirArrayEnterosPostgreSql(
            Integer[] valores) {

        if (valores == null) {
            return null;
        }

        StringBuilder value = new StringBuilder();
        value.append('{');

        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                value.append(',');
            }

            Integer actual = valores[i];
            value.append(
                    actual != null
                            ? actual.toString()
                            : "NULL"
            );
        }

        value.append('}');
        return value.toString();
    }

    private String construirArrayNumericosPostgreSql(
            BigDecimal[] valores) {

        if (valores == null) {
            return null;
        }

        StringBuilder value = new StringBuilder();
        value.append('{');

        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                value.append(',');
            }

            BigDecimal actual = valores[i];
            value.append(
                    actual != null
                            ? actual.toPlainString()
                            : "NULL"
            );
        }

        value.append('}');
        return value.toString();
    }


    private List<Integer> bloquearPrestacionesComparativaCorreccion(Connection con, int id)
            throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT id_comparativa FROM compras.requerimiento_comparativa "
                    + "WHERE id_requerimiento = ? ORDER BY id_comparativa FOR UPDATE");
            stmt.setInt(1, id); rs = stmt.executeQuery();
            while (rs.next()) { rs.getInt(1); }
        } finally { cerrar(rs); ConnectionHelper.cerrar(stmt); }
        stmt = null; rs = null;
        List<Integer> prestaciones = new java.util.ArrayList<Integer>();
        try {
            stmt = con.prepareStatement("SELECT cd.id_prestacion FROM compras.requerimiento_comparativa_detalle cd "
                    + "JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa "
                    + "WHERE c.id_requerimiento = ? ORDER BY cd.id_detalle FOR UPDATE OF cd");
            stmt.setInt(1, id); rs = stmt.executeQuery();
            while (rs.next()) {
                int prestacion = rs.getInt(1);
                prestaciones.add(rs.wasNull() ? null : Integer.valueOf(prestacion));
            }
            return prestaciones;
        } finally { cerrar(rs); ConnectionHelper.cerrar(stmt); }
    }

    private int actualizarReferenciaComparativaCorreccion(Connection con, int requerimiento,
            Integer origen, int destino, String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("UPDATE compras.requerimiento_comparativa_detalle cd "
                    + "SET id_prestacion = ?, modi_fecha = now(), modi_usr = ? "
                    + "FROM compras.requerimiento_comparativa c WHERE c.id_comparativa = cd.id_comparativa "
                    + "AND c.id_requerimiento = ? AND cd.id_prestacion = ? AND cd.id_prestacion IS DISTINCT FROM ?");
            stmt.setInt(1, destino); stmt.setString(2, usuario); stmt.setInt(3, requerimiento);
            if (origen == null) stmt.setNull(4, Types.INTEGER); else stmt.setInt(4, origen.intValue());
            stmt.setInt(5, destino); return stmt.executeUpdate();
        } finally { ConnectionHelper.cerrar(stmt); }
    }

    private java.util.Map<String, Object> consultarPrestadorCorreccion(Connection con, int id)
            throws Exception {
        PreparedStatement stmt = null; ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT id_prestador,cuit,baja_fecha,solicitar_cotizacion "
                    + "FROM public.prestador WHERE id_prestador = ?");
            stmt.setInt(1, id); rs = stmt.executeQuery();
            if (!rs.next()) return null;
            java.util.Map<String, Object> datos = new java.util.HashMap<String, Object>();
            datos.put("id", Integer.valueOf(rs.getInt("id_prestador")));
            datos.put("cuit", rs.getString("cuit")); datos.put("baja", rs.getTimestamp("baja_fecha"));
            datos.put("solicitar", Boolean.valueOf(rs.getBoolean("solicitar_cotizacion")));
            return datos;
        } finally { cerrar(rs); ConnectionHelper.cerrar(stmt); }
    }

    private List<String> listarRubrosPrestadorCorreccion(Connection con, int id) throws Exception {
        PreparedStatement stmt = null; ResultSet rs = null;
        List<String> rubros = new java.util.ArrayList<String>();
        try {
            stmt = con.prepareStatement("SELECT rubro FROM public.prestador_rubro WHERE id_prestador = ?");
            stmt.setInt(1, id); rs = stmt.executeQuery();
            while (rs.next()) rubros.add(rs.getString("rubro"));
            return rubros;
        } finally { cerrar(rs); ConnectionHelper.cerrar(stmt); }
    }


    public void marcarEmpresaAdjudicada(Connection con, int idRequerimiento,
            int idPresupuesto, String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("UPDATE compras.requerimiento_presupuesto SET empresa_adjudicada = FALSE "
                    + "WHERE id_requerimiento = ? AND tipo_documento = 3 AND empresa_adjudicada");
            stmt.setInt(1, idRequerimiento); stmt.executeUpdate();
            ConnectionHelper.cerrar(stmt); stmt = null;
            if (idPresupuesto > 0) {
                stmt = con.prepareStatement("UPDATE compras.requerimiento_presupuesto SET empresa_adjudicada = TRUE "
                        + "WHERE id_requerimiento_presupuesto = ? AND id_requerimiento = ? AND tipo_documento = 3 AND baja_fecha IS NULL");
                stmt.setInt(1, idPresupuesto); stmt.setInt(2, idRequerimiento);
                if (stmt.executeUpdate() != 1) throw new SQLException("No se pudo marcar la cotizacion adjudicada.");
                ConnectionHelper.cerrar(stmt); stmt = null;
            }
            stmt = con.prepareStatement("UPDATE compras.requerimiento SET modi_fecha = now(), modi_usr = ? WHERE id_requerimiento = ?");
            stmt.setString(1, usuario); stmt.setInt(2, idRequerimiento);
            if (stmt.executeUpdate() != 1) throw new SQLException("No se pudo auditar la adjudicacion de Empresa.");
        } finally { ConnectionHelper.cerrar(stmt); }
    }

    public int insertarDocumento(Connection con, RequerimientoCompraPresupuesto documento,
            String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("INSERT INTO compras.requerimiento_presupuesto "
                    + "(id_requerimiento,tipo_documento,fecha_documento,numero_receta,id_prestador,"
                    + "empresa_cuit,empresa_sucursal,descripcion_empresa,dl_group_id,dl_folder_id,"
                    + "dl_file_entry_id,dl_file_uuid,nombre_original,nombre_persistido,titulo,descripcion_prestador,alta_usr) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id_requerimiento_presupuesto");
            stmt.setInt(1, documento.getIdRequerimiento().intValue());
            stmt.setInt(2, documento.getTipoDocumento().intValue());
            if (documento.getFechaDocumento() == null) {
                stmt.setNull(3, Types.DATE);
            } else {
                stmt.setDate(3, new java.sql.Date(documento.getFechaDocumento().getTime()));
            }
            stmt.setString(4, documento.getNumeroReceta());
            setNullableInteger(stmt, 5, documento.getIdPrestador());
            stmt.setString(6, documento.getEmpresaCuit());
            stmt.setString(7, documento.getEmpresaSucursal());
            stmt.setString(8, documento.getDescripcionEmpresa());
            stmt.setLong(9, documento.getDlGroupId().longValue());
            stmt.setLong(10, documento.getDlFolderId().longValue());
            stmt.setLong(11, documento.getDlFileEntryId().longValue());
            stmt.setString(12, documento.getDlFileUuid());
            stmt.setString(13, documento.getNombreOriginal());
            stmt.setString(14, documento.getNombrePersistido());
            stmt.setString(15, documento.getTitulo());
            stmt.setString(16, documento.getDescripcionPrestador());
            stmt.setString(17, usuario);
            rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public String bloquearEstadoPrestadorCotizacion(Connection con,
            int idRequerimiento, int idPrestador) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT estado_envio FROM compras.requerimiento_cotizacion_prestador "
                    + "WHERE id_requerimiento = ? AND id_prestador = ? FOR UPDATE");
            stmt.setInt(1, idRequerimiento);
            stmt.setInt(2, idPrestador);
            rs = stmt.executeQuery();
            return rs.next() ? rs.getString(1) : null;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public boolean actualizarEstadoPrestadorCotizacion(Connection con,
            int idRequerimiento, int idPrestador, String estadoAnterior,
            String estadoNuevo, String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("UPDATE compras.requerimiento_cotizacion_prestador "
                    + "SET estado_envio = ?, modi_fecha = now(), modi_usr = ? "
                    + "WHERE id_requerimiento = ? AND id_prestador = ? AND estado_envio = ?");
            stmt.setString(1, estadoNuevo);
            stmt.setString(2, usuario);
            stmt.setInt(3, idRequerimiento);
            stmt.setInt(4, idPrestador);
            stmt.setString(5, estadoAnterior);
            return stmt.executeUpdate() == 1;
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    public boolean cambiarBajaDocumento(Connection con, int idRequerimiento,
            int idDocumento, boolean reactivar, boolean limpiarAdjudicacion,
            String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt = null;
        try {
            String sql = reactivar
                    ? "UPDATE compras.requerimiento_presupuesto SET baja_fecha = NULL, baja_usr = NULL "
                    : "UPDATE compras.requerimiento_presupuesto SET baja_fecha = now(), baja_usr = ? "
                            + (limpiarAdjudicacion ? ", empresa_adjudicada = FALSE " : "");
            sql += "WHERE id_requerimiento = ? AND id_requerimiento_presupuesto = ? AND baja_fecha IS "
                    + (reactivar ? "NOT NULL" : "NULL");
            stmt = con.prepareStatement(sql);
            int posicion = 1;
            if (!reactivar) {
                stmt.setString(posicion++, usuario);
            }
            stmt.setInt(posicion++, idRequerimiento);
            stmt.setInt(posicion, idDocumento);
            return stmt.executeUpdate() == 1;
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    public void bloquearClaveReceta(Connection con, int idRequerimiento,
            java.util.Date fecha, String receta) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT pg_advisory_xact_lock(hashtext('compras.orden_medica.receta'),"
                    + "hashtext(r.afiliado_cuil_titular || chr(31) || r.afiliado_int::varchar || chr(31) "
                    + "|| to_char(?::date,'YYYY-MM-DD') || chr(31) || ?)) "
                    + "FROM compras.requerimiento r WHERE r.id_requerimiento = ?");
            stmt.setDate(1, new java.sql.Date(fecha.getTime()));
            stmt.setString(2, receta);
            stmt.setInt(3, idRequerimiento);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("No se encontro la cabecera bloqueada para la clave de receta.");
            }
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public Integer buscarRequerimientoRecetaDuplicada(Connection con,
            int idRequerimiento, java.util.Date fecha, String receta, int estadoExcluido)
            throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT rp.id_requerimiento FROM compras.requerimiento_presupuesto rp "
                    + "JOIN compras.requerimiento r ON r.id_requerimiento = rp.id_requerimiento "
                    + "JOIN compras.requerimiento origen ON origen.id_requerimiento = ? "
                    + "WHERE rp.tipo_documento = ? AND rp.baja_fecha IS NULL AND rp.numero_receta = ? "
                    + "AND rp.fecha_documento = ? AND r.estado <> ? "
                    + "AND r.afiliado_cuil_titular = origen.afiliado_cuil_titular AND r.afiliado_int = origen.afiliado_int "
                    + "ORDER BY rp.id_requerimiento,rp.id_requerimiento_presupuesto LIMIT 1");
            stmt.setInt(1, idRequerimiento);
            stmt.setInt(2, RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_ORDEN_MEDICA);
            stmt.setString(3, receta);
            stmt.setDate(4, new java.sql.Date(fecha.getTime()));
            stmt.setInt(5, estadoExcluido);
            rs = stmt.executeQuery();
            return rs.next() ? Integer.valueOf(rs.getInt(1)) : null;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public boolean actualizarEmpresaDocumento(Connection con, int idRequerimiento,
            int idDocumento, String nombre, String cuit, String sucursal,
            String usuario) throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("UPDATE compras.requerimiento_presupuesto SET descripcion_empresa = ?, "
                    + "empresa_cuit = ?, empresa_sucursal = ? WHERE id_requerimiento = ? "
                    + "AND id_requerimiento_presupuesto = ? AND tipo_documento = ? AND baja_fecha IS NULL");
            stmt.setString(1, nombre);
            stmt.setString(2, cuit);
            stmt.setString(3, sucursal);
            stmt.setInt(4, idRequerimiento);
            stmt.setInt(5, idDocumento);
            stmt.setInt(6, RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_COTIZACION_EMPRESA);
            if (stmt.executeUpdate() != 1) {
                return false;
            }
            ConnectionHelper.cerrar(stmt);
            stmt = con.prepareStatement("UPDATE compras.requerimiento SET modi_fecha = now(), modi_usr = ? WHERE id_requerimiento = ?");
            stmt.setString(1, usuario);
            stmt.setInt(2, idRequerimiento);
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("No se pudo guardar la auditoria de la Empresa.");
            }
            return true;
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    public String resolverUsuarioAuditoria(Connection con,String usuario)throws Exception {
        String normalizado=EditarRequerimientoCompraHelper.normalizarUsuarioAuditoria(usuario);
        if(normalizado!=null)return normalizado;
        PreparedStatement stmt=null;ResultSet rs=null;
        try{
            stmt=con.prepareStatement("SELECT current_user::character varying");rs=stmt.executeQuery();
            if(!rs.next()||rs.getString(1)==null||rs.getString(1).length()==0)
                throw new SQLException("No se pudo obtener la identidad de auditoria de la conexion.");
            return rs.getString(1);
        }finally{cerrar(rs);ConnectionHelper.cerrar(stmt);}
    }

    private int persistirObservacionInterna(Connection con,int id,int estado,String observacion,String usuario)throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt=null;
        try{
            stmt=con.prepareStatement("UPDATE compras.requerimiento SET observacion_interna=?,modi_fecha=now(),modi_usr=? WHERE id_requerimiento=? AND estado=?");
            stmt.setString(1,observacion);stmt.setString(2,usuario);stmt.setInt(3,id);stmt.setInt(4,estado);return stmt.executeUpdate();
        }finally{ConnectionHelper.cerrar(stmt);}
    }

    public int persistirEstado(Connection con,int id,int anterior,int nuevo,String usuario,String motivo,boolean baja)throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt=null;
        try {
            String sql="UPDATE compras.requerimiento SET estado=?,modi_fecha=now(),modi_usr=?";
            if(baja)sql+=",motivo_baja=?,baja_fecha=now(),baja_usr=?";
            sql+=" WHERE id_requerimiento=? AND estado=? AND baja_fecha IS NULL";
            stmt=con.prepareStatement(sql);stmt.setInt(1,nuevo);stmt.setString(2,usuario);int i=3;
            if(baja){stmt.setString(i++,motivo);stmt.setString(i++,usuario);}
            stmt.setInt(i++,id);stmt.setInt(i,anterior);return stmt.executeUpdate();
        }finally{ConnectionHelper.cerrar(stmt);}
    }

    private int persistirSurge(Connection con,int id,int estado,boolean surge,String usuario)throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt=null;
        try{
            stmt=con.prepareStatement("UPDATE compras.requerimiento SET surge=?,modi_fecha=now(),modi_usr=? "
                + "WHERE id_requerimiento=? AND estado=? AND baja_fecha IS NULL");
            stmt.setBoolean(1,surge);stmt.setString(2,usuario);stmt.setInt(3,id);stmt.setInt(4,estado);return stmt.executeUpdate();
        }finally{ConnectionHelper.cerrar(stmt);}
    }

    private int persistirBajaDetalle(Connection con,int id,int detalle,String usuario)throws Exception {
        usuario = resolverUsuarioAuditoria(con, usuario);
        PreparedStatement stmt=null;
        try{
            stmt=con.prepareStatement("UPDATE compras.requerimiento_detalle SET baja_fecha=now(),baja_usr=?,modi_fecha=now(),modi_usr=? "
                + "WHERE id_requerimiento=? AND id_detalle=? AND baja_fecha IS NULL");
            stmt.setString(1,usuario);stmt.setString(2,usuario);stmt.setInt(3,id);stmt.setInt(4,detalle);return stmt.executeUpdate();
        }finally{ConnectionHelper.cerrar(stmt);}
    }

    private java.util.Map<String,Object> obtenerSectorCatalogo(Connection con,int id)throws Exception {
        PreparedStatement stmt=null;ResultSet rs=null;
        try{
            stmt=con.prepareStatement("SELECT descripcion,requiere_afiliado,activo,baja_fecha FROM compras.sector_requerimiento WHERE id_sector=? FOR SHARE");
            stmt.setInt(1,id);rs=stmt.executeQuery();if(!rs.next())return null;
            java.util.Map<String,Object> f=new java.util.HashMap<String,Object>();
            f.put("descripcion",rs.getString("descripcion"));f.put("requiere_afiliado",Boolean.valueOf(rs.getBoolean("requiere_afiliado")));
            f.put("activo",Boolean.valueOf(rs.getBoolean("activo")));f.put("baja",rs.getTimestamp("baja_fecha"));return f;
        }finally{cerrar(rs);ConnectionHelper.cerrar(stmt);}
    }

    private java.util.List<java.util.Map<String,Object>> obtenerRelacionesRp(Connection con,int id)throws Exception {
        PreparedStatement stmt=null;ResultSet rs=null;
        try{
            stmt=con.prepareStatement("SELECT rr.estado,rr.id_reclamo_prestacional,rr.baja_fecha AS vinculo_baja,rp.id AS rp_id,rp.baja_fecha AS rp_baja "
                + "FROM compras.requerimiento_reclamo_prestacional rr LEFT JOIN autorizaciones.reclamos_prestacionales rp "
                + "ON rp.id=rr.id_reclamo_prestacional WHERE rr.id_requerimiento=? ORDER BY rr.id_requerimiento");
            stmt.setInt(1,id);rs=stmt.executeQuery();java.util.List<java.util.Map<String,Object>> filas=new java.util.ArrayList<java.util.Map<String,Object>>();
            while(rs.next()){
                java.util.Map<String,Object> f=new java.util.HashMap<String,Object>();f.put("estado",rs.getString("estado"));
                f.put("id",Integer.valueOf(rs.getInt("id_reclamo_prestacional")));f.put("rp_id",Integer.valueOf(rs.getInt("rp_id")));
                f.put("baja",rs.getTimestamp("rp_baja"));f.put("vinculo_baja",rs.getTimestamp("vinculo_baja"));filas.add(f);
            }return filas;
        }finally{cerrar(rs);ConnectionHelper.cerrar(stmt);}
    }

    public static final class Transaccion {

        private final EditarRequerimientoCompraServiceImpl service;
        private Connection con;

        private Transaccion(
                EditarRequerimientoCompraServiceImpl service,
                Connection con) {

            this.service = service;
            this.con = con;
        }

        public int guardarRequerimientoCompra(
                RequerimientoCompra requerimiento,
                String usuario) throws Exception {

            return service.guardarRequerimientoCompra(
                    con,
                    requerimiento,
                    usuario
            );
        }

        public int guardarDetalle(
                RequerimientoCompraDetalle detalle,
                String usuario) throws Exception {

            return service.guardarDetalle(con, detalle, usuario);
        }

        public int registrarPresupuesto(RequerimientoCompraPresupuesto presupuesto,String usuario)throws Exception {
            return new ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper().registrarAsociacionEnTransaccion(
                    new EditarRequerimientoCompraServiceUtil.Transaccion(this),presupuesto,usuario);
        }

        public void guardarEmpresaAdjudicada(
                int idRequerimientoCompra,
                String empresaCuit,
                String empresaSucursal,
                String usuario) throws Exception {

            service.guardarEmpresaAdjudicada(
                    con, idRequerimientoCompra, empresaCuit,
                    empresaSucursal, usuario);
        }

        public void guardarEmpresaAdjudicada(
                int idRequerimientoCompra,
                int idRequerimientoPresupuesto,
                String usuario) throws Exception {

            service.guardarEmpresaAdjudicada(
                    con, idRequerimientoCompra, idRequerimientoPresupuesto,
                    usuario);
        }

        public int registrarOrdenMedica(
                int idRequerimiento,
                OrdenMedicaValidada ordenMedica,
                DocumentoComprasCreado documento,
                String usuario) throws Exception {

            return service.registrarOrdenMedica(
                    con,
                    idRequerimiento,
                    ordenMedica,
                    documento,
                    usuario
            );
        }

        public boolean tieneCreacionReclamoPrestacional(int idRequerimiento) throws Exception {
            return service.tieneCreacionReclamoPrestacional(con, idRequerimiento);
        }

        public boolean existeRequerimientoDuplicado(String cuilTitular, int inte,
                int idPrestacion, java.util.Date fechaOrdenMedica,
                int idRequerimientoExcluir) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.existeRequerimientoDuplicado(
                    con, cuilTitular, inte, idPrestacion, fechaOrdenMedica,
                    idRequerimientoExcluir);
        }

        public int persistirObservacionInterna(int id,int estado,String observacion,String usuario)throws Exception {
            return service.persistirObservacionInterna(con,id,estado,observacion,usuario);
        }
        public int persistirEstado(int id,int anterior,int nuevo,String usuario,String motivo,boolean baja)throws Exception {
            return service.persistirEstado(con,id,anterior,nuevo,usuario,motivo,baja);
        }
        public int persistirSurge(int id,int estado,boolean surge,String usuario)throws Exception {
            return service.persistirSurge(con,id,estado,surge,usuario);
        }
        public int persistirBajaDetalle(int id,int detalle,String usuario)throws Exception {
            return service.persistirBajaDetalle(con,id,detalle,usuario);
        }
        public java.util.List<java.util.Map<String,Object>> listarCatalogoSectores()throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.listarCatalogoSectores(con);
        }
        public java.util.Map<String,Object> obtenerSectorCatalogo(int id)throws Exception {
            return service.obtenerSectorCatalogo(con,id);
        }
        public java.util.List<java.util.Map<String,Object>> obtenerRelacionesRp(int id)throws Exception {
            return service.obtenerRelacionesRp(con,id);
        }

        public RequerimientoCompra bloquearYObtenerRequerimiento(
                int idRequerimiento) throws Exception {
            service.bloquearRequerimientoYDetalles(con, idRequerimiento);
            return BusquedaRequerimientoCompraServiceUtil
                    .getRequerimientoCompra(con, idRequerimiento);
        }

        public List<RequerimientoCompraDetalle> getDetalles(
                int idRequerimiento) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.getDetalles(con, idRequerimiento);
        }

        public List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(int idRequerimiento) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.listarOrdenesMedicas(con, idRequerimiento);
        }

        public List<RequerimientoCompraPresupuesto> listarPresupuestos(int idRequerimiento) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.listarPresupuestos(con, idRequerimiento);
        }

        public List<TipoPrestacionCompra> listarTiposPrestacion() throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.listarTiposPrestacion(con);
        }

        public List<RequerimientoCompraSector> listarSectores() throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.listarSectores(con);
        }

        public List<SectorNomencladorCompra> listarAsociacionesNomenclador(
                int idSector) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil
                    .listarAsociacionesNomenclador(con, idSector);
        }

        public Nomenclador obtenerNomencladorCanonico(int idPrestacion) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil
                    .obtenerNomencladorCanonico(con, idPrestacion);
        }

        public String getConfiguracionCompra(String clave) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.getConfiguracionCompra(clave, con);
        }

        public List<Integer> bloquearPrestacionesComparativaCorreccion(int id) throws Exception {
            return service.bloquearPrestacionesComparativaCorreccion(con, id);
        }
        public int actualizarReferenciaComparativaCorreccion(int id, Integer origen,
                int destino, String usuario) throws Exception {
            return service.actualizarReferenciaComparativaCorreccion(con, id, origen, destino, usuario);
        }
        public java.util.Map<String, Object> consultarPrestadorCorreccion(int id) throws Exception {
            return service.consultarPrestadorCorreccion(con, id);
        }
        public List<String> listarRubrosPrestadorCorreccion(int id) throws Exception {
            return service.listarRubrosPrestadorCorreccion(con, id);
        }

        public int corregirItem(RequerimientoCompraDetalle detalle,
                String usuario) throws Exception {
            return service.corregirItem(con, detalle, usuario);
        }

        public int guardarConceptoAutomatico(RequerimientoCompraDetalle detalle,
                String usuario) throws Exception {
            return service.guardarConceptoAutomatico(con, detalle, usuario);
        }

        public int persistirTotalDetalle(RequerimientoCompraDetalle detalle,
                String usuario) throws Exception {
            return service.persistirTotalDetalle(con, detalle, usuario);
        }

        public int persistirImportesDetalle(RequerimientoCompraDetalle detalle,
                String usuario) throws Exception {
            return service.persistirImportesDetalle(con, detalle, usuario);
        }

        public int guardarCotizacion(int id,Integer[] ids,BigDecimal[] precios,Integer[] eliminados,
                Integer prestador,boolean surge,String usuario)throws Exception {
            return new EditarRequerimientoCompraHelper().guardarCotizacionEnTransaccion(
                    new EditarRequerimientoCompraServiceUtil.Transaccion(this),id,ids,precios,eliminados,prestador,Boolean.valueOf(surge),usuario);
        }

        public RequerimientoCompraPresupuesto bloquearDocumento(int idRequerimiento, int idDocumento) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.bloquearDocumento(con, idRequerimiento, idDocumento);
        }
        public java.util.List<ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion> listarPrestadoresEnviados(int id,int limite)throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.listarPrestadoresEnviados(con,id,limite);
        }
        public List<RequerimientoCompraPresupuesto> listarDocumentos(int idRequerimiento, int tipoDocumento) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.listarDocumentos(con, idRequerimiento, tipoDocumento);
        }
        public java.util.List<ar.com.ospim.global.beans.Empresa> buscarEmpresasCotizacion(String cuit, String sucursal) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.buscarEmpresasCotizacion(con, cuit, null, sucursal, 2);
        }
        public void marcarEmpresaAdjudicada(int idRequerimiento, int idPresupuesto, String usuario) throws Exception {
            service.marcarEmpresaAdjudicada(con, idRequerimiento, idPresupuesto, usuario);
        }

        public int insertarDocumento(RequerimientoCompraPresupuesto documento, String usuario) throws Exception {
            return service.insertarDocumento(con, documento, usuario);
        }
        public String bloquearEstadoPrestadorCotizacion(int idRequerimiento, int idPrestador) throws Exception {
            return service.bloquearEstadoPrestadorCotizacion(con, idRequerimiento, idPrestador);
        }
        public boolean actualizarEstadoPrestadorCotizacion(int idRequerimiento, int idPrestador, String anterior, String nuevo, String usuario) throws Exception {
            return service.actualizarEstadoPrestadorCotizacion(con, idRequerimiento, idPrestador, anterior, nuevo, usuario);
        }
        public boolean cambiarBajaDocumento(int idRequerimiento, int idDocumento, boolean reactivar, boolean limpiarAdjudicacion, String usuario) throws Exception {
            return service.cambiarBajaDocumento(con, idRequerimiento, idDocumento, reactivar, limpiarAdjudicacion, usuario);
        }
        public void bloquearClaveReceta(int idRequerimiento, java.util.Date fecha, String receta) throws Exception {
            service.bloquearClaveReceta(con, idRequerimiento, fecha, receta);
        }
        public Integer buscarRequerimientoRecetaDuplicada(int idRequerimiento, java.util.Date fecha, String receta, int estadoExcluido) throws Exception {
            return service.buscarRequerimientoRecetaDuplicada(con, idRequerimiento, fecha, receta, estadoExcluido);
        }

        public void guardarDatosComparativas(
                java.util.List<ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa> comparativas,
                java.util.List<Integer> vaciados, String usuario) throws Exception {
            RequerimientoCompraComparativaServiceUtil.guardarDatos(con, comparativas, vaciados, usuario);
        }

        public boolean actualizarEmpresaDocumento(int idRequerimiento, int idDocumento,
                String nombre, String cuit, String sucursal, String usuario) throws Exception {
            return service.actualizarEmpresaDocumento(con, idRequerimiento, idDocumento, nombre, cuit, sucursal, usuario);
        }

        public void commit() throws Exception {
            con.commit();
        }

        public void rollback() throws Exception {
            con.rollback();
        }

        public void cerrar() {
            Connection actual = con;
            con = null;
            ConnectionHelper.cerrar(actual);
        }
    }

    private void setNullableInteger(
            PreparedStatement stmt,
            int index,
            Integer value) throws SQLException {

        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value.intValue());
        }
    }


    private void cerrar(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception ignored) {
        }
    }
}
