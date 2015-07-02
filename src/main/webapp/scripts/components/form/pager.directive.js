/* globals $ */
'use strict';

angular.module('competitionApp')
    .directive('competitionAppPager', function() {
        return {
            templateUrl: 'scripts/components/form/pager.html'
        };
    });
