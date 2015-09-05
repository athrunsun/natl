(function (ates, $, undefined) {
    ates.reloadJenkinsJobUrl = ates.jenkinsUrl + "/job/ATES_Test_FullBuild/buildWithParameters";
    ates.createExecutionFormTplFn = doT.template($('#create_execution_form_tpl').text(), undefined, undefined);
    ates.addCaseToSuiteFormTplFn = doT.template($('#add_case_to_suite_form_tpl').text(), undefined, undefined);
    ates.customParameterRowTplFn = doT.template($('#custom_parameter_row_tpl').text(), undefined, undefined);

    //ates.
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
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
            $.Dialog({
                shadow: true,
                overlay: false,
                draggable: true,
                icon: false,
                title: 'Create Execution',
                width: 700,
                //height: 400,
                padding: 10,
                content: '',
                onShow: function () {
                    $.Dialog.content(ates.createExecutionFormTplFn({}));

                    $("#create_execution_form").on("submit", function (event) {
                        var hasCheckedTestCase = false;

                        $("#test_case_table .check-to-run-test-case").each(function (index, item) {
                            if ($(item).is(":checked") === true) {
                                hasCheckedTestCase = true;
                                $("#create_execution_form").append("<input name=\"selected_test_cases\" type=\"hidden\" value=\"" + $(item).attr("data-id") + "\">");
                            }
                        });

                        if (hasCheckedTestCase === false) {
                            $.Notify({
                                style: {background: 'red', color: 'white'},
                                content: "You haven't select any test cases!"
                            });
                            event.preventDefault();
                        }
                    });

                    ates.bindRemoveParaOnClickEventHandler();

                    $("#create_execution_form .addPara").on('click', function(event){
                        $("#custom_params").append(ates.customParameterRowTplFn({}));
                        ates.bindRemoveParaOnClickEventHandler();
                        $.Dialog.autoResize();
                        event.preventDefault();
                    });
                }
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
                            }
                        });
                    }
                });
            });
        }
    });

    //$("#reload_all_test_cases").on("click", function(event) {
    //    $.ajax({
    //        type: "POST",
    //        dataType: "json",
    //        url: ates.contextPath + "/project/projectDetailsAJAX",
    //        data: "projectId=" + $.cookie(ates.cookieKeyProjectPref),
    //        success: function(projectDetails){
    //            $.ajax({
    //                type: "POST",
    //                dataType: "json",
    //                url: ates.reloadJenkinsJobUrl,
    //                data: "ProjectId=" + projectDetails["id"].toString() + "&TestJarWithDependenciesName=" + projectDetails["jar_with_dependency_name"] + "&GitURL=" + projectDetails["git_url"],
    //                success: function(data, status, xhr) {
    //                    xhr.getResponseHeader('Location') + "/api/json";
    //                }
    //            });
    //        }
    //    });
    //});
});