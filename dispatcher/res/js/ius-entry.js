$(document).ready(function() {
    var signBtnPressed = false;
    $$.entryLayer.show();
    $$.signBtn.hide();
    $$.shopServerLine.hide();
    $$.shopTrigger.pointerCursor();
    $$.shopCardTextIcon.shade();
    $$.shopCardInTvIcon.hide();
    $$.shopTrigger.mouseover(function() {
	    if(!signBtnPressed) {
		$$.shopCardTextIcon.hide();
		$$.signBtn.show();
	    }
	});
    $$.shopTrigger.mouseleave(function() {
	if(!signBtnPressed) {
	    $$.shopCardTextIcon.show();	   
	    $$.signBtn.hide();
	}
    });
	$$.shopTrigger.click(function() {
	    $.ajax("../cmd?cmd=submitShop");
	    signBtnPressed = true;
	    $$.signBtn.hide();
	    $$.shopCardTextIcon.unshade();
	    $$.shopCardTextIcon.show();
	    $$.shopTrigger.defaultCursor();
	    $$.shopServerLine.show();
	    $$.marqueeAdd($$.shopServerLine);
	    $$.marqueeAdd($$.serverEncapsLine);
	    $$.marqueeAdd($$.encapsTowerLine);
	    $$.animateTracing($$.towerWavesArray);
	    setTimeout(function() {
		$$.shopCardInTvIcon.attr('opacity', 0).show().animate(
		    {svgOpacity: 1}, 2000);
	    }, 3000);
	});
    $('.carousel').carousel({
	interval: false
    });
    $("#demontration-btn").on("click", function demonstration() {
	$('.carousel').carousel(1);
    });
});