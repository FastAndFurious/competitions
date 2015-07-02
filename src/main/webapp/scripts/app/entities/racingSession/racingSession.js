'use strict';

angular.module('competitionApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('racingSession', {
                parent: 'entity',
                url: '/racingSession',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'competitionApp.racingSession.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/racingSession/racingSessions.html',
                        controller: 'RacingSessionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('racingSession');
                        return $translate.refresh();
                    }]
                }
            })
            .state('compSessions', {
                parent: 'entity',
                url: '/racingSession/:competitionId',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'competitionApp.racingSession.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/racingSession/racingSessions.html',
                        controller: 'RacingSessionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('racingSession');
                        return $translate.refresh();
                    }]
                }
            })
            .state('racingSessionDetail', {
                parent: 'entity',
                url: '/racingSession/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'competitionApp.racingSession.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/racingSession/racingSession-detail.html',
                        controller: 'RacingSessionDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('racingSession');
                        return $translate.refresh();
                    }]
                }
            });
    });
