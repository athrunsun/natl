(function (ates, $, undefined) {
    ates.createExecutionFormTplFn = null;
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    ates.createExecutionFormTplFn = doT.template($('#create_execution_form_tpl').text(), undefined, undefined);
    ates.addCaseToSuiteFormTplFn = doT.template($('#add_case_to_suite_form_tpl').text(), undefined, undefined);

    $("#run_selected_test_cases").on('click', function (e) {
        var hasCheckedTestCase = false;

        $("#test_case_table .check-to-run-test-case").each(function (index, item) {
            if ($(item).is(":checked") === true) {
                hasCheckedTestCase = true;
            }
        });

        if (hasCheckedTestCase === false) {
            $.Notify({style: {background: 'red', color: 'white'}, content: "You haven't select any test cases!"});
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
                            var hasCheckedTestCase = false;
                            var selectedTestCases = [];

                            $("#test_case_table .check-to-run-test-case").each(function (index, item) {
                                if ($(item).is(":checked") === true) {
                                    hasCheckedTestCase = true;
                                    //selectedTestCases.push($(item).attr("data-id"));
                                    $("#create_execution_form").append("<input name=\"selected_test_cases\" type=\"hidden\" value=\"" + $(item).attr("data-id") + "\">");
                                }
                            });

                            if (hasCheckedTestCase === false) {
                                $.Notify({
                                    style: {background: 'red', color: 'white'},
                                    content: "You haven't select any test cases!"
                                });
                                event.preventDefault();
                            } else {
                                // Submit selected test case names as an array
                                //$(selectedTestCases).each(function(index, item) {
                                //    $("#create_execution_form").append("<input name=\"selected_test_cases[]\" type=\"hidden\" value=\"" + item + "\">");
                                //});

                                //$("#create_execution_form").submit();
                            }
                        });
                    }
                });
            });
        }
    });

    $("#assign_to_test_suite").on('click', function (e) {
        var hasCheckedTestCase = false;

        $("#test_case_table .check-to-run-test-case").each(function (index, item) {
            if ($(item).is(":checked") === true) {
                hasCheckedTestCase = true;
            }
        });

        if (hasCheckedTestCase === false) {
            $.Notify({style: {background: 'red', color: 'white'}, content: "You haven't select any test cases!"});
            e.preventDefault();
        } else {
            $.ajax({
                type: "POST",
                dataType: "json",
                url: ates.contextPath + "/testsuite/fetchTestSuitesByProjectIdAsJson",
                data: "projectId=" + $.cookie(ates.cookieKeyProjectPref)
            }).done(function(suiteList) {
                $.Dialog({
                    shadow: true,
                    overlay: false,
                    draggable: true,
                    icon: false,
                    title: 'Assign Test Case to Test Suite',
                    width: 700,
                    padding: 10,
                    content: '',
                    onShow: function () {
                        $.Dialog.content(ates.addCaseToSuiteFormTplFn({"array":suiteList}));

                        $("#add_case_to_suite_form").on("submit", function (event) {
                            var hasCheckedTestCase = false;

                            $("#test_case_table .check-to-run-test-case").each(function (index, item) {
                                if ($(item).is(":checked") === true) {
                                    hasCheckedTestCase = true;
                                    $("#add_case_to_suite_form").append("<input name=\"selected_test_cases\" type=\"hidden\" value=\"" + $(item).attr("data-id") + "\">");
                                }
                            });

                            if (hasCheckedTestCase === false) {
                                $.Notify({
                                    style: {background: 'red', color: 'white'},
                                    content: "You haven't select any test cases!"
                                });
                                event.preventDefault();
                            } else {
                            }
                        });
                    }
                });
            });
        }
    });
});