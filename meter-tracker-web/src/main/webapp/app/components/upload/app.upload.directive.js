/**
 * Created by asuprun on 3/17/15.
 */
(function () {
    angular
        .module('app')
        .directive('appUpload', appUpload);

    function appUpload() {
        return {
            restrict: 'EA',
            templateUrl: 'app/components/upload/app.upload.directive.html',
            controller: AppUploadController,
            controllerAs: 'vm',
            bindToController: true,
            required: "^appTargetId",
            scope: {
                targetId: "@appTargetId"
            },
            link: link
        };

        function link(scope, element, attr) {
            scope.targetId = attr.appTargetId;
        }
    }

    AppUploadController.$inject = ['Events', 'indicationService', '$rootScope', 'errorMessageService', 'meterService'];

    function AppUploadController(Events, indicationService, $rootScope, errorMessageService, meterService) {
        var vm = this;
        vm.file = null;
        vm.meterId = null;
        vm.meters = [];

        init();

        this.upload = function () {
            var fd = new FormData();
            fd.append('file', vm.file);
            fd.append('meterId', new Blob([vm.meterId], {type: "text/plain"}));

            indicationService.upload(fd, function () {
                errorMessageService.remove('app.upload.directive.UPLOAD_IMAGE_ERROR');

                $rootScope.$broadcast(Events.INDICATION_UPLOADED);
                vm.file = null;
                vm.meterId = null;
                $('#' + vm.targetId).modal('hide');
            }, function (error) {
                errorMessageService.add('app.upload.directive.UPLOAD_IMAGE_ERROR', error.data.message, true)
            });
        };

        function init() {
            meterService.query({}, function (result) {
                errorMessageService.remove('app.upload.directive.METERS_LOAD_ERROR');
                vm.meters = result;
            }, function (error) {
                errorMessageService.add('app.upload.directive.METERS_LOAD_ERROR', error.data.message);
            });
        }
    }

})();