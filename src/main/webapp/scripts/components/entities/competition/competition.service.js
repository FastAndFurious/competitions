'use strict';

angular.module('competitionApp')
    .factory('Competition', function ($resource) {
        return $resource('api/competitions/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    if (data.startDate != null){
                        var startDateFrom = data.startDate.split("-");
                        data.startDate = new Date(new Date(startDateFrom[0], startDateFrom[1] - 1, startDateFrom[2]));
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
