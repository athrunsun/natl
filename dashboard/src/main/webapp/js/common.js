(function(ates, $, undefined){
    ates.contextPath = "/ates";
    ates.jenkinsUrl = "http://10.136.4.133:8080/jenkins";
    ates.cookieKeyProjectPref = "ates_project_pref";
    ates.maxTestNameLength = 80;

    ates.passrateColorEnum = {
        "PASSED":'#60a917',
        "FAILED":'#e51400',
        "SKIPPED":'#e3c800',
        "UNKNOWN":'#555555'
    };

    ates.queueEntryStatusEnum = {
        "WAITING":0,
        "RUNNING":1,
        "FINISHED":2,
        "STOPPED":3
    };

    ates.execResultEnum = {
        "UNKNOWN":0,
        "SKIPPED":1,
        "PASSED":2,
        "FAILED":3
    };

    ates.padStr = function(i) {
        return (i < 10) ? "0" + i : "" + i;
    }

    ates.convertJSONDateToString = function(jsonDate)
    {
        var re = /-?\d+/;
        var m = re.exec(jsonDate);
        var dateObj = new Date(parseInt(m[0]));

        var dateTimeStr = ates.padStr(dateObj.getFullYear()) + "-" +
            ates.padStr(1 + dateObj.getMonth()) + "-" +
            ates.padStr(dateObj.getDate()) + " " +
            ates.padStr(dateObj.getHours()) + ":" +
            ates.padStr(dateObj.getMinutes()) + ":" +
            ates.padStr(dateObj.getSeconds());

        return dateTimeStr;
    }
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function(){
    $("#navbar_project_list").on("change", function(event){
        $.cookie(ates.cookieKeyProjectPref, $(this).find("option:selected").val());
        window.location.replace(window.location.href);
    });

    if ($('.sticky-sidebar').length) {
        // Check the initial position of the sticky sidebar
        var stickySidebarTop = $('.sticky-sidebar').offset().top;

        $(window).scroll(function () {
            var stickySidebarWidth = $('.sticky-sidebar').width();
            var stickySidebarHeight = $('.sticky-sidebar').height();
            console.log(stickySidebarWidth);
            console.log(stickySidebarHeight);

            if ($(window).scrollTop() > stickySidebarTop) {
                $('.sticky-sidebar').css({ position: 'fixed', top: '0px', 'margin-top': '0px', width: stickySidebarWidth, height: stickySidebarHeight, 'z-index': '10' });
            } else {
                $('.sticky-sidebar').css({ position: '', top: '', background: '', 'margin-top': '', width: '', height: '', 'z-index': '' });
            }
        });
    }
});