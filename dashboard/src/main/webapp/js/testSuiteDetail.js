(function (ates, $, undefined) {
    ates.customParameterRowTplFn = doT.template($('#custom_parameter_row_tpl').text(), undefined, undefined);
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    ates.bindRemoveParaOnClickEventHandler();

    $("#update_custom_params .addPara").on('click', function(event){
        $("#custom_params").append(ates.customParameterRowTplFn({}));
        ates.bindRemoveParaOnClickEventHandler();
        event.preventDefault();
    });
});