$(document).ready(function() {
    var svgRoot = document.getElementById("svg").contentDocument.documentElement;
    $$.$ = function(path) {
        return $(path, svgRoot);
    }
    $$.mainLayer = $$.$("#main-layer");
    $$.entryLayer = $$.$("#entry-layer");
    $([$$.mainLayer, $$.entryLayer]).each($$.hideEach);
    $$.mainLayer.show();
    $$.signBtn = $$.$("#sign-btn");
    $$.shopTrigger = $$.$("#shop-trigger");
    $$.shopCardTextIcon = $$.$("#shop-card-texticon");
    $$.shopServerLine = $$.$("#shop-server-line");
    $$.govServerLine = $$.$("#gov-server-line");
    $$.bankServerLine = $$.$("#bank-server-line");
    $$.eviServerLine = $$.$("#evi-server-line");
    $$.serverEncapsLine = $$.$("#server-encaps-line");
    $$.towerTvLine = $$.$("#tower-tv-line");
    $$.backChannelLine = $$.$("#back-channel-line");
    $$.encapsTowerLine = $$.$("#encaps-tower-line");
    $$.towerWavesArray=[$$.$("#wave0"), $$.$("#wave1"), $$.$("#wave2"), $$.$("#wave3")];
    $$.remoteWavesArray=[$$.$("#remote-wave0"), $$.$("#remote-wave1")];
    $$.shopCardInTvIcon=$$.$("#shop-card-in-tv-icon");
    $$.compass = $$.$('#compass');
});