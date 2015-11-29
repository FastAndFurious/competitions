'use strict';

angular.module('competitionApp')
    .controller('RunController', function ($scope, FuriousRun, $stateParams, RacingSession, Competition, Status) {
        $scope.runs = [];

        Status.receive().then(null, null, function(status) {
            $scope.status = status;

            if ( status.recentRunInfo == null ) {
                $scope.runs.forEach(function(r) {
                    r.status = 'QUALIFIED';
                })
            }
        });


        $scope.loadAll = function(sessionId) {

            RacingSession.get({id:sessionId}, function (session) {
                $scope.session = session;

                Competition.get({id:session.competition}, function (competition) {
                    $scope.competition = competition;

                    FuriousRun.query({ 'cmd': 'schedule', 'id': session.id}, function(runs) {
                        $scope.runs = runs;
                    });

                    RacingSession.query({'cmd': 'erroneousLifeSigns' }, function (erroneousLifeSigns) {
                        $scope.erroneousLifeSigns = erroneousLifeSigns;
                    })
                })
            });
        };

        $scope.startRun = function ( run ) {

            FuriousRun.start({'cmd': 'start', 'id': run.id}, function(result) {

                if ( result ) {
                    $scope.loadAll(run.sessionId);
                }
            }, function ( error ) {
                $scope.currentErrorMessage = error.data.message;
                $('#errorMessageModal').modal('show');
            });
        };

        $scope.stopRun = function ( run ) {

            FuriousRun.query({'cmd': 'stop', 'id': run.id}, function(result) {

                if ( result ) {
                    $scope.loadAll(run.sessionId);
                }
            });
        };



        $scope.loadAll($stateParams.sessionId);

    });
