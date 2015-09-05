(function (ates, $, undefined) {
    ates.initCurrentExecutionPassratePieChart = function($chartContainer) {
        $.ajax({
            type: "POST",
            dataType: "json",
            url: ates.contextPath + "/execution/fecthPassrateAsJson",
            data: "executionId=" + $("#current_execution_id").val()
        }).done(function(passrates) {
            if(passrates === undefined || passrates === null) {
                return;
            }

            $.each(passrates, function(executionIdAndName, execResultCount) {
                var firstCommaIndex = executionIdAndName.indexOf(",");
                var executionId = executionIdAndName.substring(0, firstCommaIndex);
                var executionName = executionIdAndName.substring(firstCommaIndex + 1);

                $chartContainer.highcharts({
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

                return false;
            });
        });
    }
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    ates.initCurrentExecutionPassratePieChart($("#passrate_chart_container"));
});