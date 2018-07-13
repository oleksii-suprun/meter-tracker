(function () {
    angular
        .module('app')
        .factory('dashboardService', dashboardService);

    dashboardService.$inject = ['$resource'];

    function dashboardService($resource) {
        return $resource("rs/dashboard/items/:id/:action", {}, {
            items: {
                method: 'GET',
                isArray: true
            },
            series: {
                method: 'GET',
                isArray: true,
                params: {
                    action: 'series'
                }
            }
        });
    }
})();
