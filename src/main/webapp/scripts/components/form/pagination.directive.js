/* globals $ */
'use strict';

angular.module('competitionApp')
    .directive('competitionAppPagination', function() {
        return {
            templateUrl: 'scripts/components/form/pagination.html'
        };
    });
