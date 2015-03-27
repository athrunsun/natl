(function (ates, $, undefined) {
    ates.initCoveragePieChart = function() {
        $.ajax({
            type: "POST",
            dataType: "json",
            url: ates.contextPath + "/project/fetchAutomationCoverageAsJson",
            data: "projectId=" + $.cookie(ates.cookieKeyProjectPref)
        }).done(function(coverage) {
            if(coverage === undefined || coverage === null) {
                return;
            }

            $('#chart_container_coverage').highcharts({
                chart: {
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
                    //pointFormat: '{series.name}: <b>{point.percentage:.2f} %</b>',
                    pointFormatter: function() {
                        return this.series.name + ": <b>" + parseFloat(this.y * 100.0 / coverage["TOTAL"]).toFixed(2).toString() + " %</b>";
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
                    labelFormat: '{name}: <b>{y}</b>'
                },
                series: [{
                    type: 'pie',
                    name: 'Percentage',
                    data: [
                        ['AUTOMATED', coverage['AUTOMATED']],
                        ['MANUAL', coverage['TOTAL'] - coverage['AUTOMATED']]
                    ]
                }]
            });
        });
    }

    ates.initPassratePieChart = function($outerChartContainer) {
        $.ajax({
            type: "POST",
            dataType: "json",
            url: ates.contextPath + "/summary/fetchPassrateOfRecentExecutionsAsJson",
            data: "projectId=" + $.cookie(ates.cookieKeyProjectPref)
        }).done(function(passrates) {
            if(passrates === undefined || passrates === null) {
                return;
            }

            // Reformat passrates map to an array
            var execIdMapArray = Object.keys(passrates).map(function(elem, index, arr){
                var execIdMap = {};
                var firstCommaIndex = elem.indexOf(",");
                var executionId = elem.substring(0, firstCommaIndex);
                execIdMap["execId"] = executionId;
                execIdMap["execIdAndName"] = elem;
                return execIdMap;
            });

            // Sort the reformatted array by execution id DESC
            execIdMapArray.sort(function(objA, objB) {
                return objB["execId"] - objA["execId"];
            });

            var chartIndex = 0;
            var $chartRow = undefined;

            $.each(execIdMapArray, function(index, value) {
                var executionIdAndName = execIdMapArray[index]["execIdAndName"];
                var execResultCount = passrates[executionIdAndName];
                var firstCommaIndex = executionIdAndName.indexOf(",");
                var executionId = executionIdAndName.substring(0, firstCommaIndex);
                var executionName = executionIdAndName.substring(firstCommaIndex + 1);

                if(chartIndex % 2 === 0) {
                    $chartRow = $("<div class=\"row\"></div>");
                }

                var $chartColumn = $("<div class=\"span6\"></div>");
                var $chartContainer = $("<div id=\"last_passrate_chart_container_" + (chartIndex + 1) + "\"></div>");

                ates.$passratePieChart = $chartContainer.highcharts({
                    chart: {
                        plotBackgroundColor: null,
                        plotBorderWidth: null,
                        plotShadow: false
                    },
                    credits: {
                        enabled: false
                    },
                    title: {
                        useHTML: true,
                        text: "<a target=\"_blank\" href=\"" + ates.contextPath + "/execution/detail/" + executionId + "\">" + executionName + "</a>"
                    },
                    tooltip: {
                        //pointFormat: '{series.name}: <b>{point.percentage:.2f} %</b>',
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

                $chartColumn.append($chartContainer);
                $chartRow.append($chartColumn);

                if(chartIndex % 2 === 0) {
                    $outerChartContainer.append($chartRow);
                }

                chartIndex += 1;
            });
        });
    }
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    ates.initCoveragePieChart();
    ates.initPassratePieChart($("#last_executions_passrate_chart_outer_container"));
});