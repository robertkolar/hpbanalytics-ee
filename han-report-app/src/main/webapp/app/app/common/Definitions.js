/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.common.Definitions', {
    statics: {
        //urlPrefix: 'http://localhost:28080/han-report/rest/report',
        urlPrefix: 'http://' + window.location.host + '/han-report/rest/report',

        //wsUrl: 'ws://localhost:28080/han-report/websocket/report',
        wsUrl: 'ws://' + window.location.host + '/han-report/websocket/report'
    }
});