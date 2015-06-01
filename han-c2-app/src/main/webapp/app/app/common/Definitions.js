/**
 * Created by robertk on 4/17/15.
 */
Ext.define('C2.common.Definitions', {
    statics: {
        urlPrefix: 'http://localhost:28080/han-c2/rest/',
        //urlPrefix: 'http://' + window.location.host + '/han-c2/rest/',

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
        }
    }
});