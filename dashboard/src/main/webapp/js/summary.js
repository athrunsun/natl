(function (ates, $, undefined) {
    ates.initPieChart = function(id) {
        $(id).highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: 'Browser market shares at a specific website, 2014'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    }
                }
            },
            series: [{
                type: 'pie',
                name: 'Browser share',
                data: [
                    ['Firefox', 45.0],
                    ['IE', 26.8],
                    {
                        name: 'Chrome',
                        y: 12.8,
                        sliced: true,
                        selected: true
                    },
                    ['Safari', 8.5],
                    ['Opera', 6.2],
                    ['Others', 0.7]
                ]
            }]
        });
    }
})(window.ates = window.ates || {}, jQuery)

$(document).ready(function () {
    ates.initPieChart('#chart_container_coverage');
    ates.initPieChart('#chart_container_coverage1');
    ates.initPieChart('#chart_container_coverage2');
    ates.initPieChart('#chart_container_coverage3');
    ates.initPieChart('#chart_container_coverage4');
    ates.initPieChart('#chart_container_coverage5');
    ates.initPieChart('#chart_container_coverage6');
});