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
import ar.com.ospim.util.ConnectionHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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

    private static final Log _log = LogFactoryUtil.getLog(
            RequerimientoCompraOrdenServiceImpl.class);

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
                    : obtenerPresupuestoAdjudicado(con, orden.getIdRequerimientoCompra()));
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

    public RequerimientoCompraOrden crear(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        Connection con = ConnectionHelper.getConnectionForTransaction();
        if (con == null) {
            throw new SQLException("No se pudo obtener la conexion de Compras.");
        }
        try {
            bloquearCreacion(con, orden.getIdRequerimientoCompra(), usuario);
            RequerimientoCompraOrden existente = obtenerPorRequerimiento(
                    con, orden.getIdRequerimientoCompra());
            if (existente != null) {
                confirmar(con, orden.getIdRequerimientoCompra(), usuario);
                con.commit();
                return existente;
            }
            guardarCabecera(con, orden, usuario);
            int posicion = 0;
            for (RequerimientoCompraDetalle detalle : orden.getDetalles()) {
                guardarDetalle(con, orden.getIdOrdenCompra(), ++posicion, detalle);
            }
            confirmar(con, orden.getIdRequerimientoCompra(), usuario);
            RequerimientoCompraOrden persistida = obtenerPorRequerimiento(
                    con, orden.getIdRequerimientoCompra());
            con.commit();
            return persistida;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException rollbackError) {
                _log.error("No se pudo revertir la creacion de la Orden de Compra.", rollbackError);
            }
            throw e;
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    public RequerimientoCompraOrden actualizar(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        Connection con = ConnectionHelper.getConnectionForTransaction();
        if (con == null) {
            throw new SQLException("No se pudo obtener la conexion de Compras.");
        }
        try {
            int idExistente = bloquearCreacion(con, orden.getIdRequerimientoCompra(), usuario);
            if (idExistente <= 0 || idExistente != orden.getIdOrdenCompra()
                    || orden.getVersionEdicion() < 0
                    || orden.getVersionEdicion() == Integer.MAX_VALUE) {
                throw new IllegalArgumentException("La Orden de Compra emitida ya no esta disponible.");
            }
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE compras.requerimiento_orden_compra "
                    + "SET condicion = ?, forma_pago = ?, plazo_pago = ?, entrega = ?, "
                    + "observaciones = ?, modi_fecha = now(), modi_usr = ?, "
                    + "version_edicion = version_edicion + 1 "
                    + "WHERE id_orden_compra = ? AND id_requerimiento = ? AND version_edicion = ?");
            try {
                stmt.setString(1, orden.getCondicion());
                stmt.setString(2, orden.getFormaPago());
                stmt.setString(3, orden.getPlazoPago());
                stmt.setString(4, orden.getEntrega());
                stmt.setString(5, orden.getObservaciones());
                stmt.setString(6, usuario);
                stmt.setInt(7, orden.getIdOrdenCompra());
                stmt.setInt(8, orden.getIdRequerimientoCompra());
                stmt.setInt(9, orden.getVersionEdicion());
                if (stmt.executeUpdate() != 1) {
                    throw new IllegalArgumentException(
                            "La Orden de Compra fue modificada por otro usuario. Actualice la pantalla.");
                }
            } finally {
                stmt.close();
            }
            completarPreciosPendientes(con, orden);
            RequerimientoCompraOrden persistida = obtenerPorRequerimiento(
                    con, orden.getIdRequerimientoCompra());
            con.commit();
            return persistida;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException rollbackError) {
                _log.error("No se pudo revertir la modificacion de la Orden de Compra.", rollbackError);
            }
            throw e;
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    private void completarPreciosPendientes(Connection con, RequerimientoCompraOrden orden)
            throws Exception {
        if (orden.getDetalles() == null || orden.getDetalles().isEmpty()) {
            return;
        }
        PreparedStatement stmt = con.prepareStatement(
                "UPDATE compras.requerimiento_orden_compra_detalle d SET precio_unitario = ? "
                + "WHERE d.id_orden_compra = ? AND d.id_requerimiento_detalle = ? "
                + "AND d.precio_unitario IS NULL AND EXISTS ("
                + "SELECT 1 FROM compras.requerimiento_orden_compra o "
                + "JOIN compras.requerimiento r ON r.id_requerimiento = o.id_requerimiento "
                + "JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector "
                + "WHERE o.id_orden_compra = d.id_orden_compra AND r.id_requerimiento = ? "
                + "AND compras.normalizar_sector(sr.descripcion) IN ('SISTEMAS', 'RRHH'))");
        try {
            for (RequerimientoCompraDetalle detalle : orden.getDetalles()) {
                if (detalle == null || detalle.getIdInt() <= 0
                        || detalle.getPrecioUnitarioEstimado() == null) {
                    throw new IllegalArgumentException("El precio pendiente de la Orden de Compra no es valido.");
                }
                stmt.setBigDecimal(1, detalle.getPrecioUnitarioEstimado());
                stmt.setInt(2, orden.getIdOrdenCompra());
                stmt.setInt(3, detalle.getIdInt());
                stmt.setInt(4, orden.getIdRequerimientoCompra());
                if (stmt.executeUpdate() != 1) {
                    throw new IllegalArgumentException(
                            "El precio pendiente fue modificado o ya no esta disponible. Actualice la pantalla.");
                }
            }
        } finally {
            stmt.close();
        }
    }

    private int bloquearCreacion(Connection con, int idRequerimiento,
            String usuario) throws Exception {
        CallableStatement stmt = con.prepareCall(
                "{ ? = call compras.bloquear_creacion_orden_compra(?,?) }");
        try {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, idRequerimiento);
            stmt.setString(3, usuario);
            stmt.execute();
            return stmt.getInt(1);
        } finally {
            stmt.close();
        }
    }

    private void confirmar(Connection con, int idRequerimiento,
            String usuario) throws Exception {
        CallableStatement stmt = con.prepareCall(
                "{ ? = call compras.confirmar_orden_compra_requerimiento(?,?) }");
        try {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, idRequerimiento);
            stmt.setString(3, usuario);
            stmt.execute();
            if (stmt.getInt(1) != WebKeysCompras.ESTADO_ORDEN_COMPRA) {
                throw new SQLException("No se confirmo la Orden de Compra.");
            }
        } finally {
            stmt.close();
        }
    }

    private int obtenerPresupuestoAdjudicado(Connection con, int idRequerimiento)
            throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT rp.id_requerimiento_presupuesto "
                + "FROM compras.requerimiento_presupuesto rp "
                + "JOIN compras.requerimiento r ON r.id_requerimiento = rp.id_requerimiento "
                + "JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector "
                + "WHERE rp.id_requerimiento = ? AND rp.baja_fecha IS NULL "
                + "AND ((compras.normalizar_sector(sr.descripcion) IN ('RRHH', 'SISTEMAS') "
                + "AND rp.tipo_documento = 3 AND rp.empresa_adjudicada) "
                + "OR (compras.normalizar_sector(sr.descripcion) NOT IN ('RRHH', 'SISTEMAS') "
                + "AND rp.tipo_documento = 1 AND rp.id_prestador = ("
                + "SELECT min(d.id_prestador) FROM compras.requerimiento_detalle d "
                + "WHERE d.id_requerimiento = r.id_requerimiento AND d.baja_fecha IS NULL "
                + "HAVING count(DISTINCT d.id_prestador) = 1 "
                + "AND count(*) = count(d.id_prestador))))");
        try {
            stmt.setInt(1, idRequerimiento);
            ResultSet rs = stmt.executeQuery();
            try {
                if (!rs.next()) {
                    throw new IllegalArgumentException(
                            "Debe existir una cotizacion adjudicada unica del requerimiento para emitir la Orden de Compra.");
                }
                int idPresupuesto = rs.getInt("id_requerimiento_presupuesto");
                if (idPresupuesto <= 0 || rs.next()) {
                    throw new IllegalArgumentException(
                            "Debe existir una cotizacion adjudicada unica del requerimiento para emitir la Orden de Compra.");
                }
                return idPresupuesto;
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    private void guardarCabecera(Connection con, RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        int idPresupuesto = obtenerPresupuestoAdjudicado(con,
                orden.getIdRequerimientoCompra());
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
