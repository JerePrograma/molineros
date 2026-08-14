CREATE OR REPLACE FUNCTION autorizaciones.busca_nomenclador_prest_med_compras(
    tiponomenclador_p integer,
    descripcionnomenclador_p character varying,
    especialidad_p integer,
    codigonomenclador_p character varying,
    recuperasur_p boolean,
    resolucionnomenclador_p character varying
)
RETURNS SETOF autorizaciones.nomenclador_detalle
LANGUAGE 'plpgsql'
COST 100
VOLATILE
ROWS 1000
AS $BODY$

BEGIN

    RETURN QUERY

    SELECT n.*
    FROM autorizaciones.busca_nomenclador(
            tiponomenclador_p,
            NULL,
            especialidad_p,
            codigonomenclador_p,
            recuperasur_p,
            resolucionnomenclador_p
    ) n
    WHERE n.id_tipo_nomenclador <> 1
      AND (
            tiponomenclador_p IS NULL
            OR n.id_tipo_nomenclador = tiponomenclador_p
      )
      AND (
            descripcionnomenclador_p IS NULL
            OR BTRIM(descripcionnomenclador_p) = ''
            OR
            TRANSLATE(
                UPPER(
                    COALESCE(
                        n.descripcion,
                        ''
                    )
                ),
                CHR(193) || CHR(201) || CHR(205) || CHR(211) || CHR(218) || CHR(220) || CHR(209) || CHR(192) || CHR(200) || CHR(204) || CHR(210) || CHR(217) || CHR(196) || CHR(203) || CHR(207) || CHR(214) || CHR(220) || CHR(194) || CHR(202) || CHR(206) || CHR(212) || CHR(219),
                'AEIOUUNAEIOUAEIOUAEIOU'
            )
            LIKE
            '%'
            ||
            TRANSLATE(
                UPPER(
                    BTRIM(
                        descripcionnomenclador_p
                    )
                ),
                CHR(193) || CHR(201) || CHR(205) || CHR(211) || CHR(218) || CHR(220) || CHR(209) || CHR(192) || CHR(200) || CHR(204) || CHR(210) || CHR(217) || CHR(196) || CHR(203) || CHR(207) || CHR(214) || CHR(220) || CHR(194) || CHR(202) || CHR(206) || CHR(212) || CHR(219),
                'AEIOUUNAEIOUAEIOUAEIOU'
            )
            ||
            '%'
      );

END;

$BODY$;

ALTER FUNCTION autorizaciones.busca_nomenclador_prest_med_compras(
    integer,
    character varying,
    integer,
    character varying,
    boolean,
    character varying
)
OWNER TO postgres;
