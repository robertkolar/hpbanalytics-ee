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
            me.reloadAll();
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
            interval = me.lookupReference('intervalCombo').getValue(),
            underlyingCombo =  me.lookupReference('underlyingCombo');

        me.reportId = record.data.id;

        executions.getProxy().setUrl(Report.common.Definitions.urlPrefix + '/reports/' + me.reportId  + '/executions');
        trades.getProxy().setUrl(Report.common.Definitions.urlPrefix + '/reports/' + me.reportId + '/trades');
        statistics.getProxy().setUrl(Report.common.Definitions.urlPrefix + '/reports/' + me.reportId + '/statistics/' + interval);

        Ext.Ajax.request({
            url: Report.common.Definitions.urlPrefix + '/reports/' + me.reportId  + '/underlyings',
            success: function(response, opts) {
                var undls = Ext.decode(response.responseText);
                var undlsData = [];
                undlsData.push(['ALLUNDLS', '--All--']);
                for (var i = 0; i < undls.length; i++) {
                    undlsData.push([undls[i]], undls[i]);
                }
                underlyingCombo.getStore.loadData(undlsData);
                underlyingCombo.setValue('ALLUNDLS');
                statistics.getProxy().setExtraParam('underlying', underlyingCombo.getValue());
            },
            failure: function(response, opts) {
                console.log('cannot get underlyings from server, status=' + response.status);
            }
        });
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
        me.reloadStatistics();
    },

    onIntervalChange: function(comboBox, newValue, oldValue, eOpts) {
        var me = this;

        me.getStore('statistics').getProxy().setUrl(Report.common.Definitions.urlPrefix + '/reports/' + me.reportId + '/statistics/' + me.lookupReference('intervalCombo').getValue());
        me.reloadStatistics();
    },

    onUnderlyingChange: function(comboBox, newValue, oldValue, eOpts) {
        var me = this;

        me.getStore('statistics').getProxy().setExtraParam('underlying', me.lookupReference('underlyingCombo').getValue());
        me.reloadStatistics();
    },

    onRecalculateStatistics: function(button, evt) {
        var me = this,
            interval = me.lookupReference('intervalCombo').getValue(),
            underlying =  me.lookupReference('underlyingCombo').getValue();

        Ext.Ajax.request({
            method: 'PUT',
            url: Report.common.Definitions.urlPrefix + '/reports/' + me.reportId  + '/statistics/' + interval,
            params: {underlying: underlying},
            success: function(response, opts) {
                console.log('successfully recalculated statistics for report id=' + me.reportId + ', interval=' + interval + ', underlying=' + underlying);
            },
            failure: function(response, opts) {
                console.log('cannot recalculate statistics, status=' + response.status);
            }
        });

        me.reloadStatistics();
    },

    onAnalyzeReport: function(button) {
        var me = this,
            report = button.getWidgetRecord().data;

        Ext.Msg.show({
            title:'Analyze Report?',
            message: 'All trades will be deleted and recreated again',
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(btn) {
                if (btn === 'yes') {
                    Ext.Ajax.request({
                        method: 'PUT',
                        url: Report.common.Definitions.urlPrefix + '/reports/' + me.reportId ,
                        success: function(response, opts) {
                            console.log('successfully analyzed report id=' + me.reportId);
                            me.reloadAll();
                        },
                        failure: function(response, opts) {
                            console.log('cannot analyze report, status=' + response.status);
                        }
                    });
                }
            }
        });
    },

    onDeleteReport: function(button) {
        var me = this,
            reports = me.getStore('reports'),
            reportsGrid = me.lookupReference('reportsGrid'),
            report = button.getWidgetRecord().data;

        Ext.Msg.show({
            title:'Analyze Report?',
            message: 'All trades will be deleted and recreated again',
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(btn) {
                if (btn === 'yes') {
                    Ext.Ajax.request({
                        method: 'DELETE',
                        url: Report.common.Definitions.urlPrefix + '/reports/' + me.reportId ,
                        success: function(response, opts) {
                            console.log('successfully deleted report id=' + me.reportId);
                            reportsGrid.setSelection(reports.first());
                        },
                        failure: function(response, opts) {
                            console.log('cannot delete report, status=' + response.status);
                        }
                    });
                }
            }
        });
    },

    reloadStatistics: function() {
        var me = this,
            statistics = me.getStore('statistics'),
            interval = me.lookupReference('intervalCombo').getValue(),
            underlying =  me.lookupReference('underlyingCombo').getValue();

        statistics.load(function(records, operation, success) {
            if (success) {
                console.log('reloaded statistics for report, id=' + me.reportId + ', interval=' + interval + ', underlying=' + underlying);
            }
        });
    },

    reloadAll: function() {
        var me = this,
            reports = me.getStore('reports'),
            executions = me.getStore('executions'),
            trades = me.getStore('trades'),
            statistics = me.getStore('statistics');

        reports.reload();
        executions.reload();
        trades.reload();
        statistics.reload();
    }
});