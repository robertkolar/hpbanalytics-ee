/**
 * Created by robertk on 4/17/15.
 */
Ext.define('HanGui.view.c2.C2Model', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'HanGui.model.c2.C2Signal',
        'HanGui.model.c2.C2System',
        'HanGui.model.c2.InputRequest'
    ],

    alias: 'viewmodel.han-c2',

    stores: {
        c2Systems: {
            model: 'HanGui.model.c2.C2System',
            autoload: true,
            pageSize: 10
        },
        inputRequests: {
            model: 'HanGui.model.c2.InputRequest',
            autoload: true,
            pageSize: 10,
            remoteFilter: true,
            remoteSort: false
        },
        c2Signals: {
            model: 'HanGui.model.c2.C2Signal',
            autoload: true,
            pageSize: 15,
            remoteFilter: true,
            remoteSort: false
        }
    }
});