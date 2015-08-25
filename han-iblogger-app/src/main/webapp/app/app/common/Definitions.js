/**
 * Created by robertk on 4/17/15.
 */
Ext.define('IbLogger.common.Definitions', {
    statics: {
        //urlPrefix: 'http://localhost:28080/han-iblogger/rest/iblogger',
        urlPrefix: 'http://' + window.location.host + '/han-iblogger/rest/iblogger',

        //wsUrl: 'ws://localhost:28080/han-iblogger/websocket/iblogger',
        wsUrl: 'ws://' + window.location.host + '/han-iblogger/websocket/iblogger',

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