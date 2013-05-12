$(document).ready(function() {
    $$.marqueeAdd($$.eviServerLine);
    $$.marqueeAdd($$.serverEncapsLine);
    $$.marqueeAdd($$.encapsTowerLine);
    $$.animateTracing($$.towerWavesArray);
    var carousel = $('.carousel');
    var demBut =     $("#demontration-btn");
    carousel.carousel({
	interval: false
    });
    demBut.on("click", function() {
	    carousel.carousel(1);
    });
    $('#test-btn').on('click', function() {
	$.ajax("../cmd?cmd=testEvi&packets=" + $("#test-input").val());
	carousel.carousel(0);
	demBut.hide();
    });
});