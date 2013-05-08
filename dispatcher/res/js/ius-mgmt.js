$(document).ready(function() {
    var bitrixRoot = $("#bitrix");
    var carousel = $('.carousel');
    carousel.carousel({
        interval: false
    });
    $("#demontration-btn").on("click", function demonstration() {
        carousel.carousel(1);
    });
    bitrixRoot.load(function() {
        $$.reconnect();
        $$.wsMessageListener = function(m) {
            var msg = JSON.parse(m.data);
            if (msg.type === "ShopComplaintEvent") {
                $('#newcomplaint', bitrixRoot.contents()).show();
            }
        }
    });
});