(function (ates, $, undefined) {
    ates.createTestSuiteFormTplFn = doT.template($('#create_testsuite_form_tpl').text(), undefined, undefined);
    ates.editTestSuiteJvmTplFn = doT.template($('#edit_testsuite_jvm_parameter_form_tpl').text(), undefined, undefined);
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    $("#create_testsuite").on('click', function () {
        $.Dialog({
            shadow: true,
            overlay: false,
            draggable: true,
            icon: false,
            title: 'Create Test Suite',
            width: 500,
            padding: 10,
            content: '',
            onShow: function () {
                $.Dialog.content(ates.createTestSuiteFormTplFn({}));
            }
        });
    });

    $('#testsuite_table .icon-pencil').on('click', function () {
        event.preventDefault();
        var testsuiteId = $(this).attr("data-id");

        $.ajax({
            type: "POST",
            dataType: "json",
            url: ates.contextPath + "/testsuite/fetchJvmOptionsBySuiteIdAsJson",
            data: "testSuiteId=" + testsuiteId
        }).done(function (parameterList) {
            $.Dialog({
                shadow: true,
                overlay: false,
                draggable: true,
                icon: false,
                title: 'Edit JVM Options',
                width: 500,
                height: 300,
                padding: 10,
                content: '',
                onShow: function () {
                    $.Dialog.content(ates.editTestSuiteJvmTplFn({"paraArray": parameterList}));
                    $("#customFields tbody").append("<input name=\"testSuiteId\" type=\"hidden\" value=\"" + testsuiteId + "\">");
                }
            });
        });
    });
});