/**
 * Created by robertk on 9/6/15.
 */
Ext.define('HanGui.view.report.ReportController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'HanGui.common.Definitions'
    ],

    alias: 'controller.han-report',

    init: function() {
        var me = this,
            reports = me.getStore('reports'),
            reportsGrid = me.lookupReference('reportsGrid');

        if (reports) {
            reports.getProxy().setUrl(HanGui.common.Definitions.urlPrefixReport + '/reports');
            reports.load(function(records, operation, success) {
                if (success) {
                    console.log('reloaded reports');
                    reportsGrid.setSelection(reports.first());
                }
            });
        }

        var ws = new WebSocket(HanGui.common.Definitions.wsUrlReport);
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

    reloadAll: function() {
        var me = this,
            reports = me.getStore('reports'),
            executions = me.getStore('executions'),
            trades = me.getStore('trades');

        me.lookupReference('chartsButton').toggle(false);
        reports.load(function(records, operation, success) {
            if (success) {
                console.log('reloaded reports');
            }
        });
        executions.reload();
        trades.reload();
        me.reloadStatisticsAndCharts();
    },

    onReportSelect: function(grid, record, index, eOpts) {
        var me = this,
            executions = me.getStore('executions'),
            trades = me.getStore('trades'),
            statistics = me.getStore('statistics'),
            charts = me.getStore('charts'),
            interval = me.lookupReference('intervalCombo').getValue(),
            underlyingCombo =  me.lookupReference('underlyingCombo'),
            executionsPaging = me.lookupReference('executionsPaging'),
            tradesPaging = me.lookupReference('tradesPanel').lookupReference('tradesPaging');

        me.reportId = record.data.id;

        executions.getProxy().setUrl(HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId  + '/executions');
        trades.getProxy().setUrl(HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId + '/trades');
        statistics.getProxy().setUrl(HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId + '/statistics/' + interval);
        charts.getProxy().setUrl(HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId + '/charts/' + interval);

        Ext.Ajax.request({
            url: HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId  + '/underlyings',
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
                charts.getProxy().setExtraParam('underlying', underlyingCombo.getValue());
            }
        });

        me.lookupReference('chartsButton').toggle(false);
        if (executionsPaging.getStore().isLoaded()) {
            executionsPaging.moveFirst();
        } else {
            executions.load(function(records, operation, success) {
                if (success) {
                    console.log('reloaded executions for report, id=' + me.reportId)
                }
            });
        }
        if (tradesPaging.getStore().isLoaded()) {
            tradesPaging.moveFirst();
        } else {
            trades.load(function(records, operation, success) {
                if (success) {
                    console.log('reloaded trades for report, id=' + me.reportId)
                }
            });
        }
        me.reloadStatisticsAndCharts();
    },

    onIntervalChange: function(comboBox, newValue, oldValue, eOpts) {
        var me = this;

        me.lookupReference('chartsButton').toggle(false);
        me.getStore('statistics').getProxy().setUrl(HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId + '/statistics/' + me.lookupReference('intervalCombo').getValue());
        me.getStore('charts').getProxy().setUrl(HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId + '/charts/' + me.lookupReference('intervalCombo').getValue());
        me.reloadStatisticsAndCharts();
    },

    onUnderlyingChange: function(comboBox, newValue, oldValue, eOpts) {
        var me = this;

        me.lookupReference('chartsButton').toggle(false);
        me.getStore('statistics').getProxy().setExtraParam('underlying', me.lookupReference('underlyingCombo').getValue());
        me.getStore('charts').getProxy().setExtraParam('underlying', me.lookupReference('underlyingCombo').getValue());
        me.reloadStatisticsAndCharts();
    },

    onRecalculateStatistics: function(button, evt) {
        var me = this,
            interval = me.lookupReference('intervalCombo').getValue(),
            underlying =  me.lookupReference('underlyingCombo').getValue();

        Ext.Ajax.request({
            method: 'PUT',
            url: HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId  + '/statistics/' + interval,
            params: {underlying: underlying}
        });
        me.reloadStatisticsAndCharts();
    },

    onAnalyzeReport: function(button) {
        var me = this;

        Ext.Msg.show({
            title:'Analyze Report?',
            message: 'All trades will be deleted and recreated again',
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(btn) {
                if (btn === 'yes') {
                    Ext.Ajax.request({
                        method: 'PUT',
                        url: HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId
                    });
                }
            }
        });
    },

    onDeleteReport: function(button) {
        var me = this,
            reports = me.getStore('reports'),
            reportsGrid = me.lookupReference('reportsGrid');

        Ext.Msg.show({
            title:'Delete Report?',
            message: 'All trades will be deleted',
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(btn) {
                if (btn === 'yes') {
                    Ext.Ajax.request({
                        method: 'DELETE',
                        url: HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId ,
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

    onAddExecution: function(button, e, options) {
        var me = this;

        me.lookupReference('executionsPanel').add(Ext.create('HanGui.view.report.window.ExecutionAddWindow', {
            reference: 'executionAddWindow',
            title: 'Add New Execution for Report id=' + me.reportId
        })).show();
    },

    onSubmitAddExecution: function(button, e, options) {
        var me = this,
            form = me.lookupReference('executionAddForm'),
            window = me.lookupReference('executionAddWindow');

        if (form && form.isValid()) {
            Ext.Ajax.request({
                method: 'POST',
                url: HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId + '/executions',
                jsonData: {
                    origin: form.getForm().findField('origin').lastValue,
                    referenceId: form.getForm().findField('referenceId').lastValue,
                    action: form.getForm().findField('action').lastValue,
                    quantity: form.getForm().findField('quantity').lastValue,
                    underlying: form.getForm().findField('underlying').lastValue,
                    currency: form.getForm().findField('currency').lastValue,
                    symbol: form.getForm().findField('symbol').lastValue,
                    secType: form.getForm().findField('secType').lastValue,
                    fillPrice: form.getForm().findField('fillPrice').lastValue,
                    fillDate: new Date(form.getForm().findField('fillDate').lastValue).getTime(),
                    comment: form.getForm().findField('comment').lastValue
                },
                success: function(response, opts) {
                    window.close();
                }
            });
        }
    },

    onCancelAddExecution: function(button, e, options) {
        this.lookupReference('executionAddWindow').close();
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
                        url: HanGui.common.Definitions.urlPrefixReport + '/reports/' + me.reportId + '/executions/' + execution.id
                    });
                }
            }
        });
    },

    reloadStatisticsAndCharts: function() {
        var me = this,
            statistics = me.getStore('statistics'),
            charts = me.getStore('charts'),
            interval = me.lookupReference('intervalCombo').getValue(),
            underlying =  me.lookupReference('underlyingCombo').getValue(),
            statisticsPaging = me.lookupReference('statisticsPaging');

        if (statisticsPaging.getStore().isLoaded()) {
            statisticsPaging.moveFirst();
        }
        statistics.load(function(records, operation, success) {
            if (success) {
                console.log('reloaded statistics for report, id=' + me.reportId + ', interval=' + interval + ', underlying=' + underlying);
            }
        });
        charts.load(function(records, operation, success) {
            if (success) {
                console.log('reloaded charts for report, id=' + me.reportId + ', interval=' + interval + ', underlying=' + underlying);
            }
        });
    },

    createCharts: function(tabPanel, newCard, oldCard, eOpts) {
        var me = this,
            statistics = me.getStore('charts'),
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

        if (!Ext.get('hpb_c1')) {
            return;
        }

        if (me.hpbC1) {me.hpbC1.destroy();}
        if (me.hpbC2) {me.hpbC2.destroy();}
        if (me.hpbC3) {me.hpbC3.destroy();}
        if (me.hpbC4) {me.hpbC4.destroy();}
        if (me.hpbC5) {me.hpbC5.destroy();}
        if (me.hpbC6) {me.hpbC6.destroy();}

        statistics.each(function (record, id) {
            var d = record.data;

            numberOpened.push([d.periodDate, d.numOpened]);
            numberClosed.push([d.periodDate, d.numClosed]);
            numberWinners.push([d.periodDate, d.numWinners]);
            numberLosers.push([d.periodDate, d.numLosers]);
            maxWinner.push([d.periodDate, d.maxWinner]);
            maxLoser.push([d.periodDate, d.maxLoser]);
            winnersProfit.push([d.periodDate, d.winnersProfit]);
            losersLoss.push([d.periodDate, d.losersLoss]);
            profitLoss.push([d.periodDate, d.profitLoss]);
            cumulativePl.push([d.periodDate, d.cumulProfitLoss]);
        });

        me.hpbC1 = HpbChart.createC1(numberOpened, numberClosed);
        me.hpbC2 = HpbChart.createC2(numberWinners, numberLosers);
        me.hpbC3 = HpbChart.createC3(maxWinner, maxLoser);
        me.hpbC4 = HpbChart.createC4(winnersProfit, losersLoss);
        me.hpbC5 = HpbChart.createC5(profitLoss);
        me.hpbC6 = HpbChart.createC6(cumulativePl);
    },

    setGlyphs: function() {
        var me = this;

        me.lookupReference('executionsPanel').setGlyph(HanGui.common.Glyphs.getGlyph('orderedlist'));
        me.lookupReference('tradesPanel').setGlyph(HanGui.common.Glyphs.getGlyph('money'));
        me.lookupReference('statisticsPanel').setGlyph(HanGui.common.Glyphs.getGlyph('barchart'));
    },

    onChartsToggle: function(button, pressed, eOpts) {
        var me = this,
            chartsContainer = me.lookupReference('chartsContainer');

        if (pressed) {
            chartsContainer.setVisible(true);
            me.createCharts();
        } else {
            chartsContainer.setVisible(false);
        }
    }
});