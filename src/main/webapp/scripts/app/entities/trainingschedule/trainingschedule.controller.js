'use strict';

angular.module('competitionApp')
    .controller('TrainingController', function ($scope, TrainingSchedule, FuriousRun, $stateParams, RacingSession, Competition, Status) {
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

                    TrainingSchedule.query({ 'cmd': 'schedule', 'id': session.id}, function(runs) {
                        $scope.runs = runs;
                    });

                    RacingSession.query({'cmd': 'erroneousLifeSigns' }, function (erroneousLifeSigns) {
                        $scope.erroneousLifeSigns = erroneousLifeSigns;
                    })
                })
            });
        };

        $scope.startRun = function ( run ) {

            FuriousRun.start({'cmd': 'start', 'id': run.furiousId}, function(result) {

                if ( result ) {

                    var notification = {
                        teamName: run.teamId,
                        sessionId: run.sessionId
                    };
                    $scope.loadAll(run.sessionId);

                }
            }, function ( error ) {
                $scope.currentErrorMessage = error.data.message;
                $('#errorMessageModal').modal('show');
            });
        };


        $scope.stopRun = function ( run ) {

            FuriousRun.query({'cmd': 'stop', 'id': run.furiousId}, function(result) {

                if ( result ) {
                    $scope.loadAll(run.sessionId);
                }
            });
        };

        $scope.pauseRun = function ( run ) {

            FuriousRun.query({'cmd': 'pause', 'id': run.furiousId}, function(result) {

                if ( result ) {
                    $scope.loadAll(run.sessionId);
                }
            });
        };

        $scope.continueRun = function ( run ) {

            FuriousRun.query({'cmd': 'continue', 'id': run.furiousId}, function(result) {

                if ( result ) {
                    $scope.loadAll(run.sessionId);
                }
            });
        };

        $scope.applyForTraining = function ( run ) {

            var application = {
                teamName: run.teamId,
                sessionId: run.sessionId
            };
            TrainingSchedule.applyForTraining({'cmd': 'applications'}, application , function(result) {

                if ( result ) {
                    $scope.loadAll(run.sessionId);
                }
            });
        };

        $scope.missedTraining = function ( run ) {

            var notification = {
                teamName: run.teamId,
                sessionId: run.sessionId
            };
            TrainingSchedule.registerMissedTraining({'cmd': 'missed'}, notification , function(result) {

                if ( result ) {
                    $scope.loadAll(run.sessionId);
                }
            });
        };



        $scope.loadAll($stateParams.sessionId);

    });
