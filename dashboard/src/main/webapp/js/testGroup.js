(function (ates, $, undefined) {
    ates.createExecutionFormTplFn = null;
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    ates.createExecutionFormTplFn = doT.template($('#create_execution_form_tpl').text(), undefined, undefined);

    $("#run_selected_test_groups").on('click', function (e) {
        var hasCheckedTestGroup = false;

        $("#test_group_table .check-to-run-test-group").each(function (index, item) {
            if ($(item).is(":checked") === true) {
                hasCheckedTestGroup = true;
            }
        });

        if (hasCheckedTestGroup === false) {
            $.Notify({style: {background: 'red', color: 'white'}, content: "You don't select any test groups!"});
            e.preventDefault();
        } else {
            $.ajax({
                type: "POST",
                dataType: "json",
                //contentType: "application/json; charset=utf-8",
                url: ates.contextPath + "/env/fetchEnvsByProjectIdAsJson",
                data: "projectId=" + $.cookie(ates.cookieKeyProjectPref)
            }).done(function(envList) {
                $.Dialog({
                    shadow: true,
                    overlay: false,
                    draggable: true,
                    icon: false,
                    title: 'Create Execution',
                    width: 700,
                    padding: 10,
                    content: '',
                    onShow: function () {
                        $.Dialog.content(ates.createExecutionFormTplFn({"array":envList}));

                        $("#create_execution_form").on("submit", function (event) {
                            var hasCheckedTestGroup = false;
                            var selectedTestGroups = [];

                            $("#test_group_table .check-to-run-test-group").each(function (index, item) {
                                if ($(item).is(":checked") === true) {
                                    hasCheckedTestGroup = true;
                                    selectedTestGroups.push($(item).attr("data-id"));
                                }
                            });

                            if (hasCheckedTestGroup === false) {
                                $.Notify({
                                    style: {background: 'red', color: 'white'},
                                    content: "You don't select any test groups!"
                                });
                                event.preventDefault();
                            } else {
                                // Submit selected test group ids as comma separated
                                $(this).find("#selected_test_groups").val(selectedTestGroups.join(","));
                                $(this).submit();
                            }
                        });
                    }
                });
            });

        }
    });
});