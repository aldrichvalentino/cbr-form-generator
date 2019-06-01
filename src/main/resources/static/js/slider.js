//--- Normalized Query Section ---//
const defaultValue = 1;

var formNameWeight = document.getElementsByName("formNameWeight")[0];
var inputFieldsWeight = document.getElementsByName("inputFieldsWeight")[0];
var outputFieldsWeight = document.getElementsByName("outputFieldsWeight")[0];
var controlButtonsWeight = document.getElementsByName("controlButtonsWeight")[0];

var formNameValue = document.getElementById("formNameValue");
var inputFieldsValue = document.getElementById("inputFieldsValue");
var outputFieldsValue = document.getElementById("outputFieldsValue");
var controlButtonsValue = document.getElementById("controlButtonsValue");

// set value to default value
formNameWeight.value = defaultValue;
inputFieldsWeight.value = defaultValue;
outputFieldsWeight.value = defaultValue;
controlButtonsWeight.value = defaultValue;

formNameValue.innerHTML = formNameWeight.value;
inputFieldsValue.innerHTML = inputFieldsWeight.value;
outputFieldsValue.innerHTML = outputFieldsWeight.value;
controlButtonsValue.innerHTML = controlButtonsWeight.value;

formNameWeight.oninput = function () {
    formNameValue.innerHTML = this.value;
}
inputFieldsWeight.oninput = function () {
    inputFieldsValue.innerHTML = this.value;
}
outputFieldsWeight.oninput = function () {
    outputFieldsValue.innerHTML = this.value;
}
controlButtonsWeight.oninput = function () {
    controlButtonsValue.innerHTML = this.value;
}