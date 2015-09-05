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

    $.ajax({
        type: "POST",
        dataType: "json",
        url: ates.contextPath + "/testsuite/passrateAJAX",
        data: "testSuiteId=" + $("#current_testsuite_id").val()
    }).done(function(execResultCount) {
        if(execResultCount === undefined || execResultCount === null) {
            return;
        }
        $("#passrate_chart_container").highcharts({
            chart: {
                backgroundColor: 'transparent',
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            credits: {
                enabled: false
            },
            title: {
                text: null
            },
            tooltip: {
                pointFormatter: function() {
                    return this.series.name + ": <b>" + parseFloat(this.y * 100.0 / execResultCount["TOTAL"]).toFixed(2).toString() + " %</b>";
                }
            },
            plotOptions: {
                pie: {
                    allowPointSelect: false,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.2f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    },
                    showInLegend: true
                }
            },
            legend: {
                labelFormatter: function() {
                    return this.name + ": <b>" + this.y + "</b>";
                }
            },
            series: [{
                type: 'pie',
                name: 'Percentage',
                data: [
                    {name: 'PASSED', y: execResultCount["PASSED"], color: ates.passrateColorEnum["PASSED"]},
                    {name: 'FAILED', y: execResultCount["FAILED"], color: ates.passrateColorEnum["FAILED"]},
                    {name: 'SKIPPED', y: execResultCount["SKIPPED"], color: ates.passrateColorEnum["SKIPPED"]},
                    {name: 'UNKNOWN', y: execResultCount["UNKNOWN"], color: ates.passrateColorEnum["UNKNOWN"]}
                ]
            }]
        });
    });
});