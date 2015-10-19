/**
 * Created by robertk on 15.10.2015.
 */
Ext.define('Report.view.report.TradesController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'Report.common.Definitions'
    ],

    alias: 'controller.han-report-trades',

    onCloseTrade: function(button, evt) {
        var me = this,
            trade = button.getWidgetRecord().data;

        var closeDate,
            maxCloseDate,
            closePrice,
            ready = false;

        if ('OPT' != trade.secType) {
            closeDate = new Date();
            maxCloseDate = closeDate;
            closePrice = trade.avgOpenPrice;
            ready = true;
        } else {
            Ext.Ajax.request({
                url: Report.common.Definitions.urlPrefix + '/optionutil/parse',
                params: {
                    optionSymbol: trade.symbol
                },
                success: function (response, opts) {
                    closeDate = response.data.expDate;
                    maxCloseDate = closeDate;
                    closePrice = response.data.strikePrice;
                    ready = true;
                }
            });
        }

        var interval = setInterval(function() {
            if (ready) {
                clearInterval(interval);
                var window = Ext.create('Report.view.report.window.TradeCloseWindow', {
                    reference: 'tradeCloseWindow',
                    title: 'Close Trade, id=' + trade.id
                });
                me.lookupReference('tradesPanel').add(window);
                window.trade = trade;
                me.lookupReference('closeDate').setMaxValue(closeDate);
                me.lookupReference('closeDate').setValue(maxCloseDate);
                me.lookupReference('closePrice').setValue(closePrice);
                window.show();
            } else {
                console.log('Waiting for completion...');
            }
        }, 500);
    },

    onSubmitCloseTrade: function(button, evt) {
        var me = this,
            form = me.lookupReference('tradeCloseForm'),
            window = me.lookupReference('tradeCloseWindow'),
            trade = window.trade;

        var urlString = Report.common.Definitions.urlPrefix + '/reports/' + trade.reportId  + '/trades/' + trade.id + '/close';

        if (form && form.isValid()) {
            Ext.Ajax.request({
                method: 'PUT',
                url: urlString,
                jsonData: {
                    closeDate: form.getForm().findField('closeDate').lastValue,
                    closePrice: form.getForm().findField('closePrice').lastValue
                },
                success: function (response, opts) {
                    window.close();
                }
            });
        }
    },

    onCancelCloseTrade: function(button, evt) {
        this.lookupReference('tradeCloseWindow').close();
    },

    onExpireTrade: function(button, evt) {
        var me = this,
            trade = button.getWidgetRecord().data;

        me.assignOrExpire('EXPIRE', trade);
    },

    onAssignTrade: function(button, evt) {
        var me = this,
            trade = button.getWidgetRecord().data;

        me.assignOrExpire('ASSIGN', trade);
    },

    assignOrExpire: function(requestType, trade, closeDate, closePrice) {
        var urlString = Report.common.Definitions.urlPrefix + '/reports/' + trade.reportId  + '/trades/' + trade.id + '/' + requestType.toLowerCase();

        Ext.Msg.show({
            title: requestType + ' Trade?',
            message: 'Are you sure you want to ' + requestType + ' the trade, id=' + trade.id + '?',
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(btn) {
                if (btn === 'yes') {
                    if ('CLOSE' == requestType) {
                        Ext.Ajax.request({
                            method: 'PUT',
                            jsonData: {
                                closeDate: closeDate,
                                closePrice: closePrice
                            },
                            url: urlString
                        });
                    } else {
                        Ext.Ajax.request({
                            method: 'PUT',
                            url: urlString
                        });
                    }
                }
            }
        });
    },

    showSplitExecutions: function (view, cell, cellIndex, record, row, rowIndex, e) {
        if (cellIndex != 13) {
            return;
        }
        var me = this;

        if (!me.splitExecutionsGrid) {
            me.splitExecutionsGrid =  Ext.create('Report.view.report.grid.SplitExecutionsGrid');
            me.splitExecutionsWindow = Ext.create('Report.view.report.window.SplitExecutionsWindow');
            me.splitExecutionsWindow.add(me.splitExecutionsGrid);
        }
        me.splitExecutionsGrid.setStore(record.splitExecutions());
        me.splitExecutionsWindow.setTitle("Split Executions for Trade id=" + record.data.id);
        me.splitExecutionsWindow.show();
    }
});