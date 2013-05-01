function slideActions() {
    var serviceLines = [$$.bankServerLine, $$.shopServerLine, $$.govServerLine, $$.eviServerLine];
    var currentActiveLine = 0;
    $$.marqueeAdd($$.serverEncapsLine, false);
    $$.animateTracing($$.towerWavesArray);
    window.setInterval(function() {
	$$.marqueeDelete(serviceLines[currentActiveLine]);
	if(currentActiveLine == serviceLines.length -1) currentActiveLine = 0;
	else currentActiveLine++;
	$$.marqueeAdd(serviceLines[currentActiveLine]);
    }, 3000);
}