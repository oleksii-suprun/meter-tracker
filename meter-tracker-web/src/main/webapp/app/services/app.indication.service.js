/**
 * Created by asuprun on 3/4/15.
 */
(function () {
    angular
        .module('app')
        .factory('indicationService', indicationService);

    indicationService.$inject = ['$resource'];

    function indicationService($resource) {
        return $resource("rs/indications/:id/:action", {}, {
            upload: {
                method: "POST",
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }
        });
    }
})();