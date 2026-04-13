CREATE OR REPLACE FUNCTION split_cadena_str(cadena character varying, separador character varying)
  RETURNS SETOF varchar AS
$BODY$
declare posicion_inicial integer;
declare posicion_final integer;
declare cadena_aux varchar;
BEGIN
drop table if exists aux;
posicion_inicial=1;
posicion_final=position(separador in cadena);
cadena_aux=cadena;
create temp table aux (valor varchar);
if posicion_final<>0  or length(cadena_aux)<>0 then
	LOOP
		RAISE INFO 'POSICION INICIAL= %',posicion_inicial;
		RAISE INFO 'POSICION FINAL= %',posicion_final;
		RAISE INFO 'CADENA AUX= %',cadena_aux;	
		--RAISE INFO 'SUBSTRING= %', substring(cadena_aux from posicion_inicial for posicion_final-1);			
		if length(cadena_aux)>0 and posicion_final>0 then			
			insert into aux values (cast(substring(cadena_aux from posicion_inicial for posicion_final-1) as varchar));	
			cadena_aux=substring(cadena_aux,posicion_final+1,length(cadena_aux)-(posicion_final-1));	
		else if posicion_final<0 then			
			insert into aux values (cast(substring(cadena_aux from posicion_inicial for length(cadena_aux)) as varchar));	
			cadena_aux='0';	
		end if;
		end if;
		
		if(posicion_final=0) then		   
		   EXIT;
		END IF;		
		
		posicion_final=position(separador in cadena_aux);
		
	END LOOP;
END IF;	
return query
select * from aux;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

