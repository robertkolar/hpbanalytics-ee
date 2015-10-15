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

        // TODO form for closeDate, closePrice
        var closeDate = new Date().getTime(),
            closePrice = 101.0;

        me.sendRequest('CLOSE', trade, closeDate, closePrice);
    },

    onExpireTrade: function(button, evt) {
        var me = this,
            trade = button.getWidgetRecord().data;

        me.sendRequest('EXPIRE', trade);
    },

    onAssignTrade: function(button, evt) {
        var me = this,
            trade = button.getWidgetRecord().data;

        me.sendRequest('ASSIGN', trade);
    },

    sendRequest: function(requestType, trade, closeDate, closePrice) {
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
    }
});