'use strict';

angular.module('competitionApp')
    .controller('CompetitionController', function ($scope, Competition) {
        $scope.competitions = [];
        $scope.loadAll = function() {
            Competition.query(function(result) {
               $scope.competitions = result;
            });
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Competition.get({id: id}, function(result) {
                $scope.competition = result;
                $('#saveCompetitionModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.competition.id != null) {
                Competition.update($scope.competition,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Competition.save($scope.competition,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Competition.get({id: id}, function(result) {
                $scope.competition = result;
                $('#deleteCompetitionConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Competition.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteCompetitionConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveCompetitionModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.competition = {name: null, trackId: null, startDate: null, bestSequence: null, bestSet: null, firstPriority: null, secondPriority: null, runDuration: null, numberOfSessions: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
