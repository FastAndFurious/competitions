'use strict';

var scheduleSubscriber = function ($rootScope, $cookies, $http, $q) {
    var stompClient = null;
    var subscriber = null;
    var stateListener = $q.defer();
    var connected = $q.defer();

    return {
        connect: function () {
            var socket = new SockJS('/websocket/competition');
            stompClient = Stomp.over(socket);
            var headers = {};
            headers['X-CSRF-TOKEN'] = $cookies[$http.defaults.xsrfCookieName];
            stompClient.connect(headers, function(frame) {
                connected.resolve("success");
            });
        },
        subscribe: function() {
            connected.promise.then(function() {
                subscriber = stompClient.subscribe("/topic/schedule", function(data) {
                    stateListener.notify(JSON.parse(data.body));
                });
            }, null, null);
        },
        unsubscribe: function() {
            if (subscriber != null) {
                subscriber.unsubscribe();
            }
        },
        receive: function() {
            return stateListener.promise;
        },
        disconnect: function() {
            if (stompClient != null) {
                stompClient.disconnect();
                stompClient = null;
            }
        }
    };
};

angular.module('scheduleBoardApp')
    .factory('Schedule', scheduleSubscriber );

