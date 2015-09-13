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
            statistics = me.getStore('statistics');

        if (reports) {
            reports.reload();
        }
        if (executions) {
            executions.reload();
        }
        if (trades) {
            trades.reload();
        }
        if (statistics) {
            statistics.reload();
        }
    }
});