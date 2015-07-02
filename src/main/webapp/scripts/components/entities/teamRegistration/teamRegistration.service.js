'use strict';

angular.module('competitionApp')
    .factory('TeamRegistration', function ($resource) {
        return $resource('api/teamRegistrations/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.registrationTime = new Date(data.registrationTime);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
