(function (ates, $, undefined) {
    ates.createExecutionFormTplFn = doT.template($('#create_execution_form_tpl').text(), undefined, undefined);
    ates.addGroupToSuiteFormTplFn = doT.template($('#add_group_to_suite_form_tpl').text(), undefined, undefined);
    ates.customParameterRowTplFn = doT.template($('#custom_parameter_row_tpl').text(), undefined, undefined);
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    $("#run_selected_test_groups").on('click', function (e) {
        var hasCheckedTestGroup = false;

        $("#test_group_table .check-to-run-test-group").each(function (index, item) {
            if ($(item).is(":checked") === true) {
                hasCheckedTestGroup = true;
            }
        });

        if (hasCheckedTestGroup === false) {
            $.Notify({style: {background: 'red', color: 'white'}, content: "You haven't select any test groups!"});
            e.preventDefault();
        } else {
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
                    $.Dialog.content(ates.createExecutionFormTplFn({}));

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
                                content: "You haven't select any test groups!"
                            });
                            event.preventDefault();
                        } else {
                            // Submit selected test group ids as comma separated
                            $(this).find("#selected_test_groups").val(selectedTestGroups.join(","));
                        }
                    });

                    $("#custom_params .remPara").on('click', function (evt) {
                        $(this).parent().parent().remove();
                        evt.preventDefault();
                    });

                    $("#create_execution_form .addPara").on('click', function (event) {
                        $("#custom_params").append(ates.customParameterRowTplFn({}));

                        $("#custom_params .remPara").on('click', function (evt) {
                            $(this).parent().parent().remove();
                            evt.preventDefault();
                        });

                        $.Dialog.autoResize();
                        event.preventDefault();
                    });
                }
            });
        }
    });

    $("#assign_to_test_suite").on('click', function (e) {
        var hasCheckedTestGroup = false;

        $("#test_group_table .check-to-run-test-group").each(function (index, item) {
            if ($(item).is(":checked") === true) {
                hasCheckedTestGroup = true;
            }
        });

        if (hasCheckedTestGroup === false) {
            $.Notify({style: {background: 'red', color: 'white'}, content: "You haven't select any test groups!"});
            e.preventDefault();
        } else {
            $.ajax({
                type: "POST",
                dataType: "json",
                url: ates.contextPath + "/testsuite/fetchTestSuitesByProjectIdAsJson",
                data: "projectId=" + $.cookie(ates.cookieKeyProjectPref)
            }).done(function (suiteList) {
                $.Dialog({
                    shadow: true,
                    overlay: false,
                    draggable: true,
                    icon: false,
                    title: 'Assign Test Group to Test Suite',
                    width: 700,
                    padding: 10,
                    content: '',
                    onShow: function () {
                        $.Dialog.content(ates.addGroupToSuiteFormTplFn({"array": suiteList}));

                        $("#add_group_to_suite_form").on("submit", function (event) {
                            var hasCheckedTestGroup = false;

                            $("#test_group_table .check-to-run-test-group").each(function (index, item) {
                                if ($(item).is(":checked") === true) {
                                    hasCheckedTestGroup = true;
                                    $("#add_group_to_suite_form").append("<input name=\"selected_test_groups\" type=\"hidden\" value=\"" + $(item).attr("data-id") + "\">");
                                }
                            });

                            if (hasCheckedTestGroup === false) {
                                $.Notify({
                                    style: {background: 'red', color: 'white'},
                                    content: "You haven't select any test groups!"
                                });
                                event.preventDefault();
                            }
                        });
                    }
                });
            });
        }
    });
});