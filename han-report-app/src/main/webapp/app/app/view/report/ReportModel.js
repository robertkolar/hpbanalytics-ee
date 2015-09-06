/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.view.report.ReportModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'Report.model.report.Report',
        'Report.model.report.Execution',
        'Report.model.report.SplitExecution',
        'Report.model.report.Trade',
        'Report.model.report.Statistics'
    ],

    alias: 'viewmodel.report',

    stores: {
        reports: {
            model: 'Report.model.report.Report',
            autoload: true,
            pageSize: 10
        },
        executions: {
            model: 'Report.model.Execution',
            autoload: true,
            pageSize: 25
        },
        trades: {
            model: 'Report.model.report.Trade',
            autoload: true,
            pageSize: 25
        },
        statistics: {
            model: 'Report.model.report.Statistics',
            autoload: true,
            pageSize: 10
        }
    }
});