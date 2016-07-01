/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.common.Definitions', {
    statics: {
        //urlPrefixReport: 'http://localhost:28080/han-report/rest/report',
        urlPrefixReport: 'http://' + window.location.host + '/han-report/rest/report',

        //wsUrlReport: 'ws://localhost:28080/han-report/websocket/report'
        wsUrlReport: 'ws://' + window.location.host + '/han-report/websocket/report',

        //urlPrefixC2: 'http://localhost:28080/han-c2/rest/c2',
        urlPrefixC2: 'http://' + window.location.host + '/han-c2/rest/c2',

        //wsUrlC2: 'ws://localhost:28080/han-c2/websocket/c2',
        wsUrlC2: 'ws://' + window.location.host + '/han-c2/websocket/c2',

        //urlPrefixIbLogger: 'http://localhost:28080/han-iblogger/rest/iblogger',
        urlPrefixIbLogger: 'http://' + window.location.host + '/han-iblogger/rest/iblogger',

        //wsUrlIbLogger: 'ws://localhost:28080/han-iblogger/websocket/iblogger',
        wsUrlIbLogger: 'ws://' + window.location.host + '/han-iblogger/websocket/iblogger',

        getRequestStatusColor: function(status) {
            var statusColor;
            switch(status) {
                case 'NEW':         statusColor = 'blue';   break;
                case 'PROCESSED':   statusColor = 'green';  break;
                case 'IGNORED':     statusColor = 'brown';  break;
                case 'ERROR':       statusColor = 'red';    break;
            }
            return statusColor;
        },

        getPublishStatusColor: function(status) {
            var statusColor;
            switch(status) {
                case 'POSOK':       statusColor = 'green';  break;
                case 'POSERR':      statusColor = 'red';    break;
                case 'SBMOK':       statusColor = 'green';  break;
                case 'SBMERR':      statusColor = 'red';    break;
                case 'CNCOK':       statusColor = 'green';  break;
                case 'CNCERR':      statusColor = 'red';    break;
                case 'UPDCNCOK':    statusColor = 'green';  break;
                case 'UPDCNCERR':   statusColor = 'red';    break;
                case 'UPDSBMOK':    statusColor = 'green';  break;
                case 'UPDSBMERR':   statusColor = 'red';    break;
            }
            return statusColor;
        },

        getPollStatusColor: function(status) {
            var statusColor;
            switch(status) {
                case 'NOTPOLLED':   statusColor = 'gray';   break;
                case 'WORKING':     statusColor = 'blue';   break;
                case 'CANCELLED':   statusColor = 'brown';  break;
                case 'FILLED':      statusColor = 'green';  break;
                case 'EXPIRED':     statusColor = 'orange'; break;
                case 'POLLERR':     statusColor = 'red';    break;
                case 'UNKNOWN':     statusColor = 'gray';   break;
            }
            return statusColor;
        },

        getIbOrderStatusColor: function(status) {
            var statusColor;

            switch(status) {
                case 'SUBMITTED':   statusColor = 'blue';   break;
                case 'UPDATED':     statusColor = 'blue';   break;
                case 'CANCELLED':   statusColor = 'brown';  break;
                case 'FILLED':      statusColor = 'green';  break;
                case 'UNKNOWN':     statusColor = 'gray';   break;
            }
            return statusColor;
        }
    }
});