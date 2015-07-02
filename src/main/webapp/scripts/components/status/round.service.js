'use strict';

angular.module('scoreBoardApp')
    .factory('Round', function ($rootScope, $cookies, $http, $q) {
        var stompClient = null;
        var roundSubscriber = null;
        var roundListener = $q.defer();
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
                    roundSubscriber = stompClient.subscribe("/topic/rounds", function(data) {
                        roundListener.notify(JSON.parse(data.body));
                    });
                }, null, null);
            },
            unsubscribe: function() {
                if (roundSubscriber != null) {
                    roundSubscriber.unsubscribe();
                }
            },
            receive: function() {
                return roundListener.promise;
            },
            disconnect: function() {
                if (stompClient != null) {
                    stompClient.disconnect();
                    stompClient = null;
                }
            }
        };
    });
