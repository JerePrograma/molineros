/*
*** Multiple dynamic combo boxes
*** by Mirko Elviro, 9 Mar 2005
*** Script featured and available on JavaScript Kit (http://www.javascriptkit.com)
***
***Please do not remove this comment
*/

// This script supports an unlimited number of linked combo boxed
// Their id must be "combo_0", "combo_1", "combo_2" etc.
// Here you have to put the data that will fill the combo boxes
// ie. data_2_1 will be the first option in the second combo box
// when the first combo box has the second option selected


//// first combo box
//	data_1 = new Option("1", "$");
//	data_2 = new Option("2", "$$");
//
//// second combo box
//
//	data_1_1 = new Option("11", "-");
//	data_1_2 = new Option("12", "-");
//	data_2_1 = new Option("21", "--");
//	data_2_2 = new Option("22", "--");
//	data_2_3 = new Option("23", "--");
//	data_2_4 = new Option("24", "--");
//	data_2_5 = new Option("25", "--");
//
//// third combo box
//
//	data_1_1_1 = new Option("111", "*");
//	data_1_1_2 = new Option("112", "*");
//	data_1_1_3 = new Option("113", "*");
//	data_1_2_1 = new Option("121", "*");
//	data_1_2_2 = new Option("122", "*");
//	data_1_2_3 = new Option("123", "*");
//	data_1_2_4 = new Option("124", "*");
//	data_2_1_1 = new Option("211", "**");
//	data_2_1_2 = new Option("212", "**");
//	data_2_2_1 = new Option("221", "**");
//	data_2_2_2 = new Option("222", "**");
//	data_2_3_1 = new Option("231", "***");
//	data_2_3_2 = new Option("232", "***");
//
//// fourth combo box
//
//	data_2_2_1_1 = new Option("2211","%")
//	data_2_2_1_2 = new Option("2212","%%")

// other parameters

    displaywhenempty=""
    valuewhenempty=-1

    displaywhennotempty="-select-"
    valuewhennotempty=0


function change(currentbox) {
	numb = currentbox.id.split("_");
/*	alert('numb: '+numb);*/
	currentbox = numb[1];
/*	alert('numb[1]: '+currentbox);*/
    i=parseInt(currentbox)+1

// I empty all combo boxes following the current one

    while ((eval("typeof(document.getElementById(\"combo_"+i+"\"))!='undefined'")) &&
           (document.getElementById("combo_"+i)!=null)) {
         son = document.getElementById("combo_"+i);
	     // I empty all options except the first one (it isn't allowed)
	     for (m=son.options.length-1;m>0;m--) son.options[m]=null;
	     // I reset the first option
	     son.options[0]=new Option(displaywhenempty,valuewhenempty)
	     i=i+1
    }


// now I create the string with the "base" name ("stringa"), ie. "data_1_0"
// to which I'll add _0,_1,_2,_3 etc to obtain the name of the combo box to fill

    stringa='data'
    i=0
    while ((eval("typeof(document.getElementById(\"combo_"+i+"\"))!='undefined'")) &&
           (document.getElementById("combo_"+i)!=null)) {
           eval("stringa=stringa+'_'+document.getElementById(\"combo_"+i+"\").selectedIndex")
           if (i==currentbox) break;
           i=i+1
    }


// filling the "son" combo (if exists)

    following=parseInt(currentbox)+1

    if ((eval("typeof(document.getElementById(\"combo_"+following+"\"))!='undefined'")) &&
       (document.getElementById("combo_"+following)!=null)) {
       son = document.getElementById("combo_"+following);
       stringa=stringa+"_"
       i=0
       while ((eval("typeof("+stringa+i+")!='undefined'")) || (i==0)) {

       // if there are no options, I empty the first option of the "son" combo
	   // otherwise I put "-select-" in it

	   	  if ((i==0) && eval("typeof("+stringa+"0)=='undefined'"))
	   	      if (eval("typeof("+stringa+"1)=='undefined'"))
	   	         eval("son.options[0]=new Option(displaywhenempty,valuewhenempty)")
	   	      else
	             eval("son.options[0]=new Option(displaywhennotempty,valuewhennotempty)")
	      else
              eval("son.options["+i+"]=new Option("+stringa+i+".text,"+stringa+i+".value)")
	      i=i+1
	   }
       //son.focus()
       i=1
       combostatus=''
       cstatus=stringa.split("_")
       while (cstatus[i]!=null) {
          combostatus=combostatus+cstatus[i]
          i=i+1
          }
       return combostatus;
    }
}