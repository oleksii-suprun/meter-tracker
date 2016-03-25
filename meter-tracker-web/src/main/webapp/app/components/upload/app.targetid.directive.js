/**
 * Created by asuprun on 3/17/15.
 */
(function () {
    angular
        .module('app')
        .directive('appTargetId', appTargetId);

    function appTargetId() {
        return {
            restrict: 'A'
        }
    }

})();