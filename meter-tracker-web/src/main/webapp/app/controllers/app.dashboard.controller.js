(function () {
    angular
        .module('app')
        .controller('DashboardController', DashboardController);

    DashboardController.$inject = ['dashboardService', 'errorMessageService'];

    function DashboardController(dashboardService, errorMessageService) {
        var vm = this;
        vm.dashboardItems = [];

        dashboardService.items({}, function (result) {
            vm.dashboardItems = result;
        });

        this.chartOptions = function (dashboardItem) {
            return {
                chart: {
                    type: "area",
                    height: 300,
                    events: {
                        load: function (event) {
                            loadChartData(event.target, dashboardItem)
                        }
                    }
                },

                title: {
                    text: dashboardItem.name
                },

                xAxis: {
                    type: 'datetime'
                },

                yAxis: {
                    title: {
                        text: 'Consumption'
                    }
                },

                plotOptions: {
                    area: {
                        stacking: 'normal',
                        marker: {
                            enabled: false,
                            symbol: 'circle',
                            radius: 2,
                            states: {
                                hover: {
                                    enabled: true
                                }
                            }
                        }
                    }
                },

                tooltip: {
                    shared: true,
                    crosshairs: true
                }
            }
        };

        function loadChartData(chart, dashboardItem) {
            console.log('onLoad', event);
            dashboardService.series({
                id: dashboardItem.id
            }, function (result) {
                console.log('success', result);
                var meterSeriesMap = {};
                result.forEach(function (e) {
                    meterSeriesMap[e.meterId] = e.series.map(function (s) {
                        return [Date.UTC(s.date[0], s.date[1]), s.value];
                    });
                });
                dashboardItem.entries.forEach(function (entry) {
                    chart.addSeries({
                        color: '#' + entry.color.toString(16),
                        name: entry.meter.name,
                        data: meterSeriesMap[entry.meter.id]
                    }, false);
                });
                chart.redraw();
            }, function (error) {
                console.log('failure', error);
            });
        }
    }
})();
