/**
 * Created by robertk on 4/17/15.
 */
Ext.define('C2.view.c2.C2Model', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'C2.model.c2.C2Signal',
        'C2.model.c2.C2System',
        'C2.model.c2.InputRequest'
    ],

    alias: 'viewmodel.han-c2',

    stores: {
        c2Systems: {
            model: 'C2.model.c2.C2System',
            autoload: true,
            pageSize: 10
        },
        inputRequests: {
            model: 'C2.model.c2.InputRequest',
            autoload: true,
            pageSize: 10,
            remoteFilter: true,
            remoteSort: false
        },
        c2Signals: {
            model: 'C2.model.c2.C2Signal',
            autoload: true,
            pageSize: 10,
            remoteFilter: true,
            remoteSort: false
        }
    }
});