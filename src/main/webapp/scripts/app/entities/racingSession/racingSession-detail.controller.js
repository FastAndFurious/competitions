'use strict';

angular.module('competitionApp')
    .controller('RacingSessionDetailController', function ($scope, $stateParams, RacingSession) {
        $scope.racingSession = {};
        $scope.load = function (id) {
            RacingSession.get({id: id}, function(result) {
              $scope.racingSession = result;
            });
        };
        $scope.load($stateParams.id);
    });
