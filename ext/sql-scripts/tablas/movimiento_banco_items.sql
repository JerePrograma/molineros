CREATE TABLE movimiento_banco_items (
	id serial,
    id_movimiento integer,
	nro_cheque numeric(15,0),
	id_banco integer,
	id_estado_cheque_viejo integer,
	id_estado_cheque_nuevo integer,
	recibo_ingreso_id integer,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone,
    modi_usr character varying(15),
    baja_fecha timestamp without time zone,
    baja_usr character varying(15)
);


ALTER TABLE public.movimiento_banco_items OWNER TO postgres;

--

ALTER TABLE ONLY movimiento_banco_items
    ADD CONSTRAINT pk_movimiento_banco_items PRIMARY KEY (id);


--
ALTER TABLE ONLY movimiento_banco_items
    ADD CONSTRAINT fk_movimiento_banco FOREIGN KEY (id_movimiento) REFERENCES movimiento_banco(id_movimiento) MATCH FULL;


--
ALTER TABLE ONLY movimiento_banco_items
    ADD CONSTRAINT fk_id_Estado_viejo FOREIGN KEY (id_estado_cheque_viejo) REFERENCES cheque_estado(id) MATCH FULL;


--
ALTER TABLE ONLY movimiento_banco_items
    ADD CONSTRAINT fk_id_Estado_nuevo FOREIGN KEY (id_estado_cheque_nuevo) REFERENCES cheque_estado(id) MATCH FULL;


ALTER TABLE ONLY movimiento_banco_items 
	ADD CONSTRAINT fk_mov_cheque FOREIGN KEY (nro_cheque, id_banco)  REFERENCES cheque (nro_cheque, id_banco) MATCH FULL;

	
ALTER TABLE ONLY movimiento_banco_items 
	ADD CONSTRAINT fk_mov_cheque_rec_ing FOREIGN KEY (recibo_ingreso_id)  REFERENCES recibo_ingresos (id) MATCH FULL;