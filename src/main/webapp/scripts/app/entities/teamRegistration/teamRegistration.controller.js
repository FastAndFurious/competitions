'use strict';

angular.module('competitionApp')
    .controller('TeamRegistrationController', function ($scope, TeamRegistration, BatchRegistration) {
        $scope.teamRegistrations = [];
        $scope.batchRegistration = { 'numberOfRegistrations': 5 };
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

        $scope.createBatch = function () {
            console.log("creating batch...");
            BatchRegistration.createBatch($scope.batchRegistration,
                function () {
                    $scope.loadAll();
                    $scope.clear();
                });
        };



        $scope.update = function (id) {
            TeamRegistration.get({id: id}, function(result) {
                $scope.teamRegistration = result;
                $('#saveTeamRegistrationModal').modal('show');
            });
        };

        $scope.showBatchDialog = function (id) {
            $('#batchRegistrationModal').modal('show');
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
            $scope.batchForm.$setPristine();
            $scope.batchForm.$setUntouched();
        };
    });
