/**
 * Created by asuprun on 3/20/15.
 */
(function () {
    angular
        .module('app')
        .directive('appError', appError);

    function appError() {
        return {
            restrict: 'A',
            templateUrl: 'app/components/error/app.error.directive.html',
            controller: AppErrorController,
            controllerAs: 'vm',
            bindToController: true,
            scope: {
                appError: '@'
            },
            link: link
        };

        function link(scope, attr) {
            scope.appError = attr.appError;
        }
    }

    AppErrorController.$inject = ['errorMessageService', '$scope'];
    function AppErrorController(errorMessageService, $scope) {
        var vm = this;
        vm.messages = {};

        $scope.$watch(function () {
            return errorMessageService.get();
        }, onChange, true);

        function onChange(newValue) {
            vm.messages = {};
            for (var key in newValue) {
                if (!newValue.hasOwnProperty(key)) {
                    continue;
                }
                if ((vm.appError !== 'local' && !newValue[key].local) || (vm.appError === 'local' && newValue[key].local)) {
                    vm.messages[key] = newValue[key].message;
                }
            }
        }

        this.isEmpty = function () {
            return $.isEmptyObject(vm.messages);
        }
    }

})();
