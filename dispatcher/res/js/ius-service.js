$(document).ready(function() {
    var bitrixRoot = $("#bitrix");
    var serviceLines = [$$.bankServerLine, $$.shopServerLine, $$.govServerLine, $$.eviServerLine];
    var wavesAnimation = $$.animateTracing($$.towerWavesArray);
    var remoteAnimation;
    var carousel = $('.carousel');
    var lineAnimaton;
    var nextActiveLine = 0;
    function changeLine() {
        $$.marqueeDelete(serviceLines[nextActiveLine]);
	var prevLine =  $$.marqueeDelete(serviceLines[nextActiveLine ? nextActiveLine -1 : serviceLines.length -1]);
	$$.marqueeAdd(serviceLines[nextActiveLine]);
        nextActiveLine = (nextActiveLine == serviceLines.length -1 ) ? 0 : nextActiveLine+1;
    }
    carousel.carousel({
        interval: false
    });
    function updateServiceLink(event) {
        carousel.carousel(0);
	$$.marqueeDeleteAll();
	window.clearInterval(remoteAnimation);
        $$.marqueeAdd($$.serverEncapsLine, false);
	$$.marqueeAdd($$.encapsTowerLine, false);
	changeLine();
        lineAnimaton = window.setInterval(changeLine , 2000);
    };
    updateServiceLink();
    $('#update-service-link').on('click', updateServiceLink);
    $('#service-order-link').on('click', function(event) {
        carousel.carousel(0);
        window.clearInterval(wavesAnimation);
        window.clearInterval(lineAnimaton);
        $($$.towerWavesArray).each($$.showEach);
        $$.marqueeDeleteAll();
        remoteAnimation = $$.animateTracing($$.remoteWavesArray);
        $$.marqueeAdd($$.backChannelLine, true);
    });
    $('#registration-link').on('click', function(event){
        carousel.carousel(1);
    });
    bitrixRoot.load(function() {
        $$.reconnect();
        $$.wsMessageListener = function(m) {
            var msg = JSON.parse(m.data);
            if (msg.type === "ShopOrderEvent") {
                $('#neworder-tr', bitrixRoot.contents()).show();
                $('#order-name', bitrixRoot.contents()).text(msg.position);
                $('#order-price', bitrixRoot.contents()).text(msg.price);
                $('#order-id', bitrixRoot.contents()).text(msg.positionId);
            }
        }
    });
});
