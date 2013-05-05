$$ = {};
$(document).ready(function(){
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
    $$.hideEach = function(key, value) {
	value.hide();
    }
    $$.showEach = function(key, value) {
	value.show();
    }
    $$.marqueeAdd = function(pathObject, direction) {
	pathObject.css("stroke-dasharray", "7, 7");
	marqueeList[pathObject.selector]={pathObject:pathObject, direction:direction};
    }
    $$.marqueeDelete = function(pathObject) {
	pathObject.css("stroke-dasharray", "none");
	delete marqueeList[pathObject.selector];
    }
    $$.marqueeDeleteAll = function() {
	for(var i in marqueeList) {
	    $$.marqueeDelete(marqueeList[i].pathObject);
	}
    }
    $$.marqueeUpdtate = function() {
	$.each(marqueeList, function(key, value) {
	    value.pathObject.css("stroke-dashoffset", value.direction ? marquee : -marquee);
	});
	marquee++;
    }
    $$.animateTracing = function(frameArray) {
	var currentFrame = frameArray.length;
	return window.setInterval(function() {
	    if(currentFrame == frameArray.length) {
		currentFrame = 0;
		$(frameArray).each($$.hideEach);
	    } else {
		frameArray[currentFrame++].show();
	    }
	}, 500);
    }
    $$.wsMessageListener = function(){};
    $$.reconnect = function() {
    	$$.ws = new WebSocket("ws://localhost:8089/webSocket");
    	$$.ws.onclose = function() {
    	    setTimeout($$.reconnect, 3000);
    	}
	$$.ws.onmessage = function(m) {
	    $$.wsMessageListener(m);
	}
	setTimeout(function() {
	    if ($$.ws.readyState != WebSocket.OPEN) {
		$$.reconnect();
	    }
	}, 3000);
    }
    var marqueeList={};
    var marquee=0;
    window.setInterval($$.marqueeUpdtate, 100);
});
