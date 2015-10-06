'use strict';

angular.module('competitionApp')
    .factory('FuriousRun', function ($resource) {
        return $resource('api/furiousruns/:cmd/:id', {}, {
            'start' : { method: 'GET', isArray: false},
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.scheduledStartDate = new Date(data.scheduledStartDate);
                    data.startDate = new Date(data.startDate);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
