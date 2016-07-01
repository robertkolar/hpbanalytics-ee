/**
 * Created by robertk on 4/11/15.
 */
Ext.define('HanGui.model.c2.C2System', {
    extend: 'HanGui.model.c2.Base',
    idProperty: 'systemId',

    fields: [
        'systemId',
        'systemName',
        'origin',
        'stk',
        'opt',
        'fut',
        'fx',
        'useSsl',
        'email'
    ]
});