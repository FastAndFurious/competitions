'use strict';

angular.module('competitionApp')
    .controller('TeamRegistrationController', function ($scope, TeamRegistration) {
        $scope.teamRegistrations = [];
        $scope.loadAll = function() {
            TeamRegistration.query(function(result) {
               $scope.teamRegistrations = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            TeamRegistration.update($scope.teamRegistration,
                function () {
                    $scope.loadAll();
                    $('#saveTeamRegistrationModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            TeamRegistration.get({id: id}, function(result) {
                $scope.teamRegistration = result;
                $('#saveTeamRegistrationModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            TeamRegistration.get({id: id}, function(result) {
                $scope.teamRegistration = result;
                $('#deleteTeamRegistrationConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            TeamRegistration.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTeamRegistrationConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.teamRegistration = {competition: null, team: null, registrationTime: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
