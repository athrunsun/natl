(function (ates, $, undefined) {
    ates.createTestSuiteFormTplFn = undefined;
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    ates.createTestSuiteFormTplFn = doT.template($('#create_testsuite_form_tpl').text(), undefined, undefined);

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

    $("#testsuite_table .delete-testsuite").on("click", function(event) {
        event.preventDefault();
        var testsuiteId = $(this).attr("data-id");

        $.ajax({
            type: "POST",
            dataType: "json",
            url: ates.contextPath + "/testsuite/delete",
            data: "testsuiteId=" + testsuiteId,
            success: function(result) {
                window.location.replace(window.location.href);
            }
        });
    });

    $("#test_case_table .remove-from-testsuite").on("click", function(event) {
        event.preventDefault();

        $.ajax({
            type: "POST",
            dataType: "json",
            url: ates.contextPath + "/testsuite/removeCaseFromSuite",
            data: {testcaseName:$(this).attr("data-name"),testsuiteId:$(this).attr("data-id")},
            success: function(result) {
                window.location.replace(window.location.href);
            }
        });
    });
    
});