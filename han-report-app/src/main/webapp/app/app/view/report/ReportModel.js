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

    alias: 'viewmodel.han-report',

    stores: {
        reports: {
            model: 'Report.model.report.Report',
            pageSize: 10
        },
        executions: {
            model: 'Report.model.report.Execution',
            pageSize: 25,
            remoteFilter: true,
            remoteSort: false
        },
        trades: {
            model: 'Report.model.report.Trade',
            pageSize: 25,
            remoteFilter: true,
            remoteSort: false
        },
        statistics: {
            model: 'Report.model.report.Statistics',
            pageSize: 10
        },
        charts: {
            model: 'Report.model.report.Statistics'
        }
    }
});