package ar.com.ospim.compras.requerimientos.service;

import ar.com.empresas.beans.Contacto;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.TelefonoServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion;
import ar.com.ospim.liquidaciones.beans.TelefonoPrestador;
import ar.com.ospim.liquidaciones.beans.ContactoElectronicoPrestador;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import ar.com.ospim.util.ConnectionHelper;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RequerimientoCompraOrdenServiceImpl {

    /** Enriquece la consulta con los padrones existentes, sin copiar sus datos a la OC. */
    public void completarDatosConsulta(RequerimientoCompraOrden orden,
            RequerimientoCompra requerimiento) throws Exception {
        if (orden == null || requerimiento == null
                || orden.getIdRequerimientoCompra() != requerimiento.getIdRequerimientoCompra()) {
            throw new IllegalArgumentException("No se pudo identificar el requerimiento de la Orden de Compra.");
        }
        if (!requerimiento.esSectorSinCotizacionPrestador()
                && requerimiento.tieneAfiliadoInformado()) {
            completarAfiliado(orden, requerimiento);
        }
        RequerimientoCompraPresupuesto origen = obtenerOrigenProveedor(orden);
        if (requerimiento.esSectorSinCotizacionPrestador()) {
            if (normalizar(origen.getEmpresaCuit()) != null
                    && normalizar(origen.getEmpresaSucursal()) != null) {
                Empresa empresa = buscarEmpresa(origen.getEmpresaCuit(), origen.getEmpresaSucursal());
                if (empresa != null && (!origen.getEmpresaCuit().trim().equals(normalizar(empresa.getCuit()))
                        || !origen.getEmpresaSucursal().trim().equals(normalizar(empresa.getSucursal())))) {
                    throw new IllegalArgumentException("La empresa consultada no coincide con la cotizacion de origen.");
                }
                completarEmpresa(orden, empresa);
            }
        } else if (origen.getIdPrestador() != null
                && origen.getIdPrestador().intValue() > 0) {
            Prestador prestador = buscarPrestador(origen.getIdPrestador().intValue());
            if (prestador != null && prestador.getId_prestador() != origen.getIdPrestador().intValue()) {
                throw new IllegalArgumentException("El prestador consultado no coincide con la cotizacion de origen.");
            }
            completarPrestador(orden, prestador);
        }
    }

    private void completarAfiliado(RequerimientoCompraOrden orden,
            RequerimientoCompra r) throws Exception {
        Afiliado afiliado = buscarAfiliado(r.getAfiliadoCuilTitular(), r.getAfiliadoInt().intValue());
        validarIdentidadAfiliado(afiliado, r.getAfiliadoCuilTitular(), r.getAfiliadoInt().intValue());
        if (afiliado == null) {
            return;
        }
        if (normalizar(afiliado.getNombrePlan()) != null) {
            orden.setPlan(afiliado.getNombrePlan().trim());
        }
        if (afiliado.getId_ospim() > 0) {
            r.setAfiliadoIdOspim(afiliado.getId_ospim());
        }
        if (normalizar(afiliado.getNombre()) != null) {
            r.setAfiliadoNombre(afiliado.getNombre());
        }
        if (normalizar(afiliado.getApellido()) != null) {
            r.setAfiliadoApellido(afiliado.getApellido());
        }
        if (normalizar(afiliado.getNombre()) != null || normalizar(afiliado.getApellido()) != null) {
            r.setAfiliadoNombreApellido(null);
        }
        if (normalizar(afiliado.getDocumento_tipo()) != null) {
            r.setAfiliadoDocumentoTipo(afiliado.getDocumento_tipo());
        }
        if (normalizar(afiliado.getDocu_numero()) != null) {
            r.setAfiliadoDocumentoNro(afiliado.getDocu_numero());
        }
        if (normalizar(afiliado.getDocumento_tipo()) != null || normalizar(afiliado.getDocu_numero()) != null) {
            r.setAfiliadoDocumento(null);
        }
        String email = normalizar(afiliado.getEmail());
        if (email == null && r.getAfiliadoInt().intValue() != 0) {
            Afiliado titular = buscarAfiliadoOpcional(r.getAfiliadoCuilTitular(), 0);
            validarIdentidadAfiliado(titular, r.getAfiliadoCuilTitular(), 0);
            email = titular != null ? normalizar(titular.getEmail()) : null;
        }
        if (email != null) {
            r.setAfiliadoEmail(email);
        }
        Domicilio domicilio = primerDomicilioVigente(buscarDomicilios(
                r.getAfiliadoCuilTitular(), r.getAfiliadoInt().intValue()));
        if (domicilio == null && r.getAfiliadoInt().intValue() != 0) {
            domicilio = primerDomicilioVigente(buscarDomicilios(r.getAfiliadoCuilTitular(), 0));
        }
        if (domicilio != null) {
            if (normalizar(direccion(domicilio)) != null) {
                r.setAfiliadoDireccion(direccion(domicilio));
            }
            if (normalizar(domicilio.getLocalidadAsString()) != null) {
                r.setAfiliadoLocalidad(domicilio.getLocalidadAsString());
            }
            if (normalizar(domicilio.getProvinciaAsString()) != null) {
                r.setAfiliadoProvincia(domicilio.getProvinciaAsString());
            }
            if (normalizar(domicilio.getTelefono()) != null) {
                r.setAfiliadoTelefono(unir(domicilio.getCod_area_telefono(), domicilio.getTelefono()));
            }
            if (normalizar(domicilio.getCelular()) != null) {
                r.setAfiliadoCelular(unir(domicilio.getCod_area_celular(), domicilio.getCelular()));
            }
        }
        int parentesco = afiliado.getId_parentesco();
        int inteTelefono = parentesco == WebKeysAfiliados.PARENTESCO_DEFAULT
                || parentesco == WebKeysAfiliados.CONYUGE_DEFAULT
                || parentesco == WebKeysAfiliados.CONCUBINO_DEFAULT
                ? r.getAfiliadoInt().intValue() : 0;
        Set<String> numeros = new LinkedHashSet<String>();
        agregarTelefonos(numeros, buscarTelefonos(r.getAfiliadoCuilTitular(), inteTelefono));
        if (numeros.isEmpty() && inteTelefono != 0) {
            agregarTelefonos(numeros, buscarTelefonos(r.getAfiliadoCuilTitular(), 0));
        }
        if (!numeros.isEmpty()) {
            r.setAfiliadoTelefono(listar(numeros));
        }
    }

    private void validarIdentidadAfiliado(Afiliado afiliado, String cuilTitular, int inte) {
        if (afiliado != null && (!cuilTitular.trim().equals(normalizar(afiliado.getCuil_titular()))
                || afiliado.getInte() != inte)) {
            throw new IllegalArgumentException("El afiliado consultado no coincide con el requerimiento.");
        }
    }

    private Domicilio primerDomicilioVigente(List<Domicilio> domicilios) {
        if (domicilios != null) {
            for (Domicilio domicilio : domicilios) {
                if (domicilio != null && domicilio.getBaja_fecha() == null) {
                    return domicilio;
                }
            }
        }
        return null;
    }

    protected Afiliado buscarAfiliado(String cuilTitular, int inte) throws Exception {
        return buscarAfiliadoOpcional(cuilTitular, inte);
    }

    protected Afiliado buscarAfiliadoOpcional(String cuilTitular, int inte) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall("{call buscar_afiliados_componente(?,?,?,?,?,?,?,?,?,?,?)}");
            stmt.setString(1, cuilTitular);
            stmt.setInt(2, inte);
            stmt.setNull(3, Types.VARCHAR);
            stmt.setNull(4, Types.VARCHAR);
            stmt.setNull(5, Types.INTEGER);
            stmt.setNull(6, Types.VARCHAR);
            stmt.setNull(7, Types.VARCHAR);
            // BusquedaAfiliadoServiceUtil convierte O.S.P.I.M. al indice SQL 0.
            stmt.setInt(8, WebKeysGlobal.ENTIDADES_UOMA_INDICES[0]);
            stmt.setNull(9, Types.INTEGER);
            stmt.setNull(10, Types.INTEGER);
            stmt.setNull(11, Types.INTEGER);
            ResultSet rs = stmt.executeQuery();
            try {
                if (!rs.next()) {
                    return null;
                }
                Afiliado afiliado = new Afiliado();
                afiliado.setCuil_titular(rs.getString("cuil"));
                afiliado.setInte(rs.getInt("inte"));
                afiliado.setId_parentesco(rs.getInt("id_parentesco_sss"));
                afiliado.setId_ospim(rs.getInt("id_ospim"));
                afiliado.setNombre(rs.getString("nombre"));
                afiliado.setApellido(rs.getString("apellido"));
                afiliado.setDocumento_tipo(rs.getString("tdoc"));
                afiliado.setDocu_numero(rs.getString("documento"));
                afiliado.setUltimo_plan(new Plan(rs.getInt("id_plan"), rs.getString("nombre_plan")));
                afiliado.setEmail(rs.getString("email"));
                validarIdentidadAfiliado(afiliado, cuilTitular, inte);
                if (rs.next()) {
                    throw new IllegalArgumentException("La consulta del afiliado de la Orden de Compra no es unica.");
                }
                return afiliado;
            } finally {
                rs.close();
            }
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    protected List<Domicilio> buscarDomicilios(String cuilTitular, int inte) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        List<Domicilio> domicilios = new ArrayList<Domicilio>();
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall("{call busca_afiliado_domicilio(?,?)}");
            stmt.setString(1, cuilTitular);
            stmt.setInt(2, inte);
            ResultSet rs = stmt.executeQuery();
            try {
                while (rs.next()) {
                    domicilios.add(Domicilio.getMapping(rs, "afidom_"));
                }
            } finally {
                rs.close();
            }
            return domicilios;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    protected List<Telefono> buscarTelefonos(String cuilTitular, int inte) throws Exception {
        return TelefonoServiceUtil.getTelefonos(cuilTitular, inte);
    }

    protected Prestador buscarPrestador(int idPrestador) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall("{call buscar_prestador_by_id(?)}");
            stmt.setInt(1, idPrestador);
            ResultSet rs = stmt.executeQuery();
            Prestador prestador = null;
            try {
                if (rs.next()) {
                    prestador = new Prestador(rs.getInt("prs__id_prestador"));
                    prestador.setContacto(rs.getString("prs__contacto"));
                    if (prestador.getId_prestador() != idPrestador || rs.next()) {
                        throw new IllegalArgumentException("La consulta del prestador de origen no es unica o no coincide.");
                    }
                }
            } finally {
                rs.close();
            }
            if (prestador != null) {
                List<PrestadorLugarAtencion> lugares = buscarLugaresPrestador(con, idPrestador);
                for (PrestadorLugarAtencion lugar : lugares) {
                    if (lugar.getId_prestador() != idPrestador) {
                        throw new IllegalArgumentException("El lugar de atencion no pertenece al prestador de origen.");
                    }
                    if (lugar.getBajaFecha() == null) {
                        lugar.setTelefonos(buscarTelefonosPrestador(con, idPrestador, lugar.getId_domicilio()));
                        lugar.setContactosElectronicos(buscarContactosPrestador(con, idPrestador, lugar.getId_domicilio()));
                    }
                }
                prestador.setLugaresAtencion(lugares);
            }
            return prestador;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private List<PrestadorLugarAtencion> buscarLugaresPrestador(Connection con, int idPrestador)
            throws SQLException {
        CallableStatement stmt = con.prepareCall("{call buscar_lugares_atencion_del_prestador_by_id(?)}");
        try {
            stmt.setInt(1, idPrestador);
            ResultSet rs = stmt.executeQuery();
            try {
                List<PrestadorLugarAtencion> lugares = new ArrayList<PrestadorLugarAtencion>();
                while (rs.next()) {
                    lugares.add(PrestadorLugarAtencion.getMappingConDomicilio("pla_", rs));
                }
                return lugares;
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    private List<TelefonoPrestador> buscarTelefonosPrestador(Connection con, int idPrestador,
            int idDomicilio) throws SQLException {
        CallableStatement stmt = con.prepareCall("{call buscar_telefonos_prestador(?,?)}");
        try {
            stmt.setInt(1, idPrestador);
            stmt.setInt(2, idDomicilio);
            ResultSet rs = stmt.executeQuery();
            try {
                List<TelefonoPrestador> telefonos = new ArrayList<TelefonoPrestador>();
                while (rs.next()) {
                    telefonos.add(TelefonoPrestador.getMapping(rs));
                }
                return telefonos;
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    private List<ContactoElectronicoPrestador> buscarContactosPrestador(Connection con,
            int idPrestador, int idDomicilio) throws SQLException {
        CallableStatement stmt = con.prepareCall("{call buscar_contactos_prestador(?,?)}");
        try {
            stmt.setInt(1, idPrestador);
            stmt.setInt(2, idDomicilio);
            ResultSet rs = stmt.executeQuery();
            try {
                List<ContactoElectronicoPrestador> contactos = new ArrayList<ContactoElectronicoPrestador>();
                while (rs.next()) {
                    contactos.add(ContactoElectronicoPrestador.getMapping(rs));
                }
                return contactos;
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    protected Empresa buscarEmpresa(String cuit, String sucursal) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall("{call informacion_afip.buscar_datos_empresa(?,?)}");
            stmt.setString(1, cuit);
            stmt.setString(2, sucursal);
            ResultSet rs = stmt.executeQuery();
            Empresa empresa = null;
            try {
                if (rs.next()) {
                    empresa = new Empresa();
                    empresa.setCuit(rs.getString("cuit"));
                    empresa.setSucursal(rs.getString("sucursal"));
                    empresa.setContacto(rs.getString("contacto"));
                    if (!cuit.trim().equals(normalizar(empresa.getCuit()))
                            || !sucursal.trim().equals(normalizar(empresa.getSucursal())) || rs.next()) {
                        throw new IllegalArgumentException("La consulta de la empresa de origen no es unica o no coincide.");
                    }
                }
            } finally {
                rs.close();
            }
            if (empresa != null) {
                empresa.setDomicilios(EmpresaServiceUtil.getInstance().buscaDomicilios(cuit, sucursal, con, stmt));
                List<Contacto> contactos = new ArrayList<Contacto>();
                EmpresaServiceUtil.getInstance().agregarTelefonos(cuit, sucursal, contactos, con, stmt);
                EmpresaServiceUtil.getInstance().agregarContactosE(cuit, sucursal, contactos, con, stmt);
                empresa.setContactos(contactos);
            }
            return empresa;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    protected RequerimientoCompraPresupuesto obtenerOrigenProveedor(RequerimientoCompraOrden orden)
            throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = ConnectionHelper.getConnection();
            String sql = "SELECT p.id_prestador, p.empresa_cuit, p.empresa_sucursal "
                    + "FROM compras.requerimiento_presupuesto p ";
            if (orden.getIdOrdenCompra() > 0) {
                sql += "JOIN compras.requerimiento_orden_compra o ON o.id_presupuesto = p.id_requerimiento_presupuesto "
                        + "AND o.id_requerimiento = p.id_requerimiento "
                        + "WHERE o.id_orden_compra = ? AND o.id_requerimiento = ?";
            } else {
                sql += "WHERE p.id_requerimiento_presupuesto = ? AND p.id_requerimiento = ?";
            }
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, orden.getIdOrdenCompra() > 0 ? orden.getIdOrdenCompra()
                    : new RequerimientoCompraOrdenHelper().obtenerPresupuestoOrigenConsulta(orden.getIdRequerimientoCompra()));
            stmt.setInt(2, orden.getIdRequerimientoCompra());
            ResultSet rs = stmt.executeQuery();
            try {
                if (!rs.next()) {
                    throw new IllegalArgumentException("No se pudo obtener la cotizacion de origen de la Orden de Compra.");
                }
                RequerimientoCompraPresupuesto origen = new RequerimientoCompraPresupuesto();
                int idPrestador = rs.getInt("id_prestador");
                origen.setIdPrestador(rs.wasNull() ? null : Integer.valueOf(idPrestador));
                origen.setEmpresaCuit(rs.getString("empresa_cuit"));
                origen.setEmpresaSucursal(rs.getString("empresa_sucursal"));
                if (rs.next()) {
                    throw new IllegalArgumentException("La cotizacion de origen de la Orden de Compra no es unica.");
                }
                return origen;
            } finally {
                rs.close();
            }
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private void completarPrestador(RequerimientoCompraOrden orden, Prestador prestador) {
        if (prestador == null) {
            return;
        }
        Set<String> domicilios = new LinkedHashSet<String>();
        Set<String> telefonos = new LinkedHashSet<String>();
        Set<String> correos = new LinkedHashSet<String>();
        agregar(domicilios, direccionCompleta(prestador.getDomicilio()));
        agregarTelefonos(telefonos, prestador.getTelefonos());
        agregarCorreos(correos, prestador.getContactosElectronicos());
        List<PrestadorLugarAtencion> lugares = prestador.getLugaresAtencion();
        if (lugares != null) {
            for (PrestadorLugarAtencion lugar : lugares) {
                if (lugar != null && lugar.getBajaFecha() == null) {
                    String etiqueta = normalizar(lugar.getNombre());
                    agregar(domicilios, etiquetar(etiqueta, direccionCompleta(lugar.getDomicilio())));
                    if (lugar.getTelefonos() != null) {
                        for (Telefono telefono : lugar.getTelefonos()) {
                            agregar(telefonos, etiquetar(etiqueta, telefono(telefono)));
                        }
                    }
                    if (lugar.getContactosElectronicos() != null) {
                        for (ContactoElectronico contacto : lugar.getContactosElectronicos()) {
                            if (contacto != null && contacto.getBajaFecha() == null
                                    && ContactoElectronico.Tipo.EMAIL.equals(contacto.getTipo())) {
                                agregar(correos, etiquetar(etiqueta, contacto.getContacto()));
                            }
                        }
                    }
                }
            }
        }
        if (normalizar(prestador.getContacto()) != null) {
            orden.setProveedorContacto(prestador.getContacto().trim());
        }
        aplicarContactosProveedor(orden, domicilios, telefonos, correos);
    }

    private void completarEmpresa(RequerimientoCompraOrden orden, Empresa empresa) {
        if (empresa == null) {
            return;
        }
        Set<String> domicilios = new LinkedHashSet<String>();
        Set<String> telefonos = new LinkedHashSet<String>();
        Set<String> correos = new LinkedHashSet<String>();
        Set<String> personas = new LinkedHashSet<String>();
        agregar(personas, empresa.getContacto());
        if (empresa.getDomicilios() != null) {
            for (Domicilio domicilio : empresa.getDomicilios()) {
                if (domicilio != null && domicilio.getBaja_fecha() == null) {
                    agregar(domicilios, etiquetar(domicilio.getDomi_tipo(), direccionCompleta(domicilio)));
                }
            }
        }
        if (empresa.getContactos() != null) {
            for (Contacto contacto : empresa.getContactos()) {
                if (contacto != null && contacto.getBajaFecha() == null
                        && (contacto.getTelefono() == null || contacto.getTelefono().getBajaFecha() == null)
                        && (contacto.getContacto() == null || contacto.getContacto().getBajaFecha() == null)) {
                    String persona = unir(contacto.getNombreApe(), contacto.getCargo());
                    agregar(personas, persona);
                    agregar(telefonos, etiquetar(persona, telefono(contacto.getTelefono())));
                    ContactoElectronico correo = contacto.getContacto();
                    if (correo != null && correo.getBajaFecha() == null
                            && ContactoElectronico.Tipo.EMAIL.equals(correo.getTipo())) {
                        agregar(correos, etiquetar(persona, correo.getContacto()));
                    }
                }
            }
        }
        if (!personas.isEmpty()) {
            orden.setProveedorContacto(listar(personas));
        }
        aplicarContactosProveedor(orden, domicilios, telefonos, correos);
    }

    private void aplicarContactosProveedor(RequerimientoCompraOrden orden, Set<String> domicilios,
            Set<String> telefonos, Set<String> correos) {
        if (!domicilios.isEmpty()) {
            orden.setProveedorDomicilio(listar(domicilios));
        }
        if (!telefonos.isEmpty()) {
            orden.setProveedorTelefono(listar(telefonos));
        }
        if (!correos.isEmpty()) {
            orden.setProveedorEmail(listar(correos));
        }
    }

    private void agregarTelefonos(Set<String> destino, List<Telefono> telefonos) {
        if (telefonos != null) {
            for (Telefono telefono : telefonos) {
                agregar(destino, telefono(telefono));
            }
        }
    }

    private void agregarCorreos(Set<String> destino, List<ContactoElectronico> contactos) {
        if (contactos != null) {
            for (ContactoElectronico contacto : contactos) {
                if (contacto != null && contacto.getBajaFecha() == null
                        && ContactoElectronico.Tipo.EMAIL.equals(contacto.getTipo())) {
                    agregar(destino, contacto.getContacto());
                }
            }
        }
    }

    private String direccion(Domicilio domicilio) {
        if (domicilio == null) {
            return null;
        }
        String texto = unir(domicilio.getCalle(), domicilio.getNumero());
        texto = unir(texto, prefijar("Piso", domicilio.getPiso()));
        texto = unir(texto, prefijar("Dto.", domicilio.getDepto()));
        texto = unir(texto, prefijar("Of.", domicilio.getOficina()));
        return unir(texto, domicilio.getPostal_codi());
    }

    private String direccionCompleta(Domicilio domicilio) {
        return domicilio == null || domicilio.getBaja_fecha() != null ? null : unir(unir(direccion(domicilio),
                domicilio.getLocalidadAsString()), domicilio.getProvinciaAsString());
    }

    private String telefono(Telefono telefono) {
        return telefono == null || telefono.getBajaFecha() != null
                || normalizar(telefono.getNumero()) == null ? null
                : unir(unir(unir(telefono.getCodigoPais(), telefono.getCodigoArea()),
                        telefono.getNumero()), prefijar("Int.", telefono.getExtension()));
    }

    private String normalizar(String texto) {
        String valor = WebKeysCompras.trimToNull(texto);
        return valor != null && !"null".equalsIgnoreCase(valor) ? valor : null;
    }

    private String unir(String izquierda, String derecha) {
        String a = normalizar(izquierda);
        String b = normalizar(derecha);
        return a == null ? b : b == null ? a : a + " " + b;
    }

    private String prefijar(String prefijo, String valor) {
        return normalizar(valor) == null ? null : prefijo + " " + valor.trim();
    }

    private String etiquetar(String etiqueta, String valor) {
        return normalizar(valor) == null ? null : normalizar(etiqueta) == null
                ? valor.trim() : etiqueta.trim() + ": " + valor.trim();
    }

    private void agregar(Set<String> destino, String valor) {
        if (normalizar(valor) != null) {
            destino.add(valor.trim());
        }
    }

    private String listar(Set<String> valores) {
        StringBuilder texto = new StringBuilder();
        for (String valor : valores) {
            if (texto.length() > 0) {
                texto.append(" / ");
            }
            texto.append(valor);
        }
        return normalizar(texto.toString());
    }

    public RequerimientoCompraOrden obtenerPorRequerimiento(int idRequerimiento)
            throws Exception {
        Connection con = null;
        try {
            con = ConnectionHelper.getConnection();
            return obtenerPorRequerimiento(con, idRequerimiento);
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    private RequerimientoCompraOrden obtenerPorRequerimiento(Connection con,
            int idRequerimiento) throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT * FROM compras.requerimiento_orden_compra_consulta "
                + "WHERE id_requerimiento = ?");
        RequerimientoCompraOrden orden = null;
        try {
            stmt.setInt(1, idRequerimiento);
            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) {
                    orden = new RequerimientoCompraOrden();
                    orden.setIdOrdenCompra(rs.getInt("id_orden_compra"));
                    orden.setIdRequerimientoCompra(rs.getInt("id_requerimiento"));
                    orden.setAltaUsr(rs.getString("alta_usr"));
                    orden.setModiFecha(rs.getTimestamp("modi_fecha"));
                    orden.setModiUsr(rs.getString("modi_usr"));
                    orden.setVersionEdicion(rs.getInt("version_edicion"));
                    orden.setFechaEmision(rs.getDate("fecha_emision"));
                    orden.setFechaOrdenMedica(rs.getDate("fecha_orden_medica"));
                    orden.setFechaRecepcion(rs.getDate("fecha_recepcion"));
                    orden.setMoneda(rs.getString("moneda"));
                    orden.setProveedor(rs.getString("proveedor"));
                    orden.setProveedorCuit(rs.getString("proveedor_cuit"));
                    orden.setProveedorDomicilio(rs.getString("proveedor_domicilio"));
                    orden.setProveedorContacto(rs.getString("proveedor_contacto"));
                    orden.setProveedorEmail(rs.getString("proveedor_email"));
                    orden.setProveedorTelefono(rs.getString("proveedor_telefono"));
                    orden.setCondicion(rs.getString("condicion"));
                    orden.setFormaPago(rs.getString("forma_pago"));
                    orden.setPlazoPago(rs.getString("plazo_pago"));
                    orden.setEntrega(rs.getString("entrega"));
                    orden.setObservaciones(rs.getString("observaciones"));
                    orden.setConfeccionadoPor(rs.getString("confeccionado_por"));
                    orden.setTelefonoCompras(rs.getString("telefono_compras"));
                    orden.setSolicitante(rs.getString("solicitante"));
                    orden.setPlan(rs.getString("plan"));
                    orden.setIdReclamoPrestacional(rs.getInt("id_reclamo_prestacional"));
                    orden.setIva(rs.getBigDecimal("iva"));
                    orden.setIibb(rs.getBigDecimal("iibb"));
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
        if (orden == null) {
            return null;
        }
        stmt = con.prepareStatement(
                "SELECT * FROM compras.requerimiento_orden_compra_detalle_consulta "
                + "WHERE id_orden_compra = ? ORDER BY orden");
        try {
            stmt.setInt(1, orden.getIdOrdenCompra());
            ResultSet rs = stmt.executeQuery();
            try {
                while (rs.next()) {
                    RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();
                    detalle.setId(Integer.valueOf(rs.getInt("id_requerimiento_detalle")));
                    detalle.setIdRequerimiento(Integer.valueOf(idRequerimiento));
                    detalle.setCodigoItem(rs.getString("codigo_item"));
                    detalle.setDescripcionItem(rs.getString("descripcion_item"));
                    detalle.setCantidad(Integer.valueOf(rs.getInt("cantidad")));
                    detalle.setPrecioUnitarioEstimado(rs.getBigDecimal("precio_unitario"));
                    detalle.setPrecioTotalEstimado(rs.getBigDecimal("precio_total"));
                    orden.getDetalles().add(detalle);
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
        return orden;
    }

    /** @deprecated Las reglas se coordinan en Java bajo una unica transaccion. */
    @Deprecated
    public RequerimientoCompraOrden crear(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        return new RequerimientoCompraOrdenHelper().crear(orden, usuario);
    }

    /** @deprecated Las reglas se coordinan en Java bajo una unica transaccion. */
    @Deprecated
    public RequerimientoCompraOrden actualizar(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        return new RequerimientoCompraOrdenHelper().actualizar(orden, usuario);
    }

    public Transaccion abrirTransaccion() throws Exception {
        Connection con = ConnectionHelper.getConnectionForTransaction();
        if (con == null) throw new SQLException("No se pudo obtener la conexion de Compras.");
        return new Transaccion(this, con);
    }

    public static class Transaccion {
        private final RequerimientoCompraOrdenServiceImpl service;
        private final Connection con;
        private Transaccion(RequerimientoCompraOrdenServiceImpl service, Connection con) {
            this.service = service; this.con = con;
        }
        private void bloquear(String sql, int id) throws Exception {
            PreparedStatement stmt = con.prepareStatement(sql);
            try { stmt.setInt(1, id); ResultSet rs = stmt.executeQuery();
                try { while (rs.next()) rs.getObject(1); } finally { rs.close(); }
            } finally { stmt.close(); }
        }
        public RequerimientoCompra bloquearYObtenerRequerimiento(int id) throws Exception {
            CallableStatement stmt = con.prepareCall("{call compras.bloquear_requerimiento_reclamo_prestacional(?)}");
            try { stmt.setInt(1, id); stmt.execute(); } finally { stmt.close(); }
            bloquear("SELECT rp.id FROM autorizaciones.reclamos_prestacionales rp "
                    + "JOIN compras.requerimiento_reclamo_prestacional rr ON rr.id_reclamo_prestacional = rp.id "
                    + "WHERE rr.id_requerimiento = ? ORDER BY rp.id FOR UPDATE OF rp", id);
            bloquear("SELECT id_requerimiento FROM compras.requerimiento_reclamo_prestacional "
                    + "WHERE id_requerimiento = ? FOR UPDATE", id);
            bloquear("SELECT id_requerimiento FROM compras.requerimiento WHERE id_requerimiento = ? FOR UPDATE", id);
            bloquear("SELECT id_detalle FROM compras.requerimiento_detalle WHERE id_requerimiento = ? "
                    + "AND baja_fecha IS NULL ORDER BY id_detalle FOR UPDATE", id);
            bloquear("SELECT id_requerimiento_presupuesto FROM compras.requerimiento_presupuesto "
                    + "WHERE id_requerimiento = ? ORDER BY id_requerimiento_presupuesto FOR UPDATE", id);
            bloquear("SELECT id_comparativa FROM compras.requerimiento_comparativa "
                    + "WHERE id_requerimiento = ? ORDER BY id_comparativa FOR UPDATE", id);
            bloquear("SELECT cd.id_detalle FROM compras.requerimiento_comparativa_detalle cd "
                    + "JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa "
                    + "WHERE c.id_requerimiento = ? ORDER BY cd.id_detalle FOR UPDATE OF cd", id);
            bloquear("SELECT id_orden_compra FROM compras.requerimiento_orden_compra "
                    + "WHERE id_requerimiento = ? FOR UPDATE", id);
            bloquear("SELECT od.id_orden_compra FROM compras.requerimiento_orden_compra_detalle od "
                    + "JOIN compras.requerimiento_orden_compra o ON o.id_orden_compra = od.id_orden_compra "
                    + "WHERE o.id_requerimiento = ? ORDER BY od.orden FOR UPDATE OF od", id);
            return BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(con, id);
        }
        public String getConfiguracionCompra(String clave) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.getConfiguracionCompra(clave, con);
        }
        public Map<String, Object> obtenerSector(int id) throws Exception {
            PreparedStatement stmt = con.prepareStatement("SELECT activo,baja_fecha FROM compras.sector_requerimiento WHERE id_sector = ? FOR SHARE");
            try { stmt.setInt(1,id); ResultSet rs=stmt.executeQuery(); try {
                if (!rs.next()) return null;
                Map<String,Object> f=new HashMap<String,Object>();f.put("activo",Boolean.valueOf(rs.getBoolean("activo")));f.put("baja",rs.getTimestamp("baja_fecha"));return f;
            } finally {rs.close();} } finally {stmt.close();}
        }
        public Map<String, Object> obtenerRelacionRp(int id) throws Exception {
            PreparedStatement stmt=con.prepareStatement("SELECT rr.estado,rr.id_reclamo_prestacional,rp.id AS rp_id,rp.baja_fecha "
                    + "FROM compras.requerimiento_reclamo_prestacional rr LEFT JOIN autorizaciones.reclamos_prestacionales rp "
                    + "ON rp.id=rr.id_reclamo_prestacional WHERE rr.id_requerimiento=?");
            try {stmt.setInt(1,id);ResultSet rs=stmt.executeQuery();try {if(!rs.next())return null;
                Map<String,Object> f=new HashMap<String,Object>();f.put("estado",rs.getString("estado"));f.put("rp_id",Integer.valueOf(rs.getInt("rp_id")));f.put("id",Integer.valueOf(rs.getInt("id_reclamo_prestacional")));f.put("baja",rs.getTimestamp("baja_fecha"));return f;
            }finally{rs.close();}}finally{stmt.close();}
        }
        public RequerimientoCompraOrden obtenerOrden(int id) throws Exception {return service.obtenerPorRequerimiento(con,id);}
        public List<RequerimientoCompraPresupuesto> listarDocumentos(int id,int tipo) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.listarDocumentos(con,id,tipo);
        }
        public List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(int id) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.listarOrdenesMedicas(con,id);
        }
        public List<RequerimientoCompraComparativa> listarComparativas(int id,int prestador) throws Exception {
            return RequerimientoCompraComparativaServiceUtil.listar(con,id,prestador);
        }
        public List<Empresa> buscarEmpresa(String cuit,String sucursal) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.buscarEmpresasCotizacion(con,cuit,null,sucursal,2);
        }
        public int obtenerRequerimientoOrigenOrden(int id) throws Exception {
            PreparedStatement stmt=con.prepareStatement("SELECT p.id_requerimiento FROM compras.requerimiento_orden_compra o "
                    + "JOIN compras.requerimiento_presupuesto p ON p.id_requerimiento_presupuesto=o.id_presupuesto "
                    + "WHERE o.id_orden_compra=?");
            try{stmt.setInt(1,id);ResultSet rs=stmt.executeQuery();try{return rs.next()?rs.getInt(1):0;}finally{rs.close();}}finally{stmt.close();}
        }
        public int obtenerPresupuestoOrigenOrden(int id) throws Exception {
            PreparedStatement stmt=con.prepareStatement("SELECT id_presupuesto FROM compras.requerimiento_orden_compra WHERE id_orden_compra=?");
            try{stmt.setInt(1,id);ResultSet rs=stmt.executeQuery();try{return rs.next()?rs.getInt(1):0;}finally{rs.close();}}finally{stmt.close();}
        }
        public Date getFechaActual() throws Exception {
            PreparedStatement stmt=con.prepareStatement("SELECT CURRENT_DATE");try{ResultSet rs=stmt.executeQuery();try{return rs.next()?rs.getDate(1):null;}finally{rs.close();}}finally{stmt.close();}
        }
        public void insertar(RequerimientoCompraOrden orden,int presupuesto,String usuario) throws Exception {
            service.guardarCabecera(con,orden,presupuesto,usuario);int posicion=0;
            for(RequerimientoCompraDetalle detalle:orden.getDetalles())service.guardarDetalle(con,orden.getIdOrdenCompra(),++posicion,detalle);
        }
        public int actualizar(RequerimientoCompraOrden orden,String usuario) throws Exception {
            PreparedStatement stmt=con.prepareStatement("UPDATE compras.requerimiento_orden_compra SET condicion=?,forma_pago=?,plazo_pago=?,entrega=?,"
                    + "observaciones=?,modi_fecha=now(),modi_usr=?,version_edicion=version_edicion+1 "
                    + "WHERE id_orden_compra=? AND id_requerimiento=? AND version_edicion=?");
            int filas;try{stmt.setString(1,orden.getCondicion());stmt.setString(2,orden.getFormaPago());stmt.setString(3,orden.getPlazoPago());stmt.setString(4,orden.getEntrega());stmt.setString(5,orden.getObservaciones());stmt.setString(6,usuario);stmt.setInt(7,orden.getIdOrdenCompra());stmt.setInt(8,orden.getIdRequerimientoCompra());stmt.setInt(9,orden.getVersionEdicion());filas=stmt.executeUpdate();}finally{stmt.close();}
            return filas;
        }
        public int completarPrecio(int orden,int detalle,java.math.BigDecimal precio) throws Exception {
            PreparedStatement stmt=con.prepareStatement("UPDATE compras.requerimiento_orden_compra_detalle SET precio_unitario=? WHERE id_orden_compra=? AND id_requerimiento_detalle=? AND precio_unitario IS NULL");
            try{stmt.setBigDecimal(1,precio);stmt.setInt(2,orden);stmt.setInt(3,detalle);return stmt.executeUpdate();}finally{stmt.close();}
        }
        public int confirmarEstado(int id,int esperado,int nuevo,String usuario) throws Exception {
            PreparedStatement stmt=con.prepareStatement("UPDATE compras.requerimiento SET estado=?,modi_fecha=now(),modi_usr=? WHERE id_requerimiento=? AND estado=? AND baja_fecha IS NULL");
            try{stmt.setInt(1,nuevo);stmt.setString(2,usuario);stmt.setInt(3,id);stmt.setInt(4,esperado);return stmt.executeUpdate();}finally{stmt.close();}
        }
        public void commit() throws Exception {con.commit();}
        public void rollback() throws Exception {con.rollback();}
        public void cerrar() {ConnectionHelper.cerrar(con);}
    }

    private void guardarCabecera(Connection con, RequerimientoCompraOrden orden,
            int idPresupuesto, String usuario) throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO compras.requerimiento_orden_compra "
                + "(id_requerimiento, id_presupuesto, alta_usr, fecha_emision, "
                + "condicion, forma_pago, plazo_pago, entrega, observaciones, iva, iibb) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_orden_compra");
        try {
            stmt.setInt(1, orden.getIdRequerimientoCompra());
            stmt.setInt(2, idPresupuesto);
            stmt.setString(3, usuario);
            stmt.setDate(4, orden.getFechaEmision() == null ? null
                    : new java.sql.Date(orden.getFechaEmision().getTime()));
            stmt.setString(5, orden.getCondicion());
            stmt.setString(6, orden.getFormaPago());
            stmt.setString(7, orden.getPlazoPago());
            stmt.setString(8, orden.getEntrega());
            stmt.setString(9, orden.getObservaciones());
            stmt.setBigDecimal(10, orden.getIva());
            stmt.setBigDecimal(11, orden.getIibb());
            ResultSet rs = stmt.executeQuery();
            try {
                if (!rs.next()) {
                    throw new SQLException("No se obtuvo el numero de Orden de Compra.");
                }
                orden.setIdOrdenCompra(rs.getInt(1));
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    private void guardarDetalle(Connection con, int idOrdenCompra,
            int posicion, RequerimientoCompraDetalle detalle) throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO compras.requerimiento_orden_compra_detalle "
                + "(id_orden_compra, orden, id_requerimiento_detalle, cantidad, precio_unitario) "
                + "VALUES (?, ?, ?, ?, ?)");
        try {
            stmt.setInt(1, idOrdenCompra);
            stmt.setInt(2, posicion);
            stmt.setInt(3, detalle.getIdInt());
            stmt.setInt(4, detalle.getCantidadInt());
            stmt.setBigDecimal(5, detalle.getPrecioUnitarioEstimado());
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("No se pudo guardar el detalle de la Orden de Compra.");
            }
        } finally {
            stmt.close();
        }
    }
}
