package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;
import ar.com.ospim.compras.requerimientos.beans.SectorNomencladorCompra;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.farmaciaOspim.beans.ItemMedicacionTotal;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;

import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import java.util.Set;

/**
 * Consulta y filtrado de nomencladores para el editor de detalle de Compras.
 *
 * La busqueda tecnica resuelve y evalua la configuracion DB en Java.
 * Se conserva la resolución de droga y troquel de Farmacia.
 */
public final class NomencladorCompraBusquedaHelper {

    public List<Nomenclador> buscar(
            int idSector,
            String sectorDescripcion,
            Integer idTipoPrestacion,
            String codigo,
            String descripcion,
            String droga) throws Exception {

        String sector =
                WebKeysCompras.normalizarSectorCompra(
                        sectorDescripcion
                );

        String codigoNormalizado =
                codigo != null
                        ? codigo.trim()
                        : "";

        String descripcionNormalizada =
                descripcion != null
                        ? descripcion.trim()
                        : "";

        String drogaNormalizada =
                esSectorFarmacia(sector) && droga != null
                        ? droga.trim()
                        : "";

        Set<Integer> troqueles = null;

        if (drogaNormalizada.length() > 0) {
            List<Medicamento> medicamentos =
                    BusquedaMedicamentoServiceUtil
                            .getBusquedaMedicamentosDroga(
                                    drogaNormalizada
                            );

            if (medicamentos == null) {
                throw new Exception(
                        "No se pudieron buscar medicamentos por droga."
                );
            }

            troqueles = new HashSet<Integer>();

            for (int i = 0; i < medicamentos.size(); i++) {
                Medicamento medicamento = medicamentos.get(i);

                if (medicamento != null
                        && medicamento.getTroquel() > 0) {

                    troqueles.add(
                            Integer.valueOf(medicamento.getTroquel())
                    );
                }
            }

        }

        List<Nomenclador> resultados =
                BusquedaRequerimientoCompraServiceUtil.buscarNomencladores(
                        idSector, idTipoPrestacion,
                        descripcionNormalizada, codigoNormalizado,
                        troqueles != null ? new ArrayList<Integer>(troqueles) : null);

        List<Nomenclador> filtrados =
                new ArrayList<Nomenclador>();

        for (int i = 0;
                resultados != null
                        && i < resultados.size();
                i++) {

            Nomenclador nomenclador =
                    resultados.get(i);

            if (nomenclador == null
                    || nomenclador.getBaja_fecha() != null
                    || nomenclador.getId_prestacion() <= 0
                    || nomenclador.getId_tipo_nomenclador() <= 0) {

                continue;
            }

            filtrados.add(
                    nomenclador
            );
        }

        return filtrados;
    }

    public String obtenerDrogaMedicamento(
            String sectorDescripcion,
            String codigo) throws Exception {

        String sector =
                WebKeysCompras.normalizarSectorCompra(
                        sectorDescripcion
                );

        if (!esSectorFarmacia(sector)) {
            return "";
        }

        String codigoTroquel =
                codigo != null
                        ? codigo.trim()
                        : "";

        if (!codigoTroquel.matches("^[0-9]+$")) {
            return "";
        }

        int troquel = GetterUtil.getInteger(codigoTroquel, 0);

        if (troquel <= 0) {
            return "";
        }

        List<ItemMedicacionTotal> medicamentos =
                BusquedaMedicamentoServiceUtil
                        .getBusquedaMedicamentosOspimTotal(
                                troquel,
                                0,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                false,
                                0,
                                false
                        );

        if (medicamentos == null) {
            throw new Exception(
                    "No se pudo consultar la droga del medicamento."
            );
        }

        Medicamento medicamento =
                medicamentos.size() > 0
                        ? medicamentos.get(0)
                        : null;

        return medicamento != null && medicamento.getDroga() != null
                ? medicamento.getDroga().trim()
                : "";
    }


    public static String validarConsultaDuplicado(String cuil,int integrante,int prestacion,java.util.Date fecha)throws Exception {
        String normalizado=WebKeysCompras.trimToNull(cuil);
        if(normalizado==null||!normalizado.matches("^[0-9]{11}$"))throw new Exception("Debe informar un CUIL titular valido.");
        if(integrante<0)throw new Exception("Debe informar un integrante valido.");
        if(prestacion<=0)throw new Exception("Debe informar una prestacion valida.");
        if(fecha==null)throw new Exception("Debe informar la fecha del adjunto.");
        return normalizado;
    }

    public static boolean existeRequerimientoDuplicado(java.sql.Connection con,String cuil,int integrante,
            int prestacion,java.util.Date fecha,int excluir)throws Exception {
        String parametro=quitarEspaciosSql(cuil);
        if(parametro==null||parametro.length()==0||integrante<0||prestacion<=0||fecha==null)return false;
        String estados=BusquedaRequerimientoCompraServiceUtil.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS,con);
        int anulado=ConfiguracionComprasHelper.resolverEstado("ANULADO",estados);
        List<Map<String,Object>> filas=BusquedaRequerimientoCompraServiceUtil.listarDatosDuplicado(con,parametro,integrante,prestacion);
        return evaluarRequerimientoDuplicado(parametro,Integer.valueOf(integrante),Integer.valueOf(prestacion),fecha,
                Integer.valueOf(excluir),anulado,filas);
    }

    public static boolean evaluarRequerimientoDuplicado(String cuil,Integer integrante,Integer prestacion,
            java.util.Date fecha,Integer excluir,int anulado,List<Map<String,Object>> filas) {
        String parametro=quitarEspaciosSql(cuil);
        if(parametro==null||parametro.length()==0||integrante==null||integrante.intValue()<0
                ||prestacion==null||prestacion.intValue()<=0||fecha==null)return false;
        for(int i=0;filas!=null&&i<filas.size();i++){
            Map<String,Object> f=filas.get(i);Integer id=(Integer)f.get("id_requerimiento"),estado=(Integer)f.get("estado");
            if(id==null||estado==null||estado.intValue()==anulado||f.get("requerimiento_baja")!=null
                    ||f.get("detalle_baja")!=null||f.get("documento_baja")!=null)continue;
            if(!parametro.equals(f.get("cuil"))||!integrante.equals(f.get("integrante"))||!prestacion.equals(f.get("id_prestacion"))
                    ||!ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR.equals(f.get("tipo_item"))
                    ||!Integer.valueOf(ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_ORDEN_MEDICA).equals(f.get("tipo_documento"))
                    ||!mismaFechaDuplicado(fecha,(java.util.Date)f.get("fecha_documento")))continue;
            if(excluir==null||excluir.intValue()<=0||!id.equals(excluir))return true;
        }
        return false;
    }

    private static String quitarEspaciosSql(String valor) {
        if(valor==null)return null;int inicio=0,fin=valor.length();
        while(inicio<fin&&valor.charAt(inicio)==' ')inicio++;
        while(fin>inicio&&valor.charAt(fin-1)==' ')fin--;
        return valor.substring(inicio,fin);
    }

    private static boolean mismaFechaDuplicado(java.util.Date a,java.util.Date b) {
        if(a==null||b==null)return false;
        java.util.Calendar ca=java.util.Calendar.getInstance(),cb=java.util.Calendar.getInstance();ca.setTime(a);cb.setTime(b);
        return ca.get(java.util.Calendar.ERA)==cb.get(java.util.Calendar.ERA)
                &&ca.get(java.util.Calendar.YEAR)==cb.get(java.util.Calendar.YEAR)
                &&ca.get(java.util.Calendar.MONTH)==cb.get(java.util.Calendar.MONTH)
                &&ca.get(java.util.Calendar.DAY_OF_MONTH)==cb.get(java.util.Calendar.DAY_OF_MONTH);
    }

    public static List<RequerimientoCompraSector> filtrarSectoresSeleccionables(
            List<Map<String,Object>> catalogo,String excluidos) {
        ConfiguracionComprasHelper.resolverSectorSeleccionable(null,excluidos);
        String clave="COMPRAS_SECTORES_NO_SELECCIONABLES";
        List<String> tokens=ConfiguracionComprasHelper.resolverListaOpciones(clave,excluidos);
        Set<String> existentes=new HashSet<String>();
        for(int i=0;catalogo!=null&&i<catalogo.size();i++)
            existentes.add(WebKeysCompras.normalizarSectorCompra((String)catalogo.get(i).get("descripcion")));
        for(int i=0;i<tokens.size();i++)if(!existentes.contains(tokens.get(i)))
            throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException(clave);
        List<RequerimientoCompraSector> resultado=new ArrayList<RequerimientoCompraSector>();
        for(int i=0;catalogo!=null&&i<catalogo.size();i++){
            Map<String,Object> fila=catalogo.get(i);String descripcion=(String)fila.get("descripcion");
            Integer id=(Integer)fila.get("id");
            if(id!=null&&id.intValue()>0&&Boolean.TRUE.equals(fila.get("activo"))&&fila.get("baja")==null
                    &&!WebKeysCompras.isEmpty(descripcion)
                    &&ConfiguracionComprasHelper.resolverSectorSeleccionable(descripcion,excluidos))
                resultado.add(new RequerimientoCompraSector(id,descripcion,(Boolean)fila.get("requiere_afiliado")));
        }
        return resultado;
    }

    public static List<Nomenclador> buscarConfigurados(int idSector,
            Integer idTipoPrestacion, String descripcion, String codigo,
            List<Integer> troqueles) throws Exception {
        List<SectorNomencladorCompra> configuracion =
                BusquedaRequerimientoCompraServiceUtil.getConfiguracionNomenclador(
                        idSector, idTipoPrestacion);
        RequerimientoCompraSector sector =
                BusquedaRequerimientoCompraServiceUtil.getSector(idSector);
        if (sector == null) {
            throw new Exception("El sector informado no esta disponible.");
        }
        boolean sinAcentos = ConfiguracionComprasHelper.compararBusquedaSinAcentos(
                sector.getDescripcion());
        String descripcionComparada = sinAcentos
                ? WebKeysCompras.normalizarClaveTexto(descripcion) : null;
        List<Nomenclador> candidatos = BusquedaRequerimientoCompraServiceUtil
                .buscarNomencladoresCandidatos(null, sinAcentos ? null : descripcion, codigo);
        if (candidatos == null) {
            throw new Exception("No se pudieron consultar los nomencladores.");
        }
        List<Nomenclador> resultado = new ArrayList<Nomenclador>();
        Set<Integer> ids = new HashSet<Integer>();
        for (int i = 0; i < candidatos.size(); i++) {
            Nomenclador candidato = candidatos.get(i);
            if (!esNomencladorConfigurado(configuracion, candidato)
                    || (sinAcentos && descripcionComparada.length() > 0
                        && WebKeysCompras.normalizarClaveTexto(candidato.getDescripcion())
                            .indexOf(descripcionComparada) < 0)
                    || !cumpleTroqueles(candidato.getCodigo(), troqueles)) {
                continue;
            }
            if (ids.add(Integer.valueOf(candidato.getId_prestacion()))) {
                resultado.add(candidato);
            }
        }
        return resultado;
    }

    private static boolean cumpleTroqueles(String codigo, List<Integer> troqueles) {
        if (troqueles == null) {
            return true;
        }
        String valor = codigo != null ? codigo.trim() : "";
        if (!valor.matches("^[0-9]+$")) {
            return false;
        }
        java.math.BigDecimal numero = new java.math.BigDecimal(valor);
        for (int i = 0; i < troqueles.size(); i++) {
            Integer troquel = troqueles.get(i);
            if (troquel != null && numero.compareTo(
                    new java.math.BigDecimal(troquel.intValue())) == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean esSectorFarmacia(String sector) {
        Integer filtro = ConfiguracionComprasHelper.getFiltroTipoNomenclador(sector);
        int tipoFarmacia = ConfiguracionComprasHelper.getTipoNomencladorFarmacia();
        return filtro != null && filtro.intValue() == tipoFarmacia;
    }
    /*
     * Resuelve la precedencia de asociaciones en la capa funcional Java.
     * Los callers deben leer las listas DB dentro de su operacion; no cachear.
     * El overload con datos permite usar la misma conexion/transaccion
     * que mantiene los bloqueos cuando la operacion escribe.
     */
    public static List<SectorNomencladorCompra> resolverConfiguracion(
            int idSector,
            Integer idTipoPrestacion,
            List<RequerimientoCompraSector> sectoresActivos,
            List<TipoPrestacionCompra> tipos,
            List<SectorNomencladorCompra> asociaciones) throws Exception {
        if (idSector <= 0 || sectoresActivos == null || tipos == null
                || asociaciones == null) {
            throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException("compras.sector_nomenclador");
        }
        boolean sectorValido = false;
        for (int i = 0; i < sectoresActivos.size(); i++) {
            RequerimientoCompraSector sector = sectoresActivos.get(i);
            if (sector != null && sector.getIdSector() == idSector) {
                sectorValido = true;
            }
        }
        if (!sectorValido) {
            throw new Exception("El sector informado no existe o no esta disponible.");
        }
        if (idTipoPrestacion != null) {
            boolean tipoValido = false;
            for (int i = 0; i < tipos.size(); i++) {
                TipoPrestacionCompra tipo = tipos.get(i);
                if (tipo != null && tipo.getIdInt() == idTipoPrestacion.intValue()
                        && tipo.getIdSectorInt() == idSector) {
                    tipoValido = true;
                }
            }
            if (!tipoValido) {
                throw new Exception("El tipo de cotizacion no existe o no pertenece al sector.");
            }
        }
        boolean especifica = false;
        for (int i = 0; i < asociaciones.size(); i++) {
            SectorNomencladorCompra asociacion = asociaciones.get(i);
            if (esAsociacionDelSector(asociacion, idSector)
                    && idTipoPrestacion != null
                    && idTipoPrestacion.equals(asociacion.getIdTipoPrestacion())) {
                especifica = true;
            }
        }
        List<SectorNomencladorCompra> efectivas = new ArrayList<SectorNomencladorCompra>();
        for (int i = 0; i < asociaciones.size(); i++) {
            SectorNomencladorCompra asociacion = asociaciones.get(i);
            if (!esAsociacionDelSector(asociacion, idSector)) {
                continue;
            }
            boolean efectiva = especifica
                    ? idTipoPrestacion.equals(asociacion.getIdTipoPrestacion())
                    : asociacion.getIdTipoPrestacion() == null;
            if (efectiva) {
                validarCriterios(asociacion);
                efectivas.add(asociacion);
            }
        }
        if (efectivas.isEmpty()) {
            throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException("compras.sector_nomenclador");
        }
        return efectivas;
    }

    private static boolean esAsociacionDelSector(
            SectorNomencladorCompra asociacion, int idSector) {
        return asociacion != null && asociacion.getIdSector() != null
                && asociacion.getIdSector().intValue() == idSector;
    }

    private static void validarCriterios(SectorNomencladorCompra asociacion)
            throws Exception {
        if (asociacion == null
                || (asociacion.getIdTipoNomenclador() == null
                    && asociacion.getMarcaReinLiq() == null
                    && asociacion.getCodigoNomenclador() == null)
                || (asociacion.getIdTipoNomenclador() != null
                    && (asociacion.getIdTipoNomenclador().intValue() <= 0
                        || !asociacion.getIdTipoNomenclador().equals(
                            asociacion.getIdTipoNomencladorCatalogo())))
                || (asociacion.getCodigoNomenclador() != null
                    && (asociacion.getCodigoNomenclador().trim().length() == 0
                        || !asociacion.getCodigoNomenclador().equals(
                            asociacion.getCodigoNomenclador().trim())))) {
            throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException("compras.sector_nomenclador");
        }
    }

    /* AND dentro de la fila; OR entre filas. Valida todas las filas efectivas. */
    public static boolean esNomencladorConfigurado(
            List<SectorNomencladorCompra> asociaciones, Nomenclador nomenclador)
            throws Exception {
        if (asociaciones == null || asociaciones.isEmpty()) {
            throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException("compras.sector_nomenclador");
        }
        boolean permitido = false;
        for (int i = 0; i < asociaciones.size(); i++) {
            SectorNomencladorCompra asociacion = asociaciones.get(i);
            validarCriterios(asociacion);
            if (nomenclador != null && nomenclador.getId_prestacion() > 0
                    && nomenclador.getId_tipo_nomenclador() > 0
                    && nomenclador.getBaja_fecha() == null
                    && (asociacion.getIdTipoNomenclador() == null
                        || asociacion.getIdTipoNomenclador().intValue()
                            == nomenclador.getId_tipo_nomenclador())
                    && (asociacion.getMarcaReinLiq() == null
                        || asociacion.getMarcaReinLiq().intValue()
                            == nomenclador.getMarcaReintegroLiquidacion())
                    && (asociacion.getCodigoNomenclador() == null
                        || asociacion.getCodigoNomenclador().equals(
                            nomenclador.getCodigo() != null
                                ? nomenclador.getCodigo().trim() : ""))) {
                permitido = true;
            }
        }
        return permitido;
    }

    public static boolean esNomencladorValidoParaSector(String sector,
            int tipoNomenclador, int marca, String codigo) {
        return esNomencladorValidoParaContexto(sector, null, tipoNomenclador, marca, codigo);
    }

    public static boolean esNomencladorValidoParaTipoPrestacion(String sector,
            int tipoPrestacion, int tipoNomenclador, int marca, String codigo) {
        return esNomencladorValidoParaContexto(sector,
                tipoPrestacion > 0 ? Integer.valueOf(tipoPrestacion) : null,
                tipoNomenclador, marca, codigo);
    }

    private static boolean esNomencladorValidoParaContexto(String descripcionSector,
            Integer tipoPrestacion, int tipoNomenclador, int marca, String codigo) {
        try {
            String clave = WebKeysCompras.normalizarSectorCompra(descripcionSector);
            List<RequerimientoCompraSector> sectores =
                    BusquedaRequerimientoCompraServiceUtil.listarSectores();
            RequerimientoCompraSector encontrado = null;
            for (int i = 0; sectores != null && i < sectores.size(); i++) {
                RequerimientoCompraSector sector = sectores.get(i);
                if (sector != null && clave.equals(
                        WebKeysCompras.normalizarSectorCompra(sector.getDescripcion()))) {
                    if (encontrado != null) {
                        throw new Exception("La descripcion del sector de Compras es ambigua.");
                    }
                    encontrado = sector;
                }
            }
            if (encontrado == null) {
                throw new Exception("El sector informado no existe o no esta disponible.");
            }
            List<SectorNomencladorCompra> asociaciones =
                    BusquedaRequerimientoCompraServiceUtil.getConfiguracionNomenclador(
                            encontrado.getIdSector(), tipoPrestacion);
            boolean permitido = false;
            for (int i = 0; i < asociaciones.size(); i++) {
                SectorNomencladorCompra asociacion = asociaciones.get(i);
                validarCriterios(asociacion);
                if (tipoNomenclador > 0
                        && (asociacion.getIdTipoNomenclador() == null
                            || asociacion.getIdTipoNomenclador().intValue() == tipoNomenclador)
                        && (asociacion.getMarcaReinLiq() == null
                            || asociacion.getMarcaReinLiq().intValue() == marca)
                        && (asociacion.getCodigoNomenclador() == null
                            || asociacion.getCodigoNomenclador().equals(
                                codigo != null ? codigo.trim() : ""))) {
                    permitido = true;
                }
            }
            return permitido;
        } catch (Exception e) {
            throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException(
                    "compras.sector_nomenclador", e);
        }
    }


    public static Map<Integer, String> obtenerDrogasNomencladorFarmacia(
            List<Nomenclador> nomencladores) throws Exception {

        int tipoFarmacia = ConfiguracionComprasHelper.getTipoNomencladorFarmacia();
        List<Integer> troqueles = new ArrayList<Integer>();
        Map<Integer, Integer> troquelesPorPrestacion =
                new HashMap<Integer, Integer>();

        for (int i = 0;
                nomencladores != null && i < nomencladores.size();
                i++) {

            Nomenclador nomenclador = nomencladores.get(i);

            if (nomenclador == null
                    || nomenclador.getId_tipo_nomenclador()
                    != tipoFarmacia) {
                continue;
            }

            String codigo = nomenclador.getCodigo() != null
                    ? nomenclador.getCodigo().trim()
                    : "";
            int troquel = codigo.matches("^[0-9]+$")
                    ? GetterUtil.getInteger(codigo, 0)
                    : 0;

            if (troquel > 0) {
                Integer claveTroquel = Integer.valueOf(troquel);
                if (!troqueles.contains(claveTroquel)) {
                    troqueles.add(claveTroquel);
                }
                troquelesPorPrestacion.put(
                        Integer.valueOf(nomenclador.getId_prestacion()),
                        claveTroquel
                );
            }
        }

        Map<Integer, String> drogasPorTroquel =
                BusquedaRequerimientoCompraServiceUtil.listarDrogasMedicamentosBatch(troqueles);
        Map<Integer, String> drogasPorPrestacion =
                new HashMap<Integer, String>();

        for (Map.Entry<Integer, Integer> item
                : troquelesPorPrestacion.entrySet()) {
            String droga = drogasPorTroquel.get(item.getValue());
            drogasPorPrestacion.put(
                    item.getKey(),
                    droga != null ? droga : ""
            );
        }

        return drogasPorPrestacion;
    }

    public static Map<Integer, String> obtenerDrogasDetalleFarmacia(
            List<RequerimientoCompraDetalle> detalles) throws Exception {

        int tipoFarmacia = ConfiguracionComprasHelper.getTipoNomencladorFarmacia();
        List<Integer> troqueles = new ArrayList<Integer>();

        for (int i = 0; detalles != null && i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle = detalles.get(i);
            if (detalle == null) {
                continue;
            }

            int troquel = 0;
            if (detalle.esMedicamento()) {
                troquel = detalle.getTroquelInt();
            } else if (detalle.esNomenclador()
                    && detalle.getIdTipoNomencladorInt()
                    == tipoFarmacia) {
                String codigo = detalle.getCodigoNomencladorVisible().trim();
                if (codigo.matches("^[0-9]+$")) {
                    troquel = GetterUtil.getInteger(codigo, 0);
                }
            }

            Integer claveTroquel = Integer.valueOf(troquel);
            if (troquel > 0 && !troqueles.contains(claveTroquel)) {
                troqueles.add(claveTroquel);
            }
        }

        return BusquedaRequerimientoCompraServiceUtil.listarDrogasMedicamentosBatch(troqueles);
    }


    public static List<RequerimientoCompraDetalle> buscarItemsHistoricosAfiliado(
            String cuilTitular,
            int inte,
            int idSector,
            int idRequerimientoExcluir) throws Exception {

        String cuil = WebKeysCompras.trimToNull(cuilTitular);

        if (cuil == null || !cuil.matches("^[0-9]{11}$")) {
            throw new Exception(
                    "Debe informar un CUIL titular válido."
            );
        }

        if (inte < 0) {
            throw new Exception(
                    "Debe informar un integrante válido."
            );
        }

        if (idSector <= 0) {
            throw new Exception(
                    "Debe informar el sector del requerimiento."
            );
        }

        List<RequerimientoCompraDetalle> items =
                BusquedaRequerimientoCompraServiceUtil.consultarItemsHistoricosAfiliado(
                        cuil,
                        inte,
                        idSector,
                        idRequerimientoExcluir > 0
                                ? idRequerimientoExcluir
                                : 0,
                        ConfiguracionComprasHelper.getMaxItemsHistoricosAfiliado()
                );

        int cantidadInicial = ConfiguracionComprasHelper.getCantidadDetalleInicial();
        for (int i = 0; i < items.size(); i++) {
            RequerimientoCompraDetalle detalle = items.get(i);

            if (detalle == null) {
                continue;
            }

            detalle.setTipoItem(
                    RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR
            );
            detalle.setCodigoItem(
                    detalle.getCodigoNomenclador()
            );
            detalle.setDescripcionItem(
                    detalle.getDescripcionNomenclador()
            );
            detalle.setCantidad(Integer.valueOf(cantidadInicial));
        }

        return items;
    }

    public static List<SectorNomencladorCompra> resolverConfiguracion(int idSector,
            Integer idTipoPrestacion, ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil.Transaccion transaccion) throws Exception {
        try {
            return resolverConfiguracion(idSector, idTipoPrestacion,
                    transaccion.listarSectores(), transaccion.listarTiposPrestacion(),
                    transaccion.listarAsociacionesNomenclador(idSector));
        } catch (ConfiguracionComprasHelper.ConfiguracionInvalidaException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException(
                    "compras.sector_nomenclador", e);
        }
    }

    public static List<SectorNomencladorCompra> resolverConfiguracion(int idSector,
            Integer idTipoPrestacion, ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalTransaccion transaccion) throws Exception {
        try {
            return resolverConfiguracion(idSector, idTipoPrestacion,
                    transaccion.listarSectores(), transaccion.listarTiposPrestacion(),
                    transaccion.listarAsociacionesNomenclador(idSector));
        } catch (ConfiguracionComprasHelper.ConfiguracionInvalidaException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException(
                    "compras.sector_nomenclador", e);
        }
    }
}
