/**
 * Created by asuprun on 3/4/15.
 */
(function () {
    angular
        .module('app')
        .directive('appFileModel', appFileModel);

    function appFileModel() {
        return {
            scope: {
                appFileModel: "="
            },
            link: link
        };

        function link(scope, element) {
            element.bind("change", function (changeEvent) {
                scope.$apply(function () {
                    scope.appFileModel = changeEvent.target.files[0];
                    scope.element = element[0];
                });
            });

            scope.$watch(function () {
                return scope.appFileModel
            }, function (newValue, oldValue) {
                if (oldValue && !newValue) {
                    scope.element.value = '';
                }
            });
        }
    }
})();