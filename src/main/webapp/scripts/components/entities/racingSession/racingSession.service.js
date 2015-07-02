'use strict';

angular.module('competitionApp')
    .factory('RacingSession', function ($resource) {
        return $resource('api/racingSessions/:cmd/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.plannedStartTime = new Date(data.plannedStartTime);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
