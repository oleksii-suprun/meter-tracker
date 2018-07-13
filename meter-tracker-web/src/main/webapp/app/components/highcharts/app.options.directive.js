(function () {
    angular
        .module('app')
        .directive('appHighchartsOptions', appHighchartsOptions);

    function appHighchartsOptions() {
        return {
            restrict: 'A'
        }
    }

})();
