/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.view.report.Report', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.layout.container.VBox',
        'Report.view.report.ReportController',
        'Report.view.report.ReportModel',
        'Report.view.report.grid.ReportsGrid',
        'Report.view.report.grid.ExecutionsGrid',
        'Report.view.report.grid.TradesGrid',
        'Report.view.report.grid.StatisticsGrid',
        'Report.common.Glyphs',
        'Ext.tab.Panel'
    ],

    xtype: 'report',
    header: false,
    border: false,
    controller: 'report',
    viewModel: {
        type: 'report'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [{
        xtype: 'reports-grid',
        title: 'Reports',
        reference: 'reportsGrid'
    }, {
        xtype: 'tabpanel',
        items: [{
            xtype: 'executions-grid',
            title: 'Executions',
            glyph: Report.common.Glyphs.getGlyph('orderedlist')
        }, {
            xtype: 'trades-grid',
            title: 'Trades',
            glyph: Report.common.Glyphs.getGlyph('money')
        }, {
            xtype: 'statistics-grid',
            title: 'Statistics',
            glyph: Report.common.Glyphs.getGlyph('barchart')
        }]
    }]
});