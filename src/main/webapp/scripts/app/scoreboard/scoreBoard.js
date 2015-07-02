'use strict';

angular.module('scoreBoardApp', ['LocalStorageModule', 'tmh.dynamicLocale',
    'ngResource', 'ui.router', 'ngCookies', 'pascalprecht.translate', 'ngCacheBuster', 'infinite-scroll'])

    .run(function ( $state) {
        $state.go('scoreBoard')
    })

    .config(function ($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, $translateProvider, tmhDynamicLocaleProvider, httpRequestInterceptorCacheBusterProvider) {

        //enable CSRF
        $httpProvider.defaults.xsrfCookieName = 'CSRF-TOKEN';
        $httpProvider.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';

        //Cache everything except rest api requests
        httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/, /.*protected.*/], true);

        //$urlRouterProvider.otherwise('/login');
        $stateProvider.state('scoreBoard', {
            'abstract': false,
            views: {
                'content@': {
                    templateUrl: 'scripts/app/scoreboard/scoreBoardDetails.html',
                    controller: 'ScoreBoardController'
                }
            },
            onEnter: function(Status, Round) {
                Status.connect();
                Round.connect();
                Status.subscribe();
                Round.subscribe();
            },
            onExit: function(Status, Round) {
                Status.unsubscribe();
                Round.unsubscribe();
                Status.disconnect();
                Round.disconnect();
            }

        });


    })

    .controller('ScoreBoardController', function ( $scope, Status) {

        $scope.title = "HSR Challenge 2012 - The Grand Final";

        Status.receive().then(null, null, function(status) {
            $scope.status = status;
        });

    });
