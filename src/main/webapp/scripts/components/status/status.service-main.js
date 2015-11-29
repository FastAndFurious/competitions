'use strict';

var statusSubscriber = function ($rootScope, $cookies, $http, $q) {
    var stompClient = null;
    var stateSubscriber = null;
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
                stateSubscriber = stompClient.subscribe("/topic/status", function(data) {
                    stateListener.notify(JSON.parse(data.body));
                });
            }, null, null);
        },
        unsubscribe: function() {
            if (stateSubscriber != null) {
                stateSubscriber.unsubscribe();
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

angular.module('competitionApp')
    .factory('Status', statusSubscriber );
