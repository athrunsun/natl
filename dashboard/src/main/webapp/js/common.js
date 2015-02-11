(function(ates, $, undefined){
    ates.contextPath = "/ates";
    ates.cookieKeyProjectPref = "ates_project_pref";
    ates.maxTestNameLength = 80;

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
});