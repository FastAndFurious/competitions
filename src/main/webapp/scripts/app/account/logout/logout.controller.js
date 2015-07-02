'use strict';

angular.module('competitionApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
