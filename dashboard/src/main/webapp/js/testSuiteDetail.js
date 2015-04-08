(function (ates, $, undefined) {
    ates.customParameterRowTplFn = doT.template($('#custom_parameter_row_tpl').text(), undefined, undefined);
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    $("#custom_params .remPara").on('click', function(evt){
        $(this).parent().parent().remove();
        evt.preventDefault();
    });

    $("#update_custom_params .addPara").on('click', function(event){
        $("#custom_params").append(ates.customParameterRowTplFn({}));

        $("#custom_params .remPara").on('click', function(evt){
            $(this).parent().parent().remove();
            evt.preventDefault();
        });

        event.preventDefault();
    });
});