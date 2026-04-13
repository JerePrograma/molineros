// ============= DisableKeys.js =============//

// Keys to be disabled can be added to the lists below.
// The number is the key code for the particular key
// and the text is the description displayed in the
// status window if the key [combination] is pressed.

var badKeys = new Object();
badKeys.single = new Object();
badKeys.single['8'] = 'Backspace outside text fields';
badKeys.single['13'] = 'Enter outside text fields';

badKeys.alt = new Object();
badKeys.alt['37'] = 'Alt+Left Cursor';
//badKeys.alt['39'] = 'Alt+Right Cursor';
//badKeys.alt['122'] = 'F11 (Full Screen)';

badKeys.ctrl = new Object();
badKeys.ctrl['78'] = 'Ctrl+N';
badKeys.ctrl['79'] = 'Ctrl+O';

function checkKeyCode(type, code) {
if (badKeys[type][code]) {
return true;
} else {
return false;
}
}
function getKeyText(type, code) {
return badKeys[type][code];
}

var ie=document.all;
var w3c=document.getElementById&&!document.all;

function keyEventHandler(evt) {
this.target = evt.target || evt.srcElement;
this.keyCode = evt.keyCode || evt.which;
var targtype = this.target.type;
if (w3c) {
if (document.layers) {
this.altKey = ((evt.modifiers & Event.ALT_MASK) > 0);
this.ctrlKey = ((evt.modifiers & Event.CONTROL_MASK) > 0);
this.shiftKey = ((evt.modifiers & Event.SHIFT_MASK) > 0);
} else {
this.altKey = evt.altKey;
this.ctrlKey = evt.ctrlKey;
}
// Internet Explorer
} else {
this.altKey = evt.altKey;
this.ctrlKey = evt.ctrlKey;
}
// Find out if we need to disable this key combination
var badKeyType = "single";
if (this.ctrlKey) {
badKeyType = "ctrl";
} else if (this.altKey) {
badKeyType = "alt";
}
if (checkKeyCode(badKeyType, this.keyCode)) {
return cancelKey(evt, this.keyCode, this.target, getKeyText(badKeyType, this.keyCode));
}
}

function cancelKey(evt, keyCode, target, keyText) {
if (keyCode==8) { //keyCode==13) {	
 //don't want to disable Backspace	 
 if (target.type == "text" || target.type == "textarea") {
 window.status = "";
 return true;
 }
 }
if (evt.preventDefault) {
evt.preventDefault();
evt.stopPropagation();
} else {
evt.keyCode = 0;
evt.returnValue = false;
}
window.status = keyText+" is disabled";
return false;
}
// ============= DisableKeys.js =============//
