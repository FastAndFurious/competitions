'use strict';

angular.module('competitionApp')
    .controller('RacingSessionController', function ($scope, RacingSession, Competition, $stateParams) {
        $scope.racingSessions = [];
        $scope.sessionTypes = ['Preparation', 'Training', 'Qualification', 'Competition'];

        $scope.loadAll = function(competitionId) {
            Competition.get({id: competitionId}, function (competition) {
                $scope.competition = competition;
                RacingSession.query ( { 'cmd': 'find', 'competitionName': competition.name }, function(result) {
                    $scope.racingSessions = result;
                });
            });
        };

        $scope.loadAll($stateParams.competitionId);

        $scope.create = function () {
            RacingSession.update($scope.racingSession,
                function () {
                    $scope.loadAll($scope.competition.id);
                    $('#saveRacingSessionModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            RacingSession.get({id: id}, function(result) {
                $scope.racingSession = result;
                $('#saveRacingSessionModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            RacingSession.get({id: id}, function(result) {
                $scope.racingSession = result;
                $('#deleteRacingSessionConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            RacingSession.delete({id: id},
                function () {
                    $scope.loadAll($scope.competition.id);
                    $('#deleteRacingSessionConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.racingSession = {competition: $scope.competition.name,
                type: 'Training', seqNo: 1,
                plannedStartTime: $scope.competition.startDate,
                trackLayout: null, id: null};

            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
