/**
 * Created by asuprun on 3/19/15.
 */
(function () {
    angular
        .module('app')
        .controller('IndicationListController', IndicationListController);

    IndicationListController.$inject = ['$location', 'indicationService', 'errorMessageService'];

    function IndicationListController($location, indicationService, errorMessageService) {
        var vm = this;
        vm.indications = [];
        vm.selectedIndication = null;

        init();

        this.onViewImageClick = function(indication) {
            vm.selectedIndication = indication;
        };

        function init() {
            errorMessageService.remove('app.indication.list.controller.INDICATIONS_LOAD_ERROR');

            indicationService.query({meterId: $location.search().meter, unrecognized: false}, function (result) {
                vm.indications = result;
            }, onError);
        }

        function onError(error) {
            errorMessageService.add('app.indication.list.controller.INDICATIONS_LOAD_ERROR', error.data.message)
        }
    }
})();