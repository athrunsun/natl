(function (ates, $, undefined) {
    ates.createEnvFormTplFn = undefined;
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    ates.createEnvFormTplFn = doT.template($('#create_env_form_tpl').text(), undefined, undefined);

    $("#create_env").on('click', function () {
        $.Dialog({
            shadow: true,
            overlay: false,
            draggable: true,
            icon: false,
            title: 'Create Env',
            width: 500,
            padding: 10,
            content: '',
            onShow: function () {
                $.Dialog.content(ates.createEnvFormTplFn({}));
            }
        });
    });

    $("#env_table .delete-env").on("click", function(event) {
        event.preventDefault();
        var envId = $(this).attr("data-id");

        $.ajax({
            type: "POST",
            dataType: "json",
            //contentType: "application/json; charset=utf-8",
            url: ates.contextPath + "/env/delete",
            data: "envId=" + envId,
            success: function(result) {
                window.location.replace(window.location.href);
            }
        });
    });
});