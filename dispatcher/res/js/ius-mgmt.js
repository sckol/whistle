$(document).ready(function() {
    var carousel = $('.carousel');
    carousel.carousel({
	interval: false
    });
    $("#demontration-btn").on("click", function demonstration() {
	carousel.carousel(1);
    });
});