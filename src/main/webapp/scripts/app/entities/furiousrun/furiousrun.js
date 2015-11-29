'use strict';

angular.module('competitionApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('runschedule', {
                parent: 'entity',
                url: '/furiousruns/:sessionId',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Run Schedule'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/furiousrun/furiousruns.html',
                        controller: 'RunController'
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
