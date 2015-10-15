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
        'Ext.tab.Panel',
        'Report.view.report.form.ExecutionAddForm'
    ],

    xtype: 'han-report',
    header: false,
    border: false,
    controller: 'han-report',
    viewModel: {
        type: 'han-report'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [{
        xtype: 'han-report-reports-grid',
        title: 'Reports',
        reference: 'reportsGrid'
    }, {
        xtype: 'tabpanel',
        title: 'Details',
        listeners: {
            beforerender: 'setGlyphs'
        },
        items: [{
            xtype: 'han-report-executions-grid',
            title: 'Executions',
            reference: 'executionsPanel'
        }, {
            xtype: 'han-report-trades-grid',
            title: 'Trades',
            reference: 'tradesPanel'
        }, {
            xtype: 'panel',
            title: 'Statistics',
            reference: 'statisticsPanel',
            items: [{
                xtype: 'han-report-statistics-grid'
            }, {
                xtype: 'container',
                reference: 'chartsContainer',
                defaults: {
                    height: 400,
                    width: 1210,
                    margin: '0 0 20 0'
                },
                items: [{
                    html: '<div id="hpb_c1"></div>'
                }, {
                    html: '<div id="hpb_c2"></div>'
                }, {
                    html: '<div id="hpb_c3"></div>'
                }, {
                    html: '<div id="hpb_c4"></div>'
                }, {
                    html: '<div id="hpb_c5"></div>'
                }, {
                    html: '<div id="hpb_c6"></div>'
                }]
            }]
        }]
    }]
});