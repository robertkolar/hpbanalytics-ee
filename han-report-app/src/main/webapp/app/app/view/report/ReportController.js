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
            reports.reload();
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
                    undlsData.push([undls[i], undls[i]]);
                }
                underlyingCombo.getStore().loadData(undlsData);
                underlyingCombo.setValue('ALLUNDLS');
                statistics.getProxy().setExtraParam('underlying', underlyingCombo.getValue());
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
            params: {underlying: underlying}
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
                        url: Report.common.Definitions.urlPrefix + '/reports/' + me.reportId
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
            message: 'All trades will be deleted and recreated again...',
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(btn) {
                if (btn === 'yes') {
                    Ext.Ajax.request({
                        method: 'DELETE',
                        url: Report.common.Definitions.urlPrefix + '/reports/' + me.reportId ,
                        success: function(response, opts) {
                            reports.load(function(records, operation, success) {
                                if (success) {
                                    reportsGrid.setSelection(reports.first());
                                }
                            });
                        }
                    });
                }
            }
        });
    },

    onDeleteExecution: function(button) {
        var me = this,
            executions = me.getStore('executions'),
            execution = button.getWidgetRecord().data;

        Ext.Msg.show({
            title:'Delete Execution?',
            message: 'Are you sure you want to delete the execution, id=' + execution.id + '?',
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(btn) {
                if (btn === 'yes') {
                    Ext.Ajax.request({
                        method: 'DELETE',
                        url: Report.common.Definitions.urlPrefix + '/reports/' + me.reportId + '/executions/' + execution.id
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
                me.createCharts();
            }
        });
    },

    createCharts: function() {
        var me = this,
            statistics = me.getStore('statistics'),
            numberOpened = [],
            numberClosed = [],
            numberWinners = [],
            numberLosers = [],
            maxWinner = [],
            maxLoser = [],
            winnersProfit = [],
            losersLoss = [],
            profitLoss = [],
            cumulativePl = [];

        if (me.hpbC1) {me.hpbC1.destroy();}
        if (me.hpbC2) {me.hpbC2.destroy();}
        if (me.hpbC3) {me.hpbC3.destroy();}
        if (me.hpbC4) {me.hpbC4.destroy();}
        if (me.hpbC5) {me.hpbC5.destroy();}
        if (me.hpbC6) {me.hpbC6.destroy();}

        statistics.each(function (record, id) {
            var d = record.data;

            numberOpened.push([d.timeInMillis, d.numOpened]);
            numberClosed.push([d.timeInMillis, d.numClosed]);
            numberWinners.push([d.timeInMillis, d.numWinners]);
            numberLosers.push([d.timeInMillis, d.numLosers]);
            maxWinner.push([d.timeInMillis, d.maxWinner]);
            maxLoser.push([d.timeInMillis, d.maxLoser]);
            winnersProfit.push([d.timeInMillis, d.winnersProfit]);
            losersLoss.push([d.timeInMillis, d.losersLoss]);
            profitLoss.push([d.timeInMillis, d.profitLoss]);
            cumulativePl.push([d.timeInMillis, d.cumulProfitLoss]);
        });

        me.hpbC1 = HpbChart.createC1(numberOpened, numberClosed);
        me.hpbC2 = HpbChart.createC2(numberWinners, numberLosers);
        me.hpbC3 = HpbChart.createC3(maxWinner, maxLoser);
        me.hpbC4 = HpbChart.createC4(winnersProfit, losersLoss);
        me.hpbC5 = HpbChart.createC5(profitLoss);
        me.hpbC6 = HpbChart.createC6(cumulativePl);
    }
});