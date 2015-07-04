'use strict';

angular.module('competitionApp')
    .controller('RunController', function ($scope, FuriousRun, $stateParams, RacingSession, Competition) {
        $scope.runs = [];
        $scope.loadAll = function(sessionId) {

            RacingSession.get({id:sessionId}, function (session) {
                $scope.session = session;

                Competition.get({id:session.competition}, function (competition) {
                    $scope.competition = competition;

                    FuriousRun.query({ 'cmd': 'schedule', 'id': session.id}, function(runs) {
                        $scope.runs = runs;
                    });
                })
            });
        };
        $scope.loadAll($stateParams.sessionId);

    });
