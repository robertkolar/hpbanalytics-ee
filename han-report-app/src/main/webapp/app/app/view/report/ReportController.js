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
            reports.load(function(records, operation, success) {
                if (success) {
                    reportsGrid.setSelection(reports.first());
                }
            });
        }

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
            executions = me.getStore('executions'),
            trades = me.getStore('trades'),
            statistics = me.getStore('statistics'),
            interval = me.lookupReference('intervalCombo').getValue();

        me.reportId = record.data.id,

        executions.getProxy().setUrl(Report.common.Definitions.urlPrefix + '/reports/' + me.reportId  + '/executions');
        trades.getProxy().setUrl(Report.common.Definitions.urlPrefix + '/reports/' + me.reportId + '/trades');
        statistics.getProxy().setUrl(Report.common.Definitions.urlPrefix + '/reports/' + me.reportId + '/statistics/' + interval);

        executions.load(function(records, operation, success) {
            if (success) {
                console.log('reloaded executions for report, id=' + me.reportId)
            }
        });
        trades.load(function(records, operation, success) {
            if (success) {
                console.log('reloaded trades for report, id=' + me.reportId)
            }
        });
        statistics.load(function(records, operation, success) {
            if (success) {
                console.log('reloaded statistics for report, id=' + me.reportId + ', interval=' + interval);
            }
        });
    },

    onIntervalChange: function(comboBox, newValue, oldValue, eOpts) {
        var me = this,
            statistics = me.getStore('statistics'),
            interval = me.lookupReference('intervalCombo').getValue();

        statistics.getProxy().setUrl(Report.common.Definitions.urlPrefix + '/reports/' + me.reportId + '/statistics/' + interval);
        statistics.load(function(records, operation, success) {
            if (success) {
                console.log('reloaded statistics for report, id=' + me.reportId + ', interval=' + interval);
            }
        });
    }
});