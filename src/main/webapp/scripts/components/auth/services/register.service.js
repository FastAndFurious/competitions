'use strict';

angular.module('competitionApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


