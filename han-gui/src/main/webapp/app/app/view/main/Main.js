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
        'HanGui.model.c2.C2Signal',
        'HanGui.model.c2.PublishEvent',
        'HanGui.model.c2.PollEvent',
        'HanGui.model.iblogger.IbOrder',
        'HanGui.model.iblogger.IbOrderEvent'
    ],
    
    controller: 'main',
    viewModel: {
        type: 'main'
    },
    items: [{
        xtype: 'han-iblogger'
    }, {
        xtype: 'han-c2'
    }, {
        xtype: 'han-report'
    }]
});
