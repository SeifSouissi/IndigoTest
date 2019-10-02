import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import uuidv1 from 'uuid/v1';

Vue.use(Vuex)

import { API_ENDPOINT } from './config';
import testData from '@/data/test-data';

export default new Vuex.Store({
    state: {
        // TODO don't use testData
        events: [],//testData,
        basicToken: false
    },
    mutations: {
        submitBasicToken: function( state, token ) {
            state.basicToken = `basic ${token}`;
        },
        updateList: function( state, list ) {
            state.events = list;
        }
    },
    actions: {
        deleteEvent: function( { commit, state }, eventId ) {
          return axios({
            method: 'GET',
            url: `${API_ENDPOINT}/deleteEvent/${eventId}`,
            //headers: { authorization: state.basicToken }
          });
        },
        modifyEvent: function( { commit, state }, calendarEvent ) {

          return axios({
            method: 'POST',
            url: `${API_ENDPOINT}/updateEvent`,
            data: {"data":{
              "id": calendarEvent.id,
              "name": calendarEvent.name,
              "dateTime": calendarEvent.dateTime,
              "duration": calendarEvent.duration,
              "brief": calendarEvent.brief
          }},
            headers: { "content-type": "text/plain"  }
            //headers: { authorization: state.basicToken }
          });
        },
        createEvent: function( { commit, state }, calendarEvent ) {
          return axios({
            method: 'POST',
            url: `${API_ENDPOINT}/createEvent`,
            data: {"data":{
              "name": calendarEvent.name,
              "dateTime": calendarEvent.dateTime,
              "duration": calendarEvent.duration,
              "brief": calendarEvent.brief
          }},
            headers: { "content-type": "text/plain"  }
          });
        },
        checkBasicToken: function( { commit, state }, token ) {

          // TODO remove return, actually implement basic authentication
            return;
          // TODO end remove return

          return axios({
            method: 'GET',
            url: `${API_ENDPOINT}/check`,
            headers: { authorization: `basic ${token}` }
          });
        },
        getList: function( { commit, state } ) {

        var allEvents = [];

          var result=[{date:"2019-10-12",events:[]},
          {date:"2019-10-13",events:[]},
          {date:"2019-10-14",events:[]}];

          return axios({
            method: 'GET',
            url: `${API_ENDPOINT}/getAllEvents`,
            // headers: { authorization: state.basicToken }
          }).then( res => { 

            result.forEach(function(eventDay,index){
              allEvents = [];
              var currentDate = new Date(eventDay.date);

              res.data.message.forEach(function(element) {
                
                var current = element.data;
                var eventDate = new Date(current.dateTime);
                current.id = element.name;

                if (currentDate.getDate() == eventDate.getDate() && currentDate.getMonth() == eventDate.getMonth()
                && currentDate.getFullYear() == eventDate.getFullYear())
                  allEvents.push(current);
              });
              result[index].events = allEvents;
            })
            
            commit( 'updateList',  result);

          });
        }
    }
})
