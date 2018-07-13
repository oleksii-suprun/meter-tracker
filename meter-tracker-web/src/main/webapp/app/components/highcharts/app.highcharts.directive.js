(function () {
    angular
        .module('app')
        .directive('appHighcharts', function ($parse) {
            return {
                restrict: 'E',
                template: '<div></div>',
                required: "^appHighchartsOptions",
                link: function (scope, element, attrs) {
                    scope.options = $parse(attrs.appHighchartsOptions)(scope);
                    Highcharts.chart(element[0].firstChild, scope.options);
                }
            };
        });
})();
