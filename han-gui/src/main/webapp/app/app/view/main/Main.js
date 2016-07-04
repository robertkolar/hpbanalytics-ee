/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 *
 */
Ext.define('HanGui.view.main.Main', {
    extend: 'Ext.tab.Panel',

    requires: [
        'HanGui.view.main.MainController',
        'HanGui.view.main.MainModel',
        'HanGui.view.iblogger.IbLogger',
        'HanGui.view.c2.C2',
        'HanGui.view.report.Report',
        'HanGui.model.iblogger.IbOrder',
        'HanGui.model.iblogger.IbOrderEvent',
        'HanGui.model.c2.C2Signal',
        'HanGui.model.c2.PublishEvent',
        'HanGui.model.c2.PollEvent',
        'HanGui.model.report.Trade',
        'HanGui.model.report.SplitExecution'
    ],
    
    controller: 'main',
    viewModel: {
        type: 'main'
    },
    listeners: {
        beforerender: 'setGlyphs'
    },
    items: [{
        xtype: 'han-iblogger',
        title: 'IB Orders',
        reference: 'ibLoggerPanel'
    }, {
        xtype: 'han-c2',
        title: 'C2 Signals',
        reference: 'c2Panel'
    }, {
        xtype: 'han-report',
        title: 'Reports',
        reference: 'reportPanel'
    }]
});
