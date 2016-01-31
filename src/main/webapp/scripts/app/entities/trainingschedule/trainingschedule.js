'use strict';

angular.module('competitionApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('trainingschedule', {
                parent: 'entity',
                url: '/trainingschedule/:sessionId',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Training Schedule'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/trainingschedule/trainingschedule.html',
                        controller: 'TrainingController'
                    }
                },

                onEnter: function(Status) {
                    Status.connect();
                    Status.subscribe();
                },
                onExit: function(Status) {
                    Status.unsubscribe();
                    Status.disconnect();
                }

            })
    });
