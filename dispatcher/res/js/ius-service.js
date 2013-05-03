function slideActions() {
    var bitrixRoot = $("#bitrix");
    var serviceLines = [$$.bankServerLine, $$.shopServerLine, $$.govServerLine, $$.eviServerLine];
    var currentActiveLine = 0;
    var wavesAnimation = $$.animateTracing($$.towerWavesArray);
    var remoteAnimation;
    var carousel = $('.carousel');
    $$.marqueeAdd($$.serverEncapsLine, false);
    var lineAnimaton = window.setInterval(function() {
	$$.marqueeDelete(serviceLines[currentActiveLine]);
	if(currentActiveLine == serviceLines.length -1) currentActiveLine = 0;
	else currentActiveLine++;
	$$.marqueeAdd(serviceLines[currentActiveLine]);
    }, 3000);
    $('#service-order-link').on('click', function(event){
	carousel.carousel(0);
	window.clearInterval(wavesAnimation);
	window.clearInterval(lineAnimaton);
	$($$.towerWavesArray).each($$.showEach);
	$$.marqueeDeleteAll();
	remoteAnimation = $$.animateTracing($$.remoteWavesArray);
	$$.marqueeAdd($$.backChannelLine, true);
    });
    carousel.carousel({
	interval: false
    });
    $('#registration-link').on('click', function(event){
	carousel.carousel(1);
    });
    	carousel.carousel(1);
    bitrixRoot.load(function() {
	    $$.reconnect();
    $$.ws.onmessage = function(m) {
	if (m.data === "Shop submitted event") {
	    $('#neworder-tr', bitrixRoot.contents()).show();
	}
    }
    });
}
