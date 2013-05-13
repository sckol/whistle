$(document).ready(function() {
    var svgRoot = document.getElementById("plan").contentDocument.documentElement;
    $$.$ = function(path) {
        return $(path, svgRoot);
    }
    var control = $('#control');
    var colors = ['#44aa00', '#ffcc00', '#ff0000'];
    function changeSensorState(id, state) {
        if (state >=0 && state < colors.length) {
            $$.$('#sens' + id).fill(colors[state]);
        }
    }
    function changeControlState(state) {
        if (state >=0 && state < colors.length) {
            control.css('backgroundColor', colors[state]);
        }
    }
    function changeLocation(newLocation) {
        if (newLocation == 208) {
            $$.$('#num008').text("8 чел.");
            $$.$('#num006').text("7 чел.");
        } else {
            $$.$('#num008').text("7 чел.");
            $$.$('#num006').text("8 чел.");
        }
    }
    $$.reconnect();
    $$.wsMessageListener = function(m) {
        var msg = JSON.parse(m.data);
        if (msg.type === "SensorChangedEvent") {
            changeSensorState(msg.sensorId, msg.buttonPressed);
        } else if (msg.type === "UserLocationChangedEvent") {
            changeLocation(msg.newLocation);
        } else if (msg.type === "StateChangedEvent") {
	    alert("lllo" + msg.newState);
	    changeControlState(msg.newState);
        }
    }

    changeLocation(208);
    changeControlState(0);
    changeSensorState("006", 1);
});
