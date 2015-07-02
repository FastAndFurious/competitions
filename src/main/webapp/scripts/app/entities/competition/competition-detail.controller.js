'use strict';

angular.module('competitionApp')
    .controller('CompetitionDetailController', function ($scope, $stateParams, Competition, RacingSession) {
        $scope.competition = {};
        $scope.newSession = {};
        $scope.sessions = [];
        $scope.updating = false;
        $scope.sessionTypes = ['Preparation', 'Training', 'Qualification', 'Competition'];

        $scope.load = function (id) {
            Competition.get({id: id}, function (result) {
                $scope.competition = result;
                $scope.sessions = RacingSession.query ( { 'cmd': 'find', 'competitionName': result.name });
                $scope.newSession = {
                    competition: $scope.competition.name,
                    type: 'Training',
                    plannedStartTime: $scope.competition.startDate
                };
            });
        };

        $scope.saveNewSession = function () {
            RacingSession.update($scope.newSession,
                function () {
                    $scope.load( $scope.competition.id);
                    $('#saveRacingSessionModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.createNewSession = function (){
            $scope.updating = false;
            $scope.newSession = {
                competition: $scope.competition.name
            };
        };

        $scope.updateSession = function ( session ) {
            $scope.updating = true;

        };

        $scope.clear = function () {
            $scope.newSession = {};
        };

        $scope.load($stateParams.id);
    });
