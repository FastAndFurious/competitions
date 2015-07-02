'use strict';

angular.module('competitionApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('competition', {
                parent: 'entity',
                url: '/competition',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'competitionApp.competition.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/competition/competitions.html',
                        controller: 'CompetitionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('competition');
                        return $translate.refresh();
                    }]
                }
            })
            .state('competitionDetail', {
                parent: 'entity',
                url: '/competition/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'competitionApp.competition.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/competition/competition-detail.html',
                        controller: 'CompetitionDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('competition');
                        return $translate.refresh();
                    }]
                }
            });
    });
