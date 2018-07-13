/**
 * Created by asuprun on 3/11/15.
 */
(function () {
    angular
        .module('app')
        .directive('appNav', appNav);

    function appNav() {
        return {
            restrict: 'EA',
            templateUrl: 'app/components/nav/app.nav.directive.html',
            controller: AppNavController,
            controllerAs: 'vm',
            bindToController: true
        }
    }

    AppNavController.$inject = ['Events', 'indicationService', '$rootScope', 'errorMessageService', 'meterService', '$location','RoutePaths'];

    function AppNavController(Events, indicationService, $rootScope, errorMessageService, meterService, $location, RoutePaths) {
        var vm = this;
        vm.unrecognized = [];
        vm.meters = [];

        $rootScope.$on(Events.INDICATION_UPLOADED, init);
        $rootScope.$on(Events.INDICATION_DELETED, init);
        $rootScope.$on(Events.INDICATION_CONFIRMED, init);
        init();

        this.isIndicationsActive = function() {
            return $location.path().indexOf(RoutePaths.INDICATIONS) === 0;
        };

        this.isDashboardActive = function() {
            return $location.path().indexOf(RoutePaths.DASHBOARD) === 0;
        };

        function init() {
            errorMessageService.remove('app.nav.directive.LOAD_UNRECOGNIZED_ERROR');

            meterService.query({}, function(result) {
                vm.meters = result;
            });

            indicationService.query({unrecognized: true}, function (result) {
                vm.unrecognized = result;
            }, function (error) {
                errorMessageService.add('app.nav.directive.LOAD_UNRECOGNIZED_ERROR', error.data.message);
            });
        }
    }

})();
