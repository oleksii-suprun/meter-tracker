/**
 * Created by asuprun on 3/13/15.
 */
(function () {
    angular
        .module('app')
        .controller('RecognitionController', RecognitionController);

    RecognitionController.$inject = ['indicationService', 'Events', '$rootScope', 'errorMessageService', 'IndicationTypes'];

    function RecognitionController(indicationService, Events, $rootScope, errorMessageService, IndicationTypes) {
        var vm = this;
        vm.indications = [];

        $rootScope.$on(Events.INDICATION_UPLOADED, init);
        init();

        this.onDigitChange = function (digit) {
            digit.updated = true;
        };

        this.submit = function (indication) {
            indication.digits.map(function (digit) {
                if (!digit.updated) {
                    delete digit.image;
                }
                delete digit.id;
                delete digit.updated;
                return digit;
            });

            indicationService.save({
                action: 'digits',
                id: indication.id
            }, indication.digits, function (response) {
                $rootScope.$broadcast(Events.INDICATION_CONFIRMED);
                init();
            }, function (error) {
                errorMessageService.add('app.recognition.controller.INDICATION_CONFIRM_ERROR', error.data.message);
            });
        };

        this.decline = function (indication) {
            indicationService.delete({id: indication.id}, function (result) {
                init();
                $rootScope.$broadcast(Events.INDICATION_DELETED);
            }, function (error) {
                errorMessageService.add('app.recognition.controller.INDICATION_DELETE_ERROR', error.data.message);
            });
        };

        function init() {
            errorMessageService.remove('app.recognition.controller.UNRECOGNIZED_LOAD_ERROR');
            errorMessageService.remove('app.recognition.controller.DIGITS_LOAD_ERROR');
            errorMessageService.remove('app.recognition.controller.INDICATION_DELETE_ERROR');
            errorMessageService.remove('app.recognition.controller.INDICATION_CONFIRM_ERROR');

            indicationService.query({unrecognized: true}, function (result) {
                vm.indications = result;
                result.map(function (indication) {
                    indication.digits = [];
                    indicationService.query({action: 'digits', id: indication.id}, function (result) {
                        indication.digits = result;
                    }, function (error) {
                        errorMessageService.add('app.recognition.controller.DIGITS_LOAD_ERROR', error.data.message);
                    });
                });
            }, function (error) {
                errorMessageService.add('app.recognition.controller.DIGITS_LOAD_ERROR', error.data.message);
            });

        }
    }
})();