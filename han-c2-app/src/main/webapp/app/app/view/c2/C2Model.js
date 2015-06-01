/**
 * Created by robertk on 4/17/15.
 */
Ext.define('C2.view.c2.C2Model', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'C2.model.C2Signal',
        'C2.model.C2System',
        'C2.model.InputRequest'
    ],

    alias: 'viewmodel.c2',

    stores: {
        c2Systems: {
            model: 'C2.model.C2System',
            autoload: true,
            pageSize: 10
        },
        inputRequests: {
            model: 'C2.model.InputRequest',
            autoload: true,
            pageSize: 10
        },
        c2Signals: {
            model: 'C2.model.C2Signal',
            autoload: true,
            pageSize: 10
        }
    }
});