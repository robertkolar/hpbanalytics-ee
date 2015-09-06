/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.view.report.Report', {
    extend: 'Ext.panel.Panel',
    xtype: 'report',
    reference: 'report',
    header: false,
    border: false,
    requires: [
        'Ext.layout.container.VBox'
    ],
    controller: 'report',
    viewModel: {
        type: 'report'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [{
    }]
});