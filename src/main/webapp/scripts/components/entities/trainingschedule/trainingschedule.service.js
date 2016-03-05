'use strict';

angular.module('competitionApp')
    .factory('TrainingSchedule', function ($resource) {
        return $resource('api/trainingschedule/:cmd/:id', {}, {
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
            'update': { method:'PUT' },
            'applyForTraining' : { method: 'POST' },
            'registerPerformedTraining' : { method: 'POST' },
            'registerMissedTraining': { method: 'POST' }
        });
    });
