update reintegro set id_reintegro_user = id_reintegro where tipo_reintegro = 'pre'

select max(id_reintegro_user) from reintegro where tipo_reintegro = 'pre'

drop sequence reintegro_user_id_seq

CREATE SEQUENCE reintegro_user_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START select max(id_reintegro_user)+1 from reintegro where tipo_reintegro = 'pre'
  CACHE 1;
ALTER TABLE reintegro_user_id_seq OWNER TO postgres;