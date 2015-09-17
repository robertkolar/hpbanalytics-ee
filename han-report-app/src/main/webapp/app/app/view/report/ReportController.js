/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.view.report.ReportController', {
    extend: 'Ext.app.ViewController',
    requires: [
        'Report.common.Definitions'
    ],

    alias: 'controller.report',

    init: function() {
        var me = this,
            reports = me.getStore('reports'),
            executions = me.getStore('executions'),
            trades = me.getStore('trades'),
            statistics = me.getStore('statistics'),
            reportsGrid = me.lookupReference('reportsGrid');

        if (reports) {
            reports.reload();
        }
        reportsGrid.getSelectionModel().select(0);

        var ws = new WebSocket(Report.common.Definitions.wsUrl);
        ws.onopen = function(evt) {
            console.log('WS opened');
        };
        ws.onclose = function(evt) {
            console.log('WS closed');
        };
        ws.onmessage = function(evt) {
            console.log('WS message, content=' + evt.data + ' --> reloading stores...');
            executions.reload();
            trades.reload();
            statistics.reload();
        };
        ws.onerror = function(evt) {
            console.log('WS error');
        };
    },

    onReportSelect: function(grid, record, index, eOpts) {
        var me = this,
            reportId = record.data.id,
            executions = me.getStore('executions'),
            trades = me.getStore('trades'),
            statistics = me.getStore('statistics');

        executions.getProxy().setUrl(Report.common.Definitions.urlPrefix + '/reports/' + reportId  + '/executions');
        trades.getProxy().setUrl(Report.common.Definitions.urlPrefix + '/reports/' + reportId + '/trades');
        statistics.getProxy().setUrl(Report.common.Definitions.urlPrefix + '/reports/' + reportId + reportId + '/statistics/MONTH');

        executions.load(function(records, operation, success) {if (success) {console.log('reloaded executions for reportId=' + reportId)}});
        trades.load(function(records, operation, success) {if (success) {console.log('reloaded trades for reportId=' + reportId)}});
        statistics.load(function(records, operation, success) {if (success) {console.log('reloaded statistics for reportId=' + reportId)}});
    }
});