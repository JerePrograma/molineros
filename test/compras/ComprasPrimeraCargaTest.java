import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraFiltro;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.util.ConnectionHelper;

/* Calls actual productive ServiceImpl, ServiceUtil and beans.
 * Mocked JDBC returns predetermined row fixtures and records invocations.
 * It does not parse or execute SQL and cannot prove PostgreSQL performance. */
public class ComprasPrimeraCargaTest {
    private static int checks;
    private static State jdbc;

    public static void main(String[] args) throws Exception {
        ConfiguracionComprasDatosFixture.instalar();
        testConfiguracionBusqueda();
        testInitialRoute();
        testFilterRouting();
        testInitialFailures();
        testFiltersAndRp();

        testValidationAndFailures();
        testUnlimitedExport();
        System.out.println("COMPRAS_PRIMERA_CARGA_OFFLINE_OK checks=" + checks);
        System.out.println("Actual Java production paths; JDBC boundary mocked; no DB, Tomcat or authenticated UI.");
    }

    private static void testConfiguracionBusqueda() throws Exception {
        ConfiguracionComprasDatosFixture datos = new ConfiguracionComprasDatosFixture();
        String limites = datos.getConfiguracionCompra("COMPRAS_LIMITES");
        String estados = datos.getConfiguracionCompra("COMPRAS_ESTADOS_IDS");
        try {
            ConfiguracionComprasDatosFixture.configurar("COMPRAS_LIMITES",
                    limites.replace("REQUERIMIENTOS_POR_PAGINA=50", "REQUERIMIENTOS_POR_PAGINA=7"));
            ConfiguracionComprasDatosFixture.configurar("COMPRAS_ESTADOS_IDS",
                    estados.replace("ANULADO=99", "ANULADO=98"));
            reset(rows(100, 1, 1));
            paginated(null, false, 3);
            Query consulta = jdbc.queries.get(0);
            verifyBound(consulta, 1, "setInt", Integer.valueOf(98));
            verifyBound(consulta, 2, "setInt", Integer.valueOf(7));
            verifyBound(consulta, 3, "setLong", Long.valueOf(14));
            check(normalized(consulta.sql).indexOf("estado = 99") < 0,
                    "el estado de anulacion no queda fijo en SQL");
        } finally {
            ConfiguracionComprasDatosFixture.configurar("COMPRAS_LIMITES", limites);
            ConfiguracionComprasDatosFixture.configurar("COMPRAS_ESTADOS_IDS", estados);
        }
    }

    private static void testInitialRoute() throws Exception {
        int[] pages = new int[] {1, 2, 3, 0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE};
        for (int i = 0; i < pages.length; i++) {
            int count = pages[i] == Integer.MAX_VALUE ? 0 : pages[i] == 3 ? 23 : 50;
            int first = 1000 - (Math.max(1, pages[i]) - 1) * 50;
            reset(rows(first, count, 123));
            List<RequerimientoCompra> actual = paginated(i % 2 == 0 ? new RequerimientoCompraFiltro() : null, false, pages[i]);
            eq("initial size", Integer.valueOf(count), Integer.valueOf(actual.size()));
            for (int j = 0; j < actual.size(); j++) {
                eq("initial order " + j, Integer.valueOf(first - j), actual.get(j).getId());
                eq("initial mapped total " + j, Integer.valueOf(123), Integer.valueOf(actual.get(j).getTotalRegistros()));
            }
            verifyInitialQuery(pages[i]);
            verifyClosed(1, 1, 1);
        }
        reset(new ArrayList<Map<String, Object>>());
        eq("zero results", Integer.valueOf(0), Integer.valueOf(paginated(null, false, 1).size()));
        verifyInitialQuery(1); verifyClosed(1, 1, 1);

        RequerimientoCompraFiltro blank = new RequerimientoCompraFiltro();
        blank.setAfiliadoCuilTitular("  "); blank.setIdTercerizadora("\t"); blank.setTexto("  ");
        reset(rows(700, 1, 1)); paginated(blank, false, 1); verifyInitialQuery(1); verifyClosed(1, 1, 1);

        List<Map<String, Object>> mapped = rows(700, 1, 1); Map<String, Object> row = mapped.get(0);
        row.put("alta_fecha", new java.sql.Timestamp(1727740800000L)); row.put("alta_usr", "offline");
        row.put("afiliado_cuil_titular", "20000000001"); row.put("afiliado_int", Integer.valueOf(0));
        row.put("afiliado_apellido", "Apellido"); row.put("afiliado_nombre", "Nombre");
        row.put("afiliado_nombre_apellido", "Apellido, Nombre"); row.put("afiliado_documento", "DNI 123");
        row.put("id_sector", Integer.valueOf(8)); row.put("sector_descripcion", "Sector offline");
        row.put("requiere_afiliado", Boolean.TRUE); row.put("recupero", Boolean.FALSE);
        row.put("surge", Boolean.TRUE); row.put("observaciones", "Observacion offline");
        reset(mapped); RequerimientoCompra value = paginated(null, false, 1).get(0);
        eq("date mapped", row.get("alta_fecha"), value.getAltaFecha());
        eq("user mapped", "offline", value.getAltaUsr());
        eq("affiliate CUIL mapped", "20000000001", value.getAfiliadoCuilTitular());
        eq("affiliate titular mapped", Integer.valueOf(0), value.getAfiliadoInt());
        eq("formatted name mapped", "Apellido, Nombre", value.getAfiliadoNombreApellido());
        eq("formatted document mapped", "DNI 123", value.getAfiliadoDocumento());
        eq("sector mapped", Integer.valueOf(8), value.getIdSector());
        eq("sector label mapped", "Sector offline", value.getSectorDescripcion());
        eq("sector requires affiliate mapped", Boolean.TRUE, value.getRequiereAfiliado());
        eq("recupero false mapped", Boolean.FALSE, value.getRecupero());
        eq("surge true mapped", Boolean.TRUE, value.getSurge());
        eq("observations mapped", "Observacion offline", value.getObservaciones());
        verifyClosed(1, 1, 1);
    }

    private static void verifyInitialQuery(int page) {
        eq("one initial query", Integer.valueOf(1), Integer.valueOf(jdbc.queries.size()));
        Query query = jdbc.queries.get(0); String sql = normalized(query.sql);
        eq("initial prepared", "prepareStatement", query.preparation);
        check(sql.indexOf("compras.buscar_requerimientos(") < 0, "first load must not materialize full buscar_requerimientos before LIMIT");
        check(sql.indexOf("from compras.requerimiento ") >= 0, "initial selects base requirements");
        check(sql.indexOf("baja_fecha is null") >= 0, "initial excludes deleted requirements");
        check(sql.indexOf("join compras.sector_requerimiento") >= 0, "initial retains sector membership");
        int limit = sql.indexOf("limit ? offset ?");
        int format = sql.indexOf("concat_ws(");
        int state = sql.indexOf("compras.estado_requerimiento_descripcion(");
        check(limit >= 0 && format > limit && state > limit, "initial limits IDs before name/document/state enrichment");
        check(sql.substring(0, limit).indexOf("order by id_requerimiento desc") >= 0, "initial limits descending IDs");
        check(sql.indexOf("count(*) as total_registros from candidatos") >= 0, "initial counts candidate IDs without full enrichment");
        check(sql.indexOf("order by r.id_requerimiento desc") > limit, "initial retains final descending order");
        eq("initial SQL placeholders", Integer.valueOf(3), Integer.valueOf(occurrences(sql, "?")));
        eq("initial state, limit and offset", Integer.valueOf(3), Integer.valueOf(query.binds.size()));
        verifyBound(query, 1, "setInt", Integer.valueOf(99));
        verifyBound(query, 2, "setInt", Integer.valueOf(50));
        verifyBound(query, 3, "setLong", Long.valueOf(((long) Math.max(1, page) - 1L) * 50L));
    }

    private static void testFilterRouting() throws Exception {
        for (int i = 0; i < 17; i++) {
            RequerimientoCompraFiltro value = new RequerimientoCompraFiltro();
            switch (i) {
                case 0: value.setIdEstado(Integer.valueOf(3)); break;
                case 1: value.setIdSector(Integer.valueOf(8)); break;
                case 2: value.setAfiliadoCuilTitular("20000000001"); break;
                case 3: value.setAfiliadoInt(Integer.valueOf(0)); break;
                case 4: value.setIdTercerizadora("OFFLINE"); break;
                case 5: value.setRecupero(Boolean.FALSE); break;
                case 6: value.setSurge(Boolean.FALSE); break;
                case 7: value.setTexto("texto %_' parametrizado"); break;
                case 8: value.setFechaAltaDesde(new Date(1727740800000L)); break;
                case 9: value.setFechaAltaHasta(new Date(1728604800000L)); break;
                case 10: value.setIdRequerimientoCompra(Integer.valueOf(99)); break;
                case 11: value.setIdEstado(Integer.valueOf(0)); break;
                case 12: value.setIdSector(Integer.valueOf(-1)); break;
                case 13: value.setAfiliadoInt(Integer.valueOf(-1)); break;
                case 14: value.setIdRequerimientoCompra(Integer.valueOf(0)); break;
                case 15: value.setRecupero(Boolean.TRUE); break;
                case 16: value.setSurge(Boolean.TRUE); break;
                default: throw new AssertionError("unknown filter case");
            }
            reset(rows(900, 1, 1)); paginated(value, false, 2);
            verifyQuery(false, 2); verifyFilter(jdbc.queries.get(0), value, 0, value.getIdEstado()); verifyClosed(1, 1, 1);
        }
        reset(rows(900, 1, 1)); paginated(null, true, 1);
        verifyQuery(true, 1); verifyClosed(1, 1, 1);
    }

    private static void testInitialFailures() throws Exception {
        String[] failures = new String[] {"prepare", "execute", "map"};
        for (int i = 0; i < failures.length; i++) {
            reset(rows(1, 1, 1)); jdbc.failAt = failures[i]; Exception failure = null;
            try { paginated(null, false, 1); } catch (Exception expected) { failure = expected; }
            check(failure != null && failure.getMessage().indexOf("offline injected") >= 0, "initial " + failures[i] + " failure propagated");
            verifyClosed(1, i == 0 ? 0 : 1, i == 2 ? 1 : 0);
        }
    }
    private static void testFiltersAndRp() throws Exception {
        RequerimientoCompraFiltro filtro = fullFilter();
        reset(rows(999, 4, 73));
        List<RequerimientoCompra> actual = paginated(filtro, false, 2);
        eq("filters page result", Integer.valueOf(4), Integer.valueOf(actual.size()));
        verifyFilter(jdbc.queries.get(0), filtro, 0, filtro.getIdEstado());
        verifyQuery(false, 2); verifyClosed(1, 1, 1);

        List<Map<String, Object>> mixed = rows(999, 6, 83);
        for (int i = 0; i < mixed.size(); i++) mixed.get(i).put("id_estado", Integer.valueOf(i % 2 == 0 ? 3 : ConfiguracionComprasHelper.getEstadoReclamoRP()));
        reset(mixed);
        actual = paginated(filtro, true, 2);
        eq("RP merged result size", Integer.valueOf(6), Integer.valueOf(actual.size()));
        eq("RP state preserved in caller", Integer.valueOf(3), filtro.getIdEstado());
        for (int i = 0; i < actual.size(); i++) {
            eq("RP global row order " + i, Integer.valueOf(999 - i), Integer.valueOf(actual.get(i).getIdRequerimientoCompra()));
            eq("RP total mapped " + i, Integer.valueOf(83), Integer.valueOf(actual.get(i).getTotalRegistros()));
            eq("RP state mapped " + i, mixed.get(i).get("id_estado"), actual.get(i).getIdEstado());
        }
        verifyFilter(jdbc.queries.get(0), filtro, 0, Integer.valueOf(3));
        verifyFilter(jdbc.queries.get(0), filtro, 12, Integer.valueOf(ConfiguracionComprasHelper.getEstadoReclamoRP()));
        verifyQuery(true, 2); verifyClosed(1, 1, 1);

        reset(rows(500, 1, 1));
        filtro.setFechaAltaHasta(filtro.getFechaAltaDesde());
        actual = paginated(filtro, false, 1);
        eq("equal dates accepted", Integer.valueOf(1), Integer.valueOf(actual.size()));
        verifyFilter(jdbc.queries.get(0), filtro, 0, filtro.getIdEstado());
    }

    private static void testValidationAndFailures() throws Exception {
        RequerimientoCompraFiltro filtro = fullFilter();
        filtro.setFechaAltaDesde(new Date(3000)); filtro.setFechaAltaHasta(new Date(2000));
        reset(new ArrayList<Map<String, Object>>());
        Exception failure = null;
        try { paginated(filtro, true, 1); } catch (Exception expected) { failure = expected; }
        check(failure != null && failure.getMessage() != null, "inverted dates explicit failure");
        eq("inverted dates before connection", Integer.valueOf(0), Integer.valueOf(ConnectionHelper.acquisitions));
        eq("inverted dates state preserved", Integer.valueOf(3), filtro.getIdEstado());

        String[] failures = new String[] {"prepare", "execute", "map"};
        for (int i = 0; i < failures.length; i++) {
            reset(rows(1, 1, 1)); jdbc.failAt = failures[i]; failure = null;
            filtro = fullFilter(); try { paginated(filtro, true, 1); } catch (Exception expected) { failure = expected; }
            check(failure != null, failures[i] + " failure propagated"); eq("caller state after " + failures[i], Integer.valueOf(3), filtro.getIdEstado());
            check(failure.getMessage().indexOf("offline injected") >= 0, failures[i] + " original failure retained");
            verifyClosed(1, i == 0 ? 0 : 1, i == 2 ? 1 : 0);
        }
        reset(rows(1, 1, 1)); ConnectionHelper.failure = new RuntimeException("offline injected connection");
        failure = null;
        try { paginated(new RequerimientoCompraFiltro(), false, 1); } catch (Exception expected) { failure = expected; }
        check(failure != null, "connection failure propagated"); verifyClosed(0, 0, 0);
        ConnectionHelper.failure = null;
    }

    private static void testUnlimitedExport() throws Exception {
        for (int i = 0; i < 2; i++) {
            reset(rows(1000, 123, 123));
            RequerimientoCompraFiltro empty = i == 0 ? null : new RequerimientoCompraFiltro();
            List<RequerimientoCompra> initial = BusquedaRequerimientoCompraServiceUtil.buscarRequerimientosListado(empty, false);
            eq("unfiltered export retains all rows " + i, Integer.valueOf(123), Integer.valueOf(initial.size()));
            eq("unfiltered export retains last row " + i, Integer.valueOf(878), initial.get(122).getId());
            verifyUnlimited(jdbc.queries.get(0));
            verifyFilter(jdbc.queries.get(0), new RequerimientoCompraFiltro(), 0, null);
            verifyClosed(1, 1, 1);
        }
        RequerimientoCompraFiltro filtro = fullFilter();
        reset(rows(1000, 123, 123));
        List<RequerimientoCompra> actual = BusquedaRequerimientoCompraServiceUtil.buscarRequerimientosListado(filtro, false);
        eq("export retains more than 50", Integer.valueOf(123), Integer.valueOf(actual.size()));
        eq("export retains last row", Integer.valueOf(878), Integer.valueOf(actual.get(122).getIdRequerimientoCompra()));
        verifyUnlimited(jdbc.queries.get(0));
        verifyFilter(jdbc.queries.get(0), filtro, 0, filtro.getIdEstado()); verifyClosed(1, 1, 1);

        reset(rows(1000, 80, 120)); jdbc.results.add(rows(2000, 40, 120));
        actual = BusquedaRequerimientoCompraServiceUtil.buscarRequerimientosListado(filtro, true);
        eq("export combined all rows", Integer.valueOf(120), Integer.valueOf(actual.size()));
        eq("export combined global first", Integer.valueOf(2000), Integer.valueOf(actual.get(0).getIdRequerimientoCompra()));
        eq("export combined global last", Integer.valueOf(921), Integer.valueOf(actual.get(119).getIdRequerimientoCompra()));
        eq("export caller state restored", Integer.valueOf(3), filtro.getIdEstado());
        eq("export two calls", Integer.valueOf(2), Integer.valueOf(jdbc.queries.size()));
        verifyUnlimited(jdbc.queries.get(0)); verifyUnlimited(jdbc.queries.get(1));
        verifyFilter(jdbc.queries.get(1), filtro, 0, Integer.valueOf(ConfiguracionComprasHelper.getEstadoReclamoRP()));
        verifyClosed(2, 2, 2);
    }

    private static List<RequerimientoCompra> paginated(RequerimientoCompraFiltro value, boolean rp, int page) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.buscarRequerimientosListado(value, rp, page);
    }

    private static RequerimientoCompraFiltro fullFilter() {
        RequerimientoCompraFiltro value = new RequerimientoCompraFiltro();
        value.setIdEstado(Integer.valueOf(3)); value.setIdSector(Integer.valueOf(8));
        value.setAfiliadoCuilTitular("  20000000001  "); value.setAfiliadoInt(Integer.valueOf(2));
        value.setIdTercerizadora("  TEST-TERCERO  "); value.setRecupero(Boolean.TRUE); value.setSurge(Boolean.FALSE);
        value.setTexto("  prueba %_' parametrizada  ");
        value.setFechaAltaDesde(new Date(1727740800000L)); value.setFechaAltaHasta(new Date(1728604800000L));
        value.setIdRequerimientoCompra(Integer.valueOf(999));
        return value;
    }

    private static void verifyFilter(Query query, RequerimientoCompraFiltro value, int shift, Integer state) {
        verifyInteger(query, 1 + shift, state); verifyInteger(query, 2 + shift, value.getIdSector());
        verifyBound(query, 3 + shift, "setString", value.getAfiliadoCuilTitular()); verifyInteger(query, 4 + shift, value.getAfiliadoInt());
        verifyBound(query, 5 + shift, "setString", value.getIdTercerizadora()); verifyBoolean(query, 6 + shift, value.getRecupero());
        verifyBoolean(query, 7 + shift, value.getSurge()); verifyBound(query, 8 + shift, "setString", value.getTexto());
        verifyDate(query, 9 + shift, value.getFechaAltaDesde()); verifyDate(query, 10 + shift, value.getFechaAltaHasta());
        verifyInteger(query, 11 + shift, value.getIdRequerimientoCompra());
        verifyBound(query, 12 + shift, "setInt", Integer.valueOf(99));
    }
    private static void verifyInteger(Query query, int index, Integer value) {
        if (value == null || value.intValue() < 0) verifyBound(query, index, "setNull", Integer.valueOf(Types.INTEGER));
        else verifyBound(query, index, "setInt", value);
    }
    private static void verifyBoolean(Query query, int index, Boolean value) {
        if (value == null) verifyBound(query, index, "setNull", Integer.valueOf(Types.BOOLEAN));
        else verifyBound(query, index, "setBoolean", value);
    }
    private static void verifyDate(Query query, int index, Date value) {
        if (value == null) verifyBound(query, index, "setNull", Integer.valueOf(Types.DATE));
        else verifyBound(query, index, "setDate", new java.sql.Date(value.getTime()));
    }
    private static void verifyBound(Query query, int index, String method, Object value) {
        Bound bound = query.binds.get(Integer.valueOf(index)); check(bound != null, "parameter " + index + " bound");
        eq("parameter " + index + " type", method, bound.method); eq("parameter " + index + " value", value, bound.value);
    }
    private static void verifyQuery(boolean rp, int page) {
        eq("one paginated query", Integer.valueOf(1), Integer.valueOf(jdbc.queries.size()));
        Query query = jdbc.queries.get(0); String sql = normalized(query.sql);
        eq("uses prepareStatement", "prepareStatement", query.preparation);
        check(sql.indexOf("with ") == 0, "uses CTE");
        check(sql.indexOf("count(") >= 0 && sql.indexOf("total_registros") >= 0, "SQL counts matching total");
        check(sql.matches("(?s).*order by (?:[a-z_]+\\.)?id desc limit \\? offset \\?.*"), "global descending bounded ordering");
        eq("union only for RP", Boolean.valueOf(rp), Boolean.valueOf(sql.indexOf("union all") >= 0));
        eq("raw data selections", Integer.valueOf(rp ? 2 : 1), Integer.valueOf(occurrences(sql, "from compras.requerimiento_base()")));
        int filterParams = rp ? 24 : 12;
        eq("SQL placeholder count", Integer.valueOf(filterParams + 2), Integer.valueOf(occurrences(sql, "?")));
        eq("bound parameter count", Integer.valueOf(filterParams + 2), Integer.valueOf(query.binds.size()));
        verifyBound(query, filterParams + 1, "setInt", Integer.valueOf(50));
        verifyBound(query, filterParams + 2, "setLong", Long.valueOf(((long) page - 1L) * 50L));
        check(sql.indexOf("prueba") < 0 && sql.indexOf("20000000001") < 0, "filters are not interpolated into SQL");
    }
    private static void verifyUnlimited(Query query) {
        String sql = normalized(query.sql);
        eq("export retains prepared query", "prepareStatement", query.preparation);
        check(sql.indexOf("limit") < 0 && sql.indexOf("offset") < 0, "export SQL remains unlimited");
        eq("export function args", Integer.valueOf(12), Integer.valueOf(occurrences(sql, "?")));
        eq("export binds", Integer.valueOf(12), Integer.valueOf(query.binds.size()));
    }
    private static void verifyClosed(int connections, int statements, int results) {
        eq("connections closed", Integer.valueOf(connections), Integer.valueOf(jdbc.closedConnections));
        eq("statements closed", Integer.valueOf(statements), Integer.valueOf(jdbc.closedStatements));
        eq("results closed", Integer.valueOf(results), Integer.valueOf(jdbc.closedResults));
    }
    private static String normalized(String value) { return value.toLowerCase(Locale.ENGLISH).replaceAll("\\s+", " ").trim(); }
    private static int occurrences(String value, String needle) { int count = 0, index = 0; while ((index = value.indexOf(needle, index)) >= 0) { count++; index += needle.length(); } return count; }
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }
    private static void eq(String label, Object expected, Object actual) { check(expected == null ? actual == null : expected.equals(actual), label + ": expected=" + expected + " actual=" + actual); }

    private static List<Map<String, Object>> rows(int firstId, int count, int total) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < count; i++) {
            Map<String, Object> row = new HashMap<String, Object>(); row.put("id", Integer.valueOf(firstId - i));
            row.put("id_estado", Integer.valueOf(3)); row.put("total_registros", Integer.valueOf(total)); result.add(row);
        }
        return result;
    }
    private static void reset(List<Map<String, Object>> rows) {
        jdbc = new State(); jdbc.results.add(rows); ConnectionHelper.failure = null; ConnectionHelper.acquisitions = 0;
        ConnectionHelper.connection = (Connection) Proxy.newProxyInstance(ComprasPrimeraCargaTest.class.getClassLoader(), new Class[] {Connection.class}, jdbc);
    }
    private static class Bound { String method; Object value; Bound(String method, Object value) { this.method = method; this.value = value; } }
    private static class Query implements InvocationHandler {
        State state; String sql, preparation; Map<Integer, Bound> binds = new HashMap<Integer, Bound>(); List<Map<String, Object>> rows;
        Query(State state, String sql, String preparation, List<Map<String, Object>> rows) { this.state = state; this.sql = sql; this.preparation = preparation; this.rows = rows; }
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if (name.indexOf("set") == 0 && args != null && args.length >= 2 && args[0] instanceof Integer) { binds.put((Integer) args[0], new Bound(name, args[1])); return null; }
            if ("close".equals(name)) { state.closedStatements++; return null; }
            if ("executeQuery".equals(name)) {
                if ("execute".equals(state.failAt)) throw new SQLException("offline injected execute");
                return Proxy.newProxyInstance(ComprasPrimeraCargaTest.class.getClassLoader(), new Class[] {ResultSet.class}, new Result(state, rows));
            }
            return defaultValue(method.getReturnType());
        }
    }
    private static class State implements InvocationHandler {
        List<List<Map<String, Object>>> results = new ArrayList<List<Map<String, Object>>>(); List<Query> queries = new ArrayList<Query>();
        int closedConnections, closedStatements, closedResults; String failAt;
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if ("close".equals(name)) { closedConnections++; return null; }
            if ("prepareCall".equals(name) || "prepareStatement".equals(name)) {
                if ("prepare".equals(failAt)) throw new SQLException("offline injected prepare");
                Query query = new Query(this, (String) args[0], name, results.get(queries.size())); queries.add(query);
                return Proxy.newProxyInstance(ComprasPrimeraCargaTest.class.getClassLoader(), new Class[] {"prepareCall".equals(name) ? CallableStatement.class : PreparedStatement.class}, query);
            }
            return defaultValue(method.getReturnType());
        }
    }
    private static class Result implements InvocationHandler {
        State state; List<Map<String, Object>> rows; int index = -1; boolean wasNull;
        Result(State state, List<Map<String, Object>> rows) { this.state = state; this.rows = rows; }
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if ("close".equals(name)) { state.closedResults++; return null; }
            if ("next".equals(name)) return Boolean.valueOf(++index < rows.size());
            if ("wasNull".equals(name)) return Boolean.valueOf(wasNull);
            if ("findColumn".equals(name)) { if (!rows.get(index).containsKey(args[0])) throw new SQLException("absent fixture column " + args[0]); return Integer.valueOf(1); }
            if (name.indexOf("get") == 0 && args != null && args.length >= 1) {
                if ("map".equals(state.failAt) && "getInt".equals(name) && "id".equals(args[0])) throw new SQLException("offline injected map");
                Object value = rows.get(index).get(args[0]); wasNull = value == null;
                if ("getInt".equals(name)) return Integer.valueOf(value == null ? 0 : ((Number) value).intValue());
                if ("getLong".equals(name)) return Long.valueOf(value == null ? 0 : ((Number) value).longValue());
                if ("getBoolean".equals(name)) return Boolean.valueOf(value != null && ((Boolean) value).booleanValue());
                if ("getString".equals(name)) return value == null ? null : value.toString();
                return value;
            }
            return defaultValue(method.getReturnType());
        }
    }
    private static Object defaultValue(Class type) {
        if (type == Boolean.TYPE) return Boolean.FALSE;
        if (type == Integer.TYPE) return Integer.valueOf(0);
        if (type == Long.TYPE) return Long.valueOf(0);
        if (type == Short.TYPE) return Short.valueOf((short) 0);
        if (type == Byte.TYPE) return Byte.valueOf((byte) 0);
        if (type == Double.TYPE) return Double.valueOf(0);
        if (type == Float.TYPE) return Float.valueOf(0);
        if (type == Character.TYPE) return Character.valueOf((char) 0);
        return null;
    }
}
