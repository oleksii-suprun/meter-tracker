/**
 * Created by asuprun on 8/9/15.
 */
(function () {
    angular
        .module('app')
        .factory('meterService', meterService);

    meterService.$inject = ['$resource'];

    function meterService($resource) {
        return $resource("rs/meters/:id", {}, {});
    }
})();