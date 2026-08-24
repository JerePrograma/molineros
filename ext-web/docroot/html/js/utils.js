/**
 * Trim de cadenas de string en javascript
 */
function trim(cadena)
{		
	for(i=0; i<cadena.length; )
	{
		if(cadena.charAt(i)==" ")
			cadena=cadena.substring(i+1, cadena.length);
		else
			break;
	}
	for(i=cadena.length-1; i>=0; i=cadena.length-1)
	{
		if(cadena.charAt(i)==" ")
			cadena=cadena.substring(0,i);
		else
			break;
	}	
	return cadena;
}

/**
 * Valida CUILS (sólo numérico y de 11 chars)
 */
function validarCuil(input, message){	
	if (input.trim() == "00000000000"){
		return true;
	}
	
	if(input.trim().length>0){		
		if(isPositiveInteger(input)){			
			if(input.trim().length==11){				
				return true;
			}else{				
				alert(message);
				return false;
			}
		}else{
			alert(message);
			return false;
		}
	}else{
			
		return false;
	}
}

function esCUITValida(inputValor) {
    inputString = inputValor.toString()
    if (inputString.length == 11) {
        var Caracters_1_2 = inputString.charAt(0) + inputString.charAt(1)
        if (Caracters_1_2 == "20" || Caracters_1_2 == "23" || Caracters_1_2 == "24" || Caracters_1_2 == "27" || Caracters_1_2 == "30" || Caracters_1_2 == "33" || Caracters_1_2 == "34") {
            var Count = inputString.charAt(0) * 5 + inputString.charAt(1) * 4 + inputString.charAt(2) * 3 + inputString.charAt(3) * 2 + inputString.charAt(4) * 7 + inputString.charAt(5) * 6 + inputString.charAt(6) * 5 + inputString.charAt(7) * 4 + inputString.charAt(8) * 3 + inputString.charAt(9) * 2 + inputString.charAt(10) * 1
            Division = Count / 11;
            if (Division == Math.floor(Division)) {
                return true
            }
        }
    }
    return false
}


/**
 * deshabilita el evento submit cuando se presiona enter
 */
function disableEnterKey(e)
{
     var key;      
     if(window.event)
          key = window.event.keyCode; //IE
     else
          key = e.which; //firefox      

     return (key != 13);
}

function validarMail(email) {
	   var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
	   if(reg.test(email) == false) {
	      alert('Email invalido');
	      return false;
	   }
	   return true;
	}

function allowOnlyDigits(e){
	var unicode=e.keyCode? e.keyCode : e.charCode;
	if ((unicode >= 48 && unicode <= 57) || unicode == 9 || unicode ==8 ||
			unicode==13 || unicode == 17 || unicode == 16 || unicode == 86 || (unicode >= 37 && unicode <=40)|| (unicode >= 96 && unicode <=110)) {
	    return true;
	}
	if (window.event) {
		window.event.returnValue=false;
	} else {
		e.preventDefault();
	}
	return false;
}

function allowOnlyDigitsAndDecimals(e){
	var unicode=e.keyCode? e.keyCode : e.charCode;
	if ((unicode >= 48 && unicode <= 57) || unicode == 9 || unicode ==8 || unicode ==190 ||
			unicode==13 || unicode == 17 || unicode == 16 || (unicode >= 37 && unicode <=40) || (unicode >= 96 && unicode <=110)) {
	    return true;
	}
	if (window.event) {
		window.event.returnValue=false;
	} else {
		e.preventDefault();
	} 
	return false;
}

function IsNumeric(expression) {
	if (expression != null){
		var nums = "0123456789.-";
		if (expression.length==0)
			return(false);
		for (var n=0; n < expression.length; n++){
		if(nums.indexOf(expression.charAt(n))==-1)
			return(false);
		}
	}
	return(true);

}
function limitDecimals(cant, input, e){
	var unicode=e.keyCode? e.keyCode : e.charCode;	
	if (unicode == 46 || unicode==8 || unicode == 9 || unicode == 16) {	
	    return true;
	}
	if (input.value.indexOf(".") != -1){
		if (input.value.substring(input.value.indexOf(".")).length >= (cant+1)){		
			if (window.event) {
				window.event.returnValue=false;				
			} else {
				e.preventDefault();
			} 
			return false;
		}
	}
	return true;
}

function seleccionarSelect(nombreSelect, valueParam){
	var sel  = document.getElementById(nombreSelect);
	var len = sel.options.length;
	for (var i = 0; i<len; i++){
		if (sel.options[i].value == valueParam){
			sel.selectedIndex = i;
		}
	}
}

function agregarCeros(el){
	if (trim(el.value) != ""){
		if (el.value.indexOf(".") == -1){
			el.value = el.value + ".00";
		}
	}
}

function ismaxlength(obj){
	var mlength=obj.getAttribute? parseInt(obj.getAttribute("maxlength")) : ""
	if (obj.getAttribute && obj.value.length>mlength)
	obj.value=obj.value.substring(0,mlength)
}	

function roundNumber(number) { // Arguments: number to round, number of decimal places 	
	var newnumber = (Math.round(number*100))/100;	
	return parseFloat(newnumber); // Output the result to the form field (change for your purposes)
}

function allowOnlyDigitsConSuprimir(e){
	var unicode=e.keyCode? e.keyCode : e.charCode;
	if ((unicode >= 48 && unicode <= 57) || unicode == 9 || unicode ==8 || unicode ==46 ||
			unicode==13 || unicode == 17 || unicode == 16 || unicode == 86 
			|| (unicode >= 37 && unicode <=40)|| (unicode >= 96 && unicode <=110) || unicode == 173) {
	    return true;
	}
	if (window.event) {
		window.event.returnValue=false;
	} else {
		e.preventDefault();
	}
	return false;
}

function allowOnlyDigitsAndDecimalsConSuprimir(e){
	var unicode=e.keyCode? e.keyCode : e.charCode;
	if ((unicode >= 48 && unicode <= 57) || unicode == 9 || unicode ==8 || unicode ==190 || unicode ==46 ||
			unicode==13 || unicode == 17 || unicode == 16 || (unicode >= 37 && unicode <=40) || (unicode >= 96 && unicode <=110)) {
	    return true;
	}
	if (window.event) {
		window.event.returnValue=false;
	} else {
		e.preventDefault();
	} 
	return false;
}

function validarCBU(input, message){	
	if(input.trim().length==22){
		a=input.substring(0,1);
		b=input.substring(1,2);
		c=input.substring(2,3);
		d=input.substring(3,4);
		
		
		if(a+b+c==0){
			alert('ERROR AL VALIDAR CBU. El código de Banco de las 3 primeras posiciones no pueden ser Ceros');
			return false;
		}
		
		q=input.substring(4,5);
		r=input.substring(5,6);
		s=input.substring(6,7);
		
		valida1=input.substring(7,8);
		//alert(a+' '+b+' '+c+' '+d+' '+q+' '+r+' '+s);
		
		suma1=a*7+b*1+c*3+d*9+q*7+r*1+s*3;
		cadenaVal=suma1.toString().substring(suma1.toString().length-1,suma1.toString().length);
		diferencia1= 10-parseInt(cadenaVal);
		
		if(diferencia1==10){
            diferencia1=0;
    	}
		
		if(valida1!=diferencia1){
			alert('ERROR AL VALIDAR CBU, VERIFIQUE NUMEROS');
			return false;				
		}
		
		a=input.substring(8,9);
		b=input.substring(9,10);
		c=input.substring(10,11);
		d=input.substring(11,12);
		e=input.substring(12,13);
		f=input.substring(13,14);
		g=input.substring(14,15);
		h=input.substring(15,16);
		i=input.substring(16,17);
		j=input.substring(17,18);
		k=input.substring(18,19);
		l=input.substring(19,20);
		m=input.substring(20,21);
		
		//alert(a+' '+b+' '+c+' '+d+' '+e+' '+f+' '+g+' '+h+' '+i+' '+j+' '+k+' '+l+' '+m);
		valida2=input.substring(21,22);
		
		suma2=a*3+b*9+c*7+d*1+e*3+f*9+g*7+h*1+i*3+j*9+k*7+l*1+m*3;
		
		cadenaVal2=suma2.toString().substring(suma2.toString().length-1,suma2.toString().length);
		diferencia2= 10-parseInt(cadenaVal2);			
		
		if(diferencia2==10){
            diferencia2=0;
    	}
		
		if(valida2!=diferencia2){
			alert('Ha ingresado un CBU inválido, por favor, verifique dígitos ingresados');
			return false;				
		}
		
		
		if(isPositiveInteger(input)){
			return true
		}		
	}
	alert(message);
	return false;		
}

	 