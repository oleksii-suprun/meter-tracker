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
        vm.indicationPreview = {};
        vm.selectedIndication = {};

        init();

        this.onViewImageClick = function (indication) {
            vm.indicationPreview.created = indication.created;
            vm.indicationPreview.imageUrl = indication.originalImageUrl;
        };

        this.onEditClick = function (indication) {
            vm.selectedIndication = angular.copy(indication)
        };

        this.updateSelectedIndication = function () {
            indicationService.update({
                id: vm.selectedIndication.id
            }, vm.selectedIndication, function () {
                init();
                $('#editModal').modal('hide')
            }, function (error) {
                errorMessageService.add('app.indication.list.controller.INDICATIONS_UPDATE_ERROR', error.data.message, true)
            });
        };

        this.onUpdateCancelClick = function () {
            errorMessageService.remove('app.indication.list.controller.INDICATIONS_UPDATE_ERROR');
        };

        function init() {
            errorMessageService.remove('app.indication.list.controller.INDICATIONS_LOAD_ERROR');
            errorMessageService.remove('app.indication.list.controller.INDICATIONS_UPDATE_ERROR');

            indicationService.query({meterId: $location.search().meter, unrecognized: false}, function (result) {
                vm.indications = result;
            }, onError);
        }

        function onError(error) {
            errorMessageService.add('app.indication.list.controller.INDICATIONS_LOAD_ERROR', error.data.message)
        }
    }
})();
