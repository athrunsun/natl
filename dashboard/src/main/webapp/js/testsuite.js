(function (ates, $, undefined) {
    ates.createTestSuiteFormTplFn = doT.template($('#create_testsuite_form_tpl').text(), undefined, undefined);
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
});