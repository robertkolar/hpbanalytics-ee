/**
 * Created by robertk on 4/11/15.
 */
Ext.define('C2.model.C2System', {
    extend: 'C2.model.Base',
    idProperty: 'systemId',

    fields: [
        'systemId',
        'systemName',
        'conversionOrigin',
        'email',
        'useSsl'
    ]
});