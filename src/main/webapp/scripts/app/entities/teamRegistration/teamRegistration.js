'use strict';

angular.module('competitionApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('teamRegistration', {
                parent: 'entity',
                url: '/teamRegistration',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'competitionApp.teamRegistration.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/teamRegistration/teamRegistrations.html',
                        controller: 'TeamRegistrationController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('teamRegistration');
                        return $translate.refresh();
                    }]
                }
            })
            .state('teamRegistrationDetail', {
                parent: 'entity',
                url: '/teamRegistration/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'competitionApp.teamRegistration.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/teamRegistration/teamRegistration-detail.html',
                        controller: 'TeamRegistrationDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('teamRegistration');
                        return $translate.refresh();
                    }]
                }
            });
    });
