(function (ates, $, undefined) {
    ates.createProjectFormTplFn = undefined;
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    ates.createProjectFormTplFn = doT.template($('#create_project_form_tpl').text(), undefined, undefined);

    $("#create_project").on('click', function () {
        $.Dialog({
            shadow: true,
            overlay: false,
            draggable: true,
            icon: false,
            title: 'Create Project',
            width: 500,
            padding: 10,
            content: '',
            onShow: function () {
                $.Dialog.content(ates.createProjectFormTplFn({}));
            }
        });
    });
    
    $("#email_enabled").on('click', function(){
    	var disabledAll = !document.getElementById("email_enabled").checked;
    	$("#execution_started").prop('disabled', disabledAll);
    	$("#execution_finished").prop('disabled', disabledAll);
    	$("#default_recipients").prop('disabled', disabledAll);
    });
});