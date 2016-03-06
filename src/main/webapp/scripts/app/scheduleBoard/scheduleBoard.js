'use strict';

angular.module('scheduleBoardApp', ['LocalStorageModule', 'tmh.dynamicLocale',
        'ngResource', 'ui.router', 'ngCookies', 'pascalprecht.translate', 'ngCacheBuster', 'infinite-scroll'])

    .run(function ($state) {
        $state.go('scheduleBoard')
    })

    .config(function ($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, $translateProvider, tmhDynamicLocaleProvider, httpRequestInterceptorCacheBusterProvider) {

        //enable CSRF
        $httpProvider.defaults.xsrfCookieName = 'CSRF-TOKEN';
        $httpProvider.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';

        //Cache everything except rest api requests
        httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/, /.*protected.*/], true);

        //$urlRouterProvider.otherwise('/login');
        $stateProvider.state('scheduleBoard', {
            'abstract': false,
            views: {
                'content@': {
                    templateUrl: 'scripts/app/scheduleBoard/scheduleBoardDetails.html',
                    controller: 'ScheduleBoardController'
                }
            },
            onEnter: function (Schedule) {
                Schedule.connect();
                Schedule.subscribe();
            },
            onExit: function (Schedule) {
                Schedule.unsubscribe();
                Schedule.disconnect();
            }

        });


    })

    .controller('ScheduleBoardController', function ($scope, Schedule) {

        $scope.currentRoundElapsed = 0;
        $scope.currentRoundStart = Date.now();
        $scope.currentRunElapsed = 0;
        $scope.currentRunStart = Date.now();

        $scope.schedule = {
            currentSession: "Not started yet.",
            currentPositions: []
        };

        Schedule.receive().then(null, null, function (schedule) {
            $scope.schedule = schedule;

        });

        $scope.showMain = function () {
            return $scope.display === "MAIN";
        };

        $scope.showOngoing = function () {
            return $scope.display === "ONGOING";
        };
    });
