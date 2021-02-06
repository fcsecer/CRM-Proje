(function($) {
  "use strict"; // Start of use strict

  // JQuery hareket hızı kullanarak kaydırma yapılıyor
  $('a.js-scroll-trigger[href*="#"]:not([href="#"])').click(function() {
    if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
      var target = $(this.hash);
      target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
      if (target.length) {
        $('html, body').animate({
          scrollTop: (target.offset().top - 54)
        }, 1000, "easeInOutExpo");
        return false;
      }
    }
  });

  // Bir kaydırma bağlantıya tıklandığında menüyü kapatır
  $('.js-scroll-trigger').click(function() {
    $('.navbar-collapse').collapse('hide');
  });

  // Kaydırma sırasında gezinme çubuğu öğelerine etkin sınıf eklemek için kaydırmayı etkinleştirin
  $('body').scrollspy({
    target: '#mainNav',
    offset: 54
  });

})(jQuery); // End of use strict
