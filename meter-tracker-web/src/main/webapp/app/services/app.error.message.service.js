/**
 * Created by asuprun on 3/20/15.
 */
(function () {
    angular
        .module('app')
        .factory('errorMessageService', errorMessageService);

    function errorMessageService() {
        var messages = {};
        return {
            add: function (key, message, local) {
                messages[key] = {
                    message: message,
                    local: local
                };
            },

            remove: function (key) {
                delete messages[key];
            },

            get: function () {
                return messages;
            }
        };
    }
})();