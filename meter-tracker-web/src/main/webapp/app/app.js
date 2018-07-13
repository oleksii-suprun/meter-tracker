/**
 * Created by asuprun on 3/3/15.
 */
(function () {
    angular
        .module('app', ['ngRoute', 'ngResource'])
        .constant('Events', {
            INDICATION_UPLOADED: 'app.constants.Events.INDICATION_UPLOADED',
            INDICATION_DELETED: 'app.constants.Events.INDICATION_DELETED',
            INDICATION_CONFIRMED: 'app.constants.Events.INDICATION_CONFIRMED'
        })
        .constant('IndicationTypes', {
            ELECTRIC: {key: 'ELECTRIC', label: 'Electric'},
            WATER_COLD: {key: 'WATER_COLD', label: 'Water Cold'},
            WATER_HOT: {key: 'WATER_HOT', label: 'Water Hot'}
        })
        .constant('RoutePaths', {
            RECOGNIZE: '/recognize',
            INDICATIONS: '/indications',
            DASHBOARD: '/dashboard'
        })
        .config(config);

    function config($routeProvider, RoutePaths) {
        $routeProvider
            .when(RoutePaths.DASHBOARD, {
                templateUrl: 'app/views/dashboard.html',
                controller: 'DashboardController',
                controllerAs: 'vm'
            }).when(RoutePaths.RECOGNIZE, {
                templateUrl: 'app/views/recognize.html',
                controller: 'RecognitionController',
                controllerAs: 'vm'
            }).when(RoutePaths.INDICATIONS, {
                templateUrl: 'app/views/indication.list.html',
                controller: 'IndicationListController',
                controllerAs: 'vm'
            }).otherwise(RoutePaths.DASHBOARD);
    }
})();
