$(document).ready(function(){
    var svgObj = document.getElementById("svg");
	$.fn.fill = function(color) {
	    return this.each(function() {
		return $(this).css("fill", color);
	    });
	}
	$.fn.stroke = function(color) {
	    return this.each(function() {
		return $(this).css("stroke", color);
	    });
	}

	$.fn.shade = function() {
	    return this.each(function() {
		$(this).fill("DarkGray");
		});
	}
	$.fn.unshade = function() {
	    return this.each(function() {
		$(this).fill("Black");
	    });
	}
	$.fn.pointerCursor = function() {
	    return this.each(function() {
		$(this).css("cursor", "pointer");
	    });
	}
	$.fn.defaultCursor = function() {
	    return this.each(function() {
		$(this).css("cursor", "default");
	    });
	}
	function $$(path) {
	    return $(path, svgRoot);
	}
	function marqueeAdd(pathObject, direction) {
	    pathObject.css("stroke-dasharray", "7, 7");
	    marqueeList[pathObject.selector]={pathObject:pathObject, direction:direction};
	}
	function marqueeUpdtate() {
	    $.each(marqueeList, function(key, value) {
		value.pathObject.css("stroke-dashoffset", value.direction ? marquee : -marquee);
	    });
	marquee++;
	}
	function animateTracing(frameArray) {
	    var currentFrame = frameArray.length;
	    window.setInterval(function() {
		if(currentFrame == frameArray.length) {
		    currentFrame = 0;
		    $(frameArray).each(function(key, value) {value.hide()});
		} else {
		    frameArray[currentFrame++].show();
		}
	    }, 500);
	}

	var marqueeList={};
	var marquee=0;
	var signBtnPressed = false;
	
	var svgRoot = svgObj.contentDocument.documentElement;
	var signBtn = $$("#sign-btn");
	var shopTrigger = $$("#shop-trigger");
	var shopCardTextIcon = $$("#shop-card-texticon");
	var shopCardText = $$("#shop-card-text");
	var shopServerLine = $$("#shop-server-line");
	var serverEncapsLine = $$("#server-encaps-line");
	var towerTvLine = $$("#tower-tv-line");
	var towerWavesArray=[$$("#wave0"), $$("#wave1"), $$("#wave2"), $$("#wave3")];
	var shopCardInTvIcon=$$("#shop-card-in-tv-icon");

	signBtn.hide();
	shopServerLine.hide();
	shopTrigger.pointerCursor();
	shopCardTextIcon.shade();
	shopCardInTvIcon.hide();
	shopTrigger.mouseover(function() {
	    if(!signBtnPressed) {
		shopCardTextIcon.hide();
		signBtn.show();
	    }
	});
	shopTrigger.mouseleave(function() {
	    if(!signBtnPressed) {
		shopCardTextIcon.show();
		shopCardText.show();
		signBtn.hide();
	    }
	});
	shopTrigger.click(function() {
	    $.ajax("../cmd?cmd=submitShop");
	    signBtnPressed = true;
	    signBtn.hide();
	    shopCardTextIcon.unshade();
	    shopCardTextIcon.show();
	    shopCardText.show();
	    shopTrigger.defaultCursor();
	    shopServerLine.show();
	    marqueeAdd(shopServerLine, false);
	    marqueeAdd(serverEncapsLine,false);
	    animateTracing(towerWavesArray);
	    setTimeout(function() {
		shopCardInTvIcon.attr('opacity', 0).show().animate(
		    {svgOpacity: 1}, 2000);
	    }, 3000);
	});
        window.setInterval(marqueeUpdtate, 100);

    $('.carousel').carousel({
	interval: false
    });
});

function demonstration() {
    $('.carousel').carousel(1);
};
