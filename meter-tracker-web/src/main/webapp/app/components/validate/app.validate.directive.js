/**
 * Created by asuprun on 3/17/15.
 */
(function () {
    angular
        .module('app')
        .directive('appValidate', appValidate);

    function appValidate() {
        return {
            restrict: 'A',
            link: link
        }
    }

    function link(scope, element, attrs) {
        element.bind('keyup', updateClass);
        element.bind('blur', updateClass);

        function updateClass() {
            element.parent().toggleClass('has-error', element.hasClass('ng-invalid') && element.hasClass('ng-dirty'));
            element.parent().toggleClass('has-success', element.hasClass('ng-valid') && element.hasClass('ng-dirty'));
        }
    }

})();