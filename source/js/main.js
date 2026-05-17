/*-----------------------------------------------------------------------------------

    Theme Name: Lawyer - Attorney and Lawyers HTML Template
    Description: Attorney and Lawyers HTML Template
    Author: Chitrakoot Web
    Version: 1.0

    /* ----------------------------------

    JS Active Code Index
            
        01. Preloader
        02. Sticky Header
        03. Scroll To Top
        04. Parallax
        05. Video
        06. Resize function
        07. FullScreenHeight function
        08. ScreenFixedHeight function
        09. Copy to clipboard
        10. FullScreenHeight and screenHeight with resize function
        11. Sliders
        12. Tabs
        13. CountUp
        14. Countdown
        15. Current Year
        16. Portfolio        
                
    ---------------------------------- */    

(function($) {

    "use strict";

    var $window = $(window);

        /*------------------------------------
            01. Preloader
        --------------------------------------*/

        $('#preloader').fadeOut('normall', function() {
            $(this).remove();
        });


        /*------------------------------------
            02. Sticky Header
        --------------------------------------*/

        $window.on('scroll', function() {
            var scroll = $window.scrollTop();
            var logochange = $(".navbar-brand img");
            var logodefault = $(".navbar-brand.logodefault img");
            if (scroll <= 50) {
                $("header").removeClass("scrollHeader").addClass("fixedHeader");
                logochange.attr('src', 'img/logos/logo-inner.png');
                logodefault.attr('src', 'img/logos/logo.png');
            } 
            else {
                $("header").removeClass("fixedHeader").addClass("scrollHeader");
                logochange.attr('src', 'img/logos/logo.png');
                logodefault.attr('src', 'img/logos/logo.png');
            }
        });

        /*------------------------------------
            03. Scroll To Top
        --------------------------------------*/

        $window.on('scroll', function() {
            if ($(this).scrollTop() > 500) {
                $(".scroll-to-top").fadeIn(400);

            } else {
                $(".scroll-to-top").fadeOut(400);
            }
        });

        $(".scroll-to-top").on('click', function(event) {
            event.preventDefault();
            $("html, body").animate({
                scrollTop: 0
            }, 600);
        });

        /*------------------------------------
            04. Parallax
        --------------------------------------*/

        // sections background image from data background
        var pageSection = $(".parallax,.bg-img");
        pageSection.each(function(indx) {

            if ($(this).attr("data-background")) {
                $(this).css("background-image", "url(" + $(this).data("background") + ")");
            }
        });
        
        /*------------------------------------
            05. Video
        --------------------------------------*/

        $('.story-video').magnificPopup({
            delegate: '.video',
            type: 'iframe'
        });

        $('.source-modal').magnificPopup({
            type: 'inline',
            mainClass: 'mfp-fade',
            removalDelay: 160
        });

        /*------------------------------------
            06. Resize function
        --------------------------------------*/

        $window.resize(function(event) {
            setTimeout(function() {
                SetResizeContent();
            }, 500);
            event.preventDefault();
        });

        /*------------------------------------
            07. FullScreenHeight function
        --------------------------------------*/

        function fullScreenHeight() {
            var element = $(".full-screen");
            var $minheight = $window.height();
            element.css('min-height', $minheight);
        }

        /*------------------------------------
            08. ScreenFixedHeight function
        --------------------------------------*/

        function ScreenFixedHeight() {
            var $headerHeight = $("header").height();
            var element = $(".screen-height");
            var $screenheight = $window.height() - $headerHeight;
            element.css('height', $screenheight);
        }

        /*------------------------------------
            09. Copy to clipboard
        --------------------------------------*/

        if ($(".copy-clipboard").length !== 0) {
            new ClipboardJS('.copy-clipboard');
            $('.copy-clipboard').on('click', function() {
                var $this = $(this);
                var originalText = $this.text();
                $this.text('Copied');
                setTimeout(function() {
                    $this.text('Copy')
                    }, 2000);
            });
        };

        /*------------------------------------
            10. FullScreenHeight and screenHeight with resize function
        --------------------------------------*/        

        function SetResizeContent() {
            fullScreenHeight();
            ScreenFixedHeight();
        }

        SetResizeContent();

        // === when document ready === //
        $(document).ready(function(){


        /*------------------------------------
            11. Sliders
        --------------------------------------*/

        // info-carousel
        $('.info-carousel').owlCarousel({
            loop: true,
            responsiveClass: true,
            autoplay: true,
            smartSpeed: 1500,            
            nav: false,
            dots: true,
            center:true,
            margin: 30,
            responsive: {
                0: {
                    items: 1
                    
                },
                768: {
                    items: 2
                },
                1200: {
                    items: 3
                }
            }
        });

        // service-carousel
        $('.service-carousel').owlCarousel({
            loop: true,
            responsiveClass: true,
            autoplay: false,
            smartSpeed: 1500,            
            nav: false,
            dots: true,
            center:false,
            margin: 30,
            responsive: {
                0: {
                    items: 1,
                    margin: 0
                },
                768: {
                    items: 2
                },
                1200: {
                    items: 3
                }
            }
        });

        // testimonial-style1-carousel
        $('.testimonial-style1').owlCarousel({
            loop: true,
            responsiveClass: true,
            autoplay: false,
            thumbs: true,
            thumbsPrerendered: true,
            smartSpeed: 1500,
            nav: false,
            dots: false,
            center:true,
            items: 1,
            margin: 0
        });

        // testimonial-style2-carousel
        $('.testimonial-style2').owlCarousel({
            loop: true,
            responsiveClass: true,
            autoplay: false,
            autoplayTimeout: 3000,
            smartSpeed: 1500,            
            nav: true,
            center: false,
            dots: false,
            navText: ["<i class='fa fa-angle-left'></i>", "<i class='fa fa-angle-right'></i>"],
            margin: 20,
            items: 1
        });

        // testimonial-style3-carousel
        $('.testimonial-style3').owlCarousel({
            loop: true,
            responsiveClass: true,
            autoplay: false,
            autoplayTimeout: 3000,
            smartSpeed: 1500,            
            nav: false,
            center: false,
            dots: true,
            margin: 20,
            items: 1
        });

        // case-study-carousel
        $('.case-study-carousel').owlCarousel({
            loop: true,
            responsiveClass: true,
            autoplay: false,
            autoplayTimeout: 5000,
            smartSpeed: 1500,            
            nav: false,
            dots: true,
            margin: 20,
            responsive: {
                0: {
                    items: 1
                },
                576: {
                    items: 2
                },
                992: {
                    items: 3,
                    dots: false
                },
                1200: {
                    items: 3
                }

            }
        });

        // client-carousel
        $('.client-carousel').owlCarousel({
            loop: true,
            responsiveClass: true,
            autoplay: true,
            autoplayTimeout: 5000,
            smartSpeed: 1500,
            nav: false,
            dots: false,
            center:false,
            margin: 0,
            responsive: {
                0: {
                    items: 1
                },
                480: {
                    items: 2
                },
                768: {
                    items: 3
                }
            }
        });

        // case-study-details carousel
        $('.case-study-details').owlCarousel({
            loop: true,
            responsiveClass: true,
            autoplay: false,
            smartSpeed: 800,            
            nav: false,
            dots: false,
            center: false,
            margin: 20,
            responsive: {
                0: {
                    items: 1
                },
                767: {
                    items: 2
                },
                768: {
                    items: 2
                },
                992: {
                    items: 3
                }
            }
        });

        // client carousel
        $('.service-client-carousel').owlCarousel({
            loop: true,
            responsiveClass: true,
            autoplay: true,
            autoplayTimeout: 5000,
            smartSpeed: 900,
            nav: false,
            dots: false,
            items: 1,
            margin: 15
        });

        // banner1
        $('.banner1 .owl-carousel').owlCarousel({
            items: 1,
            loop:true,
            dots: false,
            margin: 0,
            nav: true,
            navText: ["<i class='ti-angle-left'></i>", "<i class='ti-angle-right'></i>"],
            autoplay: true,
            autoplayTimeout: 5000,
            smartSpeed:900,
            animateIn: 'fadeIn',
            animateOut: 'fadeOut',
            responsive: {
                0: {
                    nav: false,
                    dots: false
                },
                576: {
                    nav: false,
                    dots: true
                },
                991: {
                    nav: false,
                    dots: true
                },
                1200: {
                    nav: true,
                    dots: false
                }
            }            
        }); 
        
        // default owlCarousel
        $('.owl-carousel').owlCarousel({
            items: 1,
            loop:true,
            dots: false,
            margin: 0,
            autoplay:true,
            smartSpeed:500
        });   

        // slider text animation
        var owl = $('.slider-fade');
        owl.on('changed.owl.carousel', function(event) {
            var item = event.item.index - 2;     // Position of the current item
            $('.sub-title').removeClass('animated fadeInUp');
            $('h1').removeClass('animated fadeInUp');
            $('p').removeClass('animated fadeInUp');
            $('.butn').removeClass('animated fadeInUp');
            $('.banner-single-img').removeClass('animated fadeInUp');
            $('.owl-item').not('.cloned').eq(item).find('.sub-title').addClass('animated fadeInUp');
            $('.owl-item').not('.cloned').eq(item).find('h1').addClass('animated fadeInUp');
            $('.owl-item').not('.cloned').eq(item).find('p').addClass('animated fadeInUp');
            $('.owl-item').not('.cloned').eq(item).find('.butn').addClass('animated fadeInUp');
            $('.owl-item').not('.cloned').eq(item).find('.banner-single-img').addClass('animated fadeInUp');
        });

        /*------------------------------------
            12. Tabs
        --------------------------------------*/

        //Horizontal Tab
        if ($(".horizontaltab").length !== 0) {
            $('.horizontaltab').easyResponsiveTabs({
                type: 'default', //Types: default, vertical, accordion
                width: 'auto', //auto or any width like 600px
                fit: true, // 100% fit in a container
                tabidentify: 'hor_1', // The tab groups identifier
                activate: function(event) { // Callback function if tab is switched
                    var $tab = $(this);
                    var $info = $('#nested-tabInfo');
                    var $name = $('span', $info);
                    $name.text($tab.text());
                    $info.show();
                }
            });
        }

        /*------------------------------------
            13. CountUp
        --------------------------------------*/

        $('.countup').counterUp({
            delay: 25,
            time: 2000
        });

        /*------------------------------------
            14. Countdown
        --------------------------------------*/

        // CountDown for coming soon page
        $(".countdown").countdown({
            date: "01 Mar 2026 00:01:00", //set your date and time. EX: 15 May 2014 12:00:00
            format: "on"
        });

        /*------------------------------------
            15. Current Year
        --------------------------------------*/

        $('.current-year').text(new Date().getFullYear());
      
    });

    // === when window loading === //
    $window.on("load", function() {

        /*------------------------------------
            16. Isotop
        --------------------------------------*/

        $('.portfolio-gallery,.portfolio-gallery-isotope').lightGallery();

        $('.portfolio-link').on('click', (e) => {
            e.stopPropagation();
        });
    
    });

})(jQuery);