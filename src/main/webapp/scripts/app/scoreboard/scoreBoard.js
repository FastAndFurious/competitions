'use strict';

angular.module('scoreBoardApp', ['LocalStorageModule', 'tmh.dynamicLocale',
        'ngResource', 'ui.router', 'ngCookies', 'pascalprecht.translate', 'ngCacheBuster', 'infinite-scroll'])

    .run(function ($state) {
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
            onEnter: function (Status, Round) {
                Status.connect();
                Round.connect();
                Status.subscribe();
                Round.subscribe();
            },
            onExit: function (Status, Round) {
                Status.unsubscribe();
                Round.unsubscribe();
                Status.disconnect();
                Round.disconnect();
            }

        });


    })

    .controller('ScoreBoardController', function ($scope, Status, $interval) {

        $scope.currentRoundElapsed = 0;
        $scope.currentRoundStart = Date.now();
        $scope.currentRunElapsed = 0;
        $scope.currentRunStart = Date.now();

        $scope.display = "ONGOING";

        $scope.title = "Competition not yet started";

        $scope.status = {
            name: "HSR Challenge 2015",
            currentSession: "Competition No 1",
            currentBoard: [
                {
                    position: 1,
                    team: "kobayashi",
                    duration: 5960
                },
                {
                    position: 2,
                    team: "skyfall",
                    duration: 6239
                },
                {
                    position: 3,
                    team: "amidala",
                    duration: 7023
                },
                {
                    position: 4,
                    team: "schumi",
                    duration: 7100
                },
                {
                    position: 5,
                    team: "geronimo",
                    duration: 7301
                }
            ],
            recentRunInfo: {
                team: "kobayashi",
                bestOfThisTeam: {
                    position: 2,
                    duration: 5678
                },
                currentRunResults: [
                    {
                        duration: 5678
                    },
                    {
                        duration: 6100
                    },
                    {
                        duration: 6200
                    },
                    {
                        duration: 7101
                    },
                    {
                        duration: 7234
                    }
                ]
            }
        };

        var stopRun;

        Status.receive().then(null, null, function (status) {
            $scope.status = status;

            if (status.recentRunInfo == null) {
                if ($scope.display !== "MAIN") {
                    $scope.display = "MAIN";
                    if (angular.isDefined(stopRun)) {
                        $interval.cancel(stopRun);
                        stopRun = undefined;
                    }
                }
            } else {
                $scope.currentRoundStart = Date.now();
                if ($scope.display !== "ONGOING") {
                    $scope.currentRunStart = Date.now();
                    stopRun = $interval(function () {
                        $scope.currentRoundElapsed = ( Date.now() - $scope.currentRoundStart ) / 1000.0;
                        $scope.currentRunElapsed = ( Date.now() - $scope.currentRunStart ) / 1000.0;
                    }, 10);
                    $scope.display = "ONGOING";
                }
            }
        });

        $scope.showMain = function () {
            return $scope.display === "MAIN";
        };

        $scope.showOngoing = function () {
            return $scope.display === "ONGOING";
        };
    });
