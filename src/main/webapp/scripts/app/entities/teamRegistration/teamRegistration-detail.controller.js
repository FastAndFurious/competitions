'use strict';

angular.module('competitionApp')
    .controller('TeamRegistrationDetailController', function ($scope, $stateParams, TeamRegistration) {
        $scope.teamRegistration = {};
        $scope.load = function (id) {
            TeamRegistration.get({id: id}, function(result) {
              $scope.teamRegistration = result;
            });
        };
        $scope.load($stateParams.id);
    });
