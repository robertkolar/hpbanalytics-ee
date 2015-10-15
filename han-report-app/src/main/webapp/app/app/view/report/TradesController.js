/**
 * Created by robertk on 15.10.2015.
 */
Ext.define('Report.view.report.ReportController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'Report.common.Definitions'
    ],

    alias: 'controller.han-report-trades',

    onCloseTrade: function(button, evt) {
        var me = this,
            trade = button.getWidgetRecord().data;

        // TODO form for closeDate, closePrice
        var closeDate,
            closePrice;

        me.sendRequest('expire', trade, closeDate, closePrice);
    },

    onExpireTrade: function(button, evt) {
        var me = this,
            trade = button.getWidgetRecord().data;

        me.sendRequest('expire', trade);
    },

    onAssignTrade: function(button, evt) {
        var me = this,
            trade = button.getWidgetRecord().data;

        me.sendRequest('assign', trade);
    },

    sendRequest: function(requestType, trade, closeDate, closePrice) {
        var me = this,
            trades = me.getView().getStore();

        Ext.Msg.show({
            title: requestType.toUpperCase() + ' Trade?',
            message: 'Are you sure you want to assign the trade, id=' + trade.id + '?',
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(btn) {
                if (btn === 'yes') {
                    Ext.Ajax.request({
                        method: 'PUT',
                        url: Report.common.Definitions.urlPrefix + '/reports/' + trade.reportId  + '/trades/' + trade.id + '/' + requestType,
                        success: function(response, opts) {
                            var appliedExecutions = response.data;
                            var msg = '';
                            appliedExecutions.forEach(function(el, index, arr) {
                                console.log(el);
                                msg = msg + index;
                            });
                            Ext.Msg.alert('Trade ' + requestType + 'ed!', msg);
                            trades.reload();
                        }
                    });
                }
            }
        });
    }
});